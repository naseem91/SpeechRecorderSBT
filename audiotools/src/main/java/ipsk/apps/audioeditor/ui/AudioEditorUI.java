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
 * Date  : Apr 28, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.audioeditor.ui;

import ips.incubator.audio.arr.clip.ui.EnergyAudioClipUI;
import ips.incubator.audio.arr.clip.ui.PitchAudioClipUI;
import ips.media.action.MediaViewActions;
import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioOptions;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.ConvenienceFileAudioSource;
import ipsk.audio.PluginChain;
import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.URLAudioSource;
import ipsk.audio.actions.LoopAction;
import ipsk.audio.actions.PauseAction;
import ipsk.audio.actions.SetFramePositionAction;
import ipsk.audio.actions.StartPlaybackAction;
import ipsk.audio.actions.StartRecordAction;
import ipsk.audio.actions.StopAction;
import ipsk.audio.ajs.AJSAudioSystem;
import ipsk.audio.ajs.AJSAudioSystem.DeviceType;
import ipsk.audio.ajs.AJSDevice;
import ipsk.audio.ajs.AJSDeviceInfo;
import ipsk.audio.ajs.DeviceSelection;
import ipsk.audio.ajs.DeviceSelectionListener;
import ipsk.audio.ajs.ui.DevicesUI;
import ipsk.audio.arr.Selection;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.AudioClipListener;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.events.FramePositionChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.audio.arr.clip.ui.AudioClipScrollPane;
import ipsk.audio.arr.clip.ui.AudioClipUIContainer;
import ipsk.audio.arr.clip.ui.AudioSignalUI;
import ipsk.audio.arr.clip.ui.AudioTimeScaleUI;
import ipsk.audio.arr.clip.ui.FourierUI;
import ipsk.audio.arr.clip.ui.FragmentActionBarUI;
import ipsk.audio.capture.Capture2;
import ipsk.audio.capture.CaptureException;
import ipsk.audio.capture.PrimaryRecordTarget;
import ipsk.audio.capture.event.CaptureCloseEvent;
import ipsk.audio.capture.event.CaptureErrorEvent;
import ipsk.audio.capture.event.CaptureEvent;
import ipsk.audio.capture.event.CaptureRecordedEvent;
import ipsk.audio.capture.event.CaptureStartCaptureEvent;
import ipsk.audio.capture.event.CaptureStartEvent;
import ipsk.audio.capture.event.CaptureStartRecordEvent;
import ipsk.audio.capture.event.CaptureStopEvent;
import ipsk.audio.dsp.AudioOutputStreamFloatConverter;
import ipsk.audio.dsp.LevelInfo;
import ipsk.audio.dsp.LevelInfosBean;
import ipsk.audio.dsp.LevelMeasureFloatAudioOutputStream;
import ipsk.audio.events.FramePositionActionEvent;
import ipsk.audio.events.StartPlaybackActionEvent;
import ipsk.audio.io.AudioFileWriter;
import ipsk.audio.io.AudioFileWriterListener;
import ipsk.audio.io.event.AudioFileWriterCancelledEvent;
import ipsk.audio.io.event.AudioFileWriterErrorEvent;
import ipsk.audio.io.event.AudioFileWriterEvent;
import ipsk.audio.io.event.AudioFileWriterWrittenEvent;
import ipsk.audio.mixer.ui.PortMixersUI;
import ipsk.audio.player.Player;
import ipsk.audio.player.PlayerException;
import ipsk.audio.player.PlayerListener;
import ipsk.audio.player.event.PlayerCloseEvent;
import ipsk.audio.player.event.PlayerErrorEvent;
import ipsk.audio.player.event.PlayerEvent;
import ipsk.audio.player.event.PlayerOpenEvent;
import ipsk.audio.player.event.PlayerPauseEvent;
import ipsk.audio.player.event.PlayerStartEvent;
import ipsk.audio.player.event.PlayerStopEvent;
import ipsk.audio.plugins.AppendPlugin;
import ipsk.audio.plugins.ChannelSelectorPlugin;
import ipsk.audio.plugins.CutPlugin;
import ipsk.audio.plugins.EditPlugin;
import ipsk.audio.plugins.EncodingPlugin;
import ipsk.audio.plugins.InsertPlugin;
import ipsk.audio.tools.FrameUnitParser;
import ipsk.audio.ui.AudioFileFilter;
import ipsk.audio.ui.AudioFileFormatChooser;
import ipsk.audio.ui.LevelMeter;
import ipsk.audio.ui.TransportUI;
import ipsk.awt.WorkerException;
import ipsk.awt.datatransfer.EmptyTransferable;
import ipsk.awt.print.ComponentPrinter;
import ipsk.swing.JPopupMenuListener;
import ipsk.swing.JProgressDialogPanel;
import ipsk.swing.TitledPanel;
import ipsk.swing.action.tree.ActionFolder;
import ipsk.swing.action.tree.ActionGroup;
import ipsk.swing.action.tree.ActionTreeRoot;
import ipsk.swing.action.tree.JMenuBuilder;
import ipsk.swing.image.JComponentImageFileWriteAction;
import ipsk.text.StringTokenizer;
import ipsk.util.LocalizableMessage;
import ipsk.util.optionparser.Option;
import ipsk.util.optionparser.OptionParser;
import ipsk.util.optionparser.OptionParserException;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

// import javax.swing.Timer;



/**
 * GUI application to record, playback and edit audio files.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class AudioEditorUI extends JFrame implements ActionListener,
		AdjustmentListener, ClipboardOwner, ComponentListener, PlayerListener,
		ipsk.audio.capture.CaptureListener, AudioFileWriterListener,
		AudioClipListener, FlavorListener, WindowStateListener, WindowListener, DropTargetListener, DeviceSelectionListener {

	private static final long serialVersionUID = 6684491340301105239L;

	final static boolean DEBUG = false;

	public final static String APPNAME = "Audio Editor";

	public final static String VERSION = AudioEditorUI.class.getPackage().getImplementationVersion();

	public final static String COPYRIGHT = "\u00A9 K.J\u00E4nsch, 2004-2016";

	public final static int REFRESH_DELAY = 200;

	public final static String PREF_WINDOW_RESTORE = "window.restore_on_startup";

	public final static String PREF_WINDOW_STATE = "window.preferredState";

	public final static String PREF_WINDOW_POS_X = "window.preferredPosition.x";

	public final static String PREF_WINDOW_POS_Y = "window.preferredPosition.y";
	
	public final static String PREF_WINDOW_WIDTH = "window.preferredSize.width";

	public final static String PREF_WINDOW_HEIGHT = "window.preferredSize.height";

	public enum WindowState {
		NORMAL(JFrame.NORMAL), MAXIMIZED_BOTH(JFrame.MAXIMIZED_BOTH), MAXIMIZED_HORIZ(
				JFrame.MAXIMIZED_HORIZ), MAXIMIZED_VERT(JFrame.MAXIMIZED_VERT), ICONIFIED(
				JFrame.ICONIFIED);
		public final int awtState;

		private WindowState(int awtState) {
			this.awtState = awtState;
		}
	}
	
	private final static int REOPEN_ON_SAVE = 0;

	private final static int CLOSE_ON_SAVE = 1;

	private final static int EXIT_ON_SAVE = 2;

	private static final Float PREFERRED_LINE_BUFFER_SIZE_MILLIS = (float)4000;

	public final boolean DEF_HOLD_LINE_OPEN = true;
	
	private static final boolean DEFAULT_FAST_LEVEL_MEASURING=false;
	
	private class AudioFilewriterEventProcessor implements Runnable{
		final AudioFileWriterEvent event;
		AudioFilewriterEventProcessor(AudioFileWriterEvent event){
			this.event=event;
		}
		
		public void run() {
			processAudioFileWriterevent(event);
			
		}
	}

	private boolean viewUpdate;

	private AudioClip audioSample;

	private AudioClipUIContainer asc;

	private Clipboard clipBoard;

	private JMenuItem miAudioFormat;

	private String title;

	private PortMixersUI mixerUI;

	private DevicesUI devicesUI;

	private JPanel aboutPanel = null;

	private JMenuItem miMDevices;

	// Declarations for menus
	private JMenuBar mainMenuBar = new JMenuBar();

	private JMenu fileMenu = new JMenu("File");

	protected JMenuItem miNew;

	protected JMenuItem miOpen;
    
    protected JMenuItem miPrint;

	protected JMenuItem miClose;

	protected JMenuItem miSave;

	protected JMenuItem miSaveAs;

	protected JMenuItem miQuit;

	
	public class CutAction extends ipsk.swing.CutAction{

		private static final long serialVersionUID = 7140861734683174053L;

		/* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            doCut();
            
        }
	}
	
	private CutAction cutAction=new CutAction();
	
	   public class CopyAction extends ipsk.swing.CopyAction{
		   
		private static final long serialVersionUID = -8578607687877669062L;

			/* (non-Javadoc)
	         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
	         */
	        @Override
	        public void actionPerformed(ActionEvent arg0) {
	            doCopy();
	            
	        }
	    }
	    
	    private CopyAction copyAction=new CopyAction();
	    
	    public class PasteAction extends ipsk.swing.PasteAction{

			private static final long serialVersionUID = -6510760462243552390L;

			/* (non-Javadoc)
             * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
             */
            @Override
            public void actionPerformed(ActionEvent arg0) {
                doPaste();
                
            }
        }
	    private PasteAction pasteAction=new PasteAction();
	    
	    public class AppendAction extends ipsk.swing.AbstractLocalizableAction{

			private static final long serialVersionUID = 4123861114935395069L;

			public AppendAction() {
                super("append", new LocalizableMessage("Append"));
            }

            /* (non-Javadoc)
             * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
             */
            @Override
            public void actionPerformed(ActionEvent arg0) {
                doAppend();
            }
        }
        private AppendAction appendAction=new AppendAction();
        
        public class UndoAction extends ipsk.swing.UndoAction{

			private static final long serialVersionUID = -5162984122121810941L;

			/* (non-Javadoc)
             * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
             */
            @Override
            public void actionPerformed(ActionEvent arg0) {
                doUndo();
                
            }
        }
        private UndoAction undoAction=new UndoAction();
        public class SelectAllAction extends ipsk.swing.SelectAllAction{

			private static final long serialVersionUID = -4467050585331391998L;

			/* (non-Javadoc)
             * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
             */
            @Override
            public void actionPerformed(ActionEvent arg0) {
               doSelectAll();
            }
        }
        private SelectAllAction selectAllAction=new SelectAllAction();
        
        public class CancelSelectionAction extends ipsk.swing.CancelSelectionAction{
        	
			private static final long serialVersionUID = -8147414359554906777L;

			/* (non-Javadoc)
             * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
             */
            @Override
            public void actionPerformed(ActionEvent arg0) {
               doCancelSelection();    
            }
        }
        private CancelSelectionAction cancelSelectionAction=new CancelSelectionAction();
        
        
       
        
	private JMenu editMenu;// = new JMenu("Edit");

	// protected JMenuItem miUndo;
	//private JMenuItem miCut;

//	private JMenuItem miCopy;

//	private JMenuItem miPaste;

//	private JMenuItem miAppend;

	// protected JMenuItem miClear;
//	private JMenuItem miSelectAll;

//	private JMenuItem miSelectNone;

