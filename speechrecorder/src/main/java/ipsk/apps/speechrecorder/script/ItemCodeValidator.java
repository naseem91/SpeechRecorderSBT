//    Speechrecorder
// 	  (c) Copyright 2011
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


package ipsk.apps.speechrecorder.script;

import ipsk.io.FilenameValidator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author klausj
 *
 */
public class ItemCodeValidator {
    
    
    private Set<String> existingCodes;
    
    public ItemCodeValidator(){
        this(new HashSet<String>());
    }
    public ItemCodeValidator(Set<String> existingCodes){
        super();
        this.existingCodes=existingCodes;
    }
    
    
    
    public String validateItemCode(String itemCode){
       
       FilenameValidator.ValidationResult vr=FilenameValidator.validateFileNameCharacters(itemCode);
       if(!vr.isValid()){
           return vr.getMessage().localize();
       }
       if(existingCodes!=null){
           // we cannot use the contains() method, because we have to check ignoring case (Windows OS)
           for(String ec:existingCodes){
               if(itemCode.equals(ec)){
                   return "duplicate";
               }
               if(itemCode.equalsIgnoreCase(ec)){
                   return "duplicate (ignore case)";
               }
           }
       }
       return null;
    }
    
    public Set<String> getExistingCodes() {
        return existingCodes;
    }
    public void setExistingCodes(Set<String> existingCodes) {
        this.existingCodes = existingCodes;
    }
    
    
}
