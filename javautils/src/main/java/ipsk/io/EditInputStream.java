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
import java.io.InputStream;

/**
 * This stream reads a segment of the underlying input stream.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class EditInputStream extends FramedEditingInputStream {

	
	private long fromByte;

	private long toByte;

	private long pos;

	private byte[] pushBackBuf;

	private int pushBackLen;

	/**
	 * Creates editing InputStream. Only the data bytes from the given position
	 * with the given length of the underlying InputStream are read. (the bytes
	 * from 'off' to 'off+length')
	 * 
	 * @param is
	 *            the underlying (source) InputStream
	 * @param off
	 *            read data starts from this frame position
	 * @param length
	 *            read length frames
	 */
	public EditInputStream(InputStream is, long off, long length) {
		this(is, 1, off, length);

	}

	/**
	 * Creates editing InputStream. Only the data frames from the given position
	 * with the given length of the underlying InputStream are read. (the bytes
	 * from 'off * frameSize' to '(off+length) * frameSize')
	 * 
	 * @param is
	 *            the underlying (source) InputStream
	 * @param frameSize
	 *            size of the data frames
	 * @param from
	 *            read data starts from this frame position
	 * @param length
	 *            read length frames
	 */
	public EditInputStream(InputStream is, int frameSize, long from, long length) {
		super(is,frameSize);
		fromByte = from * frameSize;
		toByte = (from + length) * frameSize;
		pos = 0;
		pushBackBuf = new byte[frameSize];
		pushBackLen=0;
		//available = 0;
	}

	

	public long skip(long n) throws IOException {
		//		Skip to edit start position
		// TODO check on file end !!
		if (n < 0 || n % frameSize > 0)
			throw new IOException(
					"bytes to skip must be multiple of frame size");
		long skipped = 0;
		if (n == 0)
			return 0;

		skipped += pushBackLen;
		n -= pushBackLen;
		pos+=pushBackLen;
		pushBackLen = 0;

		if (pos < fromByte) {
			tryToSkipToFrom();
		}

		//		now skip the data
		if (pos >= fromByte) {
			if (pos < toByte) {
				long toSkip = toByte - pos;
				if (toSkip > n)
					toSkip = n;
				do {
					long s= is.skip(toSkip - skipped);
					pos+=s;
					skipped+=s;
				} while (pos % frameSize > 0);
			}
		}
		if(pos % frameSize>0)System.err.println("EditInputStream: pos=="+pos);
		return skipped;
	}

	private void tryToSkipToFrom() throws IOException {
		long toSkip = fromByte - pos;
		long skipped = 0;
		do {
			long s= is.skip(toSkip - skipped);
			pos+=s;
			skipped+=s;
		} while (pos % frameSize > 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.io.FramedInputStream#read(byte[], int, int)
	 */
	public int read(byte[] buf, int offset, int len) throws IOException {
		if (len % frameSize > 0)
			throw frameSizeException;
		int copied = 0;
		while (pos < fromByte) {
			// TODO AudioSystem.write does not "like" a zero read. File this as a bug.
			tryToSkipToFrom();
		}
		if (pos >= toByte)
			return -1;
		long toRead = toByte - pos;
		if ((long) len < toRead)
			toRead = (long)len;
		if (pushBackLen > 0) {
			System.arraycopy(pushBackBuf, 0, buf, offset, pushBackLen);
			copied += pushBackLen;
			toRead -= pushBackLen;
			offset += pushBackLen;
			pushBackLen = 0;
		}
		if (offset+toRead > buf.length){
			System.err.println("buf "+buf.length+" offset: "+offset+" Read: "+toRead);
		}
		int read = is.read(buf, offset, (int) toRead);
		pushBackLen = (read+copied) % frameSize;
		if (pushBackLen > 0) {
			System.arraycopy(buf, offset, pushBackBuf, 0, pushBackLen);
		}
		copied += read - pushBackLen;
		pos += copied;
		if(pos % frameSize>0)System.err.println("EditInputStream: pos=="+pos);
		return copied;
	}
	
	public int available() throws IOException{
		if (pos<fromByte){
			tryToSkipToFrom();
		}
		if (pos<fromByte){
			return 0;
		}else {
			return is.available();  
		}
	}

}