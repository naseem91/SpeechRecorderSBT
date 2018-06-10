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
 * Date  : Feb 9, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.impl.j2audio2;

import ipsk.audio.AudioController2;

import ipsk.audio.AudioControllerException;
import ipsk.audio.AudioSource;
import ipsk.audio.DeviceInfo;
import ipsk.audio.DeviceProvider;
import ipsk.audio.DeviceProviderInfo;
import ipsk.audio.FileAudioSource;
import ipsk.audio.capture.Capture;
import ipsk.audio.capture.CaptureException;
import ipsk.audio.capture.CaptureListener;
import ipsk.audio.capture.PrimaryRecordTarget;
import ipsk.audio.capture.event.CaptureEvent;
import ipsk.audio.dsp.LevelInfo;
import ipsk.audio.mixer.MixerManager;
import ipsk.audio.player.Player;
import ipsk.audio.player.PlayerException;
import ipsk.audio.player.PlayerListener;
import ipsk.audio.player.event.PlayerEvent;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;


/**
 * Audiocontroller implementation.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class J2AudioController2 implements AudioController2, PlayerListener,
		CaptureListener {


	private Vector<AudioController2Listener> listeners;

	private Player player;

	private Capture capture;

	private MixerManager mm;
    
    private float preferredCaptureLineBufferSizeMilliSeconds;

	private float preferredPlaybackLineBufferSizeMilliSeconds;
	
	private Logger logger;

	/**
	 * 
	 */
	public J2AudioController2() {
		super();
		String packageName=getClass().getPackage().getName();
		logger=Logger.getLogger(packageName);
		logger.setLevel(Level.INFO);
		listeners = new Vector<AudioController2Listener>();

		player = new Player();
		player.addPlayerListener(this);
		capture = new Capture();
		capture.addCaptureListener(this);
		logger.info("J2AudioController2 created");
	}

	public synchronized Mixer.Info[] getPlaybackMixerInfos()
			throws AudioControllerException {
		if (mm == null){
				mm = new MixerManager();
			}
		try {
			return mm.getPlaybackMixerInfos();
		} catch (LineUnavailableException e1) {
			throw new AudioControllerException(
					"Could not get playback mixer infos.", e1);
		}
	}

	public synchronized Mixer.Info[] getCaptureMixerInfos()
			throws AudioControllerException {
		if (mm == null){
				mm = new MixerManager();
		}
		try {
			return mm.getCaptureMixerInfos();
		} catch (LineUnavailableException e1) {
			throw new AudioControllerException(
					"Could not get capture mixer infos.", e1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setPlaybackDeviceByName(java.lang.String)
	 */
	public synchronized void setPlaybackDeviceByName(String deviceName)
			throws AudioControllerException {
		if (mm == null){
			mm = new MixerManager();
		}
		Mixer playbackMixer = mm.getPlaybackMixerByName(deviceName);
		if (playbackMixer == null)
			throw new AudioControllerException("Playback device \"" + deviceName
					+ "\" not found !");
		try {
			player.setMixer(playbackMixer);
		} catch (PlayerException e1) {
			throw new AudioControllerException(
					"Could not set playback device.", e1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setCaptureDeviceByName(java.lang.String)
	 */
	public synchronized void setCaptureDeviceByName(String deviceName)
			throws AudioControllerException {
		if (mm == null){
				mm = new MixerManager();
		} 
		Mixer captureMixer = mm.getCaptureMixerByName(deviceName);
		if (captureMixer == null)
			throw new AudioControllerException("Capture device \"" + deviceName
					+ "\" not found !");
		capture.setMixer(captureMixer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getPlaybackAudioFormat()
	 */
	public AudioFormat getPlaybackAudioFormat() {
		if (player == null)
			return null;
		return player.getAudioFormat();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#openPlayback()
	 */
	public void openPlayback() throws AudioControllerException {
		try {
			player.open();
		} catch (PlayerException e) {
			throw new AudioControllerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#closePlayback()
	 */
	public void closePlayback() throws AudioControllerException {
		try {
			player.close();
		} catch (PlayerException e) {
			throw new AudioControllerException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#openCapture()
	 */
	public void openCapture() throws AudioControllerException {
		try {
			capture.open();
		} catch (CaptureException e) {
			throw new AudioControllerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#closeCapture()
	 */
	public void closeCapture() throws AudioControllerException {
		try {
			capture.close();
		} catch (CaptureException e) {
			throw new AudioControllerException(e);
		}

	}

    
    public boolean isCaptureOpen(){
        return capture.isOpen();
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setPlaybackFile(java.io.File)
	 */
	public void setPlaybackFile(File playbackFile)
			throws AudioControllerException {
		try {
			player.setAudioSource(new FileAudioSource(playbackFile));
		} catch (PlayerException e) {
			throw new AudioControllerException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getPlaybackFile()
	 */
	public File getPlaybackFile() {
		if (player == null)
			return null;

		AudioSource as = player.getAudioSource();
		if (as instanceof FileAudioSource) {
			return ((FileAudioSource) as).getFile();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setPlaybackAudioSource(ipsk.audio.AudioSource)
	 */
	public void setPlaybackAudioSource(AudioSource source)
			throws AudioControllerException {
		try {
			player.setAudioSource(source);
		} catch (PlayerException e) {
			throw new AudioControllerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setOverwrite(boolean)
	 */
	public void setOverwrite(boolean overwrite) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#isOverwrite()
	 */
	public boolean isOverwrite() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setRecordingFile(java.io.File)
	 */
	public void setRecordingFile(File recordingFile) {
		capture.setRecordingFile(recordingFile);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getRecordingFile()
	 */
	public File getRecordingFile() {
		return capture.getRecordingFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#startPlayback()
	 */
	public void startPlayback() throws AudioControllerException {
		player.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#startCapture()
	 */
	public void startCapture() throws AudioControllerException {
		capture.setCaptureOnly(true);
		capture.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#startRecording()
	 */
	public void startRecording() throws AudioControllerException {
		synchronized (capture) {
			capture.setCaptureOnly(false);

			capture.start();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#pausePlayback()
	 */
	public void pausePlayback() throws AudioControllerException {
		player.pause();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#stopPlayback()
	 */
	public void stopPlayback() throws AudioControllerException {
		player.stop();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#stopCapture()
	 */
	public void stopCapture() throws AudioControllerException {
		try {
			capture.stop();
		} catch (CaptureException e) {
			throw new AudioControllerException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#stopRecording()
	 */
	public void stopRecording() throws AudioControllerException {
		try {
			capture.stop();
		} catch (CaptureException e) {
			throw new AudioControllerException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getCaptureLevelInfos()
	 */
	public LevelInfo[] getCaptureLevelInfos() {
		return capture.getLevelInfos();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getPlaybackLevelInfos()
	 */
	public LevelInfo[] getPlaybackLevelInfos() {
		return player.getLevelInfos();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#isPlaybackRandomPositioningSupported()
	 */
	public boolean isPlaybackRandomPositioningSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getPlaybackFrameLength()
	 */
	public long getPlaybackFrameLength() {

		return player.getFrameLength();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setPlaybackFramePosition(long)
	 */
	public long setPlaybackFramePosition(long newPosition)
			throws AudioControllerException {
		try {
			return player.setFramePosition(newPosition);
		} catch (PlayerException e) {
			throw new AudioControllerException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getPlaybackFramePosition()
	 */
	public long getPlaybackFramePosition() {

		return player.getFramePosition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setPlaybackStartFramePosition(long)
	 */
	public void setPlaybackStartFramePosition(long startPosition) {
		player.setStartFramePosition(startPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getPlaybackStartFramePosition()
	 */
	public long getPlaybackStartFramePosition() {
		return player.getStartFramePosition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setPlaybackStopFramePosition(long)
	 */
	public void setPlaybackStopFramePosition(long stopPosition) {
		player.setStopFramePosition(stopPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getPlaybackStopFramePosition()
	 */
	public long getPlaybackStopFramePosition() {

		return player.getStopFramePosition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getRecordingFramePosition()
	 */
	public long getCaptureFramePosition() {
		return capture.getFramePosition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getPropertyNames()
	 */
	public String[] getPropertyNames() {

		return new String[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getPropertyDescription(java.lang.String)
	 */
	public String getPropertyDescription(String propertyName) {
		
			return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setProperty(java.lang.String,
	 *      java.lang.String)
	 */
	public void setProperty(String propertyName, String value) {
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getProperty(java.lang.String)
	 */
	public String getProperty(String propertyName) {
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#addAudioController2Listener(ipsk.audio.AudioController2.AudioController2Listener)
	 */
	public void addAudioController2Listener(AudioController2Listener acl) {

		if (acl != null && !listeners.contains(acl)) {
			listeners.addElement(acl);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#removeAudioController2Listener(ipsk.audio.AudioController2.AudioController2Listener)
	 */
	public void removeAudioController2Listener(AudioController2Listener acl) {

		if (acl != null) {
			listeners.removeElement(acl);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#setRecordingAudioFileFormat(javax.sound.sampled.AudioFileFormat)
	 */
	public void setRecordingAudioFileFormat(AudioFileFormat audiofileFormat) {
		AudioFormat audioFormat=audiofileFormat.getFormat();
		int channels=audioFormat.getChannels();
		capture.setAudioFileFormat(audiofileFormat);
		boolean multiChannelMode=(channels>2);
		capture.setForceOpening(multiChannelMode);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2#getAudioFileFormat()
	 */
	public AudioFileFormat getAudioFileFormat() {
		if (capture == null)
			return null;
		return capture.getAudioFileFormat();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.player.PlayerListener#update(ipsk.audio.player.PlayerEvent)
	 */
	public void update(PlayerEvent playerEvent) {
		updateListeners(playerEvent);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.capture.CaptureListener#update(ipsk.audio.capture.event.CaptureEvent)
	 */
	public void update(CaptureEvent captureEvent) {
		updateListeners(captureEvent);

	}

	protected void updateListeners(AudioControllerEvent ace) {
		synchronized(listeners){
			Iterator<AudioController2Listener> it = listeners.iterator();
			while (it.hasNext()) {
				AudioController2Listener listener = it.next();
				listener.update(ace);
			}
		}
	}


    public float getPreferredCaptureLineBufferSizeMilliSeconds() {
        return preferredCaptureLineBufferSizeMilliSeconds;
    }

    public void setPreferredCaptureLineBufferSizeMilliSeconds(
            float preferredCaptureLineBufferSizeMilliSeconds) {
        this.preferredCaptureLineBufferSizeMilliSeconds = preferredCaptureLineBufferSizeMilliSeconds;
        capture.setPreferredLineBufferSizeMillis(preferredCaptureLineBufferSizeMilliSeconds);
    }
    
    public float getPreferredPlaybackLineBufferSizeMilliSeconds() {
        return preferredPlaybackLineBufferSizeMilliSeconds;
    }

    public void setPreferredPlaybackLineBufferSizeMilliSeconds(
            float preferredPlaybackLineBufferSizeMilliSeconds) {
        this.preferredPlaybackLineBufferSizeMilliSeconds = preferredPlaybackLineBufferSizeMilliSeconds;
        player.setPreferredLineBufferSizeMillis(preferredPlaybackLineBufferSizeMilliSeconds);
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#getPrimaryRecordTarget()
     */
    public PrimaryRecordTarget getPrimaryRecordTarget() {
//        return capture.isUseTempFile()?PrimaryRecordTarget.TEMP_RAW_FILE:PrimaryRecordTarget.DIRECT;
        return capture.getPrimaryRecordTarget();
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#setPrimaryRecordTarget(ipsk.audio.AudioController2.PrimaryRecordTarget)
     */
    public void setPrimaryRecordTarget(PrimaryRecordTarget primaryRecordTarget) {
//        capture.setUseTempFile(primaryRecordTarget!=null && primaryRecordTarget.equals(PrimaryRecordTarget.TEMP_RAW_FILE));
            capture.setPrimaryRecordTarget(primaryRecordTarget);
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#setMaxRecordingFrameLength(long)
     */
    public void setMaxRecordingFrameLength(Long maxFrameLength) {
       capture.setMaxFrameLength(maxFrameLength);
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#getMaxRecordingFrameLength()
     */
    public Long getMaxRecordingFrameLength() {
       return capture.getMaxFrameLength();
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#isFileTransitionRecordingSupported()
     */
    public boolean isFileTransitionRecordingSupported() {
        return false;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#getRecordingSessionInfoFile()
     */
    public File getRecordingSessionInfoFile() {
       
        return null;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#setRecordingSessionInfoFile(java.io.File)
     */
    public void setRecordingSessionInfoFile(File recordingSessionInfoFile) {
        // not supported
        
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#isSessionInfoHandlingSupported()
     */
    public boolean isSessionInfoHandlingSupported() {
        return false;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#supportsDeviceProviders()
     */
    public boolean supportsDeviceProviders() {
        
        return false;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#getDeviceProviders()
     */
    public List<DeviceProviderInfo> getDeviceProviderInfos()
            throws AudioControllerException {
       throw new AudioControllerException("Device providers are not supported by this audio controller implementation.");
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#setPlaybackDeviceByInfo(ipsk.audio.DeviceInfo)
     */
    public void setPlaybackDeviceByInfo(DeviceInfo deviceInfo)
            throws AudioControllerException {
        throw new AudioControllerException("Device providers are not supported by this audio controller implementation.");
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#setCaptureDeviceByinfo(ipsk.audio.DeviceInfo)
     */
    public void setCaptureDeviceByinfo(DeviceInfo deviceInfo)
            throws AudioControllerException {
        throw new AudioControllerException("Device providers are not supported by this audio controller implementation.");
        
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#getInstance(ipsk.audio.DeviceProviderInfo)
     */
    public DeviceProvider getInstance(DeviceProviderInfo providerInfo)
            throws AudioControllerException {
        throw new AudioControllerException("Device providers are not supported by this audio controller implementation.");
    }

    
    public DeviceInfo convertLegacyDeviceName(String deviceName)
            throws AudioControllerException {
        throw new AudioControllerException("Device providers are not supported by this audio controller implementation.");
    }

    

}
