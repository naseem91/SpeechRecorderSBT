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
import ipsk.apps.speechrecorder.script.PromptItemsTableModel;
import ipsk.apps.speechrecorder.script.RecScriptManager;
import ipsk.db.speech.Mediaitem;
import ipsk.db.speech.Nonrecording;
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.PromptItemsList;
import ipsk.db.speech.Recording;
import ipsk.db.speech.Section;
import ipsk.lang.DisplayBoolean;
import ipsk.swing.CopyAction;
import ipsk.swing.CutAction;
import ipsk.swing.EnumSelectionItem;
import ipsk.swing.EnumVector;
import ipsk.swing.PasteAction;
import ipsk.swing.RedoAction;
import ipsk.swing.UndoAction;
import ipsk.swing.action.EditActions;
import ipsk.swing.action.EditActionsListener;
import ipsk.swing.table.AutoFontCellRenderer;
import ipsk.swing.text.EditorKitMenu;
import ipsk.util.collections.ObservableList;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.undo.StateEdit;
import javax.swing.undo.StateEditable;
import javax.swing.undo.UndoManager;


public class SectionUI extends JPanel implements ListSelectionListener,
		FlavorListener, ClipboardOwner, PropertyChangeListener, ActionListener, FocusListener, EditActionsListener, StateEditable {

	static class EnumSel {
		private String displayName;
		private Enum string;

		public EnumSel(Enum e,String displayName){
			this.string = e;
			this.displayName = displayName;
		}
		public EnumSel(Enum e) {
			this(e,e==null?null:e.toString());
		}
		
		public Enum getEnum(){
			return string;
		}

		public String toString() {
			return displayName;
		}

		public boolean equals(Object o) {
			if (o != null) {
				if (o instanceof EnumSel) {
					EnumSel mo = (EnumSel) o;
					if (string == null) {
						if (mo.getEnum() == null)
							return true;
					} else {
						if (string.equals(mo.getEnum())) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}
	
	
	
	
	
//	private static EnumSel[] MODES = new EnumSel[] { new EnumSel(null,"Manual (Default)"),
//			new EnumSel(Section.Mode.MANUAL), new EnumSel(Section.Mode.AUTOPROGRESS),
//			new EnumSel(Section.Mode.AUTORECORDING) };
	private EnumVector<Section.Mode> modes = new EnumVector<Section.Mode>(Section.Mode.class,"(Default)");
	private static EnumVector<Section.Order> ORDERS = new EnumVector<Section.Order>(Section.Order.class,"Sequential (Default)");
	
//	private static StringSel[] PROMPTPHASES = new StringSel[] { new StringSel(null,"Idle (Default)"),
//		new StringSel(Section.PromptPhase.IDLE.value()), new StringSel(Section.PromptPhase.PRERECORDING.value()) ,new StringSel(Section.PromptPhase.RECORDING.value())};
	private static EnumVector<Section.PromptPhase> PROMPTPHASES=new EnumVector<Section.PromptPhase>(Section.PromptPhase.class,"Idle (Default)");
	private Section section;

	private JTextField nameField;

	private JComboBox modeBox;
	private JComboBox orderBox;
	private JComboBox promptphasesBox;
	private JComboBox speakerdisplayBox;


	private JPanel promptItemsPanel;

	private PromptItemsTableModel promptItemTableModel;
	
	private AutoFontCellRenderer promptTextCellRenderer;
	
	private JTable promptItemsTable;
	
	private String[] promptFontFamilies;
	
	private String[] instructionsFontFamilies;
	/**
	 * @return the instructionsFontFamilies
	 */
	public String[] getInstructionsFontFamilies() {
		return instructionsFontFamilies;
	}

	/**
	 * @param instructionsFontFamilies the instructionsFontFamilies to set
	 */
	public void setInstructionsFontFamilies(String[] instructionsFontFamilies) {
		this.instructionsFontFamilies = instructionsFontFamilies;
		promptItemEditor.setInstructionsFontFamilies(instructionsFontFamilies);
	}

	private String[] descriptionFontFamilies;

	/**
	 * @return the descriptionFontFamilies
	 */
	public String[] getDescriptionFontFamilies() {
		return descriptionFontFamilies;
	}

	/**
	 * @param descriptionFontFamilies the descriptionFontFamilies to set
	 */
	public void setDescriptionFontFamilies(String[] descriptionFontFamilies) {
		this.descriptionFontFamilies = descriptionFontFamilies;
		promptItemEditor.setDescriptionFontFamilies(descriptionFontFamilies);
	}

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
		promptTextCellRenderer.setPreferredFontFamilies(promptFontFamilies);
		promptItemEditor.setPromptFontFamilies(promptFontFamilies);
	}

	private JPanel actionPanel;

	//private JButton editButton;

	private JButton submitButton;

	private JDialog d;

	private Object value = JOptionPane.UNINITIALIZED_VALUE;
	
	private AddRecordingAction addRecordingAction;
    private AddNonRecordingAction addNonRecordingAction;
   
	
	//private JMenu editMenu;
	private CutAction cutAction;

	private CopyAction copyAction;

	private PasteAction pasteAction;

	private Clipboard clipboard = null;
	
	private JSplitPane splitPane;
//	protected PromptItemUI piUI;
	
	private Section.Mode defaultMode=Section.Mode.MANUAL;
	private int defaultPreRecording;
	private int defaultPostRecording;
	private boolean defaultPromptAutoPlay=true;
	  
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
		promptItemEditor.setProjectContext(projectContext);
	}

	private PromptItemUI promptItemEditor;
    /**
	 * @return the promptItemEditor
	 */
	public PromptItemUI getPromptItemEditor() {
		return promptItemEditor;
	}

	private EditActions editActions;
    private EditActionsListener editActionsListener;
    
    private boolean userSelection=true;
    private List<Action> newActionsList;
    private ItemcodeGenerator itemcodeGenerator;
    private ObservableList<String> itemCodesList;
    private UndoManager undoManager=new UndoManager();
    private UndoAction undoAction;
//
//	public SectionUI(){
//	    this((URL)null);
//	}
    private RedoAction redoAction;
    
    private List<PromptPresenterServiceDescriptor> availablePromptPresenters;
	
	public SectionUI(URL projectContext, ObservableList<String> itemCodesSetProvider,ItemcodeGenerator itemcodeGenerator,List<PromptPresenterServiceDescriptor> availablePromptPresenters2) {
		super(new BorderLayout());
		this.projectContext=projectContext;
		this.itemCodesList=itemCodesSetProvider;
		this.itemcodeGenerator=itemcodeGenerator;
		this.availablePromptPresenters=availablePromptPresenters2;
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
		
		JPanel sectionPanel=new JPanel(new GridBagLayout());
//		setLayout(new GridBagLayout());
		// this.script=script;
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new Insets(2, 2, 2, 2);
		c.anchor = GridBagConstraints.PAGE_START;

		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;
		JLabel nameLabel = new JLabel("Name (Id):");
		sectionPanel.add(nameLabel, c);
		nameField = new JTextField();
        EditorKitMenu nameFieldEkm=new EditorKitMenu(nameField);
        nameFieldEkm.setPopupMenuActiv(true);
        nameField.addActionListener(this);
//        nameField.getDocument().addDocumentListener(this);
		c.gridx++;
		sectionPanel.add(nameField, c);

		c.gridx = 0;
		c.gridy++;
		sectionPanel.add(new JLabel("Mode:"), c);
		modeBox = new JComboBox(modes);
		c.gridx++;
		sectionPanel.add(modeBox, c);
		
		c.gridx = 0;
		c.gridy++;
		sectionPanel.add(new JLabel("Order:"), c);
		orderBox = new JComboBox(ORDERS);
		c.gridx++;
		sectionPanel.add(orderBox, c);
		
		c.gridx = 0;
		c.gridy++;
		sectionPanel.add(new JLabel("Promptphase:"), c);
		promptphasesBox = new JComboBox(PROMPTPHASES);
		c.gridx++;
		sectionPanel.add(promptphasesBox, c);
		
		c.gridx = 0;
		c.gridy++;
		sectionPanel.add(new JLabel("Speakerdisplay:"), c);
		speakerdisplayBox = new JComboBox(DisplayBoolean.getDefinedvalues());
		c.gridx++;
		sectionPanel.add(speakerdisplayBox, c);
		
		promptItemTableModel = new PromptItemsTableModel();
		promptItemsTable = new JTable(promptItemTableModel);
		TableColumn txtTableCol=promptItemsTable.getColumnModel().getColumn(PromptItemsTableModel.COL_PROMPT);
		TableCellRenderer txtTcr=txtTableCol.getCellRenderer();
		if(txtTcr==null){
		    txtTcr=promptItemsTable.getDefaultRenderer(promptItemsTable.getColumnClass(PromptItemsTableModel.COL_PROMPT));
		}
		promptTextCellRenderer=new AutoFontCellRenderer(txtTcr);
        txtTableCol.setCellRenderer(promptTextCellRenderer);
        
		//promptItemsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		MouseAdapter listMouseListener = new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	        	if (e.getClickCount() == 2) {
	        		
	        		if(isEnabled()){
	        			editSelectedPromptItem();
	        		}
	        	}
	    	}
	    };
	    promptItemsTable.addMouseListener(listMouseListener); 

		ListSelectionModel promptItemsSelModel=promptItemsTable.getSelectionModel();
		promptItemsSelModel.setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		promptItemsSelModel.addListSelectionListener(this);
		promptItemsTable.setDragEnabled(true);
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy++;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane = new JScrollPane(promptItemsTable);
//		System.out.println(promptItemsTable.getPreferredScrollableViewportSize()+" "+promptItemsTable.getPreferredSize());
		promptItemsTable.setPreferredScrollableViewportSize(new Dimension(100,100));

		promptItemsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints sc = new GridBagConstraints();
		sc.gridx = 0;
		sc.gridy = 0;
		sc.weightx = 2.0;
		sc.weighty = 2.0;
		sc.fill = GridBagConstraints.BOTH;
		promptItemsPanel.setBorder(BorderFactory
				.createTitledBorder("Prompt items"));
		
		promptItemsPanel.add(scrollPane, sc);

		//add(promptItemsPanel, c);
		sectionPanel.add(scrollPane,c);
		
//		c.gridx+=2;
//		c.gridy=0;
//		c.gridwidth=1;
//		c.gridheight=6;

		promptItemEditor=new PromptItemUI(projectContext,availablePromptPresenters2);
//		add(promptItemEditor, c);
		sectionPanel.setMinimumSize(new Dimension(0,0));
        promptItemEditor.setMinimumSize(new Dimension(0,0));
		splitPane =new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,sectionPanel,promptItemEditor);
		
		add(splitPane,BorderLayout.CENTER);
	    
	        final ActionListener al = this;
	        addRecordingAction = new AddRecordingAction() {
	            public void actionPerformed(ActionEvent ae) {
	                al.actionPerformed(ae);
	            }
	        };
	        addRecordingAction.setEnabled(false);
	       
	        addNonRecordingAction = new AddNonRecordingAction() {
	            public void actionPerformed(ActionEvent ae) {
	                al.actionPerformed(ae);
	            }
	        };
	        addNonRecordingAction.setEnabled(false);
	     
        cutAction = new CutAction() {
            public void actionPerformed(ActionEvent ae) {
                al.actionPerformed(ae);
            }
        };
        cutAction.setEnabled(false);
      
       
        copyAction = new CopyAction() {
            public void actionPerformed(ActionEvent ae) {
                al.actionPerformed(ae);
            }
        };

        copyAction.setEnabled(false);

        pasteAction = new PasteAction() {
            public void actionPerformed(ActionEvent ae) {
                al.actionPerformed(ae);
            }
        };
        pasteAction.setEnabled(false);
        
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
        editActions=new EditActions(cutAction,copyAction,pasteAction,undoAction,redoAction);
		updateEditActions();
		InputMap imap = promptItemsTable.getInputMap();
		imap.put(KeyStroke.getKeyStroke("ctrl X"), cutAction
				.getValue(Action.ACTION_COMMAND_KEY));
		imap.put(KeyStroke.getKeyStroke("ctrl C"), copyAction
				.getValue(Action.NAME));
		imap.put(KeyStroke.getKeyStroke("ctrl V"), pasteAction
				.getValue(Action.NAME));
		ActionMap map = promptItemsTable.getActionMap();
		map.put(cutAction.getValue(Action.ACTION_COMMAND_KEY), cutAction);
		map.put(copyAction.getValue(Action.NAME), copyAction);
		map.put(pasteAction.getValue(Action.NAME), pasteAction);
		
		
		promptItemsTable.addFocusListener(this);
		newActionsList=new ArrayList<Action>();
		newActionsList.add(addRecordingAction);
		newActionsList.add(addNonRecordingAction);
		promptItemEditor.setEditActionListener(this);
	}

	 private void setTablePromptItems(PromptItem[] tablepromptItems) {
	     promptItemTableModel.setPromptItems(tablepromptItems);
	        if(tablepromptItems.length>0){
	            // set first row selected by default
	            promptItemsTable.setRowSelectionInterval(0, 0);
	        }else{
	            promptItemsTable.clearSelection();
	            promptItemEditor.setPromptItem(null);
	        }
	 }
	
    private void setPromptItemsCopy(){
        int promptItemCount=0;
        ArrayList<PromptItem> copyPis=new ArrayList<PromptItem>();
        if(section!=null){
            List<PromptItem> pis=section.getPromptItems();
            copyPis.addAll(pis);
        }
        setTablePromptItems(copyPis.toArray(new PromptItem[0]));
	}
    
	public void setSection(Section section) {
	    if(this.section!=null){
	        this.section.removePropertyChangeListener(this);
	        applyValues();
	    }
		this.section = section;
		userSelection=false;
		boolean enabled=(this.section!=null);
		
		
		
		if(enabled){
		    
//		    nameField.getDocument().removeDocumentListener(this);
		    nameField.setText(section.getName());
//		    nameField.getDocument().addDocumentListener(this);
		    modeBox.setSelectedItem(modes.getItem(this.section.getMode()));
		    orderBox.setSelectedItem(ORDERS.getItem(this.section.getOrder()));
		    //promptphasesBox.setSelectedItem(new StringSel(this.section.getNNPromptphase().value()));
		    promptphasesBox.setSelectedItem(PROMPTPHASES.getItem(this.section.getPromptphase()));
		    speakerdisplayBox.setSelectedItem(new DisplayBoolean(this.section.getSpeakerDisplay()));
		    this.section.addPropertyChangeListener(this);
		}
		
		
		undoManager.discardAllEdits();
		setEnabled(enabled);
		setPromptItemsCopy();
		updateEditActions();
		userSelection=true;
		
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SectionUI sui = new SectionUI((URL)null,null,new ItemcodeGenerator(),null);

	}
	
	private void editSelectedPromptItem(){
		
	
		ListSelectionModel selModel = promptItemsTable.getSelectionModel();
		int sel = selModel.getMinSelectionIndex();
		
	}
	
	
	private void stopCellEditing(){
	    TableCellEditor cellEditor=promptItemsTable.getCellEditor();
        if(cellEditor!=null){
            cellEditor.stopCellEditing();
        }
	}
	
	protected void applyValues(){
	    if(section!=null){
	        promptItemEditor.applyValues();
	        String nameFieldText = nameField.getText();
	        if (!(section.getName() == null && nameFieldText.equals(""))) {
	            section.setName(nameFieldText);
	        }
	        // TODO still type unsafe
	        section.setMode((Section.Mode) ((EnumSelectionItem<Section.Mode>)modeBox.getSelectedItem()).getEnumVal());
	        section.setOrder((Section.Order) ((EnumSelectionItem<Section.Order>)orderBox.getSelectedItem()).getEnumVal());
	        //section.setPromptphase(Section.PromptPhase.getByValue(((StringSel)promptphasesBox.getSelectedItem()).getString()));
	        section.setPromptphase((Section.PromptPhase) ((EnumSelectionItem<Section.PromptPhase>)promptphasesBox.getSelectedItem()).getEnumVal());
	        section.setSpeakerDisplay(((DisplayBoolean)speakerdisplayBox.getSelectedItem()).getValue());
	        stopCellEditing();
	        PromptItem[] tablePis=promptItemTableModel.getPromptItems();
	        List<PromptItem> pisList=Arrays.asList(tablePis);
	        section.setPromptItems(pisList);
	    }
	}
    
    
	public void actionPerformed(ActionEvent e) {
		
		Object src = e.getSource();
			String cmd = e.getActionCommand();
			if (copyAction.getActionCommand().equals(cmd)) {
			    clipboardSelectedPromptItems();
			} else if (pasteAction.getActionCommand().equals(cmd)) {
				DataFlavor[] adf=clipboard.getAvailableDataFlavors();
				try {
					for(DataFlavor df:adf){
						if(PromptItem.CLASS_DATA_FLAVOR.equals(df)){
							insert((PromptItem) (clipboard.getContents(this)
									.getTransferData(PromptItem.CLASS_DATA_FLAVOR)));
						}else if(PromptItemsList.CLASS_DATA_FLAVOR.equals(df)){
							PromptItemsList pisDf=((PromptItemsList) (clipboard.getContents(this)
									.getTransferData(PromptItemsList.CLASS_DATA_FLAVOR)));
							if(pisDf!=null){
								insert(pisDf);
							}
						}
					}
				} catch (UnsupportedFlavorException e1) {
					// ignore
					e1.printStackTrace();
				} catch (IOException e1) {
					//ignore
					e1.printStackTrace();
				}
			} else if (cutAction.getActionCommand().equals(cmd)) {
				clipboardSelectedPromptItems();
				removeSelectedPromptItems();
			}else if(undoAction.getActionCommand().equals(cmd)){
                undoManager.undo();
            }else if(redoAction.getActionCommand().equals(cmd)){
                undoManager.redo();
            }else if (addRecordingAction.getActionCommand().equals(cmd)) {
			    Recording r=new Recording();
			    Mediaitem mi=new Mediaitem();
			    List<Mediaitem> mis=new ArrayList<Mediaitem>();
			    mis.add(mi);
			    r.setMediaitems(mis);
                insert(r);
            }else if (addNonRecordingAction.getActionCommand().equals(cmd)) {
                Nonrecording nr=new Nonrecording();
                Mediaitem mi=new Mediaitem();
                List<Mediaitem> mis=new ArrayList<Mediaitem>();
                mis.add(mi);
                nr.setMediaitems(mis);
                insert(nr);
            }else if(src==nameField){
               section.setName(nameField.getText());
            }
		//}
		
	}



	public void valueChanged(ListSelectionEvent e) {
	    if(!e.getValueIsAdjusting()){
	        updateEditActions();
	        ListSelectionModel selModel=promptItemsTable.getSelectionModel();
	        int selIndex=selModel.getMinSelectionIndex();
	        
	        if(selIndex>=0){
	            List<PromptItem> selPis=new ArrayList<PromptItem>();
	            int maxSelIdx=selModel.getMaxSelectionIndex();
	            if(maxSelIdx!=-1){
	                for(int si=selIndex;si<=maxSelIdx;si++){
	                    if(selModel.isSelectedIndex(si)){
	                        PromptItem pi=promptItemTableModel.getPromptItems()[si];
	                        selPis.add(pi);
	                    }
	                }
	                int selPisSize=selPis.size();
	                if(selPisSize==0){
	                    promptItemEditor.setPromptItem(null);
	                }else if(selPisSize==1){   
	                    promptItemEditor.setPromptItem(selPis.get(0));
	                }else{
	                    promptItemEditor.setPromptItems(selPis);
	                }
	            }
	        }else{
	            promptItemEditor.setPromptItem(null);
	            //promptItemEditor.setEnabled(false);
	        }
	    }
	}

	public void propertyChange(PropertyChangeEvent evt) {
	    Object src=evt.getSource();
	    
        String propertyName=evt.getPropertyName();
        if(src instanceof Section){
            if("name".equals(propertyName)){
                nameField.setText((String)evt.getNewValue());
            }
        }
	    
	}

	public void removeSelectedPromptItem() {
	    StateEdit removeStateEdit=new StateEdit(this,"Remove prompt item");
	    stopCellEditing();
		ArrayList<PromptItem> newPromptItems = new ArrayList<PromptItem>(Arrays
				.asList(promptItemTableModel.getPromptItems()));
		int selRow=promptItemsTable.getSelectedRow();
		if(selRow>=0){
		    PromptItem removedItem=newPromptItems.remove(selRow);
		    if(removedItem instanceof Recording){
		        Recording removedRec=(Recording)removedItem;
		        String removedItemcode=removedRec.getItemcode();
		       
		        itemCodesList.remove(removedItemcode);
		    }
		    int newPisCount=newPromptItems.size();
		    promptItemTableModel.setPromptItems(newPromptItems.toArray(new PromptItem[0]));
		    if(newPisCount==0){
		        promptItemsTable.clearSelection();
		    }else{
	               int newSelRow=selRow-1;
	               if(newSelRow<0)newSelRow=0;
	               promptItemsTable.setRowSelectionInterval(newSelRow, newSelRow);
	        }
		}
		removeStateEdit.end();
        undoManager.addEdit(removeStateEdit);
        updateEditActions();
	}
	
	public void removeSelectedPromptItems() {
	    StateEdit removeStateEdit=new StateEdit(this,"Remove prompt items");
	    stopCellEditing();
		ArrayList<PromptItem> newPromptItems = new ArrayList<PromptItem>(Arrays
				.asList(promptItemTableModel.getPromptItems()));
		int[] selRowsOrg=promptItemsTable.getSelectedRows();
		if(selRowsOrg.length>0){
			int[] selRowsAsc=Arrays.copyOf(selRowsOrg, selRowsOrg.length);
			// doc of getSelectedRow() does not mention if the list is ordered, so we order it here
			Arrays.sort(selRowsAsc);
			int lowestSelRow=Integer.MAX_VALUE;
			// remove items in reverse order
			for(int i=selRowsAsc.length-1;i>=0;i--){
				int selRow=selRowsAsc[i];

				if(selRow<lowestSelRow){
					lowestSelRow=selRow;
				}
				PromptItem removedItem=newPromptItems.remove(selRow);
				if(removedItem instanceof Recording){
					Recording removedRec=(Recording)removedItem;
					String removedItemcode=removedRec.getItemcode();

					itemCodesList.remove(removedItemcode);
				}

			}
			int newPisCount=newPromptItems.size();
			promptItemTableModel.setPromptItems(newPromptItems.toArray(new PromptItem[newPromptItems.size()]));
			if(newPisCount==0){
				promptItemsTable.clearSelection();
			}else{
				int newSelRow=lowestSelRow-1;
				if(newSelRow<0)newSelRow=0;
				promptItemsTable.setRowSelectionInterval(newSelRow, newSelRow);
			}
		}
		removeStateEdit.end();
        undoManager.addEdit(removeStateEdit);
        updateEditActions();
	}

