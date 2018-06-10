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

import ipsk.audio.AudioPluginException;
import ipsk.audio.arr.Selection;
import ipsk.io.EditInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class EditPlugin extends BasicPCMPlugin {

	private long from;

	private long length;

	private AudioFormat af;

	private long frameLength;

	private int frameSize;

	public EditPlugin(long from, long length) {
		this.from = from;
		this.length = length;
	}

	public EditPlugin(Selection s) {
		this(s.getLeft(), s.getLength());
	}

	public AudioInputStream getAudioInputStream(AudioInputStream ais)
			throws AudioPluginException {

		frameLength = ais.getFrameLength();
		af = ais.getFormat();
		frameSize = af.getFrameSize();

		if (from == -1) {
			from = frameLength;
		}
		if (length == -1) {
			length = frameLength;
		}
		if (from + length > frameLength) {
			length = frameLength - from;
		}

		EditInputStream eis = new EditInputStream(ais, frameSize, from, length);
		AudioInputStream editAudioInputStream = new AudioInputStream(eis, af,
				length);
		return editAudioInputStream;
	}
}
