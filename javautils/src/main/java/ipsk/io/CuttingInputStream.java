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
public class CuttingInputStream extends FramedEditingInputStream {

	
	private long fromByte;
	private long toByte;
	private long pos;
	private long isPos;
	
	private byte[] frameBuf;
	private int pushBackLen;

	/**
		 * Creates editing InputStream.
		 * Only the data bytes from the given position with the given length of the underlying InputStream are read.
		 * (the bytes from 'off' to 'off+length')
		 *  
		 * @param is the underlying (source) InputStream
		 * @param off read data starts from this frame position 
		 * @param length read length frames
		 */
	public CuttingInputStream(InputStream is, long off, long length) {
		this(is, 1, off, length);

	}

	/**
	 * Creates editing InputStream.
	 * Only the data frames from the given position with the given length of the underlying InputStream are read.
	 * (the bytes from 'off * frameSize' to '(off+length) * frameSize')
	 *  
	 * @param is the underlying (source) InputStream
	 * @param frameSize size of the data frames
	 * @param off read data starts from this frame position 
	 * @param length read length frames
	 */
	public CuttingInputStream(
		InputStream is,
		int frameSize,
		long off,
		long length) {
	    super(is,frameSize);
		
		this.fromByte = off*frameSize;
		toByte = fromByte + (length*frameSize);
		pos = 0;
		frameBuf = new byte[frameSize];
		pushBackLen = 0;
isPos=0;
	}

	


	public int read(byte[] buf, int offset, int length) throws IOException {
		if (length % frameSize > 0)
			throw new IOException("bytes to read must be multiple of frame size");
		
		if (length == 0)
			return 0;
		// Check for rest bytes in frame buffer	
		int copied=0;
		long toRead=length;
		
		
		    if (pushBackLen > 0) {
	            System.arraycopy(frameBuf, 0, buf, offset, pushBackLen);
	            offset += pushBackLen;
	            copied += pushBackLen;
	            pos+=pushBackLen;
	            length -= pushBackLen;
	            toRead-=pushBackLen;
	            pushBackLen=0;
	        }
	
		if (isPos < fromByte) {
			if (toRead > fromByte -isPos)toRead=fromByte-isPos;
			int read = is.read(buf, offset, (int)toRead);
			if (read==-1)return read;
			isPos+=read;
			pushBackLen=(copied+read)%frameSize;
			if (pushBackLen>0){
			System.arraycopy(buf,offset+read-pushBackLen,frameBuf,0,pushBackLen);
			}
			copied+=(read-pushBackLen);
			return copied;
		}
		// Check for cut position
		while (isPos >= fromByte && isPos < toByte) {
			long toSkip = toByte - isPos;
				isPos+= is.skip(toSkip);
		}
		
		if (isPos >= toByte) {
		    int read = is.read(buf, offset, (int)toRead);
		    if (read==-1)return read;
			isPos+=read;
			pushBackLen=(copied+read)%frameSize;
			if (pushBackLen>0){
			System.arraycopy(buf,offset+read-pushBackLen,frameBuf,0,pushBackLen);
			}
			copied+=(read-pushBackLen);
			return copied;
		
		}
		if(copied==0)return read(buf,offset,length);
		return copied;

	}

	public long skip(long n) throws IOException {
		if (n % frameSize > 0)
			throw new IOException("bytes to skip must be multiple of frame size");
		if (n == 0)
			return 0;
		// Check for rest bytes in frame buffer	
		int skipped=0;
		long toSkip=n;
		
		
		    if (pushBackLen > 0) {
	            skipped += pushBackLen;
	            pos+=pushBackLen;
	            toSkip-=pushBackLen;
	            pushBackLen=0;
	        }
	
		if (isPos < fromByte) {
			if (toSkip > fromByte -isPos)toSkip=fromByte-isPos;
			do{
			    long s = is.skip(toSkip);
			    skipped+=s;
			    isPos+=s;
			    
			}while(skipped %frameSize >0);
			
			return skipped;
		}
		// Check for cut position
		if (isPos >= fromByte && isPos < toByte) {
			long isToSkip = toByte - isPos;
				isPos+= is.skip(isToSkip);
		}
		if (isPos >= toByte) {
		    do{
		        long s=is.skip(toSkip);
		        skipped+=s;
		        isPos+=s;
		    }while(skipped % frameSize>0);
		}
		return skipped;
	}
	
	public int available() throws IOException{
	    int avail=is.available();
		if (isPos<fromByte){
		    if (avail>fromByte-isPos)avail=(int)(fromByte-isPos);
		}else if(isPos>=fromByte){
		    if (isPos<toByte){
		        avail-=toByte-isPos;
		        if (avail<0)avail=0;
		    }
		}
		return avail;
	}
}
