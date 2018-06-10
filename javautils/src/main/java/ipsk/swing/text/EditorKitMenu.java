//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.


package ipsk.swing.text;

import ipsk.swing.CopyAction;
import ipsk.swing.CutAction;
import ipsk.swing.PasteAction;
import ipsk.swing.RedoAction;
import ipsk.swing.SelectAllAction;
import ipsk.swing.UndoAction;
import ipsk.swing.action.EditActionsListener;
import ipsk.swing.action.EditActions;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

/**
 * Editor menu kit for Swing text components. Creates an undo manager and a popup
 * menu for the given text component and applies a popup mouse listener by
 * default.
 * 
 * @author klausj
 * 
 */
public class EditorKitMenu implements CaretListener,
		MouseListener, FlavorListener, FocusListener, UndoableEditListener {

	private JTextComponent textComponent;

	private ArrayList<JMenuItem> menuItems = new ArrayList<JMenuItem>();

	private Action[] actions;

	private Action cutAction;

	private Action copyAction;

	private Action pasteAction;

	private UndoAction undoAction;

	private RedoAction redoAction;

	private JPopupMenu popupMenu;

	private boolean popupMenuActiv = true;

	private Clipboard clipboard;
	
	private Vector<EditActionsListener> editActionsListenerList;
	private EditActions editActions;
	
	private UndoManager undoManager=new UndoManager();
	
	/**
	 * Create editor kit with menu and register edit action listener.
	 * @param textComponent the Swing text component which will be wrapped by this editor kit
	 * @param editActionsListener listener is notified about active edit actions if the widget gains foucus  
	 */
	public EditorKitMenu(JTextComponent textComponent,EditActionsListener editActionsListener) {
		this(textComponent);
		addEditActionListener(editActionsListener);
	}
	/**
	 * Create editor kit with menu.
	 * @param textComponent the Swing text component which will be wrapped by this editor kit
	 */
	public EditorKitMenu(JTextComponent textComponent) {
	    this(textComponent,true);
	}
	
	/**
	 * Create editor kit with menu.
	 * If editable is false the cut action will not be added to the menu.
	 * @param textComponent the Swing text component which will be wrapped by this editor kit
	 * @param editable if false the widget content is considered as reda only
	 */
	public EditorKitMenu(JTextComponent textComponent,boolean editable) {
		super();
		this.textComponent = textComponent;
		//SecurityManager security = System.getSecurityManager();

		//if (security != null) {
			try {
				//security.checkSystemClipboardAccess();
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			} catch (SecurityException se) {
				clipboard = null;
			}
			if (clipboard != null) {
				clipboard.addFlavorListener(this);
			}
		//}

		actions = textComponent.getActions();
		InputMap iMap = textComponent.getInputMap();
		ActionMap aMap = textComponent.getActionMap();
		textComponent.addCaretListener(this);
		
		final UndoManager um=undoManager;
		undoAction = new ipsk.swing.UndoAction() {
			public void actionPerformed(ActionEvent e) {
				um.undo();
				updateActions();
			}
		};
		iMap.put(UndoAction.ACCELERATOR_VAL, UndoAction.NAME);
		aMap.put(UndoAction.NAME, undoAction);
		redoAction = new ipsk.swing.RedoAction() {
			public void actionPerformed(ActionEvent e) {
				um.redo();
				updateActions();
			}
		};
		iMap.put(RedoAction.ACCELERATOR_VAL, RedoAction.NAME);
		aMap.put(RedoAction.NAME, redoAction);
		undoAction.setEnabled(false);
		redoAction.setEnabled(false);
		Document d = textComponent.getDocument();
		d.addUndoableEditListener(this);

		// filter actions
		for (Action a : actions) {
			if (a.getValue(Action.NAME).equals(DefaultEditorKit.cutAction)) {
				final Action aCut=a;
				cutAction = new CutAction(){
					public void actionPerformed(ActionEvent e){
						aCut.actionPerformed(e);
					}
				};
				// cutAction.putValue(Action.ACCELERATOR_KEY,
				// CutAction.ACCELERATOR_VAL);
				if(editable){
				menuItems.add(new JMenuItem(cutAction));
				}

			} else if (a.getValue(Action.NAME).equals(
					DefaultEditorKit.copyAction)) {
				final Action aCopy=a;
				copyAction = new CopyAction(){
					public void actionPerformed(ActionEvent e){
						aCopy.actionPerformed(e);
					}
				};
				menuItems.add(new JMenuItem(copyAction));

			} else if (a.getValue(Action.NAME).equals(
					DefaultEditorKit.pasteAction)) {
				final Action aPaste=a;
				pasteAction=new PasteAction(){
					public void actionPerformed(ActionEvent e){
						aPaste.actionPerformed(e);
					}
				};
				pasteAction.setEnabled(false);
				if(editable){
				menuItems.add(new JMenuItem(pasteAction));
				}

			} else if (a.getValue(Action.NAME).equals(
					DefaultEditorKit.selectAllAction)) {
				final Action aSelectAll=a;
				Action selectAllAction=new SelectAllAction(){
					public void actionPerformed(ActionEvent e){
						aSelectAll.actionPerformed(e);
					}
				};
				menuItems.add(new JMenuItem(selectAllAction));

			}
		}
		if(editable){
		menuItems.add(new JMenuItem(undoAction));
		menuItems.add(new JMenuItem(redoAction));
		}
		popupMenu = createJPopupMenu();
		editActionsListenerList=new Vector<EditActionsListener>();
		editActions=new EditActions(cutAction, copyAction, pasteAction,undoAction,redoAction);
		
		textComponent.addFocusListener(this);
		
		updateActions();
        setPopupMenuActiv(true);
	}

	public JPopupMenu createJPopupMenu() {
		JPopupMenu popMenu = new JPopupMenu();
		for (JMenuItem mi : menuItems) {
			popMenu.add(mi);
		}
		return popMenu;
	}

	public boolean isPopupMenuActiv() {
		return popupMenuActiv;
	}

	public void setPopupMenuActiv(boolean popupMenuActiv) {
		this.popupMenuActiv = popupMenuActiv;
		if (this.popupMenuActiv) {
			textComponent.addMouseListener(this);
		} else {
			textComponent.removeMouseListener(this);
		}

	}

	public void propertyChange(PropertyChangeEvent evt) {
		// Sun's actions are always enabled !!
		System.out.println(evt);

	}

	private void updateActions() {
		if (clipboard != null) {
			boolean clipBoardAvail = false;
			try {
				clipBoardAvail = clipboard
						.isDataFlavorAvailable(DataFlavor.stringFlavor);
			} catch (IllegalStateException ise) {
				// Accessed by another application
			}
			String selText = textComponent.getSelectedText();
			// TODO Paste action (Clipboard listener)
			if (selText != null) {
				if (cutAction != null)
					cutAction.setEnabled(true);
				if (copyAction != null)
					copyAction.setEnabled(true);
			} else {
				if (cutAction != null)
					cutAction.setEnabled(false);
				if (copyAction != null)
					copyAction.setEnabled(false);
			}
			if (clipBoardAvail) {
				if (pasteAction != null)
					pasteAction.setEnabled(true);
			} else {
				if (pasteAction != null)
					pasteAction.setEnabled(false);
			}
		}
		undoAction.setEnabled(undoManager.canUndo());
		undoAction.putValue(Action.NAME, undoManager.getUndoPresentationName());
		redoAction.setEnabled(undoManager.canRedo());
		redoAction.putValue(Action.NAME, undoManager.getRedoPresentationName());
	}

	public void caretUpdate(CaretEvent arg0) {
		// use caret listener as selection listener
		updateActions();
	}

	public void mousePressed(MouseEvent e) {
	    Component c=e.getComponent();
		if (c.isEnabled() && e.isPopupTrigger()) {
			popupMenu.show(c, e.getX(), e.getY());
			e.consume();
		}

	}

	public void mouseReleased(MouseEvent e) {
	    Component c=e.getComponent();
		if (c.isEnabled() && e.isPopupTrigger()) {
			popupMenu.show(c, e.getX(), e.getY());
			e.consume();
		}

	}

	public void mouseClicked(MouseEvent arg0) {
		// nothing to do
	}

	public void mouseEntered(MouseEvent arg0) {
		// nothing to do

	}

	public void mouseExited(MouseEvent arg0) {
		// nothing to do

	}

	public void undoableEditHappened(UndoableEditEvent e) {
		undoManager.undoableEditHappened(e);
		updateActions();
	}

	public void flavorsChanged(FlavorEvent e) {
		updateActions();
	}
	
	public void addEditActionListener(EditActionsListener editActionsListener){
		synchronized(editActionsListenerList){
			if(!editActionsListenerList.contains(editActionsListener)){
				editActionsListenerList.add(editActionsListener);
			}
		}
	}
	public void removeEditActionListener(EditActionsListener editActionsListener){
		synchronized(editActionsListenerList){
			editActionsListenerList.remove(editActionsListener);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
	 */
	public void focusGained(FocusEvent e) {
		// notify listener about the activated edit actions
		for(EditActionsListener eal:editActionsListenerList){
			eal.providesEditActions(this, editActions);
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		// do nothing
		// a notification of listener is currently not provided
	}
	
	
	public void discardAllEdits(){
	   undoManager.discardAllEdits();
	   updateActions();
	}
	
	
	public void release(){
	    setPopupMenuActiv(false);
	    
	    clipboard.removeFlavorListener(this);
	    textComponent.removeCaretListener(this);
	    textComponent.getDocument().removeUndoableEditListener(this);
	    textComponent.removeFocusListener(this);
	    editActionsListenerList.clear();
	    undoManager.discardAllEdits();
	    undoManager=null;
	    textComponent=null;
	   
	}
	
}
