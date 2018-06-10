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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.ButtonGroup;
import javax.swing.JTabbedPane;

/**
 *
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * use MixerManagerUI instead
 *  
 */
public class MixerDevicesUI extends JTabbedPane implements ActionListener {

	final static boolean DEBUG = false;

	private Mixer.Info[] mixerInfos = null;

	private Mixer[] mixers = null;

	private Mixer selectedPlaybackMixer = null;

	private Mixer selectedCaptureMixer = null;

	private MixerConfigPanel[] mixerPanels = null;

	private int numMixers;

	private ButtonGroup playbackMixerButtonGroup;

	private ButtonGroup captureMixerButtonGroup;

	private Vector<ActionListener> listenerList = new Vector<ActionListener>();

	private static int id = 0;

	public MixerDevicesUI() {
		super();
		createUI();
	}

	public MixerDevicesUI(Mixer selectedTargetMixer, Mixer selectedSourceMixer) {
		super();
		createUI();
		setSelectedCaptureMixer(selectedTargetMixer);
		setSelectedPlaybackMixer(selectedSourceMixer);
	}

	private void createUI() {
		//setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		mixerInfos = AudioSystem.getMixerInfo();
		numMixers = mixerInfos.length;
		mixers = new Mixer[numMixers];
		mixerPanels = new MixerConfigPanel[numMixers];
		playbackMixerButtonGroup = new ButtonGroup();
		captureMixerButtonGroup = new ButtonGroup();
		for (int i = 0; i < numMixers; i++) {
			mixers[i] = AudioSystem.getMixer(mixerInfos[i]);
			if (mixers[i] == null)
				continue;
			if (DEBUG)
				System.out.println(mixerInfos[i]);
			mixerPanels[i] = new MixerConfigPanel(mixers[i], this);
			addTab(mixerInfos[i].getName(), mixerPanels[i]);
			playbackMixerButtonGroup.add(mixerPanels[i].selectPlayRadioButton);
			captureMixerButtonGroup
					.add(mixerPanels[i].selectCaptureRadioButton);
		}

		revalidate();
		repaint();

	}

	//	public void update() {
	//		for (int i = 0; i < numMixers; i++) {
	//			if (mixers[i] == ac.getSourceMixer()) {
	//				mixerPanels[i].selectPlayRadioButton.setSelected(true);
	//				selectedPlaybackMixer = mixers[i];
	//			}
	//			if (mixers[i] == ac.getTargetMixer()) {
	//				mixerPanels[i].selectCaptureRadioButton.setSelected(true);
	//				selectedCaptureMixer = mixers[i];
	//			}
	//		}
	//	}
	//	
	public void setSelectedPlaybackMixer(Mixer playbackMixer) {
		for (int i = 0; i < numMixers; i++) {
			mixerPanels[i].selectPlayRadioButton.setSelected(false);
			if (mixers[i] == playbackMixer) {
				mixerPanels[i].selectPlayRadioButton.setSelected(true);
				selectedPlaybackMixer = mixers[i];
			}

		}
	}

	public void setSelectedCaptureMixer(Mixer captureMixer) {
		for (int i = 0; i < numMixers; i++) {
			mixerPanels[i].selectCaptureRadioButton.setSelected(false);
			if (mixers[i] == captureMixer) {
				mixerPanels[i].selectCaptureRadioButton.setSelected(true);
				selectedCaptureMixer = mixers[i];
			}
		}
	}

	public Mixer getSelectedPlaybackMixer() {
		return selectedPlaybackMixer;
	}

	public Mixer getSelectedCaptureMixer() {
		return selectedCaptureMixer;
	}

	public void setEnabled(boolean enabled) {
		for (int i = 0; i < numMixers; i++) {
			mixerPanels[i].setEnabled(enabled);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < numMixers; i++) {
			if (e.getSource() == mixerPanels[i].selectPlayRadioButton) {
				selectedPlaybackMixer = mixers[i];
				updateListeners(new ActionEvent(this, id++,
						"playback device changed"));
			} else if (e.getSource() == mixerPanels[i].selectCaptureRadioButton) {
				selectedCaptureMixer = mixers[i];
				updateListeners(new ActionEvent(this, id++,
						"capture device changed"));
			}
		}
	}

	public synchronized void addActionListener(ActionListener al) {
		if (al != null && !listenerList.contains(al)) {
			listenerList.addElement(al);
		}
	}

	public synchronized void removeActionListener(ActionListener al) {
		if (al != null) {
			listenerList.removeElement(al);
		}
	}

	protected synchronized void updateListeners(ActionEvent ae) {
		Iterator<ActionListener> it = listenerList.iterator();
		while (it.hasNext()) {
			ActionListener listener = (ActionListener) it.next();
			listener.actionPerformed(ae);
		}
	}

}
