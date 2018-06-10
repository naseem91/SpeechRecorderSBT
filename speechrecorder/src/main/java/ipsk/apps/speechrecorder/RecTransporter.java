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

//
//  Created by Christoph Draxler on Thu Dec 05 2002.
//

package ipsk.apps.speechrecorder;

import ipsk.apps.speechrecorder.actions.RecTransporterActions;

import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

public class RecTransporter extends JPanel implements RecObserver, KeyEventDispatcher {

    /**
     * 
     */
    private static final long serialVersionUID = 8217409677981575042L;

//    private RecScriptManager recScriptManager;

    // buttons for normal operation
    private JButton recButton;
    private boolean showRecStartAction=true;
    private boolean showRecStopAction=true;

    // public static
    private JButton backButton;

    private JButton forwardButton;

    private JButton advanceToNextButton;

    private JButton pauseButton;

    private JButton playButton;

    private JButton playPauseButton;

    private JButton[] enumAnnotationButtons;

    private JLabel fileLabel;

    private final String startLabel;

    private final String stopLabel;
    
    private final String stopAndNextLabel;

    private UIResources uiString = null;

//    private RecStatus recStat;

    private Border eb;

    private String itemCode;

    private boolean speakerAddressed = false;

    // private boolean autoProgress = false;
    private boolean autoRecording = false;

    private boolean started = false;

    // private RecScriptManager recScriptManager;
//    private SpeechRecorder speechRecorder;

    private InputMap keyMap = new InputMap();

    private boolean consumeAllKeys;

    private RecTransporterActions rta;

    private boolean listening=false;

    private boolean annotationMode=false;
    
    private boolean playbackEnabled=false;
    private boolean progressPaused=true;
   

    public RecTransporter(RecTransporterActions recTransporterActions,
            boolean speakerAddressed) {
        super();
//        this.speechRecorder = speechRecorder;
        this.rta=recTransporterActions;
        this.speakerAddressed = speakerAddressed;

        uiString = UIResources.getInstance();
//        recStat = RecStatus.getInstance();
//        recScriptManager = speechRecorder.getRecScriptManager();

        startLabel = uiString.getString("StartButtonText");
//        recLabel = uiString.getString("RecordingButtonText");
//        pauseLabel = uiString.getString("PauseButtonText");
        stopLabel = uiString.getString("StopButtonText");
        stopAndNextLabel = uiString.getString("StopAndNextButtonText");
//        continueLabel = uiString.getString("ContinueButtonText");
//        backLabel = uiString.getString("BackwardButtonText");
//        forwardLabel = uiString.getString("ForwardButtonText");
//        advanceToNextLabel = uiString.getString("AdvanceToNextButtonText");
//        playLabel = uiString.getString("PlayButtonText");
//        playPauseLabel = new String(uiString.getString("PlayButtonText") + "-"
//                + uiString.getString("PauseButtonText"));

        fileLabel = new JLabel("---");
        fileLabel.setHorizontalAlignment(JLabel.CENTER);

        backButton = new JButton(rta.backwardAction);
        backButton.setToolTipText(null);
        
        forwardButton = new JButton(rta.forwardAction);
        forwardButton.setToolTipText(null);

        advanceToNextButton = new JButton(rta.advanceToNextAction);
        advanceToNextButton.setToolTipText(null);
       
        pauseButton = new JButton(rta.pauseAutoRecordingAction);
        pauseButton.setToolTipText(null);

        rta.startRecordAction = recTransporterActions.startRecordAction;
        rta.stopRecordAction = recTransporterActions.stopRecordAction;
        
        recButton = new JButton(rta.startRecordAction);
        recButton.setToolTipText(null);

        playButton = new JButton(rta.startPlaybackAction);
        playButton.setToolTipText(null);
        
        playPauseButton = new JButton(rta.pausePlaybackAction);
        playPauseButton.setToolTipText(null);
        
        if(rta.annotationActions!=null){
        	enumAnnotationButtons=new JButton[rta.annotationActions.length];
        	for(int i=0;i<rta.annotationActions.length;i++){
        		enumAnnotationButtons[i]=new JButton(rta.annotationActions[i]);
        	}
        }
        
        eb = BorderFactory.createEmptyBorder(4, 10, 4, 10);
        setBorder(eb);
        create();
        disableAll();

        // now attach the controller to the recording status
//        recStat.attach(this);
    }
    

