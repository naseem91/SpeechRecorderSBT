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

/**
 * @author klausj
 *
 */
public class LPC {


	public static double autocorr(double[] sig, int N, int i) {
		double v = 0.0;
		for (int n = 0; n < N; n++) {
			v += sig[n] * sig[n + i];
		}
		return v;
	}

	public static double[] process(double[] x) {
		return process(x,x.length-1);
	}
	public static double[] process(double[] x, int n) {

		// size
		int N = x.length -1;
		
		// Initialize R with autocorrelation coefficients
		double[] r = new double[n+1];
		for (int i = 0; i<n+1; i++) {
			for (int j=0;j<= N-i; j++) {
				r[i] += x[j] * x[j+i];
			}
		}

		double[] ak = new double[n+1];
		ak[0] = 1.0;

		double ek = r[0];

		// Levinson Durbin
		for (int k = 0; k < n; k++) {
			
			double lambda = 0.0;
			for (int j=0;j<=k;j++) {
				lambda -= ak[j] * r[k+1-j];
			}

			lambda /= ek;

			for (int i=0; i<=(k + 1)/2;i++) {
				double temp = ak[k+1-i]+lambda*ak[i];
				ak[i] = ak[i] + lambda * ak[k+1-i];
				ak[k+1-i] = temp;
			}

			ek *= 1.0 - lambda*lambda;
		}

		return ak;
	}

}
