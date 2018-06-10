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
 * Date  : May 28, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.ui;

import ipsk.audio.impl.j2audio.J2AudioController;

import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class OptionsPanel extends JPanel {

	private ResourceBundle rb;

	JCheckBox fullDuplex;

	JCheckBox overwriteCheckBox;

	public OptionsPanel() {
		super();
		String packageName = getClass().getPackage().getName();
		rb = ResourceBundle.getBundle(packageName + ".ResBundle");
		BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(bl);
		fullDuplex = new JCheckBox(rb.getString("fullduplexMode"));
		add(fullDuplex);
		overwriteCheckBox = new JCheckBox(rb.getString("askForOverwrite"));
		add(overwriteCheckBox);

	}

	public void setMode(long mode) {
		if (mode == J2AudioController.FULLDUPLEX) {
			fullDuplex.setSelected(true);
		} else {
			fullDuplex.setSelected(false);
		}

	}

	/**
	 * Returns mode flags.
	 * 
	 * @return mode
	 */
	public long getMode() {
		if (fullDuplex.isSelected()) {
			return J2AudioController.FULLDUPLEX;
		} else {
			return J2AudioController.OPEN_ON_DEMAND;
		}

	}

	public void setOverwrite(boolean overwrite) {

		overwriteCheckBox.setSelected(overwrite);

	}

	/**
	 * Returns overwrite mode.
	 * 
	 * @return mode
	 */
	public boolean getOverwrite() {
		return overwriteCheckBox.isSelected();

	}
}