    public RecTransporter(SpeechRecorder speechrecorder,
            RecTransporterActions actions) {
        this(actions, false);
    }
    
    
    private void setButtActionClearToolTip(JButton butt,Action action){
        butt.setAction(action);
        butt.setToolTipText(null);
    }
    

    public void setKeyButtonBindingEnabled(boolean enabled) {
       
        KeyboardFocusManager kfm = KeyboardFocusManager
                .getCurrentKeyboardFocusManager();
        if (enabled && keyMap.size() >0) {
            if (!listening)kfm.addKeyEventDispatcher(this);
            listening=true;
        } else {
            kfm.removeKeyEventDispatcher(this);
            listening=false;
        }
    }
    
   

    private void create() {
        started = false;
        GridLayout layout = new GridLayout();
        layout.setHgap(2);
        layout.setVgap(2);
        setLayout(layout);
        removeAll();
        if (!speakerAddressed) {
            // setLayout(new GridLayout(1, 7, 2, 2));
            add(fileLabel);
            if(annotationMode){
            	if(enumAnnotationButtons==null){
            		enumAnnotationButtons=new JButton[rta.annotationActions.length];
                	for(int i=0;i<rta.annotationActions.length;i++){
                		enumAnnotationButtons[i]=new JButton(rta.annotationActions[i]);
                	}
            	}
            	for(JButton ab:enumAnnotationButtons){
            		add(ab);
            	}
            }else{
            add(backButton);
            add(recButton);
            add(forwardButton);
            add(advanceToNextButton);
            if (autoRecording){
                add(pauseButton);
            }
            }
            add(playButton);
            add(playPauseButton);
            // add(testButton);
        } else {
           if(showRecStartAction || showRecStopAction)add(recButton);
           
            if (autoRecording) {
                setButtActionClearToolTip(pauseButton,rta.startAutoRecordingAction);
                add(pauseButton);
            }
        }

    }

    
    private void switchMode(boolean annotate){
    	if(annotationMode!=annotate){
    		annotationMode=annotate;
    		create();
    		revalidate();
    		repaint();
    	}
    }
    
    
    private void setAnnotationActionsEnabled(boolean enabled){
    	if(rta.annotationActions!=null){
        	for(Action annoAction:rta.annotationActions){
        		annoAction.setEnabled(enabled);
        	}
        }
    }

