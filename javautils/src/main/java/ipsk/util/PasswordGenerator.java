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

public class PasswordGenerator {

	
	public static int DEFAULT_LENGTH=6;
	private StringBuffer allowedPasswdChars;
	private int length=DEFAULT_LENGTH;
	
	public PasswordGenerator(){
	allowedPasswdChars = new StringBuffer();
    for (char c = '0'; c < '9'; c++) {
        allowedPasswdChars.append(c);
    }
    for (char c = 'a'; c < 'z'; c++) {
        allowedPasswdChars.append(c);
    }
    for (char c = 'A'; c < 'Z'; c++) {
        allowedPasswdChars.append(c);
    }
	}
    
    public String generateRandom(){
    StringBuffer passwd = new StringBuffer();
    for (int i = 0; i < length; i++) {
        int r = (int) (Math.random() * allowedPasswdChars.length());
        passwd.append(allowedPasswdChars.charAt(r));
    }
    return passwd.toString();
    }


	public int getLength() {
		return length;
	}


	public void setLength(int length) {
		this.length = length;
	}
}
