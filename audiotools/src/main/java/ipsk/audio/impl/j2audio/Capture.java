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

package ipsk.audio.impl.j2audio;

import ipsk.audio.AudioController2;
import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.dsp.PeakDetector;
import ipsk.io.VectorBufferedInputStream;
import ipsk.io.VectorBufferedOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * Captures/records audio data from audio source lines.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * @deprecated Please use {@link ipsk.audio.capture.Capture} instead.
 *  
 */
public class Capture implements Runnable, LineListener, FileWriterListener {

	private int maxBufferFill;

	private static final boolean DEBUG = false;

	//private static int DEFAULT_BUF_SIZE = 2048;
	private static int DEFAULT_BUF_SIZE_IN_FRAMES = 512;

	private FileWriterThread directFileWriterThread;

	private PipedOutputStream outPipe;

	private PipedInputStream inPipe;

	private TargetDataLine tdl = null;

	//private DataLine.Info tdlInfo = null;
	private int lineBufferSize;

	private int preferredLineBufferSize = AudioSystem.NOT_SPECIFIED;

	private LineStatus tdlStatus = new LineStatus(LineStatus.CLOSED);

	private CaptureStatus cs = new CaptureStatus(CaptureStatus.CLOSED);

	//private float latencyTime = (float) 0.2; //latency time in seconds
	private float latencyTime = AudioSystem.NOT_SPECIFIED;

	private int bufSize;

	private byte[] buffer;

	private File recFile = null;

	private OutputStream outStream;

	private File tmpFile = null;

	//private FileOutputStream fileOut;
	private OutputStream out;

	//private boolean overwrite = false;

	private Thread tdlThread = null;

	private PeakDetector abp = null;

	private AudioFormat format;

	private int numChannels;

	private float levels[] = new float[0];

	private long blength = 0;

	private IOException captureException = null;

	private int framePosition = 0;

	private int frameSize;

	private int frameSizeInBytes;

	private AudioFileFormat.Type audioFileFormat = AudioFileFormat.Type.WAVE;

	private boolean useTempFile;

	private AudioInputStream directFileWriterStream = null;

	private VectorBufferedOutputStream outBuffer = null;

	private CaptureListener cl;

	/**
	 * Creates a capture engine for the given input line
	 * 
	 * @param cl
	 *            caputure listener
	 * @param tdl
	 *            input line
	 */
	public Capture(CaptureListener cl, TargetDataLine tdl) {
		this.cl = cl;
		this.tdl = tdl;
		useTempFile = false; //default has changed !
		if (DEBUG)
			System.out.println("Created capture with Line: "
					+ tdl.getClass().getName() + " " + tdl);
	}

	/**
	 * Force the use of temporary files.
	 * 
	 * @param useTempFile
	 */
	public void setUseTempFile(boolean useTempFile) {
		this.useTempFile = useTempFile;
	}

