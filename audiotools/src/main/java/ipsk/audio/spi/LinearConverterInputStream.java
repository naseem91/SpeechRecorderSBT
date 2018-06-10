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
 * Date  : May 7, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.spi;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class LinearConverterInputStream extends InputStream {

	InputStream in;

	int byteCount = 0;

	byte[] sample;

	int read = 0;

	int index = 2;

	public LinearConverterInputStream(InputStream in) {
		this.in = in;
		sample = new byte[4];
		read = 0;
		index = 2;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {

		while (read < 4) {
			int readIn = in.read(sample, read, 4 - read);
			if (readIn == -1)
				return readIn;
			read += readIn;
		}
		int ret = ((int) sample[index++]) & 0x000000FF;
		if (index == 4) {
			read = 0;
			index = 2;
		}
		//System.out.println(read+" "+)
		return ret;
	}

}
