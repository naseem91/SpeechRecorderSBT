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

import ipsk.text.html.HTMLTextEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;


/**
 * Editable URI.
 * Can be used to construct an URI step by step. 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class EditableURI {

	private String uri;
	private String encoding;

	/**
	 * Creates new editable URI object in UTF-8 encoding.
	 * @param uri the base URI
	 */
	public EditableURI(String uri) {
		this(uri,"UTF-8");
	}
	/**
	 * Creates new editable URI with given encoding.
	 * @param uri the base URI
	 */
	public EditableURI(String uri,String encoding) {
		this.uri = uri;
		this.encoding=encoding;
	}

	/**
	 * Adds query map (key/array of value String pairs) to the URI. 
	 * @param queryMap query map
	 * @throws UnsupportedEncodingException if the queries cannot be URL encoded 
	 */
	public String appendQueryMap(Map<String,String[]> queryMap) throws UnsupportedEncodingException {
	
		StringBuffer newUri=new StringBuffer(uri);
		
		if (newUri.indexOf("?")==-1){
			newUri.append("?");
		}
		for(Map.Entry<String,String[]> me:queryMap.entrySet()){
		
			String key=me.getKey();
			String[] values=me.getValue();
			for(String val:values){
				String lastChar=newUri.substring(newUri.length()-1);
		if(!(lastChar.equals("&") || lastChar.equals("?")) ){
			newUri.append("&");
		}
		
			newUri.append(
					URLEncoder.encode(key, encoding)
						+ "="
						+ URLEncoder.encode(val, encoding));
			
		}
		}
		uri=newUri.toString();
		return uri;
	}

	/**
	 * Adds a query parameter to the URI. 
	 * @param key key
	 * @param value value
	 * @throws UnsupportedEncodingException if the queries cannot be URL encoded 
	 */
	public String appendQuery(String key,Object value) throws UnsupportedEncodingException {
		
		StringBuffer newUri=new StringBuffer(uri);
		
		if (newUri.indexOf("?")==-1){
			newUri.append("?");
		}else{
		if(!(newUri.substring(newUri.length()-1)).equals("&")){
			newUri.append("&");
		}
		}
		newUri.append(
					URLEncoder.encode(key, encoding)
						+ "="
						+ URLEncoder.encode(value.toString(), encoding));
		uri=newUri.toString();
		return uri;
	}

	/**
	 * Get the URI.
	 * @return uri URI
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Set the URI.
	 * @param uri the URI to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	/**
	 * @see ipsk.text.html.HTMLTextEncoder
	 * @return HTML encoded URI
	 */
	public String getHTMLEncodedUri(){
		return HTMLTextEncoder.encode(uri);
	}
	

	public String toString() {
		return uri;
	}
	
	public Object clone(){
		return new EditableURI(uri,encoding);
	}
	
}