	/**
	 * Initialize the engine and open the capture lines. NOTE: The latency time
	 * parameter is currently IGNORED !!
	 * 
	 * @param af
	 *            audio format to use
	 * @throws IOException
	 * @throws LineUnavailableException
	 * @throws AudioFormatNotSupportedException
	 */
	public void open(AudioFormat af) throws IOException,
			LineUnavailableException, AudioFormatNotSupportedException {
		format = af;
		numChannels = af.getChannels();
		levels = new float[numChannels];
		resetLevels();

		//tdlInfo = new DataLine.Info(TargetDataLine.class, format);
		frameSize = format.getFrameSize();
		int tdlBufSizeInFrames;
		if (latencyTime != AudioSystem.NOT_SPECIFIED) {
			tdlBufSizeInFrames = (int) (latencyTime * (float) format
					.getSampleRate());
		} else {
			tdlBufSizeInFrames = DEFAULT_BUF_SIZE_IN_FRAMES;
		}
		bufSize = frameSize * tdlBufSizeInFrames;
		latencyTime = (float) tdlBufSizeInFrames / format.getSampleRate();

		if (useTempFile) {
			prepareTmpFile();
			outPipe = null;
		}

		tdlStatus = new LineStatus(LineStatus.CLOSED);

		tdl.addLineListener(this);
		DataLine.Info tdlInfo = ((DataLine.Info) tdl.getLineInfo());
		int maxBufSize = tdlInfo.getMaxBufferSize();
		int minBufSize = tdlInfo.getMinBufferSize();
		if (preferredLineBufferSize == AudioSystem.NOT_SPECIFIED) {
		    if (DEBUG)System.out.println("TDL Opening with :"+format.getSampleRate());
			tdl.open(format);
		} else {
			int lineBufSizeInFrames = preferredLineBufferSize / frameSize;
			lineBufferSize = lineBufSizeInFrames * frameSize;
			if (DEBUG)System.out.println("TDL Opening with :"+format.getSampleRate());
			tdl.open(format, lineBufferSize);
		}
		lineBufferSize = tdl.getBufferSize();
		if (DEBUG)
			System.out.println("Buffer size: max: " + maxBufSize + " min: "
					+ minBufSize + ", set: " + lineBufferSize + " array: "
					+ bufSize);
		if (DEBUG)
			System.out.println("Line: " + tdl.getClass().getName() + " " + tdl);
		tdlStatus.waitFor(LineStatus.OPENED);
		buffer = new byte[bufSize];

		abp = new PeakDetector(af);

		//abp.setPriority(Thread.MIN_PRIORITY);

		framePosition = 0;
		cs.setStatus(CaptureStatus.OPEN);
		cl.update(this, cs);
	}

	//	/**
	//	 * Implements {@link ipsk.audio.AudioBufferProcessorListener}.
	//	 * The method is called when the processor has calculated the level
	//	 * of a buffer.
	//	 */
	//	public void processed() {
	//		Statistic s = abp.getStatistic();
	//		if (DEBUG)
	//			System.out.println("Level: " + s);
	//		if (cs.getStatus() == CaptureStatus.CAPTURING || cs.getStatus() ==
	// CaptureStatus.RECORDING) {
	//			for (int i = 0; i < s.logMaxLevel.length; i++) {
	//				levels[i] = s.linMaxLevel[i];
	//			}
	//		}
	//	}

	/**
	 * Reset stored audio amplitude peak levels.
	 *  
	 */
	private void resetLevels() {
		for (int i = 0; i < numChannels; i++) {
			levels[i] = 0;
		}
	}

	/**
	 * Prepare file to record to.
	 * 
	 * @param recFile
	 *            the file to record to.
	 * @throws IOException
	 */
	public void prepareToRecord(File recFile) throws IOException {
		captureException = null;
		String currCs = cs.getStatus();
		if (currCs == CaptureStatus.OPEN || currCs == CaptureStatus.PREPARED) {
			outStream = null;
			this.recFile = recFile;
			if (!useTempFile) {

				inPipe = new PipedInputStream();
				directFileWriterStream = new AudioInputStream(inPipe, format,
						AudioSystem.NOT_SPECIFIED);
				outPipe = new PipedOutputStream(inPipe);
				//inPipe.connect(outPipe);
				out = (OutputStream) outPipe;
				directFileWriterThread = new FileWriterThread(this,
						directFileWriterStream, AudioFileFormat.Type.WAVE,
						recFile);
				directFileWriterThread.create();
			}
			//abp.start();
			cs.setStatus(CaptureStatus.PREPARED);
			cl.update(this, cs);
		}
	}

