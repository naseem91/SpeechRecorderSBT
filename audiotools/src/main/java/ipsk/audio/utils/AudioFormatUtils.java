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
 * Date  : Feb 7, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.utils;
import java.util.ArrayList;
import java.util.List;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.Profile;
import ipsk.util.LocalizableMessage;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JOptionPane;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class AudioFormatUtils {

   

    public static AudioFormat.Encoding getEncodingByName(String name){
        if (AudioFormat.Encoding.ALAW.toString().equalsIgnoreCase(name)){
            return AudioFormat.Encoding.ALAW;
        }else if(AudioFormat.Encoding.PCM_SIGNED.toString().equalsIgnoreCase(name)){
            return AudioFormat.Encoding.PCM_SIGNED;
        }else if(AudioFormat.Encoding.PCM_UNSIGNED.toString().equalsIgnoreCase(name)){
            return AudioFormat.Encoding.PCM_UNSIGNED;
        }else if(AudioFormat.Encoding.ULAW.toString().equalsIgnoreCase(name)){
            return AudioFormat.Encoding.ULAW;
        }else return null;
    }
    
    public static String getMimetype(AudioFileFormat.Type type){
        if (type.equals(AudioFileFormat.Type.WAVE)){
            return "audio/wav";
        }else if(type.equals(AudioFileFormat.Type.AIFC)){
            // Is this correct ?
            return "audio/aiff";
        }else if(type.equals(AudioFileFormat.Type.AIFF)){
            return "audio/aiff";
        }else if(type.equals(AudioFileFormat.Type.AU)){
            return "audio/basic";
        }else if(type.equals(AudioFileFormat.Type.SND)){
            return "audio/basic";
        }else if(type.equals(new AudioFileFormat.Type("FLAC","flac"))){
            return "audio/x-flac";
        }else return "application/octet-stream";
    }
    
    
    public static int pcmSizeInBytesFromLength(AudioFormat audioFormat,float seconds) throws AudioFormatNotSupportedException{
        AudioFormat.Encoding encoding=audioFormat.getEncoding();
        if(AudioFormat.Encoding.PCM_SIGNED.equals(encoding) || AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding)){
            int frameSize=audioFormat.getFrameSize();
            float frameRate=audioFormat.getFrameRate();
            int frames=(int)(frameRate*seconds);
            int size=frames*frameSize;
            return size;
        }else{
            throw new AudioFormatNotSupportedException(audioFormat);
        }
    }
    
    public static float pcmLengthFromByteLength(AudioFormat audioFormat,int sizeInBytes) throws AudioFormatNotSupportedException{
        AudioFormat.Encoding encoding=audioFormat.getEncoding();
        if(AudioFormat.Encoding.PCM_SIGNED.equals(encoding) || AudioFormat.Encoding.PCM_UNSIGNED.equals(encoding)){
            int frameSize=audioFormat.getFrameSize();
            float frameRate=audioFormat.getFrameRate();
            int frames=sizeInBytes/frameSize;
            float length=frames/frameRate;
            return length;
        }else{
            throw new AudioFormatNotSupportedException(audioFormat);
        }
        
    }
    
    
    public static List<LocalizableMessage> getFormatQualityWarningsForProfile(AudioFormat format,Profile profile){
        List<LocalizableMessage> msgs=new ArrayList<LocalizableMessage>();
       
            if(format.getSampleRate()<profile.getMinSampleRate()){
               msgs.add(new LocalizableMessage("Minimum sample rate of "+profile.getMinSampleRate()));
            }
            if(format.getSampleSizeInBits()<profile.getMinSampleSizeInBits()){
                msgs.add(new LocalizableMessage("Minimum sample size of "+profile.getMinSampleSizeInBits()));
            }
            if(profile.isPcmSignedRecommended()&&!AudioFormat.Encoding.PCM_SIGNED.equals(format.getEncoding())){
                msgs.add(new LocalizableMessage("Encoding "+AudioFormat.Encoding.PCM_SIGNED));
            }
           return msgs;
           
       
    }
    
    public static Double lengthInSeconds(AudioSource audioSource) throws AudioSourceException{
    	long frameLength=audioSource.getFrameLength();
    	if(frameLength!=AudioSystem.NOT_SPECIFIED && frameLength>=0){
    		AudioFormat af=audioSource.getFormat();
    		float sr=af.getSampleRate();
    		if(sr!=AudioSystem.NOT_SPECIFIED){
    			return (double)frameLength/af.getSampleRate();
    		}
    	}
    	return null;
    }
    
    
}
