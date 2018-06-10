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

import java.util.Comparator;

/**
 * @author klausj
 *
 */
public class MIMETypePrecedenceComparator implements Comparator<MIMEType>{
	
	private static int TYPE_NOT_WILCARD=8;
	private static int SUBTYPE_NOT_WILDCARD=4;
	private static int SUBTYPE_NOT_NULL=2;
	public static int MAX_PRECENDENCE_VALUE=TYPE_NOT_WILCARD+SUBTYPE_NOT_WILDCARD;
	
	private int precedenceValue(MIMEType mime){
		int val=0;
		String type=mime.getType();
		if(!MIMEType.WILDCARD_TYPE.equals(type)){
			val+=TYPE_NOT_WILCARD;
		}
		String subType=mime.getSubType();
		if(subType!=null){
			val+=SUBTYPE_NOT_NULL;
			if(!MIMEType.WILDCARD_TYPE.equals(subType)){
				val+=SUBTYPE_NOT_WILDCARD;
			}
		}
		return val;
	}
	
	public int compare(MIMEType m1,MIMEType m2) {
		return precedenceValue(m2)-precedenceValue(m1);		
	}

	
}
