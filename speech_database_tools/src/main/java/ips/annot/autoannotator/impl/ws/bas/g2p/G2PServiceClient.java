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

package ips.annot.autoannotator.impl.ws.bas.g2p;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;

import ips.annot.autoannotator.AutoAnnotation;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotatorException;
import ips.annot.autoannotator.BundleAutoAnnotation;
import ips.annot.autoannotator.impl.ws.bas.BasicBasServiceClient;
import ips.annot.model.PredefinedLevelDefinition;
import ips.annot.model.db.Bundle;
import ips.annot.model.db.LinkDefinition;
import ips.annot.partitur.PartiturParser;
import ipsk.io.StreamCopy;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

public class G2PServiceClient extends BasicBasServiceClient{

	
    public static final G2PAnnotatorServiceDescriptor DESCRIPTOR=new G2PAnnotatorServiceDescriptor();
   
    private final static boolean DEBUG=false;
    // Only Basic should work
//    private static String URL = "https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUS";
    protected static String URL = BASE_URL+"/runG2P";
    
   
    public static final String RESPONSE_XML_TOP_ELEM="WebServiceResponseLink";
    public static final String RESPONSE_XML_DOWNLOAD_ELEM="downloadLink";
    public static final String RESPONSE_XML_SUCCESS_ELEM="success";
    
    
    public G2PServiceClient(){
        super();
        docBuilderFactory=DocumentBuilderFactory.newInstance();
    }
    

    
    
    public AutoAnnotation g2pClient(String orthography,Locale loc)
            throws AutoAnnotatorException {

        try {
            getServerCaps();
        } catch (Exception e){
            // Ignore server caps and try to MAUS anyway
        }


        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(URL);

        String iso639ThreeLetters=loc.getISO3Language();
        if(DEBUG){
            System.out.println("Set language param: "+iso639ThreeLetters);
        }
        StringBody languageBody=new  StringBody(iso639ThreeLetters,DEFAULT_TEXT_CONTENT_TYPE);
       
        MultipartEntityBuilder reqEntityBuilder = MultipartEntityBuilder.create();
       
        reqEntityBuilder.addPart("lng",languageBody);
        // We need a simple filename for this body, BASWebservices uses the filename for the temporary output file
        // If no filename is given the text itself is used, which may not be a good filename
        // Extension txt is required. Without this, the download URL is not unique ! (does not contain a UUID) 
        ByteArrayBody ib=new ByteArrayBody(orthography.getBytes(DEFAULT_CHARSET),DEFAULT_TEXT_CONTENT_TYPE, "SpeechDatabaseTools_input"+UUID.randomUUID()+".txt");
        reqEntityBuilder.addPart("i",ib);

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
                URLConnection dlConn=dlUrl.openConnection();
                //                                System.out.println(dlConn.getContentType());
                // return text/plain without charset
                InputStream is=dlConn.getInputStream();
                // save contents to array
                ByteArrayOutputStream bos=new ByteArrayOutputStream();
                StreamCopy.copy(is, bos);
                byte[] bin=bos.toByteArray();


                ByteArrayInputStream bisd=new ByteArrayInputStream(bin);
                if(DEBUG)StreamCopy.toSystemOut(bisd);
                
                ByteArrayInputStream bis=new ByteArrayInputStream(bin);
                InputStreamReader kanCont=new InputStreamReader(bis,DEFAULT_CHARSET);
                
                PartiturParser pp=new PartiturParser();
                Set<LinkDefinition> linkDefs=new HashSet<LinkDefinition>();
                LinkDefinition ortKanLd=new LinkDefinition();
                ortKanLd.setSuperTier(PredefinedLevelDefinition.ORT.getLevelDefinition());
                ortKanLd.setSubTier(PredefinedLevelDefinition.KAN.getLevelDefinition());
                linkDefs.add(ortKanLd);
                pp.setLinkDefinitions(linkDefs);
                Bundle inb=null;
                if(annotationRequest!=null){
                    inb=annotationRequest.getBundle();
                }
                Bundle resBundle=pp.parse(inb, kanCont);
                BundleAutoAnnotation baa=new BundleAutoAnnotation(resBundle);
                return baa;
            }
        } catch (IOException e) {
           throw new AutoAnnotatorException(e);
        }finally{
            
        }


            return null;

        }
    
    public static void main(String[] args){
        G2PServiceClient g2pc=new G2PServiceClient();
        
        String example2Txt="Einzelzimmer 248 Mark.";
        try {
            AutoAnnotation aa=g2pc.g2pClient(example2Txt,Locale.GERMAN);
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
        if(DEBUG){
        	System.out.println("G2P call for "+inputBundle.getName());
        }
        String orthography=orthoGraphyTextFromTemplateLevel(inputBundle);
        if(orthography!=null){
            Locale loc=inputBundle.getLocale();
            return g2pClient(orthography, loc);
            
        }else{
            throw new AutoAnnotatorException(); 
        }
    }


    @Override
    public void open() {
        // nothing todo (separate HTTP connections/requests for each annotation
    }


    @Override
    public void close() {
        // nothing todo
    }


    public void setAnnotationRequest(AnnotationRequest ar) {
       this.annotationRequest=ar;
    }
    

    @Override
    public AutoAnnotationServiceDescriptor getServiceDescriptor() {
      
        return DESCRIPTOR;
    }

    @Override
    public boolean isBundleSupported(Bundle bundle) throws IOException {
           boolean basicSupp=super.isBundleSupported(bundle);
           if(!basicSupp){
               return basicSupp;
           }
           String orthography=orthoGraphyTextFromTemplateLevel(bundle);
           return (orthography!=null);
           
    }
    
	
   
}
