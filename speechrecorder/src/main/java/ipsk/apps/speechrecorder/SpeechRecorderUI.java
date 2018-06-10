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

package ipsk.apps.speechrecorder;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ips.annot.model.AnnotatedAudioClip;
import ips.annot.view.AnnotationAudioClipUI;
import ipsk.apps.speechrecorder.actions.EditScriptAction;
import ipsk.apps.speechrecorder.actions.ExportScriptAction;
import ipsk.apps.speechrecorder.actions.ImportScriptAction;
import ipsk.apps.speechrecorder.actions.PrintScriptAction;
import ipsk.apps.speechrecorder.actions.SetIndexAction;
import ipsk.apps.speechrecorder.actions.StartPlaybackAction;
import ipsk.apps.speechrecorder.config.DescriptionFont;
import ipsk.apps.speechrecorder.config.InstructionFont;
import ipsk.apps.speechrecorder.config.KeyInputMap;
import ipsk.apps.speechrecorder.config.KeyStrokeAction;
import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import ipsk.apps.speechrecorder.config.PromptConfiguration;
import ipsk.apps.speechrecorder.config.PromptFont;
import ipsk.apps.speechrecorder.config.Prompter.SpeakerWindowType;
import ipsk.apps.speechrecorder.config.TransportPanel;
import ipsk.apps.speechrecorder.config.WorkspaceProject;
import ipsk.apps.speechrecorder.config.ui.ProjectConfigurationView;
import ipsk.apps.speechrecorder.db.ExportSpeakersUIDialog;
import ipsk.apps.speechrecorder.monitor.RecMonitor;
import ipsk.apps.speechrecorder.monitor.StartStopSignal;
import ipsk.apps.speechrecorder.project.NewProjectConfiguration;
import ipsk.apps.speechrecorder.project.NewProjectDialog;
import ipsk.apps.speechrecorder.prompting.PromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.prompting.PromptViewer;
import ipsk.apps.speechrecorder.prompting.PromptViewerListener;
import ipsk.apps.speechrecorder.prompting.PrompterException;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerEvent;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterPluginException;
import ipsk.apps.speechrecorder.script.ItemcodeGenerator;
import ipsk.apps.speechrecorder.script.RecScriptManager;
import ipsk.apps.speechrecorder.script.ui.ExportScriptUIDialog;
import ipsk.apps.speechrecorder.script.ui.ImportScriptUIDialog;
import ipsk.apps.speechrecorder.script.ui.ScriptSourceEditor;
import ipsk.apps.speechrecorder.script.ui.ScriptUI;
import ipsk.apps.speechrecorder.script.ui.ScriptUIDialog;
import ipsk.apps.speechrecorder.session.SessionManager;
import ipsk.apps.speechrecorder.storage.StorageManagerException;
import ipsk.apps.speechrecorder.ui.InfoViewer;
import ipsk.apps.speechrecorder.workspace.WorkspaceException;
import ipsk.apps.speechrecorder.workspace.ui.WorkspacePanel;
import ipsk.audio.AudioController4;
import ipsk.audio.AudioControllerException;
import ipsk.audio.Profile;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.ui.AudioClipScrollPane;
import ipsk.audio.arr.clip.ui.AudioClipUIContainer;
import ipsk.audio.arr.clip.ui.AudioClipsUIContainer;
import ipsk.audio.arr.clip.ui.AudioSignalUI;
import ipsk.audio.arr.clip.ui.AudioTimeScaleUI;
import ipsk.audio.arr.clip.ui.FourierUI;
import ipsk.audio.dsp.LevelInfo;
import ipsk.audio.dsp.LevelInfosBean;
import ipsk.audio.mixer.ui.PortMixersUI;
import ipsk.audio.player.PlayerException;
import ipsk.audio.ui.LevelMeter;
import ipsk.audio.utils.AudioFormatUtils;
import ipsk.audio.view.AudioStatus;
import ipsk.awt.print.ComponentPrinter;
import ipsk.beans.DOMCodec;
import ipsk.beans.DOMCodecException;
import ipsk.db.speech.Mediaitem;
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.Reccomment;
import ipsk.db.speech.Recinstructions;
import ipsk.db.speech.Script;
import ipsk.db.speech.Section;
import ipsk.net.UploadCacheUI;
import ipsk.swing.JPopupMenuListener;
import ipsk.swing.ZipFileFilter;
import ipsk.swing.action.tree.ActionFolder;
import ipsk.swing.action.tree.ActionGroup;
import ipsk.swing.action.tree.ActionTreeRoot;
import ipsk.swing.action.tree.CheckActionLeaf;
import ipsk.swing.action.tree.JMenuBuilder;
import ipsk.util.LocalizableMessage;
import ipsk.util.SystemHelper;
import ipsk.util.apps.UpdateManager;
import ipsk.util.apps.descriptor.ApplicationVersionDescriptor;
import ipsk.util.apps.ui.UpdateDialogUI;
import ipsk.util.collections.ObservableArrayList;
import ipsk.util.collections.ObservableList;
import ipsk.xml.DOMConverter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipFile;

import javax.help.CSH;
import javax.help.CSH.DisplayHelpFromSource;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.UnsupportedOperationException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTable.PrintMode;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.w3c.dom.Document;

public class SpeechRecorderUI extends JFrame implements ActionListener, PromptViewerListener, DialogTargetProvider {
	
	private static final long serialVersionUID = 5486901704480120471L;

	private static final boolean DEBUG = false;
	 
//	private final int SPLASH_DURATION = 7000;
	private final int UPDATE_DELAY = 200;
	private boolean waiting;
	private PromptConfiguration promptConfiguration;
	//protected AboutBox aboutBox;
	private static final float LEFT_FRACTION = (float) 0.7;
	private static final float TOP_FRACTION = (float) 0.5;

//	private static final String ACTION_CMD_SCRIPT_SETTINGS="settings.script";
    private static final String ACTION_CMD_SCRIPT_SOURCE_EDIT="script.source.edit";
    
    public final static String[] ICON_FILENAMES=new String[]{"icons/speechrecorder_16x16.png",
        "icons/speechrecorder_32x32.png","icons/speechrecorder_64x64.png","icons/speechrecorder_128x128.png","icons/speechrecorder_256x256.png"};
    private List<Image> iconImages=new ArrayList<Image>();
    
	private Dimension screenSize;
	private GraphicsConfiguration expScreenConfig;
	private GraphicsConfiguration spkScreenConfig;
	private int expScreenIdx;

	// Declarations for menus
	static final JMenuBar mainMenuBar = new JMenuBar();

	protected JMenu fileMenu;
	protected JMenuItem miPrint;
	protected JMenuItem miSave;
	//protected JMenuItem miSaveAs;
	protected JMenuItem miQuit;

//	private JMenu viewMenu;
//	protected JCheckBoxMenuItem miDisplaySettings;
	
	protected JMenu editMenu;
	protected JMenuItem miUndo;
	protected JMenuItem miCut;
	protected JMenuItem miCopy;
	protected JMenuItem miPaste;
	protected JMenuItem miClear;
	protected JMenuItem miSelectAll;
	private JMenu workspaceMenu;
	private JMenuItem miWorkspace;
	private JMenu projectMenu;
	protected JMenuItem miNew;
	//	protected JMenuItem miOpen;
	protected JMenu openSubMenu;
	protected JMenuItem[] miAvailableProjects;
	protected JMenuItem miClose;
	protected JMenuItem miImport;

	protected JMenuItem miExport;

	private JMenu speakersMenu;
	protected JMenuItem miSpkSettings;
	protected JMenuItem miSpkTableExport; 
	private JMenuItem miSessionClipView;
	


	private JMenu scriptMenu;
    protected JMenuItem miEditScript;
    protected JMenuItem miEditScriptSrc;
    private JMenuItem miImportScript;
    private JMenuItem miExportScript;
    private JMenuItem miPrintScript;
    
    protected JMenu settingsMenu;
	protected JMenuItem miProjectSettings;
//	protected JMenuItem miSpkSettings;  
	protected JMenuItem miRecSettings;
	//protected JMenuItem miDeviceSettings;
	protected JMenuItem miSkipSettings;
	protected JMenuItem miViewSettings;
	
    
    protected JMenu helpMenu;
    protected JMenuItem miHelp;
    protected JMenuItem miAbout;
    protected JMenuItem miInfo;
    private JMenuItem miCheckUpdates; 
    private JMenuItem miContact;
    
//	private JMenuBar recorderMenuBar;

	private SessionViewer sessionViewer;
    private InfoViewer infoViewer;
	private RecTransporter recTransporter;
	private RecMonitor recMonitor;
	private RecWindow recWindow;
	private RecWindowFrame recWindowFrame;
	private RecWindowWindow recwindowWindow;
	private boolean fullScreenMode=false;
//	private PromptViewer promptViewer;
//	private JFrame uploadFrame;
	private LevelMeter recLevel;
	
	public final static int LEVEL_METER_DISABLE=0;
	public final static int LEVEL_METER_PLAYBACK=1;
	public final static int LEVEL_METER_CAPTURE=2;
	public final static int LEVEL_METER_RECORDING=3;
   
	private int levelMeterMode;
	
	//private RecDisplay recDisplay;
	private AudioClipUIContainer audioUI;
	private AudioClipScrollPane arrScrollPane; 
	private JPanel progressPanel;
	private ProgressViewer progressViewer;
	private UploadCacheUI uploadCacheUI;
	private JSplitPane dataPane;
	private SpeechRecorder speechRecorder;
	//private MixerManagerUI devicesUI;
	private PortMixersUI mixerUI;
	
	protected Locale currentLocale = Locale.getDefault();

	protected String language = currentLocale.getLanguage();
	protected String country = currentLocale.getCountry();

	protected UIResources uiString = null;
    private String defaultTitle;
	private Cursor defCursor;
	private Cursor waitCursor;
	private javax.swing.Timer updateTimer;
	//boolean projectEditable=false;
	private boolean fileSystemWorkspaceEnabled=true;
	public boolean isFileSystemWorkspaceEnabled() {
		return fileSystemWorkspaceEnabled;
	}

	public void setFileSystemWorkspaceEnabled(boolean fileSystemWorkspaceEnabled) {
		this.fileSystemWorkspaceEnabled = fileSystemWorkspaceEnabled;
		miWorkspace.setEnabled(fileSystemWorkspaceEnabled);
	}

	private boolean editingEnabled;
	private ScriptUIDialog scriptUIDialog=null;
    private ScriptSourceEditor scriptSrcEditor=null;

//	private boolean scriptPositioningEnabled;

	private boolean instructionNumbering=true;
	private boolean autoRecording;
	private boolean playbackEnabled;
	private boolean progressPaused=true;
	
	private ipsk.apps.speechrecorder.prompting.Prompter prompter;
	
	private SetIndexAction setIndexAction;
	private EditScriptAction editScriptAction;
	
    //private Class<? extends StartStopSignal> startStopSignalClass;
    private JPanel recMonitorPanel;
    private List<PromptPresenterServiceDescriptor> promptPresentersClassList;
    private HelpBroker helpBroker;
    private AudioSignalUI audioSignalView;
    private FourierUI sonagram;
    private ToggleSubjectDisplayAction toggleSubjectDisplayAction;
    private WorkspacePanel workspaceDialog;
    private ProjectConfigurationView promptConfigurationView;
    protected SpeakerDatabaseViewer spkDbView;
    private ExportSpeakersUIDialog exportSpeakersDialog;
    private DisplayHelpFromSource displayHelpFromSource;
    private ImportScriptUIDialog importScriptDialog;
    private ExportScriptUIDialog exportScriptDialog;
    private ImportScriptAction importScriptAction;
    private ExportScriptAction exportScriptAction;
    
    private PrintScriptAction printScriptAction;
//	private UpdateDialogUI updateDialog;
	private SplashScreen splashScreen;
	private Mixer promptMixer=null;
	private int promptAudioChannelOffset=0;
	
	/**
	 * @return the promptAudioChannelOffset
	 */
	public int getPromptAudioChannelOffset() {
		return promptAudioChannelOffset;
	}

	/**
	 * @param promptAudioChannelOffset the promptAudioChannelOffset to set
	 */
	public void setPromptAudioChannelOffset(int promptAudioChannelOffset) {
		this.promptAudioChannelOffset = promptAudioChannelOffset;
	}

