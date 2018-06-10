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
 * Date  : Aug 6, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Control;


/**
 * Interface for audio processing plugins. 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public interface AudioPlugin {

	public AudioInputStream getAudioInputStream(AudioInputStream source)
			throws AudioPluginException;

	public AudioFormat[] getSupportedInputFormats();

	public AudioFormat[] getSupportedOutputFormats(AudioFormat inputFormat);

	public boolean isInputFormatSupported(AudioFormat inputFormat);

	public boolean isOutputFormatSupported(AudioFormat inputFormat,
			AudioFormat outputFormat);

	public void setInputFormat(AudioFormat inputFormat)
			throws AudioFormatNotSupportedException;

	public AudioFormat getInputFormat();

	public void setOutputFormat(AudioFormat outputFormat)
			throws AudioFormatNotSupportedException;

	public AudioFormat getOutputFormat();

	public Control[] getControls();

}
