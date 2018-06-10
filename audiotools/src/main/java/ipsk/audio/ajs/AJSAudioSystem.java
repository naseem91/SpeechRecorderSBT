//    IPS Java Audio Tools
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.audio.ajs;

import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.ajs.impl.stdjs.JavaSoundServiceDescriptor;

import ipsk.util.services.ServiceDescriptorsInspector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.spi.MixerProvider;
import javax.swing.SwingUtilities;

/**
 * @author klausj
 *
 */
public class AJSAudioSystem extends ThreadSafeAudioSystem {
    public static enum DeviceType {CAPTURE,PLAYBACK};
    private static HashMap<String, MixerProvider> mixerProviderCache=new HashMap<String, MixerProvider>();
    private static ServiceDescriptorsInspector<MixerProviderServiceDescriptor,MixerProvider> mixerProviderServiceInspector=new ServiceDescriptorsInspector<MixerProviderServiceDescriptor,MixerProvider>(MixerProvider.class);
    private static String applicationName=null;
    private static String freeDesktopApplicationIconName=null;
    
    /**
	 * @return the freeDesktopApplicationIconName
	 */
	public static String getFreeDesktopApplicationIconName() {
		return freeDesktopApplicationIconName;
	}

	/**
	 * @param freeDesktopApplicationIconName the freeDesktopApplicationIconName to set
	 */
	public static void setFreeDesktopApplicationIconName(String freeDesktopApplicationIconName) {
		AJSAudioSystem.freeDesktopApplicationIconName = freeDesktopApplicationIconName;
	}

	/**
	 * @return the applicationName
	 */
	public static String getApplicationName() {
		return applicationName;
	}

	/**
	 * @param applicationName the applicationName to set
	 */
	public static void setApplicationName(String applicationName) {
		AJSAudioSystem.applicationName = applicationName;
		
	}

	private static  DataLine.Info sourceDataLineInfo = new DataLine.Info(
            SourceDataLine.class, null);

    private static DataLine.Info targetDataLineInfo = new DataLine.Info(
            TargetDataLine.class, null);
    
    public static void init(){
    	
    		Runnable comInitializer=new Runnable() {
    		@Override
    		public void run() {
    			List<MixerProviderServiceDescriptor> mpsdList=listMixerProviderDescriptors();
    			for(MixerProviderServiceDescriptor mpsd:mpsdList){
    				String mpsdIname=mpsd.getAudioInterfaceName();
    				if("DirectSound".equals(mpsdIname)){
    					// Windows directSound COM workaround
    					// dirty workaround to initialize the COM library before DirectSound is used
    					// standard JavaSound uses DirectSound without initializing the COM lib
    					// 
    					try{
    						Class<?> dsMixerclass=Class.forName(mpsd.getServiceImplementationClassname());
    						if(dsMixerclass!=null){
    							dsMixerclass.newInstance();
    						}
    					}catch(Error err){

    					}catch(ClassNotFoundException cnfe){
    						cnfe.printStackTrace();
    					} catch (InstantiationException e) {
    						e.printStackTrace();
    					} catch (IllegalAccessException e) {
    						e.printStackTrace();
    					}
    				}
    			}
    		}
    	
    	};
    	if (java.awt.EventQueue.isDispatchThread()) {
    		comInitializer.run();
    	}else{
    		try {
    			SwingUtilities.invokeAndWait(comInitializer);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		} catch (InvocationTargetException e) {
    			e.printStackTrace();
    		}
    	}
    }
    		
