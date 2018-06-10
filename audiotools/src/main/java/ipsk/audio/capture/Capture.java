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
 * Created on 12.08.2005
 *
 */
package ipsk.audio.capture;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.LimitLengthAudioInputStream;
import ipsk.audio.capture.event.CaptureCloseEvent;
import ipsk.audio.capture.event.CaptureErrorEvent;
import ipsk.audio.capture.event.CaptureEvent;
import ipsk.audio.capture.event.CaptureOpenEvent;
import ipsk.audio.capture.event.CaptureRecordedEvent;
import ipsk.audio.capture.event.CaptureStartCaptureEvent;
import ipsk.audio.capture.event.CaptureStartRecordEvent;
import ipsk.audio.capture.event.CaptureStopEvent;
import ipsk.audio.capture.event.CaptureStoppedEvent;
import ipsk.audio.dsp.LevelInfo;
import ipsk.audio.dsp.LevelMeasureAudioInputStream;
import ipsk.audio.tools.FrameUnitParser;
import ipsk.audio.utils.AudioFormatUtils;
import ipsk.util.EventQuequeListener;
import ipsk.util.optionparser.Option;
import ipsk.util.optionparser.OptionParser;
import ipsk.util.optionparser.OptionParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/**
 * Audio capture engine.
 * 
 * @author klausj
 */
public class Capture implements Runnable, EventQuequeListener {

	private final static boolean DEBUG = false;
	
	private static final Float STANDARD_MAX_LINE_BUFFER_SIZE_MILLIS = (float)1000;
	
	private static String[] LARGE_BUFFER_SIZE_CAPABLE_LINE_CLASS_NAMES=new String[]{
	    "ipsk.audio.asio.ASIOTargetDataLine","ips.audio.ds.DSTargetDataLine","ips.audio.coreaudio.CoreAudioTargetDataLine"
	};
	
	private final static boolean USE_JS_LINE_FRAME_POSITION =false;

	public final static int CLOSE = 0;

	public final static int STOP = 1;

	public final static int CAPTURE = 2;

	public final static int RECORD = 3;

	public final static int PAUSE = 4;

	public final static int ERROR = -1;

	public static AudioFileFormat.Type DEF_AUDIO_FILE_TYPE = AudioFileFormat.Type.WAVE;

	public static AudioFormat DEF_AUDIO_FORMAT = new AudioFormat(44100, 16, 2,
			true, false);

	public static AudioFileFormat DEF_AUDIO_FILE_FORMAT = new AudioFileFormat(
			AudioFileFormat.Type.WAVE, DEF_AUDIO_FORMAT,
			AudioSystem.NOT_SPECIFIED);

	// private final static int DEF_PREFERRED_BUFFER_SIZE = 2048;
	private final static int DEF_PREFERRED_BUFFER_SIZE = 8092;

	private TargetDataLine line;

	private Mixer device;

	private AudioInputStream tdlAis;

	private AudioInputStream ais;

	private TargetDataLineInputStream tdlis;

	private File recFile;

	private AudioFileFormat.Type fileType;

	private Long maxFrameLength=null;

	private Thread thread;

	private volatile boolean running;

	private AudioFormat format;

	private int frameSize;

	private DataLine.Info lineInfo;

	private Object streamNotify;

	private volatile int status;

	private Vector<CaptureListener> listeners;

	// private EventQueque eventQueque;

	private boolean measureLevel = true;

	// private boolean useLongForPosition;

	//private long stopFramePosition;

	private volatile long streamPosOffset;

	private volatile long streamFramePosition;

	private boolean captureOnly;

	private int bufferSize;

	private int preferredBufferSize;

	private Integer preferredLineBufferSize = null;

	private Integer lineBufferSize = null;
	
	private HashSet<String> largeBufferSizeCapableLineClassNames;
	
	private int channels;

	private LevelInfo[] levelInfos;
	
	private Float preferredLineBufferSizeMillis;

	//private float byteRate;
    
    private boolean useAWTEventThread=true;
    
    private boolean forceOpening=false;
    
//    private boolean useTempFile=false;
    
    private PrimaryRecordTarget primaryRecordTarget=null;
  

	private File tempRecFile;
    
    
	public boolean isUseTempFile() {
		return PrimaryRecordTarget.TEMP_RAW_FILE.equals(primaryRecordTarget);
	}

