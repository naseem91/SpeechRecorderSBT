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

package ips.test;

import java.util.Locale;

/**
 * @author klausj
 *
 */
public class LowerUppercaseTest {
	
public static void main(String[] args){
		
		// some locale tests
		System.out.println("locale: "+Locale.getDefault());
		char ilc='i';
		char iuc='I';
		int iucCodepoint='I';
		System.out.println(Character.toUpperCase(ilc));
		System.out.println(Character.toLowerCase(iuc));
		System.out.println(Character.toLowerCase(iucCodepoint));
		System.out.println("i".toUpperCase());
		System.out.println("aBcIdef".equalsIgnoreCase("abcidef"));
		
		
	}
}
