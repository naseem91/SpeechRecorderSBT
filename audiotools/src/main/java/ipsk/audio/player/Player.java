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
package ipsk.audio.player;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioPluginException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.ConvenienceFileAudioSource;
import ipsk.audio.FileAudioSource;
import ipsk.audio.URLAudioSource;
import ipsk.audio.ajs.AJSAudioSystem;
import ipsk.audio.ajs.AJSDevice;
import ipsk.audio.dsp.BufferLevelInfo;
import ipsk.audio.dsp.BufferLevelInfoArray;
import ipsk.audio.dsp.LevelInfo;
import ipsk.audio.dsp.PeakDetector;
import ipsk.audio.io.InterceptorAudioInputStream;
import ipsk.audio.io.push.IAudioOutputStream;
import ipsk.audio.player.event.PlayerCloseEvent;
import ipsk.audio.player.event.PlayerEndOfMediaEvent;
import ipsk.audio.player.event.PlayerErrorEvent;
import ipsk.audio.player.event.PlayerEvent;
import ipsk.audio.player.event.PlayerOpenEvent;
import ipsk.audio.player.event.PlayerPauseEvent;
import ipsk.audio.player.event.PlayerStartEvent;
import ipsk.audio.player.event.PlayerStopEvent;
import ipsk.audio.player.event.PlayerStoppedEvent;
import ipsk.audio.plugins.ChannelRoutingPlugin;
import ipsk.audio.utils.AudioFormatUtils;
import ipsk.awt.UpdateAWTEventTransferAgent;
import ipsk.io.ChannelRouting;
import ipsk.util.optionparser.Option;
import ipsk.util.optionparser.OptionParser;
import ipsk.util.optionparser.OptionParserException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

/**
 * Audio player.
 * 
 * @author klausj
 */
public class Player implements Runnable, LineListener{

	private final static int DEBUG_LEVEL = 0;

    private enum State {CLOSE,STOP,PLAY,PAUSE,DRAINING,STOPPING,CLOSING,ERROR}

	protected final static int DEF_PREFERRED_BUFFER_SIZE = 2048;
	//protected final static int DEF_PREFERRED_BUFFER_SIZE = 32768;

    private static final Float PREFERRED_LINE_BUFFER_SIZE_MILLIS = (float) 4000;
    private static final Float STANDARD_MAX_LINE_BUFFER_SIZE_MILLIS = (float)1000;

    private static String[] LARGE_BUFFER_SIZE_CAPABLE_LINE_CLASS_NAMES=new String[]{
        "ipsk.audio.asio.ASIOSourceDataLine","ips.audio.ds.DSSourceDataLine"
    };
    private static String[] STOP_WHILE_DRAIN_CAPABLE_LINE_CLASS_NAMES=new String[]{
            "ipsk.audio.asio.ASIOSourceDataLine"
    };
	protected final static int THREAD_INTERRUPT_TIMEOUT = 30000;

    // Problem with Terratec Soundcard with ALSA driver CMI8738MC6 (ALSA 1.0.11
    // - 1.0.13)
    // if direct devive is selected:
    // device cannot be closed if a call to line.write is blocking. -> hangup
    // workaround: write only if there is enough space in the buffer
    // (line.available()).
    private final static boolean DEFAULT_AVOID_WRITE_LOCK = true;

	protected UpdateAWTEventTransferAgent<PlayerListener, PlayerEvent> apETA=new UpdateAWTEventTransferAgent<PlayerListener,PlayerEvent>();
	
	private SourceDataLine line;

	private Mixer device;

	protected AudioSource audioSource;

	private AudioInputStream srcStream;
	private InterceptorAudioInputStream interCeptorStream;
	private AudioInputStream ais;

	private long frameLength;

	private volatile long streamFramePosition;

	private volatile long streamPosOffset;

	private volatile boolean streaming;

	private byte[] buffer;

	private Thread thread;

	private volatile boolean running;

	private AudioFormat format;

	//private int frameSize;

	private DataLine.Info lineInfo;

	private Object streamNotify;

	private volatile int avail = 0;

    private volatile State status;

    //private Vector<PlayerListener> listeners;

	// private boolean useLongForPosition;

	private volatile long startFramePosition;

	private volatile long stopFramePosition;

	private boolean looping;

	private BufferLevelInfoArray bufferInfos;

	private PeakDetector peakDetector;

	//private EventQueque eventQueque;

	private float[] peakLevelHolds;

	private LevelInfo[] zeroLevelInfos;

//	private int channels;

	private int bufferSize;

	private int preferredBufferSize;

    private HashSet<String> largeBufferSizeCapableLineClassNames;
    
    private HashSet<String> stopWhileDrainCapableLineClassNames;
    
//	private byte[] silentDrainBuffer = null;

//	private int appendSilenceFrames = 0;

   private boolean avoidWriteLock = DEFAULT_AVOID_WRITE_LOCK;
   
	private volatile int loopOffsets=0;
	
	//private Runnable updateRunnable=null;
	
	//private EventObject currentEventObject=null;

	private Integer preferredLineBufferSize = null;

	private Float preferredLineBufferSizeMillis;

    private boolean measureLevel = true;

//    private boolean useAWTEventThread = true;
    
    private boolean forceOpening=true;

    private ChannelRouting channelRouting=null;

