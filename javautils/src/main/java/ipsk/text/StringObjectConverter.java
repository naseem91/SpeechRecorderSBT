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


package ipsk.text;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class StringObjectConverter {
	
public static Object stringToObject(String strValue, Class<?> propertyType,Locale locale) throws ParserException {
		if(locale==null){
			return stringToObject(strValue, propertyType);
		}
		if(Date.class.isAssignableFrom(propertyType)){
			try {
				return DateFormat.getDateTimeInstance(DateFormat.DEFAULT,DateFormat.DEFAULT,locale).parse(strValue);
			} catch (ParseException e) {
				throw new ParserException(e);
			}
		}else{
			return stringToObject(strValue, propertyType);
		}
	
	}
	public static Object stringToObject(String strValue, Class<?> propertyType) throws ParserException {
		
		try{
			if (propertyType.equals(Character.TYPE) || propertyType.equals(Character.class)) {
				if(strValue.length()!=1){
					throw new ParserException("Cannot parse to single character!");
				}
				return  new Character(strValue.charAt(0));
			}else if (propertyType.equals(Integer.TYPE) || propertyType.equals(Integer.class)) {
			return (Object) new Integer(strValue);
		} else if (propertyType.equals(Float.TYPE) || propertyType.equals(Float.class)) {
			return (Object) new Float(strValue);
		} else if (propertyType.equals(Double.TYPE) || propertyType.equals(Double.class)) {
			return (Object) new Double(strValue);
		} else if (propertyType.equals(Long.TYPE) || propertyType.equals(Long.class)) {
			return (Object) new Long(strValue);
		} else if (propertyType.equals(Short.TYPE) || propertyType.equals(Short.class)) {
			return (Object) new Long(strValue);
		} else if (propertyType.equals(Boolean.TYPE) || propertyType.equals(Boolean.class)) {
			return (Object) new Boolean(strValue);
		} else if (propertyType.equals(String.class)) {
			return (Object) strValue;
		} else if(Date.class.isAssignableFrom(propertyType)){
			try {
				return DateFormat.getDateTimeInstance().parse(strValue);
			} catch (ParseException e) {
				throw new ParserException(e);
			}
		}else
			return null;
		}catch(NumberFormatException nfe){
			throw new ParserException(nfe);
		}
	}
}
