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
 * Date  : Jun 10, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.io;

import java.awt.Component;
import java.io.IOException;
import java.io.InterruptedIOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.swing.ProgressMonitor;

/**
 * Provides a progress monitor for audio streams.
 * @see javax.swing.ProgressMonitor
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class ProgressMeasuringAudioInputStream extends AudioInputStream {

	private AudioInputStream srcAudioInputStream;

	private int frameSize;

	private int position;

	private long frameLength;

	private ProgressMonitor progressMonitor;

	
	public ProgressMeasuringAudioInputStream(
			AudioInputStream srcAudioInputStream, Component parentComponent,
			Object message, String note) {
		super(srcAudioInputStream, srcAudioInputStream.getFormat(),
				srcAudioInputStream.getFrameLength());
		this.srcAudioInputStream = srcAudioInputStream;
		AudioFormat audioFormat = srcAudioInputStream.getFormat();
		frameSize = audioFormat.getFrameSize();
		//channels = audioFormat.getChannels();
		frameLength = srcAudioInputStream.getFrameLength();
		position = 0;
		// TODO divide size to fit in integer
		progressMonitor = new ProgressMonitor(parentComponent, message, note,
				0, (int) (frameLength * frameSize));
		progressMonitor.setMillisToDecideToPopup(0);
	}

	public int available() throws IOException {
		return srcAudioInputStream.available();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#close()
	 */
	public void close() throws IOException {

		srcAudioInputStream.close();
		progressMonitor.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#getFormat()
	 */
	public AudioFormat getFormat() {
		return srcAudioInputStream.getFormat();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#getFrameLength()
	 */
	public long getFrameLength() {
		return srcAudioInputStream.getFrameLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#mark(int)
	 */
	public synchronized void mark(int arg0) {
		srcAudioInputStream.mark(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#markSupported()
	 */
	public boolean markSupported() {
		return srcAudioInputStream.markSupported();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#read()
	 */
	public int read() throws IOException {
//		if (frameSize != 1)
//			throw new IOException(
//					"read method only allowed for frame size == 1");
		if (progressMonitor.isCanceled()) {
			throw new InterruptedIOException("Audio I/O transfer cancelled");
		}
		byte[] buf = new byte[1];

		int read = read(buf, 0, 1);
		if (read == -1)
			return -1;

		return 0x00FF & (int) read;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#read(byte[])
	 */
	public int read(byte[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#read(byte[], int, int)
	 */
	public int read(byte[] buf, int offset, int len) throws IOException {
		// try to fill process buffer
//		if (len % frameSize != 0)
//			throw new IOException("only multiple of framesize can be read");
		if (progressMonitor.isCanceled()) {
			throw new InterruptedIOException("Audio I/O transfer cancelled");
		}
		int read = srcAudioInputStream.read(buf, offset, len);
		if (read == -1)
			return read;
		position += read;
		progressMonitor.setProgress(position);
		return read;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#reset()
	 */
	public synchronized void reset() throws IOException {
		if (progressMonitor.isCanceled()) {
			throw new InterruptedIOException("Audio I/O transfer cancelled");
		}
		srcAudioInputStream.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		if (progressMonitor.isCanceled()) {
			throw new InterruptedIOException("Audio I/O transfer cancelled");
		}
		long skipped = srcAudioInputStream.skip(n);
		position += n;
		return skipped;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new String("Progress monitoring audio stream based on:"
				+ srcAudioInputStream.toString());
	}

}
