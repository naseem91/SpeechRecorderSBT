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
 * Date  : May 7, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.spi;

import ipsk.audio.ThreadSafeAudioSystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.*;
import javax.sound.sampled.AudioFormat.Encoding;

import javax.sound.sampled.spi.FormatConversionProvider;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class LinearAudioConverter extends FormatConversionProvider {

	AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;

	AudioFormat.Encoding[] encodings;

	static AudioFormat srcFormat;

	AudioInputStream srcIn;

	AudioInputStream srcOut;

	static AudioFormat trgFormat;

	byte[] sample;

	public LinearAudioConverter() {
		encodings = new AudioFormat.Encoding[1];
		encodings[0] = encoding;
		srcFormat = new AudioFormat(48000, 32, 1, true, false);
		trgFormat = new AudioFormat(48000, 16, 1, true, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.spi.FormatConversionProvider#getSourceEncodings()
	 */
	public Encoding[] getSourceEncodings() {

		return encodings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.spi.FormatConversionProvider#getTargetEncodings()
	 */
	public Encoding[] getTargetEncodings() {

		return encodings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.spi.FormatConversionProvider#getTargetEncodings(javax.sound.sampled.AudioFormat)
	 */
	public Encoding[] getTargetEncodings(AudioFormat sourceFormat) {

		return encodings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.spi.FormatConversionProvider#getTargetFormats(javax.sound.sampled.AudioFormat.Encoding,
	 *      javax.sound.sampled.AudioFormat)
	 */
	public AudioFormat[] getTargetFormats(Encoding targetEncoding,
			AudioFormat sourceFormat) {
		if (sourceFormat.matches(srcFormat)) {
			AudioFormat[] fmtArr = new AudioFormat[1];
			fmtArr[0] = trgFormat;
			return fmtArr;
		} else {

			return new AudioFormat[0];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.spi.FormatConversionProvider#getAudioInputStream(javax.sound.sampled.AudioFormat,
	 *      javax.sound.sampled.AudioInputStream)
	 */
	public AudioInputStream getAudioInputStream(AudioFormat targetFormat,
			AudioInputStream sourceStream) {
		if (targetFormat.matches(trgFormat)) {
			LinearConverterInputStream lcis = new LinearConverterInputStream(
					sourceStream);
			return new AudioInputStream((InputStream) lcis, trgFormat,
					sourceStream.getFrameLength());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.spi.FormatConversionProvider#getAudioInputStream(javax.sound.sampled.AudioFormat.Encoding,
	 *      javax.sound.sampled.AudioInputStream)
	 */
	public AudioInputStream getAudioInputStream(Encoding targetEncoding,
			AudioInputStream sourceStream) {
		if (targetEncoding != encoding) {
			return null;
		}

		LinearConverterInputStream lcis = new LinearConverterInputStream(
				sourceStream);
		return new AudioInputStream((InputStream) lcis, trgFormat, sourceStream
				.getFrameLength());

	}

	public static void main(String[] args) {
		File in = new File("/homes/klausj/AAA1019Z0_0.wav");
		AudioInputStream ins = null;
		try {
			ins = AudioSystem.getAudioInputStream(in);
		} catch (UnsupportedAudioFileException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		LinearAudioConverter lac = new LinearAudioConverter();
		//lac.getAudioInputStream()

		boolean supp = AudioSystem.isConversionSupported(trgFormat, srcFormat);
		//System.out.println("S: " + supp);
		File out = new File("/homes/klausj/test_16.wav");
		AudioInputStream outs = null;
		outs = lac.getAudioInputStream(trgFormat, ins);
		if (supp) {
			try {
				ThreadSafeAudioSystem.write(outs, AudioFileFormat.Type.WAVE, out);
			} catch (IOException e1) {

				e1.printStackTrace();
			}finally{
				if(outs!=null)
					try {
						outs.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}

	}
}