	/**
	 * @return the promptMixer
	 */
	public Mixer getPromptMixer() {
		return promptMixer;
	}

	/**
	 * @param promptMixer the promptMixer to set
	 */
	public void setPromptMixer(Mixer promptMixer) {
		this.promptMixer = promptMixer;
	}

	private boolean applyWorkaroundBugID0006=false;
	private boolean applyWorkaroundBugID0007=false;

    private List<AutoAnnotationServiceDescriptor> autoAnnotatorDescriptors;

	private java.awt.Font promptFont;
	private java.awt.Font instrunctionsFont;
	private java.awt.Font descriptionsFont;

	private String[] promptFontFamilies;
	private String[] instructionsFontFamilies;
	private String[] descriptionFontFamilies;

	
    public class ToggleSubjectDisplayAction extends CheckActionLeaf{

        /**
         * @param displayName
         */
        public ToggleSubjectDisplayAction(LocalizableMessage displayName) {
            super(displayName);
          
        }

        /* (non-Javadoc)
         * @see ipsk.swing.action.tree.CheckActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
              doDisplaySettings();
        }
        
        
        
    }
    
    
//    private HelpSet helpSet;
  

	/**
	 * adds the items for the "File" menu.
	 */
	private void addFileMenuItems() {
		miPrint = new JMenuItem(uiString.getString("MenuItemPrint"));
        miPrint.setEnabled(true);
        fileMenu.add(miPrint);
        miPrint.addActionListener(this);
		miSave = new JMenuItem(uiString.getString("MenuItemSave"));
		miSave.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_S, java.awt.Event.META_MASK));
        miSave.setEnabled(false);
		fileMenu.add(miSave);
		miSave.addActionListener(this);

		//		miSaveAs = new JMenuItem(uiString.getString("MenuItemSaveAs"));
		//		fileMenu.add(miSaveAs).setEnabled(false);
		//		miSaveAs.addActionListener(this);

		miQuit = new JMenuItem(uiString.getString("MenuItemQuit"));
		fileMenu.add(miQuit).setEnabled(true);
		miQuit.addActionListener(this);

		mainMenuBar.add(fileMenu);
	}
	
	/**
	 * adds the items for the "Project" menu.
	 */
	private void addProjectMenuItems() {
		miNew = new JMenuItem(uiString.getString("MenuItemNew"));
		miNew.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_N, java.awt.Event.META_MASK));
		projectMenu.add(miNew).setEnabled(false);
		miNew.addActionListener(this);

		openSubMenu = new JMenu(uiString.getString("MenuItemOpen"));
		miAvailableProjects = new JMenuItem[0];
		//		miOpen = new JMenuItem(uiString.getString("MenuItemProjectFile"));
		//		miOpen.setAccelerator(
		//			KeyStroke.getKeyStroke(
		//				java.awt.event.KeyEvent.VK_O,
		//				java.awt.Event.META_MASK));
		//		openSubMenu.add(miOpen);
		projectMenu.add(openSubMenu).setEnabled(false);
		//		miOpen.addActionListener(this);

		miClose = new JMenuItem(uiString.getString("MenuItemClose"));
		miClose.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_W, java.awt.Event.META_MASK));
		projectMenu.add(miClose).setEnabled(false);
		miClose.addActionListener(this);

		miImport = new JMenuItem(uiString.getString("MenuItemImport"));
		miImport.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_I, java.awt.Event.META_MASK));
		projectMenu.add(miImport).setEnabled(false);
		miImport.addActionListener(this);
		miExport = new JMenuItem(uiString.getString("MenuItemExport"));
		miExport.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_E, java.awt.Event.META_MASK));
		projectMenu.add(miExport).setEnabled(false);
		miExport.addActionListener(this);
