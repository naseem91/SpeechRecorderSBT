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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * An URL container whose contents can be modified.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class EditableURL {

	private URL url;

	/**
	 * Creates new editable URL object.
	 * @param url the url to modify
	 */
	public EditableURL(URL url) {
		this.url = url;
	}

	/**
	 * Adds some queries (key/value String pairs) to the URL. 
	 * @param queries query pairs
	 * @throws UnsupportedEncodingException if the queries cannot be URL encoded 
	 */
	public void addQuerys(Hashtable<String,String> queries) throws UnsupportedEncodingException {
		String query = url.getQuery();
		if (query == null) {
			query = new String();
		}
		Set<Map.Entry<String,String>> s = queries.entrySet();
		Iterator<Map.Entry<String,String>> it = s.iterator();
		if (!query.equals("") && it.hasNext()) {
			query = query.concat("&");
		}
		while (it.hasNext()) {
			Map.Entry<String,String> me =it.next();
			query =
				query.concat(
					URLEncoder.encode((String) me.getKey(), "UTF-8")
						+ "="
						+ URLEncoder.encode((String) me.getValue(), "UTF-8"));
			if (it.hasNext())
				query = query.concat("&");
		}

		String urlStr = new String(url.getProtocol() + "://" + url.getAuthority() + url.getPath());
		if (query != null && !query.equals("")) {
			urlStr = urlStr.concat("?" + query);
		}
		if (url.getRef() != null && !url.getRef().equals("")) {
			urlStr = urlStr.concat("#" + url.getRef());
		}
		URL queryUrl = null;
		try {
			queryUrl = new URL(urlStr);
		} catch (MalformedURLException e) {
			throw new RuntimeException("", e);
		}
		url = queryUrl;
	}

	/**
	 * Get the URL.
	 * @return url URL
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * Set the URL.
	 * @param url the URL to set
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * Test method.
	 * @param args
	 */
	public static void main(String[] args) {
		EditableURL editUrl = null;
		Hashtable<String,String> vals = new Hashtable<String, String>();
		vals.put("key2", "val2");
		try {
			editUrl = new EditableURL(new URL("http://www/test.pl?key1=val1"));
			editUrl.addQuerys(vals);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(editUrl.getUrl().toExternalForm());
	}

}
