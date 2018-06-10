//    IPS Java Utils
// 	  (c) Copyright 2011
// 	  Institute of Phonetics and Speech Processing,
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

package ips.dsp;

import ipsk.io.DoubleRingBuffer;
import ipsk.io.FloatStream;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author klausj
 *
 */
public class FIRFilter implements FloatStream{

    private FloatStream srcStream;
    private double[] coeff;
//    private double[] inBuf;
    private DoubleRingBuffer inBuf;
    
    private int zeroPadding=0;
    private int tailZeroPad=0;
    private boolean inStreamEof=false;
    private boolean inEof=false;
    
    private long position=0;
  
    
    public FIRFilter(FloatStream srcStream,double[] coeff){
        super();
        this.srcStream=srcStream;
        this.coeff=coeff;
//        inBuf=new double[coeff.length*2];
        inBuf=new DoubleRingBuffer(coeff.length*2);
        zeroPadding=coeff.length/2-1;
       Arrays.fill(inBuf.getBuffer(), 0, zeroPadding, 0.0);
       inBuf.written(zeroPadding);
      tailZeroPad=zeroPadding;
    }
    
    
//    private int getBufferOffset(long pos){
//        return (int)(pos % inBuf.length);
//    }
//    
    
    private void preloadData() throws IOException{
        int wPos=inBuf.bufferWritePosition();
        int cAvailWrite=inBuf.continuosAvailableToWrite();
        int inRead=0;
        if(!inStreamEof){
            inRead=srcStream.read(inBuf.getBuffer(),wPos,cAvailWrite);
            if(inRead<0){
                inStreamEof=true;
            }
        }
        if(inStreamEof){
           
            if(tailZeroPad>0){
                int zeroPad=tailZeroPad;
                if(zeroPad>cAvailWrite){
                    zeroPad=cAvailWrite;
                }
                Arrays.fill(inBuf.getBuffer(), wPos, wPos+zeroPad, 0.0);
                tailZeroPad-=zeroPad;
                inRead=zeroPad;
            }else{
                inEof=true;
            }
            
        }
        inBuf.written(inRead);
    }
    
    public int read(double[] buf, int offset,int len) throws IOException{

        
        preloadData();
        
//        inBufAvail+=inRead;

        if(inBuf.filled()<coeff.length){
            if(inEof){
                return -1;
            }else{
                return 0;
            }
        }
        int avail=inBuf.filled()-coeff.length+1;

        if(len>avail){
            len=avail;
        }
        for (int tc=0;tc<len;tc++){
            buf[offset+tc]=calcValue();
            inBuf.read(1);
        }
//        inBufAvail-=len;
//        filled=len;
        
        position+=len;
        System.out.println("Position: "+position);
        return len;
    }
    
    private double calcValue(){
        double value=0;
//        int bufOff;
       long readPos=inBuf.getReadPosition();
        for(int i=0;i<coeff.length;i++){
//          bufOff=getBufferOffset(pos+i);
            
            Double bufVal=inBuf.valueAtPosition(readPos+i);

            value+=bufVal*coeff[i];
        }
//        System.out.println(value);
        return value;
    }


    /* (non-Javadoc)
     * @see ipsk.io.FloatStream#skip(long)
     */
    public long skip(long skip) {
        long skipped=0;
//        if(skip > coeff.length){
//            // OK we can drop buffered data
//            long rPos=inBuf.getReadPosition();
//            long wPos=inBuf.getWritePosition();
//            inBuf.skip(skip);
//            
//        }else{
//            
//        }
      return skipped;
    }


    /* (non-Javadoc)
     * @see ipsk.io.FloatStream#close()
     */
    public void close() throws IOException {
        srcStream.close();
    }
    
    
}
