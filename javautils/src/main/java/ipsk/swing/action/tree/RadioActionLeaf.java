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

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;

/**
 * @author klausj
 *
 */
public abstract class RadioActionLeaf extends AbstractActionLeaf {

//    private Set<RadioActionLeaf> group=new HashSet<RadioActionLeaf>();
    private RadioActionGroup radioActionGroup;
    /**
     * @param displayName
     */
    public RadioActionLeaf(LocalizableMessage displayName) {
        super(displayName);
    }

    /* (non-Javadoc)
     * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public abstract void actionPerformed(ActionEvent arg0);

//    public Set<RadioActionLeaf> getGroup() {
//        return group;
//    }
//
//    public void setGroup(Set<RadioActionLeaf> group) {
//        this.group = group;
//    }
    
    public boolean isSelected() {
        Object selObj=getValue(Action.SELECTED_KEY);
        if(selObj!=null){
            if(selObj.equals(true)){
                return true;
            }
        }
        return false;
    }

    public void setSelected(boolean selected) {
//        if(selected != isSelected()){
            putValue(Action.SELECTED_KEY, selected);
//        }
    }

    public RadioActionGroup getRadioActionGroup() {
        return radioActionGroup;
    }

    public void setRadioActionGroup(RadioActionGroup radioActionGroup) {
        this.radioActionGroup = radioActionGroup;
    }


}
