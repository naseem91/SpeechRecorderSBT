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
 * Plain text and audio presenter using JavaSound audio.
 *
 * @author Klaus Jaensch
 */

package ipsk.apps.speechrecorder.prompting.combined;

import ipsk.apps.speechrecorder.MIMETypes;
import ipsk.apps.speechrecorder.prompting.BasicPromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.prompting.MediaPromptPresenter2;
import ipsk.apps.speechrecorder.prompting.PromptAudioJavaSound;
import ipsk.apps.speechrecorder.prompting.PromptPlainTextViewer;
import ipsk.apps.speechrecorder.prompting.PromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterListener;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterPluginException;
import ipsk.apps.speechrecorder.prompting.presenter.UnsupportedContentException;
import ipsk.audio.ChannelGroupLocator;
import ipsk.db.speech.Mediaitem;
import ipsk.util.LocalizableMessage;
import ipsk.util.services.Description;
import ipsk.util.services.Title;
import ipsk.util.services.Vendor;
import ipsk.util.services.Version;

import javax.sound.sampled.Mixer;
import javax.swing.Action;


@Title("Plain text and audio prompter")
@Description("Presents plain text and audio at the same time.")
@Vendor("Institute of Phonetics and Speech processing, Munich")
@Version(major=1)
public class PlainTextAndAudioJavaSoundViewer extends PromptPlainTextViewer implements MediaPromptPresenter2{
   
	private static final long serialVersionUID = 1L;
	public static final PromptPresenterServiceDescriptor DESCRIPTOR=new BasicPromptPresenterServiceDescriptor(PlainTextAndAudioJavaSoundViewer.class.getName(),new LocalizableMessage("Plain text and audio prompter"), "Institute of Phonetics and Speech processing, Munich", new ipsk.text.Version(new int[]{1,0,0}), new LocalizableMessage("Presents plain text and audio at the same time."),PlainTextAndAudioJavaSoundViewer.getSupportedMIMETypes());
    private Mediaitem audioMediaitem;
    private PromptAudioJavaSound audioJavaSoundPrompter;
  
   /**
	 * @param channelOffset
	 * @see ipsk.apps.speechrecorder.prompting.PromptAudioJavaSound#setAudioChannelOffset(int)
	 */
	public void setAudioChannelOffset(int channelOffset) {
		audioJavaSoundPrompter.setAudioChannelOffset(channelOffset);
	}

	/**
	 * @param channelGroupLocator
	 * @throws PromptPresenterPluginException
	 * @see ipsk.apps.speechrecorder.prompting.PromptAudioJavaSound#setAudioChannelGroupLocator(ipsk.audio.ChannelGroupLocator)
	 */
	public void setAudioChannelGroupLocator(
			ChannelGroupLocator channelGroupLocator)
			throws PromptPresenterPluginException {
		audioJavaSoundPrompter.setAudioChannelGroupLocator(channelGroupLocator);
	}

public PlainTextAndAudioJavaSoundViewer() {
        super();
        audioJavaSoundPrompter=new PromptAudioJavaSound();
    }
   
   public void setContents(Mediaitem[] mediaitems) throws PromptPresenterException{
       if(mediaitems.length != 2){
           throw new UnsupportedContentException("Only audio text combinations are supported!");
       }  
       
       for(Mediaitem mi:mediaitems){
           if(MIMETypes.isOfType(mi.getNNMimetype(), MIMETypes.PLAINTEXTMIMETYPES)){
               super.setContents(new Mediaitem[]{mi});
           }
           if(MIMETypes.isOfType(mi.getNNMimetype(), MIMETypes.AUDIOMIMETYPES)){
               audioMediaitem=mi;
              
           }
           
       }
   }
    
    public void loadContents()
    throws PromptPresenterException {
   
    super.loadContents();
    audioJavaSoundPrompter.loadAudioContents(audioMediaitem, contextURL);
    }
    
 
    public static String[][] getSupportedMIMETypes() {
        int combinations=MIMETypes.AUDIOMIMETYPES.length*MIMETypes.PLAINTEXTMIMETYPES.length;
        String[][] mtypes=new String[combinations][];
        int ci=0;
        for(int i=0;i<MIMETypes.PLAINTEXTMIMETYPES.length;i++){
            for(int j=0;j<MIMETypes.AUDIOMIMETYPES.length;j++){
                mtypes[ci++]=new String[]{MIMETypes.PLAINTEXTMIMETYPES[i],MIMETypes.AUDIOMIMETYPES[j]};
                }
        }
        return mtypes;
    }

    public void close() {
        audioJavaSoundPrompter.close();
    }

    public boolean isSilent() {
        return audioJavaSoundPrompter.isSilent();
    }

    public void open() throws PromptPresenterException {
        audioJavaSoundPrompter.open();
    }

    public void setAudioMixer(Mixer mixer)
            throws PromptPresenterPluginException {
        audioJavaSoundPrompter.setAudioMixer(mixer);
    }

    public void setSilent(boolean silent) {
        audioJavaSoundPrompter.setSilent(silent);
    }

    public void setStartControlAction(Action startControlAction) {
        audioJavaSoundPrompter.setStartControlAction(startControlAction);
    }

    public void setStartControlEnabled(boolean startEnabled) {
        audioJavaSoundPrompter.setStartControlEnabled(startEnabled);
    }

    public void setStopControlAction(Action stopControlAction) {
        audioJavaSoundPrompter.setStopControlAction(stopControlAction);
    }

    public void setStopControlEnabled(boolean stopEnabled) {
        audioJavaSoundPrompter.setStopControlEnabled(stopEnabled);
    }

    public void start() throws PromptPresenterException {
        audioJavaSoundPrompter.start();
    }

    public void stop() {
        audioJavaSoundPrompter.stop();
    }

    public void addPromptPresenterListener(PromptPresenterListener listener) {
        super.addPromptPresenterListener(listener);
        audioJavaSoundPrompter.addPromptPresenterListener(listener);
    }

    public void removePromptPresenterListener(PromptPresenterListener listener) {
        super.removePromptPresenterListener(listener);
        audioJavaSoundPrompter.removePromptPresenterListener(listener);
    }
    /* (non-Javadoc)
     * @see ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter#getServiceDescriptor()
     */
    public PromptPresenterServiceDescriptor getServiceDescriptor() {
       return DESCRIPTOR;
    }

}
