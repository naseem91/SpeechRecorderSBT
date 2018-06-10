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

package ips.annot.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import ips.annot.model.AnnotatedAudioClip;
import ips.annot.model.db.Bundle;
import ips.annot.textgrid.TextGridFileParser;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.URLAudioSource;
import ipsk.audio.VectorBufferAudioSource;
import ipsk.audio.actions.LoopAction;
import ipsk.audio.actions.PauseAction;
import ipsk.audio.actions.StartPlaybackAction;
import ipsk.audio.actions.StopAction;
import ipsk.audio.arr.Selection;
import ipsk.audio.arr.clip.AudioClipListener;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.audio.arr.clip.ui.AudioClipScrollPane;
import ipsk.audio.arr.clip.ui.AudioClipUIContainer;
import ipsk.audio.arr.clip.ui.AudioSignalUI;
import ipsk.audio.arr.clip.ui.AudioTimeScaleUI;
import ipsk.audio.arr.clip.ui.FourierUI;
import ipsk.audio.arr.clip.ui.FragmentActionBarUI;
import ipsk.audio.bean.AudioPlayerBean;
import ipsk.audio.bean.AudioPlayerBean.Status;
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
import ipsk.audio.ui.TransportUI;
import ipsk.awt.ProgressListener;
import ipsk.awt.PropertyChangeAWTEventTransferAgent;
import ipsk.awt.WorkerException;
import ipsk.awt.event.ProgressErrorEvent;
import ipsk.awt.event.ProgressEvent;
import ipsk.io.VectorBuffer;
import ipsk.io.VectorBufferedInputStream;
import ipsk.io.VectorBufferedOutputStream;
import ipsk.net.URLContentLoader;
import ipsk.net.http.ContentType;
import ipsk.swing.JPopupMenuListener;
import ipsk.swing.JProgressDialogPanel;
import ipsk.swing.action.tree.ActionFolder;
import ipsk.swing.action.tree.ActionGroup;
import ipsk.swing.action.tree.ActionTreeRoot;
import ipsk.swing.action.tree.JMenuBuilder;
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;




