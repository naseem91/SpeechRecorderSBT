//    IPS Java Audio Tools
// 	  (c) Copyright 2014
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.audio;

import ipsk.io.ChannelRouting;

/**
 * Extended audio controller interface for controllers which implement input channel routing. 
 *
 */
public interface AudioController3 extends AudioController2 {

	/**
	 * Set channel routing for recording.
	 * @param inputChannelRouting channel routing
	 */
	public void setInputChannelRouting(ChannelRouting inputChannelRouting);

	/**
	 * @return current channel routing for recording 
	 */
	public ChannelRouting getInputChannelRouting();
}
