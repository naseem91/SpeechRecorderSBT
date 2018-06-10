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

package ipsk.swing.action.tree;

import ipsk.util.LocalizableMessage;

import java.util.List;

/**
 * Helper class to organize structured action trees.
 * @author klausj
 *
 */
public class ActionFolder extends ActionList{
    
    // top level
   public static final String FILE_FOLDER_KEY="file";
   public static final String EDIT_FOLDER_KEY="edit";
   public static final String VIEW_FOLDER_KEY="view";
   public static final String NAVIGATE_FOLDER_KEY="navigate";
   
   public static final String HELP_FOLDER_KEY="help";
   

    private boolean isTopLevel=false;
    
   
    
    public ActionFolder(String key,LocalizableMessage displayName){
        this(false,key,displayName);
    }
    
    public ActionFolder(String key){
        super(key);
   
        this.isTopLevel=false;
    }
    public ActionFolder(boolean isTopLevel,String key,LocalizableMessage displayName){
        super(key,displayName);
        this.isTopLevel=isTopLevel;
       
    }
 
    
    public static ActionFolder buildTopLevelFolder(String key){
        // TODO localize
        
        String m=key.substring(0,1);
        m=m.toUpperCase();
        m=m.concat(key.substring(1));
        LocalizableMessage displayName=new LocalizableMessage(m);
        return new ActionFolder(true,key, displayName);
    }
    
    public Object clone(){
        return new ActionFolder(isTopLevel, key, displayName);
    }
//    public ActionFolder deepClone(){
//         ActionFolder dClone=new ActionFolder(isTopLevel, key, displayName);
//        for(ActionNode an:getChildren()){
//            if(an instanceof ActionFolder){
//                ActionFolder chAf=(ActionFolder)an;
//                ActionFolder cdClone=chAf.deepClone();
//                dClone.getChildren().add(cdClone);
//            }else if (an instanceof ActionGroup){
//                ActionGroup chAg=(ActionGroup)an;
//                ActionGroup cdClone=chAg.deepClone();
//                dClone.getChildren().add(cdClone);
//            }else if (an instanceof ActionLeaf){
//                // actions are NOT cloned !
//                dClone.getChildren().add(an);
//            }
//        }
//        return dClone;
//    }
//    
    
 
   
    public boolean equals(Object o){
        if(!(o instanceof ActionFolder)){
            return false;
        }else{
            ActionFolder oAF=(ActionFolder)o;
            if(key==null){
                return false;
            }else{
                return key.equals(oAF.getKey());
            }
        }
    }

 
   
}