    /**
     * implements the RecObserver interface and changes the state of the
     * recording control buttons according to the current recording status.
     */
    public void update(int status) {
        if (status == RecStatus.INIT) {
            disableAll();
            started = false;
            return;
        } else if (status == RecStatus.CLOSE) {
           close();
            return;
        }
        
//        ipsk.db.speech.PromptItem pi= recScriptManager.getCurrentPromptItem();
//        if( pi instanceof Recording){
//         recordingFile=((Recording)pi).getItemcode();
//        }

        // boolean playEnabled =
        // (recScriptManager.getRecCounter(recScriptManager.getRecIndex()) > 0);
//        boolean playEnabled = speechRecorder.isItemPlayable();
        
//        fileLabel.setText(recordingFilename);
        // if (!speakerAddressed) {
        if (status == RecStatus.IDLE) {
        	switchMode(false);
            setAutoRecording(autoRecording);
            rta.stopRecordAction.setEnabled(false);
            setButtActionClearToolTip(recButton,rta.startRecordAction);
            rta.startRecordAction.setEnabled(true);
            recButton.setVisible(showRecStartAction);
            if (autoRecording) {
                //recButton.setEnabled(false);
                rta.pauseAutoRecordingAction.setEnabled(false);
                rta.startRecordAction.setEnabled(false);
                if (progressPaused) {

                    if (started){
                            
                        rta.startAutoRecordingAction.setEnabled(false);
                        rta.continueAutoRecordingAction.setEnabled(true);
                        setButtActionClearToolTip(pauseButton,rta.continueAutoRecordingAction);
                     
                    } else{
                        setButtActionClearToolTip(pauseButton,rta.startAutoRecordingAction);              
                        rta.continueAutoRecordingAction.setEnabled(false);
                    rta.startAutoRecordingAction.setEnabled(true);
                    }
                    fileLabel.setEnabled(true);
                } else {

                    if(!started){
                    setButtActionClearToolTip(pauseButton,rta.startAutoRecordingAction); 
                    
                    rta.startAutoRecordingAction.setEnabled(true);
                    }
                    fileLabel.setEnabled(true);
                }
//            } else {
//                recButton.setEnabled(true);
            }
            rta.forwardAction.setEnabled(true);
            rta.advanceToNextAction.setEnabled(true);
            rta.backwardAction.setEnabled(true);
            rta.startPlaybackAction.setEnabled(playbackEnabled);
            rta.stopPlaybackAction.setEnabled(false);
            rta.pausePlaybackAction.setEnabled(false);
            setButtActionClearToolTip(playButton,rta.startPlaybackAction);
           
            rta.pausePlaybackAction.setEnabled(false);
            setButtActionClearToolTip(playPauseButton,rta.pausePlaybackAction);
            
            fileLabel.setEnabled(true);
            setAnnotationActionsEnabled(false);
        } else if (status == RecStatus.ITEM_ERROR) {
        	switchMode(false);
            setAutoRecording(autoRecording);
            rta.stopRecordAction.setEnabled(false);
            setButtActionClearToolTip(recButton,rta.startRecordAction);
            rta.startRecordAction.setEnabled(false);
            recButton.setVisible(showRecStartAction);
            if (autoRecording) {
                //recButton.setEnabled(false);
                rta.pauseAutoRecordingAction.setEnabled(false);
                rta.startRecordAction.setEnabled(false);
                if (progressPaused) {

                    if (started){
                            
                        rta.startAutoRecordingAction.setEnabled(false);
                        rta.continueAutoRecordingAction.setEnabled(false);
                        setButtActionClearToolTip(pauseButton,rta.continueAutoRecordingAction);
                     
                    } else{
                        setButtActionClearToolTip(pauseButton,rta.startAutoRecordingAction);              
                        rta.continueAutoRecordingAction.setEnabled(false);
                    rta.startAutoRecordingAction.setEnabled(false);
                    }
                    fileLabel.setEnabled(true);
                } else {

                    if(!started){
                    setButtActionClearToolTip(pauseButton,rta.startAutoRecordingAction); 
                    
                    rta.startAutoRecordingAction.setEnabled(false);
                    }
                    fileLabel.setEnabled(true);
                }
//            } else {
//                recButton.setEnabled(true);
            }
            rta.forwardAction.setEnabled(true);
            rta.advanceToNextAction.setEnabled(true);
            rta.backwardAction.setEnabled(true);
            rta.startPlaybackAction.setEnabled(playbackEnabled);
            rta.stopPlaybackAction.setEnabled(false);
            rta.pausePlaybackAction.setEnabled(false);
            setButtActionClearToolTip(playButton,rta.startPlaybackAction);
           
            rta.pausePlaybackAction.setEnabled(false);
            setButtActionClearToolTip(playPauseButton,rta.pausePlaybackAction);
            
            fileLabel.setEnabled(true);
            setAnnotationActionsEnabled(false);
        }else if (status == RecStatus.NON_RECORDING) {
        	switchMode(false);
            setAutoRecording(autoRecording);
            rta.stopRecordAction.setEnabled(false);
            setButtActionClearToolTip(recButton,rta.startRecordAction);
            rta.startRecordAction.setEnabled(false);
            recButton.setVisible(showRecStartAction);
            if (autoRecording) {
                //recButton.setEnabled(false);
                rta.startRecordAction.setEnabled(false);
                if (progressPaused) {

                    if (started){
                            
                        rta.startAutoRecordingAction.setEnabled(false);
                        rta.continueAutoRecordingAction.setEnabled(true);
                        setButtActionClearToolTip(pauseButton,rta.continueAutoRecordingAction);
                     
                    } else{
                        setButtActionClearToolTip(pauseButton,rta.startAutoRecordingAction);              
                        rta.continueAutoRecordingAction.setEnabled(false);
                    rta.startAutoRecordingAction.setEnabled(true);
                    }
                    fileLabel.setEnabled(true);
                } else {

                    if(!started){
                    setButtActionClearToolTip(pauseButton,rta.startAutoRecordingAction); 
                    
                    rta.startAutoRecordingAction.setEnabled(true);
                    }
                    fileLabel.setEnabled(true);
                }
//            } else {
//                recButton.setEnabled(true);
            }
            rta.forwardAction.setEnabled(true);
            rta.advanceToNextAction.setEnabled(true);
            rta.backwardAction.setEnabled(true);
            rta.startPlaybackAction.setEnabled(false);
            rta.stopPlaybackAction.setEnabled(false);
            rta.pausePlaybackAction.setEnabled(false);
            setButtActionClearToolTip(playButton,rta.startPlaybackAction);
           
            rta.pausePlaybackAction.setEnabled(false);
            setButtActionClearToolTip(playPauseButton,rta.pausePlaybackAction);
            
            fileLabel.setEnabled(false);
        }else if (status == RecStatus.PLAY_PROMPT || status ==RecStatus.PLAY_PROMPT_PREVIEW) {
           
            rta.advanceToNextAction.setEnabled(false);
            rta.forwardAction.setEnabled(false);
          
            rta.backwardAction.setEnabled(false);
           
            rta.startPlaybackAction.setEnabled(false);
            rta.stopPlaybackAction.setEnabled(false);
            rta.pausePlaybackAction.setEnabled(false);
            
        }else if (status == RecStatus.NON_RECORDING_WAIT) {
            switchMode(false);
            // TODO this should go to startPlayPrompt() in b
            started = true;
            rta.startRecordAction.setEnabled(false);
            rta.stopRecordAction.setEnabled(false);
            rta.stopNonrecordingAction.setEnabled(true);
            
            rta.forwardAction.setEnabled(false);
            rta.advanceToNextAction.setEnabled(false);
            rta.backwardAction.setEnabled(false);
            
            rta.startPlaybackAction.setEnabled(false);
            rta.stopPlaybackAction.setEnabled(false);
            rta.pausePlaybackAction.setEnabled(false);
            if (autoRecording) {
                setButtActionClearToolTip(recButton,rta.stopNonrecordingAction);
//                rta.stopRecordAction.putValue(Action.NAME,stopAndNextLabel);
//                rta.pauseAutoRecordingAction.setEnabled(true);
//                setButtActionClearToolTip(pauseButton,rta.pauseAutoRecordingAction);
                rta.continueAutoRecordingAction.setEnabled(false);
                rta.startAutoRecordingAction.setEnabled(false);
               
                
            } 
        } else if (status == RecStatus.PRERECWAITING) {
        	switchMode(false);
            // TODO this should go to startPlayPrompt() in b
            started = true;
            rta.startRecordAction.setEnabled(false);
            rta.stopRecordAction.setEnabled(false);
            rta.advanceToNextAction.setEnabled(false);
            rta.forwardAction.setEnabled(false);
          
            rta.backwardAction.setEnabled(false);
           
            rta.startPlaybackAction.setEnabled(false);
            rta.stopPlaybackAction.setEnabled(false);
            rta.pausePlaybackAction.setEnabled(false);
            recButton.setVisible(showRecStopAction);
            if (autoRecording) {
                
                rta.stopRecordAction.putValue(Action.NAME,stopAndNextLabel);
                setButtActionClearToolTip(recButton,rta.stopRecordAction);
                rta.pauseAutoRecordingAction.setEnabled(false);
                rta.continueAutoRecordingAction.setEnabled(false);
                rta.startAutoRecordingAction.setEnabled(false);
                setButtActionClearToolTip(pauseButton,rta.pauseAutoRecordingAction);
                
            } else {
            	rta.stopRecordAction.putValue(Action.NAME,stopLabel);
                setButtActionClearToolTip(recButton,rta.stopRecordAction);

            }
            setAnnotationActionsEnabled(false);
        } else if (status == RecStatus.RECORDING) {
        	switchMode(false);
            rta.startRecordAction.setEnabled(false);
            rta.stopRecordAction.setEnabled(true);
            
            rta.forwardAction.setEnabled(false);
            rta.advanceToNextAction.setEnabled(false);
            recButton.setVisible(showRecStopAction);
            recButton.setEnabled(true);
            rta.backwardAction.setEnabled(false);
            
            rta.startPlaybackAction.setEnabled(false);
            rta.stopPlaybackAction.setEnabled(false);
            rta.pausePlaybackAction.setEnabled(false);
            if (autoRecording) {
                
                setButtActionClearToolTip(recButton,rta.stopRecordAction);
                rta.stopRecordAction.putValue(Action.NAME,stopAndNextLabel);
                rta.pauseAutoRecordingAction.setEnabled(true);
                rta.continueAutoRecordingAction.setEnabled(false);
                rta.startAutoRecordingAction.setEnabled(false);
                setButtActionClearToolTip(pauseButton,rta.pauseAutoRecordingAction);
                
            } else {
            	rta.stopRecordAction.putValue(Action.NAME,stopLabel);
                setButtActionClearToolTip(recButton,rta.stopRecordAction);

            }
        } else if (status == RecStatus.POSTRECWAITING) {
            rta.startRecordAction.setEnabled(false);
            rta.stopRecordAction.setEnabled(false);
            if (autoRecording) {
                setButtActionClearToolTip(recButton,rta.stopRecordAction);
                rta.stopRecordAction.putValue(Action.NAME,stopAndNextLabel);
                rta.pauseAutoRecordingAction.setEnabled(false);
                rta.continueAutoRecordingAction.setEnabled(false);
                rta.startAutoRecordingAction.setEnabled(false);
               
            } else {
            	rta.stopRecordAction.putValue(Action.NAME,stopLabel);
                setButtActionClearToolTip(recButton,rta.stopRecordAction);

            }
            rta.forwardAction.setEnabled(false);
            rta.backwardAction.setEnabled(false);
            //playButton.setEnabled(false);
            rta.startPlaybackAction.setEnabled(false);
            rta.stopPlaybackAction.setEnabled(false);
            rta.pausePlaybackAction.setEnabled(false);
            rta.pauseAutoRecordingAction.setEnabled(false);
        } else if (status == RecStatus.ANNOTATE) {
        	switchMode(true);
        	rta.stopRecordAction.setEnabled(false);
        	// setButtActionClearToolTip(recButton,rta.startRecordAction);

        	rta.startRecordAction.setEnabled(false);
        	recButton.setVisible(false);

        	rta.pauseAutoRecordingAction.setEnabled(false);
        	rta.startRecordAction.setEnabled(false);

        	rta.startAutoRecordingAction.setEnabled(false);
        	rta.continueAutoRecordingAction.setEnabled(false);
        	// setButtActionClearToolTip(pauseButton,rta.continueAutoRecordingAction);

        	//setButtActionClearToolTip(pauseButton,rta.startAutoRecordingAction);              
        	rta.continueAutoRecordingAction.setEnabled(false);
        	rta.startAutoRecordingAction.setEnabled(false);

        	fileLabel.setEnabled(true);

        	//setButtActionClearToolTip(pauseButton,rta.startAutoRecordingAction); 

        	rta.startAutoRecordingAction.setEnabled(false);

        	rta.forwardAction.setEnabled(false);
        	rta.advanceToNextAction.setEnabled(false);
        	rta.backwardAction.setEnabled(false);
        	
        	rta.startPlaybackAction.setEnabled(playbackEnabled);
        	rta.stopPlaybackAction.setEnabled(false);
        	rta.pausePlaybackAction.setEnabled(false);
        	setButtActionClearToolTip(playButton,rta.startPlaybackAction);

        	rta.pausePlaybackAction.setEnabled(false);
        	setButtActionClearToolTip(playPauseButton,rta.pausePlaybackAction);

        	fileLabel.setEnabled(true);
        	setAnnotationActionsEnabled(true);
        	
        } else if (status == RecStatus.PLAY) {
            rta.startRecordAction.setEnabled(false);
            rta.startAutoRecordingAction.setEnabled(false);
            rta.continueAutoRecordingAction.setEnabled(false);
            recButton.setEnabled(false);
            rta.backwardAction.setEnabled(false);
            rta.forwardAction.setEnabled(false);
            rta.advanceToNextAction.setEnabled(false);

            rta.startPlaybackAction.setEnabled(false);
            rta.stopPlaybackAction.setEnabled(true);
            setButtActionClearToolTip(playButton,rta.stopPlaybackAction);

            rta.pausePlaybackAction.setEnabled(true);
            setButtActionClearToolTip(playPauseButton,rta.pausePlaybackAction);
            setAnnotationActionsEnabled(false);
            
        } else if (status == RecStatus.PLAYPAUSE) {
            rta.startRecordAction.setEnabled(false);
            rta.forwardAction.setEnabled(false);
            recButton.setEnabled(false);
            rta.backwardAction.setEnabled(false);

            rta.startPlaybackAction.setEnabled(false);
            rta.stopPlaybackAction.setEnabled(true);
            setButtActionClearToolTip(playButton,rta.stopPlaybackAction);

            rta.pausePlaybackAction.setEnabled(false);
            rta.continuePlaybackAction.setEnabled(true);
            setButtActionClearToolTip(playPauseButton,rta.continuePlaybackAction);
            
        } else if (status == RecStatus.INIT) {
            rta.startRecordAction.setEnabled(true);
            recButton.setEnabled(true);
            recButton.setText(startLabel);
            recButton.setActionCommand("start");
            rta.forwardAction.setEnabled(false);
            rta.backwardAction.setEnabled(false);
            setButtActionClearToolTip(playButton,rta.startPlaybackAction);
            rta.startPlaybackAction.setEnabled(false);
            rta.stopPlaybackAction.setEnabled(false);

            rta.pausePlaybackAction.setEnabled(false);
            rta.continuePlaybackAction.setEnabled(false);
            setButtActionClearToolTip(playPauseButton,rta.pausePlaybackAction);
        } else if (status == RecStatus.PROCESSING) {
          
            rta.startRecordAction.setEnabled(false);
            setButtActionClearToolTip(recButton,rta.startRecordAction);
            recButton.setVisible(showRecStartAction);
            rta.forwardAction.setEnabled(false);
            rta.startRecordAction.setEnabled(false);
            rta.backwardAction.setEnabled(false);
            rta.startPlaybackAction.setEnabled(false);
            rta.stopPlaybackAction.setEnabled(false);
            rta.pauseAutoRecordingAction.setEnabled(false);
        }
    }

