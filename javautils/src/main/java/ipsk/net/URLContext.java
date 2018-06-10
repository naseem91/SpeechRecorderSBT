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

package ipsk.net;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import ipsk.text.StringSequenceBuilder;

/**
 * Generates absolute URLs from relative URLs in a given context.
 * This class works much like @see java.net.URL#Constructor(URL context,String spec) .
 * @author klausj
 *
 */
public class URLContext {

	private URL context;

	public URLContext() {
		super();
	}

	/**
	 * Creates an URL context.
	 * @param context the context
	 */
	public URLContext(URL context) {
		super();
		// Important note:
		// The URL must end with "/" to indicate a directory, otherwise URI.resolve will not work as expected
		// If the URL is build from a File object (file.toURI().toURL() ) the directory must exist when the URL is build to get a "directory" URL  
		this.context = context;
	}
	
	/**
			 * Creates a URL by parsing the given URL within the specified context.
			 * If context is <code>null</code> the given URL is returned.
			 * If the URL is relative it is appended to the context.
			 * This method works much like the constructor @see java.net.URL#Constructor(URL context,String spec) ,
			 * but it treats Windows pathnames like "C:/foo" as absolute.   
			 * @param relURL the URL to transform
			 * @return absolute URL in the given context 
			 * @throws MalformedURLException
			 */
	public URL getContextURL(URL relURL) throws MalformedURLException {
		return URLContext.getContextURL(context, relURL);
	}
	/**
			 * Creates a URL by parsing the given spec within the specified context.
			 * If context is <code>null</code> an URL from the given spec is created. 
			 * This method works much like the constructor @see java.net.URL#Constructor(URL context,String spec) ,
			 * but it treats Windows pathnames like "C:/foo" as absolute.   
			 * @param spec URL specifcation
			 * @return absolute URL in the given context 
			 * @throws MalformedURLException
			 */
	public URL getContextURL(String spec) throws MalformedURLException {
		return URLContext.getContextURL(context, spec);
	}
	/**
				 * Creates a URL by parsing the given URL within the specified context.
				 * If context is <code>null</code> the given URL is returned.
				 * If the URL is relative it is appended to the context.
				 * This method works much like the constructor @see java.net.URL#Constructor(URL context,String spec) ,
				 * but it treats Windows pathnames like "C:/foo" as absolute.   
				 * @param contextURL the context
				 * @param relURL the URL to transform
				 * @return absolute URL in the given context 
				 * @throws MalformedURLException
				 */
	public static URL getContextURL(URL contextURL, URL relURL)
		throws MalformedURLException {
		if (contextURL == null)
			return relURL;
		if (relURL.getProtocol().equals("file")) {
			// the context constructor for URL does not recognize absolute windows paths (e.g. C:/foo)
			// if it does not begin with a "/"
			//URL tmpURL=new URL(recDirName);
			String dirPath = relURL.getPath();
			File tmpFile = new File(dirPath);
			if (!dirPath.startsWith("/") && tmpFile.isAbsolute()) {
				dirPath = "/".concat(dirPath);
			}
			return new URL(contextURL, "file:" + dirPath);
		} else {
			return new URL(contextURL, relURL.toExternalForm());
		}
	}

