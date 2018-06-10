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
 * Date  : Tue Jan 27 12:54:16 CET 200
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.ui;

import ipsk.swing.TitledPanel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

/**
 * UI to select a set of URLs.
 *
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

public class URLSetChooser extends TitledPanel implements ActionListener {

	URLSelector[] selectors;

	URL[] selectedURLs = null;

	Vector<ActionListener> listenerList = new Vector<ActionListener>();

	public URLSetChooser(URL[] selectedFiles) {
		this();
		setSelectedURLs(selectedFiles);
	}

	public URLSetChooser() {
		super("URL set chooser");
		selectors = new URLSelector[0];
	}

	public void setSelectedURLs(URL[] files) {
		// remove old selectors
		selectedURLs = files;
		if (files.length != selectors.length) {
			for (int i = 0; i < selectors.length; i++) {
				selectors[i].removeActionListener(this);
				remove(selectors[i]);

			}
			// set new Layout
			setLayout(new GridLayout(1, files.length));
			selectors = new URLSelector[files.length];
			for (int i = 0; i < files.length; i++) {
				selectors[i] = new URLSelector();

				add(selectors[i]);
				selectors[i].addActionListener(this);
			}
		}
		for (int i = 0; i < files.length; i++) {
			selectors[i].setSelectedURL(files[i]);
		}

		revalidate();
		repaint();

	}

	public URL[] getSelectedURLs() {

		return selectedURLs;
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

	protected synchronized void updateListeners() {
		for(ActionListener listener:listenerList){
			listener.actionPerformed(new ActionEvent((Object) this, hashCode(),
					"URL set has changed"));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public synchronized void actionPerformed(ActionEvent e) {
		for (int i = 0; i < selectors.length; i++) {
			selectedURLs[i] = selectors[i].getSelectedURL();
		}
		updateListeners();
	}
}
