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
 * Date  : 16.09.2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.tools;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.dsp.Statistic;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class CrackFinder {

	private int threshold;

	//AudioInputStream ais;

	private boolean bigEndian;

	private boolean signed = false;

	static final boolean DEBUG = false;

	byte[] data;

	int length;

	AudioFormat af;

	int channels;

	int frameSize;

	int sampleSize;

	int frames;

	int last[];

	float maxValue, minValue;

	int resolutionBits;

	float pp;

	float ln = (float) (20 / Math.log(10));

	boolean processing = false;

	Statistic s;

	AudioFormat.Encoding ae;

	public CrackFinder(AudioFormat af, int threshold)
			throws AudioFormatNotSupportedException {

		this.af = af;
		this.threshold = threshold;
		if (af.getSampleSizeInBits() > 64)
			throw new AudioFormatNotSupportedException(af);
		ae = af.getEncoding();
		if (ae == AudioFormat.Encoding.PCM_SIGNED) {
			signed = true;
			resolutionBits = af.getSampleSizeInBits();
			pp = (float) Math.pow(2.0, (double) resolutionBits);
			maxValue = pp / 2;
			minValue = -maxValue + 1;
		} else if (ae == AudioFormat.Encoding.PCM_UNSIGNED) {
			throw new AudioFormatNotSupportedException(af);
			/*
			 * resolutionBits=af.getSampleSizeInBits(); pp=(float)
			 * Math.pow(2.0,(double)resolutionBits); maxValue=pp; minValue=0;
			 */
		} else {

			throw new AudioFormatNotSupportedException(af);
		}

		channels = af.getChannels();
		frameSize = af.getFrameSize();
		sampleSize = frameSize / af.getChannels();
		bigEndian = af.isBigEndian();

		//		max = new float[channels];
		//		min = new float[channels];
		last = new int[channels];
	}

	protected int getSampleValue(byte[] sample) {
		int is = 0;
		int size = sample.length;
		if (signed) {
			if (!bigEndian) {

				for (int i = size - 1; i > 0; i--) {
					is = is | (0xFF & (int) sample[i]);
					is = is << 8;
				}
				is = is | (0xFF & (int) sample[0]);

			} else {

				for (int i = 0; i < size - 1; i++) {
					is = is | (0xFF & (int) sample[i]);
					is = is << 8;
				}
				is = is | (0xFF & (int) sample[size - 1]);

			}

			if (size == 1) {
				byte b = (byte) is;
				is = (int) b;
			} else if (size == 2) {
				short s = (short) is;
				is = (int) s;
			}

			//fval = (float) is / pp;

		}

		return is;

	}

	public void findCracks(File file, AudioInputStream ais)
			throws UnsupportedAudioFileException, IOException {

		frames = (int) ais.getFrameLength();
		data = new byte[(int) ((long) ais.getFormat().getFrameSize() * frames)];
		ais.read(data, 0, data.length);

		int bc = 0;
		byte[] sample = new byte[sampleSize];
		int[] val = new int[channels];
		int[] delta = new int[channels];
		//int[] lastDelta=new int[channels];
		for (long f = 0; f < frames; f++) {
			for (int ch = 0; ch < channels; ch++) {

				for (int i = 0; i < sampleSize; i++) {
					sample[i] = data[bc++];

				}
				val[ch] = getSampleValue(sample);
				if (DEBUG)
					System.out.println(val[ch] + " - " + last[ch]);
				delta[ch] = val[ch] - last[ch];
				//if (Math.abs(val[ch] - last[ch]) > threshold) {
				if (Math.abs(delta[ch]) > threshold) {
					System.out.println(file.getAbsolutePath() + " " + f
							+ " delta: " + delta[ch]);
				}

				//				if (val[ch] > max[ch])
				//					max[ch] = val[ch];
				//				if (val[ch] < min[ch])
				//					min[ch] = val[ch];
				last[ch] = val[ch];
				//lastDelta[ch]=delta[ch];

			}

		}
	}

	public static void main(String[] args) {
		for (int i = 1; i < args.length; i++) {
			File f = new File(args[i]);
			AudioInputStream ais;
			try {
				ais = AudioSystem.getAudioInputStream(f);

				CrackFinder cf;

				cf = new CrackFinder(ais.getFormat(), Integer.parseInt(args[0]));

				cf.findCracks(f, ais);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
