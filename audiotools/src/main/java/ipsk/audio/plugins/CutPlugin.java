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
 * Date  : 02.01.2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.plugins;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioPluginException;
import ipsk.audio.arr.Selection;
import ipsk.io.CuttingInputStream;

import java.io.OutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class CutPlugin extends BasicPCMPlugin {

	//private AudioSource source;
	//private long from;
	//private long frameLength;
	private Selection selection;

	AudioInputStream ais;

	AudioInputStream editAudioInputStream;

	OutputStream os;

	AudioFileFormat aff;

	AudioFormat af;

	long frameLength;

	int frameSize;

	boolean signed = true;

	public CutPlugin(long from, long length) {
		selection = new Selection();
		selection.setStart(from);
		selection.setEnd(from + length);

	}

	public CutPlugin(Selection s) {
		selection = s;
	}

	public AudioInputStream getAudioInputStream(AudioInputStream ais)
			throws AudioPluginException {

		if (selection == null)
			return ais;
		frameLength = ais.getFrameLength();
		af = ais.getFormat();
		frameSize = af.getFrameSize();
		if (af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
			signed = true;
		} else if (af.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED) {
			signed = false;
		} else {
			throw new AudioPluginException(
					new AudioFormatNotSupportedException(af));
		}
		long from = selection.getLeft();
		long length = selection.getLength();
		if (from == -1) {
			from = frameLength;
		}
		if (length == -1) {
			length = frameLength;
		}
		if (from + length > frameLength) {
			length = frameLength - from;
		}

		long resLength = frameLength - length;
		CuttingInputStream eis = new CuttingInputStream(ais, frameSize, from,
				length);
		return new AudioInputStream(eis, af, resLength);
	}
}