	public void setUseTempFile(boolean useTempFile) {
		if(!useTempFile){
		    if(PrimaryRecordTarget.TEMP_RAW_FILE.equals(primaryRecordTarget)){
		        primaryRecordTarget=null;
		    }
		}else{
		    primaryRecordTarget=PrimaryRecordTarget.TEMP_RAW_FILE;
		}
	}

	private class EventSender implements Runnable {
		private final EventObject eo;

		public EventSender(EventObject eo) {
			this.eo = eo;
		}

		public void run() {
			update(eo);
		}
	}

    /**
     * Create capture engine.
     *
     */
	public Capture() {
		this(null);
	}

    /**
     * Create capture engine which will capture audio data from given device.
     * @param device audio device to use
     */
	public Capture(Mixer device) {
		this.device = device;
		preferredBufferSize = DEF_PREFERRED_BUFFER_SIZE;

		streamNotify = new Object();
		listeners = new Vector<CaptureListener>();

		setAudioFileFormat(DEF_AUDIO_FILE_FORMAT);
		resetPeakHold();
		//stopFramePosition = frameLength;
		streamPosOffset = 0;
		status = CLOSE;
		captureOnly = false;
		largeBufferSizeCapableLineClassNames=new HashSet<String>(Arrays.asList(LARGE_BUFFER_SIZE_CAPABLE_LINE_CLASS_NAMES));
	}
    /**
     * Create capture engine.
     * @param device audio device to use
     * @param recFile recordingfile 
     * @param aff audio file format
     */
	public Capture(Mixer device, File recFile, AudioFileFormat aff) {
		this(device);
		this.recFile = recFile;
		setAudioFileFormat(aff);
	}

    /**
     * Set audio file format.
     * @param aff audio file format
     */
	public void setAudioFileFormat(AudioFileFormat aff) {
		this.format = aff.getFormat();
//		this.frameLength = aff.getFrameLength();
		this.fileType = aff.getType();
		setAudioFormat(aff.getFormat());
	}

    /**
     * Get audio file format.
     * @return audio file format
     */
	public AudioFileFormat getAudioFileFormat() {
		return new AudioFileFormat(fileType, format, AudioSystem.NOT_SPECIFIED);
	}

    /**
     * Set audio format.
     * @param af audio format.
     */
	public void setAudioFormat(AudioFormat af) {
		this.format = af;
		frameSize = format.getFrameSize();
		channels = format.getChannels();
		if(levelInfos!=null){
			if(levelInfos.length!=channels){
				levelInfos=null;
			}
		}
		lineInfo = new DataLine.Info(TargetDataLine.class, format);
	}

    /**
     * Get audio format.
     * @return audio format
     */
	public AudioFormat getAudioFormat() {
		return format;
	}

    /**
     * Get minimum line bugffer size.
     * @return minimum line buffer size or null if not available
     */
	public Integer getMinLineBufferSize() {
		if (lineInfo == null)
			return null;
		return lineInfo.getMinBufferSize();
	}

     /**
     * Get maximum line bugffer size.
     * @return maximum line buffer size or null if not available
     */
	public Integer getMaxLineBufferSize() {
		if (lineInfo == null)
			return null;
		return lineInfo.getMaxBufferSize();
	}

