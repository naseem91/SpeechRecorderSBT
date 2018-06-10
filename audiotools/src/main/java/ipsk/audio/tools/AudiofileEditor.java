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
import ipsk.audio.arr.Selection;
import ipsk.io.EditInputStream;
import ipsk.io.InterleaveEditInputStream;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Command line tool to edit audio streams.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class AudiofileEditor {

	private AudioInputStream ais;

	private AudioInputStream editAudioInputStream;

	private AudioFormat af;

	private long frameLength;

	private int frameSize;

	private boolean signed = true;

	public AudiofileEditor(AudioInputStream ais)
			throws AudioFormatNotSupportedException {
		this.ais = ais;
		frameLength = ais.getFrameLength();
		af = ais.getFormat();
		frameSize = af.getFrameSize();
		if (af.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
			signed = true;
		} else if (af.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED) {
			signed = false;
		} else {
			throw new AudioFormatNotSupportedException(af);
		}

	}

    
  

	public AudioInputStream cut(Selection s) throws IOException {
		return cut(s.getLeft(), s.getLength());
	}

	public AudioInputStream cut(long from, long length) throws IOException {

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

		EditInputStream eis = new EditInputStream(ais, frameSize, from, length);
		editAudioInputStream = new AudioInputStream(eis, af, length);
		//corrAudioInputStream= new AudioInputStream(eis, af, -1);
		return editAudioInputStream;
	}

	public AudioInputStream shorten(long toCut) throws IOException {
		return cut(0, frameLength - toCut);
	}

    public AudioInputStream cutFromEnd(long toCutFromEnd) throws IOException {
        return cut(frameLength-toCutFromEnd, frameLength);
    }

	private AudioInputStream pick(int ch) {
		int sampleBytes = (af.getSampleSizeInBits() / 8);
		if (af.getSampleSizeInBits() % 8 > 0)
			sampleBytes++;
		int off = ch * sampleBytes;
		InterleaveEditInputStream eis = new InterleaveEditInputStream(ais, af.getFrameSize(),off,
				sampleBytes);

		AudioFormat pickAf = new AudioFormat(af.getSampleRate(), af
				.getSampleSizeInBits(), 1, signed, af.isBigEndian());
		editAudioInputStream = new AudioInputStream(eis, pickAf, ais
				.getFrameLength());
		//corrAudioInputStream= new AudioInputStream(eis, af, -1);
		return editAudioInputStream;
	}

	public AudioInputStream append(AudioInputStream ais2)
			throws AudioFileEditorException {
		if (!ais.getFormat().matches(ais2.getFormat())) {
			throw new AudioFileEditorException(
					"Cannot append streams with different audio formats !");
		}

		SequenceInputStream resStream = new SequenceInputStream(ais, ais2);
		return new AudioInputStream(resStream, af, AudioSystem.NOT_SPECIFIED);
	}

	private static void printUsage() {
		System.out
				.println("Usage: java ipsk.audio.tools.AudiofileEditor audioInputFile audioOutputFile command [ cmdparm1 ] ...[cmdparmn]\n"
						+ "       commands:\n"
						+ "       cut from frameLength\n"
                        + "       cut_from_end frames\n"
						+ "       shorten frames\n"
						+ "       pick channelindex\n"
						+ "       append filename\n"
						+ "Note: All position and frameLength values must be given in audio frames.\n"
						+ "Note: channelindex counts from channel 0.");
	}

	public static void main(String[] args) {
		//AudiofileEditor ae=new AudiofileEditor();
		if (args.length < 3) {
			printUsage();
			System.exit(-1);
		} else {
			File inFile = null;
			File outFile = null;
			AudioInputStream ais = null;
			try {
				inFile = new File(args[0]);
				ais = AudioSystem.getAudioInputStream(inFile);
			} catch (UnsupportedAudioFileException e) {
				System.err.println(e.getLocalizedMessage());
			} catch (IOException e) {
				System.err.println("Cannot open " + args[0] + ": "
						+ e.getLocalizedMessage());
			}

			outFile = new File(args[1]);

			AudiofileEditor ae = null;
			try {
				ae = new AudiofileEditor(ais);
			} catch (AudioFormatNotSupportedException e1) {
				System.err.println(e1.getLocalizedMessage());
				e1.printStackTrace();
			}
			String cmd = args[2];
			String cmdOpts[] = new String[args.length - 3];
			for (int i = 3; i < args.length; i++) {
				cmdOpts[i - 3] = args[i];
			}

			if (cmd.equals("cut")) {
				long pos = 0;
				long len = 0;
				if (cmdOpts.length != 2) {
					printUsage();
					System.exit(-1);
				}
				try {
					pos = Long.parseLong(cmdOpts[0]);
				} catch (NumberFormatException e) {
					System.err.println(cmdOpts[0] + " is not a number!");
					System.exit(-1);
				}
				try {
					len = Long.parseLong(cmdOpts[1]);

				} catch (NumberFormatException e) {
					System.err.println(cmdOpts[1] + " is not a number!");
					System.exit(-1);
				}
				try {
					AudioSystem.write(ae.cut(pos, len), AudioSystem
							.getAudioFileFormat(inFile).getType(), outFile);
					//ae.close();
				} catch (IOException e) {
					System.err.println("Cannot cut file: "
							+ e.getLocalizedMessage());
					System.exit(-1);
				} catch (UnsupportedAudioFileException e) {
					System.err.println(e.getLocalizedMessage());
					System.exit(-1);
				}

			} else if (cmd.equals("cut_from_end")) {
                //long pos = 0;
                long len = 0;
                if (cmdOpts.length != 1) {
                    printUsage();
                    System.exit(-1);
                }
               
                try {
                    len = Long.parseLong(cmdOpts[0]);

                } catch (NumberFormatException e) {
                    System.err.println(cmdOpts[0] + " is not a number!");
                    System.exit(-1);
                }
                try {
                    AudioSystem.write(ae.cutFromEnd(len), AudioSystem
                            .getAudioFileFormat(inFile).getType(), outFile);
                    //ae.close();
                } catch (IOException e) {
                    System.err.println("Cannot cut file: "
                            + e.getLocalizedMessage());
                    System.exit(-1);
                } catch (UnsupportedAudioFileException e) {
                    System.err.println(e.getLocalizedMessage());
                    System.exit(-1);
                }

            }else if (cmd.equals("shorten")) {
				long toCut = 0;
				if (cmdOpts.length != 1) {
					printUsage();
					System.exit(-1);
				}
				try {
					toCut = Long.parseLong(cmdOpts[0]);
				} catch (NumberFormatException e) {
					System.err.println(cmdOpts[0] + " is not a number!");
					System.exit(-1);
				}
				try {
					AudioSystem.write(ae.shorten(toCut), AudioSystem
							.getAudioFileFormat(inFile).getType(), outFile);

				} catch (IOException e) {
					System.err.println("Cannot shorten file: "
							+ e.getLocalizedMessage());
					System.exit(-1);
				} catch (UnsupportedAudioFileException e) {
					System.err.println(e.getLocalizedMessage());
					System.exit(-1);
				}

			} else if (cmd.equals("pick")) {
				int ch = 0;
				if (cmdOpts.length != 1) {
					printUsage();
					System.exit(-1);
				}
				try {
					ch = Integer.parseInt(cmdOpts[0]);
				} catch (NumberFormatException e) {
					System.err
							.println(cmdOpts[0] + " is not a channel number!");
					System.exit(-1);
				}
				try {
					AudioSystem.write(ae.pick(ch), AudioSystem
							.getAudioFileFormat(inFile).getType(), outFile);

				} catch (IOException e) {
					System.err.println("Cannot shorten file: "
							+ e.getLocalizedMessage());
					System.exit(-1);
				} catch (UnsupportedAudioFileException e) {
					System.err.println(e.getLocalizedMessage());
					System.exit(-1);
				}

			} else if (cmd.equals("append")) {
				File appendFile = null;
				if (cmdOpts.length != 1) {
					printUsage();
					System.exit(-1);
				}
				appendFile = new File(cmdOpts[0]);
				try {
					AudioSystem.write(ae.append(AudioSystem
							.getAudioInputStream(appendFile)), AudioSystem
							.getAudioFileFormat(inFile).getType(), outFile);
				} catch (Exception e) {
					System.err.println(e.getLocalizedMessage());
				}
			} else {
				System.err.println("Unknown command: " + cmd);
				printUsage();
				System.exit(-1);
			}

		}

	}

}
