//    Speechrecorder
// 	  (c) Copyright 2014
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



package ipsk.apps.speechrecorder.session;

import ipsk.awt.ProgressListener;
import ipsk.awt.WorkerException;
import ipsk.awt.event.ProgressErrorEvent;
import ipsk.awt.event.ProgressEvent;
import ipsk.io.DeleteDirectoryWorker;
import ipsk.swing.JDialogPanel;
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

/**
 * @author klausj
 *
 */
public class SessionDeleteDialog extends JDialogPanel implements ProgressListener {

    
    private String sessionId;
    private File sessionDir;
    private JTextField messageLabel;
    private JProgressBar progressBar;
    
    private static final String DELETE_SESSION_ACTION_CMD="delete_session";
    
    /**
     * 
     */
    public SessionDeleteDialog(String sessionId,File sessionDir) {
       super("Delete session");
       this.sessionId=sessionId;
       this.sessionDir=sessionDir;
       okButton.setText("Delete");
       okButton.setActionCommand(DELETE_SESSION_ACTION_CMD);
       Container cp=getContentPane();
       cp.setLayout(new GridBagLayout());
       GridBagConstraints c=new GridBagConstraints();
       c.gridx=0;
       c.gridy=0;
       cp.add(new JLabel("Session: "),c);
       c.gridx++;
       JTextField nameField=new JTextField(sessionId);
       nameField.setEditable(false);
       cp.add(nameField,c);
       c.gridx=0;
       c.gridy++;
       cp.add(new JLabel("Status:"),c);
       c.gridx++;
       messageLabel=new JTextField(20);
       messageLabel.setEditable(false);
       cp.add(messageLabel,c);
       c.gridx=0;
       c.gridy++;
       c.gridwidth=2;
       progressBar=new JProgressBar();
       cp.add(progressBar,c);
//       c.gridy++;
//       deleteButton=new JButton("Delete");
//       deleteButton.addActionListener(this);
//       cp.add(deleteButton,c);
       
    }
    
    
    public void doReallyDelete(){
        // deleting project simply means deleting the project directory
        DeleteDirectoryWorker deleteWorker=new DeleteDirectoryWorker();
        deleteWorker.setDirectory(sessionDir);
        deleteWorker.addProgressListener(this);
//        JProgressDialogPanel progressDialog=new JProgressDialogPanel(deleteWorker,"Delete project","Deleting...");
        try {
            deleteWorker.open();
            okButton.setEnabled(false);
            deleteWorker.start();
            
//            progressDialog.showDialog((JFrame)null);
        } catch (WorkerException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae){
        Object aSrc=ae.getSource();
        if(aSrc==okButton){
        	if(DELETE_SESSION_ACTION_CMD.equals(okButton.getActionCommand())){
        	    int ans=JOptionPane.showConfirmDialog(this,"Do you really want to delete this session?\nAll recordings,speaker and session data will be deleted!\nThis action cannot be undone!", "Confirm session delete!", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE );
        	    if(ans==JOptionPane.YES_OPTION){
        	        doReallyDelete();
        	    }
        	}else{
        		super.actionPerformed(ae);
        	}
        }else{
            super.actionPerformed(ae);
        }
    }
    
    public void update(ProgressEvent progressEvent) {
        ProgressStatus ps=progressEvent.getProgressStatus();
        if (ps != null) {
            LocalizableMessage message=ps.getMessage();
            if (message != null) {
                messageLabel.setText(message.localize());
            }
        }
        if(progressEvent instanceof ProgressErrorEvent){
      
            messageLabel.setForeground(Color.RED);
        }else{

            if (ps != null) {
                Short pProgr=ps.getPercentProgress();
                if(pProgr!=null){
                    progressBar.setValue((int) ps.getPercentProgress());
                }

                if(ps.isError()){
                    
                    setValue(OK_OPTION);
                    okButton.setText("OK");
                    okButton.setEnabled(true);
                }
                if(ps.isDone()){
                    cancelButton.setEnabled(false);
                    setValue(OK_OPTION);
                    okButton.setText("OK");
                    okButton.setActionCommand("ok");
                    okButton.setEnabled(true);
                }
            }
        }
        }

}
