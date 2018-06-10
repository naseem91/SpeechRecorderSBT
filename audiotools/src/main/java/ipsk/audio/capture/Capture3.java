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

package ipsk.audio.capture;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioPluginException;
import ipsk.audio.ajs.AJSAudioSystem;
import ipsk.audio.capture.event.CaptureCloseEvent;
import ipsk.audio.capture.event.CaptureErrorEvent;
import ipsk.audio.capture.event.CaptureEvent;
import ipsk.audio.capture.event.CaptureOpenEvent;
import ipsk.audio.capture.event.CaptureRecordedEvent;
import ipsk.audio.capture.event.CaptureRecordingFileTransitEvent;
import ipsk.audio.capture.event.CaptureStartCaptureEvent;
import ipsk.audio.capture.event.CaptureStartRecordEvent;
import ipsk.audio.capture.event.CaptureStopEvent;
import ipsk.audio.capture.event.CaptureStoppedEvent;
import ipsk.audio.capture.session.info.RecordingFile;
import ipsk.audio.capture.session.info.RecordingSegment;
import ipsk.audio.capture.session.info.RecordingSequence;
import ipsk.audio.capture.session.info.RecordingSession;
import ipsk.audio.dsp.LevelInfo;
import ipsk.audio.dsp.LevelMeasureAudioInputStream;
import ipsk.audio.io.InterceptorAudioInputStream;
import ipsk.audio.io.push.AudioOutputStream;
import ipsk.audio.io.push.IAudioOutputStream;
import ipsk.audio.plugins.ChannelRoutingPlugin;
import ipsk.audio.tools.FrameUnitParser;
import ipsk.audio.utils.AudioFormatUtils;
import ipsk.io.ChannelRouting;
import ipsk.io.InterleavedChannelRoutingInputStream;
import ipsk.util.EventQuequeListener;
import ipsk.util.optionparser.Option;
import ipsk.util.optionparser.OptionParser;
import ipsk.util.optionparser.OptionParserException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.Timer;
import javax.xml.bind.JAXB;

/**
 * Audio capture engine.
 * 
 * @author klausj
 */
public class Capture3 implements Runnable, EventQuequeListener, TargetDataLineListener, ActionListener, LineListener {

	private final static boolean DEBUG = false;
	
	private static final boolean THREAD_START_ON_OPEN=true;
//	private static final int THREAD_PRIORITY=Thread.MAX_PRIORITY;
	private static final int THREAD_PRIORITY=Thread.MAX_PRIORITY;
	private static final int IDLE_WAIT_MILLISECONDS=1;
//	private static final int IDLE_WAIT_MILLISECONDS=100;
	private static final Float STANDARD_MAX_LINE_BUFFER_SIZE_MILLIS = (float)1000;
	
	private static String[] LARGE_BUFFER_SIZE_CAPABLE_LINE_CLASS_NAMES=new String[]{
	    "ipsk.audio.asio.ASIOTargetDataLine","ips.audio.ds.DSTargetDataLine","ips.audio.coreaudio.CoreAudioTargetDataLine"
	};
	
	private final static boolean USE_JS_LINE_FRAME_POSITION =false;

	public class RecordingInfos{
	    private List<RecordingInfo> recordingInfos;
	}
	
	public class RecordingInfo{
	    private long startFrame;
        private long frameLength;
	}
	
	public class RecordingFileSequenceInfo extends RecordingInfo{
	    private List<RecordingFileInfo> recordingSequence;
	}
	
	public class RecordingFileInfo extends RecordingInfo{
	    private String filename;
	    
	}
	
	public enum State {CLOSE,CLOSING,STOPPING,STOP,CAPTURE,RECORD,TARGET_CHANGE,PAUSE,ERROR};
	
	
	public static AudioFileFormat.Type DEF_AUDIO_FILE_TYPE = AudioFileFormat.Type.WAVE;

	public static AudioFormat DEF_AUDIO_FORMAT = new AudioFormat(44100, 16, 2,
			true, false);

