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
 * Created on 18.08.2005
 *
 */
package ipsk.audio.player;

import ipsk.audio.player.event.PlayerEvent;
import ipsk.audio.player.event.PlayerStartEvent;
import ipsk.audio.player.event.PlayerStopEvent;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author klausj
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PlayerUI extends JFrame implements ActionListener, ChangeListener,
		PlayerListener, Runnable {
	JButton playB;

	JButton startB;

	JButton stopB;

	JButton pauseB;

	Player p;

	Container content;

	JSlider posSlider;

	JSlider startPosSlider;

	JSlider stopPosSlider;

	JCheckBox loopBox;

	Timer updateTimer;

	private boolean posUpdate;

	public PlayerUI(File f) throws PlayerException {
		super();
		content = getContentPane();
		content.setLayout(new FlowLayout());
		addWindowListener(new WindowAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
			 */
			public void windowClosing(WindowEvent arg0) {
				close();
			}

		});
		Mixer.Info[] infos = AudioSystem.getMixerInfo();
		Mixer m = AudioSystem.getMixer(infos[0]);
		p = new Player(m, f);
		p.addPlayerListener(this);
		p.open();
		playB = new JButton("Play");
		playB.addActionListener(this);
		content.add(playB);
		startB = new JButton("Start");
		startB.addActionListener(this);
		content.add(startB);
		stopB = new JButton("Stop");
		stopB.addActionListener(this);
		content.add(stopB);
		pauseB = new JButton("Pause");
		pauseB.addActionListener(this);
		content.add(pauseB);
		posSlider = new JSlider();
		posSlider.setMinimum(0);

		// TODO divide this value if frameLength does not fit into Integer size
		posSlider.setMaximum((int) p.getFrameLength());
		posSlider.addChangeListener(this);
		startPosSlider = new JSlider();
		startPosSlider.setMinimum(0);

		// TODO divide this value if frameLength does not fit into Integer size
		startPosSlider.setMaximum((int) p.getFrameLength());
		startPosSlider.addChangeListener(this);
		stopPosSlider = new JSlider();
		stopPosSlider.setMinimum(0);

		// TODO divide this value if frameLength does not fit into Integer size
		stopPosSlider.setMaximum((int) p.getFrameLength());
		stopPosSlider.setValue((int) p.getFrameLength());
		stopPosSlider.addChangeListener(this);
		loopBox = new JCheckBox("Loop");
		loopBox.setSelected(p.isLooping());
		loopBox.addActionListener(this);
		content.add(posSlider);
		content.add(startPosSlider);
		content.add(stopPosSlider);
		content.add(loopBox);
		posUpdate = false;
		updateTimer = new Timer(1, this);
		updateTimer.start();

	}

	public void close() {

		try {
			p.close();
		} catch (PlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateTimer.stop();
		this.dispose();
		System.exit(0);
	}

	public static void main(String[] args) {

		PlayerUI playerUi = null;

		try {
			playerUi = new PlayerUI(new File(args[0]));
		} catch (PlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		playerUi.pack();
		playerUi.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == updateTimer) {

			long pos = p.getFramePosition();
			// System.out.println(pos);
			// do not "disturb" manual user adjusting
			if (!posSlider.getValueIsAdjusting()) {
				posUpdate = true;
				posSlider.setValue((int) pos);
				posUpdate = false;

			}

		} else if (src == playB) {
			try {
				p.play();
			} catch (PlayerException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		} else if (src == startB) {
			p.start();
		} else if (src == stopB) {
			p.stop();
		} else if (src == pauseB) {
			p.pause();
		} else if (src == loopBox) {
			p.setLooping(loopBox.isSelected());
		}

	}

	public void stateChanged(ChangeEvent arg0) {
		Object src = arg0.getSource();
		if (src == posSlider) {
			if (!posSlider.getValueIsAdjusting() && !posUpdate) {

				try {
					p.setFramePosition(posSlider.getValue());
				} catch (PlayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} else if (src == startPosSlider) {
			p.setStartFramePosition(startPosSlider.getValue());
		} else if (src == stopPosSlider) {
			p.setStopFramePosition(stopPosSlider.getValue());
		}
	}

	public void update(PlayerEvent playerEvent) {

		if (playerEvent instanceof PlayerStartEvent) {
			startB.setEnabled(false);
			stopB.setEnabled(true);

		} else if (playerEvent instanceof PlayerStopEvent) {
			stopB.setEnabled(false);
			startB.setEnabled(true);
		}

	}

	public void run() {

	}

}
