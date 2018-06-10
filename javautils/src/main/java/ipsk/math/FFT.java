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

package ipsk.math;

import ipsk.math.Complex;

/**
 * Discrete Fast Fourier Transformation (FFT) implementation.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class FFT implements DFTAlgorithm {
	
	
	/**
	 * Process complex array.
	 */
	public Complex[] process(Complex[] srcBuf) {

		// implementation based on FFT Pseudo code from Wikipedia
		int sizeN = srcBuf.length;

		Complex[] dstBuf = new Complex[sizeN];
		if(sizeN==1){
			return srcBuf;
		}else{
		int halfN=sizeN/2;
		Complex[] g=new Complex[halfN];
		Complex[] u=new Complex[halfN];
		for(int i=0;i<halfN;i++){
			g[i]=srcBuf[i*2];
			u[i]=srcBuf[i*2+1];
		}
		Complex[] gBuf=process(g);
		Complex[] uBuf=process(u);
		for(int k=0;k<halfN;k++){
			double arc=(-2*Math.PI*k)/sizeN;
			Complex tmpC=new Complex(Math.cos(arc),Math.sin(arc));
			tmpC=tmpC.mult(uBuf[k]);
			Complex cK=gBuf[k].add(tmpC);
			dstBuf[k]=cK;
			Complex cK2=gBuf[k].sub(tmpC);
			dstBuf[halfN+k]=cK2;
		}
		}
		return dstBuf;
	}
	
	/**
	 * Process double array.
	 */
	public Complex[] process(double[] srcBuf) {

		int sizeN = srcBuf.length;
		Complex[] cSrcBuf=new Complex[sizeN];
		for(int i=0;i<sizeN;i++){
			cSrcBuf[i]=new Complex(srcBuf[i],0);
		}
		return process(cSrcBuf);
	}
	
//	/**
//     * Process float array.
//     */
//    public Complex[] process(float[] srcBuf) {
//        double[] srcDBuf=new double[srcBuf.length];
//        for(int i=0;i<srcBuf.length;i++){
//            srcDBuf[i]=srcBuf[i];
//        }
//        return process(srcDBuf);
//    }

}
