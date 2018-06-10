//    IPS Java Utils
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.util.apps;

import ipsk.awt.ProgressListener;
import ipsk.awt.WorkerException;
import ipsk.awt.event.ProgressEvent;
import ipsk.io.VectorBufferedInputStream;
import ipsk.io.VectorBufferedOutputStream;
import ipsk.net.URLContentLoader;
import ipsk.swing.text.EditorKitMenu;
import ipsk.text.ParserException;
import ipsk.text.StringTokenizer;
import ipsk.text.Version;
import ipsk.text.VersionPattern;
import ipsk.util.apps.descriptor.ApplicationDescriptor;
import ipsk.util.apps.descriptor.ApplicationVersionDescriptor;
import ipsk.util.apps.descriptor.Change;
import ipsk.util.apps.descriptor.Change.Priority;
import ipsk.util.apps.descriptor.InstallationPackage;
import ipsk.util.apps.event.UpdateAvailableEvent;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTextField;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;

/**
 * @author klausj
 *
 */
public class UpdateManager implements ProgressListener {

	private static final boolean DEBUG=false;
	public final String APPLICATION_DESCRIPTOR_KEY="ipsk.util.apps.descriptor.url";
	public final String APPLICATION_DESCRIPTOR_INSTALLATION_TYPE="ipsk.util.apps.descriptor.installationType";
	public final String APPLICATION_DESCRIPTOR_OPTIONS="ipsk.util.apps.descriptor.options";
	private static final int DEFAULT_NETWORK_TIMEOUT=1000*60*5;
	private int netWorkTimeout=DEFAULT_NETWORK_TIMEOUT;
    private ApplicationDescriptor applicationDescriptor;
    private Version currentVersion;
    /**
	 * @return the currentVersion
	 */
	public Version getCurrentVersion() {
		return currentVersion;
	}

	private volatile URLContentLoader loader;
    private volatile VectorBufferedOutputStream loaderOutputStream;
    private Vector<UpdateManagerListener> listeners=new Vector<UpdateManagerListener>();
    private String osName;
    private String installationType;
    public class Option{
    	String key;
    	String value;
    }
    private List<Option> optionsList=null;
    private String osArch;
    private String osVersion;
    
    public enum Status {IDLE,LOADING,ERROR,SUCCESS}
    private Status status=Status.IDLE;
    private Desktop desktop=null;
    
