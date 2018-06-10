//    Speechrecorder
//    (c) Copyright 2012
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
import ipsk.db.speech.Mediaitem;
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
public class MediaitemUI extends BaseMediaitemUI implements ActionListener, EditActionsListener, PropertyChangeListener, DocumentListener{

    public MediaitemUI(URL projectContext, List<PromptPresenterServiceDescriptor> availablePromptPresenters) {
     this(projectContext,null,availablePromptPresenters);
    }

    public MediaitemUI(URL projectContext,Mediaitem mediaitem, List<PromptPresenterServiceDescriptor> availablePromptPresenters) {
        super(projectContext,availablePromptPresenters);
        createWidgets();
        JTabbedPane itemPanel = makeItemPanel();
        
        setLayout(new BorderLayout());
        add(itemPanel,BorderLayout.CENTER);
        setMediaitem(mediaitem);
    }

 


  

 


   
 

    /**
     * creates a panel with the item code, the values for pre- and postrecording
     * delays and recording duration, and the recording options (silence detection, 
     * beep, recording type)
     * 
     * @return itemPanel
     */
    private JTabbedPane makeItemPanel() {
        JTabbedPane itemPanel = new JTabbedPane();

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
        
        tpC.fill = GridBagConstraints.HORIZONTAL;
        tpC.insets = new Insets(0,5,5,5);
        tpC.gridx = 0;
        tpC.gridy = 0;
       
        tpC.gridy++;

        tpC.anchor = GridBagConstraints.WEST;

        volumeLabel = new JLabel("Volume %", JLabel.RIGHT);

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
        
  
        srcTypeLabel = new JLabel("Location", JLabel.RIGHT);
        mimeTypeLabel = new JLabel("MIME-Type", JLabel.RIGHT);

        loadButton.addActionListener(this);
        
        importButton.addActionListener(this);
        
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

        mspC.gridx++;
        mspC.gridwidth=3;
        mspC.anchor = GridBagConstraints.WEST;
        mspC.weightx=2;
        mspC.fill = GridBagConstraints.HORIZONTAL;

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
        //		mspL.setConstraints(promptSourcePanel, mspC);

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
        mspC.gridwidth=4;
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
        //		mediaSourcePanel.add(promptSourceWidget);
        //mediaSourcePanel.add(promptSourcePanel);
        mediaSourcePanel.add(altTextLabel);
        mediaSourcePanel.add(altTextWidget);
//        mediaSourcePanel.add(annotationTemplateLabel);
//        mediaSourcePanel.add(annotationTemplateWidget);
//    
        mspC.gridx=0;
     
        
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
            //	        image = new ImageIcon(url);
            //	        imageLabel.setIcon(image);
            //	        imageLabel.setText(null);
        } catch (MalformedURLException e) {
            JOptionPane errorMessage = new JOptionPane("Error loading from URL: " + e, JOptionPane.ERROR_MESSAGE);
            errorMessage.setVisible(true);
            //imageLabel.setIcon(null);
            //imageLabel.setText("ERROR: Invalid URL!");
        }
    }

    /**
     * 
     * saves the values of the editor fields to a prompt item which 
     * is then saved to the current recording script.
     *
     */
    protected void applyValues() {

        if(mediaitem==null) return;
        
        String altText=altTextWidget.getText();
        if (altText.equals("")){
            mediaitem.setAlt(null);
        }else{
            mediaitem.setAlt(altText);
        }
        
//        mi.setNNAutoplay(autoPlayWidget.isSelected());
        if(autoPlayNonDefaultWidget.isSelected()){
            mediaitem.setAutoplay(autoPlayWidget.isSelected());
        }else{
            mediaitem.setAutoplay(null);
        }
        
        mediaitem.setNNModal(modalPlayWidget.isSelected());

        //promptItem.setFinalSilence(finalSilenceWidget.isSelected());
        mediaitem.setNNHeight(Integer.parseInt(mediaHeightWidget.getText()));
        mediaitem.setNNWidth(Integer.parseInt(mediaWidthWidget.getText()));
        Integer vol=(Integer)mediaVolumeWidget.getValue();
        if(vol==100){
            mediaitem.setVolume(null);
        }else{
            mediaitem.setVolume(vol);
        }
        mediaitem.setNNMimetype((String)mimeTypeWidget.getSelectedItem());
        StringSel selCharSet=(StringSel)charsetWidget.getSelectedItem();
        String selCharSetStr=selCharSet.getString();

        mediaitem.setCharSet(selCharSetStr); 
        Locale miLocale=localeSelector.getSelectedLocale();
        if(miLocale==null){
            mediaitem.setLanguageISO639code(null);
            mediaitem.setCountryISO3166code(null);
        }else{
            mediaitem.setLanguageISO639code(miLocale.getLanguage());
            String miCountry=miLocale.getCountry();
            if(!"".equals(miCountry)){
                mediaitem.setCountryISO3166code(miCountry);
            }
        }
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

            File resProjFile=null;
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selFile=chooser.getSelectedFile();
                String relProjPath=JOptionPane.showInputDialog(this,"Please input relative path to store the resource", RESOURCE_PATH+"/"+mimeTypeWidget.getSelectedItem().toString());
                
                
                String relprojResFilePath=relProjPath+"/"+selFile.getName();
                try {
                    URL projResUrl=URLContext.getContextURL(projectContext,relprojResFilePath);
                    URI projResUri=projResUrl.toURI();
                    String projResFilePath= projResUri.getPath();
					
                    resProjFile=new File(projResFilePath);
                    //System.out.println("Copy "+selFile+" to "+resProjFile);
                    if(resProjFile.exists()){
                        // TODO add option: reuse the existing local file
                        int overwriteRes = JOptionPane.showConfirmDialog(this, resProjFile+" already exists.\nDo you want to overwrite?");
                        if(overwriteRes!=JOptionPane.YES_OPTION){
                           return;
                        }
                    }
                    StreamCopy.copy(selFile, resProjFile,true);
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
        
        Mediaitem mi=new Mediaitem();
        mi.setAlt("Alternative");
        mi.setAutoplay(false);
        //		pi.setBeepPlay(false);
        //		pi.setFinalSilence(false);
        mi.setHeight(100);
        mi.setWidth(100);
        mi.setVolume(50);
        mi.setMimetype("text/plain");
        mi.setModal(false);
        
        try {
            mi.setSrc(new URI("http://www.source.url/a_very_long_filename.html"));
        } catch (URISyntaxException e) {
           e.printStackTrace();
        }
        mi.setText("Prompt text");
       
        MediaitemUI pie = new MediaitemUI(null,mi,null);

        pie.createTree();
    }


  
}
