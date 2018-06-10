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
 * Date  : Mar 24, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.mixer.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.BooleanControl;
import javax.swing.JCheckBox;

/**
 * UI for audio port boolean control.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class BooleanControlUI extends ControlUI implements ActionListener {
	private BooleanControl bc;

	private JCheckBox checkBox;

	private boolean updating = false;

	public BooleanControlUI(BooleanControl bc) {
		super();
		this.bc = bc;
		checkBox = new JCheckBox(bc.getType().toString());
		// checkBox.setFont(getFont());

		updateValue();
		checkBox.addActionListener(this);
		add(checkBox);
	}

	public void setFont(Font newFont) {
		if (checkBox != null) {
			checkBox.setFont(newFont);
		}
		super.setFont(newFont);
		revalidate();
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		if (!updating) {
			bc.setValue(checkBox.isSelected());
		}
	}

	public void setEnabled(boolean enabled) {
		checkBox.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	public void updateValue() {
		boolean s = bc.getValue();
		if (s != checkBox.isSelected()) {
			updating = true;
			checkBox.setSelected(s);
			updating = false;
		}
	}

}
