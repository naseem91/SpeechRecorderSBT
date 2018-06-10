//    Speechrecorder
// 	  (c) Copyright 2012
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



package ipsk.apps.speechrecorder.config.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ipsk.apps.speechrecorder.config.MixerName;

import javax.swing.table.AbstractTableModel;

/**
 * @author klausj
 *
 */
public class AudioDevicesTableModel extends AbstractTableModel {
   
	private static final long serialVersionUID = 1L;
	private static final int COL_INTERFACE=0;
    private static final int COL_PROVIDERID=1;
    private static final int COL_DEVICE = 2;
    private static final int COL_REGEX=3;
    private static final int LAST_COL_INDEX=COL_REGEX;
    
    
    private List<MixerName> deviceNames=new ArrayList<MixerName>();
    
    public String getColumnName(int colIndex){
        if(colIndex==COL_INTERFACE){
            return "Interface";
        }else if(colIndex==COL_PROVIDERID){
            return "Provider ID";
        }else if(colIndex==COL_DEVICE){
            return "Device";
        }else if(colIndex==COL_REGEX){
            return "Regular expression";
        }else return null;
    }
    
    public Class<?> getColumnClass(int colIndex){
        if(colIndex==COL_INTERFACE){
            return String.class;
        }else if(colIndex==COL_PROVIDERID){
            return String.class;
        }else if(colIndex==COL_DEVICE){
            return String.class;
        }else if(colIndex==COL_REGEX){
            return Boolean.class;
        }else return null;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        
        return LAST_COL_INDEX+1;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return deviceNames.size();
    }
    
    public boolean isCellEditable(int rowIndex,
            int colIndex){
        if(colIndex==COL_INTERFACE){
//            // only editable if the provider ID is not set
//            // a particular provider has a fixed interface type  
//            if(rowIndex>=deviceNames.size()){
//                return false;
//            }
//            MixerName rowMn=deviceNames.get(rowIndex);
//            String rowMnProvId=rowMn.getProviderId();
//            
//            return (rowMnProvId==null || rowMnProvId.equals(""));
            // Not editable for now
            // deferred to future version
            return false;
        }else if(colIndex==COL_PROVIDERID){
            // Not editable for now
            // deferred to future version
//            return true;
            return false;
        }else if(colIndex==COL_DEVICE){
            return true;
        }else if(colIndex==COL_REGEX){
            return true;
        }else return false;
        
    }

   
    public Object getValueAt(int row, int col) {
        if(row>=deviceNames.size() || col>LAST_COL_INDEX){
            return null;
        }
        MixerName rowMn=deviceNames.get(row);
        if(col==COL_INTERFACE){
            return rowMn.getInterfaceName();
        }else if(col==COL_PROVIDERID){
            
            String providerId=rowMn.getProviderId();
            if(providerId==null){
                return "";
            }else{
                return providerId;
            }
        }else if(col==COL_DEVICE){
            return rowMn.getName();
        }else if(col==COL_REGEX){
            return rowMn.isRegex();
        }
        return null;
    }

    public void setValueAt(Object aValue,
            int rowIndex,
            int col){
        if(rowIndex<=deviceNames.size()){
            MixerName mn=deviceNames.get(rowIndex);
            if(col==COL_INTERFACE){
                if(aValue instanceof String){
                    String strVal=(String)aValue;
                    mn.setInterfaceName(strVal);
                }
            }else if(col==COL_PROVIDERID){
                if(aValue instanceof String){
                    String strVal=(String)aValue;
                    if(strVal==null || "".equals(strVal)){
                        mn.setProviderId(null);
                    }else{
                        mn.setProviderId(strVal);
                    }
                }
            }else if(col==COL_DEVICE){
                if(aValue instanceof String){
                    String strVal=(String)aValue;
                    mn.setName(strVal);
                }

            }else if(col==COL_REGEX){
                if(aValue instanceof Boolean){
                    Boolean bVal=(Boolean)aValue;
                    mn.setRegex(bVal);
                }
            }
            fireTableDataChanged();
        }
    }

    public boolean add(MixerName arg0) {
        
        boolean ret=deviceNames.add(arg0);
        if(ret){
            fireTableDataChanged();
        }
        return ret;
    }

    public void clear() {
        deviceNames.clear();
        fireTableDataChanged();
    }

    public MixerName get(int arg0) {
        MixerName ret=deviceNames.get(arg0);
        fireTableDataChanged();
        return ret;
    }

    public MixerName remove(int arg0) {
        MixerName ret=deviceNames.remove(arg0);
        fireTableDataChanged();
        return ret;
    }

    public boolean remove(MixerName arg0) {
        boolean ret= deviceNames.remove(arg0);
        fireTableDataChanged();
        return ret;
        
    }

    public int size() {
        return deviceNames.size();
    }

    public boolean contains(MixerName o) {
        return deviceNames.contains(o);
    }

    public boolean containsAll(Collection<MixerName> c) {
        return deviceNames.containsAll(c);
    }

    public List<MixerName> getDeviceNames() {
        return deviceNames;
    }

}
