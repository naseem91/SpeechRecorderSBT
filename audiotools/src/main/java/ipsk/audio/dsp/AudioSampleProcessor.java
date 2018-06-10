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
 * Calculates amplitude values for an audio sample.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public abstract class AudioSampleProcessor {

	public final static double AUDITORY_THRESHOLD=2*10E-05;
	
	// map OdB (maximum) ampltitude to sound pressure values (0dB ampl -> 60dB sound pressure level)
	// TV set at 1m
	public final static double DEFAULT_0DB_SOUND_PRESSURE=2*10E-02;
	
	static final boolean DEBUG = false;

	protected AudioFormat af;

	protected AudioFormat.Encoding ae;

	protected boolean bigEndian;

	protected boolean signed = false;

	protected int channels;

	protected int frameSize;

	protected int sampleSize;

	protected double maxValue, minValue;

	protected int resolutionBits;

	protected double pp;

	/**
	 * Creates new audio sample processor.
	 *  
	 */
	public AudioSampleProcessor() {
	}

	/**
	 * Creates new audio sample processor.
	 * 
	 * @param af
	 *            audio format of the samples to process
	 * @throws AudioFormatNotSupportedException
	 */
	public AudioSampleProcessor(AudioFormat af)
			throws AudioFormatNotSupportedException {
		setAudioFormat(af);
	}

	/**
	 * Sets audio format of the samples to process.
	 * 
	 * @param af
	 *            audio format of the samples
	 * @throws AudioFormatNotSupportedException
	 */
	public void setAudioFormat(AudioFormat af)
			throws AudioFormatNotSupportedException {
		this.af = af;

		if (af.getSampleSizeInBits() > 32)
			throw new AudioFormatNotSupportedException(af);
		ae = af.getEncoding();
		if (ae == AudioFormat.Encoding.PCM_SIGNED) {
			signed = true;
			resolutionBits = af.getSampleSizeInBits();
			pp = (float) Math.pow(2.0, (double) resolutionBits);
			maxValue = pp / 2;
			minValue = -maxValue + 1;
		} else if (ae == AudioFormat.Encoding.PCM_UNSIGNED) {
			//throw new AudioFormatNotSupportedException(af);
			
			resolutionBits=af.getSampleSizeInBits(); 
			pp=(float)Math.pow(2.0,(double)resolutionBits);
			maxValue=pp;
			minValue=0;
			
		} else {

			throw new AudioFormatNotSupportedException(af);
		}
		channels = af.getChannels();
		frameSize = af.getFrameSize();
		sampleSize = frameSize / af.getChannels();
		bigEndian = af.isBigEndian();
	}

	/**
	 * Returns the normalized double value (amplitude) of the given sample.
	 * 
	 * @param sample
	 * @return normalized value (-1.0 ... +1.0)
	 */
	public double getNormalizedValue(byte[] sample) {
		return getDoubleValue(sample, 0)*2;
	}
	/**
	 * Returns the normalized float value (amplitude) of the given sample.
	 * 
	 * @param sample audio sample data buffer
	 * @return double value (-0.5 ... +0.5)
	 */
	public float getFloatValue(byte[] sample) {
		return getFloatValue(sample, 0);
	}

	/**
	 * Returns the double value (amplitude) of the given data.
	 * 
	 * @param sample audio sample data buffer
 	 * @param offset offset in the buffer
	 * @return double value 
	 */
	public double getDoubleValue(byte[] sample, int offset) {

		if (signed) {
			int is;
			if (!bigEndian) {

				is = (int) sample[offset + sampleSize - 1];
				for (int i = sampleSize - 2; i >= 0; i--) {
					is = is << 8;
					is = is | (0xFF & (int) sample[offset + i]);
				}
			} else {
				is = (int) sample[offset];
				for (int i = 0; i < sampleSize-1; i++) {
					is = is << 8;
					is = is | (0xFF & (int) sample[i + offset]);
				}
			}
			return (double) is / pp;
		} else {
			// Not tested !!!
			long is;
			if (!bigEndian) {

				is = 0xFF & (long) sample[offset + sampleSize - 1];
				for (int i = sampleSize - 2; i >= 0; i--) {
					is = is << 8;
					is = is | (0xFF & (long) sample[offset + i]);
				}
			} else {
				is = 0xFF & (long) sample[offset];
				for (int i = 0; i < sampleSize-1; i++) {
					is = is << 8;
					is = is | (0xFF & (long) sample[i + offset]);
				}
			}
			return (double) is / pp;
		}
	}
	/**
	 * Returns the float value (amplitude) of the given data.
	 * 
	 * @param sample audio sample data buffer
 	 * @param offset offset in the buffer
	 * @return normalized float value (-1.0 ... +1.0)
	 */
	
	public double getNormalizedValue(byte[] sample, int offset) {
	return getDoubleValue(sample,offset)*2;
	}
	/**
	 * Returns the normalized float value (amplitude) of the given data.
	 * 
	 * @param sample audio sample data buffer
 	 * @param offset offset in the buffer
	 * @return normalized float value (-0.5 ... +0.5)
	 */
	public float getFloatValue(byte[] sample, int offset) {

		if (signed) {
			int is;
			if (!bigEndian) {

				is = (int) sample[offset + sampleSize - 1];
				for (int i = sampleSize - 2; i >= 0; i--) {
					is = is << 8;
					is = is | (0xFF & (int) sample[offset + i]);
				}
			} else {
				is = (int) sample[offset];
				for (int i = 0; i < sampleSize-1; i++) {
					is = is << 8;
					is = is | (0xFF & (int) sample[i + offset]);
				}
			}
			return (float) is / (float)pp;
		} else {
			// Not tested !!!
			long is;
			if (!bigEndian) {

				is = 0xFF & (long) sample[offset + sampleSize - 1];
				for (int i = sampleSize - 2; i >= 0; i--) {
					is = is << 8;
					is = is | (0xFF & (long) sample[offset + i]);
				}
			} else {
				is = 0xFF & (long) sample[offset];
				for (int i = 0; i < sampleSize-1; i++) {
					is = is << 8;
					is = is | (0xFF & (long) sample[i + offset]);
				}
			}
			return (float) is / (float)pp;
		}
	}
	
	/**
	 * Encodes double values into byte array.
	 * 
	 * @param f
	 *            normalized amplitude value (-1.0 ... 1.0)
	 * @param buf
	 *            target data buffer
	 * @param offset
	 *            offset in the buffer
	 */
	public void encodeValue(double f, byte[] buf, int offset) {
		long value = (long) (maxValue * f);
		if (signed) {
			if (bigEndian) {
				for (int i = sampleSize - 1; i > 0; i--) {
					buf[offset + i] = (byte) value;
					value = value >> 8;
				}
				buf[offset] = (byte) value;
			} else {
				for (int i = 0; i < sampleSize - 1; i++) {
					buf[offset + i] = (byte) value;
					value = value >> 8;
				}
				buf[offset + sampleSize - 1] = (byte) value;
			}
		}
	}

	public double amplitudeToSoundPressure(double ampl){
		
		// TODO test !
		return ampl*2*DEFAULT_0DB_SOUND_PRESSURE;
	}
	
	/**
	 * Encodes normalized double values to byte array.
	 * 
	 * @param f
	 *            normalized amplitude value (-1.0 ... 1.0)
	 * @return encoded buffer
	 */
	public byte[] getEncodedValue(double f) {
		byte[] buf = new byte[sampleSize];
		encodeValue(f, buf, 0);
		return buf;
	}

	public int getChannels() {
		return channels;
	}

	
	public int getFrameSize() {
		return frameSize;
	}


	public int getSampleSize() {
		return sampleSize;
	}

}