	/**
	 * Prepare the stream to which the recording data (including file header)
	 * will be written
	 * 
	 * @param os
	 *            the recording stream
	 * @throws IOException
	 */
	public void prepareToRecord(OutputStream os) throws IOException {
		captureException = null;
		String currCs = cs.getStatus();
		if (currCs == CaptureStatus.OPEN || currCs == CaptureStatus.PREPARED) {
			recFile = null;
			outStream = os;
			if (!useTempFile) {
				outBuffer = new VectorBufferedOutputStream();
				out = (OutputStream) outBuffer;
			}
			//abp.start();
			cs.setStatus(CaptureStatus.PREPARED);
			cl.update(this, cs);
		}
	}

	/**
	 * Start capturing. Capturing does not store any audio data. It is intended
	 * to get peak levels of the incoming audio signal to adjust gain control by
	 * the user.
	 *  
	 */
	public void startCapturing() {
		cs.setStatus(CaptureStatus.CAPTURING);
		//abp.start();
		if (tdlStatus.getStatus() != LineStatus.STARTED) {
			//startTargetDataLine();
			startNewCaptureThread();
			tdl.start();
		}
		cl.update(this, cs);
	}

	/**
	 * Starts recording.
	 *  
	 */
	public synchronized void startRecording() {
		maxBufferFill = 0;
		if (cs.getStatus() == CaptureStatus.PREPARED
				|| cs.getStatus() == CaptureStatus.CAPTURING) {
			blength = 0;
			framePosition = 0;
			cs.setStatus(CaptureStatus.RECORDING);
			if (tdlStatus.getStatus() != LineStatus.STARTED) {
				//			startTargetDataLine();
				startNewCaptureThread();
				tdl.start();
			}
			if (!useTempFile && recFile != null) {
				directFileWriterThread.start();
			}
			cl.update(this, cs);
		}
	}