	public static AudioFileFormat DEF_AUDIO_FILE_FORMAT = new AudioFileFormat(
			AudioFileFormat.Type.WAVE, DEF_AUDIO_FORMAT,
			AudioSystem.NOT_SPECIFIED);

	// private final static int DEF_PREFERRED_BUFFER_SIZE = 2048;
	private final static int DEF_PREFERRED_BUFFER_SIZE = 8092;

	private static final int LINE_ACTIVE_TIMEOUT_MS = 4000;

	private TargetDataLine line;

	private Mixer device;

//	private AudioInputStream tdlAis;

	private InterceptorAudioInputStream ais;

	private TargetDataLineInputStream2 tdlis;
	
	private Timer lineActiveTimeoutTimer=null;
	
	private long startTimeMs=-1;

	private File recFile;
	private String recId;

	private AudioFileFormat.Type fileType;

	private long maxFrameLength;

	private Thread thread;

	private volatile boolean running;

	private AudioFormat format;
	
	private AudioFormat captureFormat;
	
	private CaptureException formatException=null;
	
	private int frameSize;

	private DataLine.Info lineInfo;

	private Object streamNotify;

	private volatile State status;

	private Vector<CaptureListener> listeners;

	// private EventQueque eventQueque;

	//private boolean measureLevel = true;

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

//	private LevelInfo[] levelInfos;
	
	private Float preferredLineBufferSizeMillis;

	//private float byteRate;
    
    private boolean useAWTEventThread=true;
    
    private boolean forceOpening=false;
    
//    private boolean useTempFile=false;
    
    private PrimaryRecordTarget primaryRecordTarget=null;
  
    private Vector<IAudioOutputStream> outputStreams;
    
	private File tempRecFile;
	
	private volatile boolean writeRecordingInfo=false;
	
	private File recordingSessionInfoFile;
	
	private boolean notifyLineActivation;

    /**
	 * @return the notifyLineActivation
	 */
	public boolean isNotifyLineActivation() {
		return notifyLineActivation;
	}

	/**
	 * @param notifyLineActivation the notifyLineActivation to set
	 */
	public void setNotifyLineActivation(boolean notifyLineActivation) {
		this.notifyLineActivation = notifyLineActivation;
	}

	private volatile RecordingSession recordingSessionInfo;
    private volatile RecordingSequence currentRecordingSequence;

    private long sequenceFramePos;
    
    private ChannelRouting channelAssignment=null;

//	private Integer minInputChannelCount=null;
   
	private ChannelRoutingPlugin channelRouterPlugin=null;
    
    
	public boolean isUseTempFile() {
		return PrimaryRecordTarget.TEMP_RAW_FILE.equals(primaryRecordTarget);
	}

//	public void setUseTempFile(boolean useTempFile) {
//		if(!useTempFile){
//		    if(PrimaryRecordTarget.TEMP_RAW_FILE.equals(primaryRecordTarget)){
//		        primaryRecordTarget=null;
//		    }
//		}else{
//		    primaryRecordTarget=PrimaryRecordTarget.TEMP_RAW_FILE;
//		}
//	}

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
	public Capture3() {
		this(null);
	}

    /**
     * Create capture engine which will capture audio data from given device.
     * @param device audio device to use
     */
	public Capture3(Mixer device) {
		this.device = device;
		preferredBufferSize = DEF_PREFERRED_BUFFER_SIZE;

		streamNotify = new Object();
		listeners = new Vector<CaptureListener>();
		outputStreams=new Vector<IAudioOutputStream>();
		setAudioFileFormat(DEF_AUDIO_FILE_FORMAT);
//		resetPeakHold();
		//stopFramePosition = frameLength;
		streamPosOffset = 0;
		status = State.CLOSE;
		captureOnly = false;
		largeBufferSizeCapableLineClassNames=new HashSet<String>(Arrays.asList(LARGE_BUFFER_SIZE_CAPABLE_LINE_CLASS_NAMES));
	}
    /**
     * Create capture engine.
     * @param device audio device to use
     * @param recFile recordingfile 
     * @param aff audio file format
     */
	public Capture3(Mixer device, File recFile, AudioFileFormat aff) {
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
//		if(levelInfos!=null){
//			if(levelInfos.length!=channels){
//				levelInfos=null;
//			}
//		}
		updateCaptureFormat();
	}

