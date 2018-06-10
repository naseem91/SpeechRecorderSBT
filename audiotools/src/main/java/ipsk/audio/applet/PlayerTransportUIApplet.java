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
 * Date  : Oct 20, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.applet;

import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.URLAudioSource;
import ipsk.audio.actions.StartPlayAudioSourceAction;
import ipsk.audio.actions.StopAction;
import ipsk.audio.ui.TransportUI;

import java.applet.Applet;
import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

/**
 * Audio player applet.
 * Avoid double code in audio player applet and bean: This an applet  wrapper around the bean.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class PlayerTransportUIApplet extends JApplet implements PropertyChangeListener {

	public final static boolean DEBUG = true;

	public final static String VERSION = PlayerTransportUIApplet.class.getPackage()
			.getImplementationVersion();
	public enum Status {EXISTING,INITIALIZED,READY,APPLET_STOPPED,APPLET_DESTROYED}
	private volatile URL audioURL;
	private TransportUI transportUI=null;
	private StartPlayAudioSourceAction startAction;
	
	private StopAction stopAction;
	//private PauseAction pauseAction;
	private Status status=null;
	
	private boolean bound=false;
	
	private final static String[][] pInfo = {
	         {"url",    "url",    "Audio URL"}
	 };
	
	/**
	 * Constructor.
	 * 
	 * @throws java.awt.HeadlessException
	 */
	public PlayerTransportUIApplet() throws HeadlessException {
		super();
//		ThreadSafeAudioSystem.setEnabled(false);
		status=Status.EXISTING;
		startAction=new StartPlayAudioSourceAction();
		startAction.setEnabled(false);
		stopAction=new StopAction();
//		pauseAction=new PauseAction();
		

		if(DEBUG)System.out.println("PlayerTransportUI "+this.hashCode());
		
	}

	public String[][] getParameterInfo(){
		return pInfo;
	}
	
	public String getAppletInfo(){
		return "Audio control applet, Klaus Jaensch, Copyright 2010"; 
	}
	
	
	
	
	public void init(){
		String audioURLStr = getParameter("url");
		if(audioURLStr!=null){
			try {
				audioURL = new URL(audioURLStr);
//				System.out.println("TransportUI URL: '" + audioURL);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				showStatus("Malformed URL: '" + audioURL + "'");
				return;
			}
			if(java.awt.EventQueue.isDispatchThread()){
				_init();
			}else{
				try {
					SwingUtilities.invokeAndWait(new Runnable(){
						public void run(){
							_init();
						}
					});
				} catch (InterruptedException e) {
					showStatus(e.getMessage());
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					showStatus(e.getMessage());
					e.printStackTrace();
				}
			}
		}else{
//			System.out.println("Ctrl "+this.hashCode()+": could not get url parameter!");
			status=Status.INITIALIZED;
		}
		
		
	}
	
	public void start(){
//		if(Status.EXISTING.equals(status)){
//			init();
//		}
		if(java.awt.EventQueue.isDispatchThread()){
			_start();
		}else{
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					_start();
				}
			});
		} catch (InterruptedException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		}
		}
	}
	
	public void stop(){
		if(java.awt.EventQueue.isDispatchThread()){
			_stop();
		}else{
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					_stop();
				}
			});
		} catch (InterruptedException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		}
		}
	}
	
	public void destroy(){
		if(java.awt.EventQueue.isDispatchThread()){
			_destroy();
		}else{
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					_destroy();
				}
			});
		} catch (InterruptedException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		}
		}
	}
	
	public void _init() {
		URLAudioSource urlAudioSource=new URLAudioSource(audioURL);
		startAction.setAudioSource(urlAudioSource);
		stopAction.setHighlighted(true);
		stopAction.setEnabled(false);
//		transportUI=new TransportUI(startAction,stopAction,pauseAction);
		if(transportUI==null){
			transportUI=new TransportUI(startAction,stopAction);
			getContentPane().add(transportUI);
		}
		status=Status.READY;
//		audioPlayer.addPropertyChangeListener(this);
	}

	public void _start() {
	
//		Applet a=getAppletContext().getApplet("audioplayer");
//		if(a!=null && (a instanceof MultiSourcePlayerApplet)){
//			System.out.println("Player applet found.");
//			
//		}else{
//			System.out.println("Player applet not found !!");
//		}
		getContentPane().validate();
	}

	public void _stop() {
		//audioPlayer.deactivate();
	}




	public void _destroy() {
		getContentPane().removeAll();
		//audioPlayer.close();
	}


	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt!=null){
			String pName=evt.getPropertyName();
			if("message".equals(pName)){
				String msg=(String)evt.getNewValue();
				if(msg!=null){
					showStatus(msg);
				}else{
					showStatus("");
				}
			}
		}
		
	}


	public StartPlayAudioSourceAction getStartAction() {
		return startAction;
	}


	public StopAction getStopAction() {
		return stopAction;
	}


//	public PauseAction getPauseAction() {
//		return pauseAction;
//	}


	public boolean isReady(){
		return (status!=null && status.equals(Status.READY));
	}
	
	public URL getAudioURL() {
		if(audioURL==null && ! Status.READY.equals(status)){
			init();
		}
		return audioURL;
	}


	public boolean isBound() {
		return bound;
	}


	public void setBound(boolean bound) {
		this.bound = bound;
	}


	
	
}
