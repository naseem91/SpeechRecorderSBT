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
 * Basic class for framed stream operations.
 * @author klausj
 *
 */
public abstract class FramedInputStream extends InputStream {

	
	protected int frameSize;
	private byte[] singleByteBuf=new byte[1];
	protected IOException frameSizeException=new IOException(
    "bytes to read must be multiple of frame size");
	
	/**
	 * 
	 */
	public FramedInputStream(int frameSize) {
		super();
		this.frameSize=frameSize;
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		if (frameSize != 1)
            throw new IOException(
                    "read() method is only allowed if frame size equals 1");
		
        int read = read(singleByteBuf, 0, 1);
        if (read == -1)
            return read;
        return 0xFF & singleByteBuf[0];
	}
	
	public int read(byte[] buf) throws IOException{
		return read(buf,0,buf.length);
	}
	public abstract int read(byte[] buf,int offset,int len) throws IOException;
	

}
