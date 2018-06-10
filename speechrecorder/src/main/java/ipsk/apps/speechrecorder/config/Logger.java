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

import java.util.logging.Level;

/**
 * Logger configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */


@DOMElements({"enabled"})
@DOMAttributes({"name","handlerName","level"})
public class Logger {
    private boolean enabled;
    private String handlerName=null;
    protected String level=Level.OFF.toString();
 
    private String name="";

    public Logger(){
        
    }
  

    /**
     * @return Returns the attributeLevel.
     */
    public String getLevel() {
        return level;
    }

    /**
     * @param attributeLevel The attributeLevel to set.
     */
    public void setLevel(String attributeLevel) {
        this.level = attributeLevel;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }
    /**
     * @param enabled The enabled to set.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    /**
     * @return Returns the attributeHandlerName.
     */
    public String getHandlerName() {
        return handlerName;
    }


    /**
     * @param attributeHandlerName The attributeHandlerName to set.
     */
    public void setHandlerName(String attributeHandlerName) {
        this.handlerName = attributeHandlerName;
    }

    

}
