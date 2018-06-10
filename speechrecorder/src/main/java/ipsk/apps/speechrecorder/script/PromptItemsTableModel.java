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

import ipsk.apps.speechrecorder.MIMETypes;
import ipsk.db.speech.Mediaitem;
import ipsk.db.speech.Nonrecording;
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.Recording;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.AbstractTableModel;

public class PromptItemsTableModel extends AbstractTableModel implements PropertyChangeListener {

	public static final int COL_INDEX=0;
	public static final int COL_FILE = 1;
	public static final int COL_PROMPT=2;
	private static final int LAST_COL_INDEX=COL_PROMPT;
	
	private PromptItem[] promptItems;
	
	
	public PromptItemsTableModel(){
		promptItems=new PromptItem[0];
		
	}
	
	public Class<?> getColumnClass(int colIndex){
		if(colIndex==COL_INDEX){
			return Integer.class;
		}else if(colIndex==COL_FILE){
            return String.class;
        }else if(colIndex==COL_PROMPT){
			return String.class;
		}else return null;
			
	}
	public String getColumnName(int colIndex){
		if(colIndex==COL_INDEX){
			return "Index in section";
		}else if(colIndex==COL_FILE){
		    return "File";
		}else if(colIndex==COL_PROMPT){
			return "Prompt";
		}else return null;
	}
	
	public int getColumnCount() {
		return LAST_COL_INDEX+1;
		
	}

	public int getRowCount() {
		
		return promptItems.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
	    if(columnIndex>LAST_COL_INDEX || rowIndex>promptItems.length){
	        return null;
	    }
		PromptItem rowItem=promptItems[rowIndex];
		Recording r=null;
		if(rowItem instanceof Recording){
		    r=(Recording)rowItem;
		}
		if (columnIndex==COL_INDEX){
			return rowIndex;
		}else if (columnIndex==COL_FILE){
            if(r==null){
                return "";
            }else{
                return r.getItemcode();
            }
        }else if(columnIndex==COL_PROMPT){
			return rowItem.getDescription();
		}else{
			return null;
		}
	}
	
	public void setValueAt(Object aValue,
            int rowIndex,
            int columnIndex){
//	    if(columnIndex<=LAST_COL_INDEX || rowIndex<=promptItems.length){
	    if(columnIndex==COL_FILE){
	        PromptItem pi=promptItems[rowIndex];
	        if(pi instanceof Recording && aValue instanceof String){
	            Recording r=(Recording)pi;
	            r.setItemcode((String)aValue);
	        }
	    }else if(columnIndex==COL_PROMPT && aValue instanceof String){
	        PromptItem pi=promptItems[rowIndex];
	        // TODO
	        pi.getMediaitems().get(0).setText((String)aValue);
	    }
//	    }
	}
	
	public boolean isCellEditable(int row, int col){
	    PromptItem pi=promptItems[row];
	    if(col==COL_FILE){
	        // non recording items do not have an item code
	        if(pi instanceof Nonrecording){
	            return false;
	        }else{
	            return true;
	        }
	        
	    }
	    if(col==COL_PROMPT){
	       
	        String mType=pi.getMediaitems().get(0).getNNMimetype();
	        for(String ptMimeType:MIMETypes.PLAINTEXTMIMETYPES){
	            if(mType.equals(ptMimeType)){
	                return true;
	            }
	        }
	    }
	    return false;
	}

	public PromptItem[] getPromptItems() {
		return promptItems;
	}

	public void setPromptItems(PromptItem[] promptItems) {
        if (this.promptItems != null) {
            for (PromptItem oldPromptItem : this.promptItems) {
                Mediaitem mi=oldPromptItem.getMediaitems().get(0);
                mi.removePropertyChangeListener(this);
                oldPromptItem.removePropertyChangeListener(this);
            }
        }
        this.promptItems = promptItems;
        if (this.promptItems != null) {
            for (PromptItem pi : this.promptItems) {
                Mediaitem mi=pi.getMediaitems().get(0);
                mi.addPropertyChangeListener(this);
                pi.addPropertyChangeListener(this);
            }
        }
        fireTableDataChanged();
    }
	

    public void propertyChange(PropertyChangeEvent evt) {
        Object src=evt.getSource();
        //String pName=evt.getPropertyName();
        if(src instanceof PromptItem){
            for(int i=0;i<promptItems.length;i++){
                if(src==promptItems[i]){
                    fireTableRowsUpdated(i, i);
                }
            }
        }else if(src instanceof Mediaitem){
            for(int i=0;i<promptItems.length;i++){
                Mediaitem mi=promptItems[i].getMediaitems().get(0);
                if(src==mi){
                    fireTableRowsUpdated(i, i);
                }
            }
        }
        
    }
	

}
