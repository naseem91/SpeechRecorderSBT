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

package ipsk.apps.speechrecorder.monitor;

import ipsk.apps.speechrecorder.PluginLoadingException;

import javax.swing.JPanel;

/**
 * RecMonitor displays a vertical panel with a traffic light to monitor the 
 * recording. A red light means that no speech is recorded, yellow is displayed
 * when the recording is about to begin or has ended, and a green light indicates
 * that the speaker may speak.
 *
 * @author Christoph Draxler
 */

public class RecMonitor extends JPanel {

   
	private static final long serialVersionUID = 1L;
	private StartStopSignal monitorPlugin;
    

	/**
	 * RecMonitor consists of six image files, three lights
	 * of the traffic light either on or off. The images are organized in a 
	 * vertical box layout.
	 * @throws PluginLoadingException 
	 */
	public RecMonitor(){
		super();
	}
    
	public void setStartStopSignal(StartStopSignal startStopSignal){
	    
	    monitorPlugin=startStopSignal;
	    
	    removeAll();
	    if(monitorPlugin!=null){
	        add(monitorPlugin.getComponent());
	    }
	    repaint();
	}
	
	
	public void setStartStopSignalStatus(StartStopSignal.State status){
		if(monitorPlugin!=null){
			monitorPlugin.setStatus(status);
		}
	}
  
//	/**
//	 * update() implements the RecObserver interface. It changes the status of
//	 * the traffic lights depending on the current recording status.
//	 */
//	public void update(int status) {
//	    if(monitorPlugin==null){
//	        return;
//	    }
//		if (status == RecStatus.INIT) {
//		    monitorPlugin.setStatus(State.OFF);
//		} else if (status == RecStatus.IDLE) {
//			monitorPlugin.setStatus(State.IDLE);
//		} else if (status == RecStatus.NON_RECORDING) {
//            monitorPlugin.setStatus(State.OFF);
//        }else if (status == RecStatus.PRERECWAITING) {
//			monitorPlugin.setStatus(State.PRERECORDING);
//		} else if (status == RecStatus.POSTRECWAITING) {
//			monitorPlugin.setStatus(State.POSTRECORDING);
//		} else if (status == RecStatus.RECORDING) {
//			monitorPlugin.setStatus(State.RECORDING);
//		}else if (status==RecStatus.TERMINATE){
//		    monitorPlugin.setStatus(State.OFF);
//        }else if (status==RecStatus.CLOSE){
//            monitorPlugin.setStatus(State.OFF);
//		}else if (status==RecStatus.ITEM_ERROR){
//            monitorPlugin.setStatus(State.OFF);
//		}
//	}
}