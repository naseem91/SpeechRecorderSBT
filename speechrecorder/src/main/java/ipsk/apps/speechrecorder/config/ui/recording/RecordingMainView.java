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
 * Date  : Jun 2, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config.ui.recording;

import ipsk.apps.speechrecorder.config.Format;
import ipsk.apps.speechrecorder.config.RecordingConfiguration;
import ipsk.apps.speechrecorder.config.RecordingConfiguration.CaptureScope;
import ipsk.audio.Profile;
import ipsk.audio.capture.PrimaryRecordTarget;
import ipsk.audio.ui.AudioFormatChooser;
import ipsk.swing.EnumSelectionItem;
import ipsk.swing.EnumVector;
import ipsk.swing.panel.JConfigPanel;
import ipsk.swing.text.EditorKitMenu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.sound.sampled.AudioFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class RecordingMainView extends JConfigPanel implements DocumentListener, ChangeListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JLabel progressToNextUnrecordedLabel;
	private JCheckBox resetPeakOnRecordingCheckBox;
	private Document urlDoc;
	private JComboBox numLineBox;
//	private Integer[] numLines;
//	private int MAX_NUMLINES = 16;
	//private JLabel autoProgressLabel;
	//private JCheckBox autoProgressCheckBox;
	private JCheckBox progressToNextUnrecordedCheckbox;
	//private JCheckBox autoRecCheckBox;
	private JComboBox modeBox;
	private JTextField urlField;
//	private JLabel cacheInFilesLabel;
	//private JCheckBox cacheInFilesCheckBox;
	private JSpinner preRecDelSpinner;
	private JLabel preRecLabel;
	private JSpinner postRecDelSpinner;
	private JLabel postRecLabel;
	private JCheckBox forcePostRecPhaseCheckBox;
	private JCheckBox seamlessCheckBox;
//	private AudioFormatChooser afc;
//	private AudioFormat af;
	private JPanel recCfgPanel;
//	private Format f;
//	private RecordingConfiguration r;
	private EnumVector<CaptureScope> captureScopes=new EnumVector<RecordingConfiguration.CaptureScope>(CaptureScope.class);
	private JComboBox captureScopeBox;
	private EnumVector<PrimaryRecordTarget> primaryRecordTargets = new EnumVector<PrimaryRecordTarget>(PrimaryRecordTarget.class);
	private JComboBox primaryRecordTargetBox;
