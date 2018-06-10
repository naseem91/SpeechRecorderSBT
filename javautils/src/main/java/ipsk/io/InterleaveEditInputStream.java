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
 * Edits channels (interleaved data) from underlying stream.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class InterleaveEditInputStream extends FramedEditingInputStream{

    
    private int frameOffset;
    private int frameRead;
    //private int frameSkip;
    //private int bufSize;
    private byte[] isBuf;
    
    
    /**
     * Edits bytes from frameOffset to frameOffset+frameRead from each frame with given framesize.
     */
    public InterleaveEditInputStream(InputStream is,int frameSize,int frameOffset,int frameRead) {
        super(is,frameSize);
        this.frameOffset=frameOffset;
        this.frameRead=frameRead;
        

        isBuf=new byte[0];
    }

    /* (non-Javadoc)
     * @see ipsk.io.FramedInputStream#read(byte[], int, int)
     */
    public int read(byte[] buf, int offset, int len) throws IOException {
        if (len % frameRead > 0)
			throw frameSizeException;
        int framesToRead=len/frameRead;
        int bytesToRead=framesToRead * frameSize;
        if (isBuf.length < bytesToRead){
            isBuf=new byte[bytesToRead];
        }
        int read=is.read(isBuf,0,bytesToRead);
        if (read==-1) return read;
        if ((read % frameSize)>0) throw frameSizeException;
        int framesRead=read /frameSize;
       
        for (int i=0;i<framesRead;i++){
        	System.arraycopy(isBuf,(i*frameSize)+frameOffset , buf, i*frameRead+offset, frameRead);
//            for(int j=0;j<frameRead;j++){
//                buf[i*frameRead+offset+j]=isBuf[(i*frameSize)+frameOffset+j];
//         
//            }
        }
        return framesRead * frameRead;
    }
    
    
    public long skip(long n) throws IOException{
        if (n % frameRead > 0)
			throw frameSizeException;
        long framesToSkip=n/frameRead;
        long toSkip=framesToSkip * frameSize;
        long skipped=is.skip(toSkip);
        if (skipped % frameSize >0)throw frameSizeException;
        long ret= (skipped /frameSize) * frameRead;
        return ret;
    }

}
