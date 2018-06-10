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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.Port.Info;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Scrollable;


/**
 * Mixer ports (audio mixer controls) UI for playback or capture.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class FilteredMixerPortsUI extends JPanel implements Scrollable {
	final static boolean DEBUG = false;

	private ResourceBundle rb;

	private Vector<Info> portInfos;

	private Line[] ports;

	private int numPorts;

	private JPanel portsPanel;
	
	private Vector<LineControlsUI> lineControls=new Vector<LineControlsUI>();
	
	

	public FilteredMixerPortsUI(Mixer mixer, boolean isSource) {
		super();
		portsPanel = new JPanel();
		portsPanel.setLayout(new GridBagLayout());
		String packageName = getClass().getPackage().getName();
		rb = ResourceBundle.getBundle(packageName + ".ResBundle");
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.NORTH;
		//c.insets = new Insets(2, 5, 2, 5);
		c.gridx = 0;
		c.gridy = 0;

		//mixerInfo = mixer.getMixerInfo();

		portInfos = new Vector<Info>();

		Line.Info[] sourceLineInfos = mixer.getSourceLineInfo();
		for (int i = 0; i < sourceLineInfos.length; i++) {

			if (sourceLineInfos[i] instanceof Port.Info) {

				portInfos.add((Port.Info) sourceLineInfos[i]);
			}
		}
		Line.Info[] targetLineInfos = mixer.getTargetLineInfo();
		for (int i = 0; i < targetLineInfos.length; i++) {
			if (targetLineInfos[i] instanceof Port.Info) {

				portInfos.add((Port.Info) targetLineInfos[i]);
			}
		}

		int nPorts = portInfos.size();
		ports = new Line[nPorts];
		for (int i = 0; i < nPorts; i++) {

			Line.Info info = (Line.Info) portInfos.get(i);
			Port.Info pi = (Port.Info) info;
			if (pi.isSource() == isSource) {
				numPorts++;
				Line l = null;
				try {
					l = mixer.getLine(info);
					l.open();
				} catch (LineUnavailableException e) {
					JOptionPane.showMessageDialog(this, rb
							.getString("error_audio_system"), e
							.getLocalizedMessage(), JOptionPane.ERROR_MESSAGE);
				}

				c.gridx++;
				LineControlsUI lc=new LineControlsUI(l);
				lineControls.add(lc);
				portsPanel.add(lc, c);
			}
		}
		add(portsPanel);
	}

	public void close() {
		for (int i = 0; i < ports.length; i++) {
			ports[i].close();
		}
	}

	//	/**
	//	 * @param m
	//	 * @return
	//	 */
	//	private static boolean hasPorts(Mixer m) throws LineUnavailableException
	// {
	//		m.open();
	//		boolean hasPorts = false;
	//		Line.Info[] lineInfo = m.getSourceLineInfo();
	//		for (int i = 0; i < lineInfo.length; i++) {
	//			if (lineInfo[i] instanceof Port.Info)
	//				hasPorts = true;
	//			break;
	//		}
	//		if (!hasPorts) {
	//			lineInfo = m.getTargetLineInfo();
	//			for (int i = 0; i < lineInfo.length; i++) {
	//				if (lineInfo[i] instanceof Port)
	//					hasPorts = true;
	//			}
	//		}
	//		m.close();
	//		return hasPorts;
	//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	public boolean getScrollableTracksViewportHeight() {

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth() {

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {

		double height = getPreferredSize().getHeight();
		return new Dimension(-1, (int) height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle,
	 *      int, int)
	 */
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle,
	 *      int, int)
	 */
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNumPorts() {
		return numPorts;
	}

	public void setNumPorts(int numPorts) {
		this.numPorts = numPorts;
	}

	public void updateValue() {
		for(LineControlsUI lc:lineControls){
			lc.updateValue();
		}
		
	}

}
