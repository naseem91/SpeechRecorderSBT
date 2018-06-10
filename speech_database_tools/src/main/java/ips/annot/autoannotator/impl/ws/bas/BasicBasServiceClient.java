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

package ips.annot.autoannotator.impl.ws.bas;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.entity.ContentType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ips.annot.autoannotator.AutoAnnotation;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotator;
import ips.annot.autoannotator.AutoAnnotatorException;
import ips.annot.model.PredefinedLevelDefinition;
import ips.annot.model.db.Bundle;
import ips.annot.model.db.Item;
import ips.annot.model.db.Level;
import ips.dom.DocUtils;
import ipsk.audio.ThreadSafeAudioSystem;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

public abstract class BasicBasServiceClient implements AutoAnnotator {

	
	
    public class ServerCaps{
        private List<String> supportedLangs;

        public List<String> getSupportedLangs() {
            return supportedLangs;
        }

        public void setSupportedLangs(List<String> supportedLangs) {
            this.supportedLangs = supportedLangs;
        }
        
        public String toString(){
            StringBuffer sb=new StringBuffer();
            if(supportedLangs!=null){
                sb.append("Supported languages: ");
                int suppLanglen=supportedLangs.size();
                for(int i=0;i<suppLanglen;i++){
                    String suppLang=supportedLangs.get(i);
                    sb.append(suppLang);
                    if(i<suppLanglen-1){
                        sb.append(',');
                    }
                }
            }
            return sb.toString();
            
        }
 
    }
    private final static boolean DEBUG=false;
    // Only Basic should work
//    private static String URL = "https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUS";
    protected static String BASE_URL = "https://clarin.phonetik.uni-muenchen.de/BASWebServices/services";
    protected static String CMDI_URL="http://clarin.phonetik.uni-muenchen.de/BASRepository/WebServices/BAS_Webservices.cmdi.xml";
    
    private static String PARAM_LANG="LANGUAGE";
    
    public static final String RESPONSE_XML_TOP_ELEM="WebServiceResponseLink";
    public static final String RESPONSE_XML_DOWNLOAD_ELEM="downloadLink";
    public static final String RESPONSE_XML_SUCCESS_ELEM="success";
    public static final String RESPONSE_XML_OUTPUT_ELEM="output";
    public static final String RESPONSE_XML_WARNINGS_ELEM="warnings";
    
    protected AnnotationRequest annotationRequest;
    protected ServerCaps serverCaps=null;
    
    protected static final Charset DEFAULT_CHARSET=Charset.forName("UTF-8");
    protected static final ContentType DEFAULT_TEXT_CONTENT_TYPE=ContentType.create("text/plain",DEFAULT_CHARSET);
   
    protected DocumentBuilderFactory docBuilderFactory;
    
    public BasicBasServiceClient(){
        super();
        docBuilderFactory=DocumentBuilderFactory.newInstance();
    }
    
