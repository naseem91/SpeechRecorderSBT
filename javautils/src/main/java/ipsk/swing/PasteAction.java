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

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public abstract class PasteAction extends AbstractLocalizableAction{

  
    
    public final static String ACTION_COMMAND = new String("paste");
    public final static String NAME=new String("Paste");
    public final static KeyStroke ACCELERATOR_VAL=KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK);
    public final static int MNEMONIC_VAL=KeyEvent.VK_P;
    private LocalizableMessage displayName=new LocalizableMessage(NAME);
    /**
     *  
     */
    public PasteAction() {
        super(ACTION_COMMAND);
        setDisplayName(displayName);
        setMnemonic(MNEMONIC_VAL);
        setAccelerator(ACCELERATOR_VAL);
    }

    public String getActionCommand() {
        return (String) getValue(Action.ACTION_COMMAND_KEY);
    }

	public abstract void actionPerformed(ActionEvent e);


}
