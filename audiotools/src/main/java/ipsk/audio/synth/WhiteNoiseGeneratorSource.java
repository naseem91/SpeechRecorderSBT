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
 * Date  : Mar 17, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.synth;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSourceException;
import ipsk.audio.BasicAudioSource;


/**
 * White noise generator audio source.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class WhiteNoiseGeneratorSource extends BasicAudioSource {

    private long length;

    private AudioFormat audioFormat;
    
    /** 
     * Easy convenience constructor: generates 44,1 kHZ mono white noise
     */
    public WhiteNoiseGeneratorSource(){
        
        this(new AudioFormat(44100, 16, 1, true, false),AudioSystem.NOT_SPECIFIED);
    }
    /**
     * Creates an audio source for the given frequency.
     * 
     * @param audioFormat
     *            audio format to encode the sine wave
     * @param length
     *            length of generated wave in sample frames or
     *            AudioSystem.NOT_SPECIFIED for infinite generation
     */
    public WhiteNoiseGeneratorSource(AudioFormat audioFormat, long length) {
 
        this.audioFormat = audioFormat;
        this.length = length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioSource#getAudioInputStream()
     */
    public AudioInputStream getAudioInputStream() throws AudioSourceException {
        WhiteNoiseGenerator wng;
        try {
            wng = new WhiteNoiseGenerator(audioFormat, length);
        } catch (AudioFormatNotSupportedException e) {
            throw new AudioSourceException(e);
        }
        return new AudioInputStream(wng, audioFormat, length);
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioSource#getFrameLength()
     */
    public long getFrameLength() throws AudioSourceException {
       
        return length;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioSource#getFormat()
     */
    public AudioFormat getFormat() throws AudioSourceException {
       
        return audioFormat;
    }
    
    public static void main(String args[]) {
        // TODO parse arguments
        WhiteNoiseGeneratorSource swg = new WhiteNoiseGeneratorSource(new AudioFormat(48000, 16, 2, true, false),
                10000000);
        AudioInputStream swgAis= null;
        try {
        	swgAis=swg.getAudioInputStream();
            AudioSystem.write(swgAis,
                    AudioFileFormat.Type.WAVE, new File("test.wav"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AudioSourceException e) {
            e.printStackTrace();
        }finally{
        	if(swgAis!=null)
				try {
					swgAis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        }

    }

 
}