    /**
     * Open audio line and engine.
     * @throws CaptureException
     */
	public synchronized void open() throws CaptureException {
		if (status != CLOSE)
			return;
//		resetPeakHold();
		// make sure buffer size is multiple of frame size
		int bufferFrames = preferredBufferSize / frameSize;
		bufferSize = bufferFrames * frameSize;

		if (line == null) {

			if (device == null) {
				try {
					line = (TargetDataLine) AudioSystem.getLine(lineInfo);

				} catch (LineUnavailableException e) {
					if (DEBUG)
						e.printStackTrace();
					throw new CaptureException(
							"Could not get line from audio system: ", e);
				} catch (IllegalArgumentException e) {
					if (DEBUG)
						e.printStackTrace();
					throw new CaptureException(e);
				}
			} else {
				try {

					line = (TargetDataLine) device.getLine(lineInfo);
				} catch (LineUnavailableException e) {
					if (DEBUG)
						e.printStackTrace();
					throw new CaptureException(
							"Could not get line from device: ", e);
				} catch (IllegalArgumentException e) {
					if (DEBUG)
						e.printStackTrace();
					if(forceOpening){
						Line[] lines=device.getTargetLines();
						for(Line l:lines){
							if(l instanceof TargetDataLine){
								line=(TargetDataLine)l;
								break;
							}
						}
						if(line==null){
							throw new CaptureException(e);
						}
					}else{
					throw new CaptureException(e);
					}
				}
			}
		}
		try {
		    Integer requestedLinebufferSize=null;
		    if(preferredLineBufferSize!=null){
		        requestedLinebufferSize=preferredLineBufferSize;
		    }else if (preferredLineBufferSizeMillis != null) {
			    float preferredLimitedLineBufferSizeMillis=preferredLineBufferSizeMillis;
				requestedLinebufferSize=AudioFormatUtils.pcmSizeInBytesFromLength(format, preferredLimitedLineBufferSizeMillis/1000);	
			}
			if (requestedLinebufferSize != null) {

			    float requestedLineBufferSizeInMillis=AudioFormatUtils.pcmLengthFromByteLength(format, requestedLinebufferSize)*1000;
			    // limit to one second for standard JavaSound implementation
                if(requestedLineBufferSizeInMillis> STANDARD_MAX_LINE_BUFFER_SIZE_MILLIS){
                    Class<?> lineClass=line.getClass();
                    String lineClassName=lineClass.getName();
                    if(!largeBufferSizeCapableLineClassNames.contains(lineClassName)){
                        requestedLinebufferSize=AudioFormatUtils.pcmSizeInBytesFromLength(format,STANDARD_MAX_LINE_BUFFER_SIZE_MILLIS/1000);
                    }      
				}
				line.open(format, requestedLinebufferSize);
			} else {
				line.open(format);
			}
			lineBufferSize=line.getBufferSize();
			line.flush();
		} catch (LineUnavailableException e1) {
			if (DEBUG)
				e1.printStackTrace();
			throw new CaptureException("Could not open line: "+e1.getMessage(), e1);
		}catch(Exception e){
		    if (DEBUG)
                e.printStackTrace();
            throw new CaptureException("Could not open line: "+e.getMessage(),e);
		}
		tdlis = new TargetDataLineInputStream(line);
		tdlAis = new AudioInputStream(tdlis, format,AudioSystem.NOT_SPECIFIED);
		// if (System.getProperty("debug.sinustest") != null){
		// System.out.println("Capture buffersize: "+line.getBufferSize());
		// measureLevel=false;
		// }
		ais=tdlAis;
		if(maxFrameLength!=null && maxFrameLength!=AudioSystem.NOT_SPECIFIED){
		    LimitLengthAudioInputStream llais=new LimitLengthAudioInputStream(ais, maxFrameLength);
		    ais=llais;
		}
		if (measureLevel) {
			try {
				LevelMeasureAudioInputStream lmais = new LevelMeasureAudioInputStream(ais,levelInfos);
				levelInfos=lmais.getLevelInfos();
				ais=lmais;
			} catch (AudioFormatNotSupportedException e) {
				throw new CaptureException(e);
			}
		} 
		
		if(isUseTempFile()){
			try {
				tempRecFile=File.createTempFile(getClass().getName(),".raw");
				tempRecFile.deleteOnExit();
			} catch (IOException e) {
				throw new CaptureException(e);
			}
		}
		
		running = true;
		status = STOP;

		syncPosition();
		if (System.getProperty("debug.sinustest") != null) {
			DataLine.Info lInfo = (DataLine.Info) line.getLineInfo();
//			System.out.println("Capture max buffersize: "
//					+ lInfo.getMaxBufferSize());
			// System.out.println("Capture buffersize: "+line.getBufferSize());
		}
		//System.out.println("Capture buffersize: " + line.getBufferSize());
		if (thread == null) {
            thread = new Thread(this, "Audio-Capture");
            //thread.setPriority(Thread.NORM_PRIORITY + 1);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
        }
		sendEventAndWait(new CaptureOpenEvent(this));
         
	}

