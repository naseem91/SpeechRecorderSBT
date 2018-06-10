//    IPS Java Utils
// 	  (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
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

import ipsk.text.quoting.TextPart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Concatentaes strings.
 * @author klausj
 *
 */
public class StringSequenceBuilder {
    
   
    
	public static String buildString(List<String> strings,char fieldSeparator){
		return buildString(strings, fieldSeparator, false);
	}
	
	public static String buildString(List<String> strings,char fieldSeparator,boolean trim){
	    StringBuffer sb=new StringBuffer();
	    int listSize=strings.size();
	    for(int i=0;i<listSize;i++){
	        String s=strings.get(i);
	        if(s==null){
	            s="";
	        }
	        sb.append(s);
	        if(i<listSize-1){
	            sb.append(fieldSeparator);
	        }
	    }
	    return sb.toString();
	}
	
	public static String buildString(Collection<String> strings,char fieldSeparator){
        return buildString(strings, fieldSeparator, false);
    }
    
    public static String buildString(Collection<String> strings,char fieldSeparator,boolean trim){
        StringBuffer sb=new StringBuffer();
        int listSize=strings.size();
        int i=0;
        for(String s:strings){
            if(s==null){
                s="";
            }
            sb.append(s);
            if(i<listSize-1){
                sb.append(fieldSeparator);
            }
            i++;
        }
        return sb.toString();
    }
	
	
	public static String buildStringOfObjs(List<?> objs,char fieldSeparator){
        return buildStringOfObjs(objs, fieldSeparator, false);
    }
    
    public static String buildStringOfObjs(List<?> objs,char fieldSeparator,boolean trim){
        StringBuffer sb=new StringBuffer();
        int listSize=objs.size();
        for(int i=0;i<listSize;i++){
            Object o=objs.get(i);
            if(o==null){
                o="";
            }
            sb.append(o.toString());
            if(i<listSize-1){
                sb.append(fieldSeparator);
            }
        }
        return sb.toString();
    }
    
    public static String buildStringOfObjs(Collection<?> objs,char fieldSeparator){
        return buildStringOfObjs(objs, fieldSeparator, false);
    }
    
    public static String buildStringOfObjs(Collection<?> objs,char fieldSeparator,boolean trim){
        StringBuffer sb=new StringBuffer();
        int listSize=objs.size();
        int i=0;
        for(Object o:objs){
            if(o==null){
                o="";
            }
            sb.append(o.toString());
            if(i<listSize-1){
                sb.append(fieldSeparator);
            }
            i++;
        }
        return sb.toString();
    }
    
	
}
