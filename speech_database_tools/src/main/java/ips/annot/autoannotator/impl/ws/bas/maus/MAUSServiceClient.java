//    IPS Speech database tools
// 	  (c) Copyright 2016
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Speech database tools
//
//
//    IPS Speech database tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Speech database tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Speech database tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Mar 4, 2013
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ips.annot.autoannotator.impl.ws.bas.maus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;

import ips.annot.autoannotator.AutoAnnotation;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotatorException;
import ips.annot.autoannotator.BundleAutoAnnotation;
import ips.annot.autoannotator.impl.maus.BasicMAUSAnnotation;
import ips.annot.autoannotator.impl.ws.bas.BasicBasServiceClient;
import ips.annot.model.PredefinedLevelDefinition;
import ips.annot.model.db.Bundle;
import ips.annot.model.db.Level;
import ips.annot.model.db.LinkDefinition;
import ips.annot.partitur.PartiturParser;
import ipsk.io.StreamCopy;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

public class MAUSServiceClient extends BasicBasServiceClient{

	
	public static final MAUSAnnotatorServiceDescriptor DESCRIPTOR=new MAUSAnnotatorServiceDescriptor();
   
    private final static boolean DEBUG=false;
    // Only Basic should work
//    private static String URL = "https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUS";
    private static String URL = "https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUS";
    
    private static String PARAM_LANG="LANGUAGE";
    
    public static final String RESPONSE_XML_TOP_ELEM="WebServiceResponseLink";
    public static final String RESPONSE_XML_DOWNLOAD_ELEM="downloadLink";
    public static final String RESPONSE_XML_SUCCESS_ELEM="success";
    
    
    private boolean insertKanonicalTier=false;
    /**
	 * @return the insertKanonicalTier
	 */
	public boolean isInsertKanonicalTier() {
		return insertKanonicalTier;
	}

	/**
	 * @param insertKanonicalTier the insertKanonicalTier to set
	 */
	public void setInsertKanonicalTier(boolean insertKanonicalTier) {
		this.insertKanonicalTier = insertKanonicalTier;
	}

	/**
	 * @return the insertOrthographyTier
	 */
	public boolean isInsertOrthographyTier() {
		return insertOrthographyTier;
	}

	/**
	 * @param insertOrthographyTier the insertOrthographyTier to set
	 */
	public void setInsertOrthographyTier(boolean insertOrthographyTier) {
		this.insertOrthographyTier = insertOrthographyTier;
	}

	private boolean insertOrthographyTier=false;
    
    
    public MAUSServiceClient(){
        super();
        docBuilderFactory=DocumentBuilderFactory.newInstance();
       
    }
    

    
    