//		miProjectSettings = new JMenuItem(uiString
//				.getString("MenuItemProjectSettings"));
		miProjectSettings = new JMenuItem(uiString
                .getString("MenuItemPreferences"));
		miProjectSettings.addActionListener(this);
		projectMenu.add(miProjectSettings).setEnabled(false);
		mainMenuBar.add(projectMenu);
	}
	
	/**
     * adds the items for the "Speakers" menu.
     */
    public void addSpeakersMenuItems() {
        

        miSpkSettings = new JMenuItem(uiString
                .getString("MenuItemSpeakerSettings"));
        miSpkSettings.addActionListener(this);
        speakersMenu.add(miSpkSettings).setEnabled(false);
        
        miSpkTableExport=new JMenuItem("Table export ...");
        miSpkTableExport.addActionListener(this);
        speakersMenu.add(miSpkTableExport).setEnabled(false);
        
//        miSessionClipView = new JMenuItem("Session overview");
//        miSessionClipView.addActionListener(this);
//        speakersMenu.add(miSessionClipView).setEnabled(true);
        
      
        mainMenuBar.add(speakersMenu);
    }
    
	
	/**
	 * adds the items for the "Script" menu.
	 */
	public void addScriptMenuItems() {
		
//		miScriptSettings=new JMenuItem(uiString.getString("MenuItemScriptSettings"));
//		miScriptSettings.setActionCommand(ACTION_CMD_SCRIPT_SETTINGS);
//		miScriptSettings.addActionListener(this);
	    miEditScript= new JMenuItem(editScriptAction);
//		miScriptSettings.setEnabled(false);
		scriptMenu.add(miEditScript);
        
        miEditScriptSrc=new JMenuItem("Edit script XML source");
        miEditScriptSrc.setActionCommand(ACTION_CMD_SCRIPT_SOURCE_EDIT);
        miEditScriptSrc.addActionListener(this);
        miEditScriptSrc.setEnabled(false);
        scriptMenu.add(miEditScriptSrc);
               
        printScriptAction=new PrintScriptAction(speechRecorder,"Print script...");
        printScriptAction.setEnabled(false);
        miImportScript=new JMenuItem(importScriptAction);
        miExportScript=new JMenuItem(exportScriptAction);
        miPrintScript=new JMenuItem(printScriptAction);
        scriptMenu.add(miImportScript);
        scriptMenu.add(miExportScript);
//        scriptMenu.add(miPrintScript);
        
		mainMenuBar.add(scriptMenu);
	}
	
	/**
	 * adds the items for the "Edit" menu.
	 */
	public void addEditMenuItems() {
		miUndo = new JMenuItem(uiString.getString("MenuItemUndo"));
		miUndo.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Z, java.awt.Event.META_MASK));
		editMenu.add(miUndo).setEnabled(false);
		miUndo.addActionListener(this);
		editMenu.addSeparator();

		miCut = new JMenuItem(uiString.getString("MenuItemCut"));
		miCut.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_X, java.awt.Event.META_MASK));
		editMenu.add(miCut).setEnabled(false);
		miCut.addActionListener(this);

		miCopy = new JMenuItem(uiString.getString("MenuItemCopy"));
		miCopy.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_C, java.awt.Event.META_MASK));
		editMenu.add(miCopy).setEnabled(false);
		miCopy.addActionListener(this);

		miPaste = new JMenuItem(uiString.getString("MenuItemPaste"));
		miPaste.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_V, java.awt.Event.META_MASK));
		editMenu.add(miPaste).setEnabled(false);
		miPaste.addActionListener(this);

		miClear = new JMenuItem(uiString.getString("MenuItemClear"));
		editMenu.add(miClear).setEnabled(false);
		miClear.addActionListener(this);
		editMenu.addSeparator();

		miSelectAll = new JMenuItem(uiString.getString("MenuItemSelectAll"));
		miSelectAll.setAccelerator(KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_A, java.awt.Event.META_MASK));
		editMenu.add(miSelectAll).setEnabled(false);
		miSelectAll.addActionListener(this);

		mainMenuBar.add(editMenu);
	}

	/**
	 * adds the items for the "View" menu.
	 */
	public void addViewMenuItems() {
		//		miUploadCache = new JMenuItem("Cache View");
		//		miUploadCache.setAccelerator(
		//			KeyStroke.getKeyStroke(
		//				java.awt.event.KeyEvent.VK_U,
		//				java.awt.Event.META_MASK));
		//		viewMenu.add(miUploadCache).setEnabled(false);
		//		miUploadCache.addActionListener(this);
		//		viewMenu.addSeparator();

		//		mainMenuBar.add(viewMenu);
	        
//	        miDisplaySettings = new JCheckBoxMenuItem(uiString
//	                .getString("MenuItemSpeakerWindow"), false);
//	        
//	        miDisplaySettings.addActionListener(this);
	    
//	        miDisplaySettings=new JCheckBoxMenuItem(toggleSubjectDisplayAction);
//	        //settingsMenu.add(miDisplaySettings);
//	        viewMenu.add(miDisplaySettings).setEnabled(true);
	    ActionTreeRoot ascActionTree=arrScrollPane.getActionTreeRoot();
	    ActionFolder signalViewFolder=new ActionFolder("signalview",new LocalizableMessage("Signal view"));
	    ActionTreeRoot shiftedAscActionTree=ascActionTree.shiftFromTopLevel(signalViewFolder);
        ActionFolder afft=new ActionTreeRoot();
       
//        aff.add(pa);
        
        
        ActionFolder avf=ActionFolder.buildTopLevelFolder(ActionFolder.VIEW_FOLDER_KEY);
//        afft.add(avf);
        ActionGroup subjectViewGroup=new ActionGroup("view.subjectGroup");
        subjectViewGroup.add(toggleSubjectDisplayAction);
        avf.add(subjectViewGroup);
        afft.add(avf);
        afft.merge(shiftedAscActionTree);
        
        
//        MediaViewActions mediaViewActions=new MediaViewActions(asc);
// 
//        ActionGroup lengthUnitGroup=new ActionGroup("view.length_unit");
//        ActionFolder uaf=new ActionFolder("view.units", new LocalizableMessage("Units"));
//        lengthUnitGroup.add(uaf);
//        
//        ActionFolder tff=new ActionFolder("view.units.time", new LocalizableMessage("Time"));
//        uaf.add(tff);
//        MediaViewActions.MediaLengthUnitFramesAction mediaLenFramesAction=mediaViewActions.getMediaLenFramesAction();
//        
//        MediaViewActions.MediaLengthUnitTimeAction mediaLenTimeAction=mediaViewActions.getMediaLenTimeAction();
//        MediaViewActions.TimeFormatSecondsMsAction timeFormatSecondsMsAction=mediaViewActions.getTimeFormatSecondsMsAction();
//        MediaViewActions.MediaTimeFormatAction mediaTimeAction=mediaViewActions.getMediaTimeAction();
//        
//        tff.add(timeFormatSecondsMsAction);
//        tff.add(mediaTimeAction);
//        timeFormatSecondsMsAction.setSelected(true);
//        
//        uaf.add(mediaLenFramesAction);
//        uaf.add(mediaLenTimeAction);
//        
//        avf.add(lengthUnitGroup);
        
        // build popup menu for signal view
        JMenuBuilder pmb=new JMenuBuilder(ascActionTree);
        JPopupMenu pm=pmb.buildJPopupMenu();
        JPopupMenuListener pml=new JPopupMenuListener(pm);
        arrScrollPane.addMouseListener(pml);
        audioUI.addPopupMouseListener(pml);
        
        // build view menu in menu bar
        JMenuBuilder menuBuilder=new JMenuBuilder(afft);
        JMenu viewMenu=menuBuilder.buildMenu(ActionFolder.VIEW_FOLDER_KEY);
	    mainMenuBar.add(viewMenu);
	  
	}
	
	/**
	 * adds the items for the "Settings" menu.
	 */
	public void addSettingsMenuItems() {
		

//		miSpkSettings = new JMenuItem(uiString
//				.getString("MenuItemSpeakerSettings"));
//		miSpkSettings.addActionListener(this);
//		settingsMenu.add(miSpkSettings).setEnabled(false);
		
		miRecSettings = new JMenuItem(uiString
				.getString("MenuItemRecordingSettings"));
		miRecSettings.addActionListener(this);
		//settingsMenu.add(miRecSettings);
		settingsMenu.add(miRecSettings).setEnabled(true);

		miSkipSettings = new JMenuItem(uiString
				.getString("MenuItemSkipSettings"));
		miSkipSettings.addActionListener(this);
		settingsMenu.add(miSkipSettings).setEnabled(false);

		miViewSettings = new JMenuItem(uiString
				.getString("MenuItemViewSettings"));
		miViewSettings.addActionListener(this);
		//settingsMenu.add(miViewSettings);
		settingsMenu.add(miViewSettings).setEnabled(false);

//		miDisplaySettings = new JCheckBoxMenuItem(uiString
//				.getString("MenuItemSpeakerWindow"), false);
//		miDisplaySettings.addActionListener(this);
//		//settingsMenu.add(miDisplaySettings);
//		settingsMenu.add(miDisplaySettings).setEnabled(true);

		mainMenuBar.add(settingsMenu);
	}
    
    
    private void addHelpMenuItems(){
        miAbout=new JMenuItem("About");
        miAbout.addActionListener(this);
        miAbout.setEnabled(true);
        helpMenu.add(miAbout);
        miHelp=new JMenuItem("Help");
//        miHelp.addActionListener(this);
        miHelp.setEnabled(true);
        if(helpBroker!=null){
            helpMenu.add(miHelp);
            HelpSet helpSet=helpBroker.getHelpSet();
            helpBroker.enableHelpOnButton(miHelp, helpSet.getHomeID().getIDString(),helpSet);
        }
        miInfo=new JMenuItem("Info");
        miInfo.addActionListener(this);
        miInfo.setEnabled(true);
        helpMenu.add(miInfo);
        miContact=new JMenuItem("Contact");
        
        boolean desktopSupported=Desktop.isDesktopSupported();
        if(desktopSupported){
            miContact.addActionListener(this);
            miContact.setEnabled(true);
            helpMenu.add(miContact);
        }
        UpdateManager updateManager=speechRecorder.getUpdateManager();
       
        miCheckUpdates=new JMenuItem("Check for updates...");
        if(updateManager!=null){
        	helpMenu.add(miCheckUpdates);
        	miCheckUpdates.setEnabled(true);
        	miCheckUpdates.addActionListener(this);
        }
        mainMenuBar.add(helpMenu);
       
        
       
    }

	/**
	 * adds all menus to the main menu bar.
	 */
	public void addMenus() {
		mainMenuBar.removeAll();
		fileMenu = new JMenu(uiString.getString("MenuFile"));
		addFileMenuItems();
		editMenu = new JMenu(uiString.getString("MenuEdit"));
        //addEditMenuItems();
//		viewMenu=new JMenu(uiString.getString("MenuView"));
        addViewMenuItems();
        workspaceMenu=new JMenu("Workspace");
        mainMenuBar.add(workspaceMenu);
        miWorkspace=new JMenuItem("Workspace...");
        workspaceMenu.add(miWorkspace);
        miWorkspace.setEnabled(fileSystemWorkspaceEnabled);
        miWorkspace.addActionListener(this);
		projectMenu=new JMenu(uiString.getString("MenuProject"));
		addProjectMenuItems();
		speakersMenu=new JMenu(uiString.getString("MenuSpeakers"));
        addSpeakersMenuItems();
		scriptMenu=new JMenu(uiString.getString("MenuScript"));
		addScriptMenuItems();
		
		settingsMenu = new JMenu(uiString.getString("MenuSettings"));
		addSettingsMenuItems();
        // help menu is traditionally right aligned
        mainMenuBar.add(Box.createHorizontalGlue());
        helpMenu=new JMenu(uiString.getString("MenuHelp"));
        addHelpMenuItems();
		setJMenuBar(mainMenuBar);
	}

	/**
	 * constructor for the JSpeechRecorder class
	 */
	public SpeechRecorderUI(SpeechRecorder spRec, int expScreenIdx,GraphicsConfiguration exc,
			GraphicsConfiguration spkc) {
		//super(exc);
	    super();
		speechRecorder = spRec;
		this.expScreenIdx=expScreenIdx;
		expScreenConfig = exc;
		spkScreenConfig = spkc;
		uiString = UIResources.getInstance();
		getContentPane().setLayout(new BorderLayout());

		WindowAdapter wa = new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				handleQuit();
			}
		};
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(wa);
		getContentPane().setLayout(new BorderLayout());
		updateTimer=new Timer(UPDATE_DELAY, this);
		updateTimer.setRepeats(true);
		
		
		toggleSubjectDisplayAction=new ToggleSubjectDisplayAction(new LocalizableMessage(uiString.getString("MenuItemSpeakerWindow")));
		toggleSubjectDisplayAction.setSelected(false);
		toggleSubjectDisplayAction.setEnabled(false);
		
		defCursor = getContentPane().getCursor();
		waitCursor = new Cursor(Cursor.WAIT_CURSOR);
		
		String helpHS = "ipsk/apps/speechrecorder/manual/SpeechRecorderUserManual_jh.hs";

        ClassLoader cl = getClass().getClassLoader();
            URL hsURL = HelpSet.findHelpSet(cl, helpHS);
            if(hsURL!=null){
            	try {
            		HelpSet helpSet = new HelpSet(null, hsURL);
            		helpBroker=helpSet.createHelpBroker();
            		try{
            			
            			helpBroker.setScreen(expScreenIdx);
            		}catch(UnsupportedOperationException uoe){
            			
            		}
            	} catch (HelpSetException e1) {
            		e1.printStackTrace();
            		// OK disable help

            	}
            }

	}
    

	public void createUI(SetIndexAction sia, EditScriptAction editScriptAction, ImportScriptAction importScriptAction,ExportScriptAction exportScriptAction, StartPlaybackAction startPlaybackAction) throws PluginLoadingException {
	    this.setIndexAction=sia;
	    this.editScriptAction=editScriptAction;
	    this.importScriptAction=importScriptAction;
	    this.exportScriptAction=exportScriptAction;
	    setIndexAction.addPropertyChangeListener(new PropertyChangeListener(){    
            public void propertyChange(PropertyChangeEvent evt) {
               miSkipSettings.setEnabled(setIndexAction.isEnabled());
            }
        });
		getContentPane().removeAll();
		screenSize = expScreenConfig.getBounds().getSize();

		iconImages=new ArrayList<Image>();
        for(String ifn:ICON_FILENAMES){
            ImageIcon imageIcon = new ImageIcon(getClass().getResource(ifn));
            Image iconImage=imageIcon.getImage();
            iconImages.add(iconImage);
        }
        
        //setIconImage(icon.getImage());
        setIconImages(iconImages);
	
		//recording buttons: Back, Record/Stop, Forward, Play        
		recTransporter = new RecTransporter(speechRecorder, speechRecorder
				.getRecTransporterActions());

		//traffic light
		recMonitor = new RecMonitor();
//		recMonitor.setStartStopSignal(startStopSignal);

		// attach is not done in constructor anymore
		RecStatus rs=RecStatus.getInstance();
		rs.attach(recTransporter);
//		rs.attach(recMonitor);
		
		//recording level meter
		recLevel = new LevelMeter();
		recLevel.setUseIntervalPeakLevel(true);
		recLevel.addActionListener(this);
		levelMeterMode=LEVEL_METER_DISABLE;

		//signal display panel for speech wave
		//recDisplay = new RecDisplay(speechRecorder);
		
		AnnotatedAudioClip audioClip=speechRecorder.getAudioClip();
        audioUI=new AudioClipUIContainer();
        
        audioSignalView = new AudioSignalUI();
        audioSignalView.setUseThread(true);
        sonagram = new FourierUI();
        sonagram.setUseThread(true);
        // set defaults similar to Praat
        FourierUI.Profile sonagramProfile=FourierUI.Profile.PHONETIC1;
        sonagram.setMaxFrequency(sonagramProfile.getMaxFrequency());
        sonagram.setDynamicRangeDB(sonagramProfile.getDynamicRangeDB());
        sonagram.setWindowSize(sonagramProfile.getWindowLength());
        sonagram.setEmphasisPerOctaveDB(sonagramProfile.getEmphasisPerOctaveDB());
        
        AnnotationAudioClipUI annoClipUI=new AnnotationAudioClipUI(audioClip);
       
        annoClipUI.setStartPlaybackAction(startPlaybackAction);
        audioUI.add(annoClipUI);
        
        AudioTimeScaleUI timeScale = new AudioTimeScaleUI();
        audioUI.add(audioSignalView);
        audioUI.add(sonagram);
       
        audioUI.add(timeScale);
        audioUI.setAudioClip(audioClip);
        audioUI.setFixXZoomFitToPanel(true);

        arrScrollPane = new AudioClipScrollPane();
        arrScrollPane.setShowYScales(true);
        arrScrollPane.setAudioClipUiContainer(audioUI);
        
        addMenus();
        
        
		//speaker data
		sessionViewer = new SessionViewer(speechRecorder);
		sessionViewer.setMinimumSize(new Dimension(0, 0));
        
        infoViewer=new InfoViewer(speechRecorder);

        prompter =new ipsk.apps.speechrecorder.prompting.Prompter();
        prompter.setDialogTargetProvider(this);
        
		//prompt viewer for text, image, and multimedia prompts
		PromptViewer promptViewer = new PromptViewer(promptPresentersClassList,prompter.getStartPromptPlaybackAction(),prompter.getStopPromptPlaybackAction());
		promptViewer.setDialogTargetProvider(prompter.getDialogTargetProvider());
		prompter.setExperimenterViewer(promptViewer);
		
		
		// TODO if we want configurable window type (frame or window) we have to create rec window in cnfigure() method. 
		// create speaker display, but do not display it right away.
		
		//recWindow.setVisible(false);
		
		//progress of recordings and upload
		// recording script progress viewer: a table with the prompt text or
		// description,
		//item number and checkbox for recorded yes/no
		progressPanel = new JPanel();
		progressPanel.setLayout(new BorderLayout());
		progressViewer = new ProgressViewer(SessionManager.getInstance(),
				setIndexAction,editScriptAction);
		progressPanel.add(progressViewer,BorderLayout.CENTER);

		recMonitorPanel = new JPanel();
		recMonitorPanel.setLayout(new BorderLayout());
		recMonitorPanel.add(recMonitor,BorderLayout.WEST);
		recMonitorPanel.add(promptViewer,BorderLayout.CENTER);
		recMonitorPanel.add( new JLabel(uiString
				.getString("SpeakerWindow"), JLabel.CENTER),BorderLayout.NORTH);

		JPanel recSignalPanel = new JPanel();
		recSignalPanel.setLayout(new BorderLayout());
		recSignalPanel.add(recLevel,BorderLayout.WEST);
		//recSignalPanel.add(BorderLayout.CENTER, recDisplay);
		recSignalPanel.add(arrScrollPane,BorderLayout.CENTER);
		recSignalPanel.add(new JLabel(uiString
				.getString("SignalDisplay"), JLabel.CENTER),BorderLayout.NORTH);

		dataPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sessionViewer,
				progressPanel);
		JSplitPane recordingPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				recMonitorPanel, recSignalPanel);
		defaultTitle = uiString.getString("SpeechRecorder") + " "
				+ SpeechRecorder.VERSION + " " + SpeechRecorder.COPYRIGHT;
		setTitle(defaultTitle);

		recordingPane
				.setDividerLocation((int) (screenSize.getHeight() * TOP_FRACTION));
		recordingPane.setMinimumSize(new Dimension(0, 0));

		JSplitPane displayPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				recordingPane, dataPane);
		displayPanel
				.setDividerLocation((int) (screenSize.getWidth() * LEFT_FRACTION));
		displayPanel.setMinimumSize(new Dimension(0, 0));

		getContentPane().add(recTransporter,BorderLayout.SOUTH);
		getContentPane().add(displayPanel,BorderLayout.CENTER);
	}
	/**
	 * creates and shows a splash screen. If the parameter &quot;closeable&quot;
	 * is true, then the splash screen has a close button, otherwise not.
	 * 
	 * @param closeable
	 */
	public void showSplashScreen(boolean closeable) {
		splashScreen = new SplashScreen(expScreenConfig, closeable);
		splashScreen.setIconImages(iconImages);
		splashScreen.showScreen();
	}
	
	/*
	 * public void handleAbout() { aboutBox.setResizable(false);
	 * aboutBox.setVisible(true); aboutBox.show(); }
	 */

	public boolean isInstructionNumbering() {
		return instructionNumbering;
	}
    
	public void setInstructionNumbering(boolean instructionNumbering) {
		this.instructionNumbering=instructionNumbering;
		if(prompter!=null){
			prompter.setInstructionNumbering(instructionNumbering);
		}
//		if(recWindow!=null){
//			recWindow.setInstructionNumbering(instructionNumbering);
//		}
	}
    
	
	
	public void configure() {
		
		// TODO use ipsk.utils.SystemHelper
		String osName=System.getProperty("os.name");
		String javaVersion=System.getProperty("java.version");
		String javaVendor=System.getProperty("java.vendor");
//		System.out.println("OS name "+osName+" Java vendor: "+javaVendor+" vers: "+javaVersion);
		boolean isMacOSX="Mac OS X".equalsIgnoreCase(osName);
		boolean isOracleJava7="Oracle corporation".equalsIgnoreCase(javaVendor) && javaVersion.startsWith("1.7.0");
		
		// BUgs ID 0006 and 0007 seem to be fixed in Oracle Java 8 for Mac OS X preview
		// so I apply this only to Java 7
		applyWorkaroundBugID0006=isMacOSX && isOracleJava7;
		if(applyWorkaroundBugID0006){
			System.err.println("Applying workaround for bug ID 0006");
		}
		applyWorkaroundBugID0007=isMacOSX && isOracleJava7;
		if(applyWorkaroundBugID0007){
			System.err.println("Applying workaround for bug ID 0007");
		}
		ProjectConfiguration configuration = speechRecorder.getConfiguration();
        setTitle(configuration.getName()+" - "+defaultTitle);
		//	progress of upload (only in webrecorder mode)
		if (speechRecorder.isUsingUploadCache()) {
			uploadCacheUI = (UploadCacheUI) getUploadCacheUI();
			progressPanel.add(uploadCacheUI,BorderLayout.SOUTH);
		}
		//recLevel.setAudioController(speechRecorder.getAudioController());

		// prompt configuration
		promptConfiguration = speechRecorder.getConfiguration()
				.getPromptConfiguration();

		PromptViewer promptViewer=prompter.getExperimenterViewer();
		promptViewer.setContext(speechRecorder.getProjectContext());
		PromptFont promptFontCfg=promptConfiguration.getPromptFont();
		InstructionFont instructionsFontCfg=promptConfiguration.getInstructionsFont();
		DescriptionFont descriptionFontCfg=promptConfiguration.getDescriptionFont();
		
		promptFont = promptFontCfg.toFont();
		instrunctionsFont=instructionsFontCfg.toFont();
		descriptionsFont=descriptionFontCfg.toFont();
		
		promptViewer
				.setPromptFont(promptFont);
		promptFontFamilies=promptFontCfg.getFamily();
		instructionsFontFamilies=instructionsFontCfg.getFamily();
		descriptionFontFamilies=descriptionFontCfg.getFamily();
		if(scriptUIDialog!=null){
			scriptUIDialog.setPromptFontFamilies(promptFontFamilies);
			scriptUIDialog.setInstructionsFontFamilies(instructionsFontFamilies);
			scriptUIDialog.setDescriptionFontFamilies(descriptionFontFamilies);
		}
		
		progressViewer.setUseablePromptFontFamilies(promptFontFamilies);
		
		promptViewer.setInstructionsFont(instrunctionsFont);
		descriptionsFont=promptConfiguration
				.getDescriptionFont().toFont();
		promptViewer.setDescriptionFont(descriptionsFont);
		
		ipsk.apps.speechrecorder.config.Prompter prompterCfg=null;
		ipsk.apps.speechrecorder.config.Prompter[] prompterCfgs=promptConfiguration.getPrompter();
		if(prompterCfgs!=null && prompterCfgs.length>0){
		    prompterCfg=prompterCfgs[0];
		}
		if(prompterCfg!=null){
		    Boolean fullScreenModeCfg=prompterCfg.getFullScreenMode();
		    if(fullScreenModeCfg!=null && fullScreenModeCfg){
		    	boolean supported=(!applyWorkaroundBugID0007) && spkScreenConfig.getDevice().isFullScreenSupported();
		        if(!supported){
		        	JOptionPane.showConfirmDialog(this, "You configured fullscreen mode for speaker window, but this Java plaform does not support it.","WARNING: Fullscreen mode",JOptionPane.WARNING_MESSAGE);
		        }
		    	fullScreenMode=supported;
		    }else{
		    	fullScreenMode=false;
		    }
		}else{
			fullScreenMode=false;
		}
		if(prompterCfg!=null && prompterCfg.getSpeakerWindowType().equals(SpeakerWindowType.WINDOW)){
		    try {
		        if(recwindowWindow==null){
		            recwindowWindow = new RecWindowWindow(speechRecorder.getRecTransporterActions(),promptPresentersClassList, spkScreenConfig,prompter);
		        }
		        recWindow=recwindowWindow;
		        Rectangle spkScreenBounds=spkScreenConfig.getBounds();
		        Window recWin=recWindow.getWindow();
		        recWin.setIconImages(iconImages);
		        recWin.setSize(spkScreenBounds.width,spkScreenBounds.height);
		        recWin.validate();
		    } catch (PluginLoadingException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		}else{
		    if(recWindowFrame==null){
		        try {
		            recWindowFrame = new RecWindowFrame(speechRecorder.getRecTransporterActions(),promptPresentersClassList, spkScreenConfig,prompter);
		            recWindowFrame.setIconImages(iconImages);
		        } catch (PluginLoadingException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        } 
		    }else{
		    	// reset possible fullscreen undecoration setting
		    	recWindowFrame.dispose();
//		    	SystemHelper.disposeWindow(recWindowFrame);
//		    	recWindowFrame.setUndecorated(false);
		    }
		    recWindowFrame.setUndecorated(fullScreenMode);
//		    recWindowFrame.setResizable(!fullScreenMode);
		    recWindow=recWindowFrame;
		}
		recWindow.attachToRecStatus();
      
        prompter.addSubjectViewer(recWindow.getPromptViewer());
        prompter.setPromptMixer(promptMixer);
        prompter.setAudioChannelOffset(promptAudioChannelOffset);
        setInstructionNumbering(instructionNumbering);
        speechRecorder.setSpeakerWindowShowing(false);
        WindowAdapter wa = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                speechRecorder.setSpeakerWindowShowing(false);
            }
        };
        recWindow.getWindow().addWindowListener(wa);
        
		PromptViewer recPromptViewer = recWindow.getPromptViewer();

		recPromptViewer.setContext(speechRecorder.getProjectContext());
		recPromptViewer.setPromptFont(promptConfiguration.getPromptFont()
				.toFont());
		recPromptViewer.setInstructionsFont(promptConfiguration
				.getInstructionsFont().toFont());
		recPromptViewer.setDescriptionFont(promptConfiguration
				.getDescriptionFont().toFont());

		sessionViewer.setData(speechRecorder.getSpeaker());
		dataPane.resetToPreferredSizes();

		//		if (promptConfiguration.getShowPromptWindow()) {
		//			recWindow = new RecWindow(speechRecorder, spkScreenConfig);
		//			promptViewer.setSilent(true);
		//		}
		boolean autoPromptPlay = promptConfiguration.getAutomaticPromptPlay();
		prompter.setAutomaticPromptPlay(autoPromptPlay);
