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

/**
 * Complex math type.
 * Implementation with float data type.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class ComplexFloat {

	public float real;
	public float img;

	public ComplexFloat() {
		real = (float) 0.0;
		img = (float) 0.0;
	}

	public ComplexFloat(float real, float imaginary) {
		this.real = real;
		this.img = imaginary;
	}

	public float magnitude() {
		return (float) Math.sqrt((real * real) + (img * img));
	}

	public ComplexFloat add(ComplexFloat addC) {
		return new ComplexFloat(real + addC.real, img + addC.img);
	}

	public ComplexFloat sub(ComplexFloat subC) {
		return new ComplexFloat(real - subC.real, img - subC.img);
	}

	public ComplexFloat mult(ComplexFloat multC) {
		float multR = (real * multC.real) - (img * multC.img);
		float multI = (real * multC.img) + (multC.real * img);
		return new ComplexFloat(multR, multI);
	}

	public ComplexFloat mult(float multF) {
		return new ComplexFloat(real * multF, img * multF);
	}

	public ComplexFloat div(ComplexFloat divisor) {
		float divReal = divisor.real;
		float divImg = divisor.img;
		float div = (divReal * divReal) + (divImg * divImg);
		float divisionReal = ((real * divReal) + (img * divImg)) / div;
		float divisionImg = ((divReal * img) - (real * divImg)) / div;

		return new ComplexFloat(divisionReal, divisionImg);
	}

	public ComplexFloat div(float divisor) {
		float div = divisor * divisor;
		float divsionReal = (real * divisor) / div;
		float divsionImg = (divisor * img) / div;

		return new ComplexFloat(divsionReal, divsionImg);
	}

	public ComplexFloat conjugate() {
		return new ComplexFloat(real, -img);
	}

	public boolean equals(ComplexFloat c) {
		if (c == null)
			return false;
		return (real == c.real && img == c.img);
	}
	
	public String toString(){
		return "Real: "+real+", Img: "+img;
	}

}
