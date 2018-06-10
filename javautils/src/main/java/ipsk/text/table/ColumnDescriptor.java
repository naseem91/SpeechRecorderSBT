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

import ipsk.util.LocalizableMessage;

/**
 * @author klausj
 *
 */
public class ColumnDescriptor {

    private String keyName;
    private boolean required=false;
    private LocalizableMessage localizedName=null;
    private String repString;
    
    
    public ColumnDescriptor(String keyName,boolean required) {
        super();
        this.keyName = keyName;
        this.required=required;
    }

    public ColumnDescriptor(String keyName, boolean required, LocalizableMessage localizedName) {
        super();
        this.keyName = keyName;
        this.required=required;
        this.localizedName = localizedName;
    }

    public String getKeyName() {
        return keyName;
    }

    public LocalizableMessage getLocalizedName() {
        return localizedName;
    }

    public boolean isRequired() {
        return required;
    }
    
    public boolean equals(Object o){
        if( o instanceof ColumnDescriptor){
            ColumnDescriptor othColdescr=(ColumnDescriptor)o;
            return(othColdescr.getKeyName().equals(keyName));
        }
        return false;
    }

    public String toString(){
        if(repString==null){
            if(localizedName!=null){
                repString=localizedName.localize()+" ("+keyName+")";
            }else{
                repString=keyName;
            }
        }
        return(repString);
    }

}
