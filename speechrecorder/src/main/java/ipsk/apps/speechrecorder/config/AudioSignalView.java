//    Speechrecorder
//    (c) Copyright 2009-2011
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

package ipsk.apps.speechrecorder.config;

import ipsk.beans.dom.DOMAttributes;

/**
 * Audio signal view configuration
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
//@DOMElements({"maxDisplaylevel"})
@DOMAttributes({"amplitudeScaleType","logarithmicBaseLevel"})
public class AudioSignalView {
   
	public final int DEFAULT_LOGARITHMIC_BASE_LEVEL=-50;
    public final static String LINEAR="LINEAR";
    public final static String LOGARITHMIC="LOGARITHMIC";
    
    private String amplitudeScaleType=LINEAR;
    private int logarithmicBaseLevel=DEFAULT_LOGARITHMIC_BASE_LEVEL;
    
    
    public AudioSignalView(){
    	super();
    }

    
	public String getAmplitudeScaleType() {
		return amplitudeScaleType;
	}


	public void setAmplitudeScaleType(String amplitudeScaleType) {
		this.amplitudeScaleType = amplitudeScaleType;
	}


	public int getLogarithmicBaseLevel() {
		return logarithmicBaseLevel;
	}


	public void setLogarithmicBaseLevel(int logarithmicBaseLevel) {
		this.logarithmicBaseLevel = logarithmicBaseLevel;
	}

 
    
}
