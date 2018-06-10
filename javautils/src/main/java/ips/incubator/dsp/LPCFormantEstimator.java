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

import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.complex.Complex;



/**
 * @author klausj
 *
 */
public class LPCFormantEstimator implements FormantEstimator {

	private LaguerreSolver solver;
	private ZPlane zPlane;
	private int estimatedNrOfFormants;
	/**
	 * 
	 */
	public LPCFormantEstimator(double sampleRate) {
		super();
		solver=new LaguerreSolver();
		zPlane=new ZPlane(sampleRate);
		estimatedNrOfFormants=estimateNrOfFormants(sampleRate);
	}
	
	public int estimateNrOfFormants(double sampleRate){
		// use rule of thumb for number of filter coeffs/formants
		return(int)Math.floor(2+(sampleRate/1000.0));
	}

	/* (non-Javadoc)
	 * @see ips.incubator.dsp.FormantEstimator#estimate(double[])
	 */
	@Override
	public Complex[] estimatePoles(double[] x,int nrPoles) {
		double[] lpcFilterCoeffs=LPC.process(x, nrPoles);
		// TODO Coeff and roots are in reverse oder compared to Matlab !!!
		Complex[] roots=solver.solveAllComplex(lpcFilterCoeffs, 0.0);
		return roots;
	}
	
	/* (non-Javadoc)
	 * @see ips.incubator.dsp.FormantEstimator#estimate(double[])
	 */
	@Override
	public Complex[] estimatePoles(double[] x,double sampleRate) {
		return estimatePoles(x,estimateNrOfFormants(sampleRate));
	}

	/* (non-Javadoc)
	 * @see ips.incubator.dsp.FormantEstimator#estimateFormantFrequencies(double[], int)
	 */
	@Override
	public double[] estimateFormantFrequencies(double[] x, int nrFormants) {
		Complex[] poles=estimatePoles(x,nrFormants);
		// TODO filter poles in the bottom  half of Z-plane
		double[] freqs=new double[poles.length];
		for(int i=0;i<poles.length;i++){
			freqs[i]=zPlane.frequency(poles[i]);
		}
		return freqs;
	}

	/* (non-Javadoc)
	 * @see ips.incubator.dsp.FormantEstimator#estimateFormantFrequencies(double[], double)
	 */
	@Override
	public double[] estimateFormantFrequencies(double[] x, double sampleRate) {
		return estimateFormantFrequencies(x,estimateNrOfFormants(sampleRate));
	}
	
	private double toFrequency(org.apache.commons.math3.complex.Complex c,double sampleRate) {
		double f=Math.atan2(c.getImaginary(),c.getReal())*sampleRate/(2*Math.PI);
		return f;
	}
	

	
	
	
	

}
