//    Speechrecorder
// 	  (c) Copyright 2014
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
import ipsk.beans.dom.DOMElements;

/**
 * Definition of a channel routing.
 * @author klausj
 *
 */
@DOMElements({"channelOffset","srcChannelCount","assign"})
@DOMAttributes({"name"})
public class ChannelRouting {
	
	public ChannelRouting(){
		super();
//		assign=new int[]{3,4};
	}
	
	private String name=null;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Set name for this routing
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	private Integer srcChannelCount=null;
	
	/**
	 * Get source channel count
	 * @return the srcChannelCount
	 */
	public Integer getSrcChannelCount() {
		return srcChannelCount;
	}
	/**
	 * Set source channel count
	 * @param srcChannelCount the srcChannelCount to set
	 */
	public void setSrcChannelCount(Integer srcChannelCount) {
		this.srcChannelCount = srcChannelCount;
	}

	private int[] assign=null;

	/**
	 * Get channel assignment
	 * @return the assign
	 */
	public int[] getAssign() {
		return assign;
	}
	/**
	 * Set channel assignment
	 * @param assign the assignment to set
	 */
	public void setAssign(int[] assign) {
		this.assign = assign;
	}
	
	
	
	private int channelOffset=0;

	/**
	 * @return the channelOffset
	 */
	public int getChannelOffset() {
		return channelOffset;
	}
	/**
	 * @param channelOffset the channelOffset to set
	 */
	public void setChannelOffset(int channelOffset) {
		this.channelOffset = channelOffset;
	}
	

	
	
}
