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

import ipsk.audio.actions.LoopAction;
import ipsk.audio.actions.PauseAction;
import ipsk.audio.actions.SetFramePositionAction;
import ipsk.audio.actions.StartPlaybackAction;
import ipsk.audio.actions.StartRecordAction;
import ipsk.audio.actions.StopAction;
import ipsk.audio.events.FramePositionActionEvent;
import ipsk.audio.events.PauseActionEvent;
import ipsk.audio.events.StartPlaybackActionEvent;
import ipsk.swing.TitledPanel;
import ipsk.text.MediaTimeFormat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.sampled.AudioSystem;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
public class TransportUI extends JPanel implements PropertyChangeListener,
		ChangeListener {

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

	private JButton playB;

	private JButton recB;

	private JButton stopB;

	private JButton pauseB;
	private JCheckBox loopBox;

	private JTextField framePosLabel;

	private JPanel buttonPanel;

	private JPanel posPanel;

	private JSlider posSlider;

	//private JSlider startPosSlider;
	//private JSlider stopPosSlider;
	//private long startPosition;
	//private long stopPosition;

	private MediaTimeFormat timeFormatter;

	private Vector<ActionListener> listenerList = new Vector<ActionListener>();

	private float frameRate;

	private boolean showRecordingUI = true;

	private StartPlaybackAction startPlaybackAction;

	private StartRecordAction startRecordAction;

	private StopAction stopAction;

	private PauseAction pauseAction;

	private boolean paused;

	private SetFramePositionAction setFramePositionAction;

	private LoopAction loopAction;
	TransportUI() {
	super();
	}

	
	public TransportUI(StartPlaybackAction startAction, StopAction stopAction) {
		this(startAction,stopAction,null);
	}
	
	public TransportUI(StartPlaybackAction startAction, StopAction stopAction,
			PauseAction pauseAction) {
		this(startAction,stopAction,pauseAction,null,null,null);
	}
	
	
    public TransportUI(StartPlaybackAction startAction, StopAction stopAction,
            PauseAction pauseAction, LoopAction loopAction) {
        this(startAction,stopAction,pauseAction,null,null,loopAction);
    }

	
	public TransportUI(StartPlaybackAction spa, StopAction stopAction,
			PauseAction pauseAction,
			SetFramePositionAction setFramePositionAction,
			StartRecordAction startRecordAction,LoopAction loopAction) {

//		super("Transport");
		super();
		this.startPlaybackAction = spa;
		this.stopAction = stopAction;
		this.pauseAction = pauseAction;
		this.setFramePositionAction = setFramePositionAction;
		this.startRecordAction = startRecordAction;
		this.loopAction=loopAction;
		setLayout(new BorderLayout());
		framePosLabel = new JTextField("0", 10);
		framePosLabel.setEditable(false);
		framePosLabel.setHorizontalAlignment(JTextField.RIGHT);
		playbackFrameLength = 100000;

		buttonPanel = new JPanel(new FlowLayout());

		playB = new JButton(startPlaybackAction);
		playB.setBackground(Color.GREEN.darker());
		buttonPanel.add(playB);
		//playB.addActionListener(this);
		if (showRecordingUI && startRecordAction!=null) {
			recB = new JButton(startRecordAction);
			recB.setBackground(Color.RED.darker());
			buttonPanel.add(recB);
			startRecordAction.addPropertyChangeListener(this);
		}

		//recB.addActionListener(this);
		stopB = new JButton(stopAction);
		stopB.setBackground(Color.YELLOW.darker());
		buttonPanel.add(stopB);
		//stopB.addActionListener(this);
		if(pauseAction!=null){
		pauseB = new JButton(pauseAction);
		pauseB.setBackground(Color.YELLOW.darker());
		buttonPanel.add(pauseB);
		pauseAction.addPropertyChangeListener(this);
		}
		//pauseB.addActionListener(this);
		if(loopAction!=null){
			loopBox=new JCheckBox(loopAction);
			buttonPanel.add(loopBox);
		}
		posUpdate = false;
		if(setFramePositionAction!=null){
			posSlider = new JSlider();
			posSlider.setMinimum(0);

			// TODO divide this value if frameLength does not fit into Integer size
			posSlider.setMaximum((int) playbackFrameLength);
			//posSlider.setExtent(10000);

			// TODO Ugly !!
			posSlider.setMajorTickSpacing(500000);
			posSlider.setPaintTicks(true);
			//posSlider.setPaintLabels(true);
			//posSlider.setEnabled(true);
			posSlider.addChangeListener(this);

			//		startPosSlider = new JSlider();
			//		startPosSlider.setValue(0);
			//		startPosSlider.setEnabled(false);
			//		stopPosSlider = new JSlider();
			//		stopPosSlider.setValue(stopPosSlider.getMaximum());
			//		stopPosSlider.setEnabled(false);
			//		startPosSlider.addChangeListener(this);
			//		stopPosSlider.addChangeListener(this);
			posPanel = new JPanel(new GridLayout(2, 1));
			posPanel.add(framePosLabel);
			posPanel.add(posSlider);
			//		posPanel.add(startPosSlider);
			//		posPanel.add(stopPosSlider);
			add(posPanel, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.NORTH);
			setFramePositionAction.addPropertyChangeListener(this);
		}else{
			add(buttonPanel,BorderLayout.CENTER);
		}
		timeFormatter = new MediaTimeFormat();
		//setStatus(CLOSE);

		startPlaybackAction.addPropertyChangeListener(this);
		stopAction.addPropertyChangeListener(this);
		
		
		validate();
		repaint();

	}
    
	

    /**
	 * @see javax.swing.event.ChangeListener#stateChanged
	 */
	public synchronized void stateChanged(ChangeEvent ev) {
		JSlider source = (JSlider) ev.getSource();

		if (!source.getValueIsAdjusting() && !posUpdate) {
			if (source == posSlider) {
				long newPos = source.getValue();
				if (DEBUG)
					System.out.println("Slider: " + newPos);

				if (paused && pendingStart) {
					pendingStart = false;
					FramePositionActionEvent fpae = new FramePositionActionEvent(
							this, newPos);
					//fireActionEvent(fpae);
					setFramePositionAction.actionPerformed(fpae);
					StartPlaybackActionEvent spae = new StartPlaybackActionEvent(
							this);
					startPlaybackAction.actionPerformed(spae);
					//fireActionEvent(spae);
				} else if (paused) {
					FramePositionActionEvent fpae = new FramePositionActionEvent(
							this, newPos);
					//fireActionEvent(fpae);
					setFramePositionAction.actionPerformed(fpae);
				}
			}
			//			} else if (source == startPosSlider) {
			//				startPosition = source.getValue();
			//				//setPlaybackStartFramePosition(startPosition);
			//			} else if (source == stopPosSlider) {
			//				stopPosition = source.getValue();
			//				//setPlaybackStopFramePosition(stopPosition);
			//			}
		} else if (source.getValueIsAdjusting() && !posUpdate) {
			if (source == posSlider) {
				if(posSlider.isEnabled()){
				setPosLabel(source.getValue());
				if (!paused) {
					PauseActionEvent ppae = new PauseActionEvent(this);
					//fireActionEvent(ppae);
					pauseAction.actionPerformed(ppae);
					pendingStart = true;
				}	
				}
//				else{
//					posSlider.setValueIsAdjusting(false);
//				}
			}
		}
	}

	public void setFrameLength(long length) {
		playbackFrameLength = length;
		posUpdate = true;

		posSlider.setMaximum((int) playbackFrameLength);
		if (DEBUG)
			System.out.println("Slider max set to " + playbackFrameLength);

		posUpdate = false;
		//posSlider.setEnabled(true);
	}

	private void setPosLabel(long pos) {

		Object formatObj = null;
		if (pos != AudioSystem.NOT_SPECIFIED) {
			double seconds = pos / frameRate;
			formatObj = new Double(seconds);
			framePosLabel.setText(timeFormatter.format(formatObj));
		}else{
		framePosLabel.setText(timeFormatter.format(null));
		}
	}

	public synchronized void setFramePosition(long pos) {
		playbackFramePos = pos;

		// do not "disturb" manual user adjusting
		if (!posSlider.getValueIsAdjusting()) {
			if (!paused) {
				posUpdate = true;
				posSlider.setValue((int) playbackFramePos);
				posUpdate = false;
			}

			setPosLabel(playbackFramePos);
		}
		if (DEBUG)
			System.out.println("Slider: " + posSlider.getMinimum() + " "
					+ playbackFramePos + "+" + posSlider.getMaximum());
	}

	public synchronized void addActionListener(ActionListener acl) {
		if (acl != null && !listenerList.contains(acl)) {
			listenerList.addElement(acl);
		}
	}

	public synchronized void removeActionListener(ActionListener acl) {
		if (acl != null) {
			listenerList.removeElement(acl);
		}
	}

	protected synchronized void fireActionEvent(ActionEvent ae) {
		Iterator<ActionListener> it = listenerList.iterator();

		while (it.hasNext()) {
			ActionListener listener = it.next();
			listener.actionPerformed(ae);
		}
	}

	public float getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(float f) {
		frameRate = f;
	}

	public boolean isShowRecordingUI() {
		return showRecordingUI;
	}

	
	public synchronized void setShowRecordingUI(boolean b) {
		if (showRecordingUI != b) {
			if (!b) {
				buttonPanel.remove(recB);
			}
			showRecordingUI = b;

		}

	}

	/**
	 * Returns playback start action
	 * @return start playback action
	 */
	public StartPlaybackAction getStartPlaybackAction() {
		return startPlaybackAction;
	}

	/**
	 * Set playback start action.
	 * @param startPlaybackAction start playback action
	 *          
	 */
	public void setStartPlaybackAction(StartPlaybackAction startPlaybackAction) {
		this.startPlaybackAction.removePropertyChangeListener(this);
		this.startPlaybackAction = startPlaybackAction;
		if(playB!=null){
			playB.setAction(this.startPlaybackAction);
	}
		this.startPlaybackAction.addPropertyChangeListener(this);
	}

	/**
	 * Get pause action.
	 * @return pause action
	 */
	public PauseAction getPauseAction() {
		return pauseAction;
	}

	/**
	 * Set pause action.
	 * @param pauseAction pause action
	 *            
	 */
	public void setPauseAction(PauseAction pauseAction) {
		this.pauseAction = pauseAction;
		if(pauseB!=null){
			pauseB.setAction(pauseAction);
		}
	}

	/**
	 * Get start record action.
	 * @return start record action
	 */
	public StartRecordAction getStartRecordAction() {
		return startRecordAction;
	}

	/**
	 * Set start record action.
	 * @param startRecordAction start record action
	 */
	public void setStartRecordAction(StartRecordAction startRecordAction) {
		this.startRecordAction = startRecordAction;
	}

	/**
	 * Get stop action.
	 * @return stop action.
	 */
	public StopAction getStopAction() {
		return stopAction;
	}

	/**
	 * Set stop action.
	 * @param stopAction stop action
	 */
	public void setStopAction(StopAction stopAction) {
		this.stopAction.removePropertyChangeListener(this);
		this.stopAction = stopAction;
		if(stopB!=null){
			stopB.setAction(this.stopAction);
		}
		this.stopAction.addPropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		Object src = evt.getSource();
		if (src == startPlaybackAction) {
			if (startPlaybackAction.isHighlighted()) {
				playB.setBackground(Color.GREEN);
			} else {
				playB.setBackground(Color.GREEN.darker());
			}
		} else if (src == stopAction) {
			if (stopAction.isHighlighted()) {
				stopB.setBackground(Color.YELLOW);
			} else {
				stopB.setBackground(Color.YELLOW.darker());
			}
		} else if (src == pauseAction) {
			if (pauseAction.isHighlighted()) {
				pauseB.setBackground(Color.YELLOW);
			} else {
				pauseB.setBackground(Color.YELLOW.darker());
			}
		} else if (src == startRecordAction) {
			if (startRecordAction.isHighlighted()) {
				recB.setBackground(Color.RED);
			} else {
				recB.setBackground(Color.RED.darker());
			}
		} else if (src == setFramePositionAction) {
			boolean framePositioningEnabled=setFramePositionAction.isEnabled();
			posSlider.setEnabled(framePositioningEnabled);
			if(!framePositioningEnabled){
				posSlider.setValueIsAdjusting(false);
			}
		}

	}

	/**
	 * Set pause status.
	 * @param b pause status 
	 */
	public void setPaused(boolean b) {
		paused = b;

	}

	/**
	 * Get pause status.
	 * @return pause status
	 */
	public boolean isPaused() {
		return paused;
	}
}
