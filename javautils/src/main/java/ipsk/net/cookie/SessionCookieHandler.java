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

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Cookie handler to store Java session cookie (JEE).
 * We use it to transfer session cookies to Java Web start applications.
 * Java Web Start applications do not inherit browser session cookies (anymore),
 * We transfer the session cookie as an application  parameter string. 
 * @author Klaus Jaensch 
 *
 */
public class SessionCookieHandler extends CookieHandler {

	/**
	 * The Java EE session ID key ("JSESSIONID"). Is this a fixed value for all servlet container implementations?
	 */
	public final static String KEY_JSESSIONID="JSESSIONID";
	//private URI uri;
	private SimpleCookie sessionCookie;
	private boolean acceptNewCookies=false;
	
	public SessionCookieHandler(String sessionCookieStr){
		this.sessionCookie=new SimpleCookie(sessionCookieStr);
	}
	
	@Override
	public Map<String, List<String>> get(URI arg0,
			Map<String, List<String>> map) throws IOException {
		HashMap<String,List<String>> retMap=new HashMap<String, List<String>>();
		URI cookieUri=null;
		boolean uriMatch=false;
		try {
			String pathProp=sessionCookie.getProperty("path");
			if(pathProp!=null){
			cookieUri = new URI(pathProp);
			URI serverAuthReq=arg0.normalize();
			
			URI serverAuthSessionCookie=cookieUri.normalize();
			uriMatch=(serverAuthReq.getScheme().equals(serverAuthSessionCookie.getScheme())&& serverAuthReq.getHost().equals(serverAuthSessionCookie.getHost()) && serverAuthReq.getPort() ==serverAuthSessionCookie.getPort()  && serverAuthReq.getPath().startsWith(serverAuthSessionCookie.getPath()));
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			uriMatch=false;
		}
		if(!uriMatch){
			return map;
		}
		boolean hasCookieMap=false;
		List<String> newCookieEntries=new ArrayList<String>();
		for(String key:map.keySet()){
			
			if(key.equalsIgnoreCase("Cookie")){
				hasCookieMap=true;
				
				List<String> cookieEntries=map.get(key);
				for(String cookie:cookieEntries){
					boolean isJsessionId=false;
					StringTokenizer cookieTokenizer=new StringTokenizer(cookie,";");
					while(cookieTokenizer.hasMoreElements()){
						String cookieProp=cookieTokenizer.nextToken().trim();
						String propKey=cookieProp.substring(0,cookieProp.indexOf('='));
						if(propKey.equalsIgnoreCase(KEY_JSESSIONID)){
							isJsessionId=true;
						}
					}
					if(!isJsessionId){
						newCookieEntries.add(cookie);
					}else{
						// filter
					}
				}
				
				// finally add our session id:
				// TODO compare path/URI
				
				newCookieEntries.add(sessionCookie.toString());
				retMap.put(key,newCookieEntries);
			}else{
				// pass through
				retMap.put(key, map.get(key));
			}
			if(!hasCookieMap){
				// No cookie key found, add one
				newCookieEntries.add(sessionCookie.toString());
				retMap.put("Cookie", newCookieEntries);
			}
		}
		return java.util.Collections.unmodifiableMap(retMap);
	}

	@Override
	public void put(URI arg0, Map<String, List<String>> cookies)
			throws IOException {
		if(acceptNewCookies){
			// TODO
		}else{
		return;
		}

	}

	public boolean isAcceptNewCookies() {
		return acceptNewCookies;
	}

	/**
	 * Determines if the handler accepts new cookies send from the server.
	 * Default is false.
	 * @param acceptNewCookies
	 */
	public void setAcceptNewCookies(boolean acceptNewCookies) {
		this.acceptNewCookies = acceptNewCookies;
	}

}
