//    Speechrecorder
//    (c) Copyright 2012
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

/**
 * Audio prompt "viewer" using JavaSound audio.
 *
 * @author Klaus Jaensch
 */

package ipsk.apps.speechrecorder.prompting.sound.javasound;

import ipsk.apps.speechrecorder.MIMETypes;
import ipsk.apps.speechrecorder.prompting.BasicPromptPresenter;
import ipsk.apps.speechrecorder.prompting.BasicPromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.prompting.MediaPromptPresenter;
import ipsk.apps.speechrecorder.prompting.MediaPromptPresenter2;
import ipsk.apps.speechrecorder.prompting.PromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterListener;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterPluginException;
import ipsk.apps.speechrecorder.prompting.presenter.UnsupportedContentException;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterClosedEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterOpenedEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterStartEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterStopEvent;
import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.ChannelGroupLocator;
import ipsk.audio.PluginChain;
import ipsk.audio.URLAudioSource;
import ipsk.audio.player.Player;
import ipsk.audio.player.PlayerException;
import ipsk.audio.player.PlayerListener;
import ipsk.audio.player.event.PlayerCloseEvent;
import ipsk.audio.player.event.PlayerEvent;
import ipsk.audio.player.event.PlayerOpenEvent;
import ipsk.audio.player.event.PlayerStartEvent;
import ipsk.audio.player.event.PlayerStopEvent;
import ipsk.audio.plugins.VolumeControlPlugin;
import ipsk.db.speech.Mediaitem;
import ipsk.io.ChannelRouting;
import ipsk.text.Version;
import ipsk.util.LocalizableMessage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.Mixer;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

// import java.util.logging.Level;