    /*
     * Send an event and wait until processing finished.
     */
	private void sendEventAndWait(EventObject eo) {

		if (!useAWTEventThread || java.awt.EventQueue.isDispatchThread()) {
			update(eo);
		} else {
			EventSender es = new EventSender(eo);
			try {
				java.awt.EventQueue.invokeAndWait(es);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

    /*
     * Send an event (fire and forget)
     */
	private void sendEvent(EventObject eo) {

		if (!useAWTEventThread || java.awt.EventQueue.isDispatchThread()) {
			update(eo);
		} else {
			EventSender es = new EventSender(eo);
			java.awt.EventQueue.invokeLater(es);

		}

	}

    /**
     * Start capturing or recording.
     *
     */
	public synchronized void start() {
	    line.start();
		if (status != RECORD && status != CAPTURE) {
			if (captureOnly) {
				status = CAPTURE;
			} else {
				status = RECORD;
			}
//			if (thread == null) {
//				thread = new Thread(this, "Audio-Capture");
//				//thread.setPriority(Thread.NORM_PRIORITY + 1);
//				thread.setPriority(Thread.MAX_PRIORITY);
//				thread.start();
//			} else {
				synchronized (streamNotify) {
					streamNotify.notifyAll();
				}
//			}
			if (status == CAPTURE) {
				sendEventAndWait(new CaptureStartCaptureEvent(this));
			} else {
				sendEventAndWait(new CaptureStartRecordEvent(this));
			}
		}
	}

    /**
     * Get maximum recording length in frames.
     * @return length in frames
     */
	public long getMaxFrameLength() {
		return maxFrameLength;
	}

    /**
     * Pause capturing/recording.
     * If already paused the engine will be restarted.
     */
	public synchronized void pause() {
		if (line != null) {
			if (status == RECORD) {
				line.stop();
			} else if (status == STOP) {
				line.start();
			}
		}
	}

   
    /**
     * Stop engine.
     * @throws CaptureException
     */
	public synchronized void stop() throws CaptureException {

		if (status != STOP && status != CLOSE) {
			int oldStatus = status;
			status = STOP;

			// 
			// Linux ALSA impl hangs on read method if line.stop() is called
			// so we close the stream first

			// It was an ALSA misconfiguration error and the workaround seems
			// to crash
			// the ASIO JavaSound adapter
			// try {
			// if (ais != null)
			// ais.close();
			// } catch (IOException e) {
			// throw new CaptureException("Could not close stream", e);
			// } finally {
			tdlis.stop();
			if (line != null){
				line.stop();
				
			}
			if (oldStatus == CAPTURE)
				sendEventAndWait(new CaptureStoppedEvent(this));
			// }
		}
	}

    /**
     * Close engine.
     * @throws CaptureException
     */
	public synchronized void close() throws CaptureException {
		if (status == CLOSE)
			return;
		stop();
		running = false;
		synchronized (streamNotify) {
			streamNotify.notifyAll();
		}
		if (thread != null) {
			try {
				thread.join(5000);
			} catch (InterruptedException e) {
				// OK
			}
			thread = null;
		}

		try {
			if (ais != null)
				ais.close();
		} catch (IOException e) {
			throw new CaptureException(e);
		} finally {
			try {
				if (tdlAis != null)
					tdlAis.close();
			} catch (IOException e) {
				throw new CaptureException(e);
			} finally {
//				try {
//					if (tdlis != null)
//						tdlis.close();
//				} catch (IOException e) {
//					throw new CaptureException(e);
//				} finally {

					if (line != null){
						line.flush();
						line.close();
						if (DEBUG) System.out.println("Line closed");
					}
					line = null;
					if(isUseTempFile()){
						if(tempRecFile!=null){
							tempRecFile.delete();
						}
					}
					status = CLOSE;
					sendEventAndWait(new CaptureCloseEvent(this));
					// sendEvent(new CaptureCloseEvent(this));
				//}
			}
		}

	}

    /**
     * Get already captured frames.
     * @return frames captured/recorded
     */
	public long getFramePosition() {
		if(USE_JS_LINE_FRAME_POSITION){
		if (line == null)
			return 0;
		return line.getLongFramePosition() - streamPosOffset;
		}else{
			if (tdlis == null)
				return 0;
			return tdlis.getFramePosition() - streamPosOffset;
		}

	}

	/*
     * capture engine
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		byte[] buffer = new byte[bufferSize];
		while (running) {
			while (running && status != RECORD && status != CAPTURE) {
//				synchronized (streamNotify) {
//					streamNotify.notifyAll();
//				}
				synchronized (streamNotify) {
					try {
						streamNotify.wait(1);
					} catch (InterruptedException e1) {
						// OK
					}
				}
			}
//			synchronized (streamNotify) {
//				streamNotify.notifyAll();
//			}
			Exception exception=null;
			while (status == CAPTURE) {
				try {
					ais.read(buffer);
				} catch (IOException e) {
					sendEvent(new CaptureErrorEvent(this, e));
				}
			}
			if (status == RECORD) {
				syncPosition();
				if(isUseTempFile()){
					// capture to temporary file in raw format
					FileOutputStream tmpOs=null;
					try {
						tmpOs=new FileOutputStream(tempRecFile);

						int read=0;
						while((read=ais.read(buffer))!=-1){
							tmpOs.write(buffer,0,read);
						}
					} catch (IOException e) {
						exception=e;
					}
					if(tmpOs!=null){
						try {
							tmpOs.close();
						} catch (IOException e1) {	
							tmpOs=null;
							if(exception==null){
								exception=e1;
							}
						}
					}
					running=false;
					if (status != STOP && status != CLOSE) {
						status = STOP;
						if (line != null){
							line.stop();
						}
					}
					// TODO set status processing here
					if(exception!=null){
						sendEvent(new CaptureErrorEvent(this, exception));
						System.err.println(exception.getLocalizedMessage());
						return;
					}

					// Stream the raw data from temporary file to final audio file with header
					if(tmpOs!=null){
						
						FileInputStream tmpIs=null;
						try {
							tmpIs = new FileInputStream(tempRecFile);

							int frameSize=format.getFrameSize();
							long fileLength=tempRecFile.length();
							long frameLength=fileLength/frameSize;

							AudioInputStream tmpAis=new AudioInputStream(tmpIs, format, frameLength);
							AudioSystem.write(tmpAis, fileType, recFile);
						} catch (IOException e) {
							exception=e;
						}
						if(tmpIs!=null){
							try {
								tmpIs.close();
							} catch (IOException e) {
								if(exception==null){
									exception=e;
								}
								
							}
						}
						if(exception!=null){
							sendEvent(new CaptureErrorEvent(this, exception));
							System.err.println(exception.getLocalizedMessage());
							return;
						}
					}
					
				}else{
					try {

						AudioSystem.write(ais, fileType, recFile);
						// Code for debugging: Generates random buffer overrun errors
						// if( Math.random()>0.5){
						// throw new BufferOverrunException("Debug Buffer overrun
						// for testing !!!");
						// }

					} catch (IOException e) {

						// Try to save the part before the buffer overrun
						try{
							File tmp=File.createTempFile("speechrecorder", fileType.getExtension());
							tmp.deleteOnExit();
							AudioInputStream tmpAis=AudioSystem.getAudioInputStream(recFile);
							AudioSystem.write(tmpAis, fileType, tmp);
							tmpAis.close();
							AudioInputStream tmpAis2=AudioSystem.getAudioInputStream(tmp);
							AudioSystem.write(tmpAis2, fileType, recFile);
							tmpAis2.close();
							tmp.delete();
							//System.out.println(recFile+" repaired.");
						}catch(Exception repairException){
							// Delete audio file if repair fails
							recFile.delete();
							//System.out.println(recFile+" deleted.");
						}
						sendEvent(new CaptureErrorEvent(this, e));
						//e.printStackTrace();
						System.err.println(e.getLocalizedMessage());
						return;
					}finally{
						running=false;
						if (status != STOP && status != CLOSE) {
							status = STOP;
							if (line != null){
								line.stop();
							}

						}
					}
				}
				sendEvent(new CaptureRecordedEvent(this));
			}

		}
	}

    /*
     * Synchronize frame position with audio line.
     */
	private void syncPosition() {
		if (line == null) {
			streamPosOffset = 0;
			return;
		}

		streamPosOffset = line.getLongFramePosition() - streamFramePosition;

	}

    /**
     * Add capture listener.
     * @param cl capture listener
     */
	public synchronized void addCaptureListener(CaptureListener cl) {
		if (cl != null && !listeners.contains(cl)) {
			listeners.addElement(cl);
		}
	}

    /**
     * Remove capture listener.
     * @param cl capture listener
     */
	public synchronized void removeCaptureListener(CaptureListener cl) {
		if (cl != null) {
			listeners.removeElement(cl);
		}
	}

//    /**
//     * Get the frame position where cpature/recording will be stopped.
//     * @return
//     */
//	public long getStopFramePosition() {
//		return stopFramePosition;
//	}
//
//    /**
//     * Set frame length of capturing/recording.
//     * @param stopFramePosition
//     */
//	public void setStopFramePosition(long stopFramePosition) {
//		this.stopFramePosition = stopFramePosition;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.util.EventQuequeListener#update(java.util.EventObject)
	 */
	public void update(EventObject eventObject) {
		synchronized (listeners) {
			Iterator<CaptureListener> it = listeners.iterator();
			while (it.hasNext()) {
				CaptureListener listener = it.next();
				listener.update((CaptureEvent) (eventObject));
			}
		}
	}

	/**
     * Enable audio level measuring.
     * Default is true.
	 * @param b true to enable measuring
	 */
	public void setMeasureLevel(boolean b) {
		measureLevel = b;

	}

	/**
     * Get current audio levels.
     * Returns the maximum level of the current audio buffer.
	 * @return current audio levels.
	 */
	public LevelInfo[] getLevelInfos() {
		if (ais != null && ais instanceof LevelMeasureAudioInputStream) {
			return ((LevelMeasureAudioInputStream) ais).getLevelInfos();
		} else {
			return levelInfos;
		}
	}

	/**
     * Returns true if recording.
	 * @return true if recording
	 */
	public boolean isRecording() {
		return (status == RECORD);
	}

	/**
     * Return true if capturing.
	 * @return true if capturing
	 */
	public boolean isCapturing() {
		return (status == CAPTURE);
	}

	/**
     * Set recording file.
	 * @param file recording file
	 */
	public void setRecordingFile(File file) {
		recFile = file;

	}

	/**
     * Get recording file
	 * @return recording file
	 */
	public File getRecordingFile() {
		return recFile;
	}

	/**
     * Set the recording device to capture from.
     * The given device will be used if the capture engine is opened the next time.
	 * @param newCaptureMixer recording device
	 */
	public void setMixer(Mixer newCaptureMixer) {
		device = newCaptureMixer;

	}

    /**
     * Returns true if the engine is only capturing, not recording to file.
     * @return true if capturing
     */
	public boolean isCaptureOnly() {
		return captureOnly;
	}

     /**
     * Set capture only mode.
     * In capture mode no recording file will be written. Capture mode may be used to measure input level without recording.
     */
	public void setCaptureOnly(boolean captureOnly) {
		this.captureOnly = captureOnly;
		if (status == CAPTURE && !captureOnly) {

			status = RECORD;
			sendEventAndWait(new CaptureStartRecordEvent(this));
		} else if (status == RECORD && captureOnly) {
			status = CAPTURE;
			sendEventAndWait(new CaptureStartCaptureEvent(this));
		}
	}

    /**
     * Get preferred buffer size in bytes.
     * @return preferred buffer size or null if default
     */
	public Integer getPreferredBufferSize() {
		return preferredBufferSize;
	}

    /**
     * Set the buffer size of this capture engine in bytes.
     * @param preferredBufferSize
     */
	public void setPreferredBufferSize(int preferredBufferSize) {
		this.preferredBufferSize = preferredBufferSize;
	}

    /**
     * The actually set buffer size.
     * @return buffer size
     */
	public int getBufferSize() {
		return bufferSize;
	}

    /**
     * Get the maximum peak levels of each channel since the line is open.
     * @return array of max peak levels hold
     */
	public float[] getPeakLevelHold() {
		if (ais != null && ais instanceof LevelMeasureAudioInputStream) {
			return ((LevelMeasureAudioInputStream) ais).getPeakLevelHold();
		} else
			return null;
	}

    /**
     * Reset the hold peak level maximum.
     *
     */
	public void resetPeakHold() {
//		if (ais != null && ais instanceof LevelMeasureAudioInputStream) {
//			((LevelMeasureAudioInputStream) ais).resetPeakHold();
//		}
//		levelInfos = new LevelInfo[channels];
		if(levelInfos!=null){
		
		for (int ch = 0; ch < channels; ch++) {
			//zeroLevelInfos[ch] = new LevelInfo();
			levelInfos[ch].setPeakLevelHold(0);
		}
		}
	}

    /**
     * Get the preferred audio line buffer size setting.
     * @return preferred audio line buffer size
     */
	public Integer getPreferredLineBufferSize() {
		return preferredLineBufferSize;
	}

    /**
     * Set the buffer size of the audio line.
     * It is recommended to set this to one second ( @see {@link #setPreferredLineBufferSizeMillis(Float)} setPreferredLineBufferSizeMillis(Float)), which is the maximum line buffer size under Windows XP to avoid buffer overruns (especially during garbage collector runs).
     * The default setting is to open the line with default JavaSound settings (500ms for Windows). 
     * @param preferredLineBufferSize
     */
	public void setPreferredLineBufferSize(Integer preferredLineBufferSize) {
		preferredLineBufferSizeMillis = null;
		this.preferredLineBufferSize = preferredLineBufferSize;
	}
    
	/**
     * Get the last used line buffer size.
     * Returns the last used line buffer size. 
     * @return audio line buffer size or null if capture was not started yet
     */
    public Integer getLineBufferSize() {
        return lineBufferSize;
    }
    
    /**
     * Get the last used line buffer size in seconds.
     * Returns the last used line buffer size. 
     * @return audio line buffer size or null if capture line was not opened yet
     */
    public Float getLineBufferSizeSeconds() {
       Integer lineBufferSize=getLineBufferSize();
       if(lineBufferSize==null || format==null){
           return null;
       }
       
       float framerate=format.getFrameRate();
       if(framerate==AudioSystem.NOT_SPECIFIED){
           return null;
       }
       int frameSize=format.getFrameSize();
       if(frameSize==AudioSystem.NOT_SPECIFIED){
           return null;
       }
       int lineBufferFrames=lineBufferSize /frameSize;
       return (float)lineBufferFrames/framerate;
       
    }
    
    /**
     * Return open status.
     * @return false if capture engine is closed
     */
    public boolean isOpen() {       
        return (status!=CLOSE);
    }

    /**
     * Get preferred line buffer size in milliseconds.
     * @return preferred line buffer size in milliseconds or null if not set.
     */
    public Float getPreferredLineBufferSizeMillis() {
        return preferredLineBufferSizeMillis;
    }

     /**
     * Set the buffer size of the audio line.
     * It is recommended to set this to one second, which is the maximum line buffer size under Windows XP to avoid buffer overruns (especially during garbage collector runs).
     * The default setting is to open the line with default JavSound settings (500ms for Windows). 
     * @param preferredLineBufferSizeMillis preferred line buffer size in milliseconds or null for default
     */
    public void setPreferredLineBufferSizeMillis(
            Float preferredLineBufferSizeMillis) {
        preferredLineBufferSize = null;
        this.preferredLineBufferSizeMillis = preferredLineBufferSizeMillis;
    }

    /**
     * True if generated events will be queued to AWT event queue.
     * @return true if AWT event queue is used
     */
    public boolean isUseAWTEventThread() {
        return useAWTEventThread;
    }
    
    /**
     * Determines if events should be queued to the AWTEventThread.
     * For GUI applications it is recommended to set this to true, to avoid 
     * AWT/Swing deadlocks (Swing is not thread safe). 
     * @param useAWTEventThread
     */
    public void setUseAWTEventThread(boolean useAWTEventThread) {
        this.useAWTEventThread = useAWTEventThread;
    }

    /**
     * Set the maximum length of recording in frames.
     * The capture engine will stop if the amount of frames are recorded.  
     * @param maxFrameLength maximum recording length in frames
     */
    public void setMaxFrameLength(long maxFrameLength) {
        this.maxFrameLength = maxFrameLength;
    }


    private static void printUsage() {
        System.out
                .println("Audio recorder version "+Capture.class.getPackage().getImplementationVersion()+"\n"
                        +"Records audio from standard audio input device to recordingfilename.\n"
                        +"Usage: java "+Capture.class.getName()+" [-length recordingtime] recordingfilename\n"
                        + "Without option the program records until the program terminates (by Ctrl-C)\n"
                        + "Options:\n"
                        + "       -length recordingtime:\n"
                        + "       Stop recording after recordingtime.\n"
                        + "       \n"
                        + "       -overwrite\n"
                        + "       force  overwrite existing recording file.\n"
                        + "\n"
                        + "Note: Recording time value must can be given in audio frames (without unit)\n"
                        + "      in seconds (with unit \"s\") or in milliseconds (unit \"ms\").\n"
                        + "      units must be directly appended to the value (no blank)"
                        + "Examples:\n"
                        + "java "+Capture.class.getName()+" record.wav\n"
                        + "Records to file record.wav. Recording can be stopped by Ctrl-C.\n"
                        + "java "+Capture.class.getName()+" -length 44100 record.wav\n"
                        + "Records 44100 frames (samples) to file record.wav\n"
                        + "java "+Capture.class.getName()+" -v -overwrite -length 1s record.wav\n"
                        + "Records 1 second to file record.wav, overrides record.wav if it exists and shows verbose messages\n");
    }
    
    private static class Shutdown extends Thread{
       private  Capture c;
       
       private boolean verbose;
        public Shutdown(Capture c,boolean verbose){
            this.c=c;
            
            this.verbose=verbose;
        }
        
        public void run() {
          //c.removeCaptureListener(cl);
            if(verbose)System.out.println("Shutdown ...");
//            try {
//               
//                    c.stop();
//                if(verbose)System.out.println("Capture stopped.");
//               
//            } catch (CaptureException e) {
//                System.err.println("Could not stop capture engine:");
//                if(verbose)e.printStackTrace();
//            }
            try {
                if (c.isOpen()){
                    if(verbose)System.out.println("Capture closing...");
                    c.close();
                    
                }
                
            } catch (CaptureException e) {
                System.err.println("Could not close capture engine !");
                if(verbose)e.printStackTrace();
            }
        }
        
    }
    
	public static void main(String[] args) {
       
        OptionParser op = new OptionParser();
        Option overwriteOption=new Option("overwrite");
        Option recTimeOption=new Option("length",null);
        Option verboseOption=new Option("v");
        Option helpOption=new Option("h");
        op.addOption(verboseOption);
        op.addOption(helpOption);
        op.addOption(recTimeOption);
        op.addOption(overwriteOption);
       
        try {
            op.parse(args);
        } catch (OptionParserException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(-1);
        }
       
        if(helpOption.isSet()){
            printUsage();
            System.exit(0);
        }
        final boolean verbose=verboseOption.isSet();
        Capture c = new Capture();
        c.setUseAWTEventThread(false);
        c.setMeasureLevel(false);
        
        String[] params;
        String recordingTime = null;
       
        File recFile=null;
        boolean overwrite=false;
        overwrite=overwriteOption.isSet();
        recordingTime=recTimeOption.getParam();
        
        params = op.getParams();

        if (params.length == 0) {
           printUsage();
           System.exit(-1);
        } else if (params.length == 1) {
            try {
                URL url = new URL(params[0]);
                String urlProto=url.getProtocol();
                if(!urlProto.equalsIgnoreCase("file")){
                    System.err.println("Only file protocol URL's are supported");
                    System.exit(-1); 
                }
               recFile=new File(url.toURI().getPath());
            } catch (MalformedURLException e1) {
                // OK no URL try file now
                    recFile=new File(params[0]);
            } catch (URISyntaxException e) {
            	recFile=new File(params[0]);
            }
        }
        if(verbose)System.out.println("Audio recorder version "+Capture.class.getPackage().getImplementationVersion()+"\n");
        if(!overwrite && recFile.exists()){
            System.err.println("Recording file exists. Exiting.\nUse overwrite option to force overwrite.");
            System.exit(-1);  
        }
       
       
		c.setRecordingFile(recFile);
        CaptureListener cl=null;
        final Shutdown shutDown=new Shutdown(c,verbose);
        cl=new CaptureListener(){

            public void update(CaptureEvent captureEvent) {
              
               if(captureEvent instanceof CaptureStopEvent){
                   if(verbose)System.out.println("Capture stopped.");
               }
                if(captureEvent instanceof CaptureCloseEvent){
                   if(verbose)System.out.println("Capture closed.");
               }
                
            }
            
        };
        c.addCaptureListener(cl);
        
        Runtime.getRuntime().addShutdownHook(shutDown);
        if(recordingTime!=null){
           FrameUnitParser fup=new FrameUnitParser(c.getAudioFormat().getFrameRate());
           long frameLength=fup.parseFrameUnitString(recordingTime);
            c.setMaxFrameLength(frameLength);
            
        }
		try {
			c.open();
             if(verbose)System.out.println("Capture open.");
		} catch (CaptureException e1) {
			System.err.println("Could not open capture engine:");
			e1.printStackTrace();
            System.exit(-1);
		}
        
		c.start();
         if(verbose)System.out.println("Capture started.");
         if(verbose && !recTimeOption.isSet())System.out.println("Press Ctrl-C to stop.");

	}

	public boolean isForceOpening() {
		return forceOpening;
	}

	public void setForceOpening(boolean forceOpening) {
		this.forceOpening = forceOpening;
	}

    public PrimaryRecordTarget getPrimaryRecordTarget() {
        return primaryRecordTarget;
    }

    public void setPrimaryRecordTarget(PrimaryRecordTarget primaryRecordTarget) {
        this.primaryRecordTarget = primaryRecordTarget;
    }

    
}
