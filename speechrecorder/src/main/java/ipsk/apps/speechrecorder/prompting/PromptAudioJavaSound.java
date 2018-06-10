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

package ipsk.apps.speechrecorder.prompting;

import ipsk.apps.speechrecorder.MIMETypes;
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
import ipsk.net.URLContext;

import java.awt.EventQueue;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.Mixer;
import javax.swing.Action;



public class PromptAudioJavaSound implements PlayerListener{
    
   
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
   
    private Mixer mixer;
    protected Player player;

    private Vector<PromptPresenterListener> listeners;

   

    protected URL audioURL = null;

    private Logger audioLogger;
    
    protected Action startAction;
    private Action startPromptAction;
    protected Action stopAction;
    
    
    protected float volume=(float)1.0;
    
    private boolean startEnabled;
    
   
    protected boolean silent=false;
    
    

    // private Level logLevel = Level.INFO;

    /**
     * PromptAudioViewer contains a button representing the media to play. The
     * button contains a generic media icon together with a short description of
     * the media contents taken from the recording script.
     * 
     * The media can be played by clicking on the button.
     * 
     */
   public PromptAudioJavaSound() {
        super();
//        this.startAction=startAction;
        mixer=AudioSystem.getMixer(null);
//        mixer.addLineListener(this);
        player = new Player(mixer);
        player.addPlayerListener(this);
        listeners = new Vector<PromptPresenterListener>();

        audioLogger = Logger.getLogger("ipsk.apps.speechrecorder");
        // audioLogger.setLevel(logLevel);

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


 	public void setAudioChannelOffset(int channelOffset) {
     	player.setChannelRouting(null);
 		player.setChannelOffset(channelOffset);
 	}

 
 	public void setAudioChannelGroupLocator(
 			ChannelGroupLocator channelGroupLocator)
 			throws PromptPresenterPluginException {
 		Mixer mixer=channelGroupLocator.getDevice();
 		setAudioMixer(mixer);
 		setAudioChannelOffset(channelGroupLocator.getChannelOffset());
 		ChannelRouting channelRouting=channelGroupLocator.getChannelRouting();
 		player.setChannelRouting(channelRouting);
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
//            audioButton.setAction(stopAction);
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

    
    public void loadAudioContents(Mediaitem mi,URL context)
    throws PromptPresenterException {
    
   
    URL url;
    try {
        url = URLContext.getContextURL(context,mi.getSrc().toString());
    } catch (MalformedURLException e1) {
        throw new PromptPresenterException(e1);
    }
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
//            audioButton.setAction(startAction);
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
//            audioButton.setAction(stopAction);
            updateListeners(new PromptPresenterStartEvent(this));
        } else if (playerEvent instanceof PlayerStopEvent) {
            //stopAction.setEnabled(false);
            updateListeners(new PromptPresenterStopEvent(this));
        } else if (playerEvent instanceof PlayerCloseEvent) {
            // audioButton.setEnabled(true);
            //startAction.setEnabled(false);
//            audioButton.setAction(startAction);
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
//      if(audioButton.getAction()==null){
//          audioButton.setAction(startAction);
//      }
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

    public String[][] getSupportedMIMETypes() {
        String[][] mtypes=new String[MIMETypes.AUDIOMIMETYPES.length][1];
        for(int i=0;i<MIMETypes.AUDIOMIMETYPES.length;i++){
            mtypes[i][0]=MIMETypes.AUDIOMIMETYPES[i];
        }
        return mtypes;
    }

 
   

}
