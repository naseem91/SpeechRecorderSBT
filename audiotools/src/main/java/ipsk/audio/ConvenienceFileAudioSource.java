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
 * Date  : Aug 6, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio;

import ipsk.audio.io.KnownLengthAudioInputStream;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A file based convenience PCM audio source.
 * Converts underlying audio source to PCM stream.
 * If frame length is unknown calculates the frame length. 
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class ConvenienceFileAudioSource extends FileAudioSource {

	private int DETERMINE_FRAME_LEN_BUFFER_SIZE = 2048;
	private long DETERMINE_FRAME_LEN_SKIP_SIZE=1000000; // one million frames

	private long frameLength = ThreadSafeAudioSystem.NOT_SPECIFIED;

	private AudioFormat audioFormat;

	/**
	 * Create new audio source from file.
	 * 
	 * @param file
	 * @throws AudioSourceException
	 */
	public ConvenienceFileAudioSource(File file) throws AudioSourceException {
		super(file);
		getFrameLength();
	}

	private AudioInputStream getPCMAudioInputStream()
			throws UnsupportedAudioFileException, IOException {
		AudioInputStream srcStream = ThreadSafeAudioSystem.getAudioInputStream(file);
		if (srcStream.getFormat().getEncoding().equals(
				AudioFormat.Encoding.PCM_SIGNED))
			return srcStream;
		return ThreadSafeAudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,
				srcStream);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioSource#getAudioInputStream()
	 */
	public AudioInputStream getAudioInputStream() throws AudioSourceException {
		try {
			return new KnownLengthAudioInputStream(getPCMAudioInputStream(),
					frameLength);
		} catch (Exception e) {
			throw new AudioSourceException(e);
		}
	}

	/**
	 * @return Returns the frameLength.
	 * @throws AudioSourceException
	 */
	public long getFrameLength() throws AudioSourceException {
		// check if fraemLength is already calculated
		if (frameLength != ThreadSafeAudioSystem.NOT_SPECIFIED)
			return frameLength;
		// try to get it from the stream
		//AudioInputStream
		// ais=AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED,getAudioInputStream());
		
		AudioInputStream ais;
		try {
			ais = getPCMAudioInputStream();
		} catch (Exception e) {
			throw new AudioSourceException(e);
		}
		long streamLength = ais.getFrameLength();
		if (streamLength == ThreadSafeAudioSystem.NOT_SPECIFIED) {
			int frameSize=ais.getFormat().getFrameSize();
			long toSkip=DETERMINE_FRAME_LEN_SKIP_SIZE*frameSize;
			long s;
			long byteLength = 0;
			int read;
			byte[] buf = new byte[DETERMINE_FRAME_LEN_BUFFER_SIZE*frameSize];
			try {
				// First skip until 0 is returned
				while((s=ais.skip(toSkip))!=0){
					byteLength+=s;
				}
				// Skip does not return a clear result to detect end of file
				// Read to the end
				while ((read = ais.read(buf)) != -1) {
					byteLength += read;
				}
			} catch (IOException e) {
				throw new AudioSourceException(e);
			} finally {
				try {
					ais.close();
				} catch (IOException e1) {
					throw new AudioSourceException(e1);
				}
			}
			streamLength = byteLength / frameSize;

		}
		// store evaluated frameLength
		frameLength = streamLength;

		try {
			ais.close();
		} catch (IOException e) {
			throw new AudioSourceException(e);
		}
		return frameLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.RandomAccessAudioSource#getAudioFormat()
	 */
	public AudioFormat getFormat() throws AudioSourceException {
		if (audioFormat == null) {
			AudioInputStream ais = getAudioInputStream();
			audioFormat = ais.getFormat();
			try {
				ais.close();
			} catch (IOException e) {
				throw new AudioSourceException(e);
			}
		}
		return audioFormat;
	}

	
}
