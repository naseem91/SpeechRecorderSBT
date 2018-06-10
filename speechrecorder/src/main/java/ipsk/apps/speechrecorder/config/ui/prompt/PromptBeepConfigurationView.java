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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import ipsk.apps.speechrecorder.config.PromptBeep;
import ipsk.apps.speechrecorder.config.PromptConfiguration;
import ipsk.apps.speechrecorder.config.RecordingConfiguration;
import ipsk.apps.speechrecorder.config.RecordingConfiguration.CaptureScope;
import ipsk.audio.dsp.DSPUtils;
import ipsk.io.StreamCopy;
import ipsk.net.URLContext;
import ipsk.swing.action.EditActionsListener;
import ipsk.swing.panel.JConfigPanel;
import ipsk.swing.text.EditorKitMenu;

/**
 * UI panel for prompts (stimuli) configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class PromptBeepConfigurationView extends JConfigPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	// TODO duplicate entry in BasemediaItemUI for script resources 
	public final static String RESOURCE_PATH="resources";
	private URL projectContext;
	private JCheckBox defaultBeepCb;
	private JTextField promptBeepUrlWidget;
	private JButton importButton;
	private JSpinner volumeSpinner;
	
	private PromptBeep currentConfig;

	private JLabel beepUrlLabel;
	
	public PromptBeepConfigurationView() {
		super();
//		this.projectContext=projectContext;
		JPanel cp=getContentPane();
		GridBagLayout layout=new GridBagLayout();
		cp.setLayout(layout);
		GridBagConstraints gc=new GridBagConstraints();
		gc.weightx = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(2, 5, 2, 5);
		 gc.gridx=0;
		    gc.gridy=0;
		JLabel defaultBeepLbl=new JLabel("Default beep");
		defaultBeepCb=new JCheckBox();
		defaultBeepCb.addActionListener(this);
		cp.add(defaultBeepLbl, gc);
		gc.gridx++;
		cp.add(defaultBeepCb, gc);
		beepUrlLabel = new JLabel("Beep file URL");
	    promptBeepUrlWidget = new JTextField(20);
	    new EditorKitMenu(promptBeepUrlWidget);
	    promptBeepUrlWidget.addActionListener(this);
	    promptBeepUrlWidget.setToolTipText("Enter a URL from which to retrieve the prompt beep audio file.");
	    importButton=new JButton("Import");
	    importButton.addActionListener(this);
	    gc.gridx=0;
	    gc.gridy++;
	    cp.add(beepUrlLabel,gc);
	    gc.gridx++;
	    cp.add(promptBeepUrlWidget,gc);
	    gc.gridx++;
	    cp.add(importButton,gc);
	    
	    JLabel volumeLabel = new JLabel("Volume dB");
	    volumeSpinner = new JSpinner(new SpinnerNumberModel(0,-90,6,1));
	    volumeSpinner.setToolTipText("Enter a value between -90 (nearly silent) and 0 (full volume) for the output volume of the prompt beep.");
	    gc.gridx=0;
	    gc.gridy++;
	    cp.add(volumeLabel,gc);
	    gc.gridx++;
	    cp.add(volumeSpinner,gc);
	}
	
	/**
     * @param promptConfiguration
     */
    public void setPromptBeep(PromptBeep promptBeep) {
    	currentConfig=promptBeep;
    	String beepFileUrl=promptBeep.getBeepFileURL();
    	boolean defBeep=(beepFileUrl==null);
    	String beepFileUrlStr=defBeep?"":beepFileUrl;
    	defaultBeepCb.setSelected(defBeep);
    	promptBeepUrlWidget.setText(beepFileUrlStr);
    	Double beepvol=promptBeep.getBeepGainRatio();
    	
    	if(beepvol==null){
    		volumeSpinner.setValue(new Integer(0));
    	}else{
    		int beepLvlInt=(int)DSPUtils.getLevelInDB(beepvol);
    		volumeSpinner.setValue(beepLvlInt);
    	}
       setDependencies();
    }

	public void applyValues(PromptBeep p){
		if(defaultBeepCb.isSelected()){
			p.setBeepFileURL(null);
		}else{
			p.setBeepFileURL(promptBeepUrlWidget.getText());
		}
		Integer volDbInt=(Integer)volumeSpinner.getValue();
		double volDb=volDbInt.doubleValue();
        double volRatio=DSPUtils.toLinearLevel(volDb);
        p.setBeepGainRatio(volRatio);
        
	}
	
	private void setDependencies() {
		boolean defaultBeep=defaultBeepCb.isSelected();
		beepUrlLabel.setEnabled(!defaultBeep);
		promptBeepUrlWidget.setEnabled(!defaultBeep);
		importButton.setEnabled(!defaultBeep);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		Object as=ae.getSource();
		if(as==importButton){
			  JFileChooser chooser = new JFileChooser();
	            
	            chooser.setDialogTitle("Import prompt beep audio file");
	            chooser.setApproveButtonText("Import");
	            chooser.setApproveButtonToolTipText("Copies beep prompt audio file into the project workspace");
	            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	            // try to preselect file chooser
	            try {
	                URL currentSrcUrl=new URL(promptBeepUrlWidget.getText());
	                if(currentSrcUrl!= null && "file".equalsIgnoreCase(currentSrcUrl.getProtocol())){
	                    File preselectedFile=null;

	                    try {
	                        URI preselectedURI=currentSrcUrl.toURI();
	                        String preselectedFilePath=preselectedURI.getPath();
	                        if(preselectedFilePath!=null){
	                            preselectedFile = new File(preselectedFilePath);
	                        }
	                    } catch (URISyntaxException e1) {
	                        // OK no preselection
	                    }
	                    if(preselectedFile != null && preselectedFile.exists()){
	                        chooser.setSelectedFile(preselectedFile);
	                    }
	                }
	            } catch (MalformedURLException e3) {
	                // OK no preselection
	            }
//	            if(chooser.getSelectedFile()==null){
//	                chooser.setCurrentDirectory(lastImportDir);
//	            }
	            File resProjFile=null;
	            int returnVal = chooser.showOpenDialog(null);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File selFile=chooser.getSelectedFile();
	                // save last import dir
//	                if(selFile!=null){
//	                    lastImportDir=selFile.getParentFile();
//	                }
	                String relProjPath=JOptionPane.showInputDialog(this,"Please input relative path to store the resource", RESOURCE_PATH+"/audio");


	                String relprojResFilePath=relProjPath+"/"+selFile.getName();
	                try {
	                    URL projResUrl=URLContext.getContextURL(projectContext,relprojResFilePath);
	                    URI projResUri=projResUrl.toURI();
	                    String projResFilePath= projResUri.getPath();
	                    resProjFile=new File(projResFilePath);
	                    
	                    if(resProjFile.exists()){
	                        if(selFile.equals(resProjFile)){
	                            // Fixes Bug ID 0021 
	                            int selectSameFile = JOptionPane.showConfirmDialog(this, resProjFile+" is same file.\nDo you want to select this file?");
	                            if(selectSameFile!=JOptionPane.YES_OPTION){
	                                return;
	                            }
	                        }else{
	                            int overwriteRes = JOptionPane.showConfirmDialog(this, resProjFile+" already exists.\nDo you want to overwrite?");
	                            if(overwriteRes!=JOptionPane.YES_OPTION){
	                                return;
	                            }
	                            StreamCopy.copy(selFile, resProjFile,true);
	                        }
	                    }else{
	                        StreamCopy.copy(selFile, resProjFile,true);
	                    }
	                    
	                    promptBeepUrlWidget.setText(relprojResFilePath);
	                } catch (MalformedURLException e1) {
	                    e1.printStackTrace();
	                    JOptionPane.showMessageDialog(this, "Could not convert to valid URL:\n"+e1.getMessage(), "Malformed URL error", JOptionPane.ERROR_MESSAGE);
	                } catch (IOException e2) {
	                    e2.printStackTrace();
	                    // cleanup
	                    if(resProjFile!=null){
	                        resProjFile.delete();
	                    }
	                    JOptionPane.showMessageDialog(this, "Input/Output error:\n"+e2.getMessage(), "I/O error", JOptionPane.ERROR_MESSAGE);
	                } catch (URISyntaxException e3) {
	                    e3.printStackTrace();
	                    JOptionPane.showMessageDialog(this, "Could not convert to valid URI:\n"+e3.getMessage(), "URI syntax error", JOptionPane.ERROR_MESSAGE);
	                }
	           
	            }
		}else{
			super.actionPerformed(ae);
		}
		setDependencies();
	}
	
	 /* (non-Javadoc)
     * @see ipsk.swing.panel.JConfigPanel#resetToDefaults()
     */
    @Override
    public void resetToDefaults() {
    	PromptBeep defaultConfig=new PromptBeep();
 	    PromptBeep oldCurrentConfig=currentConfig;
 	  
 	    setPromptBeep(defaultConfig);
 	    
 	    // do not override current config with defaults
 	    currentConfig=oldCurrentConfig;
    }

    /* (non-Javadoc)
     * @see ipsk.swing.panel.JConfigPanel#resetToInitial()
     */
    @Override
    public void resetToInitial() {
       setPromptBeep(currentConfig);
    }

	/**
	 * @param projectContext
	 */
	public void setProjectContext(URL projectContext) {
		this.projectContext=projectContext;
		
	}


    
}
