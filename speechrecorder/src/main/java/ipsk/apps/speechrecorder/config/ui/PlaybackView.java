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
 * Date  : Jun 24, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config.ui;

import ipsk.apps.speechrecorder.config.ConfigHelper;
import ipsk.apps.speechrecorder.config.MixerName;
import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import ipsk.apps.speechrecorder.config.ui.audio.DeviceChooserTabs;
import ipsk.audio.AudioController2;
import ipsk.audio.AudioController2.DeviceType;

import javax.swing.JTabbedPane;


/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class PlaybackView extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private AudioController2 audioController;
    private DeviceChooserTabs playbackListChooser;

    public PlaybackView(AudioController2 audioController) {
        super();

        this.audioController = audioController;
        playbackListChooser = new DeviceChooserTabs(audioController,
                DeviceType.PLAYBACK);
        addTab("Device", playbackListChooser);
    }

    /**
     * @param project
     */
    public void setProjectConfiguration(ProjectConfiguration project) {
        MixerName[] orgPlayMixerNames = project.getPlaybackMixerName();
        MixerName[] playMixerNames = ConfigHelper.getAJSConvertedMixerNames(
                audioController, orgPlayMixerNames);
        playbackListChooser.setSelectedMixerNames(playMixerNames);
    }

    public void applyValues(ProjectConfiguration p) {
        playbackListChooser.stopEditing();
        MixerName[] playMixerNames = playbackListChooser
                .getSelectedMixerNames();
        p.setPlaybackMixerName(playMixerNames);
    }

}
