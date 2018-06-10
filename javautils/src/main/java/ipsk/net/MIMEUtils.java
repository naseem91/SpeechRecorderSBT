//    IPS Java Utils
// 	  (c) Copyright 2016
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

package ipsk.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ipsk.text.ParserException;

/**
 * @author klausj
 *
 */
public class MIMEUtils {

	static class QualityMIMEType implements Comparable<QualityMIMEType>{
		
		private MIMEType mimetype;
		public MIMEType getMimetype() {
			return mimetype;
		}
		public double getQ() {
			return q;
		}
		/**
		 * @param mimetype
		 * @param q
		 */
		public QualityMIMEType(MIMEType mimetype, double q) {
			super();
			this.mimetype = mimetype;
			this.q = q;
		}
		private double q;
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(QualityMIMEType o) {
			
			double sign= Math.signum(o.q-q);
			if(sign==0.0){
				MIMETypePrecedenceComparator mpc=new MIMETypePrecedenceComparator();
				return mpc.compare(this.getMimetype(),o.getMimetype());
			}else{
				return (int) sign*2*MIMETypePrecedenceComparator.MAX_PRECENDENCE_VALUE;
			}
		}
		
	}
	
	
	
	public static List<MIMEType> parseMediaRange(String mediaRangeStr) throws ParserException{
		List<QualityMIMEType> prefList=new ArrayList<QualityMIMEType>();
		String[] mediaStrs=mediaRangeStr.trim().split("\\s*,\\s*");
		for(String mStr:mediaStrs){
			// example: text/xml
			// example: text/xml;q=0.6;bla=foo
			String[] mediaTks=mStr.trim().split("\\s*;\\s*");
			String mTk=mediaTks[0].trim();
			double q=1.0;
			for(int i=1;i<mediaTks.length;i++){
				// only look for q param
				String acceptParam=mediaTks[i];
				String[] accParKeyVal=acceptParam.split("\\s*=\\s*");
				if(accParKeyVal!=null && accParKeyVal.length==2){
					if("q".equals(accParKeyVal[0])){
						q=Double.parseDouble(accParKeyVal[1]);
					}
				}
			}
			MIMEType mt=MIMEType.parse(mTk);
			prefList.add(new QualityMIMEType(mt, q));
		}
		Collections.sort(prefList);
		List<MIMEType> mimeList=new ArrayList<MIMEType>();
		for(QualityMIMEType qmt:prefList){
			mimeList.add(qmt.getMimetype());
		}
		return mimeList;
	}
	
	public static MIMEType preferredMimeType(List<MIMEType> suppMimes,List<MIMEType> reqMimes){
		for(MIMEType mt:reqMimes){	
			for(MIMEType suppMime:suppMimes){
				if(suppMime.matches(mt)){
					return suppMime;
				}
			}
		}
		return null;
	}
	
}
