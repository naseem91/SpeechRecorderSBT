//    IPS Java Audio Tools
// 	  (c) Copyright 2015
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.audio.dsp.speech;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import ipsk.audio.dsp.speech.vad.VoiceActivityDetector;
import ipsk.audio.dsp.speech.vad.VoiceActivityDetectorEvent;
import ipsk.audio.dsp.speech.vad.VoiceActivityDetectorListener;

/**
 * @author klausj
 *
 */
public class SpeechFinalSilenceDetector implements VoiceActivityDetectorListener, ActionListener {

    private static boolean DEBUG=false;
    
    private double DEFAULT_SILENCE_LENGTH=4.0;
	private VoiceActivityDetector voiceActivityDetector;
	private SpeechFinalSilenceDetectorListener listener;
	private double silencelength=DEFAULT_SILENCE_LENGTH;
	private boolean voiceDetected=false;
	private Timer finalSilenceTimer;
	
	private boolean running=false;
	
	/**
	 * 
	 */
	public SpeechFinalSilenceDetector(VoiceActivityDetector voiceActivityDetector,SpeechFinalSilenceDetectorListener listener) {
		super();
		this.voiceActivityDetector=voiceActivityDetector;
		this.listener=listener;
		this.voiceActivityDetector.setVoiceActivityDetectorListener(this);
		
	}
	/* (non-Javadoc)
	 * @see ipsk.audio.dsp.speech.vad.VoiceActivityDetectorListener#update(ipsk.audio.dsp.speech.vad.VoiceActivityDetectorEvent)
	 */
	@Override
	public void update(VoiceActivityDetectorEvent event) {
	    
		if(event.isVoiced()){
		    if(DEBUG){
	            System.out.println("Received voiced event.");
	        }
			voiceDetected=true;
			if(finalSilenceTimer!=null){
				finalSilenceTimer.stop();
			}
		}else{
		    if(DEBUG){
                System.out.println("Received unvoiced event.");
            }
			if(running && voiceDetected){
				// start timer
				int finalSilenceMs=(int)(silencelength*1000.0);
				if(finalSilenceTimer==null){
					finalSilenceTimer=new Timer(finalSilenceMs,this);
					finalSilenceTimer.setRepeats(false);
					  finalSilenceTimer.start();
				}else{
					finalSilenceTimer.setInitialDelay(finalSilenceMs);
					finalSilenceTimer.setDelay(finalSilenceMs);
					finalSilenceTimer.restart();
				}
			    
			  
			}
		}
	}
	
	
	public void start(){
		running=true;
	}
	
	public void stop(){
		running=false;
		reset();
		
	}
	
	public void reset(){
	    voiceDetected=false;
	    if(finalSilenceTimer!=null){
	        finalSilenceTimer.stop();
	      
	    }
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(DEBUG){
		    System.out.println("Silence detected.");
		}
		if(listener!=null && running){
		    listener.update(new SpeechFinalSilenceDetectorEvent(this));
		}
	}
    public double getSilencelength() {
        return silencelength;
    }
    public void setSilencelength(double silencelength) {
        this.silencelength = silencelength;
    }
   
}
