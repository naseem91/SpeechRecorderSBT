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

package ipsk.audio.mixer.ui;

import ipsk.audio.mixer.MixerManager;
import ipsk.audio.mixer.MixerManagerListener;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class MixerManagerUI extends JPanel implements MixerManagerListener {

	protected JComboBox playbackSelect;

	protected JComboBox captureSelect;
    
    protected class DefaultMixerInfo extends Mixer.Info {
        public DefaultMixerInfo(boolean isSource){
            super("Default "+(isSource?"playback":"capture")+" mixer","","Default mixer selected by operating system.","");
        }
    }
    
    Mixer.Info defaultPlayback;
    Mixer.Info defaultCapture;

	/**
	 * 
	 */
	public MixerManagerUI(MixerManager mm) throws LineUnavailableException {
		super(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new Insets(2, 5, 2, 5);
		c.anchor = GridBagConstraints.PAGE_START;

		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;
        Mixer.Info[] infos=mm.getPlaybackMixerInfos();
        defaultPlayback=new DefaultMixerInfo(true);
        Mixer.Info[] mifs=new Mixer.Info[infos.length+1];
        mifs[0]=defaultPlayback;
        for(int i=0;i<infos.length;i++){
            mifs[i+1]=infos[i];
        }
		playbackSelect = new JComboBox(mifs);
		Mixer selPlaybackMixer = mm.getSelectedPlaybackMixer();
		if (selPlaybackMixer != null) {
			playbackSelect.setSelectedItem(selPlaybackMixer.getMixerInfo());
		}else{
            playbackSelect.setSelectedItem(defaultPlayback);
        }
		JLabel playbackSelectLabel = new JLabel("Playback mixer:");
		add(playbackSelectLabel, c);
		c.gridx++;
		add(playbackSelect, c);
		c.gridx = 0;
		c.gridy++;
         Mixer.Info[] cinfos=mm.getCaptureMixerInfos();
            defaultCapture=new DefaultMixerInfo(false);
            Mixer.Info[] cmifs=new Mixer.Info[cinfos.length+1];
            cmifs[0]=defaultCapture;
            for(int i=0;i<cinfos.length;i++){
                cmifs[i+1]=cinfos[i];
            }
		captureSelect = new JComboBox(cmifs);
		Mixer selCaptureMixer = mm.getSelectedCaptureMixer();
		if (selCaptureMixer != null) {
			captureSelect.setSelectedItem(selCaptureMixer.getMixerInfo());
		}else{
            captureSelect.setSelectedItem(defaultCapture);
        }
		add(new JLabel("Capture mixer:"), c);
		c.gridx++;
		add(captureSelect, c);
		mm.addMixerManagerListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.mixer.MixerManagerListener#selectedPlaybackMixerChanged(java.lang.Object,
	 *      javax.sound.sampled.Mixer)
	 */
	public void selectedPlaybackMixerChanged(Object src, Mixer newPlaybackMixer) {
	    if(newPlaybackMixer==null){
            playbackSelect.setSelectedItem(defaultPlayback);
        }else{
		playbackSelect.setSelectedItem(newPlaybackMixer.getMixerInfo());
        }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.mixer.MixerManagerListener#selectedCaptureMixerChanged(java.lang.Object,
	 *      javax.sound.sampled.Mixer)
	 */
	public void selectedCaptureMixerChanged(Object src, Mixer newCaptureMixer) {
        if (newCaptureMixer==null){
            captureSelect.setSelectedItem(defaultCapture);
        }else{
		captureSelect.setSelectedItem(newCaptureMixer.getMixerInfo());
        }

	}

	public Mixer.Info getSelectedPlaybackMixerInfo() {
        Mixer.Info selInfo=(Mixer.Info) playbackSelect.getSelectedItem();
        if(selInfo.equals(defaultPlayback))return null;
		return selInfo;
	}

	public Mixer.Info getSelectedCaptureMixerInfo() {
        Mixer.Info selInfo=(Mixer.Info) captureSelect.getSelectedItem();
        if(selInfo.equals(defaultCapture))return null;
        return selInfo;
		
	}

}
