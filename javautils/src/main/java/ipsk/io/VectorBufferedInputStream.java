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
import java.io.InputStream;


/**
 * InputStream based on {@link ipsk.io.VectorBuffer} data.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class VectorBufferedInputStream extends InputStream {


	private byte[] currBuf = new byte[0];
	private int bufPos;
	private VectorBuffer buffers;
	//private int available;
	private int vectorCount;
	private int vectorCountMark = 0;
	private int offsetMark = 0;
	
	/**
	 * Create an {@link java.io.InputStream} which gets its data from an container with byte buffer references.
	 * @param vb container with byte buffer references 
	 */
	public VectorBufferedInputStream(VectorBuffer vb) {
		super();
		vectorCount = 0;
		buffers = vb;
		if(buffers.size()>0)currBuf=buffers.get(vectorCount);
	}

/**
 *  Create an {@link java.io.InputStream} which gets its data from a {@link ipsk.io.VectorBufferedOutputStream}. 
 * @param vbos
 */
	public VectorBufferedInputStream(VectorBufferedOutputStream vbos) {
		this(vbos.getVectorBufferCopy());
	}

	/**
	 * @see java.io.InputStream#available
	 */
	public synchronized int available() {
		if (bufPos < currBuf.length){
		    return currBuf.length-bufPos;
		}else{
		    if (vectorCount+1 >=buffers.size()) return 0;
		    byte[] nextBuf=buffers.get(vectorCount+1);
		    return nextBuf.length;
		}
	}

	/**
	 * @see java.io.InputStream#mark
	 */
	public void mark(int readlimit) {
		offsetMark = bufPos;
		vectorCountMark = vectorCount;
	}
	/**
		 * @see java.io.InputStream#reset
		 */
	public void reset() {
		bufPos = offsetMark;
		vectorCount = vectorCountMark;
		currBuf=buffers.get(vectorCount);
	}
	
	/**
	 * Returns always true because mark/reset is supported.
	 * @see java.io.InputStream#markSupported
	 * @return true
	 */
	public boolean markSupported(){
		return true;
	}

	/**
	 * @see java.io.InputStream#read
	 */
	public synchronized int read() {

		if (bufPos >= currBuf.length) {
			vectorCount++;
			if (vectorCount >= buffers.size())
				return -1;
			currBuf = buffers.get(vectorCount);
			bufPos = 0;
		}
		return ((int)currBuf[bufPos++] & 0xFF);

	}
	
	

	/**
	 * @see java.io.InputStream#read(byte[],int,int)
	 */
	public synchronized int read(byte[] buf, int offset, int len) {
	    if (bufPos >= currBuf.length) {
			vectorCount++;
			if (vectorCount >= buffers.size())
				return -1;
			currBuf =  buffers.get(vectorCount);
			bufPos = 0;
		}
	    int toCopy=len;
	    int bufAvail=currBuf.length-bufPos;
	    if (bufAvail<toCopy)toCopy=bufAvail;
	    System.arraycopy(currBuf,bufPos,buf,offset,toCopy);
	    bufPos+=toCopy;
	    return toCopy;
	}


	/**
	 * @see java.io.InputStream#skip(long)
	 */
	public synchronized long skip(long n){
		long skipped=0;
		
		// skip current buffer
		int currAvail=currBuf.length-bufPos;
		int currSkip=currAvail;
		if((long)currSkip>n){
			currSkip=(int)n;
		}
		skipped+=currSkip;
		bufPos+=currSkip;
		if(bufPos==currBuf.length){
			// end fo current buffer
			// skip through buffers
			while (vectorCount+1 < buffers.size() && skipped<n){
				long toSkip=n-skipped;
				vectorCount++;
				bufPos=0;
				currBuf = buffers.get(vectorCount);
				if(toSkip > currBuf.length){
					toSkip=currBuf.length;
				}
				bufPos+=toSkip;
				skipped+=toSkip;
			}
		}
		return skipped;
		
	}
	
}
