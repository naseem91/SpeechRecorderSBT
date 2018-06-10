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
import ipsk.audio.AudioController2;
import ipsk.swing.text.EditorKitMenu;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.UUID;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;


/**
 * Panel for general project properties.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class ProjectConfigurationPanel extends JPanel implements DocumentListener {

	private static final long serialVersionUID = 1L;
	private ProjectConfiguration p;
//	private JPanel jp;
	private JTextField nameField;
	private Document nameDoc;
	private JTextField uuidField;
	private JTextArea descrArea;
	private Document descrDoc;

	public ProjectConfigurationPanel(AudioController2 audioController) {
		super(new GridBagLayout());
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 5, 2, 5);
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty=0.0;
		add(new JLabel("Name"), c);
		c.gridx++;
		c.weightx = 1;
		nameField = new JTextField(20);
		// display project name bold
		Font defFont=getFont();
		if(defFont!=null){
		    Font boldFont=defFont.deriveFont(Font.BOLD);
		    nameField.setFont(boldFont);
		}
		nameField.setEditable(false);
		nameDoc = nameField.getDocument();
		nameDoc.addDocumentListener(this);
		EditorKitMenu nameEkm=new EditorKitMenu(nameField,false);
		nameEkm.setPopupMenuActiv(true);
		add(nameField, c);
		
		c.gridx=0;
		c.gridy++;
		add(new JLabel("UUID"), c);
        c.gridx++;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.LINE_START;
        uuidField = new JTextField(36);
        uuidField.setEditable(false);  
        EditorKitMenu uuidEkm=new EditorKitMenu(uuidField,false);
        uuidEkm.setPopupMenuActiv(true);
        add(uuidField, c);

		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel("Description"), c);
		c.gridx++;
		c.weightx = 1;
		descrArea = new JTextArea(5, 20);
		
		descrDoc = descrArea.getDocument();
		descrDoc.addDocumentListener(this);
		EditorKitMenu edMenu=new EditorKitMenu(descrArea);
		edMenu.setPopupMenuActiv(true);
		JScrollPane sp = new JScrollPane(descrArea);
		add(sp, c);
		//add(jp, BorderLayout.NORTH);
		
		//setProjectConfiguration(p);
	}

    /**
     * @param project
     */
    public void setProjectConfiguration(ProjectConfiguration project) {
        this.p=project;
        nameField.setText(p.getName());
        String uuidStr="";
        UUID uuid=p.getUuid();
        if(uuid!=null){
            uuidStr=uuid.toString();
        }
        uuidField.setText(uuidStr);
        descrArea.setText(p.getDescription());
    }
    
	public void applyValues(ProjectConfiguration template){
	    template.setName(nameField.getText());
	    template.setDescription(descrArea.getText());
	 
	}
	
	public void documentChanged(DocumentEvent de) {
//		Document src = de.getDocument();
//		if (src == nameDoc) {
//			p.setName(nameField.getText());
//		} else if (src == descrDoc) {
//			p.setDescription(descrArea.getText());
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




}
