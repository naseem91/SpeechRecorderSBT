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
 * Date  : May 9, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.ajs.ui;

import ipsk.audio.ajs.AJSDeviceInfo;
import ipsk.audio.ajs.DeviceSelection;
import ipsk.audio.ajs.MixerProviderServiceDescriptor;
import ipsk.audio.ajs.ui.DeviceChooserComponentProvider.DeviceView;
import ipsk.audio.ajs.ui.DeviceChooserComponentProvider.InterfaceElement;
import ipsk.awt.util.gridbaglayout.GridBuilder;
import ipsk.awt.util.gridbaglayout.Gridbag;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class DevicesUI extends JPanel {

    private DeviceChooserComponentProvider captureCp;
	private DeviceChooserComponentProvider playbackCp;
  

	/**
	 * 
	 */
	public DevicesUI(DeviceSelection captureDeviceSelection,DeviceSelection playbackDeviceSelection) throws LineUnavailableException {
		super(new GridBagLayout());
		GridBuilder builder=new GridBuilder(this);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new Insets(2, 5, 2, 5);
		c.anchor = GridBagConstraints.PAGE_START;

		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;
        
		playbackCp = new DeviceChooserComponentProvider(playbackDeviceSelection);
		playbackCp.setParentComponent(this);
		captureCp = new DeviceChooserComponentProvider(captureDeviceSelection);
		captureCp.setParentComponent(this);
		JLabel playbackSelectLabel = new JLabel("Playback device:");
		add(playbackSelectLabel, c);
		c.gridx++;
		Point gridPos=builder.insertGrid(playbackCp, c.gridx, c.gridy);
		
		c.gridx = 0;
		c.gridy=gridPos.y;
		
		add(new JLabel("Capture device:"), c);
		c.gridx++;
		c.gridy++;
		gridPos=builder.insertGrid(captureCp, c.gridx, c.gridy);
	}


    public AJSDeviceInfo getSelectedCaptureDeviceInfo(){
        return captureCp.getSelectedDeviceInfo();
    }

  
    public AJSDeviceInfo getSelectedPlaybackDeviceInfo(){
        return playbackCp.getSelectedDeviceInfo();
    }

}
