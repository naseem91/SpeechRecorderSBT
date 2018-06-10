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

import ipsk.apps.speechrecorder.config.FontView;
import ipsk.apps.speechrecorder.config.PromptConfiguration;
import ipsk.swing.panel.JConfigPanel;
import ipsk.swing.text.EditorKitMenu;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * UI panel for prompts (stimuli) configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class PromptFontConfigurationView extends JConfigPanel{

	private static final long serialVersionUID = 1L;
	
	private FontView fv;
	private PromptConfiguration initialConfig;

	public PromptFontConfigurationView() {
		super();
		
		JPanel cp=getContentPane(); 
		cp.setLayout(new BorderLayout());
		fv = new FontView();
		cp.add(fv,BorderLayout.CENTER);

	}
	
	/**
     * @param promptConfiguration
     */
    public void setPromptConfiguration(PromptConfiguration promptConfiguration) {
       fv.setSelectedFont(promptConfiguration.getPromptFont());
      initialConfig=promptConfiguration;
    }

	public void applyValues(PromptConfiguration p){
	    fv.applySelectedFont(p.getPromptFont());
	   
	}
	
	  /* (non-Javadoc)
     * @see ipsk.swing.panel.JConfigPanel#resetToDefaults()
     */
    @Override
    public void resetToDefaults() {
        PromptConfiguration defPc=new PromptConfiguration();
        fv.setSelectedFont(defPc.getPromptFont());
    }

    /* (non-Javadoc)
     * @see ipsk.swing.panel.JConfigPanel#resetToInitial()
     */
    @Override
    public void resetToInitial() {
        if(initialConfig!=null){
            fv.setSelectedFont(initialConfig.getPromptFont());
        }
    }
}
