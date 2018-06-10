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
 * Date  : May 10, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.config;

import ipsk.apps.speechrecorder.SpeechRecorder;
import ipsk.beans.dom.DOMAttributes;



/**
 * Configuration for a logging formatter.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

@DOMAttributes({"className"})
public class Formatter {
    
    protected String className=null;
    protected String name;
    
    public Formatter(){
        this(null,null);
    }
    public Formatter(String attributeClassName,String name){
        this.className=attributeClassName;
        this.name=name;
    }
    
    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return className;
    }
    /**
     * @param className
     *            The className to set.
     */
    public void setClassName(String className) {
        this.className = className;
        
    }
    
    public boolean equals(Object o){
        if (o==null)return false;
        if (!(o instanceof Formatter)){
            return false;
        }
        String attrClassName=((Formatter)o).getClassName();
        if (attrClassName==null){
            return (className==null); 
        }else{
            return ((Formatter)o).getClassName().equals(className);  
        }
    }
    
    public String toString(){
        String name="";
        for(int i=0;i<SpeechRecorder.LOG_FORMATTERS.length;i++){
            String knownAttrClassName=SpeechRecorder.LOG_FORMATTERS[i].getClassName();
            if (knownAttrClassName==null){
                if(className==null){
                   return SpeechRecorder.LOG_FORMATTERS[i].name;
                  
                }
            }else{
            if (SpeechRecorder.LOG_FORMATTERS[i].getClassName().equals(className)){
                name=SpeechRecorder.LOG_FORMATTERS[i].name;
                break;
            }
            }
                
        }
         return new String(name);
    }
    
//    public String toString(){
//        
//        if(name!=null)return name;
//        if(attributeClassName!=null)return attributeClassName;
//        return "(Default formatter)";
//    }
}
    
    

