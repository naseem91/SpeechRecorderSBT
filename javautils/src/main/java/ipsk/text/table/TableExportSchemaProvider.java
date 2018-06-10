//    IPS Java Utils
// 	  (c) Copyright 2015
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

package ipsk.text.table;

import java.util.List;

/**
 * @author klausj
 *
 */
public interface TableExportSchemaProvider {
    
    /**
     * Get list of column descriptors.
     * @return list of column descriptors
     */
    public List<ColumnDescriptor> getColumnDescriptors();
    
    /**
     *  Returns true if the object can be completely restored from exported table data (all columns).
     *  @return true if lossless
     */
    public boolean isCompleteTableLossless();
    
    
}
