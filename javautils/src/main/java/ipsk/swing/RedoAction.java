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

public abstract class RedoAction extends AbstractLocalizableAction{

	public final static String NAME = new String("Redo");
    public final static String ACTION_COMMAND = new String("redo");
    public final static String SHORT_DESCRIPTION_VAL=new String("Redo");
    public final static KeyStroke ACCELERATOR_VAL=KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK);
    //public final static int MNEMONIC_VAL=KeyEvent.VK_R;
    
    private LocalizableMessage displayName=new LocalizableMessage(NAME);
    /**
     *  
     */
    public RedoAction() {
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
        boolean canRedo=undoManager.canRedo();
        setEnabled(canRedo);
        if(canRedo){
        putValue(Action.NAME, undoManager.getRedoPresentationName());
        }else{
            putValue(Action.NAME, NAME);
        }
    }
}
