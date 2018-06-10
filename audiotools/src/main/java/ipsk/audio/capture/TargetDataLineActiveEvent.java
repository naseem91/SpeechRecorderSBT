//    IPS Java Audio Tools
// 	  (c) Copyright 2015
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

package ipsk.audio.capture;

import javax.sound.sampled.AudioSystem;

import ipsk.audio.capture.event.CaptureEvent;


/**
 * @author klausj
 *
 */
public class TargetDataLineActiveEvent extends CaptureEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8956217320630677630L;

	private long frames;
	/**
	 * @return the frames
	 */
	public long getFrames() {
		return frames;
	}

	/**
	 * @param source
	 */
	public TargetDataLineActiveEvent(Object source) {
		super(source);
		frames=AudioSystem.NOT_SPECIFIED;
	}
	
	/**
	 * @param source
	 */
	public TargetDataLineActiveEvent(Object source,long frames) {
		super(source);
		this.frames=frames;
	}

}
