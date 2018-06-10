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
 * Inserts another input stream in this stream.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class InsertInputStream extends FramedEditingInputStream {

    
    private byte[] frameBuf;
   
    
    private long bytePos;
    private InputStream insert;
    
    private long byteAt;
    private boolean insertEnd;
    private static int DEF_PREFERRED_BUF_SIZE = 2048;
    private int bufSize;
   // private byte[] buf;
    private byte[] skipBuf;
 
    private int pushBackLen;

    /**
     *  Insert stream insert to on stream is at the given position using framesize 1.
     *  
     */
    public InsertInputStream(InputStream is, InputStream insert, long at) {
        this(is, insert, 1, at);
    }

    /**
     * Creates inserting InputStream. At the given position the stream is
     * inserted. First the bytes from the source stream are read until the given
     * insert position is reached. Next the insert stream is read until its end.
     * At least the rest of the source stream is read.
     * 
     * @param is
     *            the underlying (source) InputStream
     * @param insert
     *            the inserted input stream
     * @param frameSize
     *            size of the data frames
     * @param at
     *            position where the insert stream is inserted in the source
     *            stream
     */
    public InsertInputStream(InputStream is, InputStream insert, int frameSize,
            long at) {
        super(is,frameSize);
       
        this.insert = insert;
       

    
        bytePos = 0;
        byteAt = at * frameSize;
        frameBuf = new byte[frameSize];
        
        int bufSizeFrames = DEF_PREFERRED_BUF_SIZE / frameSize;
        bufSize = bufSizeFrames * frameSize;
        if (bufSize < frameSize)
            bufSize = frameSize;
       // buf = new byte[bufSize];
        skipBuf=new byte[frameSize];
        insertEnd = false;
        pushBackLen = 0;
    }

   
    public int read() throws IOException {
        if (frameSize != 1)
            throw new IOException(
                    "read() method is only allowed if frame size equals 1");
        byte[] buf = new byte[1];
        int read = read(buf, 0, 1);
        if (read == -1)
            return read;
        return 0xFF & buf[0];
    }

    public int read(byte[] buf, int offset, int len) throws IOException {
        if (len % frameSize > 0)
            throw new IOException(
                    "bytes to read must be multiple of frame size");
        long toRead = len;
        int copied = 0;
        if (pushBackLen > 0) {
            System.arraycopy(frameBuf, 0, buf, offset, pushBackLen);
            offset += pushBackLen;
            copied += pushBackLen;
            bytePos+=pushBackLen;
            len -= pushBackLen;
            toRead-=pushBackLen;
            pushBackLen=0;
        }

        int read = 0;
        if (bytePos < byteAt || insertEnd) {
            // read the data
           
            if (!insertEnd && toRead > byteAt - bytePos)
                toRead = byteAt - bytePos;

            read = is.read(buf, offset, (int) toRead);
            if (read == -1) {
                if (copied > 0)
                    throw new IOException(
                            "stream length is not mutiple of frame size");
                return -1;
            }

            copied += read;

        } else {
            // read from insert stream
            read = insert.read(buf, offset, (int)toRead);
            if (read == -1) {
            	if (copied > 0)
                    throw new IOException(
                            "stream length is not mutiple of frame size");
            	insertEnd = true;
            	insert.close();
                read = 0;
            }

            copied += read;
        }
        pushBackLen = copied % frameSize;
        if (pushBackLen > 0) {
            System.arraycopy(buf, read - pushBackLen, frameBuf, 0, pushBackLen);
            copied-=pushBackLen;
        }
        bytePos+=copied;
       // assert copied %frameSize > 0;
        
        // TODO AudioSystem.write does not "like" a zero read. File this as a bug.
        if (copied==0)return read(buf,offset,len);
        return copied;
    }

    public long skip(long n) throws IOException {
        if (n % frameSize > 0 || n < 0)
            throw new IOException(
                    "bytes to skip must be multiple of frame size");
        long toSkip = n;
        long totalSkipped = 0;
        if (toSkip == 0)
            return 0;
        toSkip -= pushBackLen;
        totalSkipped += pushBackLen;
        bytePos+=pushBackLen;
        pushBackLen = 0;
        if (bytePos < byteAt || insertEnd) {
            if (!insertEnd && toSkip > byteAt - bytePos)
                toSkip = byteAt - bytePos;
            do {
                long skipped = is.skip(toSkip);
                toSkip -= skipped;
                totalSkipped += skipped;
                bytePos += skipped;
            } while (bytePos % frameSize > 0);
            return totalSkipped;
        } else {
            do {
                long skipped = insert.skip(toSkip);
                toSkip -= skipped;
                totalSkipped += skipped;
                bytePos += skipped;
                
            } while (bytePos % frameSize > 0);
            if (totalSkipped == 0) {
                // this maybe the end of the insert stream

                int read = insert.read(skipBuf);
               
                if (read == -1) {
                    insertEnd = true;
                    read=0;
                }
                pushBackLen=read %frameSize;
                if (pushBackLen > 0) {
                    System.arraycopy(skipBuf,0, frameBuf, 0, pushBackLen);
                }else{
                    totalSkipped+=read;
                    bytePos+=read;
                }
            }
        }
        	
        return totalSkipped;
    }
    public int available() throws IOException{
    	int available=0;
    	if (insertEnd){
    		available= is.available();
    	}else{
    	 if (bytePos < byteAt) {
    	 	long maxAvailable=byteAt-bytePos;
    	 	long isAvailable=is.available();
    	 	if (maxAvailable>isAvailable){
    	 		available=(int)isAvailable;
    	 	}else{
    	 		available=(int)maxAvailable;
    	 	}
    	 }else{
    	 	available=insert.available();
    	 }
    	}
    	return available+pushBackLen;
    }
    
    
    public void close() throws IOException{
        insert.close();
        super.close();
    }
    
  
}
