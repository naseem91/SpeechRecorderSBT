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
import ipsk.apps.speechrecorder.UIResources;
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
import ipsk.io.StreamCopy;
import ipsk.net.URLContext;
import ipsk.swing.JServiceSelector;
import ipsk.swing.TitledPanel;
import ipsk.swing.action.EditActions;
import ipsk.swing.action.EditActionsListener;
import ipsk.swing.text.EditorKitMenu;
import ipsk.swing.text.JLocaleSelector;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

/**
 * Displays the contents of a media item. 
 * 
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
public class BaseMediaitemUI extends JPanel implements ActionListener, EditActionsListener, PropertyChangeListener, DocumentListener{

    public final static String RESOURCE_PATH="resources";
    //public final static String [] REC_TYPES = {"Audio", "Video"};
    public final static String [] REC_TYPES = {"Audio"};
    public final static String LOCATION_INTERNAL="Internal";
    public final static String LOCATION_EXTERNAL="External";
    public final static String [] LOCATIONS = {LOCATION_INTERNAL,LOCATION_EXTERNAL};

   
    public final static int ICON_WIDTH = 100;
    public final static int ICON_HEIGHT = 100;

    public final static int ROWS = 5;
    public final static int COLUMNS = 10;

    public static class MIMETypeSorter implements Comparator<List<String>>{
        
        public static String[] majorMIMETypeOrder=new String[]{"text/plain","text/","image/","audio/"};
        private List<String> mimetypeCombination;
       
        private static int getMajorIndex(List<String> mimeCombination){
            int majorIndex=majorMIMETypeOrder.length;
            
            if(mimeCombination.size()>0){
                String firstMimeType=mimeCombination.get(0);
                if(firstMimeType!=null){
                for(int i=0;i<majorMIMETypeOrder.length;i++){
                 if(firstMimeType.startsWith(majorMIMETypeOrder[i])){
                     majorIndex=i;
                     break;
                 }
                }
                }
            }
            return majorIndex;
        }

        public int compare(List<String> arg0, List<String> arg1) {
            int majorIndex0=getMajorIndex(arg0);
            int majorIndex1=getMajorIndex(arg1);
            
            return majorIndex0-majorIndex1;
        }
        
    }
    protected UIResources uiString = null;

    protected Mediaitem mediaitem;
   
   
    protected JComboBox mimeTypeWidget;
    protected JComboBox charsetWidget;
    protected JComboBox external;
    protected JTextField promptSourceWidget;
    private EditorKitMenu promptSourceWidgetEditorKitMenu;

    protected JTextField altTextWidget;
    private EditorKitMenu altTextWidgetEditorKitMenu;

    // dummy widgets for width and height, there is no prompter which uses this properties/attributes
    protected JTextField mediaWidthWidget;
    protected JTextField mediaHeightWidget;
    
    
    protected JSpinner mediaVolumeWidget;
    protected JCheckBox autoPlayNonDefaultWidget;
    protected JCheckBox autoPlayWidget;
    protected JCheckBox modalPlayWidget;

    protected JTextArea promptTextWidget;
    protected AutoFontFamilyManager promptFontManager;
   
    private String[] promptFontFamilies;
   
    /**
     * @return the promptFontFamilies
     */
    public String[] getPromptFontFamilies() {
        return promptFontFamilies;
    }

    /**
     * @param promptFontFamilies the promptFontFamilies to set
     */
    public void setPromptFontFamilies(String[] promptFontFamilies) {
        this.promptFontFamilies = promptFontFamilies;
        promptFontManager.setPreferredFontFamilies(promptFontFamilies);
    }

    private EditorKitMenu promptTextWidgetEditorKitMenu;
    protected JCheckBox annotationTemplateWidget;
   
    
    private JLabel imageLabel;
    private ImageIcon image;
    protected JButton loadButton;
    protected JButton importButton;
    private File lastImportDir=null;
    
    protected JLabel altTextLabel;
    private JLabel annotationTemplateLabel;
    protected JLocaleSelector localeSelector;
    protected JLabel sourceLabel;
    protected TitledPanel promptTextContentsPanel;   
    protected StringSel externalCharset=null;
    protected Vector<StringSel> selectableCharsets;
    protected JLabel charsetLabel;
    protected URL projectContext=null;
    // private TitledBorder promtTextContentTitledBorder;
    protected JLabel srcTypeLabel;
 
    protected JLabel volumeLabel;
   
    protected boolean defaultPromptAutoPlay=true;
    
    protected EditActionsListener editActionsListener;
