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

import ipsk.audio.bean.AudioPlayerBean;
import ipsk.swing.applet.JAppletDispatchThreadWrapper;

import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Audio player applet.
 * Avoid double code in audio player applet and bean: This an applet  wrapper around the bean.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class AudioPlayerBeanApplet extends JAppletDispatchThreadWrapper implements PropertyChangeListener  {

	public final static boolean DEBUG = false;

	public final static String VERSION = AudioPlayerBeanApplet.class.getPackage()
			.getImplementationVersion();

//	private static final Float PREFERRED_LINE_BUFFER_SIZE_MILLIS = (float) 1000;
	public enum Status {EXISTING,INITIALIZED,LOADING,PROCESS,READY,APPLET_STOPPED,APPLET_DESTROYED}
	protected AudioPlayerBean audioPlayer=null;
	
	protected URL audioURL;
//	private Status status=null;
	protected static String[][] pInfo = {
        {"url",    "url",    "Audio URL"},
        {"ui","string", "Comma separated list of UI modules: signal,sonagram,dsp-info"}
      };

	
	/**
	 * Constructor.
	 * 
	 * @throws java.awt.HeadlessException
	 */
	public AudioPlayerBeanApplet() throws HeadlessException {
		super();
//		ThreadSafeAudioSystem.setEnabled(false);
		if(DEBUG)System.out.println("Constructor: thread: "+Thread.currentThread().getName());
	}

	
	public String[][] getParameterInfo(){
		return pInfo;
	}
	

	
	public void initByDT() {
		audioPlayer=new AudioPlayerBean();
		getContentPane().add(audioPlayer);
		
		String uiConfig=getParameter("ui");
		boolean showDSPInfo=false;
		boolean showAudioSignal=false;
		boolean showSona=false;
		boolean showTimeScale=false;
		if(uiConfig!=null){
			
			String[] uiModuls=uiConfig.split(",");
			for(String uiModstr:uiModuls){
				String uiModul=uiModstr.toUpperCase(Locale.ENGLISH).trim();
				if(uiModul.equals("SIGNAL")){
					showAudioSignal=true;
				}else if(uiModul.equals("SONAGRAM")){
					showSona=true;
				}else if(uiModul.equals("TIMESCALE")){
					showTimeScale=true;
				}else if(uiModul.equals("DSPINFO")){
					showDSPInfo=true;
				}
			}
			
		}else{
			// default: audio signal, no sonagram, no DSP info
			showAudioSignal=true;
			
			showTimeScale=true;
			
		}
		audioPlayer.setShowDSPInfo(showDSPInfo);
		audioPlayer.setShowSonagram(showSona);
		audioPlayer.setShowTimeScale(showTimeScale);
		audioPlayer.setVisualizing(true);
		String audioURLStr = getParameter("url");
		try {
			audioURL = new URL(audioURLStr);
			// System.out.println("Audio URL:"+audioURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			showStatus("Malformed URL: '" + audioURL + "'");
			return;
		}
		audioPlayer.addPropertyChangeListener(this);
	}

	public void startByDT() {
		audioPlayer.setURL(audioURL);
		audioPlayer.reactivate();
		getContentPane().validate();
	}

	public void stopByDT() {
		audioPlayer.deactivate();
	}




	public void destroyByDT() {
		getContentPane().removeAll();
		audioPlayer.close();
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

}
