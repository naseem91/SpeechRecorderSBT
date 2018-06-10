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

package ipsk.text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * @author klausj
 *
 */
public class TableWriter extends BufferedWriter{

    
    private Writer writer;
    private TableTextFormat format;
    
    /**
     * 
     */
    public TableWriter(Writer writer,TableTextFormat format) {
       super(writer);
       this.writer=writer;
       this.format=format;
    }
  
   
   
    private void writeRecord(List<String> record) throws IOException{
        int recordSize=record.size();
        for(int ui=0;ui<recordSize;ui++){
            String wr="";
            String r=record.get(ui);
            if(r!=null){
                wr=r;
            }
            writer.write(wr);
            if(ui<recordSize-1){
                writer.write(format.getUnitSeparator());
            }
        }
    }
    
    public void writeRecords(List<List<String>> records) throws IOException{
        int recordsSize=records.size();
        for(int ri=0;ri<recordsSize;ri++){
            writeRecord(records.get(ri));
            if(ri<recordsSize-1){
                writer.write(format.getRecordSeparator());
            }
        }
    }
    
    
    public void writeGroups(List<List<List<String>>> groups) throws IOException{
        int groupsSize=groups.size();
        char[] groupSeparator=format.getGroupSeparator();
        for(int gi=0;gi<groupsSize;gi++){
            writeRecords(groups.get(gi));
            if(gi<groupsSize-1 && groupSeparator!=null){
                writer.write(groupSeparator);
            }
        }
    }

  

}
