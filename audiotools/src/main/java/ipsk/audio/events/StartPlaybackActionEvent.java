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

/*
 * Date  : Jun 17, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.events;

import ipsk.audio.AudioSource;
import ipsk.audio.actions.StartPlaybackAction;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.sound.sampled.AudioSystem;

/**
 * Event indicates start of playback.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class StartPlaybackActionEvent extends PlaybackActionEvent {

	
	private AudioSource playbackSource;
	private long startFramePosition;

	private long stopFramePosition;

	public StartPlaybackActionEvent(Object source) {
		this(source,null);
	}

	/**
	 * Create start playback event for a selection of a particular audio source.
	 * This is useful for applications with multiple audio sources.
	 * @param source event source
	 * @param playbackSource playback audio source
	 * @param startFramePosition start position in frames
	 * @param stopFramePosition stop position in frames
	 */
	public StartPlaybackActionEvent(Object source, AudioSource playbackSource, long startFramePosition,
			long stopFramePosition) {
		super(source, ActionEvent.ACTION_PERFORMED,
				StartPlaybackAction.ACTION_COMMAND);
		this.playbackSource=playbackSource;
		this.startFramePosition = startFramePosition;
		this.stopFramePosition = stopFramePosition;
	}
	/**
	 * Create start playback event for a selection.
	 * @param source event source
	 * @param startFramePosition start position in frames
	 * @param stopFramePosition stop position in frames
	 */
	public StartPlaybackActionEvent(Object source, long startFramePosition,
			long stopFramePosition) {
		this(source,null,startFramePosition,stopFramePosition);
	}

	/**
     * @param source event source
     * @param audioSource playback audio source
     */
    public StartPlaybackActionEvent(Object source, AudioSource audioSource) {
       this(source,audioSource,0,AudioSystem.NOT_SPECIFIED);
    }
   
    /** 
     * Returns the start position in frames.
	 * @return start position
	 */
	public long getStartFramePosition() {
		return startFramePosition;
	}

	/**
	 * Returns the stop position in frames.
	 * @return stop position
	 */
	public long getStopFramePosition() {
		return stopFramePosition;
	}
	public AudioSource getPlaybackSource() {
		return playbackSource;
	}
	
    public void setPlaybackSource(AudioSource playbackSource) {
        this.playbackSource = playbackSource;
    }


}