//    private EditorKitMenu promptItemTextWidgetEditorKitMenu;
    
    protected List<PromptPresenterServiceDescriptor> availablePromptPresenters;
    
    protected JServiceSelector<PromptPresenter> promptPresenterSelector;
    private List<Mediaitem> mediaitems;
    
    // property enable state
    private boolean autoPlayEnabled;
    private boolean volumePropEnabled;
    private boolean modalPropEnabled;
    private boolean annotationTemplatePropEnabled;
    private boolean localePropEnabled;
    
    private boolean allMediaItemsOfTextType;
    protected JLabel mimeTypeLabel;
    private boolean allMediaItemsOfMediaType;
    private boolean mimeTypePropEnabled;

    public EditActionsListener getEditActionListener() {
        return editActionsListener;
    }

    public void setEditActionListener(EditActionsListener editActionsListener) {
        this.editActionsListener = editActionsListener;
    }

 
    public BaseMediaitemUI(URL projectContext, List<PromptPresenterServiceDescriptor> availablePromptPresenters2) {
        super();
        this.availablePromptPresenters=availablePromptPresenters2;
        this.projectContext=projectContext;
        uiString = UIResources.getInstance();
        promptFontManager=new AutoFontFamilyManager();
    }

    protected void createWidgets() {

        String[] singleMIMETypeCombinations = getSelectableMIMETypeCombinationsDisplayList(availablePromptPresenters);
        mimeTypeWidget = new JComboBox(singleMIMETypeCombinations);
        mimeTypeWidget.setAlignmentX(JLabel.LEFT);
        mimeTypeWidget.setToolTipText("The MIME-Type of the content of the URL.");

        charsetLabel = new JLabel("Charset", JLabel.RIGHT);
        // Create a list of available charsets and an additional default item (UTF-8)
        String[] availableCharSets=Charset.availableCharsets().keySet().toArray(new String[0]);
        StringSel defCharset=new StringSel(null,"UTF-8 (Default)",true);
        externalCharset=defCharset;
        selectableCharsets = new Vector<StringSel>(availableCharSets.length+1);
        selectableCharsets.add(defCharset);
        for(int i=0;i<availableCharSets.length;i++){
            selectableCharsets.add(new StringSel(availableCharSets[i],availableCharSets[i],true));
        }
        charsetWidget = new JComboBox(selectableCharsets);
        charsetWidget.setAlignmentX(JLabel.LEFT);
        charsetWidget.setToolTipText("The Charset of the contents of the URL (default: UTF-8).");

        external=new JComboBox(LOCATIONS);
        external.addActionListener(this);
        //promptSrcType=new ButtonGroup();
        //promptSrcType.add(internal);
        //promptSrcType.add(external);


        //if (mi.getSrc() != null) {
        sourceLabel = new JLabel("Source", JLabel.RIGHT);
        promptSourceWidget = new JTextField(8);
        promptSourceWidgetEditorKitMenu=new EditorKitMenu(promptSourceWidget,this);
        promptSourceWidget.addActionListener(this);
       
        promptSourceWidget.setToolTipText("Enter a URL from which to retrieve the prompt contents.");
        imageLabel = new JLabel();
        imageLabel.setAlignmentX(JLabel.CENTER);
        loadButton = new JButton("Browse");
        loadButton.setToolTipText("Select media file");
        
        importButton = new JButton("Import");
        importButton.setToolTipText("Import media file to project resources folder");
        
        altTextLabel = new JLabel("ALT-Text", JLabel.RIGHT);
        altTextWidget = new JTextField(10);
        altTextWidget.setToolTipText("The contents of this field will be shown in the tool tip window.");
        altTextWidgetEditorKitMenu=new EditorKitMenu(altTextWidget,this);
        
        annotationTemplateLabel=new JLabel("Annotation");
        annotationTemplateWidget=new JCheckBox("Use as annotation template");
        annotationTemplateWidget.setToolTipText("Enable if the prompt text can be used for annotation template.\nThe text is the same as the speaker should say.");
        
        localeSelector=new JLocaleSelector();
        localeSelector.setToolTipText("Select language of a text prompt.");
        
        mediaWidthWidget = new JTextField(4);
        mediaWidthWidget.setToolTipText("Enter a number > 0 to set the width of prompt display, or 0 to display the prompt in its original width or scaled to fit. This setting does not affect audio or text prompts.");

        mediaHeightWidget = new JTextField(4);
        mediaHeightWidget.setToolTipText("Enter a number > 0 to set the height of the prompt display, or 0 to display the prompt in its original width or scaled to fit. This setting does not affect audio or text prompts.");

        mediaVolumeWidget = new JSpinner(new SpinnerNumberModel(100,0,1000000,1));
        mediaVolumeWidget.setToolTipText("Enter a value between 0 (silent) and 100 (full volume) for the output volume of the audio or video prompt. This setting does not affect image or text prompts.");

        autoPlayNonDefaultWidget=new JCheckBox("Autoplay not default");
        autoPlayNonDefaultWidget.addActionListener(this);
     
        autoPlayWidget = new JCheckBox("Autoplay");
        autoPlayWidget.setToolTipText("Check this box to start playing the prompt automatically as soon as it is displayed. This setting does not affect text or image prompts.");
        autoPlayWidget.setSelected(false);

        modalPlayWidget = new JCheckBox("Modal");
        modalPlayWidget.setToolTipText("Check this box to prevent the interruption of playback of the current prompt. This setting does not affect text or image prompts.");
        modalPlayWidget.setSelected(false);

        promptTextWidget = new JTextArea();
        promptTextWidget.setRows(ROWS);
        promptTextWidget.setColumns(COLUMNS);
      
        promptTextWidget.setLineWrap(true);
        promptTextWidget.setWrapStyleWord(true);
        promptTextWidgetEditorKitMenu=new EditorKitMenu(promptTextWidget,this);
        

    }


    public void setMediaitem(Mediaitem mi){
        if(mediaitem!=null){
            mediaitem.removePropertyChangeListener(this);
            _applyValues();
        }
        mimeTypePropEnabled=false;
        autoPlayEnabled=false;
        modalPropEnabled=false;
        volumePropEnabled=false;
        annotationTemplatePropEnabled=false;
        localePropEnabled=false;
        
        allMediaItemsOfTextType=false;
        allMediaItemsOfMediaType=false;
     
        mediaitem=mi;
       
        setEnabled(mediaitem!=null);
           
        initializeWidgets(mediaitem);
        if(mediaitem!=null){
            mimeTypePropEnabled=true;
            autoPlayEnabled=true;
            modalPropEnabled=true;
            volumePropEnabled=true;
            annotationTemplatePropEnabled=true;
            localePropEnabled=true;
           
            mediaitem.addPropertyChangeListener(this);

        }
        _setDependencies();
    }

    /**
     * Creates all required graphical widgets for media item
     * and sets them to a defined state, i.e. the prompt item
     * fields.
     * 
     * @param mediaitem media item
     */
    protected void initializeWidgets(Mediaitem mediaitem) {
        promptTextWidget.getDocument().removeDocumentListener(this);
        charsetWidget.removeActionListener(this);
        mimeTypeWidget.removeActionListener(this);
        
        if(mediaitem!=null){
           
           
            String mimetype=mediaitem.getNNMimetype();
            mimeTypeWidget.setSelectedItem(mimetype);


            StringSel selCs=selectableCharsets.get(selectableCharsets.indexOf(new StringSel(mediaitem.getCharSet())));
            charsetWidget.setSelectedItem(selCs);
            
            boolean annotationTemplate=mediaitem.getAnnotationTemplate();
            annotationTemplateWidget.setSelected(annotationTemplate);
            
            String langCode=mediaitem.getLanguageISO639code();
            String countrCode=mediaitem.getCountryISO3166code();
            Locale miLocale=null;
            if(langCode!=null){
               
                if(countrCode==null){
                    miLocale=new Locale(langCode);
                }else{
                    miLocale=new Locale(langCode, countrCode);
                }
            }
            localeSelector.setSelectedLocale(miLocale);
            
            if (mediaitem.getSrc() != null) {
                external.setSelectedItem(LOCATION_EXTERNAL);

                promptSourceWidget.setText(mediaitem.getSrc().toString());

                imageLabel.setAlignmentX(JLabel.CENTER);

                loadPromptContents();
            } else {
                external.setSelectedItem(LOCATION_INTERNAL);
                promptSourceWidget.setText("");

            }

            altTextWidget.setText(mediaitem.getAlt());

            mediaWidthWidget.setText(String.valueOf(mediaitem.getNNWidth()));
            mediaHeightWidget.setText(String.valueOf(mediaitem.getNNHeight()));
            Integer volume=mediaitem.getVolume();
            if(volume==null)volume=100;
            mediaVolumeWidget.setValue(volume);

            //these are attributes that come from the recording section! Hence they 
            //should not be part of the prompt item editor!
            Boolean autoPlay=mediaitem.getAutoplay();
            if(autoPlay!=null){
                autoPlayNonDefaultWidget.setSelected(true);
                autoPlayWidget.setSelected(autoPlay);
            }else{
                autoPlayNonDefaultWidget.setSelected(false);
                autoPlayWidget.setSelected(defaultPromptAutoPlay);
            }
            modalPlayWidget.setSelected(mediaitem.getNNModal());

            promptTextWidget.setText(mediaitem.getText());

        }else{
            promptTextWidget.setText("");
            promptSourceWidget.setText("");
            // TODO reset other widgets
        }
        promptSourceWidgetEditorKitMenu.discardAllEdits();
        altTextWidgetEditorKitMenu.discardAllEdits();
        promptTextWidgetEditorKitMenu.discardAllEdits();
       
        mimeTypeWidget.addActionListener(this);
        charsetWidget.addActionListener(this);
        
        promptTextWidget.getDocument().addDocumentListener(this);
    }
    

    
    protected void setDependencies(){
        _setDependencies();
    }

    protected void _setDependencies(){

        boolean enabled=isEnabled();
        boolean singleItemMode=(mediaitem!=null);
        
        boolean autoPlayNonDefault=autoPlayNonDefaultWidget.isSelected();
        if(!autoPlayNonDefault){
            autoPlayWidget.setSelected(defaultPromptAutoPlay);
        }
        autoPlayWidget.setEnabled(enabled && autoPlayEnabled && autoPlayNonDefault);
        autoPlayNonDefaultWidget.setEnabled(enabled && autoPlayEnabled);
        
        mimeTypeLabel.setEnabled(enabled && mimeTypePropEnabled);
        mimeTypeWidget.setEnabled(enabled && mimeTypePropEnabled);
        boolean fileBasedProject=(projectContext!=null && "file".equalsIgnoreCase(projectContext.getProtocol()));
        String mimeType=(String)mimeTypeWidget.getSelectedItem();
        if(mimeType==null)mimeType="text/plain";
//        List<PromptPresenterServiceDescriptor> avPPs=availablePromptPresenters(new String[]{mimeType});
//        List<Class<? extends PromptPresenter>> avPPsClasses=new ArrayList<Class<? extends PromptPresenter>>();
//        for(PromptPresenterServiceDescriptor avPP:avPPs){
//            avPPsClasses.add(avPP.getClass());
//        }
//        promptPresenterSelector.setPluginClasses(avPPsClasses);
        String mimeMajorType=MIMETypes.getType(mimeType);
        
//        boolean mediaType=(mimeMajorType.startsWith("image") || mimeMajorType.startsWith("audio"));
        boolean mediaType=MIMETypes.isMediaType(mimeType);
        // audio and images can only be external
        external.setEnabled(enabled && mediaitem!=null && !mediaType);
        srcTypeLabel.setEnabled(enabled && mediaitem!=null && !mediaType);
      
        if(mediaType){
            external.setSelectedItem(LOCATION_EXTERNAL);
            promptTextContentsPanel.setTitle("Prompt (description)");
        }else{
            promptTextContentsPanel.setTitle("Prompt");
        }
        boolean containsAudio=mediaType;
        mediaVolumeWidget.setEnabled(enabled && volumePropEnabled && containsAudio);
        volumeLabel.setEnabled(enabled && volumePropEnabled && containsAudio);
       
        boolean isExternal=external.getSelectedItem().equals(LOCATION_EXTERNAL);
        boolean isTextType=(mimeMajorType.equalsIgnoreCase("text"));
        // Charset selection is enabled for external text contents
        boolean charSetSelEnabled=(isTextType && isExternal);
        if (isTextType){
            
            // internal text can only have the charset of the containing XML script: UTF-8
            // remove the listener temporarily because it is not a selection bay the user
            charsetWidget.removeActionListener(this);
            if(isExternal){
                charsetWidget.setSelectedItem(externalCharset);
            }else{

                //charsetWidget.setSelectedItem(new StringSel(Mediaitem.DEF_CHARSET));
                for(StringSel strSel:selectableCharsets){
                    if(strSel.getString()==null){
                        // Set to default (UTF-8)
                        charsetWidget.setSelectedItem(strSel);
                    }
                }


            }
            charsetWidget.addActionListener(this);
        }
        charsetLabel.setEnabled(enabled && charSetSelEnabled);
        charsetWidget.setEnabled(enabled && charSetSelEnabled);
        sourceLabel.setEnabled(enabled && singleItemMode && isExternal);
        promptSourceWidget.setEnabled(enabled && singleItemMode && isExternal);
        loadButton.setEnabled(enabled && singleItemMode && isExternal);
        importButton.setVisible(fileBasedProject);
        importButton.setEnabled(enabled && singleItemMode && isExternal && fileBasedProject);
        
        altTextLabel.setEnabled(enabled && isExternal);
        altTextWidget.setEnabled(enabled && isExternal);
        
        modalPlayWidget.setEnabled(enabled && modalPropEnabled);
        
//        boolean annotationTemplateUsable=isRecording && isTextType;
        boolean annotationTemplateUsable=isTextType;
        annotationTemplateLabel.setEnabled(enabled && ((singleItemMode && annotationTemplateUsable) || allMediaItemsOfTextType) && annotationTemplatePropEnabled);
        annotationTemplateWidget.setEnabled(enabled && annotationTemplateUsable && annotationTemplatePropEnabled);
        if(!annotationTemplateUsable && annotationTemplateWidget.isSelected()){
            annotationTemplateWidget.setSelected(false);
        }
        
        localeSelector.setEnabled(enabled && localePropEnabled);
        
        promptTextWidget.setEnabled(enabled && singleItemMode);
        promptTextContentsPanel.setEnabled(enabled && singleItemMode);
       
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
    
    /**
     * @param mi
     */
    private void applyAutoPlay(Mediaitem mi) {
      if(autoPlayNonDefaultWidget.isSelected()){
          mi.setAutoplay(autoPlayWidget.isSelected());
      }else{
          mi.setAutoplay(null);
      } 
    }
    
    private void applyVolume(Mediaitem mi){
        Integer vol=(Integer)mediaVolumeWidget.getValue();
        if(vol==100){
            mi.setVolume(null);
        }else{
            mi.setVolume(vol);
        }
    }
    
    private void applyLocale(Mediaitem mi){
        Locale miLocale=localeSelector.getSelectedLocale();
        if(miLocale==null){
            mi.setLanguageISO639code(null);
            mi.setCountryISO3166code(null);
        }else{
            mi.setLanguageISO639code(miLocale.getLanguage());
            String miCountry=miLocale.getCountry();
            if(!"".equals(miCountry)){
                mi.setCountryISO3166code(miCountry);
            }else{
                mi.setCountryISO3166code(null);
            }
        }
    }

    protected void applyValues() {
        _applyValues();
    }
    /**
     * 
     * saves the values of the editor fields to a prompt item which 
     * is then saved to the current recording script.
     *
     */
    protected void _applyValues() {

        if(mediaitem!=null){

            String altText=altTextWidget.getText();
            if (altText.equals("")){
                mediaitem.setAlt(null);
            }else{
                mediaitem.setAlt(altText);
            }

            applyAutoPlay(mediaitem);

            mediaitem.setNNModal(modalPlayWidget.isSelected());

            applyVolume(mediaitem);
            
            //promptItem.setFinalSilence(finalSilenceWidget.isSelected());
            mediaitem.setNNHeight(Integer.parseInt(mediaHeightWidget.getText()));
            mediaitem.setNNWidth(Integer.parseInt(mediaWidthWidget.getText()));
           
            mediaitem.setNNMimetype((String)mimeTypeWidget.getSelectedItem());
            StringSel selCharSet=(StringSel)charsetWidget.getSelectedItem();
            String selCharSetStr=selCharSet.getString();

            mediaitem.setCharSet(selCharSetStr); 

            applyLocale(mediaitem);

            mediaitem.setAnnotationTemplate(annotationTemplateWidget.isSelected());
            if(external.getSelectedItem().equals(LOCATION_EXTERNAL)){
                //URL promptSrcURL=null;
                String promptSrcUrlStr=promptSourceWidget.getText();

                URI promptSrcUri=null;
                try {
                    promptSrcUri=new URI(promptSrcUrlStr);
                } catch (URISyntaxException e) {
                    try {
                        promptSrcUri=new URI(null,promptSrcUrlStr,null);
                    } catch (URISyntaxException e1) {

                    }
                }
                mediaitem.setSrc(promptSrcUri);

            }else{
                mediaitem.setSrc(null);
            }
            mediaitem.setText(promptTextWidget.getText());
        }else if(mediaitems!=null){

            // multiple selection mode
            for(Mediaitem mi:mediaitems){
                if(autoPlayEnabled){
                    applyAutoPlay(mi);
                }
                if(modalPropEnabled){
                    mi.setNNModal(modalPlayWidget.isSelected());
                }
                if(volumePropEnabled){
                    applyVolume(mi);
                }
                if(annotationTemplatePropEnabled){
                    mi.setAnnotationTemplate(annotationTemplateWidget.isSelected());
                }
                if(localePropEnabled){
                    applyLocale(mi);
                }
                

            }
        }

    }

   

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        Object src=e.getSource();
        if(src==mimeTypeWidget || src==autoPlayNonDefaultWidget){
            setDependencies();
        }else if(src==charsetWidget){
            externalCharset=(StringSel)charsetWidget.getSelectedItem();
            setDependencies();
        }else if(src==external){
            setDependencies();
        }else if (src == loadButton) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select prompt media file");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selFile=chooser.getSelectedFile();
                URI selFileUri=selFile.toURI();
                
                promptSourceWidget.setText(selFileUri.toString());
            }
        }else if (src == importButton) {

            JFileChooser chooser = new JFileChooser();
            
            chooser.setDialogTitle("Import prompt media file");
            chooser.setApproveButtonText("Import");
            chooser.setApproveButtonToolTipText("Copies file into the project workspace");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            // try to preselect file chooser
            try {
                URL currentSrcUrl=new URL(promptSourceWidget.getText());
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
            if(chooser.getSelectedFile()==null){
                chooser.setCurrentDirectory(lastImportDir);
            }
            File resProjFile=null;
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selFile=chooser.getSelectedFile();
                // save last import dir
                if(selFile!=null){
                    lastImportDir=selFile.getParentFile();
                }
                String relProjPath=JOptionPane.showInputDialog(this,"Please input relative path to store the resource", RESOURCE_PATH+"/"+mimeTypeWidget.getSelectedItem().toString());


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
                    
                    promptSourceWidget.setText(relprojResFilePath);
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
        } 
    }



  



