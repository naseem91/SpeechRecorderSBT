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
 * Date  : Nov 29, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.tools;

import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.arr.clip.AudioClip;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * 
 * Parses string paremeter for frame positon/length values.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class FrameUnitParser {
    public final static String SECONDS_SUFFIX="s";
    public final static String MILLISECONDS_SUFFIX="ms";
    public final static String END_CONSTANT="end";
    long frameLength=AudioSystem.NOT_SPECIFIED;
    float frameRate;
    
    public FrameUnitParser(float frameRate){
        this.frameLength=AudioSystem.NOT_SPECIFIED;
        this.frameRate=frameRate;  
     }
    
    public FrameUnitParser(float frameRate,long frameLength){
        this.frameLength=frameLength;
        this.frameRate=frameRate;  
     }
    
    public FrameUnitParser(AudioInputStream ais){
       frameLength=ais.getFrameLength();
       frameRate=ais.getFormat().getFrameRate();  
    }
    
    public FrameUnitParser(AudioClip clip) throws AudioSourceException{
        frameLength=clip.getFrameLength();
        frameRate=clip.getFormat().getFrameRate();  
     }
    
    public FrameUnitParser(AudioSource srcAs) throws AudioSourceException {
        frameLength=srcAs.getFrameLength();
        frameRate=srcAs.getFormat().getFrameRate();  
    }

    public long parseValue(String s){
        if (s.endsWith(MILLISECONDS_SUFFIX)){
            String msStr=s.substring(0,s.length()-MILLISECONDS_SUFFIX.length());
            
            double msVal=Double.parseDouble(msStr);
            return (long)(msVal*frameRate/1000);
       }else if(s.endsWith(SECONDS_SUFFIX)){
           String sStr=s.substring(0,s.length()-SECONDS_SUFFIX.length());
           double sVal=Double.parseDouble(sStr);
           return (long)(sVal*frameRate);
       }else{
           return Long.parseLong(s);
       }
    }
    
    public long parseFrameUnitString(String s){
        if(s.equals("end"))return frameLength;
        if(s.startsWith("end")){
            char operator=s.charAt(END_CONSTANT.length());
            if (operator=='-'){
                return frameLength-parseValue(s.substring(s.indexOf('-')+1));
            }
        }
       
        return parseValue(s);
       
    }
    
 
    
    
}
