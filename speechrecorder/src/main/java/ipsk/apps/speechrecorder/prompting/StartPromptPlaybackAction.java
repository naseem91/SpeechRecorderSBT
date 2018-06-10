//    Speechrecorder
//    (c) Copyright 2012
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

/*
 * Date  : Jul 1, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.prompting;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class StartPromptPlaybackAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public final static String ACTION_COMMAND = new String("start_prompt_playback");
    //public final static String SHORT_DESCRIPTION_VAL=new String("Start prompt playback");
    
    private Prompter prompter;
    private Icon icon;
	/**
	 *  
	 */
	public StartPromptPlaybackAction(Prompter prompter,Icon icon) {
		super();
        this.prompter=prompter;
        this.icon=icon;
		putValue(Action.ACTION_COMMAND_KEY, ACTION_COMMAND);
		resetIcon();
	}
	
	public void resetIcon(){
		putValue(Action.SMALL_ICON,icon);
		putValue(Action.NAME,null);
	}

	public String getActionCommand() {
		return (String) getValue(Action.ACTION_COMMAND_KEY);
	}

    public void actionPerformed(ActionEvent arg0) {
           prompter.play();
    }

}