//	private JMenuItem miUndo;

	// protected JMenuItem miSelectAll;

	private JMenu naviMenu=new JMenu("Navigate");
	private JMenuItem miGoto=new JMenuItem("Go to");
	
	private JMenu generateMenu = new JMenu("Generate");
	
	private JMenuItem sineWaveGeneratorMi;
	
	private JMenu settingsMenu = new JMenu("Control");

	private JMenuItem miMixer;

	private JMenu miChannelSelector;
	
	private JMenuItem miAudioOptions;
	
	private JRadioButtonMenuItem[] miChannelSelects;

	private JRadioButtonMenuItem miChannelsAll;

	private JMenu optionsMenu = new JMenu("Options");

	private JCheckBoxMenuItem miRestoreWindow;

	private JMenu helpMenu = new JMenu("Help");

	private JMenuItem miAbout;

	private JMenuItem miInfo;

	// private JCustomScrollPane arrScrollPane;
	private ResourceBundle rb;

	private File recFile;

	// private AudioFormat audioFormat;

	private AudioFileFormat audioFormat;

	private TransportUI tp;

	private LevelMeter lm;

	private JPanel levelPanel;

	private StatusBar statusBar;

	private StartPlaybackAction startPlaybackAction;

	private StopAction stopAction;

	private PauseAction pauseAction;

	private SetFramePositionAction setFramePositionAction;

	private StartRecordAction startRecordAction;

	private LoopAction loopAction;
	
	// private FileSetChooser fsc;
	// private J2AudioController j2ac;
	private Player playback;

	private Capture2 capture;
	
	private AudioOutputStreamFloatConverter captureOutputStream;
	private LevelMeasureFloatAudioOutputStream captureLevelMeasureStream;

	private AudioSource audioSource;

	private PluginChain editSource;

	private PluginChain playbackSource;

	private int editableOffset;

	// private UploadCache uploadCache = null;
	private Timer viewUpdateTimer = null;

	private JPanel content;

	private static AudioEditorUI demo;

	private File lastOpenDirectory;

	private File lastSaveDirectory;

	// private boolean configured;
//	private MixerManager mixerManager;

	private DeviceSelection captureDeviceSelection;
	private DeviceSelection playbackDeviceSelection;
	// private AudioFileFormat.Type audioFileFormatType =
	// AudioFileFormat.Type.WAVE;

	private boolean overwrite;

	private boolean viewUpdateRunning;

	private int selectedChannel = -1;

	private InfoViewer infoPanel;

	private int actionOnSaved = REOPEN_ON_SAVE;

	private boolean saved = true;
	private AudioFileWriter afw;

	//private Object saveNotify = new Object();

	//private boolean quitOnSave;

	//private boolean holdLineOpen = DEF_HOLD_LINE_OPEN;

	private int windowPreferredState = JFrame.NORMAL;
	private boolean restoringWindowState=false;

	private Dimension windowPreferredSize = null;

	private Preferences preferences = null;

	private AudioClipScrollPane arrScrollPane = null;
	
	private JDialog mixerDialog;
	private JProgressDialogPanel progressDialog=null;
	
	private boolean fastLevelMeasuring=DEFAULT_FAST_LEVEL_MEASURING;
	
	private AudioOptions audioOptions;
	private String defaultPrimaryRecordTargetName;

    private volatile boolean closing=false;
    
    private boolean keepPlayerOpen=true;

	class EncodingConversionAllowedAction extends AbstractAction {

		private static final long serialVersionUID = 71761657739109628L;

		public EncodingConversionAllowedAction(String text, Icon icon) {
			super(text, icon);
		}

		public void actionPerformed(ActionEvent e) {
			// playback.setConversionAllowed(e.get)
		}
	}

	/**
	 * The UI interface for an audio controller.
	 */
	public AudioEditorUI() {
		super();
//		fastLevelMeasuring=true;
		addWindowListener(this);
		addWindowStateListener(this);
		addComponentListener(this);
		title = new String(APPNAME);
		setTitle(title);
		String packageName = getClass().getPackage().getName();
		rb = ResourceBundle.getBundle(packageName + ".ResBundle");

		try{
			// using UNIX the default preferences implementation tries to 
			// lock the system preferences under /etc/.java./.systemPrefs
			// every 30 seconds
			// the locking fails as non root user
			// workaround: set the timeout to a very high value
			// The setting has to be done before the Preferences class is loaded
			// because the value is stored in a static final way in FileSystemPreferences
			// Oracle should fix this
			System.setProperty("java.util.prefs.syncInterval","100000000");
			
		}catch(SecurityException se){
			// fail silently
			System.err.println("Could not increase lockinterval");
		}
//		Preferences sysPreferences=null;
//		sysPreferences=Preferences.systemNodeForPackage(getClass());
		
		preferences = Preferences.userNodeForPackage(this.getClass());
		restoringWindowState = preferences.getBoolean(PREF_WINDOW_RESTORE,
				false);
		
		if (restoringWindowState) {
			windowPreferredState = preferences.getInt(PREF_WINDOW_STATE, 0);

			String windowPreferredWidthStr = preferences.get(PREF_WINDOW_WIDTH,
					null);
		String windowPreferredHeightStr = preferences.get(
				PREF_WINDOW_HEIGHT, null);
		if (windowPreferredWidthStr != null
				&& windowPreferredHeightStr != null) {
			int width = Integer.parseInt(windowPreferredWidthStr);
			int height = Integer.parseInt(windowPreferredHeightStr);
			windowPreferredSize = new Dimension(width, height);
		}
			int locX = preferences.getInt(PREF_WINDOW_POS_X, 0);
			int locY = preferences.getInt(PREF_WINDOW_POS_Y, 0);
			setLocation(locX, locY);
		}else{
			setLocationByPlatform(true);
		}
			
		audioOptions=new AudioOptions(); 
		PrimaryRecordTarget primaryRecordTargetApplDefault=PrimaryRecordTarget.TEMP_RAW_FILE;
//		PrimaryRecordTarget primaryRecordTarget=null;
        defaultPrimaryRecordTargetName="Default: "+primaryRecordTargetApplDefault;
        String primRecTgtUsrPref=preferences.get("primaryRecordTarget",null);
//        if(sysPreferences!=null){
//            String primRecTgtSysPref=sysPreferences.get("primaryRecordTarget",null);
//            if(primRecTgtSysPref!=null){
//                PrimaryRecordTarget primaryRecordTargetSys=PrimaryRecordTarget.valueOf(primRecTgtSysPref);
//                audioOptions.setPrimaryRecordTargetDefault(primaryRecordTargetSys);
//                defaultPrimaryRecordTargetName="Default (System preference: "+primaryRecordTargetSys+")";
//            }
//            
//        }
        if(primRecTgtUsrPref!=null){
        	try{
        		PrimaryRecordTarget primaryRecordTarget=PrimaryRecordTarget.valueOf(primRecTgtUsrPref);
        		audioOptions.setPrimaryRecordTarget(primaryRecordTarget);
        	}catch(IllegalArgumentException iae){
        		 iae.printStackTrace();
                 JOptionPane.showMessageDialog(this,
                         "Could not set primary recording target setting "+primRecTgtUsrPref+".", "Primary recording target setting error",
                         JOptionPane.ERROR_MESSAGE);
        	}
            
        }
        if(capture!=null){
            audioOptions.setLineBufferSize(capture.getLineBufferSize());
        }
        
		content = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// content=new
		content.setOpaque(true); // content panes must be opaque
		miNew = new JMenuItem("New");
		fileMenu.add(miNew);
		miNew.setEnabled(false);
		miNew.addActionListener(this);
		miOpen = new JMenuItem("Open");
		fileMenu.add(miOpen);
		miOpen.setEnabled(false);
		miOpen.addActionListener(this);
        miPrint=new JMenuItem("Print...");
        // TODO does not work with windows !!
        fileMenu.add(miPrint);
        miPrint.addActionListener(this);
		miSave = new JMenuItem("Save");
		fileMenu.add(miSave);
		miSave.setEnabled(false);
		miSave.addActionListener(this);
		miSaveAs = new JMenuItem("Save As");
		fileMenu.add(miSaveAs);
		miSaveAs.setEnabled(false);
		miSaveAs.addActionListener(this);
		miClose = new JMenuItem("Close");
		miClose.setEnabled(false);
		fileMenu.add(miClose);
		miClose.addActionListener(this);
		miQuit = new JMenuItem("Exit");
		fileMenu.add(miQuit);
		miQuit.addActionListener(this);
//		miCut = new JMenuItem("Cut");
//		miCut.setEnabled(false);
//		editMenu.add(miCut);
//		miCut.addActionListener(this);
//		miCopy = new JMenuItem("Copy");
//		miCopy.setEnabled(false);
//		editMenu.add(miCopy);
//		miCopy.addActionListener(this);
//		miPaste = new JMenuItem("Paste");
//		miPaste.setEnabled(false);
//		editMenu.add(miPaste);
//		miPaste.addActionListener(this);
//		miAppend = new JMenuItem("Append");
//		miAppend.setEnabled(false);
//		editMenu.add(miAppend);
//		miAppend.addActionListener(this);
		appendAction.setEnabled(false);
		
//		editMenu.addSeparator();

//		miSelectAll = new JMenuItem("Select all");
//		miSelectAll.setEnabled(false);
//		editMenu.add(miSelectAll);
//		miSelectAll.addActionListener(this);
//		
		selectAllAction.setEnabled(false);
		
//		miSelectNone = new JMenuItem("Select none");
//		miSelectNone.setEnabled(false);
//		editMenu.add(miSelectNone);
//		miSelectNone.addActionListener(this);
//		
		cancelSelectionAction.setEnabled(false);

//		miUndo = new JMenuItem("Undo");
//		miUndo.setEnabled(false);
//		editMenu.add(miUndo);
//		miUndo.addActionListener(this);
		
		undoAction.setEnabled(false);
		
		undoAction.setEnabled(false);

		naviMenu.add(miGoto);
		miGoto.setEnabled(false);
		miGoto.addActionListener(this);
		
		sineWaveGeneratorMi=new JMenuItem("Sine wave");
		sineWaveGeneratorMi.addActionListener(this);
		generateMenu.add(sineWaveGeneratorMi);
		
		miMixer = new JMenuItem("Mixer");
		miMixer.addActionListener(this);
		settingsMenu.add(miMixer);
		miAudioFormat = new JMenuItem("Audio format");
		miAudioFormat.addActionListener(this);
		settingsMenu.add(miAudioFormat);
		miMDevices = new JMenuItem("Devices");
		miMDevices.addActionListener(this);
		settingsMenu.add(miMDevices);
		
		miAudioOptions=new JMenuItem("Audio options");
		miAudioOptions.addActionListener(this);
		settingsMenu.add(miAudioOptions);
		
		miChannelSelector = new JMenu("Channels");
		settingsMenu.add(miChannelSelector);
		miChannelsAll = new JRadioButtonMenuItem("All channels");
		miChannelsAll.addActionListener(this);

		miRestoreWindow = new JCheckBoxMenuItem("Restore window size and state");
		miRestoreWindow.setSelected(restoringWindowState);
		miRestoreWindow.addActionListener(this);
		optionsMenu.add(miRestoreWindow);
		
		
		
		miAbout = new JMenuItem("About");
		miAbout.addActionListener(this);
		helpMenu.add(miAbout);
		miInfo = new JMenuItem("Info");
		miInfo.addActionListener(this);
		helpMenu.add(miInfo);

		

		overwrite = false;
//		try {
//		mixerManager = new MixerManager();
		AJSAudioSystem.setApplicationName(APPNAME);
		captureDeviceSelection=new DeviceSelection(DeviceType.CAPTURE);
		AJSDevice defCaptureDev=AJSAudioSystem.getDefaultCaptureDevice();
        captureDeviceSelection.setDevice(defCaptureDev);
		playbackDeviceSelection=new DeviceSelection(DeviceType.PLAYBACK);
		AJSDevice defPlayDev=AJSAudioSystem.getDefaultPlaybackDevice();
		playbackDeviceSelection.setDevice(defPlayDev);
		
		 try {
            devicesUI=new DevicesUI(captureDeviceSelection, playbackDeviceSelection);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    e.getLocalizedMessage(), "Audio devices error",
                    JOptionPane.ERROR_MESSAGE);
        }
		 
//		} catch (LineUnavailableException e2) {
//			JOptionPane.showMessageDialog(this, e2.getLocalizedMessage(),
//					"Line unavailable", JOptionPane.ERROR_MESSAGE);
//		}
		audioFormat = new AudioFileFormat(AudioFileFormat.Type.WAVE,
				new AudioFormat(44100, 16, 2, true, false),
				ThreadSafeAudioSystem.NOT_SPECIFIED);
		// j2ac = new J2AudioController();

		startPlaybackAction = new StartPlaybackAction();
		startPlaybackAction.setEnabled(false);
		startPlaybackAction.addActionListener(this);
		stopAction = new StopAction();
		stopAction.addActionListener(this);
		pauseAction = new PauseAction();
		pauseAction.addActionListener(this);
		setFramePositionAction = new SetFramePositionAction();
		setFramePositionAction.addActionListener(this);
		startRecordAction = new StartRecordAction();
		startRecordAction.addActionListener(this);
		loopAction = new LoopAction();
		loopAction.addActionListener(this);
		tp = new TransportUI(startPlaybackAction, stopAction, pauseAction,
				setFramePositionAction, startRecordAction,loopAction);

