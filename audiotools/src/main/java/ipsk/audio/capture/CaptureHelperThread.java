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

package ipsk.audio.capture;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

/**
 * Helper thread to read (capture) continuously from line.
 * Capture data is discarded.
 * @author klausj
 *
 */
public class CaptureHelperThread extends Thread{

    
    private boolean DEBUG=false;
    private volatile AudioInputStream audioInputStream;
    private volatile boolean running=true;
    private byte[] buf;
    
    /**
     * Create helper capture thread
     * @param audioInputStream audio input stream to read from
     * @param bufferSize buffer size for dummy buffer 
     */
    public CaptureHelperThread(AudioInputStream audioInputStream,int bufferSize) {
       super("Idle capture thread");
       this.audioInputStream=audioInputStream;
       buf=new byte[bufferSize];
    }

    /**
     * Get audio input stream
     * @return audio input stream
     */
    public AudioInputStream getAudioInputStream() {
        return audioInputStream;
    }

  
    public void run(){
        int read=0;
        while(running){
            if(audioInputStream!=null){
                try {
                    read=audioInputStream.read(buf);
                    if(DEBUG)System.out.println("Dummy capture read "+read+" bytes (helper thread");
                    if(read==0){
                        Thread.sleep(50);
                    }
                    if(read==-1){
                        running=false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                   // OK
                }
                
            }
            
        }
        
    }
    
    /**
     * Close this capture helper thread.
     * Stops reading from audio stream, and blocks until thread is joined (finished).
     */
    public void close(){
        running=false;
        interrupt();
        try {
            join();
            if(DEBUG)System.out.println("Dummy read thread joined");
        } catch (InterruptedException e) {
            //OK
        }
    }

}
