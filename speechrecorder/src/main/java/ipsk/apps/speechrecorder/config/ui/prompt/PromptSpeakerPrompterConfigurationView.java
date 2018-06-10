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

import ipsk.apps.speechrecorder.config.PromptConfiguration;
import ipsk.apps.speechrecorder.config.Prompter;
import ipsk.apps.speechrecorder.config.Prompter.SpeakerWindowType;
import ipsk.apps.speechrecorder.config.ui.KeyInputMapView;
import ipsk.apps.speechrecorder.config.TransportPanel;
import ipsk.swing.EnumSelectionItem;
import ipsk.swing.EnumVector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;


/**
 * UI panel for speaker window configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class PromptSpeakerPrompterConfigurationView extends JPanel implements ActionListener, DocumentListener {

	private static final long serialVersionUID = 1L;
	private JCheckBox promptWindowCheckBox;
    private JComboBox speakerDisplayTypeBox;
    private JCheckBox speakerDisplayFullScreenModeBox;
	private JCheckBox buttonsInPromptWindowCheckBox;
	private JLabel buttonsInPromptWindowLabel;
	
//	private PromptConfiguration p;
//    private PromptViewerConfig spkDisplayPrompter=null;
    
    private JLabel showStartRecordActionLabel;
    private JCheckBox showStartRecordActionChkBox;
    private JLabel showStopRecordActionLabel;
    private JCheckBox showStopRecordActionChkBox;
    
    private KeyInputMapView keyInputMapView;

	public PromptSpeakerPrompterConfigurationView( KeyInputMapView keyInputMapView) {
		super(new GridBagLayout());
		this.keyInputMapView=keyInputMapView;
		
        
		//uiString = UIResources.getInstance();
		Border loweredbevel = BorderFactory.createLoweredBevelBorder();
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 5, 2, 5);
		c.gridx = 0;
		c.gridy = 0;
		
		add(new JLabel("Show separate prompt window:"), c);
		promptWindowCheckBox = new JCheckBox();
		
		promptWindowCheckBox.addActionListener(this);
		c.gridx++;
		add(promptWindowCheckBox, c);
		
		c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        JLabel speakerDisplayFullScreenModeLabel= new JLabel("Speaker display fullscreen mode:");
        add(speakerDisplayFullScreenModeLabel, c);
        speakerDisplayFullScreenModeBox=new JCheckBox();
        speakerDisplayFullScreenModeBox.addActionListener(this);
        c.gridx++;
        add(speakerDisplayFullScreenModeBox,c);
        
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        JLabel speakerDisplayTypeLabel= new JLabel("Choose speaker display type:");
        add(speakerDisplayTypeLabel, c);
        EnumVector<Prompter.SpeakerWindowType> wTypeVector=new EnumVector<SpeakerWindowType>(SpeakerWindowType.class);
        speakerDisplayTypeBox = new JComboBox(wTypeVector);
//        speakerDisplayTypeBox.setSelectedItem(spkDisplayPrompter.getSpeakerWindowType());
        speakerDisplayTypeBox.addActionListener(this);
        c.gridx++;
        add(speakerDisplayTypeBox,c);
                
		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		buttonsInPromptWindowLabel = new JLabel("Show transport buttons in prompt window:");
		add(buttonsInPromptWindowLabel, c);
		buttonsInPromptWindowCheckBox = new JCheckBox();
		
		buttonsInPromptWindowCheckBox.addActionListener(this);
		c.gridx++;
		add(buttonsInPromptWindowCheckBox, c);
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        showStartRecordActionLabel=new JLabel("Show start record button");
        showStartRecordActionChkBox=new JCheckBox();
        showStartRecordActionChkBox.addActionListener(this);
        add(showStartRecordActionLabel,c);
        c.gridx++;
        add(showStartRecordActionChkBox,c);
        c.gridx = 0;
        c.gridy++;
        c.weightx = 0;
        showStopRecordActionLabel=new JLabel("Show stop record button");
        showStopRecordActionChkBox=new JCheckBox();
        showStopRecordActionChkBox.addActionListener(this);
        add(showStopRecordActionLabel,c);
        c.gridx++;
        add(showStopRecordActionChkBox,c);
        
       
//        if(spkDisplayPrompter!=null){
//            TransportPanel tr=spkDisplayPrompter.getTransportPanel();
//            if(tr!=null){
//            showStartRecordActionChkBox.setSelected(tr.isShowStartRecordAction());
//            showStopRecordActionChkBox.setSelected(tr.isShowStopRecordAction());
//            }
//           SpeakerWindowType speakerWindowType=spkDisplayPrompter.getSpeakerWindowType();
//           EnumSelectionItem<SpeakerWindowType> spkWTEsi=new EnumSelectionItem<SpeakerWindowType>(speakerWindowType);
//           speakerDisplayTypeBox.setSelectedItem(spkWTEsi);
//           Boolean fsm=spkDisplayPrompter.getFullScreenMode();
//           boolean fsmb=false;
//           if(fsm!=null){
//               fsmb=fsm;
//           }
//           speakerDisplayFullScreenModeBox.setSelected(fsmb);
//        }else{
//            showStartRecordActionChkBox.setSelected(p.getShowButtonsInPromptWindow());
//            showStopRecordActionChkBox.setSelected(p.getShowButtonsInPromptWindow());
//        }
        
		setDependencies();

	}
	
	/**
     * @param promptConfiguration
     */
    public void setPromptConfiguration(PromptConfiguration promptConfiguration) {
        Prompter spkDisplayPrompter=null;
        Prompter[] prompters=promptConfiguration.getPrompter();
       
        if (prompters!=null && prompters.length>0){
            spkDisplayPrompter=prompters[0];
        }
        promptWindowCheckBox.setSelected(promptConfiguration.getShowPromptWindow());
        buttonsInPromptWindowCheckBox.setSelected(promptConfiguration.getShowButtonsInPromptWindow());
        if(spkDisplayPrompter!=null){
            TransportPanel tr=spkDisplayPrompter.getTransportPanel();
            if(tr!=null){
            showStartRecordActionChkBox.setSelected(tr.isShowStartRecordAction());
            showStopRecordActionChkBox.setSelected(tr.isShowStopRecordAction());
            }
           SpeakerWindowType speakerWindowType=spkDisplayPrompter.getSpeakerWindowType();
           EnumSelectionItem<SpeakerWindowType> spkWTEsi=new EnumSelectionItem<SpeakerWindowType>(speakerWindowType);
           speakerDisplayTypeBox.setSelectedItem(spkWTEsi);
           Boolean fsm=spkDisplayPrompter.getFullScreenMode();
           boolean fsmb=false;
           if(fsm!=null){
               fsmb=fsm;
           }
           speakerDisplayFullScreenModeBox.setSelected(fsmb);
        }else{
            showStartRecordActionChkBox.setSelected(promptConfiguration.getShowButtonsInPromptWindow());
            showStopRecordActionChkBox.setSelected(promptConfiguration.getShowButtonsInPromptWindow());
        }
        setDependencies();
    }
	
	public void applyValues(PromptConfiguration p){
	    p.setShowPromptWindow(promptWindowCheckBox.isSelected());
    
        boolean showTransport=buttonsInPromptWindowCheckBox.isSelected();
        p.setShowButtonsInPromptWindow(showTransport);
        
        boolean showStartButt=showStartRecordActionChkBox.isSelected();
        boolean showStopButt=showStopRecordActionChkBox.isSelected();
        
//        if(showTransport && (!showStartButt || !showStopButt)){
            // if we have a detailed configuration (one of the button types should not show up)
            // then we create a Prompter  element with speaker viewer config and transport panel configuration 
            // the prompter structure is mainly intended for more complex features in the future 
            Prompter[] prompters=p.getPrompter();
            Prompter prompter=null;
            if (prompters==null || prompters.length==0){
                prompter=new Prompter();
                prompters=new Prompter[]{prompter};
                p.setPrompter(prompters);
            }else{
                // only one prompter for Speechrecorder version 2.4.0.
                prompter=prompters[0];
            }
            EnumSelectionItem<SpeakerWindowType> swtsi=(EnumSelectionItem<SpeakerWindowType>) speakerDisplayTypeBox.getSelectedItem();
            prompter.setSpeakerWindowType(swtsi.getEnumVal());
            Boolean fullscreenMode=null;
            if(speakerDisplayFullScreenModeBox.isSelected()){
                fullscreenMode=new Boolean(true);
            }
            prompter.setFullScreenMode(fullscreenMode);
            TransportPanel tr=prompter.getTransportPanel();
            if(tr==null){
                tr=new TransportPanel();
                prompter.setTransportPanel(tr);
            }
            tr.setShowStartRecordAction(showStartButt);
            tr.setShowStopRecordAction(showStopButt);
            
           
//        }
       
       
	}
	

	private void setDependencies() {
		//buttonsInPromptWindowCheckBox.setEnabled(p.getShowPromptWindow());
		//buttonsInPromptWindowLabel.setEnabled(p.getShowPromptWindow());
	}

