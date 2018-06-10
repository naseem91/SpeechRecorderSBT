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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Formats a string in RFC3339 date representation.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class RFC3339DateTimeFormat extends Format {

	//	public static final int HOUR_FIELD=1;
	//	public static final int MINUTE_FIELD=2;
	//	public static final int SECOND_FIELD=3;
	//	public static final int MILLISECOND_FIELD=4;

	public enum TemporalType {DATE,TIMESTAMP};
	public final static boolean DEFAULT_FIXED_LENGTH=true;
	
	
	private TemporalType temporalType=TemporalType.TIMESTAMP;
	private boolean fixedLength=DEFAULT_FIXED_LENGTH;
	
	private RFC3339TimeFormat timeFormat=new RFC3339TimeFormat();
	private DecimalFormat twoDigitFormatter = new DecimalFormat("00");
	private DecimalFormat fourDigitFormatter = new DecimalFormat("0000");
	private DecimalFormat milliSecFormatter=new DecimalFormat("###");
	private DecimalFormat fixedMilliSecFormatter = new DecimalFormat("000");

	
	/**
	 * Creates a new media time formatter.
	 */
	public RFC3339DateTimeFormat() {
		super();
		twoDigitFormatter.setMaximumFractionDigits(0);
	}
	
	

//	/**
//	 * Parses a string in hh:mm:ss.mmm format.
//	 * @param timeStr the string to parse
//	 * @param parsePosition the current parsing position
//	 * @return a Double object with the time value in nanoseconds or null if an parse error occurred
//	 * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
//	 */
	public Object parseObject(String dateStr, ParsePosition parsePosition){
		if (parsePosition == null)
			throw new NullPointerException();
		//String trimmedDateStr=dateStr.trim();
		Calendar cal=Calendar.getInstance();
		if(temporalType.equals(TemporalType.DATE)){
			cal.set(Calendar.HOUR,0);
			cal.set(Calendar.MINUTE,0);
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND,0);
			// do not use timezone for dates without time
			cal.set(Calendar.ZONE_OFFSET, 0);
		}
		cal.setTimeInMillis(0);
	
		
		Number year=fourDigitFormatter.parse(dateStr,parsePosition);
		cal.set(Calendar.YEAR, year.intValue());
		if(dateStr.charAt(parsePosition.getIndex())!='-'){
			parsePosition.setErrorIndex(parsePosition.getIndex());
			//throw new ParseException("Character ':' expected.",parsePosition.getIndex());
			return null;
		}
		parsePosition.setIndex(parsePosition.getIndex()+1);
		Number month=twoDigitFormatter.parse(dateStr, parsePosition);
		
		// month count starts at zero!!
		cal.set(Calendar.MONTH, month.intValue()-1);
		if(dateStr.charAt(parsePosition.getIndex())!='-'){
			parsePosition.setErrorIndex(parsePosition.getIndex());
			//throw new ParseException("Character ':' expected.",parsePosition.getIndex());
			return null;
		}
		parsePosition.setIndex(parsePosition.getIndex()+1);
		Number day=twoDigitFormatter.parse(dateStr, parsePosition);
		cal.set(Calendar.DAY_OF_MONTH, day.intValue());
		if(temporalType.equals(TemporalType.DATE)){
			return cal.getTime();
		}
		
		if(dateStr.charAt(parsePosition.getIndex())!='T'){
			parsePosition.setErrorIndex(parsePosition.getIndex());
			//throw new ParseException("Character ':' expected.",parsePosition.getIndex());
			return null;
		}
		parsePosition.setIndex(parsePosition.getIndex()+1);
		
		Number hour=twoDigitFormatter.parse(dateStr, parsePosition);
		cal.set(Calendar.HOUR,hour.intValue());
		//int year=Integer.parseInt(trimmedDateStr.substring(pInd,pInd+4));
		//parsePosition.setIndex(parsePosition.getIndex()+2);
		if(dateStr.charAt(parsePosition.getIndex())!=':'){
			parsePosition.setErrorIndex(parsePosition.getIndex());
			//throw new ParseException("Character ':' expected.",parsePosition.getIndex());
			return null;
		}
		parsePosition.setIndex(parsePosition.getIndex()+1);
		// DecimalFormat is greedy and parses the fraction part
	
		
		Number minutes=twoDigitFormatter.parse(dateStr, parsePosition);
		
		cal.set(Calendar.MINUTE,minutes.intValue());
		if(dateStr.charAt(parsePosition.getIndex())!=':'){
			parsePosition.setErrorIndex(parsePosition.getIndex());
			//throw new ParseException("Character ':' expected.",parsePosition.getIndex());
			return null;
		}
		parsePosition.setIndex(parsePosition.getIndex()+1);
		int currPos=parsePosition.getIndex();
		ParsePosition secondsParsePo=new ParsePosition(0);
		String secondsStringwithoutFraction=dateStr.substring(currPos, currPos+2);
		Number seconds=twoDigitFormatter.parse(secondsStringwithoutFraction,secondsParsePo);
		if (seconds==null){
			parsePosition.setErrorIndex(parsePosition.getIndex()+secondsParsePo.getErrorIndex());
			return null;
		}
		cal.set(Calendar.SECOND,seconds.intValue());
		parsePosition.setIndex(parsePosition.getIndex()+2);
		if(dateStr.charAt(parsePosition.getIndex())=='.'){
			parsePosition.setIndex(parsePosition.getIndex()+1);
			Number mss;
			if(fixedLength){
				mss=fixedMilliSecFormatter.parse(dateStr,parsePosition);
			}else{
				mss=milliSecFormatter.parse(dateStr,parsePosition);
			}
			cal.set(Calendar.MILLISECOND, mss.intValue());
		}
		if(dateStr.charAt(parsePosition.getIndex())=='Z'){
			
			boolean negative=false;
			parsePosition.setIndex(parsePosition.getIndex()+1);
			char maybePlusMinus=dateStr.charAt(parsePosition.getIndex());
			if(maybePlusMinus=='+'){
				parsePosition.setIndex(parsePosition.getIndex()+1);
			}else{
				parsePosition.setIndex(parsePosition.getIndex()+1);
				negative=true;
			}
			Number tzHours=twoDigitFormatter.parse(dateStr,parsePosition);
			if(dateStr.charAt(parsePosition.getIndex())!=':'){
				parsePosition.setErrorIndex(parsePosition.getIndex());
				//throw new ParseException("Character ':' expected.",parsePosition.getIndex());
				return null;
			}
			parsePosition.setIndex(parsePosition.getIndex()+1);
			Number tzMinutes=twoDigitFormatter.parse(dateStr,parsePosition);
			int tzMillis=(((tzHours.intValue()*60)+tzMinutes.intValue())*60)*1000;
			if(negative){
				tzMillis=-tzMillis;
			}
			cal.set(Calendar.ZONE_OFFSET,tzMillis);
			
		}
		
		return cal.getTime();
}
	public Object parseObject(String dateStr) throws ParseException{
		ParsePosition parsePosition=new ParsePosition(0);
		Object parsedDate=parseObject(dateStr, parsePosition);
		if(parsedDate==null){
			ParseException pe=new ParseException(dateStr,parsePosition.getErrorIndex());
			throw pe;
		}
		return parsedDate;
	}
	
	private void formatTime(long time,Calendar calendar,StringBuffer arg1){
		int hour=calendar.get(Calendar.HOUR_OF_DAY);
		arg1.append(twoDigitFormatter.format(hour));
		arg1.append(':');
		int min=calendar.get(Calendar.MINUTE);
		arg1.append(twoDigitFormatter.format(min));
		arg1.append(':');
		int sec=calendar.get(Calendar.SECOND);
		arg1.append(twoDigitFormatter.format(sec));
		
		int msec=calendar.get(Calendar.MILLISECOND);
		if(fixedLength){
			arg1.append('.');
			arg1.append(fixedMilliSecFormatter.format(msec));
		}else{
		if(msec>0){
			arg1.append('.');
			arg1.append(msec);
		}
		}
		//arg1.append(time);
		
		TimeZone tz=calendar.getTimeZone();
		int offset=tz.getOffset(time);
		if(fixedLength || offset!=0){
			arg1.append('Z');
			int offsetMinutes=(offset/1000)/60;
			int offsetHours=offsetMinutes/60;
			int offsetRestMinutes=offsetMinutes%60;
			if(fixedLength && offset>=0){
				arg1.append('+');
			}
			arg1.append(twoDigitFormatter.format(offsetHours));
			arg1.append(':');
			arg1.append(twoDigitFormatter.format(offsetRestMinutes));
		}
	}

	/* (non-Javadoc)
	 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
		if (arg0==null){
			throw new NullPointerException();
		}else if (arg0 instanceof Long || arg0 instanceof Date) {
			long dateInMillis;
			if(arg0 instanceof Date){
				dateInMillis=((Date)arg0).getTime();
			}else{
				dateInMillis=(Long)arg0;
			}
			Calendar calendar=Calendar.getInstance();
			calendar.setTimeInMillis(dateInMillis);
			int year=calendar.get(Calendar.YEAR);
			arg1.append(fourDigitFormatter.format(year));
			arg1.append('-');
			int month=calendar.get(Calendar.MONTH);
			arg1.append(twoDigitFormatter.format(month+1));
			arg1.append('-');
			int day=calendar.get(Calendar.DAY_OF_MONTH);
			arg1.append(twoDigitFormatter.format(day));
			if(temporalType.equals(TemporalType.TIMESTAMP)){
				arg1.append('T');
				formatTime(dateInMillis, calendar, arg1);
			}
			return arg1;
		}
		return null;
	}

	public static void main(String[] args) {
		// do some tests
		RFC3339DateTimeFormat mtf = new RFC3339DateTimeFormat();
		// Test date time
		Date date=new Date(0);
		System.out.println(date);
		String result = mtf.format(date.getTime());
		System.out.println(result);
		Date parsedDate;;
		try {
			parsedDate=(Date)(mtf.parseObject(result));
			System.out.println(parsedDate);
			System.out.println(date.equals(parsedDate));
			System.out.println(date.getTime());
			System.out.println(parsedDate.getTime());
			System.out.println(date.getTime()==parsedDate.getTime());
			
			System.out.println(mtf.parseObject("1979-12-31T16:04:00.000Z+00:00"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// test date only
		Date dateOnly=new Date();
		System.out.println(dateOnly);
		mtf.setTemporalType(RFC3339DateTimeFormat.TemporalType.DATE);
		result = mtf.format(dateOnly);
		System.out.println(result);
		
		try {
			Date dateOnlyParsed=(Date)(mtf.parseObject(result));
			
			System.out.println(dateOnlyParsed);
			Date earlyTestdate=(Date)(mtf.parseObject("1969-12-31"));
			System.out.println(earlyTestdate.getTime());
			System.out.println(mtf.format(earlyTestdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		
	}



	public TemporalType getTemporalType() {
		return temporalType;
	}



	public void setTemporalType(TemporalType temporalType) {
		this.temporalType = temporalType;
	}

}
