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

package ipsk.audio.dsp;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSourceException;
import ipsk.audio.FileAudioSource;
import ipsk.math.Complex;
//import ipsk.math.FFT;
import ipsk.math.DFT;
import ipsk.math.FFT;
import ipsk.math.FFT2;
import ipsk.math.GaussianWindow;
import ipsk.math.HammingWindow;
import ipsk.math.Window;

/**
 * A FFT converted audio stream.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class FourierAudioInputStream {

	// private DFTProcessor dftProcessor;
	private int channels;

	private double[][] srcBuf;

	private int n;

	private int len;

	private FloatRandomAccessStream srcStream;

	//private FFT fft;

//	private FFT fft;
	private FFT2 fft;
	
	private Window window;

	private int windowLen;
	
	private int frameOffset=0;
	

	public FourierAudioInputStream(FloatRandomAccessStream srcStream) throws AudioSourceException {
		this(srcStream,128,128);
	}

	public FourierAudioInputStream(FloatRandomAccessStream srcStream, int n) throws AudioSourceException {
		this(srcStream, n, n);
	}

	// public FourierAudioInputStream(FloatAudioInputStream srcStream,int n,int
	// len){
	// this(srcStream,n,len,new HammingWindow(n));
	// }
	public FourierAudioInputStream(FloatRandomAccessStream srcStream, int n,
			int windowSize) throws AudioSourceException {
		this.srcStream = srcStream;
		channels=getChannels();
		setNAndWindowSize(n, windowSize);
	}
	
	
	public void setNAndWindowSize(int n,int windowSize){
		this.n=n;
		this.len=windowSize;
		
		if (n > len) {
			srcBuf = new double[n][channels];
			// zero padding at start of stream
			int padding = n - len;
			for (int ch = 0; ch < channels; ch++) {
				for (int i = 0; i < padding; i++) {
					srcBuf[len + i][ch] = 0;
				}
			}
			windowLen = len;
		} else {
			srcBuf = new double[len][channels];
			windowLen = n;
		}
//		window = new HammingWindow(windowLen);
		window=new GaussianWindow(windowLen);
		// dftProcessor=new DFTProcessor(size,channels);
		fft = new FFT2(n);
		//fft=new DFT();
	}

	public void setFramePosition(long newPos) throws AudioSourceException{
		long srcStreamPos=newPos-len/2;
		if(srcStreamPos<0){
			frameOffset=(int)-srcStreamPos;
			srcStreamPos=0;
		}else{
			frameOffset=0;
		}
		srcStream.setFramePosition(srcStreamPos);
	}
	
	public boolean readFrame(Complex[][][] buf, int offset) throws  AudioSourceException {

		int read = 0;
		if(frameOffset>0){
			for(int i=0;i<frameOffset;i++){
				for(int ch=0;ch<channels;ch++){
				srcBuf[i][ch]=0;
				}
			}
			read+=frameOffset;
		}
		while (read < len) {
			int r = srcStream.readFrames(srcBuf, read, len - read);
			if (r == -1) {
				if(read==0){
					return false;
				}else{
					// zero padding at end of stream
					for(;read<len;read++){
						for (int ch = 0; ch < channels; ch++) {
							srcBuf[read][ch]=0.0;

						}
					}

				}
			}else{
				read += r;
			}
		}
		// dftProcessor.processFrame(srcBuf,0,buf,offset,size/2);
		// Complex[] x=new Complex[size];
		for (int ch = 0; ch < channels; ch++) {
			double[] chBuf = new double[n];
			for (int i = 0; i < windowLen; i++) {
				chBuf[i] = srcBuf[i][ch] * window.getScale(i);
			}
			Complex[] x = fft.process(chBuf);
			
//			for (int i = 0; i < n; i++) {
//				buf[offset][ch][i] = x[i];
//			}
			System.arraycopy(x, 0, buf[offset][ch],0,n);
		}
		return true;
	}
	
	public void close() throws AudioSourceException{
		srcStream.close();
	}

	public static void main(String[] args) throws AudioSourceException {
		long start = System.currentTimeMillis();
		PrintStream out = System.out;
		FileAudioSource ais = null;
		if (args.length < 3) {
			System.err.println("Usage: "
					+ FourierAudioInputStream.class.getName()
					+ " audioFilename DFT_size window_len [ouputfile]");
		}
		int size = new Integer(args[1]).intValue();
		float windowLen = new Float(args[2]).floatValue();
		if (args.length > 2) {
			try {
				out = new PrintStream(new FileOutputStream(args[2]));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ais = new FileAudioSource(new File(args[0]));
		FloatRandomAccessStream fAis = null;
		try {
			fAis = new FloatRandomAccessStream(ais);
		} catch (AudioFormatNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AudioSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		float sampleRate=AudioSystem.NOT_SPECIFIED;
		try {
			sampleRate = ais.getFormat().getSampleRate();
		} catch (AudioSourceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int windowSize = (int) (sampleRate * windowLen);
		//System.out.println("Length: "+fAis.getFrameLength()/sampleRate+" s Window: " + windowSize + " N: " + size);
		FourierAudioInputStream dftAis = new FourierAudioInputStream(fAis,
				size, windowSize);
		int channels = ais.getFormat().getChannels();
		
		float fStep = sampleRate / size;
		Complex[][][] buf = new Complex[1][channels][size];
		int frame = 0;
		while (dftAis.readFrame(buf, 0)) {
			//out.println("Frame: " + frame);
			for (int ch = 0; ch < channels; ch++) {
				//out.println("Channel: " + ch);
				for (int i = 0; i < size / 2; i++) {
					// out.println(i*fStep+" Hz:
					// "+buf[0][ch][i].magnitude());
				}
			}
			frame++;
		}
		dftAis.close();
		long end = System.currentTimeMillis();
		//System.out.println("Calculation time: " + (end - start) / 1000 + " s");
		Toolkit.getDefaultToolkit().beep();
		Toolkit.getDefaultToolkit().beep();
		Toolkit.getDefaultToolkit().beep();
	}

	public int getChannels() throws AudioSourceException{
		return srcStream.getChannels();
	}

	public long getFrameLength() throws AudioSourceException {
		return srcStream.getFrameLength();
	}


	
}
