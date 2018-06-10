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

package ipsk.text;

import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Text format for natural numbers. 
 * @author klausj
 *
 */
public class NaturalNumberFormat extends NumberFormat {

	private int digitCount;
	private long MIN_VALUE=0;
	private long maxValue=0;
	
	
	/**
	 * Create natural number format.
	 * Similar to class {@link  java.text.DecimalFormat DecimalFormat} but accepts only natural numbers.
	 * The parser is strict and accepts only strings containing only number characters and the same length as the specified number of digits.
	 * Examples:
	 *    nnFmt=NaturalNumberFormat(4)
	 *    nnFmt.format(47); => "0047"
	 *    nnFmt.parse("0048"); => 48
	 *    nnFmt.parse("00048"); throws {@link java.text.ParseException ParserException} !
	 * @param digitCount number of digits
	 */
	public NaturalNumberFormat(int digitCount){
		super();
		this.digitCount=digitCount;
		if(digitCount<0){
			throw new IllegalArgumentException("Digit count must be greater than 0");
		}
		// TODO
		// calculate once and store as constant!
		BigInteger maxValueBi=BigInteger.TEN.pow(digitCount);
		BigInteger maxLongValue=BigInteger.valueOf(Long.MAX_VALUE);
		int cmp=maxValueBi.compareTo(maxLongValue);
		if(cmp>0){
			throw new IllegalArgumentException("Digit count exponent exceeds max value of native type long");
		}
		maxValue=maxValueBi.longValue()-1;
	}
	
	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(double, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo,
			FieldPosition pos) {
		// check if this is an integer
		boolean infiniteOrNaN=Double.isInfinite(number) || Double.isNaN(number);
		if(!infiniteOrNaN){
			double flooredNumber=Math.floor(number);
			if ((flooredNumber==number)) {
				long longNumber=(long)flooredNumber;
				return(format(longNumber,toAppendTo,pos));
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#format(long, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo,
			FieldPosition pos) {
		if(number>=MIN_VALUE && number<=maxValue){
			// OK use number format 
			String longStr=Long.toString(number);
			int leadingZeroCnt=digitCount-longStr.length();
			for(int i=0;i<leadingZeroCnt;i++){
				toAppendTo.append('0');
			}
			toAppendTo.append(longStr);
		}
		return toAppendTo;
	}

	/* (non-Javadoc)
	 * @see java.text.NumberFormat#parse(java.lang.String, java.text.ParsePosition)
	 */
	@Override
	public Number parse(String source, ParsePosition parsePosition) {
		Number val=null;
		int pp=parsePosition.getIndex();
		int sourceLength=source.length();
		if(sourceLength==digitCount){
			// length OK
			int exponent=1;
			long lVal=0;
			for(int i=pp+sourceLength-1;i>=pp;i--){
				
				char ch=source.charAt(i);
				if(!Character.isDigit(ch)){
					parsePosition.setIndex(pp+i);
					parsePosition.setErrorIndex(pp+i);
					return null;
				}
				int digit=Character.digit(ch, 10);
				lVal+=digit*exponent;
				exponent*=10;
			}
			// success
			parsePosition.setIndex(pp+digitCount);
			val=lVal;
			
		}else{
			// set error index
			int relErrIdx=sourceLength;
			if(relErrIdx>digitCount){
				relErrIdx=digitCount;
			}
			parsePosition.setErrorIndex(parsePosition.getIndex()+relErrIdx);
		}
		return val;
	}

}
