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
 * Date  : 17.09.2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.dsp;

import ipsk.audio.AudioFormatNotSupportedException;

import javax.sound.sampled.AudioFormat;


/**
 * Calculates amplitude values for an audio frame. An audio frame contains the
 * samples for all channels.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
public class AudioFrameProcessor extends AudioSampleProcessor {

	static final boolean DEBUG = false;

	private float[] floatValues;

	/**
	 * Create a frame processor.
	 * 
	 * @param af
	 *            frame must have this audio format
	 * @throws AudioFormatNotSupportedException
	 */
	public AudioFrameProcessor(AudioFormat af)
			throws AudioFormatNotSupportedException {
		super(af);
		floatValues = new float[af.getChannels()];
	}

	/**
	 * Returns the normalized float values (amplitudes) of the given frame.
	 * 
	 * @param frameData PCM coded data array
	 * @return values (range -0.5 ... +0.5)
	 */
	public float[] getFloatValues(byte[] frameData) {
		return getFloatValues(frameData, 0);
	}

	/**
	 * Returns the normalized float values (amplitudes) of the given data.
	 * 
	 * @param frameData PCM coded data array
	 * @param offset offset in the data
	 * @return values (range -0.5 ... +0.5)
	 */
	public float[] getFloatValues(byte[] frameData, int offset) {
		//values = new float[channels];
		for (int i = 0; i < channels; i++) {
			floatValues[i] = getFloatValue(frameData, (i * sampleSize) + offset);
		}
		return floatValues;
	}
	/**
	 * Converts PCM coded data to normalized double values (amplitudes).
	 * 
	 * @param frameData PCM coded data array
	 * @param frames count of frames to process 
	 * @param normBuffer buffer for normalized audio data (value range -1.0 ... +1.0)
	 * @param normBufferFrameOffset offset in frames from where to write converted values 
	 */
	public void getNormalizedInterleavedValues(byte[] frameData,int frames,double[][] normBuffer,int normBufferFrameOffset) {

		for(int f=0;f<frames;f++){
			for (int i = 0; i < channels; i++) {
			normBuffer[normBufferFrameOffset+f][i]=getNormalizedValue(frameData, f*frameSize+i*sampleSize);
			//offs+=sampleSize;
			}
			//offs+=frameSize;
		}
		
		
	}
	/**
     * Converts PCM coded data to normalized double values (amplitudes).
     * 
     * @param frameData PCM coded data array
     * @param frameDataOffset offset in frameData
     * @param frames count of frames to process 
     * @param normBuffer buffer for normalized audio data (value range -1.0 ... +1.0)
     * @param normBufferFrameOffset offset in frames from where to write converted values 
     */
    public void getNormalizedInterleavedValues(byte[] frameData,int frameDataOffset,int frames,double[][] normBuffer,int normBufferFrameOffset) {

        for(int f=0;f<frames;f++){
            for (int i = 0; i < channels; i++) {
            normBuffer[normBufferFrameOffset+f][i]=getNormalizedValue(frameData,frameDataOffset+ f*frameSize+i*sampleSize);
            
            }
        }
    }
	/**
	 * Converts to normalized float values (amplitudes) of the given data.
	 * 
	 * @param frameData PCM coded data array
	 * @param offset offset (of bytes) in PCM input data 
	 * @param normBuffer buffer for normalized audio data (value range -1.0 ... +1.0)
	 */
	public void getNormalizedValues(byte[] frameData, int offset,double[] normBuffer) {
		//values = new float[channels];
		for (int i = 0; i < channels; i++) {
			normBuffer[i] = getNormalizedValue(frameData, (i * sampleSize) + offset);
		}
	}
	
	/**
	 * Converts to normalized double values (amplitudes) of the given data.
	 * 
	 * @param frameData PCM coded data array
	 * @param offset offset (of bytes) in PCM input data 
	 * @param buffer buffer for audio data values (range -0.5 ... +0.5)
	 */
	public void getDoubleValues(byte[] frameData, int offset,double[] buffer) {
		//values = new float[channels];
		for (int i = 0; i < channels; i++) {
			buffer[i] = getDoubleValue(frameData, (i * sampleSize) + offset);
		}
	}
	/**
	 * Converts to normalized float values (amplitudes) of the given data.
	 * 
	 * @param frameData PCM coded data array
	 * @param offset offset (of bytes) in PCM input data 
	 * @param buffer buffer for audio data values (range -0.5 ... +0.5)
	 */
	public void getFloatValues(byte[] frameData, int offset,float[] buffer) {
		//values = new float[channels];
		for (int i = 0; i < channels; i++) {
			buffer[i] = getFloatValue(frameData, (i * sampleSize) + offset);
		}
	}
	/**
	 * Encode one frame of normalized float values to byte buffer.
	 * 
	 * @param values
	 *            float values for all channels
	 * @param buf
	 *            the buffer
	 * @param offset
	 *            bufPos in the buffer
	 */
	public void encodeToFloatValues(float[] values, byte[] buf, int offset) {
		for (int i = 0; i < channels; i++) {
			encodeValue(values[i], buf, offset + (i * sampleSize));
		}
	}
	/**
	 * Encode one frame of normalized float values to byte buffer.
	 * 
	 * @param values
	 *            float values for all channels
	 * @param buf
	 *            the buffer
	 * @param offset
	 *            bufPos in the buffer
	 */
	public void encodeValues(float[] values, byte[] buf, int offset) {
		for (int i = 0; i < channels; i++) {
			encodeValue(values[i], buf, offset + (i * sampleSize));
		}
	}
	
	   /**
     * Encode one frame of normalized double values to byte buffer.
     * 
     * @param values
     *            double values for all channels
     * @param buf
     *            the buffer
     * @param offset
     *            bufPos in the buffer
     */
    public void encodeValues(double[] values, byte[] buf, int offset) {
        for (int i = 0; i < channels; i++) {
            encodeValue(values[i], buf, offset + (i * sampleSize));
        }
    }
    
	/**
     * Encode normalized double values to byte buffer.
     * 
     * @param values
     *            double values for all channels
     * @param buf
     *            the buffer
     * @param offset
     *            bufPos in the buffer
     */
    public void encode(double[][] values,int srcOffset, byte[] buf, int offset,int frames) {
        int trgPos=offset;
        for(int f=0;f<frames;f++){
            encodeValues(values[srcOffset+f], buf, trgPos);
            trgPos+=frameSize;
        }
    }
    /**
     * Encode normalized double values to byte buffer.
     * 
     * @param values
     *            double values (one value per sample)
     * @param srcOffset
     *            offset in the source buffer
     * @param buf
     *            the buffer
     * @param offset
     *            offset in the target byte buffer
     * @param samples
     *          sample count to convert          
     *  
     */
    public void encode(double[] values,int srcOffset, byte[] buf, int offset,int samples) {
        int srcPos=srcOffset;
        int trgPos=offset;
        for(int s=0;s<samples;s++){
          encodeValue(values[srcPos], buf, trgPos);
          srcPos++;
          trgPos+=sampleSize;
            
        }
    }


}
