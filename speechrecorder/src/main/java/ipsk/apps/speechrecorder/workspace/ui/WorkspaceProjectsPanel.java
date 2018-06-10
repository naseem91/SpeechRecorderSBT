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

import ipsk.apps.speechrecorder.config.WorkspaceProject;
import ipsk.apps.speechrecorder.workspace.WorkspaceException;
import ipsk.apps.speechrecorder.workspace.WorkspaceManager;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author klausj
 *
 */
public class WorkspaceProjectsPanel extends JPanel implements ActionListener, ListSelectionListener{


    private WorkspaceManager workspaceManager;
    private JScrollPane projectsScrollPane;
    private JList projectsList;
//    private JMenuItem openMi;
    private JMenuItem deleteMi;
    private JMenuItem renameMi;
    private ListSelectionModel listSelectionModel;
    
    /**
     * 
     */
    public WorkspaceProjectsPanel(WorkspaceManager workspaceManager) {
       super(new GridBagLayout());
      
       this.workspaceManager=workspaceManager;
       GridBagConstraints c = new GridBagConstraints();
       c.insets=new Insets(2,2,2,2);
       c.gridx=0;
       c.gridy=0;
       c.fill=GridBagConstraints.BOTH;
       c.weightx=1.0;
       c.weighty=1.0;
       projectsList=new JList(workspaceManager);
       projectsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       listSelectionModel = projectsList.getSelectionModel();
       listSelectionModel.addListSelectionListener(this);
       projectsScrollPane=new JScrollPane(projectsList);
       add(projectsScrollPane,c);
       
//       c.weightx=0;
//       c.weighty=0;
//       c.fill=GridBagConstraints.NONE;
       
//       openMi=new JMenuItem("Open");
//       openMi.addActionListener(this);
//       
       deleteMi=new JMenuItem("Delete...");
       deleteMi.addActionListener(this);
//       c.gridx=0;
//       c.gridy++;
//       add(deleteButton,c);
       
       renameMi=new JMenuItem("Rename...");
       renameMi.addActionListener(this);
       
   	JPopupMenu jPopupMenu = new JPopupMenu() {
	    @Override
	    public void show(Component invoker, int x, int y) {
	        int row = projectsList.locationToIndex(new Point(x, y));
	        if (row != -1) {
	            projectsList.setSelectedIndex(row);
	        }
	        super.show(invoker, x, y);
	    }
	};
//	jPopupMenu.add(openMi);
	jPopupMenu.add(deleteMi);
	jPopupMenu.add(renameMi);
	projectsList.setComponentPopupMenu(jPopupMenu);
       updateUIDependencies();
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object src=ae.getSource();
        WorkspaceProject selProj=null;
//        if(src==openMi || src==deleteMi || src==renameMi){
        if(src==deleteMi || src==renameMi){
            int selRow=projectsList.getSelectedIndex();
            if(selRow>=0){
                selProj=workspaceManager.getWorkspaceProjects().get(selRow);
                if(workspaceManager.locked(selProj)){
                	String msg;
//                	if(src==openMi){
//                		msg="Project already open.";
//                	}else{
                		msg="Please close project first.";
//                	}
                    JOptionPane.showMessageDialog(this,msg,"Project", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
        }
//        if(src==openMi){
//            if(selProj!=null){
//            	try {
//					workspaceManager.openProject(selProj);
//				} catch (WorkspaceException e) {
//					
//					e.printStackTrace();
//					JOptionPane.showMessageDialog(this, "Could not open project: \n"+e.getLocalizedMessage(),"Project open error",JOptionPane.ERROR_MESSAGE);
//				}
//            }
//            
//        }else 
        if(src==deleteMi){
            if(selProj!=null){
           
                File projectDir=new File(workspaceManager.getWorkspaceDir(),selProj.getConfiguration().getName());
                ProjectDeleteDialog projdelDlg=new ProjectDeleteDialog(selProj,projectDir);
                
                projdelDlg.showDialog(this);
                try {
                    workspaceManager.scanWorkspace();
                } catch (WorkspaceException e) {
                	JOptionPane.showMessageDialog(this,"Workspace scan error:\n"+e.getLocalizedMessage(), "Workspace scan error!", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        }else if(src==renameMi){
            if(selProj!=null){
                String currentName=selProj.getConfiguration().getName();
                ProjectRenameDialog.RenameModel rnM=new ProjectRenameDialog.RenameModel(currentName);
                Object res=ProjectRenameDialog.showDialog(this,new ArrayList<String>(),rnM);
                if(res.equals(JOptionPane.OK_OPTION) && rnM.changed()){
                    try {
                        workspaceManager.renameProject(currentName, rnM.getNewName());
                    } catch (WorkspaceException e) {
                        JOptionPane.showMessageDialog(this,"Could not rename project:\n"+e.getLocalizedMessage(), "Rename project error!", JOptionPane.ERROR_MESSAGE);
                    }
                }
                
              // TODO
                // rename project!!
//                File projectDir=new File(workspaceManager.getWorkspaceDir(),selProj.getConfiguration().getName());
//                ProjectDeleteDialog projdelDlg=new ProjectDeleteDialog(selProj,projectDir);
//                
//                projdelDlg.showDialog(this);
//                try {
//                    workspaceManager.scanWorkspace();
//                } catch (WorkspaceException e) {
//                   
//                }
            }
            
        }
        updateUIDependencies();
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    @Override
    public void valueChanged(ListSelectionEvent lse) {
        updateUIDependencies();
    }

    /**
     * 
     */
    private void updateUIDependencies() {
       ListSelectionModel lsm=projectsList.getSelectionModel();
//       deleteButton.setEnabled(lsm!=null && !lsm.isSelectionEmpty());
           
    }

   

}
