//    IPS Java Audio Tools
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.audio.impl.j2audio2;

import java.util.List;

import javax.sound.sampled.spi.MixerProvider;

import ipsk.audio.DeviceInfo;
import ipsk.audio.DeviceProvider;
import ipsk.audio.DeviceProviderInfo;
import ipsk.audio.ajs.AJSAudioSystem;
import ipsk.audio.ajs.AJSDeviceInfo;
import ipsk.audio.ajs.MixerProviderServiceDescriptor;

/**
 * @author klausj
 *
 */
public class DeviceProviderImpl implements DeviceProvider {

    private MixerProvider mixerProvider;
    private MixerProviderServiceDescriptor mixerProviderServiceDescriptor;
    
    

    public DeviceProviderImpl(MixerProvider mixerProvider,
            MixerProviderServiceDescriptor mixerProviderServiceDescriptor) {
        super();
        this.mixerProvider = mixerProvider;
        this.mixerProviderServiceDescriptor = mixerProviderServiceDescriptor;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.DeviceProvider#getInfo()
     */
    public DeviceProviderInfo getInfo() {
        return mixerProviderServiceDescriptor;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.DeviceProvider#getCaptureDeviceInfos()
     */
    public List<? extends DeviceInfo> getCaptureDeviceInfos() {
        List<AJSDeviceInfo> infos=AJSAudioSystem.availableCaptureDeviceInfos(mixerProviderServiceDescriptor);
        return infos;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.DeviceProvider#getPlaybackDeviceInfos()
     */
    public List<? extends DeviceInfo> getPlaybackDeviceInfos() {
        List<AJSDeviceInfo> infos=AJSAudioSystem.availablePlaybackDeviceInfos(mixerProviderServiceDescriptor);
        return infos;
    }

}
