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

package ips.incubator.dsp;

import org.junit.Test;

/**
 * @author klausj
 *
 */
public class TestLPC {

	// Simple test case verified using Mathworks Matlab filter function
	
	public static double[] testX=new double[] {0.2669,0.1695,0.1554,0.4680,0.4190, 0.3466, 0.9530,0.9225};
	
	
	@Test
	public void test() {
	
		double[] coeffs=LPC.process(testX,3);
		for(double coeff:coeffs){
			System.out.print(coeff+" ");
		}
		System.out.println();
	}

}
