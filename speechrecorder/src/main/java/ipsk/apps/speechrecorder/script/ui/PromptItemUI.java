//    Speechrecorder
//    (c) Copyright 2012
//    Institute of Phonetics and Speech Processing,
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
 * Created on Apr 24, 2005

 *
 * Project: JSpeechRecorder
 * Original author: draxler
 */
package ipsk.apps.speechrecorder.script.ui;

import ipsk.apps.speechrecorder.MIMETypes;
import ipsk.apps.speechrecorder.prompting.PromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter;
import ipsk.awt.font.AutoFontFamilyManager;
import ipsk.beans.MultiSelectPropertyState;
import ipsk.db.speech.Mediaitem;
import ipsk.db.speech.Nonrecording;
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.Reccomment;
import ipsk.db.speech.Recinstructions;
import ipsk.db.speech.Recording;
import ipsk.swing.JServiceSelector;
import ipsk.swing.RedoAction;
import ipsk.swing.TitledPanel;
import ipsk.swing.UndoAction;
import ipsk.swing.action.EditActions;
import ipsk.swing.action.EditActionsListener;
import ipsk.swing.text.EditorKitMenu;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.StateEdit;
import javax.swing.undo.StateEditable;
import javax.swing.undo.UndoManager;

/**
 *
 * class PromptItemEditor
 * 
 * displays the contents of a prompt item. Not all fields are editable:
 * 
 * control fields (prerec, postrec, rec, beep, modal, etc.) are editable
 * 
 * remotely stored prompts, i.e. non-empty URL: 
 * - URL is editable
 * - some attributes can be edited 
 * - prompt field only displays URL contents
 * - prompt description displayed in text area
 *  
 * local text prompt: 
 * - prompt field is editable
 * 
 * @author draxler
 * @version 1.0, 24.04.2005
 * */
public class PromptItemUI extends BaseMediaitemUI implements ActionListener, EditActionsListener, PropertyChangeListener, DocumentListener, StateEditable{

  

  
    private PromptItem promptItem;
//    private boolean itemNeedsSaving;

    private JTextField recordingCodeWidget;
//    private JButton addExtraMediaButton;
//    private JButton removeExtraMediaButton;
    private EditorKitMenu recordingCodeWidgetEditorKitMenu;

    private JCheckBox blockedWidget;
    private JCheckBox preRecDelayNonDefaultWidget;
    private JTextField preRecDelayWidget;
    private JCheckBox recDurationLimitedWidget;
    private JTextField recDurationWidget;
    private JCheckBox postRecDelayNonDefaultWidget;
    private JTextField postRecDelayWidget;
    private JCheckBox finalSilenceWidget;
    private JCheckBox beepPlayWidget;
    private JComboBox recTypeWidget;
//    private JComboBox mediaItemCountWidget;
    private List<MediaitemUI> mediaItemUIs;
   
    
    private String[] promptFontFamilies;
    private String[] instructionsFontFamilies;
    /**
     * @return the instructionsFontFamilies
     */
    public String[] getInstructionsFontFamilies() {
        return instructionsFontFamilies;
    }

    private String[] descriptionFontFamilies;
    
    /**
     * @return the descriptionFontFamilies
     */
    public String[] getDescriptionFontFamilies() {
        return descriptionFontFamilies;
    }

   
     private AutoFontFamilyManager instructionsFontManager=new AutoFontFamilyManager();
        private AutoFontFamilyManager descriptionFontManager=new AutoFontFamilyManager();
  

    private JTextField promptInstructionsWidget;
    private EditorKitMenu promptInstructionsWidgetEditorKitMenu;
    private JTextField promptCommentWidget;
    private EditorKitMenu promptCommentWidgetEditorKitMenu;
   
  
 
    private JLabel codeLabel;
//    private JLabel miCountLabel;
    private TitledPanel instructionsPanel;
    private TitledPanel commentPanel;

    private boolean isRecording;
   
    private JLabel preRecLabel;
    private JLabel preRecUnitLabel;
    private JLabel recDurationLabel;
    private JLabel recDurationUnitLabel;
    private JLabel postRecLabel;
    private JLabel postRecUnitLabel;
    
    private int defaultPreRecording;
    private int defaultPostRecording;

    
    private EditActionsListener editActionsListener;
   
  
    
    private JServiceSelector<PromptPresenter> promptPresenterSelector;
    private JTabbedPane itemPanel;
    
    private List<Action> actions=new ArrayList<Action>();
    /**
     * @return the actions
     */
    public List<Action> getActions() {
        return actions;
    }

    private int maxMediaitemCombinations;
    private int mediaItemCount=1;
    private AddMediaitemAction addMediaitemAction;
    private RemoveMediaitemAction removeMediaitemAction;
    private UndoManager undoManager=new UndoManager();
    private UndoAction undoAction;
    private RedoAction redoAction;
    private EditActions editActions;
    private List<PromptItem> promptItems;
    private boolean preRecEnabled;
    private boolean durationEnabled;
    private boolean instructionsEnabled;
    private boolean commentEnabled;
    private boolean postRecEnabled;
    private boolean blockedPropEnabled;
    private boolean beepPropEnabled;
    private boolean finalSilenceEnabled;

    public EditActionsListener getEditActionListener() {
        return editActionsListener;
    }

    public void setEditActionListener(EditActionsListener editActionsListener) {
        this.editActionsListener = editActionsListener;
    }

    public PromptItemUI(URL projectContext, List<PromptPresenterServiceDescriptor> availablePromptPresenters) {
        this(projectContext,null,availablePromptPresenters);
    }

    public PromptItemUI(URL projectContext,PromptItem promptItem, List<PromptPresenterServiceDescriptor> availablePromptPresenters) {
        super(projectContext,availablePromptPresenters);
        
        final ActionListener al = this;
        addMediaitemAction = new AddMediaitemAction(){
            public void actionPerformed(ActionEvent ae) {
                al.actionPerformed(ae);
            }
        };
        actions.add(addMediaitemAction);
        
        removeMediaitemAction = new RemoveMediaitemAction(){
            public void actionPerformed(ActionEvent ae) {
                al.actionPerformed(ae);
            }
        };
        actions.add(removeMediaitemAction);
        
        
        maxMediaitemCombinations = 1;
        
        for(PromptPresenterServiceDescriptor ppsd:availablePromptPresenters){
            String[][] mtcs=ppsd.getSupportedMIMETypes();
            for(String[] mtc:mtcs){
                int mtcLen=mtc.length;
                if(mtcLen>maxMediaitemCombinations){
                    maxMediaitemCombinations=mtcLen;
                }
            }
        }
        addMediaitemAction.setEnabled(maxMediaitemCombinations>1);
        removeMediaitemAction.setEnabled(false);
        
        undoAction = new ipsk.swing.UndoAction() {

            public void actionPerformed(ActionEvent e) {
                al.actionPerformed(e);
            }
        };
        //iMap.put(UndoAction.ACCELERATOR_VAL, UndoAction.NAME);
        //aMap.put(UndoAction.NAME, undoAction);
        redoAction = new ipsk.swing.RedoAction() {
           
            public void actionPerformed(ActionEvent e) {
                al.actionPerformed(e);
            }
        };
        //iMap.put(RedoAction.ACCELERATOR_VAL, RedoAction.NAME);
        //aMap.put(RedoAction.NAME, redoAction);
        undoAction.setEnabled(false);
        redoAction.setEnabled(false);
        
        editActions=new EditActions(null, null, null, undoAction, redoAction);
        
        mediaItemUIs=new ArrayList<MediaitemUI>();
       
        
        //      setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //      addWindowListener(this);
        //      
        isRecording=(promptItem instanceof Recording);
        this.projectContext=projectContext;
        this.promptItem = promptItem;
      
        createWidgets();
        //initializeWidgets(promptItem);

        JTabbedPane itemPanel = makeItemPanel();
        //JPanel buttonPanel = makeButtonPanel();


        setLayout(new BorderLayout());
        add(itemPanel,BorderLayout.CENTER);
        setPromptItem(promptItem);

    }




