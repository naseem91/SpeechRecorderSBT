//    IPS Java Utils
// 	  (c) Copyright 2015
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
public class AutoCorrelator {

	public static class AutoCorrelationResult{
		private double energy;
		private int positionMax;
		private double corrEnergyMax;
		/**
		 * @return the energy
		 */
		public double getEnergy() {
			return energy;
		}
		/**
		 * @return the positionMax
		 */
		public int getPositionMax() {
			return positionMax;
		}
		/**
		 * @return the corrEnergyMax
		 */
		public double getCorrEnergyMax() {
			return corrEnergyMax;
		}
		
		public double correlation(){
			return corrEnergyMax/energy;
		}
		
		/**
		 * @param energy
		 * @param positionMax
		 * @param energyMax
		 */
		public AutoCorrelationResult(double energy, int positionMax,
				double energyMax) {
			super();
			this.energy = energy;
			this.positionMax = positionMax;
			this.corrEnergyMax = energyMax;
		}
		
	}
	public static AutoCorrelationResult autoCorrelate(double[] buf,int offset,int corrLen,int corrStart,int corrEnd){
		int corrIvLen=corrEnd-corrStart;
		
		//		double[] sum=new double[corrIvLen];
		// calc pos zero (energy)
		double sum0=0.0;
		int pos=offset;
		for(int i=0;i<corrLen;i++){
			sum0+=buf[pos]*buf[pos];
			pos++;
		}
		double val;
		double max=0.0;
		int maxPosOff=0;
		// shift from start to end
		for(int p=0;p<corrIvLen;p++){
			val=0.0;
			pos=offset;
			int pos2=offset+corrStart+p;
			for(int i=0;i<corrLen;i++){
				val+=buf[pos]*buf[pos2];
				pos++;
				pos2++;
			}
			if(val>max){
				max=val;
				maxPosOff=p;
			}
//			sum[p]=val;
		}
		
		AutoCorrelationResult rs=new AutoCorrelationResult(sum0/corrLen, maxPosOff, max/corrLen);
		return rs;
	}
	
	public static double[] autoCorrelate(double[] buf){
		int corrLen=buf.length;
		double[] corr=new double[corrLen];
		
		double val;
		
		for(int p=0;p<corrLen;p++){
			val=0.0;
			
			for(int i=0;i<corrLen;i++){
				int pos=p+i;
				if(pos<corrLen) {
				val+=buf[pos]*buf[i];
				}
			}
			corr[p]=val;
		}
		
		return corr;
	}
	
	public static void main(String[] args) {
		double[] fcs=AutoCorrelator.autoCorrelate(new double[] {2,4,0.3});
		for(int i=0;i<fcs.length;i++) {
			System.out.println(fcs[i]);
		}
	}

}
