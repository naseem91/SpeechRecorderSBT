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
 * Date  : Apr 22, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.mixer.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 * Graphical panel for audio mixer controls.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class PortMixersUI extends JPanel implements AncestorListener, ActionListener {

	public int DEF_UPDATE_INTERVALL=100;
	
	private ArrayList<MixerPortsUI> mixerPortUIs=new ArrayList<MixerPortsUI>();
	private JTabbedPane portMixersTabPane = null;

	private Object value;

	private JDialog d;
	
	private Timer updateTimer;
	private int updateIntervall;
	private boolean updating=true;

	/**
	 * Creates panel with mixer controls.
	 * All port mixers visible by JavaSound are graphically displayed.
	 * A timer for updating the mixer controls (there is no notification from the system, or ?) is also created.
	 * @throws LineUnavailableException
	 */
	public PortMixersUI() throws LineUnavailableException {
		super(new BorderLayout());
		Mixer.Info[]  mixerInfos = AudioSystem.getMixerInfo();
		for (int i = 0; i < mixerInfos.length; i++) {
			Mixer m = AudioSystem.getMixer(mixerInfos[i]);
			String name = m.getMixerInfo().getName();
			if (hasPorts(m)) {
				if (portMixersTabPane == null)
					portMixersTabPane = new JTabbedPane();
				//JPanel
				MixerPortsUI mpui=new MixerPortsUI(m);
				mixerPortUIs.add(mpui);
				portMixersTabPane.addTab(name,mpui );
				//add(name,new MixerPortsUI(m));
			}
		}
		if (portMixersTabPane == null) {
			add(new JLabel("Sorry, no mixers found !"), BorderLayout.CENTER);
		} else {
			//portMixersTabPane.validate();
			add(portMixersTabPane, BorderLayout.CENTER);
		}
		updateIntervall=DEF_UPDATE_INTERVALL;
		updateTimer=new Timer(updateIntervall,this);
		updateTimer.setRepeats(true);
		addAncestorListener(this);
	}

	
	private boolean hasPorts(Mixer m) throws LineUnavailableException {
		//m.open();
		Line.Info[] lineInfo = m.getSourceLineInfo();
		for (int i = 0; i < lineInfo.length; i++) {
			if (lineInfo[i] instanceof Port.Info)
				return true;
		}
		lineInfo = m.getTargetLineInfo();
		for (int i = 0; i < lineInfo.length; i++) {
			if (lineInfo[i] instanceof Port.Info)
				return true;
		}
		return false;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		//Make sure we have nice window decorations.
		//JFrame.setDefaultLookAndFeelDecorated(true);

		//Create and set up the window.
		JFrame frame = new JFrame("Audio Mixer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		JComponent newContentPane = null;
		try {
			newContentPane = new PortMixersUI();
		} catch (LineUnavailableException e) {
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
					"Audio mixer error", JOptionPane.ERROR_MESSAGE);
			;
		}

		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);

		frame.pack();
		frame.setVisible(true);
	}

	public Object getValue(){
		return value;
	}
	
	public JDialog getDialog(){
		return d;
	}
	
	public JDialog createDialog(Frame owner){
		JDialog dialog=new JDialog(owner,"Audio mixer",true);
		dialog.getContentPane().add(this);
		//dialog.addWindowListener(this);
		return dialog;
	}
	
	public Object showDialog(Frame parent){
		d=createDialog(parent);
		d.pack();
		if(parent!=null){
		d.setLocationRelativeTo(parent);
		}
		d.setVisible(true);
		
		return getValue();
	}
	
	
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/**
	 * Update all mixer controls.
	 *
	 */
	public void updateValue() {
		for(MixerPortsUI mpui:mixerPortUIs){
			mpui.updateValue();
		}
	}


	public void ancestorAdded(AncestorEvent event) {
		if(event.getAncestor().isVisible()&&updating){
			updateTimer.start();
		}
	}

	public void ancestorMoved(AncestorEvent event) {
		// Not interesting
		
	}

	public void ancestorRemoved(AncestorEvent event) {
		updateTimer.stop();
		
	}

	public void actionPerformed(ActionEvent e) {
		Object src=e.getSource();
		if(src==updateTimer && updating)updateValue();
		
	}

	/**
	 * Get time intervall of automatic updates of the mixer controls.
	 * @return update intervall in milliseconds
	 */
	public int getUpdateIntervall() {
		return updateIntervall;
	}

	/**
	 * Set time intervall of automatic updates of the mixer controls.
	 * @param updateIntervall in milliseconds
	 */
	public void setUpdateIntervall(int updateIntervall) {
		this.updateIntervall = updateIntervall;
	}

	/**
	 * Get automatic updating flag.
	 * @return true if the mixer controls are automtically updated.
	 */
	public boolean isUpdating() {
		return updating;
	}

	/**
	 * Enable/disable automatic updating of the mixer controls.
	 * Default is true;
	 * @param updating
	 */
	public void setUpdating(boolean updating) {
		this.updating = updating;
		updateTimer.stop();
	}

}
