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
 * Date  : Sep 9, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.plugins;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioPlugin;
import ipsk.audio.AudioPluginException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public abstract class BasicPCMPlugin implements AudioPlugin {

	protected AudioFormat[] supportedAudioFormats;

	protected AudioFormat inputFormat;

	protected AudioFormat outputFormat;

	/**
	 *  
	 */
	public BasicPCMPlugin() {
		supportedAudioFormats = new AudioFormat[] {
				new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, true),
				new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, false),
				new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, true),
				new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, false) };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#getAudioInputStream(javax.sound.sampled.AudioInputStream)
	 */
	public abstract AudioInputStream getAudioInputStream(AudioInputStream source)
			throws AudioPluginException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#getSupportedInputFormats()
	 */
	public AudioFormat[] getSupportedInputFormats() {
		return supportedAudioFormats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#getSupportedOutputFormats(javax.sound.sampled.AudioFormat)
	 */
	public AudioFormat[] getSupportedOutputFormats(AudioFormat inputFormat) {
		if (isInputFormatSupported(inputFormat))
			return new AudioFormat[] { inputFormat };
		else
			return new AudioFormat[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#getControls()
	 */
	public Control[] getControls() {
		return new Control[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#isFormatSupported(javax.sound.sampled.AudioFormat)
	 */
	public boolean isInputFormatSupported(AudioFormat audioFormat) {
		for (int i = 0; i < supportedAudioFormats.length; i++) {
			// AudioFormat.matches() checks AudioSystem.NOT_SPECIFIED
			// only on samplerate and framerate, so we have to write it
			// ourselves
			//			if (audioFormat.matches(supportedAudioFormats[i]))
			AudioFormat suppFormat = supportedAudioFormats[i];
			if (suppFormat.getEncoding().equals(audioFormat.getEncoding())
					&& ((suppFormat.getSampleRate() == (float) AudioSystem.NOT_SPECIFIED) || (suppFormat
							.getSampleRate() == audioFormat.getSampleRate()))
					&& (suppFormat.getSampleSizeInBits() == AudioSystem.NOT_SPECIFIED || suppFormat
							.getSampleSizeInBits() == audioFormat
							.getSampleSizeInBits())
					&& (suppFormat.getChannels() == AudioSystem.NOT_SPECIFIED || suppFormat
							.getChannels() == audioFormat.getChannels()
							&& (suppFormat.getFrameSize() == AudioSystem.NOT_SPECIFIED || suppFormat
									.getFrameSize() == audioFormat
									.getFrameSize())
							&& ((suppFormat.getFrameRate() == (float) AudioSystem.NOT_SPECIFIED) || (suppFormat
									.getFrameRate() == audioFormat
									.getFrameRate()))
							&& ((suppFormat.getSampleSizeInBits() <= 8) || (suppFormat
									.isBigEndian() == audioFormat.isBigEndian()))))
				return true;

		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#isOutputFormatSupported(javax.sound.sampled.AudioFormat,
	 *      javax.sound.sampled.AudioFormat)
	 */
	public boolean isOutputFormatSupported(AudioFormat inputFormat,
			AudioFormat outputFormat) {
		if (isInputFormatSupported(inputFormat)
				&& outputFormat.matches(inputFormat))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#setInputFormat(javax.sound.sampled.AudioFormat)
	 */
	public void setInputFormat(AudioFormat inputFormat)
			throws AudioFormatNotSupportedException {
		if (!isInputFormatSupported(inputFormat))
			throw new AudioFormatNotSupportedException(inputFormat);
		this.inputFormat = inputFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#getInputFormat()
	 */
	public AudioFormat getInputFormat() {
		return inputFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#setOutputFormat(javax.sound.sampled.AudioFormat)
	 */
	public void setOutputFormat(AudioFormat outputFormat)
			throws AudioFormatNotSupportedException {
		if (!isOutputFormatSupported(inputFormat, outputFormat))
			throw new AudioFormatNotSupportedException(outputFormat);
		this.outputFormat = outputFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#getOutputFormat()
	 */
	public AudioFormat getOutputFormat() {
		return outputFormat;
	}
}
