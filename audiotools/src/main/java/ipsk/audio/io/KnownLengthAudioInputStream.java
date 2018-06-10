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

package ipsk.audio.io;

import ipsk.audio.AudioFormatNotSupportedException;

import javax.sound.sampled.AudioInputStream;


/**
 * Audio input stream which stores the frame length if available.
 * Wraps audio input streams which return {@link javax.sound.sampled.AudioSystem#NOT_SPECIFIED}, but the length processed and known.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class KnownLengthAudioInputStream extends AudioInputStream {

	private long frameLength;

	
	public KnownLengthAudioInputStream(AudioInputStream srcAudioInputStream,
			long knownFrameLength) throws AudioFormatNotSupportedException {
		super(srcAudioInputStream, srcAudioInputStream.getFormat(),
				srcAudioInputStream.getFrameLength());
		frameLength = knownFrameLength;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#getFrameLength()
	 */
	public long getFrameLength() {
		return frameLength;
	}

}
