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

package ipsk.swing;

import ipsk.util.LocalizableMessage;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.undo.UndoManager;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

public abstract class UndoAction extends AbstractLocalizableAction{

	public final static String NAME = new String("Undo");
    public final static String ACTION_COMMAND = new String("undo");
    public final static String SHORT_DESCRIPTION_VAL=new String("Undo");
    public final static KeyStroke ACCELERATOR_VAL=KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK);
    //public final static int MNEMONIC_VAL=KeyEvent.VK_U;
    
    private LocalizableMessage displayName=new LocalizableMessage(NAME);
    /**
     *  
     */
    public UndoAction() {
        super(ACTION_COMMAND);
        setDisplayName(displayName);
//        setMnemonic(MNEMONIC_VAL);
        setAccelerator(ACCELERATOR_VAL);
    }

    public String getActionCommand() {
        return (String) getValue(Action.ACTION_COMMAND_KEY);
    }

	public abstract void actionPerformed(ActionEvent e);

	/**
	 * Update to the corresponding undo manager
	 * @param undoManager
	 */
	public void update(UndoManager undoManager){
	    boolean canUndo=undoManager.canUndo();
	    setEnabled(canUndo);
	    if(canUndo){
	        putValue(Action.NAME, undoManager.getUndoPresentationName());
	    }else{
//	        putValue(Action.NAME, NAME);
	        setDisplayName(displayName);
	    }
	}

}
