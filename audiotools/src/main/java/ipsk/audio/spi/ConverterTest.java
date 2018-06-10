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

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class ConverterTest {

	public static void main(String[] args) {
		AudioFormat srcFormat = new AudioFormat(48000, 32, 1, true, false);
		AudioFormat trgFormat = new AudioFormat(48000, 16, 1, true, false);
		File in = new File("/homes/klausj/AAA1019Z0_0.wav");
		File out = new File("/homes/klausj/test_16.wav");
		AudioInputStream ins = null;
		AudioInputStream outs = null;
		try {
			ins = AudioSystem.getAudioInputStream(in);
			outs = AudioSystem.getAudioInputStream(trgFormat, ins);
		} catch (UnsupportedAudioFileException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		//LinearAudioConverter lac= new LinearAudioConverter();
		//lac.getAudioInputStream()

		boolean supp = AudioSystem.isConversionSupported(trgFormat, srcFormat);
		//System.out.println("S: " + supp);
		if (supp) {
			try {
				AudioSystem.write(outs, AudioFileFormat.Type.WAVE, out);
			} catch (IOException e1) {

				e1.printStackTrace();
			}finally{
				if(outs!=null)
					try {
						outs.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}
}
