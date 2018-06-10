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
package ipsk.audio;

import ipsk.audio.impl.j2audio.SynchronizedStatus;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Mixer;

/**
 * Manages audio recording and playback. This interface allows easy recording of
 * playback and recording auf audio. It supports simultanous recording/playback
 * of more than one line. Implementations should try to synchronize this lines.
 * Audio lines and channels are sometimes confused: A line is a way for an audio
 * stream. The audio stream can have one or more channels, determined by the
 * audio format setting. After the number of lines (using
 * {@link #setNumLines(int)}) and the audio format (using
 * {@link #setAudioFormat(AudioFormat)}) are set, you must configure (
 * {@link #configure()}) the controller. If the controller is not able to find
 * matching audio lines it will throw an exception. Before starting playback or
 * recording (or capturing) you have to open the controller. <br>
 * Summary of features:
 * <ul>
 * <li>looks for matching audio mixers and lines
 * <li>configures and opens the lines
 * <li>handles synchronized starting and stopping
 * </ul>
 * 
 * <p>
 * A typical scenario for a session:
 * </p>
 * <ol>
 * <li>Set audio format for the session.
 * <li>Optionally set the mixer and/or number of lines to use.
 * <li>Configure the session.
 * <li>Open the session.
 * <li>Prepare recording.
 * <li>Start recording.
 * <li>Stop recording.
 * <li>Prepare playback.
 * <li>Optionally set start/stop position.
 * <li>Start playback.
 * <li>Pause playback.
 * <li>Start playback.
 * <li>Stop playback or the playback finishes at the end of file(s)
 * <li>Repeat several times from Nr. 5.
 * <li>Close session.
 * </ol>
 * 
 * @version $Id: AudioController.java,v 1.15 2011/04/07 19:49:01 klausj Exp $
 * @author Klaus J&auml;nsch, klausj@phonetik.uni-muenchen.de
 * @deprecated Please use {@link AudioController2} instead.
 */
public interface AudioController {

    /**
     * Represents status of the capture engine.
     * 
     * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
     */
    public class CaptureStatus extends SynchronizedStatus {

        public static final String CLOSED = "Closed";

        public static final String CONFIGURED = "Configured";

        public static final String OPEN = "Opened";

        public static final String PREPARED = "Prepared";

        public static final String PAUSED = "Paused";

        public static final String CAPTURING = "Capturing";

        public static final String RECORDING = "Recording";

        public static final String BUSY = "Busy";

        public static final String RECORDED = "Recorded";

        public static final String SAVED = "Saved";

        public static final String FILES_SET = "Files set";

        public static final String ERROR = "Error";

		public static final String IDLE = "Idle";

        private Exception exception = null;

        /**
         * Create new status object.
         * 
         * @param status
         *            current status
         */
        public CaptureStatus(String status) {
            super(status);
        }

        /**
         * Get exception.
         * @return exception
         */
        public Exception getException() {
            return exception;
        }

        /**
         * Set exception.
         * @param exception
         */
        public void setException(Exception exception) {
            this.exception = exception;
        }

    }

    /**
     * Represents status of the playback engine.
     * 
     * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
     */
    public class PlaybackStatus extends SynchronizedStatus {

        public static final String CLOSED = "Closed";

        public static final String CONFIGURED = "Configured";

        public static final String OPEN = "Open";

        public static final String PREPARED = "Prepared";

        public static final String PAUSE = "Pause";

        public static final String PLAYING = "Playing";

        public static final String PLAYED = "Played";

        public static final String FILES_SET = "Files set";

        /**
         * Creates new playback status object.
         * 
         * @param status
         *            new status
         */
        public PlaybackStatus(String status) {
            super(status);
        }

    }

    public static long AUDIO_END = -1;

    public static long POS_UNKNOWN = -1;

    public static long FULLDUPLEX = 1;

    public static long OPEN_ON_DEMAND = 2;

    public static long DISABLE_CAPTURE = 4;

    public static long HOLD_OPEN = 5;

    /**
     * Use the source mixer (playback) with the given name.
     * 
     * @param mixerName
     *            the name of the source mixer
     */
    public void setSourceMixerName(String mixerName);

    /**
     * Use the source mixer (playback) described by mixerInfo.
     * 
     * @param mixerInfo
     *            the info (name) of the mixer
     */
    public void setSourceMixer(Mixer.Info mixerInfo);

