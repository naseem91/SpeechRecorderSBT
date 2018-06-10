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
 * Date  : Jan 15, 2008
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.workspace;

import ipsk.apps.speechrecorder.PluginLoadingException;
import ipsk.apps.speechrecorder.SpeechRecorder;
import ipsk.apps.speechrecorder.config.ConfigHelper;
import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import ipsk.apps.speechrecorder.config.PromptConfiguration;
import ipsk.apps.speechrecorder.config.SpeakersConfiguration;
import ipsk.apps.speechrecorder.config.WorkspaceProject;
import ipsk.apps.speechrecorder.script.RecscriptManagerException;
import ipsk.apps.speechrecorder.storage.StorageManagerException;
import ipsk.audio.AudioControllerException;
import ipsk.beans.DOMCodec;
import ipsk.beans.DOMCodecException;
import ipsk.io.FileUtils;
import ipsk.net.URLContext;
import ipsk.net.Utils;
import ipsk.xml.DOMConverter;
import ipsk.xml.DOMConverterException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;

import org.w3c.dom.Document;

/**
 * Workspace manager.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class WorkspaceManager extends AbstractListModel {

    public static final String PROJECT_CONFIG_PACKAGE = "ipsk.apps.speechrecorder.config.ProjectConfiguration";

    public static final int COL_NAME=0;
    //private SpeechRecorder speechRecorder;
    private File workspaceDir = null;
    private List<WorkspaceProject> workspaceProjects=new ArrayList<WorkspaceProject>();
    private PropertyChangeSupport propertyChangeSupport;
    private HashSet<String> lockedProjects=new HashSet<String>();
    
    
    public WorkspaceManager(File workspaceDir) {
    	//this.speechRecorder=speechrecorder;
        this.workspaceDir = workspaceDir;
        propertyChangeSupport=new PropertyChangeSupport(this);
    }

    public void lock(String projectName){
        lockedProjects.add(projectName);
    }
    
    public void unlock(String projectName){
        lockedProjects.remove(projectName);
    }
    
    public boolean locked(String projectName){
        return(lockedProjects.contains(projectName));
    }
    
    public boolean locked(WorkspaceProject workspaceProject){
        if(workspaceProject!=null){
            String projectName=workspaceProject.getConfiguration().getName();
            return(locked(projectName));
        }
        return false;
    }
    
    private File projectConfigFile(String projectName) throws WorkspaceException{
        File projectDir=new File(workspaceDir,projectName);
        File projectConfigFile=null;
        File[] projectFiles = projectDir.listFiles();
        if(projectFiles!=null){

            for (int j = 0; j < projectFiles.length; j++) {

                File projectFile = projectFiles[j];
                if (projectFile.isFile() && ! projectFile.isHidden()) {
                    if (projectFile.getName().endsWith(
                            SpeechRecorder.PROJECT_FILE_EXTENSION)) {
                        if(projectConfigFile!=null){
                            throw new WorkspaceException("Multiple project file candidates in "+projectDir+": "+projectConfigFile.getName()+" ,"+projectFile.getName());
                        }
                        projectConfigFile=projectFile;
                        // do not break here to continue search for multiple (ambigious config) files
                    }
                }
            }
        }
        return projectConfigFile;
    }
    
    public WorkspaceProject[] scanWorkspace() throws WorkspaceException {
    	fireIntervalRemoved(this, 0, workspaceProjects.size());
    	DOMConverter domConverter;
    	
    	domConverter = new DOMConverter();
    	Package configBasePack;
    	try {
    		configBasePack = Class.forName(PROJECT_CONFIG_PACKAGE).getPackage();
    	} catch (ClassNotFoundException e1) {
    		e1.printStackTrace();
    		throw new WorkspaceException("Package " + PROJECT_CONFIG_PACKAGE,
    				e1);
    	}

    	DOMCodec domCodec;
    	try {
    		domCodec = new DOMCodec(configBasePack);
    	} catch (DOMCodecException e1) {
    		e1.printStackTrace();
    		throw new WorkspaceException("Could not create DOM codec", e1);
    	}
    	List<WorkspaceProject> newWorkspaceProjectsList = new ArrayList<WorkspaceProject>();
    	if (workspaceDir.exists()) {
    		File[] projectDirs = workspaceDir.listFiles();
    		if(projectDirs!=null){
    			for (int i = 0; i < projectDirs.length; i++) {
    				File projectDir=projectDirs[i];
    				if (projectDir!=null && projectDir.isDirectory() && ! projectDir.isHidden()) {
    					File projectConfigFile=null;
    					String projNameByDirName=projectDir.getName();
    					String standardProjectFilename=projNameByDirName+SpeechRecorder.PROJECT_FILE_EXTENSION;
    					File standardProjectFile=new File(projectDir,standardProjectFilename);
    					if(standardProjectFile.exists()){
    						projectConfigFile=standardProjectFile;
    					}
    					if(projectConfigFile==null){
    						File[] projectFiles = projectDir.listFiles();
    						if(projectFiles!=null){

    							for (int j = 0; j < projectFiles.length; j++) {

    								File projectFile = projectFiles[j];
    								if (projectFile.isFile() && ! projectFile.isHidden()) {
    									if (projectFile.getName().endsWith(
    											SpeechRecorder.PROJECT_FILE_EXTENSION)) {
    										if(projectConfigFile!=null){
    											throw new WorkspaceException("Multiple project file candidates in "+projNameByDirName+": "+projectConfigFile.getName()+" ,"+projectFile.getName());
    										}
    										projectConfigFile=projectFile;
    										// do not break here to continue search for multiple (ambigious config) files
    									}
    								}
    							}
    						}
    					}
    					if(projectConfigFile!=null){
    						ProjectConfiguration p = null;
    						try {
    							Document d = domConverter
    									.readXML(new FileInputStream(
    											projectConfigFile));
    							p = (ProjectConfiguration) domCodec
    									.readDocument(d);
    						} catch (Exception e) {
    							e.printStackTrace();
    							JOptionPane.showMessageDialog(null,
    									"Cannot create project from "
    											+ projectConfigFile.getPath()
    											+ " .\n"
    											+ e.getLocalizedMessage(),
    											"Warning !",
    											JOptionPane.WARNING_MESSAGE);
    							continue;
    						}
    						WorkspaceProject wp = new WorkspaceProject(p,
    								projectConfigFile);
    						newWorkspaceProjectsList.add(wp);
    					}
    				}
    			}
    		}
    	}
    	List<WorkspaceProject> oldWorkspaceProjects=workspaceProjects;
    	workspaceProjects=newWorkspaceProjectsList;
    	propertyChangeSupport.firePropertyChange("workspaceProjects",oldWorkspaceProjects, workspaceProjects);
//    	fireTableDataChanged();
    	fireIntervalAdded(this, 0, workspaceProjects.size());
    	return newWorkspaceProjectsList.toArray(new WorkspaceProject[0]);
    }
    
    
    
    public String getColumnName(int colIndex){
        if(colIndex==COL_NAME){
            return "Name";
        }else return null;
    }
    
    public Class<?> getColumnClass(int colIndex){
        if(colIndex==COL_NAME){
            return String.class;
        }else return null;
    }

//    /* (non-Javadoc)
//     * @see javax.swing.table.TableModel#getColumnCount()
//     */
//    @Override
//    public int getColumnCount() {
//       
//        return 1;
//    }
//
//    /* (non-Javadoc)
//     * @see javax.swing.table.TableModel#getRowCount()
//     */
//    @Override
//    public int getRowCount() {
//        
//        return workspaceProjects.size();
//    }

