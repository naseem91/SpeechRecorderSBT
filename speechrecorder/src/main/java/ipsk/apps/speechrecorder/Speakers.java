//    Speechrecorder
// 	  (c) Copyright 2014
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



package ipsk.apps.speechrecorder;

import ipsk.apps.speechrecorder.db.Speaker;
import ipsk.text.table.ColumnDescriptor;
import ipsk.text.table.TableExportProvider;
import ipsk.text.table.TableExportSchemaProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Speakers  implements TableExportProvider{
   
    public static class SpeakersTableSchemaProvider implements TableExportSchemaProvider{
        static ColumnDescriptor[] colDescriptors=new ColumnDescriptor[]{new ColumnDescriptor("id", true),new ColumnDescriptor("code",true),new ColumnDescriptor("name", true),new ColumnDescriptor("forename", true),new ColumnDescriptor("gender", true),new ColumnDescriptor("accent",true),new ColumnDescriptor("dateOfBirth", true)};
        /* (non-Javadoc)
         * @see ipsk.text.table.TableExportProvider#getColumnDescriptors()
         */
        @Override
        public List<ColumnDescriptor> getColumnDescriptors() {
            List<ColumnDescriptor> cdList=Arrays.asList(colDescriptors);
            return cdList;
        }

        /* (non-Javadoc)
         * @see ipsk.text.table.TableExportProvider#isCompleteTableLossless()
         */
        @Override
        public boolean isCompleteTableLossless() {

            return false;
        }
    }
    
    private static SpeakersTableSchemaProvider tableSchemaProvider=new SpeakersTableSchemaProvider();
    
    private List<ipsk.apps.speechrecorder.db.Speaker> speakers = new ArrayList<ipsk.apps.speechrecorder.db.Speaker>();

    
    public List<ipsk.apps.speechrecorder.db.Speaker> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<ipsk.apps.speechrecorder.db.Speaker> speakers) {
        this.speakers = speakers;
    }
    
    public int maxID(){
        int maxID=-1;
        for(Speaker s:speakers){
            int sId=s.getPersonId();
            if(sId>maxID){
                maxID=sId;
            }
        }
        return maxID;
    }
    
   
    /* (non-Javadoc)
     * @see ipsk.text.table.TableExportProvider#tableData(java.util.List)
     */
    @Override
    public List<List<List<String>>> tableData(List<ColumnDescriptor> columns) {
        List<List<List<String>>> d=new ArrayList<List<List<String>>>(1);
        List<List<String>> dg=new ArrayList<List<String>>(1);
        for(Speaker spk:speakers){
            List<String> row=new ArrayList<String>();
            for(ColumnDescriptor cd:columns){
                String key=cd.getKeyName();
                if("id".equals(key)){
                    row.add(Integer.toString(spk.getPersonId()));
                }else if("code".equals(key)){
                    row.add(spk.getCode());
                }else if("name".equals(key)){
                    row.add(spk.getName());
                }else if("forename".equals(key)){
                    row.add(spk.getForename());
                }else if("gender".equals(key)){
                    row.add(spk.getGender());
                }else if("accent".equals(key)){
                    row.add(spk.getAccent());
                }else if("dateOfBirth".equals(key)){
                    row.add(spk.getDateOfBirthString());
                }
            }
            dg.add(row);
        }
        d.add(dg);
        return d;
    }

    /* (non-Javadoc)
     * @see ipsk.text.table.TableExportProvider#tableData()
     */
    @Override
    public List<List<List<String>>> tableData() {
       return tableData(tableSchemaProvider.getColumnDescriptors());
    }

    public static SpeakersTableSchemaProvider getTableSchemaProvider() {
        return tableSchemaProvider;
    }

    
}