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

package ipsk.apps.speechrecorder.script;

import ipsk.apps.speechrecorder.script.ui.ScriptUI;
import ipsk.db.speech.Section;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class SectionTransferHandler extends TransferHandler {
	
	 private boolean shouldRemove;

	public boolean importData(JComponent c, Transferable t) {
	        Section section;
	        if (canImport(c, t.getTransferDataFlavors())) {
	            ScriptUI sui = (ScriptUI)c;
	           
	            try {
	                section = (Section)t.getTransferData(Section.CLASS_DATA_FLAVOR);
	             
	                sui.insert(section);
	                return true;
	            } catch (UnsupportedFlavorException ufe) {
	                System.err.println("importData: unsupported data flavor");
	            } catch (IOException ioe) {
	                System.err.println("importData: I/O exception");
	            }
	        }
	        return false;
	    }

	    protected Transferable createTransferable(JComponent c) {
	        Section section = ((ScriptUI)c).getSelectedSection();
	        shouldRemove = true;
	        return section;
	    }

	    public int getSourceActions(JComponent c) {
	        return COPY_OR_MOVE;
	    }

	    protected void exportDone(JComponent c, Transferable data, int action) {
	        if (shouldRemove && (action == MOVE)) {
	            ((ScriptUI)c).removeSelectedSection();
	        }
	        //section = null;
	    }

	    public boolean canImport(JComponent c, DataFlavor[] flavors) {
	        for (int i = 0; i < flavors.length; i++) {
	            if (Section.CLASS_DATA_FLAVOR.equals(flavors[i])) {
	                return true;
	            }
	        }
	        return false;
	    }
}
