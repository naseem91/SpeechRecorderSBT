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

import ipsk.apps.speechrecorder.config.ItemcodeGeneratorConfiguration;
import ipsk.apps.speechrecorder.config.PromptConfiguration;
import ipsk.apps.speechrecorder.script.ui.ItemcodeGeneratorConfigurationUI;
import ipsk.swing.TitledPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/**
 * UI panel for prompts (stimuli) configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class PromptScriptConfigurationView extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private ItemcodeGeneratorConfigurationUI itemcodeGeneratorConfigurationUI;
	public PromptScriptConfigurationView() {
		super(new BorderLayout());
		TitledPanel itemcodeGeneratorPanel=new TitledPanel("Item code generator");
		itemcodeGeneratorConfigurationUI=new ItemcodeGeneratorConfigurationUI();
		itemcodeGeneratorPanel.add(itemcodeGeneratorConfigurationUI);
		add(itemcodeGeneratorPanel,BorderLayout.CENTER);
	}
	
	/**
     * @param promptConfiguration
     */
    public void setPromptConfiguration(PromptConfiguration promptConfiguration) {
       ItemcodeGeneratorConfiguration iccf=promptConfiguration.getItemcodeGeneratorConfiguration();
       itemcodeGeneratorConfigurationUI.setItemcodeGeneratorConfiguration(iccf);
       setDependencies();
    }

	public void applyValues(PromptConfiguration p){
	   itemcodeGeneratorConfigurationUI.applyValues(p.getItemcodeGeneratorConfiguration());
	}
	
	private void setDependencies() {
		//buttonsInPromptWindowCheckBox.setEnabled(p.getShowPromptWindow());
		//buttonsInPromptWindowLabel.setEnabled(p.getShowPromptWindow());
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		
		setDependencies();
	}


    
}
