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



package ipsk.util;

public class UnitConverter {

	public final static double centiMeterToMeter(double cm){
		return cm/100;
	}
	public final static double meterToCentiMeter(double m){
		return m * 100;
	}
	public final static double feetToMeter(double feet){
		return feet * 0.3048;
	}
	public final static double meterToFeet(double meter){
		return meter / 0.3048;
	}
	public final static double inchToMeter(double inch){
		return inch * 0.0254;
	}
	public final static double meterToInch(double meter){
		return meter / 0.0254;
	}
	public final static double poundToKilogramme(double pound) {
		 return pound * 0.45359237;
	}
	public final static double kilogrammeToPound(double kg) {
		 return kg / 0.45359237;
	}
	public final static double stoneToKilogramme(double stone) {
		 return stone * 14  * 0.45359237;
	}
	public final static double kilogrammeToStone(double kg) {
		 return kg / (14 * 0.45359237);
	}
}