    public AutoAnnotation webMAUSClient(File audioFile, String kanCont,Locale loc)
            throws AutoAnnotatorException {

           try {
            getServerCaps();
        } catch (Exception e){
            // Ignore server caps and try to MAUS anyway
        }
         
        
           
//        Bundle bundle = null;
        BasicMAUSAnnotation anno=null;
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(URL);
        AudioFileFormat aff = null;
        try {
            aff = AudioSystem.getAudioFileFormat(audioFile);
        } catch (UnsupportedAudioFileException e1) {

            e1.printStackTrace();
            throw new AutoAnnotatorException(e1);
        } catch (IOException e1) {
            e1.printStackTrace();
            throw new AutoAnnotatorException(e1);
        }
        AudioFormat af = aff.getFormat();
        float sampleRate = af.getSampleRate();
        FileBody audio = new FileBody(audioFile, ContentType.create("audio/wav"),audioFile.getName());
        // StringBody comment = new StringBody("Filename: " + fileName);
        //        FileBody text = new FileBody(textFile, "text/plain");
//        Locale loc=null;
//        if(annotationRequest!=null){
//            loc=annotationRequest.getBundle().getLocale();
//        }
//        if(loc==null){
//            loc=Locale.getDefault();
//        }
        
        String iso639ThreeLetters=loc.getISO3Language();
        if(DEBUG){
            System.out.println("Set language param: "+iso639ThreeLetters);
        }
        StringBody languageBody=new  StringBody(iso639ThreeLetters,DEFAULT_TEXT_CONTENT_TYPE);
        StringBody trueBody=new StringBody("true", ContentType.TEXT_PLAIN);
        StringBody falseBody=new StringBody("false", ContentType.TEXT_PLAIN);
//        StringBody falseBody=new StringBody("false", ContentType.TEXT_PLAIN);
        MultipartEntityBuilder reqEntityBuilder = MultipartEntityBuilder.create();
//        reqEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        reqEntity.setLaxMode();
//        StringBody parBody=new StringBody("par", ContentType.TEXT_PLAIN);
//        reqEntityBuilder.addPart("v",new StringBody("1", ContentType.TEXT_PLAIN));
        reqEntityBuilder.addPart("LANGUAGE",languageBody);
        reqEntityBuilder.addPart("INFORMAT",new StringBody("bpf",DEFAULT_TEXT_CONTENT_TYPE));
        reqEntityBuilder.addPart("OUTFORMAT",new StringBody("par",DEFAULT_TEXT_CONTENT_TYPE));
//        File bpfTestFile=new File("/homes/klausj/DOWNLOAD/SpeechDatabaseTools_input.g2p.par");
//        FileBody bpfFb=new FileBody(bpfTestFile);
        ByteArrayBody ib=new ByteArrayBody(kanCont.getBytes(DEFAULT_CHARSET),DEFAULT_TEXT_CONTENT_TYPE, "SpeechDatabaseTools_input"+UUID.randomUUID()+".par");
//        reqEntityBuilder.addPart("BPF",new StringBody(kanCont,DEFAULT_TEXT_CONTENT_TYPE));
        reqEntityBuilder.addPart("BPF",ib);
        reqEntityBuilder.addPart("USETRN",falseBody);
        reqEntityBuilder.addPart("SIGNAL", audio);
        if(DEBUG)System.out.println("MAUS request audio content length: "+audio.getContentLength());
//        reqEntityBuilder.addPart("OUTFORMAT", parBody);
        if(insertKanonicalTier){
        	reqEntityBuilder.addPart("INSKANTEXTGRID",trueBody);
        }
        if(insertOrthographyTier){
        	reqEntityBuilder.addPart("INSORTTEXTGRID",trueBody);
        }
        
        HttpEntity reqEntity=reqEntityBuilder.build();
        httppost.setEntity(reqEntity);
        HttpResponse response;

        try {
            response = httpclient.execute(httppost);

            StatusLine sl=response.getStatusLine();
            int status=sl.getStatusCode();
            if (status >= HttpStatus.SC_OK
                    && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                HttpEntity resEntity = response.getEntity();

                InputStream content = resEntity.getContent();


                java.net.URL dlUrl=downloadLinkFromResponse(content);
                if(dlUrl!=null){
                    URLConnection dlConn=dlUrl.openConnection();
                    //                                System.out.println(dlConn.getContentType());
                    // return text/plain without charset
                    InputStream is=dlConn.getInputStream();
                    // save contents to array
                    ByteArrayOutputStream bos=new ByteArrayOutputStream();
                    StreamCopy.copy(is, bos);
                    byte[] mausBinData=bos.toByteArray();


                    ByteArrayInputStream bisD=new ByteArrayInputStream(mausBinData);
                    if(DEBUG)StreamCopy.toSystemOut(bisD);

                    ByteArrayInputStream bis=new ByteArrayInputStream(mausBinData);
                    InputStreamReader conReader=new InputStreamReader(bis,DEFAULT_CHARSET);
                    PartiturParser pp=new PartiturParser();
                    Set<LinkDefinition> linkDefs=new HashSet<LinkDefinition>();
                    LinkDefinition ortKanLd=new LinkDefinition();
                    ortKanLd.setSuperTier(PredefinedLevelDefinition.ORT.getLevelDefinition());
                    ortKanLd.setSubTier(PredefinedLevelDefinition.KAN.getLevelDefinition());
                    ortKanLd.setType(LinkDefinition.ONE_TO_MANY);
                    linkDefs.add(ortKanLd);
                    LinkDefinition kanMauLd=new LinkDefinition();
                    kanMauLd.setSuperTier(PredefinedLevelDefinition.KAN.getLevelDefinition());
                    kanMauLd.setSubTier(PredefinedLevelDefinition.MAU.getLevelDefinition());
                    kanMauLd.setType(LinkDefinition.ONE_TO_MANY);
                    linkDefs.add(kanMauLd);
                    pp.setLinkDefinitions(linkDefs);
                    Bundle inputBundle=null;
                    
                    if(annotationRequest!=null){
                        inputBundle=annotationRequest.getBundle();
//                        List<Level> filteredLvls=new ArrayList<Level>();
                        List<Level> lvlsToRemove=new ArrayList<Level>();
                        // ORT,KAN levels will be replaced, (MAU should not exist)
                        List<Level> lvls=inputBundle.getLevels();
                        for(Level lvl:lvls){
                        	String lvlNm=lvl.getName();
                        	if(PredefinedLevelDefinition.ORT.getKeyName().equals(lvlNm) ||
                        			PredefinedLevelDefinition.KAN.getKeyName().equals(lvlNm) ||
                        			PredefinedLevelDefinition.MAU.getKeyName().equals(lvlNm)){
//                        		filteredLvls.add(lvl);
                        	    lvlsToRemove.add(lvl);
                        	   
                        	}
                        }
                        for(Level ltr:lvlsToRemove){
                            inputBundle.removeLevelAndAssociatedLinks(ltr);
                        }
                        
                    }
                    Bundle bundle = pp.parse(inputBundle,conReader);
                    if(DEBUG)System.out.println(bundle);
                    conReader.close();
                    BundleAutoAnnotation baa=new BundleAutoAnnotation(bundle);
                    return baa;
                }else{
                    
                }
            }
        } catch (ClientProtocolException e) {
            throw new AutoAnnotatorException(e);
        } catch (IOException e) {
            throw new AutoAnnotatorException(e);
        } 

        return anno;

}

public static void main(String[] args){
    MAUSServiceClient wmc=new MAUSServiceClient();
    //        
    //        String example2Txt="Einzelzimmer 248 Mark.";
    String exampleKan=new String("KAN: 0 ?aInts@ltsIm6\nKAN: 1 tsvaIhUnd6t?axtUntfi:rtsIC\nKAN: 2 mark\nORT: 0 Einzelzimmer\nORT: 1 zweihundertachtundvierzig\nORT: 2 Mark");
    try {
        AutoAnnotation aa=wmc.webMAUSClient(new File("/homes/klausj/examples-WebMAUS/example2.wav"), exampleKan,Locale.GERMAN);
        System.out.println(aa);

    } catch (AutoAnnotatorException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

}


@Override
public AutoAnnotation call() throws Exception {
    //        File audioFile=annotationRequest.getMediaFile();
    //        String orthoGraphy=annotationRequest.getOrthoGraphy();
    Bundle inputBundle=annotationRequest.getBundle();
    if(inputBundle!=null){
    	if(DEBUG){
    		System.out.println("MAUS call for "+inputBundle.getName());
        }
        List<String> sigPaths=inputBundle.getSignalpaths();
        if(sigPaths!=null && sigPaths.size()==1){
            String sigPath=sigPaths.get(0);
            File audioFile=new File(sigPath);
            if(audioFile!=null && audioFile.exists()){
                Level ortLvl=inputBundle.getTierByName(PredefinedLevelDefinition.ORT.getKeyName());
                Level kanLvl=inputBundle.getTierByName(PredefinedLevelDefinition.KAN.getKeyName());
                
                List<Level> lvls=new ArrayList<Level>();
                if(ortLvl!=null){
                    lvls.add(ortLvl);
                }
                if(kanLvl==null){
                    throw new AutoAnnotatorException("MAUS web service client requires valid KAN input tier/level");
                }else{
                    lvls.add(kanLvl);
                }
                PartiturParser pp=new PartiturParser();

                String ortKanCont=pp.writeLevels(lvls,inputBundle.getLinksAsSet());

                if(DEBUG){
                    System.out.println(ortKanCont);
                }
                AutoAnnotation baa=webMAUSClient(audioFile,ortKanCont,inputBundle.getLocale());
                return baa;

            }
        }
    }
    // TODO err message
    throw new AutoAnnotatorException();  

}


    @Override
    public void open() {
        // nothing todo (separate HTTP connections/requests for each annotation
    }


    @Override
    public void close() {
        // nothing todo
    }
    
    @Override
    public boolean isBundleSupported(Bundle bundle) throws IOException {
           boolean basicSupp=super.isBundleSupported(bundle);
           if(!basicSupp){
               return basicSupp;
           }
           Level kanLvl=bundle.getTierByName(PredefinedLevelDefinition.KAN.getKeyName());
           return(kanLvl!=null);
           
    }
    

    public void setAnnotationRequest(AnnotationRequest ar) {
       this.annotationRequest=ar;
    }
    

    @Override
    public AutoAnnotationServiceDescriptor getServiceDescriptor() {
        
        return DESCRIPTOR;
    }

}
