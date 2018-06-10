//    Speechrecorder
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.



package ipsk.apps.speechrecorder.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import ipsk.audio.AudioController2;
import ipsk.audio.AudioControllerException;
import ipsk.audio.DeviceInfo;
import ipsk.audio.DeviceProviderInfo;
import ipsk.beans.DOMCodec;
import ipsk.beans.DOMCodecException;
import ipsk.text.ParserException;
import ipsk.text.Version;
import ipsk.xml.DOMConverter;
import ipsk.xml.DOMConverterException;

import javax.sound.sampled.Mixer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author klausj
 *
 */
public class ConfigHelper {
    
    private DocumentBuilderFactory documentBuilderFactory;

    public ConfigHelper() {
        super();
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
    }
    
    public void writeConfig(ProjectConfiguration pc,File file) throws DOMCodecException, DOMConverterException, IOException{
        DOMCodec domCodec=new DOMCodec();   

        Document d = domCodec.createDocument(pc);
        DOMConverter domConverter=new DOMConverter();
        FileOutputStream fos=new FileOutputStream(file);
        OutputStreamWriter ow=new OutputStreamWriter(fos,Charset.forName("UTF-8"));
        domConverter.writeXML(d,ow);
        ow.close();
        fos.close();
    }
    
    public Version getProjectConfigurationVersion(InputStream projectConfigurationInputStream){
        String versionString=getProjectConfigurationVersionString(projectConfigurationInputStream);
        try {
            return Version.parseString(versionString);
        } catch (ParserException e) {
           return null;
        }
    }
    
    public static void applyLegacyToStrictConversions(ProjectConfiguration legacyCfg){
    
    	String projectVersionStr=legacyCfg.getVersion();
    	
    		Version strictVersion=new Version(new int[]{2,6,0});
			Version projectVersion;
			try {
				projectVersion = Version.parseString(projectVersionStr);
				if(projectVersion.compareTo(strictVersion)<0){
					RecordingConfiguration recCfg=legacyCfg.getRecordingConfiguration();
					String recUrlStr=recCfg.getUrl();
					if(recUrlStr!=null){
						String strictRecUrlstr=convertLegacyURLString(recUrlStr);
						recCfg.setUrl(strictRecUrlstr);
					}
					SpeakersConfiguration spksCfg=legacyCfg.getSpeakers();
					String spksUrlStr=spksCfg.getSpeakersUrl();
					if(spksUrlStr!=null){
						String strictSpksUrlstr=convertLegacyURLString(spksUrlStr);
						spksCfg.setSpeakersUrl(strictSpksUrlstr);
					}
					PromptConfiguration promptCfg=legacyCfg.getPromptConfiguration();
					String promptUrlStr=promptCfg.getPromptsUrl();
					if(promptUrlStr!=null){
						String strictPromptUrlstr=convertLegacyURLString(promptUrlStr);
						promptCfg.setPromptsUrl(strictPromptUrlstr);
					}
				}
			} catch (ParserException e) {
				// could not get version
				// fail silently, do not apply legacy conversions
				e.printStackTrace();
			}
			
    }
    
    public static String convertLegacyURLString(String legacyURL){
    	String strictURLString=legacyURL;
    	
				
//		    	String osName=System.getProperty("os.name");
//		    	if(osName!=null && osName.matches("[Ww]indows.*") && File.separatorChar=='\\' ){
		    	if(File.separatorChar=='\\' ){
		    		strictURLString=legacyURL.replace(File.separatorChar, '/');
		    	}
		   
    	
    	return strictURLString;
    }
    
    public  String getProjectConfigurationVersionString(InputStream projectConfigurationInputStream){

        // try to parse
        String versionStr=ProjectConfiguration.DEFAULT_VERSION;
        DocumentBuilder db;
        try {
            db = documentBuilderFactory.newDocumentBuilder();
            Document d = db.parse(projectConfigurationInputStream);
            NodeList peNl=d.getElementsByTagName("ProjectConfiguration");
            if(peNl!=null && peNl.getLength()==1){
                Node peN=peNl.item(0);
                if(peN instanceof Element){
                    Element pe=(Element)peN;
                    String attrVersionStr=pe.getAttribute("version");
                    if("".equals(attrVersionStr)){
                        versionStr=attrVersionStr;
                    }
                }
            }

        } catch (ParserConfigurationException e1) {
            return null;
        } catch (SAXException e1) {
            return null;
        } catch (IOException e1) {
            return null;
        }
        return versionStr;
    }
    
    public static MixerName[] getAJSConvertedMixerNames(AudioController2 audioController,MixerName[] orgMns){
       if(orgMns==null)return null;
        int mnCount=orgMns.length;
        MixerName[] ajsConvertedMixerNames=new MixerName[mnCount];
        
//      List<? extends DeviceProviderInfo> dpiList=audioController.getDeviceProviderInfos();
        for(int i=0;i<mnCount;i++){
            MixerName orgMn=orgMns[i];
            ajsConvertedMixerNames[i]=orgMn;
            String providerClassname=orgMn.providerIdAsJavaClassName();

            if(providerClassname==null){
                String orgName=orgMn.getName();
                String newName=orgName;
                String providerId=null;
                String interfaceName=null;
                if(audioController.supportsDeviceProviders()){
                    
                    DeviceInfo ndi=null;
                    try {
                        ndi = audioController.convertLegacyDeviceName(orgName);
                    } catch (AudioControllerException e) {
                       continue;
                    }
                    DeviceProviderInfo dpi=ndi.getDeviceProviderInfo();
                    if(dpi!=null){
                        String dpiCn=dpi.getImplementationClassname();
                        providerId=MixerName.javaClassnameToProviderId(dpiCn);
                        interfaceName=dpi.getAudioInterfaceName();
                    }
                    Mixer.Info mInfo=ndi.getMixerInfo();
                    if(mInfo!=null){
                        newName=mInfo.getName();
                    }
                }
//              for(DeviceProviderInfo dpi:dpiList){
//                  String pSuffix=dpi.getLegacyJavaSoundSuffix();
//                  if(pSuffix!=null && orgName!=null && orgName.endsWith(pSuffix)){
//                      // convert
//                      String newName=orgName.substring(0,orgName.length()-pSuffix.length());
//                      String implClassname=dpi.getImplementationClassname();
//
//                      String interfaceId=orgMn.javaClassnameToInterfaceId(implClassname);
                        MixerName cMn=new MixerName(providerId,interfaceName,newName,orgMn.isRegex());
                        
//                      // overwrite with converted name
                        ajsConvertedMixerNames[i]=cMn;
//
//                  }
//              }
            }
        }
        return ajsConvertedMixerNames;

    }
}