    /**
     * initializeWidgets creates all required graphical widgets 
     * and sets them to a defined state or geometric size
     * 
     */
    protected void createWidgets() {
    
        recordingCodeWidget = new JTextField(10);
        recordingCodeWidget.setToolTipText("Identification code for the recording. This code must be unique within the recording script.");
        recordingCodeWidgetEditorKitMenu=new EditorKitMenu(recordingCodeWidget,this);
        recordingCodeWidget.addActionListener(this);
        
//        miCountLabel=new JLabel("Media items:");
//        Integer[] mediaItemCountSels=new Integer[maxMediaitemCombinations];
//        for(int i=0;i<maxMediaitemCombinations;i++){
//          mediaItemCountSels[i]=new Integer(i+1);
//        }
//        
//        
        
//        mediaItemCountWidget=new JComboBox(mediaItemCountSels);
//        mediaItemCountWidget.addActionListener(this);
////        addExtraMediaButton=new JButton("Add extra media");
////        addExtraMediaButton.addActionListener(this);
////        removeExtraMediaButton=new JButton("Remove extra media");
////        removeExtraMediaButton.addActionListener(this);
        
        blockedWidget = new JCheckBox("Blocked by prompt");
        blockedWidget.setToolTipText("Select if recording should start after prompt has finished.");
        blockedWidget.setSelected(false);

        preRecDelayNonDefaultWidget=new JCheckBox("Not default");
        preRecDelayNonDefaultWidget.setToolTipText("Select to apply a value different from project default");
        preRecDelayNonDefaultWidget.addActionListener(this);
        preRecDelayWidget = new JTextField(6);
        preRecDelayWidget.setToolTipText("Duration of pre-recording phase in milliseconds.");

        recDurationLimitedWidget=new JCheckBox("Limit");
        recDurationLimitedWidget.setToolTipText("Select to limit recording duration");
        recDurationLimitedWidget.addActionListener(this);

        recDurationWidget = new JTextField(6);
        recDurationWidget.setToolTipText("Recording duration in milliseconds.");

        postRecDelayNonDefaultWidget=new JCheckBox("Not default");
        postRecDelayNonDefaultWidget.setToolTipText("Select to apply a value different from project default");
        postRecDelayNonDefaultWidget.addActionListener(this);
        postRecDelayWidget = new JTextField(6);
        postRecDelayWidget.setToolTipText("Duration of post-recording phase in milliseconds.");

        finalSilenceWidget = new JCheckBox("Silence detection");
        finalSilenceWidget.setToolTipText("Select the checkbox if the recording will by terminated by silence detection");
        finalSilenceWidget.setSelected(false);

        beepPlayWidget = new JCheckBox("Beep");
        beepPlayWidget.setToolTipText("Select the checkbox if a beep should be played before recording.");
        beepPlayWidget.setSelected(false);

        recTypeWidget = new JComboBox(REC_TYPES);
        recTypeWidget.setSelectedIndex(0);

       
        
        promptInstructionsWidget = new JTextField(10);
        promptInstructionsWidgetEditorKitMenu=new EditorKitMenu(promptInstructionsWidget,this);
        promptCommentWidget = new JTextField(10);
        promptCommentWidgetEditorKitMenu=new EditorKitMenu(promptCommentWidget,this);
        
        super.createWidgets();

    }

    private MediaitemUI extraMediaItemUi(int idx){

        int s=mediaItemUIs.size();
        for(int i=s;i<=idx;i++){
            MediaitemUI miUI=new MediaitemUI(projectContext,availablePromptPresenters);
            mediaItemUIs.add(miUI);
        }
        return mediaItemUIs.get(idx);
    }
    
    private void disableAllProperties(){
        instructionsEnabled=false;
        commentEnabled=false;
        preRecEnabled=false;
        durationEnabled=false;
        postRecEnabled=false;
        beepPropEnabled=false;
        blockedPropEnabled=false;
        finalSilenceEnabled=false;
    }
    

    public void setPromptItem(PromptItem pi){
        // apply new prompt item
        // apply values of current UI state
        applyValues();
  
        // remove listener from current item 
        if(promptItem!=null){
            promptItem.removePropertyChangeListener(this);
        }
        // initialize count of media items (will be set later)
        mediaItemCount=1;
        
        // remove media item UIs
        for(MediaitemUI mui:mediaItemUIs){
            if(mui!=null){
                itemPanel.remove(mui);
            }
        }
        // set first media item
        Mediaitem mi=null;
        if(pi!=null){
            mi=pi.getMediaitems().get(0);
        }
        setMediaitem(mi);
        
        // set new prompt item
        promptItem=pi;
       
        
        isRecording=(promptItem instanceof Recording);
        
        // enable UI if we have a prompt item 
        setEnabled(promptItem!=null);
           
        // set data to UI widgets
        initializeWidgets(promptItem);
        
        // determine enable state of each property
        // reset
        disableAllProperties();
        if(promptItem!=null){
         // and set if prompt item, recording respectively 
            instructionsEnabled=true;
            commentEnabled=true;
            preRecEnabled=isRecording;
            durationEnabled=true;
            postRecEnabled=isRecording;
            beepPropEnabled=isRecording;
            blockedPropEnabled=isRecording;
            finalSilenceEnabled=isRecording;
            
            // add listener
            promptItem.addPropertyChangeListener(this);
            
            // add UI tabs for extra media items
            List<Mediaitem> miList=promptItem.getMediaitems();
            mediaItemCount=miList.size();
               for(int i=1;i<mediaItemCount;i++){
                    mi=miList.get(i);
                    MediaitemUI miUI=extraMediaItemUi(i-1);
                    miUI.setMediaitem(mi);
                    itemPanel.addTab("Media "+(i+1), miUI);
                }
        }
        // set UI dependencies
        setDependencies();
    }

    /**
     * initializeWidgets creates all required graphical widgets 
     * and sets them to a defined state, i.e. the prompt item
     * fields.
     * 
     * @param promptItem
     */
    private void initializeWidgets(PromptItem promptItem) {
       
        promptInstructionsWidget.getDocument().removeDocumentListener(this);
        promptCommentWidget.getDocument().removeDocumentListener(this);
       
        Mediaitem mi=null;
        if(promptItem!=null){
           
            Recording recording=null;
            isRecording=(promptItem instanceof Recording);
            if(isRecording){
                recording=(Recording)promptItem;

                recordingCodeWidget.setText(recording.getItemcode());
                blockedWidget.setSelected(recording.getNNBlocked());
              
                Integer preRecDelay=recording.getPrerecdelay();
                boolean preRecDelayNonDefault=(preRecDelay!=null);
                if(preRecDelayNonDefault){
                    preRecDelayWidget.setText(String.valueOf(preRecDelay));
                }
                preRecDelayNonDefaultWidget.setSelected(preRecDelayNonDefault);
                
                Integer recDuration=recording.getRecduration();
                boolean recDurationLimited=(recDuration!=null);
                if(recDurationLimited){
                    recDurationWidget.setText(String.valueOf(recDuration));
                }
                recDurationLimitedWidget.setSelected(recDurationLimited);
                
                
                Integer postRecDelay=recording.getPostrecdelay();
                boolean postRecDelayNonDefault=(postRecDelay!=null);
                if(postRecDelayNonDefault){
                    postRecDelayWidget.setText(String.valueOf(postRecDelay));
                }
                postRecDelayNonDefaultWidget.setSelected(postRecDelayNonDefault);
//                Integer postRecDelay=recording.getNNPostrecdelay();
//
//                postRecDelayWidget.setText(postRecDelay.toString());

                // TODO 
                Integer fs=recording.getFinalsilence();
                boolean fsSel=(fs!=null && fs!=0);
                finalSilenceWidget.setSelected(fsSel);

                beepPlayWidget.setSelected(new Boolean(recording.getBeep()));
                String recType=recording.getRectype();
                if(recType==null)recType=Recording.DEF_RECTYPE;
                recTypeWidget.setSelectedItem(recType);

                Recinstructions ri=recording.getRecinstructions();
                String recInstruction="";
                if (ri!=null)recInstruction=ri.getRecinstructions();
                promptInstructionsWidget.setText(recInstruction);
                Reccomment rc=recording.getReccomment();
                String comment="";
                if (rc!=null)comment=rc.getReccomment();
                promptCommentWidget.setText(comment);
            }else{
                // Nonrecording
                
                Integer duration=((Nonrecording)promptItem).getDuration();
                boolean durationLimited=(duration!=null);
                if(durationLimited){
                    recDurationWidget.setText(String.valueOf(duration));
                }
                recDurationLimitedWidget.setSelected(durationLimited);
            }
            mi=promptItem.getMediaitems().get(0);

        }else{
            recordingCodeWidget.setText("");
         
            promptInstructionsWidget.setText("");
            promptCommentWidget.setText("");
            // TODO reset other widgets
        }
        recordingCodeWidgetEditorKitMenu.discardAllEdits();
        promptInstructionsWidgetEditorKitMenu.discardAllEdits();
        
        promptCommentWidgetEditorKitMenu.discardAllEdits();
       
        instructionsFontManager.applyFontCanDisplay(promptInstructionsWidget);
        descriptionFontManager.applyFontCanDisplay(promptCommentWidget);
        
        promptInstructionsWidget.getDocument().addDocumentListener(this);
        promptCommentWidget.getDocument().addDocumentListener(this);
        
        super.initializeWidgets(mi);
    }


