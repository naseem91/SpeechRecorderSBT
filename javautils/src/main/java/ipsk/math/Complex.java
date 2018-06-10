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
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class Complex {

	public double real;
	public double img;

	public Complex() {
		real =  0.0;
		img =  0.0;
	}

	public Complex(double real, double imaginary) {
		this.real = real;
		this.img = imaginary;
	}
	
	public static Complex fromPolarForm(double magnitude,double argument){
	    double r=Math.cos(argument)*magnitude;
	    double i=Math.sin(argument)*magnitude;
	    return new Complex(r,i);
	}

	public double magnitude() {
		return Math.sqrt((real * real) + (img * img));
	}
	
	public double argument(){
	    return Math.atan2(img,real);
	}

	public Complex add(Complex addC) {
		return new Complex(real + addC.real, img + addC.img);
	}

	public Complex sub(Complex subC) {
		return new Complex(real - subC.real, img - subC.img);
	}

	public Complex mult(Complex multC) {
		double multR = (real * multC.real) - (img * multC.img);
		double multI = (real * multC.img) + (multC.real * img);
		return new Complex(multR, multI);
	}

	public Complex mult(double multF) {
		return new Complex(real * multF, img * multF);
	}

	public Complex div(Complex divisor) {
		double divReal = divisor.real;
		double divImg = divisor.img;
		double div = (divReal * divReal) + (divImg * divImg);
		double divisionReal = ((real * divReal) + (img * divImg)) / div;
		double divisionImg = ((divReal * img) - (real * divImg)) / div;

		return new Complex(divisionReal, divisionImg);
	}

	public Complex div(double divisor) {
		double div = divisor * divisor;
		double divsionReal = (real * divisor) / div;
		double divsionImg = (divisor * img) / div;

		return new Complex(divsionReal, divsionImg);
	}

	public Complex conjugate() {
		return new Complex(real, -img);
	}

	public boolean equals(Complex c) {
		if (c == null)
			return false;
		return (real == c.real && img == c.img);
	}

	public String toString(){
		return "Real: "+real+", Img: "+img;
	}

}