    // TODO
    protected synchronized void getServerCaps() throws ParserConfigurationException, SAXException, IOException{
//        java.net.URL cmdiUrl=new java.net.URL(CMDI_URL);
        
       // JAXB.  xjc generated java source does not compile.
        if(serverCaps==null){
            List<String> supportedLangs=new ArrayList<String>();
        DocumentBuilder db=docBuilderFactory.newDocumentBuilder();
        Document cmdiDoc=db.parse(CMDI_URL);
        DocUtils docUtils=new DocUtils(cmdiDoc);
        List<Element> basServiceElems=docUtils.getElementsByTagName(new String[]{"CMD", "Components"},"BASWebService");
        if(basServiceElems!=null && basServiceElems.size()==1){
            Element serviceElem=DocUtils.getFirstElementByTagNameWith(basServiceElems.get(0), "Service","Name","BAS Webservices");
            if(serviceElem!=null){
                Element opsElem=DocUtils.getFirstElementByTagName(serviceElem,"Operations");
                if(opsElem!=null){
                    Element opElem=DocUtils.getFirstElementByTagNameWith(opsElem,"Operation", "Name","runMAUSBasic");
                    if(opElem!=null){
                        Element inputElement=DocUtils.getFirstElementByTagName(opElem, "Input");
                        if(inputElement!=null){
                            Element paramElem=DocUtils.getFirstElementByTagNameWith(opElem,"Parameter","Name", PARAM_LANG);
                            if(paramElem!=null){
//                                Element valueE=DocUtils.getFirstElementByTagName(paramElem,"Values");
                                List<Element> valueElems=DocUtils.getElementsByTagName(paramElem, "ParameterValue");
                                for(Element valE:valueElems){
                                    Element vEl=DocUtils.getFirstElementByTagName(valE, "Value");
                                    String langISO639ThreeLetter=vEl.getTextContent();
                                    supportedLangs.add(langISO639ThreeLetter);
                                }
                            }
                        }
                       
                    }
                }
            }
            
            serverCaps=new ServerCaps();
            serverCaps.setSupportedLangs(supportedLangs);
            if(DEBUG)System.out.println(serverCaps);
        }
        }else{
            // TODO parse error
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
    
    

    protected boolean isMediafileSupported(File mediaFile) throws IOException{
        try {
            AudioFileFormat aff=ThreadSafeAudioSystem.getAudioFileFormat(mediaFile);
            AudioFormat af=aff.getFormat();
            if(AudioFormat.Encoding.PCM_SIGNED.equals(af.getEncoding()) && af.getChannels()==1){
                return true;
            }
        } catch (UnsupportedAudioFileException e) {
           return false;
        }
        return false;
    }

   
    protected boolean isLanguageSupported(Locale reqLocale) {
       try {
        getServerCaps();
       } catch (Exception e) {
        e.printStackTrace();
       }
      if(serverCaps!=null){
//          Locale reqLocale=new Locale(languageISO639);
          String reqISO_3_lang=reqLocale.getISO3Language();
          List<String> suppLangsISO639_3=serverCaps.getSupportedLangs();
          return suppLangsISO639_3.contains(reqISO_3_lang);
          
      }
       return false;
    }
    
    @Override
    public boolean isBundleSupported(Bundle bundle) throws IOException {
           Locale l=bundle.getLocale();
           boolean langSupp=isLanguageSupported(l);
           List<String> sigPaths=bundle.getSignalpaths();
           if(sigPaths.size()==0){
               return false;
           }
           File sigFile=new File(sigPaths.get(0));
           boolean mSupp=isMediafileSupported(sigFile);
           return(langSupp && mSupp);
    }
    
    // this method was only used for debugging
    protected void parseResponseHeader(HttpEntity resEntity){
        org.apache.http.Header contentTypeHeader = resEntity.getContentType();
        if (contentTypeHeader != null) {
            String ctHeNm = contentTypeHeader.getName();
            String ctHeVal = contentTypeHeader.getValue();
            if(DEBUG)System.out.println(ctHeNm + " : " + ctHeVal);
            HeaderElement[] hes = contentTypeHeader.getElements();
            if (hes != null) {
                for (HeaderElement he : hes) {
                    String heName = he.getName();
                    String heval = he.getValue();
                    if(DEBUG)System.out.println(heName + " : " + heval);
                    // one content type header is application/json
                    int pc = he.getParameterCount();
                    for (int i = 0; i < pc; i++) {
                        NameValuePair nvp = he.getParameter(i);
                        if(DEBUG)System.out.println("Param: " + nvp);
                    }
                }
            }
        }
    }
    
    protected URL downloadLinkFromResponse(InputStream response) throws AutoAnnotatorException{
//        ByteArrayOutputStream bos=new ByteArrayOutputStream();
//        try {
//            StreamCopy.copy(res, bos);
//        } catch (IOException e2) {
//            // TODO Auto-generated catch block
//            e2.printStackTrace();
//        }
//        byte[] responseData=bos.toByteArray();
//        ByteArrayInputStream response=new ByteArrayInputStream(responseData);
        String downloadLink = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder dBuilder;
        Document doc =null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();


            doc = dBuilder.parse(response);
        } catch (ParserConfigurationException e1) {
            throw new AutoAnnotatorException(e1);
        } catch (SAXException e) {
            throw new AutoAnnotatorException(e);
        } catch (IOException e) {
            throw new AutoAnnotatorException(e);
        }
        NodeList wsResLnkNl = doc
                .getElementsByTagName(RESPONSE_XML_TOP_ELEM);
        int wsResLnkNlLen = wsResLnkNl.getLength();
        if (wsResLnkNlLen < 1) {
            throw new AutoAnnotatorException(
                    "Expected one XML element \""
                            + RESPONSE_XML_TOP_ELEM + "\"");
        }
        if(wsResLnkNlLen>1){
            throw new AutoAnnotatorException("Expected exactly one XML element \""+RESPONSE_XML_TOP_ELEM+"\". Found "+wsResLnkNlLen+" elements.");
        }
        Node wsResLnkN=wsResLnkNl.item(0);

        NodeList nl=wsResLnkN.getChildNodes();
        int dlLen=nl.getLength();
        boolean success=false;
        String output=null;
        String warnings=null;
        for(int i=0;i<dlLen;i++){
            Node n=nl.item(i);
            if(n instanceof Element){
                Element e=(Element)n;

                if(RESPONSE_XML_SUCCESS_ELEM.equals(e.getNodeName())){
                    String successStr=e.getTextContent();
                    success=Boolean.parseBoolean(successStr);
                    if(!success){
                        System.out.println(doc.toString());
                    }
                }
                if(RESPONSE_XML_OUTPUT_ELEM.equals(e.getNodeName())){
                    output=e.getTextContent();
                }
                if(RESPONSE_XML_WARNINGS_ELEM.equals(e.getNodeName())){
                    warnings=e.getTextContent();
                }
            }
        }
      
        if(!success){
            String msg;
            msg="Bas web service result unsuccessful";
            if(output!=null && !"".equals(output)){
                msg=msg.concat(",output: "+output);
            }
            if(warnings!=null && !"".equals(warnings)){
                msg=msg.concat(",warnings: "+warnings);
            }
//            ByteArrayInputStream rd=new ByteArrayInputStream(responseData);
//            try {
//                StreamCopy.toSystemOut(rd);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            throw new AutoAnnotatorException(msg);
        }

        for(int i=0;i<dlLen;i++){
            Node n=nl.item(i);
            if(n instanceof Element){
                Element e=(Element)n;

                if(RESPONSE_XML_DOWNLOAD_ELEM.equals(e.getNodeName())){
                    if(downloadLink!=null){
                        throw new AutoAnnotatorException("Expected exactly one XML element \""+RESPONSE_XML_DOWNLOAD_ELEM+"\" in WebMAUS response.");
                    }
                    downloadLink=e.getTextContent();
                    java.net.URL dlUrl;
                    try {
                        dlUrl = new java.net.URL(downloadLink);
                    } catch (MalformedURLException e1) {
                        throw new AutoAnnotatorException(e1);
                    }
                    return dlUrl;
                }
            }
        }
        return null;
    }
    
    protected File masterSignalFile(Bundle bundle){
        List<String> sigPaths = bundle.getSignalpaths();
        if (sigPaths != null && sigPaths.size() == 1) {
            String sigPath = sigPaths.get(0);
            File audioFile = new File(sigPath);
            if (audioFile != null && audioFile.exists()) {
                return audioFile;
            }
        }
        return null;
    }

    protected String orthoGraphyTextFromTemplateLevel(Bundle bundle){

        Level tplLvl = bundle.getTierByName(PredefinedLevelDefinition.TPL.getKeyName());
        if (tplLvl != null) {
            List<Item> its = tplLvl.getItems();
            if (its.size() == 1) {
                Item it = its.get(0);
                Map<String, Object> itLbls = it.getLabels();
                Object itLbl = itLbls.get(PredefinedLevelDefinition.TPL.getKeyName());
                if (itLbl instanceof String) {
                    String templateText = (String) itLbl;
                    if (templateText != null) {
                        return templateText;
                    }
                }
            }
        }
        return null;
    }
   
    @Override
    public boolean needsWorker() {
        return true;
    }
    @Override
    public abstract AutoAnnotation call() throws Exception;
        

    @Override
    public abstract AutoAnnotationServiceDescriptor getServiceDescriptor();

    

}