//    private PromptViewerConfig getspeakerDisplayPrompterConfig(boolean createAndAdd){
//     Prompter[] prompter=p.getPrompter();
//     if (prompter!=null){
////         for(Prompter pr:prompter){
////             if (pr.getName().equals(Prompter.SPEAKER_DISPLAY_NAME)){
////                 return pr;
////             }
////         }
//         // for Speechrecorder 2.4 we have only one prompter
//         Prompter p=prompter[0];
//       
//         return p.getSpeakerPromptViewersConfig();
////         if(!createAndAdd)return null;
////         Prompter spkPrompter=new Prompter();
////         spkPrompter.setName(Prompter.SPEAKER_DISPLAY_NAME);
////         ArrayList<Prompter> prList=new ArrayList<Prompter>();
////         Prompter[] prms=p.getPrompter();
////         if(prms ==null){
////             prms=new Prompter[0];
////         }
////         prList.addAll(Arrays.asList(prms));
////         prList.add(spkPrompter);
////         p.setPrompter(prList.toArray(new Prompter[0]));
////         return spkPrompter;
//     }else{
//         if(!createAndAdd)return null;
//         Prompter spkPrompter=new Prompter();
////         spkPrompter.setName(Prompter.SPEAKER_DISPLAY_NAME);
//         p.setPrompter(new Prompter[]{spkPrompter});
//         return spkPrompter.getSpeakerPromptViewersConfig();
//     }
//
//    }
    
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
	    Object src=ae.getSource();
	    if(src==speakerDisplayTypeBox){
	        EnumSelectionItem<SpeakerWindowType> swtsi=(EnumSelectionItem<SpeakerWindowType>) speakerDisplayTypeBox.getSelectedItem();
            SpeakerWindowType spkwType=swtsi.getEnumVal();
            boolean closeWindowActionBound=keyInputMapView.isCloseSpeakerDisplayActionBound();
            if(spkwType!=null && spkwType.equals(SpeakerWindowType.WINDOW) && !closeWindowActionBound){
                int res=JOptionPane.showOptionDialog(this, "You selected a plain window as display type for the speaker window.\nYou may not be able to use the mouse to get back to the operating system view.\nIt is recommended to associate a key to close the window.\nAssociate the Escape key with speaker window close action?",
                        "Speaker window configuration warning", JOptionPane.YES_NO_OPTION,0, null,null,JOptionPane.YES_OPTION );
                if(res==JOptionPane.YES_OPTION){
                    keyInputMapView.bindCloseSpeakerDisplayActiontoEscape();
                }
            }
	    }else if(src==speakerDisplayFullScreenModeBox){
	    	boolean closeWindowActionBound=keyInputMapView.isCloseSpeakerDisplayActionBound();
            if(speakerDisplayFullScreenModeBox.isSelected() && !closeWindowActionBound){
                int res=JOptionPane.showOptionDialog(this, "You selected fullscreen display for the speaker window.\nYou may not be able to use the mouse to get back to the operating system view.\nIt is recommended to associate a key to close the window.\nAssociate the Escape key with speaker window close action?",
                        "Speaker window configuration warning", JOptionPane.YES_NO_OPTION,0, null,null,JOptionPane.YES_OPTION );
                if(res==JOptionPane.YES_OPTION){
                    keyInputMapView.bindCloseSpeakerDisplayActiontoEscape();
                }
            }
	    }
		setDependencies();
	}

	
	public void documentChanged(DocumentEvent de) {
		Document src = de.getDocument();
		
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
