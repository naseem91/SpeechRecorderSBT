//    Speechrecorder
//    (c) Copyright 2015
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


package ipsk.apps.speechrecorder.workspace.ui;

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
import java.util.List;

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
 * Dialog to rename project.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class ProjectRenameDialog extends JPanel implements ActionListener, DocumentListener {

	private static final long serialVersionUID = 1L;
	private JTextField nameField;
	private static JButton okButton;
	private static JButton cancelButton;
	private static JOptionPane selPane;

	private Document nameDoc;
	
	private JTextField statusLabel;
	private RenameModel renameModel;
	private String originalName;
	private List<String> existingNames;
	
	public static class RenameModel{
	    private String originalName;
	    private String newName;
        /**
         * @param name
         */
        public RenameModel(String name) {
           super();
           this.originalName=name;
        }
        public String getNewName() {
            return newName;
        }
        public void setNewName(String newName) {
            this.newName = newName;
        }
        public String getOriginalName() {
            return originalName;
        }
        public boolean changed(){
            if(originalName!=null){
                return !originalName.equals(newName);
            }
            return false;
        }
	}

	public ProjectRenameDialog(List<String> existingProjectNames,RenameModel renameModel) {
		super(new GridBagLayout());
		this.existingNames=existingProjectNames;
		this.renameModel=renameModel;
		this.originalName=renameModel.getOriginalName();
		okButton = new JButton("OK");
		okButton.setEnabled(false);
		okButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.setEnabled(true);
		cancelButton.addActionListener(this);
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
		
		
		c.gridy++;
		c.gridx=0;
		c.gridwidth=2;
		c.weightx=2.0;
		c.fill=GridBagConstraints.HORIZONTAL;
		statusLabel=new JTextField(30);
		statusLabel.setEditable(false);
		add(statusLabel,c);
		nameField.setText(originalName);
	}
	public static Object showDialog(
		Component parent,List<String> existingProjects, RenameModel renameModel) {

	    
		ProjectRenameDialog pv = new ProjectRenameDialog(existingProjects,renameModel);

		selPane =
			new JOptionPane(
				pv,
				JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION,
				null,
				new Object[] { okButton, cancelButton});
		JDialog dialog = selPane.createDialog(parent, "Rename Project");
		dialog.setVisible(true);

		return selPane.getValue();
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();

		if (src == okButton) {
			//			p.setAudioControllerClass(acClassNameField.getText());
			selPane.setValue(new Integer(JOptionPane.OK_OPTION));
			//dialog.dispose();
		}else if(src==cancelButton){
			selPane.setValue(new Integer(JOptionPane.CANCEL_OPTION));
		}

	}
	
	
	public String getNewName(){
	    return null;
	}

	private void documentChanged(Document d) {
	    if (d == nameDoc) {
	        String name=nameField.getText();
	        if ("".equals(name)) {
	            statusLabel.setText("Project name must not be empty.");
                okButton.setEnabled(false);
            }else if(originalName.equals(name)){
            	 statusLabel.setText("No change");
                 okButton.setEnabled(false);
            }else if(existingNames.contains(name)){
            		statusLabel.setText("Project with this name exists.");
                    okButton.setEnabled(false);
            }else{
	        FilenameValidator.ValidationResult vr=FilenameValidator.validate(name);
	        if(vr.isValid()){
	            
//	            String projectDirPath=defWorkspaceDirPath.concat(File.separator + name);
//	            File testProjectDir=new File(projectDirPath);
//	            if(testProjectDir.exists()){
//	                statusLabel.setText("\""+name+"\" already exists.");
//	                okButton.setEnabled(false);
//	            }else{
//	               
//	                try {
//                        String nameURLEncoded=URLEncoder.encode(name, "UTF-8");
                       
                        statusLabel.setText("");
                        renameModel.setNewName(name);
                        okButton.setEnabled(true);
//                    } catch (UnsupportedEncodingException e) {
//                        statusLabel.setText(e.getMessage());
//                        okButton.setEnabled(false);
//                    }
                   
//	            }
	        }else{
	            ipsk.util.LocalizableMessage lm= vr.getMessage();
	            String locText=lm.localize();
	            statusLabel.setText(locText);
	            
	            okButton.setEnabled(false);
	        }
            }
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
