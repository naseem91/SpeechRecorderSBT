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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ResourceBundle;

import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;


/**
 * UI for audio mixer ports.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class MixerPortsUI extends JPanel {

	final static boolean DEBUG = false;

	final static boolean USE_SCROLL_PANES = false;

	private ResourceBundle rb;

	// private Box capturePortsPanel;
	// private Box playbackPortsPanel;
	private FilteredMixerPortsUI capturePortsPanel;

	private FilteredMixerPortsUI playbackPortsPanel;

	private JTabbedPane portPanels;

	private Mixer.Info mixerInfo;

	private Line[] ports;

	int numPorts;

	public MixerPortsUI(Mixer mixer) {
		super();
		String packageName = getClass().getPackage().getName();
		rb = ResourceBundle.getBundle(packageName + ".ResBundle");
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0;
		// c.fill = GridBagConstraints.HORIZONTAL;
		// c.insets = new Insets(2, 5, 2, 5);
		c.gridx = 0;
		c.gridy = 0;
		portPanels = new JTabbedPane();
		playbackPortsPanel = new FilteredMixerPortsUI(mixer, false);
		capturePortsPanel = new FilteredMixerPortsUI(mixer, true);

		// capturePortsPanel.setLayout(new
		// BoxLayout(capturePortsPanel,BoxLayout.X_AXIS));
		mixerInfo = mixer.getMixerInfo();
		JLabel descLabel = new JLabel(rb.getString("description") + ":"
				+ mixerInfo.getDescription());
		add(descLabel, c);
		c.gridy++;
		JLabel vendorLabel = new JLabel(rb.getString("vendor") + ":"
				+ mixerInfo.getVendor());
		add(vendorLabel, c);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;

		if (USE_SCROLL_PANES) {
			if (playbackPortsPanel.getNumPorts() > 0) {
				JScrollPane playbackScrollPane = new JScrollPane(
						playbackPortsPanel);
				portPanels.addTab(rb.getString("playback"), playbackScrollPane);
			}
			if (capturePortsPanel.getNumPorts() > 0) {
				JScrollPane captureScrollPane = new JScrollPane(
						capturePortsPanel);

				portPanels.addTab(rb.getString("capture"), captureScrollPane);
			}
		} else {
			if (playbackPortsPanel.getNumPorts() > 0) {
				portPanels.addTab(rb.getString("playback"), playbackPortsPanel);
			}
			if (capturePortsPanel.getNumPorts() > 0) {
				portPanels.addTab(rb.getString("capture"), capturePortsPanel);
			}
		}
		c.gridy++;
		add(portPanels, c);

	}
	
	public void updateValue(){
		playbackPortsPanel.updateValue();
		capturePortsPanel.updateValue();
	}

	// static MixerPortsUI createPortsUI(Mixer m){
	// return
	// }

	public void close() {
		for (int i = 0; i < numPorts; i++) {
			ports[i].close();
		}
	}

}
