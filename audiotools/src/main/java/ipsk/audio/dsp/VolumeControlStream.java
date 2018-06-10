//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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

/*
 * Date  : Jan 21, 2009
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.dsp;

import java.io.IOException;

import ipsk.audio.AudioFormatNotSupportedException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class VolumeControlStream extends FloatAudioInputStream{
    
    private double volume=1.0;
    private boolean limit=false;
    
    public VolumeControlStream(AudioInputStream srcAudioInputStream)
    throws AudioFormatNotSupportedException {   
        super(srcAudioInputStream);
    }
    
    public VolumeControlStream(AudioInputStream srcAudioInputStream,boolean limit)
    throws AudioFormatNotSupportedException {   
        super(srcAudioInputStream);
        this.limit=limit;
    }

    public int read(float[][] normBuf, int frameOffset, int frames) throws IOException {
       int readFrames=super.read(normBuf, frameOffset, frames);
       if(readFrames>0){
       for(int f=frameOffset;f<readFrames;f++){
           float[] frame=normBuf[f];
           for(int ch=0;ch<frame.length;ch++){
               if(limit){
                   float val=frame[ch]*(float)volume;
                   if(val>1.0){
                       val=(float) 1.0;
                   }else if(val<-1.0){
                       val=(float) -1.0;
                   }
                   frame[ch]=val;
               }else{
               frame[ch]=frame[ch]*(float)volume;
               }
           }
       }
       }
        return readFrames;
    }
    
    public int read(double[][] normBuf, int frameOffset, int frames) throws IOException {
        int readFrames=super.read(normBuf, frameOffset, frames);
        if(readFrames>0){
        for(int f=frameOffset;f<readFrames;f++){
            double[] frame=normBuf[f];
            for(int ch=0;ch<frame.length;ch++){
                if(limit){
                    double val=frame[ch]*volume;
                    if(val>1.0){
                        val=1.0;
                    }else if(val<-1.0){
                        val=-1.0;
                    }
                    frame[ch]=val;
                }else{
                frame[ch]=frame[ch]*volume;
                }
            }
        }
        }
         return readFrames;
     }
     
    
    public float getVolume() {
        return (float)volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
    
    public double getGainRatio(double gainRatio){
    	return volume;
    }
    
    public void setGainRatio(double volume) {
        this.volume = volume;
    }

    public boolean isLimit() {
        return limit;
    }

    public void setLimit(boolean limit) {
        this.limit = limit;
    }


}
