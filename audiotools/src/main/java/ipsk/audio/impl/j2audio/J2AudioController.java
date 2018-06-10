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
 * Date  : Apr 24, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
package ipsk.audio.impl.j2audio;

import ipsk.audio.AudioController;
import ipsk.audio.AudioControllerException;
import ipsk.audio.AudioControllerListener;
import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.FileAudioSource;
import ipsk.audio.URLAudioSource;
import ipsk.audio.ui.AudioControllerUI;
import ipsk.util.optionparser.Option;
import ipsk.util.optionparser.OptionParser;
import ipsk.util.optionparser.OptionParserException;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Mixer.Info;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

//import ipsk.util.*;

/**
 * The main class to manage multi-line audio recording and playback.
 * <ul>
 * <li>looks for matching audio mixers and lines
 * <li>opens the lines
 * <li>handles line-synchronized starting and stopping
 * </ul>
 * 
 * @author klausj
 *  
 */

@SuppressWarnings("deprecation")
public class J2AudioController implements AudioController, CaptureListener,
        PlaybackListener {

    private static final boolean DEBUG = false;

    private long mode;

    private AudioControllerUI acUi = null;

    private int numLines = 1;

    private int numLineChannels;

    private int numTotalChannels;

    //private File[] playbackFiles;
    //private URL[] playbackURLs=null;
    private AudioSource[] audioSources = null;

    private InputStream[] playbackInputStreams;

    private File[] recordingFiles;

    //private URL[] recordingURLs;
    private OutputStream[] recordingOutputStreams;

    private String reqTargetMixerName = null;

    //	private Mixer.Info reqTargetMixerInfo = null;
    private Mixer.Info reqTargetMixerInfo = null;

    private Mixer reqTargetMixer = null;

    private String reqSourceMixerName = null;

    private Mixer.Info reqSourceMixerInfo = null;

    private Mixer reqSourceMixer = null;

    //	private Mixer mixers[] = null;
    private Mixer sourceMixer = null;

    private Mixer targetMixer = null;

    private SourceDataLine[] sdl = null;

    private DataLine.Info sdlInfo = null;

    private TargetDataLine[] tdl = null;

    private DataLine.Info tdlInfo = null;

    private PlaybackStatus playbackStatus = new PlaybackStatus(
            PlaybackStatus.CLOSED);

    private CaptureStatus captureStatus = new CaptureStatus(
            CaptureStatus.CLOSED);

    //private float latencyTime = (float) 0.01; //latency time in seconds

    private Vector<AudioControllerListener> listenerList = new Vector<AudioControllerListener>();

    private AudioFormat audioFormat;

    private AudioFormat playbackAudioFormat;

    private float levels[];

    private Playback[] p = null;

    private Object playbackLock = new Object();

    private Capture[] c = null;

    private boolean overwrite = false;

    private boolean captureDeviceOpen = false;

    private boolean playbackDeviceOpen = false;

    private JFrame uiFrame = null;

    private Properties properties;
    
    private Logger logger;

    /**
     * Default constructor. Needed for use as plugin.
     *  
     */
    public J2AudioController() {
    	super();
    	String packageName=getClass().getPackage().getName();
		logger=Logger.getLogger(packageName);
		logger.setLevel(Level.INFO);
        // set default configuration
        setAudioFormat(new AudioFormat(44100, 16, 2, true, false));
        setNumLines(1);
        setMode(OPEN_ON_DEMAND);

        setPlaybackFiles(null);
        setRecordingFiles(null);
        properties = new Properties();
        logger.info("J2AudioController created.");
    }

    private Capture createCapture(TargetDataLine tdl) {
        Capture c = null;
        c = new Capture(this, tdl);
        String tmpFileProperty = getProperty("RECORD_USE_TMP_FILE");
        if (tmpFileProperty != null) {
            c.setUseTempFile(Boolean.valueOf(tmpFileProperty).booleanValue());
        }
        String lineBufSizeProperty = getProperty("CAPTURE_PREFERRED_LINE_BUFFER_SIZE");
        if (lineBufSizeProperty != null) {
            c.setPreferredBufferSize(Integer.valueOf(lineBufSizeProperty)
                    .intValue());
        }
        return c;
    }

    /**
     * Trys to open numLines audio input lines.
     * 
     * @param m
     *            the mixer to query for lines
     * @param numLines
     *            number of lines
     * @return true on success
     */
    private boolean checkMixerTargetLines(Mixer m, int numLines)
            throws LineUnavailableException {

        Line.Info[] tlinfos = null;

        tlinfos = m.getTargetLineInfo(tdlInfo);

        if (tlinfos.length < numLines) {
            if (DEBUG)
                System.out.println("Not enough TDLs");
            return false;
        }

        try {
            for (int i = 0; i < numLines; i++) {

                tdl[i] = (TargetDataLine) m.getLine(tlinfos[i]);
                c[i] = createCapture(tdl[i]);
            }
        } catch (Exception e) {

            return false;
        }

        return true;

    }

    /**
     * Trys to open numLines audio output lines.
     * 
     * @param m
     *            the mixer to query for lines
     * @param numLines
     *            number of lines
     * @return true on success
     */
    private boolean checkMixerSourceLines(Mixer m, int numLines)
            throws LineUnavailableException {

        Line.Info[] slinfos = m.getSourceLineInfo(sdlInfo);
        //Line.Info[] slinfos = m.getSourceLineInfo(); // the above does not
        // work with Tritonus
        if (slinfos.length < numLines) {

            return false;
        }
        try {
            for (int i = 0; i < numLines; i++) {

                //sdl[i] = (SourceDataLine) m.getLine(slinfos[i]);
                //p[i] = new Playback(ac, sdl[i]);
                p[i] = new Playback(this, m);

            }
        } catch (Exception e) {

            return false;
        }

        return true;

    }

    public void setNumLines(int numLines) {

        this.numLines = numLines;

    }

    public void setSourceMixer(Mixer.Info mixerInfo) {

        reqSourceMixerInfo = mixerInfo;
        reqSourceMixer = null;
        reqSourceMixerName = null;

    }

    public void setMode(int mode) {

        this.mode = mode;
    }

    public synchronized void configure() throws AudioControllerException {
        String pStatus = playbackStatus.getStatus();
        String cStatus = captureStatus.getStatus();
        if (pStatus != PlaybackStatus.CLOSED
                && pStatus != PlaybackStatus.CONFIGURED) {
            throw new AudioControllerException(new LineUnavailableException(
                    "Playback device is busy."));
        }
        if (cStatus != CaptureStatus.CLOSED
                && cStatus != CaptureStatus.CONFIGURED) {

            throw new AudioControllerException(new LineUnavailableException(
                    "Capture device is busy."));
            //closeCapture();
        }

        numLineChannels = audioFormat.getChannels();
        numTotalChannels = numLines * numLineChannels;
        levels = new float[numTotalChannels];
        resetLevels();
        sdlInfo = new DataLine.Info(SourceDataLine.class, playbackAudioFormat);
        tdlInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

        boolean sourceMixerFound = false;
        boolean targetMixerFound = false;
        sourceMixer=null;
        targetMixer=null;

        Mixer.Info[] mi = AudioSystem.getMixerInfo();
        String mixerName = null;

        tdl = new TargetDataLine[numLines];
        c = new Capture[numLines];
        boolean mixerNameFound = false;

        if (reqTargetMixerInfo != null) {
            targetMixer = AudioSystem.getMixer(reqTargetMixerInfo);
            try {
                targetMixerFound = checkMixerTargetLines(targetMixer, numLines);
            } catch (LineUnavailableException e) {
                throw new AudioControllerException(e);
            }
        } else if (reqTargetMixer != null) {
            targetMixer = reqTargetMixer;
            try {
                targetMixerFound = checkMixerTargetLines(targetMixer, numLines);
            } catch (LineUnavailableException e) {
                throw new AudioControllerException(e);
            }
        } else if (reqTargetMixerName != null) {
            if (DEBUG)
                System.out.println("Req. mixer name for recording: "
                        + reqTargetMixerName);
            //		Look for a mixer which has requested lines
            int mixerInd;

            for (mixerInd = 0; mixerInd < mi.length; mixerInd++) {
                mixerName = mi[mixerInd].getName();
                if (DEBUG)
                    System.out.println("Try mixer for recording: " + mixerName);

                if (reqTargetMixerName != null
                        && !reqTargetMixerName.equals(mixerName))
                    continue;
                mixerNameFound = true;
                targetMixer = AudioSystem.getMixer(mi[mixerInd]);

                if (!targetMixerFound) {

                    try {
                        targetMixerFound = checkMixerTargetLines(targetMixer,
                                numLines);
                    } catch (LineUnavailableException e) {
                        throw new AudioControllerException(e);
                    }
                }
                if (targetMixerFound) {
                    reqTargetMixerInfo = targetMixer.getMixerInfo();
                    if (DEBUG)
                        System.out.println("Using mixer for recording: "
                                + mixerName + reqTargetMixerInfo.getVersion());
                    break;
                } else {
                    if (DEBUG)
                        System.out.println("Mixer " + mixerName
                                + " does not match for recording.");
                }

            }
        } else {
            // Try to find first (default) mixer with capture lines
            int mixerInd;
            for (mixerInd = 0; mixerInd < mi.length; mixerInd++) {
                mixerName = mi[mixerInd].getName();
                if (DEBUG)
                    System.out.println("Try mixer for recording: " + mixerName);
                targetMixer = AudioSystem.getMixer(mi[mixerInd]);
                Line.Info[] tlInfos = targetMixer
                        .getTargetLineInfo(new DataLine.Info(
                                TargetDataLine.class, null));

                if (tlInfos != null && tlInfos.length > 0) {

                    try {
                        targetMixerFound = checkMixerTargetLines(targetMixer,
                                numLines);
                    } catch (LineUnavailableException e) {
                        throw new AudioControllerException(e);
                    }
                    
                        break;
                }
            }
        }
        if (!targetMixerFound) {
            //targetMixer.close();
            close();
            targetMixer=null;
            AudioFormatNotSupportedException e;
            if (reqTargetMixerName != null) {
                if (!mixerNameFound) {
                    throw new AudioControllerException("Capture device "
                            + reqTargetMixerName + " not found !");
                }
                e = new AudioFormatNotSupportedException(false,
                        reqTargetMixerName, audioFormat);
            } else
                e = new AudioFormatNotSupportedException(false, audioFormat);
            throw new AudioControllerException(e);
        }
        String asioUseMaxBufferSize=getProperty("ASIO_USE_MAX_BUFFER_SIZE");
        Class<? extends Mixer> targetMixerClass=targetMixer.getClass();
        if (targetMixerClass.getName().equals("ipsk.audio.asio.ASIOMixer") && asioUseMaxBufferSize !=null){
        	try {
				Method m=targetMixerClass.getMethod("setUseMaxBufferSize",new Class[]{Boolean.TYPE});
				m.invoke(targetMixer,new Object[]{new Boolean(asioUseMaxBufferSize)});
			} catch (Exception e) {
				System.err.println("Could not set max buffer size of ASIO mixer: "+e.getLocalizedMessage());
			} 	
        }
        
        captureStatus.setStatus(CaptureStatus.CONFIGURED);
        updateListeners(null, captureStatus);

        sdl = new SourceDataLine[numLines];
        p = new Playback[numLines];

        if (reqSourceMixerInfo != null) {
            sourceMixer = AudioSystem.getMixer(reqSourceMixerInfo);
            try {
                sourceMixerFound = checkMixerSourceLines(sourceMixer, numLines);
            } catch (LineUnavailableException e) {
                throw new AudioControllerException(e);
            }
        } else if (reqSourceMixer != null) {
            sourceMixer = reqSourceMixer;
            try {
                sourceMixerFound = checkMixerSourceLines(sourceMixer, numLines);
            } catch (LineUnavailableException e) {
                throw new AudioControllerException(e);
            }
        } else if (reqSourceMixerName != null) {
            int mixerInd;
            mixerNameFound = false;
            for (mixerInd = 0; mixerInd < mi.length; mixerInd++) {
                mixerName = mi[mixerInd].getName();
                if (reqSourceMixerName != null
                        && !reqSourceMixerName.equals(mixerName))
                    continue;
                mixerNameFound = true;
                sourceMixer = AudioSystem.getMixer(mi[mixerInd]);
                try {
                    sourceMixerFound = checkMixerSourceLines(sourceMixer,
                            numLines);
                } catch (LineUnavailableException e) {
                    throw new AudioControllerException(e);
                }

                if (sourceMixerFound) {
                    reqSourceMixerInfo = sourceMixer.getMixerInfo();
                    if (DEBUG)
                        System.out.println("Using mixer for playback: "
                                + mixerName);
                    break;
                }
            }
        } else {
            int mixerInd;
            for (mixerInd = 0; mixerInd < mi.length; mixerInd++) {
                mixerName = mi[mixerInd].getName();
                sourceMixer = AudioSystem.getMixer(mi[mixerInd]);
                Line.Info[] slInfos = sourceMixer
                        .getSourceLineInfo(new DataLine.Info(
                                SourceDataLine.class, null));

                if (slInfos != null && slInfos.length > 0) {

                    try {
                        sourceMixerFound = checkMixerSourceLines(sourceMixer,
                                numLines);

                    } catch (LineUnavailableException e) {
                        throw new AudioControllerException(e);
                    }
                    
                        break;
                }
            }
        }
        if (!sourceMixerFound) {
            close();
            sourceMixer=null;
            AudioFormatNotSupportedException e;
            if (reqSourceMixerName != null) {
                if (!mixerNameFound) {
                    throw new AudioControllerException("Playback device "
                            + reqSourceMixerName + " not found !");
                }
                e = new AudioFormatNotSupportedException(false,
                        reqSourceMixerName, playbackAudioFormat);
            } else
                e = new AudioFormatNotSupportedException(false,
                        playbackAudioFormat);
            throw new AudioControllerException(e);

        }
        Class<? extends Mixer> sourceMixerClass=sourceMixer.getClass();
        if (sourceMixerClass.getName().equals("ipsk.audio.asio.ASIOMixer") && asioUseMaxBufferSize !=null){
        	try {
				Method m=sourceMixerClass.getMethod("setUseMaxBufferSize",new Class[]{Boolean.TYPE});
				m.invoke(sourceMixer,new Object[]{new Boolean(asioUseMaxBufferSize)});
			} catch (Exception e) {
				System.err.println("Could not set max buffer size of ASIO mixer: "+e.getLocalizedMessage());
			} 	
        }
        playbackStatus.setStatus(PlaybackStatus.CONFIGURED);
        updateListeners(playbackStatus, null);
        logger.info("J2AudioController configured.");
    }

    public void open() throws AudioControllerException {
        try {
            if (captureStatus.getStatus() != CaptureStatus.CONFIGURED) {
                new LineUnavailableException("Controller not configured.");
            }
            if (mode == FULLDUPLEX) {
                openCapture();
                openPlayback();
            }
        } catch (IOException e) {
            throw new AudioControllerException(e);
        } catch (LineUnavailableException e) {
            throw new AudioControllerException(e);
        } catch (AudioFormatNotSupportedException e) {
            throw new AudioControllerException(e);
        }
        captureStatus.setStatus(CaptureStatus.OPEN);
        updateListeners(null, captureStatus);
        playbackStatus.setStatus(PlaybackStatus.OPEN);
        updateListeners(playbackStatus, null);
    }

    void openCapture() throws IOException, LineUnavailableException,
            AudioFormatNotSupportedException {
        //targetMixer.open();
        for (int i = 0; i < numLines; i++) {
            if (c[i] == null) {
                checkMixerTargetLines(targetMixer, numLines);
            }
            c[i].open(audioFormat);

            log("Capture open");
        }
        captureDeviceOpen = true;

    }

    void openPlayback() throws AudioFormatNotSupportedException,
            LineUnavailableException {
        //sourceMixer.open();
        for (int i = 0; i < numLines; i++) {
            if (p[i] == null) {
                checkMixerSourceLines(sourceMixer, numLines);
            }
            //sdl[i] = p[i].open(playbackAudioFormat, latencyTime);
            sdl[i] = p[i].open(playbackAudioFormat);
            log("Playback open");
        }
        playbackDeviceOpen = true;
    }

    private void resetLevels() {
        for (int j = 0; j < numTotalChannels; j++) {
            levels[j] = 0;
        }
    }

    public void prepareRecording() throws AudioControllerException {
        try {
            if (recordingOutputStreams != null) {
                prepareRecording(false);
            } else {
                if (!prepareRecording(false)) {
                    String msg = new String();
                    for (int i = 0; i < numLines; i++) {
                        File recordingFile = recordingFiles[i];
                        if (recordingFile.exists()) {
                            msg = msg.concat("'"
                                    + recordingFile.getCanonicalPath()
                                    + "' already exists !");
                        }
                    }
                    throw new IOException(
                            msg
                                    + "\nSet overwrite option to overwrite automatically.");
                }
            }
        } catch (IOException e) {
            throw new AudioControllerException(e);
        }
    }

    public boolean prepareRecording(boolean forceOverwrite)
            throws AudioControllerException {
        if (!captureDeviceOpen) {

            try {
                openCapture();
            } catch (Exception e) {
                throw new AudioControllerException(e);
            }

        }
        if (targetMixer != null
                && targetMixer.isSynchronizationSupported(tdl, true))
            targetMixer.synchronize(tdl, true);

        if (recordingOutputStreams != null) {
            //recordingOutputStreams = new
            // VectorBufferedOutputStream[numLines];
            for (int i = 0; i < numLines; i++) {
                //recordingOutputStreams[i] = new VectorBufferedOutputStream();
                try {
                    c[i].prepareToRecord(recordingOutputStreams[i]);
                } catch (IOException e) {
                    throw new AudioControllerException(e);
                }
            }
        } else {
            if (recordingFiles == null) {
                throw new AudioControllerException("Files have not been set.");
            }
            if (!forceOverwrite) {
                for (int i = 0; i < numLines; i++) {
                    File recordingFile = recordingFiles[i];
                    if (!overwrite && recordingFile.exists()) {
                        return false;
                    }
                }
            }
            for (int i = 0; i < numLines; i++) {
                try {
                    c[i].prepareToRecord(recordingFiles[i]);
                } catch (IOException e) {
                    throw new AudioControllerException(e);
                }
            }
        }
        captureStatus.setStatus(CaptureStatus.PREPARED);
        updateListeners(null, captureStatus);
        return true;
    }

    public void startRecording() {
        for (int i = 0; i < numLines; i++) {
            //c[i].starttargetDataLine();
            c[i].startRecording();
        }
        captureStatus.setStatus(CaptureStatus.RECORDING);
        updateListeners(null, captureStatus);

    }

    public void stopRecording() throws AudioControllerException {
        Exception captureException = null;

        for (int i = 0; i < numLines; i++) {
            try {
                c[i].stopRecording();
            } catch (IOException e) {
                if (captureException == null)
                    captureException = e;
            }
        }

        if (targetMixer != null
                && targetMixer.isSynchronizationSupported(tdl, true))
            targetMixer.unsynchronize(tdl);

        captureStatus.setStatus(CaptureStatus.RECORDED);
        updateListeners(null, captureStatus);
        if (mode == OPEN_ON_DEMAND) {
            closeCapture();
        }
        captureStatus.setStatus(CaptureStatus.SAVED);
        updateListeners(null, captureStatus);
        //	TODO this is not correct behavoiur
        if (captureException != null)
            throw new AudioControllerException(captureException);
    }

    public synchronized void played(Playback pb) {

        boolean played = true;
        for (int i = 0; i < numLines; i++) {
            if (p[i].isPlaying()) {
                played = false;
                if (DEBUG)
                    log("Playing: " + i + " still playing...");
            }
        }
        if (played) {
            if (sourceMixer != null
                    && sourceMixer.isSynchronizationSupported(sdl, true)) {
                sourceMixer.unsynchronize(sdl);
            }
            synchronized (playbackLock) {
                playbackLock.notifyAll();
            }

            playbackStatus.setStatus(PlaybackStatus.PLAYED);
            updateListeners(playbackStatus, null);
            if (mode == OPEN_ON_DEMAND) {
                closePlayback();
            }
        }
    }

    //	/**
    //	 * synchronized playing of all lines
    //	 * blocks until playback is finished !
    //	 */
    //	public void play() {
    //		//if (ps.getStatus() == PlayStatus.PLAYING) return;
    //		// startSourceDataLine();
    //		played = false;
    //		for (int i = 0; i < numLines; i++) {
    //			p[i].play();
    //		}
    //		if (DEBUG)
    //			log("playing ...");
    //		synchronized (playbackLock) {
    //			while (!played) {
    //				try {
    //					playbackLock.wait();
    //				} catch (InterruptedException e) {
    //				}
    //			}
    //		}
    //		if (DEBUG)
    //			log("finished playing.");
    //		if (sourceMixer != null)
    //			sourceMixer.unsynchronize(sdl);
    //
    //		return;
    //
    //	}

    public void preparePlayback() throws AudioControllerException {

        AudioFormat fallBackPlaybackAudioFormat = playbackAudioFormat;
        AudioFormat paf = null;
        try {
            if (playbackInputStreams != null) {
                paf = AudioSystem.getAudioFileFormat(playbackInputStreams[0])
                        .getFormat();

            } else {
                if (audioSources == null)
                    throw new AudioControllerException(
                            "files have not been set.");
                paf = audioSources[0].getAudioInputStream().getFormat();
            }

            if (!paf.matches(playbackAudioFormat)) {
                playbackAudioFormat = paf;
                close();
                configure();
                open();
            }
            if (!playbackDeviceOpen) {
                openPlayback();
            }

            for (int i = 0; i < numLines; i++) {
                // Look for cached audio files
                if (playbackInputStreams != null) {
                    p[i].prepareToPlay(playbackInputStreams[i]);
                } else {
                    p[i].prepareToPlay(audioSources[i]);
                }
            }
            if (sourceMixer != null
                    && sourceMixer.isSynchronizationSupported(sdl, true))
                sourceMixer.synchronize(sdl, true);
        } catch (Exception e) {
            playbackAudioFormat = fallBackPlaybackAudioFormat;
            throw new AudioControllerException(e);
        }

        playbackStatus.setStatus(PlaybackStatus.PREPARED);
        updateListeners(playbackStatus, null);

    }

    /**
     * gets current normalized linear audio levels maximum level is 1.0
     * representing 0dB minimum level is 0 array has channels * numLines entries
     */
    public float[] getLevels() {

        if (captureStatus.getStatus() == CaptureStatus.RECORDING
                || captureStatus.getStatus() == CaptureStatus.CAPTURING) {
            for (int i = 0; i < numLines; i++) {
                float[] lineLevels = c[i].getLevels();
                for (int k = 0; k < lineLevels.length; k++) {
                    int ik = i * numLineChannels + k;
                    levels[ik] = lineLevels[k];
                }
            }
            return levels;
        } else if (playbackStatus.getStatus() == PlaybackStatus.PLAYING) {
            for (int i = 0; i < numLines; i++) {
                if (p[i] == null)
                    return null;
                float[] lineLevels = p[i].getLevels();
                if (lineLevels == null)
                    return null;
                for (int k = 0; k < lineLevels.length; k++) {
                    int ik = i * numLineChannels + k;
                    levels[ik] = lineLevels[k];
                }
            }
            return levels;
        } else {
            return null;
        }

        /*
         * // print microsecond positions for test for (int i=0;i <numLines;i++) {
         * long p=tdl[i].getMicrosecondPosition(); float pInSec=(float)p /
         * 1000000; if (DEBUG) System.out.println("TDL "+i+" Pos: "+pInSec+"
         * s"); }
         */

    }

    /**
     * closes this session
     */
    public void close() {
        closePlayback();
        if (sourceMixer != null)
            sourceMixer.close();
        closeCapture();
        if (targetMixer != null)
            targetMixer.close();
        playbackStatus.setStatus(PlaybackStatus.CLOSED);
        captureStatus.setStatus(CaptureStatus.CLOSED);
        updateListeners(playbackStatus, captureStatus);
        logger.info("J2AudioController closed.");

    }

    protected void closePlayback() {

        if (p != null) {

            for (int i = 0; i < numLines; i++) {
                if (p[i] != null) {
                    p[i].close();
                    //p[i] = null; // sdl is now nulled
                }
            }
            //playbackStatus.setStatus(PlaybackStatus.CONFIGURED);
            //updateListeners(playbackStatus, null);
        }

        playbackDeviceOpen = false;
    }

    protected void closeCapture() {

        if (c != null) {
            for (int i = 0; i < numLines; i++) {
                if (c[i] != null) {
                    c[i].close();
                    //c[i] = null;
                }
            }
            //captureStatus.setStatus(CaptureStatus.CONFIGURED);
            //updateListeners(null, captureStatus);
        }
        //busy = false;

        captureDeviceOpen = false;
    }

    public synchronized void addAudioControllerListener(
            AudioControllerListener acl) {
        if (acl != null && !listenerList.contains(acl)) {
            listenerList.addElement(acl);
        }
    }

    public synchronized void removeAudioControllerListener(
            AudioControllerListener acl) {
        if (acl != null) {
            listenerList.removeElement(acl);
        }
    }

    protected synchronized void updateListeners(PlaybackStatus pe,
            CaptureStatus ce) {
        Iterator<AudioControllerListener> it = listenerList.iterator();
        while (it.hasNext()) {
            AudioControllerListener listener =  it.next();
            listener.update(pe, ce);
        }
    }

    /**
     * get current selected audio format
     */
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    private void log(String msg) {
        if (DEBUG)
            System.out.println(this.getClass().getName() + ": " + msg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#startPlayback()
     */
    public synchronized void startPlayback() throws AudioControllerException {
        boolean started = false;
        for (int i = 0; i < numLines; i++) {
            started |= p[i].startPlayback();
        }
        if (started) {
            playbackStatus.setStatus(PlaybackStatus.PLAYING);

        } else {
            playbackStatus.setStatus(PlaybackStatus.PLAYED);
        }
        updateListeners(playbackStatus, null);
    }

    /**
     * Plays the audio files(s). (Blocking!) Blocks until the whole file is
     * played.
     * 
     * @throws AudioControllerException
     */
    public void play() throws AudioControllerException {
        startPlayback();
        playbackStatus.waitForNot(PlaybackStatus.PLAYING);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#pausePlayback()
     */
    public void pausePlayback() {
        boolean paused = true;
        for (int i = 0; i < numLines; i++) {
            paused &= p[i].pausePlayback();
        }
        if (paused) {
            playbackStatus.setStatus(PlaybackStatus.PAUSE);
            updateListeners(playbackStatus, null);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#stopPlayback()
     */
    public void stopPlayback() {
        if (p == null)
            return;
        for (int i = 0; i < numLines; i++) {
            if (p[i] != null)
                p[i].stopPlayback();
        }
        // wait for all playbacks to calling the played() method

    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#startCapture()
     */
    public void startCapture() {
        //if (c==null) openCapture();
        for (int i = 0; i < numLines; i++) {
            //c[i].starttargetDataLine();
            c[i].startCapturing();
        }
        captureStatus.setStatus(CaptureStatus.CAPTURING);
        updateListeners(null, captureStatus);

    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#stopCapture()
     */
    public void stopCapture() {

        for (int i = 0; i < numLines; i++) {
            c[i].stopCapturing();
        }

        captureStatus.setStatus(CaptureStatus.OPEN);
        updateListeners(null, captureStatus);
        if (mode == OPEN_ON_DEMAND) {
            closeCapture();
        }
    }

    public synchronized void stop() throws IOException {
        stopCapture();
        stopPlayback();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#startDuplex()
     */
    public void startDuplex() throws AudioControllerException {
        // TODO startDuplex not implemented yet
        throw new AudioControllerException(
                "Duplex mode not implemented yet ! Sorry!");

    }

    public long getPlaybackFrameLength() {

        long l = 0;
        // get maximum of all lines
        for (int i = 0; i < numLines; i++) {
            if (p[0] != null) {
                if (l < p[i].getFrameLength()) {
                    l = p[i].getFrameLength();
                }
            }
        }
        return l;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getPlaybackFramePosition()
     */
    public long getPlaybackFramePosition() {
        if (p == null)
            return -1;
        if (p[0] == null)
            return -1;
        return p[0].getFramePosition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getRecordingFramePosition()
     */
    public synchronized long getRecordingFramePosition() {
        if (c == null || c[0] == null)
            return 0;
        return c[0].getFramePosition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#setPlaybackFramePosition(long)
     */
    public long setPlaybackFramePosition(long newPosition) {

        if (p != null) {
            for (int i = 0; i < numLines; i++) {

                try {
                    if (p[i] == null)
                        return -1;

                    p[i].setFramePosition(newPosition);

                } catch (IOException e) {
                    return -1;
                }
            }
            long resultPosition = p[0].getFramePosition();
            for (int i = 1; i < numLines; i++) {
                if (p[i].getFramePosition() != resultPosition) {
                    return -1;
                }
            }
            return resultPosition;
        } else {
            return -1;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#setSettings()
     */
    public void setSettings() {

        if (uiFrame == null) {
            if (acUi == null)
                acUi = new AudioControllerUI(this);
            uiFrame = new JFrame(acUi.getTitle());
            uiFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            uiFrame.getContentPane().add(acUi,BorderLayout.CENTER);
            uiFrame.setLocationRelativeTo(null);

        } else {
            acUi.update();
            int newState = uiFrame.getExtendedState() & ~JFrame.ICONIFIED;
            uiFrame.setExtendedState(newState);

        }
        Runnable packAndShow = new Runnable() {
            public void run() {
                uiFrame.pack();
                uiFrame.toFront();
                uiFrame.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(packAndShow);
    }

    /**
     * Get status of capture engine.
     * 
     * @return capture status
     */
    public synchronized CaptureStatus getCaptureStatus() {
        return captureStatus;
    }

    /**
     * Get status of playback engine.
     * 
     * @return playback status
     */
    public synchronized PlaybackStatus getPlaybackStatus() {
        return playbackStatus;
    }

    /**
     * @deprecated Use getPlaybackURLs instead !
     * @return playback files array (returns alaways null !)
     */
    public File[] getPlaybackFiles() {

        if (audioSources == null)
            return null;
        File[] files = new File[audioSources.length];
        for (int i = 0; i < audioSources.length; i++) {
            if (!(audioSources[i] instanceof FileAudioSource)) {
                return null;
            }
            files[i] = ((FileAudioSource) audioSources[i]).getFile();
        }

        return files;
    }

    /**
     * Get recording files.
     * 
     * @return recording files array
     */
    public File[] getRecordingFiles() {
        return recordingFiles;
    }

    private void newPlaybackFilesSet() {
        // Problems with old files
        //setPlaybackFramePosition(0);
        setPlaybackStartFramePosition(0);
        setPlaybackStopFramePosition(AudioController.AUDIO_END);
        updateListeners(new PlaybackStatus(PlaybackStatus.FILES_SET), null);
    }

    /**
     * Set playback files.
     * 
     * @param files
     */
    public void setPlaybackFiles(File[] files) {
        playbackInputStreams = null;
        if (files == null) {
            audioSources = null;
            return;
        }
        audioSources = new AudioSource[files.length];
        for (int i = 0; i < audioSources.length; i++) {
            audioSources[i] = new FileAudioSource(files[i]);
        }
        newPlaybackFilesSet();
    }

    /**
     * Set playback URL's.
     * 
     * @param urls
     */
    public void setPlaybackURLs(URL[] urls) {
        playbackInputStreams = null;
        if (urls == null) {
            audioSources = null;
            return;
        }
        audioSources = new AudioSource[urls.length];
        for (int i = 0; i < audioSources.length; i++) {
            audioSources[i] = new URLAudioSource(urls[i]);
        }
        newPlaybackFilesSet();
    }

    /**
     * Set playback audio sources.
     * 
     * @param audioSources sources for playback
     */
    public void setPlaybackAudioSources(AudioSource[] audioSources) {
        playbackInputStreams = null;
        this.audioSources = audioSources;
        newPlaybackFilesSet();
    }

    /**
     * Get playback audio sources.
     * 
     * @return playback audio sources
     */
    public AudioSource[] getPlaybackAudioSources() {
        return audioSources;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#setPlaybackInputStreams(java.io.InputStream[])
     */
    public void setPlaybackInputStreams(InputStream[] inputStreams) {
        playbackInputStreams = inputStreams;
        audioSources = null;
        setPlaybackFramePosition(0);
        setPlaybackStartFramePosition(0);
        setPlaybackStopFramePosition(AudioController.AUDIO_END);
        updateListeners(new PlaybackStatus(PlaybackStatus.FILES_SET), null);
    }

    /**
     * @param files
     */
    public void setRecordingFiles(File[] files) {
        recordingOutputStreams = null;
        recordingFiles = files;
        updateListeners(null, new CaptureStatus(CaptureStatus.FILES_SET));
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#setRecordingURLs(java.net.URL[])
     */
    public void setRecordingOutputStreams(OutputStream[] streams) {
        recordingFiles = null;

        recordingOutputStreams = streams;

    }

    public OutputStream[] getRecordingOutputStreams() {
        return recordingOutputStreams;

    }

//    /**
//     * @return the playback mixer
//     */
//    private Mixer getSourceMixer() {
//        return sourceMixer;
//    }

//    /**
//     * @return the capture mixer
//     */
//    public Mixer getTargetMixer() {
//        return targetMixer;
//    }

    /**
     * @param format
     */
    public synchronized void setAudioFormat(AudioFormat format) {
        audioFormat = format;
        playbackAudioFormat = format;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#isPlaybackRandomPositioningSupported()
     */
    public boolean isPlaybackRandomPositioningSupported() {
        return true;
    }

    /**
     * Sets playback mixer.
     * 
     * @param mixer
     */
    public void setSourceMixer(Mixer mixer) {
        reqSourceMixer = mixer;
        reqSourceMixerInfo = null;
        reqSourceMixerName = null;
    }

    /**
     * Sets recording mixer.
     * 
     * @param mixer
     */
    public void setTargetMixer(Mixer mixer) {
        reqTargetMixer = mixer;
        reqTargetMixerInfo = null;
        reqTargetMixerName = null;
    }

    public void setTargetMixerName(String targetMixerName) {
        this.reqTargetMixerName = targetMixerName;
        reqTargetMixerInfo = null;
        reqTargetMixer = null;
    }

    /**
     * Returns mode flags.
     * 
     * @return mode
     */
    public long getMode() {

        return mode;
    }

    /**
     * Creates a controller. Usage: java J2AudioController [-f][-i][-r][-p]
     * [recordfile] Example: <code>java J2AudioController r</code> records to
     * Untitled.wav 44100kHz,16bit, stereo
     */
    public static void main(String args[]) {

        final J2AudioController ac = new J2AudioController();
        String[] params;

        OptionParser op = new OptionParser();
        op.addOption("p");
        op.addOption("r");
        op.addOption("f", "");
        op.addOption("i");
        op.addOption("d");

        try {
            op.parse(args);
        } catch (OptionParserException e1) {
            System.err.println(e1.getLocalizedMessage());
        }

        params = op.getParams();
        int numLines = 1;
        File[] recFiles = null;
        if (op.isOptionSet("f")) {
            Option fileOption = op.getOption("f");
            String fileName = fileOption.getParam();
            File[] files = new File[1];
            files[0] = new File(fileName);
            ac.setRecordingFiles(files);
            ac.setPlaybackFiles(files);
        }

        boolean interactive = op.isOptionSet("i");
        if (params.length == 1) {
            numLines = 1;
            recFiles = new File[numLines];
            recFiles[0] = new File(params[0]);
            ac.setRecordingFiles(recFiles);
            ac.setPlaybackFiles(recFiles);
        }
        try {
            if (op.isOptionSet("d")) {
                ac.setMode(FULLDUPLEX);
            } else {

                ac.setMode(OPEN_ON_DEMAND);
            }
            ac.configure();

        } catch (Exception e) {
            ac.close();
            System.err.println("Error opening device: " + e.getMessage());
        }

        LineNumberReader r = new LineNumberReader(new InputStreamReader(
                System.in));

        if (op.isOptionSet("r")) {
            try {
                ac.prepareRecording();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
                ac.close();

            }
            if (interactive) {
                System.out.println("Press Return to record");
                try {
                    r.readLine();
                } catch (Exception e) {
                    System.err.println("Cannot read: " + e);
                }
            }
            ac.startRecording();
            if (interactive) {

                System.out.println("Recording ...");
                System.out.println("Press Return to stop.");

                try {
                    r.readLine();

                } catch (Exception e) {
                    System.err.println("Cannot read: " + e);
                }
            }
            try {
                ac.stopRecording();
            } catch (AudioControllerException e) {
                System.err.println("AudioController Problems: " + e);
            }

        }
        if (op.isOptionSet("p")) {
            if (interactive) {

                System.out.println("Press Return to play.");
                try {
                    r.readLine();
                } catch (Exception e) {
                    System.err.println("Cannot read: " + e);
                }
            }
            try {

                ac.preparePlayback();

                //ac.startPlayback();
                ac.play();
                if (interactive)
                    System.out.println("Playback started.");

            } catch (Exception e) {
                System.err.println("Cannot prepare: " + e);
            }

        }

        ac.close();

        if (interactive)
            System.out.println("Exiting.");
        System.exit(0);

    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#setOverwrite(boolean)
     */
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getOverwrite()
     */
    public boolean isOverwrite() {

        return overwrite;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#setPlaybackStartFramePosition(long)
     */
    public long setPlaybackStartFramePosition(long startPosition) {
        if (p == null)
            return 0;
        for (int i = 0; i < numLines; i++) {
            if (p[i] == null)
                return 0;
            p[i].setStartFramePosition(startPosition);
        }

        return p[0].getStartFramePosition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getPlaybackStartFramePosition()
     */
    public long getPlaybackStartFramePosition() {
        return p[0].getStartFramePosition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#setPlaybackStopFramePosition(long)
     */
    public long setPlaybackStopFramePosition(long stopPosition) {
        if (p == null)
            return AudioController.AUDIO_END;
        for (int i = 0; i < numLines; i++) {
            if (p[i] == null)
                return AudioController.AUDIO_END;
            p[i].setStopFramePosition(stopPosition);
        }
        return p[0].getStopFramePosition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getPlaybackStoptFramePosition()
     */
    public long getPlaybackStopFramePosition() {
        return p[0].getStopFramePosition();

    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.PlaybackListener#update(ipsk.audio.Playback,
     *      ipsk.audio.Playback.PlayerStatus)
     */
    public void update(Playback p, Playback.PlayerStatus ps) {

        if (ps.getStatus() == Playback.PlayerStatus.PLAYING) {
            playbackStatus.setStatus(PlaybackStatus.PLAYING);
            updateListeners(playbackStatus, null);
        }
        if (ps.getStatus() == Playback.PlayerStatus.IDLE) {
            played(p);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getNumLines()
     */
    public int getNumLines() {

        return numLines;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getSourceMixerInfo()
     */
    public Info getSourceMixerInfo() {
        if (sourceMixer ==null)return null;
        return sourceMixer.getMixerInfo();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#setTargetMixer(javax.sound.sampled.Mixer.Info)
     */
    public void setTargetMixer(Info mixerInfo) {
        this.reqTargetMixerInfo = mixerInfo;
        reqTargetMixerName = null;
        reqTargetMixer = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getTargetMixerInfo()
     */
    public Info getTargetMixerInfo() {
        if (targetMixer==null)return null;
        return targetMixer.getMixerInfo();

    }
   
//    private String getTargetMixerName() {
//        if (targetMixer==null)return null;
//        return targetMixer.getMixerInfo().getName();
//
//    }
    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#setMode(long)
     */
    public void setMode(long modeBits) {
        this.mode = modeBits;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.impl.j2audio.CaptureListener#update(ipsk.audio.impl.j2audio.Capture,
     *      ipsk.audio.impl.j2audio.CaptureStatus)
     */
    public void update(Capture src, ipsk.audio.impl.j2audio.CaptureStatus cs) {
        if (cs.getStatus() == ipsk.audio.impl.j2audio.CaptureStatus.ERROR) {
            captureStatus.setException(cs.getException());
            captureStatus.setStatus(CaptureStatus.ERROR);
            updateListeners(null, this.captureStatus);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getPlaybackAudioFormat()
     */
    public AudioFormat getPlaybackAudioFormat() {
        return playbackAudioFormat;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getPropertyNames()
     */
    public String[] getPropertyNames() {
        Enumeration<?> e = properties.propertyNames();
        Vector<Object> names = new Vector<Object>();
        while (e.hasMoreElements()) {
            names.add(e.nextElement());
        }
        return (String[]) (names.toArray(new String[0]));
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getPropertyDescription(java.lang.String)
     */
    public String getPropertyDescription(String propertyName) {
        if (propertyName.equals("CAPTURE_USE_TEMP_FILE")) {
            return "If set the capture engine writes raw data to a temporary file.";
        } else if (propertyName.equals("CAPTURE_PREFERRED_LINE_BUFFER_SIZE")) {
            return "Try to set the size of the capture buffer.\n"
                    + "Set this if you experience buffer overruns (dropouts) on recording.\n"
                    + "128 kBytes for example is a save value on 500 Mhz hosts";
        }else if(propertyName.equals("ASIO_USE_MAX_BUFFER_SIZE")){
        	return "If set to true and if an ASIO JavaSound mixer is used the native ASIO buffer size is set to maximum.";
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#setProperty(java.lang.String,
     *      java.lang.Object)
     */
    public void setProperty(String arg0, String arg1) {
        properties.setProperty(arg0, arg1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioController#getProperty(java.lang.String)
     */
    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

//    /**
//     * @return
//     */
//    private  String getSourceMixerName() {
//        if (sourceMixer ==null)return null;
//        return sourceMixer.getMixerInfo().getName();
//    }

    /**
     * @param string
     */
    public void setSourceMixerName(String string) {
        reqSourceMixerName = string;
        reqSourceMixerInfo = null;
        reqSourceMixer = null;
    }

}
