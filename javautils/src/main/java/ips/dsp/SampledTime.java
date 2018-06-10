//    IPS Java Audio Tools
// 	  (c) Copyright 2015
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

package ips.dsp;

/**
 * @author klausj
 *
 */
public class SampledTime {

	private double samplerate;
	private long samplePosition;
	
	/**
	 * @param samplerate
	 * @param framePosition
	 */
	public SampledTime(double samplerate, long framePosition) {
		super();
		this.samplerate = samplerate;
		this.samplePosition = framePosition;
	}

	/**
	 * @return the samplePosition
	 */
	public long getSamplePosition() {
		return samplePosition;
	}

	/**
	 * @param samplePosition the samplePosition to set
	 */
	public void setSamplePosition(long samplePosition) {
		this.samplePosition = samplePosition;
	}

	/**
	 * @return the samplerate
	 */
	public double getSamplerate() {
		return samplerate;
	}

	
	public double time(){
		return (double)samplePosition/(double)samplerate;
	}

}
