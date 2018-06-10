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

package ipsk.apps.speechrecorder.script.ui;

import ipsk.apps.speechrecorder.prompting.PromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.script.ItemcodeGenerator;
import ipsk.apps.speechrecorder.script.MetadataTableModel;
import ipsk.apps.speechrecorder.script.SectionsTableModel;
import ipsk.db.speech.Metadata;
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.PromptItemsList;
import ipsk.db.speech.Property;
import ipsk.db.speech.Recording;
import ipsk.db.speech.Script;
import ipsk.db.speech.Section;
import ipsk.swing.CopyAction;
import ipsk.swing.CutAction;
import ipsk.swing.PasteAction;
import ipsk.swing.RedoAction;
import ipsk.swing.UndoAction;
import ipsk.swing.action.EditActions;
import ipsk.swing.action.EditActionsListener;
import ipsk.swing.text.EditorKitMenu;
import ipsk.util.collections.ObservableArrayList;
import ipsk.util.collections.ObservableList;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.undo.StateEdit;
import javax.swing.undo.StateEditable;
import javax.swing.undo.UndoManager;

public class ScriptUI extends JPanel implements ListSelectionListener,
		ActionListener, PropertyChangeListener, ClipboardOwner, FlavorListener,
		TableModelListener, TableColumnModelListener, EditActionsListener, FocusListener ,StateEditable{

    private static final long serialVersionUID = 9051086308204571026L;

    private Script script;

	private JTextField nameField;
	
	private MetadataTableModel metadataTableModel;
	private JPanel metadataPanel;
	private JTable metadataTable;
	
	private JPanel sectionsPanel;

	private SectionsTableModel sectionTableModel;
	
	private JTable sectionsTable;

//	private JPanel actionPanel;

	//private JButton editButton;

//	private JButton submitButton;

	private AddMetadataPropertyAction addMetadataPropertyAction;
	
	private AddSectionAction addSectionAction;
	
	private CutAction cutSectionAction;

	private CopyAction copySectionAction;
	private CopyAction copyMetadataPropertyAction;

	private PasteAction pasteSectionAction;
	private PasteAction pasteMetadataPropertyAction;
	
	private UndoAction undoAction;
	private RedoAction redoAction;

	private Clipboard clipboard = null;

	private JSplitPane splitPane;
	private SectionUI sectionUI;
	

	/**
	 * @param instructionsFontFamilies
	 * @see ipsk.apps.speechrecorder.script.ui.SectionUI#setInstructionsFontFamilies(java.lang.String[])
	 */
	public void setInstructionsFontFamilies(String[] instructionsFontFamilies) {
		sectionUI.setInstructionsFontFamilies(instructionsFontFamilies);
	}

	/**
	 * @param descriptionFontFamilies
	 * @see ipsk.apps.speechrecorder.script.ui.SectionUI#setDescriptionFontFamilies(java.lang.String[])
	 */
	public void setDescriptionFontFamilies(String[] descriptionFontFamilies) {
		sectionUI.setDescriptionFontFamilies(descriptionFontFamilies);
	}

	/**
	 * @param promptFontFamilies
	 * @see ipsk.apps.speechrecorder.script.ui.SectionUI#setPromptFontFamilies(java.lang.String[])
	 */
	public void setPromptFontFamilies(String[] promptFontFamilies) {
		sectionUI.setPromptFontFamilies(promptFontFamilies);
	}

	/**
	 * @return the sectionUI
	 */
	public SectionUI getSectionUI() {
		return sectionUI;
	}

	private JScrollPane scrollPane;

	private boolean marginChange = false;

	private boolean userSized = false;

	private URL projectContext=null;

    /**
	 * @return the projectContext
	 */
	public URL getProjectContext() {
		return projectContext;
	}

	/**
	 * @param projectContext the projectContext to set
	 */
	public void setProjectContext(URL projectContext) {
		this.projectContext = projectContext;
		sectionUI.setProjectContext(this.projectContext);
	}

	private EditActions editSectionActions;
    private EditActions editMetadataPropertyActions;
    private EditActionsListener editActionsListener;
//    private boolean userSelection=true;
    
    private List<Action> newActionsList;
    
    // Default values from project configuration
    // default default mode is manual
    private Section.Mode defaultSectionMode=Section.Mode.MANUAL;
    private int defaultPreRecording;
    private int defaultPostRecording;
    private boolean defaultPromptAutoPlay=true;
//    private ItemcodeGenerator itemcodeGenerator;
    private ObservableList<String> itemCodesList=new ObservableArrayList<String>();
    private UndoManager undoManager=new UndoManager();
    
    private List<PromptPresenterServiceDescriptor> availablePromptPresenters;

    private CutAction cutMetadataPropertyAction;

	private ItemcodeGenerator itemcodeGenerator;
//    public ScriptUI(){
//        this(null);
//    }

	public ScriptUI(URL projectContext,ItemcodeGenerator itemcodeGenerator,List<PromptPresenterServiceDescriptor> promptPresentersClassList) {
		super(new BorderLayout());
		
		this.projectContext=projectContext;
		this.itemcodeGenerator=itemcodeGenerator;
		this.availablePromptPresenters=promptPresentersClassList;
		// sectionTransferHandler=new SectionTransferHandler();
		SecurityManager security = System.getSecurityManager();

		if (security != null) {
			try {
				security.checkSystemClipboardAccess();
			} catch (SecurityException se) {
				System.err.println("WARNING: System clipboard not accessible.");
				clipboard = new Clipboard("Script Clipboard");
			}
		}
		if (clipboard == null) {
			clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		}
		clipboard.addFlavorListener(this);
		  final ActionListener ampal = this;
          addMetadataPropertyAction = new AddMetadataPropertyAction() {
            private static final long serialVersionUID = -7518637055732090023L;

            public void actionPerformed(ActionEvent ae) {
                  ampal.actionPerformed(ae);
              }
          };
          addMetadataPropertyAction.setEnabled(false);
      
		  final ActionListener asal = this;
          addSectionAction = new AddSectionAction() {
           
            private static final long serialVersionUID = -1771803589437890001L;

            public void actionPerformed(ActionEvent ae) {
                  asal.actionPerformed(ae);
              }
          };
          addSectionAction.setEnabled(false);
      
      
		final ActionListener al = this;
		cutSectionAction = new CutAction() {
		 
            private static final long serialVersionUID = 6101617971307295382L;

            public void actionPerformed(ActionEvent ae) {
				al.actionPerformed(ae);
			}
		};
		cutSectionAction.putValue(Action.ACTION_COMMAND_KEY, CutAction.ACTION_COMMAND+"_section");
		
		cutSectionAction.setEnabled(false);
	
        cutMetadataPropertyAction = new CutAction() {
     
            private static final long serialVersionUID = 451031345451371880L;

            public void actionPerformed(ActionEvent ae) {
                al.actionPerformed(ae);
            }
        };
        cutMetadataPropertyAction.putValue(Action.ACTION_COMMAND_KEY, CutAction.ACTION_COMMAND+"_metadata_property");
        cutMetadataPropertyAction.setEnabled(false);
		
		copySectionAction = new CopyAction() {
	
            private static final long serialVersionUID = 8631641300808763372L;

            public void actionPerformed(ActionEvent ae) {
				al.actionPerformed(ae);
			}
		};
		copySectionAction.putValue(Action.ACTION_COMMAND_KEY, CopyAction.ACTION_COMMAND+"_section");
		copySectionAction.setEnabled(false);
		
		copyMetadataPropertyAction = new CopyAction() {
            
            private static final long serialVersionUID = -656609839645719928L;

            public void actionPerformed(ActionEvent ae) {
                al.actionPerformed(ae);
            }
        };
        copyMetadataPropertyAction.putValue(Action.ACTION_COMMAND_KEY, CopyAction.ACTION_COMMAND+"_metadata_property");
        copyMetadataPropertyAction.setEnabled(false);
		
        pasteMetadataPropertyAction = new PasteAction() {
           
            private static final long serialVersionUID = -7806292733670303839L;

            public void actionPerformed(ActionEvent ae) {
                al.actionPerformed(ae);
            }
        };
        pasteMetadataPropertyAction.putValue(Action.ACTION_COMMAND_KEY, PasteAction.ACTION_COMMAND+"_metadata_property");
        pasteMetadataPropertyAction.setEnabled(false);
        
        pasteSectionAction = new PasteAction() {
            private static final long serialVersionUID = 7446884452860757692L;

            public void actionPerformed(ActionEvent ae) {
				al.actionPerformed(ae);
			}
		};
		pasteSectionAction.putValue(Action.ACTION_COMMAND_KEY, PasteAction.ACTION_COMMAND+"_section");
		pasteSectionAction.setEnabled(false);
		
		undoAction = new ipsk.swing.UndoAction() {
            private static final long serialVersionUID = -8261733834310117974L;

            public void actionPerformed(ActionEvent e) {
				al.actionPerformed(e);
			}
		};
		//iMap.put(UndoAction.ACCELERATOR_VAL, UndoAction.NAME);
		//aMap.put(UndoAction.NAME, undoAction);
		redoAction = new ipsk.swing.RedoAction() {
            private static final long serialVersionUID = 8317910445407384268L;

            public void actionPerformed(ActionEvent e) {
				al.actionPerformed(e);
			}
		};
		//iMap.put(RedoAction.ACCELERATOR_VAL, RedoAction.NAME);
		//aMap.put(RedoAction.NAME, redoAction);
		undoAction.setEnabled(false);
		redoAction.setEnabled(false);
		
		
		JPanel scriptPanel=new JPanel(new GridBagLayout());
//		setLayout(new GridBagLayout());
		// this.script=script;
		GridBagConstraints c = new GridBagConstraints();
		
		c.insets = new Insets(2, 5, 2, 5);
//		c.anchor = GridBagConstraints.PAGE_START;
		c.anchor=GridBagConstraints.NORTHWEST;
		
		
		c.gridx = 0;
		c.gridy = 0;
		JLabel nameLabel = new JLabel("Name (Id):");
		scriptPanel.add(nameLabel, c);
		nameField = new JTextField(8);
		
        EditorKitMenu nameFieldEkm=new EditorKitMenu(nameField);
        nameFieldEkm.setPopupMenuActiv(true);
		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;

		c.weightx = 2;
		scriptPanel.add(nameField, c);
		
		metadataTableModel=new MetadataTableModel();
        metadataTable = new JTable(metadataTableModel);
        metadataTable
                .setPreferredScrollableViewportSize(new Dimension(150, 20));
        metadataTable.setDragEnabled(true);
        metadataTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        metadataTable.getSelectionModel().addListSelectionListener(this);
        metadataTableModel.addTableModelListener(this);
//        metadataTable.getColumnModel().addColumnModelListener(this);
        metadataTable.addFocusListener(this);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy++;
        c.weightx = 2.0;
        c.weighty = 2.0;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane metaDataScrollPane = new JScrollPane(metadataTable);
        metadataPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mc = new GridBagConstraints();
        mc.gridx = 0;
        mc.gridy = 0;
        mc.weightx = 1.0;
        mc.weighty = 1.0;
        mc.fill = GridBagConstraints.BOTH;
        metadataPanel.setBorder(BorderFactory.createTitledBorder("Metadata"));
        metadataPanel.add(metaDataScrollPane, mc);
        scriptPanel.add(metadataPanel, c);
        
		sectionTableModel = new SectionsTableModel();
		sectionsTable = new JTable(sectionTableModel);
		//sectionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		//sectionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		//sectionsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sectionsTable
				.setPreferredScrollableViewportSize(new Dimension(150, 100));
		sectionsTable.setDragEnabled(true);
		sectionsTable.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		sectionsTable.getSelectionModel().addListSelectionListener(this);
		sectionTableModel.addTableModelListener(this);
        sectionsTable.getColumnModel().addColumnModelListener(this);
        sectionsTable.addFocusListener(this);
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy++;
		c.weightx = 2.0;
		c.weighty = 4.0;
		c.fill = GridBagConstraints.BOTH;
		scrollPane = new JScrollPane(sectionsTable);
		// scrollPane.setPreferredSize(new Dimension(500, 300));
		sectionsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints sc = new GridBagConstraints();
		sc.gridx = 0;
		sc.gridy = 0;
		sc.weightx = 1.0;
		sc.weighty = 1.0;
		sc.fill = GridBagConstraints.BOTH;
		sectionsPanel.setBorder(BorderFactory.createTitledBorder("Sections"));
		sectionsPanel.add(scrollPane, sc);
//		System.out.println("Sections: "+sectionsTable.getPreferredScrollableViewportSize()+" "+sectionsTable.getPreferredSize());
	
		scriptPanel.add(sectionsPanel, c);
		
		ItemcodeGeneratorUI itemCodeGenUI=new ItemcodeGeneratorUI(itemcodeGenerator);
		Border tbicg=BorderFactory.createTitledBorder("Item code generator");
        itemCodeGenUI.setBorder(tbicg);
		c.gridy++;
		scriptPanel.add(itemCodeGenUI,c);
		
		
		editMetadataPropertyActions=new EditActions(cutMetadataPropertyAction,copyMetadataPropertyAction,pasteMetadataPropertyAction);
		editSectionActions=new EditActions(cutSectionAction,copySectionAction,pasteSectionAction,undoAction,redoAction);
		updateEditActions();
		
		InputMap metadataImap = metadataTable.getInputMap();
		metadataImap.put(KeyStroke.getKeyStroke("ctrl X"), cutMetadataPropertyAction
                .getValue(Action.ACTION_COMMAND_KEY));
		metadataImap.put(KeyStroke.getKeyStroke("ctrl C"), copyMetadataPropertyAction
                .getValue(Action.NAME));
		metadataImap.put(KeyStroke.getKeyStroke("ctrl V"), pasteMetadataPropertyAction
                .getValue(Action.NAME));
        ActionMap metadataMap = metadataTable.getActionMap();
        metadataMap.put(cutMetadataPropertyAction.getValue(Action.ACTION_COMMAND_KEY), cutMetadataPropertyAction);
        metadataMap.put(copyMetadataPropertyAction.getValue(Action.NAME), copyMetadataPropertyAction);
        metadataMap.put(pasteMetadataPropertyAction.getValue(Action.NAME), pasteMetadataPropertyAction);
        
		InputMap imap = sectionsTable.getInputMap();
		imap.put(KeyStroke.getKeyStroke("ctrl X"), cutSectionAction.getActionCommand());
		imap.put(KeyStroke.getKeyStroke("ctrl C"), copySectionAction.getActionCommand());
		imap.put(KeyStroke.getKeyStroke("ctrl V"), pasteSectionAction.getActionCommand());
		ActionMap map = sectionsTable.getActionMap();
		map.put(cutSectionAction.getActionCommand(), cutSectionAction);
		map.put(copySectionAction.getActionCommand(), copySectionAction);
		map.put(pasteSectionAction.getActionCommand(), pasteSectionAction);
		
		sectionUI=new SectionUI(projectContext,itemCodesList,itemcodeGenerator,availablePromptPresenters);
		
//        c.gridx=2;
//        c.gridy=0;
//        c.gridwidth=1;
//        c.gridheight=2;
//        c.weightx=2.0;
//        c.weighty=2.0;
//        c.fill = GridBagConstraints.BOTH;
//        //c.weightx = 0.5;
//        add(sectionUI,c);
        
		scriptPanel.setMinimumSize(new Dimension(0,0));
		sectionUI.setMinimumSize(new Dimension(0,0));
		splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,scriptPanel,sectionUI);
		add(splitPane,BorderLayout.CENTER);
		
		
		
		sectionUI.setEditActionListener(this);
		
		newActionsList=new ArrayList<Action>();
		newActionsList.add(addMetadataPropertyAction);
		newActionsList.add(addSectionAction);
//		newActionsList.addAll(sectionUI.getNewActionsList());
		
	}

	public ScriptUI() {
       this(null,new ItemcodeGenerator(),null);
    }

	private void setTableSections(Section[] tableSections){
		sectionTableModel.setSections(tableSections);
		if(tableSections!=null && tableSections.length>0){
		    // set first row selected by default
		sectionsTable.setRowSelectionInterval(0, 0);
		}else{
		    sectionUI.setSection(null);
		}
	}
    private void setSectionsCopy() {
    	Section[] sectionsCopy = null;
    	if(script!=null){
		List<Section> sections = script.getSections();
        if (sections!=null){
            sectionsCopy=new Section[sections.size()];
        
		for (int i = 0; i < sections.size(); i++) {
			sectionsCopy[i] = sections.get(i);
		}
        }
    	}
		setTableSections(sectionsCopy);
		
	}

	public void setScript(Script script) {
		this.script = script;
		if(script!=null){
		    nameField.setText(script.getName());
		    Metadata md=script.getMetadata();
		    if(md==null){
		        metadataTableModel.getMetadataProperties().clear();
		    }else{
		        metadataTableModel.setMetadataProperties(md.getProperties());
		    }
		}
//		userSelection=false;
		
		setSectionsCopy();
//		userSelection=true;
		
		itemCodesList.clear();
		// copy item codes set
		if(script!=null){
		    Set<String> sics=script.itemCodesSet();
		    itemCodesList.addAll(sics);
		}
		undoManager.discardAllEdits();
		updateEditActions();
	}


	public void valueChanged(ListSelectionEvent e) {
	    boolean adjusting=e.getValueIsAdjusting();
	    if(adjusting){
	        return;
	    }else{
	        updateEditActions();
	        boolean editorEnabled = super.isEnabled();
	        Object src=e.getSource();
	        
	        if(src==sectionsTable.getSelectionModel()){
	            ListSelectionModel lsm=sectionsTable.getSelectionModel();
	            int startIndex=lsm.getMinSelectionIndex();
	            int stopIndex=lsm.getMaxSelectionIndex();

	            if(startIndex>=0){
	                if(startIndex==stopIndex){
	                    Section currSection=sectionTableModel.getSections()[startIndex];
	                    sectionUI.setSection(currSection);
	                }else{
	                    sectionUI.setSection(null);
	                }
	            }
	          
	            if (lsm.isSelectionEmpty()) {
	                //editButton.setEnabled(false);
	                copySectionAction.setEnabled(false);
	                cutSectionAction.setEnabled(false);
	            } else {
	                //editButton.setEnabled(editorEnabled);
	                copySectionAction.setEnabled(editorEnabled);
	                cutSectionAction.setEnabled(editorEnabled);
	                if(editActionsListener!=null){
	                    editActionsListener.providesEditActions(this, editSectionActions);
	                }
	            }
	        }else if(src==metadataTable.getSelectionModel()){
	            ListSelectionModel lsm=metadataTable.getSelectionModel();
              
                if (lsm.isSelectionEmpty()) {
                    //editButton.setEnabled(false);
                    copyMetadataPropertyAction.setEnabled(false);
                    cutMetadataPropertyAction.setEnabled(false);
                } else {
                    //editButton.setEnabled(editorEnabled);
                    copyMetadataPropertyAction.setEnabled(editorEnabled);
                    cutMetadataPropertyAction.setEnabled(editorEnabled);
                    if(editActionsListener!=null){
                        editActionsListener.providesEditActions(this, editMetadataPropertyActions);
                    }
                }
	        }
	    }
	}

