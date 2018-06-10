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
 * Date  : Jun 21, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;


/**
 * Basic audio source implementation.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public abstract class BasicAudioSource implements AudioSource {

    
    protected AudioFormat audioFormat=null;
    protected Long frameLengthObj=null;
	/**
	 *  
	 */
	public BasicAudioSource() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioSource#getAudioInputStream()
	 */
	public abstract AudioInputStream getAudioInputStream()
			throws AudioSourceException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioSource#getFrameLength()
	 */
	public long getFrameLength() throws AudioSourceException {
		AudioInputStream ais = getAudioInputStream();
		frameLengthObj = new Long(ais.getFrameLength());
		try {
			ais.close();
		} catch (IOException e) {
			throw new AudioSourceException(e);
		}
		return frameLengthObj.longValue();

	}
	
	public AudioFormat getFormat() throws AudioSourceException{
	    if (audioFormat==null){
	        AudioInputStream ais = getAudioInputStream();
	        audioFormat=ais.getFormat();
			frameLengthObj = new Long(ais.getFrameLength());
			
			try {
				ais.close();
			} catch (IOException e) {
				throw new AudioSourceException(e);
			}
	    }
	    return audioFormat;
	}
	
	protected void setAudioFormat(AudioFormat audioFormat){
	    this.audioFormat=audioFormat;
	}
	protected void setFrameLength(long frameLength){
	    frameLengthObj=new Long(frameLength);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioSource#isRandomAccessible()
	 */
	public boolean isRandomAccessible() {
		return false;
	}

}
