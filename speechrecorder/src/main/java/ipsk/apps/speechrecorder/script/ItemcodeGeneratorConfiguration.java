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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;

/**
 * Configuration of itemcode generator.
 * Contains following properties for an itemcode generator:
 * prefix,counter start value,counter increment,fixed decimal places of counter
 * @author klausj
 *
 */
public class ItemcodeGeneratorConfiguration implements Cloneable{
 
    public String DEFAULT_PREFIX="item";
    public int DEFAULT_FIXED_DECIMAL_PLACES=4;
    public static int MIN_FIXED_DECIMAL_PLACES=0;
    public static int MAX_FIXED_DECIMAL_PLACES=10;
    
    
    private String generatorName="Decimal Generator";
    private int fixedDecimalPlaces=DEFAULT_FIXED_DECIMAL_PLACES;
    private boolean useSectionName=false;
    private DecimalFormat counterFormat=null;
    private String prefix=DEFAULT_PREFIX;
//    private String postfix=null;
    private int counterStart=0;
    private int increment=1;
    private boolean active=true;
  
    private PropertyChangeSupport propertyChangeSupport;
    
    public ItemcodeGeneratorConfiguration(){
        super();
        propertyChangeSupport=new PropertyChangeSupport(this);
        setFixedDecimalPlaces(DEFAULT_FIXED_DECIMAL_PLACES);
    }
    
    
    public String getGeneratorName() {
        return generatorName;
    }
    public void setGeneratorName(String generatorName) {
        String oldGeneratorName=this.generatorName;
        this.generatorName = generatorName;
        propertyChangeSupport.firePropertyChange("generatorName",oldGeneratorName, this.generatorName);
    }
    public DecimalFormat counterFormat() {
        return counterFormat;
    }
   
    /**
     * @return prefix string
     */
    public String getPrefix() {
        
        return prefix;
    }
    public void setPrefix(String prefix) {
        String oldPrefix=this.prefix;
        this.prefix = prefix;
        propertyChangeSupport.firePropertyChange("prefix", oldPrefix, this.prefix);
    }
//    public String getPostfix() {
//        return postfix;
//    }
//    public void setPostfix(String postfix) {
//        String oldPostfix=this.postfix;
//        this.postfix = postfix;
//        propertyChangeSupport.firePropertyChange("postfix", oldPostfix, this.postfix);
//    }
    public int getCounterStart() {
        return counterStart;
    }
    
    public void setCounterStart(int counterStart) {
        this.counterStart = counterStart;
    }
    public int getIncrement() {
        return increment;
    }
    public void setIncrement(int increment) {
        int oldIncrement=this.increment;
        this.increment = increment;
        propertyChangeSupport.firePropertyChange("increment", oldIncrement, this.increment);
    }
 
    public int getFixedDecimalPlaces() {
        return fixedDecimalPlaces;
    }
    /**
     * Set the number of fixed decimal places.
     * This value can be used to produce itemcodes with fixed length.  
     * @param fixedDecimalPlaces
     */
    public void setFixedDecimalPlaces(int fixedDecimalPlaces) {
        int oldFixedDecimalPlaces=this.fixedDecimalPlaces;
        this.fixedDecimalPlaces = fixedDecimalPlaces;

        if(fixedDecimalPlaces<MIN_FIXED_DECIMAL_PLACES || fixedDecimalPlaces >MAX_FIXED_DECIMAL_PLACES){
            throw new IllegalArgumentException("Fixed decimal places must be a number between "+MIN_FIXED_DECIMAL_PLACES+" and "+MAX_FIXED_DECIMAL_PLACES);
        }
        if(fixedDecimalPlaces==0){
            counterFormat=null;
        }else{
            StringBuffer patternBuff=new StringBuffer();
            for(int i=0;i<fixedDecimalPlaces;i++){
                patternBuff.append('0');
            }
            String pattern=patternBuff.toString();
            counterFormat=new DecimalFormat(pattern);
        }

        propertyChangeSupport.firePropertyChange("fixedDecimalPlaces", oldFixedDecimalPlaces, this.fixedDecimalPlaces);
    }
    public boolean isUseSectionName() {
        return useSectionName;
    }
    public void setUseSectionName(boolean useSectionName) {
        boolean oldUseSectionName=this.useSectionName;
        this.useSectionName = useSectionName;
        propertyChangeSupport.firePropertyChange("useSectionName", oldUseSectionName, this.useSectionName);
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


    public boolean isActive() {
        return active;
    }


    public void setActive(boolean active) {
        boolean oldActive=this.active;
        this.active = active;
        firePropertyChange("active", oldActive, this.active);
    }
    
    public ItemcodeGeneratorConfiguration cloneTyped(){
    	ItemcodeGeneratorConfiguration clone=new ItemcodeGeneratorConfiguration();
    	clone.setActive(isActive());
    	clone.setCounterStart(getCounterStart());
    	clone.setIncrement(getIncrement());
    	clone.setGeneratorName(getGeneratorName());
    	clone.setPrefix(getPrefix());
    	clone.setFixedDecimalPlaces(getFixedDecimalPlaces());
    	clone.setUseSectionName(isUseSectionName());
    	return clone;
    }
    
    public Object clone(){
    	return cloneTyped();
    }
    
    
    
}
