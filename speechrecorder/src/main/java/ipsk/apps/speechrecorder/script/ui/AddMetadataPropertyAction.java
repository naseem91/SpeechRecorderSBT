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
 * Date  : Jul 28, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.script.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

public abstract class AddMetadataPropertyAction extends AbstractAction{

    private static final long serialVersionUID = 3643722843406896083L;
    public final static String NAME = new String("Add metadata property");
    public final static String ACTION_COMMAND = new String("metadata_property_add");
    public final static String SHORT_DESCRIPTION_VAL=new String("Add new metadata property");
//    public final static KeyStroke ACCELERATOR_VAL=KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK);
//    public final static int MNEMONIC_VAL=KeyEvent.VK_S;
    
    
    /**
     *  
     */
    public AddMetadataPropertyAction() {
        super(NAME);
        
        putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND);
//        putValue(Action.ACCELERATOR_KEY, ACCELERATOR_VAL);
//        putValue(Action.MNEMONIC_KEY, MNEMONIC_VAL);
        //putValue(Action.SHORT_DESCRIPTION,SHORT_DESCRIPTION_VAL);
    }

    public String getActionCommand() {
        return (String) getValue(Action.ACTION_COMMAND_KEY);
    }

	public abstract void actionPerformed(ActionEvent e);


}
