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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * @author klausj
 *
 */
public abstract class AbstractActionLeaf extends AbstractActionNode implements ActionLeaf{
 
//    public class DelegateAction{
//        private AbstractActionLeaf al;
//        public DelagteAction(AbstractActionLeaf al){
//            super();
//            this.al=al;
//        }
//    protected void firePropertyChange(String propertyName,
//            Object oldValue,
//            Object newValue){
//            
//    }
//    }
//    private AbstractAction action;
    
    private HashMap<String, Object> propertyMap=new HashMap<String, Object>();
    private boolean enabled=true;
    private PropertyChangeSupport propertyChangeSupport=new PropertyChangeSupport(this);
    private Icon icon;
    
    
    public AbstractActionLeaf(LocalizableMessage displayName){
        super(displayName);
//        action=new AbstractAction() {
//            
//            public void actionPerformed(ActionEvent arg0) {
//                this.actionPerformed(arg0);
//                
//            }
//        };
        if(displayName!=null){
            putValue(Action.NAME, displayName.localize());
        }
      
    }
    
    public abstract void actionPerformed(ActionEvent arg0);

//    public void addPropertyChangeListener(PropertyChangeListener arg0) {
//        action.addPropertyChangeListener(arg0);
//    }
//
//    public Object getValue(String arg0) {
//        return action.getValue(arg0);
//    }
//
//    public boolean isEnabled() {
//        return action.isEnabled();
//    }
//
//    public void putValue(String arg0, Object arg1) {
//        action.putValue(arg0, arg1);
//    }
//
//    public void removePropertyChangeListener(PropertyChangeListener arg0) {
//        action.removePropertyChangeListener(arg0);
//    }
//
//    public void setEnabled(boolean arg0) {
//        action.setEnabled(arg0);
//    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    /* (non-Javadoc)
     * @see javax.swing.Action#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
        
    }

    /* (non-Javadoc)
     * @see javax.swing.Action#getValue(java.lang.String)
     */
    public Object getValue(String key) {
        return propertyMap.get(key);
    }

    /* (non-Javadoc)
     * @see javax.swing.Action#isEnabled()
     */
    public boolean isEnabled() {
        return enabled;
    }

    /* (non-Javadoc)
     * @see javax.swing.Action#putValue(java.lang.String, java.lang.Object)
     */
    public void putValue(String key, Object value) {
        Object oldValue=propertyMap.get(key);
        propertyMap.put(key,value);
        if((value!=null && !value.equals(oldValue)) || (oldValue!=null)){
           propertyChangeSupport.firePropertyChange(key, oldValue, value);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.Action#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /* (non-Javadoc)
     * @see javax.swing.Action#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
       boolean oldValue=this.enabled;
       this.enabled=enabled;
       if(this.enabled!=oldValue){
           propertyChangeSupport.firePropertyChange("enabled", oldValue, this.enabled);
       }
    }


}
