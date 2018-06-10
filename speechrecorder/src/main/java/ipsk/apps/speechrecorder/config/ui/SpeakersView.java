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

import ipsk.apps.speechrecorder.config.SpeakersConfiguration;
import ipsk.swing.text.EditorKitMenu;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * Speakers database configuration UI.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class SpeakersView extends JPanel implements DocumentListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JTextField urlField;
	private Document urlDoc;
	private JPanel content;

	private JButton speakersUrlBrowseButton;
	public SpeakersView() {
		super(new BorderLayout());
//		this.s = s;
		content = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weighty=0;
		content.add(new JLabel("Speakers URL:"), c);
		
		urlField = new JTextField();
		EditorKitMenu urlFieldEkm=new EditorKitMenu(urlField);
		urlFieldEkm.setPopupMenuActiv(true);
		urlDoc=urlField.getDocument();
		urlDoc.addDocumentListener(this);
       
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx++;
		content.add(urlField, c);
		speakersUrlBrowseButton = new JButton("Browse...");
		speakersUrlBrowseButton.addActionListener(this);
         c.fill=GridBagConstraints.NONE;
		c.gridx++;
		c.weightx = 0;
		content.add(speakersUrlBrowseButton, c);
		add(content, BorderLayout.NORTH);

	}
	  /**
     * @param speakersConfig
     */
    public void setSpeakersConfiguration(SpeakersConfiguration speakersConfig) {
        String spksUriStr=speakersConfig.getSpeakersUrl();
//        URI spksUri=URI.create(spksUriStr);
        
        urlField.setText(spksUriStr);
        
    }
    
	public void applyValues(SpeakersConfiguration s){
	    s.setSpeakersUrl(urlField.getText());
	}
	
	public void documentChanged(DocumentEvent de) {
		Document src = de.getDocument();
		if (src == urlDoc) {
//			s.setSpeakersUrl(urlField.getText());
		} 
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

	public void actionPerformed(ActionEvent e) {
		Object src=e.getSource();
		if (src == speakersUrlBrowseButton) {
			JFileChooser chooser = new JFileChooser();
			//chooser.setDialogTitle(uiString.getString("SelectPromptFile"));
			chooser.setDialogTitle("Select speaker database file");
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
		
	}

  
}
