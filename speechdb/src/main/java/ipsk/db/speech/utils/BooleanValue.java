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

import org.w3c.dom.Attr;


/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class BooleanValue {

    
    /**
     * Return boolean value of a string.
     * Unlike {@link java.lang.Boolean#valueOf(String)} this method includes "yes" and "no" as valid input values.  
     * @param str String to parse
     * @return true if str is "yes", false if str is "no", return value of Boolean.valueOf otherwise
     */
    public static boolean parseExtendedBoolean(String str){
        if("yes".equalsIgnoreCase(str)){
            return true;
        }else if("no".equalsIgnoreCase(str)){
            return false;
        }else{
            return (Boolean.valueOf(str)).booleanValue();
        }
    }
    
    /**
     * Return boolean value of a string.
     * Unlike {@link java.lang.Boolean#valueOf(String)} this method includes "yes" and "no" as valid input values.  
     * @param attr DOM attribute to parse
     * @return true if attribute value is "yes", false if it is "no", return value of Boolean.valueOf otherwise
     */
    public static Boolean parseExtendedBoolean(Attr attr){
        if(attr!=null){
            return BooleanValue.parseExtendedBoolean(attr.getValue());
        }
        return null;
    }
}