//	public PromptItem getSelectedPromptItem() {
//		return promptItemTableModel.getPromptItems()[promptItemsTable.getSelectedRow()];
//	}
	
	public PromptItemsList getSelectedPromptItems() {
		int[] selRowIdcs=promptItemsTable.getSelectedRows();
		PromptItemsList selPis=new PromptItemsList();
		for(int selRowIdx:selRowIdcs){
			PromptItem selPi=promptItemTableModel.getPromptItems()[selRowIdx];
			selPis.add(selPi);
		}
		return selPis;
	}
	
	private void clipboardSelectedPromptItems(){
	    PromptItemsList pil=getSelectedPromptItems();
	    if(pil.size()==1){
	        PromptItem pi=pil.get(0);
	        clipboard.setContents(pi, this);
	    }else{
	        clipboard.setContents(pil, this);
	    }
	}

//	public PromptItemsList getSelectedTransferablePromptItems() {
//		
//		return new PromptItemsList(getSelectedPromptItems());
//	}

	public void insert(PromptItem promptItem) {
	    StateEdit insertStateEdit=new StateEdit(this,"Insert prompt item");
	    stopCellEditing();
	    if(promptItem instanceof Recording){
	        Recording recording=(Recording)promptItem;
	        String itemcode=recording.getItemcode();
	        
	        if(itemcodeGenerator.getConfig().isActive() && (itemcode==null || "".equals(itemcode) || (itemCodesList.contains(itemcode)))){
	            HashSet<String> ics=new HashSet<String>(itemCodesList);
//	            itemcodeGenerator.next();
	            // TODO
	            itemcodeGenerator.toNext(ics);
	            String icGen=itemcodeGenerator.getItemCode();
	            recording.setItemcode(icGen);
	        }
	        
	        itemCodesList.add(recording.getItemcode());
	    }
		ArrayList<PromptItem> newPromptItems = new ArrayList<PromptItem>(Arrays
				.asList(promptItemTableModel.getPromptItems()));

		ListSelectionModel selModel = promptItemsTable.getSelectionModel();
		
		int insertIndex = 0;
		if (!selModel.isSelectionEmpty()) {
			insertIndex = promptItemsTable.getSelectedRow()+1;
		}
		newPromptItems.add(insertIndex, promptItem);
		promptItemTableModel.setPromptItems(newPromptItems.toArray(new PromptItem[0]));
//		promptItemsTable.setRowSelectionInterval(insertIndex, insertIndex);
		insertStateEdit.end();
        undoManager.addEdit(insertStateEdit);
        promptItemsTable.setRowSelectionInterval(insertIndex, insertIndex);
        updateEditActions();
	}
	
	/**
	 * @param promptItemsList
	 */
	public void insert(List<PromptItem> promptItemsList) {
		 StateEdit insertStateEdit=new StateEdit(this,"Insert prompt items");
		 stopCellEditing();
		 // set new itemcodes if necessary
		 for(PromptItem promptItem:promptItemsList){
			 if(promptItem instanceof Recording){
				 Recording recording=(Recording)promptItem;
				 String itemcode=recording.getItemcode();

				 if(itemcodeGenerator.getConfig().isActive() && (itemcode==null || "".equals(itemcode) || (itemCodesList.contains(itemcode)))){
					 HashSet<String> ics=new HashSet<String>(itemCodesList);
					 //		            itemcodeGenerator.next();
					 // TODO
					 itemcodeGenerator.toNext(ics);
					 String icGen=itemcodeGenerator.getItemCode();
					 recording.setItemcode(icGen);
				 }

				 itemCodesList.add(recording.getItemcode());
			 }
		 }
		 
		 	// determine insert position
			ListSelectionModel selModel = promptItemsTable.getSelectionModel();
			
			int startInsertIndex=0;
			
			
			if (!selModel.isSelectionEmpty()) {
//				startInsertIndex = promptItemsTable.getSelectedRow()+1;
				startInsertIndex = selModel.getMaxSelectionIndex()+1;
			}
			
			// insert prompt items
			ArrayList<PromptItem> newPromptItems = new ArrayList<PromptItem>(Arrays
					.asList(promptItemTableModel.getPromptItems()));
			int insertIndex=startInsertIndex;
			for(PromptItem promptItem:promptItemsList){
				//insert into list
				newPromptItems.add(insertIndex, promptItem);
				insertIndex++;
			}
			int endInsertIndex=insertIndex-1;
			// apply list to table model
			promptItemTableModel.setPromptItems(newPromptItems.toArray(new PromptItem[newPromptItems.size()]));
			// edit end
			insertStateEdit.end();
	        undoManager.addEdit(insertStateEdit);
	        promptItemsTable.setRowSelectionInterval(startInsertIndex, endInsertIndex);
	        updateEditActions();
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// System.out.println("lostOwnerShip");
	}

	public void flavorsChanged(FlavorEvent e) {
	    userSelection=false;
		updateEditActions();
		userSelection=true;
	}
	
	private void updateEditActions() {
		boolean editorEnabled=super.isEnabled();
		// check if clipboard conatins flavors to accept 
		boolean clipBoardAvail = false;
		try {
			// accept prompt item or list of prompt items
			clipBoardAvail = clipboard
					.isDataFlavorAvailable(PromptItem.CLASS_DATA_FLAVOR)||
					clipboard.isDataFlavorAvailable(PromptItemsList.CLASS_DATA_FLAVOR);
		} catch (IllegalStateException ise) {
			// Accessed by another application
		}
		// enable actions accordingly
		pasteAction.setEnabled(editorEnabled && clipBoardAvail);
		ListSelectionModel selModel = promptItemsTable.getSelectionModel();
		if (selModel.isSelectionEmpty()) {
			//editButton.setEnabled(false);
			copyAction.setEnabled(false);
			cutAction.setEnabled(false);
		} else {
			//editButton.setEnabled(editorEnabled);
			copyAction.setEnabled(editorEnabled);
			cutAction.setEnabled(editorEnabled);
			if(editActionsListener!=null && userSelection){
			    editActionsListener.providesEditActions(this, editActions);
			}
		}
		addRecordingAction.setEnabled(editorEnabled);
		addNonRecordingAction.setEnabled(editorEnabled);
		// update undo/redo manager
		undoAction.update(undoManager);
        redoAction.update(undoManager);
	
	}
	   
    private void setDeepEnabled(Container c,boolean b){
        for(Component cc:c.getComponents()){
            if(cc instanceof Container){
                setDeepEnabled((Container)cc,b);
            }
            cc.setEnabled(b);
        }
    }
    
	public void setEnabled(boolean b){
		super.setEnabled(b);
//		updateEditActions();
//		nameField.setEnabled(b);
//        modeBox.setEnabled(b);
//        orderBox.setEnabled(b);
//        promptphasesBox.setEnabled(b);
//        speakerdisplayBox.setEnabled(b);
//		if (piUI != null){
//			piUI.setEnabled(b);
//		}
		setDeepEnabled(this, b);
	}

	public Action getCutAction(){
	    return cutAction;
	}


    public EditActionsListener getEditActionListener() {
        return editActionsListener;
    }


    public void setEditActionListener(EditActionsListener editActionsListener) {
        this.editActionsListener = editActionsListener;
    }


    public void focusGained(FocusEvent e) {
        updateEditActions();
    }


    public void focusLost(FocusEvent e) {
       
        
    }
    
    public List<Action> getNewActionsList(){
        return newActionsList;
    }


	public void providesEditActions(Object src, EditActions editActions) {
		if(editActionsListener!=null){
			editActionsListener.providesEditActions(src, editActions);
		}
	}
    public void restoreState(Hashtable<?, ?> state) {
        PromptItem[] pis=promptItemTableModel.getPromptItems();
        for(PromptItem pi: pis){
            if(pi instanceof Recording){
                Recording r=(Recording)pi;
                String ic=r.getItemcode();
                itemCodesList.remove(ic);
            }
        }
        PromptItem[] tablepromptItems=(PromptItem[])state.get("promptItems");
        for(PromptItem pi: tablepromptItems){
            if(pi instanceof Recording){
                Recording r=(Recording)pi;
                String ic=r.getItemcode();
                itemCodesList.add(ic);
            }
        }
        setTablePromptItems(tablepromptItems);
        Integer selRow=(Integer)state.get("_promptItems.selected_row");
        if(selRow==null || selRow==-1){
            promptItemsTable.clearSelection();
        }else{
            promptItemsTable.setRowSelectionInterval(selRow,selRow);
        }
    }

   

    public void storeState(Hashtable<Object, Object> state) {
        state.put("promptItems",promptItemTableModel.getPromptItems());
        state.put("_promptItems.selected_row", promptItemsTable.getSelectedRow());
    }

    public void setSelectedPromptItem(PromptItem pi) {
        PromptItem[] promptItems=promptItemTableModel.getPromptItems();
        if(pi!=null){
        Integer i=pi.getPosition();
        if(i!=null){
            promptItemsTable.setRowSelectionInterval(i,i);
        }
        }
//        for(int i=0;i<promptItems.length;i++){
//            if(promptItems[i]==pi){
//                promptItemsTable.setRowSelectionInterval(i,i);
//            }
//        }
    }

    public Section.Mode getDefaultMode() {
        return defaultMode;
    }

    public void setDefaultMode(Section.Mode defaultMode) {
        this.defaultMode = defaultMode;
        if(defaultMode!=null){
            modes.getItem(null).setDisplayName(defaultMode.toString()+" (Default)");
        }
    }

    public int getDefaultPreRecording() {
        return defaultPreRecording;
    }

    public void setDefaultPreRecording(int defaultPreRecording) {
        this.defaultPreRecording = defaultPreRecording;
        if(promptItemEditor!=null){
            promptItemEditor.setDefaultPreRecording(defaultPreRecording);
        }
    }

    public int getDefaultPostRecording() {
        return defaultPostRecording;
    }

    public void setDefaultPostRecording(int defaultPostRecording) {
        this.defaultPostRecording = defaultPostRecording;
        if(promptItemEditor!=null){
            promptItemEditor.setDefaultPostRecording(defaultPostRecording);
        }
    }

    public boolean isDefaultPromptAutoPlay() {
        return defaultPromptAutoPlay;
    }

    public void setDefaultPromptAutoPlay(boolean defaultPromptAutoPlay) {
        this.defaultPromptAutoPlay = defaultPromptAutoPlay;
        if(promptItemEditor!=null){
            promptItemEditor.setDefaultPromptAutoPlay(defaultPromptAutoPlay);
        }
    }

//    public Set<List<String>> getAvailablePromptPresenters() {
//        return availablePromptPresenters;
//    }

//    private void documentUpdate(DocumentEvent e){
//        javax.swing.text.Document d=e.getDocument();
//        if(d.equals(nameField.getDocument())){
//            section.removePropertyChangeListener(this);
//            section.setName(nameField.getText());
//            section.addPropertyChangeListener(this);
//        }
//    }
//    public void changedUpdate(DocumentEvent e) {
//       documentUpdate(e);
//    }
//
//    public void insertUpdate(DocumentEvent e) {
//        documentUpdate(e);
//    }
//
//    public void removeUpdate(DocumentEvent e) {
//        documentUpdate(e);
//        
//    }

//    public void setSelectableMIMETypeCombinations(
//            Set<List<String>> selectableMIMETypeCombinations) {
//        this.selectableMIMETypeCombinations = selectableMIMETypeCombinations;
//       promptItemEditor.setSelectableMIMETypeCombinations(selectableMIMETypeCombinations);
//    }

  
}