    protected void setDependencies(){

        boolean enabled=isEnabled();
        
        addMediaitemAction.setEnabled(enabled && mediaItemCount<maxMediaitemCombinations);
        removeMediaitemAction.setEnabled(enabled && mediaItemCount>1);
        
        codeLabel.setEnabled(enabled && isRecording && mediaitem!=null);
        recordingCodeWidget.setEnabled(enabled && isRecording && mediaitem!=null);

  
        boolean recDurationLimited=recDurationLimitedWidget.isSelected();
        recDurationWidget.setEnabled(enabled && recDurationLimited && durationEnabled);

        recDurationLabel.setEnabled(enabled && durationEnabled);
        recDurationUnitLabel.setEnabled(enabled && durationEnabled);
        
        preRecLabel.setEnabled(enabled && preRecEnabled);
        preRecUnitLabel.setEnabled(enabled && preRecEnabled);
        postRecLabel.setEnabled(enabled && postRecEnabled);
        postRecUnitLabel.setEnabled(enabled && postRecEnabled);
        recDurationLimitedWidget.setEnabled(enabled && durationEnabled);
        
        boolean preRecNonDefault=preRecDelayNonDefaultWidget.isSelected();
        preRecDelayNonDefaultWidget.setEnabled(enabled && preRecEnabled);
        
        if(!preRecNonDefault){
            preRecDelayWidget.setText(Integer.toString(defaultPreRecording));
        }
        preRecDelayWidget.setEnabled(enabled && preRecEnabled && preRecNonDefault);
        
        boolean postRecNonDefault=postRecDelayNonDefaultWidget.isSelected();
        if(!postRecNonDefault){
            postRecDelayWidget.setText(Integer.toString(defaultPostRecording));
        }
        postRecDelayNonDefaultWidget.setEnabled(enabled && postRecEnabled);
        postRecDelayWidget.setEnabled(enabled && postRecEnabled && postRecNonDefault);

       
        instructionsPanel.setEnabled(enabled && instructionsEnabled);
        promptInstructionsWidget.setEnabled(enabled && instructionsEnabled);
        commentPanel.setEnabled(enabled && commentEnabled);
        promptCommentWidget.setEnabled(enabled && commentEnabled);
       
        beepPlayWidget.setEnabled(enabled && beepPropEnabled);
        blockedWidget.setEnabled(enabled && blockedPropEnabled);
        finalSilenceWidget.setEnabled(enabled && finalSilenceEnabled);
        
//        boolean itemPanelEnabled=multiSelectPropEnabled();
        itemPanel.setEnabled(enabled);
        
        super.setDependencies();
        undoAction.update(undoManager);
        redoAction.update(undoManager);

    }

 

    /**
     * creates a panel with the item code, the values for pre- and postrecording
     * delays and recording duration, and the recording options (silence detection, 
     * beep, recording type)
     * 
     * @return itemPanel
     */
    private JTabbedPane makeItemPanel() {
        itemPanel = new JTabbedPane();

        // Two reasons not to use prompt specific panels:
        // - Embedded (local) HTML and RTF prompts are also possible
        // - User cannot switch between local/remote 

        itemPanel.addTab("Prompt", makeMediaSourcePanel());
        itemPanel.addTab("Control", makeControlPanel());
     
        
        // prompt presenter selector disabled in this version
//        JPanel presentationPanel=makePresentationPanel();
//        itemPanel.addTab("Presentation",presentationPanel);
        itemPanel.setSelectedIndex(0);
        return itemPanel;
    }

    /**
     * Creates a panel for the recording item controls, 
     * i.e. code panel, timing panel and option panel.
     * @return control panel
     */
    private JPanel makeControlPanel() {
        JPanel controlPanel = new JPanel();
        
        GridBagLayout tpL = new GridBagLayout();
        controlPanel.setLayout(tpL);
        GridBagConstraints tpC = new GridBagConstraints();
        
        preRecLabel = new JLabel("Prerecording", JLabel.RIGHT);
        preRecUnitLabel = new JLabel("ms");
        recDurationLabel = new JLabel("Recording", JLabel.RIGHT);
        recDurationUnitLabel =new JLabel("ms");
        postRecLabel = new JLabel("Postrecording", JLabel.RIGHT);
        postRecUnitLabel=new JLabel("ms");

        tpC.fill = GridBagConstraints.HORIZONTAL;
        tpC.insets = new Insets(0,5,5,5);
        tpC.gridx = 0;
        tpC.gridy = 0;
       
        tpC.anchor = GridBagConstraints.NORTHEAST;
        tpL.setConstraints(preRecLabel, tpC);
        
        tpC.gridx++;
        tpC.anchor = GridBagConstraints.NORTHWEST;
        tpL.setConstraints(preRecDelayNonDefaultWidget, tpC);
        
        tpC.gridx++;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(preRecDelayWidget, tpC);
        
        tpC.gridx++;
        tpL.setConstraints(preRecUnitLabel, tpC);

        tpC.gridx = 0;
        tpC.gridy++;
        tpC.anchor = GridBagConstraints.EAST;
        tpL.setConstraints(recDurationLabel, tpC);

        tpC.gridx++;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(recDurationLimitedWidget, tpC);

        tpC.gridx++;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(recDurationWidget, tpC);
        
        tpC.gridx++;
        tpL.setConstraints(recDurationUnitLabel, tpC);
        
        tpC.gridx = 0;
        tpC.gridy++;
        tpC.anchor = GridBagConstraints.EAST;
        tpL.setConstraints(postRecLabel, tpC);
        
        tpC.gridx++;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(postRecDelayNonDefaultWidget, tpC);
        
        tpC.gridx++;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(postRecDelayWidget, tpC);
        
        tpC.gridx++;
        tpL.setConstraints(postRecUnitLabel, tpC);


        controlPanel.add(preRecLabel);
        controlPanel.add(preRecDelayNonDefaultWidget);
        controlPanel.add(preRecDelayWidget);
        controlPanel.add(preRecUnitLabel);
        controlPanel.add(recDurationLabel);
        controlPanel.add(recDurationLimitedWidget);
        controlPanel.add(recDurationWidget);
        controlPanel.add(recDurationUnitLabel);
        controlPanel.add(postRecLabel);
        controlPanel.add(postRecDelayNonDefaultWidget);
        controlPanel.add(postRecDelayWidget);
        controlPanel.add(postRecUnitLabel);
        
        tpC.gridx = 0;
        tpC.gridy ++;
        tpC.gridwidth=3;
        tpC.weightx=0;
        tpC.fill = GridBagConstraints.NONE;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(blockedWidget, tpC);

        tpC.gridy++;

        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(beepPlayWidget, tpC);



        tpC.gridy++;
        //        opC.gridy = 1;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(finalSilenceWidget, tpC);
        //        
        //        opC.gridx = 0;
        //        opC.gridy = 2;
        //        opC.anchor = GridBagConstraints.EAST;
        //        opL.setConstraints(recTypeLabel, opC);
        //        
        //        opC.gridx = 1;
        //        opC.gridy = 2;
        //        opC.anchor = GridBagConstraints.WEST;
        //        opL.setConstraints(recTypeWidget, opC);


        controlPanel.add(blockedWidget);
        controlPanel.add(beepPlayWidget);

        //optionPanel.add(finalSilenceWidget);
        //optionPanel.add(recTypeLabel);
        //optionPanel.add(recTypeWidget);
        //optionPanel.add(box);

        volumeLabel = new JLabel("Volume %", JLabel.RIGHT);


        //        mdpC.gridx = 0;
        //        mdpC.gridy = 0;
        //        mdpC.anchor = GridBagConstraints.EAST;
        //        mdpL.setConstraints(widthLabel, mdpC);
        //        
        //        mdpC.gridx = 1;
        //        mdpC.gridy = 0;
        //        mdpC.anchor = GridBagConstraints.WEST;
        //        mdpL.setConstraints(mediaWidthWidget, mdpC);
        //        
        //        mdpC.gridx = 0;
        //        mdpC.gridy++;
        //        mdpC.anchor = GridBagConstraints.EAST;
        //        mdpL.setConstraints(heightLabel, mdpC);
        //        
        //        mdpC.gridx = 1;
        //        mdpC.anchor = GridBagConstraints.WEST;
        //        mdpL.setConstraints(mediaHeightWidget, mdpC);

        tpC.gridx = 0;
        tpC.gridy++;
        tpC.gridwidth=1;
        //mspC.anchor = GridBagConstraints.EAST;
        tpL.setConstraints(volumeLabel, tpC);

        tpC.gridx++;
        tpC.gridwidth=3;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(mediaVolumeWidget, tpC);

        tpC.gridx = 0;
        tpC.gridy++;
        tpC.gridwidth=1;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(autoPlayNonDefaultWidget, tpC);
        
        tpC.gridx++;
        tpC.gridwidth=1;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(autoPlayWidget, tpC);

        tpC.gridx=0;
        tpC.gridy++;
        tpC.gridwidth=3;
        tpC.anchor = GridBagConstraints.WEST;
        tpL.setConstraints(modalPlayWidget, tpC);

        //        mediaDisplayPanel.add(widthLabel);
        //        mediaDisplayPanel.add(mediaWidthWidget);
        //        mediaDisplayPanel.add(heightLabel);
        //        mediaDisplayPanel.add(mediaHeightWidget);
        controlPanel.add(volumeLabel);
        controlPanel.add(mediaVolumeWidget);
        controlPanel.add(autoPlayNonDefaultWidget);
        controlPanel.add(autoPlayWidget);
        controlPanel.add(modalPlayWidget);
        controlPanel.add(finalSilenceWidget);
        return controlPanel; 
    }