//    /* (non-Javadoc)
//     * @see javax.swing.table.TableModel#getValueAt(int, int)
//     */
//    @Override
//    public Object getValueAt(int rowIndex, int colIndex) {
//        if(colIndex==0){
//            WorkspaceProject proj=workspaceProjects.get(rowIndex);
//            ProjectConfiguration pCfg=proj.getConfiguration();
//            return pCfg.getName();
//        }else{
//            return null;
//        }
//    }

    public File getWorkspaceDir() {
        return workspaceDir;
    }

    public List<WorkspaceProject> getWorkspaceProjects() {
        return workspaceProjects;
    }
    
    public WorkspaceProject projectByName(String name){
        for(WorkspaceProject wp:workspaceProjects){
            ProjectConfiguration pc=wp.getConfiguration();
            if(pc!=null){
                String wpName=pc.getName();
                if(wpName.equals(name)){
                    return wp;
                }
            }
        }
        return null;
    }
    
    public void renameProject(String oldName,String newName) throws WorkspaceException{
        WorkspaceProject wp=projectByName(oldName);
        // TODO check if project is closed
        ProjectConfiguration wpPc=wp.getConfiguration();
        String cfPDirNm=wpPc.getDirectory();
//         check if project config directory and workspace project dir are te same 
//        File cfPDir=new File(cfPDirNm);
        File wsPDir=new File(workspaceDir,oldName);
//        if(!wsPDir.equals(cfPDir)){
//            throw new WorkspaceException("Configuration ")
//        }
        File newWsPDir=new File(workspaceDir,newName);
        if(newWsPDir.exists()){
            throw new WorkspaceException("Could not rename project: Path "+newWsPDir+" already exists!");
        }
        boolean renamed=wsPDir.renameTo(newWsPDir);
        if(!renamed){
            throw new WorkspaceException("Could not rename folder "+wsPDir+" to "+newWsPDir);
        }
        
       
        URL dirURL=null;
        try {
            dirURL = newWsPDir.toURI().toURL();
            
//            wpPc.setDirectory(dirURL.toExternalForm());
           
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        wpPc.setName(newName);
        
        //TODO
            
          //  write config to  newProjectFile
            
        File oldProjectFile=new File(newWsPDir,oldName+SpeechRecorder.PROJECT_FILE_EXTENSION);
        File newProjectFile=new File(newWsPDir,newName+SpeechRecorder.PROJECT_FILE_EXTENSION);
       
        ConfigHelper ch=new ConfigHelper();
        
        try {
            ch.writeConfig(wpPc, newProjectFile);
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new WorkspaceException(e1);
            
        }
        
       
        // rename old file as backup file
        File projectOldBkpFile=FileUtils.moveToBackup(oldProjectFile, ".bak");
        

        // should already work ...
        scanWorkspace();
        
        // but we should rename speaker db and script file as well
        
        WorkspaceProject newWsp=projectByName(newName);
        ProjectConfiguration pc=newWsp.getConfiguration();
        try {

            SpeakersConfiguration spksCfg=pc.getSpeakers();
            String spksUrl=spksCfg.getSpeakersUrl();
            if (spksUrl != null && !spksUrl.equals("")) {
                URL speakerURL = URLContext.getContextURL(dirURL,spksUrl);
                File spksFile=Utils.fileFromDecodedURL(speakerURL);
                if(spksFile!=null){
                    // check default location in top level
                    File spksParent=spksFile.getParentFile();
                   
                    if(newWsPDir.equals(spksParent)){
                        String oldSpksFileName=spksFile.getName();
                        if(oldSpksFileName.startsWith(oldName)){
                            // OK this is default speakers db file
                            // try rename
                            String newSpksFn=oldSpksFileName.replaceFirst("^"+oldName, newName);
                            File newSpksFile=new File(spksParent,newSpksFn);
                            if(spksFile.renameTo(newSpksFile)){
                                spksCfg.setSpeakersUrl(newSpksFn);
                                ch.writeConfig(pc, newProjectFile);
                            }
                        }
                    }
                }
            }
            
            PromptConfiguration prCfg=pc.getPromptConfiguration();
            String prUrl=prCfg.getPromptsUrl();
            if (prUrl != null && !prUrl.equals("")) {
                URL promptsURL = URLContext.getContextURL(dirURL,prUrl);
                File promptsFile=Utils.fileFromDecodedURL(promptsURL);
                if(promptsFile!=null){
                    // check default location in top level
                    File promptsParent=promptsFile.getParentFile();
                    if(newWsPDir.equals(promptsParent)){
                        String oldPromptsFileName=promptsFile.getName();
                        if(oldPromptsFileName.startsWith(oldName)){
                            // OK this is default script file
                            // try rename
                            String newPromptsFn=oldPromptsFileName.replaceFirst("^"+oldName, newName);
                            File newPromptsFile=new File(promptsParent,newPromptsFn);
                            if(promptsFile.renameTo(newPromptsFile)){
                                prCfg.setPromptsUrl(newPromptsFn);
                                ch.writeConfig(pc, newProjectFile);
                            }
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            throw new WorkspaceException(e);
        } catch (DOMCodecException e) {
        	throw new WorkspaceException(e);
        } catch (DOMConverterException e) {
        	throw new WorkspaceException(e);
        } catch (IOException e) {
        	throw new WorkspaceException(e);
        }
        if(projectOldBkpFile!=null){
            projectOldBkpFile.delete();
        }
        
        
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName,
                listener);
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    @Override
    public Object getElementAt(int rowIndex) {
    	WorkspaceProject proj=workspaceProjects.get(rowIndex);
    	ProjectConfiguration pCfg=proj.getConfiguration();
    	return pCfg.getName();
    }

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return workspaceProjects.size();
	}

//	/**
//	 * @param selProj
//	 * @throws WorkspaceException 
//	 */
//	public void openProject(WorkspaceProject selProj) throws WorkspaceException {
//		try {
//			speechRecorder.openProject(selProj.getConfiguration().getName());
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//			throw new WorkspaceException(e);
//		} 
//	}



}
