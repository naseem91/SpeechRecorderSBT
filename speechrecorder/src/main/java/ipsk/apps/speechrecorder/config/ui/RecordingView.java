//    Speechrecorder
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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

package ipsk.apps.speechrecorder.config.ui;

import ipsk.apps.speechrecorder.config.ConfigHelper;
import ipsk.apps.speechrecorder.config.MixerName;
import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import ipsk.apps.speechrecorder.config.RecordingConfiguration;
import ipsk.apps.speechrecorder.config.ui.audio.DeviceChooserTabs;
import ipsk.apps.speechrecorder.config.ui.recording.ChannelAssignmentUI;
import ipsk.apps.speechrecorder.config.ui.recording.RecordingFormatView;
import ipsk.apps.speechrecorder.config.ui.recording.RecordingMainView;
import ipsk.audio.AudioController2;

import javax.swing.JTabbedPane;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class RecordingView extends JTabbedPane {

    private static final long serialVersionUID = 1L;
    private DeviceChooserTabs dvc;
    private RecordingFormatView rfv;
    private RecordingMainView rmv;
    private ChannelAssignmentUI rui;
    private AudioController2 audioController;

    public RecordingView(AudioController2 ac) {
        super();
        this.audioController=ac;
        dvc=new DeviceChooserTabs(ac, AudioController2.DeviceType.CAPTURE);
        rui=new ChannelAssignmentUI();
        rfv=new RecordingFormatView(rui);
        rmv=new RecordingMainView();
        addTab("Device",dvc);
        addTab("Channel routing",rui);
        addTab("Format",rfv);
        addTab("Options", rmv);
        
    }

    /**
     * Set project configuration
     * @param projectConfiguration project configuration
     */
    public void setProjectConfiguration(ProjectConfiguration projectConfiguration) {
        
        MixerName[] orgRecMixerNames=projectConfiguration.getRecordingMixerName();
        MixerName[] recMixerNames=ConfigHelper.getAJSConvertedMixerNames(audioController, orgRecMixerNames);
        dvc.setSelectedMixerNames(recMixerNames);
        
        RecordingConfiguration recordingConfiguration=projectConfiguration.getRecordingConfiguration();
        rfv.setRecordingConfiguration(recordingConfiguration);
        rmv.setRecordingConfiguration(recordingConfiguration);
        rui.setConfig(recordingConfiguration.getChannelAssignment());
    }
    
    public void applyValues(ProjectConfiguration p){
        RecordingConfiguration r=p.getRecordingConfiguration();
        dvc.stopEditing();
        MixerName[] recMixerNames=dvc.getSelectedMixerNames();
        p.setRecordingMixerName(recMixerNames);
        rfv.applyValues(r);
        rmv.applyValues(r);
    }


}
