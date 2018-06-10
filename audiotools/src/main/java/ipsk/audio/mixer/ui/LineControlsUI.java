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

import ipsk.swing.TitledPanel;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Port;
import javax.swing.JLabel;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class LineControlsUI extends TitledPanel {
	// public class LineControlsUI extends JPanel {
	private final static boolean DEBUG = false;

	private Line.Info info;

	private Control[] memberControls;

	private ControlUI[] controlUIs;

	private Font smallFont;

	public LineControlsUI(Line l) {
		super();
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.VERTICAL;
		// c.insets = new Insets(2, 5, 2, 5);
		c.anchor = GridBagConstraints.NORTH;

		smallFont = getFont().deriveFont((float) 10);
		setFont(smallFont);
		info = l.getLineInfo();
		if (l instanceof Port) {
			Port.Info pi = (Port.Info) info;
			setTitle(pi.getName());
		}
		memberControls = l.getControls();
		if (memberControls.length == 0) {
			add(new JLabel("(no controls)"), c);
		} else {
			controlUIs = new ControlUI[memberControls.length];
			int gridHeight = 0;
			int gridHeightMax = 0;
			for (int i = 0; i < memberControls.length; i++) {
				Control memberControl = memberControls[i];

				if (DEBUG)
					System.out.println(memberControl);
				if (memberControl instanceof BooleanControl) {
					BooleanControlUI bcUI = new BooleanControlUI(
							(BooleanControl) memberControl);
					controlUIs[i] = bcUI;
					gridHeight++;
				} else if (memberControl instanceof FloatControl) {
					FloatControlUI fcUI = new FloatControlUI(
							(FloatControl) memberControl);
					controlUIs[i] = fcUI;
					gridHeight++;
				} else if (memberControl instanceof EnumControl) {
					EnumControlUI ecUI = new EnumControlUI(
							(EnumControl) memberControl);
					controlUIs[i] = ecUI;
					gridHeight++;
				} else if (memberControl instanceof CompoundControl) {
					CompoundControlUI ccUI = new CompoundControlUI(
							(CompoundControl) memberControl);
					ccUI.setFont(smallFont);
					controlUIs[i] = ccUI;
					gridHeight = 0;
				}
				if (gridHeight > gridHeightMax)
					gridHeightMax = gridHeight;
			}
			c.gridx = 0;
			c.gridy = 0;
			for (int i = 0; i < controlUIs.length; i++) {
				if (controlUIs[i] instanceof CompoundControlUI) {
					c.gridx++;
					c.gridy = 0;
					c.gridheight = gridHeight;
				} else {
					c.gridy++;
				}
				add(controlUIs[i], c);
			}
		}
	}

	public void setEnabled(boolean enabled) {
		Component[] childs = getComponents();
		for (int i = 0; i < childs.length; i++) {
			childs[i].setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}

	public void updateValue() {
		if (controlUIs != null) {
			for (ControlUI cui : controlUIs) {
				cui.updateValue();
			}
		}

	}
}
