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

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;


/**
 * Measures the level of the the audio stream and holds the peak.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class LevelMeasureAudioInputStream extends AudioInputStream {

	private final static boolean DEBUG=false; 
	//private static int DEFAULT_BUF_SIZE_IN_FRAMES = 512;
	private static int DEFAULT_BUF_SIZE_IN_FRAMES = 2048;

	private AudioInputStream srcAudioInputStream;

	private int bufSize;

	//private byte[] buffer;

	private int frameSize;

	private int channels;

	private int available = 0;

	private int offset = 0;

	private PeakDetector abp;

	//private float[] levels;

	private LevelInfo[] levelInfos;

//	private float[] peakLevelHolds;
	
//    private LevelInfo[] zeroLevelInfos;
	/**
	 * Create a measuring audio stream with the given source stream.
	 * @param srcAudioInputStream source audio stream (the stream to measure)
	 */
	public LevelMeasureAudioInputStream(AudioInputStream srcAudioInputStream)
			throws AudioFormatNotSupportedException {
			this(srcAudioInputStream,null);
	}
	/**
	 * Create a measuring audio stream with the given source stream.
	 * @param srcAudioInputStream source audio stream (the stream to measure)
	 */
	public LevelMeasureAudioInputStream(AudioInputStream srcAudioInputStream,LevelInfo[] levelInfos)
			throws AudioFormatNotSupportedException {
		super(srcAudioInputStream, srcAudioInputStream.getFormat(),
				srcAudioInputStream.getFrameLength());
		this.srcAudioInputStream = srcAudioInputStream;
		
		if (DEBUG)System.out.println("Level measure debug on");
		AudioFormat audioFormat = srcAudioInputStream.getFormat();
		frameSize = audioFormat.getFrameSize();
		channels = audioFormat.getChannels();
//		peakLevelHolds = new float[channels];
		bufSize = DEFAULT_BUF_SIZE_IN_FRAMES * frameSize;
		//buffer = new byte[bufSize];
		abp = new PeakDetector(audioFormat);
		//levels = new float[audioFormat.getChannels()];
		if(levelInfos==null){
			levelInfos = new LevelInfo[audioFormat.getChannels()];
//        zeroLevelInfos = new LevelInfo[channels];
			for (int ch = 0; ch < channels; ch++) {
//            zeroLevelInfos[ch] = new LevelInfo();
            	levelInfos[ch]=new LevelInfo();
			}
		}
		 this.levelInfos=levelInfos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#available()
	 */
	public int available() throws IOException {
		int srcAvail = srcAudioInputStream.available();
		if (srcAvail > bufSize){
			if (DEBUG) System.out.println("LevelMeasureInputStream. isAvailable: shrinked to bufSize");
			return bufSize;
		}
		return srcAvail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#close()
	 */
	public void close() throws IOException {
		available = 0;
		offset = 0;
		srcAudioInputStream.close();
        resetLevels();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#mark(int)
	 */
	public synchronized void mark(int arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#markSupported()
	 */
	public boolean markSupported() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#read()
	 */
	public int read() throws IOException {
		if (frameSize != 1)
			throw new IOException(
					"read method only allowed for frame size == 1");
		byte[] buf = new byte[1];

		int read = read(buf, 0, 1);
		if (read == -1)
			return -1;
		return 0x00FF & (int) read;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#read(byte[])
	 */
	public int read(byte[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}

    
    private void resetLevels(){
        for (int ch = 0; ch < channels; ch++) {
            levelInfos[ch].setLevel(0);
            levelInfos[ch].setPeakLevel(0);
            
        }
    }
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#read(byte[], int, int)
	 */
	public int read(byte[] buf, int trgOffset, int len) throws IOException {
		// try to fill process buffer
		
		if (len % frameSize != 0){
			//System.err.println("Read not multiple of frame size (len="+len+")");
			len-=len %frameSize;
			
		}
//		return srcAudioInputStream.read(buf,trgOffset,len);
		int read=0;
		//if (available<bufSize){
		read = srcAudioInputStream.read(buf, trgOffset, len);
		if (read == -1){
           
            resetLevels();
        
			return read;
        }
		//available += read;
	//	}
//		if (available % frameSize != 0){
//			System.err.println("Problem !!");
//			return 0;
//		}
		if (read > 0) {

			//LevelInfo[] tmpLevelInfos = abp.processBuffer(buf, trgOffset, read);
            abp.processBuffer(buf, trgOffset, read,levelInfos);
//			for (int ch = 0; ch < channels; ch++) {
//				float peakLevel = levelInfos[ch].getPeakLevel();
//				if (peakLevelHolds[ch] < peakLevel) {
//					peakLevelHolds[ch] = peakLevel;
//				}
//				levelInfos[ch].setPeakLevelHold(peakLevelHolds[ch]);
//			}
			
		}
		
		return read;
		//abp.process(buffer,bufPos,available);
		//levels=abp.getPeakLevels();

//		int toCopy = len;
//		if (available < len)
//			toCopy = available;
		//System.out.println(buffer.length+" "+bufPos+" "+trgOffset+"
		// "+toCopy);
		//System.arraycopy(buffer, offset, buf, trgOffset, toCopy);
//		available -= toCopy;
//		offset += read;
//		if (offset == bufSize)
//			offset = 0;
//		return toCopy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#reset()
	 */
	public void reset() throws IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#skip(long)
	 */
	public long skip(long arg0) throws IOException {
		// TODO Hmmm. Is this correct ?
		long toSkip = arg0;
		if (available > 0) {
			if (available <= toSkip) {
				toSkip = available;
			}
			offset += toSkip;
			available -= toSkip;
			if (offset == bufSize)
				offset = 0;
			return toSkip;
		} else {
			return srcAudioInputStream.skip(toSkip);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String("Level measuring audio stream based on:"
				+ srcAudioInputStream.toString());
	}

	/**
	 * Get level infos of all channels.
	 * @return array of level infos
	 */
	public  LevelInfo[] getLevelInfos() {
		return levelInfos;
	}

	/**
	 * Reset the peak hold.
	 *
	 */
	public void resetPeakHold() {
//		peakLevelHolds = new float[channels];
		if(levelInfos!=null){
		for(LevelInfo li:levelInfos){
			li.setPeakLevelHold(0);
		}
		}
	}

	/**
	 *  Get current level peaks.
	 *  @return array with normalized peak for each channel
	 */
	public float[] getPeakLevelHold() {
		//return peakLevelHolds;
		float[] plhs=null;
		if(levelInfos!=null){
			int lsl=levelInfos.length;
			plhs=new float[lsl];
			for(int i=0;i<lsl;i++){
				plhs[i]=levelInfos[i].getPeakLevelHold();
			}
		}
		return plhs;
	}
}
