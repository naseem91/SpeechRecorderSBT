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
 * Date  : May 9, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.mixer;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.EnumControl;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * Class to manage audio devices.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class MixerManager {

	public final static boolean DEBUG = false;

	public static Port.Info[] KNOWN_PORTS=new Port.Info[]{Port.Info.SPEAKER,Port.Info.HEADPHONE,Port.Info.MICROPHONE,Port.Info.COMPACT_DISC,Port.Info.LINE_IN,Port.Info.LINE_OUT}; 
	private Mixer.Info[] mi;

	private Mixer selectedPlaybackMixer;

	private Mixer selectedCaptureMixer;

	private Vector<MixerManagerListener> listenerList;

	private DataLine.Info sourceDataLineInfo = new DataLine.Info(
			SourceDataLine.class, null);

	private DataLine.Info targetDataLineInfo = new DataLine.Info(
			TargetDataLine.class, null);

	/**
	 * Create new device manager.
	 * 
	 * @throws LineUnavailableException
	 * 
	 */
	public MixerManager(){
		mi = AudioSystem.getMixerInfo();
		listenerList = new Vector<MixerManagerListener>();

	}

	/**
	 * Get available playback devices.
	 * 
	 * @return array of playback devices
	 * @throws LineUnavailableException
	 */
	public synchronized Mixer[] getPlaybackMixers()
			throws LineUnavailableException {
		return getPlaybackMixers(false);
	}

	public Mixer getCaptureMixerByName(String name){
	    Mixer m = null;
		for (int i = 0; i < mi.length; i++) {
			m = AudioSystem.getMixer(mi[i]);
			Line.Info[] tlInfos = m.getTargetLineInfo(targetDataLineInfo);
			if (tlInfos != null && tlInfos.length > 0 && mi[i].getName().equals(name)) {
				return m;
			}
		}
		return null;
	}
	public Mixer getCaptureMixerByName(String name,boolean open) throws LineUnavailableException{
	    Mixer m = null;
		for (int i = 0; i < mi.length; i++) {
			m = AudioSystem.getMixer(mi[i]);
			boolean wasOpen = m.isOpen();
			if (!wasOpen && open)
				m.open();
			Line.Info[] tlInfos = m.getTargetLineInfo(targetDataLineInfo);
			if (!wasOpen && open)
				m.close();
			if (tlInfos != null && tlInfos.length > 0 && mi[i].getName().equals(name)) {
				return m;
			}
		}
		return null;
	}
	public Mixer getPlaybackMixerByName(String name){
	    Mixer m = null;
		for (int i = 0; i < mi.length; i++) {
			m = AudioSystem.getMixer(mi[i]);
			Line.Info[] slInfos = m.getSourceLineInfo(sourceDataLineInfo);
			if (slInfos != null && slInfos.length > 0 && mi[i].getName().equals(name)) {
				return(m);
			}
		}
		return null;
	}
	public Mixer getPlaybackMixerByName(String name,boolean open) throws LineUnavailableException{
	    Mixer m = null;
		for (int i = 0; i < mi.length; i++) {
			m = AudioSystem.getMixer(mi[i]);
			boolean wasOpen = m.isOpen();
			if (!wasOpen && open)
				m.open();
			Line.Info[] slInfos = m.getSourceLineInfo(sourceDataLineInfo);
			if (!wasOpen && open)
				m.close();
			if (slInfos != null && slInfos.length > 0 && mi[i].getName().equals(name)) {
				return(m);
			}
		}
		return null;
	}
	
	/**
	 * Get available playback devices
	 * 
	 * @param open
	 *            if true open each mixer to check for additional lines
	 * @return array of playback devices
	 * @throws LineUnavailableException
	 */
	public synchronized Mixer[] getPlaybackMixers(boolean open)
			throws LineUnavailableException {
		Vector<Mixer> playbackMixers = new Vector<Mixer>();
		Mixer m = null;
		for (int i = 0; i < mi.length; i++) {
			m = AudioSystem.getMixer(mi[i]);
			boolean wasOpen = m.isOpen();
			if (!wasOpen && open)
				m.open();
			Line.Info[] slInfos = m.getSourceLineInfo(sourceDataLineInfo);
			if (!wasOpen && open)
				m.close();
			if (slInfos != null && slInfos.length > 0) {
				playbackMixers.add(m);
			}
		}
		return (Mixer[]) playbackMixers.toArray(new Mixer[0]);
	}

	/**
	 * Get playback devices ignoring 'java Sound Audio Engine'. This is more or
	 * less a workaround method: Under Linux JRE 1.4.2 the 'Java Sound Audio
	 * Engine' needs about some seconds to open.
	 * 
	 * @return playback mixers without 'Java Sound Audio Engine'
	 * @throws LineUnavailableException
	 */
	public synchronized Mixer[] getDirectPlaybackMixers()
			throws LineUnavailableException {
		return getDirectPlaybackMixers(false);
	}

	/**
	 * Get playback devices ignoring 'java Sound Audio Engine'. This is more or
	 * less a workaround method: Under Linux JRE 1.4.2 the 'Java Sound Audio
	 * Engine' needs about some seconds to open.
	 * 
	 * @param open
	 *            if true open each mixer to check for additional lines
	 * @return playback mixers without 'Java Sound Audio Engine'
	 * @throws LineUnavailableException
	 */
	public synchronized Mixer[] getDirectPlaybackMixers(boolean open)
			throws LineUnavailableException {
		Vector<Mixer> playbackMixers = new Vector<Mixer>();
		Mixer m = null;
		for (int i = 0; i < mi.length; i++) {
			m = AudioSystem.getMixer(mi[i]);
			if (DEBUG)
				System.out.println("Checking: " + m.getMixerInfo().getName());
			boolean wasOpen = m.isOpen();
			if (!wasOpen && open)
				m.open();
			Line.Info[] slInfos = m.getSourceLineInfo(sourceDataLineInfo);
			if (!wasOpen && open)
				m.close();
			if (slInfos != null
					&& slInfos.length > 0
					&& !(m.getMixerInfo().getName()
							.equals("Java Sound Audio Engine"))) {
				if (DEBUG)
					System.out.println("adding: " + m.getMixerInfo().getName());
				playbackMixers.add(m);
			}
		}
		if (playbackMixers.size() == 0)
			return getPlaybackMixers(open);
		return (Mixer[]) playbackMixers.toArray(new Mixer[0]);
	}

	/**
	 * Get infos of available playback devices.
	 * 
	 * @return array of playback devices
	 * @throws LineUnavailableException
	 */
	public synchronized Mixer.Info[] getPlaybackMixerInfos()
			throws LineUnavailableException {
		return getPlaybackMixerInfos(false);
	}

	/**
	 * Get infos of available playback devices.
	 * 
	 * @param open
	 *            if true open each mixer to check for additional lines
	 * @return array of playback devices
	 * @throws LineUnavailableException
	 */
	public synchronized Mixer.Info[] getPlaybackMixerInfos(boolean open)
			throws LineUnavailableException {
		Vector<Info> playbackMixers = new Vector<Info>();

		for (int i = 0; i < mi.length; i++) {
			Mixer m = AudioSystem.getMixer(mi[i]);

			boolean wasOpen = m.isOpen();
			if (!wasOpen && open)
				m.open();

			Line.Info[] slInfos = m.getSourceLineInfo(sourceDataLineInfo);
			if (!wasOpen && open)
				m.close();
			if (slInfos != null && slInfos.length > 0) {
				playbackMixers.add(mi[i]);
			}
		}

		return (Mixer.Info[]) playbackMixers.toArray(new Mixer.Info[0]);
	}

	/**
	 * Get available capture devices.
	 * 
	 * @return array of capture devices
	 * @throws LineUnavailableException
	 * @throws LineUnavailableException
	 */
	public synchronized Mixer[] getCaptureMixers()
			throws LineUnavailableException {
		return getCaptureMixers(false);
	}

	/**
	 * Get available capture devices.
	 * 
	 * @param open
	 *            f true open each mixer to check for additional lines
	 * @return array of capture devices
	 * @throws LineUnavailableException
	 */
	public synchronized Mixer[] getCaptureMixers(boolean open)
			throws LineUnavailableException {
		Vector<Mixer> captureMixers = new Vector<Mixer>();
		Mixer m = null;
		for (int i = 0; i < mi.length; i++) {
			m = AudioSystem.getMixer(mi[i]);
			boolean wasOpen = m.isOpen();
			if (!wasOpen && open)
				m.open();
			Line.Info[] tlInfos = m.getTargetLineInfo(targetDataLineInfo);
			if (!wasOpen && open)
				m.close();
			if (tlInfos != null && tlInfos.length > 0) {
				captureMixers.add(m);
			}
		}
		return (Mixer[]) captureMixers.toArray(new Mixer[0]);
	}

	/**
	 * Get infos of available capture devices.
	 * 
	 * @return array of capture devices
	 * @throws LineUnavailableException
	 */
	public synchronized Mixer.Info[] getCaptureMixerInfos()
			throws LineUnavailableException {
		return getCaptureMixerInfos(false);
	}

	/**
	 * Get infos of available capture devices.
	 * 
	 * @param open
	 *            if true open each mixer to check for additional lines
	 * @return array of capture devices
	 * @throws LineUnavailableException
	 */
	public synchronized Mixer.Info[] getCaptureMixerInfos(boolean open)
			throws LineUnavailableException {
		Vector<Info> captureMixers = new Vector<Info>();
		for (int i = 0; i < mi.length; i++) {
			Mixer m = AudioSystem.getMixer(mi[i]);
			boolean wasOpen = m.isOpen();
			if (!wasOpen && open)
				m.open();
			Line.Info[] tlInfos = m.getTargetLineInfo(targetDataLineInfo);
			if (!wasOpen && open)
				m.close();
			if (tlInfos != null && tlInfos.length > 0) {
				captureMixers.add(mi[i]);
			}
		}
		return (Mixer.Info[]) captureMixers.toArray(new Mixer.Info[0]);
	}

	/**
	 * Get selected capture device.
	 * 
	 * @return selected capture mixer
	 */
	public Mixer getSelectedCaptureMixer() {
//		if (selectedCaptureMixer == null)
//			try {
//				Mixer[] captureMixers = getCaptureMixers();
//				if (captureMixers == null || captureMixers.length == 0) {
//					selectedCaptureMixer = null;
//				} else {
//					selectedCaptureMixer = captureMixers[0];
//				}
//
//			} catch (LineUnavailableException e) {
//
//			}
		return selectedCaptureMixer;
	}

	/**
	 * Get selected playback device.
	 * 
	 * @return selected playback mixer
	 */
	public Mixer getSelectedPlaybackMixer() {
//		if (selectedPlaybackMixer == null)
//			try {
//				Mixer[] playbackMixers = getPlaybackMixers();
//				if (playbackMixers == null || playbackMixers.length == 0) {
//					selectedPlaybackMixer = null;
//				} else {
//					selectedPlaybackMixer = playbackMixers[0];
//				}
//			} catch (LineUnavailableException e) {
//
//			}
		return selectedPlaybackMixer;
	}

	/**
	 * Set selected capture device.
	 * 
	 * @param mixer
	 *            selected capture mixer
	 */
	public void setSelectedCaptureMixer(Mixer mixer) {
		selectedCaptureMixer = mixer;
		fireSelectedCaptureMixerChanged();
	}

	/**
	 * Set selected playback device.
	 * 
	 * @param mixer
	 *            selected playback mixer
	 */
	public void setSelectedPlaybackMixer(Mixer mixer) {
		selectedPlaybackMixer = mixer;
		fireSelectedPlaybackMixerChanged();
	}
	
	
	private String printMixer(Mixer m){
		return "Mixer: "+m.getMixerInfo().getName()+":\n";
	}
	
	public String printPort(Mixer m,Port.Info pi) throws LineUnavailableException{
		StringBuffer sb=new StringBuffer("Port: "+pi.getName()+"\n");
		Line tgtLine=m.getLine(pi);
		if(tgtLine instanceof Port){
			Port tgtPort=(Port)tgtLine;
			tgtPort.open();
			Control[] pCtrls=tgtPort.getControls();
			
			for(Control pCtrl:pCtrls){
				sb.append(printControl(pCtrl));
			}
			tgtPort.close();
		}
		return sb.toString();
	}
	
	private String printControl(Control c){
		StringBuffer sb=new StringBuffer();
		if(c instanceof CompoundControl){
			CompoundControl cc=(CompoundControl)c;
			sb.append("Compound control: "+cc.getType().toString()+"\n");
			Control[] ccms=cc.getMemberControls();
			for(Control cm:ccms){
				sb.append(printControl(cm));
			}
		}else if(c instanceof FloatControl){
			FloatControl fc=(FloatControl)c;
			sb.append("Float control: "+fc.getType()+" Min: "+fc.getMinimum()+" Max: "+fc.getMaximum()+ " Val: "+fc.getValue()+"\n");
		}else if(c instanceof BooleanControl){
			BooleanControl bc=(BooleanControl)c;
			sb.append("Boolean control: "+bc.getType()+" Val: "+bc.getValue()+" Label:"+bc.getStateLabel(bc.getValue())+"\n");
		}else if(c instanceof EnumControl){
			EnumControl ec=(EnumControl)c;
			sb.append("Enum control: "+ec.getType()+" Val: "+ec.getValue()+"\n");
		}
		return sb.toString();
	}
	
	public String printPortControls() throws LineUnavailableException{
		StringBuffer sb=new StringBuffer();
		Mixer.Info[] mInfos=AudioSystem.getMixerInfo();
		for(Mixer.Info info:mInfos){
			Mixer m =AudioSystem.getMixer(info);
			m.open();
			boolean mixerPrinted=false;
			
			Control[] mcs=m.getControls();
			for(Control mc:mcs){
				if(!mixerPrinted){
					sb.append(printMixer(m));
					mixerPrinted=true;
				}
				sb.append(printControl(mc));
			}
			
			Line.Info[] tgtLineInfos=m.getTargetLineInfo();
			for(Line.Info tgtLineInfo:tgtLineInfos){
				if(tgtLineInfo instanceof Port.Info){
					Port.Info tgtPortInfo=(Port.Info)tgtLineInfo;
					if(!mixerPrinted){
						sb.append(printMixer(m));
						mixerPrinted=true;
					}
					sb.append(printPort(m, tgtPortInfo));
				}
			}
			Line.Info[] srcLineInfos=m.getSourceLineInfo();
			for(Line.Info srcLineInfo:srcLineInfos){
				if(srcLineInfo instanceof Port.Info){
					Port.Info srcPortInfo=(Port.Info)srcLineInfo;
					if(!mixerPrinted){
						sb.append(printMixer(m));
						mixerPrinted=true;
					}
					sb.append(printPort(m, srcPortInfo));
				}
			}
			m.close();
			if(mixerPrinted)sb.append("\n");
		}
		return sb.toString();
		
	}

	public synchronized void addMixerManagerListener(MixerManagerListener acl) {
		if (acl != null && !listenerList.contains(acl)) {
			listenerList.addElement(acl);
		}
	}

	public synchronized void removeMixerManagerListener(MixerManagerListener acl) {
		if (acl != null) {
			listenerList.removeElement(acl);
		}
	}

	protected synchronized void fireSelectedPlaybackMixerChanged() {
		Iterator<MixerManagerListener> it = listenerList.iterator();
		while (it.hasNext()) {
			MixerManagerListener listener = it.next();
			listener.selectedPlaybackMixerChanged(this, selectedPlaybackMixer);
		}
	}

	protected synchronized void fireSelectedCaptureMixerChanged() {
		Iterator<MixerManagerListener> it = listenerList.iterator();
		while (it.hasNext()) {
			MixerManagerListener listener = it.next();
			listener.selectedCaptureMixerChanged(this, selectedCaptureMixer);
		}
	}

	/**
	 * @param info
	 */
	public void setSelectedPlaybackMixer(Mixer.Info info) {
        if (info==null) {
            selectedPlaybackMixer=null;
        }else{
		selectedPlaybackMixer = AudioSystem.getMixer(info);
        }
		fireSelectedPlaybackMixerChanged();

	}

	/**
	 * @param selectedCaptureMixerInfo
	 */
	public void setSelectedCaptureMixer(Mixer.Info selectedCaptureMixerInfo) {
        if (selectedCaptureMixerInfo==null){
            selectedCaptureMixer=null;
        }else{
		selectedCaptureMixer = AudioSystem.getMixer(selectedCaptureMixerInfo);
        }
		fireSelectedCaptureMixerChanged();

	}
	
	
	public static void main(String[] args){
		MixerManager mm;
		try {
			mm = new MixerManager();
			JTextPane tp=new JTextPane();
			JScrollPane sp=new JScrollPane(tp);
			tp.setPreferredSize(new Dimension(300,400));
			 JFrame f=new JFrame();
			 f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        f.getContentPane().add(sp);
		        f.pack();
		        f.setVisible(true);
			tp.setText(mm.printPortControls());
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		
	
	}

}
