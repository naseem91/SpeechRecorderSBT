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
 * Date  : Apr 25, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.ui;

import ipsk.audio.AudioController;
import ipsk.audio.AudioControllerException;
import ipsk.audio.AudioControllerListener;
import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioController.CaptureStatus;
import ipsk.audio.AudioController.PlaybackStatus;
import ipsk.audio.impl.j2audio.J2AudioController;
import ipsk.swing.TitledPanel;
import ipsk.text.MediaTimeFormat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel for audio transport. Has buttons to play,record,stop and pause audio.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class TransportPanel extends TitledPanel implements ActionListener,
		ChangeListener, AudioControllerListener, Runnable {

	private final static boolean DEBUG = false;

	static final int STOP = 0;

	static final int PAUSE = 1;

	static final int PLAY_PAUSE = 2;

	static final int PLAY = 3;

	static final int REC_PAUSE = 4;

	static final int REC = 5;

	static final int BUSY = 6;

	static final int CLOSE = 7;

	private boolean pendingStart = false;

	private boolean posUpdate;

	private long playbackFramePos;

	private long playbackFrameLength;

	private boolean running;

	private AudioController ac;

	private int status = CLOSE;

	private JButton playB;

	private JButton recB;

	private JButton stopB;

	private JButton pauseB;

	private JTextField framePosLabel;

	private Thread updateThread;

	private JPanel buttonPanel;

	private JPanel posPanel;

	private JSlider posSlider;

	private JSlider startPosSlider;

	private JSlider stopPosSlider;

	private long startPosition;

	private long stopPosition;

	private MediaTimeFormat timeFormatter;

	/**
	 * Creates transport panel for an audio controller.
	 * 
	 * @param ac
	 *            the audio controller to control
	 */
	public TransportPanel(AudioController ac) {

		super("Transport");
		this.ac = ac;
		setLayout(new BorderLayout());
		buttonPanel = new JPanel(new FlowLayout());
		framePosLabel = new JTextField("0", 10);
		framePosLabel.setEditable(false);
		framePosLabel.setHorizontalAlignment(JTextField.RIGHT);
		//
		createButtons(buttonPanel);
		add(buttonPanel, BorderLayout.NORTH);
		posUpdate = true;

		posSlider = new JSlider();
		posSlider.setMinimum(0);
		posSlider.setMaximum(100000);
		//posSlider.setExtent(10000);
		posSlider.setMajorTickSpacing(500000);
		posSlider.setPaintTicks(true);
		//posSlider.setPaintLabels(true);
		posSlider.setEnabled(false);
		posSlider.addChangeListener(this);

		startPosSlider = new JSlider();
		startPosSlider.setValue(0);
		startPosSlider.setEnabled(false);
		stopPosSlider = new JSlider();
		stopPosSlider.setValue(stopPosSlider.getMaximum());
		stopPosSlider.setEnabled(false);
		startPosSlider.addChangeListener(this);
		stopPosSlider.addChangeListener(this);
		posPanel = new JPanel(new GridLayout(4, 1));
		posPanel.add(framePosLabel);
		posPanel.add(posSlider);
		posPanel.add(startPosSlider);
		posPanel.add(stopPosSlider);
		add(posPanel, BorderLayout.CENTER);
		ac.addAudioControllerListener(this);
		setStatus(CLOSE);
		timeFormatter = new MediaTimeFormat();
		validate();
		repaint();
		updateThread = new Thread(this);
		updateThread.setPriority(Thread.MIN_PRIORITY);
		running = true;
		updateThread.start();
		posUpdate = false;
	}

	/**
	 * Sets status of the panel.
	 * 
	 * @param newStatus
	 */
	public void setStatus(int newStatus) {
		status = newStatus;
		switch (status) {
		case CLOSE:
		case BUSY:
			posSlider.setEnabled(false);
			playB.setEnabled(false);
			playB.setBackground(Color.GREEN.darker());
			recB.setEnabled(false);
			recB.setBackground(Color.RED.darker());
			stopB.setEnabled(false);
			stopB.setBackground(Color.YELLOW.darker());
			pauseB.setEnabled(false);
			pauseB.setBackground(Color.YELLOW.darker());
			break;
		case STOP:
			posSlider.setEnabled(false);
			playB.setEnabled(true);
			playB.setBackground(Color.GREEN.darker());
			recB.setEnabled(true);
			recB.setBackground(Color.RED.darker());
			stopB.setEnabled(false);
			stopB.setBackground(Color.YELLOW);
			pauseB.setEnabled(true);
			pauseB.setBackground(Color.YELLOW.darker());
			break;
		case PAUSE:
			posSlider.setEnabled(true);
			playB.setEnabled(true);
			playB.setBackground(Color.GREEN.darker());
			recB.setEnabled(true);
			recB.setBackground(Color.RED.darker());
			stopB.setEnabled(true);
			stopB.setBackground(Color.YELLOW.darker());
			pauseB.setEnabled(false);
			pauseB.setBackground(Color.YELLOW);
			break;
		case PLAY_PAUSE:
			posSlider.setEnabled(true);
			playB.setEnabled(true);
			playB.setBackground(Color.GREEN);
			recB.setEnabled(false);
			recB.setBackground(Color.RED.darker());
			stopB.setEnabled(true);
			stopB.setBackground(Color.YELLOW.darker());
			pauseB.setEnabled(true);
			pauseB.setBackground(Color.YELLOW);
			break;
		case REC_PAUSE:
			posSlider.setEnabled(false);
			playB.setEnabled(false);
			playB.setBackground(Color.GREEN.darker());
			recB.setEnabled(true);
			recB.setBackground(Color.RED);
			stopB.setEnabled(true);
			stopB.setBackground(Color.YELLOW.darker());
			pauseB.setEnabled(true);
			pauseB.setBackground(Color.YELLOW);
			break;
		case PLAY:
			posSlider.setEnabled(true);
			playB.setEnabled(false);
			playB.setBackground(Color.GREEN);
			recB.setEnabled(false);
			recB.setBackground(Color.RED.darker());
			stopB.setEnabled(true);
			stopB.setBackground(Color.YELLOW.darker());
			pauseB.setEnabled(true);
			pauseB.setBackground(Color.YELLOW.darker());

			break;
		case REC:
			posSlider.setEnabled(false);
			playB.setEnabled(false);
			playB.setBackground(Color.GREEN.darker());
			recB.setEnabled(false);
			recB.setBackground(Color.RED);
			stopB.setEnabled(true);
			stopB.setBackground(Color.YELLOW.darker());
			pauseB.setEnabled(false);
			pauseB.setBackground(Color.YELLOW.darker());
			break;
		}
	}

	protected void createButtons(JPanel buttonPanel) {
		playB = new JButton("PLAY");
		playB.setBackground(Color.GREEN.darker());
		buttonPanel.add(playB);
		playB.addActionListener(this);
		recB = new JButton("REC");
		recB.setBackground(Color.RED.darker());
		buttonPanel.add(recB);
		recB.addActionListener(this);
		stopB = new JButton("STOP");
		stopB.setBackground(Color.YELLOW);
		buttonPanel.add(stopB);
		stopB.addActionListener(this);
		pauseB = new JButton("PAUSE");
		pauseB.setBackground(Color.YELLOW.darker());
		buttonPanel.add(pauseB);
		pauseB.addActionListener(this);
	}

	/**
	 * Prepare record files. If the file exists ask the user to overwrite the
	 * file.
	 * 
	 * @return true if the files are prepared
	 * @throws AudioControllerException
	 * @throws HeadlessException
	 * @throws IOException
	 * @throws LineUnavailableException
	 * @throws AudioFormatNotSupportedException
	 */
	private boolean prepareRecordingInterActive()
			throws AudioControllerException, HeadlessException, IOException,
			LineUnavailableException, AudioFormatNotSupportedException {
		if (ac instanceof J2AudioController) {
			J2AudioController j2ac = (J2AudioController) ac;
			if (!j2ac.prepareRecording(false)) {
				String msg = new String();
				File[] recFiles = j2ac.getRecordingFiles();
				for (int i = 0; i < recFiles.length; i++) {
					if (recFiles[i].exists()) {
						msg = msg.concat("'" + recFiles[i].getCanonicalPath()
								+ "' already exists !\n");
					}
					if (JOptionPane.showConfirmDialog(this, msg
							+ " Do you want to overwrite ?",
							"Overwrite file(s) ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						j2ac.prepareRecording(true);

					} else {
						return false;
					}
				}
			}
		} else {
			ac.prepareRecording();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public synchronized void actionPerformed(ActionEvent ae) {
		//String ps = ac.getPlaybackStatus().getStatus();
		//String cs = ac.getCaptureStatus().getStatus();
		//boolean filesExits = false;
		if (ae.getSource() == recB) {

			try {
				if (status == STOP) {
					if (prepareRecordingInterActive())
						ac.startRecording();
				} else if (status == REC_PAUSE) {
					ac.startRecording();
				} else if (status == PAUSE) {
					if (prepareRecordingInterActive())
						ac.startCapture();
				}
			} catch (IOException e) {
				String errMsg = new String("I/O error:\n"
						+ e.getLocalizedMessage());
				JOptionPane.showMessageDialog(this, errMsg, "I/O Error !",
						JOptionPane.ERROR_MESSAGE);
				System.err.println(errMsg);
			} catch (AudioFormatNotSupportedException e) {
				//String errMsg= new String("Audio format not supported\n"+e);
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error !",
						JOptionPane.ERROR_MESSAGE);
				System.err.println(e);
			} catch (LineUnavailableException e) {
				String errMsg = new String("Error opening audio device\n"
						+ e.getMessage());
				JOptionPane.showMessageDialog(this, errMsg, "Error !",
						JOptionPane.ERROR_MESSAGE);
				System.err.println(errMsg);
			} catch (AudioControllerException e) {
				String errMsg = new String("Audio controller error\n"
						+ e.getLocalizedMessage());
				JOptionPane.showMessageDialog(this, errMsg,
						"Audio controller error !", JOptionPane.ERROR_MESSAGE);
			}
		} else if (ae.getSource() == stopB) {
			try {
				if (status == PAUSE) {
					setStatus(STOP);
				} else if (status == REC) {
					ac.stopRecording();
				} else if (status == REC_PAUSE) {
					ac.stopCapture();
				} else if (status == PLAY) {
					ac.stopPlayback();

				} else if (status == PLAY_PAUSE) {
					ac.stopPlayback();
				}

			} catch (AudioControllerException e) {
				String errMsg = new String(e.getLocalizedMessage());
				JOptionPane.showMessageDialog(this, errMsg,
						"AudioController Error !", JOptionPane.ERROR_MESSAGE);
				System.err.println(errMsg);
			}
		} else if (ae.getSource() == playB) {
			try {
				if (status == PAUSE) {
					ac.preparePlayback();
					ac.pausePlayback();
				} else if (status == STOP) {
					ac.preparePlayback();
					ac.startPlayback();
				} else if (status == PLAY_PAUSE) {
					ac.startPlayback();
				}
			} catch (AudioControllerException e) {
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
						"AudioController Error !", JOptionPane.ERROR_MESSAGE);
				System.err.println(e);
			}
		} else if (ae.getSource() == pauseB) {
			try {
				if (ac.getPlaybackStatus().getStatus() == PlaybackStatus.PLAYING) {
					ac.pausePlayback();
				} else if (status == REC_PAUSE) {
					ac.startRecording();

				} else if (status == PLAY_PAUSE) {

					ac.startPlayback();

				} else if (status == STOP) {
					setStatus(PAUSE);
				}
			} catch (AudioControllerException e) {
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
						"AudioController Error !", JOptionPane.ERROR_MESSAGE);
				System.err.println(e.getLocalizedMessage());
			}
		}
	}

	/**
	 * @see javax.swing.event.ChangeListener#stateChanged
	 */
	public void stateChanged(ChangeEvent ev) {
		JSlider source = (JSlider) ev.getSource();
		if (!source.getValueIsAdjusting() && !posUpdate) {
			if (source == posSlider) {
				playbackFramePos = source.getValue();

				if (status == PLAY_PAUSE && pendingStart) {
					pendingStart = false;
					try {
						//ac.preparePlayback();
						ac.setPlaybackFramePosition(playbackFramePos);
						ac.startPlayback();
					} catch (AudioControllerException e) {
						JOptionPane.showMessageDialog(this, e
								.getLocalizedMessage(),
								"AudioController Error !",
								JOptionPane.ERROR_MESSAGE);
						System.err.println(e.getLocalizedMessage());
					}
				} else if (status == PLAY_PAUSE) {
					ac.setPlaybackFramePosition(playbackFramePos);
				}
			} else if (source == startPosSlider) {
				startPosition = source.getValue();
				ac.setPlaybackStartFramePosition(startPosition);
			} else if (source == stopPosSlider) {
				stopPosition = source.getValue();
				ac.setPlaybackStopFramePosition(stopPosition);
			}
		} else if (source.getValueIsAdjusting() && !posUpdate) {
			if (source == posSlider) {
				if (status == PLAY) {
					try {
						ac.pausePlayback();
					} catch (AudioControllerException e) {
						JOptionPane.showMessageDialog(this, e
								.getLocalizedMessage(),
								"AudioController Error !",
								JOptionPane.ERROR_MESSAGE);
						System.err.println(e.getLocalizedMessage());
					}
					pendingStart = true;
				}
			}
		}
	}

	private void configurePositionSliders() {
		posUpdate = true;
		playbackFrameLength = ac.getPlaybackFrameLength();
		posSlider.setMaximum((int) playbackFrameLength);
		if (DEBUG)
			System.out.println("Slider max set to " + playbackFrameLength);
		startPosSlider.setMaximum((int) playbackFrameLength);
		startPosSlider.setValue((int) ac.getPlaybackStartFramePosition());
		stopPosSlider.setMaximum((int) playbackFrameLength);
		long stopPosition = ac.getPlaybackStopFramePosition();
		if (stopPosition == J2AudioController.AUDIO_END) {
			stopPosSlider.setValue((int) playbackFrameLength);
		} else {
			stopPosSlider.setValue((int) stopPosition);
		}
		posUpdate = false;
		posSlider.setEnabled(true);
		//		startPosSlider.setEnabled(true);
		//		stopPosSlider.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioControllerListener#update(ipsk.audio.PlaybackStatus,
	 *      ipsk.audio.CaptureStatus)
	 */
	public synchronized void update(PlaybackStatus ps, CaptureStatus cs) {
		if (ps != null) {
			if (DEBUG)
				System.out.println("Playback " + ps + ".");
			String pss = ps.getStatus();
			if (pss == PlaybackStatus.PLAYED || pss == PlaybackStatus.OPEN) {
				setStatus(STOP);
				setPlaybackFramePosition();
			} else if (pss == PlaybackStatus.PLAYING) {
				setStatus(PLAY);
			} else if (pss == PlaybackStatus.PAUSE) {
				setStatus(PLAY_PAUSE);
			} else if (pss == PlaybackStatus.PREPARED) {
				configurePositionSliders();
				setStatus(STOP);
			} else if (pss == PlaybackStatus.CLOSED) {
				setStatus(CLOSE);
			} else if (pss == PlaybackStatus.FILES_SET) {
				//configurePositionSliders();
			}
		}
		if (cs != null) {
			String css = cs.getStatus();
			if (DEBUG)
				System.out.println("Capture " + cs + ".");
			if (css == CaptureStatus.RECORDING) {
				setStatus(REC);
			} else if (css == CaptureStatus.OPEN) {
				setStatus(STOP);
				//ac.preparePlayback();
				playbackFrameLength = ac.getPlaybackFrameLength();
				configurePositionSliders();
			} else if (css == CaptureStatus.RECORDED) {
				setStatus(BUSY);
			} else if (css == CaptureStatus.SAVED) {
				setStatus(STOP);

			} else if (css == CaptureStatus.PAUSED
					|| css == CaptureStatus.CAPTURING) {
				setStatus(REC_PAUSE);

			} else if (css == CaptureStatus.CLOSED) {
				setStatus(CLOSE);
			}
		}
	}

	private void setPlaybackFramePosition() {
		playbackFramePos = ac.getPlaybackFramePosition();
		//posSlider.setValueIsAdjusting(true);
		posUpdate = true;
		//posSlider.setMaximum((int) playbackFrameLength);
		posSlider.setValue((int) playbackFramePos);
		//posSlider.setValueIsAdjusting(false);
		posUpdate = false;
		//framePosLabel.setText(new Long(playbackFramePos).toString());
		double seconds = playbackFramePos
				/ ac.getPlaybackAudioFormat().getFrameRate();
		framePosLabel.setText(timeFormatter.format(new Double(seconds)));
		if (DEBUG)
			System.out.println("Slider: " + posSlider.getMinimum() + " "
					+ playbackFramePos + "+" + posSlider.getMaximum());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (running) {
			if (status == REC) {
				//				framePosLabel.setText(
				//					new Long(ac.getRecordingFramePosition()).toString());
				double seconds = ac.getRecordingFramePosition()
						/ ac.getAudioFormat().getFrameRate();
				framePosLabel
						.setText(timeFormatter.format(new Double(seconds)));
			} else if (status == PLAY) {
				playbackFrameLength = ac.getPlaybackFrameLength();
				setPlaybackFramePosition();
				if (DEBUG)
					System.out.println("Slider: 0-" + playbackFramePos + "-"
							+ playbackFrameLength);
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// O.K., no problem
			}
		}
	}
}
