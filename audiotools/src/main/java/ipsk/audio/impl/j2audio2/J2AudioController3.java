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

import ipsk.audio.AudioController3;
import ipsk.audio.AudioControllerException;
import ipsk.audio.AudioSource;
import ipsk.audio.DeviceInfo;
import ipsk.audio.DeviceProvider;
import ipsk.audio.DeviceProviderInfo;
import ipsk.audio.FileAudioSource;
import ipsk.audio.ajs.AJSAudioSystem;
import ipsk.audio.ajs.AJSDevice;
import ipsk.audio.ajs.AJSDeviceInfo;
import ipsk.audio.ajs.DeviceSelection;
import ipsk.audio.ajs.MixerProviderServiceDescriptor;
import ipsk.audio.capture.Capture2;
import ipsk.audio.capture.CaptureException;
import ipsk.audio.capture.CaptureListener;
import ipsk.audio.capture.PrimaryRecordTarget;
import ipsk.audio.capture.event.CaptureEvent;
import ipsk.audio.dsp.AudioOutputStreamFloatConverter;
import ipsk.audio.dsp.LevelInfo;
import ipsk.audio.dsp.LevelMeasureFloatAudioOutputStream;
import ipsk.audio.mixer.MixerManager;
import ipsk.audio.player.Player;
import ipsk.audio.player.PlayerException;
import ipsk.audio.player.PlayerListener;
import ipsk.audio.player.event.PlayerEvent;
import ipsk.io.ChannelRouting;

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
import javax.sound.sampled.spi.MixerProvider;


