//    Speechrecorder
//    (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Created on Sep 23, 2004
 *
 * Project: JSpeechRecorder
 * Original author: draxler
 */
package ipsk.apps.speechrecorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JWindow;
import javax.swing.Timer;

/**
 * @author draxler
 *
 * SplashScreen displays the splash screen at application startup. It 
 * is displayed either for a given time, or until all dynamically loadable
 * components or plugins have been loaded.
 */
public class SplashScreen extends JWindow{

//	private static final int TINY = 8;
//	private static final int SMALL = 10;
//	private static final int NORMAL = 12;
//	private static final int LARGE = 18;
//	private static final int HUGE = 36;
	
	private static final long serialVersionUID = 1L;
	private JButton closeButton;
	private SplashPanel splashPane;
	private GraphicsConfiguration splashScreenConfiguration;
//	private Logger logger;
	
	private int displayTime = 5000;
	private boolean closeable;
	//private Thread timer;
	private Timer timer;
	
	public SplashScreen (GraphicsConfiguration gc, boolean closeable) {
		super();
		setBackground(Color.WHITE);
//		logger = Logger.getLogger("ipsk.apps.speechrecorder");
		
		splashScreenConfiguration = gc;
		splashPane = new SplashPanel();
		
		closeButton = new JButton("OK");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disposeSplashScreen();
			}
		});
		setCloseable(closeable);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(splashPane,BorderLayout.CENTER);
		setCloseable(closeable);
		pack();
		
		int locX = (int) ((splashScreenConfiguration.getBounds().width - getSize().getWidth()) / 2);
		int locY = (int) ((splashScreenConfiguration.getBounds().height - getSize().getHeight()) / 2);
		setLocation(locX, locY);
		
		//timer = new Thread(this);
		
		// klausj: using Swing timer instead of own thread.
		// Improves thread safety because the Swing timer calls the
		// action listener with the AWT-EventThread
		
		timer=new Timer(displayTime,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disposeSplashScreen();
				
			}
		});
	}

	/**
	 * @return boolean true if user is allowed to close the window
	 */
	private boolean isCloseable() {
		return closeable;
	}

	/**
	 * adds a close button to the splash screen or removes it
	 * 
	 * @param boolean true if user is allowed to close the window
	 */
	private void setCloseable(boolean b) {
		closeable = b;
		if (isCloseable()) {
			getContentPane().add(closeButton,BorderLayout.SOUTH);
		} else {
			getContentPane().remove(closeButton);
		}
		repaint();
	}

	public void showScreen() {
		this.setVisible(true);
		this.toFront();
		timer.start();
	}

//	public void run() {
//		this.setVisible(true);
//		this.toFront();
//		try {
//			Thread.sleep(displayTime);
//		} catch (InterruptedException e) {
//		}
//		disposeSplashScreen();
//	}
	
	public void disposeSplashScreen() {
		
		this.setVisible(false);
		this.dispose();
		timer.stop();
	}
}
