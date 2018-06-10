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

//
//	RecWindow.java
//	JSpeechRecorder
//
//	Created by Christoph Draxler on Fri Dec 06 2002.
//

package ipsk.apps.speechrecorder;

import java.awt.Window;
import ipsk.apps.speechrecorder.monitor.RecMonitor;
import ipsk.apps.speechrecorder.prompting.PromptViewer;

public interface RecWindow {

	
    public Window getWindow();
    
    
	public PromptViewer getPromptViewer();



	/**
	 * sets the activation state of the recording window and hides or shows the
	 * window accordingly
	 * 
	 * @param wa
	 */
	public void setWindowActive(boolean wa);

	/**
	 * returns the activation state of the window
	 * @return true if window is active
	 */
	public boolean isWindowActive();

	/**
	 * if true, transporter panel with recording control buttons is shown
	 * @param show
	 */
	public void setTransporterShowing(boolean show);
	/**
	 * returns true if transporter panel with recording buttons is shown
	 * @return boolean
	 */
	public boolean isTransporterShowing();
	
	public void setAutoRecording(boolean b);

    public RecTransporter getRecTransporter();

    public boolean isInstructionNumbering();

	public void setInstructionNumbering(boolean instructionNumbering);

    public RecMonitor getRecMonitor();
    
    public void attachToRecStatus();
    public void detachFromRecStatus();



}
