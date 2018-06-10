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

/**
 * UIResources.java
 * JSpeechRecorder
 *
 * loads the language settings from external resource files either
 * at startup time or when the language settings are changed by the 
 * user.
 *
 * The class implements a singleton to make sure that the user 
 * inferface is consistent.
 *
 * @author Chr. Draxler
 */

package ipsk.apps.speechrecorder;
import java.util.*;

public class UIResources {

	private final String UIRESOURCENAME = "SpRecBundle";
	protected Locale currentLocale = null;
	protected String language = null;
	protected String country = null;
	protected ResourceBundle uiString = null;
	
	private static UIResources _instance = null;

	private UIResources() {
		currentLocale = Locale.getDefault();
		language = currentLocale.getLanguage();
		country = currentLocale.getCountry();
		//System.out.println("Language, country: " + language + ", " + country);
		try {
			uiString = ResourceBundle.getBundle(UIRESOURCENAME, new Locale(language, country));			
		} catch (MissingResourceException e) {
			Locale emptyLocale = new Locale("en", "");
			uiString = ResourceBundle.getBundle(UIRESOURCENAME, emptyLocale);
		}

	}

	public static UIResources getInstance() {
		if (_instance == null) {
			_instance = new UIResources();
		}
		return _instance;
	}

	/**
	 * returns the UI String corresponding to the given key; if no
	 * UI string is found, the key is returned. This way a simple default
	 * behavior is achieved
	 *
	 * @param key String to identify the UI item
	 */
	public String getString(String key) {
		if (_instance != null) {
			return _instance.uiString.getString(key);
		} else {
			return key;
		}
	}

}
