//    IPS Java Utils
// 	  (c) Copyright 2016
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

import org.junit.Test;

/**
 * @author klausj
 *
 */
public class TestIIRFilter {

	// Simple test case verified using Mathworks Matlab filter function
	public static double[] bCoeffs = new double[]{0.994975383507587,-1.98995076701517, 0.994975383507587};
	public static double[] aCoeffs = new double[]{1, -1.98992552008493, 0.989976013945421};
	public static double[] testX=new double[] {0.2669,0.1695,0.1554,0.4680,0.4190, 0.3466, 0.9530,0.9225};
	public static double[] res=new double[] {0.2656,0.1660,0.1502,0.4597,0.4064,0.3302,0.9301,0.8903};

//	>> filter(rlbBcoeffs,rlbAcoeffs,x)
//
//	ans =
//
//	    0.2656    0.1660    0.1502    0.4597    0.4064    0.3302    0.9301    0.8903

	
	@Test
	public void test() {
		IIRFilter f=new IIRFilter(bCoeffs, aCoeffs);
		for(int i=0;i<testX.length;i++){
			double x=testX[i];
			double y=f.step(x);
			double diff=y-res[i];
			assert(diff<0.0001);
		}
	}

}
