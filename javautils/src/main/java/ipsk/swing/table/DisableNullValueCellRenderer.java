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

 
package ipsk.swing.table;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Wrapper to disable the default cell renderer component if the value of the cell is null.
 * This is currently used to disable the check box of Boolean null values.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class DisableNullValueCellRenderer implements TableCellRenderer {

    private TableCellRenderer defRenderer;
    
    /**
     * Create Cell renderer wrapper which disables components for null values. 
     * @param defRenderer
     */
    public DisableNullValueCellRenderer(TableCellRenderer defRenderer){
        super();
        this.defRenderer=defRenderer;
        //setOpaque(true);
    }
    
    /**
     * Implementation of TableCellRenderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component defComp = defRenderer.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        if (defComp != null) {
            defComp.setEnabled((value != null));
        }
        return defComp;
    }

}