    /**
     * Get audio format.
     * @return audio format
     */
	public AudioFormat getAudioFormat() {
		return format;
	}

    /**
     * Get minimum line buffer size.
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
	
	private void updateCaptureFormat(){
		// methods setAudioFormat and setChannelRouting do not throw exceptions
		// we have to store the format exception for later (open() method)
		formatException=null;
		
		captureFormat = format;
		if(channelAssignment!=null ){
        	if(channelRouterPlugin==null){
        		channelRouterPlugin=new ChannelRoutingPlugin();
        	}
        	channelRouterPlugin.setChannelRouting(channelAssignment);
//        	channelRouterPlugin.setMinInputChannelCount(minInputChannelCount);
			try {
				// reset input format
				channelRouterPlugin.setInputFormat(null);
				// set requested format
				channelRouterPlugin.setOutputFormat(format);
				// get input format of channel which is the capture format coming from the audio device
				captureFormat=channelRouterPlugin.getInputFormat();
				
			} catch (AudioFormatNotSupportedException e) {
				captureFormat=null;
				lineInfo=null;
				formatException=new CaptureException("Cannot set audio format for channel routing",e);
				return;
			}
		}else{
			channelRouterPlugin=null;
		}
		lineInfo=new DataLine.Info(TargetDataLine.class, captureFormat);
	}

    /**
     * Open audio line and engine.
     * @throws CaptureException
     */
	public void open() throws CaptureException {
	    
		if (!State.CLOSE.equals(status))
			return;
//		resetPeakHold();
		// make sure buffer size is multiple of frame size
		int bufferFrames = preferredBufferSize / frameSize;
		bufferSize = bufferFrames * frameSize;
		
		if (line == null) {

			if (device == null) {
				try {
					line = (TargetDataLine) AJSAudioSystem.getLine(lineInfo);

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
				requestedLinebufferSize=AudioFormatUtils.pcmSizeInBytesFromLength(captureFormat, preferredLimitedLineBufferSizeMillis/1000);	
			}
			if (requestedLinebufferSize != null) {

			    float requestedLineBufferSizeInMillis=AudioFormatUtils.pcmLengthFromByteLength(captureFormat, requestedLinebufferSize)*1000;
			    // limit to one second for standard JavaSound implementation
                if(requestedLineBufferSizeInMillis> STANDARD_MAX_LINE_BUFFER_SIZE_MILLIS){
                    Class<?> lineClass=line.getClass();
                    String lineClassName=lineClass.getName();
                    if(!largeBufferSizeCapableLineClassNames.contains(lineClassName)){
                        requestedLinebufferSize=AudioFormatUtils.pcmSizeInBytesFromLength(captureFormat,STANDARD_MAX_LINE_BUFFER_SIZE_MILLIS/1000);
                    }      
				}
				line.open(captureFormat, requestedLinebufferSize);
			} else {
				line.open(captureFormat);
			}
			line.addLineListener(this);
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
		tdlis = new TargetDataLineInputStream2(line);
		if(notifyLineActivation){
			tdlis.setListener(this);
		}
		float linebufferSizeSeconds;
        try {
            linebufferSizeSeconds = AudioFormatUtils.pcmLengthFromByteLength(captureFormat, lineBufferSize);
            if(linebufferSizeSeconds>=4.0){
                // increase buffer reserve
                tdlis.setMaxBufferFill(0.66);
            }
        } catch (AudioFormatNotSupportedException e1) {
           // ignore
        }

        InputStream routedIs=tdlis;
        
        // insert channel routing plugin to pick or route channels from capture stream 
        if(channelRouterPlugin!=null){
        	AudioInputStream captureAis=new AudioInputStream(tdlis, captureFormat, AudioSystem.NOT_SPECIFIED);
        	try {
				routedIs=channelRouterPlugin.getAudioInputStream(captureAis);
			} catch (AudioPluginException e) {
				new CaptureException("Could not apply channel routing plugin: ", e);
			}
        }
		ais=new InterceptorAudioInputStream(routedIs, format,AudioSystem.NOT_SPECIFIED);
		synchronized (outputStreams) {
		    for(IAudioOutputStream aos:outputStreams){
		        try {
                    ais.addAudioOutputStream(aos);
                } catch (AudioFormatNotSupportedException e) {
                   new CaptureException("Capture listener stream: ", e);
                }
		    }
		}
		// if (System.getProperty("debug.sinustest") != null){
		// System.out.println("Capture buffersize: "+line.getBufferSize());
		// measureLevel=false;
		// }
//		if (measureLevel) {
//			try {
//				LevelMeasureAudioInputStream lmais = new LevelMeasureAudioInputStream(tdlAis,levelInfos);
//				levelInfos=lmais.getLevelInfos();
//				ais=lmais;
//			} catch (AudioFormatNotSupportedException e) {
//				throw new CaptureException(e);
//			}
//		} else {
//			ais = tdlAis;
//		}
		
		if(isUseTempFile()){
			try {
				tempRecFile=File.createTempFile(getClass().getName(),".raw");
				tempRecFile.deleteOnExit();
			} catch (IOException e) {
				throw new CaptureException(e);
			}
		}
		synchronized(streamNotify){
		    running = true;
		    status = State.STOP;
		    syncPosition();
		}

		
		if (System.getProperty("debug.sinustest") != null) {
			DataLine.Info lInfo = (DataLine.Info) line.getLineInfo();
//			System.out.println("Capture max buffersize: "
//					+ lInfo.getMaxBufferSize());
			// System.out.println("Capture buffersize: "+line.getBufferSize());
		}
		//System.out.println("Capture buffersize: " + line.getBufferSize());
		if (THREAD_START_ON_OPEN && thread == null) {
            thread = new Thread(this, "Audio-Capture");
            //thread.setPriority(Thread.NORM_PRIORITY + 1);
            thread.setPriority(THREAD_PRIORITY);
            thread.start();
        }
		if(writeRecordingInfo){
		    if(recordingSessionInfoFile!=null && recordingSessionInfoFile.exists()){
		        recordingSessionInfo = JAXB.unmarshal(recordingSessionInfoFile, RecordingSession.class);
		    }else{
		        recordingSessionInfo = new RecordingSession();
		    }
		   
		    currentRecordingSequence=new RecordingSequence();
		    sequenceFramePos=0;
		    currentRecordingSequence.setStartFrame(sequenceFramePos);
		}
		if(notifyLineActivation && lineActiveTimeoutTimer==null){
			lineActiveTimeoutTimer=new Timer(LINE_ACTIVE_TIMEOUT_MS,this);
			lineActiveTimeoutTimer.setRepeats(false);
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
	
	private boolean isRunning(){
	    return (State.RECORD.equals(status) || State.CAPTURE.equals(status) || State.TARGET_CHANGE.equals(status));
	}

    /**
     * Start capturing or recording.
     *
     */
	public void start() {
	    synchronized(streamNotify){
	        
	        if (!isRunning()) {
	        	try {
                    tdlis.resetStream();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
	        	
	        	syncPosition();
	            line.start();
	            startTimeMs=System.currentTimeMillis();
	            if (captureOnly) {  
	                status = State.CAPTURE;
	            } else {
	                status = State.RECORD;
	            }
	            if (!THREAD_START_ON_OPEN && thread == null) {
	                thread = new Thread(this, "Audio-Capture");
	                //thread.setPriority(Thread.NORM_PRIORITY + 1);
	                thread.setPriority(THREAD_PRIORITY);
	                thread.start();
	            }
	            streamNotify.notifyAll();
	            if(notifyLineActivation && lineActiveTimeoutTimer!=null){
	            	lineActiveTimeoutTimer.start();
	            }
	            if (State.CAPTURE.equals(status)) {
	                sendEventAndWait(new CaptureStartCaptureEvent(this));
	            } else {
	                sendEventAndWait(new CaptureStartRecordEvent(this));
	            }
	            
	        }
	    }
	}

    /**
     * Get maxim recording length in frames.
     * @return max length in frames
     */
	public long getMaxFrameLength() {
		return maxFrameLength;
	}

  

   
    /**
     * Stop engine.
     * @throws CaptureException
     */
	public void stop() throws CaptureException {
	    synchronized(streamNotify){
	        if (! State.STOPPING.equals(status) && ! State.STOP.equals(status) && ! State.CLOSE.equals(status)) {
	            State oldStatus = status;
	            status = State.STOPPING;

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
	            status = State.STOPPING;
	            if (State.CAPTURE.equals(oldStatus))
	                sendEventAndWait(new CaptureStoppedEvent(this));
	            // }
	        }
	    }
	}

	/**
     * Close engine.
     * @throws CaptureException
     */
	public void close() throws CaptureException {
		_close();
		if(line!=null){
			line.removeLineListener(this);
		}
	}
	
    /**
     * Close engine.
     * @throws CaptureException
     */
	public void _close() throws CaptureException {
	    synchronized(streamNotify){
	        if (State.CLOSE.equals(status) || State.CLOSING.equals(status))
	            return;
	        stop();
	        running = false;
	        streamNotify.notifyAll();
	    }
	    status=State.CLOSING;
		if (thread != null) {
			try {
				thread.join(5000);
			} catch (InterruptedException e) {
				// OK
			}
			thread = null;
		}
		if(lineActiveTimeoutTimer!=null){
			lineActiveTimeoutTimer.stop();
			lineActiveTimeoutTimer=null;
		}
		synchronized (streamNotify) {
		try {
			if (ais != null)
				ais.close();
		} catch (IOException e) {
			throw new CaptureException(e);
		} finally {
//			try {
//				if (tdlAis != null)
//					tdlAis.close();
//			} catch (IOException e) {
//				throw new CaptureException(e);
//			} finally {
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
					if(writeRecordingInfo){
					    currentRecordingSequence.setFrameLength(sequenceFramePos);
					    recordingSessionInfo.getRecordingSequenceList().add(currentRecordingSequence);
					    JAXB.marshal(recordingSessionInfo, recordingSessionInfoFile);
					}
					
					status = State.CLOSE;
					sendEventAndWait(new CaptureCloseEvent(this));
					// sendEvent(new CaptureCloseEvent(this));
				//}
//			}
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
			return tdlis.getFramePosition();
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
			while (running && ! State.RECORD.equals(status) && ! State.CAPTURE.equals(status)) {
//				synchronized (streamNotify) {
//					streamNotify.notifyAll();
//				}
				synchronized (streamNotify) {
					try {
						streamNotify.wait(IDLE_WAIT_MILLISECONDS);
					} catch (InterruptedException e1) {
						// OK
					}
				}
			}
//			synchronized (streamNotify) {
//				streamNotify.notifyAll();
//			}
			Exception exception=null;
			while (State.CAPTURE.equals(status)) {
				
				try {
					int read=ais.read(buffer);
					if(DEBUG) System.out.println("Dummy capture read "+read+" bytes");
					if(read==0){
					    try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                           // OK
                        }
					}
					if(read<0){
						// when line is stopped
						if(DEBUG) System.err.println("Capture stream terminated!");
						break;
					}
				} catch (IOException e) {
					System.err.println(e.getLocalizedMessage());
					sendEvent(new CaptureErrorEvent(this, e));
					return;
				}
			}
			if (State.RECORD.equals(status)) {
				syncPosition();
				tdlis.resetStream();
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
//					running=false;
					if (! State.CAPTURE.equals(status) && !State.STOPPING.equals(status) && !State.STOP.equals(status) && ! State.CLOSE.equals(status)) {
						status = State.STOPPING;
						if (line != null){
							line.stop();
						}
						status = State.STOP;
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
						CaptureHelperThread captureHelperThread=null;
						try {
							tmpIs = new FileInputStream(tempRecFile);

							int frameSize=format.getFrameSize();
							long fileLength=tempRecFile.length();
							long frameLength=fileLength/frameSize;
							
							if(State.CAPTURE.equals(status)){
							   
							    // we need to read from the line to avoid buffer overrun on the line
							    // but this thread is busy writing the final rec file 
							    captureHelperThread=new CaptureHelperThread(ais, bufferSize);
							    captureHelperThread.start();
							}
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
						if(captureHelperThread!=null){
						    
						    captureHelperThread.close();
						}
						if(exception!=null){
							sendEvent(new CaptureErrorEvent(this, exception));
							System.err.println(exception.getLocalizedMessage());
							return;
						}
					}
					
				}else{
				    // direct recording to file (no temp file)
				    long startFramePos=tdlis.getFramePosition();
				    RecordingFile rfInfo=null;
				    RecordingSegment rSeg=null;
					try {
					    
					    File rrecFile;
					    String rrecId;
					    synchronized(streamNotify){
					        rrecFile=recFile;
					        rrecId=recId;
					        if(writeRecordingInfo && currentRecordingSequence!=null){
					            if(rrecFile.exists()){
					                // we will overwrite so remove it from info
					                recordingSessionInfo.removeRecordingFile(rrecFile);
					            }
		                        rfInfo=new RecordingFile();
		                        rfInfo.setFile(new File(rrecFile.getName()));
		                        rSeg=new RecordingSegment();
		                        rSeg.setStartFrame(startFramePos);
		                        rSeg.setId(recId);
		                        rSeg.setStartTime(new Date());
//		                        rfInfo.setStartFrame(startFramePos);
		                    }
					       
					    }
						AudioSystem.write(ais, fileType, rrecFile);
						if(rfInfo!=null){
						    sequenceFramePos = tdlis.getFramePosition();
                            rSeg.setFrameLength(sequenceFramePos-startFramePos);
                            rfInfo.setLastModified(new Date(rrecFile.lastModified()));
                            rfInfo.getRecordingSegmentList().add(rSeg);
                            if(rrecId!=null){
                                rSeg.setId(rrecId);
                            }
                            currentRecordingSequence.getRecordingFileList().add(rfInfo);
                            
                        }
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
					    if(State.TARGET_CHANGE.equals(status)){
					        ais.setInterrupted(false);
					        
					    }else{
//					        running=false;
					        if (! State.CAPTURE.equals(status) && !State.STOPPING.equals(status) && ! State.STOP.equals(status) && ! State.CLOSE.equals(status)) {
					            status = State.STOPPING;
					            if (line != null){
					                line.stop();
					            }
					            status=State.STOP;
					        }
					    }
					}
				}
				if(State.TARGET_CHANGE.equals(status)){
                    sendEvent(new CaptureRecordingFileTransitEvent(this));
                    status=State.RECORD;
                }else{
                    if(DEBUG)System.out.println("Status: "+status);
                    sendEvent(new CaptureRecordedEvent(this));
                }
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
     * @param pl capture listener
     */
	public void addCaptureListener(CaptureListener pl) {
	    synchronized (listeners) {
	        if (pl != null && !listeners.contains(pl)) {
	            listeners.addElement(pl);
	        }
        }
		
	}

    /**
     * Remove capture listener.
     * @param pl capture listener
     */
	public void removeCaptureListener(CaptureListener pl) {
	    synchronized(listeners){
	        if (pl != null) {
	            listeners.removeElement(pl);
	        }
	    }
	}
	
	
	 /**
     * Add audio output stream.
     * @param aos audio output stream
     */
    public void addAudioOutputStream(IAudioOutputStream aos) {
        synchronized (outputStreams) {
            if (aos != null && ! outputStreams.contains(aos)) {
                outputStreams.addElement(aos);
                
            }
        }
        
    }

    /**
     * Remove audio output stream
     * @param aos audio output stream
     */
    public void removeAudioOutputStream(IAudioOutputStream aos) {
        synchronized(outputStreams){
            if (aos != null) {
                outputStreams.removeElement(aos);
                if(ais!=null){
                  
                    ais.removeOutputStream(aos);
                }
            }
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

//	/**
//     * Enable audio level measuring.
//     * Default is true.
//	 * @param b true to enable measuring
//	 */
//	public void setMeasureLevel(boolean b) {
//		measureLevel = b;
//
//	}

//	/**
//     * Get current audio levels.
//     * Returns the maximum level of the current audio buffer.
//	 * @return current audio levels.
//	 */
//	public LevelInfo[] getLevelInfos() {
//		if (ais != null && ais instanceof LevelMeasureAudioInputStream) {
//			return ((LevelMeasureAudioInputStream) ais).getLevelInfos();
//		} else {
//			return levelInfos;
//		}
//	}

	/**
     * Returns true if recording.
	 * @return true if recording
	 */
	public boolean isRecording() {
		return (State.RECORD.equals(status));
	}

	/**
     * Return true if capturing.
	 * @return true if capturing
	 */
	public boolean isCapturing() {
		return (State.CAPTURE.equals(status));
	}

	/**
     * Set recording file.
	 * @param file recording file
	 */
	public void setRecordingFile(File file) {
	    synchronized (streamNotify) {
	        recFile = file;
	        if(State.RECORD.equals(status)){
	            if(!PrimaryRecordTarget.DIRECT.equals(primaryRecordTarget)){
	                throw new IllegalArgumentException("Change recording file during capture is only allowed for direct recording files (not temporary files)");
	            }
	            status=State.TARGET_CHANGE;
	            ais.setInterrupted(true);
	        }
        }
		
		
	}
	
	
	/**
     * Set recording file and ID.
     * @param file recording file
     * @param recId recording ID
     */
    public void setRecordingItem(File file,String recId) {
        synchronized (streamNotify) {
            this.recId=recId;
            recFile = file;
            if(State.RECORD.equals(status)){
                status=State.TARGET_CHANGE;
                ais.setInterrupted(true);
            }
        }
        
        
    }

	/**
     * Get recording file
	 * @return recording file
	 */
	public File getRecordingFile() {
		return recFile;
	}
	
	
//	public void changeRecordingFile(){
//	    if(!PrimaryRecordTarget.DIRECT.equals(primaryRecordTarget)){
//	        throw new IllegalArgumentException("Change recording file during capture is only allowed for direct recording files (not temporary files)");
//	    }
//	    synchronized(streamNotify){
//	        if(State.RECORD.equals(status) && ais!=null && !ais.isInterrupted()){
//	            status=State.TARGET_CHANGE;
//	            ais.setInterrupted(true);
//	            
//	        }
//	    }
//	}

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
		if (State.CAPTURE.equals(status) && !captureOnly) {

			status = State.RECORD;
			sendEventAndWait(new CaptureStartRecordEvent(this));
		} else if (State.RECORD.equals(status) && captureOnly) {
			status = State.CAPTURE;
			tdlis.flushAndCloseStream();
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

//    /**
//     * Get the maximum peak levels of each channel since the line is open.
//     * @return array of max peak levels hold
//     */
//	public float[] getPeakLevelHold() {
//		if (ais != null && ais instanceof LevelMeasureAudioInputStream) {
//			return ((LevelMeasureAudioInputStream) ais).getPeakLevelHold();
//		} else
//			return null;
//	}

//    /**
//     * Reset the hold peak level maximum.
//     *
//     */
//	public void resetPeakHold() {
////		if (ais != null && ais instanceof LevelMeasureAudioInputStream) {
////			((LevelMeasureAudioInputStream) ais).resetPeakHold();
////		}
////		levelInfos = new LevelInfo[channels];
//		if(levelInfos!=null){
//		
//		for (int ch = 0; ch < channels; ch++) {
//			//zeroLevelInfos[ch] = new LevelInfo();
//			levelInfos[ch].setPeakLevelHold(0);
//		}
//		}
//	}

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
        return (! State.CLOSE.equals(status));
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
                .println("Audio recorder version "+Capture3.class.getPackage().getImplementationVersion()+"\n"
                        +"Records audio from standard audio input device to recordingfilename.\n"
                        +"Usage: java "+Capture3.class.getName()+" [-length recordingtime] recordingfilename\n"
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
                        + "java "+Capture3.class.getName()+" record.wav\n"
                        + "Records to file record.wav. Recording can be stopped by Ctrl-C.\n"
                        + "java "+Capture3.class.getName()+" -length 44100 record.wav\n"
                        + "Records 44100 frames (samples) to file record.wav\n"
                        + "java "+Capture3.class.getName()+" -v -overwrite -length 1s record.wav\n"
                        + "Records 1 second to file record.wav, overrides record.wav if it exists and shows verbose messages\n");
    }
    
    private static class Shutdown extends Thread{
       private  Capture3 c;
       
       private boolean verbose;
        public Shutdown(Capture3 c,boolean verbose){
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

    public File getRecordingSessionInfoFile() {
        return recordingSessionInfoFile;
    }

    public void setRecordingSessionInfoFile(File recordingSessionInfoFile) {
        this.recordingSessionInfoFile = recordingSessionInfoFile;
    }

    public boolean isWriteRecordingInfo() {
        return writeRecordingInfo;
    }

    public void setWriteRecordingInfo(boolean writeRecordingInfo) {
        this.writeRecordingInfo = writeRecordingInfo;
    }

    public String getRecId() {
        return recId;
    }

    public void setRecId(String recId) {
        this.recId = recId;
    }

    /**
     * Get channel routing
	 * @return current applied channel routing
	 */
	public ChannelRouting getChannelRouting() {
		return channelAssignment;
	}

	/**
	 * Set channel routing
	 * @param channelRouting channel routing to set
	 */
	public void setChannelRouting(ChannelRouting channelRouting) {
		this.channelAssignment = channelRouting;
		updateCaptureFormat();
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
        Capture3 c = new Capture3();
        c.setUseAWTEventThread(false);
//        c.setMeasureLevel(false);
        
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
        if(verbose)System.out.println("Audio recorder version "+Capture3.class.getPackage().getImplementationVersion()+"\n");
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

	/* (non-Javadoc)
	 * @see ipsk.audio.capture.TargetDataLineListener#update(ipsk.audio.capture.TargetDataLineEvent)
	 */
	@Override
	public void update(CaptureEvent targetDataLineEvent) {
		lineActiveTimeoutTimer.stop();
//		System.out.println("Recieved TDL active event: "+(System.currentTimeMillis()-startTimeMs));
		sendEvent(targetDataLineEvent);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src=e.getSource();
		if(src==lineActiveTimeoutTimer){
			
			LineActivationTimeoutException ex=new LineActivationTimeoutException("Waited "+LINE_ACTIVE_TIMEOUT_MS+" for activation of audio line!");
			CaptureErrorEvent ce=new CaptureErrorEvent(this,ex);
			sendEvent(ce);
		}
	}

	/* (non-Javadoc)
	 * @see javax.sound.sampled.LineListener#update(javax.sound.sampled.LineEvent)
	 */
	@Override
	public void update(LineEvent event) {
		LineEvent.Type type=event.getType();
		
		 if (LineEvent.Type.STOP.equals(type)) {
			 boolean stoppable=State.STOPPING.compareTo(status)<0; // not (CLOSE,CLOSING,STOP,STOPPING)
			 if(stoppable){
			 try {
				stop();
			} catch (CaptureException e) {
				// cannot handle this
				e.printStackTrace();
			}
			 }
		 }else if(LineEvent.Type.CLOSE.equals(type)){
			 boolean closeable=State.CLOSING.compareTo(status)<0; // not (CLOSE,CLOSING)
			 if(closeable){
				 try {
					_close();
				} catch (CaptureException e) {
					// cannot handle this
					e.printStackTrace();
				}
				 if(line!=null){
					 line.removeLineListener(this);
				 }
			 }
		 }
		 
	}
    
}
