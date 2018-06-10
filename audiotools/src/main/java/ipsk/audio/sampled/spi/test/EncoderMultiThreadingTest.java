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

package ipsk.audio.sampled.spi.test;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

public class EncoderMultiThreadingTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 4) {
			System.err.println("Usage: "
					+ EncoderMultiThreadingTest.class.getName()
					+ " audiofilesDirectory encoding fileformattype extension");
			System.err.println("e.g.   "
					+ EncoderMultiThreadingTest.class.getName()
					+ " test FLAC FLAC flac");
		}
		File dir = new File(args[0]);

		File[] files = dir.listFiles();
		for (File f : files) {
			EncoderTest dt = new EncoderTest(f, new AudioFormat.Encoding(
					args[1]), new AudioFileFormat.Type(args[2], args[3]));
			dt.start();

		}
	}

}
