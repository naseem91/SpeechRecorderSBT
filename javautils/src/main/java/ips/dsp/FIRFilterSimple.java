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

import java.io.IOException;
import java.nio.DoubleBuffer;

import ipsk.io.DoubleRingBuffer;
import ipsk.io.FloatStream;

/**
 * @author klausj
 *
 */
public class FIRFilterSimple implements FloatStream{

    private FloatStream srcStream;
    private double[] coeff;
    private double[] inBuf;
    private int filled=0;
  
    public FIRFilterSimple(FloatStream srcStream,double[] coeff){
        super();
        this.srcStream=srcStream;
        this.coeff=coeff;
      inBuf=new double[coeff.length];
 
    }
    
    
//    private int getBufferOffset(long pos){
//        return (int)(pos % inBuf.length);
//    }
//    
    public int read(double[] buf, int offset,int len) throws IOException{

       
       
        int inRead=srcStream.read(inBuf,filled,coeff.length-filled);
        
        if(inRead<0){
            return inRead;
        }else{
            filled+=inRead;
        }


        if(filled<coeff.length){
            return 0;
        }
        int avail=1;

        
        
        buf[offset]=calcValue();
        
        // shift
        filled--;
        for(int i=0;i<filled;i++){
            inBuf[i]=inBuf[i+1];
        }
        
        return 1;
    }
    
    private double calcValue(){
        double value=0;

        for(int i=0;i<coeff.length;i++){
//          bufOff=getBufferOffset(pos+i);
            
      
            value+=inBuf[i]*coeff[i];
        }
        System.out.println(value);
        return value;
    }


    /* (non-Javadoc)
     * @see ipsk.io.FloatStream#skip(long)
     */
    public long skip(long skip) {
        long skipped=0;
        // TODO
//      if(inBufAvail>0){
//          if(skip>=inBufAvail){
//              skipped+=inBufAvail;
//              inBufAvail=0;
//          }else{
//              skipped+=inBufAvail;
//              inBufAvail-=skip;
//          }
//          pos+=skipped;
//      }else{
//          skipped= srcStream.skip(skip);
//      }
      return skipped;
    }


    /* (non-Javadoc)
     * @see ipsk.io.FloatStream#close()
     */
    public void close() throws IOException {
        srcStream.close();
    }
    
    
}
