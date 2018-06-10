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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ipsk.util.LocalizableMessage;

/**
 * Helper class to organize structured action trees.
 * @author klausj
 *
 */
public class ActionList extends AbstractActionNode{
    
 
    protected String key;
    
    
    private List<ActionNode> children=new ArrayList<ActionNode>();
    public ActionList(String key){
        super();
        this.key=key;
    }
    public ActionList(String key,LocalizableMessage displayName){
        super(displayName);
        this.key=key;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public LocalizableMessage getDisplayName(){
        LocalizableMessage dn=super.getDisplayName();
        if(dn==null){
            // Use key as fallback name
            dn= new LocalizableMessage(key);
        }
        return dn;
    }
    public ActionList deepClone(){
         ActionList dClone=new ActionList(key);
        for(ActionNode an:getChildren()){
            if(an instanceof ActionList){
                ActionList chAf=(ActionList)an;
                ActionList cdClone=chAf.deepClone();
                dClone.getChildren().add(cdClone);
            }else if (an instanceof ActionLeaf){
                // actions are NOT cloned !
                dClone.getChildren().add(an);
            }
        }
        return dClone;
    }
    
    
    public void merge(ActionList merge){
        if(merge!=null){
        List<ActionNode> chs=getChildren();
        List<ActionNode> mChs=merge.getChildren();
        for(ActionNode mCh:mChs){
            boolean merged=false;
            if(mCh instanceof ActionList){
                ActionList mAf=(ActionList)mCh;
                for(ActionNode ch:chs){
                    if(mCh.equals(ch)){
                        ActionList af=(ActionList)ch;
                        // already exists
                        af.merge(mAf);
                        merged=true;
                        break;
                    }
                }
                if(!merged){
                    getChildren().add(mAf);
                }
            }else {
               
                for(ActionNode ch:chs){
                    if(mCh.equals(ch)){
                        merged=true;
                        break;
                    }
                }
                if(!merged){
                    getChildren().add(mCh);
                }
            }
        }
        }
    }
    
    public ActionList copymerge(ActionList merge){
        ActionList copy=deepClone();
        if(merge!=null){
            copy.merge(merge);
        }
        return copy;
    }
   
    public boolean add(ActionNode arg0) {
        return children.add(arg0);
    }
    public void add(int arg0, ActionNode arg1) {
        children.add(arg0, arg1);
    }
    public boolean addAll(Collection<? extends ActionNode> arg0) {
        return children.addAll(arg0);
    }
    public boolean addAll(int arg0, Collection<? extends ActionNode> arg1) {
        return children.addAll(arg0, arg1);
    }
    public void clear() {
        children.clear();
    }
    public boolean contains(Object arg0) {
        return children.contains(arg0);
    }
    public boolean containsAll(Collection<?> arg0) {
        return children.containsAll(arg0);
    }
    public ActionNode get(int arg0) {
        return children.get(arg0);
    }
    public boolean remove(Object arg0) {
        return children.remove(arg0);
    }
    public boolean removeAll(Collection<?> arg0) {
        return children.removeAll(arg0);
    }
    public ActionNode set(int arg0, ActionNode arg1) {
        return children.set(arg0, arg1);
    }
    public int size() {
        return children.size();
    }
    public List<ActionNode> subList(int arg0, int arg1) {
        return children.subList(arg0, arg1);
    }
    
  
   
    public boolean equals(Object o){
        if(!(o instanceof ActionList)){
            return false;
        }else{
            ActionList oAF=(ActionList)o;
            if(key==null){
                return false;
            }else{
                return key.equals(oAF.getKey());
            }
        }
    }
    public List<ActionNode> getChildren() {
        return children;
    }
    public void setChildren(List<ActionNode> children) {
        this.children = children;
    }
    
}
