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
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Formats a string for multimedia time representation.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class TimeFormat extends Format {

    public static Format FIXED_SECONDS_MS_TIME_FORMAT = new TimeFormat(
            TimeFormat.SECONDS, "########0.000");

	//double seconds;
	//private String pattern;
	private int divider;
	private double secondsDivider;
	private DecimalFormat format;
	public final static int NANOSECONDS=0;
	public final static int MICROSECONDS=1;
	public final static int MILLISECONDS=2;
	public final static int SECONDS=3;
    private FieldPosition zeroFieldPosition;
	
// private DecimalFormat hourFormat=null;
// private DecimalFormat minuteFormat=null;
// private DecimalFormat secondFormat=null;
// private DecimalFormat milliSecondFormat=null;
	//DecimalFormat hourMinSecFormatter = new DecimalFormat("00");
	//DecimalFormat secMilliSecFormatter = new DecimalFormat("00.000");
	/**
	 * Creates a new media time formatter.
	 */
	public TimeFormat(int unit,String pattern) {
	   divider=(int)Math.pow(1000,unit);
	   secondsDivider=Math.pow(1000,unit-3);
	   format=new DecimalFormat(pattern);
	   zeroFieldPosition=new FieldPosition(0);
	}
	
	
	
	
	private String createCharSequence(char c,int count){
	    StringBuffer sb=new StringBuffer();
	    for (int i=0;i<count;i++){
	        sb.append(c);
	    }
	    return sb.toString();
	}
	
	
	

	/**
	 * Parses a string in hh:mm:ss.mmm format.
	 * @param timeStr the string to parse
	 * @param parsePosition the current parsing position
	 * @return a Double object with the time value in nanoseconds or null if an parse error occurred
	 * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
	 */
	public Object parseObject(String timeStr, ParsePosition parsePosition) {
	    // TODO
	    return null;
		
	}

	/* (non-Javadoc)
	 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
	    
	StringBuffer sb=new StringBuffer();
	//sb.append(pattern);
		if (arg0==null){
		    
			//return new StringBuffer("--:--:--");
			return format.format(Double.NaN,sb,null);
		}else if (arg0 instanceof Double) {
			double val = ((Double) arg0).doubleValue();
			double dval=val/secondsDivider;
			
			
			return format.format(dval,sb,zeroFieldPosition);
		} else if (arg0 instanceof Long) {
			long val = ((Long) arg0).longValue();
			//arg1.append(toHourMinuteSecondMilliSecond(val));
			double dval=(double)val/divider;
			return format.format(dval,sb,zeroFieldPosition);
		}
		return null;
		
		
		
		
		
	}

	public static void main(String[] args) {
		// do some tests
		TimeFormat mtf = new TimeFormat(MILLISECONDS,"######0.000");
		String result = mtf.format(new Double(0.8));
		System.out.println(result);
		try {
			System.out.println(mtf.parseObject(result));
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		try {
//			System.out.println(mtf.parseObject("01:02:09.897"));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
		
	}

}
