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

import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import ipsk.apps.speechrecorder.config.PromptConfiguration;
import ipsk.apps.speechrecorder.config.ui.prompt.PromptAudioView;
import ipsk.apps.speechrecorder.config.ui.prompt.PromptBeepConfigurationView;
import ipsk.apps.speechrecorder.config.ui.prompt.PromptCommonConfigurationView;
import ipsk.apps.speechrecorder.config.ui.prompt.PromptFontsConfigurationView;
import ipsk.apps.speechrecorder.config.ui.prompt.PromptScriptConfigurationView;
import ipsk.apps.speechrecorder.config.ui.prompt.PromptSpeakerPrompterConfigurationView;
import ipsk.apps.speechrecorder.config.ui.prompt.StartStopSignalConfigurationView;
import ipsk.audio.AudioController2;

import java.net.URL;

import javax.swing.JTabbedPane;

/**
 * UI panel for prompts (stimuli) configuration.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class PromptConfigurationView extends JTabbedPane{

	private static final long serialVersionUID = 1L;
	//	private PromptConfiguration p;
	private StartStopSignalConfigurationView ssscv;
	private PromptFontsConfigurationView pfcv;
    private PromptCommonConfigurationView pccv;
    private PromptSpeakerPrompterConfigurationView pspcv;
    private PromptScriptConfigurationView pscv;
    private PromptAudioView pacv;
    private PromptBeepConfigurationView pbcv;
//    private AudioController2 audioController;

	public PromptConfigurationView(AudioController2 audioController,KeyInputMapView keyInputMapView,String defaultScriptUrl) {
		super();
//		this.audioController=audioController;
		ssscv=new StartStopSignalConfigurationView();
		pfcv=new PromptFontsConfigurationView();
        pccv=new PromptCommonConfigurationView(defaultScriptUrl);
        pspcv=new PromptSpeakerPrompterConfigurationView(keyInputMapView);
        pscv=new PromptScriptConfigurationView();
        pacv=new PromptAudioView(audioController);
        pbcv=new PromptBeepConfigurationView();
        
        addTab("Start stop signal",ssscv);
        addTab("Fonts",pfcv);
        addTab("Common", pccv);
        addTab("Speaker window", pspcv);
        addTab("Script",pscv);
        addTab("Audio",pacv);
        addTab("Beep",pbcv);
    }
	
	/**
	 * Set project configuration
     * @param projectConfiguration project configuration
     */
    public void setProjectConfiguration(ProjectConfiguration projectConfiguration){
        
        PromptConfiguration promptConfiguration=projectConfiguration.getPromptConfiguration();
        ssscv.setPromptConfiguration(promptConfiguration);
        pfcv.setPromptConfiguration(promptConfiguration);
        pccv.setPromptConfiguration(promptConfiguration);
        pspcv.setPromptConfiguration(promptConfiguration);
        pscv.setPromptConfiguration(promptConfiguration);
        pacv.setProjectConfiguration(projectConfiguration);
        pbcv.setPromptBeep(promptConfiguration.getPromptBeep());
//        setDependencies();
    }
    
    public void setProjectContext(URL projectContext){
    	pbcv.setProjectContext(projectContext);
    }
	
	public void applyValues(ProjectConfiguration p){
	    PromptConfiguration pc=p.getPromptConfiguration();
	    ssscv.applyValues(pc);
	    pfcv.applyValues(pc);
	    pccv.applyValues(pc);
	    pspcv.applyValues(pc);
	    pscv.applyValues(pc);
	    pacv.applyValues(p);
	    pbcv.applyValues(pc.getPromptBeep());
	}
	
}
