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


/**
 * @author klausj
 *
 */
public class IIRFilter{

   
    protected double[] bCoeff;
    protected double[] aCoeff;
    protected double[] bDelayBuf;
    protected double[] aDelayBuf;
    protected double[] delayBuf;
   
    public IIRFilter(double[] bCoeff,double[] aCoeff){
        super();
        this.bCoeff=bCoeff;
        this.aCoeff=aCoeff;
        bDelayBuf=new double[bCoeff.length-1];
        aDelayBuf=new double[aCoeff.length];
       
    }
    
 
    public double step(double in){
    	double sum=bCoeff[0]*in;
    	
    	for(int i=0;i<bDelayBuf.length;i++){
    		sum+=bCoeff[i+1]*bDelayBuf[i];
    		
    	}
    	for(int i=0;i<aDelayBuf.length-1;i++){
    		sum-=aCoeff[i+1]*aDelayBuf[i];
    	}
    	// shift delay buffers
    	// a (out) delay
    	for(int i=bDelayBuf.length-1;i>0;i--){
    		bDelayBuf[i]=bDelayBuf[i-1];
    	}
    	bDelayBuf[0]=in;
    	// b (in)  delay
    	for(int i=aDelayBuf.length-1;i>0;i--){
    		aDelayBuf[i]=aDelayBuf[i-1];
    	}
    	sum=sum/aCoeff[0];
    	aDelayBuf[0]=sum;
    	
    	return sum;
    }
    
}
