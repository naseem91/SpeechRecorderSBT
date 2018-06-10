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
public class PromptCommonConfigurationView extends JConfigPanel implements ActionListener, DocumentListener {

	private static final long serialVersionUID = 1L;
	private JCheckBox autoPromptPlayCheckBox;
	private JCheckBox recManualPlayCheckBox;
	private JCheckBox instructionNumberingCheckBox;
	//private UIResources uiString;
	private JCheckBox selectPromptFileCheckBox;
	private JButton promptsUrlBrowseButton;
	private JTextField urlField;
	String defaultScriptUrl;
	private Document urlDoc;
	private PromptConfiguration initalPromptConfiguration=null;

	public PromptCommonConfigurationView(String defaultScriptUrl) {
		super();
		this.defaultScriptUrl=defaultScriptUrl;
//		this.p = p;
		//uiString = UIResources.getInstance();
//		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
//		JPanel psp = new JPanel(new GridLayout(1, 3));

		JPanel pp = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 5, 2, 5);
		c.gridx = 0;
		c.gridy = 0;
		
		pp.add(new JLabel("Select recording script manually:"), c);
		selectPromptFileCheckBox=new JCheckBox();
		selectPromptFileCheckBox.addActionListener(this);
		c.gridx++;
		pp.add(selectPromptFileCheckBox,c);
		
		c.gridx=0;
		c.gridy++;
		pp.add(new JLabel("Prompts URL:"), c);
		urlField = new JTextField();
		urlDoc = urlField.getDocument();
		urlDoc.addDocumentListener(this);
		EditorKitMenu urlFieldEkm=new EditorKitMenu(urlField);
		urlFieldEkm.setPopupMenuActiv(true);
		c.gridx++;
		c.weightx = 1;
		pp.add(urlField, c);
		promptsUrlBrowseButton = new JButton("Browse...");
		promptsUrlBrowseButton.addActionListener(this);
		c.gridx++;
		c.weightx = 0;
		pp.add(promptsUrlBrowseButton, c);
		
		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		pp.add(new JLabel("Automatic prompt play:"), c);
		autoPromptPlayCheckBox = new JCheckBox();
		autoPromptPlayCheckBox.addActionListener(this);
		c.gridx++;
		pp.add(autoPromptPlayCheckBox, c);
		
		c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        pp.add(new JLabel("Enable manual prompt start and stop while recording:"), c);
        recManualPlayCheckBox = new JCheckBox();
        recManualPlayCheckBox.addActionListener(this);
        c.gridx++;
        pp.add(recManualPlayCheckBox, c);
        
		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		pp.add(new JLabel("Instruction numbering:"), c);
		instructionNumberingCheckBox = new JCheckBox();
	
		instructionNumberingCheckBox.addActionListener(this);
		c.gridx++;
		pp.add(instructionNumberingCheckBox, c);
		
		setDependencies();
//		add(psp, BorderLayout.CENTER);
		add(pp, BorderLayout.CENTER);

	}
	
	/**
     * @param promptConfiguration
     */
    public void setPromptConfiguration(PromptConfiguration promptConfiguration) {
    	initalPromptConfiguration=promptConfiguration;
    	String cfgPromptsUrl=promptConfiguration.getPromptsUrl();
    	boolean selectpromptsFileManually=(cfgPromptsUrl==null);
    	selectPromptFileCheckBox.setSelected(selectpromptsFileManually);
    	String promptsUrl=defaultScriptUrl;
    	if(!selectpromptsFileManually){
    		promptsUrl=cfgPromptsUrl;
    	}
    	urlField.setText(promptsUrl);
       autoPromptPlayCheckBox.setSelected(promptConfiguration.getAutomaticPromptPlay());
       recManualPlayCheckBox.setSelected(promptConfiguration.getRecManualPlay());
       instructionNumberingCheckBox.setSelected(promptConfiguration.getInstructionNumbering());
       setDependencies();
    }

	public void applyValues(PromptConfiguration p){
	    p.setAutomaticPromptPlay(autoPromptPlayCheckBox.isSelected());
	    p.setRecManualPlay(recManualPlayCheckBox.isSelected());
        p.setInstructionNumbering(instructionNumberingCheckBox.isSelected());
        String promptsUrl=null;
        boolean selectPromptsFile=selectPromptFileCheckBox.isSelected();
        if(!selectPromptsFile){
        	promptsUrl=urlField.getText();
        }
        p.setPromptsUrl(promptsUrl);
        
	}
	
	private void setDependencies() {
		boolean selectablePromptsUrl=!selectPromptFileCheckBox.isSelected();
		promptsUrlBrowseButton.setEnabled(selectablePromptsUrl);
		urlField.setEnabled(selectablePromptsUrl);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		if (src == autoPromptPlayCheckBox) {
//			p.setAutomaticPromptPlay(autoPromptPlayCheckBox.isSelected());
		} else if (src == instructionNumberingCheckBox) {
//			p.setInstructionNumbering(instructionNumberingCheckBox.isSelected());
		}else if (src == selectPromptFileCheckBox) {
			setDependencies();
		}else if (src == promptsUrlBrowseButton) {
		
			JFileChooser chooser = new JFileChooser();
			//chooser.setDialogTitle(uiString.getString("SelectPromptFile"));
			chooser.setDialogTitle("Select prompt file");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
//				urlField.setText("file:" + chooser.getSelectedFile().getAbsolutePath());
				File selFile=chooser.getSelectedFile();
            	URI selFileUri=selFile.toURI();
            	String selFileuriStr=selFileUri.toString();
            	urlField.setText(selFileuriStr);
			}
		}
		super.actionPerformed(ae);
		setDependencies();
	}

	public void documentChanged(DocumentEvent de) {
//		Document src = de.getDocument();
//		if (src == urlDoc) {
//			p.setPromptsUrl(urlField.getText());
//		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent arg0) {
		documentChanged(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent arg0) {
		documentChanged(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent arg0) {
		documentChanged(arg0);
	}

	/* (non-Javadoc)
	 * @see ipsk.swing.panel.JConfigPanel#resetToDefaults()
	 */
	@Override
	public void resetToDefaults() {
		selectPromptFileCheckBox.setSelected(false);
		urlField.setText(defaultScriptUrl);
		
		PromptConfiguration defPc=new PromptConfiguration();
		recManualPlayCheckBox.setSelected(defPc.getRecManualPlay());
		instructionNumberingCheckBox.setSelected(defPc.getInstructionNumbering());
		autoPromptPlayCheckBox.setSelected(defPc.getAutomaticPromptPlay());
	}

	/* (non-Javadoc)
	 * @see ipsk.swing.panel.JConfigPanel#resetToInitial()
	 */
	@Override
	public void resetToInitial() {
		setPromptConfiguration(initalPromptConfiguration);
	}

    
}
