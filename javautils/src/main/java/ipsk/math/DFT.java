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
 * Discrete Fourier Transformation (DFT) implementation.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class DFT implements DFTAlgorithm {
	
	
	/**
	 * Process complex array.
	 */
	public Complex[] process(Complex[] srcBuf) {

		int sizeN = srcBuf.length;

		Complex[] dstBuf = new Complex[sizeN];
		for (int k = 0; k < sizeN; k++) {

			Complex cx = new Complex();
			double argTmp = - Math.PI * 2 * k;
			for (int j = 0; j < sizeN; j++) {
				Complex aj = srcBuf[j];
				double arg = (argTmp * j) / sizeN;
				Complex ec=new Complex(Math.cos(arg),Math.sin(arg));
				cx=cx.add(ec.mult(aj));
			}
			dstBuf[k] = cx;
		}
		return dstBuf;
	}
	
	/**
     * Process inverse DFT complex array.
     */
    public static Complex[] processInvers(Complex[] srcBuf) {

        int sizeN = srcBuf.length;

        Complex[] dstBuf = new Complex[sizeN];
       
        for (int k = 0; k < sizeN; k++) {
           
            Complex cx = new Complex();
            for (int j = 0; j < sizeN; j++) { 
                double arg = (Math.PI * 2 *k*j) /sizeN;
                Complex aj = srcBuf[j];
                Complex ec=new Complex(Math.cos(arg),Math.sin(arg));
                cx=cx.add(ec.mult(aj));
            }
            cx=cx.mult(1/(double)sizeN);
            dstBuf[k] = cx;
        }
        return dstBuf;
    }
	
	/**
	 * Process double array.
	 */
	public Complex[] process(double[] srcBuf) {

		int sizeN = srcBuf.length;

		Complex[] dstBuf = new Complex[sizeN];
		for (int k = 0; k < sizeN; k++) {

			Complex cx = new Complex();

			double tmpR = 0.0;
			double tmpI = 0.0;
			double argTmp = - Math.PI * 2 * k;
			for (int j = 0; j < sizeN; j++) {
				double aj = srcBuf[j];
				double arg = (argTmp * j) / sizeN;
				tmpR = tmpR + aj * Math.cos(arg);
				tmpI = tmpI - aj * Math.sin(arg);
			}
			cx.real =  tmpR;
			cx.img = tmpI;
			dstBuf[k] = cx;
		}
		return dstBuf;
	}
	
	/**
     * Process float array.
     */
    public Complex[] process(float[] srcBuf) {
        double[] srcDBuf=new double[srcBuf.length];
        for(int i=0;i<srcBuf.length;i++){
            srcDBuf[i]=srcBuf[i];
        }
        return process(srcDBuf);
    }

}