	/**
	 * Creates a URL by parsing the given spec within the specified context.
	 * If context is <code>null</code> an URL from the given spec is created. 
	 * This method works much like the constructor @see java.net.URL#Constructor(URL context,String spec) ,
	 * but it treats Windows pathnames like "C:/foo" as absolute.
	 * Note: the method accepts relative URLs with scheme part, which are not RFC conform.
	 * This URL's are used in Speechrecorder for project dir relative resources, e.g. file:project1_script.xml
	 * Use @see ipsk.net.URLContext#getContextURLStrict(URL context,String spec) for strict RFC behavoiur
	 * @param contextURL the context
	 * @param spec URL specifcation
	 * @return absolute URL in the given context 
	 * @throws MalformedURLException
	 */
	public static URL getContextURL(URL contextURL, String spec)
		throws MalformedURLException {
		 
	    URI specURI=null;
		try {
			URI sURI = new URI(spec);
			specURI=new URI(sURI.toASCIIString());
			if(specURI.isAbsolute()){
		        URL relURL = new URL(spec);
		        return URLContext.getContextURL(contextURL, relURL);
		    }
		} catch (URISyntaxException e1) {
			try {
				URI sURI = new URI(null,spec,null);
				String sURIAscii=sURI.toASCIIString();
				specURI=new URI(sURIAscii);
			} catch (URISyntaxException e) {
				throw new MalformedURLException(e.getMessage());
			}
			
		}
	    
		URI contextUri=null;
        try {
            contextUri = contextURL.toURI();
        } catch (URISyntaxException e) {
           throw new MalformedURLException(e.getMessage());
        }
	    URI resURI=contextUri.resolve(specURI);
	    URL resURL=resURI.toURL();
	    return resURL;
		
	}
	
	/**
     * Creates a URL by parsing the given spec within the specified context.
     * If context is <code>null</code> an URL from the given spec is created. 
     * This method works much like the constructor @see java.net.URL#Constructor(URL context,String spec) ,
     * but it treats Windows pathnames like "C:/foo" as absolute.
     * @param contextURL the context
     * @param spec URL specifcation
     * @return absolute URL in the given context 
     * @throws MalformedURLException
     */
	public static URL getContextURLStrict(URL contextURL, String spec)
	throws MalformedURLException {
	    URI specURI=URI.create(spec);
	    if(specURI.isAbsolute()){
	        return specURI.toURL();
	    }else{
	        URI contextUri=null;
	        try {
	            contextUri = contextURL.toURI();
	        } catch (URISyntaxException e) {
	            throw new MalformedURLException(e.getMessage());
	        }
	        
	        URI resURI=contextUri.resolve(spec);
	        URL resURL=resURI.toURL();
	        return resURL;
	    }

	}
	
	public boolean inContext(URL url){
	    // if url is in conetxt we can build a relative path
	    String relPath=relativize(context, url);
	    return(relPath!=null);
	}
    
	public String renameContextSpec(URL newContext,String spec) throws MalformedURLException{
	    URI specURI=URI.create(spec);
	    if(specURI.isAbsolute()){
	        String relSpec=relativize(context, specURI.toURL());
	        if(relSpec==null){
	            // not in/below context, renaming context does not affect URL spec
	            return spec;
	        }else{
	            URL renamedURL=getContextURL(newContext, relSpec);
	            return renamedURL.toString();
	        }
	    }else{
	        return spec;
	    }
	}
	
	private static String[] parsePath(String path){
	    if(path.matches("^\\p{Alpha}:.*")){
	        // detected windows drive syntax, e.g. C:
	        
	        // (backslash has special meaning in Java source code and in regex pattern, so we have to escape twice, four backslashes)
	        String[] bsPath=path.split("\\\\+");
	        String[] sPath=path.split("/+");
	        // both file separators allowed, take the one with more occurrences (OK, because slashes are not allowed in windows file names)
	        if(bsPath.length>sPath.length){
	            return bsPath;
	        }else{
	            return sPath;
	        }
	    }else{
	        return path.split("/+");
	    }
	}
	
	
	
