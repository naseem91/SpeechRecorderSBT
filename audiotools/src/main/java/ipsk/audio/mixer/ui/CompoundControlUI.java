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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.FloatControl;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class CompoundControlUI extends ControlUI {

	private Control[] memberControls;

	private ControlUI[] controlUIs;

	private JLabel label;

	/**
	 *  
	 */
	public CompoundControlUI(CompoundControl cc) {
		//super(cc.getType().toString());
		super();
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setLayout(new BorderLayout());
		label = new JLabel(cc.getType().toString());
		label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		memberControls = cc.getMemberControls();
		controlUIs = new ControlUI[memberControls.length];

		for (int i = 0; i < memberControls.length; i++) {
			Control memberControl = memberControls[i];

			if (memberControl instanceof BooleanControl) {
				BooleanControlUI bcUI = new BooleanControlUI(
						(BooleanControl) memberControl);
				//add(bcUI);
				controlUIs[i] = bcUI;
			} else if (memberControl instanceof FloatControl) {
				FloatControlUI fcUI = new FloatControlUI(
						(FloatControl) memberControl);
				//add(fcUI);
				controlUIs[i] = fcUI;
			} else if (memberControl instanceof EnumControl) {
				EnumControlUI ecUI = new EnumControlUI(
						(EnumControl) memberControl);
				//add(ecUI);
				controlUIs[i] = ecUI;
			} else if (memberControl instanceof CompoundControl) {
				CompoundControlUI ccUI = new CompoundControlUI(
						(CompoundControl) memberControl);
				//add(ccUI);
				controlUIs[i] = ccUI;
			}
		}

		Box verticalBox = Box.createHorizontalBox();
		verticalBox.add(Box.createHorizontalGlue());
		Box horizBox = Box.createVerticalBox();
		//JPanel horizBox=new JPanel();

		for (int i = 0; i < controlUIs.length; i++) {
			if (controlUIs[i].getPreferredOrientation() == SwingConstants.VERTICAL) {
				verticalBox.add(controlUIs[i]);
			} else {
				horizBox.add(controlUIs[i]);
			}
		}
		verticalBox.add(Box.createHorizontalGlue());
		add(label,BorderLayout.NORTH);
		add(verticalBox,BorderLayout.CENTER);
		add(horizBox,BorderLayout.SOUTH);

	}

	public void setFont(Font f) {
		if (label != null)
			label.setFont(f);
		if (controlUIs != null) {
			for (int i = 0; i < controlUIs.length; i++) {
				controlUIs[i].setFont(f);
			}
		}
		super.setFont(f);
	}

	public void setEnabled(boolean enabled) {
		if (controlUIs != null) {
			for (int i = 0; i < controlUIs.length; i++) {
				controlUIs[i].setEnabled(enabled);
			}
		}
		//super.setEnabled(enabled);
	}

	public void updateValue() {
		for(ControlUI cui:controlUIs){
			cui.updateValue();
		}
		
	}

}
