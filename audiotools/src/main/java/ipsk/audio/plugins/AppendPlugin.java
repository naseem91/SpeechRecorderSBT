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

import java.io.SequenceInputStream;

import ipsk.audio.AudioPluginException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.SourcePlugin;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;


/**
 * audio plugin to append an audio stream.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class AppendPlugin extends BasicPCMPlugin implements SourcePlugin{

	private AudioSource insertSource;

	//private long at;

	private AudioFormat af;

	private long frameLength;

	//private int frameSize;

	public AppendPlugin(AudioSource appendSource) {
		this.insertSource = appendSource;
		
	}

	public AudioInputStream getAudioInputStream(AudioInputStream ais)
			throws AudioPluginException {

		AudioInputStream insertAis;
		try {
			insertAis = insertSource.getAudioInputStream();
		} catch (AudioSourceException e) {
			throw new AudioPluginException(e);
		}
		frameLength = ais.getFrameLength() + insertAis.getFrameLength();
		af = ais.getFormat();
		AudioFormat insertAf = insertAis.getFormat();

		if (!af.matches(insertAf))
			throw new AudioPluginException(
					"Insert audio format does not match !");
		//frameSize = af.getFrameSize();

		

		SequenceInputStream sis = new SequenceInputStream(ais, insertAis);
		return new AudioInputStream(sis, af, frameLength);
	}

	public AudioSource getAudioSource() {
		return insertSource;
	}

}
