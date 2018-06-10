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
 * Date  : 16.09.2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.tools;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;



/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class RepeatFinder {

	static final boolean DEBUG = false;

	int bufLen;

	int toCompare;

	Vector<byte[]> bufs;

	File f;

	AudioInputStream ais = null;

	boolean verbose = false;

	public RepeatFinder(File f, int bufLen, int toCompare) {
		this.f = f;
		this.bufLen = bufLen;
		this.toCompare = toCompare;
		bufs = new Vector<byte[]>();
		try {
			ais = AudioSystem.getAudioInputStream(f);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void read() {
		if (verbose)
			System.out.println("Reading " + f);
		int read = 0;
		while (read >= 0) {
			byte b[] = new byte[bufLen];
			try {
				read = ais.read(b, 0, bufLen);
			} catch (IOException e) {

				e.printStackTrace();
			}
			bufs.add(b);
		}

	}

	public void findRepeats() {
		if (verbose)
			System.out.print("Checking buffer ");
		for (int i = 0; i < bufs.size(); i++) {
			if (verbose)
				System.out.print(i + " ");
			byte b1[] = (byte[]) bufs.elementAt(i);
			for (int j = i + 1; j < bufs.size(); j++) {
				byte b2[] = (byte[]) bufs.elementAt(j);
				boolean equal = true;

				for (int s = 0; s < toCompare; s++) {
					equal &= (b1[s] == b2[s]);
				}
				if (equal) {
					if (verbose)
						System.out.println();
					System.out.println(f + " equality_detected " + i + " = "
							+ j);
				}
			}
		}
	}

	public static void main(String[] args) {
		int toCmp = Integer.parseInt(args[0]);
		for (int i = 1; i < args.length; i++) {
			File f = new File(args[i]);
			RepeatFinder cf = null;
			try {

				cf = new RepeatFinder(f, 4096, toCmp);

			} catch (Exception e) {
				e.printStackTrace();
			}
			cf.read();
			cf.findRepeats();
		}
	}
}
