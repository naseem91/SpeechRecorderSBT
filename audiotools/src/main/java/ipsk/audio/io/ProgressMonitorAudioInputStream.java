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

import ipsk.awt.AWTEventTransferAgent;
import ipsk.awt.ProgressListener;
import ipsk.awt.event.ProgressEvent;
import ipsk.util.ProgressStatus;

import java.io.IOException;
import java.util.EventListener;
import java.util.EventObject;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * Audio stream which generates progress events.
 * By using the {@link javax.sound.sampled.AudioSystem#write(AudioInputStream, javax.sound.sampled.AudioFileFormat.Type, java.io.File) } method we are not able to monitor write progress
 * of large audio files. This stream wraps the original audio stream. 
 *   
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class ProgressMonitorAudioInputStream extends AudioInputStream {

	public class EventTransformer extends AWTEventTransferAgent {
		@Override
		public void fireEvent(EventListener l, EventObject ev) {
			ProgressListener pl = (ProgressListener) l;
			pl.update((ProgressEvent) ev);
		}
	}

	private AudioInputStream srcAudioInputStream;

	private int frameSize;

	private long position;

	private long frameLength;
	private EventTransformer evTr = new EventTransformer();
	private boolean sendFinishEvent = false;

	private long byteLength;

	private boolean cancelled;
	
	private ProgressStatus progressStatus;

	
	public ProgressMonitorAudioInputStream(AudioInputStream srcAudioInputStream) {
		super(srcAudioInputStream, srcAudioInputStream.getFormat(),
				srcAudioInputStream.getFrameLength());
		this.srcAudioInputStream = srcAudioInputStream;

		AudioFormat audioFormat = srcAudioInputStream.getFormat();
		frameSize = audioFormat.getFrameSize();

		// channels = audioFormat.getChannels();
		frameLength = srcAudioInputStream.getFrameLength();
		byteLength = frameSize * frameLength;
		progressStatus=new ProgressStatus();
		progressStatus.setLength(byteLength);
		progressStatus.open();
		position = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#available()
	 */
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
		progressStatus.done();
		evTr.fireAWTEventAndWait(new ProgressEvent(this, progressStatus));
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
		if (cancelled) {
			throw new CancelledException();
		}
		int read = srcAudioInputStream.read(buf, offset, len);
		if (read == -1) {
			return read;
		}

		if (read > 0) {
			position += read;
			//long progress = ((position * 100) / byteLength);
			progressStatus.setProgress(position);
			
			evTr.fireAWTEventAndWait(new ProgressEvent(this, progressStatus));
		}
		return read;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#reset()
	 */
	public synchronized void reset() throws IOException {
		srcAudioInputStream.reset();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.sound.sampled.AudioInputStream#skip(long)
	 */
	public long skip(long n) throws IOException {
		if (cancelled) {
			throw new CancelledException();
		}
		long skipped = srcAudioInputStream.skip(n);
		position += n;
		progressStatus.setProgress(position);
		evTr.fireAWTEventAndWait(new ProgressEvent(this, progressStatus));
		return skipped;
	}

	
	public synchronized void addProgressListener(
			ProgressListener progressListener) {
		evTr.addListener(progressListener);
	}

	public synchronized void removeProgressListener(
			ProgressListener progressListener) {
		evTr.removeListener(progressListener);
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

//	public boolean isSendFinishEvent() {
//		return sendFinishEvent;
//	}
//
//	public void setSendFinishEvent(boolean sendFinishEvent) {
//		this.sendFinishEvent = sendFinishEvent;
//	}
//
	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
