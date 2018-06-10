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
 * Date  : Aug 16, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.arr.clip;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.BasicAudioSource;
import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.arr.Selection;
import ipsk.audio.arr.SelectionGroup;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.events.FramePositionChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.audio.dsp.AudioClipDSPInfo;
import ipsk.audio.dsp.FloatAudioInputStream;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;


/**
 * This class is a data model for audio stream sources. It represents a source for the
 * audio stream, a (playback) frame position and a selection on the stream.
 * {@link AudioClipListener}s are notified on data changes.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class AudioClip extends BasicAudioSource implements AudioSource {

    private AudioSource audioSource;
    private Selection selection;
  
    private Hashtable<String, SelectionGroup> selectionGroups;
    public final static String PLAYBACK="Playback";
    public final static String EDIT="Edit";
    protected AudioClipDSPInfo clipDSPInfo;
    private long framePosition;
    
    private Vector<AudioClipListener> listenerList;

    /**
     * Create new empty sample.
     */
    public AudioClip() {
        super();
        audioSource = null;
        selectionGroups = new Hashtable<String, SelectionGroup>();
        
        selection=null;
        framePosition = 0;
        listenerList = new Vector<AudioClipListener>();
        clipDSPInfo=null;
        
    }

    /**
     * Create new audio sample.
     * 
     * @param audioSource
     *            the source for the audio stream
     */
    public AudioClip(AudioSource audioSource) {
        this();
        this.audioSource = audioSource;
    }

    /**
     * Get the audio source
     * 
     * @return audio source
     */
    public AudioSource getAudioSource() {
        return audioSource;
    }

    /**
     * Get frame position.
     * 
     * @return frame position
     */
    public long getFramePosition() {
        return framePosition;
    }

  
    
    
    
    
    /**
     * Set new audio source.
     * 
     * @param source
     *            new audio source
     */
	public void setAudioSource(AudioSource source) {
        audioSource = source;
		selection=null;
		//framePosition = AudioSystem.NOT_SPECIFIED;
		framePosition=0;
		frameLengthObj=null;
		
        fireAudioSampleChanged(new AudioSourceChangedEvent(this,audioSource));
    }

    /**
     * Set frame position.
     * 
     * @param l
     *            new frame position
     */
	public void setFramePosition(long l) {
		long oldFramePosition=framePosition;
        framePosition = l;
        if(framePosition!=oldFramePosition){
        	fireAudioSampleChanged(new FramePositionChangedEvent(this,framePosition));
        }
    }



    /**
     * Add listener.
     * 
     * @param acl
     *            new listener
     */
    public synchronized void addAudioSampleListener(AudioClipListener acl) {
        if (acl != null && !listenerList.contains(acl)) {
            listenerList.addElement(acl);
        }
    }

    /**
     * Remove listener.
     * 
     * @param acl
     *            listener to remove
     */
    public synchronized void removeAudioSampleListener(AudioClipListener acl) {
        if (acl != null) {
            listenerList.removeElement(acl);
        }
    }

    protected void fireAudioSampleChanged(AudioClipChangedEvent event) {
		synchronized (listenerList) {
			for (AudioClipListener listener : listenerList) {
				listener.audioClipChanged(event);
			}
		}
	}

   

    /*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioSource#getAudioInputStream()
	 */
    public AudioInputStream getAudioInputStream() throws AudioSourceException {
        if (audioSource == null)
            return null;
        AudioInputStream ais= audioSource.getAudioInputStream();
        //clipDSPInfo.setAudioFormat(ais.getFormat());
        //clipDSPInfo.setFrameLength(ais.getFrameLength());
        return ais;
    }
    
    /**
     * Returns a float value audio stream.
     * The float values a normalized to the scale -1 from to +1.
     * @return the converted float audio stream or null if no audio source set
     */
    public FloatAudioInputStream getFloatAudioInputStream() throws AudioSourceException{
        if (audioSource == null)
            return null;
        AudioInputStream ais= audioSource.getAudioInputStream();
        AudioInputStream pcmStream=null;
        FloatAudioInputStream fais;
        try {
        	if(ais.getFormat().getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)){
        		pcmStream=ais;
        	}else{
        		// Try to decode to PCM
        		pcmStream=ThreadSafeAudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, ais);
        	}
            fais = new FloatAudioInputStream(pcmStream);
            fais.setUseReadOnSkipException(true);
        } catch (AudioFormatNotSupportedException e) {
            throw new AudioSourceException(e);
        }
        return fais;
    }

   
    public Selection getSelection() {
        return selection;
    }
    public void setSelection(Selection selection) {
        this.selection = selection;
        fireAudioSampleChanged(new SelectionChangedEvent(this,selection));
    }
    
    
    public void addSelectionGroup(SelectionGroup sg){
        selectionGroups.put(sg.getName(),sg);
    }
    public void removeSelectionGroup(SelectionGroup sg){
        selectionGroups.remove(sg);
    }
    
    public SelectionGroup getSelectionGroup(String name){
        return (SelectionGroup)selectionGroups.get(name);
    }
    
    public AudioClipDSPInfo getClipDSPInfo() {
        return clipDSPInfo;
    }
    public void setClipDSPInfo(AudioClipDSPInfo info) {
        this.clipDSPInfo = info;
    }

   
}
