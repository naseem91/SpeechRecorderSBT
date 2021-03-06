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
import ipsk.apps.speechrecorder.config.ui.audio.ControllerSelector;
import ipsk.apps.speechrecorder.config.ui.audio.DeviceChooserTabs;
import ipsk.audio.AudioController2;
import ipsk.audio.AudioController2.DeviceType;

import javax.swing.JTabbedPane;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class ProjectAudioConfigPanel extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	private AudioController2 audioController;
	private DeviceChooserTabs captureListChooser;
	private DeviceChooserTabs playbackListChooser;
	private DeviceChooserTabs promptPlaybackListChooser;
    private ControllerSelector controllerSelector;


    
	public ProjectAudioConfigPanel(AudioController2 audioController) {
		super();
		
		this.audioController=audioController;
		captureListChooser=new DeviceChooserTabs(audioController,DeviceType.CAPTURE);
		addTab("Recording",captureListChooser);
		playbackListChooser=new DeviceChooserTabs(audioController, DeviceType.PLAYBACK);
		addTab("Playback", playbackListChooser);
		promptPlaybackListChooser=new DeviceChooserTabs(audioController, DeviceType.PLAYBACK);
		addTab("Prompt Playback", promptPlaybackListChooser);
		controllerSelector = new ControllerSelector();
		addTab("Controller", controllerSelector);
		
	}

	/**
     * @param project
     */
    public void setProjectConfiguration(ProjectConfiguration project) {
//        acClassNameField.setText(project.getAudioControllerClass());
        
        controllerSelector.setProjectConfiguration(project);
        MixerName[] orgRecMixerNames=project.getRecordingMixerName();
        MixerName[] recMixerNames=ConfigHelper.getAJSConvertedMixerNames(audioController, orgRecMixerNames);
        captureListChooser.setSelectedMixerNames(recMixerNames);
//        useDefRecMixerBox.setSelected(recMixerNames==null);
//        recMixerChooser.setSelectedMixerNames(recMixerNames);
        MixerName[] orgPlayMixerNames=project.getPlaybackMixerName();
        MixerName[] playMixerNames=ConfigHelper.getAJSConvertedMixerNames(audioController, orgPlayMixerNames);
//        useDefPlayMixerBox.setSelected(playMixerNames==null);
//        playMixerChooser.setSelectedMixerNames(playMixerNames);
        playbackListChooser.setSelectedMixerNames(playMixerNames);
        
        MixerName[] promptPlayMixerNames=project.getPromptPlaybackMixerName();
        promptPlaybackListChooser.setSelectedMixerNames(promptPlayMixerNames);
//        updateEnabling();
    }
  


	public void applyValues(ProjectConfiguration p){
//	    p.setAudioControllerClass(acClassNameField.getText());
//	    if(useDefRecMixerBox.isSelected()){
//            p.setRecordingMixerName(null);   
//           }else{
	    captureListChooser.stopEditing();
           MixerName[] recMixerNames=captureListChooser.getSelectedMixerNames();
           p.setRecordingMixerName(recMixerNames);
//           
//           }
//	    if(useDefPlayMixerBox.isSelected()){
//            p.setPlaybackMixerName(null);
//            
//        } else{
           playbackListChooser.stopEditing();
            MixerName[] playMixerNames=playbackListChooser.getSelectedMixerNames();
            p.setPlaybackMixerName(playMixerNames);
//        }
            
            promptPlaybackListChooser.stopEditing();
            MixerName[] promptPlayMixerNames=promptPlaybackListChooser.getSelectedMixerNames();
            p.setPromptPlaybackMixerName(promptPlayMixerNames);
	}
	



}
