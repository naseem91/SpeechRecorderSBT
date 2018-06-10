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
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.sound.sampled.spi.FormatConversionProvider;

/**
 * Inspects classpath for service implementation classes.
 * For instance finds JavaSound AudioFileReader implementations.
 * 
 * Note: With JRE7 java.util.ServiceLoader there is an implementation with similar functionality
 *          but we need it with JRE5
 *          
 * @author Klaus Jaensch
 *
 */
public class ServicesInspector<S> {
	private final static boolean DEBUG=false;
	private final static String META_PATH="META-INF";
	private final static String MANIFEST_PATH=META_PATH+"MANIFEST.MF";
	private final static String META_SERVICES_PATH=META_PATH+"/services/";
	private Class<S> serviceClass;
	public static final boolean DEFAULT_UNIQUE=true;
	private boolean unique=DEFAULT_UNIQUE;
	
	/**
	 * Create service inspector for interface class.
	 * 
	 * @param serviceClass service class (same as parameter type S)
	 */
	public ServicesInspector(Class<S> serviceClass){
		super();
		this.serviceClass=serviceClass;
	}
	
	public ServicesInspector(){
		this(null);
	}
    
    protected List<String> readServiceImplementorClassnames(InputStream is) throws IOException{
        ArrayList<String> list= new ArrayList<String>();
        Reader isReader=new InputStreamReader(is);
        LineNumberReader lnr=new LineNumberReader(isReader);
        String l=null;
        try {
            while((l=lnr.readLine())!=null){
                
                int commentIndex=l.indexOf("#");
                if(commentIndex>=0){
                    l=l.substring(0,commentIndex);
                }
                l=l.trim();
                if(!l.equals("")){
                    list.add(l);
                }
            }
        } catch (IOException e) {
            throw e;
        }finally{
            lnr.close();
        }
     return list;   
    }

    
  
