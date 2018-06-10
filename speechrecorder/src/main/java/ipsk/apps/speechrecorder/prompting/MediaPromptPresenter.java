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

package ipsk.apps.speechrecorder.prompting;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterPluginException;


import javax.sound.sampled.Mixer;
import javax.swing.Action;

/**
 * Extended prompt presenter interface for media prompts.
 *  
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public interface MediaPromptPresenter extends PromptPresenter{
 
    /**
     * Set audio mixer (for plugins using JavaSound)
     * @param mixer
     */
    public void setAudioMixer(Mixer mixer)throws PromptPresenterPluginException;
    
    
    /**
     * If set silent, the media presenter should not play audio.
     * @param silent
     */
    public void setSilent(boolean silent);
    
//    /**
//     * Set volume.
//     * For plugins playing audio.
//     * Volume 0.0: silent, 1.0:play in original volume
//     * @param volume
//     */
//    public void setVolume(float volume)throws PromptPresenterPluginException;
    
    /**
     * Set start action for playback.
     * @param startControlAction
     */
    
    public void setStartControlAction(Action startControlAction);
    /**
     * Set stop action for playback.
     * @param stopControlAction
     */
    public void setStopControlAction(Action stopControlAction);
    
    
    /**
     * Open the prompter.
     * Opens required resources to playback the media. 
     */
    public void open() throws PromptPresenterException;
    
    /**
     * Start the prompt media.
     * Moving media prompters (like video,audio players) should start to play,
     * still media prompters should do nothing. 
     * @throws PromptPresenterException 
     */
    public void start() throws PromptPresenterException;
    
    /**
     * Stop the prompt media.
     * Moving media prompters (like video,audio players) should stop,
     * still media prompters do nothing. 
     */
    public void stop() throws PromptPresenterException;
    
    /**
     * Close (release) the prompter.
     * Closes and releases all resources. 
     */
    public void close() throws PromptPresenterException;
    
}
