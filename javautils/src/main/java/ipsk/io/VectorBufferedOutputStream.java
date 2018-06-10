//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.io;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

/**
 * OutputStream stores data to a {@link ipsk.io.VectorBuffer}.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class VectorBufferedOutputStream extends OutputStream {

	public final static int DEFAULT_BUFSIZE = 2048;
	private byte[] currBuf;
	private VectorBuffer vectorBuffer;
	private int bufPos;
	private boolean closed;
	private int bufSize = DEFAULT_BUFSIZE;

	/**
	 * Create new vector buffered {@link java.io.OutputStream}.
	 *
	 */
	public VectorBufferedOutputStream() {
		this(DEFAULT_BUFSIZE);
	}
	
	/**
	 * Create new vector buffered {@link java.io.OutputStream}.
	 * 
	 * @param bufSize the bufsize to use for the buffers
	 */
	public VectorBufferedOutputStream(int bufSize) {
		this.bufSize = bufSize;
		currBuf = new byte[bufSize];
		bufPos = 0;
		vectorBuffer = new VectorBuffer();
		closed = false;

	}

	/**
	 * Get a clone of the data. 
	 * @return vector buffer clone
	 */
	public synchronized VectorBuffer getVectorBufferCopy() {
		return (VectorBuffer) vectorBuffer.clone();
	}

	/**
	 * Get the data as vector buffer.
	 * @return vector buffer
	 */
	public synchronized VectorBuffer getVectorBuffer() {
			return (VectorBuffer) vectorBuffer;
		}

	/**
	 * Get length of data.
	 * @return length of data in bytes
	 */
	public synchronized long getLength() {
		if (closed) {
			return vectorBuffer.getLength();
		} else {
			return 0;
		}
	}

	
	public boolean isClosed() {
		return closed;
	}

	/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
	public synchronized void write(int b) throws IOException {
	
		if (bufPos == bufSize) {
			// Create new buffer
			bufPos = 0;
			vectorBuffer.add(currBuf);
			
			currBuf = new byte[bufSize];
		}
		currBuf[bufPos++] = (byte) (b & 0xFF);

	}
	
	public synchronized void write(byte[] buf){
	    write(buf,0,buf.length);
	}
	public synchronized void write(byte[] buf, int offset, int len){
	    int copied=0;
	    while(copied<len){
	    if (bufPos == bufSize) {
			// Create new buffer
			bufPos = 0;
			vectorBuffer.add(currBuf);
			currBuf = new byte[bufSize];
		}
	    int toCopy=len-copied;
	    int bufAvail=currBuf.length-bufPos;
	    if(bufAvail<toCopy)toCopy=bufAvail;
	    System.arraycopy(buf,offset+copied,currBuf,bufPos,toCopy);
	    bufPos+=toCopy;
	    copied+=toCopy;
	    }
	   
}

	
	public int available(){
	    if(bufPos <currBuf.length)return currBuf.length-bufPos;
	    return bufSize;
	}
	
	public synchronized void close() {
		if (closed) return;
		if (bufPos > 0) {
			// Copy last incomplete buffer
			byte[] lastBuf = new byte[bufPos];

			for (int i = 0; i < bufPos; i++) {
				lastBuf[i] = currBuf[i];
			}
			vectorBuffer.add(lastBuf);
			
			bufPos = 0;
		}

		closed = true;
	}

}
