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

import ipsk.audio.capture.PrimaryRecordTarget;
import ipsk.audio.dsp.LevelInfo;

import java.io.File;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Mixer;

/**
 * Manages audio recording and playback. This interface allows easy recording of
 * playback and recording auf audio.
 * After the number of lines (using
 *
 * <p>
 * A typical scenario for a session:
 * </p>
 * <ol>
 * <li>Set audio file format for the session.
 * <li>Optionally set the devices to use.
 * <li>Open for recording.
 * <li>Start recording.
 * <li>Stop recording.
 * <li>Wait for CaptureRecordedEvent.
 * <li>Close capture.
 * <li>Open playback.
 * <li>Optionally set start/stop position.
 * <li>Start playback.
 * <li>Optionally pause playback.
 * <li>Start playback again.
 * <li>Stop playback or the playback finishes at the end of file(s)
 * <li>Wait for PlaybackStopEvent and close playback.
 * </ol>
 * 
 * @author Klaus J&auml;nsch, klausj@phonetik.uni-muenchen.de
 */
public interface AudioController2 {

    
//    public static enum PrimaryRecordTarget {DIRECT,TEMP_RAW_FILE}
    
//	public final static int OPTIMIZATION_OFF=0;
//	public final static int OPTIMIZE_FOR_RELIABILITY=1;
	
    /**
     * Alle events thrown by the controller must implement this interface.
     *
     */
   public interface AudioControllerEvent{}
   
   /**
    * Audio controller listener interface.
    *
    */
   public interface AudioController2Listener {

       public void update(AudioControllerEvent ace);
       
   }
  
   /**
    * Get infos of available playback mixers.
    * @return playback mixer infos
    * @throws AudioControllerException
    */
   public Mixer.Info[] getPlaybackMixerInfos() throws AudioControllerException;
   
   /**
    * Get infos of available capture mixers.
    * @return capture mixer infos
    * @throws AudioControllerException
    */
   public Mixer.Info[] getCaptureMixerInfos() throws AudioControllerException;
   
    /**
     * Use the source mixer (playback) with the given name.
     * 
     * @param deviceName
     *            the name of the source mixer
     * @throws AudioControllerException
     */
    public void setPlaybackDeviceByName(String deviceName) throws AudioControllerException;


    /**
     * Use the target mixer (recording) with the given name.
     * 
     * @param deviceName
     *            the name of the target mixer
     * @throws AudioControllerException
     */
    public void setCaptureDeviceByName(String deviceName) throws AudioControllerException;

    public boolean supportsDeviceProviders();
    public enum DeviceType {CAPTURE,PLAYBACK};
    public DeviceInfo  convertLegacyDeviceName(String deviceName) throws AudioControllerException;
    /**
     * Get available device providers.
     * @return device provider
     * @throws AudioControllerException
     */
    public List<? extends DeviceProviderInfo> getDeviceProviderInfos() throws AudioControllerException;
    public DeviceProvider getInstance(DeviceProviderInfo providerInfo) throws AudioControllerException;
    public void setPlaybackDeviceByInfo(DeviceInfo deviceInfo) throws AudioControllerException;


    
    public void setCaptureDeviceByinfo(DeviceInfo deviceInfo) throws AudioControllerException;
    /**
     * Set audio file format for recording.
     * 
     * @param audiofileFormat
     *            audio file format
     */
    public void setRecordingAudioFileFormat(AudioFileFormat audiofileFormat);

    /**
     * Get audio format.
     * 
     * @return audio format
     */
    public AudioFileFormat getAudioFileFormat();

    /**
     * Get audio format for playback. The controller sets the audio format
     * according to the set playback files. So it can differ from the chosen
     * foprmat.
     * 
     * @return audio format
     */
    public AudioFormat getPlaybackAudioFormat();

   
    /**
     * Opens the playback line.
     * 
     * @throws AudioControllerException
     */
    public void openPlayback() throws AudioControllerException;

    /**
     * Closes the playback line.
     * 
     * @throws AudioControllerException
     */
    public void closePlayback() throws AudioControllerException;

    
    /**
     * Opens the capture line.
     * 
     * @throws AudioControllerException
     */
    public void openCapture() throws AudioControllerException;

    /**
     * Closes the capture line.
     * 
     * @throws AudioControllerException
     */
    public void closeCapture() throws AudioControllerException;

    
    
    /**
     * Set playback file.
     * 
     * @param playbackFile
     *            the file to play
     * @throws AudioControllerException
     */
    public void setPlaybackFile(File playbackFile) throws AudioControllerException;

