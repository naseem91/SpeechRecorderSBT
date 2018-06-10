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
 * Date  : Jul 1, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.actions;

//import ipsk.audio.ui.icons.ConfigurableIcon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractAction;



/**
 * Base class for an action. 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class BasicAction extends AbstractAction {

	private Vector<ActionListener> listeners;

	private boolean highlighted;

	public final String HIGH_LIGHTED_KEY = "Highlighted";

	/**
	 *  
	 */
	public BasicAction() {
		super();
		listeners = new Vector<ActionListener>();
		setHighlighted(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		fireActionEvent(e);
	}

	public synchronized void addActionListener(ActionListener al) {
		if (al != null && !listeners.contains(al)) {
			listeners.addElement(al);
		}
	}

	public synchronized void removeActionListener(ActionListener al) {
		if (al != null) {
			listeners.removeElement(al);
		}
	}

	protected synchronized void fireActionEvent(ActionEvent ae) {

		Iterator<ActionListener> it = listeners.iterator();
		while (it.hasNext()) {
			ActionListener listener = it.next();
			listener.actionPerformed(ae);
		}
	}

	/**
	 * @return Returns the highlighted.
	 */
	public boolean isHighlighted() {
		return highlighted;
	}

	/**
	 * @param highlighted
	 *            The highlighted to set.
	 */
	public void setHighlighted(boolean highlighted) {
		boolean oldHighlighted = this.highlighted;
		this.highlighted = highlighted;
		
		// the LARGE_ICON_KEY constant in Action class is not implemented in JRE 1.5 ! 
//		Icon icon=(Icon)getValue(Action.LARGE_ICON_KEY);
//		if(icon instanceof ConfigurableIcon){
//			((ConfigurableIcon)icon).setHighLighted(highlighted);
//			
//		}
		putValue(HIGH_LIGHTED_KEY, new Boolean(highlighted));
		firePropertyChange(HIGH_LIGHTED_KEY, new Boolean(oldHighlighted),
				new Boolean(highlighted));
	}
	
}
