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
 * Date  : Jun 17, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.events;

import ipsk.audio.actions.SetFramePositionAction;

import java.awt.event.ActionEvent;

/**
 * Event indicates new (playback) position of the audio clip cursor.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class FramePositionActionEvent extends ActionEvent {

	private long framePosition;

	public FramePositionActionEvent(Object source) {
		this(source, 0);
	}

	/**
	 * Create position change event.
	 * @param source source object 
	 * @param framePosition new psotion in frames
	 */
	public FramePositionActionEvent(Object source, long framePosition) {
		super(source, ActionEvent.ACTION_PERFORMED,
				SetFramePositionAction.ACTION_COMMAND);
		this.framePosition = framePosition;
	}

	/**
	 * Returns the requested frame position.
	 * @return new frame position
	 */
	public long getFramePosition() {
		return framePosition;
	}

}