    /**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	
	protected UpdateManager(){
        super();
        installationType=System.getProperty(APPLICATION_DESCRIPTOR_INSTALLATION_TYPE);
        String optionsString=System.getProperty(APPLICATION_DESCRIPTOR_OPTIONS);
        if(optionsString!=null){
        	optionsList=parseOptions(optionsString);
        }
        osName=System.getProperty("os.name");
        osArch=System.getProperty("os.arch");
        osVersion=System.getProperty("os.version");
        if(Desktop.isDesktopSupported()){
            desktop=Desktop.getDesktop();
        }
    }
	
	private List<Option> parseOptions(String optionsString){
		List<Option> optionsList=new ArrayList<Option>();
		String[] optionsArr=StringTokenizer.split(optionsString,',',true);
		for(String option:optionsArr){
			String key=null;
			String value=null;
			int kvSepI= option.indexOf(':');
			if(kvSepI>0){
				// key value
				// e.g. dpiAware=false
				key=option.substring(0,kvSepI);
				value=option.substring(kvSepI+1);
			}else{
				key=option;
			}
			if(key!=null){
				Option o=new Option();
				o.key=key;
				o.value=value;
				optionsList.add(o);
			}
		}
		return optionsList;
	}
	
    public UpdateManager(Version currentVersion,ApplicationDescriptor applicationDescriptor) {
        this();
        this.currentVersion=currentVersion;
        this.applicationDescriptor = applicationDescriptor;
        applyPlatformDependencies();
        synchronized (listeners) {
		
        if(this.applicationDescriptor!=null){
        	status=Status.SUCCESS;
        }
        }
    }
    
    public UpdateManager(Version currentVersion) {
        this();
        this.currentVersion=currentVersion;
        this.applicationDescriptor = null;
    }
    
    public void startLoadApplicationDescriptor(){
    	String appDescrUrlParam=System.getProperty(APPLICATION_DESCRIPTOR_KEY);
    	if(appDescrUrlParam !=null){
    		try{
    			URL appDescrUrl=new URL(appDescrUrlParam);
    			startLoadApplicationDescriptor(appDescrUrl);
    		}catch(MalformedURLException mue){
    			// 
    		}
    	}
    }
    
    public void startLoadApplicationDescriptor(URL url){
        synchronized (listeners) {
           
        if(loaderOutputStream==null){
            loaderOutputStream=new VectorBufferedOutputStream();
        }
        if(loader!=null){
            loader.removeProgressListener(this);
            loader.cancel();
            try {
                loader.close();
            } catch (Exception e) {
               // Uuuh
            	status=Status.ERROR;
            } 
        
            loader=null;
        }
        if(loader==null){
            loader=new URLContentLoader(url, loaderOutputStream, "Applicationdescriptor loader");
            loader.setTimeouts(netWorkTimeout);
            loader.addProgressListener(this);
        }
        try {
            loader.open();
            loader.start();
            status=Status.LOADING;
        } catch (WorkerException e) {
        	try {
				loader.close();
			} catch (WorkerException e1) {
				// already error
			}
        	loader=null;
           status=Status.ERROR;
        }
        
        }
    }

    public boolean desktopDownloadPossible(){
        return (desktop!=null);
    }
    
    public void desktopBrowseApplicationDownload(ApplicationVersionDescriptor applicationVersionDescriptor) throws IOException{
        if(desktop!=null){
            InstallationPackage ip=applicationVersionDescriptor.getPlatformInstallationPackage();
            if(ip!=null){
                URL downloadURL=ip.getDownloadURL();
                if(downloadURL!=null){
                    try {
                        URI downloadURI=downloadURL.toURI();
                        desktop.browse(downloadURI);
                    } catch (URISyntaxException e) {
                        throw new IOException(e);
                    }
                }
            }  
        }
    }

    public ApplicationDescriptor getApplicationDescriptor() {
        return applicationDescriptor;
    }
    
    public void applyPlatformDependencies(){
        List<ApplicationVersionDescriptor> appds=applicationDescriptor.getVersions();
        for(ApplicationVersionDescriptor appD:appds){
            InstallationPackage inPP=installationPackageForCurrentPlatform(appD);
            appD.setPlatformInstallationPackage(inPP);
        }
    }
    
    /**
     * Returns available application descriptors sorted by versions ascending.
     * @return list of application descriptors sorted by versions ascending
     */
    public List<ApplicationVersionDescriptor> sortedVersions(){
        List<ApplicationVersionDescriptor> versionsAvailSorted=new ArrayList<ApplicationVersionDescriptor>();
        versionsAvailSorted.addAll(applicationDescriptor.getVersions());
        Collections.sort(versionsAvailSorted);
        return versionsAvailSorted;
    }
    
    /**
     * Returns available application descriptors sorted by versions descending.
     * @return list of application descriptors sorted by versions descending
     */
    public List<ApplicationVersionDescriptor> sortedVersionsDesc(){
    	List<ApplicationVersionDescriptor> sVs=sortedVersions();
    	Collections.reverse(sVs);
    	return sVs;
    }
    
    
    
    public ApplicationVersionDescriptor latestVersion(){
        ApplicationVersionDescriptor lv=null;
       List<ApplicationVersionDescriptor> sVs=sortedVersions();
       int vsCount=sVs.size();
       if(vsCount>0){
           lv=sVs.get(vsCount-1);
       }
       return lv;  
    }
    
    public ApplicationVersionDescriptor latestVersionForPlatform(){
       List<ApplicationVersionDescriptor> sVs=sortedVersionsDesc();
       for(ApplicationVersionDescriptor avd:sVs){
    	   InstallationPackage ip=installationPackageForCurrentPlatform(avd);
    	   if(ip!=null){
    		 return avd;  
    	   }
       }
       return null;  
    }
    
    public List<ApplicationVersionDescriptor> newerVersions(){
       List<ApplicationVersionDescriptor> sVs=sortedVersions();
       ArrayList<ApplicationVersionDescriptor> nVs=new ArrayList<ApplicationVersionDescriptor>();
       for(ApplicationVersionDescriptor avd:sVs){
           Version av=avd.getVersion();
           if(currentVersion.compareTo(av)<0){
               nVs.add(avd);
           }
       }
       return nVs;  
    }
    
