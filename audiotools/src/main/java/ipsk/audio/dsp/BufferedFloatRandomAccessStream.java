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
import javax.sound.sampled.AudioSystem;



/**
 * A random accessible audio stream in normalized float values.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class BufferedFloatRandomAccessStream extends FloatRandomAccessStream{

	//private static int DEFAULT_BUF_SIZE_IN_FRAMES = 512;

//	private static final int DEF_MAX_CACHE_SIZE = 65536; // 64kByte
    private static final int DEF_MAX_CACHE_SIZE = 80000000; // 80MB
	//private AudioSource audioSource;
	//private AudioFormat audioFormat;

	//private byte[] buffer;

	//private int frameSize;

	//private boolean useReadOnSkipException;

	//protected RandomAccessAudioStream raas;
	//private AudioFrameProcessor bufferProcessor;

	private int maxCacheSize=DEF_MAX_CACHE_SIZE;
	private double[][] cache;
//	long cacheFramePos=0;
	int cacheFilledFrames=0;
	
	long framePos=0;
	
	
	 /**
     * Returns a float value audio stream.
     * The float values a normalized to the scale -1 from to +1.
     * @throws AudioFormatNotSupportedException
	 * @throws AudioSourceException 
     */
	public BufferedFloatRandomAccessStream(AudioSource audioSource)
			throws AudioFormatNotSupportedException, AudioSourceException {
		this(audioSource,DEF_MAX_CACHE_SIZE);
	}
	 /**
     * Returns a float value audio stream.
     * The float values a normalized to the scale -1 from to +1.
     * @param audioSource audio source
     * @param maxCacheSize maximum cache size in bytes
     * @throws AudioFormatNotSupportedException
	 * @throws AudioSourceException 
     */
	public BufferedFloatRandomAccessStream(AudioSource audioSource,int maxCacheSize)
			throws AudioFormatNotSupportedException, AudioSourceException {
		super(audioSource);
		this.maxCacheSize=maxCacheSize;
		long frameLength=audioSource.getFrameLength();
		raas=new RandomAccessAudioStream(audioSource);
		audioFormat = audioSource.getFormat();
		frameSize = audioFormat.getFrameSize();
		int channels = audioFormat.getChannels();
		buffer = new byte[0];
		bufferProcessor=new AudioFrameProcessor(audioFormat);
		int cacheLen=maxCacheSize/(Double.SIZE*channels);
		if(frameLength!=AudioSystem.NOT_SPECIFIED){
			long length=frameLength*channels*Double.SIZE;
			if(maxCacheSize>=length){
				cacheLen=(int)frameLength;
			}
		}
		cache=new double[cacheLen][channels];
//		System.out.println(audioSource.getFrameLength());
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

    
	private int availCachedFrames(int len){
//		long cacheEnd=cacheFramePos+cacheFilledFrames;
		if(framePos>=(cacheFilledFrames)){
			return 0;
		}else{
			return (int)(cacheFilledFrames-framePos);		
		}
	}
	
	private int cachableFrames(int frames){
	    
	    long cachableFrames=framePos+frames-(long)cacheFilledFrames;
	    if(cachableFrames<0){
	        return 0;
	    }
	    long cacheFree=cache.length-cacheFilledFrames;
	    if(cachableFrames>cacheFree){
	        cachableFrames=cacheFree;
	                
	    }
	    return (int)cachableFrames;
	    
	}
	
	public int readFrames(double[][] normBuf, int frameOffset, int frames) throws  AudioSourceException {
	   
		int cacheableFrames=cachableFrames(frames);
		
		if(cacheableFrames>0){
		    // fill cache
//		    System.out.println("Set: "+cacheFilledFrames);
		   
			raas.setPosition(cacheFilledFrames);
			
			int bytesToRead=frameSize*cacheableFrames;
			if (buffer.length < bytesToRead){
			    buffer=new byte[bytesToRead];
			}
		    
			int readFrames = raas.readFrames(buffer,0, cacheableFrames);
			if (readFrames != -1){
			
			 bufferProcessor.getNormalizedInterleavedValues(buffer,readFrames,cache,cacheFilledFrames);
			 cacheFilledFrames+=readFrames;
		
//			 System.out.println("Filled cache with "+readFrames+" frames.");
			}
		}
		
		int availFrames=availCachedFrames(frames);
		if(availFrames>0){
		    int toCopy=availFrames;
		    if(toCopy>frames){
		        toCopy=frames;
		    }
		    for(int i=0;i<toCopy;i++){
                for(int ch=0;ch<audioFormat.getChannels();ch++){
                    normBuf[frameOffset+i][ch]=cache[(int)framePos+i][ch];
                }
            }
		    framePos+=toCopy;
//		    System.out.println(toCopy+" frames from cache.");
		    return toCopy;
		}else{
//		    System.out.println("Set2: "+framePos);
		    raas.setPosition(framePos);
		    int frRead=super.readFrames(normBuf, frameOffset, frames);
		    if(frRead==-1){
		        return -1;
		    }else{
		        framePos+=frRead;
//		        System.out.println(frRead+" frames from stream.");
		        return frRead;
		    }
		}
	}

	
	public void setFramePosition(long newPos) throws AudioSourceException{
		framePos=newPos;
	}
	
	public long skipFrames(long framesToSkip) throws AudioSourceException {
	    raas.setPosition(framePos*frameSize);
		long skippedFrames= raas.skipFrames(framesToSkip);
	    
		framePos+=skippedFrames;
		return skippedFrames;
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
