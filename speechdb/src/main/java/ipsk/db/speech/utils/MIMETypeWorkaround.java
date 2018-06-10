//    IPS Java Speech Database
//    (c) Copyright 2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Speech Database
//
//
//    IPS Java Speech Database is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Speech Database is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Speech Database.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.db.speech.utils;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class MIMETypeWorkaround {

    private String convertedMimeType;
    private String convertedCharset;

    public MIMETypeWorkaround(String mimeType, String charset) {
        if (mimeType != null) {
            convertedMimeType = mimeType;
            if (charset == null) {
                // check for special Speechrecorder MIME types text/UTF-8 and
                // text/ISO-
                // and convert them to correct MIME type/charset pair
                int sepIndex = mimeType.indexOf("/");
                String type = mimeType.substring(0, sepIndex).trim();
                String subType = mimeType.substring(sepIndex + 1).trim();
                if (type.equalsIgnoreCase("text")) {
                    if (subType.equalsIgnoreCase("UTF-8")) {
                        convertedMimeType = "text/plain";
                        convertedCharset = null; // null means default equals
                                                 // UTF-8
                    } else if (subType.matches("ISO[_-].*")) {
                        convertedMimeType = "text/plain";
                        convertedCharset = subType;
                    }
                }
            }
        }
    }

    public String getConvertedMimeType() {
        return convertedMimeType;
    }

 
    public String getConvertedCharset() {
        return convertedCharset;
    }

}
