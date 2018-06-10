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

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.PluginChain;
import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.VectorBufferAudioSource;
import ipsk.audio.arr.Selection;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.AudioClipListener;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.audio.arr.clip.ui.AudioClipUIContainer;
import ipsk.audio.arr.clip.ui.AudioSignalUI;
import ipsk.audio.arr.clip.ui.AudioTimeScaleUI;
import ipsk.audio.arr.clip.ui.FourierUI;
import ipsk.audio.dsp.AudioClipDSPInfo;
import ipsk.audio.dsp.AudioClipProcessor;
import ipsk.audio.dsp.ui.AudioClipDSPInfoViewer;
import ipsk.audio.mixer.MixerManager;
import ipsk.audio.player.Player;
import ipsk.audio.player.PlayerException;
import ipsk.audio.player.PlayerListener;
import ipsk.audio.player.event.PlayerCloseEvent;
import ipsk.audio.player.event.PlayerEvent;
import ipsk.audio.player.event.PlayerStartEvent;
import ipsk.audio.player.event.PlayerStopEvent;
import ipsk.audio.plugins.ChannelSelectorPlugin;
import ipsk.awt.ProgressListener;
import ipsk.awt.WorkerException;
import ipsk.awt.event.ProgressErrorEvent;
import ipsk.awt.event.ProgressEvent;
import ipsk.io.VectorBuffer;
import ipsk.io.VectorBufferedOutputStream;
import ipsk.net.URLContentLoader;
import ipsk.swing.JProgressDialogPanel;
import ipsk.util.EventQueque;
import ipsk.util.ProgressStatus;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Audio player applet.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class AudioPlayerApplet extends JApplet implements ActionListener,
		PlayerListener, AudioClipListener, ProgressListener {

	public final static boolean DEBUG = false;

	public final static String VERSION = AudioPlayerApplet.class.getPackage()
			.getImplementationVersion();

	private static final Float PREFERRED_LINE_BUFFER_SIZE_MILLIS = (float) 1000;
	public enum Status {EXISTING,INITIALIZED,LOADING,PROCESS,READY,APPLET_STOPPED,APPLET_DESTROYED}
	protected URL audioURL;

	private VectorBufferedOutputStream vbOut;

	protected AudioSource audioSource;
	
	protected AudioSource playbackSource;

	protected AudioClipUIContainer uiContainer;

	protected AudioClip audioSample;

	private JPanel playerPanel;

	private JButton playButton;

	private JButton stopButton;
	
	private JRadioButton[] channelSelectButtons=null; 
	private JRadioButton channelSelectAllButton;
	private ButtonGroup channelSelectButtonGroup;

	private Player player;

	private Timer updateTimer;

	private Mixer device;

	private Container contentPane;

	private AudioSignalUI signalUI = null;
	private FourierUI fourierUI=null;

	private boolean open = false;

	private URLContentLoader urlContentLoader;

	private JProgressDialogPanel progressPanel;

	private JScrollPane scrollPane;
	
	private Status status=null;

	private int channels;
	
	

	/**
	 * Constructor.
	 * 
	 * @throws java.awt.HeadlessException
	 */
	public AudioPlayerApplet() throws HeadlessException {
		super();
//		ThreadSafeAudioSystem.setEnabled(false);
		status=Status.EXISTING;
		if(DEBUG)System.out.println("Constructor: thread: "+Thread.currentThread().getName());
	}

	
	public void init(){
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
	}
	
	public void start(){
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
		if(Status.EXISTING.equals(status)){
		String audioURLStr = getParameter("url");
		try {
			audioURL = new URL(audioURLStr);
			// System.out.println("Audio URL:"+audioURL);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			showStatus("Malformed URL: '" + audioURL + "'");
			return;
		}
		vbOut=new VectorBufferedOutputStream();
		urlContentLoader=new URLContentLoader(audioURL,vbOut,"URL content loader");
	
		
		
		contentPane = getContentPane();

		contentPane.setLayout(new BorderLayout());
		// JScrollPane infoScrollPane=new JScrollPane(infoView);

		
		signalUI = new AudioSignalUI();
		fourierUI=new FourierUI();
		fourierUI.setUseThread(true);
		AudioTimeScaleUI timeScale = new AudioTimeScaleUI();
		uiContainer = new AudioClipUIContainer();
		uiContainer.add(signalUI);
		uiContainer.add(fourierUI);
		uiContainer.add(timeScale);
		scrollPane = new JScrollPane(uiContainer);
		audioSample = new AudioClip();
		audioSample.addAudioSampleListener(this);
		uiContainer.setAudioClip(audioSample);
		
		//contentPane.add(scrollPane, BorderLayout.CENTER);
		progressPanel=new JProgressDialogPanel(urlContentLoader,"title","Loading audio ...");
		add(progressPanel,BorderLayout.CENTER);
		
		playerPanel = new JPanel();
		playButton = new JButton("Play");
		stopButton = new JButton("Stop");
		playerPanel.add(playButton);
		playerPanel.add(stopButton);
		playButton.addActionListener(this);
		stopButton.addActionListener(this);
		contentPane.add(playerPanel, BorderLayout.SOUTH);

		channelSelectAllButton=new JRadioButton("All");
		
		
		// try to get a direct device first
		MixerManager mm;
		try {
			mm = new MixerManager();
			
			Mixer[] devices = mm.getDirectPlaybackMixers();
			// Mixer[] devices=mm.getPlaybackMixers();
			if (devices != null && devices.length > 0) {
				device = devices[0];
				player = new Player(device);
			} else {
				// default device
				player = new Player();
			}
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			showStatus("Could not get a direct audio device !");
		}
		// else default device
		if (player == null) {
			player = new Player();
		}

		if (player != null) {
			player
					.setPreferredLineBufferSizeMillis(PREFERRED_LINE_BUFFER_SIZE_MILLIS);
			// no level meter for now so player does not need to calculate
			// levels
			player.setMeasureLevel(false);
			player.addPlayerListener(this);

		}
		playButton.setEnabled(false);
		stopButton.setEnabled(false);
		// update of play cursor in signal display
		updateTimer = new Timer(200, this);
		open = true;
		showStatus("Audio player initialized.");
		status=Status.INITIALIZED;
		if (DEBUG)
			System.out.println(getClass().getName() + " version " + VERSION+ " initialized.");
		}
	}

	public void _start() {
		if(status.equals(Status.INITIALIZED)){
			
			urlContentLoader.addProgressListener(this);
			try {
				urlContentLoader.open();
			} catch (WorkerException e) {
				String errMsg="Error loading audio from '" + audioURL + "' : "+e.getLocalizedMessage();
				showStatus(errMsg);
				JOptionPane.showMessageDialog(this, errMsg, "Audio applet loading error",JOptionPane.ERROR_MESSAGE);
				return;
			}
			status=Status.LOADING;
			showStatus("Downloading audio data from '" + audioURL + "' ...");
			urlContentLoader.start();
		}else if(Status.APPLET_STOPPED.equals(status)){
			enableAudio();
			makeReady();
		}
	}
	
	
	public void makeReady(){
		
		contentPane.invalidate();
		contentPane.validate();
		repaint();
		status=Status.READY;
	}
		
	
	private void process(){
		status=Status.PROCESS;
		VectorBuffer vb = vbOut.getVectorBuffer();
		// System.out.println("Loaded "+ vb.getLength());
		VectorBufferAudioSource vbAudioSource = new VectorBufferAudioSource(vb);
		audioSource = vbAudioSource;
		playbackSource=audioSource;
		AudioClip audioClip = new AudioClip(audioSource);
		AudioClipProcessor processor = new AudioClipProcessor(audioClip);
		processor.setCalculateSBNR(true);
		showStatus("Processing audio data ...");
		try {
			processor.process();
		} catch (AudioSourceException e) {
			e.printStackTrace();
			showStatus("Error processing audio data.");
			return;
		}
		AudioClipDSPInfo dspInfo=audioClip.getClipDSPInfo();
		AudioClipDSPInfoViewer infoView = new AudioClipDSPInfoViewer(dspInfo);
		contentPane.add(infoView, BorderLayout.NORTH);
		
		contentPane.add(scrollPane, BorderLayout.CENTER);
		channels = dspInfo.getAudioFormat().getChannels();
		if(channelSelectButtons!=null){
			for(JRadioButton chSelButt:channelSelectButtons){
				chSelButt.removeActionListener(this);
				playerPanel.remove(chSelButt);
			}
			channelSelectAllButton.removeActionListener(this);
			playerPanel.remove(channelSelectAllButton);
			
		}
		if(channels>1){
			channelSelectButtons=new JRadioButton[channels+1];
			channelSelectButtonGroup= new ButtonGroup();
			for(int ch=0;ch<channels;ch++){
				JRadioButton chSelButt=new JRadioButton("Ch "+ch);
				//channelSelectButtons[ch].setSelected(true);
				channelSelectButtonGroup.add(chSelButt);
				playerPanel.add(chSelButt);
				chSelButt.addActionListener(this);
				channelSelectButtons[ch]=chSelButt;
			}
			channelSelectAllButton.setSelected(true);
			channelSelectAllButton.addActionListener(this);
			channelSelectButtonGroup.add(channelSelectAllButton);
			channelSelectButtons[channels]=channelSelectAllButton;
			playerPanel.add(channelSelectAllButton);
			
		}
		//uiContainer.revalidate();
		contentPane.validate();
		}
	
	
	private void enableAudio(){	
		if(audioSample!=null){
			
		audioSample.setAudioSource(audioSource);

		// the signal should initially fit to panel
		uiContainer.xZoomFitToPanel();
		if (player != null) {
			try {
				player.setAudioSource(playbackSource);
				playButton.setEnabled(true);
				updateTimer.start();
				showStatus("Audio player ready.");
			} catch (PlayerException e) {
				e.printStackTrace();
				showStatus("Could not set playback audio source !");
			}

		} else {
			showStatus("No audio player available.");
		}
		}else{
			showStatus("No audio clip available.");
		}
		
	}

	public void _stop() {
		if(Status.LOADING.equals(status)){
			urlContentLoader.cancel();
			try {
				urlContentLoader.close();
				urlContentLoader.reset();
			} catch (WorkerException e) {
				e.printStackTrace();
			}
			status=Status.INITIALIZED;
		}else{
			if(audioSample !=null){
				updateTimer.stop();
				playButton.setEnabled(false);
				audioSample.setAudioSource(null);
			}
			if (player != null) {
				try {
					player.close();
					showStatus("Audio player closed.");
				} catch (PlayerException e) {
					e.printStackTrace();
					showStatus("Could not close audio player !");
				}

			}
			status=Status.APPLET_STOPPED;
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		Object src = arg0.getSource();
		if (src == updateTimer) {
			audioSample.setFramePosition(player.getFramePosition());
		} else if (src == playButton) {
			playSelection();

		} else if (src == stopButton) {
			player.stop();
		}else{
			if(channelSelectButtons!=null){
				AudioSource newPlaybackSource=null;

				if(src==channelSelectAllButton){
					newPlaybackSource=audioSource;
				}else{
					for(int c=0;c<channelSelectButtons.length;c++){
						if(src==channelSelectButtons[c]){
							PluginChain pCh = new PluginChain(audioSource);
							try {
								pCh.add(new ChannelSelectorPlugin(c));
							} catch (AudioFormatNotSupportedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							newPlaybackSource=pCh;
						}
					}
				}
				if(newPlaybackSource!=null){
					playbackSource=newPlaybackSource;
					try {
						player.close();
						player.setAudioSource(playbackSource);
						//enableAudio();
					} catch (PlayerException e) {
						e.printStackTrace();
						showStatus("Could not close audio player !");
					}
				}


			}

		}

	}

	private void playSelection() {
		if (player == null || player.isOpen())
			return;
		Selection s = audioSample.getSelection();
		if (s != null) {
			player.setStartFramePosition(s.getLeft());
			player.setStopFramePosition(s.getRight());
		} else {
			player.setStartFramePosition(0);
			player.setStopFramePosition(ThreadSafeAudioSystem.NOT_SPECIFIED);
		}
		try {
			player.open();
			showStatus("Audio player open.");
			player.play();
			showStatus("Audio player playing...");
			stopButton.setEnabled(true);
		} catch (PlayerException e) {
			e.printStackTrace();
			showStatus("Cannot play audio !");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.player.PlayerListener#update(ipsk.audio.player.PlayerEvent)
	 */
	public void update(PlayerEvent playerEvent) {
		if (playerEvent instanceof PlayerStartEvent) {
			if(open){
				stopButton.setEnabled(true);
				playButton.setEnabled(false);
			}
		}else if (playerEvent instanceof PlayerStopEvent) {
			if (open) {
				stopButton.setEnabled(false);
				playButton.setEnabled(false);
			} else {
//				System.out.println("Ignored");
			}
			try {
				player.close();
			} catch (PlayerException e) {
				e.printStackTrace();
				showStatus("Could not close audio player !");
			}
		} else if (playerEvent instanceof PlayerCloseEvent) {
			if (open) {
				playButton.setEnabled(true);
				stopButton.setEnabled(false);
			}
			showStatus("Audio player closed.");
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
			if (selEv.getSelection() != null)
				playSelection();
		}

	}

	public void _destroy() {
		open = false;
		if(updateTimer !=null)updateTimer.stop();
//		if(signalUI!=null)signalUI.close();
		if(uiContainer!=null)uiContainer.close();
		
		if(audioSample!=null)audioSample.removeAudioSampleListener(this);
		contentPane.removeAll();
		status=Status.APPLET_DESTROYED;

	}

	
	/* (non-Javadoc)
	 * @see ipsk.awt.ProgressListener#update(ipsk.awt.event.ProgressEvent)
	 */
	public void update(ProgressEvent progressEvent) {
		ProgressStatus status=progressEvent.getProgressStatus();
		if(progressEvent instanceof ProgressErrorEvent){
			contentPane.remove(progressPanel);
			String errMsg="Unknown error!";
			if(status!=null){
				errMsg=status.getMessage().localize();	
			}
			//contentPane.add(errField,BorderLayout.CENTER);
			showStatus(errMsg);
			JOptionPane.showMessageDialog(this, errMsg, "Audio applet loading error",JOptionPane.ERROR_MESSAGE);
			//contentPane.validate();
			//repaint();
		}else{
			if(status!=null){
				if(status.isDone()){
					contentPane.remove(progressPanel);
					process();
					enableAudio();
					makeReady();
				}
			}
		}
	}

}
