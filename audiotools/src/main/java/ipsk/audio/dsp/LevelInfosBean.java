//    IPS Java Audio Tools
//    (c) Copyright 2009-2010
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Created on 29.08.2005
 *
 */
package ipsk.audio.dsp;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Contains level informations of a stream.
 * 
 * @author klausj
 * 
 */
public class LevelInfosBean {
	private LevelInfo[] levelInfos;
	private PropertyChangeSupport propertyChangeSupport;
	
	public LevelInfosBean(){
	    super();
	    propertyChangeSupport=new PropertyChangeSupport(this);
	}
	
    public LevelInfo[] getLevelInfos() {
        return levelInfos;
    }

    public void setLevelInfos(LevelInfo[] levelInfos) {
        LevelInfo[] oldValue=this.levelInfos;
        this.levelInfos = levelInfos;
        
        propertyChangeSupport.firePropertyChange("levelInfos", oldValue,this.levelInfos);
    }

    public void resetPeakLevelHold(){
    	if(levelInfos!=null){
    		for(LevelInfo li:levelInfos){
    			li.resetPeakLevelHold();
    		}
    	}
    }
    public void resetIntervalPeakLevel(){
    	if(levelInfos!=null){
    		for(LevelInfo li:levelInfos){
    			li.resetIntervalPeakLevel();
    		}
    	}
    }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }


    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }


    public void fireIndexedPropertyChange(String propertyName, int index,
            Object oldValue, Object newValue) {
        propertyChangeSupport.fireIndexedPropertyChange(propertyName, index,
                oldValue, newValue);
    }


    public void firePropertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(evt);
    }


    public void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue,
                newValue);
    }


    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }


    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName,
                listener);
    }
	
}
