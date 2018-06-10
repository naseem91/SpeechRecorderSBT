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
* PromptImageViewer fetches the images found in the image directory
* and provides methods for selecting them.
*
* @author Christoph Draxler
* @version 1.0
* @since JDK 1.0
*/

package ipsk.apps.speechrecorder.prompting;

import ipsk.apps.speechrecorder.MIMETypes;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterListener;
import ipsk.apps.speechrecorder.prompting.presenter.UnsupportedContentException;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterClosedEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterStopEvent;
import ipsk.audio.AudioController2;
import ipsk.audio.AudioController2.AudioController2Listener;
import ipsk.audio.AudioController2.AudioControllerEvent;
import ipsk.audio.AudioControllerException;
import ipsk.audio.URLAudioSource;
import ipsk.audio.player.event.PlayerCloseEvent;
import ipsk.audio.player.event.PlayerEvent;
import ipsk.audio.player.event.PlayerStopEvent;
import ipsk.db.speech.Mediaitem;
import ipsk.util.LocalizableMessage;
import ipsk.util.services.Description;
import ipsk.util.services.Title;
import ipsk.util.services.Vendor;
import ipsk.util.services.Version;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
//import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

@Title("Audio prompter")
@Description("Presents audio using speechrecorder audio controller interface")
@Vendor("Institute of Phonetics and Speech processing, Munich")
@Version(major=1)
public class PromptAudioControllerViewer extends BasicPromptPresenter implements PromptPresenter, AudioController2Listener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -448684398142928391L;
	private static final PromptPresenterServiceDescriptor DESCRIPTOR=new BasicPromptPresenterServiceDescriptor(PromptAudioControllerViewer.class.getName(),new LocalizableMessage("Audio prompter"), "Institute of Phonetics and Speech processing, Munich", new ipsk.text.Version(new int[]{1,0,0}), new LocalizableMessage("Presents audio using speechrecorder audio controller interface"),PromptAudioControllerViewer.getSupportedMIMETypes());
	private ImageIcon audioImage;
    private AudioController2 audioController;
    private Vector<PromptPresenterListener> listeners;
	private JButton audioButton;
	private URL audioURL = null;
//	private Logger audioLogger;
//	private Level logLevel = Level.INFO;

	/**
	 * PromptAudioViewer contains a button representing the media to play. The
	 * button contains a generic media icon together with a short description 
	 * of the media contents taken from the recording script.
	 * 
	 * The media can be played by clicking on the button.
	 *
	 */
	PromptAudioControllerViewer(AudioController2 audioController) {
		super();
        listeners=new Vector<PromptPresenterListener>();
		this.audioController=audioController;
	
//		audioLogger = Logger.getLogger("ipsk.apps.speechrecorder");
//		audioLogger.setLevel(logLevel);

		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setLayout(new BorderLayout());
	
		audioImage = new ImageIcon(getClass().getResource("icons/playAudio.gif"));
		audioButton = new JButton(audioImage);
		audioButton.setVerticalTextPosition(AbstractButton.BOTTOM);
		audioButton.setHorizontalTextPosition(AbstractButton.CENTER);
		audioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				mediaPlay();
			}
		});
		add(audioButton,BorderLayout.CENTER);
	}
	

	public void setContents(String string, String description,String type){
    	
    }

	/**
	 * Sets the prompts 
	 * @param mh
	 */
	public void setRecScriptResources(Hashtable mh) {
	}

	/**
	 * mediaPlay() plays the media retrieved from a given URL.
	 *
	 */
	public void mediaPlay() {
		if (audioURL != null) {
			try {
                audioController.addAudioController2Listener(this);
                audioController.openPlayback();
                
                audioController.startPlayback();
            } catch (AudioControllerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}
	}
	

	
	public Dimension getPreferredSize() {
		return new Dimension(getSize());
	}

	public void showContents() {
	};
	
	public void hideContents() {
	};
	
	public void loadContents(URL url) {
	};

	/**
	* setContents() empty stub
	*
	* @param name name of image file
	*/
	public void setContents(String name) {}

	/**
	* setContents() empty stub
	*
	* @param name name of image file
	* @param description description of image
	*/
	public void setContents(String name, String description) {}

	
	public void setContents(URL url) {
		audioURL = url;
//		audioLogger.INFO("setContents(String): " + audioURL.toExternalForm());
		audioButton.setIcon(audioImage);
        try {
            audioController.setPlaybackAudioSource(new URLAudioSource(audioURL));
        } catch (AudioControllerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	/**
	* setContents() selects the audio file to play
	*
	* @param url URL of audio file
	* @param description description of audio file, e.g. contents
	*/
	public void setContents(URL url, String description) {	
		audioButton.setText(description);
        setContents(url);
	}

    public void setContents(URL url, String description, String type) {
        setContents(url, description);  
    }
     
	/**
	* setContents() selects the audio file to play
	*
	* @param url URL of audio file
	* @param description description of audio file, e.g. contents
	* @param t MIME-type of audio file
    * @param charset charset of audio file (no used)
	*/
	public void setContents(URL url, String description, String t,String charset) {
		setContents(url, description,t);
	}

	/* (non-Javadoc)
	 * @see ipsk.apps.speechrecorder.PromptPresenter#mediaStop()
	 */
	public void mediaStop() {
		try {
            audioController.stopPlayback();
        } catch (AudioControllerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}

	/* (non-Javadoc)
	 * @see ipsk.apps.speechrecorder.PromptPresenter#mediaClose()
	 */
	public void close() {
		try {
            audioController.closePlayback();
        } catch (AudioControllerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}

    public void update(AudioControllerEvent ace) {
        
            if (ace instanceof PlayerEvent) {
                if (ace instanceof PlayerStopEvent) {
                 updateListeners(new PromptPresenterStopEvent(this));         
                } else if(ace instanceof PlayerCloseEvent){
                updateListeners(new PromptPresenterClosedEvent(this));  
            }
            }
    }

   
    protected synchronized void updateListeners(PromptPresenterEvent event) {
        for(PromptPresenterListener ppl:listeners){
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

    public static String[][] getSupportedMIMETypes() {
        String[][] mtypes=new String[MIMETypes.AUDIOMIMETYPES.length][1];
        for(int i=0;i<MIMETypes.AUDIOMIMETYPES.length;i++){
            mtypes[i][0]=MIMETypes.AUDIOMIMETYPES[i];
        }
        return mtypes;
    }

    public void loadContents()
            throws PromptPresenterException {
        if(mediaitems.length > 1){
            throw new UnsupportedContentException("Multiple media items not supported!");
        }  
        
        Mediaitem mi=mediaitems[0];
        URL url=applyContextToMediaitemURL(mi);
        String description=mi.getDescription();
        if(description!=null){
            audioButton.setText(description);
        }
        audioURL = url;
//      audioLogger.INFO("setContents(String): " + audioURL.toExternalForm());
        audioButton.setIcon(audioImage);
        try {
            audioController.setPlaybackAudioSource(new URLAudioSource(audioURL));
        } catch (AudioControllerException e) {
            throw new PromptPresenterException(e);
        }
        
    }


    /* (non-Javadoc)
     * @see ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter#getDescriptor()
     */
    public PromptPresenterServiceDescriptor getServiceDescriptor() {
        return DESCRIPTOR;
    }

    

  
  
}