public class PromptAudioJavaSoundViewer extends BasicPromptPresenter implements
        MediaPromptPresenter2, PlayerListener{
    public static final PromptPresenterServiceDescriptor DESCRIPTOR=new BasicPromptPresenterServiceDescriptor(PromptAudioJavaSoundViewer.class.getName(),new LocalizableMessage("Audio prompter"), "Institute of Phonetics and Speech processing, Munich", new ipsk.text.Version(new int[]{1,0,0}), new LocalizableMessage("Prompts audio using JavaSound."),PromptAudioJavaSoundViewer.getSupportedMIMETypes());
  
    /**
     * 
     */
    private static final long serialVersionUID = -448684398142928391L;
    private static Font audioButtonFont = new Font("sans-serif", Font.BOLD, 36);
    
   
    
    public class LineEventRunnable implements Runnable{
        private LineEvent lineEvent;
        //private Vector<EventListener> listeners;
        public LineEventRunnable(LineEvent lineEvent){
            this.lineEvent=lineEvent;
        }
        
        public void run(){
            updateInAWTThread(lineEvent);
        }
        
    }
    private ImageIcon audioImage;

    private Mixer mixer;
    private Player player;

    private Vector<PromptPresenterListener> listeners;

    private JButton audioButton;

    private URL audioURL = null;

    private Logger audioLogger;
    
    private Action startAction;
    private Action startPromptAction;
    private Action stopAction;
    
    
    private float volume=(float)1.0;
    
    private boolean startEnabled;
    
   
    private boolean silent=false;
    
    

    // private Level logLevel = Level.INFO;

    /**
     * PromptAudioViewer contains a button representing the media to play. The
     * button contains a generic media icon together with a short description of
     * the media contents taken from the recording script.
     * 
     * The media can be played by clicking on the button.
     * 
     */
   public PromptAudioJavaSoundViewer() {
        super();
//        this.startAction=startAction;
        mixer=AudioSystem.getMixer(null);
//        mixer.addLineListener(this);
        player = new Player(mixer);
        player.addPlayerListener(this);
        listeners = new Vector<PromptPresenterListener>();

        audioLogger = Logger.getLogger("ipsk.apps.speechrecorder");
        // audioLogger.setLevel(logLevel);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BorderLayout());
        
//        audioImage = new ImageIcon(getClass()
//                .getResource("icons/playAudio.gif"));
//        audioButton = new JButton(startAction);
//
        audioButton=new JButton();
//        audioButton.setIcon(audioImage);
        audioButton.setFont(audioButtonFont);
        audioButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        audioButton.setHorizontalTextPosition(AbstractButton.CENTER);
//        final Component thisComp=this;
//        audioButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    open();
//                    start();
//                } catch (PromptPresenterException e1) {
//                    e1.printStackTrace();
//                    Throwable realCause=e1;
//                    while(realCause.getCause()!=null){
//                        realCause=realCause.getCause();
//                    }
//                    JOptionPane.showMessageDialog(thisComp, realCause.getLocalizedMessage(), "Audio prompt player error", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }
//               
//            }
//        });
        add(audioButton,BorderLayout.CENTER);
        
    }
   private final static LocalizableMessage TITLE=new LocalizableMessage("Audio prompter");
   public LocalizableMessage getTitle(){
       return TITLE;
   }
   private static final LocalizableMessage DESCRIPTION=new LocalizableMessage("Presents audio using JavaSound.");
   public LocalizableMessage getDescription(){
       return DESCRIPTION;
   }
   private final static String VENDOR="Institute of Phonetics and Speech processing, Munich";
   public String getVendor(){
       return VENDOR;
   }
   private final static Version SPECIFICATIONVERSION= new Version(new int[]{1,0});
   public Version getSpecificationVersion(){
       return SPECIFICATIONVERSION;
   }
   private final static Version IMPLEMENTATIONVERSION= new Version(new int[]{1,0});
   public Version getImplementationVersion(){
       return IMPLEMENTATIONVERSION;
   }
//   
//   public void addNotify(){
//       if(audioButton==null){
//           audioButton = new JButton(startAction);
//           audioButton.setVerticalTextPosition(AbstractButton.BOTTOM);
//           audioButton.setHorizontalTextPosition(AbstractButton.CENTER);
//           add(audioButton,BorderLayout.CENTER);
//       }
//   }
   
    public void setAudioMixer(Mixer mixer) throws PromptPresenterPluginException{
        if(mixer==null){
            mixer=AudioSystem.getMixer(null);
        }
//        if(this.mixer!=null){
//            this.mixer.removeLineListener(this);
//        }
        this.mixer=mixer;
//        if(mixer!=null){
//            mixer.addLineListener(this);
//        }
        try {
            player.setMixer(mixer);
        } catch (PlayerException e) {
            e.printStackTrace();
            throw new PromptPresenterPluginException(e);
        }
    }
    
    @Override
	public void setAudioChannelOffset(int channelOffset) {
    	player.setChannelRouting(null);
		player.setChannelOffset(channelOffset);
	}

	/* (non-Javadoc)
	 * @see ipsk.apps.speechrecorder.prompting.MediaPromptPresenter2#setAudioChannelGroupLocator(ipsk.audio.ChannelGroupLocator)
	 */
	@Override
	public void setAudioChannelGroupLocator(
			ChannelGroupLocator channelGroupLocator)
			throws PromptPresenterPluginException {
		Mixer mixer=channelGroupLocator.getDevice();
		setAudioMixer(mixer);
		setAudioChannelOffset(channelGroupLocator.getChannelOffset());
		ChannelRouting channelRouting=channelGroupLocator.getChannelRouting();
		player.setChannelRouting(channelRouting);
	}

    public void setContents(String string, String description, String type) {

    }

    /**
     * Sets the prompts
     * 
     * @param mh
     */
    public void setRecScriptResources(Hashtable mh) {
    }
    
    public void open() throws PromptPresenterException{
        
        if(silent){
            
            // fake open event
            updateListeners(new PromptPresenterOpenedEvent(this));
        }else{
           
        if (audioURL != null) {
        
            try {
                
                player.open();
            } catch (PlayerException e) {
               
                e.printStackTrace();
                throw new PromptPresenterException(e);
                
            }
        }
        }
    }
    /**
     * mediaPlay() plays the media retrieved from a given URL.
     * 
     */
    public void start() throws PromptPresenterException{
        if(silent){
            // fake start event
            audioButton.setAction(stopAction);
            updateListeners(new PromptPresenterStartEvent(this));
            EventQueue.invokeLater(new Runnable(){
                public void run(){
                    stop();
                }
            });
        }else{
        if (audioURL != null) {
            try {
                // audioController=new Player(audioURL);
                
                //player.open();

                player.play();
                //audioButton.setEnabled(false);
            } catch (PlayerException e) {
               
                e.printStackTrace();
                throw new PromptPresenterException(e);
                
            }
        }
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(getSize());
    }

 
    
  
    
    public void loadContents()
    throws PromptPresenterException {
        if(mediaitems.length > 1){
        throw new UnsupportedContentException("Multiple media items not supported!");
    }  
    
    Mediaitem mi=mediaitems[0];
    URL url=applyContextToMediaitemURL(mi);
    String description=mi.getDescription();
    volume=mi.getNormalizedVolume();
    audioURL = url;
    // audioLogger.INFO("setContents(String, Description): " +
    // audioURL.toExternalForm() + ", " + description);
    //audioButton.setIcon(audioImage);
    AudioSource urlSource=new URLAudioSource(audioURL);
    AudioSource playbackSource;
    if(volume!=1.0){
        PluginChain pc=new PluginChain(urlSource);
        VolumeControlPlugin vcp=new VolumeControlPlugin();
        vcp.setVolume(volume);
        //System.out.println("Playback volume: "+volume);
        try {
            pc.add(vcp);
        } catch (AudioFormatNotSupportedException e) {
           throw new UnsupportedContentException(e);
        }
        playbackSource=pc;
    }else{
        playbackSource=urlSource;
    }
    try {
        player.setAudioSource(playbackSource);
    } catch (PlayerException e) {
        throw new PromptPresenterException(e);
    }
    if(description !=null){
        //audioButton.setText(description);
        
        startAction.putValue(Action.SMALL_ICON, null);
        startAction.putValue(Action.LARGE_ICON_KEY, null);
        startAction.putValue(Action.NAME, description);
        stopAction.putValue(Action.SMALL_ICON, null);
        stopAction.putValue(Action.LARGE_ICON_KEY, null);
        stopAction.putValue(Action.NAME, description);
    }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see ipsk.apps.speechrecorder.PromptPresenter#mediaStop()
     */
    public void stop() {
        if(silent){
            updateListeners(new PromptPresenterStopEvent(this));
        }else{
            player.stop();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.apps.speechrecorder.PromptPresenter#mediaClose()
     */
    public void close() {
        if(silent){
            audioButton.setAction(startAction);
            updateListeners(new PromptPresenterClosedEvent(this));
        }else{
        try {
            player.close();
        } catch (PlayerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        }

    }

    protected synchronized void updateListeners(PromptPresenterEvent event) {
        for (PromptPresenterListener ppl : listeners) {
            ppl.update(event);
        }
    }

    public void addPromptPresenterListener(PromptPresenterListener listener) {

        if (listener != null && !listeners.contains(listener)) {
            listeners.addElement(listener);
        }
    }

    public void removePromptPresenterListener(PromptPresenterListener listener) {

        if (listener != null) {
            listeners.removeElement(listener);
        }
    }

    public void update(PlayerEvent playerEvent) {

        if (playerEvent instanceof PlayerOpenEvent) {
            updateListeners(new PromptPresenterOpenedEvent(this));
        } else if (playerEvent instanceof PlayerStartEvent) {
//            updateListeners(new PromptPresenterStartEvent(this));
            audioButton.setAction(stopAction);
            updateListeners(new PromptPresenterStartEvent(this));
        } else if (playerEvent instanceof PlayerStopEvent) {
            //stopAction.setEnabled(false);
            updateListeners(new PromptPresenterStopEvent(this));
        } else if (playerEvent instanceof PlayerCloseEvent) {
            // audioButton.setEnabled(true);
            //startAction.setEnabled(false);
            audioButton.setAction(startAction);
            updateListeners(new PromptPresenterClosedEvent(this));
        }

    }

    public void setStartControlEnabled(boolean startEnabled) {
        this.startEnabled=startEnabled;
    }

    public void setStopControlEnabled(boolean stopEnabled) {
        // TODO Auto-generated method stub
        
    }

//    public void setVolume(float volume) throws PromptPresenterPluginException {
//        this.volume=volume;
//        
//    }

  
    
    private void updateInAWTThread(LineEvent lineEvent) {
        LineEvent.Type let=lineEvent.getType();
        if(LineEvent.Type.OPEN.equals(let)){
            
        }else if(LineEvent.Type.CLOSE.equals(let)){
            
        }
    }

 

    public void setStartControlAction(Action startControlAction) {
       startAction=startControlAction;
      if(audioButton.getAction()==null){
          audioButton.setAction(startAction);
      }
    }

    public void setStopControlAction(Action stopControlAction) {
        stopAction=stopControlAction;
        
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public static String[][] getSupportedMIMETypes() {
        String[][] mtypes=new String[MIMETypes.AUDIOMIMETYPES.length][1];
        for(int i=0;i<MIMETypes.AUDIOMIMETYPES.length;i++){
            mtypes[i][0]=MIMETypes.AUDIOMIMETYPES[i];
        }
        return mtypes;
    }

    /* (non-Javadoc)
     * @see ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter#getServiceDescriptor()
     */
    public PromptPresenterServiceDescriptor getServiceDescriptor() {
        return DESCRIPTOR;
    }

	/* (non-Javadoc)
	 * @see ipsk.apps.speechrecorder.prompting.MediaPromptPresenter2#setAudioCahhnelOffset(int)
	 */
	



}
