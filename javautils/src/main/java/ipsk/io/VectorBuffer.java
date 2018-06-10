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

import java.util.Vector;


/**
 * A container for byte buffer references.
 * Stores references to dynamically allocated data buffers.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
public class VectorBuffer {
	
	/**
	 * total stored (referenced) data length
	 */
	protected long length;
	/**
	 * contains the buffer references.
	 */
	protected Vector<byte[]> buffers;

	/**
	 * Create new empty container.
	 *
	 */
	public VectorBuffer() {
		buffers = new Vector<byte[]>();
		length = 0;
	}

	/**
	 * Get reference to n-th buffer in the container.
	 * @param n
	 * @return n-th byte buffer
	 */
	public byte[] get(int n) {
		return (byte[]) buffers.get(n);
	}
	/**
	 * Get number of stored refrences.
	 * NOTE: This is not the total size of data.
	 * @return number of stored refrences
	 */
	public int size() {
		return buffers.size();
	}

	/**
	 * Add new buffer reference to container.
	 * @param b the new buffer
	 */
	public void add(byte[] b) {
		synchronized (this) {
			buffers.add(b);
			length += b.length;
		}
	}

	/**
	 * Remove buffer reference at index n.
	 * @param n index
	 */
	public void remove(int n) {
		synchronized (this) {
			byte[] r = (byte[]) buffers.remove(n);
			length -= r.length;
		}
	}

	/**
	 * Get a copy of the container.
	 * Does not deep copy the data buffers !
	 * @return clone of the container
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		VectorBuffer clone = new VectorBuffer();
		synchronized (this) {
			clone.buffers = (Vector<byte[]>) buffers.clone();
			clone.length = length;
		}
		return clone;
	}
	
	/**
	 * Get total data length.
	 * The summary of the data length of each referenced buffer.
	 * @return data length
	 */
	public long getLength() {
		return length;
	}

	/**
	 * Get reference to the buffers.
	 * Returns NO copy of the container.
	 * @see #clone
	 * @return reference to container
	 */
	public Vector getBuffers() {
		return buffers;
	}

	/**
	 * Creates byte array and fills it with the data.
	 * @return contents of buffer as byte array
	 */
	public synchronized byte[] toByteArray(){
	    if (length>Integer.MAX_VALUE)return null;
	    
	    byte[] buf=new byte[(int)length];
	    int pos=0;
      for(int i=0;i<buffers.size();i++){
          byte[] srcBuf=get(i);
          System.arraycopy(srcBuf,0,buf,pos,srcBuf.length);
          pos+=srcBuf.length;
      }
	    return buf;
	}
	
}
