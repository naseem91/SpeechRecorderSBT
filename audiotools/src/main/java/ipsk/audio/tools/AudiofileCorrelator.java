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
 * Date  : 02.01.2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.tools;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.FileAudioSource;
import ipsk.audio.dsp.FloatRandomAccessStream;
import ipsk.audio.dsp.XCorrelator;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;



/**
 * Correlates to audio files.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class AudiofileCorrelator {

	AudioSource srcAs;

	AudioSource corrAs;


	AudioFileFormat aff;

	AudioFormat af;

	long frameLength;

	int frameSize;

	boolean signed = true;

	public AudiofileCorrelator(AudioSource srcAs,AudioSource corrAs)
			throws AudioFormatNotSupportedException, AudioSourceException {
		this.srcAs = srcAs;
        this.corrAs=corrAs;
		frameLength = srcAs.getFrameLength();
		af = srcAs.getFormat();
		frameSize = af.getFrameSize();
		if (af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
			signed = true;
		} else if (af.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED) {
			signed = false;
		} else {
			throw new AudioFormatNotSupportedException(af);
		}

	}

    
  


	public XCorrelator.CorrResult corr(long from, long length) throws IOException, AudioSourceException, AudioFormatNotSupportedException {

		if (from == -1) {
			from = frameLength;
		}
		if (length == -1) {
			length = frameLength;
		}
		if (from + length > frameLength) {
			length = frameLength - from;
		}
		//seek(from);

		XCorrelator xcorr=new XCorrelator(new FloatRandomAccessStream(srcAs),new FloatRandomAccessStream(corrAs));
		
		return xcorr.correlate(from, from+length);
	}

	

    public XCorrelator.CorrResult corrFromEnd(long toCorrelateFromEnd) throws AudioSourceException, IOException, AudioFormatNotSupportedException{
        long frameLength=srcAs.getFrameLength();
        long from=frameLength-toCorrelateFromEnd;
        if (from <0){
            throw new IllegalArgumentException("Cannot correlate. Source Stream is too short to correlate.");
        }
        
        return corr(from,toCorrelateFromEnd);
        
    }



	private static void printUsage() {
		System.out
				.println("Usage: java ipsk.audio.tools.AudiofileCorrelator srcFile corrFile command [ cmdparm1 ] ...[cmdparmn]\n"
						+ "       commands:\n"
						+ "       corr correlate from frameLength\n"
						+ "Frame position and length values have the following syntax:\n"
                        + "floating point values with appended 's' or 'ms' are interpreted as time (seconds respectively millisecond) values,\n"
                        + "integer values are interpreted as audio frame values\n"
                        + "The constant end can be used for the end of the sourceuadio file.\n"
                        + "Examples:\n"
                        + "corr 0 end : cross correlates the whole source file\n"
                        + "corr end-1s end : cross correlates last second of the file\n"
                        + "corr 100000 end : cross correlates from frame 100000 to the end.\n");
	}

	public static void main(String[] args) {
		//AudiofileEditor ae=new AudiofileEditor();
		if (args.length < 3) {
			printUsage();
			System.exit(-1);
		} else {
			
			File srcFile = new File(args[0]);
			

			File corrFile = new File(args[1]);

            AudioSource srcAs=new FileAudioSource(srcFile);
            
            FrameUnitParser fup=null;
			AudiofileCorrelator ae = null;
            XCorrelator.CorrResult result=null;
			try {
                fup=new FrameUnitParser(srcAs);
				ae = new AudiofileCorrelator(srcAs,new FileAudioSource(corrFile));
			} catch (Exception e1) {
				System.err.println(e1.getLocalizedMessage());
				e1.printStackTrace();
			}
			String cmd = args[2];
			String cmdOpts[] = new String[args.length - 3];
			for (int i = 3; i < args.length; i++) {
				cmdOpts[i - 3] = args[i];
			}

			if (cmd.equals("corr")) {
				long pos = 0;
				long len = 0;
				if (cmdOpts.length != 2) {
					printUsage();
					System.exit(-1);
				}
				try {
					pos = fup.parseFrameUnitString(cmdOpts[0]);
				} catch (NumberFormatException e) {
					System.err.println(cmdOpts[0] + " is not a number!");
					System.exit(-1);
				}
				try {
					len = fup.parseFrameUnitString(cmdOpts[1]);

				} catch (NumberFormatException e) {
					System.err.println(cmdOpts[1] + " is not a number!");
					System.exit(-1);
				}
				try {
					result=ae.corr(pos, len);
					//ae.close();
				} catch (Exception e) {
					System.err.println("Cannot correlate file: "
							+ e.getLocalizedMessage());
					System.exit(-1);
				}
                float fromEnd=0;
                try {
                    fromEnd = (srcAs.getFrameLength()-result.getPosition())*1000/srcAs.getFormat().getFrameRate();
                } catch (AudioSourceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                 System.out.println(result+" "+fromEnd+"ms from end");
                 
			}  else {
				System.err.println("Unknown command: " + cmd);
				printUsage();
				System.exit(-1);
			}

		}

	}

}
