//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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

package ipsk.util.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.JAXB;

/**
 * Inspects classpath for service descriptors.
 * Service descriptors describe a particular service.
 * Loading the service descriptor does not load the service itself (lazy loading). 
 *          
 * @author Klaus Jaensch
 *
 */
public class ServiceDescriptorsInspector<D extends ServiceDescriptor,S> extends ServicesInspector<S>{
	private final static boolean DEBUG=false;
	private final static String META_PATH="META-INF";
//	private final static String MANIFEST_PATH=META_PATH+"MANIFEST.MF";
	private final static String META_SERVICES_PATH=META_PATH+"/services/";
	private Class<S> serviceClass;
	public static final boolean DEFAULT_UNIQUE=true;
	private boolean unique=DEFAULT_UNIQUE;
	
	/**
	 * Create service inspector for interface class.
	 * 
	 * @param serviceClass service class (same as parameter type S)
	 */
	public ServiceDescriptorsInspector(Class<S> serviceClass){
		super();
		this.serviceClass=serviceClass;
	}
	
	public ServiceDescriptorsInspector(){
		this(null);
	}
    
  
  
   
    public List<D> getTypedServiceDescriptors(Class<S> providerClass,Class<? extends ServiceDescriptor> serviceDescriptorClass,ClassLoader cl, boolean loadImplementationClass)throws IOException{
        String providerClassname=providerClass.getName();
        
        List<D> serviceDescriptorList=new ArrayList<D>();
        HashSet<String> foundImplClassnames=new HashSet<String>();
        // check for particular XML service descriptors first
        Enumeration<URL> psdXmlUrls=cl.getResources(META_SERVICES_PATH+serviceDescriptorClass.getName()+".xml");
        
        while(psdXmlUrls.hasMoreElements()){
            URL sdXmlUrl=psdXmlUrls.nextElement();
            if(DEBUG)System.out.println(sdXmlUrl);
            // Usually a JAR file URL, e.g.: 
            // jar:file:/usr/lib/jvm/java-1.5.0-sun-1.5.0_15/jre/lib/rt.jar!/META-INF/services/javax.sound.sampled.spi.FormatConversionProvider

            // try to get the corresponding manifest file

            InputStream sdXmlIs=sdXmlUrl.openStream();
//            List<String> implClassList=readServiceImplementorClassnames(sdIs);
            // read Xml
//            Object sdInstance=serviceDescriptorClass.newInstance();
            try{
            D sd=(D)JAXB.unmarshal(sdXmlIs,serviceDescriptorClass);
            serviceDescriptorList.add(sd);
            }catch(ClassCastException cce){
                // ignore entry
            }
          
            //           implClassnameList.addAll(implClassList);
        }
        // check for particular service descriptors first
        Enumeration<URL> psdUrls=cl.getResources(META_SERVICES_PATH+serviceDescriptorClass.getName());

        while(psdUrls.hasMoreElements()){
            URL sdUrl=psdUrls.nextElement();
            if(DEBUG)System.out.println(sdUrl);
            // Usually a JAR file URL, e.g.: 
            // jar:file:/usr/lib/jvm/java-1.5.0-sun-1.5.0_15/jre/lib/rt.jar!/META-INF/services/javax.sound.sampled.spi.FormatConversionProvider

            // try to get the corresponding manifest file

            InputStream sdIs=sdUrl.openStream();
            List<String> implClassList=readServiceImplementorClassnames(sdIs);
            for(String implClassname:implClassList){
                Class<D> sdClass = null;
                
                try {
                    sdClass = (Class<D>) Class.forName(implClassname);
                    if(serviceDescriptorClass.isAssignableFrom(sdClass)){
                    D sd=sdClass.newInstance();
//                    Class<?> sdServiceClass=sd.getServiceClass();
//                    //                  String sdProviderClassname=sdServiceClass.getName();
//                    if(providerClass.equals(sdServiceClass)){
//                        if((!foundImplClassnames.contains(implClassname) && !serviceDescriptorList.contains(sd)) || !unique){
                            serviceDescriptorList.add(sd);
//                            foundImplClassnames.add(implClassname);
//                        }
//                    }
                    }
                } catch (ClassNotFoundException e) {
                    // service plugin ignored
                }catch (InstantiationException e) {

                } catch (IllegalAccessException e) {

                }

            }
            //           implClassnameList.addAll(implClassList);
        }

     // check for service descriptors next
        Enumeration<URL> sdUrls=cl.getResources(META_SERVICES_PATH+ServiceDescriptor.class.getName());

        while(sdUrls.hasMoreElements()){
            URL sdUrl=sdUrls.nextElement();
            if(DEBUG)System.out.println(sdUrl);
            String sdPath=sdUrl.getPath();
            int jarSepPos=sdPath.indexOf("!");
            URL jarUrl=null;
            if(jarSepPos>0){
                String jarPath=sdPath.substring(0,jarSepPos);
           
                jarUrl=new URL(sdUrl.getProtocol(),sdUrl.getHost(),jarPath);
            
            }
            // Usually a JAR file URL, e.g.: 
            // jar:file:/usr/lib/jvm/java-1.5.0-sun-1.5.0_15/jre/lib/rt.jar!/META-INF/services/javax.sound.sampled.spi.FormatConversionProvider

            // try to get the corresponding manifest file

            InputStream sdIs=sdUrl.openStream();
            List<String> implClassList=readServiceImplementorClassnames(sdIs);
            for(String implClassname:implClassList){
                Class<D> sdClass = null;
                
                try {
                    sdClass = (Class<D>) Class.forName(implClassname);
                    if(serviceDescriptorClass.isAssignableFrom(sdClass)){
                    D sd=sdClass.newInstance();
                   
//                    sd.setPackageURL(jarUrl);
//                    Class<?> sdServiceClass=sd.getServiceClass();
                    String sdServiceClassName=sd.getServiceImplementationClassname();
                    Class<?> sdServiceClass=Class.forName(sdServiceClassName);
                    if(providerClass.isAssignableFrom(sdServiceClass)){

                        if((!foundImplClassnames.contains(implClassname) && !serviceDescriptorList.contains(sd)) || !unique){
                            serviceDescriptorList.add(sd);
                            foundImplClassnames.add(implClassname);
                        }
                    }
                    }
                } catch (ClassNotFoundException e) {
                    // service plugin ignored
                    if(DEBUG)e.printStackTrace();
                }catch (NoClassDefFoundError e) {
                    // service plugin ignored
                    if(DEBUG)e.printStackTrace();
                }catch (InstantiationException e) {
                    if(DEBUG)e.printStackTrace();
                } catch (IllegalAccessException e) {
                    if(DEBUG)e.printStackTrace();
                }

            }
            //           implClassnameList.addAll(implClassList);
        }
        
        
        // next standard services 
        //      Enumeration<URL> metaUrls=cl.getResources(META_PATH);
        //      while(metaUrls.hasMoreElements()){
        //          URL metaUrl=metaUrls.nextElement();
        //          if(DEBUG)System.out.println(metaUrl);
        //      }
        //          URL manifestUrl=new URL(metaUrl,"MANIFEST.MF");
        //          cl.getResourceAsStream(manifestUrl.toString());
        //          if(DEBUG)System.out.println("Manifest: "+manifestUrl);
        //          InputStream mfStream=null;
        //          try{
        //              mfStream=manifestUrl.openStream();
        //          }catch(IOException ioe){
        //              //likely not found
        //          }
        //          if(mfStream!=null){
        //              Manifest mf=new Manifest(mfStream);
        //              Attributes attrs=mf.getMainAttributes();
        //              Set<Entry<Object,Object>> attrEntries=attrs.entrySet();
        //              for(Entry<Object,Object> e:attrEntries){
        //                  if(DEBUG)System.out.println(e);
        //              }
        //          }
        //          URL servicesUrl=new URL(metaUrl,"services/"+providerClassname);
        //          cl.getResourceAsStream(servicesUrl.toString());
        //          if(DEBUG)System.out.println("Services: "+servicesUrl);
        //          InputStream servicesStream=null;
        //          try{
        //              servicesStream=servicesUrl.openStream();
        //          }catch(IOException ioe){
        //              //likely not found
        //          }
        //          // Usually a JAR file URL, e.g.: 
        //          // jar:file:/usr/lib/jvm/java-1.5.0-sun-1.5.0_15/jre/lib/rt.jar!/META-INF/services/javax.sound.sampled.spi.FormatConversionProvider
        //
        //          //                InputStream is=url.openStream();
        //          if(servicesStream!=null){
        //              List<String> implClassList=readServiceImplementorClassnames(servicesStream);
        //              for(String implClassname:implClassList){
        //                  if(!implClassnameList.contains(implClassname) || !unique){
        //                      implClassnameList.add(implClassname);
        //                  }
        //              }
        ////              implClassnameList.addAll(implClassList);
        //          }
        //          if(DEBUG){
        //                         for(String cn:implClassnameList){
        //                             System.out.println("Class: "+ cn);
        //                         }
        //                       }
        //      }


        //               Enumeration<URL> urls=cl.getResources(META_SERVICES_PATH+providerClassname);
        //               while(urls.hasMoreElements()){
        //               URL url=urls.nextElement();
        //                      if(DEBUG)System.out.println(url);
        //                   // Usually a JAR file URL, e.g.: 
        //                   // jar:file:/usr/lib/jvm/java-1.5.0-sun-1.5.0_15/jre/lib/rt.jar!/META-INF/services/javax.sound.sampled.spi.FormatConversionProvider
        //                  
        //                      // try to get the corresponding manifest file
        //                      URL manifestUrl=new URL(url,"../MANIFEST.MF");
        //                    cl.getResourceAsStream(manifestUrl.toString());
        //                    if(DEBUG)System.out.println("Manifest: "+manifestUrl);
        //                    InputStream mfStream=null;
        //                    try{
        //                        mfStream=manifestUrl.openStream();
        //                    }catch(IOException ioe){
        //                        //likely not found
        //                    }
        //                    Manifest mf=null;
        //                    if(mfStream!=null){
        //                        mf=new Manifest(mfStream);
        //                    }
        //                      
        //                   InputStream is=url.openStream();
        //                   List<String> implClassList=readServiceImplementorClassnames(is);
        //                   for(String implClassname:implClassList){
        //                       ServiceDescriptor sd=new ManifestServiceDescriptor(serviceClass,implClassname, mf);
        //                       if(!implClassnameList.contains(sd) || !unique){
        //                           implClassnameList.add(sd);
        //                       }
        //                   }
        //      //             implClassnameList.addAll(implClassList);
        //               }

        Enumeration<URL> urls=cl.getResources(META_SERVICES_PATH+providerClassname);
        while(urls.hasMoreElements()){
            URL url=urls.nextElement();
            if(DEBUG)System.out.println(url);
            //        // Usually a JAR file URL, e.g.: 
            //        // jar:file:/usr/lib/jvm/java-1.5.0-sun-1.5.0_15/jre/lib/rt.jar!/META-INF/services/javax.sound.sampled.spi.FormatConversionProvider
            //       
            InputStream is=url.openStream();
            List<String> implClassList=readServiceImplementorClassnames(is);
            for(String implClassname:implClassList){
                D serviceDescriptor=null;
                if(loadImplementationClass){
                    Class<S> sImplClass = null;
                    try {
                        sImplClass = (Class<S>) Class.forName(implClassname);
                      
                        Class<?>[] sImplClassIfs=sImplClass.getInterfaces();
                        for(Class<?> sImplClassIf:sImplClassIfs){
                            if(ServiceDescriptorProvider.class.equals(sImplClassIf)){
                                ServiceDescriptorProvider<D> sdP = null;
                                try {
                                    sdP = (ServiceDescriptorProvider<D>) sImplClass.newInstance();
                                    D sd=sdP.getServiceDescriptor();
                                    if(sd.getClass().isAssignableFrom(serviceDescriptorClass)){
                                        serviceDescriptor=sd;
                                    }
                                } catch (InstantiationException e) {
                                    e.printStackTrace();
                                    break;
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                    break;
                                }
                            }
                        }
//                        if(serviceDescriptor==null && ServiceDescriptor.class.equals(serviceDescriptorClass)){
//                            Package p=sImplClass.getPackage();
//                            serviceDescriptor=new PackageServiceDescriptor<D,S>(serviceClass, implClassname,p);
//                            
//                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    
                }
//                else{
//                    if(ServiceDescriptor.class.equals(serviceDescriptorClass)){
//                        // try to get the corresponding manifest file
//                        URL manifestUrl=new URL(url,"../MANIFEST.MF");
//                        cl.getResourceAsStream(manifestUrl.toString());
//                        if(DEBUG)System.out.println("Manifest: "+manifestUrl);
//                        InputStream mfStream=null;
//                        try{
//                            mfStream=manifestUrl.openStream();
//                        }catch(IOException ioe){
//                            //likely not found
//                        }
//                        Manifest mf=null;
//                        if(mfStream!=null){
//                            mf=new Manifest(mfStream);
//                        }
//                        serviceDescriptor=new ManifestServiceDescriptor(serviceClass,implClassname, mf);
//                    }
//                }

                
                if((serviceDescriptor !=null && serviceDescriptorClass.isAssignableFrom(serviceDescriptor.getClass())&& !foundImplClassnames.contains(implClassname) && !serviceDescriptorList.contains(serviceDescriptor)) || !unique){
                    serviceDescriptorList.add(serviceDescriptor);
                    foundImplClassnames.add(implClassname);
                }
            }
        }
            //            if(!implClassnameList.contains(sd) || !unique){
            //                implClassnameList.add(sd);
            //            }
            //        }
            ////             implClassnameList.addAll(implClassList);
            //    }


            return serviceDescriptorList;
        }
 
    public List<D> getTypedServiceDescriptors(Class<? extends ServiceDescriptor> serviceDescriptorClass)throws IOException{
        
        ClassLoader cl=Thread.currentThread().getContextClassLoader();
        
        List<D> contextList=getTypedServiceDescriptors(serviceClass,serviceDescriptorClass,cl,false);
        return  contextList;
    }
    public List<D> getTypedServiceDescriptors()throws IOException{
       
        ClassLoader cl=Thread.currentThread().getContextClassLoader();
        
        List<D> contextList=getTypedServiceDescriptors(serviceClass,ServiceDescriptor.class,cl,false);
        return  contextList;
    }
    
// public List<ServiceDescriptor> getServiceDescriptors(Class<S> providerClass)throws IOException{
// 
//        ClassLoader cl=Thread.currentThread().getContextClassLoader();
//        
//        List<ServiceDescriptor> contextList=getServiceDescriptors(providerClass,cl,false);
//        return contextList;
//    }
    
    
//    private List<Class<? extends S>> getServiceImplementorClasses(String providerClassName) throws IOException, ClassNotFoundException{
//         ArrayList<Class<? extends S>> classList=new ArrayList<Class<? extends S>>();
//         List<String> classNamesList=getServiceImplementorClassnames(providerClassName);
//         for(String s:classNamesList){
//        	 @SuppressWarnings("unchecked")
//             Class<S> ic = (Class<S>) Class.forName(s);
//             classList.add(ic);
//         }
//         return classList;
//    }
    
 
    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
    
  
}
