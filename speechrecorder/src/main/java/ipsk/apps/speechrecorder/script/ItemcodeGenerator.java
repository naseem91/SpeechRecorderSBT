//    Speechrecorder
// 	  (c) Copyright 2012
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

import ipsk.util.collections.CollectionChangedEvent;
import ipsk.util.collections.CollectionListener;
import ipsk.util.collections.ObservableList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;
import java.util.Set;

/**
 * @author klausj
 *
 */
public class ItemcodeGenerator implements CollectionListener, PropertyChangeListener {
    
    private ItemcodeGeneratorConfiguration config;

    private PropertyChangeSupport propertyChangeSupport;
   
    
    private Integer counterValue=null;
//    private String sectionName;
 
    private ObservableList<String> itemcodesList;
    
 
    public ItemcodeGenerator(){
        this(new ItemcodeGeneratorConfiguration());
    }
    
    public ItemcodeGenerator(ItemcodeGeneratorConfiguration itemcodeGeneratorConfiguration){
        super();
       this.config=itemcodeGeneratorConfiguration;
       this.config.addPropertyChangeListener(this);
       propertyChangeSupport=new PropertyChangeSupport(this);
       
    }
    
    private String getItemCode(Integer counterVal){
    	if(config.isActive()){
            StringBuffer sb=new StringBuffer();
            String prefix=config.getPrefix();
            if(prefix!=null){
                sb.append(prefix);
            }
            String countstrValue;
            DecimalFormat counterFormat=config.counterFormat();
           
            if(counterFormat!=null){
                countstrValue=counterFormat.format(counterVal);
            }else{
                countstrValue=counterVal.toString();
            }
            sb.append(countstrValue);
            //       String postfix=getPostfix();
            //       if(postfix!=null){
            //           sb.append(postfix);
            //       }
            return sb.toString();
        }else{
            return "";
        }
    }
      
    public String getItemCode(){
        return getItemCode(getCounterValue());
    }
    
    public void next(){
        if(config.isActive()){
       int cv=getCounterValue();
       int increment=config.getIncrement();
       setCounterValue(cv+increment);
        }
    }
    
    public void toNext(Set<String> existingItemCodes){
    	if(config.isActive()){
    		Integer counterVal=getCounterValue();
    		boolean exists=true;
    		int increment=config.getIncrement();
    		do{
    			String itemcode=getItemCode(counterVal);
    			exists=existingItemCodes.contains(itemcode);
    			if(exists){
    				counterVal += increment;
    			}
    		}while(exists);
    		// trigger property change
    		setCounterValue(counterVal);
    	}
    }
    
    public int getCounterValue() {
        if(counterValue==null){
            int counterStart=config.getCounterStart();
            counterValue=counterStart;
        }
        return counterValue;
    }
    public void setCounterValue(Integer counterValue) {
        Integer oldCounterValue=this.counterValue;
        String oldItemcode=getItemCode();
        this.counterValue = counterValue;
        firePropertyChange("counterValue", oldCounterValue, this.counterValue);
        String itemcode=getItemCode();
        firePropertyChange("itemCode",oldItemcode,itemcode);
    }
  
    public void reset(){
    	setCounterValue(null);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void firePropertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }

    public void firePropertyChange(String propertyName, boolean oldValue,
            boolean newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
    }

    public void firePropertyChange(String propertyName, int oldValue,
            int newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
    }

    public void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return propertyChangeSupport.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(
            String propertyName) {
        return propertyChangeSupport.getPropertyChangeListeners(propertyName);
    }

    public boolean hasListeners(String propertyName) {
        return propertyChangeSupport.hasListeners(propertyName);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName,
                listener);
    }

    public ItemcodeGeneratorConfiguration getConfig() {
        return config;
    }

    public void setConfig(ItemcodeGeneratorConfiguration config) {
    	ItemcodeGeneratorConfiguration oldConfig=this.config;
    	oldConfig.removePropertyChangeListener(this);
        this.config = config;
        this.config.addPropertyChangeListener(this);
        firePropertyChange("config", oldConfig, this.config);
        reset();
    }

    public ObservableList<String> getItemcodesList() {
        return itemcodesList;
    }

    public void setItemcodesList(ObservableList<String> itemcodesList) {
        ObservableList<String> oldList=this.itemcodesList;
        if(oldList!=null){
            oldList.removeCollectionListener(this);
        }
        this.itemcodesList = itemcodesList;
        this.itemcodesList.addCollectionListener(this);
        firePropertyChange("itemcodesList", oldList, this.itemcodesList);
    }

    /* (non-Javadoc)
     * @see ipsk.util.collections.CollectionListener#collectionChanged(ipsk.util.collections.CollectionChangedEvent)
     */
    @Override
    public void collectionChanged(CollectionChangedEvent collectionChangedEvent) {
        
    }

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		firePropertyChange("config"+evt.getPropertyName(),evt.getOldValue(),evt.getNewValue());
	}
   
}