//		playback = new Player(mixerManager.getSelectedPlaybackMixer());
		playback=new Player(playbackDeviceSelection.getMixer());
		playback.addPlayerListener(this);
//		capture = new Capture2(mixerManager.getSelectedCaptureMixer());
		capture= new Capture2(captureDeviceSelection.getMixer());
		capture.addCaptureListener(this);
		captureOutputStream=new AudioOutputStreamFloatConverter();
		capture.addAudioOutputStream(captureOutputStream);
		captureLevelMeasureStream=new LevelMeasureFloatAudioOutputStream();
		captureOutputStream.addFloatAudioOutputStream(captureLevelMeasureStream);
		
		playback.setPreferredLineBufferSizeMillis(PREFERRED_LINE_BUFFER_SIZE_MILLIS);
//		mixerManager.addMixerManagerListener(this);
		captureDeviceSelection.addDeviceSelectionListener(this);
		playbackDeviceSelection.addDeviceSelectionListener(this);
		
		capture.setPrimaryRecordTarget(PrimaryRecordTarget.TEMP_RAW_FILE);
		capture.setPreferredLineBufferSizeMillis(PREFERRED_LINE_BUFFER_SIZE_MILLIS);
		capture.setAudioFileFormat(audioFormat);
//		capture.setMeasureLevel(true);
		
		
		viewUpdateTimer = new Timer(REFRESH_DELAY, this);
		viewUpdateTimer.setRepeats(true);

		viewUpdate = false;

		lastOpenDirectory = new File(System.getProperty("user.home"));
		lastSaveDirectory = new File(System.getProperty("user.home"));

		AudioSignalUI signalView = new AudioSignalUI();
		signalView.setUseThread(false);
		FourierUI sonagram=new FourierUI();
		sonagram.setUseThread(true);
//		sonagram.setMaxFrequency(5000.0);
		
//		EnergyAudioClipUI eAc=new EnergyAudioClipUI();
//		PitchAudioClipUI pAc=new PitchAudioClipUI();
		FragmentActionBarUI playActionBar = new FragmentActionBarUI();
		playActionBar.setStartPlaybackAction(startPlaybackAction);
		AudioTimeScaleUI timeScale = new AudioTimeScaleUI();

		asc = new AudioClipUIContainer();

		asc.add(signalView);
		asc.add(sonagram);
//		asc.add(eAc);
//		asc.add(pAc);
		asc.add(timeScale);
		asc.add(playActionBar);

		asc.addActionListener(this);

		arrScrollPane = new AudioClipScrollPane();
		arrScrollPane.setShowYScales(true);
		arrScrollPane.setAudioClipUiContainer(asc);
		
		JComponentImageFileWriteAction pa=new JComponentImageFileWriteAction(asc,new LocalizableMessage("Save to image file ..."));
		
		ActionFolder ascActionTree=arrScrollPane.getActionTreeRoot();
		ActionFolder afft=new ActionTreeRoot();
		ActionFolder aff=ActionFolder.buildTopLevelFolder(ActionFolder.FILE_FOLDER_KEY);
        afft.add(aff);
        aff.add(pa);
        ActionFolder aef=ActionFolder.buildTopLevelFolder(ActionFolder.EDIT_FOLDER_KEY);
        ActionGroup aeurg=new ActionGroup(ActionGroup.UNDO_REDO);
        aeurg.add(undoAction);
        aef.add(aeurg);
        
        afft.add(aef);
        
        
        ActionFolder avf=ActionFolder.buildTopLevelFolder(ActionFolder.VIEW_FOLDER_KEY);
        afft.add(avf);
       
       
        
		aef.add(cutAction);
		aef.add(copyAction);
		aef.add(pasteAction);
		aef.add(appendAction);
//		aef.add(undoAction);
		aef.add(selectAllAction);
		aef.add(cancelSelectionAction);
	
		afft.merge(ascActionTree);
		
		
		MediaViewActions mediaViewActions=new MediaViewActions(asc);
		
//		mediaLenUnitGroup.add(mediaLenFramesAction);
//        mediaLenUnitGroup.add(mediaLenTimeAction);
//        mediaLenFramesAction.setGroup(mediaLenUnitGroup);
//        mediaLenTimeAction.setGroup(mediaLenUnitGroup);    
		
//        MediaLengthUnit mlu=asc.getMediaLengthUnit();
//        if(MediaLengthUnit.TIME.equals(mlu)){
//            mediaLenTimeAction.setSelected(true);
//        }   
        ActionGroup lengthUnitGroup=new ActionGroup("view.length_unit");
        ActionFolder uaf=new ActionFolder("view.units", new LocalizableMessage("Units"));
        lengthUnitGroup.add(uaf);
        
        ActionFolder tff=new ActionFolder("view.units.time", new LocalizableMessage("Time"));
        uaf.add(tff);
        MediaViewActions.MediaLengthUnitFramesAction mediaLenFramesAction=mediaViewActions.getMediaLenFramesAction();
        
        MediaViewActions.MediaLengthUnitTimeAction mediaLenTimeAction=mediaViewActions.getMediaLenTimeAction();
        MediaViewActions.TimeFormatSecondsMsAction timeFormatSecondsMsAction=mediaViewActions.getTimeFormatSecondsMsAction();
        MediaViewActions.MediaTimeFormatAction mediaTimeAction=mediaViewActions.getMediaTimeAction();
