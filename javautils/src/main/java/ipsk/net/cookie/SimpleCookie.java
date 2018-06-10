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

package ipsk.net.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Simple storage of a cookie.
 * Accepts cookie strings with syntax: key1=value1[;key2=value2][;keyn=valuen]...
 * @see ipsk.net.cookie.SessionCookieHandler
 * @author Klaus Jaensch
 *
 */
public class SimpleCookie {
	
	private HashMap<String,String> props=new HashMap<String,String>();
	private HashMap<String,List<String>> responseHeaders=new HashMap<String,List<String>>();
	
	public SimpleCookie(String cookie){
	StringTokenizer cookieTokenizer=new StringTokenizer(cookie,";");
	while(cookieTokenizer.hasMoreElements()){
		String cookieProp=cookieTokenizer.nextToken().trim();
		int equalSignPos=cookieProp.indexOf('=');
		String propKey=cookieProp.substring(0,equalSignPos);
		String propValue=cookieProp.substring(equalSignPos+1);
		props.put(propKey,propValue);
		List<String> resValue=responseHeaders.get(propKey);
		if (resValue==null){
			resValue=new ArrayList<String>();
			responseHeaders.put(propKey, resValue);
		}
		resValue.add(propValue);
	}
	}
	
	public HashMap<String, String> getPropertyMap(){
		return props;
	}
	
	public String getProperty(String key){
		for(String pkey:props.keySet()){
			if(pkey.equalsIgnoreCase(key)){
				return props.get(pkey);
			}
		}
		return null;
	}
	
	
	public String toString(){
		StringBuffer sb=new StringBuffer();
		boolean firstProp=true;
		for(String pkey:props.keySet()){
			if(firstProp){
				firstProp=false;
			}else{
				sb.append(';');
			}
			sb.append(pkey);
			sb.append('=');
			sb.append(props.get(pkey));
		}
		return sb.toString();
	}

	public HashMap<String, List<String>> getResponseHeaders() {
		return responseHeaders;
	}
	
	
	
}
