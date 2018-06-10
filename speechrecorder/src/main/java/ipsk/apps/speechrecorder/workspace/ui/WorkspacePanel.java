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



package ipsk.apps.speechrecorder.workspace.ui;

import ipsk.apps.speechrecorder.workspace.WorkspaceManager;
import ipsk.swing.JDialogPanel;
import ipsk.swing.text.EditorKitMenu;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;


/**
 * @author klausj
 *
 */
public class WorkspacePanel extends JDialogPanel {

    private WorkspaceManager workspaceManager;
    private WorkspaceProjectsPanel projectsPanel;
    private JButton openButt;
    private Desktop desktop=null;
    private File workspaceDir;
    
    /**
     * 
     */
    public WorkspacePanel(WorkspaceManager workspaceManager) {
       super(JDialogPanel.Options.CANCEL);
       cancelButton.setText("Close");
       setFrameTitle("Workspace");
       Container panel=getContentPane();
       panel.setLayout(new BorderLayout());
       this.workspaceManager=workspaceManager;
       
//       GridBagConstraints c = new GridBagConstraints();
//       c.insets=new Insets(2,2,2,2);
//       c.gridx=0;
//       c.gridy=0;
//       c.fill=GridBagConstraints.BOTH;
//       c.weightx=1.0;
//       c.weighty=1.0;
       
       TitledBorder infoBorder=BorderFactory.createTitledBorder("Info");
       JPanel infoPanel=new JPanel(new GridBagLayout());
       GridBagConstraints ic=new GridBagConstraints();
       
       ic.insets=new Insets(2,2,2,2);
       ic.gridx=0;
       ic.gridy=0;
       ic.anchor=GridBagConstraints.EAST;
       infoPanel.setBorder(infoBorder);
       infoPanel.add(new JLabel("Path:"),ic);
       JTextField pathField=new JTextField();
       pathField.setEditable(false);
       new EditorKitMenu(pathField, false);
       workspaceDir=workspaceManager.getWorkspaceDir();
       pathField.setText(workspaceDir.getAbsolutePath());
       
       ic.gridx++;
       ic.weightx=2.0;
       ic.fill=GridBagConstraints.HORIZONTAL;
       infoPanel.add(pathField,ic);
       if(Desktop.isDesktopSupported()){
           desktop=Desktop.getDesktop();
           openButt = new JButton("Open in file manager");
           openButt.addActionListener(this);
           ic.gridx++;
           ic.weightx=0;
           ic.fill=GridBagConstraints.NONE;
           infoPanel.add(openButt,ic);
       }
       long freeSpace=workspaceDir.getFreeSpace();
       long freeSpaceGB=freeSpace / 1000000000;
       ic.gridx=0;
       ic.gridy++;
       ic.anchor=GridBagConstraints.EAST;
       infoPanel.add(new JLabel("Free space:"),ic);
       ic.gridx++;
       ic.anchor=GridBagConstraints.WEST;
       infoPanel.add(new JLabel(freeSpaceGB+" GB"),ic);
       panel.add(infoPanel,BorderLayout.NORTH);
       
       TitledBorder projsBorder=BorderFactory.createTitledBorder("Projects");
       projectsPanel=new WorkspaceProjectsPanel(workspaceManager);
       projectsPanel.setBorder(projsBorder);
       panel.add(projectsPanel,BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent ae){
        Object src=ae.getSource();
        if(src==openButt){
           if(desktop!=null){
               try {
                desktop.open(workspaceDir);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,"Could not open directory: "+e.getLocalizedMessage(),"Desktop open directory error", JOptionPane.ERROR_MESSAGE);
            }
           }
        }
        
        super.actionPerformed(ae);
    }

}
