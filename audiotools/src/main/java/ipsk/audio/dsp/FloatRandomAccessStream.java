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
 * Date  : Jun 10, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.dsp;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.RandomAccessAudioStream;

import javax.sound.sampled.AudioFormat;



/**
 * A random accessible audio stream in normalized float values.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class FloatRandomAccessStream {

	//private static int DEFAULT_BUF_SIZE_IN_FRAMES = 512;

	protected AudioSource audioSource;
	protected AudioFormat audioFormat;

	protected byte[] buffer;

	protected int frameSize;

	//private boolean useReadOnSkipException;

	protected RandomAccessAudioStream raas;
	protected AudioFrameProcessor bufferProcessor;


	 /**
     * Returns a float value audio stream.
     * The float values a normalized to the scale -1 from to +1.
     * @param audioSource audio source stream
	 * @throws AudioSourceException 
	 * @throws AudioFormatNotSupportedException
     */
	public FloatRandomAccessStream(AudioSource audioSource)
			throws AudioFormatNotSupportedException, AudioSourceException {
		
		this.audioSource=audioSource;
		raas=new RandomAccessAudioStream(audioSource);
		audioFormat = audioSource.getFormat();
		frameSize = audioFormat.getFrameSize();
		//channels = audioFormat.getChannels();
		buffer = new byte[0];
		bufferProcessor=new AudioFrameProcessor(audioFormat);
		
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#close()
	 */
	public void close() throws AudioSourceException {
		
		raas.close();
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see javax.sound.sampled.AudioInputStream#getFormat()
//	 */
//	public AudioFormat getFormat() {
//		return audioSource.getFormat();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see javax.sound.sampled.AudioInputStream#getFrameLength()
//	 */
//	public long getFrameLength() {
//		return audioSource.getFrameLength();
//	}

    

	
	public int readFrames(double[][] normBuf, int frameOffset, int frames) throws  AudioSourceException {
		int bytesToRead=frameSize*frames;
		if (buffer.length < bytesToRead){
		    buffer=new byte[bytesToRead];
		}
	    
		int readFrames = raas.readFrames(buffer,0, frames);
		if (readFrames == -1)
			return readFrames;
		
		//if (read % frameSize != 0) throw new AudioSourceException("Audio stream out of frame constraints.");
		//int readFrames=read /frameSize;
		
		   // for(int i=0;i<readFrames;i++){
		    bufferProcessor.getNormalizedInterleavedValues(buffer,readFrames,normBuf,frameOffset);
		    //}

		return readFrames;
	}

	
	public void setFramePosition(long newPos) throws AudioSourceException{
		raas.setPosition(newPos);
	}
	
	public long skipFrames(long framesToSkip) throws AudioSourceException {
		return  raas.skipFrames(framesToSkip);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String("Float value audio source (scale -1 to +1) based on:"
				+ audioSource.toString());
	}



	public long getFrameLength() throws AudioSourceException {
		return audioSource.getFrameLength();
	
	}



	public int getChannels() throws AudioSourceException {
		return audioSource.getFormat().getChannels();
	}

}
