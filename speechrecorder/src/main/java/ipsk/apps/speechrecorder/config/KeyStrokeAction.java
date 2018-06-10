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

import ipsk.beans.dom.DOMAttributes;

/**
 * Keystroke action.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

@DOMAttributes({"action","code","shift","alt","ctrl"})
public class KeyStrokeAction {
    private String action;
    //private boolean keyEnabled=false;
    private int code=0;
    private boolean shift;
    private boolean alt;
    private boolean ctrl;
    
    public KeyStrokeAction(){
        super();
    }
    public KeyStrokeAction(String action,int code){
        super();
        this.action=action;
        this.code=code;
    }
    
    /**
     * @return Returns true for alt keystroke modifier.
     */
    public boolean isAlt() {
        return alt;
    }

    /**
     * @param attributeAlt
     */
    public void setAlt(boolean attributeAlt) {
        this.alt = attributeAlt;
    }

    /**
     * @return Returns true for ctrl keystroke modifier
     */
    public boolean isCtrl() {
        return ctrl;
    }

    /**
     * @param attributeCtrl The attributeCtrl to set.
     */
    public void setCtrl(boolean attributeCtrl) {
        this.ctrl = attributeCtrl;
    }

 
    
    /**
     * @return Returns the action .
     */
    public String getAction() {
        return action;
    }
    /**
     * @param name action name
     */
    public void setAction(String name) {
        this.action = name;
    }
    /**
     * @return Returns the keyCode.
     */
    public int getCode() {
        return code;
    }
    /**
     * @param keyCode The keyCode to set.
     */
    public void setCode(int keyCode) {
        this.code = keyCode;
    }
    
    /**
     * @return true for alt keystroke modifier
     * 
     */
    public boolean getShift() {
        return shift;
    }

    /**
     * @param attributeShift The attributeShift to set.
     */
    public void setShift(boolean attributeShift) {
        this.shift = attributeShift;
    }

}