//	private JCheckBox useTempFileBox;
	private JCheckBox overwriteCheckBox;
	private JCheckBox overwriteWarningCheckBox;
	private String[] modes =
		{ RecordingConfiguration.MANUAL, RecordingConfiguration.AUTOPROGRESS, RecordingConfiguration.AUTORECORDING };

	private JTextField recShortKey;
	private JButton recsUrlBrowseButton;
	
	
	private RecordingConfiguration currentConfig;
	
	public RecordingMainView() {
		super();
		JPanel contentPane=getContentPane();
		
//		this.r = r;
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		contentPane.setLayout(new BorderLayout());

		recCfgPanel = new JPanel();
		recCfgPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.weightx = 0;
		c.gridwidth=1;
		c.insets = new Insets(2, 5, 2, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx=1;
		c2.fill = GridBagConstraints.HORIZONTAL;
		c2.weightx=1;
		c2.gridwidth=2;
		c2.insets=new Insets(2, 5, 2, 5);
		c2.anchor= GridBagConstraints.PAGE_START;
		
		GridBagConstraints c3 = new GridBagConstraints();
		c3.gridx=2;
		c3.gridwidth=1;
		c3.insets=new Insets(2, 5, 2, 5);
		c3.anchor= GridBagConstraints.PAGE_START;

		c.gridy = 0;
        c2.gridy=c.gridy;
        captureScopeBox = new JComboBox(captureScopes);
        captureScopeBox.addActionListener(this);
     
        JLabel captureScopeLabel = new JLabel("Audio capture scope:");
        recCfgPanel.add(captureScopeLabel, c); 
        recCfgPanel.add(captureScopeBox, c2);
		
		c.gridy++;
        c2.gridy++;
        primaryRecordTargetBox = new JComboBox(primaryRecordTargets);
        primaryRecordTargetBox.addActionListener(this);
     
        JLabel useTempFileLabel = new JLabel("Recording target:");
        recCfgPanel.add(useTempFileLabel, c); 
        recCfgPanel.add(primaryRecordTargetBox, c2);
        
        c.gridy++;
		c2.gridy++;
		overwriteCheckBox = new JCheckBox();
		overwriteCheckBox.addActionListener(this);
		JLabel overwriteLabel = new JLabel("Overwrite:");
		recCfgPanel.add(overwriteLabel, c);	
		recCfgPanel.add(overwriteCheckBox, c2);
		
		c.gridy++;
        c2.gridy++;
        overwriteWarningCheckBox = new JCheckBox();
        overwriteWarningCheckBox.addActionListener(this);
        JLabel overwriteWarningLabel = new JLabel("Overwrite warning:");
        recCfgPanel.add(overwriteWarningLabel, c); 
        recCfgPanel.add(overwriteWarningCheckBox, c2);
        
		c.gridy++;
		c2.gridy++;
		modeBox = new JComboBox(modes);
		modeBox.addActionListener(this);
		recCfgPanel.add(new JLabel("Recording mode:"), c);
		recCfgPanel.add(modeBox, c2);
		
		c.gridy++;
        c2.gridy++;
        seamlessCheckBox=new JCheckBox();
        seamlessCheckBox.addActionListener(this);
        recCfgPanel.add(new JLabel("Seamless (auto) recording:"), c);
        recCfgPanel.add(seamlessCheckBox, c2);
        
		c.gridy++;
		c2.gridy++;
		progressToNextUnrecordedCheckbox = new JCheckBox();
		progressToNextUnrecordedCheckbox.addActionListener(this);
		progressToNextUnrecordedLabel = new JLabel("Autoprogress to next unrecorded item:");
		recCfgPanel.add(progressToNextUnrecordedLabel, c);
		recCfgPanel.add(progressToNextUnrecordedCheckbox, c2);
		
		c.gridy++;
		c2.gridy++;
		resetPeakOnRecordingCheckBox = new JCheckBox();
		resetPeakOnRecordingCheckBox.addActionListener(this);
		recCfgPanel.add(new JLabel("Reset peak at start of recording:"), c);
		recCfgPanel.add(resetPeakOnRecordingCheckBox, c2);
//		numLines = new Integer[MAX_NUMLINES];
//		for (int i = 0; i < MAX_NUMLINES; i++) {
//			numLines[i] = new Integer(i + 1);
//		}
//		numLineBox = new JComboBox(numLines);
//		numLineBox.addActionListener(this);
//		c.gridx = 0;
//		c.gridy++;
//		recCfgPanel.add(new JLabel("Number of audio lines:"), c);
//		c.gridx++;
//		recCfgPanel.add(numLineBox, c);

		Integer min = new Integer(0);
		Integer max = new Integer(1000000);
		Integer step = new Integer(100);
		SpinnerNumberModel model = new SpinnerNumberModel(min, min, max, step);
		
		c2.gridwidth=1;
		preRecDelSpinner = new JSpinner(model);
		preRecLabel = new JLabel("Default prerecording delay:");
		c.gridy++;
		c2.gridy++;
		recCfgPanel.add(preRecLabel, c);
		recCfgPanel.add(preRecDelSpinner, c2);
		preRecDelSpinner.addChangeListener(this);
		c3.gridy=c.gridy;
		c3.anchor=GridBagConstraints.WEST;
        recCfgPanel.add(new JLabel("ms"), c3);
		
		SpinnerNumberModel postmodel = new SpinnerNumberModel(min, min, max, step);
		postRecDelSpinner = new JSpinner(postmodel);
		postRecDelSpinner.addChangeListener(this);
		postRecLabel = new JLabel("Default postrecording delay:");
		c.gridy++;
		c2.gridy++;
		recCfgPanel.add(postRecLabel, c);
		recCfgPanel.add(postRecDelSpinner, c2);
		c3.gridy=c.gridy;
        recCfgPanel.add(new JLabel("ms"), c3);
        c2.gridwidth=2;
        
		c.gridy++;
		c2.gridy++;
		
		forcePostRecPhaseCheckBox= new JCheckBox();
		forcePostRecPhaseCheckBox.addActionListener(this);
		recCfgPanel.add(new JLabel("Force post recording phase:"), c);
		recCfgPanel.add(forcePostRecPhaseCheckBox, c2);
		
		c.gridy++;
		c2.gridy++;
		c3.gridy=c.gridy;
		recCfgPanel.add(new JLabel("Recording URL (directory):"), c);
		urlField = new JTextField();
		urlDoc = urlField.getDocument();
		urlDoc.addDocumentListener(this);
		EditorKitMenu urlFieldEkm=new EditorKitMenu(urlField);
		urlFieldEkm.setPopupMenuActiv(true);
		c2.gridwidth=1;
		recCfgPanel.add(urlField, c2);
		recsUrlBrowseButton = new JButton("Browse...");
		recsUrlBrowseButton.addActionListener(this);
		recCfgPanel.add(recsUrlBrowseButton, c3);
		c2.gridwidth=2;
		
//		JPanel resetPanel=new JPanel();
//		FlowLayout rpl=new FlowLayout(FlowLayout.RIGHT);
//		resetPanel.setLayout(rpl);
//		resetToDefaultsButton = new JButton("Reset to defaults");
//		resetToDefaultsButton.addActionListener(this);
//		resetPanel.add(resetToDefaultsButton);
//        resetButton = new JButton("Reset");
//        resetButton.addActionListener(this);
//        resetPanel.add(resetButton);
        
        contentPane.add(recCfgPanel, BorderLayout.CENTER);
//		add(resetPanel,BorderLayout.SOUTH);
	}
	
	public void resetToInitial(){
        
        setRecordingConfiguration(currentConfig);
    }
	public void resetToDefaults(){
	    RecordingConfiguration defaultConfig=new RecordingConfiguration();
	    RecordingConfiguration oldCurrentConfig=currentConfig;
	    
	    // application default is still ITEM, but new projects are configured
		// with SESSION by default
	    defaultConfig.setCaptureScope(CaptureScope.SESSION);
	    setRecordingConfiguration(defaultConfig);
	    
	    // do not override current config with defaults
	    currentConfig=oldCurrentConfig;
	}

	/**
     * @param recordingConfiguration recording configuration
     */
    public void setRecordingConfiguration(RecordingConfiguration recordingConfiguration) {
        
      
       
        preRecDelSpinner.setValue(new Integer(recordingConfiguration.getPreRecDelay()));
        postRecDelSpinner.setValue(new Integer(recordingConfiguration.getPostRecDelay()));
        forcePostRecPhaseCheckBox.setSelected(recordingConfiguration.isForcePostRecDelayPhase());
        CaptureScope los=recordingConfiguration.getCaptureScope();
        if(los==null){
        	los=CaptureScope.ITEM;
        }
        EnumSelectionItem<CaptureScope> losi=new EnumSelectionItem<RecordingConfiguration.CaptureScope>(los);
        captureScopeBox.setSelectedItem(losi);
        PrimaryRecordTarget prt=recordingConfiguration.getPrimaryRecordTarget();
        EnumSelectionItem<PrimaryRecordTarget> prti=new EnumSelectionItem<PrimaryRecordTarget>(prt);
        primaryRecordTargetBox.setSelectedItem(prti);
        overwriteCheckBox.setSelected(recordingConfiguration.getOverwrite());
        overwriteWarningCheckBox.setSelected(recordingConfiguration.isOverwriteWarning());
        urlField.setText(recordingConfiguration.getUrl());
        //cacheInFilesCheckBox.setSelected(r.isCacheInFiles());
        modeBox.setSelectedItem(recordingConfiguration.getMode());
        seamlessCheckBox.setSelected(recordingConfiguration.isSeamlessAutorecording());
        progressToNextUnrecordedCheckbox.setSelected(recordingConfiguration.getProgressToNextUnrecorded());
        resetPeakOnRecordingCheckBox.setSelected(recordingConfiguration.getResetPeakOnRecording());
        setDependencies();
        currentConfig=recordingConfiguration;
    }
    
	public void applyValues(RecordingConfiguration r){
        r.setPreRecDelay(((Integer) preRecDelSpinner.getValue()).intValue());
        r.setPostRecDelay(((Integer) postRecDelSpinner.getValue()).intValue());
        r.setForcePostRecDelayPhase(forcePostRecPhaseCheckBox.isSelected());
        EnumSelectionItem<CaptureScope> selLos=(EnumSelectionItem<RecordingConfiguration.CaptureScope>)captureScopeBox.getSelectedItem();
        r.setCaptureScope(selLos.getEnumVal());
        EnumSelectionItem<PrimaryRecordTarget> selPRT=(EnumSelectionItem<PrimaryRecordTarget>)primaryRecordTargetBox.getSelectedItem();
        r.setPrimaryRecordTarget(selPRT.getEnumVal());
        r.setOverwrite(overwriteCheckBox.isSelected());
        r.setOverwriteWarning(overwriteWarningCheckBox.isSelected());
        r.setMode((String) modeBox.getSelectedItem());
        r.setSeamlessAutorecording(seamlessCheckBox.isSelected());
        r.setProgressToNextUnrecorded(progressToNextUnrecordedCheckbox.isSelected());
        r.setResetPeakOnRecording(resetPeakOnRecordingCheckBox.isSelected());
        r.setUrl(urlField.getText());
        
        
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent ce) {
		Object src = ce.getSource();
		if (src == preRecDelSpinner) {
			//System.out.println("new value: "+preRecDelSpinner.getValue());
//			r.setPreRecDelay(((Integer) preRecDelSpinner.getValue()).intValue());
		} else if (src == postRecDelSpinner) {
			//System.out.println("new value: "+postRecDelSpinner.getValue());
//			r.setPostRecDelay(((Integer) postRecDelSpinner.getValue()).intValue());
		} 

	}

	private void setDependencies() {
		//		
		//		boolean enabled = r.getMode() != RecordingConfiguration.MANUAL;
		//		progressToNextUnrecordedCheckbox.setEnabled(enabled);
		//		progressToNextUnrecordedLabel.setEnabled(enabled);
	    
	    EnumSelectionItem<PrimaryRecordTarget> selPRT=(EnumSelectionItem<PrimaryRecordTarget>)primaryRecordTargetBox.getSelectedItem();
        PrimaryRecordTarget prt=selPRT.getEnumVal();
        boolean seamlessEnabled=PrimaryRecordTarget.DIRECT.equals(prt);
        seamlessCheckBox.setEnabled(seamlessEnabled);
        
        boolean primaryRecordTargetEnabled=!seamlessCheckBox.isSelected();
        primaryRecordTargetBox.setEnabled(primaryRecordTargetEnabled);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		if (src == recsUrlBrowseButton) {
			JFileChooser chooser = new JFileChooser();
			//chooser.setDialogTitle(uiString.getString("SelectPromptFile"));
			chooser.setDialogTitle("Select recordings directory");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
//				r.setUrl("file:" + chooser.getSelectedFile().getAbsolutePath());
//				urlField.setText(r.getUrl());
				File selFile=chooser.getSelectedFile();
            	URI selFileUri=selFile.toURI();
            	String selFileuriStr=selFileUri.toString();
//				urlField.setText("file:" + chooser.getSelectedFile().getAbsolutePath());
            	urlField.setText(selFileuriStr);
			}
		}

		super.actionPerformed(ae);
		setDependencies();
		
	}
	public void documentChanged(DocumentEvent de) {
//		Document src = de.getDocument();
//		if (src == urlDoc) {
//			r.setUrl(urlField.getText());
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

    

//    /* (non-Javadoc)
//     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
//     */
//    public void keyPressed(KeyEvent arg0) {
//        // TODO Auto-generated method stub
//        int keyCode=arg0.getKeyCode();
//        String keyText=KeyEvent.getKeyText(keyCode);
//        //System.out.println("Code: "+keyCode+" text: "+keyText);
//       recShortKey.setText(keyText);
//       arg0.consume();
//    }
//
//    /* (non-Javadoc)
//     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
//     */
//    public void keyReleased(KeyEvent arg0) {
//        // TODO Auto-generated method stub
//        arg0.consume();
//    }
//
//    /* (non-Javadoc)
//     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
//     */
//    public void keyTyped(KeyEvent arg0) {
//        arg0.consume();
//    }
//
//    /* (non-Javadoc)
//     * @see java.awt.KeyEventDispatcher#dispatchKeyEvent(java.awt.event.KeyEvent)
//     */
//    public boolean dispatchKeyEvent(KeyEvent arg0) {
//        int keyCode=arg0.getKeyCode();
//        String keyText=KeyEvent.getKeyText(keyCode);
//        //System.out.println("Disp.Code: "+keyCode+" text: "+keyText);
//       //recShortKey.setText(keyText+" (char: "+arg0.getKeyChar()+")");
//        return false;
//    }

}