//		recWindow.getPromptViewer().setAutomaticPromptPlay(autoPromptPlay);
		boolean automaticRecording = Section.Mode.getByValue(configuration.getRecordingConfiguration()
				.getMode()).equals(Section.Mode.AUTORECORDING);
		recTransporter.setAutoRecording(automaticRecording);
		
		recWindow.setTransporterShowing(promptConfiguration.getShowButtonsInPromptWindow());
		if(prompterCfg!=null){
		  TransportPanel t=prompterCfg.getTransportPanel();
          if(t!=null){
          recWindow.getRecTransporter().setShowRecStartAction(t.isShowStartRecordAction());
          recWindow.getRecTransporter().setShowRecStopAction(t.isShowStopRecordAction());
          }
		}
		recLevel
				.setAudioFormat(speechRecorder.getAudioFileFormat().getFormat());
        KeyInputMap keyMap=configuration.getControl().getKeyInputMap();
        if(keyMap!=null){
        KeyStrokeAction[] keyStrokeActions=keyMap.getKeyStrokeAction();
       recTransporter.clearActionKeyCodes();
       //recTransporter.setKeyButtonBindingEnabled(false);
       
        for(int i=0;i<keyStrokeActions.length;i++){
            KeyStrokeAction ksa=keyStrokeActions[i];
            //if(a.isKeyEnabled()){
				int keyCode = ksa.getCode();
            int modifierMask=0;
				if (ksa.getShift()) {
                modifierMask |= KeyEvent.SHIFT_MASK;
            }
				if (ksa.isAlt()) {
                modifierMask |= KeyEvent.ALT_MASK;
            }
				if (ksa.isCtrl()) {
                modifierMask |= KeyEvent.CTRL_MASK;
            }
            KeyStroke ks=KeyStroke.getKeyStroke(keyCode,modifierMask);
            
				Action a = speechRecorder.getActionByActionCommand(ksa
						.getAction());
            recTransporter.addKeyStrokeAction(ks,a);
				// The accelerator seems to work only for JMenu and JMenuItem
				// components:
            //a.putValue(Action.ACCELERATOR_KEY,ks);
            
        }
        if(keyStrokeActions.length>=1){
            //recTransporter.setKeyButtonBindingEnabled(true);
				recTransporter.setConsumeAllKeys(keyMap
						.isConsumeallkeys());
        }
        }
		recWindow.setAutoRecording(automaticRecording);

//		ViewConfiguration viewConfig=speechRecorder.getConfiguration()
//        .getViewConfiguration();
//		AudioClipView audioClipViewConfig=viewConfig.getAudioClipView();
//
//		audioSignalView.setVisible(audioClipViewConfig.getShowSignalView());
//		sonagram.setVisible(audioClipViewConfig.getShowSonagram());
		
		sonagram.setVisible(false);
		//projectEditable = speechRecorder.getConfiguration().getEditable();
		miNew.setEnabled(false);
		openSubMenu.setEnabled(false);
		//miSaveAs.setEnabled(editable);
		//setEnableEditing(true);
		
		miImport.setEnabled(false);
		exportScriptAction.setEnabled(true);
		printScriptAction.setEnabled(true);
		
	}
    
    
    
	
	
