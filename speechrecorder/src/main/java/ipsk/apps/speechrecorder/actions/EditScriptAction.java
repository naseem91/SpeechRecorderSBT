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

import javax.swing.Action;

public class EditScriptAction extends BasicAction {
	
	private static final long serialVersionUID = 1L;

	public final static String ACTION_COMMAND = new String("edit_script");
	
//	 private ScriptUIDialog scriptUI;
	public EditScriptAction(SpeechRecorder speechRecorder, String name) {
		super(speechRecorder, name);
		putValue(Action.ACTION_COMMAND_KEY,ACTION_COMMAND);
	}


	public void actionPerformed(ActionEvent arg0) {
		if(isEnabled()){
		    if(arg0 instanceof EditScriptEvent){
		        EditScriptEvent ese=(EditScriptEvent)arg0;
		        speechRecorder.getSpeechRecorderUI().doEditScript(ese.getRequestPromptItem());
		    }else{
		        speechRecorder.getSpeechRecorderUI().doEditScript();
		    }
	            
		}
	}

}