/**
 * Audio player bean with speech annotations
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class AnnotatedAudioPlayerBean extends JPanel implements AudioClipListener, PlayerListener, ActionListener, ProgressListener {

   
    public final static boolean DEBUG = false;
    private boolean debug=false;
    private static final long serialVersionUID = 1L;
    private static final Float PREFERRED_LINE_BUFFER_SIZE_MILLIS = (float) 1000;
    public final static int DEF_UPDATE_INTERVALL_MS=200;
    private AnnotatedAudioClip audioClip;
    protected URL source;
    private URL annotationURL;
    private URLContentLoader audioLoader;
    private URLContentLoader annotationLoader;
    private VectorBufferedOutputStream annotionFileContent;
    private Status status=null;
    protected PropertyChangeAWTEventTransferAgent pChTa=new PropertyChangeAWTEventTransferAgent();
    private VectorBufferedOutputStream vbOut;
    protected AudioSource audioSource;
//    private List<AnnotationAudioClipUI> annotationAudioClipUIs=new ArrayList<AnnotationAudioClipUI>();
    protected AnnotationAudioClipUI annotationAudioClipUI;

    protected AudioClipUIContainer uiContainer;
    private JProgressDialogPanel progressPanel;
    private AudioClipScrollPane scrollPane;
    private String message;
    private Player player;
    protected AudioSource playbackSource;
    private StartPlaybackAction startAction;

    private StopAction stopAction;

    private PauseAction pauseAction;
    private LoopAction loopAction;
    private JPanel playerPanel;
    private Timer updateTimer;
    private Selection selection;
    private FourierUI sonagram;
    protected AudioSignalUI signalUI = null;
    private FragmentActionBarUI fragmentActionBar;
    private AudioTimeScaleUI timeScale;
    private boolean startPlayOnSelect;
    private Mixer device;
    private boolean autoPlayOnLoad=false;
//    private boolean showDSPInfo=false;
//    private boolean showSonagram=true;
//    private boolean showFragmentActionBar=true;
//    private boolean showTimeScale=true;
    
    public final static String VERSION = AudioPlayerBean.class.getPackage().getImplementationVersion();
    private volatile Bundle bundle;
    private volatile InputStreamReader annotationreader;
    
    private Charset textGridCharset=null;
    
    public AnnotatedAudioPlayerBean(){
        this(new AnnotatedAudioClip());
    }

    /**
     * Constructor.
     * 
     */
    public AnnotatedAudioPlayerBean(AnnotatedAudioClip audioClip)  {
        super(new BorderLayout());
        this.audioClip=audioClip;
        if(this.audioClip==null){
            this.audioClip = new AnnotatedAudioClip();
        }
        this.audioClip.addAudioSampleListener(this);
       
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
            if(debug){
                System.out.println("Could not get a direct audio device !");
                e.printStackTrace();
            }
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
        
//      channelSelectAllButton=new JRadioButton("All");
//      channelSelectBox=new JComboBox(new Object[]{"All"});
        
        // update of play cursor in signal display
        updateTimer = new Timer(DEF_UPDATE_INTERVALL_MS, this);
    //  open = true;
        signalUI = new AudioSignalUI();
        
        uiContainer = new AudioClipUIContainer();
        uiContainer.add(signalUI);

            sonagram=new FourierUI();
            sonagram.setUseThread(true);
            uiContainer.add(sonagram);
            
            fragmentActionBar=new FragmentActionBarUI();
            fragmentActionBar.setStartPlaybackAction(startAction);
            uiContainer.add(fragmentActionBar);
            annotationAudioClipUI=new AnnotationAudioClipUI(audioClip);
            uiContainer.add(annotationAudioClipUI);
            timeScale = new AudioTimeScaleUI();
            uiContainer.add(timeScale);
        
        scrollPane = new AudioClipScrollPane(uiContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        uiContainer.setAudioClip(audioClip);
        createPopupMenu();
        String m="Audio player initialized.";
        if(debug)System.out.println(m);
        setMessage(m);
        //status=Status.EXISTING;
        if (DEBUG)
            System.out.println(getClass().getName() + " version " + VERSION
                    + " initialized.");
        status=Status.INITIALIZED;
    }

    public URL getAnnotationURL() {
        return annotationURL;
    }

    
    private synchronized void createAnnotation(){
       
        AudioSource as=audioClip.getAudioSource();
        if(as==null){
            return;
        }
        float sampleRate=AudioSystem.NOT_SPECIFIED;
        try {
            sampleRate = audioClip.getFormat().getSampleRate();
        } catch (AudioSourceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TextGridFileParser tgfp=new TextGridFileParser(sampleRate);
       
        try {
            bundle=tgfp.parse(annotationreader);
            if(debug){
                System.out.println("Parsed TextGrid annotation:\n"+bundle);
                
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        System.out.println(annotation);
        audioClip.setBundle(bundle);
        //((AnnotatedAudioClip)audioClip).setAnnotation(annotation); 
//        Set<Tier> annoTiers=annotation.getTiers();
//        for(Tier annotationTier:annoTiers){
//            AnnotationAudioClipUI annoClipUI = new AnnotationAudioClipUI();
//            annoClipUI.setAnnotationTier(annotationTier);
////            addAudioClipUI(annoClipUI);
//            uiContainer.add(annoClipUI);
//            annotationAudioClipUIs.add(annoClipUI);
//        }
        //makeReady();
    }
//    public void update(ProgressEvent progressEvent) {
//        Object src=progressEvent.getSource();
//        if(src==annotationLoader){
//            if(progressEvent.getProgressStatus().isDone()){
//                createAnnotation();
//            }
//        }else{
//            super.update(progressEvent);
//        }
//    }
    
    /* (non-Javadoc)
     * @see ipsk.awt.ProgressListener#update(ipsk.awt.event.ProgressEvent)
     */
    public void update(ProgressEvent progressEvent) {
//      if(progressEvent.getProgressStatus().isFinished()){
//          initAudio();
//      }
        Object evSrc=progressEvent.getSource();
        if(evSrc==audioLoader){
        ProgressStatus status=progressEvent.getProgressStatus( );
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
        }else if(evSrc==annotationLoader){
            ProgressStatus status=progressEvent.getProgressStatus( );
            if(status.isDone()){
                if(debug)System.out.println("Annotation loaded.");
                VectorBufferedInputStream annoIs=new VectorBufferedInputStream(annotionFileContent);
                ContentType contentType=annotationLoader.getContentType();
                
                Charset charset=textGridCharset;
                if(charset==null){
                    charset=Charset.forName("ISO-8859-1");
                    if(debug)System.out.println("Default charset: "+charset.name());
                }else{
                    if(debug)System.out.println("Forced charset : "+charset.name());
                }
                
                if(contentType!=null){
                    String charsetName=contentType.getCharsetParameter();
                    if(debug)System.out.println("Charset string from content type header: "+charsetName);
                    if(charsetName!=null && ! "".equals(charsetName)){
                        try{
                            charset=Charset.forName(charsetName);
                            if(debug)System.out.println("Charset from content type: "+charset.name());
                        }catch(IllegalArgumentException iae){
                            // TODO better handling?
                            // continue anyway
                            if(debug){
                                System.err.println("Could not create charset object from content type header: "+charsetName+":");
                                iae.printStackTrace();
                            }

                        }
                    }
                }
                if(debug)System.out.println("Annotation charset: "+charset.name());
                annotationreader = new InputStreamReader(annoIs,charset);
//                annotationreader = new InputStreamReader(annoIs);
                createAnnotation();
            }
        }
    }
    public void makeReady(){
        if(bundle==null && annotationreader!=null){
            //we have an annotation file waiting
            createAnnotation();
        }
        revalidate();
        repaint();
        status=Status.READY;
        setMessage("Audio player ready.");
    }
    private void clearScreen(){
        if(progressPanel!=null)remove(progressPanel);
        if(scrollPane!=null)remove(scrollPane);
    }
    
    private void process() throws AudioSourceException{
        status=Status.PROCESS;
        clearScreen();
        playbackSource=audioSource;

        //AudioClip audioClip = new AudioClip(audioSource);
        //AudioClipProcessor processor = new AudioClipProcessor(audioClip);
        //processor.setCalculateSBNR(true);
        String m="Processing audio data ...";
        if(debug){
            System.out.println(m);
        }
        setMessage(m);
        
       
            audioClip.setAudioSource(audioSource);
            audioClip.setSelection(selection);
            add(scrollPane, BorderLayout.CENTER);
        
            validate();
        
 
        playerPanel.validate();
    
            // the signal should initially fit to panel
            uiContainer.xZoomFitToPanel();
        
        uiContainer.validate();
        validate();
        scrollPane.revalidate();
        scrollPane.repaint();
        
    }
    private void enableAudio(){ 
        if(audioClip!=null){
            
        //audioSample.setAudioSource(audioSource);

        // the signal should initially fit to panel
        //uiContainer.setXzoomFitToPanel();
        if (player != null) {
            try {
                player.setAudioSource(playbackSource);
//              playButton.setEnabled(true);
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
    
    public void setAnnotationURL(URL annotationURL) {
        
      
        if(annotationLoader!=null){
            annotationLoader.removeProgressListener(this);
        annotationLoader.cancel();
        try {
            annotationLoader.close();
            annotationLoader.reset();
        } catch (WorkerException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        }
        
//        if(annotationAudioClipUIs!=null){
//            for(AnnotationAudioClipUI aacui:annotationAudioClipUIs){
//                uiContainer.remove(aacui);
//                aacui.close();
//            }
//            annotationAudioClipUIs.clear();
//        }
        annotationreader=null;
        bundle=null;
        this.annotationURL = annotationURL;

        if(this.annotationURL!=null){
            annotionFileContent=new VectorBufferedOutputStream();
            annotationLoader=new URLContentLoader(this.annotationURL,annotionFileContent,"annotation loader");

            annotationLoader.addProgressListener(this);
            try {
                annotationLoader.open();
            } catch (WorkerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            annotationLoader.start();
        }


    }
    public void setMessage(String message) {
        String oldMessage=this.message;
        this.message = message;
        pChTa.fireEvent(new PropertyChangeEvent(this,"message",oldMessage,this.message));
    }
    private void createPopupMenu(){
        ActionTreeRoot ascActionTree=scrollPane.getActionTreeRoot();
        ActionFolder signalViewFolder=new ActionFolder("signalview",new LocalizableMessage("Signal view"));
        ActionTreeRoot shiftedAscActionTree=ascActionTree.shiftFromTopLevel(signalViewFolder);
        ActionFolder afft=new ActionTreeRoot();
       
//        aff.add(pa);
        
        
        ActionFolder avf=ActionFolder.buildTopLevelFolder(ActionFolder.VIEW_FOLDER_KEY);
//        afft.add(avf);
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

    public void setVisualizing() {
        signalUI = new AudioSignalUI();

        uiContainer = new AudioClipUIContainer();
        uiContainer.add(signalUI);
        sonagram=new FourierUI();
        sonagram.setUseThread(true);
        uiContainer.add(sonagram);
        fragmentActionBar=new FragmentActionBarUI();
        fragmentActionBar.setStartPlaybackAction(startAction);
        uiContainer.add(fragmentActionBar);
        timeScale = new AudioTimeScaleUI();
        uiContainer.add(timeScale);

        scrollPane = new AudioClipScrollPane(uiContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        uiContainer.setAudioClip(audioClip);
        createPopupMenu();

    }
    
    public boolean isShowSonagram() {
        return sonagram.isVisible();
    }


    public void setShowSonagram(boolean showSonagram) {
       sonagram.setVisible(showSonagram);
    }

    public boolean isShowFragmentActionBar() {
        return fragmentActionBar.isVisible();
    }


    public void setShowFragmentActionBar(boolean showFragmentActionBar) {
       fragmentActionBar.setVisible(showFragmentActionBar);
    }


    public boolean isShowTimeScale() {
        return timeScale.isVisible();
    }

    public void setShowTimeScale(boolean showTimeScale) {
       timeScale.setVisible(showTimeScale);
    }

    public boolean isAutoPlayOnLoad() {
        return autoPlayOnLoad;
    }


    public void setAutoPlayOnLoad(boolean autoPlayOnLoad) {
        this.autoPlayOnLoad = autoPlayOnLoad;
    }
    public boolean isStartPlayOnSelect() {
        return startPlayOnSelect;
    }

    public void setStartPlayOnSelect(boolean startPlayOnSelect) {
        this.startPlayOnSelect = startPlayOnSelect;
    }

    public void setURL(URL source) {
        
        URL oldUrl=this.source;
        if(this.source!=null && this.source.equals(source)){
            return;
        }
        
        if(audioClip!=null)audioClip.setAudioSource(null);
        this.source=source;
        closeAudio();
        selection=null;
//      playButton.setEnabled(false);
        startAction.setEnabled(false);
//      stopButton.setEnabled(false);
        stopAction.setEnabled(false);
        closeAudioDownload();
        vbOut=null;
        clearScreen();
        load();
        pChTa.fireEvent(new PropertyChangeEvent(this,"source",oldUrl,this.source));
    }
    
    public void deactivate() {
        closeAnnoDownload();
        if(Status.LOADING.equals(status)){
            // stop download
            closeAudioDownload();
          
            status=Status.INITIALIZED;
        }else{
            if(audioClip !=null){
                updateTimer.stop();
//              playButton.setEnabled(false);
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
            status=Status.DEACTIVATED;
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
//       setAnnotationURL(getAnnotationURL());
    }
    
    private void load(){
        clearScreen();
        String urlProtocol=source.getProtocol();
        if(urlProtocol.equalsIgnoreCase("file")){
            // do not load, just use the file
            audioSource=new URLAudioSource(source);
            if(scrollPane!=null){
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
            status=Status.LOADING;
            vbOut=new VectorBufferedOutputStream();
            audioLoader=new URLContentLoader(source,vbOut,"URL content loader");
            
                progressPanel=new JProgressDialogPanel(audioLoader,"title","Loading audio ...");
                add(progressPanel,BorderLayout.CENTER);
            

            revalidate();
            repaint();
            audioLoader.addProgressListener(this);
            try {
                audioLoader.open();
            } catch (WorkerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            audioLoader.start();
        }

    }
    private void closeAnnoDownload(){
        if(annotationLoader!=null){
            annotationLoader.removeProgressListener(this);
            annotationLoader.cancel();
            try {
                annotationLoader.close();
                annotationLoader.reset();
            } catch (WorkerException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
    private void closeAudioDownload(){
        if(audioLoader!=null){
            audioLoader.removeProgressListener(this);
            audioLoader.cancel();
            try {
                audioLoader.close();
                audioLoader.reset();
            } catch (WorkerException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }
    
private void closeAudio() {
        
        updateTimer.stop();
//      playButton.setEnabled(false);
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
      
        audioClip.setAudioSource(null);
        
        
    }
    public void close() {
        status=Status.CLOSING;
        closeAudio();
        closeAudioDownload();
        closeAnnoDownload();
        //open = false;
        if(updateTimer !=null)updateTimer.stop();
        if(uiContainer!=null)uiContainer.close();
        if(audioClip!=null)audioClip.removeAudioSampleListener(this);
        clearScreen();
        status=Status.CLOSED;
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
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.player.PlayerListener#update(ipsk.audio.player.PlayerEvent)
     */
    public void update(PlayerEvent playerEvent) {

        if(playerEvent instanceof PlayerStartEvent){
            setMessage("Audio player playing...");
//          stopButton.setEnabled(true);
            stopAction.setEnabled(true);
            stopAction.setHighlighted(false);
            pauseAction.setEnabled(true);
            pauseAction.setHighlighted(false);
            startAction.setEnabled(false);
            startAction.setHighlighted(true);
        }else if(playerEvent instanceof PlayerPauseEvent){
            setMessage("Audio player paused.");
//          stopButton.setEnabled(true);
            stopAction.setEnabled(true);
            stopAction.setHighlighted(false);
            pauseAction.setEnabled(true);
            pauseAction.setHighlighted(true);
            startAction.setEnabled(false);
            startAction.setHighlighted(true);
        }else if (playerEvent instanceof PlayerStopEvent) {
            if (Status.READY.equals(status)) {
//              stopButton.setEnabled(false);
                stopAction.setEnabled(false);
                stopAction.setHighlighted(true);
                pauseAction.setEnabled(false);
                pauseAction.setHighlighted(false);
                //playButton.setEnabled(false);
                startAction.setEnabled(false);
                startAction.setHighlighted(false);
                setMessage("Audio player stopped.");
            } else {
//              System.out.println("Ignored");
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
//              playButton.setEnabled(true);
                stopAction.setEnabled(false);
                stopAction.setHighlighted(true);
                startAction.setEnabled(true);
                startAction.setHighlighted(false);
                
            }else if (Status.CLOSING.equals(status)) {
                // bean is closing
                // do nothing
            }else{
                setMessage("Internal error state ");
//              System.out.println("Close in not ready state! "+status);
            }
            setMessage("Audio player closed.");
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
            audioClip.setFramePosition(player.getFramePosition());
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
    
	/**
	 * Test method
	 * @param args
	 */
	public static void main(String[] args){
		
		if(args.length!=2){
			System.err.println("Usage: AudioPlayerBean audioURL annotationURL");
			System.exit(-1);
		}
		
		try {
			final URL audioUrl = new URL(args[0]);
			final URL annotationURL=new URL(args[1]);
			Runnable show=new Runnable(){
				public void run() {
					JFrame f=new JFrame("Test audio player bean");
					final AnnotatedAudioPlayerBean aBean=new AnnotatedAudioPlayerBean();
					
					aBean.setShowSonagram(false);
					aBean.setShowFragmentActionBar(true);
					aBean.setShowTimeScale(false);
//					aBean.setShowDSPInfo(true);
//					aBean.setVisualizing(true);
					aBean.setStartPlayOnSelect(true);
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
					aBean.setAnnotationURL(annotationURL);
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

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setTextGridCharset(Charset cs) {
        this.textGridCharset=cs;
        
    }



}
