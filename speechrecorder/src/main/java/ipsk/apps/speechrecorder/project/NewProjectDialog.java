//    Speechrecorder
//    (c) Copyright 2012
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

package ipsk.apps.speechrecorder.project;

import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import ipsk.io.FilenameValidator;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * Dialog to create new project.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class NewProjectDialog extends JPanel implements ActionListener, DocumentListener {

	private static final long serialVersionUID = 1L;
	private String defWorkspaceDirPath;
	private String projectDir;
	private JCheckBox defCheckBox;
	private JTextField nameField;
	private JTextField dirField;
	private JRadioButton emptyScript;
	private JRadioButton exampleScript;
	private ButtonGroup scriptTemplateSelButtGroup;
	//private JTextArea descrArea;
	private static JButton okButton;
	private static JOptionPane selPane;

	//private boolean useDefDir = true;
	private Document nameDoc;
	private Document dirDoc;
	private NewProjectConfiguration newProjectConfiguration;
	private ProjectConfiguration projectConfiguration;
	private JTextField statusLabel;
	

	public NewProjectDialog(NewProjectConfiguration newProjectConfiguration, File defWorkspaceDir) {
		super(new GridBagLayout());
		this.newProjectConfiguration=newProjectConfiguration;
		this.projectConfiguration = newProjectConfiguration.getProjectConfiguration();
		defWorkspaceDirPath=defWorkspaceDir.getPath();
		//projectConfiguration.setDirectory(defWorkspaceDirPath);
		okButton = new JButton("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(this);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 10, 5, 10);
		c.gridx = 0;
		c.gridy = 0;
		add(new JLabel("Name"), c);
		c.gridx++;
		c.weightx = 1;
		nameField = new JTextField(20);
		nameDoc = nameField.getDocument();
		nameDoc.addDocumentListener(this);
		add(nameField, c);
		
//		c.gridx = 0;
//		c.gridy++;
//		add(new JLabel("Use default directory"), c);
//		c.gridx++;
//		defCheckBox = new JCheckBox();
//		defCheckBox.setSelected(true);
//		defCheckBox.addActionListener(this);
//		add(defCheckBox, c);
		
		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		add(new JLabel("Project directory"), c);
		c.gridx++;
		c.weightx = 1;
		dirField = new JTextField(20);
		dirField.setText(projectConfiguration.getDirectory());
		dirField.setEnabled(false);
		dirDoc = dirField.getDocument();
		dirDoc.addDocumentListener(this);
		add(dirField, c);
		
		scriptTemplateSelButtGroup=new ButtonGroup();
		c.gridx=0;
		c.gridy++;
		emptyScript=new JRadioButton("Load empty or");
		add(emptyScript,c);
		scriptTemplateSelButtGroup.add(emptyScript);
		//emptyScript.addActionListener(this);
		c.gridx++;
		exampleScript=new JRadioButton("example project");
		add(exampleScript,c);
		//exampleScript.addActionListener(this);
		scriptTemplateSelButtGroup.add(exampleScript);
		exampleScript.addActionListener(this);
		emptyScript.setSelected(true);

		c.gridy++;
		c.gridx=0;
		c.gridwidth=2;
		c.weightx=2.0;
		c.fill=GridBagConstraints.HORIZONTAL;
		statusLabel=new JTextField(30);
		statusLabel.setEditable(false);
		add(statusLabel,c);
	}
	public static Object showDialog(
		Component parent,
		NewProjectConfiguration newProjectConfiguration,
		File defWorkspaceDir) {

		NewProjectDialog pv = new NewProjectDialog(newProjectConfiguration, defWorkspaceDir);

		selPane =
			new JOptionPane(
				pv,
				JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION,
				null,
				new Object[] { okButton, "Cancel" });
		JDialog dialog = selPane.createDialog(parent, "New Project");
		dialog.setVisible(true);

		return selPane.getValue();
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();

		if (src == defCheckBox) {
			//projectConfiguration.setDirectory(defWorkspaceDirPath.concat(File.separator + projectConfiguration.getName()));
			projectDir=defWorkspaceDirPath.concat(File.separator + projectConfiguration.getName());
			dirField.setText(projectDir);
			dirField.setEnabled(!defCheckBox.isSelected());

		} else if (src == dirField) {
			

		}else if (src == exampleScript || src==emptyScript) {
            newProjectConfiguration.setUseExampleScript(exampleScript.isSelected());

        } else if (src == okButton) {
			//			p.setAudioControllerClass(acClassNameField.getText());
			selPane.setValue(new Integer(JOptionPane.OK_OPTION));
			//dialog.dispose();
		}

	}
	
	
	public NewProjectConfiguration getNewProjectConfiguration(){
	    NewProjectConfiguration npc=new NewProjectConfiguration();
	    npc.setProjectConfiguration(projectConfiguration);
	    npc.setUseExampleScript(exampleScript.isSelected());
	    return npc;
	}

	private void documentChanged(Document d) {
	    if (d == nameDoc) {
	        String name=nameField.getText();
	        if ("".equals(name)) {
	            statusLabel.setText("Project name must not be empty.");
                okButton.setEnabled(false);
            }else{
	        FilenameValidator.ValidationResult vr=FilenameValidator.validate(name);
	        if(vr.isValid()){
	            
	            String projectDirPath=defWorkspaceDirPath.concat(File.separator + name);
	            File testProjectDir=new File(projectDirPath);
	            if(testProjectDir.exists()){
	                statusLabel.setText("\""+name+"\" already exists.");
	                okButton.setEnabled(false);
	            }else{
	               
	                try {
                        String nameURLEncoded=URLEncoder.encode(name, "UTF-8");
                        projectConfiguration.setName(name);
                        //projectConfiguration.setDirectory(defWorkspaceDirPath.concat(File.separator + projectConfiguration.getName()));
                        projectDir=defWorkspaceDirPath.concat(File.separator + nameURLEncoded);
                        //          if (defCheckBox.isSelected()) {
                        //              dirField.setText(projectDir);
                        //          }else{
                        //              dirField.setText(projectConfiguration.getDirectory());
                        //          }

                        dirField.setText(projectDir);
                        statusLabel.setText("");
                        okButton.setEnabled(true);
                    } catch (UnsupportedEncodingException e) {
                        statusLabel.setText(e.getMessage());
                        okButton.setEnabled(false);
                    }
                   
	            }
	        }else{
	            ipsk.util.LocalizableMessage lm= vr.getMessage();
	            String locText=lm.localize();
	            statusLabel.setText(locText);
	            
	            okButton.setEnabled(false);
	        }
            }
	    } else if (d == dirDoc) {
	        //projectConfiguration.setDirectory(dirField.getText());
	    }
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent arg0) {
		documentChanged(arg0.getDocument());

	}
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent arg0) {

		documentChanged(arg0.getDocument());
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent arg0) {

		documentChanged(arg0.getDocument());
	}

}
