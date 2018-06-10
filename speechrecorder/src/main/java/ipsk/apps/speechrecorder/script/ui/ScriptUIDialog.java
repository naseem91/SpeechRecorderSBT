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
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.Script;
import ipsk.swing.CopyAction;
import ipsk.swing.CutAction;
import ipsk.swing.JDialogPanel;
import ipsk.swing.PasteAction;
import ipsk.swing.RedoAction;
import ipsk.swing.UndoAction;
import ipsk.swing.action.EditActions;
import ipsk.swing.action.EditActionsListener;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.List;

import javax.help.HelpBroker;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class ScriptUIDialog extends JDialogPanel implements
		ActionListener, PropertyChangeListener, ClipboardOwner, FlavorListener, EditActionsListener {

    private static final long serialVersionUID = -7053838453598657369L;

    private Script script;

	private JMenu scriptMenu;
	private JMenu sectionMenu;
	private JMenu itemMenu;
	
	
//	private AddRecordingAction addRecordingAction;
//    private AddNonRecordingAction addNonRecordingAction;
    
	private JMenu editMenu;

	private CutAction dummyCutAction;

	private CopyAction dummyCopyAction;

	private PasteAction dummyPasteAction;
	
	private UndoAction dummyUndoAction;
	
	 private RedoAction dummyRedoAction;

	private Clipboard clipboard = null;

	private ScriptUI scriptUI;
	
	private URL projectContext=null;

	private JMenuItem cutMenuItem;

    private JMenuItem copyMenuItem;

    private JMenuItem pasteMenuItem;

	private JMenuItem undoMenuItem;

    private JMenuItem redoMenuItem;

   

    public ScriptUIDialog(URL projectContext,List<PromptPresenterServiceDescriptor> availPromptPresenters) {
        this(projectContext,new ItemcodeGenerator(),null,null);
    }
	public ScriptUIDialog(URL projectContext,ItemcodeGenerator itemcodeGenerator,List<PromptPresenterServiceDescriptor> promptPresentersClassList,HelpBroker helpBroker) {

//		super(JDialogPanel.OK_APPLY_CANCEL_OPTION);
	    super(JDialogPanel.Options.OK_CANCEL);
		this.projectContext=projectContext;
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
		
		scriptUI=new ScriptUI(projectContext,itemcodeGenerator,promptPresentersClassList);
//		scriptUI.setPromptFontFamilies(promptFontFamilies);
//		scriptUI.setInstructionsFontFamilies(instructionsFontFamilies);
//		scriptUI.setDescriptionFontFamilies(descriptionFontFamilies);
//		JMenuItem menuItem = null;
		menuBar = new JMenuBar();
		scriptMenu = new JMenu("Script");
		scriptMenu.setMnemonic(KeyEvent.VK_S);
		List<Action> newactionsList=scriptUI.getNewActionsList();
		for(Action na:newactionsList){
		    JMenuItem nami=new JMenuItem(na);
		    scriptMenu.add(nami);
		}
		
		SectionUI sectionUI=scriptUI.getSectionUI();
		sectionMenu = new JMenu("Section");
		sectionMenu.setMnemonic(KeyEvent.VK_D);
		newactionsList=sectionUI.getNewActionsList();
		for(Action na:newactionsList){
		    JMenuItem nami=new JMenuItem(na);
		    sectionMenu.add(nami);
		}
		
		PromptItemUI piUI=sectionUI.getPromptItemEditor();
		itemMenu=new JMenu("Item");
		List<Action> actionsList=piUI.getActions();
		for(Action a:actionsList){
		    JMenuItem nami=new JMenuItem(a);
		    itemMenu.add(nami);
		}
		
		
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
//		final ActionListener tal = this;
		dummyCutAction = new CutAction() {
			
            private static final long serialVersionUID = 8989347922466623849L;

            public void actionPerformed(ActionEvent ae) {
				// dummy
			}
		};
		dummyCutAction.setEnabled(false);
		cutMenuItem = new JMenuItem(dummyCutAction);

		editMenu.add(cutMenuItem);
//		final ActionListener cal = this;
		dummyCopyAction = new CopyAction() {
            private static final long serialVersionUID = -1411818294539107013L;

            public void actionPerformed(ActionEvent ae) {
				// dummy
			}
		};

		dummyCopyAction.setEnabled(false);

		copyMenuItem = new JMenuItem(dummyCopyAction);
		editMenu.add(copyMenuItem);

		dummyPasteAction = new PasteAction() {
            private static final long serialVersionUID = -5920846157836143799L;

            public void actionPerformed(ActionEvent ae) {
				// dummy
			}
		};
		dummyPasteAction.setEnabled(false);
		pasteMenuItem = new JMenuItem(dummyPasteAction);
		editMenu.add(pasteMenuItem);

		dummyUndoAction = new UndoAction() {
			public void actionPerformed(ActionEvent ae) {
				// dummy
			}
		};
		dummyUndoAction.setEnabled(false);
		undoMenuItem = new JMenuItem(dummyUndoAction);
		editMenu.add(undoMenuItem);
		
		dummyRedoAction = new RedoAction() {
            public void actionPerformed(ActionEvent ae) {
                // dummy
            }
        };
        dummyRedoAction.setEnabled(false);
        redoMenuItem = new JMenuItem(dummyRedoAction);
        editMenu.add(redoMenuItem);
        
        menuBar.add(scriptMenu);
        menuBar.add(sectionMenu);
        menuBar.add(itemMenu);
        menuBar.add(editMenu);
        
        if(helpBroker!=null){
            JMenu helpMenu = new JMenu("Help");
            JMenuItem helpMi=new JMenuItem("Help");
            helpMenu.add(helpMi);
            helpBroker.enableHelpOnButton(helpMi, "section-7", null);
            menuBar.add(helpMenu);
        }
		
		

		Container content = getContentPane();
		content.setLayout(new BorderLayout());
	
		
		
		content.add(scriptUI, BorderLayout.CENTER);
	
		scriptUI.setEditActionListener(this);
		updateEditActions();


		
		
	}
	

	public ScriptUIDialog() {
//       this(null,null,null);
	    this(null,null);
    }

  

	public void setScript(Script script) {
		if(this.script!=null){
			this.script.removePropertyChangeListener(this);
		}
		this.script = script;
		
		setTitle();
		scriptUI.setScript(this.script);
		if(this.script!=null){
			this.script.addPropertyChangeListener(this);
		}
	}

	private void setTitle() {
		if (script != null) {
			setFrameTitle("Script \"" + script.getName() + "\"");
		}
	}

//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		ScriptUIDialog sui = new ScriptUIDialog(null,null,null);
//	}

	

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
//            script.setName(nameField.getText());
//            script.setSections(sectionTableModel.getSections());
            scriptUI.applyValues();
        }


	public void propertyChange(PropertyChangeEvent evt) {
//		System.out.println("Prop: " + evt.getPropertyName() + ": "
//				+ evt.getOldValue() + " -> " + evt.getNewValue());
		if (evt.getPropertyName().equals("name")) {
			setTitle();
		} 
	}


	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// System.out.println("lostOwnerShip");
	}

	private void updateEditActions() {
		boolean editorEnabled = super.isEnabled();
//		boolean clipBoardAvail = false;
//		try {
//			clipBoardAvail = clipboard
//					.isDataFlavorAvailable(Section.CLASS_DATA_FLAVOR);
//		} catch (IllegalStateException ise) {
//			// Accessed by another application
//		}
//		pasteAction.setEnabled(editorEnabled && clipBoardAvail);
//		
//		
		editMenu.setEnabled(editorEnabled);
		okButton.setEnabled(editorEnabled);
	}

	public void flavorsChanged(FlavorEvent e) {
		// System.out.println("flavorChanged");
		updateEditActions();

	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		updateEditActions();
		if (scriptUI != null)
			scriptUI.setEnabled(enabled);
	}

    public void providesEditActions(Object src, EditActions editActions) {
    	Action cut=dummyCutAction;
    	Action copy=dummyCopyAction;
    	Action paste=dummyPasteAction;
    	Action undo=dummyUndoAction;
    	Action redo=dummyRedoAction;
    	if(editActions!=null){
    		Action eCut=editActions.getCutAction();
    		Action eCopy=editActions.getCopyAction();
    		Action ePaste=editActions.getPasteAction();
    		Action eUndo=editActions.getUndoAction();
    		Action eRedo=editActions.getRedoAction();
    		if(eCut!=null){
    			cut=eCut;
    		}
    		if(eCopy!=null){
    			copy=eCopy;
    		}
    		if(ePaste!=null){
    			paste=ePaste;
    		}
    		if(eUndo!=null){
    			undo=eUndo;
    		}
    		if(eRedo!=null){
    			redo=eRedo;
    		}
    	}
    	cutMenuItem.setAction(cut);
        copyMenuItem.setAction(copy);
        pasteMenuItem.setAction(paste);
        undoMenuItem.setAction(undo);
        redoMenuItem.setAction(redo);
    	
    }

    public void setSelectedPromptItem(PromptItem pi) {
       scriptUI.setSelectedPromptItem(pi);
    }

    public ScriptUI getScriptUI() {
        return scriptUI;
    }
	

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
		scriptUI.setProjectContext(projectContext);
	}

	/**
	 * @param instructionsFontFamilies
	 * @see ipsk.apps.speechrecorder.script.ui.ScriptUI#setInstructionsFontFamilies(java.lang.String[])
	 */
	public void setInstructionsFontFamilies(String[] instructionsFontFamilies) {
		scriptUI.setInstructionsFontFamilies(instructionsFontFamilies);
	}
	/**
	 * @param descriptionFontFamilies
	 * @see ipsk.apps.speechrecorder.script.ui.ScriptUI#setDescriptionFontFamilies(java.lang.String[])
	 */
	public void setDescriptionFontFamilies(String[] descriptionFontFamilies) {
		scriptUI.setDescriptionFontFamilies(descriptionFontFamilies);
	}
	/**
	 * @param promptFontFamilies
	 * @see ipsk.apps.speechrecorder.script.ui.ScriptUI#setPromptFontFamilies(java.lang.String[])
	 */
	public void setPromptFontFamilies(String[] promptFontFamilies) {
		scriptUI.setPromptFontFamilies(promptFontFamilies);
	}



	
}
