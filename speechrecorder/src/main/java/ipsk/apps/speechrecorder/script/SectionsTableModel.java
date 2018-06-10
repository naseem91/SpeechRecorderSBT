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

import ipsk.db.speech.Section;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.table.AbstractTableModel;

public class SectionsTableModel extends AbstractTableModel implements PropertyChangeListener {

	private static final int COL_INDEX=0;
	private static final int COL_NAME=1;
	
	private Section[] sections;
	
	
	public SectionsTableModel(){
		sections=new Section[0];
		
	}
	
	public Class<?> getColumnClass(int colIndex){
		if(colIndex==COL_INDEX){
			return Integer.class;
		}else if(colIndex==COL_NAME){
			return String.class;
		}else return null;
			
	}
	public String getColumnName(int colIndex){
		if(colIndex==COL_INDEX){
			return "#";
		}else if(colIndex==COL_NAME){
			return "Name";
		}else return null;
	}
	
	public int getColumnCount() {
		return 2;
		
	}

	public int getRowCount() {
		if (sections==null)return 0;
		return sections.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Section rowSection=sections[rowIndex];
		if (columnIndex==COL_INDEX){
			return rowIndex;
		}else if(columnIndex==COL_NAME){
			return rowSection.getName();
		}else{
			return null;
		}
	}
	
	public void setValueAt(Object aValue,
            int rowIndex,
            int columnIndex){
       if(columnIndex==COL_NAME && aValue instanceof String){
            Section s=sections[rowIndex];
            s.setName((String)aValue);
        }
    }
    
    public boolean isCellEditable(int row, int col){
        if(col==COL_NAME){
          return true;
        }
        return false;
    }
	public Section[] getSections() {
		return sections;
		
	}
	public void setSections(Section[] sections) {
        if (this.sections != null) {
            for (Section oldSection : this.sections) {
                oldSection.removePropertyChangeListener(this);
            }
        }
        this.sections = sections;
        if (this.sections != null) {
            for (Section s : this.sections) {
                s.addPropertyChangeListener(this);
            }
        }
        fireTableDataChanged();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Object src=evt.getSource();
        //String pName=evt.getPropertyName();
        if(src instanceof Section){
            //fireTableDataChanged();
            for(int i=0;i<sections.length;i++){
                if(src==sections[i]){
                    fireTableRowsUpdated(i, i);
                }
            }
        }
    }

}
