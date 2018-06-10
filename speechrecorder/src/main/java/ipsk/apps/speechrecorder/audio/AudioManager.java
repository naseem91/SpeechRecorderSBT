//    Speechrecorder
// 	  (c) Copyright 2013
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.apps.speechrecorder.audio;

import ipsk.apps.speechrecorder.config.MixerName;
import ipsk.audio.AudioController2;
import ipsk.audio.AudioControllerException;
import ipsk.audio.DeviceInfo;
import ipsk.audio.DeviceProvider;
import ipsk.audio.DeviceProviderInfo;
import ipsk.audio.ajs.AJSAudioSystem;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.PatternSyntaxException;

import javax.sound.sampled.Mixer;

/**
 * @author klausj
 * 
 */
public class AudioManager {

	private AudioController2 audioController;
	private Logger logger = Logger.getLogger("ipsk.apps.speechrecorder");
	private List<? extends DeviceProviderInfo> deviceProviderInfosList;

	/**
	 * @param audioController
	 */
	public AudioManager(AudioController2 audioController) {
		super();
		this.audioController = audioController;
	}

	public DeviceInfo findMatchingDeviceInfo(MixerName[] mixerNames,
			AJSAudioSystem.DeviceType devType) throws AudioManagerException {
		if(deviceProviderInfosList==null){
			try {
				deviceProviderInfosList = audioController.getDeviceProviderInfos();
			} catch (AudioControllerException e) {
				throw new AudioManagerException(e);
			}
			for (DeviceProviderInfo dpi : deviceProviderInfosList) {
				logger.info("Available audio device provider : "
						+ dpi.getImplementationClassname()
						+ " for audio interface " + dpi.getAudioInterfaceName());
			}
		}
		DeviceInfo matchedDeviceInfo = null;

		for (MixerName mixerName : mixerNames) {
			String providerClassname = mixerName.providerIdAsJavaClassName();
			String audioInterfaceName = mixerName.getInterfaceName();
			String mName = mixerName.getName();
			for (DeviceProviderInfo dpi : deviceProviderInfosList) {
				String pClassname = dpi.getImplementationClassname();
				if ((AJSAudioSystem.DeviceType.CAPTURE.equals(devType) && dpi
						.isProvidesCaptureDevices())
						|| (AJSAudioSystem.DeviceType.PLAYBACK.equals(devType) && dpi
								.isProvidesPlaybackDevices())) {
					DeviceProvider dp = null;
					try {
						if (providerClassname != null) {
							if (pClassname.equals(providerClassname)) {
								dp = audioController.getInstance(dpi);
							}
						} else if (audioInterfaceName != null) {
							if (audioInterfaceName.equals(dpi
									.getAudioInterfaceName())) {
								dp = audioController.getInstance(dpi);
							}
						}
					} catch (AudioControllerException e) {
						throw new AudioManagerException(e);
					}
					if (dp != null) {
						List<? extends DeviceInfo> diList = null;
						if (AJSAudioSystem.DeviceType.CAPTURE.equals(devType)) {
							diList = dp.getCaptureDeviceInfos();
						} else if (AJSAudioSystem.DeviceType.PLAYBACK
								.equals(devType)) {
							diList = dp.getPlaybackDeviceInfos();
						}
						for (DeviceInfo di : diList) {
							Mixer.Info diMi = di.getMixerInfo();
							if (diMi != null) {
								String diDevName = diMi.getName();
								if (diDevName != null) {

									if (!mixerName.isRegex()) {
										logger.finest("Comparing: " + diDevName
												+ " " + mName);
										if (diDevName.equals(mName)) {
											// found
											matchedDeviceInfo = di;
											break;
										}

									} else {
										logger.fine("Pattern matching: "
												+ diDevName + " " + mName
												+ "...");
										boolean match = false;
										try {
											match = diDevName.matches(mName);
										} catch (PatternSyntaxException pse) {
											String msg = ("Syntax error in regular expression: "
													+ mName + "\n" + pse
													.getMessage());
											logger.severe(msg);
											// speechRecorderUI.displayError("Syntax error in regex of audio device name pattern",
											// msg);
											throw new AudioManagerException(
													"Syntax error in regex of audio device name pattern:\n"
															+ msg, pse);
										}
										if (match) {
											matchedDeviceInfo = di;
											break;
										}
									}
								}
							}

						}
						if (matchedDeviceInfo != null) {
							break;
						}
					}
				}
			}

			if (matchedDeviceInfo != null) {
				break;
			}
		}
		return matchedDeviceInfo;
	}

}
