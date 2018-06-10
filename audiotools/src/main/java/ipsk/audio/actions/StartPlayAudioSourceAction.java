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

package ipsk.audio.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import ipsk.audio.AudioSource;
import ipsk.audio.events.StartPlaybackActionEvent;

/**
 * @author klausj
 *
 */
public class StartPlayAudioSourceAction extends StartPlaybackAction {

	private AudioSource audioSource;

	
	
	public void actionPerformed(ActionEvent ae){
	    StartPlaybackActionEvent spae;
	    if(ae instanceof StartPlaybackActionEvent){
	        spae=(StartPlaybackActionEvent)ae;
	        spae.setPlaybackSource(audioSource);
	    }else{
	        spae=new StartPlaybackActionEvent(ae.getSource(),audioSource);
	    }
		super.actionPerformed(spae);
	}



    public AudioSource getAudioSource() {
        return audioSource;
    }



    public void setAudioSource(AudioSource audioSource) {
        this.audioSource = audioSource;
    }


	
}
