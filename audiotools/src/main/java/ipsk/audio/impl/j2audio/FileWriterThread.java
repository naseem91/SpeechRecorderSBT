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
 * Date  : 01.03.2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.impl.j2audio;

import ipsk.audio.ThreadSafeAudioSystem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


/**
 * Audio file writer in an own thread.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class FileWriterThread extends Thread {

	private AudioInputStream ais;

	private AudioFileFormat.Type aff;

	private File outFile;

	private FileWriterListener listener;

	private int state;

	private Exception exception;

	public static final int IDLE = 0;

	public static final int WRITING = 1;

	public static final int DONE = 2;

	public static final int ERROR = -1;

	/**
	 * Create new file writer.
	 * 
	 * @param listener
	 *            notified on events
	 * @param ais
	 *            the audio stream to read from
	 * @param aff
	 *            the audio file format
	 * @param outFile
	 *            the audio file to write to
	 */
	public FileWriterThread(FileWriterListener listener, AudioInputStream ais,
			AudioFileFormat.Type aff, File outFile) {
		this.listener = listener;
		this.ais = ais;
		this.aff = aff;
		this.outFile = outFile;
		state = IDLE;
		exception = null;

	}

	public void create() throws IOException {
		ByteArrayInputStream zeroBis = new ByteArrayInputStream(new byte[0]);
		AudioInputStream zeroAis = new AudioInputStream(zeroBis, ais
				.getFormat(), 0L);
		ThreadSafeAudioSystem.write(zeroAis, aff, outFile);
		zeroBis.close();
		zeroAis.close();
	}

	public void run() {
		listener.update(this, WRITING);
		try {
			ThreadSafeAudioSystem.write(ais, aff, outFile);
			ais.close();
		} catch (IOException e) {
			exception = e;
			state = ERROR;
			listener.update(this, state);
			//e.printStackTrace();
		}

		listener.update(this, DONE);
	}

	
	public Exception getException() {
		return exception;
	}

}
