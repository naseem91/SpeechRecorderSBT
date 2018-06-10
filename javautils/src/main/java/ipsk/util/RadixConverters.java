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


public class RadixConverters {
	
	public static String bytesToHex(byte[] bytes){
		StringBuffer hexSB=new StringBuffer(bytes.length*2);
		for(int i=0;i<bytes.length;i++){
			int twoByteInt=0xff & bytes[i];
			String intStr=Integer.toHexString(twoByteInt);
			if(intStr.length()==1){
				hexSB.append("0");
			}
			hexSB.append(intStr);
		}
		return hexSB.toString();
	}
	
	public static void main(String args[]){
		System.out.println(bytesToHex(new byte[]{0,12,-5,57,89}));
	}
	
}
