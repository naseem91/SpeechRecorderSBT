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
 * Date  : Jul 26, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.config;

import ipsk.beans.dom.DOMElements;

/**
 * Keyboard control configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
@DOMElements({"keyInputMap"})
public class Control {
    private KeyInputMap keyInputMap=new KeyInputMap();

    
    public Control(){}


    /**
     * @return Returns the keyInputMap.
     */
    public KeyInputMap getKeyInputMap() {
        return keyInputMap;
    }


    /**
     * @param keyInputMap The keyInputMap to set.
     */
    public void setKeyInputMap(KeyInputMap keyInputMap) {
        this.keyInputMap = keyInputMap;
    }
    
}
