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
 * Date  : May 5, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.config;

import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMElements;

/**
 * Handler configuration for logging.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

@DOMElements({"formatter"})
@DOMAttributes({"name","className"})
public class Handler {
    private String name;
    private Formatter formatter=new Formatter();
    private String className=null;
    
    public Handler(){
        super();
    }
 public Handler(String name){
        this.name=name;
    }
    /**
     * @return Returns the attributeClassName.
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param attributeClassName The attributeClassName to set.
     */
    public void setClassName(String attributeClassName) {
        this.className = attributeClassName;
    }

    /**
     * @return Returns the formatter.
     */
    public Formatter getFormatter() {
        return formatter;
    }

    /**
     * @param formatter The formatter to set.
     */
    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    /**
     * @return Returns the attributeName.
     */
    public String getName() {
        return name;
    }

    /**
     * @param attributeName The attributeName to set.
     */
    public void setName(String attributeName) {
        this.name = attributeName;
    }

    

}
