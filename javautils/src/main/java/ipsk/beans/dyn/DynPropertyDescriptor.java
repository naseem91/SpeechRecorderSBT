//    IPS Java Utils
// 	  (c) Copyright 2011
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

package ipsk.beans.dyn;

import ipsk.util.LocalizableMessage;

import java.lang.reflect.Type;

/**
 * @author klausj
 *
 */
public class DynPropertyDescriptor {
    private Type type;
    private String name;
    private LocalizableMessage displayName;
    private LocalizableMessage description;
    
    
    public DynPropertyDescriptor(String name,Type type){
        this.name=name;
        this.type=type;
    }
    public Type getType() {
        return type;
    }
   
    public String getName() {
        return name;
    }
  
    public LocalizableMessage getDisplayName() {
        return displayName;
    }
    public void setDisplayName(LocalizableMessage displayName) {
        this.displayName = displayName;
    }
    public LocalizableMessage getDescription() {
        return description;
    }
    public void setDescription(LocalizableMessage description) {
        this.description = description;
    }
    
}
