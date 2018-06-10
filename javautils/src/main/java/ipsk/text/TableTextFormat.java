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

package ipsk.text;


/**
 * @author klausj
 *
 */
public class TableTextFormat {

    private char[] groupSeparator;
    private char[] recordSeparator;
    private char[] unitSeparator;
    
    public TableTextFormat(char[] groupSeparator, char[] recordSeparator,
            char[] unitSeparator) {
        super();
        this.groupSeparator = groupSeparator;
        this.recordSeparator = recordSeparator;
        this.unitSeparator = unitSeparator;
    }

    public char[] getGroupSeparator() {
        return groupSeparator;
    }

    public void setGroupSeparator(char[] groupSeparator) {
        this.groupSeparator = groupSeparator;
    }

    public char[] getRecordSeparator() {
        return recordSeparator;
    }

    public void setRecordSeparator(char[] recordSeparator) {
        this.recordSeparator = recordSeparator;
    }

    public char[] getUnitSeparator() {
        return unitSeparator;
    }

    public void setUnitSeparator(char[] unitSeparator) {
        this.unitSeparator = unitSeparator;
    }
    
   
    
  
}