    /**
     * Get current used source mixer (playback) info.
     * 
     * @return mixerInfo the info of the source mixer
     */
    public Mixer.Info getSourceMixerInfo();

    /**
     * Use the target mixer (recording) with the given name.
     * 
     * @param mixerName
     *            the name of the target mixer
     */
    public void setTargetMixerName(String mixerName);

    /**
     * Use the target mixer (recording) described by mixerInfo.
     * 
     * @param mixerInfo
     *            the info of the target mixer
     */
    public void setTargetMixer(Mixer.Info mixerInfo);

    /**
     * Get current used target mixer (recording) info.
     * 
     * @return mixerInfo the info (name) of the target mixer
     */
    public Mixer.Info getTargetMixerInfo();

   
    
    
    
    public void setMode(long modeBits);

    public long getMode();

    /**
     * Use numLines lines. For each line a recording file will be created.
     * 
     * @param numLines
     *            number of lines to use
     */
    public void setNumLines(int numLines);

    /**
     * Get number of lines.
     * 
     * @return number of lines to use
     */
    public int getNumLines();

    /**
     * Use audio format audioFormat..
     * 
     * @param audioFormat
     *            audio format
     */
    public void setAudioFormat(AudioFormat audioFormat);

    /**
     * Get audio format.
     * 
     * @return audio format
     */
    public AudioFormat getAudioFormat();

    /**
     * Get audio format for playback. The controller sets the audio format
     * according to the set playback files. So it can differ from the chosen
     * foprmat.
     * 
     * @return audio format
     */
    public AudioFormat getPlaybackAudioFormat();

    /**
     * Configures a session.
     * 
     * @throws AudioControllerException
     */
    public void configure() throws AudioControllerException;

    /**
     * Opens the session.
     * 
     * @throws AudioControllerException
     */
    public void open() throws AudioControllerException;

    /**
     * Closes this session.
     * 
     * @throws AudioControllerException
     */
    public void close() throws AudioControllerException;

    /**
     * Sets playback files to use.
     * <p>
     * the size of the array must equal the number of lines opened.
     * 
     * @param playbackFiles
     *            the file array to play
     */
    public void setPlaybackFiles(File[] playbackFiles);

    /**
     * Gets playback files.
     * <p>
     * the size of the array is equal the number of lines opened.
     * 
     * @return the file array of playbackfiles set
     */
    public File[] getPlaybackFiles();

    /**
     * Sets playback URL's to use.
     * <p>
     * the size of the array must equal the number of lines opened.
     * 
     * @param urls
     *            the URL array to play
     */
    public void setPlaybackURLs(URL[] urls);

    /**
     * Sets playback Inputstreams to use.
     * <p>
     * the size of the array must equal the number of lines opened.
     * 
     * @param inputStreams
     *            the inputstream array to play
     */
    public void setPlaybackInputStreams(InputStream[] inputStreams);

    public void setPlaybackAudioSources(AudioSource[] sources);

    /**
     * Sets overwrite flag. If the overwrite flag is set, existing recording
     * files are overwritten without any exception or warning.
     * 
     * @param overwrite
     *            overwrite flag
     */
    public void setOverwrite(boolean overwrite);

    /**
     * Gets overwrite flag. If the overwrite flag is set, existing recording
     * files are overwritten without any exception or warning.
     * 
     * @return overwrite flag
     */
    public boolean isOverwrite();

    /**
     * Sets recording files to use.
     * <p>
     * the size of the array must equal the number of lines opened.
     * 
     * @param recordingFiles
     */
    public void setRecordingFiles(File[] recordingFiles);

    /**
     * Gets recording files.
     * <p>
     * the size of the array is equal the number of lines opened.
     * 
     * @return recordingFiles
     */
    public File[] getRecordingFiles();

    /**
     * Sets recording OutputStreams to use.
     * <p>
     * the size of the array must equal the number of lines opened.
     * 
     * @param streams
     *            recording OutputStreams
     */
    public void setRecordingOutputStreams(OutputStream[] streams);

    /**
     * Prepares playback.
     * 
     * @throws AudioControllerException
     */
    public void preparePlayback() throws AudioControllerException;

    /**
     * Prepares recording.
     * 
     * @throws AudioControllerException
     */
    public void prepareRecording() throws AudioControllerException;

