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
import ipsk.io.InterleavedFloatStream;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class FloatAudioInputStream implements InterleavedFloatStream{

	private static int DEFAULT_BUF_SIZE_IN_FRAMES = 512;

	private AudioInputStream srcAudioInputStream;


	private byte[] buffer;

	private int frameSize;
	private int channels;

	private boolean useReadOnSkipException;


	private AudioFrameProcessor bufferProcessor;


	 /**
     * Returns a float value audio stream.
     * The float values a normalized to the scale -1 from to +1.
     * @param srcAudioInputStream source audio stream
     */
	public FloatAudioInputStream(AudioInputStream srcAudioInputStream)
			throws AudioFormatNotSupportedException {
		
		this.srcAudioInputStream = srcAudioInputStream;
		AudioFormat audioFormat = srcAudioInputStream.getFormat();
		frameSize = audioFormat.getFrameSize();
		channels = audioFormat.getChannels();
		buffer = new byte[0];
		bufferProcessor=new AudioFrameProcessor(audioFormat);
		
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#close()
	 */
	public void close() throws IOException {
		
		srcAudioInputStream.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#getFormat()
	 */
	public AudioFormat getFormat() {
		return srcAudioInputStream.getFormat();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#getFrameLength()
	 */
	public long getFrameLength() {
		return srcAudioInputStream.getFrameLength();
	}

    
	public int read(double[][] normBuf, int frameOffset, int frames) throws IOException {
		int bytesToRead=frameSize*frames;
		if (buffer.length < bytesToRead){
		    buffer=new byte[bytesToRead];
		}
	    
		int read = srcAudioInputStream.read(buffer,0, bytesToRead);
		if (read == -1)
			return read;
		
		if (read % frameSize != 0) throw new IOException("Audio stream out of frame constraints.");
		int readFrames=read /frameSize;
		
		    for(int i=0;i<readFrames;i++){
		    bufferProcessor.getDoubleValues(buffer,i*frameSize,normBuf[frameOffset+i]);
		    }
		
		return readFrames;
	}

	
	public int read(float[][] normBuf, int frameOffset, int frames) throws IOException {
		int bytesToRead=frameSize*frames;
		if (buffer.length < bytesToRead){
		    buffer=new byte[bytesToRead];
		}
	    
		int read = srcAudioInputStream.read(buffer,0, bytesToRead);
		if (read == -1)
			return read;
		
		if (read % frameSize != 0) throw new IOException("Audio stream out of frame constraints.");
		int readFrames=read /frameSize;
		
		    for(int i=0;i<readFrames;i++){
		    bufferProcessor.getFloatValues(buffer,i*frameSize,normBuf[frameOffset+i]);
		    }
		
		return readFrames;
	}

	
	public void setFramePosition(long newPos) throws IOException{
		long toSkip=newPos*frameSize;
		while(toSkip>0){
			toSkip-=srcAudioInputStream.skip(toSkip);
		}
	}
	
	
	public long skip(long skip) throws IOException{
	    return skipFrames(skip);
	}
	public long skipFrames(long arg0) throws IOException {
		
		long toSkip = arg0*frameSize;
		long skipped=0;
		try{
		
		skipped= srcAudioInputStream.skip(toSkip);
		}catch(IOException e){
		    // many audio streams do not support skip()
		    if(useReadOnSkipException){
		        if(toSkip > (long)Integer.MAX_VALUE){
		            int bufSize=DEFAULT_BUF_SIZE_IN_FRAMES *frameSize;
		            if (buffer.length<bufSize){
		                buffer=new byte[bufSize];
		            }
		           skipped=srcAudioInputStream.read(buffer);
		        }
		    }
		}
		if(skipped % frameSize !=0)throw new IOException("Audio stream out of frame constraints.");
		return skipped / frameSize;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String("Float value audio stream (scale -1 to +1) based on:"
				+ srcAudioInputStream.toString());
	}

	
    public boolean isUseReadOnSkipException() {
        return useReadOnSkipException;
    }
    public void setUseReadOnSkipException(boolean useReadOnSkipException) {
        this.useReadOnSkipException = useReadOnSkipException;
    }



    /* (non-Javadoc)
     * @see ipsk.io.InterleavedFloatStream#getChannels()
     */
    public Integer getChannels() {
        return channels;
    }
}