//	private void editSelectedSection() {
//		ListSelectionModel selModel = sectionsTable.getSelectionModel();
//		int sel = selModel.getMinSelectionIndex();
//
//		if (sectionUI == null) {
//			sectionUI = new SectionUI(projectContext);
//        }
//		
//		sectionUI.setSection(sectionTableModel.getSections()[sel]);
//       
//	}
    
    

	protected void applyValues(){
	    sectionUI.applyValues();
	    script.setName(nameField.getText());
	   
        TableCellEditor cellEditor=metadataTable.getCellEditor();
        if(cellEditor!=null){
            cellEditor.stopCellEditing();
        }
	    List<Property> mdps=metadataTableModel.getMetadataProperties();
	    Metadata md=script.getMetadata();
	    if(mdps.size()>0){
	        if(md==null){
	            md=new Metadata();
	        }
	        md.setProperties(mdps);
	    }else{
	        md=null;
	    }
	    script.setMetadata(md);
	    
	    cellEditor=sectionsTable.getCellEditor();
        if(cellEditor!=null){
            cellEditor.stopCellEditing();
        }
        
        // Arrays.asList returns fixed-size list!
	    List<Section> sectionsList=new ArrayList<Section>(Arrays.asList(sectionTableModel.getSections()));
	   
	    script.setSections(sectionsList);
	    script.updatePositions();
	}

	public void actionPerformed(ActionEvent e) {
		
//		Object src = e.getSource();
//		if (src == okButton) {
//			if (sectionUI != null) {
//				sectionUI.disposeDialog();
//				sectionUI = null;
//			}
//			script.setName(nameField.getText());
//			script.setSections(sectionTableModel.getSections());
//
//		} else if (src == applyButton) {
//			script.setName(nameField.getText());
//			script.setSections(sectionTableModel.getSections());
//		} else if (src == cancelButton) {
//			if (sectionUI != null) {
//				sectionUI.disposeDialog();
//				sectionUI = null;
//			}
//		} else 
//        if (src == editButton) {
//			// if(sectionUI!=null)sectionUI.disposeDialog();
//			editSelectedSection();
//		} else {
			String cmd = e.getActionCommand();
			if (copyMetadataPropertyAction.getActionCommand().equals(cmd)) {
//                Section copySection = getSelectedSection();
//                clipboard.setContents(copySection, this);

            }else if (copySectionAction.getActionCommand().equals(cmd)) {
				Section copySection = getSelectedSection();
				clipboard.setContents(copySection, this);

			} else if (pasteMetadataPropertyAction.getActionCommand().equals(cmd)) {
               
                try {
                    insert((Property) (clipboard.getContents(this)
                            .getTransferData(Property.CLASS_DATA_FLAVOR)));
                } catch (UnsupportedFlavorException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }else if (pasteSectionAction.getActionCommand().equals(cmd)) {
            	DataFlavor[] avDfs=clipboard.getAvailableDataFlavors();
            	
            		try {
            			for(DataFlavor df:avDfs){
            				if(Section.CLASS_DATA_FLAVOR.equals(df)){
            				insert((Section) (clipboard.getContents(this)
            						.getTransferData(Section.CLASS_DATA_FLAVOR)));
            				}else if(PromptItemsList.CLASS_DATA_FLAVOR.equals(df)){
            					PromptItemsList pilD=(PromptItemsList)(clipboard.getContents(this).getTransferData(PromptItemsList.CLASS_DATA_FLAVOR));
            					if(pilD!=null){
            						sectionUI.insert(pilD);
            					}
            				}else if(PromptItem.CLASS_DATA_FLAVOR.equals(df)){
                				sectionUI.insert((PromptItem) (clipboard.getContents(this)
                						.getTransferData(PromptItem.CLASS_DATA_FLAVOR)));
                				}
            			}
            		} catch (UnsupportedFlavorException e1) {
            			// TODO Auto-generated catch block
            			e1.printStackTrace();
            		} catch (IOException e1) {
            			// TODO Auto-generated catch block
            			e1.printStackTrace();
            		}
            } else if (cutMetadataPropertyAction.getActionCommand().equals(cmd)) {
                clipboard.setContents(getSelectedMetadataProperty(), this);
                removeSelectedMetadataProperty();

            } else if (cutSectionAction.getActionCommand().equals(cmd)) {
                clipboard.setContents(getSelectedSection(), this);
                removeSelectedSection();

            } else if (addMetadataPropertyAction.getActionCommand().equals(cmd)) {
			    Property newProperty=new Property();
               
                insert(newProperty);
            }else if (addSectionAction.getActionCommand().equals(cmd)) {
			    Section nSection=new Section();
			    nSection.setPropertyChangeSupportEnabled(true);
				insert(nSection);
			}else if(undoAction.getActionCommand().equals(cmd)){
			    undoManager.undo();
			}else if(redoAction.getActionCommand().equals(cmd)){
                undoManager.redo();
            }
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
//		System.out.println("Prop: " + evt.getPropertyName() + ": "
//				+ evt.getOldValue() + " -> " + evt.getNewValue());
		Object src=evt.getSource();
		if(src instanceof Script){
		if (evt.getPropertyName().equals("name")) {
		    nameField.setText(script.getName());
		}
//		}else if (evt.getPropertyName().equals("sections")) {
//			setSectionsCopy();
//		}
		}
	}
	public void removeSelectedMetadataProperty() {
//        StateEdit removeStateEdit=new StateEdit(this,"Remove metadata property");
	    TableCellEditor cellEditor=metadataTable.getCellEditor();
        if(cellEditor!=null){
            cellEditor.stopCellEditing();
        }
        List<Property> props=metadataTableModel.getMetadataProperties();
        int selRow=metadataTable.getSelectedRow();
        if(selRow>=0){
            props.remove(selRow);
           int newCount=props.size();
           metadataTableModel.fireTableDataChanged();
           if(newCount==0){
               script.setMetadata(null);
               metadataTable.clearSelection();
           }else{
               int newSelRow=selRow-1;
               if(newSelRow<0)newSelRow=0;
               metadataTable.setRowSelectionInterval(newSelRow, newSelRow);
           }
        }
//        removeStateEdit.end();
//        undoManager.addEdit(removeStateEdit);
        updateEditActions();
        
    }
	public void removeSelectedSection() {
		StateEdit removeStateEdit=new StateEdit(this,"Remove section");
		TableCellEditor cellEditor=sectionsTable.getCellEditor();
        if(cellEditor!=null){
            cellEditor.stopCellEditing();
        }
        // we need itemcodes of removed section, so we have to apply the section first
        sectionUI.applyValues();
		ArrayList<Section> newSections = new ArrayList<Section>(Arrays
				.asList(sectionTableModel.getSections()));
		int selRow=sectionsTable.getSelectedRow();
		if(selRow>=0){
		    
		    Section removedSect=newSections.remove(selRow);
		    if(removedSect!=null){
		        List<String> removedIcs=removedSect.itemCodesList();
		        //itemCodesList.removeAll(removedIcs);
		        // removeAll seems to remove duplicates
		        for(String icToRemove:removedIcs){
		        	// Javadoc: removes first occurence
		        	itemCodesList.remove(icToRemove);
		        }
		    }
		   int newSectionCount=newSections.size();
		   sectionTableModel.setSections(newSections.toArray(new Section[0]));
		   if(newSectionCount==0){
		       sectionUI.setSection(null);
		       sectionsTable.clearSelection();
		   }else{
		       int newSelRow=selRow-1;
		       if(newSelRow<0)newSelRow=0;
		       sectionsTable.setRowSelectionInterval(newSelRow, newSelRow);
		   }
		}
		removeStateEdit.end();
		undoManager.addEdit(removeStateEdit);
		updateEditActions();
		
	}
	public Property getSelectedMetadataProperty() {
	    int selRow=metadataTable.getSelectedRow();
        List<Property> mdps=metadataTableModel.getMetadataProperties();
        return mdps.get(selRow);
    }
	
	public Section getSelectedSection() {
		return sectionTableModel.getSections()[sectionsTable.getSelectedRow()];
	}

	// public TransferHandler getTransferHandler(){
	// return sectionTransferHandler;
	// }
	
	public void insert(Property property) {
//        StateEdit insertStateEdit=new StateEdit(this,"Insert metadata property");
	    TableCellEditor cellEditor=metadataTable.getCellEditor();
        if(cellEditor!=null){
            cellEditor.stopCellEditing();
        }
        List<Property> newProperties =metadataTableModel.getMetadataProperties();
        ListSelectionModel selModel = metadataTable.getSelectionModel();
        int insertIndex = 0;
        if (!selModel.isSelectionEmpty()) {
            insertIndex = metadataTable.getSelectedRow()+1;
        }
        newProperties.add(insertIndex, property);
        metadataTableModel.fireTableDataChanged();
        metadataTable.setRowSelectionInterval(insertIndex, insertIndex);
//        insertStateEdit.end();
//        undoManager.addEdit(insertStateEdit);
        updateEditActions();
    }
	
	public void insert(Section section) {
	    section.setScript(script);
		StateEdit insertStateEdit=new StateEdit(this,"Insert section");
		TableCellEditor cellEditor=sectionsTable.getCellEditor();
        if(cellEditor!=null){
            cellEditor.stopCellEditing();
        }
		ArrayList<Section> newSections = new ArrayList<Section>(Arrays
				.asList(sectionTableModel.getSections()));

		ListSelectionModel selModel = sectionsTable.getSelectionModel();
		int insertIndex = 0;
		if (!selModel.isSelectionEmpty()) {

			insertIndex = sectionsTable.getSelectedRow()+1;
		}


		List<PromptItem> pis=section.getPromptItems();
		for(PromptItem pi:pis){
			if(pi instanceof Recording){
				Recording recording=(Recording)pi;
				String itemcode=recording.getItemcode();

				if(itemcodeGenerator.getConfig().isActive() && (itemcode==null || "".equals(itemcode) || (itemCodesList.contains(itemcode)))){
					// auto generate new item code
					HashSet<String> ics=new HashSet<String>(itemCodesList);

					itemcodeGenerator.toNext(ics);
					String icGen=itemcodeGenerator.getItemCode();
					recording.setItemcode(icGen);
				}
				itemCodesList.add(recording.getItemcode());
			}
		}

		newSections.add(insertIndex, section);
		sectionTableModel.setSections(newSections.toArray(new Section[0]));
		sectionsTable.setRowSelectionInterval(insertIndex, insertIndex);
		insertStateEdit.end();
		undoManager.addEdit(insertStateEdit);
		updateEditActions();
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// System.out.println("lostOwnerShip");
	}

	private void updateEditActions() {
		boolean editorEnabled = super.isEnabled();
		boolean clipBoardSectionDataAvail = false;
		boolean clipBoardMetadataPropertyAvail = false;
		try {
            clipBoardMetadataPropertyAvail = clipboard
                    .isDataFlavorAvailable(Property.CLASS_DATA_FLAVOR);
        } catch (IllegalStateException ise) {
            // Accessed by another application
        }
        pasteMetadataPropertyAction.setEnabled(editorEnabled && clipBoardMetadataPropertyAvail);
		try {
			clipBoardSectionDataAvail = clipboard.isDataFlavorAvailable(Section.CLASS_DATA_FLAVOR) || 
					clipboard.isDataFlavorAvailable(PromptItem.CLASS_DATA_FLAVOR) || 
					clipboard.isDataFlavorAvailable(PromptItemsList.CLASS_DATA_FLAVOR);
		} catch (IllegalStateException ise) {
			// Accessed by another application
		}
		pasteSectionAction.setEnabled(editorEnabled && clipBoardSectionDataAvail);
		
		addMetadataPropertyAction.setEnabled(editorEnabled);
		addSectionAction.setEnabled(editorEnabled);
		undoAction.update(undoManager);
		redoAction.update(undoManager);
	}

	public void flavorsChanged(FlavorEvent e) {
//        userSelection=false;
        updateEditActions();
//        userSelection=true;
    }
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		updateEditActions();
		if (sectionUI != null)
			sectionUI.setEnabled(enabled);
	}
	

	public void tableChanged(TableModelEvent e) {
		//System.out.println("Table changed !");
		marginChange = true;
		// Calc preferred size of index col
		TableColumnModel colModel = sectionsTable.getColumnModel();

		int tableWidth = sectionsTable.getPreferredScrollableViewportSize().width;
//		int tableWidth2=sectionsTable.getPreferredSize().width;
//		System.out.println(tableWidth+" "+tableWidth2);
		int cols = colModel.getColumnCount();
		int totalWidth = 0;
		for (int mc = 0; mc < cols; mc++) {
			int vc = sectionsTable.convertColumnIndexToView(mc);
			// if (c==1) continue;
			TableColumn indCol = colModel.getColumn(vc);
			int prefWidth = 0;
			
			for (int i = 0; i < sectionTableModel.getRowCount(); i++) {
				Component cc = sectionsTable.prepareRenderer(sectionsTable
						.getCellRenderer(i, vc), i, vc);
				Dimension pd = cc.getPreferredSize();
				if (pd.width > prefWidth)
					prefWidth = pd.width;
			}
			prefWidth += sectionsTable.getIntercellSpacing().width * 2;
			indCol.setMinWidth(prefWidth);
			// indCol.setMaxWidth(prefWidth);
			if (!userSized) {
				if (mc == 1) {
					indCol.setPreferredWidth(tableWidth - totalWidth);
				} else {
					indCol.setPreferredWidth(prefWidth);
				}
			}
			totalWidth += indCol.getPreferredWidth();

		}
		
		
		sectionsTable.doLayout();
		marginChange = false;
	}

	public void columnAdded(TableColumnModelEvent e) {
		// TODO Auto-generated method stub

	}

	public void columnMarginChanged(ChangeEvent e) {
		//System.out.println("Margin!");
		if (!marginChange) {
			userSized = true;
		}
	}

	public void columnMoved(TableColumnModelEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("Moved!");
	}

	public void columnRemoved(TableColumnModelEvent e) {
		// TODO Auto-generated method stub

	}

	public void columnSelectionChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

    public void providesEditActions(Object src, EditActions editActions) {
        if(editActionsListener!=null){
            editActionsListener.providesEditActions(src, editActions);
        }
        
    }

    public EditActionsListener getEditActionListener() {
        return editActionsListener;
    }

    public void setEditActionListener(EditActionsListener editActionsListener) {
        this.editActionsListener = editActionsListener;
    }

    public void focusGained(FocusEvent e) {
       Component c=e.getComponent();
       int eId=e.getID();
       if(eId==FocusEvent.FOCUS_GAINED){
    	   if(c==metadataTable){
    		   
    	   }else if(c==sectionsTable){
    		  ListSelectionModel sectLsm=sectionsTable.getSelectionModel();
    		  if(!sectLsm.isSelectionEmpty()){
    			  cutSectionAction.setEnabled(true);
    			  providesEditActions(this, editSectionActions);
    		  }
    	   }
       }
       updateEditActions();
        
    }

    public void focusLost(FocusEvent e) {
        // nothing to do
    }

    public List<Action> getNewActionsList() {
        return newActionsList;
    }

	public void restoreState(Hashtable<?, ?> state) {
	    Section[] currSects=sectionTableModel.getSections();
	    for(Section cs:currSects){
	        List<String> sIcs=cs.itemCodesList();
	        itemCodesList.removeAll(sIcs);
	    }
		Section[] tableSections=(Section[])state.get("sections");
		for(Section cs:tableSections){
            List<String> sIcs=cs.itemCodesList();
            itemCodesList.addAll(sIcs);
        }
		setTableSections(tableSections);
		Integer selRow=(Integer)state.get("_sections.selected_row");
		if(selRow==null || selRow==-1){
		    sectionsTable.clearSelection();
		}else{
		sectionsTable.setRowSelectionInterval(selRow,selRow);
		}
	}

	public void storeState(Hashtable<Object, Object> state) {
		state.put("sections",sectionTableModel.getSections());
		state.put("_sections.selected_row", sectionsTable.getSelectedRow());
	}

	public void setSelectedSection(Section section){
	    Section[] sections=sectionTableModel.getSections();
	    for(int i=0;i<sections.length;i++){
            if(sections[i]==section){
                sectionsTable.setRowSelectionInterval(i,i);
            }
        }
	}
	public void setSelectedPromptItem(PromptItem pi) {
	    if(pi!=null){
	        Section s=pi.getSection();
	        if(s!=null){
	            Integer sectionPos=s.getSectionPosition();
	            if(sectionPos!=null){
//	                Section[] sections=sectionTableModel.getSections();
//	                Section selSection=sections[sectionPos];

//	                List<PromptItem> pis=selSection.getPromptItems();
	                //                    for(PromptItem p:pis){
	                //                        if(p==pi){
	                sectionsTable.setRowSelectionInterval(sectionPos,sectionPos);
	                //                        }
	                //                    }
	                sectionUI.setSelectedPromptItem(pi);
	            }
	        }
	    }
	    //        Section[] sections=sectionTableModel.getSections();
	    //        for(int i=0;i<sections.length;i++){
	    //            Section s=sections[i];
	    //            List<PromptItem> pis=s.getPromptItems();
	    //            for(PromptItem p:pis){
	    //                if(p==pi){
	    //                    sectionsTable.setRowSelectionInterval(i,i);
	    //                }
	    //            }
	    //        }
	   
	}

    public Section.Mode getDefaultSectionMode() {
        return defaultSectionMode;
    }

    public void setDefaultSectionMode(Section.Mode defaultSectionMode) {
        this.defaultSectionMode = defaultSectionMode;
        if(sectionUI!=null){
            sectionUI.setDefaultMode(defaultSectionMode);
        }
    }

    public int getDefaultPreRecording() {
        return defaultPreRecording;
    }

    public void setDefaultPreRecording(int defaultPreRecording) {
        this.defaultPreRecording = defaultPreRecording;
        if(sectionUI!=null){
            sectionUI.setDefaultPreRecording(defaultPreRecording);
        }
    }

    public int getDefaultPostRecording() {
        return defaultPostRecording;
    }

    public void setDefaultPostRecording(int defaultPostRecording) {
        this.defaultPostRecording = defaultPostRecording;
        if(sectionUI!=null){
            sectionUI.setDefaultPostRecording(defaultPostRecording);
        }
    }

    public boolean isDefaultPromptAutoPlay() {
        return defaultPromptAutoPlay;
    }

    public void setDefaultPromptAutoPlay(boolean defaultPromptAutoPlay) {
        this.defaultPromptAutoPlay = defaultPromptAutoPlay;
        if(sectionUI!=null){
            sectionUI.setDefaultPromptAutoPlay(defaultPromptAutoPlay);
        }
    }

   
}