/**
 * Audiocontroller implementation.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class J2AudioController3 implements AudioController3, PlayerListener,
		CaptureListener {

    public static class AJSMixerInfo extends Mixer.Info{

        protected AJSMixerInfo(String arg0) {
            super(arg0, "", "", "");
        }
        
    }

	private Vector<AudioController2Listener> listeners;

	private DeviceSelection captureDeviceSelection;
	
	private Player player;

	private Capture2 capture;

	private MixerManager mm;
    
    private float preferredCaptureLineBufferSizeMilliSeconds;

	private float preferredPlaybackLineBufferSizeMilliSeconds;
	
	private AudioOutputStreamFloatConverter captureOutputStream;
    private LevelMeasureFloatAudioOutputStream captureLevelMeasureStream;
    
	private Logger logger;

	/**
	 * 
	 */
	public J2AudioController3() {
		super();
		String packageName=getClass().getPackage().getName();
		logger=Logger.getLogger(packageName);
		logger.setLevel(Level.INFO);
		listeners = new Vector<AudioController2Listener>();

		player = new Player();
		player.addPlayerListener(this);
//		captureDeviceSelection=new DeviceSelection(DeviceType.CAPTURE);
//		captureDeviceSelection.setDevice(AJSAudioSystem.getDefaultCaptureDevice());
		capture = new Capture2();
		capture.addCaptureListener(this);
		
		// Capture2 does not provide level measurement 
		captureOutputStream=new AudioOutputStreamFloatConverter();
		capture.addAudioOutputStream(captureOutputStream);
		captureLevelMeasureStream=new LevelMeasureFloatAudioOutputStream();
		captureOutputStream.addFloatAudioOutputStream(captureLevelMeasureStream);
		
		logger.info("J2AudioController3 created");
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
		//return capture.getLevelInfos();
	    return captureLevelMeasureStream.getLevelInfosBean().getLevelInfos();
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
        return true;
    }

    public File getRecordingSessionInfoFile() {
        return capture.getRecordingSessionInfoFile();
    }

    public void setRecordingSessionInfoFile(File recordingSessionInfoFile) {
        capture.setWriteRecordingInfo(true);
        capture.setRecordingSessionInfoFile(recordingSessionInfoFile);
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#isSessionInfoHandlingSupported()
     */
    public boolean isSessionInfoHandlingSupported() {
        return true;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#supportsDeviceProviders()
     */
    public boolean supportsDeviceProviders() {
      
        return true;
    }

   
    public List<? extends DeviceProviderInfo> getDeviceProviderInfos()
            throws AudioControllerException {
      List<MixerProviderServiceDescriptor> mpsdList=AJSAudioSystem.listMixerProviderDescriptors();
      return mpsdList;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#setPlaybackDeviceByInfo(ipsk.audio.DeviceInfo)
     */
    public void setPlaybackDeviceByInfo(DeviceInfo deviceInfo)
            throws AudioControllerException {
        DeviceProviderInfo dpi=deviceInfo.getDeviceProviderInfo();
        String providerClassname=dpi.getImplementationClassname();
        String aiName=dpi.getAudioInterfaceName();
        List<MixerProviderServiceDescriptor> mpsdList=AJSAudioSystem.listMixerProviderDescriptors();

        if(providerClassname!=null){
            for(MixerProviderServiceDescriptor mpsd:mpsdList){
                if(mpsd.isProvidesPlaybackDevices()){
                    if(providerClassname.equals(mpsd.getImplementationClassname())){
                        AJSDeviceInfo ajsDevInfo=new AJSDeviceInfo(mpsd, deviceInfo.getMixerInfo());
                        AJSDevice device=AJSAudioSystem.getDevice(ajsDevInfo);
                        if(device!=null){
                            try {
                                player.setMixer(device.getMixer());
                                return;
                            } catch (PlayerException e) {
                                throw new AudioControllerException("Could not set playback device "+deviceInfo+": "+e);
                            }
                           
                        }
                    }
                }
            }
        }else if(aiName!=null){
            for(MixerProviderServiceDescriptor mpsd:mpsdList){
                if(mpsd.isProvidesPlaybackDevices()){
                    String mpsdAi=mpsd.getAudioInterfaceName();
                    if(mpsdAi.equals(aiName)){
                        AJSDeviceInfo ajsDevInfo=new AJSDeviceInfo(mpsd, deviceInfo.getMixerInfo());
                        AJSDevice device=AJSAudioSystem.getDevice(ajsDevInfo);
                        if(device!=null){
                            try {
                                player.setMixer(device.getMixer());
                            } catch (PlayerException e) {
                                // look for another provider 
                                continue;
                            }
                            return;
                        }
                    }
                }
            }
        }else{
            for(MixerProviderServiceDescriptor mpsd:mpsdList){
                if(mpsd.isProvidesPlaybackDevices()){
                    // take first provider
                    AJSDeviceInfo ajsDevInfo=new AJSDeviceInfo(deviceInfo.getMixerInfo());
                    AJSDevice device=AJSAudioSystem.getDevice(ajsDevInfo);
                    if(device!=null){
                        try {
                            player.setMixer(device.getMixer());
                        } catch (PlayerException e) {
                            throw new AudioControllerException("Could not set playback device "+deviceInfo);
                        }
                        return;
                    }
                }
            }
        }
        throw new AudioControllerException("Could not set playback device "+deviceInfo);
    }

    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#setCaptureDeviceByinfo(ipsk.audio.DeviceInfo)
     */
    public void setCaptureDeviceByinfo(DeviceInfo deviceInfo)
    throws AudioControllerException {
        DeviceProviderInfo dpi=deviceInfo.getDeviceProviderInfo();
        String providerClassname=dpi.getImplementationClassname();
        String aiName=dpi.getAudioInterfaceName();
        List<MixerProviderServiceDescriptor> mpsdList=AJSAudioSystem.listMixerProviderDescriptors();

        if(providerClassname!=null){
            for(MixerProviderServiceDescriptor mpsd:mpsdList){
                if(mpsd.isProvidesCaptureDevices()){
                    if(providerClassname.equals(mpsd.getImplementationClassname())){
                        AJSDeviceInfo ajsDevInfo=new AJSDeviceInfo(mpsd, deviceInfo.getMixerInfo());
                        AJSDevice device=AJSAudioSystem.getDevice(ajsDevInfo);
                        if(device!=null){
                            capture.setMixer(device.getMixer());
                            return;
                        }
                    }
                }
            }
        }else if(aiName!=null){
            for(MixerProviderServiceDescriptor mpsd:mpsdList){
                if(mpsd.isProvidesCaptureDevices()){
                    String mpsdAi=mpsd.getAudioInterfaceName();
                    if(mpsdAi.equals(aiName)){
                        AJSDeviceInfo ajsDevInfo=new AJSDeviceInfo(mpsd, deviceInfo.getMixerInfo());
                        AJSDevice device=AJSAudioSystem.getDevice(ajsDevInfo);
                        if(device!=null){
                            capture.setMixer(device.getMixer());
                            return;
                        }
                    }
                }
            }
        }else{
            for(MixerProviderServiceDescriptor mpsd:mpsdList){
                if(mpsd.isProvidesCaptureDevices()){
                    // take first provider
                    AJSDeviceInfo ajsDevInfo=new AJSDeviceInfo(deviceInfo.getMixerInfo());
                    AJSDevice device=AJSAudioSystem.getDevice(ajsDevInfo);
                    if(device!=null){
                        capture.setMixer(device.getMixer());
                        return;
                    }
                }
            }
        }
        throw new AudioControllerException("Could not set capture device "+deviceInfo);
    }


    /* (non-Javadoc)
     * @see ipsk.audio.AudioController2#getInstance(ipsk.audio.DeviceProviderInfo)
     */
    public DeviceProvider getInstance(DeviceProviderInfo providerInfo)
            throws AudioControllerException {
        List<MixerProviderServiceDescriptor> mpsdList=AJSAudioSystem.listMixerProviderDescriptors();
        MixerProviderServiceDescriptor msd=null;
        for(MixerProviderServiceDescriptor mpsd:mpsdList){
            String audioInterfaceName=mpsd.getAudioInterfaceName();
            if(providerInfo.getAudioInterfaceName().equals(audioInterfaceName)){
                msd=mpsd;
                break;
            }
        }
        MixerProvider mp=AJSAudioSystem.getMixerProvider(msd);
        DeviceProvider dp=new DeviceProviderImpl(mp, msd);
        return dp;

    }

    public DeviceInfo convertLegacyDeviceName(String deviceName)
    throws AudioControllerException {

        DeviceInfo ndi=null;
        
            
//                String deviceName=mInfo.getName();

                List<MixerProviderServiceDescriptor> mpsdList=AJSAudioSystem.listMixerProviderDescriptors();
                for(MixerProviderServiceDescriptor mpsd:mpsdList){
                    
                    String pSuffix=mpsd.getLegacyJavaSoundSuffix();
                    if(pSuffix!=null && deviceName!=null && deviceName.endsWith(pSuffix)){
                        // convert
                        String newName=deviceName.substring(0,deviceName.length()-pSuffix.length());

                        // overwrite with converted name

                        Mixer.Info newMInfo=new AJSMixerInfo(newName);
                        ndi=new AJSDeviceInfo(mpsd,newMInfo); 
                        break;
                    }
                }
                if(ndi==null){
                    for(MixerProviderServiceDescriptor mpsd:mpsdList){
                        if(mpsd.isStandardJavaSoundWrapper()){
                            Mixer.Info deviceInfo=new AJSMixerInfo(deviceName);
                            ndi=new AJSDeviceInfo(mpsd,deviceInfo); 
                            break;
                        }
                    }
                }
        
        return ndi;

    }

	@Override
	public void setInputChannelRouting(
			ChannelRouting inputChannelRouting) {
		capture.setChannelRouting(inputChannelRouting);
	}

	@Override
	public ChannelRouting getInputChannelRouting() {
		return capture.getChannelRouting();
	}

}
