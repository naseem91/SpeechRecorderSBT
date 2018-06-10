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
 * Sine wave generator audio source.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class SineWaveGeneratorSource extends BasicAudioSource {

    private long length;

    private AudioFormat audioFormat;

    private float amplitudeFactor;

    private float frequency;

    /**
     * Creates an audio source for the given frequency.
     * 
     * @param frequency
     *            frequency
     * @param amplitudeFactor
     *            amplitude factor (0...1)
     * @param audioFormat
     *            audio format to encode the sine wave
     * @param length
     *            length of generated wave in sample frames or
     *            AudioSystem.NOT_SPECIFIED for infinite generation
     */
    public SineWaveGeneratorSource(float frequency, float amplitudeFactor,
            AudioFormat audioFormat, long length) {
        this.frequency = frequency;
        this.amplitudeFactor = amplitudeFactor;
        this.audioFormat = audioFormat;
        this.length = length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioSource#getAudioInputStream()
     */
    public AudioInputStream getAudioInputStream() throws AudioSourceException {
        SineWaveGenerator swg = null;
        try {
            swg = new SineWaveGenerator(frequency, amplitudeFactor,
                    audioFormat, length);
        } catch (AudioFormatNotSupportedException e) {
            throw new AudioSourceException(e);
        }
        return new AudioInputStream(swg, audioFormat, length);
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
        SineWaveGeneratorSource swg = new SineWaveGeneratorSource(377,
                (float) 0.8, new AudioFormat(48000, 16, 1, true, false),
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
