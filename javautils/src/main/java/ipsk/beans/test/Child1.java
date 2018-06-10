//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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

package ipsk.beans.test;

import java.util.Date;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class Child1 {

	private String attributeType;
	private float sampleRate;
	private int[] sampleSizes;
	public enum Selection {RED,GREEN,YELLOW};
	private Selection attributeSelection=Selection.RED;
	private Date timestamp=null;
	private Boolean extra;
	
	public Child1() {
		super();
		timestamp=new Date();
	}

	
	public String getAttributeType() {
		return attributeType;
	}

	
	public void setAttributeType(String string) {
		attributeType = string;
	}

	
	public float getSampleRate() {
		return sampleRate;
	}

	
	public void setSampleRate(float f) {
		sampleRate = f;
	}

    public int[] getSampleSizes() {
        return sampleSizes;
    }
    public void setSampleSizes(int[] sampleSizes) {
        this.sampleSizes = sampleSizes;
    }


	public Selection getAttributeSelection() {
		return attributeSelection;
	}


	public void setAttributeSelection(Selection attributeSelection) {
		this.attributeSelection = attributeSelection;
	}


	public Date getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


    public Boolean getExtra() {
        return extra;
    }


    public void setExtra(Boolean extra) {
        this.extra = extra;
    }
}
