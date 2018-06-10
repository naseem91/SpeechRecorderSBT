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
import ipsk.apps.speechrecorder.prompting.presenter.UnsupportedContentException;
import ipsk.db.speech.Mediaitem;
import ipsk.util.LocalizableMessage;
import ipsk.util.services.Description;
import ipsk.util.services.Title;
import ipsk.util.services.Vendor;
import ipsk.util.services.Version;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
//import java.util.logging.Level;

@Title("Audio prompter")
@Description("Presents audio using Java applet audio interface. (Warning! No transport control!)")
@Vendor("Institute of Phonetics and Speech processing, Munich")
@Version(major=1)
public class PromptAudioViewer extends BasicPromptPresenter implements PromptPresenter {
   
	private static final long serialVersionUID = 1L;
	private static final PromptPresenterServiceDescriptor DESCRIPTOR=new BasicPromptPresenterServiceDescriptor(PromptAudioViewer.class.getName(),new LocalizableMessage("Audio prompter"), "Institute of Phonetics and Speech processing, Munich", new ipsk.text.Version(new int[]{1,0,0}), new LocalizableMessage("Presents audio using Java applet audio interface. (Warning! No transport control!)"),PromptAudioViewer.getSupportedMIMETypes());
//    private final String EMPTY = "";
	private ImageIcon audioImage;
	private JButton audioButton;
	private URL audioURL = null;
//	private Hashtable audioList;
//	private Logger audioLogger;
    private Font audioButtonFont = new Font("sans-serif", Font.BOLD, 36);
    
    

//	private Level logLevel = Level.INFO;

	/**
	 * PromptAudioViewer contains a button representing the media to play. The
	 * button contains a generic media icon together with a short description 
	 * of the media contents taken from the recording script.
	 * 
	 * The media can be played by clicking on the button.
	 *
	 */
	public PromptAudioViewer() {
		super();
	
//		audioLogger = Logger.getLogger("ipsk.apps.speechrecorder");
//		audioLogger.setLevel(logLevel);

		setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		setLayout(new BorderLayout());
	
		audioImage = new ImageIcon(getClass().getResource("icons/playAudio.gif"));
		createAudioButton(null, null);
	}

    /**
     * createAudioButton() checks whether there exists an audio button already. If so,
     * this button is removed from the display. Then, a new button is created with
     * the appropriate button text or a default icon.
     * 
     * @param icon Icon to display (default icon)
     * @param text text to show instead of the icon
     */
    
	private void createAudioButton(ImageIcon icon, String text) {
        if (audioButton != null) {
            remove(audioButton);
        }
        audioButton = new JButton();
        audioButton.setFont(audioButtonFont);
        audioButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        audioButton.setHorizontalTextPosition(AbstractButton.CENTER);
        audioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                start();
            }
        });
        if (icon != null) {
            audioButton.setIcon(icon);
        }
        if (text != null) {
            audioButton.setText(text);
        }
        add(audioButton,BorderLayout.CENTER);
    }
    
//	/**
//	 * Sets the prompts 
//	 * @param hashtable
//	 */
//	public void setRecScriptResources(Hashtable mh) {
//		audioList = mh;
//	}

	/**
	 * mediaPlay() plays the media retrieved from a given URL.
	 *
	 */
	public void start() {
		if (audioURL != null) {
//			AudioClip mediaClip = (AudioClip) audioList.get(audioURL);
			Applet.newAudioClip(audioURL).play();	
		}
	}
	

	// PromptPresenter interface methods
	
	public Dimension getPreferredSize() {
		return new Dimension(getSize());
	}



	/* (non-Javadoc)
	 * @see ipsk.apps.speechrecorder.PromptPresenter#mediaClose()
	 */
	public void close() {
		// TODO Auto-generated method stub
		
	}

    public void setStartControlEnabled(boolean startEnabled) {
        // TODO Auto-generated method stub
        
    }

    public void setStopControlEnabled(boolean stopEnabled) {
        // TODO Auto-generated method stub
        
    }

    public void stop() {
        // TODO Auto-generated method stub
        
    }

  
   
    public void loadContents()
    throws PromptPresenterException {

        if(mediaitems.length > 1){
            throw new UnsupportedContentException("Multiple media items not supported!");
        }  

        Mediaitem mi=mediaitems[0];
        URL url=applyContextToMediaitemURL(mi);
        String description=mi.getDescription();
        audioURL = url;
        if(description==null){
            createAudioButton(audioImage, null);
        }else{
            createAudioButton(null, description);
        }
    }
    
    public static String[][] getSupportedMIMETypes() {
        String[][] mtypes=new String[MIMETypes.AUDIOMIMETYPES.length][1];
        for(int i=0;i<MIMETypes.AUDIOMIMETYPES.length;i++){
            mtypes[i][0]=MIMETypes.AUDIOMIMETYPES[i];
        }
        return mtypes;
    }

    /* (non-Javadoc)
     * @see ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter#getDescriptor()
     */
    public PromptPresenterServiceDescriptor getServiceDescriptor() {
        return DESCRIPTOR;
    }



}