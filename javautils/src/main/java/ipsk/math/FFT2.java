//    IPS Java Utils
// 	  (c) Copyright 2013
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

package ipsk.math;

import java.util.Arrays;

/**
 * @author root
 * 
 */
public class FFT2 implements DFTAlgorithm {

	private int n;
	private int m;

	private double[] cosLookup;
	private double[] sinLookup;

	public FFT2(int n) {
		this.n = n;
		this.m = (int) (Math.log(n) / Math.log(2));

		if (n != (1 << m))
			throw new RuntimeException("length N must be power of 2");

		// lookup tables
		cosLookup = new double[n / 2];
		sinLookup = new double[n / 2];

		for (int i = 0; i < n / 2; i++) {
			double arc=(-2 * Math.PI * i) / n;
			cosLookup[i] = Math.cos(arc);
			sinLookup[i] = Math.sin(arc);
		}
	}

	public Complex[] process(double[] srcBuf) {
		double[] x = Arrays.copyOf(srcBuf, srcBuf.length);
		double[] y = new double[srcBuf.length];
		Arrays.fill(y, 0.0);
		fftCooleyTurkey(x, y);
		Complex[] rc = new Complex[x.length];
		for (int i = 0; i < x.length; i++) {
			rc[i] = new Complex(x[i], y[i]);
		}
		return rc;
	}

	public void fftCooleyTurkey(double[] real, double[] img) {
		int i;
		int j=0;
		int k;
		int n1;
		int n2=n/2;
		int a;
		double c;
		double s;
		double t1;
		double t2;

		for (i = 1; i < n - 1; i++) {
			n1 = n2;
			while (j >= n1) {
				j = j - n1;
				n1 = n1 / 2;
			}
			j = j + n1;

			if (i < j) {
				t1 = real[i];
				real[i] = real[j];
				real[j] = t1;
				t1 = img[i];
				img[i] = img[j];
				img[j] = t1;
			}
		}

		n1 = 0;
		n2 = 1;
		for (i = 0; i < m; i++) {
			n1 = n2;
			n2 = n2 + n2;
			a = 0;
			for (j = 0; j < n1; j++) {
				c = cosLookup[a];
				s = sinLookup[a];
				a += 1 << (m - i - 1);

				for (k = j; k < n; k = k + n2) {
					t1 = c * real[k + n1] - s * img[k + n1];
					t2 = s * real[k + n1] + c * img[k + n1];
					real[k + n1] = real[k] - t1;
					img[k + n1] = img[k] - t2;
					real[k] = real[k] + t1;
					img[k] = img[k] + t2;
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.math.DFTAlgorithm#process(ipsk.math.Complex[])
	 */
	@Override
	public Complex[] process(Complex[] t) {
	   double[] reals=new double[n];
	   double[] imgs=new double[n];
	   Complex[] trans=new Complex[n];
	   for(int i=0;i<n;i++){
	       reals[i]=t[i].real;
	       imgs[i]=t[i].img;
	   }
	   fftCooleyTurkey(reals, imgs);
	   for(int i=0;i<n;i++){
           trans[i]=new Complex(reals[i], imgs[i]);
       }
	   return trans;
	   
		    
	}

}
