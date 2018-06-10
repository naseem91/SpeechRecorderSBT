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

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.FileAudioSource;
import ipsk.audio.PluginChain;
import ipsk.audio.URLAudioSource;
import ipsk.audio.VectorBufferAudioSource;
import ipsk.audio.actions.LoopAction;
import ipsk.audio.actions.PauseAction;
import ipsk.audio.actions.StartPlaybackAction;
import ipsk.audio.actions.StopAction;
import ipsk.audio.applet.AudioPlayerApplet.Status;
import ipsk.audio.arr.Selection;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.AudioClipListener;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.audio.arr.clip.ui.AudioClipScrollPane;
import ipsk.audio.arr.clip.ui.AudioClipUI;
import ipsk.audio.arr.clip.ui.AudioClipUIContainer;
import ipsk.audio.arr.clip.ui.AudioSignalUI;
import ipsk.audio.arr.clip.ui.AudioTimeScaleUI;
import ipsk.audio.arr.clip.ui.FourierUI;
import ipsk.audio.arr.clip.ui.FragmentActionBarUI;
import ipsk.audio.bean.event.AudioPlayerBeanEvent;
import ipsk.audio.dsp.AudioClipDSPInfo;
import ipsk.audio.dsp.AudioClipProcessor;
import ipsk.audio.dsp.ui.AudioClipDSPInfoViewer;
import ipsk.audio.events.StartPlaybackActionEvent;
import ipsk.audio.mixer.MixerManager;
import ipsk.audio.player.Player;
import ipsk.audio.player.PlayerException;
import ipsk.audio.player.PlayerListener;
import ipsk.audio.player.event.PlayerCloseEvent;
import ipsk.audio.player.event.PlayerEvent;
import ipsk.audio.player.event.PlayerPauseEvent;
import ipsk.audio.player.event.PlayerStartEvent;
import ipsk.audio.player.event.PlayerStopEvent;
import ipsk.audio.plugins.ChannelSelectorPlugin;
import ipsk.audio.ui.TransportUI;
import ipsk.awt.AWTEventTransferAgent;
import ipsk.awt.ProgressListener;
import ipsk.awt.PropertyChangeAWTEventTransferAgent;
import ipsk.awt.UpdateAWTEventTransferAgent;
import ipsk.awt.WorkerException;
import ipsk.awt.ProgressWorker.ProgressEventTransferAgent;
import ipsk.awt.event.ProgressErrorEvent;
import ipsk.awt.event.ProgressEvent;
import ipsk.io.VectorBuffer;
import ipsk.io.VectorBufferedOutputStream;
import ipsk.net.URLContentLoader;
import ipsk.swing.JPopupMenuListener;
import ipsk.swing.JProgressDialogPanel;
import ipsk.swing.action.tree.ActionFolder;
import ipsk.swing.action.tree.ActionGroup;
import ipsk.swing.action.tree.ActionTreeRoot;
import ipsk.swing.action.tree.JMenuBuilder;
import ipsk.util.EventQueque;
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 * Audio player bean
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class AudioPlayerBean extends JPanel implements ActionListener,
		PlayerListener, AudioClipListener, ProgressListener {

	public final static boolean DEBUG = false;
	public final static int DEF_UPDATE_INTERVALL_MS=200;

	public final static String VERSION = AudioPlayerBean.class.getPackage()
			.getImplementationVersion();

	private static final Float PREFERRED_LINE_BUFFER_SIZE_MILLIS = (float) 1000;
	public enum Status {INITIALIZED,LOADING,PROCESS,READY,DEACTIVATED,CLOSING,CLOSED}
	protected URL source;
	
	
	//protected UpdateAWTEventTransferAgent evTa=new UpdateAWTEventTransferAgent<AudioPlayerBeanListener,AudioPlayerBeanEvent>();
	
	protected PropertyChangeAWTEventTransferAgent pChTa=new PropertyChangeAWTEventTransferAgent();
	private VectorBufferedOutputStream vbOut;
	
	private URLContentLoader urlContentLoader;
	
	private JProgressDialogPanel progressPanel;

	protected AudioSource audioSource;

	protected AudioClipUIContainer uiContainer;

	protected AudioClip audioClip;

	private JPanel playerPanel;

	//private JButton playButton;

	//private JButton stopButton;

	private Player player;
	protected AudioSource playbackSource;

	private Timer updateTimer;

	private Mixer device;

	//private Container contentPane;
	
	
	protected AudioSignalUI signalUI = null;

	//private boolean open = false;

	private AudioClipScrollPane scrollPane;

	private boolean autoPlayOnLoad=false;
	protected boolean visualizing;
	private boolean showDSPInfo=false;
	private boolean showSonagram=true;
	private boolean showFragmentActionBar=true;
	private boolean showTimeScale=true;
	
	private Selection selection;
	
	private boolean startPlayOnSelect;
	
	private JComboBox channelSelectBox;
//	private JRadioButton[] channelSelectButtons=null; 
//	private JRadioButton channelSelectAllButton;
//	private ButtonGroup channelSelectButtonGroup;
	
	

	private int channels;

	private FourierUI sonagram;
	private Vector<AudioClipUI> additionalAudioClipUIPluginList=new Vector<AudioClipUI>();
	private FragmentActionBarUI fragmentActionBar;
	private AudioTimeScaleUI timeScale;
	
	private volatile Status status=null;
	
	private String message;

	private StartPlaybackAction startAction;

	private StopAction stopAction;

	private PauseAction pauseAction;
	private LoopAction loopAction;
	private AudioClipProcessor processor;
	private AudioClipDSPInfoViewer infoView;
	
	
	
	/**
     * Constructor.
     * 
     */
    public AudioPlayerBean()  {
        this(null);
    }
	/**
	 * Constructor.
	 * 
	 */
	public AudioPlayerBean(AudioClip audioClip)  {
		super(new BorderLayout());
		this.audioClip=audioClip;
		if(this.audioClip==null){
		    this.audioClip = new AudioClip();
		}
		this.audioClip.addAudioSampleListener(this);
		processor=new AudioClipProcessor(this.audioClip);
		processor.setCalculateSBNR(true);
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
			//showStatus("Could not get a direct audio device !");
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
		
		
		
//		playButton = new JButton("Play");
//		stopButton = new JButton("Stop");
//		playButton.setEnabled(false);
//		stopButton.setEnabled(false);
//		playButton.addActionListener(this);
//		stopButton.addActionListener(this);
		playerPanel = new JPanel();
		startAction = new StartPlaybackAction();
		stopAction = new StopAction();
		pauseAction = new PauseAction();
		loopAction=new LoopAction();
		startAction.setEnabled(false);
		stopAction.setEnabled(false);
		pauseAction.setEnabled(false);
		startAction.addActionListener(this);
		stopAction.addActionListener(this);
		pauseAction.addActionListener(this);
		loopAction.addActionListener(this);
		TransportUI tp=new TransportUI(startAction,stopAction,pauseAction,loopAction);
		playerPanel.add(tp);
		add(playerPanel, BorderLayout.SOUTH);
		
//		channelSelectAllButton=new JRadioButton("All");
//		channelSelectBox=new JComboBox(new Object[]{"All"});
		
		// update of play cursor in signal display
		updateTimer = new Timer(DEF_UPDATE_INTERVALL_MS, this);
	//	open = true;
		setMessage("Audio player initialized.");
		//status=Status.EXISTING;
		if (DEBUG)
			System.out.println(getClass().getName() + " version " + VERSION
					+ " initialized.");
//		status=Status.INITIALIZED;
		changeStatus(Status.INITIALIZED);
	}
	
	public void addAudioClipUI(AudioClipUI audioClipUI){
	    additionalAudioClipUIPluginList.add(audioClipUI);
	    uiContainer.add((Component) audioClipUI);
	}
	
	private void createPopupMenu(){
	   ActionTreeRoot ascActionTree=scrollPane.getActionTreeRoot();
       ActionFolder signalViewFolder=new ActionFolder("signalview",new LocalizableMessage("Signal view"));
       ActionTreeRoot shiftedAscActionTree=ascActionTree.shiftFromTopLevel(signalViewFolder);
       ActionFolder afft=new ActionTreeRoot();
      
//       aff.add(pa);
       
       
       ActionFolder avf=ActionFolder.buildTopLevelFolder(ActionFolder.VIEW_FOLDER_KEY);
//       afft.add(avf);
       ActionGroup subjectViewGroup=new ActionGroup("view.subjectGroup");
       // TODO
       //subjectViewGroup.add(toggleSubjectDisplayAction);
       avf.add(subjectViewGroup);
       afft.add(avf);
       afft.merge(shiftedAscActionTree);
       

       
       // build popup menu for signal view
       JMenuBuilder pmb=new JMenuBuilder(ascActionTree);
       JPopupMenu pm=pmb.buildJPopupMenu();
       JPopupMenuListener pml=new JPopupMenuListener(pm);
       scrollPane.addMouseListener(pml);
       uiContainer.addPopupMouseListener(pml);
	}
	
	public void setSource(String sourceUrlString) throws MalformedURLException{
		URL sourceURL=new URL(sourceUrlString);
		setURL(sourceURL);
	}
	
	public URL getSource() {
		return source;
	}
	public void setURL(URL url){
		setSource(url);
	}
	public void setSource(URL source) {
	
		URL oldUrl=this.source;
		if(this.source!=null && this.source.equals(source)){
			return;
		}
		
		if(audioClip!=null)audioClip.setAudioSource(null);
		this.source=source;
		closeAudio();
		selection=null;
//		playButton.setEnabled(false);
		startAction.setEnabled(false);
//		stopButton.setEnabled(false);
		stopAction.setEnabled(false);
		closeDownload();
		vbOut=null;
		clearScreen();
		load();
		pChTa.fireEvent(new PropertyChangeEvent(this,"source",oldUrl,this.source));
	}
	

	private void load(){
		clearScreen();
		String urlProtocol=source.getProtocol();
		if(urlProtocol.equalsIgnoreCase("file")){
			// do not load, just use the file
			audioSource=new URLAudioSource(source);
			if(visualizing && scrollPane!=null){
				scrollPane.setPreferredSize(new Dimension(200,100));
			}
			try {
				process();
			} catch (AudioSourceException e) {
				JOptionPane.showMessageDialog(this, "Could not process audio file: "+e.getMessage(), "Audio processing error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			enableAudio();
			makeReady();
			if(autoPlayOnLoad){
				playSelection();
			}
		}else{
			// load in own thread
//			status=Status.LOADING;
			changeStatus(Status.LOADING);
			vbOut=new VectorBufferedOutputStream();
			urlContentLoader=new URLContentLoader(source,vbOut,"URL content loader");
			if(visualizing){
				progressPanel=new JProgressDialogPanel(urlContentLoader,"title","Loading audio ...");
				add(progressPanel,BorderLayout.CENTER);
			}

			revalidate();
			repaint();
			urlContentLoader.addProgressListener(this);
			try {
				urlContentLoader.open();
			} catch (WorkerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			urlContentLoader.start();
		}

	}
	
	
	private void clearScreen(){
		if(progressPanel!=null)remove(progressPanel);
		if(scrollPane!=null)remove(scrollPane);
	}
	
	private void process() throws AudioSourceException{
//		status=Status.PROCESS;
		changeStatus(Status.PROCESS);
		clearScreen();
		playbackSource=audioSource;

		//AudioClip audioClip = new AudioClip(audioSource);
		//AudioClipProcessor processor = new AudioClipProcessor(audioClip);
		//processor.setCalculateSBNR(true);
		setMessage("Processing audio data ...");
		//		try {
		//			processor.process();
		//		} catch (AudioSourceException e) {
		//			e.printStackTrace();
		//			//showStatus("Error processing audio data.");
		//			return;
		//		}
		//		AudioClipDSPInfo dspInfo=audioClip.getClipDSPInfo();
		//		AudioClipDSPInfoViewer infoView = new AudioClipDSPInfoViewer(dspInfo);
		//		add(infoView, BorderLayout.NORTH);
		if(infoView!=null){
			remove(infoView);
		}
		if(visualizing){
			
			audioClip.setAudioSource(audioSource);
			audioClip.setSelection(selection);
			add(scrollPane, BorderLayout.CENTER);
			if(showDSPInfo){
				
				setMessage("Processing audio data ...");
				
				try {
					processor.process();
				} catch (AudioSourceException e) {
					e.printStackTrace();
					setMessage("Error processing audio data.");
					return;
				}
				AudioClipDSPInfo dspInfo=audioClip.getClipDSPInfo();
				infoView = new AudioClipDSPInfoViewer(dspInfo);
				add(infoView, BorderLayout.NORTH);
				}
			validate();
		}
		//channels = dspInfo.getAudioFormat().getChannels();
		
		channels=audioSource.getFormat().getChannels();
//		
//		if(channelSelectButtons!=null){
//			for(JRadioButton chSelButt:channelSelectButtons){
//				chSelButt.removeActionListener(this);
//				playerPanel.remove(chSelButt);
//			}
//			channelSelectAllButton.removeActionListener(this);
//			playerPanel.remove(channelSelectAllButton);
//
//		}
		
		if(channelSelectBox!=null){
			channelSelectBox.removeActionListener(this);
			playerPanel.remove(channelSelectBox);
		}
		if(channels>1){
//			channelSelectButtons=new JRadioButton[channels+1];
//			channelSelectButtonGroup= new ButtonGroup();
//			for(int ch=0;ch<channels;ch++){
//				JRadioButton chSelButt=new JRadioButton("Ch "+ch);
//				//channelSelectButtons[ch].setSelected(true);
//				channelSelectButtonGroup.add(chSelButt);
////				playerPanel.add(chSelButt);
//				chSelButt.addActionListener(this);
//				channelSelectButtons[ch]=chSelButt;
//			}
//			channelSelectAllButton.setSelected(true);
//			channelSelectAllButton.addActionListener(this);
//			channelSelectButtonGroup.add(channelSelectAllButton);
//			channelSelectButtons[channels]=channelSelectAllButton;
//			playerPanel.add(channelSelectAllButton);
			
			String[] channelStrs=new String[channels+1];
			channelStrs[0]="All";
			for(int chI=1;chI<=channels;chI++){
				channelStrs[chI]="Ch. "+Integer.toString(chI);
			}
			channelSelectBox=new JComboBox(channelStrs);
			channelSelectBox.setSelectedIndex(0);
			channelSelectBox.addActionListener(this);
			playerPanel.add(channelSelectBox);
			
		}
		playerPanel.validate();
		if(visualizing){
			// the signal should initially fit to panel
			uiContainer.xZoomFitToPanel();
		}
		if(uiContainer!=null){
		    uiContainer.validate();
		}
		validate();
		if(scrollPane!=null){
		    scrollPane.revalidate();
		    scrollPane.repaint();
		}
		
	}
	
	private void enableAudio(){	
		if(audioClip!=null){
			
		//audioSample.setAudioSource(audioSource);

		// the signal should initially fit to panel
		//uiContainer.setXzoomFitToPanel();
		if (player != null) {
			try {
				player.setAudioSource(playbackSource);
//				playButton.setEnabled(true);
				startAction.setEnabled(true);
				stopAction.setHighlighted(true);
				updateTimer.start();
				//showStatus("Audio player ready.");
			} catch (PlayerException e) {
				e.printStackTrace();
				setMessage("Could not set playback audio source !");
			}

		} else {
			setMessage("No audio player available.");
		}
		}else{
			setMessage("No audio clip available.");
		}
		
	}
	
	public void makeReady(){
		revalidate();
		repaint();
//		status=Status.READY;
		changeStatus(Status.READY);
		setMessage("Audio player ready.");
	}


	private void closeAudio() {
		
		updateTimer.stop();
//		playButton.setEnabled(false);
		startAction.setEnabled(false);
		if (player != null) {
			try {
				player.close();
				setMessage("Audio player closed.");
			} catch (PlayerException e) {
				e.printStackTrace();
				setMessage("Could not close audio player !");
			}

		}
		audioSource=null;	
		try {
			player.setAudioSource(null);
		} catch (PlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(visualizing){
		audioClip.setAudioSource(null);
		}
		
	}
	
	
	private void closeDownload(){
		if(urlContentLoader!=null){
			urlContentLoader.removeProgressListener(this);
		urlContentLoader.cancel();
		try {
			urlContentLoader.close();
			urlContentLoader.reset();
		} catch (WorkerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		Object src = arg0.getSource();
		String actionCommand=arg0.getActionCommand();
		if (src == updateTimer) {
			if(visualizing)audioClip.setFramePosition(player.getFramePosition());
		} else if (actionCommand==StartPlaybackAction.ACTION_COMMAND) {
			if(arg0 instanceof StartPlaybackActionEvent){
				StartPlaybackActionEvent spae=(StartPlaybackActionEvent)arg0;
				player.setStartFramePosition(spae.getStartFramePosition());
				player.setStopFramePosition(spae.getStopFramePosition());

				if (player != null && !player.isOpen()){

					try {
						player.open();
						setMessage("Audio player open.");
						player.play();

					} catch (PlayerException e) {
						e.printStackTrace();
						setMessage("Cannot play audio !");
					}
				}
			}else{
				playSelection();
			}
		} else if (actionCommand==StopAction.ACTION_COMMAND) {
			player.stop();
		}else if (actionCommand==PauseAction.ACTION_COMMAND) {
			player.pause();
		}else if (actionCommand==LoopAction.ACTION_COMMAND) {
		    player.setLooping((Boolean)loopAction.getValue(Action.SELECTED_KEY));
        }else if (src == channelSelectBox){
			int si=channelSelectBox.getSelectedIndex();
			AudioSource newPlaybackSource=null;
			if(si==0){
				newPlaybackSource=audioSource;
			}else{

				PluginChain pCh = new PluginChain(audioSource);
				try {
					pCh.add(new ChannelSelectorPlugin(si-1));
				} catch (AudioFormatNotSupportedException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, "Could not select audio channel\n"+e.getMessage(), "Audio format error", JOptionPane.ERROR_MESSAGE);
				}
				newPlaybackSource=pCh;
			}

			if(newPlaybackSource!=null){
				playbackSource=newPlaybackSource;
				try {
					player.close();
					player.setAudioSource(playbackSource);

				} catch (PlayerException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, "Could not close audio player\n"+e.getMessage(), "Player close error", JOptionPane.ERROR_MESSAGE);

				}
			}
		}
//		else{
//
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
			if (Status.READY.equals(status)) {
//				stopButton.setEnabled(false);
				stopAction.setEnabled(false);
				stopAction.setHighlighted(true);
				pauseAction.setEnabled(false);
				pauseAction.setHighlighted(false);
				//playButton.setEnabled(false);
				startAction.setEnabled(false);
				startAction.setHighlighted(false);
				setMessage("Audio player stopped.");
			} else {
//				System.out.println("Ignored");
			}
			setMessage("Audio player stopped.");
			try {
				player.close();
			} catch (PlayerException e) {
				e.printStackTrace();
				setMessage("Could not close audio player !");
			}
		} else if (playerEvent instanceof PlayerCloseEvent) {
			if (Status.READY.equals(status)) {
//				playButton.setEnabled(true);
				stopAction.setEnabled(false);
				stopAction.setHighlighted(true);
				startAction.setEnabled(true);
				startAction.setHighlighted(false);
				
			}else if (Status.CLOSING.equals(status)) {
				// bean is closing
				// do nothing
			}else{
				setMessage("Internal error state ");
//				System.out.println("Close in not ready state! "+status);
			}
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

	        SelectionChangedEvent selEvent = (SelectionChangedEvent) event;
	        Selection s = selEvent.getSelection();
	        

	        //playSelection();
	        if (player == null)
	            return;
	        if(audioClip!=null){

	            if (s != null) {
	                player.setStartFramePosition(s.getLeft());
	                player.setStopFramePosition(s.getRight());
	                if(startPlayOnSelect && !player.isOpen()){
	                    try {
	                        player.open();
	                        setMessage("Audio player open.");
	                        player.play();

	                    } catch (PlayerException e) {
	                        e.printStackTrace();
	                        setMessage("Cannot play audio !");
	                    }
	                }
	            } else {
	                player.setStartFramePosition(0);
	                player.setStopFramePosition(AudioSystem.NOT_SPECIFIED);
	            }
	            
	        }
	        
	        // propagate to bean listeners
	        Selection oldSel=this.selection;
	        this.selection=s;
	        PropertyChangeEvent selChEv=new PropertyChangeEvent(this,"selection", oldSel,this.selection);
	        pChTa.fireEvent(selChEv);
	    }
	}


	private void changeStatus(Status newStatus){
		Status oldStatus=this.status;
		this.status=newStatus;
		PropertyChangeEvent stChEv=new PropertyChangeEvent(this, "status",oldStatus, this.status);
		pChTa.fireEvent(stChEv);
	}
	

	public void close() {
//		status=Status.CLOSING;
		changeStatus(Status.CLOSING);
		closeAudio();
		closeDownload();
		//open = false;
		if(updateTimer !=null)updateTimer.stop();
		if(uiContainer!=null)uiContainer.close();
		if(audioClip!=null)audioClip.removeAudioSampleListener(this);
		clearScreen();
		changeStatus(Status.CLOSED);
	}
	
	

//	public Dimension getPreferredSize(){
//		return new Dimension(600,400);
//	}

	/**
	 * Set audio selection.
	 * @param selection the selection to set
	 */
	public void setSelection(Selection selection) {
		this.selection = selection;
		audioClip.setSelection(selection);
	}

	/**
	 * Get audio selection.
	 * @return the selection
	 */
	public Selection getSelection() {
	    if(audioClip!=null){
	        return audioClip.getSelection();
	    }else{   
	        return selection;
	    }
	}

	
	private void playSelection() {
		if (player == null || player.isOpen())
			return;
		if(audioClip!=null){
		Selection s = audioClip.getSelection();
		if (s != null) {
			player.setStartFramePosition(s.getLeft());
			player.setStopFramePosition(s.getRight());
		} else {
			player.setStartFramePosition(0);
			player.setStopFramePosition(AudioSystem.NOT_SPECIFIED);
		}
		try {
			player.open();
			setMessage("Audio player open.");
			player.play();
			
		} catch (PlayerException e) {
			e.printStackTrace();
			setMessage("Cannot play audio !");
		}
		}
	}
	
	/* (non-Javadoc)
	 * @see ipsk.awt.ProgressListener#update(ipsk.awt.event.ProgressEvent)
	 */
	public void update(ProgressEvent progressEvent) {
//		if(progressEvent.getProgressStatus().isFinished()){
//			initAudio();
//		}
		
		ProgressStatus status=progressEvent.getProgressStatus();
		if(progressEvent instanceof ProgressErrorEvent){
			if(progressPanel!=null){
			remove(progressPanel);
			}
			String errMsg="Unknown error!";
			if(status!=null){
				errMsg=status.getMessage().localize();	
			}
			//contentPane.add(errField,BorderLayout.CENTER);
			setMessage(errMsg);
			JOptionPane.showMessageDialog(this, errMsg, "Audio applet loading error",JOptionPane.ERROR_MESSAGE);
			//contentPane.validate();
			//repaint();
		}else{
			if(status!=null){
				if(status.isDone()){
					if(progressPanel!=null){
						remove(progressPanel);
					}
					VectorBuffer vb = vbOut.getVectorBuffer();
					VectorBufferAudioSource vbAudioSource = new VectorBufferAudioSource(vb);
					audioSource = vbAudioSource;
					try {
						process();
					} catch (AudioSourceException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(this, "Could not process audio file: "+e.getMessage(), "Audio processing error", JOptionPane.ERROR_MESSAGE);
						return;
						
					}
					enableAudio();
					makeReady();
					if(autoPlayOnLoad){
						playSelection();
					}
				}else{
					setMessage("Download audio source "+status.getPercentProgress()+"%");
				}
			}
		}
	}
	
	public boolean isStartPlayOnSelect() {
		return startPlayOnSelect;
	}

	public void setStartPlayOnSelect(boolean startPlayOnSelect) {
		this.startPlayOnSelect = startPlayOnSelect;
	}


	public boolean isVisualizing() {
		return visualizing;
	}

	public void setVisualizing(boolean visualizing) {
		this.visualizing = visualizing;
		if(visualizing){
			signalUI = new AudioSignalUI();
			
			uiContainer = new AudioClipUIContainer();
			uiContainer.add(signalUI);
			if(showSonagram){
				sonagram=new FourierUI();
				sonagram.setUseThread(true);
				uiContainer.add(sonagram);
			}
			for(AudioClipUI audioClipUI:additionalAudioClipUIPluginList){
			    uiContainer.add((Component)audioClipUI);
			}
			if(showFragmentActionBar){
			    fragmentActionBar=new FragmentActionBarUI();
			    fragmentActionBar.setStartPlaybackAction(startAction);
			    uiContainer.add(fragmentActionBar);
			}
			if(showTimeScale){
				timeScale = new AudioTimeScaleUI();
				uiContainer.add(timeScale);
			}
			
			scrollPane = new AudioClipScrollPane(uiContainer);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			
			uiContainer.setAudioClip(audioClip);
			createPopupMenu();
		}else{
			if(uiContainer!=null)uiContainer.close();
			clearScreen();
			if(audioClip!=null){
				audioClip.removeAudioSampleListener(this);
				audioClip=null;
			}
			
			
		}
	}
	
	public boolean isShowSonagram() {
		return showSonagram;
	}


	public void setShowSonagram(boolean showSonagram) {
		this.showSonagram = showSonagram;
		if(!showSonagram && sonagram != null && visualizing){
			uiContainer.remove(sonagram);
		}else{
			//TODO
			// implement
		}
	}

	public boolean isShowFragmentActionBar() {
	    return showFragmentActionBar;
	}


	public void setShowFragmentActionBar(boolean showFragmentActionBar) {
	    this.showFragmentActionBar = showFragmentActionBar;
	    if(!showFragmentActionBar && visualizing){
            uiContainer.remove(fragmentActionBar);
        }else{
            //TODO
            // implement
        }
	}


	public boolean isShowTimeScale() {
		return showTimeScale;
	}

	public void setShowTimeScale(boolean showTimeScale) {
		this.showTimeScale = showTimeScale;
		if(!showTimeScale&& visualizing){
			uiContainer.remove(timeScale);
		}else{
			//TODO
			// implement
		}
	}

	
	

	public void reactivate() {
		if(Status.DEACTIVATED.equals(status)){
			audioClip.setAudioSource(audioSource);
			enableAudio();
			makeReady();
		}else if(Status.INITIALIZED.equals(status)){
		    // restart download
			load();
		}
	}

	protected void closeContentLoaders(){
	    
	}
	
	public void deactivate() {
		
		if(Status.LOADING.equals(status)){
		    // stop download
			closeDownload();
			closeContentLoaders();
//			status=Status.INITIALIZED;
			changeStatus(Status.INITIALIZED);
		}else{
			if(audioClip !=null){
				updateTimer.stop();
//				playButton.setEnabled(false);
				startAction.setEnabled(false);
				audioClip.setAudioSource(null);
			}
			if (player != null) {
				try {
					player.close();
					setMessage("Audio player closed.");
				} catch (PlayerException e) {
					e.printStackTrace();
					setMessage("Could not close audio player !");
				}

			}
//			status=Status.DEACTIVATED;
			changeStatus(Status.DEACTIVATED);
		}
		
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		String oldMessage=this.message;
		this.message = message;
		pChTa.fireEvent(new PropertyChangeEvent(this,"message",oldMessage,this.message));
	}

	/**
	 * Add property change listener.
	 * This bean sends events for the properties "status","selection" and "message".
	 * All events are sent on the AWTEvent thread.
	 */
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


	public boolean isAutoPlayOnLoad() {
		return autoPlayOnLoad;
	}


	public void setAutoPlayOnLoad(boolean autoPlayOnLoad) {
		this.autoPlayOnLoad = autoPlayOnLoad;
	}


	public boolean isShowDSPInfo() {
		return showDSPInfo;
	}


	public void setShowDSPInfo(boolean showDSPInfo) {
		this.showDSPInfo = showDSPInfo;
	}
//	
//	/**
//	 * Test method
//	 * @param args
//	 */
//	public static void showTS(final String audioUrlStr){
//		
//		
//		
//			
//			new Thread() {
//				
//			public void run() {
//				
//			Runnable show=new Runnable(){
//				public void run() {
//					JFrame f=new JFrame("Audio player bean");
//					final AudioPlayerBean aBean=new AudioPlayerBean();
//					aBean.setShowSonagram(true);
//					aBean.setShowFragmentActionBar(true);
//					aBean.setShowTimeScale(true);
//					aBean.setShowDSPInfo(false);
//					aBean.setVisualizing(true);
//					aBean.setStartPlayOnSelect(true);
//					f.getContentPane().add(aBean);
//					
//					f.addWindowListener(new WindowAdapter(){
//
//						public void windowClosing(WindowEvent e) {
//							aBean.close();
//						}
//						public void windowClosed(WindowEvent e) {
////							System.exit(0);
//						}
//						
//					});
//					f.pack();
//					f.setVisible(true);
//					f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//					URL audioUrl;
//					try {
//						audioUrl = new URL(audioUrlStr);
//						aBean.setURL(audioUrl);
//						f.pack();
//					} catch (MalformedURLException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					
//				}
//			};
//			
//			SwingUtilities.invokeLater(show);
//		
//			}
//			}.start();
//		
//		//System.exit(0);
//		
//	}
//	
//	/**
//	 * Test method
//	 * @param args
//	 */
//	public void show(String audioUrlStr){
//		
//		
//		try {
//			final URL audioUrl = new URL(audioUrlStr);
//			final AudioPlayerBean aBean=this;
//			Runnable show=new Runnable(){
//				public void run() {
//					JFrame f=new JFrame("Audio player bean");
//				
//					setShowSonagram(true);
//					setShowFragmentActionBar(true);
//					setShowTimeScale(true);
//					setShowDSPInfo(false);
//					setVisualizing(true);
//					setStartPlayOnSelect(true);
//					f.getContentPane().add(aBean);
//					
//					f.addWindowListener(new WindowAdapter(){
//
//						public void windowClosing(WindowEvent e) {
//							aBean.close();
//						}
//						public void windowClosed(WindowEvent e) {
////							System.exit(0);
//						}
//						
//					});
//					f.pack();
//					f.setVisible(true);
//					f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//					aBean.setURL(audioUrl);
//					f.pack();
//				}
//			};
//			SwingUtilities.invokeAndWait(show);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
////			System.exit(-2);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
////			System.exit(1);
//		} catch (InvocationTargetException e) {
//			e.printStackTrace();
////			System.exit(-3);
//		}
//		
//		//System.exit(0);
//		
//	}

	/**
	 * Test method
	 * @param args
	 */
	public static void main(String[] args){
		
		if(args.length!=1){
			System.err.println("Usage: AudioPlayerBean audioURL");
			System.exit(-1);
		}
		
		try {
			final URL audioUrl = new URL(args[0]);
			
			Runnable show=new Runnable(){
				public void run() {
					JFrame f=new JFrame("Test audio player bean");
					final AudioPlayerBean aBean=new AudioPlayerBean();
					aBean.setShowSonagram(false);
					aBean.setShowFragmentActionBar(true);
					aBean.setShowTimeScale(true);
					aBean.setShowDSPInfo(true);
					aBean.setVisualizing(true);
					aBean.setStartPlayOnSelect(true);
					aBean.addPropertyChangeListener(new PropertyChangeListener() {
						
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							System.out.println("Property changed: "+evt);
							
						}
					});
					f.getContentPane().add(aBean);
					
					f.addWindowListener(new WindowAdapter(){

						public void windowClosing(WindowEvent e) {
							aBean.close();
						}
						public void windowClosed(WindowEvent e) {
							System.exit(0);
						}
						
					});
					f.pack();
					f.setVisible(true);
					f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					aBean.setURL(audioUrl);
					f.pack();
				}
			};
			SwingUtilities.invokeAndWait(show);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(-2);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			System.exit(-3);
		}
		
		//System.exit(0);
		
	}


  

}