	/**
	 * Stops recording. This method blocks until the audio line is stopped,
	 * audio data is written and the engine thread has finished.
	 * 
	 * @throws IOException
	 */
	public void stopRecording() throws IOException {
		if (cs.getStatus() == CaptureStatus.RECORDING) {
			cs.setStatus(CaptureStatus.RECORDED);

			cs.waitForNot(CaptureStatus.RECORDED);
			if (DEBUG)
				System.out.println("Capture idle again");
			if (tdlThread != null) {
				try {
					tdlThread.join();
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
			}
			stopTargetDataLine();
			//abp.stop();

			if (useTempFile || outStream != null) {
				InputStream fis = null;
				if (useTempFile) {
					try {
						fis = new FileInputStream(tmpFile);
					} catch (IOException e) {
						prepareTmpFile();
						resetLevels();
						cs.setStatus(CaptureStatus.OPEN);
						System.err.println("Temporary file not found: " + e);
						throw e;
					}
				} else {
					fis = new VectorBufferedInputStream(outBuffer
							.getVectorBuffer());
				}

				AudioInputStream ais = new AudioInputStream(fis, format,
						blength / frameSizeInBytes);
				//long milliseconds = (long) ((ais.getFrameLength() * 1000) /
				// format.getFrameRate());
				AudioInputStream outs = null;

				outs = ais;

				try {

					if (recFile != null) {

						if (AudioSystem.write(ais, audioFileFormat, recFile) == -1) {
							throw new IOException("Problems writing audio file");
						}
					} else if (outStream != null) {
						if (AudioSystem.write(ais, audioFileFormat, outStream) == -1) {
							throw new IOException(
									"Problems writing audio to OutputStream");
						}
					} else {
						throw new IOException(
								"No recording URL or stream to record.");
					}

				} catch (IOException e) {
					System.err.println("Error writing file " + e);
					throw e;
				} finally {
					fis.close();
					ais.close();
					outs.close();
					if (outStream != null)
						outStream.close();
					if (useTempFile)
						prepareTmpFile();
					resetLevels();
					cs.setStatus(CaptureStatus.OPEN);
					try {
						tdlThread.join();
					} catch (InterruptedException e) {
						System.err.println("Recording thread interrupted !");
						e.printStackTrace();
					}

				}

			} else {
				// direct file writing
			}
			if (DEBUG)
				System.out.println("Status:" + cs);
			cs.waitFor(CaptureStatus.OPEN);
			if (DEBUG)
				System.out.println("Capture idle again");
			//resetLevels();

			cl.update(this, cs);
			if (DEBUG)
				System.out.println("Max buffer fill: " + maxBufferFill);
			if (captureException != null) {
				throw captureException;
			}

		}
	}

	/**
	 * Stops capturing.
	 *  
	 */
	public void stopCapturing() {
		try {
			stopTargetDataLine();
		} catch (IOException e) {
			System.err.println("IO Error while stopping capture !");
			e.printStackTrace();
		}
		//abp.stop();

	}

	/**
	 * Get current normalized (0.0 - 1.0) level for each channel.
	 * 
	 * @return array of levels
	 */
	public float[] getLevels() {
		return levels;
	}

	/**
	 * Close the engine. The engine is stopped before.
	 *  
	 */
	public void close() {

		try {
			stopRecording();
		} catch (IOException e) {
			System.err.println("Exception closing capture device:\n");
			e.printStackTrace();
		} finally {
			if (tdl.isOpen()) {
				tdl.close();
			}
			if (tdl != null)
				tdl.removeLineListener(this);
			if (DEBUG)
				System.out.println("Record line closed");
		}
	}

	//	private void startTargetDataLine() {
	//
	//		startNewCaptureThread();
	//		tdl.start();
	//
	//		// causes hangups on some machines
	//		//tdlStatus.waitFor(LineStatus.STARTED);
	//
	//		if (DEBUG)
	//			System.out.println("Tdl started");
	//	}

	private void stopTargetDataLine() throws IOException {
		if (tdlStatus.getStatus() == LineStatus.STARTED) {
			tdl.stop();
		}
		tdl.flush();
	}

	private void prepareTmpFile() throws IOException {
		if (DEBUG)
			System.out.println("Preparing tempfile !");
		if (tmpFile != null)
			tmpFile.delete();
		try {
			tmpFile = File.createTempFile(this.getClass().getName(), null);
			if (tmpFile == null) {
				System.err.println("Cannot get tmpFile");
			}
			tmpFile.deleteOnExit();
			out = new FileOutputStream(tmpFile);
		} catch (IOException e) {
			captureException = e;
			System.err.println("Cannot get tmpFile " + e);
		}

	}

	private void startNewCaptureThread() {
		tdlThread = new Thread(this);
		tdlThread.setPriority(Thread.NORM_PRIORITY + 1);
		tdlThread.setName("Capture");

		tdlThread.start();
	}

	/**
	 * {@link javax.sound.sampled.LineListener}implementation.
	 */

	public synchronized void update(LineEvent le) {
		LineEvent.Type type = le.getType();
		Line src = le.getLine();

		if (src == (Line) tdl) {
			if (type.equals(LineEvent.Type.OPEN)) {
				tdlStatus.setStatus(LineStatus.OPENED);
			} else if (type.equals(LineEvent.Type.CLOSE)) {
				tdlStatus.setStatus(LineStatus.CLOSED);
			} else if (type.equals(LineEvent.Type.START)) {
				tdlStatus.setStatus(LineStatus.STARTED);
			} else if (type.equals(LineEvent.Type.STOP)) {
				tdlStatus.setStatus(LineStatus.OPENED);
			}
		}
	}

	public void run() {
		captureException = null;
		frameSizeInBytes = format.getFrameSize();
		int numBytesRead = 0;

		do {
			int toRead = bufSize;
			int available = tdl.available();
			if (available == tdl.getBufferSize()) {
				System.err.println("Buffer overrun detected !!");
				captureException = new BufferOverrunException();
			}
			if (available > maxBufferFill)
				maxBufferFill = available;
			//System.out.println("Line :"+available+" from "+lineBufferSize);
			numBytesRead = tdl.read(buffer, 0, toRead);

			if (numBytesRead > 0) {
				if (cs.getStatus() == CaptureStatus.RECORDING) {
					try {
						out.write(buffer, 0, numBytesRead);
						framePosition += numBytesRead / frameSize;
						blength = blength + numBytesRead;
					} catch (IOException e) {
						captureException = e;
						System.err
								.println("Unable to write to temp file: " + e);
					}
				}

				//if (abp.setData(buffer, numBytesRead)) {
				abp.process(buffer, 0, numBytesRead);
				levels = abp.getPeakLevels();

				//				} else {
				//					//if (DEBUG)
				//						//System.out.println("Processor busy at loop
				// "+loopCount+".");
				//				}
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// No problem
				}
			}
		} while ((cs.getStatus() == CaptureStatus.RECORDING || cs.getStatus() == CaptureStatus.CAPTURING));

		if (cs.getStatus() == CaptureStatus.RECORDED) {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				captureException = e;
				System.err.println("Cannot flush and close " + e);
			}
			synchronized (cs) {
				if (cs.getStatus() != CaptureStatus.OPEN)
					cs.setStatus(CaptureStatus.SAVING);
			}
		} else {
			cs.setStatus(CaptureStatus.OPEN);
		}
	}

	/**
	 * Get current used audio format.
	 * 
	 * @return audio format
	 */
	public AudioFormat getAudioFormat() {
		return format;
	}

	/**
	 * Gets number of already recorded frames.
	 * 
	 * @return current frame position
	 */
	public long getFramePosition() {
		//return tdl.getFramePosition();
		return framePosition;
	}

	//	public long getMicrosecondPosition() {
	//		return tdl.getMicrosecondPosition();
	//	}

	public void main(String args[]) {
		/*
		 * AudioController ac=null; try { ac=AudioController.openSession(new
		 * AudioFormat(44100,16,2,true,false),true); }catch (Exception e) {
		 * System.err.println("Cannot open fullduplex audio session: "+e);
		 * e.printStackTrace(); System.exit(0); }
		 * 
		 * File recFile=new File("test.wav"); ac.prepareToRecord(recFile);
		 * ac.startRecording(); try { System.in.read(); }catch (Exception e) {
		 * System.err.println("Cannot read: "+e); } ac.stopRecording(); try {
		 * System.in.read(); }catch (Exception e) { System.err.println("Cannot
		 * read: "+e); } try { ac.prepareToPlay(new
		 * URL("file:"+recFile.getName())); }catch (Exception e) {
		 * System.err.println("Cannot prepare: "+e); } ac.play();
		 * 
		 * //removeRawAudiostreamListener(abp); ac.closeSession();
		 * 
		 * System.exit(0);
		 */

	}

	//	/**
	//	 * Sets overwrite option.
	//	 * If set files are overwiritten without throwing an exception.
	//	 * @param b
	//	 */
	//	public void setOverwrite(boolean b) {
	//		overwrite = b;
	//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.impl.j2audio.FileWriterListener#update(java.lang.Object,
	 *      int)
	 */
	public void update(Object source, int event) {
		if (event == FileWriterThread.DONE) {
			synchronized (cs) {
				if (cs.getStatus() == CaptureStatus.RECORDED
						|| cs.getStatus() == CaptureStatus.SAVING) {
					cs.setStatus(CaptureStatus.OPEN);
					cl.update(this, cs);
				}
			}
		} else if (event == FileWriterThread.ERROR) {
			cs.setException(((FileWriterThread) source).getException());
			cs.setStatus(CaptureStatus.ERROR);
			cl.update(this, cs);
		}
	}


	public int getPreferredBufferSize() {
		return preferredLineBufferSize;
	}

	
	public void setPreferredBufferSize(int i) {
		preferredLineBufferSize = i;
	}

	
	public float getLatencyTime() {
		return latencyTime;
	}

	public void setLatencyTime(float f) {
		latencyTime = f;
	}

}
