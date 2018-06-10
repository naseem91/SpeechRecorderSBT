//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * Contains the current level of an audio stream and the current peak level of the whole stream.
 * @author klausj
 * 
 */
public class LevelInfo{
	private float level;

	// the peak level (of current buffer)
	private float peakLevel;
	
	// peak level of last buffers
	private float intervalPeakLevel;

	/**
	 * Returns interval scoped peak level.
	 * The peak level of last buffers.
	 * The UI update may not reflect some peak levels if the update period of the UI
	 * is greater than the audio buffer size. This value holds the peak level until it reset (by the UI).
	 * The user will not miss any level peaks. (Besides the peak hold value)
	 * @return peak level of last buffers   
	 */
	public float getIntervalPeakLevel() {
		return intervalPeakLevel;
	}

	/**
	 * Reset interval scoped peak level.
	 * The UI level meter should reset the interval peak level after the peak is displayed.  
	 */
	public void resetIntervalPeakLevel(){
		intervalPeakLevel=peakLevel;
	}

	private float peakLevelHold;
	
	private PropertyChangeSupport propertyChangeSupport;

	public LevelInfo() {
        this(0,0);
    }
	
	public LevelInfo(float level, float peakLevel) {
	    super();
		this.level = level;
		this.peakLevel = peakLevel;
		intervalPeakLevel=peakLevel;
		peakLevelHold = 0;
		propertyChangeSupport=new PropertyChangeSupport(this);
	}

	

	/**
	 * @return Returns the level.
	 */
	public float getLevel() {
		return level;
	}

	/**
	 * Set audio level.
	 * @param level level to set
	 */
	public void setLevel(float level) {
	    float oldValue=this.level;
		this.level = level;
		propertyChangeSupport.firePropertyChange("level", oldValue,level);
	}

	/**
	 * Get peak level.
	 * @return peak level
	 */
	public float getPeakLevel() {
		return peakLevel;
	}

	/**
	 * Set peak level.
	 * @param peakLevel peak level
	 */
	public void setPeakLevel(float peakLevel) {
	    float oldValue=this.peakLevel;
		this.peakLevel = peakLevel;
		if(peakLevel>intervalPeakLevel){
			intervalPeakLevel=peakLevel;
		}
		propertyChangeSupport.firePropertyChange("peakLevel", oldValue,peakLevel);
	}

	public float getPeakLevelHold() {
		return peakLevelHold;
	}

	public void setPeakLevelHold(float peakLevelHold) {
	    float oldValue=this.peakLevelHold;
		this.peakLevelHold = peakLevelHold;
		propertyChangeSupport.firePropertyChange("peakLevelHold", oldValue,peakLevelHold);
	}
	
	public void resetPeakLevelHold(){
		setPeakLevelHold(0.0f);
	}
	
	/**
	 * Merges the peak level hold of this level info the given level info.
	 * Applies peaklevelHold property to the given levelInfoToMerge level info, if the value of this level info is greater
	 * than the one of levelInfoToMerge.   
	 * @param levelInfoToMerge level info to merge peak levle hold to
	 * @return result level info (levelInfoToMerge)
	 */
	public LevelInfo mergePeakLevelHold(LevelInfo levelInfoToMerge){
		if(levelInfoToMerge==null){
			levelInfoToMerge=new LevelInfo();
		}	
		float mPeakLevelHold=levelInfoToMerge.getPeakLevelHold();
		if(mPeakLevelHold<peakLevelHold){
			levelInfoToMerge.setPeakLevelHold(peakLevelHold);
		}
		return levelInfoToMerge;
	}
	
	/**
	 * Merges this level info into given level info.
	 * Applies level properties to the given levelInfoToMerge level info, if the property value of this level info is greater
	 * than the corresponding property value in levelInfoToMerge. The properties are level, peakLevel and pekaLevelHold.
	 * @param levelInfoToMerge
	 * @return result level info (levelInfoToMerge)
	 */
	public LevelInfo merge(LevelInfo levelInfoToMerge){
		if(levelInfoToMerge==null){
			levelInfoToMerge=new LevelInfo();
		}
		float mLevel=levelInfoToMerge.getLevel();
		if(mLevel<level){
			levelInfoToMerge.setLevel(level);
		}
		float mPeakLevel=levelInfoToMerge.getPeakLevel();
		if(mPeakLevel<peakLevel){
			levelInfoToMerge.setPeakLevel(peakLevel);
		}
		mergePeakLevelHold(levelInfoToMerge);
		return levelInfoToMerge;
	}

	public String toString() {
		return new String(super.toString() + ", Level: " + level + ", Peak: "
				+ peakLevel + ", Peakhold: " + peakLevelHold);
	}

    public void addPropertyChangeListener(PropertyChangeListener arg0) {
        propertyChangeSupport.addPropertyChangeListener(arg0);
    }

    public void addPropertyChangeListener(String arg0,
            PropertyChangeListener arg1) {
        propertyChangeSupport.addPropertyChangeListener(arg0, arg1);
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
