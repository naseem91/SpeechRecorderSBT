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

package ipsk.audio.ajs;

import ipsk.audio.DeviceInfo;
import ipsk.audio.DeviceProvider;
import ipsk.audio.DeviceProviderInfo;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;

/**
 * @author klausj
 *
 */
public class AJSDeviceInfo implements DeviceInfo{

    private final Mixer.Info mixerInfo;
  
    private final MixerProviderServiceDescriptor mixerProviderServiceDescriptor;
    public AJSDeviceInfo(Mixer.Info mixerInfo){
        super();
        this.mixerInfo=mixerInfo;
        this.mixerProviderServiceDescriptor=null;
    }
    
    public AJSDeviceInfo(MixerProviderServiceDescriptor mixerProviderServiceDescriptor,Mixer.Info mixerInfo){
        super();
        
        this.mixerProviderServiceDescriptor=mixerProviderServiceDescriptor;
        this.mixerInfo=mixerInfo;
    }
 

    public MixerProviderServiceDescriptor getMixerProviderServiceDescriptor() {
        return mixerProviderServiceDescriptor;
    }


    public Mixer.Info getMixerInfo() {
        return mixerInfo;
    }


    /* (non-Javadoc)
     * @see ipsk.audio.DeviceInfo#getDeviceProviderInfo()
     */
    public DeviceProviderInfo getDeviceProviderInfo() {
      return mixerProviderServiceDescriptor;
    }

    public String toString(){
        StringBuffer sb=new StringBuffer();
        sb.append(getDeviceProviderInfo());
        sb.append(": ");
        sb.append(getMixerInfo());
        return sb.toString();
    }
    

}
