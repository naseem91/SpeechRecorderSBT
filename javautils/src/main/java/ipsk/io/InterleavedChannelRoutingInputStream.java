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
 * The stream must have an interleaved channel format, for example frameSize=8 bytes, channels=4 (samplesize=2 bytes):
 * 
 * fr0ch0sb0,fr0ch0sb1,fr0ch1sb0,fr0ch1sb1,fr0ch2sb0,fr0ch2sb1,fr0ch3sb0,fr0ch3sb1,fr1ch0sb0,fr1ch0sb1,fr1ch1sb0,fr1ch1sb1,fr1ch2sb0,fr1ch2sb1,fr1ch3sb0,fr1ch3sb1
 * 
 * fr: frame
 * ch: channel
 * sb: byte of sample
 * 
 * A assignment array of {1,3} edits the stream to:
 * 
 * fr0ch1sb0,fr0ch1sb1,fr0ch3sb0,fr0ch3sb1,fr1ch1sb0,fr1ch1sb1,fr1ch3sb0,fr1ch3sb1
 * 
 * The resulting stream has two channels.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class InterleavedChannelRoutingInputStream extends FramedEditingInputStream{

	public static final boolean DEBUG=false;
    
//	private int inChannelCount;
	private Integer[] assignment;
    private volatile byte[] isBuf;
    private int sampleSize;
    private int frameRead=0;
    
    /**
     * Edits channels
     */
    public InterleavedChannelRoutingInputStream(InputStream is,int sampleSize,int inputChannelCount,Integer[] assignment) {
        super(is,sampleSize*inputChannelCount);
        this.sampleSize=sampleSize;
//        this.inChannelCount=inputChannelCount;
//        this.frameSize=inChannelCount*sampleSize;
       
        if(inputChannelCount<0){
        	throw new IllegalArgumentException("Negative channel count for input stream: "+inputChannelCount);
        }
        this.assignment=assignment;
        
//        for(Integer pc:assignment){
//        	if(pc!=null && pc>=inputChannelCount){
//        		throw new IllegalArgumentException("Cannot pick channel "+pc+", input stream channel count is "+inputChannelCount);
//        	}
//        }
        frameRead=assignment.length*sampleSize;
        // SunFileWriter.java uses fixed buffer size of 4096
        isBuf=new byte[4096];
        if(DEBUG){
        	System.out.println(getClass().getName()+": sample size: "+sampleSize+" input channel count: "+inputChannelCount);
        }
    }
    
    

    /* (non-Javadoc)
     * @see ipsk.io.FramedInputStream#read(byte[], int, int)
     */
    public int read(byte[] buf, int offset, int len) throws IOException {
        if (len % frameRead > 0)
			throw frameSizeException;
        
        int framesToRead=len/frameRead;
        int bytesToRead=framesToRead * frameSize;
        if(bytesToRead>isBuf.length){
        	framesToRead=isBuf.length/frameSize;
        	bytesToRead=framesToRead * frameSize;
        }
        /*
        if (isBuf.length < bytesToRead){
            isBuf=new byte[bytesToRead*2];
            // If the buffer is increased to bytesRead the call to line.read() crashes !! (native code of line!!)
            // what's wrong with it?
            // Bug in JRE?
            System.out.println("Increased buffer: "+isBuf.length);
            System.out.flush();
        }
        */
        int read=is.read(isBuf,0,bytesToRead);
        if (read<=0) return read;
        
        if ((read % frameSize)>0) throw frameSizeException;
        int framesRead=read /frameSize;
       
        
        for (int i=0;i<framesRead;i++){
        	int trgFPos=offset+i*frameRead;
        	int srcFPos=i*frameSize;
        	for(int ch=0;ch<assignment.length;ch++){
        		Integer pc=assignment[ch];
        		for(int sb=0;sb<sampleSize;sb++){
        			int trgBufPos=trgFPos+(ch*sampleSize)+sb;
        			if(pc==null){
//        				System.out.println("set zero trg: "+trgBufPos);
        				buf[trgBufPos]=0;
        			}else{
        				int srcBufPos=srcFPos+(pc*sampleSize)+sb;
        				
//        				System.out.println("Src: "+srcBufPos+ " trg: "+trgBufPos);
        				buf[trgBufPos]=isBuf[srcBufPos];
        			}
        		}
        	}	
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
    
    public int available() throws IOException{
    	int srcAvail=is.available();
    	int avail=srcAvail*frameSize/frameRead;
    	return avail;
    }

}
