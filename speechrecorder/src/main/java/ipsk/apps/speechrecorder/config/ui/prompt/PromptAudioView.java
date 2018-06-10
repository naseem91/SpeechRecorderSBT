//    Speechrecorder
//    (c) Copyright 2009-2011
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

/*
 * Date  : Jun 2, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config.ui.prompt;

import ipsk.apps.speechrecorder.config.MixerName;
import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import ipsk.apps.speechrecorder.config.PromptConfiguration;
import ipsk.apps.speechrecorder.config.ui.PromptAudioChannelRoutingView;
import ipsk.apps.speechrecorder.config.ui.audio.DeviceChooserTabs;
import ipsk.audio.AudioController2;

import javax.swing.JTabbedPane;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class PromptAudioView extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private DeviceChooserTabs dvc;
    private PromptAudioChannelRoutingView pcr;
//	private AudioController2 audioController;

	public PromptAudioView(AudioController2 ac) {
		super();
//		this.audioController=ac;
		dvc=new DeviceChooserTabs(ac, AudioController2.DeviceType.PLAYBACK);
		pcr=new PromptAudioChannelRoutingView();
		addTab("Device",dvc);
		addTab("Channel routing",pcr);
	}

	/**
     * @param projectConfiguration project configuration
     */
    public void setProjectConfiguration(ProjectConfiguration projectConfiguration) {
        
        MixerName[] promptPlayMixerNames=projectConfiguration.getPromptPlaybackMixerName();
        dvc.setSelectedMixerNames(promptPlayMixerNames);
        
        PromptConfiguration promptConfiguration=projectConfiguration.getPromptConfiguration();
        pcr.setPromptConfiguration(promptConfiguration);
        
    }
    
	public void applyValues(ProjectConfiguration p){
	   
	   dvc.stopEditing();
       MixerName[] promptPlayMixerNames=dvc.getSelectedMixerNames();
       p.setPromptPlaybackMixerName(promptPlayMixerNames);
       
       pcr.applyValues(p.getPromptConfiguration());
	}


}
