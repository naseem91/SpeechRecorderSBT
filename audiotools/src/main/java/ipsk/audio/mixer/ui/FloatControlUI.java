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
import java.awt.Dimension;
import java.awt.Font;
import java.util.Hashtable;

import javax.sound.sampled.FloatControl;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * UI for a continous (float value) audio mixer control.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class FloatControlUI extends ControlUI implements ChangeListener {

	private FloatControl fc;

	private FloatControl.Type type;

	private JSlider slider;

	private JLabel label;

	private float minimum;

	private float range;

	private int ticks;

	private float precision;
	private boolean updating=false;

	/**
	 * 
	 * 
	 */
	public FloatControlUI(FloatControl fc) {
		super();
		this.fc = fc;
		setLayout(new BorderLayout());
		type = (FloatControl.Type) fc.getType();

		label = new JLabel(type.toString());
		label.setFont(getFont().deriveFont(Font.PLAIN));

		minimum = fc.getMinimum();
		precision = fc.getPrecision();
		range = fc.getMaximum() - minimum;
		ticks = (int) (range / precision);
		float value = fc.getValue();
		int sliderValue = (int) ((value - minimum) / precision);
		if (sliderValue > ticks)
			sliderValue = ticks;

		if (type == FloatControl.Type.BALANCE) {
			preferredOrientation = SwingConstants.HORIZONTAL;
		} else {
			preferredOrientation = SwingConstants.VERTICAL;
		}
		slider = new JSlider(preferredOrientation, 0, ticks, sliderValue);
		if (preferredOrientation == SwingConstants.HORIZONTAL) {
			slider.setPreferredSize(new Dimension(30, 20));
		} else {
			slider.setPreferredSize(new Dimension(20, 100));
		}
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel(fc.getMinLabel()));
		labelTable.put(new Integer(ticks), new JLabel(fc.getMaxLabel()));
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true);
		setToolTipText(fc.toString());
		label.setToolTipText(fc.toString());
		slider.setToolTipText(fc.toString());
		add(label,BorderLayout.NORTH);
		add(slider,BorderLayout.CENTER);
		slider.addChangeListener(this);
		// revalidate();
		// repaint();
	}

	public void updateToolTip() {
		setToolTipText(fc.toString());
		label.setToolTipText(fc.toString());
		slider.setToolTipText(fc.toString());
		

	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		if (type == FloatControl.Type.BALANCE) {
			return new Dimension(40, super.getPreferredSize().height);
		} else {
			return super.getPreferredSize();
		}

	}

	public Dimension getMaximumSize() {
		if (type == FloatControl.Type.BALANCE) {
			return new Dimension(80, super.getPreferredSize().height);
		} else {
			return super.getMaximumSize();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent arg0) {
		if(!updating){
		int sliderValue = slider.getValue();
		fc.setValue(sliderValue * precision + minimum);
		updateValue();
		//updateToolTip();
		}
	}

	
	public void updateValue() {
		
			float value = fc.getValue();
			int sliderValue = (int) ((value - minimum) / precision);
			if (sliderValue != slider.getValue()) {
				updating=true;
				slider.setValue(sliderValue);
				updating=false;
			}
			updateToolTip();

	}

}
