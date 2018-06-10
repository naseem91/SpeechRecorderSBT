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

package ipsk.swing;

import javax.swing.JScrollBar;



/**
 * A customized scrollbar.
 * This @see JScrollBar does neither change its value nor does it throw events if the knob is adjusting.
 * If the mouse is released the value is set and an @see javax.swing.AdjustmentEvent is thrown. 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class JCustomScrollBar extends JScrollBar {
	private static final boolean DEBUG=false;
	
	private int extValue;
	/**
	 * 
	 */
	public JCustomScrollBar() {
		super();
	}

	/**
	 * @param arg0
	 */
	public JCustomScrollBar(int arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 */
	public JCustomScrollBar(int arg0, int arg1, int arg2, int arg3, int arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
	}

	protected void fireAdjustmentValueChanged(int id, int type, int value) {
		
		if (!getValueIsAdjusting()) {
			
			super.setValue(extValue);
			super.fireAdjustmentValueChanged(id, type, extValue);
		}
	}
	public void setValue(int v) {
		if (DEBUG) System.out.println("min: "+getMinimum()+" max: "+getMaximum()+" val: "+getValue());
		extValue=v;
		super.setValue(v);
		
	}
}
