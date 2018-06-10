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
 * Date  : Jul 26, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio;

import ipsk.io.VectorBuffer;
import ipsk.io.VectorBufferedInputStream;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;


/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class VectorBufferRawAudioSource extends BasicAudioSource implements
		AudioSource {

	private AudioFormat audioFormat;

	private VectorBuffer vectorBuffer;

	/**
	 *  
	 */
	public VectorBufferRawAudioSource(VectorBuffer vectorBuffer,
			AudioFormat audioFormat) {
		this.vectorBuffer = vectorBuffer;
		this.audioFormat = audioFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioSource#getAudioInputStream()
	 */
	public AudioInputStream getAudioInputStream() throws AudioSourceException {
		long lengthInFrames = vectorBuffer.getLength()
				/ audioFormat.getFrameSize();
		InputStream is = new VectorBufferedInputStream(vectorBuffer);
		AudioInputStream ais = new AudioInputStream(is, audioFormat,
				lengthInFrames);
		return ais;
	}

	public boolean isRandomAccessible() {
		return true;
	}

}