	private ChannelRoutingPlugin channelRoutingPlugin;

	private int channelOffset=0;
	
	private ChannelRouting currentChannelRouting=null;
	private long lastLevelCheckposition;
	
	private volatile boolean stopWhileDrainCapable=false;
	

	public Player() {
		this((Mixer) null);
	}

	public Player(Mixer device) {
		this.device = device;

		preferredBufferSize = DEF_PREFERRED_BUFFER_SIZE;
		streamNotify = new Object();
        //listeners = new Vector<PlayerListener>();
		streamPosOffset = 0;
		startFramePosition = 0;
		frameLength = AudioSystem.NOT_SPECIFIED;
		stopFramePosition = frameLength;
		bufferInfos = new BufferLevelInfoArray(1024);
//		channels = 0;
		resetPeakHold();
//        updateRunnable = new Runnable() {
//
//            public void run() {
//                update(currentEventObject);
//            }
//        };
		//eventQueque = new EventQueque("Audio-Player-EventQueque", this);
        status = State.CLOSE;
		looping = false;
		largeBufferSizeCapableLineClassNames=new HashSet<String>(Arrays.asList(LARGE_BUFFER_SIZE_CAPABLE_LINE_CLASS_NAMES));
		stopWhileDrainCapableLineClassNames=new HashSet<String>(Arrays.asList(LARGE_BUFFER_SIZE_CAPABLE_LINE_CLASS_NAMES));
		
	}

	public Player(AudioSource audioSource) {
		this(null, audioSource);

	}

	public Player(File audioFile) {
		this(null, new FileAudioSource(audioFile));
	}

    public Player(URL audioURL) {
        this(null, new URLAudioSource(audioURL));
    }

	public Player(Mixer device, AudioSource audioSource) {
		this(device);
		this.audioSource = audioSource;
	}

	public Player(Mixer device, File audioFile) {
		this(device, new FileAudioSource(audioFile));
	}

    public Player(Mixer device, URL audioURL) {
        this(device, new URLAudioSource(audioURL));
    }

//	private void sendEventAndWait(EventObject eo){
//		currentEventObject=eo;
//        if (!useAWTEventThread || java.awt.EventQueue.isDispatchThread()) {
//			updateRunnable.run();
//		}else{
//		try {
//			java.awt.EventQueue.invokeAndWait(updateRunnable);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		}
//	}
//	
//	private void sendEvent(EventObject eo){
//		currentEventObject=eo;
//        if (!useAWTEventThread || java.awt.EventQueue.isDispatchThread()) {
//			updateRunnable.run();
//		}else{
//		
//			java.awt.EventQueue.invokeLater(updateRunnable);
//		}
//	
//	}

	public boolean isPlaying() {
        return (State.PLAY.equals(status));
	}

	public boolean isPaused() {
        return (State.PAUSE.equals(status));
	}

	public synchronized void setAudioSource(AudioSource audioSource)
			throws PlayerException {
        if (! State.CLOSE.equals(status)) {
			close();
			this.audioSource = audioSource;
			open();
		} else {
			this.audioSource = audioSource;
		}
	}

	private synchronized void setAudioStreamPosition(long pos)
			throws AudioSourceException, IOException, AudioPluginException, AudioFormatNotSupportedException {
		if (pos < streamFramePosition) {
			resetAudioStream();
			syncPosition();
		}
		if (ais != null) {
			int frameSize=ais.getFormat().getFrameSize();
			long toSkip = frameSize * (pos - streamFramePosition);
			while (toSkip > 0) {

				toSkip -= ais.skip(toSkip);

			}
		}
		streamFramePosition = pos;
	}

	private void resetAudioStream() throws AudioSourceException, IOException, AudioPluginException, AudioFormatNotSupportedException {
		if (ais != null)
			ais.close();
		if(audioSource!=null){
			srcStream = audioSource.getAudioInputStream();
			interCeptorStream=new InterceptorAudioInputStream(srcStream);
			if(channelRouting!=null){
				currentChannelRouting=channelRouting;
			}else if(channelOffset!=0){
				currentChannelRouting=new ChannelRouting(false,channelOffset,srcStream.getFormat().getChannels());
			}else{	
				currentChannelRouting=null;
				ais=interCeptorStream;
			}
			if(currentChannelRouting!=null){
				if(channelRoutingPlugin==null){
					channelRoutingPlugin=new ChannelRoutingPlugin();
				}
				channelRoutingPlugin.setChannelRouting(currentChannelRouting);
				ais=channelRoutingPlugin.getAudioInputStream(interCeptorStream);
			}
			applyMeasureLevelStream();
		}
		avail = 0;
		streamFramePosition = 0;
		//syncPosition();
	}

	// public boolean isFormatSupported(AudioFormat af) throws PlayerException{
	// DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);
	// close();
	// try{
	// if (device == null) {
	// line = (SourceDataLine) AudioSystem.getLine(lineInfo);
	// } else {
	// line = (SourceDataLine) device.getLine(lineInfo);
	// }
	// }catch(LineUnavailableException e){
	// return false;
	// }
	// device.
	// SourceDataLine.Info info=(SourceDataLine.Info)line.getLineInfo();
	// return info.isFormatSupported(af);
	//       
	// //line.addLineListener(this);
	//        
	//        
	// }

	public boolean isFormatSupported(AudioFormat af) {
		DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);

