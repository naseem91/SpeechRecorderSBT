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


package ipsk.text;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Formats a string in RFC3339 date representation.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class RFC3339TimeFormat extends Format {

	//	public static final int HOUR_FIELD=1;
	//	public static final int MINUTE_FIELD=2;
	//	public static final int SECOND_FIELD=3;
	//	public static final int MILLISECOND_FIELD=4;

	private double seconds;
	private DecimalFormat twoDigitFormatter = new DecimalFormat("00");
	
	//private DecimalFormat secMilliSecFormatter = new DecimalFormat("00.000");
	private DecimalFormat secMilliSecFormatter;
	/**
	 * Creates a new media time formatter.
	 */
	public RFC3339TimeFormat() {
		super();
		
		// Get number format for US to force formatting the decimal separator as dot (.) character
		NumberFormat nf=NumberFormat.getNumberInstance(Locale.US);
		if(nf instanceof DecimalFormat){
			secMilliSecFormatter=(DecimalFormat)nf;
			StringBuffer doublePattern=new StringBuffer("00.###");
//			for(int m=0;m<324;m++){
//				doublePattern.append('#');
//			}
			secMilliSecFormatter.applyPattern(doublePattern.toString());
		}
		
	}
	

	/**
	 * Returns a string representation in the form hh:mm:ss.mmm.
	 * The format is often used for multimedia purposes.
	 * example output: 01:23:12.003 ( 1 hour, 23 min, 12 seconds and 3 milliseconds)
	 * @param time in seconds
	 * @return string representation
	 */
	private String toHourMinuteSecondMilliSecond(StringBuffer sb,double valInSeconds) {
		
		// TODO check for infinity
		
		int minutes = (int) valInSeconds / 60;
		double seconds = valInSeconds - ((double) minutes * 60);
		int hours = minutes / 60;
		minutes -= hours * 60;

		
		
		sb.append(twoDigitFormatter.format(hours));
		sb.append(":");
		sb.append(twoDigitFormatter.format(minutes));
		sb.append(':');
		sb.append(secMilliSecFormatter.format(seconds));
		
		//int intSecs=(int)seconds;
		
		//sb.append(twoDigitFormatter.format(intSecs));
		//sb.append('.');
		
		return sb.toString();
	}
	/**
		 * Returns a string representation in the form hh:mm:ss.mmm.
		 * The format is often used for multimedia purposes.
		 * example output: 01:23:12.003 ( 1 hour, 23 min, 12 seconds and 3 milliseconds)
		 * @param time in nanoseconds
		 * @return string representation
		 */
	private String toHourMinuteSecondMilliSecond(StringBuffer sb,long nanoseconds) {
		double seconds = (double) nanoseconds / 1000000000;
		return toHourMinuteSecondMilliSecond(sb,seconds);
	}

//	/**
//	 * Parses a string in hh:mm:ss.mmm format.
//	 * @param timeStr the string to parse
//	 * @param parsePosition the current parsing position
//	 * @return a Double object with the time value in nanoseconds or null if an parse error occurred
//	 * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
//	 */
	public Object parseObject(String dateStr, ParsePosition parsePosition){

		//String trimmedDateStr=dateStr.trim();
		Calendar cal=Calendar.getInstance();
		
		int pInd = parsePosition.getIndex();
		//if (arg1==null)arg1=new ParsePosition(0);
		if (parsePosition == null)
			throw new NullPointerException();
		Number hour=twoDigitFormatter.parse(dateStr, parsePosition);
		//int year=Integer.parseInt(trimmedDateStr.substring(pInd,pInd+4));
		//parsePosition.setIndex(parsePosition.getIndex()+2);
		if(dateStr.charAt(parsePosition.getIndex())!=':'){
			parsePosition.setErrorIndex(parsePosition.getIndex());
			//throw new ParseException("Character ':' expected.",parsePosition.getIndex());
			return null;
		}
		parsePosition.setIndex(parsePosition.getIndex()+1);
		Number minutes=twoDigitFormatter.parse(dateStr, parsePosition);
		if(dateStr.charAt(parsePosition.getIndex())!=':'){
			parsePosition.setErrorIndex(parsePosition.getIndex());
			//throw new ParseException("Character ':' expected.",parsePosition.getIndex());
			return null;
		}
		parsePosition.setIndex(parsePosition.getIndex()+1);
		Number seconds=secMilliSecFormatter.parse(dateStr, parsePosition);
		double res=hour.doubleValue()*3600+minutes.doubleValue()*60+seconds.doubleValue();
	
		return res;
}

	/* (non-Javadoc)
	 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
		
		if (arg0==null || arg2==null){
			throw new NullPointerException();
		}
		if(arg2.getField()!=0){
			throw new IllegalArgumentException("Format of field position "+arg2+" not supported.");
		}
		if (arg0 instanceof Integer) {
			int val = ((Integer) arg0).intValue();
			toHourMinuteSecondMilliSecond(arg1,val);
			return arg1;
		}else if (arg0 instanceof Long) {
			long val = ((Long) arg0).longValue();
			toHourMinuteSecondMilliSecond(arg1,val);
			return arg1;
		}else{
			throw new IllegalArgumentException("Object type not supported.");
		}
	}

	public static void main(String[] args) {
		// do some tests
		RFC3339TimeFormat mtf = new RFC3339TimeFormat();
		Integer testSecondsValue=new Integer(2);
		testSecondsValue=Integer.MIN_VALUE;
		System.out.println(testSecondsValue);
		String result = mtf.format(testSecondsValue);
		System.out.println(result);
		
		try {
			System.out.println(mtf.parseObject(result));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
	}

}
