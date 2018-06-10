//    IPS Java Utils
// 	  (c) Copyright 2013
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

package ipsk.net;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * @author klausj
 *
 */
public class Utils {

	public static URI createAsciiURIFromFile(File file) throws URISyntaxException{
		URI fileURInonAscii=file.toURI();
		String fileURIAsciiStr=fileURInonAscii.toASCIIString();
		URI fileURIAscii=new URI(fileURIAsciiStr);
		return fileURIAscii;
		
	}
	
	public static URL createAsciiURLFromFile(File file) throws URISyntaxException, MalformedURLException{
		URI asciiURI=createAsciiURIFromFile(file);
		URL asciiURL=asciiURI.toURL();
		return asciiURL;
		
	}
	@Deprecated
	public static File fileFromURL(URL url){
	    if("file".equalsIgnoreCase(url.getProtocol())){
	        String path=url.getPath();
	        if(path!=null && !"".equals(path)){
	            return new File(path);
	        }
	    }
	    return null;
	}
	
	
	public static File fileFromDecodedURL(URL url) throws UnsupportedEncodingException{
	    if("file".equalsIgnoreCase(url.getProtocol())){
	        String path=url.getPath();
	        if(path!=null && !"".equals(path)){
	        	String decpath=URLDecoder.decode(path,Charset.defaultCharset().name());
				return new File(decpath);
	        }
	    }
	    return null;
	}
	
}
