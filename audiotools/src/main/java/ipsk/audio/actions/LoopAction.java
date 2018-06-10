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

package ipsk.audio.actions;

import javax.swing.Action;

public class LoopAction extends BasicAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2617541405299184409L;
	public final static String ACTION_COMMAND = new String("toggle_loop");
	public LoopAction() {
		super();
		putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND);
		putValue(Action.NAME, "Loop");
		putValue(Action.SHORT_DESCRIPTION, "Toggle looping");
		putValue(Action.SELECTED_KEY,false);
	}

}