//    private void setDeepEnabled(Container c,boolean b){
//        for(Component cc:c.getComponents()){
//            if(cc instanceof Container){
//                setDeepEnabled((Container)cc,b);
//            }
//            cc.setEnabled(b);
//        }
//    }
    public void setEnabled(boolean enabled){
//      
        super.setEnabled(enabled);
//        setDeepEnabled(this, enabled);
//        if(enabled && !alreadyEnabled){
            setDependencies();
//        }
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
            
        }else if(src instanceof Mediaitem){
            if("text".equals(propertyName)){
                promptTextWidget.getDocument().removeDocumentListener(this);
                promptTextWidget.setText((String)evt.getNewValue());
                promptTextWidget.getDocument().addDocumentListener(this);
            }
        }

    }

    public boolean isDefaultPromptAutoPlay() {
        return defaultPromptAutoPlay;
    }

    public void setDefaultPromptAutoPlay(boolean defaultPromptAutoPlay) {
        this.defaultPromptAutoPlay = defaultPromptAutoPlay;
    }

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
        javax.swing.text.Document d=e.getDocument();
        if(d.equals(promptTextWidget.getDocument())){
            String text=promptTextWidget.getText();
            promptFontManager.applyFontCanDisplay(promptTextWidget);
            if(mediaitem!=null){
                mediaitem.removePropertyChangeListener(this);
                mediaitem.setText(text);
                mediaitem.addPropertyChangeListener(this);
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
    
    public void setMediaitems(List<Mediaitem> mis) {
        setMediaitem(null);
        this.mediaitems=mis;
        
        setEnabled(this.mediaitems!=null && this.mediaitems.size()>0);
        
        // find common properties
        
        MultiSelectPropertyState<String> mimeTypePropState=new MultiSelectPropertyState<String>();
        MultiSelectPropertyState<Boolean> autoPlayPropState=new MultiSelectPropertyState<Boolean>();
        MultiSelectPropertyState<Integer> volumePropState=new MultiSelectPropertyState<Integer>();
        MultiSelectPropertyState<Boolean> modalPropState=new MultiSelectPropertyState<Boolean>();      
        MultiSelectPropertyState<Boolean> annotationTemplPropStare=new MultiSelectPropertyState<Boolean>();
        MultiSelectPropertyState<String> langCodePropState=new MultiSelectPropertyState<String>();
        MultiSelectPropertyState<String> countryCodePropState=new MultiSelectPropertyState<String>();
        
        allMediaItemsOfTextType = true;
        allMediaItemsOfMediaType=true;
        
        for(Mediaitem mi:mis){
            // check if all text or all media (containing audio)
            String mimeType=mi.getNNMimetype();
            mimeTypePropState.next(mimeType);
            
            boolean isTextType=MIMETypes.isTextType(mimeType);
            allMediaItemsOfTextType=allMediaItemsOfTextType && isTextType;
            boolean isMediaType=MIMETypes.isMediaType(mimeType);
            allMediaItemsOfMediaType=allMediaItemsOfMediaType && isMediaType;
            
            Integer miVolume=mi.getVolume();
            volumePropState.next(miVolume);

            Boolean miAutoPlay=mi.getAutoplay();
            autoPlayPropState.next(miAutoPlay);

            Boolean miModal=mi.getModal();
            modalPropState.next(miModal);

            annotationTemplPropStare.next(mi.getAnnotationTemplate());
            langCodePropState.next(mi.getLanguageISO639code());
            countryCodePropState.next(mi.getCountryISO3166code());

        }
        
        // check for all equal MIME types ...
        boolean mimeTypesAllEqual=mimeTypePropState.allEqual();
        if(mimeTypesAllEqual){
            mimeTypeWidget.setSelectedItem(mimeTypePropState.getObjectSet());
        }else{
            mimeTypeWidget.setSelectedIndex(0);
        }
        // ... but do not enable MIMe type editing
        // common change of MIME type for multiple items is not possible
        mimeTypePropEnabled=false;
        
        volumePropEnabled = volumePropState.allEqual() && allMediaItemsOfMediaType;
        
        if(volumePropEnabled){
            Integer v=volumePropState.getObjectSet();
            if(v!=null){
                mediaVolumeWidget.setValue(v);
            }
        }else{
            // set default 100%
            mediaVolumeWidget.setValue(100);
        }
       
        autoPlayEnabled = autoPlayPropState.allEqual();
        Boolean ap=autoPlayPropState.getObjectSet();
        if(autoPlayEnabled){
            if(ap==null){
                autoPlayNonDefaultWidget.setSelected(false);
                autoPlayWidget.setSelected(defaultPromptAutoPlay);
            }else{
                autoPlayNonDefaultWidget.setSelected(true);
                autoPlayWidget.setSelected(ap);
            }
        }
        
        modalPropEnabled=modalPropState.allEqual();
        Boolean modal=modalPropState.getObjectSet();
        boolean modalEff=false;
        if(modal!=null){
            modalEff=modal;
        }
        modalPlayWidget.setSelected(modalEff);
   
        // enable annotation template only if not at least one item is of media type audio or image 
        annotationTemplatePropEnabled=annotationTemplPropStare.allEqual() && allMediaItemsOfTextType;
        localePropEnabled=langCodePropState.allEqual() && countryCodePropState.allEqual();
    }
}