		if (device == null) {
			return AudioSystem.isLineSupported(lineInfo);
		} else {
			return device.isLineSupported(lineInfo);
		}
	}

	public synchronized void open() throws PlayerException {
		if (! State.CLOSE.equals(status)){
			return;
		}
		try {
			resetAudioStream();
			syncPosition();
		} catch (AudioSourceException e) {
			throw new PlayerException(e);
		} catch (IOException e) {
			throw new PlayerException(e);
		} catch (AudioPluginException e) {
			throw new PlayerException(e);
		} catch (AudioFormatNotSupportedException e) {
			throw new PlayerException(e);
		}
		format = srcStream.getFormat();
		frameLength = srcStream.getFrameLength();
		int frameSize = format.getFrameSize();
		//		int channels = format.getChannels();
		resetPeakHold();
		// make sure buffer size is multiple of frame size
		int bufferFrames = preferredBufferSize / frameSize;
		bufferSize = bufferFrames * frameSize;
		buffer = new byte[bufferSize];
		//		if (appendSilenceFrames > 0
		//				&& (silentDrainBuffer == null || silentDrainBuffer.length != bufferSize)) {
		//			silentDrainBuffer = new byte[bufferSize];
		//			for (int i = 0; i < silentDrainBuffer.length; i++) {
		//				// TODO check format (sample value of silence)
		//				silentDrainBuffer[i] = 0;
		//			}
		//		}

		AudioFormat playbackFormat=format;

		if(currentChannelRouting!=null){

			try {
				// reset previous output format
				channelRoutingPlugin.setOutputFormat(null);
				channelRoutingPlugin.setInputFormat(format);
				playbackFormat=channelRoutingPlugin.getOutputFormat();
			} catch (AudioFormatNotSupportedException e) {
				e.printStackTrace();
				throw new PlayerException("Cannot route playback stream: ", e);
			}

		}
		if(DEBUG_LEVEL>1){
			System.out.println("Playback format: "+playbackFormat);
		}
		lineInfo = new DataLine.Info(SourceDataLine.class, playbackFormat);
		if (this.device==null){
			AJSDevice ajsDevice=AJSAudioSystem.getDefaultResolvedPlaybackDevice();
			this.device =ajsDevice.getMixer();
			if(this.device==null){
				// fallback to JavaSound
				this.device =AudioSystem.getMixer(null);
			}
		}

		// Javadoc: some lines cannot be reopened. Always get a new line, do
		// not reuse:

		try {
			//			if (line == null) {
			if (device == null) {
				// fallback to Java Sound Engine - Brrrr.

				// if (!AudioSystem.isLineSupported(lineInfo))
				// throw new PlayerException(
				// "Playback format not supported");
				line = (SourceDataLine) AJSAudioSystem.getLine(lineInfo);
			} else {
				// if (!device.isLineSupported(lineInfo))
				// throw new PlayerException(
				// "Playback format not supported");
				line = (SourceDataLine) device.getLine(lineInfo);
			}

			// line.addLineListener(this);
			//			}
		}catch (LineUnavailableException e) {
			throw new PlayerException(e);
		}catch (IllegalArgumentException e) {
			if(forceOpening){
				Line[] lines=device.getSourceLines();
				for(Line l:lines){
					if(l instanceof SourceDataLine){
						line=(SourceDataLine)l;
						break;
					}
				}
				if(line==null){
					throw new PlayerException(e);
				}
			}else{
				throw new PlayerException(e);
			}
		}
		Class<?> lineClass=line.getClass();
		String lineClassName=lineClass.getName();
		
		stopWhileDrainCapable=(stopWhileDrainCapableLineClassNames.contains(lineClassName));
		
		line.addLineListener(this);
		try{
			Integer requestedLinebufferSize=null;
			if(preferredLineBufferSize!=null){
				requestedLinebufferSize=preferredLineBufferSize;
			}else if (preferredLineBufferSizeMillis != null) {
				float preferredLimitedLineBufferSizeMillis=preferredLineBufferSizeMillis;
				requestedLinebufferSize=AudioFormatUtils.pcmSizeInBytesFromLength(playbackFormat, preferredLimitedLineBufferSizeMillis/1000);   
			}
			if (requestedLinebufferSize != null) {

				float requestedLineBufferSizeInMillis=AudioFormatUtils.pcmLengthFromByteLength(playbackFormat, requestedLinebufferSize)*1000;
				// limit to one second for standard JavaSound implementation
				if(requestedLineBufferSizeInMillis> STANDARD_MAX_LINE_BUFFER_SIZE_MILLIS){
					if(!largeBufferSizeCapableLineClassNames.contains(lineClassName)){
						requestedLinebufferSize=AudioFormatUtils.pcmSizeInBytesFromLength(playbackFormat,STANDARD_MAX_LINE_BUFFER_SIZE_MILLIS/1000);
					}      
				}
				line.open(playbackFormat, requestedLinebufferSize);
			} else {
				line.open(playbackFormat);
			}

			line.flush();

			// check buffer size
			int lineBufferSize = line.getBufferSize();
			if (bufferSize >= lineBufferSize) {
				bufferFrames = lineBufferSize / frameSize / 4;
				bufferSize = bufferFrames * frameSize;
			}
		} catch (LineUnavailableException e) {
			throw new PlayerException(e);
		}catch (IllegalArgumentException e) {
			throw new PlayerException(e);
		} catch (AudioFormatNotSupportedException e) {
			throw new PlayerException(e);
		}
		try {
			peakDetector = new PeakDetector(format);
		} catch (AudioFormatNotSupportedException e) {
			throw new PlayerException(e);
		}
		lastLevelCheckposition=0;
		bufferInfos.clear();
		running = true;
		status = State.STOP;
		thread = new Thread(this, "Audio-Player");
		thread.setPriority(Thread.NORM_PRIORITY - 1);
		thread.start();
		syncPosition();
		apETA.fireEventAndWait(new PlayerOpenEvent(this));
	}

	
	private void applyMeasureLevelStream() throws AudioFormatNotSupportedException{
	    
            if(measureLevel){
                interCeptorStream.addAudioOutputStream(new IAudioOutputStream() {
                    private AudioFormat af;
                    private int channels;
                    private int frameSize;

                    @Override
                    public void close() throws IOException {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void flush() throws IOException {
                        // not caching, nothing to do
                    }

                    @Override
                    public void write(byte[] buf, int offset, int len) throws IOException {
                        if (len> 0) {
                            LevelInfo[] lis = peakDetector.processBuffer(
                                    buf, offset, len);
                            for (int ch = 0; ch < channels; ch++) {
                                float peakLevel = lis[ch].getPeakLevel();
                                if (peakLevelHolds[ch] < peakLevel) {
                                    peakLevelHolds[ch] = peakLevel;
                                }
                                lis[ch].setPeakLevelHold(peakLevelHolds[ch]);
                            }
                            BufferLevelInfo bli = new BufferLevelInfo(
                                    streamFramePosition, len / frameSize, lis);
                            bufferInfos.add(bli);
                        }

                    }

                    @Override
                    public void setAudioFormat(AudioFormat audioFormat)
                            throws AudioFormatNotSupportedException {
                        this.af=audioFormat;
                        this.channels=audioFormat.getChannels();
                        this.frameSize=audioFormat.getFrameSize();
                    }

                    @Override
                    public AudioFormat getAudioFormat() {
                        return this.af;
                    }
                });
            }
        

	}

	public synchronized void start() {
		synchronized(streamNotify) {
        if (State.CLOSE.equals(status) || line == null) {
			return;
		}
		line.start();
        if (State.PLAY.equals(status))
			return;
        status = State.PLAY;
//		synchronized (streamNotify) {
			streamNotify.notifyAll();
		}
        //sendEventAndWait(new PlayerStartEvent(this));
        apETA.fireEventAndWait(new PlayerStartEvent(this));
	}

	public synchronized void play() throws PlayerException {
		if (State.CLOSE.equals(status)) {
			throw new PlayerException("Cannot play. Player is not open.");
		}
		setFramePosition(startFramePosition);
		start();
	}

	public long getFrameLength() {
		return frameLength;
	}

	private synchronized void pauseAndFlush(){
		// stop line
		if (line != null) {
			line.stop();
//			// Flush here prevents some linux JS impl from hanging on write
//			// But blocks on openSuSE 11.0 !  ALSA 1.0.16 RC2 and ALSA 1.0.17
//			// No problems on Ubuntu with ALSA 1.0.16 final
//			if (line.getLongFramePosition()!=0){
//				// workaround for openSuSE 11.0 :
//				line.flush();
//				//System.out.println("set frame pos line flushed.");
//			}
		}
		synchronized (streamNotify) {
			status = State.PAUSE;
			// make sure stream engine is inactive

			while (streaming) {
				try {
					streamNotify.wait(10);
				} catch (InterruptedException e) {
					// OK
				}
			}
		}
		if (line !=null){
			if(line.getLongFramePosition()!=0){

				// workaround for openSuSE 11.0 :
				line.flush();
				//System.out.println("set frame pos line flushed.");
				
			}
		}
		
	}
	
	public synchronized long setFramePosition(long pos) throws PlayerException {
		if (DEBUG_LEVEL>0)
			System.out.println("Setting framepos: " + pos+" status: "+status);

		State oldStatus = status;
		
		pauseAndFlush();
		
		avail = 0;
		long newPos = pos;
		if (newPos < 0) {
			status = oldStatus;
			return AudioSystem.NOT_SPECIFIED;
		}
		if (newPos > frameLength)
			newPos = frameLength;
		try {
			setAudioStreamPosition(newPos);
		} catch (AudioSourceException e) {
			throw new PlayerException(e);
		} catch (IOException e) {
			throw new PlayerException(e);
		} catch (AudioPluginException e) {
			throw new PlayerException(e);
		} catch (AudioFormatNotSupportedException e) {
		    throw new PlayerException(e);
        }
		syncPosition();
		synchronized(streamNotify){
		
		 if (State.PLAY.equals(oldStatus)) {
        	// this flush works also on openSuSE 11.0
        	//line.flush();
        	
			line.start();
			status = oldStatus;
			//synchronized (streamNotify) {
				streamNotify.notifyAll();
			//}
		}else{
			status = oldStatus;
		}
		}
		return newPos;

	}

	public synchronized void pause() {
		if (State.STOP.equals(status)) {
			status = State.PAUSE;
            apETA.fireEventAndWait(new PlayerPauseEvent(this));
		} else if (State.PLAY.equals(status)) {
			if (line != null){
				status=State.STOPPING;
				line.stop();
			}
			status = State.PAUSE;
            apETA.fireEventAndWait(new PlayerPauseEvent(this));
		}else if (stopWhileDrainCapable && State.DRAINING.equals(status)) {
			// only stop 
			// restart when the line is already draining is not yet supported
			stop();
		}  else if (State.PAUSE.equals(status)) {
			start();
		}
	}

	public synchronized void stop() {
		synchronized(streamNotify){
			if (State.STOP.equals(status) || State.STOPPING.equals(status) || (!stopWhileDrainCapable && State.DRAINING.equals(status)) || State.CLOSE.equals(status) || State.CLOSING.equals(status))
				return;
			status=State.STOPPING;
			if (line != null){
				line.stop();
				if (DEBUG_LEVEL>0)
					System.out.println("Line stopped.");
			}

			status = State.STOP;
		}
		apETA.fireEventAndWait(new PlayerStoppedEvent(this));
	}

	public synchronized void close() throws PlayerException {
		synchronized(streamNotify){
        if (State.CLOSE.equals(status) || State.CLOSING.equals(status))
			return;
        status=State.CLOSING;
		running = false;
		}

//		if(delayBeforeClose >0){
//			try {
//				
//				Thread.sleep(delayBeforeClose);
//			} catch (InterruptedException e) {
//				// No problem
//			}
//		}
		if (line != null) {
			line.stop();
			if (DEBUG_LEVEL > 0)
				System.out.println("Line stopped.");
			line.flush();
			if (DEBUG_LEVEL>0)
				System.out.println("Line flushed.");
		}
		// running = false;

		if (thread != null && thread != Thread.currentThread()
				&& thread.isAlive()) {
			// The MAC OS JavaSound implementation sometimes hangs on
			// DataLine.drain().
			// so we have to interrupt the streaming thread after a while.
			// also on some Linux SuSE 9.0 hosts the player hangs native on
			// line.write()
            if (DEBUG_LEVEL>2)
                System.out.println("Joining...");
            try {

				thread.join(THREAD_INTERRUPT_TIMEOUT);
//            	thread.join();
                if (DEBUG_LEVEL>2)
                    System.out.println("Joined.");
			} catch (InterruptedException e) {
				// break;
			}
			if (thread.isAlive()) {
                if (DEBUG_LEVEL>2)
                    System.out.println("Thread still alive.");
				Thread moribund = thread;
				thread = null;
				moribund.interrupt();
				streaming = false;
				//line = null;
				synchronized (streamNotify) {
					streamNotify.notifyAll();

				}
			}
		}
//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		try {
            if (ais != null){
				ais.close();
                }
		} catch (IOException e) {
			throw new PlayerException(e);
		} finally {
            if (line != null){
				line.close();
				line.removeLineListener(this);
            }
//			line = null;
			ais = null;
			interCeptorStream=null;
			srcStream=null;
            status = State.CLOSE;
			syncPosition();
           
            apETA.fireEventAndWait(new PlayerCloseEvent(this));
		}
		
	}

	public long getFramePosition() {
		if (line == null)
			return AudioSystem.NOT_SPECIFIED;
		long linePos=line.getLongFramePosition();
		long framePos=linePos - streamPosOffset;
		if(DEBUG_LEVEL>2)System.out.println("getFramePosition: "+linePos+" "+streamPosOffset+" "+frameLength);
		if(framePos>stopFramePosition && loopOffsets>0){
			
		//if(loopOffsets>0){
			// workaround for looping mode
			//System.out.println(framePos+" "+stopFramePosition);
			
			long loopLength=AudioSystem.NOT_SPECIFIED;
			if(stopFramePosition==AudioSystem.NOT_SPECIFIED){
				if (frameLength!=AudioSystem.NOT_SPECIFIED){
					loopLength=frameLength-startFramePosition;
					//streamPosOffset+=loops*(frameLength-startFramePosition);
				}
			}else{
				loopLength=stopFramePosition-startFramePosition;
				//streamPosOffset+=loops*(stopFramePosition-startFramePosition);
			}
			long overLoops=0;
			if(loopLength >0){
				overLoops=(framePos-stopFramePosition)/loopLength;
			}
			long loops=1+overLoops;
			streamPosOffset+=loops*loopLength;
			loopOffsets-=loops;
			framePos=linePos - streamPosOffset;
		}
		//System.out.println("linePos "+linePos+" streamPosOffset "+streamPosOffset+" framePos "+framePos);
		return framePos;

	}

	public LevelInfo[] getLevelInfos() {
		synchronized (bufferInfos) {
			
			long currentFramePosition=getFramePosition();
			LevelInfo[] lis=bufferInfos.intervalLevelInfos(lastLevelCheckposition, currentFramePosition);
			lastLevelCheckposition=currentFramePosition;
			if (!isPlaying() || lis == null) {
				if(format!=null){
					int channels=format.getChannels();
					for (int ch = 0; ch < channels; ch++) {
						// TODO nullpointerexceptions here
						zeroLevelInfos[ch].setPeakLevelHold(peakLevelHolds[ch]);
					}
					return zeroLevelInfos;
				}else{
					return null;
				}
			}
			return lis;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		long toRead;
		int read = 0;

		int offset = 0;
		int written = 0;
		long totalWritten = 0;
		AudioFormat format=ais.getFormat();
		int frameSize=format.getFrameSize();
		int channels=format.getChannels();
		
		while (running) {

			while (running && ! State.PLAY.equals(status)) {

				streaming = false;
				synchronized (streamNotify) {
					streamNotify.notifyAll();
				}

				synchronized (streamNotify) {
					try {
						streamNotify.wait(10);
					} catch (InterruptedException e1) {
						// OK
					}
				}

			}
			if (!running)
				break;
			streaming = true;
			synchronized (streamNotify) {
				streamNotify.notifyAll();
			}
			if (avail == 0) {
				toRead = bufferSize;
				if (stopFramePosition != AudioSystem.NOT_SPECIFIED) {
					toRead = (stopFramePosition - streamFramePosition)
					* frameSize;
					if (toRead > bufferSize)
						toRead = bufferSize;
				}
				if (toRead <= 0) {

					// Need to sleep here for condition: looping and stopPos <
					// startPos

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// OK
					}
					read = -1;
				} else {
					try {
						read = ais.read(buffer, 0, (int) toRead);

					} catch (IOException e) {
						e.printStackTrace();
						//sendEvent(new PlayerErrorEvent(e));
						apETA.fireEvent(new PlayerErrorEvent(e));
					}
					if (read > 0) {
//						if (measureLevel) {
//							LevelInfo[] lis = peakDetector.processBuffer(
//									buffer, 0, read);
//							for (int ch = 0; ch < channels; ch++) {
//								float peakLevel = lis[ch].getPeakLevel();
//								if (peakLevelHolds[ch] < peakLevel) {
//									peakLevelHolds[ch] = peakLevel;
//								}
//								lis[ch].setPeakLevelHold(peakLevelHolds[ch]);
//							}
//							BufferLevelInfo bli = new BufferLevelInfo(
//									streamFramePosition, read / frameSize, lis);
//							bufferInfos.add(bli);
//						}
						avail += read;
						streamFramePosition += read / frameSize;
						offset = 0;

					}
				}
			}

			if (avail > 0) {
				// System.out.println((line.available() *100)/
				// line.getBufferSize());
				// detect buffer underrun
				// if (line.available() == line.getBufferSize()) {
				// updateListeners(new PlayerErrorEvent(new PlayerException(
				// "Bufferunderrun detected !")));
				// }

				int lineAvailable=line.available();
				
				if (avoidWriteLock && (lineAvailable < avail)) {
					written = 0;
				} else {
					written = line.write(buffer, offset, avail);
					if (DEBUG_LEVEL>2)
						System.out.println(written
								+ " bytes written to audio line.");
				}
				totalWritten += written;
				if (written != avail) {

					if (written == 0) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				offset += written;
				if (offset == bufferSize)
					offset = 0;

				avail -= written;

			} else {

				if (read == -1) {
					// System.out.println("Status: "+status);
					if (looping) {
						long lastStreamFramePosition=streamFramePosition;
						try {
							//setAudioStreamPosition(startFramePosition);
							if (startFramePosition < streamFramePosition) {
								resetAudioStream();
								//syncPosition();
							}
							if (ais != null) {
								long toSkip = frameSize * (startFramePosition - streamFramePosition);
								while (toSkip > 0) {

									toSkip -= ais.skip(toSkip);

								}
							}
						} catch (Exception e) {
							System.err.println("Send error event");
							apETA.fireEvent(new PlayerErrorEvent(e));
						}
						//syncPosition();
						//streamPosOffset+=lastStreamFramePosition-startFramePosition;
						loopOffsets++;
						if(DEBUG_LEVEL>2)System.out.println("looped "+loopOffsets);
						streamFramePosition=startFramePosition;
					} else {
						// System.out.println("Status: "+status);
//						// workaround for repeating last buffer with some Linux
//						// ALSA drivers
//						if (appendSilenceFrames > 0) {
//							int appSilenceBytes = appendSilenceFrames
//							* frameSize;
//							int sWritten = 0;
//							while (sWritten < appSilenceBytes) {
//								int toWrite = appSilenceBytes - sWritten;
//								if (toWrite > silentDrainBuffer.length)
//									toWrite = silentDrainBuffer.length;
//
//								written = line.write(silentDrainBuffer, 0,
//										toWrite);
//
//								if (written == 0) {
//									try {
//										Thread.sleep(10);
//									} catch (InterruptedException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								} else {
//									sWritten += written;
//								}
//							}
//						}

						if (totalWritten > 0){
							//System.out.println("Drain...");
						    synchronized(streamNotify){
						        status=State.DRAINING;
						    }
							line.drain();
							
						}
						if (status.equals(State.PAUSE)) {
							line.stop();
							syncPosition();
						}
						synchronized(streamNotify){
							// Do not fire event if player already stopped or closed. 
							if(! State.STOP.equals(status) && ! State.CLOSE.equals(status) && ! State.CLOSING.equals(status)){
								status = State.STOP;
								apETA.fireEvent(new PlayerEndOfMediaEvent(this));
							}
						}
					} 
				}
			}
		}
		streaming = false;

		synchronized (streamNotify) {
			streamNotify.notifyAll();
		}

	}


	public boolean isOpen() {
        return (! State.CLOSE.equals(status));
	}

	private void syncPosition() {
		if (line == null || !isOpen()) {
			streamPosOffset = 0;
			return;
		}
		loopOffsets=0;
		long linePosition=line.getLongFramePosition();
		streamPosOffset =  linePosition - streamFramePosition;
		if (DEBUG_LEVEL>2)System.out.println("Sync: line: "+linePosition+" stream: "+streamFramePosition);
	}

	public synchronized void addPlayerListener(PlayerListener pl) {
//        if (pl != null && !listeners.contains(pl)) {
//            listeners.addElement(pl);
//        }
    	apETA.addListener(pl);
	}

	public synchronized void removePlayerListener(PlayerListener pl) {
//        if (pl != null) {
//            listeners.removeElement(pl);
//        }
    	apETA.removeListener(pl);
	}

//    protected void updateListeners(PlayerEvent pe) { 
//        for(PlayerListener listener:listeners){
//            listener.update(pe);
//        }
//    }

	public long getStartFramePosition() {
		return startFramePosition;
	}

	public void setStartFramePosition(long startFramePosition) {
		this.startFramePosition = startFramePosition;
		lastLevelCheckposition=startFramePosition;
		try {
			setFramePosition(startFramePosition);
		} catch (PlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long getStopFramePosition() {
		return stopFramePosition;
	}

	
	public void setStopFramePosition(long stopFramePosition) {
		this.stopFramePosition = stopFramePosition;
		try {
			setFramePosition(startFramePosition);
		} catch (PlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public synchronized void setSelection(long startFramePosition,long stopFramePosition) throws PlayerException{
	    this.startFramePosition=startFramePosition;
	    this.stopFramePosition = stopFramePosition;
       setFramePosition(startFramePosition); 
	}

	public boolean isLooping() {
		return looping;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	public AudioFormat getAudioFormat() {
		return format;
	}

	public void setMixer(Mixer newPlaybackMixer) throws PlayerException {
        if (! State.CLOSE.equals(status))
			throw new PlayerException(
					"Player must be closed to set new mixer !");
		device = newPlaybackMixer;

		line = null;
	}

//    /*
//     * (non-Javadoc)
//     * 
//     * @see ipsk.util.EventQuequeListener#update(java.util.EventObject)
//     */
//    public void update(EventObject eventObject) {
//        updateListeners((PlayerEvent) eventObject);
//
//    }

	public int getPreferredBufferSize() {
		return preferredBufferSize;
	}

	public void setPreferredBufferSize(int preferredBufferSize) {
		this.preferredBufferSize = preferredBufferSize;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setPeakLevelHold(float[] peakLevels) {
		peakLevelHolds = peakLevels;
	}

	public void resetPeakHold() {
		if(format!=null){
			int channels=format.getChannels();
			peakLevelHolds = new float[channels];
			zeroLevelInfos = new LevelInfo[channels];
			for (int ch = 0; ch < channels; ch++) {
				zeroLevelInfos[ch] = new LevelInfo();
			}
		}
	}

	public SourceDataLine getLine() {
		return line;
	}

	// public int getAppendSilenceFrames() {
	// return appendSilenceFrames;
	// }
	//
	// public void setAppendSilenceFrames(int useSilentDrainBuffers) {
	// this.appendSilenceFrames = useSilentDrainBuffers;
	// }
	public AudioSource getAudioSource() {
		return audioSource;
	}

    public boolean isAvoidWriteLock() {
        return avoidWriteLock;
    }

    public void setAvoidWriteLock(boolean avoidWriteLock) {
        this.avoidWriteLock = avoidWriteLock;
    }
	
	public Integer getPreferredLineBufferSize() {
		return preferredLineBufferSize;
	}

	public void setPreferredLineBufferSize(Integer preferredLineBufferSize) {
		preferredLineBufferSizeMillis = null;
		this.preferredLineBufferSize = preferredLineBufferSize;
	}

	public Float getPreferredLineBufferSizeMillis() {
		return preferredLineBufferSizeMillis;
	}

	public void setPreferredLineBufferSizeMillis(
			Float preferredLineBufferSizeMillis) {
		preferredLineBufferSize = null;
		this.preferredLineBufferSizeMillis = preferredLineBufferSizeMillis;
	}
	
	public boolean isUseAWTEventThread() {
		return apETA.isEventsInAWTEventThread();
    }

    public void setUseAWTEventThread(boolean useAWTEventThread) {
        apETA.setEventsInAWTEventThread(useAWTEventThread);
    }

    public boolean isMeasureLevel() {
        return measureLevel;
    }

    public void setMeasureLevel(boolean measureLevel) {
        this.measureLevel = measureLevel;
    }
    
    
	public boolean isForceOpening() {
		return forceOpening;
	}

	public void setForceOpening(boolean forceOpening) {
		this.forceOpening = forceOpening;
	}
	
	/**
	 * @return the channelRouting
	 */
	public ChannelRouting getChannelRouting() {
		return channelRouting;
	}

	/**
	 * @param channelRouting the channelRouting to set
	 */
	public void setChannelRouting(ChannelRouting channelRouting) {
		this.channelRouting = channelRouting;
	}
	
	/**
	 * @return the channelOffset
	 */
	public int getChannelOffset() {
		return channelOffset;
	}

	
	public void setChannelOffset(int channelOffset) {
		this.channelOffset = channelOffset;
	}
	

    private static class Shutdown extends Thread {
        private Player player;

        private boolean verbose = false;

        public Shutdown(Player p, boolean verbose) {
            this.player = p;
            this.verbose = verbose;
        }

        public void run() {

            try {
                if(player.isOpen()){
                if(verbose)System.out.println("Player closing...");
                player.close();
                }

            } catch (PlayerException e) {
                System.err.println("Could not close player !");
                if (verbose)
                    e.printStackTrace();
            }
        }

    }

    private static void printUsage() {
        System.out.println("Audio player version "
                + Player.class.getPackage().getImplementationVersion() + "\n"
                + "Usage: java " + Player.class.getName()
                + " [-v] audiofilename\n" + "       Plays audiofile.\n"
                + "       java " + Player.class.getName() + " -h\n"
                + "       Displays this help page.\n" + "Options:\n"
                + "         -v verbose mode.");
    }

    public static void main(String[] args) {
        OptionParser op = new OptionParser();
        Option verboseOption = new Option("v");
        Option helpOption = new Option("h");
        op.addOption(verboseOption);
        op.addOption(helpOption);
        try {
            op.parse(args);
        } catch (OptionParserException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(-1);
        }

        if (helpOption.isSet()) {
            printUsage();
            System.exit(0);
        }
        final boolean verbose = verboseOption.isSet();
        if (verbose) {
            System.out.println("Audio player version "
                    + Player.class.getPackage().getImplementationVersion());
        }

        String[] params;
        params = op.getParams();

        Player p = null;
        // try to get a direct device first
        // MixerManager mm;
        // Mixer device=null;
        // try {
        // mm = new MixerManager();
        // Mixer[] devices=mm.getDirectPlaybackMixers();
        // //Mixer[] devices=mm.getPlaybackMixers();
        // if (devices !=null && devices.length >0){
        // device=devices[0];
        // p=new Player(device);
        // }else{
        // // default device
        // System.err.println("Warning: Could not get a direct audio device.");
        // p=new Player();
        // }
        // } catch (LineUnavailableException e) {
        // System.err.println("Warning: Could not get a direct audio device.");
        // e.printStackTrace();
        //            
        // }
        p = new Player();
        // // else default device
        // if(p==null){
        // p=new Player();
        // System.err.println("Warning: Using buggy JavaSound audio device.");
        // }
        p.setPreferredLineBufferSizeMillis(PREFERRED_LINE_BUFFER_SIZE_MILLIS);
        p.setUseAWTEventThread(false);
        p.setMeasureLevel(false);

        Shutdown shutDown = new Shutdown(p, verbose);

        File playFile = null;

        if (params.length == 0) {
            printUsage();
            System.exit(-1);
        } else if (params.length == 1) {
            try {
                URL url = new URL(params[0]);
                String urlProto = url.getProtocol();
                if (!urlProto.equalsIgnoreCase("file")) {
                    System.err
                            .println("Only file protocol URL's are supported !");
                    System.exit(-1);
                }
                playFile = new File(url.toURI().getPath());
            } catch (MalformedURLException e1) {
                // OK no URL try file now
                playFile = new File(params[0]);
            } catch (URISyntaxException e) {
            	// OK no URL try file now
                playFile = new File(params[0]);
            }
        }

        Runtime.getRuntime().addShutdownHook(shutDown);
        // p.setPreferredLineBufferSizeMillis(PREFERRED_LINE_BUFFER_SIZE_MILLIS);
        try {
            p.setAudioSource(new ConvenienceFileAudioSource(playFile));
            if (verboseOption.isSet())
                System.out.println("Audio source set.");
        } catch (PlayerException e2) {
            System.err.println("Could not set audio source !");
            if (verbose)
                e2.printStackTrace();
            System.exit(-1);
        } catch (AudioSourceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        p.addPlayerListener(new PlayerListener() {

            public void update(PlayerEvent playerEvent) {
                if (playerEvent instanceof PlayerStopEvent) {

                    if (verbose)
                        System.out.println("Player stop.");
                    try {
                        ((Player)playerEvent.getSource()).close();
                    } catch (PlayerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (playerEvent instanceof PlayerCloseEvent) {
                    if (verbose)
                        System.out.println("Player closed.");
                }

            }

        });

        try {
            p.open();
            if (verbose)
                System.out.println("Player opened.");
        } catch (PlayerException e1) {
            System.err.println("Could not open player !");
            if (verbose)
                e1.printStackTrace();
            System.exit(-1);
        }

        p.start();
        if (verbose)
            System.out.println("Player started.\nInterrupt with Ctrl-C.");
    }

	/* (non-Javadoc)
	 * @see javax.sound.sampled.LineListener#update(javax.sound.sampled.LineEvent)
	 */
	@Override
	public void update(LineEvent event) {
		LineEvent.Type type=event.getType();
		if(!State.CLOSING.equals(status) && ! State.CLOSE.equals(status)){
			if(LineEvent.Type.CLOSE.equals(type)){
				try {
					if (DEBUG_LEVEL>0){
						System.out.println("Close from line close event");
					}
					close();
				} catch (PlayerException e) {
					e.printStackTrace();
					// unable to handle
				}
			}else if(LineEvent.Type.STOP.equals(type) && !State.STOP.equals(status) && !State.STOPPING.equals(status) && !State.PAUSE.equals(status)){
				stop();
			}
		}
	}

}