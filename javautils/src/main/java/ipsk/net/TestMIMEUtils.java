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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import ipsk.text.ParserException;

/**
 * @author klausj
 *
 */
public class TestMIMEUtils {

	public static String TEST_ACCEPT_STRING_1="text/html,application/xhtml+xml;q=0.1,application/xml;q=0.9,*/*;q=0.8";
	
	// Default Java HTTP request:
	public static String TEST_ACCEPT_STRING_2="text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
	
	@Test
	public void test1(){

		try {
			List<MIMEType> pmts=MIMEUtils.parseMediaRange(TEST_ACCEPT_STRING_1);
			assertNotNull(pmts);
			assertEquals(4,pmts.size());
			assertEquals("text/html", pmts.get(0).toString());
			assertEquals("application/xml", pmts.get(1).toString());
			assertEquals("*/*", pmts.get(2).toString());
			assertEquals("application/xhtml+xml", pmts.get(3).toString());
		} catch (ParserException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test2(){

		try {
			List<MIMEType> pmts=MIMEUtils.parseMediaRange(TEST_ACCEPT_STRING_2);
			assertNotNull(pmts);
			assertEquals(5,pmts.size());
			assertEquals("text/html", pmts.get(0).toString());
			assertEquals("image/gif", pmts.get(1).toString());
			assertEquals("image/jpeg", pmts.get(2).toString());
			assertEquals("*/*", pmts.get(3).toString());
			assertEquals("*", pmts.get(4).toString());
		} catch (ParserException e) {
			fail(e.getMessage());
		}
	}

}
