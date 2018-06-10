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

import java.awt.Component;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;



/**
 *
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class JCustomScrollPane extends JScrollPane {

	/**
	 * 
	 */
	public JCustomScrollPane() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public JCustomScrollPane(int arg0, int arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public JCustomScrollPane(Component arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public JCustomScrollPane(Component arg0, int arg1, int arg2) {
		super(arg0, arg1, arg2);
	}

	public JScrollBar createHorizontalScrollBar(){
		return new JCustomScrollBar(JScrollBar.HORIZONTAL);
	}

}
