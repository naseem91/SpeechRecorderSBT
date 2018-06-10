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

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digester {
	
	public static String createMD5Sum(String stringToEncode,String encodingCharset) throws NoSuchAlgorithmException{
			MessageDigest md5Digest=MessageDigest.getInstance("MD5");
			byte[] ba=Charset.forName(encodingCharset).encode(stringToEncode).array();
			return RadixConverters.bytesToHex(md5Digest.digest(ba));	
	}
	
	public static String createMD5SumWithUTF8Encoding(String stringToEncode) throws NoSuchAlgorithmException{
		return createMD5Sum(stringToEncode, "UTF-8");
	}
	
	public static String createMD5SumWithDefaultEncoding(String stringToEncode) throws NoSuchAlgorithmException{
		MessageDigest md5Digest=MessageDigest.getInstance("MD5");
		byte[] ba=stringToEncode.getBytes();
		return RadixConverters.bytesToHex(md5Digest.digest(ba));	
	}

		
	public static void main(String args[]){
		String testStr="blafasel";
		try {
			
			System.out.println("MD5: "+createMD5SumWithUTF8Encoding(testStr));
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