    /**
     * Gets playback files.
     * <p>
     * the size of the array is equal the number of lines opened.
     * 
     * @return the file array of playbackfiles set
     */
    public File getPlaybackFile();

  

    public void setPlaybackAudioSource(AudioSource source) throws AudioControllerException;

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
     * Set primary record target. 
     * 
     * @param primaryRecordTarget
     */
    public void setPrimaryRecordTarget(PrimaryRecordTarget primaryRecordTarget);
    
    /**
    * Get the primary recording target.
    *
    **/
    public PrimaryRecordTarget getPrimaryRecordTarget();
    
    /**
     * Sets recording file.
     * 
     * @param recordingFile
     */
    public void setRecordingFile(File recordingFile);
    /**
     * Gets recording files.
     * <p>
     * the size of the array is equal the number of lines opened.
     * 
     * @return recordingFiles
     */
    public File getRecordingFile();

//    /**
//     * Sets recording OutputStreams to use.
//     * <p>
//     * the size of the array must equal the number of lines opened.
//     * 
//     * @param streams
//     *            recording OutputStreams
//     */
//    public void setRecordingOutputStreams(OutputStream[] streams);

   
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
     * @see #openCapture()
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
     * Stops recording.
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
    public LevelInfo[] getCaptureLevelInfos();

    
    public LevelInfo[] getPlaybackLevelInfos();
    
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
     * @throws AudioControllerException
     */
    public long setPlaybackFramePosition(long newPosition) throws AudioControllerException;

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
     */
    public void setPlaybackStartFramePosition(long startPosition);

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
     */
    public void setPlaybackStopFramePosition(long stopPosition);

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
    public long getCaptureFramePosition();

    /**
     * Check if capture engine is open.
     * 
     * @return true if open
     */
    public boolean isCaptureOpen();

//    /**
//     * A hint for implentations for optimization of capturing.  
//     * @param captureOptimizationMode
//     */
//    public void setCaptureOptimizationMode(int captureOptimizationMode);
//    
//    /**
//     * A hint for implentations for optimization of playback.  
//     * @param playbackOptimizationMode
//     */
//    public void setPlaybackOptimizationMode(int playbackOptimizationMode);
//    
    
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
     * Adds controller listener.
     * 
     * @param acl
     *            controller listener
     */
    public void addAudioController2Listener(AudioController2Listener acl);

    /**
     * Removes controller listener.
     * 
     * @param acl
     *            controller listener
     */
    public void removeAudioController2Listener(AudioController2Listener acl);
    
    /**
     * Get preferred capture line buffer size.
     * @return line buffer size in milliseconds
     */
    public float getPreferredCaptureLineBufferSizeMilliSeconds();

    /**
     * Set preferred capture line buffer size.
     * @param preferredCaptureLineBufferSizeMilliSeconds line buffer size in milliseconds
     */
    public void setPreferredCaptureLineBufferSizeMilliSeconds(
            float preferredCaptureLineBufferSizeMilliSeconds);
    
    /**
     * Get preferred playback line buffer size.
     * @return line buffer size in milliseconds
     */
    public float getPreferredPlaybackLineBufferSizeMilliSeconds();

    /**
     * Set preferred playback line buffer size.
     * @param preferredPlaybackLineBufferSizeMilliSeconds line buffer size in milliseconds
     */
    public void setPreferredPlaybackLineBufferSizeMilliSeconds(
            float preferredPlaybackLineBufferSizeMilliSeconds);
    
    /**
     * Set max recording length.
     * @param maxFrameLength max recording length in frames, null for infinite recording
     */
    public void setMaxRecordingFrameLength(Long maxFrameLength);
    /**
     * Get max recording length.
     * Default is infinite recording (null).
     * @return max recording length in frames, null for infinite recording
     */
    public Long getMaxRecordingFrameLength();
    
    /**
     * Get file transition capability.
     * File transition recording is a feature, which allows to change the recording file during capture.
     * The capture stream is not interrupted. The recording files can be concatened later to get one seamless recording file.     
     * @return true if file transitioning is supported by this controller
     */
    public boolean isFileTransitionRecordingSupported();
    
    /**
     * Get support for recording session info file handling.
     * @return true if recording session info file handling is supported
     */
    public boolean isSessionInfoHandlingSupported();
    
    /**
     * Get the current used session info file.
     * @return session info file, null if not used (default)
     */
    public File getRecordingSessionInfoFile();

    /**
     * Set the current used session info file.
     * @param recordingSessionInfoFile session info file, null if not used (default)
     */
    public void setRecordingSessionInfoFile(File recordingSessionInfoFile);

    
}