//        timeFormatGroup.add(timeFormatSecondsMsAction);
//        timeFormatGroup.add(mediaTimeAction);
//        timeFormatSecondsMsAction.setGroup(timeFormatGroup);
//        mediaTimeAction.setGroup(timeFormatGroup);
        
        tff.add(timeFormatSecondsMsAction);
        tff.add(mediaTimeAction);
        timeFormatSecondsMsAction.setSelected(true);
        
        uaf.add(mediaLenFramesAction);
        uaf.add(mediaLenTimeAction);
        
        avf.add(lengthUnitGroup);
        
		// Add File print menu items
		
		 // applets can't even read the user.home property, check the access here and 
        // do not add the file menu if access is denied
        try{
            System.getProperty("user.home");
            //AccessController.checkPermission(userHomePropPerm);
            
        }catch(SecurityException se){
            // OK no file menu added
            //se.printStackTrace();
        }
		JMenuBuilder menuBuilder=new JMenuBuilder(afft);
		JPopupMenu pm=menuBuilder.buildJPopupMenu();
		JPopupMenuListener pml=new JPopupMenuListener(pm);
		arrScrollPane.addMouseListener(pml);
		asc.addPopupMouseListener(pml);
		
		editMenu=menuBuilder.buildMenu(ActionFolder.EDIT_FOLDER_KEY);
		JMenu viewMenu=menuBuilder.buildMenu(ActionFolder.VIEW_FOLDER_KEY);
		
		mainMenuBar.add(fileMenu);
        mainMenuBar.add(editMenu);
        mainMenuBar.add(viewMenu);
        mainMenuBar.add(naviMenu);
        mainMenuBar.add(settingsMenu);
        mainMenuBar.add(optionsMenu);
        mainMenuBar.add(Box.createHorizontalGlue());
        mainMenuBar.add(helpMenu);
        
		//arrScrollPane.setRowHeaderView(asc.getyScalesComponent());
		
		if (restoringWindowState && windowPreferredSize != null) {
			this.setPreferredSize(windowPreferredSize);
			//System.out.println("From prefs: " + arrScrollPanePreferredSize);
			// arrScrollPane.setSize(arrScrollPanePreferredSize);
		}
		addComponentListener(this);
		

		levelPanel = new TitledPanel(rb.getString("level"));
		lm = new LevelMeter();
		lm.setUseIntervalPeakLevel(true);
		lm.addActionListener(this);
		levelPanel.setLayout(new BorderLayout());
		levelPanel.add(lm,BorderLayout.CENTER);
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		content.add(levelPanel, c);

		c.gridx++;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		content.add(arrScrollPane, c);

		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		content.add(tp, c);

		statusBar = new StatusBar();
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		content.add(statusBar, c);

		setJMenuBar(mainMenuBar);

		// We have to check save status first so do nothing
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setContentPane(content);
		getRootPane().setMinimumSize(content.getMinimumSize());
		Toolkit toolkit = getToolkit();
		clipBoard = toolkit.getSystemClipboard();
		clipBoard.addFlavorListener(this);
		DropTarget dt=new DropTarget(this,this);
		audioSample = new AudioClip();
		asc.setAudioClip(audioSample);
	
		
		audioSample.addAudioSampleListener(this);

	}
	
	/**
	 * @throws PlayerException 
	 * 
	 */
	public void init() throws PlayerException {
		AJSDevice cDevInfo=AJSAudioSystem.getDefaultResolvedCaptureDevice();
		if(cDevInfo!=null){
			capture.setMixer(cDevInfo.getMixer());
		}
		AJSDevice pDevInfo=AJSAudioSystem.getDefaultResolvedPlaybackDevice();
		if(pDevInfo!=null){
			playback.setMixer(pDevInfo.getMixer());
		}
	}

	public boolean isRestoringWindowState(){
		return restoringWindowState;
	}

	private void startViewUpdates() {
		// return;

		if (viewUpdateRunning)
			return;
		viewUpdateRunning = true;
		viewUpdateTimer.start();

	}

	private void stopViewUpdates() {

		viewUpdateTimer.stop();
		viewUpdateRunning = false;

		updateView();
	}

	private void updatePlaybackView() {
		viewUpdate = true;
		LevelInfo[] lis=playback.getLevelInfos();
		lm.setLevelInfos(lis);
		if(lis!=null){
			for(LevelInfo li:lis){
				li.resetIntervalPeakLevel();
			}
		}
		long pos = playback.getFramePosition();

		audioSample.setFramePosition(pos);
		tp.setFramePosition(pos);
		viewUpdate = false;
	}

	private void updateCaptureView() {
		viewUpdate = true;
		if(!fastLevelMeasuring){
			LevelInfosBean lib=captureLevelMeasureStream.getLevelInfosBean();
		    lm.setLevelInfos(lib.getLevelInfos());
		    lib.resetIntervalPeakLevel();
		}	
		tp.setFramePosition(capture.getFramePosition());
		viewUpdate = false;
	}

	private void updateView() {

		if (capture.isRecording() || capture.isCapturing()) {
			updateCaptureView();
		} else {
			updatePlaybackView();
		}
		
	}
	private void doCopy(){
	// AudioClip sample0 = arr.getAudioSamples()[0];
    Selection selection = audioSample.getSelection();
    PluginChain copyData = (PluginChain) editSource.clone();
    try {
        copyData.add(new EditPlugin(selection));
    } catch (AudioFormatNotSupportedException e) {
        JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
                "Copy error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        return;
    }
    clipBoard.setContents(copyData, this);
//  miPaste.setEnabled(true);
//  miAppend.setEnabled(true);
    updatePaste();
	}
	private void doCut(){
	// AudioClip sample0 = arr.getAudioSamples()[0];
    Selection selection = audioSample.getSelection();
    PluginChain cutData = (PluginChain) editSource.clone();
    try {
        cutData.add(new EditPlugin(selection));
        editSource.add(new CutPlugin(selection));
    } catch (AudioFormatNotSupportedException e) {
        JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
                "Cut error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        return;
    }
    clipBoard.setContents(cutData, this);
//  miPaste.setEnabled(true);
//  miAppend.setEnabled(true);
    updatePaste();
    audioSample.setSelection(null);
    miSave.setEnabled(true);
    saved = false;
    audioSource = editSource;
    // try {
    // playback.setAudioSource(audioSource);
    // } catch (Exception e1) {
    // JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(),
    // "Playback source error", JOptionPane.ERROR_MESSAGE);
    // }

    openEditSource();
    preparePlaybackSources();
}
	
	
	
	private void doPaste(){
	    Transferable pasteTrans = clipBoard.getContents(this);
        // AudioClip sample0 = arr.getAudioSamples()[0];
        long pastePosition = audioSample.getFramePosition();
        PluginChain pasteData = null;
        try {
            pasteData = (PluginChain) pasteTrans
                    .getTransferData(new DataFlavor(PluginChain.class, ""));
            editSource.add(new InsertPlugin(pasteData, pastePosition));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
                    "Paste error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }
        miSave.setEnabled(true);
        saved = false;
        audioSource = editSource;
        // try {
        // playback.setAudioSource(audioSource);
        // } catch (Exception e1) {
        // JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(),
        // "Playback source error", JOptionPane.ERROR_MESSAGE);
        // }
        //
        // sample0.setAudioSource(editSource);
        openEditSource();
        preparePlaybackSources();
	}
	private void doAppend(){
	    Transferable pasteTrans = clipBoard.getContents(this);

	    PluginChain pasteData = null;
	    try {
	        pasteData = (PluginChain) pasteTrans
	        .getTransferData(new DataFlavor(PluginChain.class, ""));
	        AppendPlugin appPlugin = new AppendPlugin(pasteData);
	        editSource.add(appPlugin);
	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
	                "Append error", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();
	    }
	    miSave.setEnabled(true);
	    saved = false;
	    audioSource = editSource;

	    openEditSource();
	    preparePlaybackSources();
	}
	
	private void doUndo(){
	    editSource.removeLast();
	    miSave.setEnabled(true);
	    saved = false;
	    openEditSource();
	    preparePlaybackSources();
	}
	
	private void doCancelSelection(){
	    audioSample.setSelection(null);
	}
	private void doSelectAll(){
	    try {
	        audioSample.setSelection(new Selection(0, audioSample
	                .getAudioSource().getFrameLength()));
	    } catch (AudioSourceException e) {
	        JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
	                "Audio source error", JOptionPane.ERROR_MESSAGE);
	    }
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ev) {
		if (DEBUG)
			System.out.println("Action event:" + ev);

		Object src = ev.getSource();
		if (src == viewUpdateTimer) {
			updateView();
		} else if (src == miNew) {
			doNew();
		} else if (src == miOpen) {
			doOpen();
		}else if (src == miPrint) {
            doPrint();
        }  else if (src == miSaveAs) {
			File file = null;
			JFileChooser fch = new JFileChooser(lastSaveDirectory);
			javax.sound.sampled.AudioFileFormat.Type aff = null;
			javax.sound.sampled.AudioFileFormat.Type[] fileTypes = ThreadSafeAudioSystem.getAudioFileTypes();
			javax.sound.sampled.AudioFileFormat.Type currentFileFormat=audioFormat.getType();
			AudioFileFilter selAfFilter=null;
			for (javax.sound.sampled.AudioFileFormat.Type ft:fileTypes) {
				AudioFileFilter afFilter=new AudioFileFilter(ft);
				fch.addChoosableFileFilter(afFilter);
				if(ft.equals(currentFileFormat)){
					//fch.setFileFilter(afFilter);
					selAfFilter=afFilter;
				}
			}
			if(selAfFilter!=null)fch.setFileFilter(selAfFilter);
			// fch.setMultiSelectionEnabled(true);
			int returnVal = fch.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// files = fch.getSelectedFiles();

				file = fch.getSelectedFile();
				FileFilter ff=fch.getFileFilter();
				
				aff = ((AudioFileFilter) fch.getFileFilter())
						.getAudioFileTypes()[0];
			} else {
				return;
			}
			lastSaveDirectory = fch.getCurrentDirectory();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			actionOnSaved = REOPEN_ON_SAVE;
			if (checkOverWrite(file)){
				
				save(aff, file);
			}
			// try {
			// for (int i = 0; i < files.length; i++) {
			// AudioInputStream ais = editSources[i].getAudioInputStream();
			// AudioSystem.write(ais, aff, files[i]);
			// editSources[i] = new PluginChain(playSources[i]);
			// }
			// } catch (Exception e) {
			// JOptionPane.showMessageDialog(
			// this,
			// e.getLocalizedMessage(),
			// "Save As error",
			// JOptionPane.ERROR_MESSAGE);
			// e.printStackTrace();
			// }
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if (src == miSave) {
			// File[] files = j2ac.getRecordingFiles();
			javax.sound.sampled.AudioFileFormat.Type aff = AudioFileFormat.Type.WAVE;
			// Type[] fileTypes = AudioSystem.getAudioFileTypes();
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			actionOnSaved = REOPEN_ON_SAVE;
			if (overwrite || checkOverWrite(recFile)) {
				save(aff, recFile);
				miSave.setEnabled(false);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if (src == miClose) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			doClose();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//		} else if (src == miCopy) {
//			// AudioClip sample0 = arr.getAudioSamples()[0];
//			Selection selection = audioSample.getSelection();
//			PluginChain copyData = (PluginChain) editSource.clone();
//			try {
//				copyData.add(new EditPlugin(selection));
//			} catch (AudioFormatNotSupportedException e) {
//				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
//						"Copy error", JOptionPane.ERROR_MESSAGE);
//				e.printStackTrace();
//				return;
//			}
//			clipBoard.setContents(copyData, this);
////			miPaste.setEnabled(true);
////			miAppend.setEnabled(true);
//			updatePaste();
//		
//		} else if (src == miCut) {
//			doCut();
//		} else if (src == miPaste) {
//
//			Transferable pasteTrans = clipBoard.getContents(this);
//			// AudioClip sample0 = arr.getAudioSamples()[0];
//			long pastePosition = audioSample.getFramePosition();
//			PluginChain pasteData = null;
//			try {
//				pasteData = (PluginChain) pasteTrans
//						.getTransferData(new DataFlavor(PluginChain.class, ""));
//				editSource.add(new InsertPlugin(pasteData, pastePosition));
//			} catch (Exception e) {
//				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
//						"Paste error", JOptionPane.ERROR_MESSAGE);
//				e.printStackTrace();
//				return;
//			}
//			miSave.setEnabled(true);
//			saved = false;
//			audioSource = editSource;
//			// try {
//			// playback.setAudioSource(audioSource);
//			// } catch (Exception e1) {
//			// JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(),
//			// "Playback source error", JOptionPane.ERROR_MESSAGE);
//			// }
//			//
//			// sample0.setAudioSource(editSource);
//			openEditSource();
//			openPlaybackSources();
//		} else if (src == miAppend) {
//
//			Transferable pasteTrans = clipBoard.getContents(this);
//
//			PluginChain pasteData = null;
//			try {
//				pasteData = (PluginChain) pasteTrans
//						.getTransferData(new DataFlavor(PluginChain.class, ""));
//				AppendPlugin appPlugin = new AppendPlugin(pasteData);
//				editSource.add(appPlugin);
//			} catch (Exception e) {
//				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
//						"Append error", JOptionPane.ERROR_MESSAGE);
//				e.printStackTrace();
//			}
//			miSave.setEnabled(true);
//			saved = false;
//			audioSource = editSource;
//
//			openEditSource();
//			openPlaybackSources();
//		} else if (src == miSelectNone) {
//
//			audioSample.setSelection(null);
//		} else if (src == miSelectAll) {
//			try {
//				audioSample.setSelection(new Selection(0, audioSample
//						.getAudioSource().getFrameLength()));
//			} catch (AudioSourceException e) {
//				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
//						"Audio source error", JOptionPane.ERROR_MESSAGE);
//			}
//		} else if (src == miUndo) {
//
//			editSource.removeLast();
//			miSave.setEnabled(true);
//			saved = false;
//			openEditSource();
//			openPlaybackSources();
		} else if(src== miGoto){
			String newFramePosString=JOptionPane.showInputDialog(this,"Input frame position to go");
			long newFramePos;
			try{
				newFramePos=Long.parseLong(newFramePosString);
			}catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(this,
						"\""+newFramePosString+"\" is not a integer number", "Number format error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			audioSample.setFramePosition(newFramePos);
		} else if (src == miMixer) {
		
			// TODO close the mixers properly
			if (mixerUI == null) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					mixerUI = new PortMixersUI();
				} catch (LineUnavailableException e) {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(this,
							e.getLocalizedMessage(), "Mixer device error",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				JOptionPane optPane = new JOptionPane(mixerUI);
				mixerDialog = optPane.createDialog(this, rb
						.getString("mixer"));
				mixerDialog.setModal(false);
				mixerDialog.setResizable(true);
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			// JOptionPane.showMessageDialog(this, mixerUI,
			// rb.getString("mixer"), JOptionPane.PLAIN_MESSAGE, null);
			
			mixerDialog.setVisible(true);
		} else if (src == miMDevices) {

			if (devicesUI == null) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
//					devicesUI = new MixerManagerUI(mixerManager);
				    devicesUI=new DevicesUI(captureDeviceSelection, playbackDeviceSelection);
				} catch (LineUnavailableException e) {
					JOptionPane.showMessageDialog(this,
							e.getLocalizedMessage(), "Audio devices error",
							JOptionPane.ERROR_MESSAGE);
					return;
				} finally {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

			}
			int result = JOptionPane.showOptionDialog(this, devicesUI, "Mixer",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
					null, null, null);
			if (result == JOptionPane.OK_OPTION) {
//				mixerManager.setSelectedPlaybackMixer(devicesUI
//						.getSelectedPlaybackMixerInfo());
//				mixerManager.setSelectedCaptureMixer(devicesUI
//						.getSelectedCaptureMixerInfo());
			    AJSDeviceInfo cdi=devicesUI.getSelectedCaptureDeviceInfo();
			    AJSDevice cDev=null;
			    if(cdi!=null){
			    	cDev=AJSAudioSystem.getResolvedCaptureDevice(cdi);
			    }
			    captureDeviceSelection.setDevice(cDev);
			    
			    AJSDeviceInfo pdi=devicesUI.getSelectedPlaybackDeviceInfo();
                AJSDevice pDev=null;
                if(pdi!=null){
                	pDev=AJSAudioSystem.getResolvedPlaybackDevice(pdi);
                }
                playbackDeviceSelection.setDevice(pDev);
			    
			}
		} else if (src == miAudioFormat) {
			// audioFormat = AudioFileFormatChooser.showDialog(this,
			// audioFormat);
			audioFormat = AudioFileFormatChooser.showDialog(this, audioFormat);
			capture.setAudioFileFormat(audioFormat);
			// audioFormat=audioFileFormat.getFormat();
			// audioFileFormatType=audioFileFormat.getType();
		} else if (src == miAudioOptions) {
		    audioOptions.setLineBufferSize(capture.getLineBufferSize());
           AudioOptionsPanel audioOptionsPanel=new AudioOptionsPanel(audioOptions,defaultPrimaryRecordTargetName);
           audioOptionsPanel.showDialog(this);
           PrimaryRecordTarget primaryRecordTarget =audioOptions.getPrimaryRecordTarget();
           if(primaryRecordTarget!=null){
               preferences.put("primaryRecordTarget", primaryRecordTarget.name());
           }else{
               preferences.remove("primaryRecordTarget");
           }
           
        }else if (src == miRestoreWindow) {
			preferences.putBoolean(PREF_WINDOW_RESTORE, miRestoreWindow
					.isSelected());
		} else if (src == miAbout) {
			if (aboutPanel == null) {
				aboutPanel = new JPanel(new GridLayout(2,1));
				aboutPanel.add(new JLabel(APPNAME + " version " + VERSION + " "
						+ COPYRIGHT));
				aboutPanel.add(new JLabel("ASIO Technology by Steinberg"));
			}

			JOptionPane.showMessageDialog(this, aboutPanel, rb
					.getString("about"), JOptionPane.INFORMATION_MESSAGE);
		} else if (src == miInfo) {
			infoPanel = new InfoViewer();
			infoPanel.setRecordingFile(recFile);
			JOptionPane.showMessageDialog(this, infoPanel, "Info",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (src == miQuit) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			doQuit();
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else if (ev.getActionCommand() == StartPlaybackAction.ACTION_COMMAND) {
			if (tp.isPaused()) {
				try {
					playback.open();
					playback.pause();
					// startPlaybackAction.setHighlighted(true);
				} catch (PlayerException e1) {
					JOptionPane.showMessageDialog(this, e1
							.getLocalizedMessage(), "Playback pause error",
							JOptionPane.ERROR_MESSAGE);

				}

			} else {
				if (ev instanceof StartPlaybackActionEvent) {
					StartPlaybackActionEvent pe = (StartPlaybackActionEvent) ev;

					playback.setStartFramePosition(pe.getStartFramePosition());
					playback.setStopFramePosition(pe.getStopFramePosition());
				}

				try {
					playback.open();
					playback.play();
				} catch (PlayerException e) {
					JOptionPane.showMessageDialog(this,
							e.getLocalizedMessage(), "Playback open error",
							JOptionPane.ERROR_MESSAGE);
				}

			}

		} else if (ev.getActionCommand() == StopAction.ACTION_COMMAND) {

			playback.stop();
			
			try {
				capture.stop();
			} catch (CaptureException e) {
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
						"Record stream error", JOptionPane.ERROR_MESSAGE);
			}
			if (tp.isPaused()) {
				tp.setPaused(false);
				pauseAction.setHighlighted(false);
			}
			stopAction.setEnabled(false);
			stopAction.setHighlighted(true);

		} else if (ev.getActionCommand() == PauseAction.ACTION_COMMAND) {
			boolean paused = playback.isPaused();
			boolean tpPaused = tp.isPaused();
			if (playback.isPlaying() && !paused) {

				playback.pause();

			} else if (paused && tpPaused) {
				playback.start();
			} else if (capture.isCapturing() && tpPaused) {
				// capture.startRecording();
				capture.setCaptureOnly(false);
				capture.start();

			} else {
				// toggle pause

				tp.setPaused(!tpPaused);
				pauseAction.setHighlighted(!tpPaused);
				stopAction.setEnabled(!tpPaused);
				stopAction.setHighlighted(tpPaused);

			}
		} else if (ev.getActionCommand() == SetFramePositionAction.ACTION_COMMAND) {
			try {
				playback.setFramePosition(((FramePositionActionEvent) ev)
						.getFramePosition());
			} catch (PlayerException e) {
				JOptionPane
						.showMessageDialog(this, e.getLocalizedMessage(),
								"Playback positioning error",
								JOptionPane.ERROR_MESSAGE);

			}
		} else if (ev.getActionCommand() == StartRecordAction.ACTION_COMMAND) {
			boolean tpPaused = tp.isPaused();

			if (tpPaused && !capture.isCapturing()) {
				startCaptureInterActive();

			} else {

				startRecordingInterActive();
			}
		} else if (ev.getActionCommand() == LoopAction.ACTION_COMMAND) {
			// Java 6.0
			// playback.setLooping(loopAction.getValue(Action.SELECTED_KEY));

			if (src instanceof ItemSelectable) {
				Object[] sel = ((ItemSelectable) src).getSelectedObjects();
				playback.setLooping(sel != null && sel.length > 0);
			}
		} else if (ev.getActionCommand() == LevelMeter.ACTION_PEAK_HOLD_RESET_CMD) {
			playback.resetPeakHold();
//			capture.resetPeakHold();
		} else if (src == miChannelsAll) {
			selectedChannel = -1;
			preparePlaybackSources();
		} else if (isChannelSelectMenuItem(src)) {

			for (int i = 0; i < miChannelSelects.length; i++) {
				if (src == miChannelSelects[i]) {
					doChannelSelect(i);

				}
			}
		}

	}

	private void doChannelSelect(int i) {
		try {
			playback.close();
		} catch (PlayerException e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Playback close error", JOptionPane.ERROR_MESSAGE);

			e.printStackTrace();
		}
		selectedChannel = i;
		preparePlaybackSources();
	}

	private boolean isChannelSelectMenuItem(Object src) {
		for (int i = 0; i < miChannelSelects.length; i++) {
			if (src == miChannelSelects[i])
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	private void doOpen() {

		File file = null;
		JFileChooser fch = new JFileChooser(lastOpenDirectory);
		AudioFileFilter audioFileFilter = new AudioFileFilter();
		fch.setFileFilter(audioFileFilter);
		// fch.setMultiSelectionEnabled(true);
		int returnVal = fch.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			file = fch.getSelectedFile();
		} else {
			return;
		}
		lastOpenDirectory = fch.getCurrentDirectory();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try {
			openFile(file);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Open error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		lm.setScaleEnabled(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
    
    
    private void doPrint(){
        boolean viewUpdating=viewUpdateRunning;
        stopViewUpdates();
        PrinterJob printJob = PrinterJob.getPrinterJob();
        ComponentPrinter cp=new ComponentPrinter(this);
        printJob.setPrintable(cp);
        if (printJob.printDialog()){
          try {
            printJob.print();
          } catch(PrinterException e) {
              JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
                    "Print error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
          }
        }
        if(viewUpdating)startViewUpdates();
    }

	private void doSave() {
		javax.sound.sampled.AudioFileFormat.Type aff = audioFormat.getType();
		// Type[] fileTypes = AudioSystem.getAudioFileTypes();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// actionOnSaved=REOPEN_ON_SAVE;
		if (overwrite || checkOverWrite(recFile))
			save(aff, recFile);
		miSave.setEnabled(false);
	}

	private void openFile(File file) throws UnsupportedAudioFileException,
			IOException, AudioSourceException {
		// AudioFormat af =
		// AudioSystem.getAudioFileFormat(files[0]).getFormat();
		// j2ac.setAudioFormat(af);
		// j2ac.setRecordingFiles(files);
		overwrite = false;
		miSave.setEnabled(false);
		recFile = file;
		capture.setRecordingFile(file);
		//startRecordAction.setEnabled(true);
		updateActions();
		audioSource = new ConvenienceFileAudioSource(file);

		// Check if we have a clipboard audio fragment to paste
		// TODO check compatible format
		Transferable clipBoardContents = clipBoard.getContents(this);

		if (clipBoardContents != null) {

			if (clipBoardContents
					.isDataFlavorSupported(AudioSource.DATA_FLAVOR)) {
//				miPaste.setEnabled(true);
				pasteAction.setEnabled(true);
//				miAppend.setEnabled(true);
				appendAction.setEnabled(true);
			}
		}
		// playSource = new FileAudioSource(file);
		editSource = new PluginChain(audioSource);
		try {
			editSource.add(new EncodingPlugin(AudioFormat.Encoding.PCM_SIGNED));
		} catch (AudioFormatNotSupportedException e1) {
			JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(),
					"Format unsupported error", JOptionPane.ERROR_MESSAGE);

		}
		// Undo cannot remove this first plugins
		editableOffset = editSource.size();
		openEditSource();
		preparePlaybackSources();
		setTitle(file.getName() + " - " + title);
	}

	private void openURL(URL url) throws UnsupportedAudioFileException,
			IOException, AudioSourceException, URISyntaxException {
		if (url.getProtocol().equals("file")) {
			File urlFile = new File(url.toURI().getPath());
			openFile(urlFile);
		}
		audioSource = new URLAudioSource(url);
		preparePlaybackSources();
		setTitle(url + " - " + title);
	}

	private void preparePlaybackSources() {

		 try {
	            playback.close();
	        } catch (PlayerException e) {
	        	JOptionPane
				.showMessageDialog(
						this,
						"Could not close playback engine.",
						"Playback engine close warning",
						JOptionPane.WARNING_MESSAGE);
	        }
		int srcChannels = -1;
		try {
			srcChannels = editSource.getFormat().getChannels();
		} catch (AudioSourceException e1) {
			playbackSource=null;
			JOptionPane.showMessageDialog(
					this,
					"Could not select playback channel.",
					"Playback error",
					JOptionPane.ERROR_MESSAGE);
			return;
			
		}
		if (selectedChannel != -1) {

			playbackSource = new PluginChain(editSource);
			try {
				playbackSource.add(new ChannelSelectorPlugin(selectedChannel));
			} catch (AudioFormatNotSupportedException e) {
				playbackSource=null;
				JOptionPane.showMessageDialog(
						this,
						"Could not select playback channel.",
						"Playback error",
						JOptionPane.ERROR_MESSAGE);
				
				return;
			}

		} else {

			playbackSource = editSource;
			miChannelsAll.setSelected(true);
		}

		try {
//			playback.close();
			playback.setAudioSource(playbackSource);
			playback.open();
		} catch (PlayerException pe) {
			if (srcChannels > 2) {
				// OK this is a multichannel file
				// try to open with selected single channel
				JOptionPane
						.showMessageDialog(
								this,
								"The playback engine cannot play "
										+ srcChannels
										+ " channels simultanously.\nThe first channel will be selected for playback.",
								"Playback engine",
								JOptionPane.INFORMATION_MESSAGE);
				selectedChannel = 0;
				miChannelSelects[selectedChannel].setSelected(true);

				playbackSource = new PluginChain(editSource);
				try {
					playbackSource.add(new ChannelSelectorPlugin(
							selectedChannel));

					playback.close();
					playback.setAudioSource(playbackSource);
					playback.open();

				} catch (AudioFormatNotSupportedException e) {
					playbackSource=null;
					JOptionPane.showMessageDialog(this,
							e.getLocalizedMessage(), "Playback open error",
							JOptionPane.ERROR_MESSAGE);
					return;
					
				} catch (PlayerException e) {
					playbackSource=null;
					JOptionPane.showMessageDialog(this,
							e.getLocalizedMessage(), "Playback open error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

			} else {
				playbackSource=null;
				JOptionPane.showMessageDialog(this, pe.getLocalizedMessage(),
						"Playback open error", JOptionPane.ERROR_MESSAGE);
				
				return;
			}

		}
		try {

			AudioFormat af = playback.getAudioFormat();
			long frameLength = editSource.getFrameLength();
			lm.setAudioFormat(af);
			tp.setFrameLength(playback.getFrameLength());
			tp.setFrameRate(af.getFrameRate());
			statusBar.setAudioFormat(af);
			statusBar.setFrameLength(frameLength);

//			playback.close();
//		} catch (PlayerException e) {
//			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
//					"Playback open error", JOptionPane.ERROR_MESSAGE);
		} catch (AudioSourceException e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Audio source error", JOptionPane.ERROR_MESSAGE);
		}
		
//		startPlaybackAction.setEnabled(true);
		updateActions();
		stopAction.setHighlighted(true);
		pauseAction.setEnabled(true);
		startViewUpdates();
	}

	private void openEditSource() {

		audioSample.setAudioSource(editSource);

		int srcChannels = -1;
		try {
			srcChannels = editSource.getFormat().getChannels();
		} catch (AudioSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		miChannelSelector.removeAll();
		ButtonGroup bg = new ButtonGroup();
		miChannelSelects = new JRadioButtonMenuItem[srcChannels];
		for (int i = 0; i < srcChannels; i++) {
			JRadioButtonMenuItem chItem = new JRadioButtonMenuItem("Channel "
					+ i);
			bg.add(chItem);
			chItem.addActionListener(this);
			miChannelSelects[i] = chItem;
			miChannelSelector.add(chItem);
			if (i == selectedChannel)
				chItem.setSelected(true);
		}
		miChannelSelector.add(miChannelsAll);
		bg.add(miChannelsAll);

//		miUndo.setEnabled(editSource.size() > editableOffset);
		undoAction.setEnabled(editSource.size() > editableOffset);
		// setFramePositionAction.setEnabled(true);
		miAudioFormat.setEnabled(false);

		miSaveAs.setEnabled(true);
		miClose.setEnabled(true);

		miOpen.setEnabled(false);
        miPrint.setEnabled(true);
		miNew.setEnabled(false);
		miGoto.setEnabled(true);
		// tp.setStatus(TransportUI.STOP);

		// startViewUpdates();
	}

	public void close() {
	    closing=true;
	    try {
            capture.close();
        } catch (CaptureException e) {
        	JOptionPane
			.showMessageDialog(
					this,
					"Could not close capture engine.",
					"Capture engine close warning",
					JOptionPane.WARNING_MESSAGE);
        }
	    try {
            playback.close();
        } catch (PlayerException e) {
        	JOptionPane
			.showMessageDialog(
					this,
					"Could not close playback engine.",
					"Playback engine close warning",
					JOptionPane.WARNING_MESSAGE);
        }
        playbackSource=null;
		// lm.stop();
		asc.close();
		selectedChannel = -1;
		overwrite = false;
		stopViewUpdates();
		lm.resetPeakHold();
		// tp.setStatus(TransportUI.CLOSE);
		stopAction.setEnabled(false);
		pauseAction.setEnabled(false);
//		startPlaybackAction.setEnabled(false);
		setFramePositionAction.setEnabled(false);
		
//		startRecordAction.setEnabled(false);
		recFile = null;
		saved = true;
		// audioSamples = new AudioClip[0];
		// arr.setAudioSamples(audioSamples);
		updateActions();
		
		audioSample.setAudioSource(null);
		lm.abandonDecay();
		lm.setLevels(null);
		lm.setScaleEnabled(false);
		statusBar.setAudioFormat(null);
		statusBar.setFrameLength(ThreadSafeAudioSystem.NOT_SPECIFIED);

		// j2ac.setRecordingFiles(files);
		// j2ac.setPlaybackFiles(files);
		// j2ac.setPlaybackAudioSources(null);
		miClose.setEnabled(false);
//		miCut.setEnabled(false);
		cutAction.setEnabled(false);
//		miCopy.setEnabled(false);
		copyAction.setEnabled(false);
//		miPaste.setEnabled(false);
		pasteAction.setEnabled(false);
//		miAppend.setEnabled(false);
		appendAction.setEnabled(false);
		miSave.setEnabled(false);
		miSaveAs.setEnabled(false);
		miOpen.setEnabled(true);
		miNew.setEnabled(true);
		miAudioFormat.setEnabled(true);
		miGoto.setEnabled(false);
		setTitle(title);
		closing=false;
	}

	private void save(AudioFileFormat.Type aff, File file) {
		miSave.setEnabled(false);
		miSaveAs.setEnabled(false);
		miClose.setEnabled(false);
		
		AudioInputStream ais = null;
		try {

			ais = editSource.getAudioInputStream();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Check if the the file is used by clipboard content
		
		
		if (clipBoard.isDataFlavorAvailable(PluginChain.DATA_FLAVOR)) {
			Transferable clipBoardContents = clipBoard.getContents(this);
				try {
					PluginChain clipBoardPluginChain = (PluginChain) clipBoardContents
							.getTransferData(PluginChain.DATA_FLAVOR);
					File[] filesInUseByClipboard = clipBoardPluginChain
							.getUsedAudioFiles();
					for (File fileInUseByClipboard : filesInUseByClipboard) {
						if (file.equals(fileInUseByClipboard)) {
							// invalidate content
							clipBoardPluginChain.setValid(false);
							
							// is there a way to clear the clipboard content ???
							// setting to null results in an exception !
							clipBoard.setContents(new EmptyTransferable(), this);
							updatePaste();
							//System.out.println("Clipboard content invalidated.");
						}
					}

				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		// We need temp files if the source files are the same as targets
		// TODO Check source=target files
		// for complex editings this is not so easy , we have to maintain a list
		// of all involved audio files
		// to prevent overwriting source files
		
		
		
		boolean fileInUse=false;
		File[] usedAudioFiles=editSource.getUsedAudioFiles();
		for(File uf:usedAudioFiles){
			if(file.equals(uf)){
				fileInUse=true;
			}
		}
		
		if(fileInUse){
			
		}
		//recFile = file;
		
		// close();
		// try {
		// AudioSystem.write(ais, aff, file);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		
		afw = new AudioFileWriter(this, ais, aff, file, true);
		progressDialog=new JProgressDialogPanel(afw,"Write audio file","Writing...");
		try {
			afw.open();
		} catch (WorkerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		afw.start();
		
		Object val=progressDialog.showDialog(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
	 */
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		if (DEBUG)
			System.out.println("Event: " + arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.ui.AudioSignalUIListener#selected(ipsk.audio.ui.Selection)
	 */
	public void selected(Selection selection) {
		// Do nothing
	}

	public static class WindowStateRunnable implements Runnable{
		private JFrame frame;
		private int windowState;
		public WindowStateRunnable(JFrame frame,int windowState){
			this.frame=frame;
			this.windowState=windowState;
		}
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			frame.setExtendedState(windowState);
		}
		
		
	}

	public static class StartupRunnable implements Runnable{
		private String audioURL=null;
		private String s0 = null;
		private String s1 = null;
		private String xZoom=null;
		
		public void setAudioURL(String audioURL) {
			this.audioURL = audioURL;
		}
		public void setS0(String s0) {
			this.s0 = s0;
		}
		public void setS1(String s1) {
			this.s1 = s1;
		}
		public void setXZoom(String xZoom){
			this.xZoom=xZoom;
		}
		public void run() {
			demo = new AudioEditorUI();
			Preferences preferences = Preferences.userNodeForPackage(demo
					.getClass());
			int prefWindowState=preferences.getInt(PREF_WINDOW_STATE, 0);
			demo.pack();
			
			// Problem with minimum size of menu bar
			// 
			Dimension minSize=demo.getMinimumSize();
			Dimension mbMinSize=demo.getJMenuBar().getMinimumSize();
		    Dimension mbPrefSize=demo.getJMenuBar().getPreferredSize();
		    demo.setMinimumSize(new Dimension(minSize.width,minSize.height+(mbPrefSize.height-mbMinSize.height)));
			//demo.setMinimumSize(demo.getMinimumSize());
			demo.setVisible(true);
//          Because of Java Bug ID 4464714
            // we have to set the Window state here instead of in the
            // constructor
            // Update: Even at this time the setting only works with Windows
            // Using invokeLater works with Linux too
            if(demo.isRestoringWindowState()){
//              int prefWindowState=preferences.getInt(PREF_WINDOW_STATE, 0);
//              demo.setExtendedState(prefWindowState);
                WindowStateRunnable wsr=new WindowStateRunnable(demo, prefWindowState);
                SwingUtilities.invokeLater(wsr);
            }
            try {
				demo.init();
			} catch (PlayerException e2) {
				
				e2.printStackTrace();
				JOptionPane.showMessageDialog(demo, e2.getMessage(),
						"Playback default device error", JOptionPane.ERROR_MESSAGE);
				System.err.println(e2.getMessage());
			}
			if (audioURL == null) {
				demo.close();
			} else {
				try {
					URL url = new URL(audioURL);
					try {
						demo.openURL(url);
					} catch (Exception e) {

						System.err.println(e.getLocalizedMessage());
						System.exit(-1);
					}
				} catch (MalformedURLException e1) {
					// OK no URL, try file now
					try {

						demo.openFile(new File(audioURL));
					} catch (Exception e) {
						JOptionPane.showMessageDialog(demo, e.getMessage(),
								"File open error", JOptionPane.ERROR_MESSAGE);
						System.err.println(e.getMessage());
						e.printStackTrace();
						System.exit(-1);
					}
				}
			}		
			if (s0 != null && s1 != null) {

				AudioClip clip = demo.getAudioSample();
				FrameUnitParser fup = null;
				try {
					fup = new FrameUnitParser(clip);
				} catch (AudioSourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long s0l = fup.parseFrameUnitString(s0);
				long s1l = fup.parseFrameUnitString(s1);
				clip.setSelection(new Selection(s0l, s1l));
			}
			if (xZoom != null) {
				AudioClipUIContainer asc = demo.getAsc();
				asc.setFixXZoomFitToPanel(false);
				asc.setXZoom(Double.parseDouble(xZoom));
			}
		}
	}
	public static void main(String[] args) {
		// thread safe packing
		StartupRunnable startupRunnable=new StartupRunnable();
		String[] params;
		String audioURL=null;
		OptionParser op = new OptionParser();
		op.addOption("open", null);
		op.addOption("s0", null);
		op.addOption("s1", null);
		op.addOption("xzoom", null);
		try {
			op.parse(args);
		} catch (OptionParserException e) {
			System.err.println(e.getLocalizedMessage());
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
					"ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		Option[] options = op.getOptions();
		for (int i = 0; i < options.length; i++) {
			if (options[i].getOptionName().equals("open")) {
				audioURL=options[i].getParam();
			}else if (options[i].getOptionName().equals("s0")) {
				startupRunnable.setS0(options[i].getParam());
			} else if (options[i].getOptionName().equals("s1")) {
				startupRunnable.setS1(options[i].getParam());
			} else if (options[i].getOptionName().equals("xzoom")) {
				startupRunnable.setXZoom(options[i].getParam());
			}
		}
		params = op.getParams();

	 if (params.length <= 1) {
			if (params.length == 1) {
				audioURL = params[0];
			}
			startupRunnable.setAudioURL(audioURL);
			// syntax OK now startup in AWTEventThread
			javax.swing.SwingUtilities.invokeLater(startupRunnable);
		} else {
			System.err.println("Usage: java " + demo.getClass().getName()
					+ " [audioFile]");
			System.exit(-1);
		}
	}

	

	private int showSaveDialog() {
		return JOptionPane.showConfirmDialog(this, recFile.getName()
				+ " is not saved. Do you want to save ?", "Save file ?",
				JOptionPane.YES_NO_CANCEL_OPTION);
	}

	private void doClose() {
		if (!saved) {
			int save = showSaveDialog();
			if (save == JOptionPane.YES_OPTION) {
				actionOnSaved = CLOSE_ON_SAVE;
				doSave();
			} else if (save == JOptionPane.CANCEL_OPTION) {
				return;
			} else {
				close();
			}
		} else {
			close();
		}
	}

	/**
	 * 
	 */
	private void doQuit() {
		if (!saved) {
			int save = showSaveDialog();
			if (save == JOptionPane.YES_OPTION) {
				actionOnSaved = EXIT_ON_SAVE;
				doSave();
			} else if (save == JOptionPane.CANCEL_OPTION) {
				return;
			} else {
				exit();
			}
		} else {
			exit();
		}

	}

	public void exit() {
		close();
		System.exit(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard,
	 *      java.awt.datatransfer.Transferable)
	 */
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
	    // TODO If KDE klipper is active we loose the ownership on cut/copy actions
	    // and the paste action is never enabled
//	    System.out.println("Lost ownership");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent arg0) {
	   Object src = arg0.getSource();
        int x = arg0.getComponent().getX();
        int y = arg0.getComponent().getY();
        // System.out.println("Moved: "+x+" "+y);
        int state = getExtendedState();
        // Rectangle maxBounds=getMaximizedBounds();
        // if (maxBounds!=null){
        // System.out.println(maxBounds);
        // }
        if (arg0 instanceof WindowEvent) {
            WindowEvent we = (WindowEvent) arg0;
            if (we.getNewState() != state) {
                System.out.println("State differs !!");
            }
        }
        if (src == this && state == JFrame.NORMAL) {
            preferences.putInt(PREF_WINDOW_POS_X, x);
            preferences.putInt(PREF_WINDOW_POS_Y, y);
            if (x == -4 && y == -4) {
                System.out.println("Pref. xy set: " + x + " " + y + " state: "
                        + state);
            }
        }
	}

	// Workaround for JRE Bug-ID 4320050

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void componentResized(ComponentEvent ce) {
		Object src = ce.getSource();
		if (src == this && this.getExtendedState() == JFrame.NORMAL) {
			Dimension d = ce.getComponent().getSize();

			double width = d.getWidth();
			double height = d.getHeight();
			Dimension minD = content.getMinimumSize();
			double minWidth = minD.getWidth();
			double minHeight = minD.getHeight();
			if (minWidth > width || minHeight > height) {
				double newWidth = width;
				if (minWidth > width)
					newWidth = minWidth;
				double newHeight = height;
				if (minHeight > height)
					newHeight = minHeight;
				setSize((int) newWidth, (int) newHeight);

			}
			//System.out.println("Frame resized");
			int awidth = getWidth();
			int aheight = getHeight();
			//System.out.println("Resized to prefs: "
			//		+ new Dimension(awidth, aheight));
			preferences.putInt(PREF_WINDOW_WIDTH, awidth);
			preferences.putInt(PREF_WINDOW_HEIGHT, aheight);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent arg0) {
	}

	private boolean checkOverWrite(File file) {
		if (!file.exists())
			return true;

		if (JOptionPane.showConfirmDialog(this, file.getName()
				+ " exists. Do you want to overwrite ?", "Overwrite file ?",
				JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
			return false;

		return true;

	}

	public void startCaptureInterActive() {
		File recFile = capture.getRecordingFile();
		if (overwrite || checkOverWrite(recFile)) {
			audioSample.setAudioSource(null);
			statusBar.setFrameLength(ThreadSafeAudioSystem.NOT_SPECIFIED);
			capture.setAudioFileFormat(audioFormat);
//			if(PrimaryRecordTarget.TEMP_RAW_FILE.equals(audioOptions.getNNPrimaryRecordTarget())){
//	            capture.setUseTempFile(true);
//	        }
			capture.setPrimaryRecordTarget(audioOptions.getNNPrimaryRecordTarget());
			try {
				capture.open();
//				capture.resetPeakHold();
//				lm.setLevelInfos(capture.getLevelInfos(),fastLevelMeasuring);
				lm.setAudioFormat(audioFormat.getFormat());
			} catch (CaptureException e) {
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
						"Audio capture open error", JOptionPane.ERROR_MESSAGE);
			}
			// capture.startCapturing();
			capture.setCaptureOnly(true);
			capture.start();
			startRecordAction.setHighlighted(true);
		}
	}

	public void startRecordingInterActive() {
	   
		File recFile = capture.getRecordingFile();
		if (!overwrite && recFile.exists()) {
			if (JOptionPane.showConfirmDialog(this, recFile.getName()
					+ " exists. Do you want to overwrite ?",
					"Overwrite file ?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
		}
		try {
			playback.close();
		} catch (PlayerException e1) {
			e1.printStackTrace(System.err);
			JOptionPane.showMessageDialog(this,"Could not close player before recording: "+e1.getLocalizedMessage());
		}
		audioSample.setAudioSource(null);
		statusBar.setFrameLength(ThreadSafeAudioSystem.NOT_SPECIFIED);
		capture.setAudioFileFormat(audioFormat);
		capture.setPrimaryRecordTarget(audioOptions.getNNPrimaryRecordTarget());
		try {
			capture.open();
//			capture.resetPeakHold();
//			lm.setLevelInfos(capture.getLevelInfos(),fastLevelMeasuring);
		} catch (CaptureException e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Capture open error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// capture.startRecording();
		capture.setCaptureOnly(false);
		lm.setAudioFormat(audioFormat.getFormat());
		if(fastLevelMeasuring){
		    lm.setLevelInfosBean(captureLevelMeasureStream.getLevelInfosBean());
		}
		capture.start();
		startRecordAction.setHighlighted(true);
	}

	private void doNew() {
		File file = null;
		JFileChooser fch = new JFileChooser(lastSaveDirectory);
		fch.setFileFilter(new AudioFileFilter());
		
		// fch.setMultiSelectionEnabled(true);
		int returnVal = fch.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			// files = fch.getSelectedFiles();

			file = fch.getSelectedFile();
		} else {
			return;
		}
		lastSaveDirectory = fch.getCurrentDirectory();
		if (file.exists()) {
			if (JOptionPane.showConfirmDialog(this, file.getName()
					+ " exists. Do you want to overwrite ?",
					"Overwrite file ?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
		}
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// Create new empty audio file
		ByteArrayInputStream zeroBis = new ByteArrayInputStream(new byte[0]);
		AudioInputStream zeroAis = new AudioInputStream(zeroBis, audioFormat
				.getFormat(), 0L);

		try {
			ThreadSafeAudioSystem.write(zeroAis, audioFormat.getType(), file);
			// on success we set the overwrite flag because this is a new file
			overwrite = true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"New error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} finally {
			try {
				zeroBis.close();
				zeroAis.close();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(),
						"New error", JOptionPane.ERROR_MESSAGE);
			}

			setTitle(file.getName() + " - " + APPNAME);
			try {
				openFile(file);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(),
						"New open error", JOptionPane.ERROR_MESSAGE);
			}
		}

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	protected void playbackFinished(){ 
		if(!closing){
			playback.setStartFramePosition(0);
			playback.setStopFramePosition(ThreadSafeAudioSystem.NOT_SPECIFIED);
			setFramePositionAction.setEnabled(false);

			pauseAction.setHighlighted(false);
			stopAction.setEnabled(false);
			stopAction.setHighlighted(true);
			//        startRecordAction.setEnabled(true);
			updateActions();
			//        startPlaybackAction.setEnabled(true);
			pauseAction.setEnabled(true);
		}
		tp.setPaused(false);
		startPlaybackAction.setHighlighted(false);
		updatePlaybackView();
	}

	public void update(PlayerEvent playerEvent) {
		if (DEBUG)
			System.out.println("Playback: " + playerEvent);

		if (playerEvent instanceof PlayerStartEvent) {
			tp.setPaused(false);
			// tp.setStatus(TransportUI.PLAY);
			startPlaybackAction.setEnabled(false);
			startPlaybackAction.setHighlighted(true);
			stopAction.setEnabled(true);
			stopAction.setHighlighted(false);
			pauseAction.setEnabled(true);
			pauseAction.setHighlighted(false);
			startRecordAction.setEnabled(false);
			setFramePositionAction.setEnabled(true);
		} else if (playerEvent instanceof PlayerStopEvent) {
			if (playerEvent instanceof PlayerPauseEvent) {
				tp.setPaused(true);
				pauseAction.setHighlighted(true);
				startPlaybackAction.setHighlighted(true);
				stopAction.setEnabled(true);
				stopAction.setHighlighted(false);
				startRecordAction.setEnabled(false);
				startPlaybackAction.setEnabled(true);
				pauseAction.setEnabled(true);
				updatePlaybackView();
			} else {
				// tp.setPaused(false);
				// pauseAction.setHighlighted(false);
				// startPlaybackAction.setHighlighted(false);
				// stopAction.setEnabled(false);
				// stopAction.setHighlighted(true);
				// startRecordAction.setEnabled(true);
				//
				// startPlaybackAction.setEnabled(true);
				// pauseAction.setEnabled(true);

				if(keepPlayerOpen){
					playbackFinished();
				}else{
					try {
						playback.close();
					} catch (PlayerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// playback.setStartFramePosition(0);
				// updatePlaybackView();
				// playback.setStopFramePosition(AudioSystem.NOT_SPECIFIED);
			}

		} else if (playerEvent instanceof PlayerOpenEvent) {
			setFramePositionAction.setEnabled(true);
		} else if (playerEvent instanceof PlayerCloseEvent) {
		   if(!keepPlayerOpen){
			   playbackFinished();
		   }


		} else if (playerEvent instanceof PlayerErrorEvent) {
			System.err.println(playerEvent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.mixer.MixerManagerListener#selectedPlaybackMixerChanged(java.lang.Object,
	 *      javax.sound.sampled.Mixer)
	 */
//	public void selectedPlaybackMixerChanged(Object src, Mixer newPlaybackMixer) {
	
	 /* (non-Javadoc)
     * @see ipsk.audio.ajs.DeviceSelectionListener#deviceChanged(ipsk.audio.ajs.AJSDevice, ipsk.audio.ajs.AJSDevice)
     */
    public void deviceChanged(Object src,AJSDevice oldDevice, AJSDevice newDevice) {
      if(src==playbackDeviceSelection){
		playback.stop();
		try {
			playback.close();
			
			Mixer newPlaybackMixer=null;
			if(newDevice!=null){
				newPlaybackMixer=newDevice.getMixer();
			}
			playback.setMixer(newPlaybackMixer);
			
		} catch (PlayerException e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Error setting playback device", JOptionPane.ERROR_MESSAGE);
		}
		
	}else if(src==captureDeviceSelection){
		try {
			capture.stop();
			capture.close();
		} catch (CaptureException e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Error stopping/closing capture engine",
					JOptionPane.ERROR_MESSAGE);
		} finally {
			Mixer newCaptureMixer=null;
			if(newDevice!=null){
				newCaptureMixer=newDevice.getMixer();
			}
			capture.setMixer(newCaptureMixer);
		   
		}
	}
      updateActions();
	}
    
    private void updateActions(){
    	boolean captureEnabled=(recFile!=null)&&(captureDeviceSelection.getDevice()!=null);
    	startRecordAction.setEnabled(captureEnabled);
    	boolean pbEnabled=(playbackSource!=null)&&(playbackDeviceSelection.getDevice()!=null);
    	startPlaybackAction.setEnabled(pbEnabled);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.capture.CaptureListener#update(ipsk.audio.capture.CaptureEvent)
	 */
	public void update(CaptureEvent captureEvent) {

		if (DEBUG)
			System.out.println("Capture: " + captureEvent);
		if (captureEvent instanceof CaptureStartEvent) {

			startRecordAction.setHighlighted(true);
			stopAction.setHighlighted(false);
			stopAction.setEnabled(true);
			startPlaybackAction.setEnabled(false);
			setFramePositionAction.setEnabled(false);
			if (captureEvent instanceof CaptureStartRecordEvent) {
				tp.setPaused(false);
				pauseAction.setEnabled(false);
				pauseAction.setHighlighted(false);
				startRecordAction.setEnabled(false);
			} else if (captureEvent instanceof CaptureStartCaptureEvent) {
				tp.setPaused(true);
				pauseAction.setEnabled(true);
				pauseAction.setHighlighted(true);
				startRecordAction.setEnabled(true);
			}
		} else if (captureEvent instanceof CaptureStopEvent) {
			// tp.setPaused(false);
			// startRecordAction.setEnabled(true);
			// startRecordAction.setHighlighted(false);
			// stopAction.setHighlighted(false);
			// stopAction.setEnabled(true);
			// pauseAction.setEnabled(true);
			// startPlaybackAction.setEnabled(true);
			// pauseAction.setHighlighted(false);
			// setFramePositionAction.setEnabled(false);
			try {
				capture.close();
				tp.setFramePosition(capture.getFramePosition());
			} catch (CaptureException e) {

				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
						"Error on record close", JOptionPane.ERROR_MESSAGE);
			}
			
			if (captureEvent instanceof CaptureRecordedEvent) {
				overwrite = false;
//				float[] peakLevelHold=capture.getPeakLevelHold();
//				if(peakLevelHold!=null){
//				    playback.setPeakLevelHold(peakLevelHold);
//				}
			}
			
		} else if (captureEvent instanceof CaptureCloseEvent) {
			tp.setPaused(false);
			boolean recFileLoaded = (recFile != null);
//			startRecordAction.setEnabled(recFileLoaded);
			startRecordAction.setHighlighted(false);
			stopAction.setHighlighted(false);
			stopAction.setEnabled(false);
			pauseAction.setEnabled(recFileLoaded);
//			startPlaybackAction.setEnabled(recFileLoaded);
			updateActions();
			pauseAction.setHighlighted(false);
			setFramePositionAction.setEnabled(false);
			try {
				openFile(capture.getRecordingFile());
			} catch (Exception e) {

					JOptionPane.showMessageDialog(this,
							e.getLocalizedMessage(),
							"Error opening recorded file",
							JOptionPane.ERROR_MESSAGE);
			}
		} else if (captureEvent instanceof CaptureErrorEvent) {
			String errMsg = ((CaptureErrorEvent) captureEvent).getCause()
					.getLocalizedMessage();
			if (errMsg == null)
				errMsg = "Unknown cause";
			System.err.println("Capture error: " + errMsg);

			JOptionPane.showMessageDialog(this, errMsg,
					"Capture (recording) error", JOptionPane.ERROR_MESSAGE);
			try {
				capture.close();
			} catch (CaptureException e) {
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
						"Capture close error", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	private void processAudioFileWriterevent(AudioFileWriterEvent event){
		if (event instanceof AudioFileWriterWrittenEvent) {
			AudioFileWriterWrittenEvent we=(AudioFileWriterWrittenEvent)event;
			saved = true;
			if (actionOnSaved == REOPEN_ON_SAVE) {
				try {
					openFile(we.getOutFile());
				} catch (Exception e) {

					JOptionPane.showMessageDialog(this,
							"Cannot open saved file:\n"
									+ e.getLocalizedMessage(), "Open error",
							JOptionPane.ERROR_MESSAGE);
				}
			} else if (actionOnSaved == CLOSE_ON_SAVE) {
				close();
			} else if (actionOnSaved == EXIT_ON_SAVE) {
				exit();
			}
		} else if (event instanceof AudioFileWriterErrorEvent) {
			Exception e = ((AudioFileWriterErrorEvent) event).getCause();
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Saving error", JOptionPane.ERROR_MESSAGE);

			miSave.setEnabled(!saved);
			miSaveAs.setEnabled(true);
		} else if (event instanceof AudioFileWriterCancelledEvent) {
			JOptionPane.showMessageDialog(this, "Saving cancelled", "Saving",
					JOptionPane.INFORMATION_MESSAGE);

			miSave.setEnabled(!saved);
			miSaveAs.setEnabled(true);
			miClose.setEnabled(true);
		}
		
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.io.AudioFileWriterListener#update(ipsk.audio.io.event.AudioFileWriterEvent)
	 */
	public void update(AudioFileWriterEvent event) {
		if (DEBUG)
			System.out.println(event);
		   if (java.awt.EventQueue.isDispatchThread()) {
	          processAudioFileWriterevent(event);
	        } else {
	            try {
	                java.awt.EventQueue.invokeAndWait(new AudioFilewriterEventProcessor(event));
	            } catch (InterruptedException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } catch (InvocationTargetException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.arr.AudioClipListener#sourceChanged(java.lang.Object,
	 *      ipsk.audio.AudioSource)
	 */
	public void audioClipChanged(AudioClipChangedEvent event) {
		if (event instanceof FramePositionChangedEvent) {
			if (!viewUpdate) {
				long newFramePosition = ((FramePositionChangedEvent) event)
						.getPosition();
				try {
					playback.setFramePosition(newFramePosition);
					tp.setFramePosition(newFramePosition);
				} catch (PlayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (event instanceof SelectionChangedEvent) {
			SelectionChangedEvent selEvent = (SelectionChangedEvent) event;
			Selection selection = selEvent.getSelection();

			if (selection == null) {

//				miCut.setEnabled(false);
				 cutAction.setEnabled(false);
//				miCopy.setEnabled(false);
				copyAction.setEnabled(false);
//				miSelectNone.setEnabled(false);
				cancelSelectionAction.setEnabled(false);
				playback.setStartFramePosition(0);
				playback.setStopFramePosition(AudioSystem.NOT_SPECIFIED);
			} else {

				//miCut.setEnabled(true);
			    cutAction.setEnabled(true);
//				miCopy.setEnabled(true);
				copyAction.setEnabled(true);
//				miSelectNone.setEnabled(true);
				cancelSelectionAction.setEnabled(true);
				playback.setStartFramePosition(selection.getLeft());
				playback.setStopFramePosition(selection.getRight());
			}
		} else if (event instanceof AudioSourceChangedEvent) {
			AudioSourceChangedEvent ev = (AudioSourceChangedEvent) event;
//			miCut.setEnabled(false);
			 cutAction.setEnabled(false);
//			miCopy.setEnabled(false);
			copyAction.setEnabled(false);
//			miSelectNone.setEnabled(false);
			cancelSelectionAction.setEnabled(false);
			if (ev.getAudioSource() == null) {
//				miSelectAll.setEnabled(false);
				selectAllAction.setEnabled(false);
			} else {
//				miSelectAll.setEnabled(true);
				selectAllAction.setEnabled(true);
			}
		}

	}

	public AudioClip getAudioSample() {
		return audioSample;
	}

	public void setAudioSample(AudioClip audioSample) {
		this.audioSample = audioSample;
	}

	public AudioClipUIContainer getAsc() {
		return asc;
	}

	public void setAsc(AudioClipUIContainer asc) {
		this.asc = asc;
	}

	private void updatePaste(){
		boolean dataAvail=(clipBoard.isDataFlavorAvailable(PluginChain.DATA_FLAVOR));
//		miPaste.setEnabled(dataAvail);
		pasteAction.setEnabled(dataAvail);
//		miAppend.setEnabled(dataAvail);
		appendAction.setEnabled(dataAvail);
	}
	
	public void flavorsChanged(FlavorEvent arg0) {
		updatePaste();
	}
	private String windowStateToString(int state) {
		if (state == JFrame.NORMAL) {
			return ("normal");
		} else {
			StringBuffer stateStr = new StringBuffer();
			if ((state & JFrame.ICONIFIED) > 0) {
				stateStr.append("iconified ");
			}
			if ((state & JFrame.MAXIMIZED_BOTH) > 0) {
				stateStr.append("maximized ");
			}
			return stateStr.toString();
		}
	}

	public void windowStateChanged(WindowEvent arg0) {
		preferences.putInt(PREF_WINDOW_STATE, arg0.getNewState());

//		System.out.println("State: " + windowStateToString(arg0.getOldState())
//				+ " -> " + windowStateToString(arg0.getNewState()));
	}

	public void windowActivated(WindowEvent arg0) {

	}

	public void windowClosed(WindowEvent arg0) {

	}

	public void windowClosing(WindowEvent arg0) {
		doQuit();

	}

	public void windowDeactivated(WindowEvent arg0) {
		// Nothing

	}

	public void windowDeiconified(WindowEvent arg0) {
		// Nothing

	}

	public void windowIconified(WindowEvent arg0) {
		// Nothing

	}

	public void windowOpened(WindowEvent arg0) {
		// Nothing

	}
	
	   private File checkForSingleAudiofile(java.util.List<File> fileList){
	        // we only accept a single file
	        if(fileList.size()==1){
	            // now check if file type supported
	            File f=fileList.get(0);
	            AudioInputStream ais=null;
	            try {
	                ais=AudioSystem.getAudioInputStream(f);
	            } catch (UnsupportedAudioFileException e) {
	                return null;
	            } catch (IOException e) {
	                return null;
	            }finally{
	                if (ais!=null){
	                    try {
	                        ais.close();
	                    } catch (IOException e) {
	                        return null;
	                    }
	                }
	            }
	            
	            return f;
	            
	        }
	        return null;
	    }
	   
	   private boolean checkForSingleAudiofileFlavour(Transferable tr){
	        DataFlavor[] dfs=tr.getTransferDataFlavors();

	        for (DataFlavor df:dfs){
	            // does not work for Linux ...
	            if(df.isFlavorJavaFileListType()){
	                return true;
	            }else if(df.isFlavorTextType()){
	                //... this is the Linux workaround
	                String mimeType=df.getMimeType();
	                String[] mimeElements=StringTokenizer.split(mimeType,';', true);
	                if(mimeElements.length>0 && "text/uri-list".equals(mimeElements[0])){
	                    
	                    try {
//	                      Object uriList=tr.getTransferData(df);
	                        Reader r=df.getReaderForText(tr);
//	                      System.out.println("Reader: "+r.toString());
	                        LineNumberReader lnr=new LineNumberReader(r);
	                        ArrayList<String> lines=new ArrayList<String>();
	                        String line=null;
	                        while((line=lnr.readLine())!=null){
	                            lines.add(line);
//	                          System.out.println("Line: "+line);
	                        }
//	                      System.out.println("Lines: "+lines.size());
	                        if(lines.size()==1){
	                            return true;
	                        }
	                    } catch (UnsupportedFlavorException e) {
//	                      e.printStackTrace();
	                        continue;
	                    } catch (IOException e) {
//	                      e.printStackTrace();
	                        continue;
	                    }
	                }
	            }
	        }
	        return false;
	    }
	   
	   
	    private File checkForSingleAudiofile(Transferable tr){
	        DataFlavor[] dfs=tr.getTransferDataFlavors();

	        for (DataFlavor df:dfs){
	            // does not work for Linux ...
	            if(df.isFlavorJavaFileListType()){
	                java.util.List<java.io.File> fileList=null;
	                try {
	                	Object trDataObj=tr.getTransferData(df);
	                	if(trDataObj instanceof List){
	                		fileList=(java.util.List<File>) trDataObj;
	                	}
	                } catch (UnsupportedFlavorException e) {
	                    // should never happen
	                    continue;
	                } catch (IOException e) {
	                    // notify the user ??
	                    continue;
	                }
	                if(fileList!=null){
	                	File f=checkForSingleAudiofile(fileList);
	                	if(f!=null){
	                		return f;
	                	}
	                }
	            }else if(df.isFlavorTextType()){
	                //... this is the Linux workaround
	                String mimeType=df.getMimeType();
	                String[] mimeElements=StringTokenizer.split(mimeType,';', true);
	                if(mimeElements.length>0 && "text/uri-list".equals(mimeElements[0])){
	                    
	                    try {
//	                      Object uriList=tr.getTransferData(df);
	                        Reader r=df.getReaderForText(tr);
//	                      System.out.println("Reader: "+r.toString());
	                        LineNumberReader lnr=new LineNumberReader(r);
	                        ArrayList<String> lines=new ArrayList<String>();
	                        String line=null;
	                        while((line=lnr.readLine())!=null){
	                            lines.add(line);
//	                          System.out.println("Line: "+line);
	                        }
//	                      System.out.println("Lines: "+lines.size());
	                        if(lines.size()==1){
	                            try {
	                                URI uri=new URI(lines.get(0));
	                                String scheme=uri.getScheme();
//	                              System.out.println("URI: "+uri+" "+scheme);
	                                if("file".equalsIgnoreCase(scheme)){
	                                    String path=uri.getPath();
	                                    File f=new File(path);
	                                AudioInputStream ais=null;
	                                try {
	                                    ais=AudioSystem.getAudioInputStream(f);
	                                } catch (UnsupportedAudioFileException e) {
	                                    continue;
	                                } catch (IOException e) {
	                                    continue;
	                                }finally{
	                                    if (ais!=null){
	                                        try {
	                                            ais.close();
	                                        } catch (IOException e) {
	                                            continue;
	                                        }
	                                    }
	                                }
//	                              System.out.println("Accept drop: "+f);
	                                return f;
	                                }
	                            } catch (URISyntaxException e) {
	                                continue;
	                            }
	                        }
	                    } catch (UnsupportedFlavorException e) {
//	                      e.printStackTrace();
	                        continue;
	                    } catch (IOException e) {
//	                      e.printStackTrace();
	                        continue;
	                    }
	                }
	            }
	        }
	        return null;
	    }

	    private void processDropTargetDragEvent(DropTargetDragEvent dtde){
//	        File af=checkForSingleAudiofile(dtde.getTransferable());
	    	boolean accept=checkForSingleAudiofileFlavour(dtde.getTransferable());
	        if(accept){
	            dtde.acceptDrag(dtde.getDropAction());
	        }else{
	            dtde.rejectDrag();
	        }
	    }
	    public void dragEnter(DropTargetDragEvent dtde) {
	        processDropTargetDragEvent(dtde);
//	    	dtde.acceptDrag(dtde.getDropAction());
	    }


	    public void dragExit(DropTargetEvent arg0) {
	        // nothing
	    }


	    public void dragOver(DropTargetDragEvent dtde) {
	        processDropTargetDragEvent(dtde);
	    }


	    public void drop(DropTargetDropEvent dtde) {
//	    	System.out.println("drop");
	        Transferable tr=dtde.getTransferable();
//	        DataFlavor[] dfs=tr.getTransferDataFlavors();
	        boolean success=false;
	        //      for (DataFlavor df:dfs){
	        //          if(df.isFlavorJavaFileListType()){
	        // always accept drop. see:
	        //      http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6516345
	        dtde.acceptDrop(dtde.getDropAction());

	        File af=checkForSingleAudiofile(tr);

	        if(af!=null){
	            //          dtde.acceptDrop(dtde.getDropAction());
	            close();
	            try {
	                openFile(af);
	                success=true;
	            } catch (UnsupportedAudioFileException e) {
	                success=false;
	            } catch (IOException e) {
	                success=false;
	            } catch (AudioSourceException e) {
	                success=false;
	            }
	            dtde.dropComplete(success);
	        }
	    }


	    public void dropActionChanged(DropTargetDragEvent arg0) {
	        // nothing
	        
	    }

       
}
