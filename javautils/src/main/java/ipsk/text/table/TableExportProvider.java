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
public interface TableExportProvider {
    
    
    /**
     * Retrieve table data
     * Table data is a list of groups. A group is a list of records (lines).
     * A record is list of units (tokens)
     * @param columns columns to retrieve
     * @return list of group
     */
    public List<List<List<String>>> tableData(List<ColumnDescriptor> columns);
    
    /**
     * Retrieve complete table data.
     * Table data is a list of groups. A group is a list of records (lines).
     * A record is list of units (tokens)
     * @return list of group
     */
    public List<List<List<String>>> tableData();
    
}
