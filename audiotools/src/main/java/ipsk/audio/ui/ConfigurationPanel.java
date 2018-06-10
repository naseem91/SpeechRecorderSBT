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
 * Date  : May 5, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.ui;

import ipsk.audio.AudioController;
import ipsk.audio.AudioControllerException;
import ipsk.audio.AudioController.CaptureStatus;
import ipsk.audio.AudioController.PlaybackStatus;
import ipsk.audio.mixer.ui.MixerDevicesUI;
import ipsk.swing.TitledPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.sound.sampled.AudioSystem;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class ConfigurationPanel extends TitledPanel implements ActionListener {

	private final static boolean DEBUG = false;

	private MixerDevicesUI mp;

	private AudioFormatChooser afc;

	private OptionsPanel ms;

	private AudioController ac;

	private JTabbedPane configPanels;

	private JButton applyButton;

	private ResourceBundle rb;

	public ConfigurationPanel(AudioController ac) {
		super();
		String packageName = getClass().getPackage().getName();
		rb = ResourceBundle.getBundle(packageName + ".ResBundle");

		super.setTitle(rb.getString("configuration"));
		setLayout(new BorderLayout());
		this.ac = ac;
		configPanels = new JTabbedPane();

		mp = new MixerDevicesUI();

		configPanels.addTab(rb.getString("mixer"), mp);
		afc = new AudioFormatChooser();

		configPanels.addTab(rb.getString("audio_format"), afc);
		ms = new OptionsPanel();
		configPanels.addTab(rb.getString("options"), ms);
		add(configPanels, BorderLayout.NORTH);
		applyButton = new JButton(rb.getString("apply"));
		add(applyButton, BorderLayout.SOUTH);
		applyButton.addActionListener(this);
		revalidate();
		repaint();
		update();
	}

	public void setEnabled(boolean enabled) {
		mp.setEnabled(enabled);
	}

	public void update() {
		mp.setSelectedPlaybackMixer(AudioSystem.getMixer(ac
				.getSourceMixerInfo()));
		mp.setSelectedCaptureMixer(AudioSystem
				.getMixer(ac.getTargetMixerInfo()));
		afc.setAudioFormat(ac.getAudioFormat());
		ms.setOverwrite(ac.isOverwrite());
		ms.setMode(ac.getMode());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public synchronized void actionPerformed(ActionEvent e) {

		String pss = ac.getPlaybackStatus().getStatus();
		String css = ac.getCaptureStatus().getStatus();

		try {
			if ((pss == PlaybackStatus.PLAYING || pss == PlaybackStatus.PAUSE)
					|| (css == CaptureStatus.RECORDING
							|| css == CaptureStatus.CAPTURING
							|| css == CaptureStatus.BUSY || css == CaptureStatus.PAUSED)) {

				throw new AudioControllerException(
						"Controller must be stopped to reconfigure !");
			}
			if (DEBUG)
				System.out.println("Closing controller.");
			ac.close();
			ac.setSourceMixer(mp.getSelectedPlaybackMixer().getMixerInfo());
			ac.setTargetMixer(mp.getSelectedCaptureMixer().getMixerInfo());
			ac.setAudioFormat(afc.getAudioFormat());
			ac.setOverwrite(ms.getOverwrite());
			ac.setMode(ms.getMode());
			ac.configure();
			ac.open();
		} catch (AudioControllerException ex) {
			JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(),
					"AudioController Error", JOptionPane.ERROR_MESSAGE);
			System.err.println(ex.getLocalizedMessage());
		}

	}

}
