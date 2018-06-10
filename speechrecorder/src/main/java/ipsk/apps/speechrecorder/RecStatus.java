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


package ipsk.apps.speechrecorder;
import java.util.*;
//import java.util.logging.Logger;
//import java.util.logging.Level;

/**
 * RecStatus is a singleton representing the current recording status. The
 * following different states are distinguished: idle, playback prompt, playback beep,waiting before recording,
 * waiting after a recording, recording, annotating, terminated and playing, pause, processing,
 * play_prompt. 
 *
 * The recording status can change automatically, e.g. when the maximum
 * recording time has elapsed, or manually, e.g. by clicking on the "stop"
 * button. Upon every status change, all listeners to the RecStatus object
 * are notified of the change.
 *
 * @author Chr. Draxler
 * Copyright Nov. 2002
 */
public class RecStatus implements RecSubject {

	public static final int INIT = 0;
	public static final int IDLE = 1;
	public static final int NON_RECORDING=2;
	public static final int PLAY_PROMPT_PREVIEW = 3;
	public static final int PLAY_PROMPT = 4;
	public static final int PLAY_BEEP = 5;
	public static final int NON_RECORDING_WAIT=6;
	public static final int PRERECWAITING = 7;
	public static final int RECORDING = 8;
	public static final int POSTRECWAITING = 9;
	public static final int RECORDED =10;
	public static final int ANNOTATE = 11;
	public static final int NAVIGATE = 12;
	public static final int PLAY = 13;
	public static final int PLAYPAUSE = 14;
	public static final int PROCESSING = 15;
	public static final int TERMINATE = 16;
	public static final int CLOSE = 17;
	
	public static final int ERROR = -1;
	public static final int ITEM_ERROR = -2;
	
	public static final int MIN_STATUS_NUMBER=ITEM_ERROR;

	private static final String [] STATUS_NAMES = {"ITEM_ERROR","ERROR","INIT", "IDLE","NON RECORDING", "PLAY_PROMPT_PREVIEW","PLAY_PROMPT", "PLAY_BEEP", "PRERECWAITING", "RECORDING", "POSTRECWAITING", "RECORDED","ANNOTATE", "NAVIGATE","PLAY", "PLAYPAUSE", "PROCESSING", "TERMINATE", "CLOSE"};

	private static RecStatus _instance = null;

//	private Logger logger;
//	private Level logLevel = Level.SEVERE;
	
	private int currentStatus;
	private Vector<RecObserver> recObservers;
    
	/**
	* calls getInstance() to obtain a singleton representing the current 
	* recording status.
	*/
	private RecStatus () {
		currentStatus = CLOSE;
        
//		logger = Logger.getLogger("ipsk.apps.speechrecorder");
//		logger.setLevel(logLevel);
        
		recObservers = new Vector<RecObserver>();
	}
	
	/**
	* creates a singleton RecStatus object
	* @return RecStatus singleton object
	*/
	public static RecStatus getInstance () {
		if (_instance == null) {
			_instance = new RecStatus ();
		}
		return _instance;
	}

	/**
	* sets the status to the given new status. All status changes 
	* are entered into a status queue which is then used to 
	* notify the RecObservers.
	*
	* @param status new status
	*/
	public synchronized void setStatus (int status) {
		currentStatus = status;
		notifyStatusChange(status);
	}

	/**
	* notifies the recording listeners attached to the recording status instance
	* that a status change has occurred.
	*
	* @param status new status that is sent to all listeners
	*/
	public synchronized void notifyStatusChange(int status) {
		RecObserver ro;
		Enumeration<RecObserver> e = recObservers.elements();
		while (e.hasMoreElements()) {
			ro = e.nextElement();
//			Calendar rightNow = Calendar.getInstance();
			//logger.info("notifyStatusChange("+ status + "): " + ro.getClass());
			if(ro != null) {
				ro.update(status);
			}
		}
	}

	public static String getStatusName(int status){
		return STATUS_NAMES[status-MIN_STATUS_NUMBER];
	}
	
	/**
	* retrieves the current recording status
	* @return int current status
	*/
	public int getStatus () {
		return currentStatus;
	}


//	/**
//	* checks whether the current recording status is "recording"
//	*
//	* @return boolean true if current status is "recording" 
//	*/
//	public boolean isRecording() {
//		return (currentStatus == RECORDING);
//	}
//
	// implementation of RecSubject interface 
	
	/**
	* registers a recording listener for the recording status instance
	*
	* @param ro recording listener to be notified
	*/
	public void attach(RecObserver ro) {
		//logger.info("recObserver(" + ro.getClass() + ") attached");
		recObservers.addElement(ro);
	}
	
	/**
	* removes a recording listener from the recording status instance
	*
	* @param ro recording listener to be removed
	*/
	public void detach(RecObserver ro) {
		//logger.info("recObserver(" + ro.getClass() + ") removed");
		recObservers.removeElement(ro);
	}
}