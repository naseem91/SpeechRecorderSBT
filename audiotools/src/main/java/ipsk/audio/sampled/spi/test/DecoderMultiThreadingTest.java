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

public class DecoderMultiThreadingTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File dir=new File(args[0]);
		int loops=Integer.parseInt(args[1]);
		File[] files=dir.listFiles();
		for(int loopCount=0;loopCount<loops;loopCount++){
			for(File f:files){
				DecoderTest dt=new DecoderTest(f);
				dt.start();
				
			}
		}
	}

}
