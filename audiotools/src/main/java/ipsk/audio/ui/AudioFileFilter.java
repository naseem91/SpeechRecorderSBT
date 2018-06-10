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
 * Date  : Jul 8, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.ui;

import ipsk.audio.ThreadSafeAudioSystem;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.filechooser.FileFilter;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class AudioFileFilter extends FileFilter {

	AudioFileFormat.Type[] audioFileTypes;

	/**
	 *  
	 */
	public AudioFileFilter() {
		super();
		audioFileTypes = ThreadSafeAudioSystem.getAudioFileReaderTypes();
//		audioFileTypes=AudioSystem.getAudioFileTypes();
	}

	public AudioFileFilter(AudioFileFormat.Type[] audioFileTypes) {
		super();
		this.audioFileTypes = audioFileTypes;
	}

	public AudioFileFilter(AudioFileFormat.Type audioFileType) {
		super();
		this.audioFileTypes = new AudioFileFormat.Type[] { audioFileType };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File arg0) {
		if (arg0.isDirectory())
			return true;
		String name = arg0.getName();
		int extIndex = name.lastIndexOf('.');
		if (extIndex == -1)
			return false;
		String ext = name.substring(extIndex + 1);
		for (int i = 0; i < audioFileTypes.length; i++) {
			if (ext.equalsIgnoreCase(audioFileTypes[i].getExtension()))
				return true;
		}

		return false;
	}

	public void setAudioFileTypes(AudioFileFormat.Type[] audioFileTypes) {
		this.audioFileTypes = audioFileTypes;

	}

	public AudioFileFormat.Type[] getAudioFileTypes() {
		return audioFileTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		if (audioFileTypes.length == 1) {
			return new String("Audio filetype " + audioFileTypes[0].toString());
		} else {
			return new String("Audio files");
		}
	}

}