    private void disableAll() {
        
        rta.startRecordAction.setEnabled(false);
        rta.stopRecordAction.setEnabled(false);
        
        rta.startAutoRecordingAction.setEnabled(false);
        rta.continueAutoRecordingAction.setEnabled(false);
        rta.pauseAutoRecordingAction.setEnabled(false);
        
        rta.backwardAction.setEnabled(false);
        rta.continuePlaybackAction.setEnabled(false);
        rta.advanceToNextAction.setEnabled(false);
        rta.forwardAction.setEnabled(false);
        
        rta.startPlaybackAction.setEnabled(false);
        rta.pausePlaybackAction.setEnabled(false);
        rta.stopPlaybackAction.setEnabled(false);
        
        fileLabel.setText("---");
        fileLabel.setEnabled(false);
        setAnnotationActionsEnabled(false);
    }

    /**
     * @return true if auto recording mode
     */
    public boolean isAutoRecording() {
        return autoRecording;
    }

    /**
     * @param b
     */
    public void setAutoRecording(boolean b) {
        if (b != autoRecording) {
            autoRecording = b;
            create();
        }
    }

    /**
     * @return true if this panel is addressed to the speaker (not the experimenter)
     */
    public boolean isSpeakerAddressed() {
        return speakerAddressed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.KeyEventDispatcher#dispatchKeyEvent(java.awt.event.KeyEvent)
     */
    public boolean dispatchKeyEvent(KeyEvent e) {
        Set actionSet = (Set) (keyMap.get(KeyStroke.getKeyStrokeForEvent(e)));
        if (actionSet != null) {
            Iterator i = actionSet.iterator();
            while (i.hasNext()) {
                Action action = (Action) i.next();

                if (action.isEnabled()) {
                    // System.out.println("Dispatching "+e.getKeyCode());
                    e.consume();
                    action.actionPerformed(new ActionEvent(this,
                            ActionEvent.ACTION_PERFORMED, (String) action
                                    .getValue(Action.ACTION_COMMAND_KEY)));

                    return true;
                }
            }
        }
        if(consumeAllKeys){
            e.consume();
            return true; 
        }else{
        // pass this event by
        return false;
        }
    }

   
    public void addKeyStrokeAction(KeyStroke keyStroke, Action action) {
        if (action != null) {
            Set<Action> actionSet = (Set<Action>) keyMap.get(keyStroke);
            if (actionSet == null) {
                actionSet = new HashSet<Action>();
            }
            actionSet.add((Action)action);
            keyMap.put(keyStroke, actionSet);
        }
    }

    public void clearActionKeyCodes() {
        keyMap.clear();
    }

    /**
     * @return Returns the consumeAllKeys.
     */
    public boolean isConsumeAllKeys() {
        return consumeAllKeys;
    }

    /**
     * @param consumeAllKeys The consumeAllKeys to set.
     */
    public void setConsumeAllKeys(boolean consumeAllKeys) {
        this.consumeAllKeys = consumeAllKeys;
    }
    
    public void close(){
        disableAll();
        started = false;
        setKeyButtonBindingEnabled(false);
    }


    public boolean isShowRecStartAction() {
        return showRecStartAction;
}

    public void setShowRecStartAction(boolean showRecStartAction) {
        this.showRecStartAction = showRecStartAction;
        create();
    }


    public boolean isShowRecStopAction() {
        return showRecStopAction;
    }


    public void setShowRecStopAction(boolean showRecStopAction) {
        this.showRecStopAction = showRecStopAction;
        create();
    }


    public String getItemCode() {
        return itemCode;
    }


    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
        String label="";
        if(this.itemCode!=null){
            label=this.itemCode;
        }
        fileLabel.setText(label);
    }


    public boolean isPlaybackEnabled() {
        return playbackEnabled;
    }


    public void setPlaybackEnabled(boolean playbackEnabled) {
        this.playbackEnabled = playbackEnabled;
    }


    public boolean isProgressPaused() {
        return progressPaused;
    }


    public void setProgressPaused(boolean progressPaused) {
        this.progressPaused = progressPaused;
    }

}
