//    IPS Java Utils
// 	  (c) Copyright 2009-2011
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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

/**
 * Reads table data from files.
 * @author klausj
 *
 */
public class TableReader{
    
    public final static char DEFAULT_FIELD_SEPARATOR='\t';
    
    private char fieldSeparator=DEFAULT_FIELD_SEPARATOR;
    
    private LineNumberReader lineNumberReader;
    
   

    /**
     * Create table reader.
     * @param in reader to read a text table from
     */
    public TableReader(Reader in) {
        super();
        lineNumberReader=new LineNumberReader(in);
    }

    
    public String[] readLineColumns() throws IOException{
        String line=lineNumberReader.readLine();
        if(line==null){
            return null;
        } 
       return StringTokenizer.split(line,fieldSeparator);
    }
   

    public char getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(char fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }


}
