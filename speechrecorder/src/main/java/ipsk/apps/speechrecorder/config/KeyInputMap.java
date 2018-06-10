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
import ipsk.beans.dom.DOMElements;

/**
 * Configuration map for key to action binding.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
@DOMElements({"keyStrokeAction"})
@DOMAttributes({"consumeallkeys"})
public class KeyInputMap {
    
    // TODO no used yet
    public static KeyInputMap  PROFILE_1=new KeyInputMap(new KeyStrokeAction[]{
        new KeyStrokeAction("start_recording",32),
        new KeyStrokeAction("stop_recording",32),
        new KeyStrokeAction("start_auto_recording",32),
        new KeyStrokeAction("pause_auto_recording",19),
        new KeyStrokeAction("continue_auto_recording",32),
        new KeyStrokeAction("advance_to_next",9),
        new KeyStrokeAction("forward",39),
        new KeyStrokeAction("backward",37),
        new KeyStrokeAction("start_playback",80),
        new KeyStrokeAction("pause_playback",19),
        new KeyStrokeAction("stop_playback",80),
        new KeyStrokeAction("continue_playback",80),
        new KeyStrokeAction("close_speaker_display",27)
    },true);
    
    
    private boolean consumeallkeys=false;
    private KeyStrokeAction[] keyStrokeAction=new KeyStrokeAction[0];

   
    /**
     * @param keyStrokeActions
     */
    public KeyInputMap(KeyStrokeAction[] keyStrokeActions,boolean consumeAllKeys) {
        super();
       this.keyStrokeAction=keyStrokeActions;
       this.consumeallkeys=consumeAllKeys;
    }

    /**
     * 
     */
    public KeyInputMap() {
        super();
    }

    /**
     * @return array of key stroke actions
     */
    public KeyStrokeAction[] getKeyStrokeAction() {
        return keyStrokeAction;
    }

    /**
     * @param action array of key stroke actions
     */
    public void setKeyStrokeAction(KeyStrokeAction[] action) {
        this.keyStrokeAction = action;
    }

    /**
     * @return true if all keys are consumed
     */
    public boolean isConsumeallkeys() {
        return consumeallkeys;
    }

    /**
     * @param attributeconsumeallkeys true if all keys are consumed
     */
    public void setConsumeallkeys(boolean attributeconsumeallkeys) {
        this.consumeallkeys = attributeconsumeallkeys;
    } 
}