    /**
     * Starts the playback.
     * 
     * @throws AudioControllerException
     */
    public void startPlayback() throws AudioControllerException;

    /**
     * Starts the capturing of data. Can be used to capture data from the line
     * without writing to files
     * 
     * @throws AudioControllerException
     */
    public void startCapture() throws AudioControllerException;

    /**
     * Starts recording to the given files.
     * 
     * @throws AudioControllerException
     * @see #prepareRecording()
     */
    public void startRecording() throws AudioControllerException;

    /**
     * Pauses playback.
     * 
     * @throws AudioControllerException
     *  
     */
    public void pausePlayback() throws AudioControllerException;

    /**
     * Stops playback.
     * 
     * @throws AudioControllerException
     *  
     */
    public void stopPlayback() throws AudioControllerException;

    /**
     * Stops capturing.
     * 
     * @throws AudioControllerException
     *  
     */
    public void stopCapture() throws AudioControllerException;

    /**
     * Stops recording. the method blocks until all files are written
     * 
     * @throws AudioControllerException
     */
    public void stopRecording() throws AudioControllerException;

    /**
     * Gets levels of all channels. maximum level is 1.0 representing 0dB <br>
     * minimum level is 0 <br>
     * array has channels * numLines entries
     * 
     * @return current audio levels
     */
    public float[] getLevels();

    /**
     * Get random positioning capability of the playback engine.
     * 
     * @return random positioning capability
     */
    public boolean isPlaybackRandomPositioningSupported();

    /**
     * Gets length of largest playback file in frames, -1 if not available
     * 
     * @return length of largest playback file in frames
     */
    public long getPlaybackFrameLength();

    /**
     * Sets the new frame position of playback.
     * 
     * @param newPosition
     *            the requested position
     * @return the actually set position or POS_UNKNOWN (-1) if not supported
     */
    public long setPlaybackFramePosition(long newPosition);

    /**
     * Get playback position.
     * 
     * @return current playback position in audio frames or POS_UNKNOWN (-1) if
     *         not supported
     */
    public long getPlaybackFramePosition();

    /**
     * Sets the start position. Used to play only a segment of an audio file.
     * 
     * @param startPosition
     *            position in audio frames from where to start
     * @return the actually set start position
     */
    public long setPlaybackStartFramePosition(long startPosition);

    /**
     * Gets the start position.
     * 
     * @return start position
     */
    public long getPlaybackStartFramePosition();

    /**
     * Sets the stop position. Used to play only a segment of an audio file. To
     * play the whole audio file set the position to AUDIO_END (-1).
     * 
     * @param stopPosition
     *            position in audio frames to stop
     * @return the actually set stop position
     */
    public long setPlaybackStopFramePosition(long stopPosition);

    /**
     * Gets the stop position.
     * 
     * @return stop position
     */
    public long getPlaybackStopFramePosition();

    /**
     * Get recording position.
     * 
     * @return current recording position in audio frames or -1 if not supported
     */
    public long getRecordingFramePosition();

    /**
     * Get an implementation specific property names.
     * 
     * @return property names
     */
    public String[] getPropertyNames();

    /**
     * Get a description of the property.
     * 
     * @param propertyName
     * @return description
     */
    public String getPropertyDescription(String propertyName);

    /**
     * Set a property for a specific implementation.
     * 
     * @param propertyName
     * @param value
     */
    public void setProperty(String propertyName, String value);

    /**
     * Get an implementation specific property.
     * 
     * @param propertyName
     * @return value of the property or null if there is no such property
     */
    public String getProperty(String propertyName);

    /**
     * Call the graphical user interface of the controller implementation to set
     * the configuration.
     *  
     */
    public void setSettings();

    /**
     * Gets current status of the capturer.
     * 
     * @return capture status
     */
    public CaptureStatus getCaptureStatus();

    /**
     * Gets current status of playback.
     * 
     * @return playback status
     */
    public PlaybackStatus getPlaybackStatus();

    /**
     * Adds controller listener.
     * 
     * @param acl
     *            controller listener
     */
    public void addAudioControllerListener(AudioControllerListener acl);

    /**
     * Removes controller listener.
     * 
     * @param acl
     *            controller listener
     */
    public void removeAudioControllerListener(AudioControllerListener acl);

}
