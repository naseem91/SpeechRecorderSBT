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
public class EncodingPlugin implements AudioPlugin {

	protected AudioFormat[] supportedAudioFormats;

	protected AudioFormat inputFormat;

	protected AudioFormat outputFormat = null;

	protected AudioFormat.Encoding encoding;

	/**
	 *  
	 */
	public EncodingPlugin(AudioFormat.Encoding encoding) {
		this.encoding = encoding;
		supportedAudioFormats = new AudioFormat[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#getAudioInputStream(javax.sound.sampled.AudioInputStream)
	 */
	public AudioInputStream getAudioInputStream(AudioInputStream source)
			throws AudioPluginException {
		if (source.getFormat().getEncoding().equals(encoding)) {
			return source;
		}
		if (outputFormat == null)
			return AudioSystem.getAudioInputStream(encoding, source);
		return AudioSystem.getAudioInputStream(outputFormat, source);

	}

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

		return AudioSystem.isConversionSupported(encoding, audioFormat);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#isOutputFormatSupported(javax.sound.sampled.AudioFormat,
	 *      javax.sound.sampled.AudioFormat)
	 */
	public boolean isOutputFormatSupported(AudioFormat inputFormat,
			AudioFormat outputFormat) {
		if (!isInputFormatSupported(inputFormat))
			return false;
		if (!outputFormat.getEncoding().equals(encoding))
			return false;
		return AudioSystem.isConversionSupported(outputFormat, inputFormat);
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
		if (outputFormat != null)
			return outputFormat;
		AudioFormat[] formats=AudioSystem.getTargetFormats(encoding, inputFormat);
		if(formats!=null && formats.length>0)return formats[0];
		// finally act as bypass plugin
		return inputFormat;
	}
}
