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
import ipsk.audio.events.StartPlaybackActionEvent;

import java.awt.event.ActionEvent;

import javax.swing.Action;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class StartPlaybackAction extends ipsk.audio.actions.StartPlaybackAction {

	private static final long serialVersionUID = 1L;
	public final static String ACTION_COMMAND = new String("start_playback");
    public final static String SHORT_DESCRIPTION_VAL=new String("Start playback");
    private SpeechRecorder speechRecorder;
	/**
	 *  
	 */
	public StartPlaybackAction(SpeechRecorder speechRecorder,String name) {
		super();
		putValue(NAME, name);
		putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND);
		putValue(Action.SHORT_DESCRIPTION, SHORT_DESCRIPTION_VAL);
		this.speechRecorder=speechRecorder;
	}

	public String getActionCommand() {
		return (String) getValue(Action.ACTION_COMMAND_KEY);
	}

	public void actionPerformed(ActionEvent arg0) {
	    try {
	        if(arg0 instanceof StartPlaybackActionEvent){
	            StartPlaybackActionEvent spae=(StartPlaybackActionEvent)arg0;
	            long start=spae.getStartFramePosition();
	            long stop=spae.getStopFramePosition();
	            speechRecorder.startPlayback(start,stop);
	        }else{
	            speechRecorder.startPlayback();
	        }
	    } catch (Exception e) {
	        speechRecorder.getSpeechRecorderUI().displayError("Playback start error",e);
	        e.printStackTrace();
	    } 

	}

}