    /**
     * creates a panel for the media source, i.e. URL, alternative text,
     * and MIME-type
     * @return mediaSourcePanel
     */
    private JPanel makeMediaSourcePanel () {
        JPanel mediaSourcePanel = new JPanel();
        GridBagLayout mspL = new GridBagLayout();
        GridBagConstraints mspC = new GridBagConstraints();
        mediaSourcePanel.setLayout(mspL);
        mspC.anchor=GridBagConstraints.WEST;
        
        codeLabel = new JLabel("Code", JLabel.RIGHT);


        srcTypeLabel = new JLabel("Location", JLabel.RIGHT);
        mimeTypeLabel = new JLabel("MIME-Type", JLabel.RIGHT);

        loadButton.addActionListener(this);
        importButton.addActionListener(this);
        
        mediaSourcePanel.add(codeLabel);
        mediaSourcePanel.add(recordingCodeWidget);
//        mediaSourcePanel.add(miCountLabel);
//        mediaSourcePanel.add(mediaItemCountWidget);
//        mediaSourcePanel.add(addExtraMediaButton);
//        mediaSourcePanel.add(removeExtraMediaButton);
        mediaSourcePanel.add(srcTypeLabel);
        //mediaSourcePanel.add(internal);
        mediaSourcePanel.add(external);
        mediaSourcePanel.add(promptSourceWidget);
        mediaSourcePanel.add(loadButton);
        mediaSourcePanel.add(importButton);

        mspC.insets = new Insets(0, 5, 5, 5);

        mspC.gridx=0;
        mspC.gridy=0;

        mspC.anchor = GridBagConstraints.EAST;
        mspL.setConstraints(codeLabel, mspC);

        mspC.gridx++;
        mspC.gridwidth=2;
        mspC.anchor = GridBagConstraints.WEST;
        mspC.weightx=2;
        mspC.fill = GridBagConstraints.HORIZONTAL;
        mspL.setConstraints(recordingCodeWidget, mspC);
        
//        mspC.gridx=0;
//        mspC.gridy++;
//        mspC.gridwidth=1;
//        mspC.weightx=0;
//        mspC.fill = GridBagConstraints.NONE;
//        mspC.anchor = GridBagConstraints.EAST;
//        mspL.setConstraints(miCountLabel, mspC);
//
//        mspC.gridx++;
//        mspC.gridwidth=1;
//        mspC.anchor = GridBagConstraints.WEST;
//        mspL.setConstraints(mediaItemCountWidget, mspC);

        mspC.gridx=0;
        mspC.gridy++;
        mspC.gridwidth=1;
        mspC.weightx=0;
        mspC.fill = GridBagConstraints.NONE;
        mspC.anchor = GridBagConstraints.EAST;
        mspL.setConstraints(mimeTypeLabel, mspC);

        mspC.gridx++;
        mspC.gridwidth=3;
        mspC.anchor = GridBagConstraints.WEST;
        mspL.setConstraints(mimeTypeWidget, mspC);
        
//        mspC.gridx++;
//        mspC.gridwidth=1;
//        mspC.anchor = GridBagConstraints.EAST;
//        mspC.weightx=2;
//        mspC.fill = GridBagConstraints.HORIZONTAL;
//        mspL.setConstraints(addExtraMediaButton, mspC);
//        
//        mspC.gridx++;
//        mspC.gridwidth=1;
//        mspC.anchor = GridBagConstraints.EAST;
//        mspC.weightx=2;
//        mspC.fill = GridBagConstraints.HORIZONTAL;
//        mspL.setConstraints(removeExtraMediaButton, mspC);

        mspC.gridx=0;
        mspC.gridy++;
        mspC.gridwidth=1;
        mspC.anchor = GridBagConstraints.EAST;
        mspL.setConstraints(charsetLabel, mspC);

        mspC.gridx++;
        mspC.anchor = GridBagConstraints.WEST;
        mspC.gridwidth=3;
        mspL.setConstraints(charsetWidget, mspC);

        mspC.gridx = 0;
        mspC.gridy++;
        mspC.gridwidth=1;
        mspC.anchor = GridBagConstraints.EAST;
        mspL.setConstraints(srcTypeLabel, mspC);

        mspC.gridx++;
        mspC.anchor = GridBagConstraints.WEST;
        mspC.gridwidth=2;
        mspL.setConstraints(external, mspC);

        mspC.gridx=0;
        mspC.gridy++;
        mspC.gridwidth=1;
        mspC.anchor = GridBagConstraints.EAST;
        mspL.setConstraints(sourceLabel, mspC);

        mspC.gridx++;
        mspC.gridwidth=1;
        mspC.weightx=2;
        mspC.anchor = GridBagConstraints.WEST;
        mspC.fill = GridBagConstraints.HORIZONTAL;
        mspL.setConstraints(promptSourceWidget, mspC);
        //      mspL.setConstraints(promptSourcePanel, mspC);

        mspC.gridx++;
        mspC.gridwidth=1;
        mspC.weightx=0;
        mspC.anchor = GridBagConstraints.WEST;
        mspC.fill = GridBagConstraints.NONE;
        mspL.setConstraints(loadButton, mspC);

        mspC.gridx++;
        mspC.gridwidth=1;
        mspC.weightx=0;
        mspC.anchor = GridBagConstraints.WEST;
        mspC.fill = GridBagConstraints.NONE;
        mspL.setConstraints(importButton, mspC);

        mspC.gridx = 0;
        mspC.gridy++;
       
        mspC.weightx=0;
        mspC.anchor = GridBagConstraints.EAST;
        mspL.setConstraints(altTextLabel, mspC);

        mspC.gridx++;
        mspC.gridwidth=3;
        mspC.anchor = GridBagConstraints.WEST;
        mspC.weightx=2;
        mspC.fill = GridBagConstraints.HORIZONTAL;
        mspL.setConstraints(altTextWidget, mspC);
        
//        mspC.gridx = 0;
//        mspC.gridy++;
//       
//        mspC.weightx=1;
//        mspC.anchor = GridBagConstraints.EAST;
//        mspL.setConstraints(annotationTemplateLabel, mspC);
//
//        mspC.gridx++;
//        mspC.gridwidth=2;
//        mspC.anchor = GridBagConstraints.WEST;
//        mspC.weightx=2;
//        mspC.fill = GridBagConstraints.HORIZONTAL;
//        mspL.setConstraints(annotationTemplateWidget, mspC);
//        
        

        mediaSourcePanel.add(mimeTypeLabel);
        mediaSourcePanel.add(mimeTypeWidget);
        mediaSourcePanel.add(charsetLabel);
        mediaSourcePanel.add(charsetWidget);
        mediaSourcePanel.add(sourceLabel);
        //      mediaSourcePanel.add(promptSourceWidget);
        //mediaSourcePanel.add(promptSourcePanel);
        mediaSourcePanel.add(altTextLabel);
        mediaSourcePanel.add(altTextWidget);
//        mediaSourcePanel.add(annotationTemplateLabel);
//        mediaSourcePanel.add(annotationTemplateWidget);
//    
        mspC.gridx=0;
        mspC.gridy++;
        mspC.gridwidth=4;
        mspC.anchor = GridBagConstraints.WEST;
        mspC.weightx=2;
        mspC.fill = GridBagConstraints.HORIZONTAL;
        instructionsPanel = new TitledPanel(BorderFactory.createEtchedBorder(),"Instructions");
        instructionsPanel.setLayout(new BorderLayout());
        //      Border etchedBorder = BorderFactory.createEtchedBorder();
        //      Border titledBorder = BorderFactory.createTitledBorder(etchedBorder, "Instructions");
        //      
        //      instructionsPanel.setBorder(titledBorder);
        instructionsPanel.add(promptInstructionsWidget,BorderLayout.CENTER);
        mspL.setConstraints(instructionsPanel, mspC);
        mediaSourcePanel.add(instructionsPanel);
        
        promptTextContentsPanel = new TitledPanel(BorderFactory.createEtchedBorder(),"Prompt");
        //promptTextContentsPanel.setLayout(new BoxLayout(promptTextContentsPanel, BoxLayout.X_AXIS));
        promptTextContentsPanel.setLayout(new GridBagLayout());
        GridBagConstraints ptGbc=new GridBagConstraints();
        ptGbc.gridx=0;
        ptGbc.gridy=0;
        ptGbc.gridwidth=2;
        ptGbc.fill=GridBagConstraints.BOTH;
        ptGbc.weightx=2;
        ptGbc.weighty=2;
//        promptTextContentsPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        //      Border etchedBorder = BorderFactory.createEtchedBorder();
        //promtTextContentTitledBorder = BorderFactory.createTitledBorder(etchedBorder, "Prompt");
        JScrollPane scrollPane = new JScrollPane(promptTextWidget, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        //promptTextContentsPanel.setBorder(promtTextContentTitledBorder);
        promptTextContentsPanel.add(scrollPane,ptGbc);
        ptGbc.gridy++;
        ptGbc.fill=GridBagConstraints.NONE;
        ptGbc.anchor = GridBagConstraints.WEST;
        ptGbc.gridwidth=2;
        ptGbc.weightx=0;
        ptGbc.weighty=0;
//        promptTextContentsPanel.add(annotationTemplateLabel,ptGbc);
//        ptGbc.gridx++;
        promptTextContentsPanel.add(annotationTemplateWidget,ptGbc);
        
        ptGbc.gridy++;
        promptTextContentsPanel.add(localeSelector,ptGbc);
        
        mspC.gridy++;
        mspC.weighty=2;
        mspC.fill = GridBagConstraints.BOTH;
        mspL.setConstraints(promptTextContentsPanel, mspC);
        mediaSourcePanel.add(promptTextContentsPanel);
        
        
        commentPanel = new TitledPanel(BorderFactory.createEtchedBorder(),"Comment");
        commentPanel.setLayout(new BorderLayout());
        commentPanel.add(promptCommentWidget,BorderLayout.CENTER);
        mspC.gridy++;
        mspC.weighty=0;
        mspC.fill = GridBagConstraints.HORIZONTAL;
        mediaSourcePanel.add(commentPanel,mspC);
        
        return mediaSourcePanel;
    }

 
    private JPanel makePresentationPanel(){
        
        JPanel presentationPanel = new JPanel(new BorderLayout());
        promptPresenterSelector=new JServiceSelector<PromptPresenter>();
        GridBagLayout mspL = new GridBagLayout();
        GridBagConstraints mspC = new GridBagConstraints();
//        presentationPanel.setLayout(mspL);
        mspC.anchor=GridBagConstraints.WEST;
       
        mspC.insets = new Insets(5, 5, 5, 5);

        mspC.gridx=0;
        mspC.gridy=0;       
        mspC.weightx=2;
        mspC.weighty=2;
        mspC.fill=GridBagConstraints.BOTH;
        mspC.anchor = GridBagConstraints.EAST;
        //mspL.setConstraints(promptPresenterSelector, mspC);
        presentationPanel.add(promptPresenterSelector,BorderLayout.CENTER);
      
        return presentationPanel;
    }

    /**
     * TODO: why not use one of the promptItemViewers here?
     *
     */
    private void loadPromptContents() {
        URL url = null;
        try {
            url = new URL(promptSourceWidget.getText());
            // 1. if the URL is not available we need a timeout here 
            // 2. Only images are handled here
            //          image = new ImageIcon(url);
            //          imageLabel.setIcon(image);
            //          imageLabel.setText(null);
        } catch (MalformedURLException e) {
            JOptionPane errorMessage = new JOptionPane("Error loading from URL: " + e, JOptionPane.ERROR_MESSAGE);
            errorMessage.setVisible(true);
            //imageLabel.setIcon(null);
            //imageLabel.setText("ERROR: Invalid URL!");
        }
    }
    
    
   
    private void applyBeep(Recording recording){
        String beepStr=null;
        boolean beepSel=beepPlayWidget.isSelected();
        if(beepSel){
            beepStr=Boolean.toString(beepSel);
        }
        recording.setBeep(beepStr);
    }
    
    private void applyPreDelay(Recording recording){
        if(preRecDelayNonDefaultWidget.isSelected()){
            String preRecDelayStr=preRecDelayWidget.getText();
            int preRecDelay=defaultPreRecording;
            try{
                preRecDelay=Integer.parseInt(preRecDelayStr);
            }catch(NumberFormatException nfe){
                // back to default
                preRecDelayWidget.setText(Integer.toString(defaultPreRecording));
            }
            recording.setPrerecdelay(preRecDelay);
        }else{
            recording.setPrerecdelay(null);
        }
    }
    
    private void applyRecDuration(Recording recording){
        if(recDurationLimitedWidget.isSelected()){
            String recDurationStr=recDurationWidget.getText();
            try{
                int recDuration=Integer.parseInt(recDurationStr);
                recording.setRecduration(recDuration);
            }catch(NumberFormatException nfe){
                // no number set to unlimited
                recDurationWidget.setText("");
                recording.setRecduration(null);
            }

        }else{
            recording.setRecduration(null);
        }
    }
    
    private void applyDuration(Nonrecording nonrecording){
        if(recDurationLimitedWidget.isSelected()){
            String durationStr=recDurationWidget.getText();
            try{
                int duration=Integer.parseInt(durationStr);
                nonrecording.setDuration(duration);
            }catch(NumberFormatException nfe){
                // no number set to unlimited
                recDurationWidget.setText("");
                nonrecording.setDuration(null);
            }

        }else{
            nonrecording.setDuration(null);
        }
    }
    
    private void applyPostDelay(Recording recording){
        if(postRecDelayNonDefaultWidget.isSelected()){
            String postRecDelayStr=postRecDelayWidget.getText();
            int postRecDelay=defaultPostRecording;
            try{
                postRecDelay=Integer.parseInt(postRecDelayStr);
            }catch(NumberFormatException nfe){
                // back to default
                postRecDelayWidget.setText(Integer.toString(defaultPostRecording));

            }
            recording.setPostrecdelay(postRecDelay);
        }else{
            recording.setPostrecdelay(null);
        }
    }
    private void applyFinalSilence(Recording recording){
        Integer fs=null;
        if(finalSilenceWidget.isSelected()){
            // TODO Test value 4 seconds
            fs=new Integer(4000);
        }
        recording.setFinalsilence(fs);
    }
    
    private void applyInstructions(Recording recording){
        String recInstrStr=promptInstructionsWidget.getText();
        Recinstructions ri=null;
        if(!recInstrStr.equals("")){
        	ri=recording.getRecinstructions();
            if(ri==null){
            	ri=new Recinstructions();
            }
            ri.setRecinstructions(recInstrStr);
        }
        recording.setRecinstructions(ri);
    }
    
    private void applyComment(Recording recording){
        Reccomment rc=null;
        String recCommentStr=promptCommentWidget.getText();
        if(!recCommentStr.equals("")){
            rc=new Reccomment();
            rc.setReccomment(recCommentStr);
        }
        recording.setReccomment(rc);
    }

    /**
     * 
     * saves the values of the editor fields to a prompt item which 
     * is then saved to the current recording script.
     *
     */
    protected void applyValues() {

        if(promptItem!=null){

            if(promptItem instanceof Recording){
                Recording recording=(Recording)promptItem;

                applyBeep(recording);
                recording.setNNBlocked(blockedWidget.isSelected());
                
                applyPreDelay(recording);
                applyPostDelay(recording);
                applyInstructions(recording);
                applyComment(recording);
                
                recording.setItemcode(recordingCodeWidget.getText());

                applyRecDuration(recording);
                applyFinalSilence(recording);
                recording.setRectype((String) recTypeWidget.getSelectedItem());

            }else if(promptItem instanceof Nonrecording){
               applyDuration((Nonrecording)promptItem);
            }


//            Mediaitem mi=promptItem.getMediaitems().get(0);
            super.applyValues();

            // now extra mediaitems
            int miSize=promptItem.getMediaitems().size();
            for(int i=0;i<miSize-1;i++){
                MediaitemUI miUI=mediaItemUIs.get(i);
                if(miUI==null){
                    JOptionPane.showConfirmDialog(this, "Undefined UI for mediaitem #"+i,"Internal error",JOptionPane.ERROR_MESSAGE);
                }else{
                    miUI.applyValues();
                }
            }
        }else if(promptItems!=null){
            // multiple selection mode
            for(PromptItem pi:promptItems){
                if(pi instanceof Recording){
                    Recording ri=(Recording)pi;
                    if(instructionsEnabled){
                        applyInstructions(ri);
                    }
                    if(commentEnabled){
                        applyComment(ri);
                    }
                    if(preRecEnabled){
                        applyPreDelay(ri);
                    }
                    if(durationEnabled){
                        applyRecDuration(ri);
                    }
                    if(postRecEnabled){
                        applyPostDelay(ri);
                    }
                    if(beepPropEnabled){
                        applyBeep(ri);
                    }
                    if(blockedPropEnabled){
                        ri.setNNBlocked(blockedWidget.isSelected());
                    }
                    if(finalSilenceEnabled){
                        applyFinalSilence(ri);
                    }
                }else if(pi instanceof Nonrecording){
                    if(durationEnabled){
                        Nonrecording nr=(Nonrecording)pi;
                        applyDuration(nr);
                    }
                }
            }
        }
        super.applyValues();
        
    }

    //  /**
    //   * closeOnUserConfirmation()
    //   * 
    //   * if the current prompt item has not been saved, the user is asked 
    //   * to confirm that all edits of the current prompt item will be discarded
    //   *
    //   */
    //  private void closeOnUserConfirmation() {
    //      if (! itemNeedsSaving) {
    //          int userConfirmation = JOptionPane.showConfirmDialog(this, "Discard all edits?", "Close", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    //          if (userConfirmation == JOptionPane.YES_OPTION) {
    //              this.disposeDialog();
    //          }
    //      } else { 
    //          // item has been saved
    //          this.disposeDialog();
    //      }
    //  }

    
    private void changeMediaitemscount(int newMiCount){
        
            List<Mediaitem> miList=promptItem.getMediaitems();
            int miSize=miList.size();

            if (miSize<newMiCount){
                int diffMiCnt=newMiCount-miSize;
                for(int i=0;i<diffMiCnt;i++){
                    Mediaitem newMi=new Mediaitem();
                    miList.add(newMi);
                    int uiIdx=miSize+i-1;
                    MediaitemUI miUi=extraMediaItemUi(uiIdx);
                    itemPanel.insertTab("Media "+uiIdx+2, null, miUi, null, miSize+i+1);
                    miUi.setMediaitem(newMi);
                }
            }else if(miSize>newMiCount){
                int diffMiCnt=miSize-newMiCount;
                for(int i=0;i<diffMiCnt;i++){
                    int uiIdx=miSize-i-2;
                    MediaitemUI miUiToRem=mediaItemUIs.get(uiIdx);
                    itemPanel.remove(miUiToRem);
                    miList.remove(miSize-i-1);
                }
                //              
            }
            mediaItemCount=newMiCount;
            
        
    }
    
  
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        Object src=e.getSource();
        String cmd=e.getActionCommand();
        if(AddMediaitemAction.ACTION_COMMAND.equals(cmd)){
            int newMiCount=mediaItemCount+1;
            
            if(newMiCount<=maxMediaitemCombinations && newMiCount>=1){
                StateEdit addMiStateEdit=new StateEdit(this, "Add media item");
                
                changeMediaitemscount(newMiCount);
                addMiStateEdit.end();
                undoManager.addEdit(addMiStateEdit);
                setDependencies();
                editActionsListener.providesEditActions(this, editActions);
            }
        }else if(RemoveMediaitemAction.ACTION_COMMAND.equals(cmd)){
            int newMiCount=mediaItemCount-1;
            if(newMiCount<=maxMediaitemCombinations && newMiCount>=1){
                StateEdit removeMiStateEdit=new StateEdit(this, "Remove media item");
                changeMediaitemscount(newMiCount);
                removeMiStateEdit.end();
                undoManager.addEdit(removeMiStateEdit);
                setDependencies();
                editActionsListener.providesEditActions(this, editActions);
            }
        }else if(src==recDurationLimitedWidget || src==preRecDelayNonDefaultWidget || src==postRecDelayNonDefaultWidget){
            setDependencies();
        }else if(undoAction.getActionCommand().equals(cmd)){
            undoManager.undo();
        }else if(redoAction.getActionCommand().equals(cmd)){
            undoManager.redo();
        }else{
            if(src==recordingCodeWidget){
                if(promptItem instanceof Recording){
                    Recording r=(Recording)promptItem;
                    r.setItemcode(recordingCodeWidget.getText());
                }
            }
        }
        super.actionPerformed(e);
    }


