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
 * Date  : Apr 28, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.ui;

import ipsk.audio.AudioController;
import ipsk.audio.AudioControllerListener;
import ipsk.audio.AudioController.CaptureStatus;
import ipsk.audio.AudioController.PlaybackStatus;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JPanel;


/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class AudioControllerUI extends JPanel implements
		AudioControllerListener, ActionListener {

	private final static boolean DEBUG = false;

	private ResourceBundle rb;

	private ConfigurationPanel cp;

	/**
	 * An UI interface to configure a {@link ipsk.audio.AudioController}.
	 *  
	 */
	public AudioControllerUI(AudioController ac) {
		super(new BorderLayout());
		String packageName = getClass().getPackage().getName();
		rb = ResourceBundle.getBundle(packageName + ".ResBundle");
		cp = new ConfigurationPanel(ac);

		add(cp, BorderLayout.CENTER);

		//add(fsc, BorderLayout.SOUTH);
		ac.addAudioControllerListener(this);
		cp.update();
		revalidate();
		repaint();

		//fsc.addActionListener(this);
	}

	public void update() {
		cp.update();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioControllerListener#update(ipsk.audio.PlaybackEvent,
	 *      ipsk.audio.CaptureEvent)
	 */
	public void update(PlaybackStatus pe, CaptureStatus ce) {
		String ps = null;
		String cs = null;
		if (pe != null) {
			ps = pe.getStatus();
		}
		if (ce != null) {
			cs = ce.getStatus();
		}

		if (ps == PlaybackStatus.CONFIGURED || cs == CaptureStatus.CONFIGURED) {
			cp.update();
			revalidate();
			repaint();
		} else if (cs == CaptureStatus.SAVED) {
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ev) {
		if (DEBUG)
			System.out.println("Action event:" + ev);

	}

	/**
	 *  
	 */
	public String getTitle() {
		return rb.getString("configuration");

	}

}
