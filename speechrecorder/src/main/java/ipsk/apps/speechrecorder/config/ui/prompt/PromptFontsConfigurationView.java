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

package ipsk.apps.speechrecorder.config.ui.prompt;

import ipsk.apps.speechrecorder.config.PromptConfiguration;

import javax.swing.JTabbedPane;

/**
 * UI tab panel for prompt fonts configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class PromptFontsConfigurationView extends  JTabbedPane {

	private static final long serialVersionUID = 1L;
	
	
	private PromptInstructionsFontConfigurationView iv;
	private PromptFontConfigurationView pv;
	private PromptDescriptionFontConfigurationView dv;

	public PromptFontsConfigurationView() {
		super();

		pv = new PromptFontConfigurationView();
		addTab("Prompt font",pv);
		iv = new PromptInstructionsFontConfigurationView();
		addTab("Instruction font",iv);
		dv = new PromptDescriptionFontConfigurationView();
		addTab("Description font",dv);
	}
	
	/**
     * @param promptConfiguration
     */
    public void setPromptConfiguration(PromptConfiguration promptConfiguration) {
       pv.setPromptConfiguration(promptConfiguration);
       iv.setPromptConfiguration(promptConfiguration);
       dv.setPromptConfiguration(promptConfiguration);
    }

	public void applyValues(PromptConfiguration p){
	    pv.applyValues(p);
	    iv.applyValues(p);
	    dv.applyValues(p);
	}
	

}
