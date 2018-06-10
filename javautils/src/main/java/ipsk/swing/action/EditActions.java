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

package ipsk.swing.action;

import ipsk.swing.RedoAction;
import ipsk.swing.UndoAction;

import javax.swing.Action;

/**
 * @author klausj
 *
 */
public class EditActions {

    private Action cutAction;
    private Action copyAction;
    private Action pasteAction;
    private Action undoAction;
    private Action redoAction;
    
    public EditActions(Action cutAction,Action copyAction,Action pasteAction){
       this(cutAction,copyAction,pasteAction,null,null);
    }
   
	public EditActions(Action cutAction, Action copyAction,
			Action pasteAction, UndoAction undoAction, RedoAction redoAction) {
		 this.cutAction=cutAction;
	        this.copyAction=copyAction;
	        this.pasteAction=pasteAction;
	        this.undoAction=undoAction;
	        this.redoAction=redoAction;
	}
	public Action getCutAction() {
        return cutAction;
    }
    public void setCutAction(Action cutAction) {
        this.cutAction = cutAction;
    }
    public Action getCopyAction() {
        return copyAction;
    }
    public void setCopyAction(Action copyAction) {
        this.copyAction = copyAction;
    }
    public Action getPasteAction() {
        return pasteAction;
    }
    public void setPasteAction(Action pasteAction) {
        this.pasteAction = pasteAction;
    }
    public Action getUndoAction() {
        return undoAction;
    }
    public void setUndoAction(Action undoAction) {
        this.undoAction = undoAction;
    }
    public Action getRedoAction() {
        return redoAction;
    }
    public void setRedoAction(Action redoAction) {
        this.redoAction = redoAction;
    }
}