   public static  List<MixerProviderServiceDescriptor> listMixerProviderDescriptors(){
       try {
       
        List<MixerProviderServiceDescriptor> mpsds=mixerProviderServiceInspector.getTypedServiceDescriptors(MixerProviderServiceDescriptor.class);
////      TODO TEST!!
//        ArrayList<MixerProviderServiceDescriptor> mpsdsJSFisrt=new ArrayList<MixerProviderServiceDescriptor>();
        mpsds.add(new JavaSoundServiceDescriptor());
//        mpsdsJSFisrt.add(new JavaSoundServiceDescriptor());
//        for(MixerProviderServiceDescriptor mpsd:mpsds){
//            mpsdsJSFisrt.add(mpsd);
//        }
        return mpsds;
//        return mpsdsJSFisrt;
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
        
        return null;
        
    }
   

   
   private static void applyProperties(MixerProvider mixerprovider){
	   // need reflection here
	   Class<?> mpClass=mixerprovider.getClass();
	   try {
		   Method setMethod=mpClass.getMethod("setAJSProperties", Properties.class);
		   if(setMethod!=null){
			   Properties p=new Properties();
			   if(applicationName!=null){
				   p.put("ips.audio.ajs.application.name", applicationName);
			   		
			   }
			   if(freeDesktopApplicationIconName!=null){
				   p.put("ips.audio.ajs.freedesktop.application.icon.name", freeDesktopApplicationIconName);
			   		
			   }
			   if(p.size()>0){
				   try {
					setMethod.invoke(mixerprovider, p);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			   }
		   }
	   } catch (NoSuchMethodException e) {
		   // OK not supported
	   } catch (SecurityException e) {
		   // OK not supported should only happen with sandboxed applets
	   }

   }
   
   public static synchronized MixerProvider getMixerProvider(MixerProviderServiceDescriptor mpsd){
       synchronized(mixerProviderCache){
           String sClname=mpsd.getServiceImplementationClassname();
           MixerProvider mp=mixerProviderCache.get(sClname);
           if(mp==null){
               // create
               Class<?> mpClass;
               try {
                   mpClass = Class.forName(sClname);
                   Object nmpo=mpClass.newInstance();
                   if(nmpo instanceof MixerProvider){
                       mp=(MixerProvider)nmpo;
                       mixerProviderCache.put(sClname, mp);
                       applyProperties(mp);
                   }
               } catch (ClassNotFoundException e) {
                   // ignore
               } catch (InstantiationException e) {
                   // ignore
               } catch (IllegalAccessException e) {
                   // ignore
               }

           }
           return mp;
       }
   }
   
   public static MixerProviderServiceDescriptor getMixerProviderServiceDescriptor(MixerProvider mp){
       if(mp==null){
           return null;
       }
       List<MixerProviderServiceDescriptor> mpsdList=listMixerProviderDescriptors();
       
       String sClname=mp.getClass().getName();
       
       for(MixerProviderServiceDescriptor mpsd:mpsdList){
           if(sClname.equals(mpsd.getServiceImplementationClassname())){
               return mpsd;
           }
       }
       return null;
   }
   public static AJSDevice getDefaultResolvedCaptureDevice(){
	   AJSDeviceInfo defDevInfo=getDefaultCaptureDeviceInfo();
	   AJSDevice defDev=getResolvedCaptureDevice(defDevInfo);
	   return defDev;
   }
   public static AJSDevice getResolvedCaptureDevice(AJSDeviceInfo deviceInfo){
	   AJSDevice dev=getDevice(deviceInfo);
	   
	   if(dev.getMixer()==null){
		   // resolve current default mixer
		   MixerProviderServiceDescriptor mpsd=deviceInfo.getMixerProviderServiceDescriptor();
		   MixerProvider mp=getMixerProvider(mpsd);
		   Mixer.Info[] mpMixerInfos=mp.getMixerInfo();
		   // default mixer is first mixer which has source data lines
		   for(Mixer.Info mpMixerInfo:mpMixerInfos){
			  Mixer m=mp.getMixer(mpMixerInfo);
			  Line.Info[] tdlInfos=m.getTargetLineInfo();
			  if(tdlInfos!=null && tdlInfos.length>0){
				  // has at least one target data line
				  dev=new AJSDevice(mpsd, m);
				  break;
			  }
		   }
	   }
	   return dev;
   }
   
   public static AJSDevice getDefaultResolvedPlaybackDevice(){
	   AJSDeviceInfo defDevInfo=getDefaultPlaybackDeviceInfo();
	   AJSDevice defDev=getResolvedPlaybackDevice(defDevInfo);
	   return defDev;
   }
   public static AJSDevice getResolvedPlaybackDevice(AJSDeviceInfo deviceInfo){
	   AJSDevice dev=getDevice(deviceInfo);
	   
	   if(dev.getMixer()==null){
		   // resolve current default mixer
		   MixerProviderServiceDescriptor mpsd=deviceInfo.getMixerProviderServiceDescriptor();
		   MixerProvider mp=getMixerProvider(mpsd);
		   Mixer.Info[] mpMixerInfos=mp.getMixerInfo();
		   // default mixer is first mixer which has source data lines
		   for(Mixer.Info mpMixerInfo:mpMixerInfos){
			  Mixer m=mp.getMixer(mpMixerInfo);
			  Line.Info[] sdlInfos=m.getSourceLineInfo();
			  if(sdlInfos!=null && sdlInfos.length>0){
				  // has at least one source data line
				  dev=new AJSDevice(mpsd, m);
				  break;
			  }
		   }
	   }
	   return dev;
   }
   public static AJSDevice getDevice(AJSDeviceInfo deviceInfo){
       AJSDevice ajsDevice=null;
       if(deviceInfo==null){
           List<MixerProviderServiceDescriptor> mpsdList=listMixerProviderDescriptors();
           if(mpsdList!=null && mpsdList.size()>1){
               return new AJSDevice(mpsdList.get(0), null);
           }
       }
       MixerProviderServiceDescriptor infoMpsd=deviceInfo.getMixerProviderServiceDescriptor();
       String mpClassname=infoMpsd.getServiceImplementationClassname();
       List<MixerProviderServiceDescriptor> mpsdList=listMixerProviderDescriptors();
       for(MixerProviderServiceDescriptor mpsd:mpsdList){
           if(mpClassname.equals(mpsd.getServiceImplementationClassname())){
               MixerProvider mp=getMixerProvider(mpsd);
               if(mp==null){
                   continue;
               }
               Mixer.Info mInfo=deviceInfo.getMixerInfo();
               Mixer m=null;
               if(mInfo!=null){
                   try{
                       m=mp.getMixer(mInfo);
                       // TODO should devices be cached too ?
                   }catch(IllegalArgumentException iae){
                       break;
                   }
               }
               ajsDevice=new AJSDevice(mpsd, m);
               break;
           }
       }
       
       return ajsDevice;
   }
   
   public static List<Mixer.Info> availableCaptureMixerInfos(MixerProviderServiceDescriptor mixerProviderServiceDescriptor){
       MixerProvider mp=AJSAudioSystem.getMixerProvider(mixerProviderServiceDescriptor);
       Mixer.Info[] mInfos=mp.getMixerInfo();
       ArrayList<Mixer.Info> captureMixerInfoList=new ArrayList<Mixer.Info>();
       for(Mixer.Info mInfo:mInfos){
//           AJSDeviceInfo ajsDevInfo=new AJSDeviceInfo(mixerProviderServiceDescriptor, mInfo);
//           AJSDevice ajsDev=AJSAudioSystem.getMixer(ajsDevInfo);
           Mixer m=mp.getMixer(mInfo);
//           Mixer m=ajsDev.getDevice();
           Line.Info[] tlInfos = m.getTargetLineInfo(targetDataLineInfo);
           if (tlInfos != null && tlInfos.length > 0) {
               captureMixerInfoList.add(mInfo);
           }
       }
       
       return captureMixerInfoList;
   }
   
   public static List<AJSDeviceInfo> availableCaptureDeviceInfos(MixerProviderServiceDescriptor mixerProviderServiceDescriptor){
       MixerProvider mp=AJSAudioSystem.getMixerProvider(mixerProviderServiceDescriptor);
       Mixer.Info[] mInfos=mp.getMixerInfo();
       ArrayList<AJSDeviceInfo> captureDeviceInfoList=new ArrayList<AJSDeviceInfo>();
       for(Mixer.Info mInfo:mInfos){
           Mixer m=mp.getMixer(mInfo);
           Line.Info[] tlInfos = m.getTargetLineInfo(targetDataLineInfo);
           if (tlInfos != null && tlInfos.length > 0) {
               AJSDeviceInfo dInfo=new AJSDeviceInfo(mixerProviderServiceDescriptor, mInfo);
               captureDeviceInfoList.add(dInfo);
           }
       }
       
       return captureDeviceInfoList;
   }
   public static List<Mixer.Info> availablePlaybackMixerInfos(MixerProviderServiceDescriptor mixerProviderServiceDescriptor){
       MixerProvider mp=AJSAudioSystem.getMixerProvider(mixerProviderServiceDescriptor);
       Mixer.Info[] mInfos=mp.getMixerInfo();
       ArrayList<Mixer.Info> playbackMixerInfoList=new ArrayList<Mixer.Info>();
       for(Mixer.Info mInfo:mInfos){
           Mixer m=mp.getMixer(mInfo);
           Line.Info[] slInfos = m.getSourceLineInfo(sourceDataLineInfo);
           if (slInfos != null && slInfos.length > 0) {
               
               playbackMixerInfoList.add(mInfo);
           }
       }
       
       return playbackMixerInfoList;
   }
   
   public static List<AJSDeviceInfo> availablePlaybackDeviceInfos(MixerProviderServiceDescriptor mixerProviderServiceDescriptor){
       MixerProvider mp=AJSAudioSystem.getMixerProvider(mixerProviderServiceDescriptor);
       Mixer.Info[] mInfos=mp.getMixerInfo();
       ArrayList<AJSDeviceInfo> playbackDeviceInfoList=new ArrayList<AJSDeviceInfo>();
       for(Mixer.Info mInfo:mInfos){
           Mixer m=mp.getMixer(mInfo);
           Line.Info[] slInfos = m.getSourceLineInfo(sourceDataLineInfo);
           if (slInfos != null && slInfos.length > 0) {
               AJSDeviceInfo dInfo=new AJSDeviceInfo(mixerProviderServiceDescriptor, mInfo);
               playbackDeviceInfoList.add(dInfo);
           }
       }
       
       return playbackDeviceInfoList;
   }
   public static AJSDeviceInfo getDefaultCaptureDeviceInfo(){
       AJSDeviceInfo di=null; 
       List<MixerProviderServiceDescriptor> mpsdList=AJSAudioSystem.listMixerProviderDescriptors();
       int mpsdCount=mpsdList.size();
       for (int i=0;i<mpsdCount;i++){
           MixerProviderServiceDescriptor mpsd=mpsdList.get(i);
           if(mpsd.isProvidesCaptureDevices()){
               di=new AJSDeviceInfo(mpsd, null);
               break;
           }
       }
       return di;
   }
   
   public static AJSDevice getDefaultCaptureDevice(){
       AJSDevice d=null;
       AJSDeviceInfo di=getDefaultCaptureDeviceInfo();
       if(di!=null){
           d=getDevice(di);
       }
       return d;
   }
   public static AJSDeviceInfo getDefaultPlaybackDeviceInfo(){
       AJSDeviceInfo di=null; 
       List<MixerProviderServiceDescriptor> mpsdList=AJSAudioSystem.listMixerProviderDescriptors();
       int mpsdCount=mpsdList.size();
       for (int i=0;i<mpsdCount;i++){
           MixerProviderServiceDescriptor mpsd=mpsdList.get(i);
           if(mpsd.isProvidesPlaybackDevices()){
               di=new AJSDeviceInfo(mpsd, null);
               break;
           }
       }
       return di;
   }
   public static AJSDevice getDefaultPlaybackDevice(){
       AJSDevice d=null;
       AJSDeviceInfo di=getDefaultPlaybackDeviceInfo();
       if(di!=null){
           d=getDevice(di);
       }
       return d;
   }
   
    public static void main(String[] args){
        List<MixerProviderServiceDescriptor> mpsds=AJSAudioSystem.listMixerProviderDescriptors();
        for(MixerProviderServiceDescriptor mpsd:mpsds){
            System.out.println("AJS mixer provider of type: "+mpsd.getAudioInterfaceName()+": "+mpsd.getTitle());
        }
    }

    /**
     * @param lineInfo audio line info
     * @return capture audio line
     * @throws LineUnavailableException 
     */
    public static DataLine getLine(Info lineInfo) throws LineUnavailableException {
        List<MixerProviderServiceDescriptor> mpsdList=listMixerProviderDescriptors();
        if(TargetDataLine.class.equals(lineInfo.getLineClass())){
        	for(MixerProviderServiceDescriptor mpsd:mpsdList){
        		List<AJSDeviceInfo> availCaptureDevices=availableCaptureDeviceInfos(mpsd);
        		for(AJSDeviceInfo di:availCaptureDevices){
        			AJSDevice cd=getDevice(di);
        			if(cd==null){
        				continue;
        			}
        			Mixer m=cd.getMixer();
        			try {
        				Line line=m.getLine(lineInfo);
        				if(line!=null && line instanceof TargetDataLine){
        					return (DataLine)line;
        				}
        			} catch (LineUnavailableException e) {
        				continue;
        			}
        		}
        	}
        }else if(SourceDataLine.class.equals(lineInfo.getLineClass())){
        	for(MixerProviderServiceDescriptor mpsd:mpsdList){
        		List<AJSDeviceInfo> availCaptureDevices=availablePlaybackDeviceInfos(mpsd);
        		for(AJSDeviceInfo di:availCaptureDevices){
        			AJSDevice cd=getDevice(di);
        			if(cd==null){
        				continue;
        			}
        			Mixer m=cd.getMixer();
        			try {
        				Line line=m.getLine(lineInfo);
        				if(line!=null && line instanceof SourceDataLine){
        					return (DataLine)line;
        				}
        			} catch (LineUnavailableException e) {
        				continue;
        			}
        		}
        	}
        }
        throw new LineUnavailableException();
    }
 }
