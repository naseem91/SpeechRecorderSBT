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

import ipsk.apps.speechrecorder.config.AudioClipView;
import ipsk.apps.speechrecorder.config.ViewConfiguration;
import ipsk.apps.speechrecorder.config.ui.view.AudioClipViewConfigurationUI;

import javax.swing.JTabbedPane;

/**
 * UI panel for prompts (stimuli) configuration.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class ViewConfigurationUI extends JTabbedPane{

	private static final long serialVersionUID = 1L;
	//	private PromptConfiguration p;
	private AudioClipViewConfigurationUI acvcui;
   

	public ViewConfigurationUI() {
		super();
//		this.p = p;
		acvcui=new AudioClipViewConfigurationUI();
        
        addTab("Audio clip view",acvcui);
    }
	 /**
     * @param viewConfiguration
     */
    public void setViewConfiguration(ViewConfiguration viewConfiguration) {
        AudioClipView acv=viewConfiguration.getAudioClipView();
        acvcui.setConfiguration(acv);
        setDependencies();
    }
    
	public void applyValues(ViewConfiguration v){
	    acvcui.applyValues(v.getAudioClipView());
	   
	}
	
	private void setDependencies() {
		//buttonsInPromptWindowCheckBox.setEnabled(p.getShowPromptWindow());
		//buttonsInPromptWindowLabel.setEnabled(p.getShowPromptWindow());
	}

   


}
