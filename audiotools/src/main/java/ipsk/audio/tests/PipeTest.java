//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : 19.09.2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.tests;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class PipeTest {

	PipedInputStream pis;

	PipedOutputStream pos;

	byte[] inbuf = new byte[4096];

	public PipeTest() throws IOException {
		pis = new PipedInputStream();

		pos = new PipedOutputStream(pis);
	}

	public void start() {

		for (int i = 0; i < 100; i++) {
			System.out.println("Writing " + i);
			try {

				pos.write(inbuf, 0, 4096);
			} catch (IOException e2) {

				e2.printStackTrace();
			}
		}
		try {
			System.out.println(pis.available());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		PipeTest pt = null;
		try {
			pt = new PipeTest();
		} catch (IOException e) {

			e.printStackTrace();
		}
		pt.start();
	}

}
