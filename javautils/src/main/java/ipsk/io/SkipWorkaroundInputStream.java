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

package ipsk.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Workaround stream for wrong inputstream implemenattions concerning the skip method. 
 * @author klausj
 *
 */
public class SkipWorkaroundInputStream extends InputStream {

	private static final int DEF_DUMMY_BUFSIZE=2048;
	private InputStream src;
	private int dummyBufsize;
	private byte[] dummyBuf;
	public SkipWorkaroundInputStream(InputStream src){
		this(src,DEF_DUMMY_BUFSIZE);
	}
	
	public SkipWorkaroundInputStream(InputStream src,int dummyBufsize){
		super();
		this.src=src;
		this.dummyBufsize=dummyBufsize;
		dummyBuf=new byte[dummyBufsize];
	}
	
	public int read() throws IOException {
		
		return src.read();
	}

	
	
	public long skip(long n) throws IOException{
		long toSkip=n;
		if (n>dummyBufsize){
			toSkip=dummyBufsize;
		}
		return src.read(dummyBuf,0,(int) toSkip);
	}

	public int available() throws IOException {
		return src.available();
	}

	public void close() throws IOException {
		src.close();
	}

	

	public void mark(int arg0) {
		src.mark(arg0);
	}

	public boolean markSupported() {
		return src.markSupported();
	}

	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		return src.read(arg0, arg1, arg2);
	}

	public int read(byte[] arg0) throws IOException {
		return src.read(arg0);
	}

	public void reset() throws IOException {
		src.reset();
	}

	public String toString() {
		return ("Workaround for bad skip() implementations. Source stream: "+src.toString());
	}
	
}
