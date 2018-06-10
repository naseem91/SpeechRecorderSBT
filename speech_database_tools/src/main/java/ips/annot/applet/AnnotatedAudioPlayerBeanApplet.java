//    IPS Speech database tools
// 	  (c) Copyright 2016
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Speech database tools
//
//
//    IPS Speech database tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Speech database tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Speech database tools.  If not, see <http://www.gnu.org/licenses/>.

package ips.annot.applet;

import java.awt.HeadlessException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;

import ips.annot.view.AnnotatedAudioPlayerBean;
import ipsk.swing.applet.JAppletDispatchThreadWrapper;

/**
 * Audio player applet with annotation view plugin.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class AnnotatedAudioPlayerBeanApplet extends JAppletDispatchThreadWrapper implements PropertyChangeListener  {

	public boolean debug = false;

	public final static String VERSION = AnnotatedAudioPlayerBeanApplet.class.getPackage()
			.getImplementationVersion();
	

//	private static final Float PREFERRED_LINE_BUFFER_SIZE_MILLIS = (float) 1000;
	public enum Status {EXISTING,INITIALIZED,LOADING,PROCESS,READY,APPLET_STOPPED,APPLET_DESTROYED}
	protected AnnotatedAudioPlayerBean audioPlayer=null;
	
	protected URL audioURL;
	protected URL annotationURL;
//	private Status status=null;
	protected static String[][] pInfo = {
        {"url",    "url",    "Audio URL (required)"},
        {"textGridAnnotationUrl",    "textGridAnnotationUrl",    "TextGrid Annotation URL (required)"},
        {"textGridAnnotationCharset",    "textGridAnnotationCharset",    "TextGrid Annotation charset (default ISO-8859-1 or set by Content-type header)"},
        {"ui","string", "Optional comma separated list of UI modules: signal,sonagram,timescale"},
        {"debug","boolean","Print debug infos"}
      };

	
	/**
	 * Constructor.
	 * 
	 * @throws java.awt.HeadlessException
	 */
	public AnnotatedAudioPlayerBeanApplet() throws HeadlessException {
		super();
		if(DEBUG)System.out.println("Constructor: thread: "+Thread.currentThread().getName());
		 
	}

	
	public String[][] getParameterInfo(){
		return pInfo;
	}
	

	
	public void initByDT() {
	    String debugParamStr=getParameter("debug");
	    if(debugParamStr!=null){
	        debug=Boolean.parseBoolean(debugParamStr);
	    }
	    if(debug){
	        System.out.println(this.getClass().getName()+" "+VERSION);
	        System.out.println("Init...");
	    }
	    audioPlayer=new AnnotatedAudioPlayerBean();
        audioPlayer.setDebug(debug);
	    String tgAnnoCs=getParameter("textGridAnnotationCharset");
	    if(tgAnnoCs!=null && ! "".equals(tgAnnoCs)){
	        Charset cs=Charset.forName(tgAnnoCs);
	        if(cs!=null){
	            audioPlayer.setTextGridCharset(cs);
	            if(debug){
	                System.out.println("TextGrid charset set: "+cs.name());
	                
	            }
	        }
	       
	    }
		
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
				}
//				}else if(uiModul.equals("DSPINFO")){
//					showDSPInfo=true;
//				}
			}
			
		}else{
			// default: audio signal, no sonagram, no DSP info
			showAudioSignal=true;
			
			showTimeScale=true;
			
		}
//		audioPlayer.setShowDSPInfo(showDSPInfo);
		audioPlayer.setShowSonagram(showSona);
		audioPlayer.setShowTimeScale(showTimeScale);
//		audioPlayer.setVisualizing(true);
		String audioURLStr = getParameter("url");
		try {
			audioURL = new URL(audioURLStr);
		} catch (MalformedURLException e) {
			
			String m=new String("Malformed audio URL: '" + audioURL + "'");
			if(debug){
			    System.err.println(m);
			    e.printStackTrace();
			}
			showStatus(m);
			return;
		}
		String annotationURLStr = getParameter("textGridAnnotationUrl");
		if(annotationURLStr!=null && ! "".equals(annotationURLStr)){
		    try {
		        annotationURL = new URL(annotationURLStr);
		    } catch (MalformedURLException e) {
		        
		        String m=new String("Malformed annotation URL: '" + annotationURL + "'");
		        if(debug){
		            System.err.println(m);
		            e.printStackTrace();
		        }
	            showStatus(m);
		        return;
		    }
		}
		audioPlayer.addPropertyChangeListener(this);
		if(debug)System.out.println("Initialized.");
	}

	public void startByDT() {
	    if(debug)System.out.println("Start...");
		audioPlayer.setURL(audioURL);
		audioPlayer.setAnnotationURL(annotationURL);
		audioPlayer.reactivate();
		getContentPane().validate();
		if(debug)System.out.println("Started.");
	}

	public void stopByDT() {
	    if(debug)System.out.println("Stop...");
		audioPlayer.deactivate();
		if(debug)System.out.println("Stopped.");
	}




	public void destroyByDT() {
	    if(debug)System.out.println("Destroy...");
		getContentPane().removeAll();
		audioPlayer.close();
		if(debug)System.out.println("Destroyed.");
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
