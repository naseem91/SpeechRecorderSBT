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

import ipsk.apps.speechrecorder.actions.RecTransporterActions;
import ipsk.apps.speechrecorder.monitor.RecMonitor;
import ipsk.apps.speechrecorder.prompting.PromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.prompting.PromptViewer;
import ipsk.apps.speechrecorder.prompting.Prompter;

import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.util.List;

import javax.swing.JFrame;

public class RecWindowFrame extends JFrame implements RecWindow{

	private static final long serialVersionUID = -1358588096232549427L;

	private boolean activeState;
	
	private RecWindowPanel recWindowPanel;
	

	public PromptViewer getPromptViewer() {
		return recWindowPanel.getPromptViewer();
	}

	public RecWindowFrame(RecTransporterActions transporterActions,List<PromptPresenterServiceDescriptor> promptPresentersClassList,GraphicsConfiguration spkc,Prompter prompter) throws PluginLoadingException {
		super(spkc);
		recWindowPanel=new RecWindowPanel(transporterActions,promptPresentersClassList, spkc, prompter);
	    
		setContentPane(recWindowPanel);
//		pack();
	}

	/**
	 * sets the activation state of the recording window and hides or shows the
	 * window accordingly
	 * 
	 * @param wa
	 */
	public void setWindowActive(boolean wa) {
		activeState = wa;
		recWindowPanel.setSilent(!activeState);
	}

	/**
	 * returns the activation state of the window
	 * @return true if window is active
	 */
	public boolean isWindowActive() {
		return activeState;
	}

	/**
	 * if true, transporter panel with recording control buttons is shown
	 * @param show
	 */
	public void setTransporterShowing(boolean show) {
		recWindowPanel.setTransporterShowing(show);
	}

	/**
	 * returns true if transporter panel with recording buttons is shown
	 * @return boolean
	 */
	public boolean isTransporterShowing() {
		return recWindowPanel.isTransporterShowing();
	}
	
	public void setAutoRecording(boolean b) {
		recWindowPanel.setAutoRecording(b);
	}

    public RecTransporter getRecTransporter() {
        return recWindowPanel.getRecTransporter();
    }

    public boolean isInstructionNumbering() {
		return recWindowPanel.isInstructionNumbering();
	}

	public void setInstructionNumbering(boolean instructionNumbering) {
		recWindowPanel.setInstructionNumbering(instructionNumbering);
	}

    public RecMonitor getRecMonitor() {
        return recWindowPanel.getRecMonitor();
    }



    public Window getWindow() {
       return this;
    }

    /* (non-Javadoc)
	 * @see ipsk.apps.speechrecorder.RecWindow#detachFromRecStatus()
	 */
	@Override
	public void attachToRecStatus() {
		recWindowPanel.attachToRecStatus();
	}
	/* (non-Javadoc)
	 * @see ipsk.apps.speechrecorder.RecWindow#detachFromRecStatus()
	 */
	@Override
	public void detachFromRecStatus() {
		recWindowPanel.detachFromRecStatus();
	}

}