    public List<ServiceDescriptor> getServiceDescriptors()throws IOException{
        
        ClassLoader cl=Thread.currentThread().getContextClassLoader();
        
        List<ServiceDescriptor> contextList=getServiceDescriptors(serviceClass,ServiceDescriptor.class,cl,false);
        return  contextList;
    }
    public List<ServiceDescriptor> getServiceDescriptors(Class<S> providerClass,Class<? extends ServiceDescriptor> serviceDescriptorClass,ClassLoader cl, boolean loadImplementationClass)throws IOException{
        String providerClassname=providerClass.getName();
        
        ArrayList<ServiceDescriptor> serviceDescriptorList=new ArrayList<ServiceDescriptor>();
        HashSet<String> foundImplClassnames=new HashSet<String>();
        
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
                Class<? extends ServiceDescriptor> sdClass = null;
                
                try {
                    sdClass = (Class<? extends ServiceDescriptor>) Class.forName(implClassname);
                    if(serviceDescriptorClass.isAssignableFrom(sdClass)){
                    ServiceDescriptor sd=sdClass.newInstance();
                    String sdServiceClassName=sd.getServiceImplementationClassname();
                    Class<?> sdServiceClass=Class.forName(sdServiceClassName);
                    if(providerClass.equals(sdServiceClass)){
                        if((!foundImplClassnames.contains(implClassname) && !serviceDescriptorList.contains(sd)) || !unique){
                            serviceDescriptorList.add(sd);
                            foundImplClassnames.add(implClassname);
                        }
                    }
                    }
                } catch (ClassNotFoundException e) {
                    // service plugin ignored
                }catch (InstantiationException e) {
                 // service plugin ignored
                } catch (IllegalAccessException e) {
                 // service plugin ignored
                }catch(LinkageError le){
                 // service plugin ignored
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
                Class<? extends ServiceDescriptor> sdClass = null;
                
                try {
                    sdClass = (Class<? extends ServiceDescriptor>) Class.forName(implClassname);
                    if(serviceDescriptorClass.isAssignableFrom(sdClass)){
                    ServiceDescriptor sd=sdClass.newInstance();
                   
//                    sd.setPackageURL(jarUrl);
                    String sdServiceClassName=sd.getServiceImplementationClassname();
                    Class<?> sdServiceClass=Class.forName(sdServiceClassName);
                    if(providerClass.equals(sdServiceClass)){

                        if((!foundImplClassnames.contains(implClassname) && !serviceDescriptorList.contains(sd)) || !unique){
                            serviceDescriptorList.add(sd);
                            foundImplClassnames.add(implClassname);
                        }
                    }
                    }
                } catch (ClassNotFoundException e) {
                    // service plugin ignored
                }catch (InstantiationException e) {
                 // service plugin ignored
                } catch (IllegalAccessException e) {
                 // service plugin ignored
                }catch(LinkageError le){
                 // service plugin ignored
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
                ServiceDescriptor serviceDescriptor=null;
                if(loadImplementationClass){
                    Class<S> sImplClass = null;
                    try {
                        sImplClass = (Class<S>) Class.forName(implClassname);
                      
                        Class<?>[] sImplClassIfs=sImplClass.getInterfaces();
                        for(Class<?> sImplClassIf:sImplClassIfs){
                            if(ServiceDescriptorProvider.class.equals(sImplClassIf)){
                                ServiceDescriptorProvider sdP = null;
                                try {
                                    sdP = (ServiceDescriptorProvider) sImplClass.newInstance();
                                    ServiceDescriptor sd=sdP.getServiceDescriptor();
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
                        if(serviceDescriptor==null && ServiceDescriptor.class.equals(serviceDescriptorClass)){
                            Package p=sImplClass.getPackage();
                            serviceDescriptor=new PackageServiceDescriptor(serviceClass, implClassname,p);
                            
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    
                }else{
                    if(ServiceDescriptor.class.equals(serviceDescriptorClass)){
                        // try to get the corresponding manifest file
                        URL manifestUrl=new URL(url,"../MANIFEST.MF");
                        cl.getResourceAsStream(manifestUrl.toString());
                        if(DEBUG)System.out.println("Manifest: "+manifestUrl);
                        InputStream mfStream=null;
                        try{
                            mfStream=manifestUrl.openStream();
                        }catch(IOException ioe){
                            //likely not found
                        }
                        Manifest mf=null;
                        if(mfStream!=null){
                            mf=new Manifest(mfStream);
                        }
                        serviceDescriptor=new ManifestServiceDescriptor(serviceClass,implClassname, mf);
                    }
                }

                
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
    /**
     * Returns list of class names implementing the service.
     * @return  list of class names implementing the service
     * @throws IOException
     */
    public List<String> getServiceImplementorClassnames()throws IOException{
    	// how to get the actual generic type at runtime??
    	//
//    	TypeVariable<?>[] typeParams=this.getClass().getTypeParameters();
//    	for(TypeVariable<?> tp:typeParams){
//    	GenericDeclaration gd=tp.getGenericDeclaration();
//    	System.out.println(gd.getTypeParameters().length);
//    	TypeVariable<?>[] tv=gd.getTypeParameters();
//    	System.out.println(tv[0]);
//  
//    	Type[] bounds=tv[0].getBounds();
//    	for(Type b:bounds){
//    		
//    		System.out.println(b);
//    	}
//    	}
//    	
//    	Class<? extends ServicesInspector> c= this.getClass();
//    	if((Type)c instanceof ParameterizedType){
//    		System.out.println("Param");
//    	}
//    	
    	
    	return getServiceImplementorClassnames(serviceClass.getName());
    }
    
    private List<String> getServiceImplementorClassnames(String providerClassname,ClassLoader cl)throws IOException{

        ArrayList<String> implClassnameList=new ArrayList<String>();
        if(DEBUG)System.out.println("Searching for services:");
            Enumeration<URL> urls=cl.getResources(META_SERVICES_PATH+providerClassname);
            while(urls.hasMoreElements()){
                URL url=urls.nextElement();
                if(DEBUG)System.out.println(url);
                // Usually a JAR file URL, e.g.: 
                // jar:file:/usr/lib/jvm/java-1.5.0-sun-1.5.0_15/jre/lib/rt.jar!/META-INF/services/javax.sound.sampled.spi.FormatConversionProvider

                InputStream is=url.openStream();
                List<String> implClassList=readServiceImplementorClassnames(is);
                for(String implClassname:implClassList){
                    if(!implClassnameList.contains(implClassname) || !unique){
                        implClassnameList.add(implClassname);
                    }
                }
            }
            if(DEBUG){
                for(String cn:implClassnameList){
                    System.out.println("Class: "+ cn);
                }
            }
            return implClassnameList;
        }
    
 
    protected List<String> getServiceImplementorClassnames(String providerClassname)throws IOException{
    	
//    	ClassLoader cl=ClassLoader.getSystemClassLoader();
//    	ClassLoader cl=ClassLoader.
//    	SecurityManager sm=System.getSecurityManager();
//    	sm.
//    
//    	List<String> list=getServiceImplementorClassnames(providerClassname,cl);
    	ClassLoader cl=Thread.currentThread().getContextClassLoader();
    	List<String> contextList=getServiceImplementorClassnames(providerClassname,cl);
//    	for(String clName:contextList){
//    		if(!list.contains(clName)){
//    			list.add(clName);
//    		}
//    	}
//    	
    	return contextList;
    }
    
    public List<Class<? extends S>> getServiceImplementorClasses() throws IOException, ClassNotFoundException{
        
        return getServiceImplementorClasses(false);
    }
    
    public List<Class<? extends S>> getServiceImplementorClasses(boolean ignoreErrors) throws IOException, ClassNotFoundException{
        ArrayList<Class<? extends S>> classList=new ArrayList<Class<? extends S>>();
        List<String> classNamesList=getServiceImplementorClassnames(serviceClass.getName());
        for(String s:classNamesList){
        	if(ignoreErrors){
        		try{
        			@SuppressWarnings("unchecked")
                    Class<S> ic = (Class<S>)Class.forName(s);
                    classList.add(ic);
        		}catch(Error err){
        			// OK continue
        		}
        	}else{
        		@SuppressWarnings("unchecked")
        		Class<S> ic = (Class<S>)Class.forName(s);
        		classList.add(ic);
        	}
        }
        return classList;
    }
    
    
    private List<Class<? extends S>> getServiceImplementorClasses(String providerClassName) throws IOException, ClassNotFoundException{
         ArrayList<Class<? extends S>> classList=new ArrayList<Class<? extends S>>();
         List<String> classNamesList=getServiceImplementorClassnames(providerClassName);
         for(String s:classNamesList){
        	 @SuppressWarnings("unchecked")
             Class<S> ic = (Class<S>) Class.forName(s);
             classList.add(ic);
         }
         return classList;
    }
    
 
    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        Class<FormatConversionProvider> EXAMPLE_SERVICE=FormatConversionProvider.class;
        ServicesInspector<FormatConversionProvider> pluginManager=new ServicesInspector<FormatConversionProvider>(FormatConversionProvider.class);
//        final String EXAMPLE_SERVICE="javax.sound.sampled.spi.FormatConversionProvider";
//        final String EXAMPLE_SERVICE="ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter";
//        final Class<?> exampleService=Class.forName(EXAMPLE_SERVICE);
        System.out.println("Example: List of service providers for "+EXAMPLE_SERVICE+"\n");
//        try {
//            List<String> list=pluginManager.getServiceImplementorClassnames(EXAMPLE_SERVICE);
//            for(String s:list){
//                System.out.println(s);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        
     
        
        

    }


}