	public static String relativize(URL context,URL url){
	    String ctxProto=context.getProtocol();
	    String urlProto=url.getProtocol();
	    String ctxHost=context.getHost();
	    String urlHost=url.getHost();
	    String ctxPathStr=context.getPath();
	    String urlPathStr=url.getPath();
	    String urlQ=url.getQuery();
	    String urlRef=url.getRef();
	    
	    if(ctxProto.equalsIgnoreCase(urlProto)){
	        boolean hostMatch=false;
	        if(ctxHost==null){
	            if(urlHost==null){
	                hostMatch=true;
	            }
	        }else{
	            hostMatch=ctxHost.equalsIgnoreCase(urlHost);
	        }
	        if(hostMatch){
	            String[] ctxPath=parsePath(ctxPathStr);
	            String[] urlPath=parsePath(urlPathStr);
	            if(urlPath.length<ctxPath.length){
	                return null;
	            }
	            if(ctxPath.length>0 && !"".equals(ctxPath[1])){
	                // windows path
	                
	            }
	            for(int i=0;i<ctxPath.length;i++){
	                if(!ctxPath[i].equals(urlPath[i])){
	                    return null;
	                }
	            }
	            int urlPathidx=ctxPath.length;
	            String[] relUrlPath=Arrays.copyOfRange(urlPath, urlPathidx,urlPath.length);
	            String relPathStr=StringSequenceBuilder.buildString(Arrays.asList(relUrlPath), '/');
	            if(urlQ!=null){
	                relPathStr=relPathStr+"?"+urlQ;
	            }
	            if(urlRef!=null){
	                relPathStr=relPathStr+"#"+urlRef;
	            }
	            return relPathStr;
	        }
	    }
	    
	    return null;
	}

	/**
	 * Get URL context.
	 * @return the context
	 */
	public URL getContext() {
		return context;
	}

	/**
	 * Set URL context.
	 * @param url context
	 */
	public void setContext(URL url) {
		context = url;
	}
	
	
	public static URL baseContextFromResourceURL(URL resURL) throws MalformedURLException{
		
		String proto=resURL.getProtocol();
		String host=resURL.getHost();
		int port=resURL.getPort();
		String path=resURL.getPath();
		
		String newPath=path.replaceAll("[^/]*$","");
		URL baseURL=new URL(proto,host,port,newPath);
		return baseURL;
	}
	
	public static void main(String[] args){
	    try {
	    	 File testDir=new File("/homes/klausj/test blank/testDirSSʊʊʙʘʧöüß");
            File testFile=new File(testDir,"testFileSSʊʊʙʘʧöüß.txt");
            URI unicodeTest=new URI("tʊ.txt");
            String unicodeAsciiStr=unicodeTest.toASCIIString();
            URI unicodeTest2=new URI("tä.txt");
            String unicodeAsciiStr2=unicodeTest2.toASCIIString();
            File tf3=new File("/t#.txt");
            
            URI unicodeTest3=tf3.toURI();
            String unicodeAsciiStr3=unicodeTest3.toASCIIString();
            URI uri3=new URI(unicodeAsciiStr3);
            URL url3=uri3.toURL();
            String url3ext=url3.toExternalForm();
//            testDir.mkdirs();
//            testFile.createNewFile();
//            File testFile=new File("\\C:\\homes\\klausj\\test blank\\testäöüß.txt");
//            System.out.println(testFile.toURL().toExternalForm());
            URI testDirUri=testDir.toURI();
            String testDirUriStr=testDirUri.toASCIIString();
            URI testDirsciiUri=new URI(testDirUriStr);
            URL testDirUrl=testDirsciiUri.toURL();
            URL context1=testDirUrl;
            URL fileinCtx=getContextURL(context1,testFile.getAbsolutePath().toString());
            
            
            
            URI testFileUri=testFile.toURI();
            URL testFileUrl=testFileUri.toURL();
            
           
            URI decTestUri=testFileUrl.toURI();
            URL decTestUrl=decTestUri.toURL();
            String decTestFilePath=decTestUri.getPath();
            File decTestFile=new File(decTestFilePath);
            boolean testOk=testFile.equals(decTestFile);
            System.out.println("Test OK:"+testOk+" Exists:"+decTestFile.exists());
            
            URL resUrl=new URL("https://webapptest.phonetik.uni-muenchen.de:443/corpusmachine/session/WebrecorderPrj.jsp?id=101236905&amp;amp;wait_for_complete_upload=true&amp;amp;transfer_rate_limit=-1");
            URL baseUrl=baseContextFromResourceURL(resUrl);
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	

}
