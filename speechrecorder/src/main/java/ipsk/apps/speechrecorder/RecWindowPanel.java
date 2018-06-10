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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class RecWindowPanel extends JPanel {

	private static final long serialVersionUID = -7638145123216040482L;
	private boolean showTransporter;
	private RecMonitor recMonitor;
	private PromptViewer promptViewer;
//	private SpeechRecorder speechRecorder;
	private RecTransporter recTransporter;
	private boolean activeState;
	
	/**
	 * Speaker addressed prompting panel.
	 * Contains a start stop signal on the upper left side, the prompt with instructions and comments placed in the center and the transport buttons at the bottom.  
	 *  
	 * @param recTransporterActions transport actions 
	 * @param promptPresenters list of prompt presenter classes
	 * @param spkc graphics configuration of the containing window
	 * @param prompter the associated prompter
	 * @throws PluginLoadingException thrown if a plugin loading error occurs
	 */
	public RecWindowPanel(RecTransporterActions recTransporterActions,List<PromptPresenterServiceDescriptor> promptPresenters,GraphicsConfiguration spkc,Prompter prompter) throws PluginLoadingException {
		super();
	
	    setLayout(new BorderLayout());
	    //recWindowPanel =new JPanel(new BorderLayout());
//		speechRecorder = spRec;

		recMonitor = new RecMonitor();
		promptViewer = new PromptViewer(promptPresenters,prompter.getStartPromptPlaybackAction(),prompter.getStopPromptPlaybackAction());
		promptViewer.setDialogTargetProvider(prompter.getDialogTargetProvider());
		//promptViewer.setSilent(false);
		promptViewer.setShowComments(false);

		//boolean autoRecording=speechRecorder.getConfiguration().getRecordingConfiguration().getAutomaticRecording();
		recTransporter = new RecTransporter(recTransporterActions, true);
		
		
		add(recMonitor,BorderLayout.WEST);
		//contentPane.add(new JLabel("hallo"),BorderLayout.WEST);
		add(promptViewer,BorderLayout.CENTER);
		add(recTransporter,BorderLayout.SOUTH);
		showTransporter=true;
	}
	
	
	public void setSilent(boolean silence){
		promptViewer.setSilent(silence);
	}

	/**
	 * sets the activation state of the recording window and hides or shows the
	 * window accordingly
	 * 
	 * @param wa
	 */
	public void setWindowActive(boolean wa) {
		activeState = wa;
		promptViewer.setSilent(!activeState);
		if (activeState) {
			// show the window thread safe
			Runnable doShow = new Runnable() {
				public void run() {
				    // pack the window (necessary (only ?) for Linux) 
                    //pack();
                    
//					setExtendedState(JFrame.MAXIMIZED_BOTH);
					
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
//				    setExtendedState(JFrame.MAXIMIZED_BOTH);
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
		if (show) {
			if(!isTransporterShowing()){
			    if(!isAncestorOf(recTransporter)){
			       add(recTransporter,BorderLayout.SOUTH);
			    }
			}
		} else {
			remove(recTransporter);
		}
		showTransporter = show;
	}

	/**
	 * returns true if transporter panel with recording buttons is shown
	 * @return boolean
	 */
	public boolean isTransporterShowing() {
		return showTransporter;
	}
	
	public PromptViewer getPromptViewer() {
		return promptViewer;
	}

	/**
	 * @param b
	 */
	public void setAutoRecording(boolean b) {
		recTransporter.setAutoRecording(b);
	}

    public RecTransporter getRecTransporter() {
        return recTransporter;
    }

    public boolean isInstructionNumbering() {
		return promptViewer.isInstructionNumbering();
	}

	public void setInstructionNumbering(boolean instructionNumbering) {
		promptViewer.setInstructionNumbering(instructionNumbering);
	}

    public RecMonitor getRecMonitor() {
        return recMonitor;
    }
    
 
	public void attachToRecStatus() {
		RecStatus rs=RecStatus.getInstance();
		rs.attach(recTransporter);
//		rs.attach(recMonitor);
		
	}
	
	public void detachFromRecStatus() {
		RecStatus rs=RecStatus.getInstance();
		rs.detach(recTransporter);
//		rs.detach(recMonitor);
	}


}
