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
 * Formats a string for (multi-)media time representation.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class MediaTimeFormat extends Format {

    public static Format MEDIA_TIME_FORMAT = new MediaTimeFormat();
    
	//	public static final int HOUR_FIELD=1;
	//	public static final int MINUTE_FIELD=2;
	//	public static final int SECOND_FIELD=3;
	//	public static final int MILLISECOND_FIELD=4;

	private double seconds;
	private DecimalFormat hourMinSecFormatter = new DecimalFormat("00");
	private DecimalFormat secMilliSecFormatter = new DecimalFormat("00.000");
	
	private boolean showMilliSeconds=true;
	
	/**
	 * Creates a new media time formatter.
	 */
	public MediaTimeFormat() {
		super();
	}
	

	/**
	 * Returns a string representation in the form hh:mm:ss.mmm.
	 * The format is often used for multimedia purposes.
	 * example output: 01:23:12.003 ( 1 hour, 23 min, 12 seconds and 3 milliseconds)
	 * @param time in seconds
	 * @return string representation
	 */
	private String toHourMinuteSecondMilliSecond(double seconds) {
		double rest;
		// TODO check for infinity
		
		int minutes = (int) seconds / 60;
		rest = seconds - ((double) minutes * 60);
		int hours = minutes / 60;
		minutes -= hours * 60;
		StringBuffer sb=new StringBuffer();
		
		sb.append(hourMinSecFormatter.format(hours));
		sb.append(":");
		sb.append(hourMinSecFormatter.format(minutes));
		sb.append(":");
		if(showMilliSeconds){
			sb.append(secMilliSecFormatter.format(rest));
		}else{
			sb.append(hourMinSecFormatter.format(rest));
		}
		return sb.toString();
	}
	/**
		 * Returns a string representation in the form hh:mm:ss.mmm.
		 * The format is often used for multimedia purposes.
		 * example output: 01:23:12.003 ( 1 hour, 23 min, 12 seconds and 3 milliseconds)
		 * @param time in nanoseconds
		 * @return string representation
		 */
	private String toHourMinuteSecondMilliSecond(long nanoseconds) {
		double seconds = (double) nanoseconds / 1000000000;
		return toHourMinuteSecondMilliSecond(seconds);
	}

	/**
	 * Parses a string in hh:mm:ss.mmm format.
	 * @param timeStr the string to parse
	 * @param parsePosition the current parsing position
	 * @return a Double object with the time value in nanoseconds or null if an parse error occurred
	 * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
	 */
	public Object parseObject(String timeStr, ParsePosition parsePosition) {

		double seconds;
		int pInd = parsePosition.getIndex();
		//if (arg1==null)arg1=new ParsePosition(0);
		if (parsePosition == null)
			throw new NullPointerException();
		try {
			seconds = 3600 * Double.parseDouble(timeStr.substring(pInd, pInd + 2));
		} catch (NumberFormatException nfe) {
			parsePosition.setErrorIndex(pInd);
			return null;
		}
		pInd += 2;
		if (timeStr.charAt(pInd) != ':') {
			parsePosition.setErrorIndex(pInd);
			return null;
		}
		pInd++;
		try {
			String subStr = timeStr.substring(pInd, pInd + 2);
			seconds += 60 * Double.parseDouble(subStr);
		} catch (NumberFormatException nfe) {
			parsePosition.setErrorIndex(pInd);
			return null;
		}
		pInd += 2;
		if (timeStr.charAt(pInd) != ':') {
			parsePosition.setErrorIndex(pInd);
			return null;
		}
		pInd++;
		try {
			seconds += Double.parseDouble(timeStr.substring(pInd, pInd + 2));
		} catch (NumberFormatException nfe) {
			parsePosition.setErrorIndex(pInd);
			return null;
		}
		pInd += 2;
		if (timeStr.charAt(pInd) != '.') {
			parsePosition.setErrorIndex(pInd);
			return null;
		}
		double nanos;
		try {
			nanos = 1000 * Double.parseDouble(timeStr.substring(pInd, pInd + 3));
		} catch (NumberFormatException nfe) {
			parsePosition.setErrorIndex(pInd);
			return null;
		}
		pInd += 3;
		nanos += seconds * 1000000000;
		parsePosition.setIndex(pInd);
		return new Double(nanos);
	}

	/* (non-Javadoc)
	 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
		if (arg0==null){
			return new StringBuffer("--:--:--");
		}else if (arg0 instanceof Double) {
			double val = ((Double) arg0).doubleValue();
			arg1.append(toHourMinuteSecondMilliSecond(val));
			return arg1;
		} else if (arg0 instanceof Long) {
			long val = ((Long) arg0).longValue();
			arg1.append(toHourMinuteSecondMilliSecond(val));
			return arg1;
		}
		return null;
	}

	public static void main(String[] args) {
		// do some tests
		MediaTimeFormat mtf = new MediaTimeFormat();
		String result = mtf.format(new Double(36012));
		System.out.println(result);
		try {
			System.out.println(mtf.parseObject(result));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(mtf.parseObject("01:02:09.897"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}


	public boolean isShowMilliSeconds() {
		return showMilliSeconds;
	}


	public void setShowMilliSeconds(boolean showMilliSeconds) {
		this.showMilliSeconds = showMilliSeconds;
	}

}
