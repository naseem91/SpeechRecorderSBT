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

//
//  SpeechRecorder.java
//  JSpeechRecorder
//
//  Created by Christoph Draxler on Thu Dec 05 2002.
//

package ipsk.apps.speechrecorder;

import ips.annot.BundleAnnotationPersistor;
import ips.annot.BundleAnnotationPersistorServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotator;
import ips.annot.autoannotator.AutoAnnotatorServiceDescriptor;
import ips.annot.autoannotator.impl.ws.bas.g2p.G2PAnnotatorServiceDescriptor;
import ips.annot.autoannotator.impl.ws.bas.g2p.G2PServiceClient;
import ips.annot.autoannotator.impl.ws.bas.maus.BasicMAUSWebServiceClient;
import ips.annot.autoannotator.impl.ws.bas.maus.MAUSServiceClient;
import ips.annot.io.BundleAnnotationFilePersistor;
import ips.annot.model.AnnotatedAudioClip;
import ips.annot.model.PredefinedLevelDefinition;
import ips.annot.model.db.AttributeDefinition;
import ips.annot.model.db.Bundle;
import ips.annot.model.db.Item;
import ips.annot.model.db.Level;
import ips.annot.model.db.LevelDefinition;
import ips.annot.model.db.Schema;
import ips.annot.model.db.Session;
import ips.annot.model.emu.EmuBundleAnnotationPersistor;
import ips.annot.text.SingleLevelTextFilePersistor;
import ips.annot.textgrid.TextGridFileParser;
import ips.annot.textgrid.TextGridFilePersistor;
import ipsk.apps.speechrecorder.actions.AdvanceToNextAction;
import ipsk.apps.speechrecorder.actions.BackwardAction;
import ipsk.apps.speechrecorder.actions.CloseSpeakerDisplayAction;
import ipsk.apps.speechrecorder.actions.ContinueAutoRecordingAction;
import ipsk.apps.speechrecorder.actions.ContinuePlaybackAction;
import ipsk.apps.speechrecorder.actions.EditScriptAction;
import ipsk.apps.speechrecorder.actions.ExportScriptAction;
import ipsk.apps.speechrecorder.actions.ForwardAction;
import ipsk.apps.speechrecorder.actions.ImportScriptAction;
import ipsk.apps.speechrecorder.actions.PauseAutoRecordingAction;
import ipsk.apps.speechrecorder.actions.PausePlaybackAction;
import ipsk.apps.speechrecorder.actions.RecTransporterActions;
import ipsk.apps.speechrecorder.actions.SetIndexAction;
import ipsk.apps.speechrecorder.actions.StartAutoRecordingAction;
import ipsk.apps.speechrecorder.actions.StartPlaybackAction;
import ipsk.apps.speechrecorder.actions.StartRecordAction;
import ipsk.apps.speechrecorder.actions.StopNonrecordingAction;
import ipsk.apps.speechrecorder.actions.StopPlaybackAction;
import ipsk.apps.speechrecorder.actions.StopRecordAction;
import ipsk.apps.speechrecorder.annotation.AnnotationManager;
import ipsk.apps.speechrecorder.annotation.auto.AutoAnnotationPluginManager;
import ipsk.apps.speechrecorder.annotation.auto.AutoAnnotationWorker;
import ipsk.apps.speechrecorder.annotation.auto.MAUSTemplateTextFilePersistor;
import ipsk.apps.speechrecorder.annotation.auto.TemplateTextFilePersistor;
import ipsk.apps.speechrecorder.annotation.auto.impl.PromptAutoAnnotator;
import ipsk.apps.speechrecorder.annotation.auto.impl.PromptAutoAnnotatorServiceDescriptor;
import ipsk.apps.speechrecorder.annotation.auto.impl.TemplateAutoAnnotator;
import ipsk.apps.speechrecorder.annotation.auto.impl.TemplateAutoAnnotatorServiceDescriptor;
//import ipsk.apps.speechrecorder.annotation.auto.impl.PromptAutoAnnotator;
import ipsk.apps.speechrecorder.audio.AudioManager;
import ipsk.apps.speechrecorder.audio.AudioManagerException;
import ipsk.apps.speechrecorder.config.Annotation;
import ipsk.apps.speechrecorder.config.AnnotationPersistence;
import ipsk.apps.speechrecorder.config.AutoAnnotation;
import ipsk.apps.speechrecorder.config.BundleAnnotationPersistorConfig;
import ipsk.apps.speechrecorder.config.ChannelRouting;
import ipsk.apps.speechrecorder.config.ConfigHelper;
import ipsk.apps.speechrecorder.config.Format;
import ipsk.apps.speechrecorder.config.Formatter;
import ipsk.apps.speechrecorder.config.ItemcodeGeneratorConfiguration;
import ipsk.apps.speechrecorder.config.LoggingConfiguration;
import ipsk.apps.speechrecorder.config.MixerName;
import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import ipsk.apps.speechrecorder.config.PromptBeep;
import ipsk.apps.speechrecorder.config.PromptConfiguration;
import ipsk.apps.speechrecorder.config.RecordingConfiguration;
import ipsk.apps.speechrecorder.config.RecordingConfiguration.CaptureScope;
import ipsk.apps.speechrecorder.config.WorkspaceProject;
import ipsk.apps.speechrecorder.monitor.StartStopSignal;
import ipsk.apps.speechrecorder.monitor.plugins.SimpleTrafficLight;
import ipsk.apps.speechrecorder.project.NewProjectConfiguration;
import ipsk.apps.speechrecorder.prompting.PromptBufferedImageViewer;
import ipsk.apps.speechrecorder.prompting.PromptFormattedTextViewer;
import ipsk.apps.speechrecorder.prompting.PromptPlainTextViewer;
import ipsk.apps.speechrecorder.prompting.PromptPresenterPluginManager;
import ipsk.apps.speechrecorder.prompting.PromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.prompting.PromptViewer;
import ipsk.apps.speechrecorder.prompting.PromptViewerListener;
import ipsk.apps.speechrecorder.prompting.PrompterException;
import ipsk.apps.speechrecorder.prompting.combined.FormattedTextAndAudioJavaSoundViewer;
import ipsk.apps.speechrecorder.prompting.combined.ImageAndAudioJavaSoundViewer;
import ipsk.apps.speechrecorder.prompting.combined.TextButtonAudioJavaSoundViewer;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerPresenterClosedEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerStartedEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerStoppedEvent;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.sound.javasound.PromptAudioJavaSoundViewer;
import ipsk.apps.speechrecorder.script.ItemcodeGenerator;
import ipsk.apps.speechrecorder.script.RecScriptChangedEvent;
import ipsk.apps.speechrecorder.script.RecScriptManager;
import ipsk.apps.speechrecorder.script.RecScriptStoreStatusChanged;
import ipsk.apps.speechrecorder.script.RecscriptHandler;
import ipsk.apps.speechrecorder.script.RecscriptManagerEvent;
import ipsk.apps.speechrecorder.script.RecscriptManagerException;
import ipsk.apps.speechrecorder.script.RecscriptManagerListener;
import ipsk.apps.speechrecorder.session.SessionManager;
import ipsk.apps.speechrecorder.session.SessionManagerEvent;
import ipsk.apps.speechrecorder.session.SessionManagerException;
import ipsk.apps.speechrecorder.session.SessionManagerListener;
import ipsk.apps.speechrecorder.session.SessionPositionChangedEvent;
import ipsk.apps.speechrecorder.storage.StorageManager;
import ipsk.apps.speechrecorder.storage.StorageManagerException;
import ipsk.apps.speechrecorder.workspace.WorkspaceException;
import ipsk.apps.speechrecorder.workspace.WorkspaceManager;
import ipsk.audio.AudioController;
import ipsk.audio.AudioController2;
import ipsk.audio.AudioController2.AudioController2Listener;
import ipsk.audio.AudioController2.AudioControllerEvent;
import ipsk.audio.AudioController4;
import ipsk.audio.AudioControllerException;
import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.ConvenienceFileAudioSource;
import ipsk.audio.DeviceInfo;
import ipsk.audio.FileAudioSource;
import ipsk.audio.PluginChain;
import ipsk.audio.URLAudioSource;
import ipsk.audio.ajs.AJSAudioSystem;
import ipsk.audio.ajs.AJSDevice;
import ipsk.audio.ajs.AJSDeviceInfo;
import ipsk.audio.ajs.MixerProviderServiceDescriptor;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.capture.BufferOverrunException;
import ipsk.audio.capture.PrimaryRecordTarget;
import ipsk.audio.capture.event.CaptureCloseEvent;
import ipsk.audio.capture.event.CaptureErrorEvent;
import ipsk.audio.capture.event.CaptureEvent;
import ipsk.audio.capture.event.CaptureRecordedEvent;
import ipsk.audio.capture.event.CaptureRecordingFileTransitEvent;
import ipsk.audio.capture.event.CaptureStartCaptureEvent;
import ipsk.audio.capture.event.CaptureStartRecordEvent;
import ipsk.audio.dsp.LevelInfo;
import ipsk.audio.dsp.speech.SpeechFinalSilenceDetector;
import ipsk.audio.dsp.speech.SpeechFinalSilenceDetectorEvent;
import ipsk.audio.dsp.speech.SpeechFinalSilenceDetectorListener;
import ipsk.audio.dsp.speech.vad.VoiceActivityDetector;
import ipsk.audio.dsp.speech.vad.impl.VoicedSpeechDetector;
import ipsk.audio.mixer.MixerManager;
import ipsk.audio.player.Player;
import ipsk.audio.player.PlayerException;
import ipsk.audio.player.PlayerListener;
import ipsk.audio.player.event.PlayerCloseEvent;
import ipsk.audio.player.event.PlayerEvent;
import ipsk.audio.player.event.PlayerPauseEvent;
import ipsk.audio.player.event.PlayerStartEvent;
import ipsk.audio.player.event.PlayerStopEvent;
import ipsk.audio.plugins.VolumeControlPlugin;
import ipsk.audio.plugins.VolumeControlPlugin.VolumeControl;
import ipsk.audio.samples.SampleManager;
import ipsk.awt.ProgressListener;
import ipsk.awt.Worker.State;
import ipsk.awt.WorkerException;
import ipsk.awt.event.ProgressEvent;
import ipsk.beans.DOMCodec;
import ipsk.beans.DOMCodecException;
import ipsk.db.speech.Mediaitem;
import ipsk.db.speech.Nonrecording;
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.Recording;
import ipsk.db.speech.Recprompt;
import ipsk.db.speech.Script;
import ipsk.db.speech.Section;
import ipsk.io.FileUtils;
import ipsk.io.StreamCopy;
import ipsk.net.SimplePasswordAuthentication;
import ipsk.net.URLContext;
import ipsk.net.Upload;
import ipsk.net.UploadCache;
import ipsk.net.UploadException;
import ipsk.net.UploadFile;
import ipsk.net.cookie.SessionCookieHandler;
import ipsk.swing.JProgressDialogPanel;
import ipsk.text.EncodeException;
import ipsk.text.ParserException;
import ipsk.text.Version;
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;
import ipsk.util.SystemHelper;
import ipsk.util.apps.UpdateManager;
import ipsk.util.apps.UpdateManagerEvent;
import ipsk.util.apps.UpdateManagerListener;
import ipsk.util.apps.descriptor.Change;
import ipsk.util.apps.event.UpdateAvailableEvent;
import ipsk.util.collections.ObservableArrayList;
import ipsk.util.collections.ObservableList;
import ipsk.util.logging.FileHandler;
import ipsk.util.optionparser.Option;
import ipsk.util.optionparser.OptionParser;
import ipsk.util.optionparser.OptionParserException;
import ipsk.util.services.ServicesInspector;
import ipsk.util.zip.UnzipWorker;
import ipsk.util.zip.ZipPackerWorker;
import ipsk.xml.DOMConverter;
import ipsk.xml.DOMConverterException;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.xml.bind.JAXB;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.utils.URIUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class SpeechRecorder implements ActionListener,
		AudioController2Listener, RecscriptManagerListener,PromptViewerListener, PlayerListener, AnnotationManager, UpdateManagerListener, ProgressListener, SessionManagerListener, SpeechFinalSilenceDetectorListener{

    /**
     * Indicates request to repeat the recording item.
     * 
     * @author klausj
     *
     */
    
    public class RepeatRequest{
        private String messageTitle;
        private String message;
        public RepeatRequest() {
            super();
          
        }
        public RepeatRequest(String messageTitle, String message) {
            super();
            this.messageTitle = messageTitle;
            this.message = message;
        }
        public String getMessageTitle() {
            return messageTitle;
        }
        public String getMessage() {
            return message;
        }
    }
    
    public final static boolean DEBUG=false;
    
    // version of Speechrecorder application
	public final static String VERSION = SpeechRecorder.class.getPackage().getImplementationVersion();
	public final static String APPLICATION_NAME="SpeechRecorder";
	public final static String FREEDESKTOP_APPLICATION_ICON_NAME="speechrecorder";
	
	// version of project configuration
	// increase value if project configuration schema changes
	// the schema is only defined by Java code classes (there is no DTD or XML Schema yet)
	public final static String PROJECT_VERSION="3.4.0";
	
	// set to true for stable releases
	public final static boolean IS_STABLE_RELEASE=true;
	// user warning message if (non stable) development version
	private final static String NON_STABLE_VERSION_WARNING="You are using a non stable development version.\nPlease use this version only for evaluation!\nProjects configured by this version might be incompatible to stable releases!";

	public final static String AUTHORS = " Chr. Draxler, K. J\u00e4nsch";

	public final static String COPYRIGHT = "Copyright \u00A9 2004-2018";
	
	public static final String CONTACT_URI="mailto:speechrecorder@phonetik.uni-muenchen.de";
	
	private final String DEF_REL_WORKSPACE_DIR = "speechrecorder";
	
	public final String APPLICATION_DESCRIPTOR_KEY="ipsk.util.apps.descriptor.url";
	public final String PREFERRED_START_STOP_SIGNAL_PLUGIN="ips.apps.speechrecorder.startstopsignal.Ampelmaennchen";
	
	public final boolean USE_MAX_REC_TIMER=true;
	
    public static final String PROJECT_FILE_EXTENSION = "_project.prj";

    // TODO comment out for new SpeechDB speakers XML format
	private final String SPEAKER_FILE_SUFFIX = "_speakers.xml";
//	private final String SPEAKER_FILE_SUFFIX = "_speakers.txt";

	private final String REC_SCRIPT_FILE_EXTENSION = "_script.xml";

	private final String LOG_FILE_SUFFIX = "_log.log";

	private final String TIME_LOG_FILE_SUFFIX = "_timelog.log";

	private final String TEMP_FILE_PREFIX = "IPSK_";
	
    private final String REC_SCRIPT_EXAMPLE = "ExampleRecScript_2.xml";
    
    private final double MIN_EXPECTED_REC_LEN_TOLERANCE=0.9;
    private final int MIN_EXPECTED_REC_LEN_LNE_ACTIVATION_MS_DEFAULT=250;

	private long SHUTDOWN_RETRY_DELAY = 2000;

	// time to wait before a new recording attempt will be made after recording
	// error
	private long RECORD_RETRY_DELAY = 3000;

	// private long CACHE_HOLD_SIZE=100000000; // 100 MB
	// private long CACHE_HOLD_SIZE =0; // 0 MB

	public String LOG_HANDLER_NAME = "default";

	public String TIMELOG_HANDLER_NAME = "timelog";

	public static ipsk.apps.speechrecorder.config.Handler[] DEF_LOG_HANDLERS = new ipsk.apps.speechrecorder.config.Handler[0];

	protected final static Formatter TIME_LOG_FORMATTER_CFG = new Formatter(
			TimeLogFormatter.class.getName(), "Time logger");

	public final static Formatter[] LOG_FORMATTERS = {
			new Formatter(null, "(Default)"),
			new Formatter("java.util.logging.SimpleFormatter", "Plain Text"),
			new Formatter("java.util.logging.XMLFormatter", "XML"),
			TIME_LOG_FORMATTER_CFG };

	private static final float PREFERRED_LINE_BUFFER_SIZE_MILLIS = 4000;

	public static ipsk.apps.speechrecorder.config.Logger[] AVAIL_LOGGERS = new ipsk.apps.speechrecorder.config.Logger[0];

	public static Action[] SESSION_ACTIONS=null;
	public static Action[] ACTIONS = new Action[0];

	private Logger logger;

//	private Level logLevel = Level.OFF;

	private ipsk.util.logging.FileHandler logFileHandler;

	private ipsk.util.logging.FileHandler timeLogFileHandler;

	// private TimeLogger timeLogger;
	private Logger timeLogger;

	private DOMCodec domCodec;

	private DOMConverter domConverter;

	private File defWorkspaceDir;

	private URL projectURL;

	private ProjectConfiguration project;

	private boolean projectConfigurationSaved;

//	private boolean scriptSaved;

	private WorkspaceProject[] workspaceProjects;

	private URL projectContext = null;

	private boolean progressPaused;

	private boolean speakerWindow;

	private GraphicsConfiguration speakerScreenConfig=null;

	private GraphicsConfiguration experimenterScreenConfig=null;

	private RecScriptManager recScriptManager;
	private SessionManager sessionManager;

	/**
	 * @return the sessionManager
	 */
	public SessionManager getSessionManager() {
		return sessionManager;
	}

	private AudioManager audioManager;
	private AudioController4 audioController;
	private AnnotatedAudioClip audioClip=new AnnotatedAudioClip();

	private boolean audioEnabled;
	
	private boolean seamlessAutoRecording=false;

	// private MixerManager mixerManager=null;
	// private JPanel levelMeterPanel;
	private RecLogger recLogger;

	private RecStatus recStat;

	private UIResources uiString;

	private SpeechRecorderUI speechRecorderUI;

	private SpeakerManager speakerManager;

	private String speakerFileName;



	private URL recBaseURL;

//	private URL sessionURL;

	private URL promptFile;
	
	private File lastPromptSelectionDir=null;

	private URL speakerURL;

	// private File applicationDirectory;
	private int numLines;

//	private Properties properties;

//	private String labelFileExtension;

	private boolean overwrite;
	
	private boolean sessionOverwriteWarning=true;

	private AudioFileFormat audioFileFormat;

	private String audioControllerClassName;

//	private int speakerID;

//	private String speakerCode;

	private StorageManager storageManager;

	// private VectorBufferedOutputStream[] recordingOutputStreams;
//	private UploadCacheUI uploadCacheUI;

	private UploadCache uploadCache;

	private boolean useUploadCache;

	private boolean waitForCompleteUpload;

	private boolean uploadDuringSessionRecording=true;

	// private boolean useVectorBuffers;
//	private String user;

//	private String password;

	private boolean itemPlayable;
	private boolean recManualPlay=false;

	private boolean allRecordingsDoneNotified;

	private boolean lastSpeakerWindowRequest;

//	private File webSessionLogFile = null;

	// thread to pass on status changes to RecStatus, timer to delay
	// execution of status changes
	// private int tmpStatus;
	private javax.swing.Timer preRecTimer;

	private javax.swing.Timer postRecTimer;

	private javax.swing.Timer maxRecTimer;
	
	private Timer nonRecordingTimer;
	
	public boolean useMaxRecTimer=USE_MAX_REC_TIMER;

	// actions
    // TODO where to put the actions ?

	private StartRecordAction startRecordAction;

	private StopRecordAction stopRecordAction;
	
	private StopNonrecordingAction stopNonrecordingAction;
	
	private RecTransporterActions recTransporterActions;

	private StartAutoRecordingAction startAutoRecordingAction;

	private PauseAutoRecordingAction pauseAutoRecordingAction;

	private ContinueAutoRecordingAction continueAutoRecordingAction;

	private AdvanceToNextAction advanceToNextAction;

	private ForwardAction forwardAction;

	private BackwardAction backwardAction;

	private StartPlaybackAction startPlaybackAction;

	private PausePlaybackAction pausePlaybackAction;

	private StopPlaybackAction stopPlaybackAction;

	private ContinuePlaybackAction continuePlaybackAction;
	
	private CloseSpeakerDisplayAction closeSpeakerDisplayAction;
	
	private SetIndexAction setIndexAction;
	
	private EditScriptAction editScriptAction;
	private ImportScriptAction importScriptAction;

	private RepeatRequest repeatRequest=null;
//    private boolean promptPlayed=false;
	private boolean debugSinusTest = false;

    private WorkspaceManager workspaceManager;
    
    
    private boolean annotatingEnabled=false;
    private Player beepPlayer;

    private Mixer promptMixer;
    
//    private boolean promptAudioLineInUse=false;

    private ipsk.db.speech.PromptItem promptItem;
    private Section.Mode sectionMode=null;
    private String promptItemCode=null;
    
    private boolean recDisplayValid=false;

//    private boolean useTempFile=false;
    private PrimaryRecordTarget primaryRecordTarget;
    
    private PromptPresenterPluginManager promptPresenterPluginManager;
    private AutoAnnotationPluginManager autoAnnotatorPluginManager;
    public AutoAnnotationPluginManager getAutoAnnotatorPluginManager() {
        return autoAnnotatorPluginManager;
    }

    private AutoAnnotationWorker autoAnnotationWorker;
    private UpdateManager updateManager;
    private ItemcodeGenerator itemcodeGenerator;

//	private ChannelRouting inputChannelRouting;
  
    private PromptItem lastPromptItem;

	private CaptureScope captureScope=CaptureScope.ITEM;
	
	private VoiceActivityDetector voiceDetector;
	private SpeechFinalSilenceDetector silenceDetector;

    private Schema schema;
    private Session annotationSession=null;
    
    private List<BundleAnnotationPersistor> bundleAnnotationPersistorList=new ArrayList<BundleAnnotationPersistor>();
//    private final static String PROMPT_LEVEL_DEF_NAME="PRT";

    private List<BundleAnnotationPersistorServiceDescriptor> availableBundleAnnotationServiceDescriptors;

private PromptAutoAnnotator promptAutoAnnotator;

private TemplateAutoAnnotator templateAutoAnnotator;

private ExportScriptAction exportScriptAction;

private Integer lastSessionId=null;

private AudioSource beepAudioSource;

private URL beepURL;

    /**
	 * @return the updateManager
	 */
	public UpdateManager getUpdateManager() {
		return updateManager;
	}



	/** Create Speechrecorder application
     * Must be called from AWT event thread ! 
     * @param projectFileURL URL of the project configuration file
     * @param user user for web authentication
     * @param password password for web authentication
     * @throws ClassNotFoundException
     * @throws DOMCodecException
     * @throws DOMConverterException
     * @throws IOException
     * @throws PluginLoadingException
     * @throws AudioControllerException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws StorageManagerException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws WorkspaceException
     */
    public SpeechRecorder(String projectFileURL, String user, String password)
    throws ClassNotFoundException, DOMCodecException,
    DOMConverterException, IOException, PluginLoadingException,
    AudioControllerException, ParserConfigurationException,
    SAXException, StorageManagerException, InstantiationException,
    IllegalAccessException, WorkspaceException {
    
        
    	
        
        Thread shutDownThread=new Thread(){
            public void run(){
                if(DEBUG)System.out.println("Shutdown thread");
                if(storageManager!=null){
                    try {
                        storageManager.close(true);
                    } catch (StorageManagerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        
//        URLClassLoader.newInstance(new URL[]{new URL("file:/homes/klausj/workspace/AmpelmaennchenPlugin/build/")});
        
        
        Runtime.getRuntime().addShutdownHook(shutDownThread);
     
       AJSAudioSystem.init();
        
        // initialize the logging mechanism
        ipsk.apps.speechrecorder.config.Handler logHandler = new ipsk.apps.speechrecorder.config.Handler(
                LOG_HANDLER_NAME);
        ipsk.apps.speechrecorder.config.Handler timeLogHandler = new ipsk.apps.speechrecorder.config.Handler(
                TIMELOG_HANDLER_NAME);
        timeLogHandler.setFormatter(TIME_LOG_FORMATTER_CFG);
        DEF_LOG_HANDLERS = new ipsk.apps.speechrecorder.config.Handler[] {
                logHandler, timeLogHandler };
        ipsk.apps.speechrecorder.config.Logger defLogger = new ipsk.apps.speechrecorder.config.Logger();
        defLogger.setName("ipsk.apps.speechrecorder");
        defLogger.setHandlerName(logHandler.getName());
        ipsk.apps.speechrecorder.config.Logger defTimeLogger = new ipsk.apps.speechrecorder.config.Logger();
        defTimeLogger.setName("time");
        defTimeLogger
        .setHandlerName(timeLogHandler.getName());
        logger = Logger.getLogger("ipsk.apps.speechrecorder");
        AVAIL_LOGGERS = new ipsk.apps.speechrecorder.config.Logger[] {
                defLogger, defTimeLogger };

        timeLogger = Logger.getLogger("time");
        // logger.setLevel(logLevel);
        // logger.info("Starting up SpeechRecorder");

        uiString = UIResources.getInstance();
        defWorkspaceDir = new File(System.getProperty("user.home")
                + File.separator + DEF_REL_WORKSPACE_DIR);
//        // Check preferences permissions
//        boolean systemPreferencesReadable=false;
//        boolean systemPrefrencesWritable=false;
//        
//        boolean checkSystemPrefsAccess=false;
//        
//        if(checkSystemPrefsAccess){
////        SecurityManager sm=System.getSecurityManager();
////        try{
////        sm.checkPermission(new RuntimePermission("preferences", "write"));
////        System.out.println("write access");
////        }catch(SecurityException se){
////            System.out.println("no write access");
////        }
////        System.setProperty("java.util.prefs.syncInterval","500");
//        try{
//        Preferences sysPrefs=Preferences.systemNodeForPackage(this.getClass());
//        systemPreferencesReadable=true;
//        sysPrefs.put("primaryRecordingTarget","TEMP_RAW_FILE");
//        try {
//            sysPrefs.flush();
//            systemPrefrencesWritable=true;
//        } catch (BackingStoreException e1) {
//            systemPrefrencesWritable=false;
//            
//        }
//        }catch(SecurityException se){
//            systemPreferencesReadable=false;
//        }
//       
//        System.out.println("System prefs: readable: "+systemPreferencesReadable+" writable: "+systemPrefrencesWritable);
//    }
//        boolean userPreferencesReadable=false;
//        boolean userPrefrencesWritable=false;
//        
////        SecurityManager sm=System.getSecurityManager();
////        try{
////        sm.checkPermission(new RuntimePermission("preferences", "write"));
////        System.out.println("write access");
////        }catch(SecurityException se){
////            System.out.println("no write access");
////        }
////        System.setProperty("java.util.prefs.syncInterval","500");
//        try{
//        Preferences userPrefs=Preferences.userNodeForPackage(this.getClass());
//        userPreferencesReadable=true;
//        userPrefs.put("primaryRecordingTarget","TEMP_RAW_FILE");
//        try {
//            userPrefs.flush();
//            userPrefrencesWritable=true;
//        } catch (BackingStoreException e1) {
//            userPrefrencesWritable=false;
//            
//        }
//        }catch(SecurityException se){
//            userPreferencesReadable=false;
//        }
//       System.out.println("User prefs: readable: "+userPreferencesReadable+" writable: "+userPrefrencesWritable);
//        
        workspaceManager=new WorkspaceManager(defWorkspaceDir);
        Package configBasePack = Class.forName(
        "ipsk.apps.speechrecorder.config.ProjectConfiguration")
        .getPackage();
        audioEnabled = false;
    	new MixerManager();
        domCodec = new DOMCodec(configBasePack);
        domConverter = new DOMConverter();
        speakerFileName = null;
        speakerManager = new SpeakerManager();
        recScriptManager = RecScriptManager.getInstance();
        recScriptManager.addRecscriptManagerListener(this);
        sessionManager=SessionManager.getInstance();
        projectConfigurationSaved = true;
//        scriptSaved = true;

        // Progress is paused until the start button is pressed
        // in auto recording mode
        progressPaused = true;

        startRecordAction = new StartRecordAction(this, uiString
                .getString("RecordingButtonText"));
        startRecordAction.setEnabled(false);
        stopRecordAction = new StopRecordAction(this, uiString
                .getString("StopButtonText"));
        stopRecordAction.setEnabled(false);
        stopNonrecordingAction = new StopNonrecordingAction(this, uiString
                .getString("NextButtonText"));
        stopNonrecordingAction.setEnabled(false);
        startAutoRecordingAction = new StartAutoRecordingAction(this, uiString
                .getString("StartButtonText"));
        pauseAutoRecordingAction = new PauseAutoRecordingAction(this, uiString
                .getString("PauseButtonText"));
        continueAutoRecordingAction = new ContinueAutoRecordingAction(this,
                uiString.getString("ContinueButtonText"));
        advanceToNextAction = new AdvanceToNextAction(this, uiString
                .getString("AdvanceToNextButtonText"));
        forwardAction = new ForwardAction(this, uiString
                .getString("ForwardButtonText"));
        backwardAction = new BackwardAction(this, uiString
                .getString("BackwardButtonText"));
        startPlaybackAction = new StartPlaybackAction(this, uiString
                .getString("PlayButtonText"));
        pausePlaybackAction = new PausePlaybackAction(this, uiString
                .getString("PlayButtonText")
                + "-" + uiString.getString("PauseButtonText"));
        stopPlaybackAction = new StopPlaybackAction(this, uiString
                .getString("StopButtonText"));
        continuePlaybackAction = new ContinuePlaybackAction(this, uiString
                .getString("PlayButtonText"));
        recTransporterActions = new RecTransporterActions();
        recTransporterActions.startRecordAction = startRecordAction;
        recTransporterActions.stopRecordAction = stopRecordAction;
        recTransporterActions.stopNonrecordingAction=stopNonrecordingAction;
        recTransporterActions.startAutoRecordingAction = startAutoRecordingAction;
        recTransporterActions.pauseAutoRecordingAction = pauseAutoRecordingAction;
        recTransporterActions.continueAutoRecordingAction = continueAutoRecordingAction;
        recTransporterActions.advanceToNextAction = advanceToNextAction;
        recTransporterActions.forwardAction = forwardAction;
        recTransporterActions.backwardAction = backwardAction;
        recTransporterActions.startPlaybackAction = startPlaybackAction;
        recTransporterActions.pausePlaybackAction = pausePlaybackAction;
        recTransporterActions.stopPlaybackAction = stopPlaybackAction;
        recTransporterActions.continuePlaybackAction = continuePlaybackAction;

        closeSpeakerDisplayAction=new CloseSpeakerDisplayAction(this,"Close speaker display");
        setIndexAction = new SetIndexAction(this, "Set index");

        editScriptAction = new EditScriptAction(this, "Edit script...");
        editScriptAction.setEnabled(false);
        importScriptAction=new ImportScriptAction(this,"Import text table...");
        importScriptAction.setEnabled(false);
        exportScriptAction=new ExportScriptAction(this,"Export script as text table file...");
        exportScriptAction.setEnabled(false);

        
        SESSION_ACTIONS= new Action[] {startRecordAction, stopRecordAction,
                startAutoRecordingAction, pauseAutoRecordingAction,
                continueAutoRecordingAction, advanceToNextAction,
                forwardAction, backwardAction, startPlaybackAction,
                pausePlaybackAction, stopPlaybackAction, continuePlaybackAction,closeSpeakerDisplayAction};
        ACTIONS = new Action[] { startRecordAction, stopRecordAction,
                startAutoRecordingAction, pauseAutoRecordingAction,
                continueAutoRecordingAction, advanceToNextAction,
                forwardAction, backwardAction, startPlaybackAction,
                pausePlaybackAction, stopPlaybackAction, continuePlaybackAction ,closeSpeakerDisplayAction,setIndexAction,editScriptAction,importScriptAction,exportScriptAction};

        if (user != null) {
            Authenticator.setDefault(new SimplePasswordAuthentication(user,
                    password));
            logger.info("Set authenticator for user " + user);
        }
        if (projectFileURL != null) {
            projectURL = new URL(projectFileURL);
            // For testing ??
            InputStream projectFileStream = projectURL.openStream();
            projectFileStream.close();
        }
        // after all components are in place get an instance of RecStatus
        recStat = RecStatus.getInstance();
        //recStat.attach(this);
        debugSinusTest = (System.getProperty("debug.sinustest") != null);


        //              now determine the graphics environment: one or two displays, what
        // resolution, etc.
        int experimenterScreenIdx=0;
        GraphicsEnvironment ge = GraphicsEnvironment
        .getLocalGraphicsEnvironment();
        GraphicsDevice[] gds = ge.getScreenDevices();
        GraphicsDevice defGd=ge.getDefaultScreenDevice();
        if(defGd==null && gds.length>0){
        	// fallback should not happen
        	defGd=gds[0];
        }
        if(gds==null || gds.length==0){
        	logger.severe("No display connected!");
        }
        for(int i=0;i<gds.length;i++){
    		GraphicsDevice gd=gds[i];
    		GraphicsConfiguration gc=gd.getDefaultConfiguration();
    		Rectangle gb=gc.getBounds();
            logger.info("Display "+i+": "+ gb.getWidth() + " x "+ gb.getHeight());
        }
        
        if(defGd!=null){
        	for(int i=0;i<gds.length;i++){
        		GraphicsDevice gd=gds[i];
        		if(gd.equals(defGd)){
        			experimenterScreenConfig = defGd.getDefaultConfiguration();
        			experimenterScreenIdx=i;
        			logger.info("Selected default display "+i+" as experimenter screen");
        		}
        	}
        }
        if (gds.length == 1) {
            // only one display connected; both the experimenter and the
            // speaker display share the same display
            speakerScreenConfig = experimenterScreenConfig;
            logger.info("Selected default display 0 as speaker screen");
        } else if (gds.length >= 2) {
            // at least two displays connected
        	// Fix Bug ID 0049
        	// On Mac OS X the first display is not necessarily the default display
        	// Choose the first display which is not the default
        	
        	for(int i=0;i<gds.length;i++){
        		GraphicsDevice gd=gds[i];
        		if(speakerScreenConfig==null && ! gd.equals(defGd)){
        			speakerScreenConfig = gd.getDefaultConfiguration();
        			logger.info("Selected display "+i+" as speaker screen");
        		}
        	}
            
        } 
   
       
//        List<PromptPresenter> promptPresenterList=new ArrayList<PromptPresenter>();
//        // add default plugins
//        
//        promptPresenterList.add(new PromptPlainTextViewer());   
//        promptPresenterList.add(new PromptFormattedTextViewer());
//        promptPresenterList.add(new PromptBufferedImageViewer());
//        MediaPromptPresenter app=new PromptAudioJavaSoundViewer();
////        app.setStartControlAction(startPromptPlaybackAction);
////        app.setStopControlAction(stopPromptPlaybackAction);
//        promptPresenterList.add(app);
//        MediaPromptPresenter app2=new PlainTextAndAudioJavaSoundViewer();
////        app2.setStartControlAction(startPromptPlaybackAction);
////        app2.setStopControlAction(stopPromptPlaybackAction);
//        promptPresenterList.add(app2);
//        
        // now additional plugins
        promptPresenterPluginManager=new PromptPresenterPluginManager();
        
        List<PromptPresenterServiceDescriptor> promptPresenterClassList=promptPresenterPluginManager.getPromptPresenterServiceDescriptors();
        PromptPresenterServiceDescriptor pptvsd=PromptPlainTextViewer.DESCRIPTOR;
        if(!promptPresenterClassList.contains(pptvsd)){
            promptPresenterClassList.add(pptvsd);
        }
        PromptPresenterServiceDescriptor pftvsd=PromptFormattedTextViewer.DESCRIPTOR;
        if(!promptPresenterClassList.contains(pftvsd)){
            promptPresenterClassList.add(pftvsd);
        }
        PromptPresenterServiceDescriptor pbivsd=PromptBufferedImageViewer.DESCRIPTOR;
        if(!promptPresenterClassList.contains(pbivsd)){
            promptPresenterClassList.add(pbivsd);
        }
        PromptPresenterServiceDescriptor pajsvsd=PromptAudioJavaSoundViewer.DESCRIPTOR;
        if(!promptPresenterClassList.contains(pajsvsd)){
            promptPresenterClassList.add(pajsvsd);
        }
//        if(!promptPresenterClassList.contains(PromptAudioViewer.class)){
//            promptPresenterClassList.add(PromptAudioViewer.class);
//        }
//        PromptPresenterServiceDescriptor ptaajsvsd=PlainTextAndAudioJavaSoundViewer.DESCRIPTOR;
        PromptPresenterServiceDescriptor ptaajsvsd=TextButtonAudioJavaSoundViewer.DESCRIPTOR;
        if(!promptPresenterClassList.contains(ptaajsvsd)){
            promptPresenterClassList.add(ptaajsvsd);
        }
        PromptPresenterServiceDescriptor pftaajsvsd=FormattedTextAndAudioJavaSoundViewer.DESCRIPTOR;
        if(!promptPresenterClassList.contains(pftaajsvsd)){
            promptPresenterClassList.add(pftaajsvsd);
        }
        PromptPresenterServiceDescriptor piaajsvsd=ImageAndAudioJavaSoundViewer.DESCRIPTOR;
        if(!promptPresenterClassList.contains(piaajsvsd)){
            promptPresenterClassList.add(piaajsvsd);
        }
//        for(Class<PromptPresenter> promptPresenterClass: promptPresenterClassList){
//            PromptPresenter pp=promptPresenterClass.newInstance();
//            promptPresenterList.add(pp);
//        }
        // Some components are realized before, so we should run the show method
        // after all events are dispatched
        
        availableBundleAnnotationServiceDescriptors=new ArrayList<BundleAnnotationPersistorServiceDescriptor>();
        availableBundleAnnotationServiceDescriptors.add(new EmuBundleAnnotationPersistor());
        availableBundleAnnotationServiceDescriptors.add(new TextGridFilePersistor());
        availableBundleAnnotationServiceDescriptors.add(new TemplateTextFilePersistor());
        availableBundleAnnotationServiceDescriptors.add(new MAUSTemplateTextFilePersistor());
        
        autoAnnotatorPluginManager=new AutoAnnotationPluginManager();
        List<AutoAnnotationServiceDescriptor> autoAnnotatorDescriptors=autoAnnotatorPluginManager.getAutoAnnotatorServiceDescriptors();
//        if(!autoAnnotatorDescriptors.contains(PromptAutoAnnotator.DESCRIPTOR)){
//            autoAnnotatorDescriptors.add(PromptAutoAnnotator.DESCRIPTOR);
//        }
        // TODO Test only. should be lazy loaded
//        promptAutoAnnotator=new PromptAutoAnnotator();
//        templateAutoAnnotator=new TemplateAutoAnnotator();
        autoAnnotatorDescriptors.add(new PromptAutoAnnotatorServiceDescriptor());
        autoAnnotatorDescriptors.add(new TemplateAutoAnnotatorServiceDescriptor());
        
        // disabled BAS services for now 
        // TODO how to get the user to read and accept trms of usage 
//        autoAnnotatorDescriptors.add(G2PServiceClient.DESCRIPTOR);
//        autoAnnotatorDescriptors.add(MAUSServiceClient.DESCRIPTOR);
        
        String appDescrUrlParam=System.getProperty(APPLICATION_DESCRIPTOR_KEY);
        if(appDescrUrlParam !=null){
        	try{
        	URL appDescrUrl=new URL(appDescrUrlParam);
        	// version should be available from package manifest, set to 0.0.1 from debugging purposes
        	Version currentVersion=null;
        	if(VERSION==null){
        	    currentVersion=new Version(new int[]{0,0,1});
        	}else{
        	   currentVersion=Version.parseString(VERSION);
        	}
        	
        	updateManager=new UpdateManager(currentVersion);
        	updateManager.addUpdateManagerListener(this);
        	updateManager.startLoadApplicationDescriptor(appDescrUrl);
        	}catch(MalformedURLException mue){
        		// ignore 
        	} catch (ParserException e) {
        		// ignore 
			} catch(Exception e){
			    // ignore
			    // continue launching speechrecorder has priority
			}
        }
        
        itemcodeGenerator=new ItemcodeGenerator();
                
        if (speechRecorderUI != null){
        	// can be disposed on all platforms it is not reused
        	speechRecorderUI.dispose();
        }
        
        speechRecorderUI = new SpeechRecorderUI(this,experimenterScreenIdx, experimenterScreenConfig,
                speakerScreenConfig);
        speechRecorderUI.setPromptPresenterServiceDescriptors(promptPresenterClassList);
        speechRecorderUI.setAutoAnnotatorServiceDescriptors(autoAnnotatorDescriptors);
        speechRecorderUI.createUI(setIndexAction,editScriptAction,importScriptAction,exportScriptAction,startPlaybackAction);
        speechRecorderUI.pack();
        speechRecorderUI.setExtendedState(JFrame.MAXIMIZED_BOTH);
        speechRecorderUI.setVisible(true);
        //  do not show splash screen anymore. The icon logo already appears on startup
        // two splash screens are too much
        
//        speechRecorderUI.showSplashScreen(true);

        // create splash screen
        // splashPanel = new SplashScreen(experimenterScreenConfig);

        if(!IS_STABLE_RELEASE){
            int ans=JOptionPane.showConfirmDialog(speechRecorderUI, NON_STABLE_VERSION_WARNING, "Warning!", JOptionPane.WARNING_MESSAGE);
            if(ans==JOptionPane.CANCEL_OPTION){
                shutdown();
            }
        }
        
        speechRecorderUI.setWaiting(true);
        if (projectURL != null) {
        	boolean canceled=false;
            try {
                canceled=configureProject(projectURL);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
                        "ERROR", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
            if(canceled){
            	// should never happen here
            	// only for interactive selection of prompt script or speaker DB files
            	return;
            }
            start();
        } else {
            try {
                workspaceProjects=workspaceManager.scanWorkspace();
            } catch (WorkspaceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            speechRecorderUI.setWorkspaceProjects(workspaceProjects);
            speechRecorderUI.setEnableOpenOrNewProject(true);
        }
        speechRecorderUI.setWaiting(false);


    }



	public Action getActionByActionCommand(String actionCmd) {
	    // TODO use hash
		for (int i = 0; i < SpeechRecorder.ACTIONS.length; i++) {
			if (SpeechRecorder.ACTIONS[i].getValue(Action.ACTION_COMMAND_KEY)
					.equals(actionCmd)) {
				return SpeechRecorder.ACTIONS[i];
			}
		}
		return null;
	}
	
	public List<AutoAnnotator> getAutoAnnotators(){
	    if(autoAnnotationWorker!=null){
	        return autoAnnotationWorker.getAutoAnnotators();
	    }else{
	        return null;
	    }
	}


	public boolean configure(ProjectConfiguration cfgProject)
			throws PluginLoadingException, AudioControllerException,
			DOMConverterException, IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, DOMCodecException, WorkspaceException, URISyntaxException, RecscriptManagerException, StorageManagerException{
	    if(DEBUG)System.out.println("Configure project");
		project = cfgProject;
		
		String promptFileName = null;

		String recDirName = null;

		PromptConfiguration pc = project.getPromptConfiguration();
		recManualPlay=pc.getRecManualPlay();
		
		RecordingConfiguration recCfg = project.getRecordingConfiguration();
//		labelFileExtension = recCfg.getLabelExtension();
//		useTempFile = recCfg.isUseRawTempFile();
		primaryRecordTarget=recCfg.getPrimaryRecordTarget();
		overwrite = recCfg.getOverwrite();
		Format aFormat = recCfg.getFormat();
//		AudioFormat audioFormat = new AudioFormat((float) aFormat
//				.getSampleRate(), aFormat.getSampleSizeInBits(), aFormat
//				.getChannels(), true, aFormat.getBigEndian());
		AudioFormat audioFormat=aFormat.toAudioFormat();
		audioFileFormat = new AudioFileFormat(AudioFileFormat.Type.WAVE,
				audioFormat, AudioSystem.NOT_SPECIFIED);
		// numLines = recCfg.getNumLines();
		// multi-line feature not supported yet !
		numLines = 1;
		promptFileName = pc.getPromptsUrl();

		// set instance variables to the values given as arguments
		if (promptFileName != null && !promptFileName.equals("")) {
		    
			promptFile = URLContext.getContextURL(projectContext,
					promptFileName);
		} else {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle(uiString.getString("SelectPromptFile"));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if(lastPromptSelectionDir!=null){
				chooser.setCurrentDirectory(lastPromptSelectionDir);
			}
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selectedFile=chooser.getSelectedFile();
				lastPromptSelectionDir=selectedFile.getParentFile();
				promptFile = new URL("file:"
						+ selectedFile.getAbsolutePath());
				
			} else {
//				System.exit(0);
				return true;
			}
		}
		
		Class<? extends StartStopSignal> startStopSignalClass=null;
		String startStopSignalClassname=null;
		
		// project setting has highest priority
		ipsk.apps.speechrecorder.config.StartStopSignal startStopSConfig=pc.getStartStopSignal();
		if(startStopSConfig!=null){
		   String startStopSClassNameAttr=startStopSConfig.getClassname();
		   if(startStopSClassNameAttr!=null){
		       startStopSignalClassname=startStopSClassNameAttr;
		   }
		}
		// next priority property setting
		if(startStopSignalClassname==null){
		 String pluginInterfaceClassName=StartStopSignal.class.getName();
	     startStopSignalClassname=System.getProperty(pluginInterfaceClassName);
		}
		// use application preference
        if(startStopSignalClassname==null){
          try{
              Class.forName(PREFERRED_START_STOP_SIGNAL_PLUGIN);
              startStopSignalClassname=PREFERRED_START_STOP_SIGNAL_PLUGIN;
          }catch(ClassNotFoundException cnfe){
              // OK preferred not available
          }
        }
		// finally use the first one in list
		if(startStopSignalClassname==null){
	      ServicesInspector<StartStopSignal> startStopSignalPluginManager=new ServicesInspector<StartStopSignal>(StartStopSignal.class);
	      List<String> startStopSignalClassnames=startStopSignalPluginManager.getServiceImplementorClassnames();
	      if(startStopSignalClassnames!=null && startStopSignalClassnames.size()>0){
	          startStopSignalClassname=startStopSignalClassnames.get(0);
	      }
		}
		
		if(startStopSignalClassname!=null){
		    // we have a class name
		    try{
		        startStopSignalClass=Class.forName(startStopSignalClassname).asSubclass(StartStopSignal.class);
		    }catch(ClassNotFoundException cnfe){
		        speechRecorderUI.displayError("Start-Stop-Signal plugin error","Could not load start stop signal plugin: "+cnfe.getMessage()+"\nUsing default signal: simple traffic light.");
		    }
		}

		if(startStopSignalClass==null){
		    // fallback default is simple traffic light
		    startStopSignalClass=SimpleTrafficLight.class;
		}
		
		ItemcodeGeneratorConfiguration itemcodeGenCfg=pc.getItemcodeGeneratorConfiguration();
		// we need a copy of the configuration 
		// the script editor may cahnge the configuration, but not for the project scope
		ipsk.apps.speechrecorder.script.ItemcodeGeneratorConfiguration itemcodeGenCfgSessionCopy=((ipsk.apps.speechrecorder.script.ItemcodeGeneratorConfiguration)itemcodeGenCfg).cloneTyped();
		itemcodeGenerator.setConfig(itemcodeGenCfgSessionCopy);
	   
		// Speaker currSpeaker=speakerManager.getSpeaker();
		String newSpeakerFilename = project.getSpeakers().getSpeakersUrl();
		if (speakerFileName == null
				|| !speakerFileName.equals(newSpeakerFilename)) {
			// speakers URL has changed
			speakerFileName = project.getSpeakers().getSpeakersUrl();

			if (speakerFileName != null && !speakerFileName.equals("")) {
				speakerURL = URLContext.getContextURL(projectContext,
						speakerFileName);
			} else {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle(uiString.getString("SelectSpeakerFile"));
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					speakerURL = new URL("file:"
							+ chooser.getSelectedFile().getAbsolutePath());
				} else {
					return true;
				}
			}
			
			// storageManager.setSpeakerCode(speakerManager.getSpeaker().getCode());
			// currSpeaker=null;
		}
		// Speaker database is closed when project is closed
		// therefore we always have to (re)load spekear db
		speakerManager.loadURL(speakerURL);
		
		recDirName = recCfg.getUrl();
		recBaseURL = URLContext.getContextURL(projectContext, recDirName);
		if (!recBaseURL.getProtocol().equalsIgnoreCase("file"))
			// enable upload cache (and "webrecording"), if destination is
			// remote
			useUploadCache = true;
		speechRecorderUI.setFileSystemWorkspaceEnabled(!useUploadCache);
		
		storageManager = new StorageManager();

		storageManager.setUseAsCache(useUploadCache);
		storageManager.setNumLines(numLines);
		
		speakerManager.setSessionPersonIDFormat(storageManager.getSpeakerIDFormat());
		// create logger

		LoggingConfiguration logCfg = project.getLoggingConfiguration();
		ipsk.apps.speechrecorder.config.Logger[] loggers = logCfg.getLogger();
		Logger rootLogger = Logger.getLogger("");
		Handler[] defHandlers = rootLogger.getHandlers();
		File logFile = null;
		File timeLogFile = null;
		// remove all default handlers set by JVM
		for (int i = 0; i < defHandlers.length; i++) {
			rootLogger.removeHandler(defHandlers[i]);
		}
		ipsk.apps.speechrecorder.config.Handler[] handlers = logCfg
				.getHandler();

		for (int i = 0; i < handlers.length; i++) {
			Formatter formatterCfg = handlers[i].getFormatter();
			java.util.logging.Formatter formatter = null;
			if (formatterCfg != null) {
				String formatterClassName = formatterCfg
						.getClassName();
				if (formatterClassName != null) {
					Class<?> fClass = Class.forName(formatterClassName);
					Object formatterObj=fClass.newInstance();
					if(formatterObj instanceof java.util.logging.Formatter){
					    formatter=(java.util.logging.Formatter)formatterObj;
					}
				}
			}
			if (handlers[i].getName().equals(LOG_HANDLER_NAME)) {
				if (logFile == null) {
					try {

						if (useUploadCache) {
							// for webrecording we log to temporary files which
							// will be uploaded at the and of the session
							logFile = File.createTempFile(TEMP_FILE_PREFIX,
									LOG_FILE_SUFFIX);
							logFile.deleteOnExit();
						} else {
							// for workspace projects we log to files in the
							// workspace instead
							logFile = new File(projectContext.toURI().getPath()
									+ File.separator + project.getName()
									+ LOG_FILE_SUFFIX);
						}
						logFileHandler = new FileHandler(logFile, true);

						if (formatter != null)
							logFileHandler.setFormatter(formatter);
					} catch (IOException e) {
						logger
								.severe("Could not associate a file with the current logger: "
										+ e);
					} catch (SecurityException e) {
						logger.severe("Could not write to a log file: " + e);
					}
				}
			} else if (handlers[i].getName().equals(
					TIMELOG_HANDLER_NAME)) {
				if (timeLogFile == null) {
					try {

						if (useUploadCache) {
							// for webrecording we log to temporary files which
							// will be uploaded at the and of the session
							timeLogFile = File.createTempFile(TEMP_FILE_PREFIX,
									TIME_LOG_FILE_SUFFIX);
							timeLogFile.deleteOnExit();
						} else {
							// for workspace projects we log to files in the
							// workspace instead
							timeLogFile = new File(projectContext.toURI().getPath()
									+ File.separator + project.getName()
									+ TIME_LOG_FILE_SUFFIX);
						}
						timeLogFileHandler = new FileHandler(timeLogFile, true);

						if (formatter != null)
							timeLogFileHandler.setFormatter(formatter);
					} catch (IOException e) {
						logger
								.severe("Could not associate a file with the current logger: "
										+ e);
					} catch (SecurityException e) {
						logger.severe("Could not write to a log file: " + e);
					}
				}
			} else {

				JOptionPane.showMessageDialog(speechRecorderUI,
						"Cannot associate log handler "
								+ handlers[i].getName()
								+ " !\n Ignoring.", "Configuration error",
						JOptionPane.ERROR_MESSAGE);
			}

		}

		for (int logI = 0; logI < loggers.length; logI++) {
			ipsk.apps.speechrecorder.config.Logger l = loggers[logI];

			java.util.logging.Level level = java.util.logging.Level.parse(l.getLevel());
			String logName = l.getName();
			String handlerName = l.getHandlerName();
			// ipsk.apps.speechrecorder.config.Handler h=l.getHandler();
			// Formatter formatter=h.getFormatter();

			java.util.logging.Logger logger = Logger.getLogger(logName);
			logger.setLevel(level);
			// for(int i=0;i<handlers.length;i++){
			if (handlerName.equals(LOG_HANDLER_NAME)) {
				logger.addHandler(logFileHandler);
			} else if (handlerName.equals(TIMELOG_HANDLER_NAME)) {
				logger.addHandler(timeLogFileHandler);
			}

		}
		
		logger.info("Created logfiles.");
		logger.info("Operating System: " + System.getProperty("os.name") + " "
				+ System.getProperty("os.arch") + " "
				+ System.getProperty("os.version"));
		logger.info("JRE: " + System.getProperty("java.version") + " "
				+ System.getProperty("java.vendor"));
		logger.info("Speechrecorder version: " + VERSION);
		logger.info("Loglevel: " + logger.getLevel());
		
		if (useUploadCache) {
			setWaitForCompleteUpload(project.getCacheConfiguration()
					.getWaitForCompleteUpload());
			String uploadCacheClassname = project.getCacheConfiguration()
					.getUploadCacheClassname();
			try {

				uploadCache = (UploadCache) Class.forName(uploadCacheClassname)
						.newInstance();
			} catch (Exception e) {
				throw (new PluginLoadingException(uploadCacheClassname, e));
			}
			// uploadCache.setHoldSize(CACHE_HOLD_SIZE); //100 MB
			// POST is default now
			// uploadCache.setRequestMethod("POST");
			uploadCache.setOverwrite(project.getRecordingConfiguration().getOverwrite());
			int transferRateLimit=project.getCacheConfiguration().getTransferRateLimit();
			if(transferRateLimit!=UploadCache.UNLIMITED){
				if(uploadCache.isTransferLimitSupported()){
					uploadCache.setTransferLimit(transferRateLimit);
					logger.info("Upload cache set transfer rate limit: "+transferRateLimit);
				}else{
					logger.warning("Upload cache does not support transfer rate limiting !");
				}
			}
			if(uploadDuringSessionRecording){
			    uploadCache.start();
			    logger.info("Upload cache: " + uploadCache.getClass().getName()+ "started.");
			}
		}

		storageManager.setUploadCache(uploadCache);
		storageManager.setUseAsCache(useUploadCache);
		storageManager.setNumLines(numLines);
		storageManager.setOverwrite(overwrite);

		// set the audio compression to use, e.g. FLAC
		String audioCompression = project.getCacheConfiguration()
				.getAudioStorageType();
		if (audioCompression != null && !audioCompression.equals("")) {
			logger.fine("Requested audio upload type: " + audioCompression);
			AudioFileFormat.Type[] types = AudioSystem.getAudioFileTypes();
			AudioFileFormat.Type type = null;
			for (int i = 0; i < types.length; i++) {
				if (types[i].toString().equalsIgnoreCase(audioCompression))
					type = types[i];
			}
			if (type != null) {
				storageManager.setUploadType(type);
				logger.fine("Audio upload type " + audioCompression + " set.");
			} else {
				logger.warning("Requested audio type \"" + audioCompression
						+ "\" not available.");
			}
		}
		storageManager.setStorageURL(recBaseURL);
		// We do not use a session ID
		storageManager.setCreateSessionDir(false);
		storageManager.setUseScriptID(false);
		storageManager.open(true);
		
		if (projectContext != null) {
			recScriptManager.setContext(projectContext);
			recScriptManager.setSystemIdBase(projectContext.toExternalForm());
			speechRecorderUI.setProjectContext(projectContext);
		}
		
		recScriptManager.setDefaultSpeakerDisplay(pc.getShowPromptWindow());
		recScriptManager.setDefaultMode(Section.Mode.getByValue(recCfg.getMode()));
		recScriptManager.setDefaultPreDelay(recCfg.getPreRecDelay());
		recScriptManager.setDefaultPostDelay(recCfg.getPostRecDelay());
		recScriptManager.setDefaultAutomaticPromptPlay(pc.getAutomaticPromptPlay());
		
		sessionManager.setStorageManager(storageManager);
		
		// auto annotation
		
		 // build auto annotator descriptor list
        Set<AutoAnnotationServiceDescriptor> aasdSet=new HashSet<AutoAnnotationServiceDescriptor>();
	    Annotation annotationCfg=project.getAnnotation();
	    List<AutoAnnotationServiceDescriptor> aasds=autoAnnotatorPluginManager.getAutoAnnotatorServiceDescriptors();
	    
	    List<AutoAnnotator> enabledAutoAnnotators=new ArrayList<AutoAnnotator>();
	    if(annotationCfg!=null){
	        AutoAnnotation autoAnnoCfg=annotationCfg.getAutoAnnotation();
	        ipsk.apps.speechrecorder.config.AutoAnnotator[] aaCfgs=autoAnnoCfg.getAutoAnnotators();
	        if(aaCfgs!=null){
	            for(ipsk.apps.speechrecorder.config.AutoAnnotator aaCfg:aaCfgs){
	                boolean en=aaCfg.isEnabled();
	                if(en){
	                    String aaClasNm=aaCfg.getClassname();
	                    //        			Class<?> aaClass=Class.forName(aaClasNm);
	                    for(AutoAnnotationServiceDescriptor aasd:aasds){
	                        String aaSdClNm=aasd.getServiceImplementationClassname();
	                        if(aaClasNm.equals(aaSdClNm)){
	                            aasdSet.add(aasd);
	                            break;
	                        }
	                    }
	                    
	                }
	            }
	        }
	        List<AutoAnnotationServiceDescriptor> aasdResolvedlist=autoAnnotatorPluginManager.resolve(aasdSet);
	        for(AutoAnnotationServiceDescriptor aasd:aasdResolvedlist){
	            String aaClsNm=aasd.getServiceImplementationClassname();
	            Class<?> aaClass=Class.forName(aaClsNm);
	            AutoAnnotator aa=(AutoAnnotator) aaClass.newInstance();
                enabledAutoAnnotators.add(aa);
	        }
	    }
        
        // configure annotation
        // instantiate an initial annotation schema 
            schema = new Schema();
//            LevelDefinition promptLd=new LevelDefinition();
//            promptLd.setType(LevelDefinition.ITEM);
//            promptLd.setName(PROMPT_LEVEL_DEF_NAME);
//            AttributeDefinition promptAd=new AttributeDefinition();
//            promptAd.setName(PROMPT_LEVEL_DEF_NAME);
            LevelDefinition promptDef=new LevelDefinition(PredefinedLevelDefinition.PRT);
            LevelDefinition templateDef=new LevelDefinition(PredefinedLevelDefinition.TPL);
            schema.addLevelDefinition(promptDef);
            schema.addLevelDefinition(templateDef);
            
            // check annotation persistors
            bundleAnnotationPersistorList.clear();
            AnnotationPersistence annoPersCfg=annotationCfg.getPersistence();
            List<BundleAnnotationPersistorConfig> perCfgList=annoPersCfg.getBundleAnnotationPersistors();
            
           
            for(BundleAnnotationPersistorConfig perCfg:perCfgList){
                if(perCfg.isEnabled()){
                    Class<?> c=Class.forName(perCfg.getClassname());
                    Object o=c.newInstance();
                    if(o instanceof BundleAnnotationPersistor){
                        BundleAnnotationPersistor bap=(BundleAnnotationPersistor)o;
                        bundleAnnotationPersistorList.add(bap);
                    }
                }
            }
            
            autoAnnotationWorker=new AutoAnnotationWorker();
            List<AutoAnnotator> wAas=autoAnnotationWorker.getAutoAnnotators();
            wAas.clear();
//            autoAnnotatorPluginManager.resolve(enabledAutoAnnotators);
            for(AutoAnnotator aa:enabledAutoAnnotators){
                if(aa instanceof PromptAutoAnnotator){
                    promptAutoAnnotator=(PromptAutoAnnotator)aa;
                }
                if(aa instanceof TemplateAutoAnnotator){
                    templateAutoAnnotator=(TemplateAutoAnnotator)aa;
                }
                // TODO resolve plugin list order
                wAas.add(aa);
            }
            autoAnnotationWorker.addProgressListener(this);

            //        }

            // beep audio prompt file URL
            beepURL = null;
            PromptBeep promtBeepCfg=getConfiguration().getPromptConfiguration().getPromptBeep();
            String promptBeepUrlStr=promtBeepCfg.getBeepFileURL();
            if(promptBeepUrlStr!=null && ! "".equals(promptBeepUrlStr)){
            	try {
            		beepURL = URLContext.getContextURL(getProjectContext(),promptBeepUrlStr);
            	} catch (MalformedURLException e1) {
            		throw e1;
            	}
            }else{
            	beepURL=SampleManager.class.getResource("beep/beep_PCM_16bit_44100Hz.wav");
            }
            beepAudioSource = null;

            // defaults
            audioEnabled = false;
            seamlessAutoRecording=false;
		
		try {
			openAudioController();
			audioEnabled = true;
			
			captureScope=recCfg.getCaptureScope();
			
			if(captureScope==null){
				// application default is still ITEM, but new projects are configured
				// with SESSION by default
				captureScope=CaptureScope.ITEM;
			}
			
			// check seamless recoding capabilities
			
			if(recCfg.isSeamlessAutorecording() ){
			    // check if audio controller supports it
			    if(audioController.isFileTransitionRecordingSupported()){
			        // Only possible with recording directly to file. Recording to temporary file in seamless mode is not supported
			        if(!PrimaryRecordTarget.DIRECT.equals(primaryRecordTarget)){
			            speechRecorderUI.displayError("Audio configuration error",
		                        "Audio configuration error: Seamless recording is only posiible if primary recording target is direct file (DIRECT)");
			        }else{
			            // OK, set seamless recording mode
			            seamlessAutoRecording=true;
			        }
			    }else{ 
			        speechRecorderUI.displayError("Audio controller error",
	                    "Audio controller implementation class "+project.getAudioControllerClass()+" does not support recording file transition during capture.\nSeamless recording will be disabled !");
			    }
			}
		} catch (PluginLoadingException pe) {
			speechRecorderUI.displayError("Plugin laoading error",
					"Could not load audio controller plugin: " + pe
							+ "\naudio recording/playback will be disabled !");
		} catch (AudioControllerException ae) {
			speechRecorderUI.displayError("Audio controller error",
					"Could not open audio controller: " + ae
							+ "\naudio recording/playback will be disabled !");
		} catch (AudioManagerException ame) {
			speechRecorderUI.displayError("Audio manager error",
					"Could not open audio controller: " + ame
							+ "\naudio recording/playback will be disabled !");
		}
		
		
		
		recScriptManager.load(promptFile);
		lastSpeakerWindowRequest = false;
		speechRecorderUI.configure();
		speechRecorderUI.setStartStopSignalClass(startStopSignalClass);
        speechRecorderUI.setInstructionNumbering(project.getPromptConfiguration().getInstructionNumbering());
        
		if (project.getEditable()){
			// is this necessary here ?
            workspaceProjects=workspaceManager.scanWorkspace();
            speechRecorderUI.setWorkspaceProjects(workspaceProjects);
        }
		workspaceManager.lock(project.getName());
		init();
		return false;
	}

	
	
	
	
	private void openAudioController() throws PluginLoadingException,
			AudioControllerException, AudioManagerException {
		// set the audio controller implementation
		try {
			audioControllerClassName = project.getAudioControllerClass();
			audioController = (AudioController4) Class.forName(
					audioControllerClassName).newInstance();
		} catch (Exception e) {
			throw (new PluginLoadingException(audioControllerClassName, e));
		}
		AJSAudioSystem.setApplicationName(APPLICATION_NAME);
		AJSAudioSystem.setFreeDesktopApplicationIconName(FREEDESKTOP_APPLICATION_ICON_NAME);
		
		audioManager=new AudioManager(audioController);
		
		// extra player for beeps
		beepPlayer=new Player();
		
		// we are interested in reliable recordings
//		audioController
//				.setCaptureOptimizationMode(AudioController2.OPTIMIZE_FOR_RELIABILITY);
		audioController.setPreferredCaptureLineBufferSizeMilliSeconds(PREFERRED_LINE_BUFFER_SIZE_MILLIS);
		audioController.setPreferredPlaybackLineBufferSizeMilliSeconds(PREFERRED_LINE_BUFFER_SIZE_MILLIS);
		// Try to set max ASIO buffer size for IPSK JavaSound adapter
		// We do not need low latency
		audioController.setProperty("ASIO_USE_MAX_BUFFER_SIZE", "true");
		
		MixerName[] orgTargetMixerNames=project.getRecordingMixerName();
		MixerName[] targetMixerNames=ConfigHelper.getAJSConvertedMixerNames(audioController,orgTargetMixerNames);

		if (targetMixerNames != null && targetMixerNames.length > 0) {
			DeviceInfo matchedDeviceInfo=audioManager.findMatchingDeviceInfo(targetMixerNames, AJSAudioSystem.DeviceType.CAPTURE);
			
			if(matchedDeviceInfo==null){
				// we have one or more entries in the config list but no matching device found
				throw new AudioControllerException("No capture device matching configuration found!");
			}else{
				logger.info("Using capture mixer: "+matchedDeviceInfo);
				audioController.setCaptureDeviceByinfo(matchedDeviceInfo);
			}
		}else {
			logger.info("Using default capture mixer.");
		}
		
		MixerName[] orgSourceMixerNames=project.getPlaybackMixerName();
		MixerName[] sourceMixerNames=ConfigHelper.getAJSConvertedMixerNames(audioController,orgSourceMixerNames);

		if (sourceMixerNames != null && sourceMixerNames.length > 0) {
			DeviceInfo matchedDeviceInfo=audioManager.findMatchingDeviceInfo(sourceMixerNames, AJSAudioSystem.DeviceType.PLAYBACK);
			if(matchedDeviceInfo==null){
				// we have one or more entries in the config list but no matching device found
				throw new AudioControllerException("No playback device matching configuration found!");
			}else{
				//			    audioController.setPlaybackDeviceByName(sourceMixerName);
				audioController.setPlaybackDeviceByInfo(matchedDeviceInfo);
				logger.info("Using playback mixer: " + matchedDeviceInfo);

			}
		} else {
			logger.info("Using default playback mixer.");
		}
		
		
		RecordingConfiguration recCfg=project.getRecordingConfiguration();
		ChannelRouting captureChannelRouting=recCfg.getChannelAssignment();
		if(captureChannelRouting!=null){
			ipsk.io.ChannelRouting chRouting;
			int chOffset=captureChannelRouting.getChannelOffset();
			if(chOffset!=0){
				int recChs=recCfg.getFormat().getChannels();
				chRouting=new ipsk.io.ChannelRouting(true,chOffset,recChs);
			}else{
				int[] inChAssignment=captureChannelRouting.getAssign();
				Integer[] inChassignmentI=null;
				if(inChAssignment!=null){
					inChassignmentI=new Integer[inChAssignment.length];
					for(int i=0;i<inChAssignment.length;i++){
						inChassignmentI[i]=inChAssignment[i];
					}
				}
				Integer inChs=captureChannelRouting.getSrcChannelCount();
				chRouting=new ipsk.io.ChannelRouting(inChs, inChassignmentI);
			}
			audioController.setInputChannelRouting(chRouting);
		}

		MixerName[] promptPlayMixerNames=project.getPromptPlaybackMixerName();
		PromptConfiguration promptCfg=project.getPromptConfiguration();

		if (promptPlayMixerNames != null && promptPlayMixerNames.length > 0) {
			DeviceInfo matchedDeviceInfo=audioManager.findMatchingDeviceInfo(promptPlayMixerNames, AJSAudioSystem.DeviceType.PLAYBACK);
			if(matchedDeviceInfo==null){
				// we have one or more entries in the config list but no matching device found
				throw new AudioManagerException("No prompt playback device matching configuration found!");
			}else{
				List<MixerProviderServiceDescriptor> mpsdList=AJSAudioSystem.listMixerProviderDescriptors();
				for(MixerProviderServiceDescriptor mpsd:mpsdList){
					String mpsdClassName=mpsd.getImplementationClassname();
					String matcheddevProvClassname=matchedDeviceInfo.getDeviceProviderInfo().getImplementationClassname();
					if(mpsdClassName.equals(matcheddevProvClassname)){
						AJSDeviceInfo ajsDevInfo=new AJSDeviceInfo(mpsd,matchedDeviceInfo.getMixerInfo());
						//						AJSDevice ajsDevice=AJSAudioSystem.getDevice(ajsDevInfo);

						AJSDevice ajsDevice=AJSAudioSystem.getDevice(ajsDevInfo);
						//promptMixer=mm.getPlaybackMixerByName(sourceMixerName);
						promptMixer=ajsDevice.getMixer();
							speechRecorderUI.setPromptMixer(promptMixer);
						
						try {
							beepPlayer.setMixer(promptMixer);
						} catch (PlayerException e) {
							e.printStackTrace();
							throw new AudioManagerException("Could not set mixer for beep player!");
						}
						logger.info("Using prompt playback mixer: " + matchedDeviceInfo);
					}
				}
			} 
		}else {
			logger.info("Using default playback mixer.");
		}
		
		// set channel routing (only by channel offset) for prompt and beep  player
		int promptAudioChannelOffset=promptCfg.getAudioChannelOffset();
		speechRecorderUI.setPromptAudioChannelOffset(promptAudioChannelOffset);
		beepPlayer.setChannelOffset(promptAudioChannelOffset);

		audioController.addAudioController2Listener(this);
		// audioController.setNumLines(numLines);

		audioController.setRecordingAudioFileFormat(audioFileFormat);

		//		audioController.setPrimaryRecordTarget(useTempFile?PrimaryRecordTarget.TEMP_RAW_FILE:PrimaryRecordTarget.DIRECT);
		audioController.setPrimaryRecordTarget(primaryRecordTarget);
		audioController.setOverwrite(overwrite);

		// audioController.configure();
		// audioController.open();


		beepPlayer.addPlayerListener(this);
	}


	private void closeAudioController() {
		if (audioController != null) {
            audioController.removeAudioController2Listener(this);
			try {
				audioController.closePlayback();
				if(DEBUG)System.out.println("Closing capture.");
				audioController.closeCapture();
			} catch (AudioControllerException e) {
				speechRecorderUI.displayError("Audiocontroller error", e
						.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		if(beepPlayer !=null){
		 beepPlayer.removePlayerListener(this);
		 try {
            beepPlayer.close();
        } catch (PlayerException e) {
            speechRecorderUI.displayError("Beep player close error", e
                    .getLocalizedMessage());
            e.printStackTrace();
        }
		}

	}

	/**
	 * Sets a new configuration. Does not reconfigure the application.
	 * 
	 * @param newProject
	 */
	private void setConfiguration(ProjectConfiguration newProject) {
		project = newProject;
	}

	/**
	 * Returns the project configuration (bean).
	 * 
	 * @return the project configuration
	 */
	public ProjectConfiguration getConfiguration() {
		return project;
	}

	/**
	 * Returns a copy (new instance) of the project configuration (bean).
	 * 
	 * @return copy of project configuration
	 */
	public ProjectConfiguration getConfigurationCopy() throws DOMCodecException {
		return (ProjectConfiguration) domCodec.copy(project);
	}

	/**
	 * Returns the project configuration URL.
	 * 
	 * @return the URL where the configuration is stored
	 */
	public URL getProjectURL() {
		return projectURL;
	}

	/**
	 * Sets new project configuration URL.
	 * 
	 * @param url
	 *            the URL where the configuration is stored
	 */
	public void setProjectURL(URL url) {
		projectURL = url;
	}

//	/**
//	 * Copies a stream.
//	 * 
//	 * @param i
//	 *            the input
//	 * @param o
//	 *            the ouput
//	 * @throws IOException
//	 */
//	private void copyBufStream(InputStream i, OutputStream o)
//			throws IOException {
//		byte[] buf = new byte[2048];
//		int b;
//		while ((b = i.read(buf)) != -1) {
//			if (b > 0)
//				o.write(buf, 0, b);
//		}
//		o.close();
//		i.close();
//	}

	/**
	 * Copies a stream.
	 * 
	 * @param i
	 *            the input
	 * @param o
	 *            the ouput
	 * @throws IOException
	 */
	private void copyStream(InputStream i, OutputStream o) throws IOException {
		int b;
		while ((b = i.read()) != -1) {
			o.write(b);
		}
		o.close();
		i.close();
	}

    
    public File getProjectDir() throws MalformedURLException, URISyntaxException{
        ProjectConfiguration pc=getConfiguration();
        if(pc!=null){
            String dirStr=pc.getDirectory();
            URL projectDirURL = new URL(projectContext, dirStr);
            return new File(projectDirURL.toURI().getPath());
        }else{
            
            return null;
        }
            
        
       
    }
    
    
    public String defaultScriptUrlString(){
    	if(project!=null){
    		return project.getName()
				+ REC_SCRIPT_FILE_EXTENSION;
    	}
    	return null;
    }
    
	/**
	 * Create a new project.
	 * 
	 * @param newProjectConfig
	 *            the configuration for the new project
	 */
	public void newProject(NewProjectConfiguration newProjectConfig) throws Exception {
	    ProjectConfiguration newProject=newProjectConfig.getProjectConfiguration();
	    newProject.setUuid(UUID.randomUUID());
	    // application default is still ITEM, but new projects are configured
		// with SESSION by default
	    newProject.getRecordingConfiguration().setCaptureScope(CaptureScope.SESSION);
//		projectContext = (new File(defWorkspaceDir, newProject.getName()))
//				.toURI().toURL();
	    File projDir=new File(defWorkspaceDir, newProject.getName());
	    URI projDirURI=projDir.toURI();
	    // convert special chars, e.g. Umlaute
	    String projURLStr=projDirURI.toASCIIString();
	    projectContext=new URL(projURLStr);
		URL projectDirURL = new URL(projectContext, newProject.getDirectory());
		File projectDir = new File(projectDirURL.toURI().getPath());
		String parent = projectDir.getParent();
		File workspaceRoot = new File(parent);

		if (!workspaceRoot.exists()) {
			int res = JOptionPane.showConfirmDialog(speechRecorderUI,
					"Workspace directory " + workspaceRoot.getPath()
							+ " does not exist.\nCreate ?", "New Project",
					JOptionPane.OK_CANCEL_OPTION);
			if (res == JOptionPane.OK_OPTION) {
				if (!workspaceRoot.mkdirs()) {
					throw new Exception("Could not create directory "
							+ workspaceRoot.getPath());
				}
			}else{
				// Bug Fix ID0069
				throw new Exception("Could not create project without workspace directory.");
			}
		}

		if (projectDir.exists()) {
			// TODO this is not really an exception
			throw new Exception("Project (directory) already exists: "
					+ projectDir.getPath());
		}
		if (!projectDir.mkdirs()) {
			throw new Exception("Could not create directory "
					+ projectDir.getPath());
		}
		URI pdURI=projectDir.toURI();
		String projectDirURIstr=pdURI.toASCIIString();
		URL projectDirURLascii=new URL(projectDirURIstr);
		setProjectContext(projectDirURLascii);
		// Handler.setProjectDir(projectDir);
		String projectFilename = newProject.getName() + PROJECT_FILE_EXTENSION;
		String recScriptFileName = newProject.getName()
				+ REC_SCRIPT_FILE_EXTENSION;
		String speakersFilename = newProject.getName() + SPEAKER_FILE_SUFFIX;
		File projectFile = new File(projectDir, projectFilename);
		File recScriptFile = new File(projectDir, recScriptFileName);
		File speakersFile = new File(projectDir, speakersFilename);

		// copy recording script DTD
		InputStream is = getClass().getResourceAsStream(RecScriptManager.REC_SCRIPT_DTD);
		FileOutputStream fos = new FileOutputStream(new File(projectDir,
				RecScriptManager.REC_SCRIPT_DTD));
		copyStream(is, fos);

		if(newProjectConfig.isUseExampleScript()){
			// copy example recording script
			is = getClass().getResourceAsStream(REC_SCRIPT_EXAMPLE);
			fos = new FileOutputStream(recScriptFile);
			copyStream(is, fos);

			// set item code generator to match example script itemcodes
			
			ItemcodeGeneratorConfiguration icCfg=newProject.getPromptConfiguration().getItemcodeGeneratorConfiguration();
			icCfg.setGeneratorName("Demo script itemcode generator");
			
			// demo script has itemcodes from demo_000 ...
			icCfg.setPrefix("demo_");
			icCfg.setFixedDecimalPlaces(3);

			//... to demo_063
			// each unit starts a new block of the items
			// So new items added by the user should start at demo_070
			icCfg.setCounterStart(70);
			
			icCfg.setActive(true);

		}else{
		    Script newScript=new Script();
		    newScript.setPropertyChangeSupportEnabled(true);
		    recScriptManager.setScript(newScript);
		   
		}
		// copy example speakers file
		is = getClass().getResourceAsStream("ExampleSpeakers.txt");
//		fos = new FileOutputStream(speakersFile);
//		copyStream(is, fos);
		
		JAXB.marshal(new ArrayList<ipsk.db.speech.Speaker>(), speakersFile);

		// newProject.getPromptConfiguration().setPromptsUrl(recScriptFile.toURL().toExternalForm());
		// newProject.getPromptConfiguration().setPromptsUrl(
		// recScriptFile.toURL().toExternalForm());
		// newProject.getSpeakers().setSpeakersUrl(
		// speakersFile.toURL().toExternalForm());
		// newProject.getRecordingConfiguration().setUrl(
		// "file:" + projectDir + File.separator + "RECS");
		
		URI recScriptURI=recScriptFile.toURI();
		URI projectDirURI=projectDir.toURI();
		URI recScriptRelURI=projectDirURI.relativize(recScriptURI);
//		URL recScriptURL=recScriptRelURI.toURL();
		newProject.getPromptConfiguration().setPromptsUrl(recScriptRelURI.toString());
//		newProject.getPromptConfiguration().setPromptsUrl(
//				"file:" + recScriptFileNameURLEncoded);
		URI speakersFileURI=speakersFile.toURI();
		URI speakersFileRelURI=projectDirURI.relativize(speakersFileURI);
		newProject.getSpeakers().setSpeakersUrl(speakersFileRelURI.toString());
		
		// TODO
		newProject.getRecordingConfiguration().setUrl("RECS/");
		setConfiguration(newProject);
		setProjectURL(projectFile.toURI().toURL());

		// recScriptManager.initialize();
		String systemIdBase=projectContext.toExternalForm();
		recScriptManager.setSystemIdBase(systemIdBase);

		saveProject();
		if(!newProjectConfig.isUseExampleScript()){
		 saveScript();
		}
		configure(newProject);
		 

	}

	

	/**
	 * Save current project configuration.
	 * 
	 * @throws DOMCodecException
	 * @throws DOMConverterException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public void saveProject() throws DOMCodecException, DOMConverterException,
			IOException, URISyntaxException {
		// save project file
		String fPath=getProjectURL().toURI().getPath();
		File f = new File(fPath);
		
//		File backupF=new File(fPath+".bak");
//		f.renameTo(backupF);
		ProjectConfiguration pc=getConfiguration();
//		String pcVersion=pc.getVersion();
//		if(pcVersion==null || PROJECT_VERSION.equals(pcVersion)){
		
		// always set to the version which created the configuration
		pc.setVersion(PROJECT_VERSION);
//		}
		Document d = domCodec.createDocument(getConfiguration());
		// backup
		FileUtils.moveToBackup(f, ".bak");
		FileOutputStream fos=new FileOutputStream(f);
		OutputStreamWriter ow=new OutputStreamWriter(fos,Charset.forName("UTF-8"));
		domConverter.writeXML(d,ow);
		ow.close();
		setProjectConfigurationSaved(true);
	}

	/**
	 * Save project file.
	 * 
	 * @param file
	 *            save to this file
	 * @throws DOMCodecException
	 * @throws DOMConverterException
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public void saveProject(File file) throws DOMCodecException,
			DOMConverterException, IOException, URISyntaxException {
		try {
			setProjectURL(file.toURI().toURL());
		} catch (MalformedURLException e) {
			// Should never happen
			e.printStackTrace();
		}
		saveProject();
	}

	/**
	 * Save current project configuration.
	 * 
	 * @throws DOMCodecException
	 * @throws DOMConverterException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws URISyntaxException 
	 */
	public void saveScript() throws DOMCodecException, DOMConverterException,
			IOException, ParserConfigurationException, URISyntaxException {
        
        RecscriptHandler recScriptHandler = new RecscriptHandler();
        recScriptHandler.setValidating(true);
        RecScriptManager recScriptManager=RecScriptManager.getInstance();
        Script script=recScriptManager.getScript();
        
        // Write DTD file to project workspace if it does not exist
        
        File dtdFile=new File(getProjectDir(),RecScriptManager.REC_SCRIPT_DTD);
        if(!dtdFile.exists()){
            InputStream is = getClass().getResourceAsStream(RecScriptManager.REC_SCRIPT_DTD);
            FileOutputStream fos = new FileOutputStream(dtdFile);
            copyStream(is, fos);
        }
        
        // Validate
       
        StringWriter stringWriter=new StringWriter();
        recScriptHandler.writeXML(script,stringWriter);
        StringReader stringReader=new StringReader(stringWriter.toString());
        recScriptHandler.readScriptFromXML(stringReader, projectContext.toExternalForm());
        
       
        
        // Backup old file
        String promptFileName = project.getPromptConfiguration().getPromptsUrl();

        // set instance variables to the values given as arguments
        if (promptFileName != null && !promptFileName.equals("")) {
        	URL promptFile = URLContext.getContextURL(projectContext,
        			promptFileName);
        	String protocol=promptFile.getProtocol();
        	if("file".equalsIgnoreCase(protocol)){
        		String fPath=promptFile.toURI().getPath();
        		File f = new File(fPath);
        		//        	 File backupFile=new File(promptFile.toURI().getPath()+".bak");
        		//        	 f.renameTo(backupFile);
        		FileUtils.moveToBackup(f, ".bak");
        		// Finally write the script file
        		recScriptHandler = new RecscriptHandler();
        		recScriptHandler.setValidating(true);
        		recScriptHandler.writeXML(script,
        				new OutputStreamWriter(new FileOutputStream(f),Charset.forName("UTF-8")));
        		recScriptManager.setScriptSaved(true);

        	}else{
        		speechRecorderUI.displayError("Save script error", "Cannot save script to URL: "+promptFile+", protocol "+protocol+ "not supported.");
        	}
        }else{
        	speechRecorderUI.displayError("Save script error", "Cannot save script to empty URL!");
        }
	}

	/**
	 * Save speaker database file.
	 * 
	 * @throws IOException
	 * @throws URISyntaxException 
	 */
	public void saveSpeakerDatabase() throws IOException, URISyntaxException {
		File f = new File(speakerURL.toURI().getPath());
		// Backup old file
		
//		if(f.exists()){
//		    File backupFile=new File(f.getPath()+".bak");
//		    boolean bkfDeleted=true;
//		    if(backupFile.exists()){
//		    	bkfDeleted=backupFile.delete();
//		    }
//		    if(bkfDeleted){
//		    	boolean moved=f.renameTo(backupFile);
//		    	if(!moved){
//		    		speechRecorderUI.displayError("Speaker database file error", "Could not move "+f+"\n as backup to \n"+backupFile+" !");
//		    	}
//		    }else{
//		    	speechRecorderUI.displayError("Speaker database backup file error", "Could not deklet old backup file:\n"+backupFile+" !");
//		    }
//		}
		FileUtils.moveToBackup(f, ".bak");
		speakerManager.getDatabaseLoader().writeDatabaseFile(f);
		// TODO the databaseloader should set the state
		speakerManager.setDatabaseSaved(true);
	}

	/**
	 * Save project configuration, speaker database and script file.
	 * 
	 * @throws DOMCodecException
	 * @throws DOMConverterException
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws URISyntaxException 
	 */
	public void saveAll() throws DOMCodecException, DOMConverterException,
			IOException, ParserConfigurationException, URISyntaxException {
		saveProject();
		saveSpeakerDatabase();
        saveScript();
	}

	/**
	 * Open a project.
	 * 
	 * @param projectUrl
	 *            must contain the project configuration
	 * @throws ClassNotFoundException
	 * @throws DOMCodecException
	 * @throws DOMConverterException
	 * @throws PluginLoadingException
	 * @throws AudioControllerException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws WorkspaceException 
	 * @throws URISyntaxException 
	 * @throws RecscriptManagerException 
	 */
	public boolean configureProject(URL projectUrl) throws ClassNotFoundException,
			DOMCodecException, DOMConverterException, StorageManagerException,
			PluginLoadingException, AudioControllerException,
			ParserConfigurationException, SAXException, IOException,
			InstantiationException, IllegalAccessException, WorkspaceException, URISyntaxException, RecscriptManagerException{
		
//		Document d = domConverter.readXML(projectUrl.openStream());
		URLConnection prUrlConn=projectUrl.openConnection();
		prUrlConn.setUseCaches(false);
		InputStream prInStream=prUrlConn.getInputStream();
		Document d = domConverter.readXML(prInStream);
		ProjectConfiguration p = (ProjectConfiguration) domCodec
				.readDocument(d);
		setProjectURL(projectUrl);
		ConfigHelper.applyLegacyToStrictConversions(p);
		boolean canceled=configure(p);
		
		setProjectConfigurationSaved(true);
		return canceled;
	}

	public synchronized void actionPerformed(ActionEvent e) {
	    Object src=e.getSource();
		if (src== maxRecTimer) {
		    if(DEBUG)System.out.println("Max rec timer event !");
			preRecTimer.stop();
			boolean forcePostRecPhase=project.getRecordingConfiguration().isForcePostRecDelayPhase();
			if(forcePostRecPhase){
				startPostRecordingPhase();
			}else{
			try {
				stopRecording();
			} catch (AudioControllerException ex) {
				speechRecorderUI.displayError("Audiocontroller error",
						"Error on stop of audio recording\n"
								+ ex.getLocalizedMessage());
				ex.printStackTrace();
				logger.severe(ex.getMessage());
			}
			}
		} else if (src == preRecTimer) {
		    if(DEBUG)System.out.println("Precrec timer event !");
			if (recStat.getStatus() == RecStatus.PRERECWAITING){
				startRecordingPhase();
				if(DEBUG)System.out.println("Recording phase  !");
			}else if (recStat.getStatus() == RecStatus.POSTRECWAITING)
				if(annotatingEnabled){
				startAnnotation();
				}else{
				setIdle();
				startPrompt();
				}
		} else if (src == postRecTimer) {
		    if(DEBUG)System.out.println("Postrec timer event !");
			try {
				stopRecording();
			} catch (AudioControllerException ex) {
				speechRecorderUI.displayError("Audio controller error ",
						"Error on stop of audio recording\n"
								+ ex.getLocalizedMessage());
				logger.severe(ex.getMessage());
			}
		}else if (src == nonRecordingTimer) {
		    setIdle();
            if(isAutoRecording()){
                continueSession();
            }
        }
	}
	
	
	public void openProject(String projectName) throws InstantiationException, IllegalAccessException, ClassNotFoundException, PluginLoadingException, AudioControllerException, IOException, DOMConverterException, DOMCodecException, WorkspaceException, URISyntaxException, RecscriptManagerException, StorageManagerException{
		boolean canceled=configureProject(projectName);
		if(!canceled){
			speechRecorderUI.showSpeakerDatabase();
			start();
		}
	}
	
	private Bundle loadBundle(BundleAnnotationFilePersistor bafp,File annoFile,Bundle bundle){
	    if(annoFile.exists()){
	        bafp.setFile(annoFile);

	        try{
	            Bundle annotBundle=bafp.load(bundle);
	            return annotBundle;
	        }catch(IOException ioe){
	            speechRecorderUI.displayError("Could not read annotation file \""+annoFile+"\"", ioe);
	        }catch(ParserException pe){
	            speechRecorderUI.displayError("Annotion parsing error","Could not read annotation file \""+annoFile+"\"");
	        }
	    }
	    return null;
	}

	/**
	 * Refresh the signal display.
	 */
	private synchronized void setRecDisplay() {
	    
	    Integer recIndex=sessionManager.getRecIndex();
	    
		if (recIndex!= null && sessionManager.getRecCounter(recIndex) > 0) {
			// URL[] recUrls = storageManager.getAudioFiles();
			if (useUploadCache) {
				try {
					// InputStream[] recIss =
					// uploadCache.getCachedInputStream(recUrls);

//					InputStream[] recIss = storageManager
//							.getCachedInputStreams();
				    File[] recFiles=storageManager.getCachedInputFiles();
					if (recFiles != null) {
//						BufferedInputStream[] biss = new BufferedInputStream[recIss.length];
//						AudioInputStream[] aiss = new AudioInputStream[numLines];
//						for (int i = 0; i < numLines; i++) {
//							biss[i] = new BufferedInputStream(recIss[i]);
//							aiss[i] = AudioSystem.getAudioInputStream(biss[i]);
//						}
						
						audioClip.setBundle(null);
					    audioClip.setAudioSource(new FileAudioSource(recFiles[0]));
						//speechRecorderUI.setRecDisplay(recUrls);
//						for (int i = 0; i < numLines; i++) {
//							aiss[i].close();
//							biss[i].close();
//							recIss[i].close();
//						}
					} else {
						// Do not download files
						// speechRecorderUI.getRecDisplay().setDisplay(recUrls);
						//speechRecorderUI.clearRecDisplay();
						audioClip.setBundle(null);
					    audioClip.setAudioSource(null);
					}
				} catch (Exception e) {
					speechRecorderUI.displayError("Audio system error",
							"Cannot get audio stream.");
				}

			} else {
				URL[] audioFileURLs = null;
				
				try {
					audioFileURLs = storageManager.generateAudioFileURLs();
					
				} catch (StorageManagerException e) {
					e.printStackTrace();
					String msg = "Storage Exception: "
							+ e.getLocalizedMessage();
					logger.severe(msg);
					speechRecorderUI.displayError("Storage manager error", msg);
				}
				try {
					//speechRecorderUI.getRecDisplay().setDisplay(audioFileURLs);
				    if(audioFileURLs.length>1){
				        throw new AudioSourceException("Mutiple lines are currently not supported.");
				    }
				    File[] audioFiles=null;
				    if(audioFileURLs!=null){
						audioFiles=new File[audioFileURLs.length];
						for(int i=0;i<audioFileURLs.length;i++){
							audioFiles[i]=StorageManager.fileURLToFile(audioFileURLs[i]);
						}
					}
				  
				    Bundle bundle=buildBaseBundle(audioFiles);
					String bundleRootFn=storageManager.getRootFileName();
					URL sessURL=storageManager.getSessionURL();
					File sessDir=StorageManager.fileURLToFile(sessURL);
				    
			        if(sessDir!=null){
			            Bundle annotBundle=null;
			            for(BundleAnnotationPersistor bap:bundleAnnotationPersistorList){
			                if(bap instanceof BundleAnnotationFilePersistor && bap.isLossless()){
			                    BundleAnnotationFilePersistor bafp=(BundleAnnotationFilePersistor)bap;
			                    File annoFile=new File(sessDir,bundleRootFn+bafp.getPreferredFilenameSuffix()+"."+bafp.getPreferredFileExtension());
			                    annotBundle=loadBundle(bafp, annoFile, bundle);
			                   
			                }else{
			                    // currently only local files
			                }
			                // stop if lossless persistor loaded complete annotation
			                if(annotBundle!=null){
			                    break;
			                }
			            }
			            if(annotBundle==null){
			                // no lossless persistor found try others
			                for(BundleAnnotationPersistor bap:bundleAnnotationPersistorList){
			                    if(bap instanceof BundleAnnotationFilePersistor){
			                        BundleAnnotationFilePersistor bafp=(BundleAnnotationFilePersistor)bap;
			                        File annoFile=new File(sessDir,bundleRootFn+bafp.getPreferredFilenameSuffix()+"."+bafp.getPreferredFileExtension());
			                        annotBundle=loadBundle(bafp, annoFile, bundle);
			                    }else{
			                        // currently only local files
			                    }
			                }
			            }
			            if(annotBundle!=null){
			                // set annotated bundle as current bundle
			                bundle=annotBundle;
			            }
			        }
				   
				    audioClip.setAudioSource(new URLAudioSource(audioFileURLs[0]));
				    audioClip.setBundle(bundle);
				} catch (Exception ex) {
					ex.printStackTrace();
					String msg = "Exception: " + ex.getLocalizedMessage();
					logger.severe(msg);
					speechRecorderUI.displayError("Display error", msg);
				}

			}
		} else {
			//speechRecorderUI.getRecDisplay().clearDisplay();
			audioClip.setBundle(null);
		    audioClip.setAudioSource(null);
		}
	}
	
	public boolean isProjectEditable(){
        ProjectConfiguration pc=getConfiguration();
        if(pc==null) return false;
        return pc.getEditable();
    }
    public void setEditingEnabled(boolean b) {  
//      Updates enabled/disabled status of project or speaker setting menu items
       boolean projectEditable=isProjectEditable();
       boolean editingEnabled=b;
       editScriptAction.setEnabled(projectEditable && editingEnabled);
       importScriptAction.setEnabled(projectEditable && editingEnabled);
       exportScriptAction.setEnabled(projectEditable && editingEnabled);
       speechRecorderUI.setEditingEnabled(b);
   }
    
    private int preRecDelay(Recording r){
        int preRecDelay;
        Integer piPreRecDelay=r.getPrerecdelay();
        if(piPreRecDelay!=null){
            preRecDelay=piPreRecDelay;
        }else{
            preRecDelay=project.getRecordingConfiguration().getPreRecDelay();
        }
        return preRecDelay;
    }
    
    private int postRecDelay(Recording r){
        int postRecDelay;
        Integer recPostRecDelay=r.getPostrecdelay();
        if(recPostRecDelay!=null){
            postRecDelay=recPostRecDelay;
        }else{
            postRecDelay=project.getRecordingConfiguration().getPostRecDelay();
        }
        return postRecDelay;
    }
    
    private int minRecLengthMs(Recording r){
        int preRecDelay=preRecDelay(r);
        int postRecDelay=postRecDelay(r);
        return preRecDelay+postRecDelay;
    }
    
    
	public void startItem(){
	    repeatRequest=null;
	    ipsk.db.speech.PromptItem promptItem = sessionManager
        .getCurrentPromptItem();
        boolean isRecording =(promptItem instanceof Recording);
        // check overwrite first
        if (isRecording) {
         
            File recFile;
            List<File> annotationFiles=new ArrayList<File>(0);
            try {
                recFile = storageManager.getNewRecordingFiles()[0];
                if(!useUploadCache && bundleAnnotationPersistorList!=null && bundleAnnotationPersistorList.size()>0){
                	annotationFiles=currentAnnotationFiles();
            	}
            } catch (StorageManagerException e) {
                  JOptionPane.showMessageDialog(speechRecorderUI.getDialogTarget(), e.getMessage(),"Storage error!",JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    return;
            }
            if(DEBUG)System.out.println("Recfile: "+recFile);
            RecordingConfiguration recConfig=project.getRecordingConfiguration();
            boolean overWriteConfirmed=recConfig.getOverwrite();
            boolean projectOverwriteWarning=recConfig.isOverwriteWarning();
            if(projectOverwriteWarning && sessionOverwriteWarning && !useUploadCache){
                overWriteConfirmed=false;
                boolean recFileExists=recFile.exists();
                int existingAnnoFileCnt=0;
                for(File annoFile:annotationFiles){
                    if(annoFile.exists()){
                        existingAnnoFileCnt++;
                    }
                }
                if(recFileExists){
                    //              Object[] options=new Object[]{"No","Yes","Yes to all in this session","Yes to all in this project"};
                    Object[] options=new Object[]{"No","Yes","Yes to all in this session","Yes to all in this project"};
                    String msg="Recording file ";
                    if(existingAnnoFileCnt>0){
                        msg=msg.concat("and "+existingAnnoFileCnt+" annotation ");
                        if(existingAnnoFileCnt==1){
                            msg=msg.concat("file ");
                        }else{
                            msg=msg.concat("files ");
                        }
                        msg=msg.concat("already exist!");
                    }else{
                        msg=msg.concat("already exists!");
                    }
                    msg=msg.concat("\nDo you want to overwrite?");
                    int selOpt=JOptionPane.showOptionDialog(speechRecorderUI.getDialogTarget(),msg, "Overwrite warning",JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,options,options[0]);
                    if(selOpt==JOptionPane.CLOSED_OPTION || selOpt==0){
                        overWriteConfirmed=false;
                        setProgressPaused(true);
                        setIdle();
                       
                        return;
                    }else if(selOpt==2){
                        overWriteConfirmed=true;
                        sessionOverwriteWarning=false;
                    }else if(selOpt==3){
                        overWriteConfirmed=true;
                       recConfig.setOverwriteWarning(false);
                       projectConfigurationSaved=false;
                    }
                   
                }
            }
            
            if(overWriteConfirmed){
                //  delete annotation
                for(File annoFile:annotationFiles){
                    if(annoFile.exists()){
                        // TODO with Java 7: get exception 
                        boolean deleted=annoFile.delete();
                        if(!deleted){
                            JOptionPane.showMessageDialog(speechRecorderUI.getDialogTarget(), "Could not delete annotation file "+annoFile.getName(),"Delete error",JOptionPane.ERROR_MESSAGE );
                        }
                    }
                }
            }
        }
       
        setEditingEnabled(false);
        setIndexAction.setEnabled(false);
        recTransporterActions.startAutoRecordingAction.setEnabled(false);
        recTransporterActions.continueAutoRecordingAction.setEnabled(false);
        recTransporterActions.startRecordAction.setEnabled(false);
        recTransporterActions.stopRecordAction.setEnabled(false);
        recTransporterActions.advanceToNextAction.setEnabled(false);
        recTransporterActions.forwardAction.setEnabled(false);

        recTransporterActions.backwardAction.setEnabled(false);

        recTransporterActions.startPlaybackAction.setEnabled(false);
        recTransporterActions.stopPlaybackAction.setEnabled(false);
        recTransporterActions.pausePlaybackAction.setEnabled(false);
        
        if (isAutoRecording()) {
            recTransporterActions.pauseAutoRecordingAction.setEnabled(false);
            recTransporterActions.continueAutoRecordingAction.setEnabled(false);
            recTransporterActions.startAutoRecordingAction.setEnabled(false);
        }
        if (Section.PromptPhase.IDLE.equals(sessionManager.currentPromptPhase()) || !isRecording) {
            boolean blocked = true;
            if (promptItemCode != null) {
                timeLogger.info("PLAY_PROMPT: " + promptItemCode);
            }else{
                timeLogger.info("PLAY_PROMPT:");
            }
            if(isRecording){
                Recording recording=(Recording)promptItem;

                blocked = recording.getNNBlocked();
                
                recStat.setStatus(RecStatus.PLAY_PROMPT);
                if(!blocked){
                    // drop playing beep here
                    // currently we have only an audio media prompter 
                    // the direct audio line does not support mixing so we cannot play a beep
                    // Check if we can play a beep
                    List<Mediaitem> mis=recording.getMediaitems();
                    boolean containsAudio=false;
                    for(Mediaitem mi:mis){

                        String mimeType=mi.getNNMimetype();
                        int mimeSep=mimeType.indexOf("/");
                        if(mimeSep>0){
                            mimeType=mimeType.substring(0,mimeSep);
                        }
                        if(mimeType.equalsIgnoreCase("audio")){
                            containsAudio=true;
                            break;
                        }
                    }
                    if(containsAudio){
                        startPreRecWaiting();
                    }else{
                        try {
                            startBeep();
                        } catch (SpeechRecorderException e) {
                            e.printStackTrace();
                            speechRecorderUI.displayError("Beep playback error", e);
                            setIdle();
                            startPrompt();
                        }
                    }
                }
            }else{
                recStat.setStatus(RecStatus.PLAY_PROMPT);
            }
            speechRecorderUI.setPromptStartControlEnabled(recManualPlay);
            try {
				speechRecorderUI.startPlayPrompt();
			} catch (PrompterException e) {
				e.printStackTrace();
				setIdle();
			}

        } else {
            
            try {
                startBeep();
            } catch (SpeechRecorderException e) {
                e.printStackTrace();
                speechRecorderUI.displayError("Beep playback error", e);
                setIdle();
            }
        }
	}
	
	private void startNonRecording(){
        ipsk.db.speech.PromptItem promptItem = sessionManager
        .getCurrentPromptItem();
        Integer duration=null;
        if(promptItem instanceof Nonrecording){
        Nonrecording nr=(Nonrecording)promptItem;
          duration=nr.getDuration();
        }
        if(duration!=null){
            if(DEBUG)System.out.println("Start non recording");
           
            timeLogger.info("NON_RECORDING:");
            
            recStat.setStatus(RecStatus.NON_RECORDING_WAIT);
            nonRecordingTimer = new javax.swing.Timer(duration, this);
            nonRecordingTimer.setRepeats(false);
            nonRecordingTimer.start();
            
        }else{
            setIdle();
            if(isAutoRecording()){
                continueSession();
            }
        }
    }
	
	public void startBeep() throws SpeechRecorderException{
	    ipsk.db.speech.PromptItem promptItem = sessionManager
        .getCurrentPromptItem();
	    boolean playBeep=false;
	    if(promptItem instanceof Recording){
	    Recording recording=(Recording)promptItem;
		   playBeep=new Boolean(recording.getBeep());
	    }
        if(playBeep){
            if(DEBUG)System.out.println("Start beep");
            if (promptItemCode != null) {
                timeLogger.info("PLAY_BEEP: " + promptItemCode);
            }else{
                timeLogger.info("PLAY_BEEP:");
            }
            recStat.setStatus(RecStatus.PLAY_BEEP);
            try {
            	
                beepPlayer.setAudioSource(beepAudioSource);
                
                if(beepPlayer.isOpen()){
                    System.err.println("Beep player still open!");
                }
                beepPlayer.open();
                beepPlayer.play();
            }  catch (PlayerException e) {
                e.printStackTrace();
                try {
                    stopRecording();
                } catch (AudioControllerException e1) {
                    throw new SpeechRecorderException(e1);
                }
                speechRecorderUI.displayError("Beep playback error", e);
                startPreRecWaiting();
            }
            
        }else{
            startPreRecWaiting();
        }
	}

	public void startPreRecWaiting() {

	    setEditingEnabled(false);
	    setIndexAction.setEnabled(false);
	    speechRecorderUI
	    .setLevelMeterMode(SpeechRecorderUI.LEVEL_METER_RECORDING);
	    if (getConfiguration().getRecordingConfiguration()
	            .getResetPeakOnRecording()) {
	        LevelInfo[] lis=audioController.getCaptureLevelInfos();
	        if(lis!=null){
	            for(LevelInfo li:lis){
	                li.setPeakLevelHold(0);
	            }
	        }
	        speechRecorderUI.getLevelMeter().resetPeakHold();
	    }
	    if (promptItemCode != null) {
	        timeLogger.info("PRERECORDING: " + promptItemCode);
	    }
	   
	    recDisplayValid=false;

	    recStat.setStatus(RecStatus.PRERECWAITING);
	    speechRecorderUI.setRecMonitorsStatus(StartStopSignal.State.PRERECORDING);
	    try {
	        if(DEBUG)System.out.println("Start recording ...");
	        startRecording();

	    } catch (AudioControllerException e) {
	        speechRecorderUI.displayError("Audiocontroller error",
	                "Error on start of audio recording\n"
	                + e.getLocalizedMessage());
	        repeatRequest=new RepeatRequest();
	        e.printStackTrace();
	        //            setIdle();
	        setProgressPaused(true);
	        
	        continueSession();
	        return;
	    } catch (StorageManagerException e) {
	        speechRecorderUI.displayError("Storage error",
	                "Error on start of audio recording\n"
	                + e.getLocalizedMessage());
	        repeatRequest=new RepeatRequest();
	        e.printStackTrace();
	        //            setIdle();
	        setProgressPaused(true);
	        continueSession();
	        return;
	    }

	    RecWindow rw=speechRecorderUI.getRecWindow();
	    if(rw!=null){
	        PromptViewer pv0=rw.getPromptViewer();
	        if(pv0!=null){
	            pv0.setInstructionsEmphased(true);
	        }
	    }
	    PromptViewer pv1=speechRecorderUI.getPromptViewer();
	    if(pv1!=null){
	        pv1.setInstructionsEmphased(true);
	    }
	    if (Section.PromptPhase.PRERECORDING.equals(sessionManager.currentPromptPhase())){
	        speechRecorderUI.setShowPromptViewers(true);
	        speechRecorderUI.setPromptStartControlEnabled(recManualPlay);
	        try {
				speechRecorderUI.startPlayPrompt();
			} catch (PrompterException e) {
				e.printStackTrace();
				setIdle();
			}
	    }

	}
    
	
	public void startRecordingPhase(){
		
		setIndexAction.setEnabled(false);
		if (promptItemCode != null) {
			timeLogger.info("RECORDING: " + promptItemCode);
		}
		speechRecorderUI.getPromptViewer().setInstructionsEmphased(false);
		speechRecorderUI.getRecWindow().getPromptViewer().setInstructionsEmphased(false);
		speechRecorderUI.getPromptViewer().setPromptEmphased(true);
		speechRecorderUI.getRecWindow().getPromptViewer().setPromptEmphased(true);
		if (Section.PromptPhase.RECORDING.equals(sessionManager.currentPromptPhase())) {
            speechRecorderUI.setShowPromptViewers(true);
            speechRecorderUI.setPromptStartControlEnabled(false);
            try {
				speechRecorderUI.startPlayPrompt();
			} catch (PrompterException e) {
				e.printStackTrace();
				setIdle();
			}
		
			// new behaviour with prompt pahse recording and prompt blocking:
			// the traffic light switches to green after the pprompt is played
			if(!sessionManager.currentPromptBlocking()){
				speechRecorderUI.setRecMonitorsStatus(StartStopSignal.State.RECORDING);
			}
    			
		}else{
			speechRecorderUI.setRecMonitorsStatus(StartStopSignal.State.RECORDING);
		}
		recStat.setStatus(RecStatus.RECORDING);
	}
	
	
	public void startPostRecordingPhase(){
		if(DEBUG)System.out.println("Starting post recording phase");
		setIndexAction.setEnabled(false);
		preRecTimer.stop();
		if(maxRecTimer!=null)maxRecTimer.stop();
		if (promptItemCode != null) {
			timeLogger.info("POSTRECORDING: " + promptItemCode);
		}
		speechRecorderUI.getPromptViewer().stop();
		speechRecorderUI.getRecWindow().getPromptViewer().stop();
		int postRecDelay=postRecDelay((Recording)promptItem);
		postRecTimer = new javax.swing.Timer(postRecDelay, this);
		postRecTimer.setRepeats(false);
		postRecTimer.start();
		
		recStat.setStatus(RecStatus.POSTRECWAITING);
		speechRecorderUI.setRecMonitorsStatus(StartStopSignal.State.POSTRECORDING);
	}

//	/**
//	 * implements the RecObserver interface.
//	 * 
//	 * @param status
//	 *            the new status
//	 */
//	public void update(int status) {
//		if (status == RecStatus.INIT) {
//			
//			// speechRecorderUI.setEnableEditing(true);
//		} else if (status == RecStatus.CLOSE) {
//			
//		} else {
//			String promptItemCode = null;
//			ipsk.db.speech.PromptItem promptItem = recScriptManager
//					.getCurrentPromptItem();
//			
//			if (status == RecStatus.IDLE) {
//				
//			} else if (status == RecStatus.PRERECWAITING) {
//				// Moved to own method
//			} else if (status == RecStatus.RECORDING) {
////				 Moved to own method// Moved to own method
//			} else if (status == RecStatus.POSTRECWAITING) {
////				 Moved to own method
//			} else if (status == RecStatus.PLAY) {
//				
//			}
//		}
//	}

	public void startRecording() throws AudioControllerException,
			StorageManagerException {
		ipsk.db.speech.PromptItem promptItem = sessionManager
				.getCurrentPromptItem();
		if (promptItem instanceof Recording) {
			Recording pi = (Recording) promptItem;

			int preRecDelay=preRecDelay(pi);
			
			preRecTimer = new javax.swing.Timer(preRecDelay, this);
			// System.out.println(
			// "maxRecTime: " + recScriptManager.getMaxRecTimeMillis() + " ms");
			
			// Create max rec timer if required
			maxRecTimer=null;
			
			
			// Do not use the prompt item method anymore
			// it does not consider project default values
			
			//Integer totalRecTime=pi.getTotalRecTime();
//			 if(totalRecTime!=null){
//	                maxRecTimer = new javax.swing.Timer(totalRecTime, this);
//	                maxRecTimer.setDelay(totalRecTime);
//	                maxRecTimer.setRepeats(false);
//	          }
			
			Integer recDuration=pi.getRecduration();
            if(recDuration!=null){
            	RecordingConfiguration recCfg=project.getRecordingConfiguration();
                // calculate max recording time
                int postRecDelay;
                Integer piPostRecDelay=pi.getPostrecdelay();
                if(piPostRecDelay!=null){
                    postRecDelay=piPostRecDelay;
                }else{
                    postRecDelay=recCfg.getPostRecDelay();
                }
                
                long totalRecTime=preRecDelay+recDuration+postRecDelay;
                long recTime=totalRecTime;
                if(recCfg.isForcePostRecDelayPhase()){
                	recTime=preRecDelay+recDuration;
                }
                if(useMaxRecTimer || seamlessAutoRecording){
                    // seamless autorecording only works with timer limited recording
                    maxRecTimer = new javax.swing.Timer((int)recTime, this);
                    maxRecTimer.setDelay((int)recTime);
                    maxRecTimer.setRepeats(false);
                }else{
                    // capture engine limits the maximum recording length
                    // has the advantage that the recordings have exactly maximum frame length
                    // of the recduration attribute
                    
                    // (currently not used)
                    float frameRate=audioController.getAudioFileFormat().getFormat().getFrameRate();
                    long maxFrameLength=((long)((float)totalRecTime*frameRate))/1000;
                    audioController.setMaxRecordingFrameLength(maxFrameLength);
                }
            }
			
			preRecTimer.setDelay(preRecDelay);
			preRecTimer.setRepeats(false);
			
			audioClip.setAudioSource(null);
			File recFile=storageManager.getNewRecordingFiles()[0];
			
			audioController.setRecordingFile(recFile);
			
			boolean silenceDetection=pi.needsSilenceDetector();
			Integer finalSilence=pi.getFinalsilence();
			
			if(silenceDetection){
			    silenceDetector.setSilencelength((double)finalSilence/1000.0);
			}
			// levelMeter.resetPeak();
//			System.gc();
//			Thread.yield();
			if (debugSinusTest)
			    if(DEBUG)System.out.println("Start recording item: "
						+ pi.getItemcode());
			
			if(!audioController.isCaptureOpen()){
			    if(DEBUG)System.out.println("Opening capture.");
			    if(silenceDetection){
			        audioController.addCaptureFloatAudioOutputStream(voiceDetector);
			    }
			    audioController.openCapture();
			    audioController.startRecording();
			}else{
//			    if(!seamlessAutoRecording){
			        audioController.startRecording();
			        
//			    }
			}
			if(silenceDetection){
//			    voiceDetector.start();
				silenceDetector.start();
			}
			if(DEBUG)System.out.println("Recording started");
		
		}
	}

	public void setProgressPaused(boolean progressPaused) {
		this.progressPaused = progressPaused;
		speechRecorderUI.setProgressPaused(progressPaused);
	}

	public boolean getProgressPaused() {
		return progressPaused;
	}

	public synchronized void stopRecording() throws AudioControllerException {
//		boolean repeat = false;
	    if(DEBUG)System.out.println("Stop recording");
		if(preRecTimer!=null)preRecTimer.stop();
		if(maxRecTimer!=null)maxRecTimer.stop();
		if(isAutoRecording() && seamlessAutoRecording && ! progressPaused){
		    PromptItem pi=sessionManager.getCurrentPromptItem();
		    lastPromptItem=pi;
		    continueSession();
		    // TODO
		    // recScriptManager.incrementIndex(); ??
		}else{
		try {
		    boolean continueCapture=CaptureScope.SESSION.equals(captureScope);
			audioController.stopRecording(continueCapture);
			// TODO
			// setLogEntries();
			// String labelFilename =
			// new String(
			// filePrefix
			// + String.valueOf(speakerID)
			// + recScriptManager.getPromptCode()
			// + labelFileExtension);
			//
			// recLogger.createLabelFile(labelFilename);

			// recScriptManager.incrementRecCounter(
			// recScriptManager.getRecIndex());
			// //URL[] recUrls = storageManager.getAudioFiles();
			// if (useUploadCache) {
			// //Upload[] uploads = new Upload[numLines];
			// storageManager.upload();
			// //uploadCache.upload(uploads);
			// }
		} catch (AudioControllerException e) {
			speechRecorderUI
					.displayError(
							"Audiocontroller error",
					"Technical error: \n"
									+ e.getLocalizedMessage()
							+ "\nPlease press OK.\nthe recording will be repeated.");
			logger.severe(e.getMessage());
//			repeat = true;
		}
		// } catch (StorageManagerException e) {
		// speechRecorderUI.displayError(
		// "Storage error",
		// "Storage error: " + e.getLocalizedMessage());
		// logger.severe(e.getMessage());
		// repeat = false;
		// }
		// continueSession(repeat);

		logger.fine("Recording stopped");
		if(DEBUG)System.out.println("Recording stopped");
		}
	}
	
//	/**
//     * @return
//     */
//    private boolean autoRecordSeamless() {
//       
//        return(audioController!=null && 
//                audioController.isFileTransitionRecordingSupported() &&
//                isAutoRecording() &&
//                project.getRecordingConfiguration().isSeamlessAutorecording());
//    }



    public synchronized void stopNonrecording(){
	    
	    if(nonRecordingTimer!=null){
	        nonRecordingTimer.stop();
	    }
	    if(speechRecorderUI.isPromptClosed()){

            if(DEBUG)System.out.println("Non recording stop event. Continue session...");
   
                continueSession();
           
        }else{
            if(DEBUG)System.out.println("Non recording stop. wait for prompt viewer....");
            speechRecorderUI.closePrompt();
        }
	  
	}
	
	
	
	public void startAnnotation(){
	   
        if(promptItem!=null){
        if (promptItem instanceof Recording) {
            
            timeLogger.info("ANNOTATE: " + promptItemCode);
        } else if(promptItem instanceof Nonrecording){
            timeLogger.info("ANNOTATE: Nonrecording");
        }
        }
                setEditingEnabled(true);
				speechRecorderUI.idle();
				speechRecorderUI
						.setLevelMeterMode(SpeechRecorderUI.LEVEL_METER_DISABLE);
    	int recVersions=0;
    	if(promptItem instanceof Recording){
				try {
					recVersions = storageManager.getRecordedVersions();
				} catch (StorageManagerException e) {
					e.printStackTrace();
				}
				
				
	}
    	itemPlayable = (recVersions > 0);
    	setRecDisplay();
    }
	
	public void startPlayback(long start,long stop) throws AudioControllerException,
	StorageManagerException {
	    //boolean allCached = true;

	    setEditingEnabled(false);
	    audioController.setPlaybackFile(storageManager.getRecordingFiles()[0]);
	    audioController.openPlayback();
	    audioController.setPlaybackStartFramePosition(start);
	    audioController.setPlaybackStopFramePosition(stop);
	    audioController.startPlayback();
	    // }
	}
	public void startPlayback() throws AudioControllerException,
			StorageManagerException {
//		boolean allCached = true;

		setEditingEnabled(false);
		audioController.setPlaybackFile(storageManager.getRecordingFiles()[0]);
		audioController.openPlayback();
		audioController.setPlaybackStartFramePosition(0);
        audioController.setPlaybackStopFramePosition(AJSAudioSystem.NOT_SPECIFIED);
		audioController.startPlayback();
		// }
	}

	// public URL[] getRecordingURLs() {
	//
	// URL[] urls;
	// urls = new URL[numLines];
	// PromptItem pi = recScriptManager.getCurrentPromptItem();
	// storageManager.setPromptCode(pi.getPromptItemCode());
	// storageManager.setSpeakerCode(speakerManager.getSpeaker().getCode());
	// urls = storageManager.getAudioFiles();
	//
	// return urls;
	// }

    public boolean isAutoRecording(){
//        Section.Mode mode = getCurrentMode();
//        return(mode
//                .equals(Section.Mode.AUTORECORDING));
        return (Section.Mode.AUTORECORDING.equals(sectionMode));
    }
    public boolean isAutoProgress(){
//        Section.Mode mode = getCurrentMode();
//        return(isAutoRecording() || mode
//                .equals(Section.Mode.AUTOPROGRESS));
        return (isAutoRecording() || Section.Mode.AUTOPROGRESS.equals(sectionMode));
    }
    
	/**
	 * When all recordings of a session have been performed, the user is
	 * informed that the session is over. Otherwise, if the automatic_recording
	 * mode is on, then the next recording is started. If automatic_recording is
	 * off, the application waits for the user to continue the recording
	 * session.
	 * 
	 */

	public void continueSession() {
		
		if (sessionManager.allRecordingsDone() && repeatRequest==null) {
			//setRecDisplay();
			//recStat.setStatus(RecStatus.IDLE);
			setIdle();
			//setRecDisplay();
			if (!allRecordingsDoneNotified) {

				JOptionPane.showMessageDialog(speechRecorderUI
						.getDialogTarget(), uiString
						.getString("DialogRecordingsCompleteText"), uiString
						.getString("DialogRecordingsCompleteTitle"),
						JOptionPane.INFORMATION_MESSAGE);
				allRecordingsDoneNotified = true;

			}
		} else {
		    boolean autoProgress = isAutoProgress();
		    boolean autoRecording = isAutoRecording();
		    
		    if(repeatRequest!=null){
		       
		        String title=repeatRequest.getMessageTitle();
		        String msg=repeatRequest.getMessage();
		        setIdle();
		        if(msg!=null){
		            JOptionPane.showMessageDialog(speechRecorderUI
		                    .getDialogTarget(),msg,title,
		                    JOptionPane.WARNING_MESSAGE);
		        }


		        //	                MessageFormat repeatAdviceMsg = new MessageFormat(uiString
		        //                            .getString("prompt_repeat_advice"));

		        
		        

		        if (autoRecording && !progressPaused) {

		            MessageFormat form = new MessageFormat(uiString
		                    .getString("prompt_repeat_info"));

		            JOptionPane.showMessageDialog(speechRecorderUI
		                    .getDialogTarget(), form
		                    .format(new Object[] { new Float(
		                            RECORD_RETRY_DELAY / 1000) }), uiString
		                            .getString("prompt_repeat"),
		                            JOptionPane.INFORMATION_MESSAGE);
		            try {
		                Thread.sleep(RECORD_RETRY_DELAY);
		            } catch (InterruptedException e) {
		                // no problem
		            }

		            startItem();
		        }else{
		          
		            JOptionPane.showMessageDialog(speechRecorderUI
	                        .getDialogTarget(),uiString.getString("prompt_repeat") , uiString
	                        .getString("prompt_repeat"),
	                        JOptionPane.INFORMATION_MESSAGE);
		            
		        }
		    }else{
		        if (autoProgress) {
		            if (project.getRecordingConfiguration()
		                    .getProgressToNextUnrecorded()) {
		                sessionManager.advanceToNextRecording();
		            } else {
		                sessionManager.incrementIndex();
		            }
		            // Update autoRecording flag
		            // Bug fix ID0032
	                autoRecording=isAutoRecording();
		        }else{
		            setIdle();
		        }

		       
		        
		        if (autoRecording && !progressPaused) {
		            startItem();
		        }
		    }
		}
		
	}
	
	
	public boolean saveAllProjectDataInteractive(){
		
		if (!isProjectConfigurationSaved()) {
			int option = JOptionPane.showConfirmDialog(speechRecorderUI,
					"The project has been modified.\nDo you want to save ?",
					"Confirm message", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				try {
					saveProject();
				} catch (Exception e) {
					speechRecorderUI.displayError("Save error", e
							.getLocalizedMessage());
                    return false;
				}
			} else if (option == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		if (!speakerManager.isDatabaseSaved()) {
			int option = JOptionPane
					.showConfirmDialog(
							speechRecorderUI,
							"The speaker database has been modified.\nDo you want to save ?",
							"Confirm message",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				try {
					saveSpeakerDatabase();
				} catch (Exception e) {
					speechRecorderUI.displayError("Save error", e
							.getLocalizedMessage());
                    return false;
				}
			} else if (option == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		if (!recScriptManager.isScriptSaved()) {
			int option = JOptionPane
					.showConfirmDialog(
							speechRecorderUI,
							"The recording script has been modified.\nDo you want to save ?",
							"Confirm message",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.INFORMATION_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				try {
					saveScript();
				} catch (Exception e) {
					speechRecorderUI.displayError("Save error", e
							.getLocalizedMessage());
                    return false;
				}
			} else if (option == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		
		return true;
	}
	

	/**
	 * Close a session. Prompts the user if files are modified and/or the upload
	 * cache has not completely transferred the data.
	 * 
	 * @throws AudioControllerException
	 *             if the controller cannot be closed
	 * @return true if closing is confirmed, false if it is canceled
	 * @throws WorkspaceException 
	 */
	public synchronized boolean close() throws AudioControllerException,
			StorageManagerException, WorkspaceException{
	    
//	    if(recStat.getStatus()==RecStatus.CLOSE || recStat.getStatus()==RecStatus.TERMINATE) return true;
	    if(recStat.getStatus()==RecStatus.CLOSE ) return true;
	    // ignore subsequent close requests
//	    if(recStat.getStatus()==RecStatus.TERMINATE) return false;
	   
//	    if(LineOpenScope.SESSION.equals(lineOpenScope)){
//	        if(DEBUG)System.out.println("Closing capture.");
//	    	audioController.closeCapture();
//		}
	    
	    boolean allSaved=saveAllProjectDataInteractive();
	    if(!allSaved)return false;
	    	
		recStat.setStatus(RecStatus.TERMINATE);
		if(autoAnnotationWorker!=null){
			// TODO warn user ?
		    autoAnnotationWorker.cancel();
		    try {
		        autoAnnotationWorker.close();
		        autoAnnotationWorker=null;
		    } catch (WorkerException e1) {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
		    }
		}
		annotationSession=null;
		closeAudioController();
	    setEditingEnabled(false);
	    speechRecorderUI.closeSession();

		if (!useUploadCache){
            workspaceProjects=workspaceManager.scanWorkspace();
        speechRecorderUI.setWorkspaceProjects(workspaceProjects);
        }
		//closeAudioController();
		audioEnabled = false;
		//speechRecorderUI.getRecDisplay().clearDisplay();
		audioClip.setBundle(null);
		audioClip.setAudioSource(null);
		
		if (useUploadCache) {

			if (!uploadCache.isIdle()) {
				// set parent to null, if the main window (speechrecorderUI)
				// closes, this dialog is remaining on the screen.
				final JDialog f = new JDialog((Frame) null, uiString
						.getString("UploadProgress"));
				f.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				// JDialog f = new JDialog(speechRecorderUI, "Uploading not
				// finished !");
				JLabel pleaseWaitLabel = new JLabel();
				pleaseWaitLabel.setBorder(BorderFactory.createEmptyBorder(10,
						10, 10, 10));
				Font defFont = speechRecorderUI.getFont();
				pleaseWaitLabel.setFont(defFont.deriveFont(Font.BOLD));
				if (waitForCompleteUpload) {
					pleaseWaitLabel.setText(uiString
							.getString("PleaseWaitForCompleteUpload"));
				} else {
					pleaseWaitLabel.setText(uiString
							.getString("PleaseWaitForUploadCanceling"));
				}
				f.getContentPane().setLayout(new BorderLayout());
				f.getContentPane().add(pleaseWaitLabel,BorderLayout.CENTER);
				f.getContentPane().add(speechRecorderUI.getUploadCacheUI(),BorderLayout.SOUTH);
				Runnable doShow = new Runnable() {
					public void run() {
						f.pack();
						f.setLocationRelativeTo(speechRecorderUI);
						f.setVisible(true);
					}
				};

				try {
					if (SwingUtilities.isEventDispatchThread()) {
						doShow.run();
					} else {
						SwingUtilities.invokeAndWait(doShow);
					}
				} catch (InterruptedException e) {
					// OK
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// Hmm ?
					e.printStackTrace();
				}

			}
		}else{
		    // close storage manager in standalone mode
		    if(storageManager!=null){
		        storageManager.close();
		    }
		}

		// recStat.setStatus(RecStatus.TERMINATE);
		setProjectURL(null);
		
		setIndexAction.setEnabled(false);
//		editScriptAction.setEnabled(false);
//		importScriptAction.setEnabled(false);
		speechRecorderUI
				.setLevelMeterMode(SpeechRecorderUI.LEVEL_METER_DISABLE);
		speechRecorderUI.setRecMonitorsStatus(ipsk.apps.speechrecorder.monitor.StartStopSignal.State.OFF);
		sessionManager.doClose();
		recScriptManager.doClose();
		speakerManager.close();
		lastSessionId=null;
		bundleAnnotationPersistorList.clear();
		recStat.setStatus(RecStatus.CLOSE);
		workspaceManager.unlock(project.getName());
        setConfiguration(null);
		return true;
	}

	/**
	 * Shutdown the application. If an uploading cache is used this method waits
	 * for complete upload.
	 */
	public void shutdown() {
		speechRecorderUI.setRecMonitorsStatus(StartStopSignal.State.OFF);
	    recStat.setStatus(RecStatus.TERMINATE);
		speechRecorderUI.setEnableOpenOrNewProject(false);
		Runnable doShutdown = new Runnable() {
			// Runnable is likely to be thread safe, it does not call Swing methods
			public void run() {
				if (uploadCache != null) {

					if (!waitForCompleteUpload) {
					    // interrupt uploads
						uploadCache.stop();
						try {
							storageManager.close(false);
						} catch (StorageManagerException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						// remove pending uploads
						uploadCache.clear();
						// uploadCache.start();

					}
					
					// restart the upload engine to upload log files now 
					uploadCache.start();

					if (logFileHandler != null) {
						// TODO the upload of files after this point is not in the log file anymore
					    
					    // remove handler and close
						logger.removeHandler(logFileHandler);
						logFileHandler.close();
						
						// upload
						File logFile = logFileHandler.getFile();
						URL logFileURL;
						try {
						
							logFileURL = storageManager.getLogFile();
							UploadFile logUpload = new UploadFile(logFile,logFileURL);
							uploadCache.upload(new Upload[] { logUpload });
                        } catch (UploadException e) {
                            e.printStackTrace();
                            // proceed with shutdown
                         // log file may be lost
                        } catch (StorageManagerException e) {
							e.printStackTrace();
							 // proceed with shutdown
	                         // log file may be lost
						}
					}
					if (timeLogFileHandler != null) {
					    // same for time log file
						timeLogger.removeHandler(timeLogFileHandler);
						timeLogFileHandler.close();
						File timeLogFile = timeLogFileHandler.getFile();
						
						try {
							UploadFile timelogUpload = new UploadFile(timeLogFile,
									storageManager.getTimeLogFile());
                            uploadCache.upload(new Upload[] { timelogUpload });
                        } catch (UploadException e) {
                            e.printStackTrace();
                         // proceed with shutdown
                           // log file may be lost
                        } catch (StorageManagerException e) {
							e.printStackTrace();
							// proceed with shutdown
	                           // log file may be lost
							
						}
					}
					
					// wait for upload cache finish
					while (!uploadCache.isIdle()) {
						try {
							Thread.sleep(SHUTDOWN_RETRY_DELAY);
						} catch (InterruptedException e) {
						}
					}
					
					// close upload engine and storage cache
					uploadCache.close();
					try {
						storageManager.close(true);
					} catch (StorageManagerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.exit(0);
			}
		};
		Thread shutdownThread = new Thread(doShutdown);
		// TODO not thread save !!
		// shutdown thread is not AWT event thread !!
		shutdownThread.start();
	}

	// /**
	// * Shows the skipping dialog.
	// */
	// public void skipRecordings() {
	// recScriptManager.skipRecordings();
	// }

	// public void setAudioSettings() {
	// // not supported for now
	// // if (audioController != null)
	// // audioController.setSettings();
	// }

	/**
	 * Start a recording session. The application must be configured,
	 * initialized and a speaker must be chosen to start a session.
	 * @throws AudioControllerException 
	 */
	public void start() throws AudioControllerException {
	    if(DEBUG)System.out.println("Start session.");
	    sessionOverwriteWarning=true;
//	    speechRecorderUI.showSpeakerDatabase();
		ipsk.db.speech.Speaker spk = speakerManager.getSpeaker();
//		if (spk == null)
//			speechRecorderUI.doSpkSettings();
//		spk = speakerManager.getSpeaker();

		if (spk == null)
			return;
		sessionManager.addSessionManagerListener(this);
		// Session ID and speaker ID are the same in this version
		int sessionId=speakerManager.getSpeaker().getPersonId();
		if(lastSessionId!=null && lastSessionId!=sessionId){
			recScriptManager.shuffleItems();
		}
		lastSessionId=sessionId;
		sessionManager.setScript(recScriptManager.getScript());
		if (storageManager != null) {
			storageManager.setSessionID(sessionId);
			annotationSession=new Session();
			NumberFormat sessFmt=storageManager.getSessionIDFormat();
			String sessNm=sessFmt.format(sessionId);
			annotationSession.setName(sessNm);
			storageManager
					.setSpeakerCode(speakerManager.getSpeaker().getCode());
		}
		try {
			storageManager.createSessionDirectory();
			if (sessionManager != null) {
				sessionManager.resetItemMarkers();
				sessionManager.updateItemMarkers();
			}
		} catch (SessionManagerException e1) {
			e1.printStackTrace();
			return;
		} catch (StorageManagerException e) {
			e.printStackTrace();
			return;
		}
		
		if(autoAnnotationWorker!=null){
		    try {
		        autoAnnotationWorker.open();
		    } catch (WorkerException e1) {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
		    }
		    autoAnnotationWorker.start();
		}
		

		if (audioEnabled){
		    if(seamlessAutoRecording){
		        URL recSessInfUrl;
                try {
                    recSessInfUrl = storageManager.getRecordingSessionInfoFile();
                    File recSessInfFile=new File(recSessInfUrl.toURI().getPath());
                    audioController.setRecordingSessionInfoFile(recSessInfFile);
                } catch (StorageManagerException e) {
                	// TODO
                  //throw new SpeechRecorderException(e);
                } catch (URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
		        
		    }
//		    applyItem();
		    int itemCount=recScriptManager.getMaxIndex();
		    if(itemCount>0){
		    	sessionManager.setRecIndex(0);
		    }else{
		        // set undefined
				sessionManager.setRecIndex(null);
		    }
		    if(sessionManager.needsBeep()){

		    	URLAudioSource orgBeepAs=new URLAudioSource(beepURL);
		    	Double beepVol=getConfiguration().getPromptConfiguration().getPromptBeep().getBeepGainRatio();
		    	if(beepVol!=null){
		    		PluginChain beepPc=new PluginChain(orgBeepAs);
		    		VolumeControlPlugin vcp=new VolumeControlPlugin();
		    		try {
		    			beepPc.add(vcp);
		    		} catch (AudioFormatNotSupportedException e) {
		    			e.printStackTrace();
		    			String eMsg="Could not set beep volume: "+e.getMessage();
		    			logger.severe(eMsg);
		    			throw new AudioControllerException(eMsg);
		    		}
		    		vcp.setGainRatio(beepVol);
		    		beepAudioSource=beepPc;
		    	}else{
		    		beepAudioSource=orgBeepAs;
		    	}
		    }
		    
		    boolean silenceDetection=sessionManager.needsSilenceDetector();
		    if(silenceDetection && silenceDetector==null){
		        voiceDetector=new VoicedSpeechDetector();
		        silenceDetector=new SpeechFinalSilenceDetector(voiceDetector,this);
		    }
		    
		    if(CaptureScope.SESSION.equals(captureScope)){
		        if(silenceDetection){
		            audioController.addCaptureFloatAudioOutputStream(voiceDetector);
		        }
		        if(DEBUG)System.out.println("Opening capture.");
		        try{
		        	audioController.openCapture();
		        	audioController.startCapture();
		        }catch(AudioControllerException ace){
		        	// Display error, close the audio controller, but then proceed here
		        	// (The user should be able to fix the project configuration)
		        	speechRecorderUI.displayError("Audio controller error", ace);
		        	closeAudioController();
		        }
		    }
		}
	}

	/**
	 * returns the current RecScriptManager to allow access to the script.
	 * 
	 * @return RecScriptManager
	 */
	public RecScriptManager getRecScriptManager() {
		return recScriptManager;
	}

	/**
	 * returns the file name of the current recording script
	 * 
	 * @return String recording script file name
	 */
	public String getRecScriptName() {
		if (promptFile == null)
			return null;
		return promptFile.toExternalForm();
	}

	/**
	 * getRecDirName() returns the name of the recording directory
	 * 
	 * @return String recording directory name
	 */
	public String getRecDirName() {
		if (recBaseURL == null)
			return null;
		return recBaseURL.toExternalForm();
	}

	/**
	 * returns the speaker selected from the database
	 * 
	 * @return Speaker current speaker
	 */
	public ipsk.apps.speechrecorder.db.Speaker getSpeaker() {
		return speakerManager.getSpeaker();
	}

	public void setLogEntries() {
		recLogger.setLogEntry("LHD: ", System.getProperty("LHD"));
		recLogger.setLogEntry("DBN: ", System.getProperty("DBN"));
		recLogger.setLogEntry("REP: ", System.getProperty("REP"));
		recLogger.setLogEntry("RSW: ", uiString.getString("QTSpeechRecorder"));
		recLogger.setLogEntry("MIT: ", System.getProperty("MIT"));
		recLogger.setLogEntry("MIP: ", System.getProperty("MIP"));
		sessionManager.setLogEntries();
	}

	/**
	 * Indicates the use of a the upload cache in web mode.
	 * 
	 * @return true if an upload cache is used.
	 */
	public boolean isUsingUploadCache() {
		return useUploadCache;
	}

	/**
	 * Returns the upload cache.
	 * 
	 * @return the upload cache or null if not used
	 */
	public UploadCache getUploadCache() {
		return uploadCache;
	}

	/**
	 * if true, a separate window is shown for the speaker prompts
	 * 
	 * @param v
	 */
	public void setSpeakerWindowShowing(boolean v) {
		speakerWindow = v;
		speechRecorderUI.setSpeakerWindowShowing(speakerWindow);
		
	}

	/**
	 * returns true if the speaker window is shown
	 * 
	 * @return true if the speaker window is shown
	 */
	public boolean isSpeakerWindowShowing() {
		return speakerWindow;
	}

	/**
	 * Returns the UI object.
	 * 
	 * @return UI object
	 */
	public SpeechRecorderUI getSpeechRecorderUI() {
		return speechRecorderUI;
	}

	/**
	 * Stops playback.
	 */
	public void stopPlayback() throws AudioControllerException {
		audioController.stopPlayback();
	}

	/**
	 * Pauses playback.
	 */
	public void pausePlayback() throws AudioControllerException {
		audioController.pausePlayback();
	}

	/**
	 * Continues playback after pause.
	 */
	public void continuePlayback() {

		try {
			audioController.startPlayback();
		} catch (AudioControllerException e) {
			speechRecorderUI.displayError("AudioController Error", e
					.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Returns the audio controller.
	 * 
	 * @return the audio controller
	 */
	public AudioController4 getAudioController() {
		return audioController;

	}

	/**
	 * Returns whether the project configuration is saved.
	 * 
	 * @return true if the project configuration is saved
	 */
	public boolean isProjectConfigurationSaved() {
		return projectConfigurationSaved;
	}

	/**
	 * Set true if the project configuration is saved.
	 * 
	 * @param saved
	 *            true if the configuration is saved
	 */
	public void setProjectConfigurationSaved(boolean saved) {
		projectConfigurationSaved = saved;
		speechRecorderUI.setProjectConfigurationSaved(saved);
		speechRecorderUI.updateSaveEnable();
	}

	/**
	 * Returns the default workspace directory.
	 * 
	 * @return default workspace directory
	 */
	public File getDefWorkspaceDir() {
		return defWorkspaceDir;
	}

//	/**
//	 * Set the default directory for the workspace.
//	 * 
//	 * @param file
//	 *            the workspace dir
//	 */
//	public void setDefWorkspaceDir(File file) {
//		defWorkspaceDir = file;
//	}

	/**
	 * Returns whether the current selected recording can be played.
	 * 
	 * @return true if the recording is available
	 */
	public boolean isItemPlayable() {

		return itemPlayable;
	}

	/**
	 * @param projectName
	 * @throws PluginLoadingException
	 * @throws AudioControllerException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws DOMConverterException
	 * @throws WorkspaceException 
	 * @throws DOMCodecException 
	 * @throws DOMCodecException 
	 * @throws WorkspaceException 
	 * @throws URISyntaxException 
	 * @throws RecscriptManagerException 
	 * @throws StorageManagerException 
	 */
	public boolean configureProject(String projectName) throws PluginLoadingException,
			AudioControllerException, IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			DOMConverterException, DOMCodecException, WorkspaceException, URISyntaxException, RecscriptManagerException, StorageManagerException{
		List<WorkspaceProject> workspaceProjects=workspaceManager.getWorkspaceProjects();
		boolean canceled=false;
		for (int i = 0; i < workspaceProjects.size(); i++) {
			ProjectConfiguration p = ((WorkspaceProject) workspaceProjects.get(i))
					.getConfiguration();
			if (p.getName().equals(projectName)) {
				setProjectURL(workspaceProjects.get(i).getProjectFile().toURI().toURL());
				File projectDir=new File(defWorkspaceDir, p.getName());
				URI projectURI=projectDir.toURI();
//				projectContext = projectURI.toURL();
				// convert special chars, e.g. Umlaute
				String projURLStr=projectURI.toASCIIString();
				projectContext=new URL(projURLStr);
				ConfigHelper.applyLegacyToStrictConversions(p);
				canceled=configure(p);
//				System.out.println("Highest session ID: "+storageManager.highestSessionID());
				setProjectConfigurationSaved(true);
				break;
			}
		}
		return canceled;
	}

	/**
	 * Gets the speaker manager.
	 * 
	 * @return the speaker manager
	 */
	public SpeakerManager getSpeakerManager() {
		return speakerManager;
	}

	/**
	 * Initialize the recorder. All components get ready, but are still
	 * disabled.
	 */
	public void init() {
		allRecordingsDoneNotified = false;
		sessionManager.setRecIndex(null);
		annotationSession=null;
		recStat.setStatus(RecStatus.INIT);
		setEditingEnabled(true);
		speechRecorderUI.init();
//		setIndexAction.setEnabled(false);
	}
	
	public void applyItem() throws PromptPresenterException{
//	    String promptItemCode = null;
        promptItem = sessionManager
                .getCurrentPromptItem();
        if(promptItem!=null){
        if (promptItem !=null && promptItem instanceof Recording) {
            promptItemCode=((Recording) promptItem).getItemcode();
        }else{
            promptItemCode=null;
        }
        storageManager.setPromptCode(promptItemCode);
        
        speechRecorderUI.setRecIndex(sessionManager.getRecIndex());
        speechRecorderUI.setPromptItem(promptItem);
        
        Section currentRecSection=sessionManager.getCurrentRecSection();
        if(currentRecSection!=null){
         // project mode is overwritten by section mode
            Section.Mode projectMode = Section.Mode.getByValue(project.getRecordingConfiguration().getMode());
            Section.Mode scriptMode=currentRecSection.getMode();
            if (scriptMode != null) {
                sectionMode=scriptMode;
            }else{
                sectionMode=projectMode;
            }
        Boolean speakerDisplay = currentRecSection.getSpeakerDisplay();
        boolean currentSpeakerWindowRequest = project
                .getPromptConfiguration().getShowPromptWindow();
        if (speakerDisplay != null) {
            currentSpeakerWindowRequest = speakerDisplay.booleanValue();
        }

        if (lastSpeakerWindowRequest != currentSpeakerWindowRequest) {
            setSpeakerWindowShowing(currentSpeakerWindowRequest);
        }
        lastSpeakerWindowRequest = currentSpeakerWindowRequest;
        }else{
            sectionMode=null;
        }
        
        speechRecorderUI.setAutoRecording(isAutoRecording());
        }else{
            
        }
        recDisplayValid=false;
       
	}
	
	public void setPromptErrorState(){
	    if(promptItem!=null){
	        if (promptItem instanceof Recording) {
	            timeLogger.info("ERROR: " + promptItemCode);
	        } else if(promptItem instanceof Nonrecording){
	            timeLogger.info("ERROR: Nonrecording");
	        }
	    }
		setEditingEnabled(true);
		speechRecorderUI.idle();
		speechRecorderUI
				.setLevelMeterMode(SpeechRecorderUI.LEVEL_METER_DISABLE);
		// TODO should be done if speaker/script  changes
		storageManager.setSpeakerCode(speakerManager.getSpeaker()
				.getCode());
		Script script = recScriptManager.getScript();
		storageManager.setScriptID(script.getName());
		if(!recDisplayValid){
			int recVersions=0;
			if(promptItem instanceof Recording){
			try {
				recVersions = storageManager.getRecordedVersions();
				  // set to last recorded version
			    storageManager.setRecVersion(recVersions - 1);
				} catch (StorageManagerException e) {
					e.printStackTrace();
				}
			}
			    // itemPlayable = storageManager.isRecorded();
			    itemPlayable = (recVersions > 0);
			    speechRecorderUI.setPlaybackEnabled(itemPlayable);
			    setRecDisplay();
			    recDisplayValid=true;
		  
		}
		speechRecorderUI.setShowPromptViewers(false);
		speechRecorderUI.setRecMonitorsStatus(ipsk.apps.speechrecorder.monitor.StartStopSignal.State.OFF);
        recStat.setStatus(RecStatus.ITEM_ERROR);
        if(isAutoRecording()){
			setProgressPaused(true);
//        	setIdle();
//			continueSession(false);
		}
       
	}
	
	public void setIdle(){
	    repeatRequest=null;
//		ipsk.db.speech.PromptItem promptItem = recScriptManager
//				.getCurrentPromptItem();
		//speechRecorderUI.setPromptItem(promptItem);
		//speechRecorderUI.setRecIndex(recScriptManager.getRecIndex());
		//speechRecorderUI.setEditingEnabled(true);
//	    if(recStat.getStatus()==RecStatus.IDLE) return;
	    if(promptItem!=null){
	        if (promptItem instanceof Recording) {
	            timeLogger.info("IDLE: " + promptItemCode);
	        } else if(promptItem instanceof Nonrecording){
	            timeLogger.info("IDLE: Nonrecording");
	        }
	    }
		setEditingEnabled(true);
		speechRecorderUI.idle();
		if(CaptureScope.SESSION.equals(captureScope)){
		    speechRecorderUI
            .setLevelMeterMode(SpeechRecorderUI.LEVEL_METER_CAPTURE);
		}else{
		    speechRecorderUI
				.setLevelMeterMode(SpeechRecorderUI.LEVEL_METER_DISABLE);
		}
		// TODO should be done if speaker/script  changes
		storageManager.setSpeakerCode(speakerManager.getSpeaker()
				.getCode());
		Script script = recScriptManager.getScript();
		storageManager.setScriptID(script.getName());
		// TODO set meta data
		// storageManager.setMetadata(script.getMetaData());

		// TODO should be done if session is created
		if (!useUploadCache) {
			try {
				storageManager.createSessionDirectory();
			} catch (StorageManagerException e) {
				e.printStackTrace();
				return;
			}
		}
		if(!recDisplayValid){
			int recVersions=0;
			if(promptItem instanceof Recording){
			try {
				recVersions = storageManager.getRecordedVersions();
			} catch (StorageManagerException e) {
				e.printStackTrace();
//					return;
			}
		    // set to last recorded version
		    storageManager.setRecVersion(recVersions - 1);
			}
		    // itemPlayable = storageManager.isRecorded();
		    itemPlayable = (recVersions > 0);
		    speechRecorderUI.setPlaybackEnabled(itemPlayable);
		    setRecDisplay();
		    recDisplayValid=true;
		}
//		setIndexAction.setEnabled(true);
		
		if(promptItem instanceof Recording){
		    if (!Section.PromptPhase.IDLE.equals(sessionManager.currentPromptPhase())){
                speechRecorderUI.setShowPromptViewers(false);
            }
		    speechRecorderUI.setRecMonitorsStatus(StartStopSignal.State.IDLE);
            recStat.setStatus(RecStatus.IDLE);
        }else if(promptItem instanceof Nonrecording){
        	speechRecorderUI.setRecMonitorsStatus(StartStopSignal.State.OFF);
            recStat.setStatus(RecStatus.NON_RECORDING);
        }
	}
	
	private void startPrompt(){
		boolean promptStartEnabled=(!isAutoRecording() || getProgressPaused());
		if(promptItem instanceof Recording){
		    
		    if (Section.PromptPhase.IDLE.equals(sessionManager.currentPromptPhase())){
		        speechRecorderUI.setShowPromptViewers(true);
		        try {
					speechRecorderUI.startPromptAutoplay();
				} catch (PrompterException e) {
					e.printStackTrace();
					setIdle();
				}
		        speechRecorderUI.setPromptStartControlEnabled(promptStartEnabled);
		    }
		}else if(promptItem instanceof Nonrecording){
		    speechRecorderUI.setShowPromptViewers(true);
		    try {
				speechRecorderUI.startPromptAutoplay();
			} catch (PrompterException e) {
				e.printStackTrace();
				setIdle();
			}
		    speechRecorderUI.setPromptStartControlEnabled(promptStartEnabled);
		}

		
	}

	
	
//	public Section.Mode getCurrentMode() {
//		Section.Mode projectMode = Section.Mode.getByValue(project.getRecordingConfiguration().getMode());
//		Section.Mode sectionMode = recScriptManager.getCurrentRecSection().getMode();
//		if (sectionMode != null) {
//			// project mode is overwritten by section mode
//			return sectionMode;
//		}
//		return projectMode;
//	}

	public static void main(String[] args) {

		String projectFileURL = null;
		String[] params;
		String user = null;
		String password = "";
		OptionParser op = new OptionParser();
		op.addOption("u", "");
		op.addOption("p", "");
		op.addOption("s", "");
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
			if (options[i].getOptionName().equals("u")) {
				user = options[i].getParam();
			} else if (options[i].getOptionName().equals("p")) {
				password = options[i].getParam();
			} else if (options[i].getOptionName().equals("s")) {
				String sessionCookie = options[i].getParam();
				
				// Cookie handling: method 1:
				// disable cookie handler
				// Java web start version 6 receives cookies from requests which do not require a authentication
				// e.g. to download jar files
				// the cookie handler then holds a JSESSIONID which belongs to a new unauthenticated session.
				// If Tomcat receives the two session id's and seems to use only the first and rejects the request
				// with HTTP 401 Unauthorized
				// Tomcat bug ?
				// so I reset the cookie handler and set the given (authenticated) cookie for each URLConnection.
				
				// method 2: (used now)
				// implemented own cookie handler to avoid applying to each URLConnection.
			
				// Setting the cookie handler requires all permissions (signed jars) !!
				CookieHandler.setDefault(new SessionCookieHandler(sessionCookie));
				
//				// method 3:
//				// jar files download requires authentication as well
//				// add cookie to default cookie handler
//				SimpleCookie sc=new SimpleCookie(sessionCookie);
//				URI uri;
//				try {
//					uri = new URI(sc.getProperty("path"));
//					CookieHandler ch=CookieHandler.getDefault();
//					ch.put(uri, sc.getResponseHeaders());
//					
//				} catch (URISyntaxException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
		params = op.getParams();

		if (params.length != 1 && params.length != 0) {
			System.out
					.println("Usage\n\n\tSpeechRecorder [PROJECT_FILE_URL]\n\n");
			System.exit(-1);
		}
		if (params.length == 1) {
			projectFileURL = params[0];
		}
		
		// "Delegate" to AWT event thread for Swing thread safety

		final String fprojectFileURL = projectFileURL;
		final String fuser = user;
		final String fpassword = password;

		// for thread safety the constructor is called in the AWT event thread from the main method
        // if we really want to do configuring,plugin loading,etc in the background we need to
        // separate the jobs which call no Swing methods and could be run safety in the background by the main thread.
		Runnable doStart=new Runnable(){
		    public void run() {
		        try {
		            new SpeechRecorder(fprojectFileURL, fuser, fpassword);
		        } catch (Exception e) {
		            e.printStackTrace();
		            JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
		                    "ERROR", JOptionPane.ERROR_MESSAGE);
		            System.exit(-1);
		        }
		    }
		};
		SwingUtilities.invokeLater(doStart);
	}

	/**
	 * @return Returns the waitForCompleteUpload.
	 */
	public boolean isWaitForCompleteUpload() {
		return waitForCompleteUpload;
	}

	/**
	 * @param waitForCompleteUpload
	 *            The waitForCompleteUpload to set.
	 */
	public void setWaitForCompleteUpload(boolean waitForCompleteUpload) {
		this.waitForCompleteUpload = waitForCompleteUpload;
	}

	/**
	 * @return project context URL
	 */
	public URL getProjectContext() {
		return projectContext;
	}

	/**
	 * @param context the project context (directory) URL
	 */
	public void setProjectContext(URL context) {
		projectContext = context;
		if(speechRecorderUI!=null){
		    speechRecorderUI.setProjectContext(this.projectContext);
		}
	}

	public void importProject(File file) throws PluginLoadingException,
			AudioControllerException, IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			DOMConverterException, WorkspaceException, DOMCodecException, URISyntaxException, RecscriptManagerException, StorageManagerException{

		// first search project main directory
		ZipFile zipFile=new ZipFile(file);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		ZipEntry firstEntry = (ZipEntry) entries.nextElement();
		// some versions of speechrecorder (respectively the ZipPacker class ) wrote
		// no base directory entry (and in general no non-empty directory entries) 
		// so we have to check for the parent dir first to get the project name
		String firstFilename=firstEntry.getName();
		File firstFile=new File(firstFilename);
		File parentFile=firstFile;
		
		while (parentFile.getParentFile() != null) {
		    parentFile=parentFile.getParentFile();
        }
		
		String projectDirName = parentFile.getName();
		File projectDirFile = new File(projectDirName);
		String projectName = projectDirFile.getName();
//		while (projectDirFile.getParentFile() != null) {
//			projectDirName = projectDirFile.getParent();
//			projectDirFile = new File(projectDirName);
//		}
		File projectDir = new File(defWorkspaceDir, projectDirName);
		// found project directory
		if (projectDir.exists()) {
			speechRecorderUI.displayError(uiString.getString("MenuItemImport"),
					"Project " + projectDirName + " already exists !");
			zipFile.close();
			return;
		}
		zipFile.close();
		
		// Open again to check project file
		zipFile=new ZipFile(file);
		File projectCfgPrototypeFile=new File(projectDirFile,projectName+PROJECT_FILE_EXTENSION);
		entries = zipFile.entries();
		ZipEntry entry=null;
		boolean hasProjectCfgFile=false;
		while(entries.hasMoreElements()){
		    entry=entries.nextElement();
		    String entryFn=entry.getName();
		    File entryF=new File(entryFn);
		    if(projectCfgPrototypeFile.equals(entryF)){
		        hasProjectCfgFile=true;
		        break;
		    }
		}
		zipFile.close();
		
		if(!hasProjectCfgFile){
		    throw new IOException("Speechrecorder project file not found in ZIP file.\nThis does not look like a Speechrecorder export.");
		}
		
		UnzipWorker unzipWorker=new UnzipWorker();
		unzipWorker.setSourceZipFile(file);
		unzipWorker.setTrgDir(defWorkspaceDir);
	
		 JProgressDialogPanel progressDialog=new JProgressDialogPanel(unzipWorker,"Import project","Importing...");
        try {
            unzipWorker.open();
        } catch (WorkerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        unzipWorker.start();
        
        Object val=progressDialog.showDialog(speechRecorderUI);
       
        try {
            unzipWorker.close();
        } catch (WorkerException e) {
           
            e.printStackTrace();
        }
       
        // TODO what is best practice to ask a worker for final status
        State s=unzipWorker.getStatus();
        if(!State.DONE.equals(s)){
        	if(State.CANCELLED.equals(s) || val.equals(JProgressDialogPanel.CANCEL_OPTION)){
        		JOptionPane.showMessageDialog(speechRecorderUI, "Project import canceled.");
        	}else{
        		LocalizableMessage lm=unzipWorker.getProgressStatus().getMessage();
        		JOptionPane.showMessageDialog(speechRecorderUI, lm.localize(),"Project import error.",JOptionPane.ERROR_MESSAGE);
        	}
        }else{
        	workspaceProjects=workspaceManager.scanWorkspace();
        	speechRecorderUI.setWorkspaceProjects(workspaceProjects);
        	openProject(projectName);
        }

	}
	
	private Bundle buildBaseBundle(File[] recFiles) throws IOException, UnsupportedAudioFileException{
	   
		// build Bundle
		Bundle bundle=new Bundle();
		bundle.setSession(annotationSession);
		String targetRootFn=storageManager.getNewRootFileName();
		bundle.setName(targetRootFn);


		if(recFiles!=null && recFiles.length>0){
			File masterFile=recFiles[0];
			bundle.setAnnotates(masterFile.getName());
			List<String> sigPathes=new ArrayList<String>();
			for(File rf:recFiles){
				sigPathes.add(rf.getAbsolutePath());
			}
			bundle.setSignalpaths(sigPathes);
			try {
				ConvenienceFileAudioSource cfas=new ConvenienceFileAudioSource(masterFile);
				AudioFormat af=cfas.getFormat();
				long fl=cfas.getFrameLength();
				bundle.setSampleRate(af.getSampleRate());
				bundle.setFrameLength(fl);
			} catch (AudioSourceException e) {

				e.printStackTrace();
				// OK could not retrieve the sample rate and frame length
			}

		}

		return bundle;

	}
	
	private Bundle prepareAutoAnnotation() throws IOException, UnsupportedAudioFileException{
	    if(promptAutoAnnotator!=null){
	        promptAutoAnnotator.setPromptText(null);
	    }
	    if(templateAutoAnnotator!=null){
	        templateAutoAnnotator.setTemplateText(null);
	    }
	    File[] recFiles;
	    Bundle bundle=null;
       
	    recFiles=storageManager.getCurrentItemRecordingFiles();
	    bundle=buildBaseBundle(recFiles);
       
        String prDescr=promptItem.getDescription();

        if(promptAutoAnnotator!=null){
            promptAutoAnnotator.setPromptText(prDescr);
        }

        List<Mediaitem> mis=promptItem.getMediaitems();
       // List<Item> tpIts=new ArrayList<Item>();
        
        for(Mediaitem mi:mis){
           if(mi.getAnnotationTemplate()){
               if(templateAutoAnnotator!=null){
                   templateAutoAnnotator.setTemplateText(mi.getText());
               }
               break;
           }
        }
       
        return bundle;
	    
	}
	
	private void itemFinished(){
		RecStatus st=RecStatus.getInstance();
		int status = st.getStatus();
		if (status == RecStatus.PRERECWAITING
				|| status == RecStatus.RECORDING
				|| status == RecStatus.POSTRECWAITING) {
			st.setStatus(RecStatus.RECORDED);
//			recScriptManager.incrementRecCounter(recScriptManager
//					.getRecIndex());
			// URL[] recUrls = storageManager.getAudioFiles();
			if (useUploadCache) {
				try {
					storageManager.upload();

				} catch (StorageManagerException e) {
					speechRecorderUI
							.displayError("Storage error",
									"Storage error: "
											+ e.getLocalizedMessage());
					logger.severe(e.getMessage());
					// repeat = false;
				}
			}
//			speechRecorderUI.closePrompt();
			if(CaptureScope.SESSION.equals(captureScope) || !audioController.isCaptureOpen()){
			    if(speechRecorderUI.isPromptClosed()){

			        if(DEBUG)System.out.println("Capture finished event. Continue session...");
			       
			        if(repeatRequest==null){
			            // check if write any annotation files
			            if(bundleAnnotationPersistorList!=null && bundleAnnotationPersistorList.size()>0){
			                // build a new annotation bundle
			                Bundle bundle=null;
                            try {
                                bundle = prepareAutoAnnotation();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } catch (UnsupportedAudioFileException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
			                if(annotatingEnabled){
			                    // start user annotation
			                    startAnnotation();
			                }
			                // already persist this bundle
			                try {
                                persistBundle(bundle);
                                // start auto annotators (asynchron)
                                startAutoAnnotation(bundle);
                            } catch (StorageManagerException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (EncodeException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
			               
			                
			            }
					}
					  
					continueSession();
					
			    }else{
			        if(DEBUG)System.out.println("Capture finished. Wait for prompt viewer....");
			        speechRecorderUI.closePrompt();
			    }
			}else{
			    System.err.println("Capture still open !");
			}
		}
	}
	
	private List<File> currentAnnotationFiles() throws StorageManagerException{
	    List<File> annoFileList=new ArrayList<File>();
	    File sessDir=StorageManager.fileURLToFile(storageManager.getSessionURL());
	    String rootFn=storageManager.getRootFileName();
        if(sessDir!=null){
            for(BundleAnnotationPersistor bap:bundleAnnotationPersistorList){
                if(bap instanceof BundleAnnotationFilePersistor){
                    BundleAnnotationFilePersistor bafp=(BundleAnnotationFilePersistor)bap;
                    File annoFile=new File(sessDir,rootFn+bafp.getPreferredFilenameSuffix()+"."+bafp.getPreferredFileExtension());
                    annoFileList.add(annoFile);
                }else{
                    // currently no other protocols than file
                }
            }
        }
        return annoFileList;
	}
	
	private void persistBundle(Bundle bundle) throws StorageManagerException, IOException, EncodeException{
		// persist bundle ( not session scoped, annotation worker has scope project)
		Session sessOfBundle=bundle.getSession();
		if(sessOfBundle!=null){

			String rootFn=bundle.getName();
			//        TemplateTextFilePersistor baw=new TemplateTextFilePersistor();
			File storageDir=StorageManager.fileURLToFile(storageManager.getStorageURL());
			String sessNm=sessOfBundle.getName();
			if(sessNm!=null && ! "".equals(sessNm)){
				File sessDir=new File(storageDir,sessNm);

				if(sessDir!=null && sessDir.isDirectory()){
					for(BundleAnnotationPersistor bap:bundleAnnotationPersistorList){
						if(bap instanceof BundleAnnotationFilePersistor){
							BundleAnnotationFilePersistor bafp=(BundleAnnotationFilePersistor)bap;
							File annoFile=new File(sessDir,rootFn+bafp.getPreferredFilenameSuffix()+"."+bafp.getPreferredFileExtension());
							bafp.setFile(annoFile);
							try{
								bap.write(bundle);
							}catch(EncodeException ee){
								ee.printStackTrace();
								// Continue with other file writers
								// TextGrid cannot handle empty annotations (no tiers)
								// TextGrid writer throws exception here
								// we have to catch it here to continue with other writers
								
							}
							if(DEBUG)System.out.println("Wrote bundle "+bundle.getName()+" to "+annoFile);
						}else{
							// currently no other protocols than file

							//	                bap.write(bundle);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioController2.AudioController2Listener#update(ipsk.audio.AudioController2.AudioControllerEvent)
	 */
	public void update(AudioControllerEvent ace) {

		if(DEBUG)System.out.println("Audio controller event: "+ace);
		if (ace instanceof PlayerEvent) {
			if (ace instanceof PlayerStopEvent) {
				if (ace instanceof PlayerPauseEvent) {
					recStat.setStatus(RecStatus.PLAYPAUSE);
				} else {
					try {
						audioController.closePlayback();
					} catch (AudioControllerException e) {
						speechRecorderUI.displayError(
								"Audio controller close playback error", e
										.getLocalizedMessage());

						e.printStackTrace();
					}
				}
			} else if (ace instanceof PlayerStartEvent) {
				setIndexAction.setEnabled(false);
				speechRecorderUI
						.setLevelMeterMode(SpeechRecorderUI.LEVEL_METER_PLAYBACK);
				recStat.setStatus(RecStatus.PLAY);
			}else if(ace instanceof PlayerCloseEvent){
			    speechRecorderUI.updateView();
			    setIdle();
			    startPrompt();
			}
		} else if (ace instanceof CaptureEvent) {
			// String status = ce.getStatus();
			if (ace instanceof CaptureStartCaptureEvent) {
			    if(DEBUG)System.out.println("Capture start capture event.");
			}else if (ace instanceof CaptureStartRecordEvent) {
			    if(DEBUG)System.out.println("Capture start record event. start timers");
				preRecTimer.start();
				if(maxRecTimer!=null)maxRecTimer.start();
			} else if (ace instanceof CaptureRecordingFileTransitEvent) {
			    if(DEBUG)System.out.println("Capture recording file transit");
			    sessionManager.incrementRecCounter(sessionManager
                        .getRecIndex()-1);
			    preRecTimer.start();
                if(maxRecTimer!=null)maxRecTimer.start();
                
//                preRecTimer.start();
//                if(maxRecTimer!=null)maxRecTimer.start();
                
                // TODO
                // continue session here
                
//                if(speechRecorderUI.isPromptClosed()){
//
//                    if(DEBUG)System.out.println("Capture file transit event. Continue session...");
//                    if(!repeatRecording && annotatingEnabled){
//                        startAnnotation();
//                    }else{
//                        
//                        continueSession(repeatRecording);
//                    }
//                }else{
//                    if(DEBUG)System.out.println("Capture file transit event, wait for prompt viewer....");
//                    speechRecorderUI.closePrompt();
//                }
			}  else if (ace instanceof CaptureRecordedEvent) {
			    //				repeatRecording=false;
			    sessionManager.incrementRecCounter(sessionManager
			            .getRecIndex());
			    //speechRecorderUI.stopPlayPrompt();
			    if(DEBUG)System.out.println("Capture recorded event.");
			    if(promptItem instanceof Recording){
			        // check recorded file
			        long recordedFrameLength=audioController.getCaptureFramePosition();
			        if(DEBUG)System.out.println("Recorded frames: "+recordedFrameLength);
			        float sampleRate=audioController.getAudioFileFormat().getFormat().getSampleRate();
			        double recordedMs=recordedFrameLength*1000.0/sampleRate;
			        Recording r=(Recording)promptItem;
			        int minRecordLenMs=minRecLengthMs(r);
			        // check if audio file has minimum length 
			        // since we are working with timers the file length may not be accurate
			        // therefore set a tolerance of 0.75
			        // this check was added to workaround problems with 
			        // Steinberg/Yamaha USB drivers 1.8.6,1.9.2,... (?)
			        // which returned in some rare cases (0.3%) no audio data

			        // Update: Startup time of capture line on Mac OS X needs sometimes about 1500 ms
			        // to activate, therefore we add 1000ms for Mac OS X here
			        // TODO Use line activation event ! (Planned for 2.14.x)
			        int lineActivateTolerance=MIN_EXPECTED_REC_LEN_LNE_ACTIVATION_MS_DEFAULT;
			        if(SystemHelper.getInstance().isMacOSX()){
			            lineActivateTolerance+=1000;
			        }
			        if(DEBUG){
			            System.out.println("Recorded/minExpected: "+recordedMs+"/"+minRecordLenMs+" ms");
			            System.out.println("Tolerance: Factor: "+MIN_EXPECTED_REC_LEN_TOLERANCE+", activation:"+lineActivateTolerance+" ms");
			        }


			        if((recordedMs+lineActivateTolerance)<(minRecordLenMs*MIN_EXPECTED_REC_LEN_TOLERANCE)){
			            // TEST !!!!
			            //if(recordedMs<2000){
			            String msg=("Recording length "+recordedMs+" ms is shorter than "+MIN_EXPECTED_REC_LEN_TOLERANCE+" of minimum expected length of "+minRecordLenMs+" ms");
			            System.err.println(msg);
			            repeatRequest=new RepeatRequest("Audio quality check failed", msg);
			            //                            speechRecorderUI.displayError("Audio file too short !",msg+"\nPlease repeat this item!");
			        }
			    }
//			    if(voiceDetector!=null){
//                    voiceDetector.stop();
//                }
                if(silenceDetector!=null){
                    silenceDetector.stop();
                }
			    if(CaptureScope.SESSION.equals(captureScope)){
			        itemFinished();
			    }else{
			        try {

			            if(DEBUG)System.out.println("Closing capture.");
//			            audioController.removeCaptureFloatAudioOutputStream(voiceDetector);
//			            silenceDetector.reset();
			            audioController.closeCapture();
			        } catch (AudioControllerException e1) {
			            speechRecorderUI.displayError(
			                    "Audio controller close capture error", e1
			                    .getLocalizedMessage());

			            e1.printStackTrace();
			        }
			    }

			} else if (ace instanceof CaptureCloseEvent) {
			    if(voiceDetector!=null){
//			        voiceDetector.stop();
			        audioController.removeCaptureFloatAudioOutputStream(voiceDetector);
			    }
			    if(silenceDetector!=null){
			        silenceDetector.stop();
			    }
			    itemFinished();
			} else if (ace instanceof CaptureErrorEvent) {
			    CaptureErrorEvent cErrEv = (CaptureErrorEvent) ace;
			    Exception cause = cErrEv.getCause();
			    String errMsg="Unknown capture error";
			    String locErrMsg="Unknown capture error";
			    if(cause!=null){
			        errMsg=cause.getMessage();
			        locErrMsg=cause.getLocalizedMessage();
			    }
			    if(preRecTimer!=null){
			    	preRecTimer.stop();
			    }
			    if(maxRecTimer!=null){
			    	maxRecTimer.stop();
			    }
			    
			    if(voiceDetector!=null){
//                    voiceDetector.stop();
                    audioController.removeCaptureFloatAudioOutputStream(voiceDetector);
                }
                if(silenceDetector!=null){
                    silenceDetector.stop();
                }
			    logger.severe(errMsg);
			    //System.err.println(RecStatus.getStatusName(recStat.getStatus()));
			    if (cause instanceof BufferOverrunException && RecStatus.IDLE!=recStat.getStatus()) {
			        repeatRequest=new RepeatRequest();
			    }
			    speechRecorderUI.displayError("Audio controller error", locErrMsg);
			    try {
			        if(DEBUG)System.out.println("Closing capture.");
			        audioController.closeCapture();
			    } catch (AudioControllerException e) {
			        speechRecorderUI.displayError(
			                "Audio controller close capture error", e
			                .getLocalizedMessage());
			    }

			    //				if (cause instanceof BufferOverrunException) {
			    //					continueSession(true);
			    //				}

			}
		}

	}

	/**
     * Start auto annotation.
     * Method puts request(s) to the annotation worker.
     * The worker is running background therefore Speechrecorder does not change its state.
	 * @param bundle 
     *  
     */
    private void startAutoAnnotation(Bundle bundle) {
        if(autoAnnotationWorker!=null){
        File[] recFiles=null;
        
        try {
            recFiles = storageManager.getRecordingFiles();
            URL sessURL=storageManager.getSessionURL();
            File targetDir=StorageManager.fileURLToFile(sessURL);
           
           
            PromptItem pi=sessionManager.getCurrentPromptItem();
            List<Mediaitem> mis=pi.getMediaitems();
            
            String orthoGraphy=null;
            for(Mediaitem mi:mis){
                if(mi.getAnnotationTemplate()){
                    java.util.Locale lLoc;
                    String langCode=mi.getLanguageISO639code();
                    if(langCode!=null){
                        lLoc= new java.util.Locale(langCode);
                    }else{
                        // default current locale
                        lLoc=java.util.Locale.getDefault();
                    }
                    lLoc.getISO3Language();
                    bundle.setLocale(lLoc);
//                    System.out.println("Lang code: "+langCode);
                    orthoGraphy=mi.getText();
                    if(orthoGraphy!=null){
                        if(orthoGraphy != null  && recFiles.length==1){
                           
//                            AutoAnnotator.AnnotationRequest ar=new AutoAnnotator.AnnotationRequest(recFiles[0], orthoGraphy,lLoc,targetDir,targetRootFn);
                           
                        }
                        break;
                    }
                }
            }
            AutoAnnotator.AnnotationRequest ar=new AutoAnnotator.AnnotationRequest(bundle);
            autoAnnotationWorker.request(ar);
        } catch (StorageManagerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        }
    }



    /**
	 * @return Returns the audioFileFormat.
	 */
	public AudioFileFormat getAudioFileFormat() {
		return audioFileFormat;
	}

	/**
	 * @param audioFileFormat
	 *            The audioFileFormat to set.
	 */
	public void setAudioFileFormat(AudioFileFormat audioFileFormat) {
		this.audioFileFormat = audioFileFormat;
	}

	public RecTransporterActions getRecTransporterActions() {

		return recTransporterActions;
	}

	public void setRecIndex(int skipTo) {
			sessionManager.setRecIndex(skipTo);
	}

	public void importResource(File f,String relpath) throws IOException, URISyntaxException{
	   
	  URL projResURL = null;
      projResURL = URLContext.getContextURL(getProjectContext(), relpath);
      String projResPath=projResURL.toURI().getPath();
      File proJResFile=new File(projResPath);
      StreamCopy.copy(f, proJResFile);
      
	}
	
	public void exportProject(File zipFile) throws IOException, URISyntaxException {

		// first search project main dir

		URL projectURL = getProjectContext();

		File projDir = new File(projectURL.toURI().getPath());
	
		FileOutputStream zipStream = new FileOutputStream(zipFile);
//		ZipPacker zipPacker = new ZipPacker(zipStream);
//		zipPacker.packDirRecursive(projDir);
//		zipPacker.close();
		
		ZipPackerWorker zipPackerWorker=new ZipPackerWorker();
		zipPackerWorker.setSrcDir(projDir);
		zipPackerWorker.setPackRecusive(true);
        zipPackerWorker.setOutputStream(zipStream);
        JProgressDialogPanel progressDialog=new JProgressDialogPanel(zipPackerWorker,"Export project","Exporting...");
        try {
            zipPackerWorker.open();
        } catch (WorkerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        zipPackerWorker.start();
        
        Object val=progressDialog.showDialog(speechRecorderUI);
       
        try {
            zipPackerWorker.close();
        } catch (WorkerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ProgressStatus finalStatus=zipPackerWorker.getProgressStatus();
        if(!finalStatus.isDone()){
            // delete (partial) file
            zipFile.delete();
        }
        if(val.equals(JOptionPane.OK_OPTION)){
            if(finalStatus.isDone()){
                // success
               Object[] options=new Object[]{"OK","Open containing folder"};
                int selOpt=JOptionPane.showOptionDialog(speechRecorderUI, "Successful export to ZIP file:\n"+zipFile, "Project export",JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,options,options[0]);
                if(selOpt==1){
                    if(Desktop.isDesktopSupported()){
                        Desktop dt=Desktop.getDesktop();
                        File containingFolder=zipFile.getParentFile();
                        dt.open(containingFolder);
                    }
                   
                }
            }else if(finalStatus.isError()){
                JOptionPane.showMessageDialog(speechRecorderUI, "Project export failed:"+finalStatus.getMessage().localize());
            }
        }else if(val.equals(JOptionPane.CANCEL_OPTION)){
            JOptionPane.showMessageDialog(speechRecorderUI, "Project export canceled.");
        }
        
	}
	
	public void annotate(Object annotatedObject, String annotationName,String propertyName,
			Object annotation) {
		if(DEBUG)System.out.println("Annotation: "+propertyName+" "+annotation);
		FileWriter annoWriter=null;
		try {
			File annotationFile=storageManager.getAnnoationFile();
			//FileOutputStream annoFos=new FileOutputStream(annotationFile);
			annoWriter=new FileWriter(annotationFile);
			annoWriter.write("Annotation: "+propertyName+" "+annotation);
			annoWriter.close();
			if(useUploadCache)storageManager.uploadAnnotation();
		} catch (Exception e) {
			e.printStackTrace();
			if(annoWriter!=null){
				try {
					annoWriter.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
					speechRecorderUI.displayError("Annotation storage error", ioe);
				}
			}
			speechRecorderUI.displayError("Annotation storage error", e);
			
		}
		
	
//		continueSession(false);
		continueSession();
	}
	
	public void resetItemcodeGenerator(){
	    if(itemcodeGenerator!=null){
	    ObservableList<String> genItemcodeList=itemcodeGenerator.getItemcodesList();
	    if(genItemcodeList==null){
	        genItemcodeList=new ObservableArrayList<String>();
	        itemcodeGenerator.setItemcodesList(genItemcodeList);
	    }else{
	        genItemcodeList.clear();
	    }
	     Script s=RecScriptManager.getInstance().getScript();
	     if(s!=null){
	         List<String> itemcodesOfScript=s.itemCodesList();
	         genItemcodeList.addAll(itemcodesOfScript);
	     }
	    }
	}

	public boolean isScriptSaved() {
		return recScriptManager.isScriptSaved();
	}

	public void setScriptSaved(boolean scriptSaved){
	    recScriptManager.setScriptSaved(scriptSaved);
	}

	public void update(RecscriptManagerEvent e){
	    if(e instanceof RecScriptStoreStatusChanged){
//	        setScriptSaved(recScriptManager.isScriptSaved());
	        speechRecorderUI.updateSaveEnable();
	    }else if(e instanceof RecScriptChangedEvent){
//	        promptPlayed=false;
	    	if(recScriptManager.getScript()!=null && !(recStat.getStatus()==RecStatus.INIT || recStat.getStatus()==RecStatus.CLOSE)){
	            try {
					applyItem();
				} catch (PromptPresenterException e1) {
					setPromptErrorState();
					return;
				}
	    		setRecDisplay();
	            setIdle();
	            startPrompt();
	        }
	    }
	}
	
	public void update(SessionManagerEvent e){
	    if(e instanceof SessionPositionChangedEvent){
//	        recStat.setStatus(RecStatus.NAVIGATE);
	        SessionPositionChangedEvent rspce=(SessionPositionChangedEvent)e;
	        Integer newPos=rspce.getPosition();
	        if(newPos==null){
	            init();
	        }else{
	    		try {
	    			applyItem();
	    		} catch (PromptPresenterException e1) {
	    			setPromptErrorState();
	    			return;
	    		}
	    		setIdle();
	    		startPrompt();
	    	}
	    }
	}

	public void update(PromptViewerEvent promptViewerEvent) {
	    if (promptViewerEvent instanceof PromptViewerStartedEvent){
	        int currStat=recStat.getStatus();
	        if(DEBUG)System.out.println("Curr status: "+RecStatus.getStatusName(currStat));
	        if(currStat==RecStatus.NON_RECORDING || currStat==RecStatus.IDLE){
	            setIndexAction.setEnabled(false);
	            setEditingEnabled(false);
	   
            recStat.setStatus(RecStatus.PLAY_PROMPT_PREVIEW);
	        }
	           
        }else if (promptViewerEvent instanceof PromptViewerStoppedEvent){
	        
           
        }else if (promptViewerEvent instanceof PromptViewerPresenterClosedEvent){
            int status=recStat.getStatus();
            if(DEBUG)System.out.println("Prompt viewer closed event.");
            if(status == RecStatus.PLAY_PROMPT){
//                promptPlayed=true;
                ipsk.db.speech.PromptItem promptItem = sessionManager
                .getCurrentPromptItem();

                if(DEBUG)System.out.println("Prompt played.");
                if(promptItem instanceof Recording){
                    if(DEBUG)System.out.println("Starting beep ...");
                    try {
                        startBeep();
                    } catch (SpeechRecorderException e) {
                        speechRecorderUI.displayError("Beep playback error",e);
                        setIdle();
                    }
                }else if(promptItem instanceof Nonrecording){
                    startNonRecording();
                }else{
                    setIdle();
                    if(isAutoRecording()){
//                        continueSession(false);
                        continueSession();
                    }
                }
            }else if(status == RecStatus.PLAY_PROMPT_PREVIEW){
//                promptPlayed=true;
          
                setIdle();
                
            }else if (status == RecStatus.PRERECWAITING
            		|| status == RecStatus.RECORDING
            		|| status == RecStatus.POSTRECWAITING) {

            	if (Section.PromptPhase.RECORDING.equals(sessionManager.currentPromptPhase())
            			&& sessionManager.currentPromptBlocking()){
            		speechRecorderUI.setRecMonitorsStatus(StartStopSignal.State.RECORDING);
            	}
            	if(DEBUG)System.out.println("Prompt viewer close. wait for capture to finish...");

            }else if(status==RecStatus.RECORDED){
            	if(DEBUG)System.out.println("Prompt viewer closed. Continue.");
            	if(speechRecorderUI.isPromptClosed()){
            		if(DEBUG)System.out.println("Continue triggered by Prompt viewer close.");
            		//                    continueSession(repeatRecording);
            		continueSession();
            	}else{
            		System.err.println("Prompter not closed. (Internal error)");
            	}
            }
        }
    }



    public void update(PlayerEvent playerEvent) {
        Player p=(Player)playerEvent.getSource();
        if(playerEvent instanceof PlayerStopEvent){
            if(DEBUG)System.out.println("Beep-Player stop event");   
            try {
                p.close();
                
            } catch (PlayerException e) {
                e.printStackTrace();
                try {
                    stopRecording();
                } catch (AudioControllerException e1) {
                   
                    e1.printStackTrace();
                }
                speechRecorderUI.displayError("Beep playback error", e);
               
            }
        }else if(playerEvent instanceof PlayerCloseEvent){
            if(DEBUG)System.out.println("Beep-Player closed event");   
           
            startPreRecWaiting();
        }
    }



    public AnnotatedAudioClip getAudioClip() {
        return audioClip;
    }



    public void setAudioClip(AnnotatedAudioClip audioClip) {
        this.audioClip = audioClip;
    }



	/* (non-Javadoc)
	 * @see ipsk.util.apps.UpdateManagerListener#update(ipsk.util.apps.UpdateManagerEvent)
	 */
	@Override
	public void update(UpdateManagerEvent event) {
		if(event instanceof UpdateAvailableEvent){
			UpdateAvailableEvent uae=(UpdateAvailableEvent)event;
			Change.Priority priority=uae.getPriority();
			if(Change.Priority.STRONGLY_RECOMMENDED.equals(priority)){
//			
//			 UpdateDialogUI ud=new UpdateDialogUI(updateManager);
////           ud.showDialog((JFrame)null);
//			 
//			 JOptionPane.showMessageDialog(null, ud, "Update available", JOptionPane.INFORMATION_MESSAGE);
			Runnable checkUpdatesRunnable=new Runnable() {
				
				@Override
				public void run() {
					 speechRecorderUI.doCheckUpdates();
					
				}
			};
			SwingUtilities.invokeLater(checkUpdatesRunnable);
			
			}
		}
	}



	/**
	 * 
	 */
	public void advanceToNextRecording() {
		getSessionManager().advanceToNextRecording();
	}



	/**
	 * 
	 */
	public void decrementIndex() {
		getSessionManager().decrementIndex();
	}



	/**
	 * 
	 */
	public void incrementIndex() {
		getSessionManager().incrementIndex();
	}



    public ItemcodeGenerator getItemcodeGenerator() {
        return itemcodeGenerator;
    }



	/**
	 * @return
	 * @throws IOException 
	 */
	public List<AutoAnnotationServiceDescriptor> getAutoAnnotatorServiceDescriptors() throws IOException {
		return autoAnnotatorPluginManager.getAutoAnnotatorServiceDescriptors();
	}



	/* (non-Javadoc)
	 * @see ipsk.awt.ProgressListener#update(ipsk.awt.event.ProgressEvent)
	 */
	@Override
	public void update(ProgressEvent progressEvent) {
		
		Object src=progressEvent.getSource();
		if(src==autoAnnotationWorker){
			// update from auto annotation worker
			if(progressEvent instanceof AutoAnnotationWorker.BundleAnnotatedEvent){
				AutoAnnotationWorker.BundleAnnotatedEvent bae=(AutoAnnotationWorker.BundleAnnotatedEvent)progressEvent;
				Bundle bundle=bae.getAnnotatedBundle();
				try {
                    persistBundle(bundle);
                    // TODO how to handle (asynchron) errors ? Log file ?
                } catch (StorageManagerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (EncodeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
				String currViewedBundle=storageManager.getRootFileName();
				
				if(currViewedBundle.equals(bundle.getName())){
				    audioClip.setBundle(bundle);
				}
			}
		}
	}



    public WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }
    
    public List<AudioClip> getSessionClipList(){
    	Script scr=recScriptManager.getScript();
    	if(scr==null){
    		return(null);
    	}
    	List<AudioClip> audioClipList=new ArrayList<AudioClip>();
    	List<String> itemcodes=scr.itemCodesList();
    	try {
    		for(String itemCode:itemcodes){
    			File af=storageManager.recentRecordingFile(itemCode);
    			if (af!=null && af.exists()){
    				FileAudioSource fas=new FileAudioSource(af);
    				AudioClip ac=new AudioClip(fas);
    				audioClipList.add(ac);

    			}
    		}
    	} catch (StorageManagerException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	return audioClipList;
    }



    /* (non-Javadoc)
     * @see ipsk.audio.dsp.speech.SpeechFinalSilenceDetectorListener#update(ipsk.audio.dsp.speech.SpeechFinalSilenceDetectorEvent)
     */
    @Override
    public void update(SpeechFinalSilenceDetectorEvent event) {
        if(event.isFinalSilenceDetected()){
            startPostRecordingPhase();
        }
    }



    /**
     * @return
     */
    public List<BundleAnnotationPersistorServiceDescriptor> getBundleAnnotationPersistorServiceDescriptors() {
        return availableBundleAnnotationServiceDescriptors;
    }

}
