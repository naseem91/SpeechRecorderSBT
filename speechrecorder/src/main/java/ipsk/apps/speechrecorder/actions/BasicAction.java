//    Speechrecorder
//    (c) Copyright 2012
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
 
package ipsk.apps.speechrecorder.actions;

import ipsk.apps.speechrecorder.SpeechRecorder;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Basic Speechrecorder action.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public abstract class BasicAction extends AbstractAction {

	protected SpeechRecorder speechRecorder;
    public String ACTION_COMMAND;
    public BasicAction(){
        super();
    }
    
    /**
     * Create action.
     * @param speechRecorder Speechrecorder instance
     * @param name name of the action
     */
    public BasicAction(SpeechRecorder speechRecorder,String name){
        this();   
        this.speechRecorder=speechRecorder;
        putValue(Action.NAME, name);
    }
    public abstract void actionPerformed(ActionEvent arg0);

}
