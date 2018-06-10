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

import javax.sound.sampled.Mixer;

import ipsk.io.ChannelRouting;

/**
 * @author klausj
 *
 */
public class ChannelGroupLocator {
	private Mixer device;
	/**
	 * @return the device
	 */
	public Mixer getDevice() {
		return device;
	}
	/**
	 * @return the channelOffset
	 */
	public int getChannelOffset() {
		return channelOffset;
	}
	/**
	 * @return the channelRouting
	 */
	public ChannelRouting getChannelRouting() {
		return channelRouting;
	}
	/**
	 * @param device
	 * @param channelOffset
	 */
	public ChannelGroupLocator(Mixer device, int channelOffset) {
		super();
		this.device = device;
		this.channelOffset = channelOffset;
	}
	private int channelOffset=0;
	private ChannelRouting channelRouting=null;
	/**
	 * @param device
	 * @param channelRouting
	 */
	public ChannelGroupLocator(Mixer device, ChannelRouting channelRouting) {
		super();
		this.device = device;
		this.channelRouting = channelRouting;
	}
}
