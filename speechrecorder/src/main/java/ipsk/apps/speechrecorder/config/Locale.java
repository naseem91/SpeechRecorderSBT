//    Speechrecorder
//    (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Oct 5, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.config;

/**
 * A Java-Beans conform presentation of a java.util.Locale object.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class Locale {

	private String language="en";
	private String country="US";
	private String variant="";
	
	/**
	 * 
	 */
	public Locale() {
		
	}

	/**
	 * @return country code
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @return language code
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @return variant code
	 */
	public String getVariant() {
		return variant;
	}

	/**
	 * @param string country code
	 */
	public void setCountry(String string) {
		country = string;
	}

	/**
	 * @param string language coe
	 */
	public void setLanguage(String string) {
		language = string;
	}

	/**
	 * @param string variant code
	 */
	public void setVariant(String string) {
		variant = string;
	}
	
	/**
	 * Convert to Jaav API Locale
	 * @return locale
	 */
	public java.util.Locale toLocale(){
		return new java.util.Locale(language,country,variant);
	}

}
