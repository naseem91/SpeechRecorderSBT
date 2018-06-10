//    IPS Java Utils
// 	  (c) Copyright 2014
// 	  Institute of Phonetics and Speech Processing,
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

import ipsk.swing.EnumSelectionItem;
import ipsk.swing.EnumVector;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * @author klausj
 *
 */
public abstract class EnumCellEditor<E extends Enum<E>> extends AbstractCellEditor implements TableCellEditor{

    private JComboBox selectBox;
    protected Class<E> enumClass;
    private EnumVector<E> enumVector;
    /**
     * 
     */
    protected EnumCellEditor(Class<E> enumClass) {
        super();
        enumVector = new EnumVector<E>(enumClass);
        selectBox=new JComboBox(enumVector);
    }

    /* (non-Javadoc)
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    @Override
    public Object getCellEditorValue() {
        EnumSelectionItem<E> eSelIt=(EnumSelectionItem<E>)selectBox.getSelectedItem();
        return eSelIt.getEnumVal();
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable, java.lang.Object, boolean, int, int)
     */
    @Override
    public Component getTableCellEditorComponent(JTable arg0, Object value,
            boolean arg2, int arg3, int arg4) {
         if(value instanceof Enum<?>){
             Enum<E> enumVal=(Enum<E>)value;  
             EnumSelectionItem<E> eSelIt=enumVector.getItem(enumVal);
             selectBox.setSelectedItem(eSelIt);
         }
         return selectBox;
    }

}
