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

import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class RecFrame extends JFrame {

	private static final long serialVersionUID = -785487645992767852L;

	private boolean activeState;
	
	private RecWindowPanel recWindowPanel;
	

	public PromptViewer getPromptViewer() {
		return recWindowPanel.getPromptViewer();
	}

	public RecFrame(RecTransporterActions transporterActions,List<PromptPresenterServiceDescriptor> promptPresenters,GraphicsConfiguration spkc,Prompter prompter) throws PluginLoadingException {
		super(spkc);
		recWindowPanel=new RecWindowPanel(transporterActions,promptPresenters, spkc, prompter);
	    
		setContentPane(recWindowPanel);
		pack();
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
		if (activeState) {
			// show the window thread safe
			Runnable doShow = new Runnable() {
				public void run() {
				    // pack the window (necessary (only ?) for Linux) 
                    //pack();
                    
					setExtendedState(JFrame.MAXIMIZED_BOTH);
					
					setVisible(true);
//					try {
//					    // SVG viewer does not render properly if the the window if shown the first time with SVG content
//					    // This is a workaround
//                        promptViewer.prepare();
//                        promptViewer.showPrompt();
//                    } catch (PromptPresenterPluginException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
					
					
				}
			};
			if(EventQueue.isDispatchThread()){
			 doShow.run();   
			}else{
			    SwingUtilities.invokeLater(doShow);
			}
			
		} else {
			// hide the window thread safe
		  
			Runnable doHide = new Runnable() {
				public void run() {
				    setExtendedState(JFrame.MAXIMIZED_BOTH);
					setVisible(false);
				}
			};
			SwingUtilities.invokeLater(doHide);
		}
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

}
