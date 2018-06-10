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
 * Date  : Jun 11, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.ui;

import ipsk.net.Utils;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * UI to select one URL.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

public class URLSelector extends JPanel implements ActionListener {

	private final static boolean DEBUG = false;

	private JButton browse;

	private JTextField URLNameField;

	private URL selectedURL = null;

	private JFileChooser chooser;

	private Vector<ActionListener> actionListenerList = new Vector<ActionListener>();

	private JPanel choosePanel;

	private JLabel selLabel;

	/**
	 * Creates new URLSelector.
	 * 
	 * @param selectedURL
	 *            initial selected URL
	 */
	public URLSelector(URL selectedURL) {
		this();
		setSelectedURL(selectedURL);
	}

	/**
	 * Creates new URLSelector.
	 */
	public URLSelector() {
		super(new GridLayout(2, 1));
		choosePanel = new JPanel(new BorderLayout());
		browse = new JButton("Browse...");
		URLNameField = new JTextField();
		choosePanel.add(URLNameField, BorderLayout.CENTER);
		choosePanel.add(browse, BorderLayout.EAST);
		chooser = new JFileChooser();
		selLabel = new JLabel();
		browse.addActionListener(this);
		URLNameField.addActionListener(this);
		add(choosePanel);
		add(selLabel);

		revalidate();
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ev) {

		if (DEBUG)
			System.out.println("URL input.");
		if (ev.getSource() == URLNameField) {
			try {
				setSelectedURL(new URL(URLNameField.getText()));
			} catch (MalformedURLException e) {
				JOptionPane.showMessageDialog(this, "URL not valid !"
						+ e.getLocalizedMessage(), "Invalid URL",
						JOptionPane.ERROR_MESSAGE);

			}
			updateListeners();
		} else if (ev.getSource() == browse) {
			int returnVal = chooser.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				try {
//					setSelectedURL(chooser.getSelectedFile().toURL());
					File selFile=chooser.getSelectedFile();
					URL selFileURL=Utils.createAsciiURLFromFile(selFile);
					setSelectedURL(selFileURL);
				} catch (MalformedURLException e) {
					JOptionPane.showMessageDialog(null, "URL not valid !"
							+ e.getMessage(), "Invalid URL",
							JOptionPane.ERROR_MESSAGE);
				} catch (URISyntaxException e) {
					JOptionPane.showMessageDialog(null, "URI syntax not valid !"
							+ e.getMessage(), "Invalid URI",
							JOptionPane.ERROR_MESSAGE);
				}
				updateListeners();
			}
		}
	}

	/**
	 * Returns selected URL.
	 * 
	 * @return selected URL
	 */
	public URL getSelectedURL() {
		return selectedURL;
	}

	/**
	 * Sets selected URL. Updates all UI child components.
	 * 
	 * @param url
	 *            new selected url
	 */
	public void setSelectedURL(URL url) {
		selectedURL = url;
		//String absPath = selectedURL.getAbsolutePath();
		String urlStr = selectedURL.toExternalForm();
		selLabel.setText(urlStr);
		URLNameField.setText(urlStr);
		//chooser.setSelectedFile(selectedURL);
		if (DEBUG)
			System.out.println(urlStr);
	}

	public synchronized void addActionListener(ActionListener acl) {
		if (acl != null && !actionListenerList.contains(acl)) {
			actionListenerList.addElement(acl);
		}
	}

	public synchronized void removeActionListener(ActionListener acl) {
		if (acl != null) {
			actionListenerList.removeElement(acl);
		}
	}

	protected synchronized void updateListeners() {

		Iterator<ActionListener> it = actionListenerList.iterator();
		while (it.hasNext()) {
			ActionListener listener =  it.next();
			listener.actionPerformed(new ActionEvent((Object) this, hashCode(),
					"File has changed"));
		}

	}
}
