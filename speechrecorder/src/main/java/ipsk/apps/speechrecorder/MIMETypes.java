//    Speechrecorder
// 	  (c) Copyright 2009-2011
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
 * Created on Jun 22, 2004
 *
 * Project: JSpeechRecorder
 * Original author: draxler
 */
package ipsk.apps.speechrecorder;

import java.util.Locale;

/**
 * @author draxler
 *
 * Helper class that knows about the legal MIME-types for the application 
 * and provides getter methods to access the MIME-type parts or check 
 * whether a given MIME-type is contained in a list of known MIME-types.
 */

public abstract class MIMETypes {

	public static final String [] PLAINTEXTMIMETYPES = {"text/plain"};
	public static final String [] FALSE_PLAINTEXTMIMETYPES = {"text/UTF-8", "text/ISO-8859-1"};
	public static final String [] FORMATTEDTEXTMIMETYPES = {"text/html", "text/rtf"};
//	public static final String [] GRAPHICMIMETYPES= {"image/svg+xml"};
	public static final String [] IMAGEMIMETYPES = {"image/gif", "image/jpeg", "image/png"};
	public static final String [] AUDIOMIMETYPES = {"audio/wav","audio/x-wav","audio/wave", "audio/x-wave","audio/vnd.wave", "audio/aif", "audio/basic"};
	//public static final String [] VIDEOMIMETYPES = {"video/quicktime", "video/mpeg","video/x-msvideo"};
	
	
	/**
	 * isInMIMETypes() returns true if a MIME type String is contained
	 * in an array of MIME types
	 * 
	 * @param mime MIME type
	 * @param givenMIMETypes list of MIME types
	 * @return boolean mime is contained in givenMIMETypes
	 */
	public static boolean isOfType(String mime, String [] givenMIMETypes) {
		boolean isEqual = false;
		for (int i = 0; (i < givenMIMETypes.length && ! isEqual); i++) {
			isEqual = (mime.toLowerCase(Locale.ENGLISH).equals(givenMIMETypes[i].toLowerCase(Locale.ENGLISH)));
			
		}
		return isEqual;
	}

	public static String getType(String mime) {
		String majorTypeStr=mime.substring(0,mime.indexOf("/"));
		if(majorTypeStr!=null){
		    return majorTypeStr.trim();
		}
		return null;
	}
	
	public static boolean isTextType(String mime){
	    String mimeMajorType=MIMETypes.getType(mime);
	    return(mimeMajorType.equalsIgnoreCase("text"));
	}
	
	public static boolean isMediaType(String mime){
	    String mimeMajorType=MIMETypes.getType(mime);
	    return(mimeMajorType.startsWith("image") || mimeMajorType.startsWith("audio") || mimeMajorType.startsWith("video"));
	
	}
	
	public static String getDescription(String mime) {
		return mime.substring(mime.indexOf("/") + 1, mime.length());
	}
	
	public static String[] getAllMimeTypes(){
//		String[] allTypes=new String[1+FORMATTEDTEXTMIMETYPES.length+GRAPHICMIMETYPES.length+IMAGEMIMETYPES.length+AUDIOMIMETYPES.length];
	    String[] allTypes=new String[1+FORMATTEDTEXTMIMETYPES.length+IMAGEMIMETYPES.length+AUDIOMIMETYPES.length];
		// text/UTF-8 and text/ISO-8859-1 are not in this list (this types are not valid (or?))
		int offset=0;
		allTypes[offset]="text/plain";
		offset+=1;
		for(int i=0;i<FORMATTEDTEXTMIMETYPES.length;i++){
			allTypes[i+offset]=FORMATTEDTEXTMIMETYPES[i];
		}
		offset+=FORMATTEDTEXTMIMETYPES.length;
//		for(int i=0;i<GRAPHICMIMETYPES.length;i++){
//            allTypes[i+offset]=GRAPHICMIMETYPES[i];
//        }
//		offset+=GRAPHICMIMETYPES.length;
		for(int i=0;i<IMAGEMIMETYPES.length;i++){
			allTypes[i+offset]=IMAGEMIMETYPES[i];
		}
		offset+=IMAGEMIMETYPES.length;
		for(int i=0;i<AUDIOMIMETYPES.length;i++){
			allTypes[i+offset]=AUDIOMIMETYPES[i];
		}
		return allTypes;
	}
}
