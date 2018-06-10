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

import ipsk.db.speech.Property;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class MetadataTableModel extends AbstractTableModel {

	private static final int COL_KEY=0;
	private static final int COL_VALUE=1;
	
	private List<Property> metadataProperties;
	
	
	public MetadataTableModel(){
	    super();
		metadataProperties=new ArrayList<Property>();
		
	}
	
	public Class<?> getColumnClass(int colIndex){
		if(colIndex==COL_KEY){
			return String.class;
		}else if(colIndex==COL_VALUE){
			return String.class;
		}else return null;
			
	}
	public String getColumnName(int colIndex){
		if(colIndex==COL_KEY){
			return "Key";
		}else if(colIndex==COL_VALUE){
			return "Value";
		}else return null;
	}
	
	public int getColumnCount() {
		return 2;
		
	}

	public int getRowCount() {
		return metadataProperties.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
	    if(rowIndex>=metadataProperties.size()){
	        return null;
	    }
		Property p=metadataProperties.get(rowIndex);
		if (columnIndex==COL_KEY){
			return p.getKey();
		}else if(columnIndex==COL_VALUE){
			return p.getValue();
		}else{
			return null;
		}
	}
	
	public void setValueAt(Object aValue,
            int rowIndex,
            int columnIndex){
       if(aValue instanceof String && rowIndex>=0 && rowIndex < metadataProperties.size()){
           String aValueStr=(String)aValue;
           Property p=metadataProperties.get(rowIndex);
           if(columnIndex==COL_KEY){
               p.setKey(aValueStr);
           }else if(columnIndex==COL_VALUE){
               p.setValue(aValueStr);
           }
        }
    }
    
    public boolean isCellEditable(int row, int col){
          return true;
    }
	
	public void setMetadataProperties(List<Property> metadata) {
        this.metadataProperties = metadata;
        fireTableDataChanged();
    }

    public List<Property> getMetadataProperties() {
        return metadataProperties;
    }

   

}
