//    IPS Java Audio Tools
// 	  (c) Copyright 2014
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

package ipsk.audio;

import ipsk.audio.io.push.FloatAudioOutputStream;


/**
 * Extended audio controller interface for controllers which implement input channel routing. 
 *
 */
public interface AudioController4 extends AudioController3 {

    /**
     * Stop recording but continue capture if continueCapture is true.
     * If continueCapture is false the method call is same as stopRecording() 
     * @param continueCapture if true continue capture
     * @throws AudioControllerException 
     */
    public void stopRecording(boolean continueCapture) throws AudioControllerException;
    
    /**
     * Add float audio output stream to capture.
     * @param floatAudioOutputStream float audio output stream
     */
    public void addCaptureFloatAudioOutputStream(FloatAudioOutputStream floatAudioOutputStream);
    

    /**
     * Remove float audio output stream from capture.
     * @param floatAudioOutputStream float audio output stream
     */
    public void removeCaptureFloatAudioOutputStream(FloatAudioOutputStream floatAudioOutputStream);
}