    public List<ApplicationVersionDescriptor> newerVersionsDescending(){
        List<ApplicationVersionDescriptor> sVs=sortedVersions();
        ArrayList<ApplicationVersionDescriptor> nVs=new ArrayList<ApplicationVersionDescriptor>();
        for(ApplicationVersionDescriptor avd:sVs){
            Version av=avd.getVersion();
            if(currentVersion.compareTo(av)<0){
                nVs.add(avd);
            }
        }
        return nVs;  
     }
    
    public InstallationPackage installationPackageForCurrentPlatform(ApplicationVersionDescriptor appVd){
        InstallationPackage ip=null;
        if(appVd!=null){
        
            List<InstallationPackage> aips=appVd.getInstallationPackages();
            List<InstallationPackage> fAips=new ArrayList<InstallationPackage>();
            // check mandatory attributes 
            for(InstallationPackage aip:aips){
            	String aOsName=aip.getOsName();
            	if (aOsName!=null && !aOsName.equals(osName)){
            			continue;
            	}
            	String aOsArch=aip.getOsArch();
            	if(aOsArch!=null && aOsArch.equals(osArch)){
            		continue;
            	}
            	String aOsVersionPattern=aip.getOsVersionPattern();
            	if(aOsVersionPattern!=null && !osVersion.matches(aOsVersionPattern)){
            		continue;
            	}
            	// OK add to filtered list
            	fAips.add(aip);
            }
            if(optionsList!=null && optionsList.size()>0){
            	// options may influence application behaviour and should be fulfilled
            	// (use java 8 features (streams) to make this code more readable 
            	List<InstallationPackage> fmoAips=null;
            	fmoAips=new ArrayList<InstallationPackage>(fAips);
            	fAips.clear();
            	for(Option o:optionsList){
            		// temp list for current option
            		List<InstallationPackage> fmcoAips=new ArrayList<InstallationPackage>();
            		for(InstallationPackage aip:fmoAips){
            			Map<String,String> aipOpts=aip.getOptions();
            			if(aipOpts!=null && aipOpts.containsKey(o.key) && aipOpts.get(o.key).equals(o.value)){            				
            				fmcoAips.add(aip); 
            			}
            		}
            		if(fmcoAips.size()>0){
            			// if we found at least one package satisfying the option use them
            			fmoAips=new ArrayList<InstallationPackage>(fmcoAips);
            		}else{
            			// continue with current option not satisfied
            		}
            	}
            	fAips=fmoAips;
            }
            if(installationType!=null){
            	List<InstallationPackage> fAInAips=new ArrayList<InstallationPackage>();
            	
            	// finally try to match installation type 
            	for(InstallationPackage aip:fAips){
            		// Installation type should match
            		if(installationType.equals(aip.getType())){
            			fAInAips.add(aip);
            		}
            	}
            	if(fAInAips.size()>0){
            		// if we found at least one package satisfying the installation type use them
            		fAips=fAInAips;
            	}
            }
            if(fAips.size()==0){
            	return null;
            }else{
            	return fAips.get(0);
            }
        }
        return null;
        
     }
//    public InstallationPackage installationPackageForCurrentPlatform(ApplicationVersionDescriptor appVd){
//        InstallationPackage ip=null;
//        if(appVd!=null){
//            List<InstallationPackage> aips=appVd.getInstallationPackages();
//             
//            // TODO new algorithm: recursive search for best package 
//            // or this is a good candidate for Java 8 lambdas
//            
//            for(InstallationPackage aip:aips){
//            	String instType=aip.getType();
//                String aOsName=aip.getOsName();
//                String aOsArch=aip.getOsArch();
//                String aOsVersionPattern=aip.getOsVersionPattern();
//                if(aOsName != null && instType!=null && aOsArch!=null && aOsVersionPattern!=null && osVersion !=null && aOsName.equals(osName) && instType.equals(installationType) && aOsArch.equals(osArch) && osVersion.matches(aOsVersionPattern)){
//                    // full match
//                    return aip;
//                }
//            }
//            
//            for(InstallationPackage aip:aips){
//            	String instType=aip.getType();
//                String aOsName=aip.getOsName();
//                String aOsArch=aip.getOsArch();
//                String aOsVersionPattern=aip.getOsVersionPattern();
//                if(aOsName != null && instType==null && aOsArch!=null && aOsVersionPattern!=null && osVersion !=null && aOsName.equals(osName) && aOsArch.equals(osArch) && osVersion.matches(aOsVersionPattern)){
//                    // full OS match, no specific installation type
//                    return aip;
//                }
//            }
//            
//            for(InstallationPackage aip:aips){
//                String aOsName=aip.getOsName();
//                String instType=aip.getType();
//                String aOsVersionPattern=aip.getOsVersionPattern();
//                if(aOsName != null  && instType!=null && aOsVersionPattern!=null && osVersion !=null && aOsName.equals(osName) && instType.equals(installationType) && osVersion.matches(aOsVersionPattern)){
//                    // OS version match
//                    return aip;
//                }
//            }
//            
//            for(InstallationPackage aip:aips){
//                String aOsName=aip.getOsName();
//                String instType=aip.getType();
//                String aOsVersionPattern=aip.getOsVersionPattern();
//                if(aOsName != null  && instType==null && aOsVersionPattern!=null && osVersion !=null && aOsName.equals(osName) && osVersion.matches(aOsVersionPattern)){
//                    // OS version match
//                    return aip;
//                }
//            }
//            for(InstallationPackage aip:aips){
//                String aOsName=aip.getOsName();
//                String instType=aip.getType();
//                String aOsArch=aip.getOsArch();
//                if(aOsName != null && instType!=null && aOsArch!=null && aOsName.equals(osName) && instType.equals(installationType) && aOsArch.equals(osArch)){
//                    // OS architecture and installation type match
//                    return aip;
//                }
//            }
//            for(InstallationPackage aip:aips){
//                String aOsName=aip.getOsName();
//                String instType=aip.getType();
//                String aOsArch=aip.getOsArch();
//                if(aOsName != null && instType==null && aOsArch!=null && aOsName.equals(osName) && aOsArch.equals(osArch)){
//                    // OS architecture match
//                    return aip;
//                }
//            }
//            for(InstallationPackage aip:aips){
//                String aOsName=aip.getOsName();
//                String instType=aip.getType();
//                String aOsArch=aip.getOsArch();
//                if(aOsName != null && instType!=null && aOsArch==null && aOsName.equals(osName) && instType.equals(installationType)){
//                    // OS match
//                    return aip;
//                }
//            }
//           
//            for(InstallationPackage aip:aips){
//                String aOsName=aip.getOsName();
//                String aOsArch=aip.getOsArch();
//                if(aOsName != null && aOsArch==null && aOsName.equals(osName)){
//                    // OS match
//                    return aip;
//                }
//            }
//           
//            for(InstallationPackage aip:aips){
//                String aOsName=aip.getOsName();
//                String aOsArch=aip.getOsArch();
//                if(aOsName == null && aOsArch==null){
//                    // Full platform independent package
//                    return aip;
//                }
//            }
//            
//            
//        }
//        return ip;
//    }
    
