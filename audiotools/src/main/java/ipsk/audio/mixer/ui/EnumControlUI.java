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
 * Date  : Apr 7, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.mixer.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.EnumControl;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/**
 * 
 * Enumeration mixer control. This class could not be tested (no soundcard with
 * enum control found) !!!
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class EnumControlUI extends ControlUI implements ActionListener {
	private EnumControl ec;

	private Object[] objects;

	private JRadioButton[] boxes;

	private boolean updating = false;

	public EnumControlUI(EnumControl ec) {
		super(ec.getType().toString());
		this.ec = ec;
		objects = ec.getValues();
		setLayout(new GridLayout(objects.length, 1));
		boxes = new JRadioButton[objects.length];
		ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < objects.length; i++) {
			boxes[i] = new JRadioButton(objects[i].toString());
			boxes[i].addActionListener(this);
			group.add(boxes[i]);
			add(boxes[i]);
		}
		updateValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		if (!updating) {
			for (int i = 0; i < boxes.length; i++) {
				if (ae.getSource().equals(boxes[i])) {
					ec.setValue(objects[i]);
				}
			}
		}
	}

	public void updateValue() {
		for (int i = 0; i < objects.length; i++) {
			if (ec.getValue().equals(objects[i])) {
				updating = true;
				if (!boxes[i].isSelected()) {
					boxes[i].setSelected(true);
				}
				updating = false;
			}
		}
	}

}
