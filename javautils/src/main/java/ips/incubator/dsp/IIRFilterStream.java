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

package ips.incubator.dsp;

import ipsk.io.FloatStream;

import java.io.IOException;

import ips.dsp.IIRFilter;

/**
 * TODO Not tested 
 * @author klausj
 *
 */
public class IIRFilterStream extends IIRFilter implements FloatStream{

    private FloatStream srcStream;
    private double[] buf; 
    private int delayLen;
    
    public IIRFilterStream(FloatStream srcStream,double[] aCoeff,double[] bCoeff){
        super(aCoeff,bCoeff);
        this.srcStream=srcStream; 
        delayLen=aCoeff.length;
        if(bCoeff.length>delayLen){
        	delayLen=bCoeff.length;
        }
        buf=new double[delayLen];
      
    }
    

    public int read(double[] buf, int offset,int len) throws IOException{
    	if(this.buf.length<len){
    		this.buf=new double[len];
    	}
    	int r=srcStream.read(this.buf,0,len);
    	if(r==-1){
    		return -1;
    	}
    	for(int i=0;i<r;i++){
    		buf[offset+i]=step(this.buf[i]);
    	}
        return r;	
    }
    

    /* (non-Javadoc)
     * @see ipsk.io.FloatStream#skip(long)
     */
    public long skip(long skip) throws IOException {
    	long skipped=0;
    	// try to skip as much as possible
    	// but make sure the delay buffers are filled properly
    	long saveSkipable=skip-delayLen;

    	if(saveSkipable>0){
    		
    		return srcStream.skip(saveSkipable);
    	}else{

    		// read to fill delay buffers
    		int toRead=(int)skip;
    		int r=srcStream.read(this.buf,0,toRead);
    		if(r==-1){
    			return 0;
    		}else{
    			skipped=r;
    		}
    		// fill delay buffers
    		for(int i=0;i<r;i++){
    			step(this.buf[i]);
    		}
    	}
    	return skipped;
    }


    /* (non-Javadoc)
     * @see ipsk.io.FloatStream#close()
     */
    public void close() throws IOException {
        srcStream.close();
    }
    
    
}
