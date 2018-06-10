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
 * Date  : Apr 29, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.mixer.ui;

import ipsk.swing.TitledPanel;

import java.awt.event.ActionListener;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.Port.Info;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;



/**
 * Audio mixer configuration panel.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class MixerConfigPanel extends JPanel {

	private final static boolean DEBUG = false;

	public JRadioButton selectPlayRadioButton;

	public JRadioButton selectCaptureRadioButton;

	private TitledPanel mixerControlPanel = null;

	private LineControlsUI lineControlsUI = null;

	private Mixer.Info mixerInfo;

	private Vector<Info> portInfos;

	public MixerConfigPanel(Mixer mixer, ActionListener al) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		mixerInfo = mixer.getMixerInfo();
		if (DEBUG)
			System.out.println("Mixer: " + mixerInfo.getName());
		JLabel descLabel = new JLabel("Description: "
				+ mixerInfo.getDescription());
		JLabel vendorLabel = new JLabel("Vendor: " + mixerInfo.getVendor());
		add(descLabel);
		add(vendorLabel);
		selectPlayRadioButton = new JRadioButton("Select for playback.");
		selectPlayRadioButton.addActionListener(al);
		add(selectPlayRadioButton);
		selectCaptureRadioButton = new JRadioButton("Select for capture.");
		selectCaptureRadioButton.addActionListener(al);
		add(selectCaptureRadioButton);
		portInfos = new Vector<Info>();
		Control[] mixerControls = mixer.getControls();
		if (mixerControls.length > 0) {
			mixerControlPanel = new TitledPanel("Mixer Controls");
			lineControlsUI = new LineControlsUI(mixer);
			mixerControlPanel.add(lineControlsUI);
		}

		if (DEBUG)
			System.out.println(" SourceLines:");

		Line.Info[] sourceLineInfos = mixer.getSourceLineInfo();
		for (int i = 0; i < sourceLineInfos.length; i++) {
			if (DEBUG)
				System.out.println("  Line: " + sourceLineInfos[i]);
			if (sourceLineInfos[i] instanceof Port.Info) {
				portInfos.add((Port.Info) sourceLineInfos[i]);

			} else if (sourceLineInfos[i] instanceof DataLine.Info) {

				DataLine.Info di = (DataLine.Info) sourceLineInfos[i];
				if (DEBUG)
					printFormats(di.getFormats());
			}
			Line l = null;
			try {
				l = mixer.getLine(sourceLineInfos[i]);
			} catch (LineUnavailableException e) {

				String errMsg = new String("Error opening audio device\n"
						+ e.getLocalizedMessage());
				JOptionPane.showMessageDialog(this, errMsg, "Error !",
						JOptionPane.ERROR_MESSAGE);
				System.err.println(errMsg);

			}
			Control[] controls = l.getControls();
			for (int j = 0; j < controls.length; j++) {
				if (DEBUG)
					System.out.println("   Control: " + controls[j]);
			}

		}
		if (DEBUG)
			System.out.println(" TargetLines:");
		Line.Info[] targetLineInfos = mixer.getTargetLineInfo();
		for (int i = 0; i < targetLineInfos.length; i++) {
			if (targetLineInfos[i] instanceof Port.Info) {
				portInfos.add((Port.Info) targetLineInfos[i]);

			} else if (targetLineInfos[i] instanceof DataLine.Info) {
				DataLine.Info di = (DataLine.Info) targetLineInfos[i];
				if (DEBUG)
					printFormats(di.getFormats());

			} else {
				if (DEBUG)
					System.out.println("  Line: " + targetLineInfos[i]);
			}
			Line l = null;

			if (l != null) {

				if (DEBUG)
					System.out.println(l.toString());
				Control[] controls = l.getControls();
				for (int j = 0; j < controls.length; j++) {
					if (DEBUG)
						System.out.println("   Control: " + controls[j]);
				}
			}
		}

		if (mixerControlPanel != null) {
			add(mixerControlPanel);
		}
		revalidate();
		repaint();
	}

	public void setEnabled(boolean enabled) {
		selectPlayRadioButton.setEnabled(enabled);
		selectCaptureRadioButton.setEnabled(enabled);
		if (lineControlsUI != null)
			lineControlsUI.setEnabled(enabled);

	}

	void printFormats(AudioFormat[] fmts) {
		for (int i = 0; i < fmts.length; i++) {
			System.out.println(i + ". AudioFormat: " + fmts[i]);
		}
	}
}
