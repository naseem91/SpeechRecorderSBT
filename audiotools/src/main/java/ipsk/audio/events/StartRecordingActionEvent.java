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

import java.awt.event.ActionEvent;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class StartRecordingActionEvent extends CaptureActionEvent {

	public StartRecordingActionEvent(Object source) {
		super(source, ActionEvent.ACTION_PERFORMED, "Start recording");

	}
}