    public boolean importantUpdatesAvailable(){
        List<ApplicationVersionDescriptor> versionsAvail=newerVersions();
        for(ApplicationVersionDescriptor avd:versionsAvail){
            Version av=avd.getVersion();
            if(currentVersion.compareTo(av)<0){
                List<Change> changes=avd.getChanges();
                for(Change change:changes){
                    VersionPattern affects=change.getAffectsVersions();
                    if(affects==null || affects.matches(currentVersion)){
                        Priority pr=change.getPriority();
                        if(pr!=null && (pr.equals(Priority.STRONGLY_RECOMMENDED))){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public Change.Priority updatePriority(){
    	Change.Priority p=null;
    	List<ApplicationVersionDescriptor> versionsAvail=newerVersions();
    	for(ApplicationVersionDescriptor avd:versionsAvail){

    		List<Change> changes=avd.getChanges();
    		for(Change change:changes){
    			VersionPattern affects=change.getAffectsVersions();
    			if(affects==null || affects.matches(currentVersion)){
    				Priority pr=change.getPriority();
    				if(pr!=null){
    					if(p==null){
    						p=pr;
    					}else{
    						if(pr.compareTo(p)>0){
    							p=pr;
    						}
    					}
    				}
    			}
    		}
    	}
    		return p;
    	}
    
    public ApplicationVersionDescriptor updateAvailable(){
       ApplicationVersionDescriptor lv=latestVersion();
       Version lvv=lv.getVersion();
       if(currentVersion.compareTo(lvv)<0){
           return lv;
       }
       
        return null;
    }
    
    public ApplicationVersionDescriptor updateAvailableForPlatform(){
        ApplicationVersionDescriptor lv=latestVersionForPlatform();
        if(lv==null){
        	return null;
        }
        Version lvv=lv.getVersion();
        if(currentVersion.compareTo(lvv)<0){
            return lv;
        }
        
         return null;
     }

    /* (non-Javadoc)
     * @see ipsk.awt.ProgressListener#update(ipsk.awt.event.ProgressEvent)
     */
    public void update(ProgressEvent progressEvent) {
    	if(DEBUG) System.out.println(progressEvent);
    	if(progressEvent.getProgressStatus().isDone()){
    		synchronized(listeners){
    			InputStream is=new VectorBufferedInputStream(loaderOutputStream.getVectorBuffer());
    			JAXBContext jc;
    			try {
//    				jc = JAXBContext.newInstance(ApplicationDescriptor.class);
//    				Unmarshaller u = jc.createUnmarshaller();
//    				Object applicationDescriptorObj=u.unmarshal(is);
//    				if(! (applicationDescriptorObj instanceof ApplicationDescriptor)){
//    					throw new JAXBException("Unmarshaller returned wrong type.");
//    				}
    				applicationDescriptor=JAXB.unmarshal(is,ApplicationDescriptor.class);
//    				applicationDescriptor=(ApplicationDescriptor)applicationDescriptorObj;
//    			} catch (JAXBException e) {
    			}catch(DataBindingException e){
    				e.printStackTrace();
    				status=Status.ERROR;
    				UpdateManagerEvent ee=new UpdateManagerEvent(this,status);
    				fireEvent(ee);
    				return;
    			}

    			if (applicationDescriptor!=null){
    				applyPlatformDependencies();
    				status=Status.SUCCESS;
    				UpdateManagerEvent dle=new UpdateManagerEvent(this,status);
    				fireEvent(dle);
    				ApplicationVersionDescriptor uvd=updateAvailableForPlatform();
    				if(uvd!=null){
    					Change.Priority updPriority=updatePriority();
    					UpdateAvailableEvent uae=new UpdateAvailableEvent(this, status,updPriority);
    					fireEvent(uae);
    				}
    			}
    		}
    	}else if(progressEvent.getProgressStatus().isError()){
    		synchronized (listeners) {
    		status=Status.ERROR;
			UpdateManagerEvent dle=new UpdateManagerEvent(this,status);
			fireEvent(dle);
    		}
    	}
    }
    
    private void fireEvent(UpdateManagerEvent event){
    	// avoid concurrent modification exceptions
    	List<UpdateManagerListener> umListeners=new ArrayList<UpdateManagerListener>(listeners);
        for(UpdateManagerListener l:umListeners){
//        	UpdateManagerListener l=lIt.next();
            l.update(event);
        }
    }
    
    public void addUpdateManagerListener(UpdateManagerListener l){
        listeners.add(l);
    }
    public void removeUpdateManagerListener(UpdateManagerListener l){
        listeners.remove(l);
    }

    /**
     * Usage: UpdateManager appVersion applicationDescriptorURL 
      * @param args URL of 
     */
    public static void main(String[] args){
        Version currentVersion;
        try {
            currentVersion = Version.parseString(args[0]);
            URL appDescrUrl=null;
            if(args.length>1){
            	appDescrUrl=new URL(args[1]);
            }
            final UpdateManager um=new UpdateManager(currentVersion);
            um.addUpdateManagerListener(new UpdateManagerListener() {
                
            	public void update(UpdateManagerEvent event) {
            		if(UpdateManager.Status.SUCCESS.equals(event.getStatus())){
            			System.out.println(event);
            			ApplicationVersionDescriptor ad=um.latestVersionForPlatform();
            			System.out.println(ad);
            			InstallationPackage ip=um.installationPackageForCurrentPlatform(ad);
            			if(ip!=null){
            				System.out.println("Download URL: "+ip.getDownloadURL());
            			}
            		}
            	}
            });
            
            if(appDescrUrl!=null){
            	um.startLoadApplicationDescriptor(appDescrUrl);
            }else{
            	um.startLoadApplicationDescriptor();
            }
        } catch (ParserException e) {
           
            e.printStackTrace();
        } catch (MalformedURLException e) {
           
            e.printStackTrace();
        }
       
        
    }
  

}