//	public void setRecScriptResources(Hashtable rs){
//		promptViewer.setRecScriptResources(rs);
//		recWindow.getPromptViewer().setRecScriptResources(rs);
//	}
	
	public void setPromptItem(PromptItem pi) throws PromptPresenterException{
		if(pi!=null){
			List<Mediaitem> mis=pi.getMediaitems();
			// check if plain text can be displayed with default prompt font (family)
			for(Mediaitem mi:mis){
				String miMimeType=mi.getNNMimetype();
				if(MIMETypes.isOfType(miMimeType, MIMETypes.PLAINTEXTMIMETYPES)){
					String miText=mi.getText();
					if(promptFont.canDisplayUpTo(miText)!=-1){
						java.awt.Font[] allPromptFonts=promptConfiguration.getPromptFont().toFonts();
						java.awt.Font altFont=null;
						for(java.awt.Font af:allPromptFonts){
							if(af.canDisplayUpTo(miText)==-1){
								// found alternative font which can display current prompt text
								altFont=af;
								break;
							}
						}
						if(altFont==null){
							displayError("Font error", "Prompt cannot be dislayed with current selected font !!");
						}else{
							prompter.setPromptFont(altFont);
						}
					}else{
						prompter.setPromptFont(promptFont);
					}
				}
			}
			
			if(pi instanceof ipsk.db.speech.Recording){
				ipsk.db.speech.Recording r=(ipsk.db.speech.Recording)pi;
				Recinstructions instrs=r.getRecinstructions();
				if(instrs!=null){
					String instrText=instrs.getRecinstructions();
					if(instrText!=null){
						if(instrunctionsFont.canDisplayUpTo(instrText)!=-1){
							java.awt.Font[] allPromptFonts=promptConfiguration.getInstructionsFont().toFonts();
							java.awt.Font altFont=null;
							for(java.awt.Font af:allPromptFonts){
								if(af.canDisplayUpTo(instrText)==-1){
									// found alternative font which can display current instruction text
									altFont=af;
									break;
								}
							}
							if(altFont==null){
								displayError("Font error", "Prompt instruction cannot be dislayed with current selected font !!");
							}else{
								prompter.setInstructionsFont(altFont);
							}
						}else{
							prompter.setInstructionsFont(instrunctionsFont);
						}
					}
				}
				Reccomment descr=r.getReccomment();
				if(descr!=null){
					String descrText=descr.getReccomment();
					if(descrText!=null){
						if(descriptionsFont.canDisplayUpTo(descrText)!=-1){
							java.awt.Font[] allPromptFonts=promptConfiguration.getDescriptionFont().toFonts();
							java.awt.Font altFont=null;
							for(java.awt.Font af:allPromptFonts){
								if(af.canDisplayUpTo(descrText)==-1){
									// found alternative font which can display current instruction text
									altFont=af;
									break;
								}
							}
							if(altFont==null){
								displayError("Font error", "Prompt description/comment cannot be dislayed with current selected font !!");
							}else{
								prompter.setDescriptionFont(altFont);
							}
						}else{
							prompter.setDescriptionFont(descriptionsFont);
						}
					}
				}
			}
			
		}
		prompter.setPromptItem(pi);
		 String itemCode=null;
		if(pi instanceof ipsk.db.speech.Recording){
		    itemCode=((ipsk.db.speech.Recording)pi).getItemcode();
		    recTransporter.setItemCode(itemCode);
		    recWindow.getRecTransporter().setItemCode(itemCode);
		}
		recTransporter.setItemCode(itemCode);
		if(recWindow!=null){
		    recWindow.getRecTransporter().setItemCode(itemCode);
		}
        try {
            prompter.prepare();
//            recWindow.getPromptViewer().prepare();
        } catch (PromptPresenterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
                    "Prompt plugin error", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
	}
	
	public void setRecIndex(Integer recIndex) {
		prompter.setRecIndex(recIndex);
//		recWindow.getPromptViewer().setRecIndex(recIndex);
	}
	
	public void setRecMonitorsStatus(StartStopSignal.State status){
		if(recMonitor!=null){
			recMonitor.setStartStopSignalStatus(status);
		}
		if(recWindow!=null){
			RecMonitor wMonitor=recWindow.getRecMonitor();
			if(wMonitor!=null){
				wMonitor.setStartStopSignalStatus(status);
			}
		}
	}
	

	public void init() {
	    setRecMonitorsStatus(StartStopSignal.State.OFF);
	    progressViewer.setEnabled(false);
	    setIndexAction.setEnabled(false);
//		setEditingEnabled(true);
		miSkipSettings.setEnabled(false);
		//setEnableEditing(true);
         recTransporter.setKeyButtonBindingEnabled(false);
         prompter.addPromptViewerListener(this);
         prompter.init();
//         recWindow.getPromptViewer().addPromptViewerListener(this);
//         recWindow.getPromptViewer().init();
//         miDisplaySettings.setEnabled(true);
         toggleSubjectDisplayAction.setEnabled(true);
	}

	public void idle() {
	    progressViewer.setEnabled(true);
//		try {
//            prompter.prepare();
////            recWindow.getPromptViewer().prepare();
//        } catch (PromptPresenterException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
//                    "Prompt plugin error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
		
        updateTimer.start();
//		setScriptPositioningEnabled(true);
		setIndexAction.setEnabled(true);
		
//		editScriptAction.setEnabled(true);
//		importScriptAction.setEnabled(true);
		recTransporter.setKeyButtonBindingEnabled(true);
	}
    
   
    
    public void setPromptStartControlEnabled(boolean enabled){
        prompter.setStartControlEnabled(enabled);
//        recWindow.getPromptViewer().setStartControlEnabled(enabled);
    }
    
    public void setShowPromptViewers(boolean showPromptViewers) {
        prompter.setShowPrompt(showPromptViewers);
    }
    
    public void startPromptAutoplay() throws PrompterException{
		prompter.autoPlay();
	}
    
//	public void startPromptViewers() {
//		promptViewer.start();
//        recWindow.getPromptViewer().start();
//	}
    

	public void closeSession() {
		setRecMonitorsStatus(StartStopSignal.State.OFF);
		if(scriptUIDialog!=null){
			scriptUIDialog.setScript(null);
		}
//		try {
//			setPromptItem(null);
//		} catch (PromptPresenterException e) {
//			e.printStackTrace();
//		}
        recTransporter.setKeyButtonBindingEnabled(false);
        setTitle(defaultTitle);
	    updateTimer.stop();
//		setEditingEnabled(false);
	    setIndexAction.setEnabled(false);
		
		miImport.setEnabled(true);
		miNew.setEnabled(true);
		openSubMenu.setEnabled(true);
		miClose.setEnabled(false);
		sessionViewer.setData(null);
		setShowPromptViewers(false);
		closePrompt();
		prompter.removePromptViewerListener(this);
		try {
			setPromptItem(null);
		} catch (PromptPresenterException e) {
			e.printStackTrace();
		}
//		miDisplaySettings.setEnabled(false);
		toggleSubjectDisplayAction.setEnabled(false);
		
		if(spkScreenConfig !=null){
			GraphicsDevice spkGd=spkScreenConfig.getDevice();
			if(spkGd!=null && spkGd.isFullScreenSupported()){
				spkGd.setFullScreenWindow(null);
			}
		}
		
		if(recWindow!=null){
			
		    //		recWindow.getPromptViewer().removePromptViewerListener(this);
		    prompter.removeSubjectViewer(recWindow.getPromptViewer());
		    recWindow.detachFromRecStatus();
		    Window w=recWindow.getWindow();
		    SystemHelper.disposeWindowForReuse(w);
		    // Reuse window
		    //recWindow=null;
		}
	
	}

	public void handleQuit() {
		boolean confirmed = true;
		try {
			confirmed = speechRecorder.close();
		} catch (AudioControllerException e) {
            e.printStackTrace();
			displayError("Audiocontroller error", e.getLocalizedMessage());
		} catch (StorageManagerException e) {
            e.printStackTrace();
			displayError("Storage error", e.getLocalizedMessage());
		} catch (WorkspaceException e) {
			e.printStackTrace();
            displayError("Workspace error", e.getLocalizedMessage());
		}
		if (confirmed)
			speechRecorder.shutdown();
		//System.exit(0);
	}

    public void updateView(){
        if (levelMeterMode==LEVEL_METER_PLAYBACK){
            setPlaybackLevel();
		} else if (levelMeterMode==LEVEL_METER_CAPTURE || levelMeterMode == LEVEL_METER_RECORDING ) {
               setCaptureLevel();
        }
        long playbackPos=speechRecorder.getAudioController().getPlaybackFramePosition();
        audioUI.getAudioClip().setFramePosition(playbackPos);
       
    }
    
    
	// ActionListener interface (for menus)
	public void actionPerformed(ActionEvent newEvent) {
		Object eventSource = newEvent.getSource();
		if(eventSource==updateTimer){
		    
		       updateView();
		    
		}else{
		for (int i = 0; i < miAvailableProjects.length; i++) {
			if (eventSource == miAvailableProjects[i]) {
				doOpenProject(newEvent.getActionCommand());
			}
		}
        String actionCommand=newEvent.getActionCommand();
		if (eventSource==miNew)
			doNew();
			// else if
			// (newEvent.getActionCommand().equals(miOpen.getActionCommand()))
		//			doOpen();
		else if (eventSource==miWorkspace)
            doWorkspaceUI();
		else if (eventSource==miClose)
			doClose();
		else if (eventSource==miImport)
			doImportProject();
			else if (eventSource==miExport)
				doExportProject();
            else if (eventSource==miPrint)
                doPrint();
		else if (eventSource==miSave)
			doSave();
		//		else if (
		//			newEvent.getActionCommand().equals(miSaveAs.getActionCommand()))
		//			doSaveAs();
		else if (eventSource==miQuit)
			doQuit();

			// else if
			// (newEvent.getActionCommand().equals(miUndo.getActionCommand()))
		//			doUndo();
			// else if
			// (newEvent.getActionCommand().equals(miCut.getActionCommand()))
		//			doCut();
			// else if
			// (newEvent.getActionCommand().equals(miCopy.getActionCommand()))
		//			doCopy();
			// else if
			// (newEvent.getActionCommand().equals(miPaste.getActionCommand()))
		//			doPaste();
			// else if
			// (newEvent.getActionCommand().equals(miClear.getActionCommand()))
		//			doClear();
			// else if
			// (newEvent.getActionCommand().equals(miSelectAll.getActionCommand()))
		//			doSelectAll();

		//		else if (
		//			newEvent.getActionCommand().equals(
		//				miUploadCache.getActionCommand()))
		//			doUploadCacheView();
		else if(eventSource==recLevel){
			resetPeakLevelHolds();
		}
		else if (actionCommand.equals(miProjectSettings.getActionCommand()))
			doProjectSettings();
		else if (actionCommand.equals(miSpkSettings.getActionCommand()))
			doSpkSettings();
		else if (actionCommand.equals(miSpkTableExport.getActionCommand()))
            doSpkTableExport();
			// else if
			// (newEvent.getActionCommand().equals(miDeviceSettings.getActionCommand()))
//			doDeviceSettings();
//		else if (actionCommand.equals(miSessionClipView.getActionCommand()))
//			doSessionClipsView();
		else if (actionCommand.equals(miRecSettings.getActionCommand()))
			doRecSettings();
//		else if(actionCommand.equals(miEditScript.getActionCommand()))
//			doEditScript();
        else if(actionCommand.equals(miEditScriptSrc.getActionCommand()))
            doEditScriptSource();
        else if(actionCommand.equals(miImportScript.getActionCommand()))
            doImportScript();
        else if(actionCommand.equals(miExportScript.getActionCommand()))
            doExportScript();
        else if(actionCommand.equals(miPrintScript.getActionCommand()))
            doPrintScript();
		else if (actionCommand.equals(miSkipSettings.getActionCommand()))
			doSkipSettings();
		else if (actionCommand.equals(miViewSettings.getActionCommand()))
			doViewSettings();
//		else if (actionCommand.equals(miDisplaySettings.getActionCommand()))
//			doDisplaySettings();
		else if(actionCommand.equals(miInfo.getActionCommand()))
            doInfo();
		else if(actionCommand.equals(miContact.getActionCommand()))
            doContact();
	
        else if(actionCommand.equals(miAbout.getActionCommand())){
            doAbout();
        }else if(actionCommand.equals(miCheckUpdates.getActionCommand()))
            doCheckUpdates();
        }
            
        
	}

	

	/**
     * 
     */
    private void doSpkTableExport() {
        if(exportSpeakersDialog==null){
            // TODO Fix speakers DB MVC model!!
            exportSpeakersDialog=new ExportSpeakersUIDialog();
        }
        Speakers speakers=speechRecorder.getSpeakerManager().getDatabaseLoader().getSpeakersDb();
        exportSpeakersDialog.setSpeakers(speakers);
        Object retVal=exportSpeakersDialog.showDialog(this);
   
     
    }

    /**
	 * 
	 */
	private void doSessionClipsView() {
		List<AudioClip> acList=speechRecorder.getSessionClipList();
		if(acList!=null){
			System.out.println("Session recording count: "+acList.size() );
			// TODO as field
//			SessionAudioClipsUI sacsUi=new SessionAudioClipsUI();
			AudioClipsUIContainer sacsUi=new AudioClipsUIContainer();
			// TODO close audio clips on window close!!
			sacsUi.setAudioClips(acList);
			JScrollPane sp=new JScrollPane(sacsUi);
//			sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			JFrame f=new JFrame();
			f.getContentPane().add(sp);
			f.setVisible(true);
			
//			sacsUi.showDialog(this);
			sacsUi.setFixXZoomFitToPanel(false);
			sacsUi.setXZoom(400);
			f.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
	}

	/**
     * 
     */
    public void doExportScript() {
        if(exportScriptDialog==null){
            exportScriptDialog=new ExportScriptUIDialog();
        }
        Script script=RecScriptManager.getInstance().getScript();
        exportScriptDialog.setScript(script);
        Object retVal=exportScriptDialog.showDialog(this);
   
      if(!retVal.equals(ScriptUIDialog.CANCEL_OPTION)){
         

      }
    }

    /**
     * 
     */
    public void doPrintScript() {
        
        // do not use JTable print
        // on Linux (and...?) it does not print non-ASCII chars correctly (Aug 2014, sitll the same encoding problems!!)
        // the table is printed as shown on the screen (this is not what I want)
        
//        // use the table of the progress
//        JTable scriptTable=progressViewer.getProgressViewTable();
//        MessageFormat footer = new MessageFormat("Page - {0}");
//        try {
//            scriptTable.print(PrintMode.FIT_WIDTH, footer,new MessageFormat("Script: "+speechRecorder.getRecScriptName()), true, null, true);
//        } catch (HeadlessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (PrinterException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
      
    }

    public void doImportScript() {
     
       speechRecorder.init();
       RecScriptManager recScriptManager=RecScriptManager.getInstance();
       if(migrateScriptDTDIfRequired()){

           Script script=recScriptManager.getScript();
           speechRecorder.resetItemcodeGenerator();
           ItemcodeGenerator icg=speechRecorder.getItemcodeGenerator();
           if(importScriptDialog==null){
               importScriptDialog = new ImportScriptUIDialog(icg);
           }
           Object retVal=importScriptDialog.showDialog(this);
           
         
         
//           Object retVal=scriptUIDialog.showDialog(this,Dialog.ModalityType.DOCUMENT_MODAL);
           if(!retVal.equals(ScriptUIDialog.CANCEL_OPTION)){
               //          speechRecorder.setScriptSaved(false);
              
               Section newSection=importScriptDialog.getSection();
               if(newSection!=null){
            	   script.getSections().add(newSection);
            	   newSection.setScript(script);
            	   recScriptManager.setScript(script);
            	   recScriptManager.setScriptSaved(false);
               }
           }
//         scriptUIDialog.showNonModalDialog(this);
       }
       try {
    	   speechRecorder.start();
       } catch (AudioControllerException e) {
    	   JOptionPane.showMessageDialog(this,"Audio controller error:\n"+ e.getLocalizedMessage(),
    			   "Session start error", JOptionPane.ERROR_MESSAGE);
    	   e.printStackTrace();
       }
    }
	
	
    private void doAbout(){
        recTransporter.setKeyButtonBindingEnabled(false);
        SplashPanel splash=new SplashPanel();
      
//        JOptionPane.showMessageDialog(this, splash, "About",
//                JOptionPane.INFORMATION_MESSAGE);
        
        // JEditorPane in SplashPanel is problematic here
        JFrame frame=new JFrame("About");
        frame.setIconImages(iconImages);
        frame.getContentPane().add(splash);
        frame.pack();
        frame.setVisible(true);
       
      
    }

    private void doInfo() {
        recTransporter.setKeyButtonBindingEnabled(false);
        infoViewer.setData();
        JOptionPane.showMessageDialog(this, infoViewer, "Info",
                JOptionPane.INFORMATION_MESSAGE);
        
    }
    private void doContact(){
        URI contactUri=null;
        try {
            contactUri = new URI(SpeechRecorder.CONTACT_URI);
        } catch (URISyntaxException e) {
            JOptionPane.showMessageDialog(this,"Intern URI syntax error:\n"+ e.getLocalizedMessage(),
                    "Contact mail error", JOptionPane.ERROR_MESSAGE);
        }
        if(Desktop.isDesktopSupported()){
            Desktop desktop=Desktop.getDesktop();
            
            try {
                desktop.mail(contactUri);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Could not open mail application:\n"+e.getLocalizedMessage(),
                        "Contact mail error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void doCheckUpdates(){
        UpdateManager updateManager=speechRecorder.getUpdateManager();
        if(updateManager!=null){
            
            UpdateDialogUI updateDialog = new UpdateDialogUI(updateManager);
            Object res=updateDialog.showDialog(this);
            if(res instanceof UpdateDialogUI.DownloadActionOption){
                UpdateDialogUI.DownloadActionOption dao=(UpdateDialogUI.DownloadActionOption)res;
                ApplicationVersionDescriptor appDs=dao.getApplicationVersionDescriptor();
                try {
                    updateManager.desktopBrowseApplicationDownload(appDs);
                    if(dao.isRequestApplicationQuit()){
                        handleQuit();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Could not browse "+appDs+": "+e.getLocalizedMessage(), "Open download URL error", JOptionPane.ERROR_MESSAGE);
                }

            }
            //    		
        }
    }
    
    /**
	 * @param projectName name of the project
	 */
	public void doOpenProject(String projectName) {
        speechRecorder.init();
		setWaiting(true);
		try {
			speechRecorder.openProject(projectName);
		} catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Could not load plugin class: "+e.getLocalizedMessage(),
                    "Project open", JOptionPane.ERROR_MESSAGE);
            try {
                speechRecorder.close();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(),
                        "Project close", JOptionPane.ERROR_MESSAGE);
            }
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Project open", JOptionPane.ERROR_MESSAGE);
			try {
				speechRecorder.close();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(this, e1.getLocalizedMessage(),
						"Project close", JOptionPane.ERROR_MESSAGE);
			}
		} finally {

			setWaiting(false);
		}
	}

	public void doNew() {
//        speechRecorder.init();
		
		NewProjectConfiguration newProject = new NewProjectConfiguration();
		Object selectedValue = NewProjectDialog.showDialog(this, newProject,
				speechRecorder.getDefWorkspaceDir());
		if (selectedValue == null)
			return;
		if (selectedValue instanceof Integer) {

			int value = ((Integer) selectedValue).intValue();
			if (value == JOptionPane.OK_OPTION) {
				setWaiting(true);
				repaint();
				
				try {
					speechRecorder.close();
					speechRecorder.newProject(newProject);
					showSpeakerDatabase();
					speechRecorder.start();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this,
							e.getLocalizedMessage(), "New Project",
						JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();

				}finally{	
				setWaiting(false);
				}
			} else {
				return;
			}
		}
	}

//	public void doOpen() {
//        speechRecorder.init();
//		try {
//			JFileChooser fc = new JFileChooser();
//			int returnVal = fc.showOpenDialog(this);
//			if (returnVal == JFileChooser.APPROVE_OPTION) {
//				setWaiting(true);
//				File file = fc.getSelectedFile();
////				speechRecorder.setProjectURL(file.toURL());
//				URL projURL=file.toURI().toURL();
//				speechRecorder.setProjectURL(projURL);
////				speechRecorder.openProject(file.toURL());
//				speechRecorder.configureProject(projURL);
//				showSpeakerDatabase();
//				speechRecorder.start();
//			}
//		} catch (Exception e) {
//			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
//					"Project close", JOptionPane.ERROR_MESSAGE);
//		} finally {
//			setWaiting(false);
//		}
//	}

	public void doClose() {
		try {
			speechRecorder.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Project close", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void doImportProject() {
        speechRecorder.init();
		JFileChooser fc = new JFileChooser();
		// accept only ZIP files
		
		fc.setFileFilter(new ZipFileFilter());
		fc.setAcceptAllFileFilterUsed(true);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			setWaiting(true);
			File file = fc.getSelectedFile();
			try {
//				ZipFile zipFile = new ZipFile(file);
				speechRecorder.importProject(file);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
						"Project import", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			} finally {
				setWaiting(false);
			}
		}
	}

	public void doExportProject() {
		if(speechRecorder.saveAllProjectDataInteractive()){
			JFileChooser fc = new JFileChooser();
			// accept only ZIP files
//			fc.addChoosableFileFilter(new ZipFileFilter());
//			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new ZipFileFilter());
			fc.setAcceptAllFileFilterUsed(true);
			fc.setSelectedFile(new File(speechRecorder.getConfiguration().getName()+".zip"));
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				File file = fc.getSelectedFile();
				// TODO localize !
				if (!file.exists()
						|| JOptionPane.showConfirmDialog(this, file.getName()
								+ " exists. Do you want to overwrite ?",
								"Overwrite file ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

					setWaiting(true);
					try {

						speechRecorder.exportProject(file);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(this,
								e.getLocalizedMessage(), "Project export",
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					} finally {
						setWaiting(false);
					}
				}
			}
		}
	}
    
    private void doPrint(){
            PrinterJob printJob = PrinterJob.getPrinterJob();
            ComponentPrinter cp=new ComponentPrinter(this.getContentPane());
            printJob.setPrintable(cp);
            if (printJob.printDialog())
              try {
                printJob.print();
              } catch(PrinterException e) {
                  JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
                        "Print error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return;
              }
    }
    
	public void doSave() {
		try {
			speechRecorder.saveProject();
			speechRecorder.saveScript();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Project save", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public void doSaveAs() {
        speechRecorder.init();
		try {
			DOMCodec dc = new DOMCodec();
			Document d = dc.createDocument(speechRecorder.getConfiguration());
			JFileChooser fc = new JFileChooser();

			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				DOMConverter dco = new DOMConverter();
				dco.writeXML(d, new OutputStreamWriter(new FileOutputStream(file),Charset.forName("UTF-8")));
				URL projURL=file.toURI().toURL();
				speechRecorder.setProjectURL(projURL);
				miSave.setEnabled(speechRecorder.getConfiguration()
						.getEditable());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Project save as", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void doQuit() {
		handleQuit();
	}

	public void doUndo() {
	}

	public void doCut() {
	}

	public void doCopy() {
	}

	public void doPaste() {
	}

	public void doClear() {
	}

	public void doSelectAll() {
	}
	
	
	public void doWorkspaceUI(){
	    if(workspaceDialog==null){
          workspaceDialog=new WorkspacePanel(speechRecorder.getWorkspaceManager());
        }
	    workspaceDialog.showDialog(this);
	    // TODO Ugly, implement clean observer pattern
	    List<WorkspaceProject> wsPrjs=speechRecorder.getWorkspaceManager().getWorkspaceProjects();
	    
	    setWorkspaceProjects(wsPrjs.toArray(new WorkspaceProject[wsPrjs.size()]));
	}

	/**
	 * Shows project configuration window.
	 *
	 */
	public void doProjectSettings() {
        speechRecorder.init();
		ProjectConfiguration newProject = null;
		URL projectUrl = speechRecorder.getProjectURL();
		try {
			newProject = speechRecorder.getConfigurationCopy();
			if(promptConfigurationView==null){
			    promptConfigurationView = new ProjectConfigurationView(
			            newProject,speechRecorder.getAudioController(),SpeechRecorder.SESSION_ACTIONS,speechRecorder.getBundleAnnotationPersistorServiceDescriptors(),speechRecorder.getAutoAnnotatorPluginManager(),speechRecorder.defaultScriptUrlString(),helpBroker);
			}
			promptConfigurationView.setProjectConfiguration(newProject);
			promptConfigurationView.setProjectContext(projectUrl);
			promptConfigurationView.setIconImages(iconImages);
			Object selectedValue=promptConfigurationView.showDialog((JFrame)null);
			
			if (selectedValue !=null && selectedValue instanceof Integer) {
				int value = ((Integer) selectedValue).intValue();
				if (value == JOptionPane.OK_OPTION) {
					setWaiting(true);
					speechRecorder.close();
					
					speechRecorder.setProjectURL(projectUrl);
					
					newProject=promptConfigurationView.getProjectConfiguration();
					
					// checks
					AudioFormat newAudioFormat=newProject.getRecordingConfiguration().getFormat().toAudioFormat();
					Profile profile=Profile.SPEECH_RECORDING;
					List<LocalizableMessage> fmtWarns=AudioFormatUtils.getFormatQualityWarningsForProfile(newAudioFormat, profile);
					 if(fmtWarns!=null && fmtWarns.size()>0){
			                StringBuffer msgSb=new StringBuffer("Recommended settings for "+profile+":\n");
			                
			                for(LocalizableMessage fmtwarn:fmtWarns){
			                    msgSb.append(fmtwarn.localize());
			                    msgSb.append('\n');
			                }
			                JOptionPane.showMessageDialog(this, msgSb, "Audio format quality", JOptionPane.WARNING_MESSAGE);
			            }
					boolean canceled=speechRecorder.configure(newProject);
					
					speechRecorder.setProjectConfigurationSaved(false);
					if(!canceled){
						showSpeakerDatabase();
						speechRecorder.start();
					}
				} else {
                    speechRecorder.start();
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Project change", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
		} finally {
			setWaiting(false);
		}
	}

       
	public void showSpeakerDatabase(){
		boolean waitStatusBefore = isWaiting();
		setWaiting(false);
		//	Some components are realized before, so we should run the show method
		// after all events are dispatched
//		SpeakerManager speakerManager = speechRecorder.getSpeakerManager();
		Runnable doSpeakerShow = new Runnable() {
			//private SpeakerDatabaseViewer spkDbView;

           

            public void run() {
                if(spkDbView==null){
                    spkDbView = new SpeakerDatabaseViewer(
                            speechRecorder.getSpeakerManager());
                    spkDbView.setIconImages(iconImages);
                }
				spkDbView.displayViewer();
			}
		};

		// If this method is called by the "open" or "New" menu item, the
		// current
		// this method is run by the event dispatch thread and we can call show
		// directly
		if (java.awt.EventQueue.isDispatchThread()) {
			doSpeakerShow.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(doSpeakerShow);
			} catch (Exception e) {
				System.err.println("Speaker selection error !! "
						+ e.getLocalizedMessage());
				return;
			} finally {
				setWaiting(waitStatusBefore);
			}
		}
		setWaiting(waitStatusBefore);
//		if (!speakerManager.isDatabaseSaved()) {
//			setProjectConfigurationSaved(false);
//		}
		//speakerViewer.setData(speechRecorder.getSpeaker());
		sessionViewer.setData(speechRecorder.getSpeaker());

		// If Speechrecorder crashes we loose all new speakers entered by the experimenter,
		// so we always save the database.
		try {
			speechRecorder.saveSpeakerDatabase();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
					"Speaker database saving error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	/**
     * doSpkSettings() calls the speaker selection method of SpeechRecorder
     * 
     */
    public void doSpkSettings() {

        speechRecorder.init();
        showSpeakerDatabase();
        SpeakerManager speakerManager = speechRecorder.getSpeakerManager();
        if (speakerManager.getSpeaker() == null) {
            speechRecorder.init();
        } else {
        	try {
        		speechRecorder.start();
        	} catch (AudioControllerException e) {
        		JOptionPane.showMessageDialog(this,"Audio controller error:\n"+ e.getLocalizedMessage(),
        				"Session start error", JOptionPane.ERROR_MESSAGE);
        		e.printStackTrace();
        	};
        }
    }
    
    private boolean migrateScriptDTDIfRequired(){
        RecScriptManager recScriptManager=RecScriptManager.getInstance();
        boolean dtdOK=true;
        try {
            if(recScriptManager.isNewVersionOfDTDFileRequired()){
                int val=JOptionPane.showConfirmDialog(this, "The recording script needs needs to be migrated to a newer format. The converted script cannot be used with older Speechrecorder versions!", "Recording script format migration", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if(val==JOptionPane.OK_OPTION){
                    recScriptManager.createDTDFileIfRequired();
                }else{
                    dtdOK=false;
                }
            }
        }catch (IOException e) {
            
            dtdOK=false;
           
        }
        return dtdOK;
    }
    
	public void doEditScript() {
			doEditScript(null);
    }
	
	public void doEditScript(PromptItem requestPromptItem) {
	    RecScriptManager recScriptManager=RecScriptManager.getInstance();
	    SessionManager sessionManager=SessionManager.getInstance();
	    if(requestPromptItem==null){
            requestPromptItem=sessionManager.getCurrentPromptItem();
        }
	    speechRecorder.init();
	    if(migrateScriptDTDIfRequired()){

	        Script script=recScriptManager.getScript();
	     
	        //		if (scriptUIDialog==null){
//	        Set<List<String>> availMimes=new HashSet<List<String>>();
//	        List<PromptPresenter> availPromptPresenters=new ArrayList<PromptPresenter>();
//	        for(PromptPresenterServiceDescriptor ppClass:promptPresentersClassList){
//	            
//	            String[][] ppMimesArrs=ppClass.getSupportedMIMETypes();
//	            for(String[] ppMimesArr:ppMimesArrs){
//	                List<String> ppMimesL=Arrays.asList(ppMimesArr);
//	                availMimes.add(ppMimesL);
//	            }
//	        }
	        speechRecorder.resetItemcodeGenerator();
	        ItemcodeGenerator icg=speechRecorder.getItemcodeGenerator();
	        if(scriptUIDialog==null){
	            scriptUIDialog=new ScriptUIDialog(speechRecorder.getProjectContext(),icg,promptPresentersClassList,helpBroker);
	            scriptUIDialog.setPromptFontFamilies(promptFontFamilies);
	            scriptUIDialog.setInstructionsFontFamilies(instructionsFontFamilies);
	            scriptUIDialog.setDescriptionFontFamilies(descriptionFontFamilies);
	        }
	        // Java Help for editor disabled: Help frame is not usable because editor dialog is modal.
//	        scriptUIDialog=new ScriptUIDialog(speechRecorder.getProjectContext(),availPromptPresenters);
	        //		}
	        ScriptUI scriptUI=scriptUIDialog.getScriptUI();
	        scriptUI.setDefaultSectionMode(recScriptManager.getDefaultMode());
	        scriptUI.setDefaultPreRecording(recScriptManager.getDefaultPreDelay());
	        scriptUI.setDefaultPostRecording(recScriptManager.getDefaultPostDelay());
	        scriptUI.setDefaultPromptAutoPlay(recScriptManager.isDefaultAutomaticPromptPlay());
	        Script editScript=null;
	        DOMCodec dc;
	        try {
	            dc = new DOMCodec();
	            editScript=(Script) dc.copy(script);
	        } catch (DOMCodecException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

	        //        try {
	        //            editScript = (Script)script.clone();
	        //        } catch (CloneNotSupportedException e) {
	        //            //
	        //        }
	        scriptUIDialog.setScript(editScript);
	        scriptUIDialog.setSelectedPromptItem(requestPromptItem);
	        //		if(pi!=null){
	        //		 scriptUIDialog.setSelectedPromptItem(pi);
	        //		}
	        
	        Object retVal=scriptUIDialog.showDialog(this,Dialog.ModalityType.DOCUMENT_MODAL);
	        if(!retVal.equals(ScriptUIDialog.CANCEL_OPTION)){
	            //		    speechRecorder.setScriptSaved(false);
	        	recScriptManager.setScript(editScript);
	        	recScriptManager.setScriptSaved(false);
	        }
//	        scriptUIDialog.showNonModalDialog(this);
	    }
	    try {
	    	   speechRecorder.start();
	       } catch (AudioControllerException e) {
	    	   JOptionPane.showMessageDialog(this,"Audio controller error:\n"+ e.getLocalizedMessage(),
	    			   "Session start error", JOptionPane.ERROR_MESSAGE);
	    	   e.printStackTrace();
	       };
	   
	}
    
	private void doEditScriptSource() {
	    speechRecorder.init();
	    RecScriptManager recScriptManager=RecScriptManager.getInstance();
	    if(migrateScriptDTDIfRequired()){
	        Script script=recScriptManager.getScript();
//	        String scriptDTD=recScriptManager.getSystemId();
	        if(scriptSrcEditor==null){
	            scriptSrcEditor=new ScriptSourceEditor();
	        }
	        String xmlSystemID=speechRecorder.getProjectContext().toExternalForm();
	        scriptSrcEditor.setSystemIdBase(xmlSystemID);
	        scriptSrcEditor.setSystemId(RecScriptManager.REC_SCRIPT_DTD);
	        scriptSrcEditor.setScript(script);  

	        Object retVal=scriptSrcEditor.showDialog(this);
	        if(!retVal.equals(ScriptUIDialog.CANCEL_OPTION)){
	        	speechRecorder.setScriptSaved(false);
	        }
	    }
	    try {
	    	speechRecorder.start();
	    } catch (AudioControllerException e) {
	    	JOptionPane.showMessageDialog(this,"Audio controller error:\n"+ e.getLocalizedMessage(),
	    			"Session start error", JOptionPane.ERROR_MESSAGE);
	    	e.printStackTrace();
	    };
	}

//	/**
//     * 
//     */
//    public void doDeviceSettings() {
//        setWaiting(true);
//        MixerManager mixerManager=speechRecorder.getMixerManager();
//        if (devicesUI == null) {
//			
//			try {
//				devicesUI = new MixerManagerUI(mixerManager);
//			} catch (LineUnavailableException e) {
//				JOptionPane.showMessageDialog(this,
//						e.getLocalizedMessage(), "Audio devices error",
//						JOptionPane.ERROR_MESSAGE);
//				return;
//			} finally {
//				setWaiting(false);
//			}
//
//		}
//        setWaiting(false);
//		int result = JOptionPane.showOptionDialog(this, devicesUI, "Mixer",
//				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
//				null, null, null);
//		if (result == JOptionPane.OK_OPTION) {
//			mixerManager.setSelectedPlaybackMixer(devicesUI
//					.getSelectedPlaybackMixerInfo());
//			mixerManager.setSelectedCaptureMixer(devicesUI
//					.getSelectedCaptureMixerInfo());
//		}
//      
//      
//    }
	public void doRecSettings() {
        //recTransporter.setKeyButtonBindingEnabled(false);
		if (mixerUI == null) {
			setWaiting(true);
			try {
				mixerUI = new PortMixersUI();
			} catch (LineUnavailableException e) {
				setWaiting(false);
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage(),
						"Mixer device error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
			setWaiting(false);
		}
//		JOptionPane optPane = new JOptionPane(mixerUI);
//		JDialog mixerDialog = optPane.createDialog(this, "Audio mixer");
//		mixerDialog.setModal(false);
//		mixerDialog.setResizable(true);
//		mixerDialog.setVisible(true);
		mixerUI.showDialog(this);
	}

	public void doSkipSettings() {
        
        recTransporter.setKeyButtonBindingEnabled(false);
		String s = JOptionPane.showInputDialog(uiString
				.getString("SkipNItemsText"));
		 if(DEBUG){
             // see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4387314
             
             
             Window[] owOws=Window.getOwnerlessWindows();
             System.out.println("has "+owOws.length+" ownerless windows.");
             for(Window w:owOws){
                 System.out.println(w);
             }
             
            
         }
        if (s != null) {
            try {
            int skipTo=Integer.parseInt(s);
            speechRecorder.setRecIndex(skipTo);
            }catch(NumberFormatException nfe){
				JOptionPane.showMessageDialog(null, s + " is not a number !",
						"Warning", JOptionPane.WARNING_MESSAGE);
            }catch(IllegalArgumentException iae){
            
            JOptionPane.showMessageDialog(null,
                    "Cannot skip to index "+s, "Warning",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        recTransporter.setKeyButtonBindingEnabled(true); 
	}

	public void doViewSettings() {
		//speechRecorder.playRecording();
	}

	public void doDisplaySettings() {
		//		if (recWindow.isWindowActive()) {
		//			recWindow.setWindowActive(false);
		//			promptViewer.setSilent(true);
		//			miDisplaySettings.setState(true);
		//		} else {
		//			recWindow.setWindowActive(true);
		//			promptViewer.setSilent(false);
		//			miDisplaySettings.setState(false);
		//		}
		if(recWindow!=null){
			boolean recWindowActive=recWindow.isWindowActive();
			speechRecorder.setSpeakerWindowShowing(!recWindowActive);
		}
		
	}

	/**
	 * @return upload user interface 
	 */
	public JComponent getUploadCacheUI() {
		//if (uploadCacheUI == null){
		return new UploadCacheUI(speechRecorder.getUploadCache());
		//}
		//return uploadCacheUI;
	}

//	public RecDisplay getRecDisplay() {
//		return recDisplay;
//	}
	
	
    
    /**
	 * Returns currently active window. (speaker or experimenter window)
	 * 
     * @return active window
     */
    public Component getActiveComponent(){
        
        if (recWindow !=null && recWindow.isWindowActive()) {
            return recWindow.getWindow();
        } else {
            return(this);
        }
    }
    
    /**
	 * Returns the window that should be used for dialog messages. If there is
	 * only one screen and the speaker window is active the speaker window is
	 * returned. Otherwise the experimenter window is returned.
	 * 
     * @return speaker or experimenter window
     */
    public Component getDialogTarget(){
        if (expScreenConfig==spkScreenConfig){
            // only one screen
            return getActiveComponent();
        }else{
            // dual graphics device mode
            // dialog with experimenter
            return (this);
        }
        
    }
    
    public void displayError(String title, Throwable cause ) {
		JOptionPane.showMessageDialog(getDialogTarget(), cause
				.getLocalizedMessage(), title, JOptionPane.ERROR_MESSAGE);
        cause.printStackTrace();
    }
    
	public void displayError(String title, String errMsg) {
		JOptionPane.showMessageDialog(getDialogTarget(), errMsg, title,
				JOptionPane.ERROR_MESSAGE);
		System.err.println(errMsg);
	}

	/**
	 * @param b
	 */
	public void setProjectConfigurationSaved(boolean b) {
		boolean editable = speechRecorder.getConfiguration().getEditable();
		miSave.setEnabled(!b && editable);
	}

	public void setWorkspaceProjects(WorkspaceProject[] workspaceProjects) {
		openSubMenu.removeAll();
		miAvailableProjects = new JMenuItem[workspaceProjects.length];
		if (workspaceProjects.length > 0) {
			for (int i = 0; i < workspaceProjects.length; i++) {
				miAvailableProjects[i] = new JMenuItem(workspaceProjects[i]
						.getConfiguration().getName());
				openSubMenu.add(miAvailableProjects[i]);
				String description = workspaceProjects[i].getConfiguration()
						.getDescription();
				if (!description.equals("")) {
					miAvailableProjects[i].setToolTipText(description);
				}
				miAvailableProjects[i].addActionListener(this);
			}
			//			openSubMenu.addSeparator();
		}
		//		openSubMenu.add(miOpen);
	}

	/**
	 * @param b
	 */
	void setWaiting(boolean b) {
		waiting = b;
		if (waiting)
			getContentPane().setCursor(waitCursor);
		else
			getContentPane().setCursor(defCursor);

	}

	/**
	 * @param b
	 */
	public void setEnableOpenOrNewProject(boolean b) {
		miNew.setEnabled(b);
		miImport.setEnabled(b);
		openSubMenu.setEnabled(b);
	}
	
	/**
	 * @return true if waiting (cursor shows clock)
	 */
	public boolean isWaiting() {
		return waiting;
	}

	/**
	 * @param showWindow true if window should be visible
	 */
	public void setSpeakerWindowShowing(boolean showWindow) {
		
		assert EventQueue.isDispatchThread();
		
	    toggleSubjectDisplayAction.setSelected(showWindow);
	    
	    recWindow.setWindowActive(showWindow);
	    
	    Window w=recWindow.getWindow();
	   
	    if( w instanceof Frame){
	    	Frame f=(Frame)w;

	    	if (showWindow) {
	    		// pack the window (necessary (only ?) for Linux and windows in some rare cases)
	    		if(!fullScreenMode){
	    			f.pack();
	    		}
//	    		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    	}else{
	    		// Bug on Max OS X Oracle Java 7 u 51:
	    		// switching the speaker display on in maximized mode works
	    		// only the first time, the next time the window gets not maximized
	    		
	    		// workaround: set to normal state on hide to trigger a change
	    		if(applyWorkaroundBugID0006){
	    			f.setExtendedState(JFrame.NORMAL);
	    		}
	    	}
	    	
	    	f.setVisible(showWindow);
	    	if(showWindow){
	    		f.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    	}
	    }else{
	    	w.setVisible(showWindow);
	    }
	    
	    if(fullScreenMode){
	    	if(showWindow){
	    		spkScreenConfig.getDevice().setFullScreenWindow(w);
//	    		if(showWindow && applyWorkaroundBugID0007){
//		    		w.setVisible(false);
//		    		w.setVisible(true);
//		    	}
	    	}else{
	    		spkScreenConfig.getDevice().setFullScreenWindow(null);
	    	}
	    }
		
		PromptViewer promptViewer=prompter.getExperimenterViewer();
		promptViewer.setSilent(showWindow);
		
	}

	public LevelMeter getLevelMeter() {
		return recLevel;
	}
//	public void setLevelMeterEnabled(boolean enabled) {
//	    recLevel.setScaleEnabled(enabled);
//		if (enabled) {
//		    
//			updateTimer.start();
//		} else {
//			updateTimer.stop();
//			
//		}
//	}
    public int getLevelMeterMode() {
        return levelMeterMode;
    }
    public void setLevelMeterMode(int levelMeterMode) {
        int lastMode=this.levelMeterMode;
        this.levelMeterMode = levelMeterMode;
        if (levelMeterMode == LEVEL_METER_DISABLE){
        	recLevel.setAudioStatus(AudioStatus.Status.OFF);
            updateTimer.stop();
			abandonLevelDecay();
            if (lastMode==LEVEL_METER_RECORDING || lastMode==LEVEL_METER_CAPTURE){
                resetCaptureIntervalPeakLevel();
                //setCaptureLevel();
                // in line session scope setCaptureLevel sets level to the current value (the line is open !)
                // so we need to reset
                resetCaptureLevel();
            }else if(lastMode==LEVEL_METER_PLAYBACK){
                setPlaybackLevel();
            }
        }else {
           if (levelMeterMode == LEVEL_METER_RECORDING){
        	   recLevel.setAudioStatus(AudioStatus.Status.RECORDING);
            }else if(levelMeterMode==LEVEL_METER_PLAYBACK){
            	recLevel.setAudioStatus(AudioStatus.Status.PLAYBACK);
            }else if(levelMeterMode==LEVEL_METER_CAPTURE){
            	recLevel.setAudioStatus(AudioStatus.Status.CAPTURE);
            }
            setLevelMeterLight((levelMeterMode==LEVEL_METER_CAPTURE));
            updateTimer.start();
        }
    }

    /**
     * 
     */
    private void setPlaybackLevel() {
        if(recLevel!=null){
			LevelInfo[] lis=speechRecorder.getAudioController()
					.getPlaybackLevelInfos();
			recLevel.setLevelInfos(lis);
			if(lis!=null){
				for(LevelInfo li:lis){
					li.resetIntervalPeakLevel();
				}
			}
        }
      
    }

    private void setLevelMeterLight(boolean light){
        if(recLevel!=null){
            if(light){
                recLevel.setTransparency(0.3f);
            }else{
                recLevel.setTransparency(1.0f);
            }
        }
    }
    private void resetCaptureIntervalPeakLevel(){
    	 if(recLevel!=null){
 			LevelInfo[] lis=speechRecorder.getAudioController().getCaptureLevelInfos();
 			if(lis!=null){
 				for(LevelInfo li:lis){
 					li.resetIntervalPeakLevel();
 				}
 			}
 		}
    }
    /**
     * 
     */
    private void setCaptureLevel() {
        if(recLevel!=null){
			LevelInfo[] lis=speechRecorder.getAudioController().getCaptureLevelInfos();
			recLevel.setLevelInfos(lis);
			resetCaptureIntervalPeakLevel();
		}
	}
	
	private void abandonLevelDecay() {
		if (recLevel != null) {
			recLevel.abandonDecay();
        }
    }
	
	/**
     * 
     */
    private void resetCaptureLevel() {
        if(recLevel!=null){
        	LevelInfo[] lis=speechRecorder.getAudioController().getCaptureLevelInfos();
        	if(lis!=null){
        		LevelInfo[] resetLis=new LevelInfo[lis.length];
        		for(int i=0;i<lis.length;i++){
        			resetLis[i]=new LevelInfo();
        			lis[i].mergePeakLevelHold(resetLis[i]);
        		}
        		recLevel.setLevelInfos(resetLis);
        		resetCaptureIntervalPeakLevel();
        	}
        }
    }
    
    private void resetPeakLevelHolds(LevelInfo[] lis){
    	if(lis!=null){
			for(LevelInfo li:lis){
				li.resetPeakLevelHold();
			}
		}
    }
    
    
    private void resetPeakLevelHolds(){
    	if(recLevel!=null){
            LevelInfo[] cLis=speechRecorder.getAudioController().getCaptureLevelInfos();
            resetPeakLevelHolds(cLis);
            LevelInfo[] pLis=speechRecorder.getAudioController()
					.getPlaybackLevelInfos();
            resetPeakLevelHolds(pLis);
            updateView();
        }
    }
    
	public void setEditingEnabled(boolean b) {	
//		 Updates enabled/disabled status of project or speaker setting menu items
        boolean projectEditable=speechRecorder.isProjectEditable();
		editingEnabled=b;
		if(scriptUIDialog!=null)scriptUIDialog.setEnabled(projectEditable && editingEnabled);
		miExport.setEnabled(projectEditable && editingEnabled);

		miClose.setEnabled(projectEditable && editingEnabled);
		miProjectSettings.setEnabled(projectEditable && editingEnabled);
		miSpkSettings.setEnabled(projectEditable && editingEnabled);
		miSpkTableExport.setEnabled(projectEditable && editingEnabled);
		miEditScriptSrc.setEnabled(projectEditable && editingEnabled);
		progressViewer.setEditEnabled(projectEditable && editingEnabled);
	}
	
//    public void setScriptPositioningEnabled(boolean b){
//		scriptPositioningEnabled=b;
//		miSkipSettings.setEnabled(scriptPositioningEnabled);
//		setIndexAction.setEnabled(false);
//	}
    

	public void startPlayPrompt() throws PrompterException {

	    prompter.open();
		prompter.start();

//		PromptViewer recWinPromptViewer=recWindow.getPromptViewer();
//		recWinPromptViewer.open();
//		recWinPromptViewer.start();
	}
    
    public void stopPlayPrompt() {
        
        prompter.stop();
//        recWindow.getPromptViewer().stop();
    }
    
    public void closePrompt() {
        
        prompter.close();
//        recWindow.getPromptViewer().close();
    }
    
    public boolean isPromptClosed() {
        boolean promptViewerClosed=prompter.isPresenterClosed();
//        boolean recWindowPromptViewerClosed=recWindow.getPromptViewer().isClosed();
//        return( promptViewerClosed && recWindowPromptViewerClosed);
        return promptViewerClosed;
    }


	public void update(PromptViewerEvent promptViewerEvent) {
		speechRecorder.update(promptViewerEvent);
	}

	

	public void updateSaveEnable() {
        ProjectConfiguration pc=speechRecorder.getConfiguration();
        if(pc!=null){
		boolean editable = pc.getEditable();
		boolean saved=speechRecorder.isProjectConfigurationSaved() && speechRecorder.getSpeakerManager().isDatabaseSaved() && speechRecorder.isScriptSaved();
		miSave.setEnabled(!saved && editable);
        }
	}

	public PromptViewer getPromptViewer() {
	    PromptViewer promptViewer=prompter.getExperimenterViewer();
		return promptViewer;
	}

	public RecWindow getRecWindow() {
		return recWindow;
	}

    public void setProjectContext(URL projectContext) {
        // progress viewer requires project context to start prompt item editor
       if(progressViewer!=null){
           progressViewer.setProjectContext(projectContext);
       }
       if(scriptUIDialog!=null){
    	   scriptUIDialog.setProjectContext(projectContext);
       }
    }

    public void setRecDisplay(URL[] recUrls) {
        
    }

    public boolean isAutoRecording() {
        return autoRecording;
    }

    public void setAutoRecording(boolean autoRecording) {
        this.autoRecording = autoRecording;
        recTransporter.setAutoRecording(autoRecording);
        recWindow.getRecTransporter().setAutoRecording(autoRecording);
    }

    public boolean isPlaybackEnabled() {
        return playbackEnabled;
    }

    public void setPlaybackEnabled(boolean playbackEnabled) {
        this.playbackEnabled = playbackEnabled;
        recTransporter.setPlaybackEnabled(playbackEnabled);
        recWindow.getRecTransporter().setPlaybackEnabled(playbackEnabled);
    }

    public boolean isProgressPaused() {
        return progressPaused;
    }

    public void setProgressPaused(boolean progressPaused) {
        this.progressPaused = progressPaused;
        recTransporter.setProgressPaused(progressPaused);
        recWindow.getRecTransporter().setProgressPaused(progressPaused);
    }

    public void setStartStopSignalClass(Class<? extends StartStopSignal> startStopSignalPlugin) throws InstantiationException, IllegalAccessException {
        //this.startStopSignalClass=startStopSignalPlugin;
        StartStopSignal startStopSignal=null;
        StartStopSignal startStopSignalRecWindow=null;
       
        startStopSignal = startStopSignalPlugin.newInstance();
        startStopSignalRecWindow=startStopSignalPlugin.newInstance();
       
        recMonitor.setStartStopSignal(startStopSignal);
        recWindow.getRecMonitor().setStartStopSignal(startStopSignalRecWindow);
    }

    public void setPromptPresenterServiceDescriptors(List<PromptPresenterServiceDescriptor> promptPresenterClassList) {
        this.promptPresentersClassList=promptPresenterClassList;
    }

    /**
     * @param autoAnnotatorDescriptors
     */
    public void setAutoAnnotatorServiceDescriptors(
            List<AutoAnnotationServiceDescriptor> autoAnnotatorDescriptors) {
        this.autoAnnotatorDescriptors=autoAnnotatorDescriptors;
    }

   

   

	


	
	
    
    
    
}
