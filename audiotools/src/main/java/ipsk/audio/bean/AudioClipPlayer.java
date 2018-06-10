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

package ipsk.audio.bean;

import ipsk.audio.FileAudioSource;
import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.actions.PauseAction;
import ipsk.audio.actions.StartPlaybackAction;
import ipsk.audio.actions.StopAction;
import ipsk.audio.arr.Selection;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.AudioClipListener;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.audio.events.StartPlaybackActionEvent;
import ipsk.audio.player.Player;
import ipsk.audio.player.PlayerException;
import ipsk.audio.player.PlayerListener;
import ipsk.audio.player.event.PlayerCloseEvent;
import ipsk.audio.player.event.PlayerEvent;
import ipsk.audio.player.event.PlayerPauseEvent;
import ipsk.audio.player.event.PlayerStartEvent;
import ipsk.audio.player.event.PlayerStopEvent;
import ipsk.awt.PropertyChangeAWTEventTransferAgent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 * Audio clip player 
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class AudioClipPlayer extends Player implements ActionListener,
		PlayerListener, AudioClipListener {

	public final static boolean DEBUG = false;

	public final static String VERSION = AudioClipPlayer.class.getPackage()
			.getImplementationVersion();

	private static final Float PREFERRED_LINE_BUFFER_SIZE_MILLIS = (float) 1000;
//	public enum Status {INITIALIZED,LOADING,PROCESS,READY,DEACTIVATED,CLOSING,CLOSED}
	
	//protected UpdateAWTEventTransferAgent evTa=new UpdateAWTEventTransferAgent<AudioPlayerBeanListener,AudioPlayerBeanEvent>();
	
	protected PropertyChangeAWTEventTransferAgent pChTa=new PropertyChangeAWTEventTransferAgent();
	
//	protected AudioSource audioSource;

	protected AudioClip audioClip;


//	private Player player;
//	protected AudioSource playbackSource;

	private Timer updateTimer;

	private Mixer device;


	private boolean autoPlayOnLoad=false;
	
	private Selection selection;
	
	private boolean startPlayOnSelect;
	
	

	private int channels;

	
	
//	private Status status=null;
	
	private String message;

	private StartPlaybackAction startAction;

	private StopAction stopAction;

	private PauseAction pauseAction;
	
	
	public AudioClipPlayer(AudioClip audioClip)  {
	    this(audioClip,null);
	}
	
	/**
	 * Constructor.
	 * 
	 */
	public AudioClipPlayer(AudioClip audioClip,Mixer device)  {
		super(device);
		this.audioClip=audioClip;
		setPreferredLineBufferSizeMillis(PREFERRED_LINE_BUFFER_SIZE_MILLIS);
			// no level meter for now so player does not need to calculate
			// levels
		setMeasureLevel(false);
		addPlayerListener(this);

		startAction = new StartPlaybackAction();
		stopAction = new StopAction();
		pauseAction = new PauseAction();
		startAction.setEnabled(false);
		stopAction.setEnabled(false);
		pauseAction.setEnabled(false);
		startAction.addActionListener(this);
		stopAction.addActionListener(this);
		pauseAction.addActionListener(this);
	
		
		// update of play cursor in signal display
		updateTimer = new Timer(200, this);
		updateTimer.addActionListener(this);
		updateTimer.start();
	//	open = true;
		setMessage("Audio player initialized.");
		//status=Status.EXISTING;
		audioClip.addAudioSampleListener(this);
		if (DEBUG)
			System.out.println(getClass().getName() + " version " + VERSION
					+ " initialized.");
		
	}
	
	
	public void setAudioClip(AudioClip audioClip) throws PlayerException{
	    if(audioClip!=null && audioClip.equals(this.audioClip)){
	        return;
	    }
	    
	    close();
	    this.audioClip=audioClip;
	    if(audioClip!=null){
	   	    setAudioSource(audioClip.getAudioSource());
	    } else{
	        setAudioSource(null);
	    }
	}
	
	
	
//	public void setAudioSource(AudioSource audioSource) throws PlayerException {
//	    
//		AudioSource oldAudioSource=audioSource;
//		if(audioSource!=null && audioSource.equals(this.audioSource)){
//			return;
//		}
//		
//		if(audioClip!=null)audioClip.setAudioSource(null);
//		this.audioSource=audioSource;
//		closeAudio();
//		selection=null;
//
//		startAction.setEnabled(false);
//
//		stopAction.setEnabled(false);
////		super.setAudioSource(audioSource);
//		enableAudio();
//		pChTa.fireEvent(new PropertyChangeEvent(this,"audioSource",oldAudioSource,audioSource));
//	}
//	

//	private void enableAudio(){	
//		if(audioClip!=null){
//			
//		//audioSample.setAudioSource(audioSource);
//
//		// the signal should initially fit to panel
//		//uiContainer.setXzoomFitToPanel();
//		
//			try {
//				setAudioSource(playbackSource);
////				playButton.setEnabled(true);
//				startAction.setEnabled(true);
//				stopAction.setHighlighted(true);
//				updateTimer.start();
//				//showStatus("Audio player ready.");
//			} catch (PlayerException e) {
//				e.printStackTrace();
//				setMessage("Could not set playback audio source !");
//			}
//
//		
//		}else{
//			setMessage("No audio clip available.");
//		}
//		
//	}
//	
//	public void makeReady(){
//	
//		status=Status.READY;
//		setMessage("Audio player ready.");
//	}
//
//
//	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		Object src = arg0.getSource();
		String actionCommand=arg0.getActionCommand();
		if (src == updateTimer) {
		    long framePosition=getFramePosition();
		    if(DEBUG)System.out.println("Frame position: "+framePosition);
		    if(framePosition!=ThreadSafeAudioSystem.NOT_SPECIFIED){
			audioClip.setFramePosition(framePosition);
		    }
		} else if (actionCommand==StartPlaybackAction.ACTION_COMMAND) {
		    if(arg0 instanceof StartPlaybackActionEvent){
                StartPlaybackActionEvent spae=(StartPlaybackActionEvent)arg0;
                setStartFramePosition(spae.getStartFramePosition());
                setStopFramePosition(spae.getStopFramePosition());

                if (!isOpen()){

                    try {
                        open();
                        setMessage("Audio player open.");
                        play();

                    } catch (PlayerException e) {
                        e.printStackTrace();
                        setMessage("Cannot play audio !");
                    }
                }
            }else{
                playSelection();
            }
		} else if (actionCommand==StopAction.ACTION_COMMAND) {
			stop();
		}else if (actionCommand==PauseAction.ACTION_COMMAND) {
			pause();
		}
//		else{
//			if(channelSelectButtons!=null){
//				AudioSource newPlaybackSource=null;
//
//				if(src==channelSelectAllButton){
//					newPlaybackSource=audioSource;
//				}else{
//					for(int c=0;c<channelSelectButtons.length;c++){
//						if(src==channelSelectButtons[c]){
//							PluginChain pCh = new PluginChain(audioSource);
//							try {
//								pCh.add(new ChannelSelectorPlugin(c));
//							} catch (AudioFormatNotSupportedException e) {
//								e.printStackTrace();
//								JOptionPane.showMessageDialog(this, "Could not select audio channel\n"+e.getMessage(), "Audio format error", JOptionPane.ERROR_MESSAGE);
//							}
//							newPlaybackSource=pCh;
//						}
//					}
//				}
//				if(newPlaybackSource!=null){
//					playbackSource=newPlaybackSource;
//					try {
//						player.close();
//						player.setAudioSource(playbackSource);
//						
//					} catch (PlayerException e) {
//						e.printStackTrace();
//						JOptionPane.showMessageDialog(this, "Could not close audio player\n"+e.getMessage(), "Player close error", JOptionPane.ERROR_MESSAGE);
//					
//					}
//				}
//
//
//			}
//
//		}

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.player.PlayerListener#update(ipsk.audio.player.PlayerEvent)
	 */
	public void update(PlayerEvent playerEvent) {
	  
		if(playerEvent instanceof PlayerStartEvent){
			setMessage("Audio player playing...");
//			stopButton.setEnabled(true);
			stopAction.setEnabled(true);
			stopAction.setHighlighted(false);
			pauseAction.setEnabled(true);
			pauseAction.setHighlighted(false);
			startAction.setEnabled(false);
			startAction.setHighlighted(true);
		}else if(playerEvent instanceof PlayerPauseEvent){
			setMessage("Audio player paused.");
//			stopButton.setEnabled(true);
			stopAction.setEnabled(true);
			stopAction.setHighlighted(false);
			pauseAction.setEnabled(true);
			pauseAction.setHighlighted(true);
			startAction.setEnabled(false);
			startAction.setHighlighted(true);
		}else if (playerEvent instanceof PlayerStopEvent) {

		    //				stopButton.setEnabled(false);
		    stopAction.setEnabled(false);
		    stopAction.setHighlighted(true);
		    pauseAction.setEnabled(false);
		    pauseAction.setHighlighted(false);
		    //playButton.setEnabled(false);
		    startAction.setEnabled(false);
		    startAction.setHighlighted(false);
		    setMessage("Audio player stopped.");

		    try {
		        super.close();
		    } catch (PlayerException e) {
		        e.printStackTrace();
		        setMessage("Could not close audio player !");
		    }
		} else if (playerEvent instanceof PlayerCloseEvent) {
		    //				playButton.setEnabled(true);
		    stopAction.setEnabled(false);
            stopAction.setHighlighted(true);
		    startAction.setEnabled(true);
		    startAction.setHighlighted(false);

		    setMessage("Audio player closed.");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.arr.clip.AudioClipListener#audioSampleChanged(ipsk.audio.arr.clip.events.AudioClipChangedEvent)
	 */
	public void audioClipChanged(AudioClipChangedEvent event) {
		if (event instanceof SelectionChangedEvent) {
			SelectionChangedEvent selEv = (SelectionChangedEvent) event;
			if (selEv.getSelection() != null && startPlayOnSelect)
				playSelection();
		}else if(event instanceof AudioSourceChangedEvent){
		    try {
		        if(audioClip!=null){
		            setAudioSource(audioClip.getAudioSource());
		        } else{
		            setAudioSource(null);
		        }
            } catch (PlayerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}

	}
	
	
	

	public void close() throws PlayerException {
		super.close();
		if(updateTimer !=null)updateTimer.stop();
		
		if(audioClip!=null)audioClip.removeAudioSampleListener(this);
	}
	
	

//	public Dimension getPreferredSize(){
//		return new Dimension(600,400);
//	}

	/**
	 * @param selection the selection to set
	 */
	public void setSelection(Selection selection) {
		this.selection = selection;
		audioClip.setSelection(selection);
	}

	/**
	 * @return the selection
	 */
	public Selection getSelection() {
		return selection;
	}

	
	private void playSelection() {
		if (isOpen())
			return;
		if(audioClip!=null){
		Selection s = audioClip.getSelection();
		if (s != null) {
			setStartFramePosition(s.getLeft());
			setStopFramePosition(s.getRight());
		} else {
			setStartFramePosition(0);
			setStopFramePosition(AudioSystem.NOT_SPECIFIED);
		}
		try {
			open();
			setMessage("Audio player open.");
			play();
			
		} catch (PlayerException e) {
			e.printStackTrace();
			setMessage("Cannot play audio !");
		}
		}
	}

	
	public boolean isStartPlayOnSelect() {
		return startPlayOnSelect;
	}

	public void setStartPlayOnSelect(boolean startPlayOnSelect) {
		this.startPlayOnSelect = startPlayOnSelect;
	}



	
	public static void main(String[] args){
		
		if(args.length!=1){
			System.err.println("Usage: AudioPlayerBean audioFile");
			System.exit(-1);
		}
		final File audioFile=new File(args[0]);
		Runnable playRunnable=new Runnable(){
		    public void run(){

		        AudioClip ac=new AudioClip();
		        AudioClipPlayer acp=new AudioClipPlayer(ac);
		        ac.setAudioSource(new FileAudioSource(audioFile));
		        acp.getStartAction().actionPerformed(new ActionEvent(acp,ActionEvent.ACTION_PERFORMED,StartPlaybackAction.ACTION_COMMAND));
		        try {
		            Thread.sleep(5000);
		        } catch (InterruptedException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }
		        acp.getStopAction().actionPerformed(new ActionEvent(acp,ActionEvent.ACTION_PERFORMED,StopAction.ACTION_COMMAND));
		        //		setStartPlayOnSelect(true);
		    }			
		};
		SwingUtilities.invokeLater(playRunnable);
		
	}

	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		String oldMessage=this.message;
		this.message = message;
		if(DEBUG)System.out.println(message);
		pChTa.fireEvent(new PropertyChangeEvent(this,"message",oldMessage,this.message));
	}


	public void addPropertyChangeListener(PropertyChangeListener listener){
		pChTa.addListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener){
		pChTa.removeListener(listener);
	}


	public StartPlaybackAction getStartAction() {
		return startAction;
	}


	public void setStartAction(StartPlaybackAction startAction) {
		if(this.startAction!=startAction){
		this.startAction.removeActionListener(this);
		this.startAction = startAction;
		startAction.addActionListener(this);
		}
	}


	public StopAction getStopAction() {
		return stopAction;
	}


	public void setStopAction(StopAction stopAction) {
		if(this.stopAction != stopAction){
		this.stopAction.removeActionListener(this);
		this.stopAction = stopAction;
		this.stopAction.addActionListener(this);
		}
	}


}