    private void addMediaItem(int miIndx){
        MediaitemUI newMiUi=new MediaitemUI(projectContext, availablePromptPresenters);
//      mediaItemUIs.add(newMiUi);
        itemPanel.insertTab("Media", null, newMiUi, null, miIndx);
    }

    private void createTree() {
        DefaultMutableTreeNode top =
            new DefaultMutableTreeNode("Recording Script");
        createNodes(top);
        final JTree tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setDragEnabled(true);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                .getLastSelectedPathComponent();

                if (node == null)
                    return;

                Object nodeInfo = node.getUserObject();
//                if (node.isLeaf()) {
//                    System.out.println("Selected element: " + nodeInfo.getClass() + ", " + node.getParent().toString());
//                } else {
//                    System.out.println("Help!");
//                }
            }
        });
        JScrollPane treeView = new JScrollPane(tree);
        JFrame frame = new JFrame("Recording script tree");
        frame.getContentPane().add(treeView);
        frame.pack();
        frame.setVisible(true);
    }

    private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode section = null;
        DefaultMutableTreeNode item = null;

        section = new DefaultMutableTreeNode("Introduction");
        top.add(section);

        item = new DefaultMutableTreeNode("promptItem 1");
        section.add(item);

        section = new DefaultMutableTreeNode("Recording");
        top.add(section);

        item = new DefaultMutableTreeNode("IMAGE\ta small but quite nice photo\tx");
        section.add(item);
        item = new DefaultMutableTreeNode("TEXT\tW O L V E R H A M P T O N");
        section.add(item);
        item = new DefaultMutableTreeNode("TEXT\tBillericay");
        section.add(item);
        item = new DefaultMutableTreeNode("AUDIO\taudio file\t_");
        section.add(item);
    }


    public static void main (String [] args) {
        Recording pi = new Recording();
        Mediaitem mi=pi.getMediaitems().get(0);
        mi.setAlt("Alternative");
        mi.setAutoplay(false);
        //      pi.setBeepPlay(false);
        //      pi.setFinalSilence(false);
        mi.setHeight(100);
        mi.setWidth(100);
        mi.setVolume(50);
        mi.setMimetype("text/plain");
        mi.setModal(false);
        pi.setPostrecdelay(500);
        pi.setPrerecdelay(1000);
        Reccomment rc=new Reccomment();
        rc.setReccomment("Comment");
        pi.setReccomment(rc);
        Recinstructions ri=new Recinstructions();
        ri.setRecinstructions("Instructions");
        pi.setRecinstructions(ri);
        pi.setItemcode("Code");
        try {
            mi.setSrc(new URI("http://www.source.url/a_very_long_filename.html"));
        } catch (URISyntaxException e) {
           e.printStackTrace();
        }
        mi.setText("Prompt text");
        pi.setRecduration(10000);
        pi.setRectype("audio");

        PromptItemUI pie = new PromptItemUI(null,pi,null);

        pie.createTree();
    }

    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        setDependencies();
    }

    public URL getProjectContext() {
        return projectContext;
    }
    public void setProjectContext(URL projectContext) {
        this.projectContext = projectContext;
        setDependencies();
    }

    public void providesEditActions(Object src, EditActions editActions) {
        if(editActionsListener!=null){
            editActionsListener.providesEditActions(src, editActions);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        // reflect changes from promptItemsTable in SectionUI
        Object src=evt.getSource();
        String propertyName=evt.getPropertyName();
        if(src instanceof Recording){
            if("itemcode".equals(propertyName)){
                recordingCodeWidget.setText((String)evt.getNewValue());
            }
        }else if(src instanceof Mediaitem){
            if("text".equals(propertyName)){
                promptTextWidget.getDocument().removeDocumentListener(this);
                promptTextWidget.setText((String)evt.getNewValue());
                promptTextWidget.getDocument().addDocumentListener(this);
            }
        }

    }

    public int getDefaultPreRecording() {
        return defaultPreRecording;
    }

    public void setDefaultPreRecording(int defaultPreRecording) {
        this.defaultPreRecording = defaultPreRecording;
    }

    public int getDefaultPostRecording() {
        return defaultPostRecording;
    }

    public void setDefaultPostRecording(int defaultPostRecording) {
        this.defaultPostRecording = defaultPostRecording;
    }

    public boolean isDefaultPromptAutoPlay() {
        return defaultPromptAutoPlay;
    }

    public void setDefaultPromptAutoPlay(boolean defaultPromptAutoPlay) {
        this.defaultPromptAutoPlay = defaultPromptAutoPlay;
    }

//    public Set<List<String>> getSelectableMIMETypeCombinations() {
//        return selectableMIMETypeCombinations;
//    }

    private String[]  getSelectableMIMETypeCombinationsDisplayList(
            List<PromptPresenterServiceDescriptor> availablePromptPresenters2) {
        Set<List<String>> availMimes=new HashSet<List<String>>();
        for(PromptPresenterServiceDescriptor ppsd:availablePromptPresenters2){
            String[][] ppMimesArrs=ppsd.getSupportedMIMETypes();
            for(String[] ppMimesArr:ppMimesArrs){
                List<String> ppMimesL=Arrays.asList(ppMimesArr);
                availMimes.add(ppMimesL);
            }
        }
    
        String[] singleMIMETypeCombinations;
        if(availMimes.size()>0){
        ArrayList<List<String>> selectableMIMETypeCombinationssorted=new ArrayList<List<String>>(availMimes);
        Collections.sort(selectableMIMETypeCombinationssorted, new MIMETypeSorter());
        // TODO for now the GUI does not support combinations
        
            ArrayList<String> singleMIMETypeCombinationList=new ArrayList<String>();
            for(List<String> mimeTypeCombi:selectableMIMETypeCombinationssorted){
                if(mimeTypeCombi.size()==1){
                    singleMIMETypeCombinationList.add(mimeTypeCombi.get(0));
                }
            }
            singleMIMETypeCombinations=singleMIMETypeCombinationList.toArray(new String[0]);
        }else{
            singleMIMETypeCombinations=MIMETypes.getAllMimeTypes();
        }
        return singleMIMETypeCombinations;
    }
    
    
    private List<PromptPresenterServiceDescriptor> availablePromptPresenters(String[] mimeTypeCombination){

        List<PromptPresenterServiceDescriptor> filteredPPList=new ArrayList<PromptPresenterServiceDescriptor>();
        if(mimeTypeCombination!=null){
            int mimeTypeMemberCount=mimeTypeCombination.length;
            for(PromptPresenterServiceDescriptor ppsd:availablePromptPresenters){
                String[][] suppMIMEspp =ppsd.getSupportedMIMETypes();
                for(String[] mtcmbs:suppMIMEspp){
                    if(mtcmbs!=null && (mtcmbs.length==mimeTypeMemberCount)){
                        boolean add=true;
                        for(int i=0;i<mimeTypeMemberCount;i++){
                            String smime=mtcmbs[i];
                            String rmime=mimeTypeCombination[i];
                            if(rmime==null || ! rmime.equals(smime)){
                                add=false;
                                break;
                            }
                        }
                        if(add){
                            filteredPPList.add(ppsd);
                            break;
                        }
                    }
                }
            }

        }
        return filteredPPList;
    }
    
    
  private void documentUpdate(DocumentEvent e){
//  javax.swing.text.Document d=e.getDocument();
//  if(d.equals(promptTextWidget.getDocument()) && promptItem!=null){
//      Mediaitem mi=promptItem.getMediaitems().get(0);
//      mi.removePropertyChangeListener(this);
//      mi.setText(promptTextWidget.getText());
//      mi.addPropertyChangeListener(this);
//  }
  if(promptItem!=null){
  javax.swing.text.Document d=e.getDocument();
  
   if(d.equals(promptInstructionsWidget.getDocument())){
        instructionsFontManager.applyFontCanDisplay(promptInstructionsWidget);
    }else if(d.equals(promptCommentWidget.getDocument())){
        descriptionFontManager.applyFontCanDisplay(promptCommentWidget);
    }
  }
}
public void changedUpdate(DocumentEvent e) {
 documentUpdate(e);
}

public void insertUpdate(DocumentEvent e) {
  documentUpdate(e);
}

public void removeUpdate(DocumentEvent e) {
  documentUpdate(e);
  
}

/* (non-Javadoc)
 * @see javax.swing.undo.StateEditable#storeState(java.util.Hashtable)
 */
@Override
public void storeState(Hashtable<Object, Object> state) {
    applyValues();
    try {
        PromptItem piClone=(PromptItem) promptItem.clone();
        state.put("promptitem", piClone);
    } catch (CloneNotSupportedException e) {
        
    }
    
}

/* (non-Javadoc)
 * @see javax.swing.undo.StateEditable#restoreState(java.util.Hashtable)
 */
@Override
public void restoreState(Hashtable<?, ?> state) {
    Object piObj=state.get("promptitem");
    PromptItem pi=null;
    if(piObj!=null && piObj instanceof PromptItem){
        pi=(PromptItem)piObj;
    }
    setPromptItem(pi);
}

/**
 * @param descriptionFontFamilies
 */
public void setDescriptionFontFamilies(String[] descriptionFontFamilies) {
    this.descriptionFontFamilies=descriptionFontFamilies;
    descriptionFontManager.setPreferredFontFamilies(descriptionFontFamilies);
}

/**
 * @param instructionsFontFamilies
 */
public void setInstructionsFontFamilies(String[] instructionsFontFamilies) {
    this.instructionsFontFamilies=instructionsFontFamilies;
    instructionsFontManager.setPreferredFontFamilies(instructionsFontFamilies);
}


/**
 * Set a list of prompt items.
 * Call this method if multiple prompt items are selected.
 * Only those UI widgets will be enabled, whose property is equal in all given prompt items.
 * This allows the user to edit properties for multiple prompt items.    
 * @param promptItemsList prompt items list
 */
public void setPromptItems(List<PromptItem> promptItemsList) {
    
    setPromptItem(null);
    
    this.promptItems=promptItemsList;
    setEnabled(this.promptItems!=null && this.promptItems.size()>0);
  
 
    disableAllProperties();
    
    // find common properties to enable
    
    MultiSelectPropertyState<Recinstructions> instrState=new MultiSelectPropertyState<Recinstructions>();
    MultiSelectPropertyState<Reccomment> commentState=new MultiSelectPropertyState<Reccomment>();
 
    MultiSelectPropertyState<Integer> preRecPropState=new MultiSelectPropertyState<Integer>();
    MultiSelectPropertyState<Integer> durPropState=new MultiSelectPropertyState<Integer>();
    MultiSelectPropertyState<Integer> postRecPropState=new MultiSelectPropertyState<Integer>();
    
    MultiSelectPropertyState<Boolean> blockedPropState=new MultiSelectPropertyState<Boolean>();
    
    MultiSelectPropertyState<String> beepPropState=new MultiSelectPropertyState<String>();
    MultiSelectPropertyState<Integer> finalSilencePropState=new MultiSelectPropertyState<Integer>();
    
    List<Mediaitem> mis=new ArrayList<Mediaitem>(promptItemsList.size());
    
    // minimum count of media items
    Integer minMiCount=null;
    
    boolean allAreRecordings=(promptItemsList.size()>0);
    for(PromptItem pi:promptItemsList){

        List<Mediaitem> piMis=pi.getMediaitems();
        int piMiCnt=piMis.size();
        if(minMiCount==null){
            minMiCount=piMiCnt;
        }else{
            if(piMiCnt<minMiCount){
                minMiCount=piMiCnt;
            }
        }
        mis.add(piMis.get(0));
        
        if(pi instanceof Recording){
            Recording r=(Recording)pi;

            Recinstructions piInstructions=r.getRecinstructions();
            instrState.next(piInstructions);
            
            Reccomment piComment=r.getReccomment();
            commentState.next(piComment);
            
            Integer piPreRec=r.getPrerecdelay();
            preRecPropState.next(piPreRec);
            
            Integer piDur=r.getRecduration();
            durPropState.next(piDur);
            
            Integer piPostRec=r.getPostrecdelay();
            postRecPropState.next(piPostRec);
            
            Boolean piBlocked=r.getBlocked();
            blockedPropState.next(piBlocked);
            
            String piBeep=r.getBeep();
            beepPropState.next(piBeep);
            
            Integer piFs=r.getFinalsilence();
            finalSilencePropState.next(piFs);
            
        }else if(pi instanceof Nonrecording){
            allAreRecordings=false;
            Nonrecording nr=(Nonrecording)pi;
            
            instrState.nextNotAvail();
            commentState.nextNotAvail();
            preRecPropState.nextNotAvail();
            
            Integer nrDur=nr.getDuration();
            durPropState.next(nrDur);
            
            postRecPropState.nextNotAvail();
            
        }else{
            // should never be visited
            allAreRecordings=false;
        }
    }
    isRecording=allAreRecordings;
    setMediaitems(mis);
    
    if(minMiCount>1){

        for(int i=1;i<minMiCount;i++){
            List<Mediaitem> msMis=new ArrayList<Mediaitem>();
            // build extra media items matrix
            for(PromptItem pi:promptItemsList){

                Mediaitem mi=pi.getMediaitems().get(i);
                msMis.add(mi);
            }
            MediaitemUI miUI=extraMediaItemUi(i-1);
            miUI.setMediaitems(msMis);
            //       itemPanel.insertTab("Extra media", null, miUI, null, i);
            itemPanel.addTab("Media "+(i+1), miUI);
        }
    }

    preRecEnabled = preRecPropState.allEqual();
    Integer preRec=preRecPropState.getObjectSet();
    if(preRecEnabled){
        if(preRec==null){
            preRecDelayNonDefaultWidget.setSelected(false);
            preRecDelayWidget.setText(Integer.toString(defaultPreRecording));
//            preRecDelayWidget.setEnabled(false);
        }else{
            preRecDelayNonDefaultWidget.setSelected(true);
            preRecDelayWidget.setText(preRec.toString());
//            preRecDelayWidget.setEnabled(true);
        }
    }else{
//        preRecDelayWidget.setEnabled(false);
    }
//    preRecLabel.setEnabled(preRecEnabled);
//    preRecDelayNonDefaultWidget.setEnabled(preRecEnabled);

    durationEnabled = durPropState.allEqual();
    Integer dur=durPropState.getObjectSet();
    if(durationEnabled){
        if(dur==null){
            recDurationLimitedWidget.setSelected(false);
            recDurationWidget.setText("");
//            recDurationWidget.setEnabled(false);
        }else{
            recDurationLimitedWidget.setSelected(true);
            recDurationWidget.setText(dur.toString());
//            recDurationWidget.setEnabled(true);
        }
    }else{
//        recDurationWidget.setEnabled(false);
    }
//    recDurationLabel.setEnabled(durationEnabled);
//    recDurationLimitedWidget.setEnabled(durationEnabled);
    
    postRecEnabled = postRecPropState.allEqual();
    Integer postRec=postRecPropState.getObjectSet();
    if(postRecEnabled){
        if(postRec==null){
            postRecDelayNonDefaultWidget.setSelected(false);
            postRecDelayWidget.setText(Integer.toString(defaultPostRecording));
        }else{
            postRecDelayNonDefaultWidget.setSelected(true);
            postRecDelayWidget.setText(postRec.toString());
        }
    }else{
//        postRecDelayWidget.setEnabled(false);
    }
    
    finalSilenceEnabled=finalSilencePropState.allEqual();
    Integer finalSilence=finalSilencePropState.getObjectSet();
    boolean fsSel=(finalSilence!=null && finalSilence!=0);
    finalSilenceWidget.setSelected(fsSel);
    
//    postRecLabel.setEnabled(postRecEnabled);
//    postRecDelayNonDefaultWidget.setEnabled(postRecEnabled);
    
    blockedPropEnabled=blockedPropState.allEqual();
    if(blockedPropEnabled){
        Boolean blocked=blockedPropState.getObjectSet();
        blockedWidget.setSelected(blocked!=null && blocked);
    }
//    blockedWidget.setEnabled(blockedPropEnabled);
    
    beepPropEnabled=beepPropState.allEqual();
    if(beepPropEnabled){
        String beep=beepPropState.getObjectSet();
        boolean bBeep=Boolean.parseBoolean(beep);
        beepPlayWidget.setSelected(bBeep);
    }
//    beepPlayWidget.setEnabled(beepPropEnabled);
    
    
    
    instructionsEnabled=instrState.allEqual();
//    instructionsPanel.setEnabled(instructionsEnabled);
//    promptInstructionsWidget.setEnabled(instructionsEnabled);
    String instrText="";
    Recinstructions instructions=instrState.getObjectSet();
    if(instructionsEnabled && instructions!=null){
        instrText=instructions.getRecinstructions();
    }
    promptInstructionsWidget.setText(instrText);
    
    commentEnabled = commentState.allEqual();
//    commentPanel.setEnabled(commentEnabled);
//    promptCommentWidget.setEnabled(commentEnabled);
    String commText="";
    Reccomment reccomment=commentState.getObjectSet();
    if(commentEnabled && reccomment!=null){
        commText=reccomment.getReccomment();
    }
    promptCommentWidget.setText(commText);
    
    setDependencies();
    
}


}
