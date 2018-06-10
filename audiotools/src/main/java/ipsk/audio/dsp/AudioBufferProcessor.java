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

package ipsk.audio.dsp;

import ipsk.audio.AudioFormatNotSupportedException;

import javax.sound.sampled.AudioFormat;

/**
 * Base class to process audio data buffers.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public abstract class AudioBufferProcessor extends AudioFrameProcessor {
	protected AudioBufferProcessorListener abpl;

	static final boolean DEBUG = false;

	private byte[] data;

	//	private int bufPos;
	//	private int frameLength;
	protected int frames;

	/**
	 * Create new processor for the given audio format. The audio frames are
	 * converted to normalized (-1.0 to 1.0) float values.
	 * 
	 * @param af
	 *            audio format to use
	 * @throws AudioFormatNotSupportedException
	 */
	public AudioBufferProcessor(AudioFormat af)
			throws AudioFormatNotSupportedException {
		this(null, af);

	}

	/**
	 * Create new processor for the given audio format. The listener is notified
	 * if the buffer processing has finished.
	 * 
	 * @param abpl
	 *            processor listener
	 * @param af
	 *            audio format to use
	 * @throws AudioFormatNotSupportedException
	 */
	public AudioBufferProcessor(AudioBufferProcessorListener abpl,
			AudioFormat af) throws AudioFormatNotSupportedException {
		super(af);
		this.abpl = abpl;
	}

	
	public synchronized boolean setData(byte[] inData, int offset, int length) {
		data = inData;
		//		this.bufPos=bufPos;
		//		this.length = frameLength;
		frames = length / frameSize;
		return true;
	}

	/**
	 * Process the buffer data. The audio frames are converted to float values
	 * for each channel.
	 * @param inData data buffer
	 * @param offset offset in the buffer from where to read data
	 * @param length length of data to process
	 */
	public void process(byte[] inData, int offset, int length) {
		if (DEBUG)
			System.out.println("Running");
		int bc = offset;
		//		byte[] sample = new byte[sampleSize];
		for (int f = 0; f < frames; f++) {
			processFrame(getFloatValues(data, bc));
			bc += frameSize;
		}
		return;
	}
	


	/**
	 * Process one frame. This method has to be implemented.
	 * 
	 * @param val
	 */
	protected abstract void processFrame(float[] val);

}
