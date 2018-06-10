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

import java.util.Vector;

import javax.sound.sampled.Mixer;

import ipsk.audio.ajs.AJSAudioSystem.DeviceType;

/**
 * @author klausj
 *
 */
public class DeviceSelection {
    
    private AJSAudioSystem.DeviceType deviceType;
    private AJSDevice device;
    private Vector<DeviceSelectionListener> listeners=new Vector<DeviceSelectionListener>();
    public DeviceSelection(DeviceType deviceType) {
        super();
        this.deviceType = deviceType;
    }
    public AJSDevice getDevice() {
        return device;
    }
    public void setDevice(AJSDevice device) {
        AJSDevice oldDevice=this.device;
        this.device = device;
        
        for(DeviceSelectionListener l:listeners){
            l.deviceChanged(this,oldDevice, this.device);
        }
    }
    public AJSAudioSystem.DeviceType getDeviceType() {
        return deviceType;
    }
    public Mixer getMixer() {
        Mixer m=null;
        if(device!=null){
            m=device.getMixer();
        }
        return m;
    }
    public void addDeviceSelectionListener(DeviceSelectionListener listener){
        listeners.add(listener);
    }
    public void removeDeviceSelectionListener(DeviceSelectionListener listener){
        listeners.remove(listener);
    }
}
