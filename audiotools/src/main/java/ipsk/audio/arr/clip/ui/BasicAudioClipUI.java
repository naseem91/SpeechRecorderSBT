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

package ipsk.audio.arr.clip.ui;

import ips.media.MediaLengthUnit;
import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.arr.Marker;
import ipsk.audio.arr.Selection;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.audio.arr.clip.ui.AudioClipUIContainer.Plugin;
import ipsk.awt.TickProvider;
import ipsk.swing.action.tree.ActionTreeRoot;
import ipsk.text.TimeFormat;
import ipsk.util.LocalizableMessage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.Format;
import java.util.Iterator;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BasicAudioClipUI extends JPanel implements AudioClipUI {

	public final Color DEFAULT_TIME_GRID_COLOR = Color.YELLOW.darker();

	public class ViewSelection{
		
		
		private Selection selection;

		public ViewSelection() {
			this(new Selection());
		}

		public ViewSelection(Selection s) {
			super();
		setSelection(s);
			
		}

		public int getXLeft(){
		    return mapFrameToPixel(selection.getLeft());
		}
		public int getXRight(){
		    return mapFrameToPixel(selection.getRight());
		}
		
		public void setXEnd(int i) {
			selection.setEnd(mapPixelToFrame(i));
			
		}

		public int getXEnd() {
			return mapFrameToPixel(selection.getEnd());
		}

		public void setXStart(int i) {
			
			selection.setStart(mapPixelToFrame(i));
			
		}

		public int getXStart() {
			return mapFrameToPixel(selection.getStart());
		}

		

		/**
		 * Returns true if the given UI position is inside the selection.
		 * @param pixelPos UI position
		 * @return true if the UI position is inside the selection
		 */
		public boolean isInXSelection(int pixelPos) {
			int xStart=getXStart();
			int xEnd=getXEnd();
			
			boolean inSelection= ((pixelPos > xStart && pixelPos <= xEnd) || (pixelPos <= xStart && pixelPos > xEnd));
			//if (inSelection)System.out.println("Select pixelPos "+pixelPos+" xStart "+xStart);
			return inSelection;
		}

		
        public long getEnd() {
            return selection.getEnd();
        }
        public long getLength() {
            return selection.getLength();
        }
       
        public boolean isInSelection(long pos) {
            return selection.isInSelection(pos);
        }
        public void limitTo(long startLimit, long endLimit) {
            selection.limitTo(startLimit, endLimit);
            
        }
        public Selection getSelection() {
            return selection;
        }
        public void setSelection(Selection selection) {
            this.selection = selection;
           
        }
        public Marker getEndMarker() {
            return selection.getEndMarker();
        }
        public long getLeft() {
            return selection.getLeft();
        }
        public String getName() {
            return selection.getName();
        }
        public long getRight() {
            return selection.getRight();
        }
        public long getStart() {
            return selection.getStart();
        }
        public Marker getStartMarker() {
            return selection.getStartMarker();
        }
        public void setEnd(long l) {
            selection.setEnd(l);
        }
        public void setEndMarker(Marker endMarker) {
            selection.setEndMarker(endMarker);
        }
        public void setName(String name) {
            selection.setName(name);
        }
        public void setStart(long l) {
            selection.setStart(l);
        }
        public void setStartMarker(Marker startMarker) {
            selection.setStartMarker(startMarker);
        }
	}

	protected AudioSource audioSource = null;

	protected AudioClip audioSample = null;

	protected int channels;

	protected double lengthInSeconds;

	protected long length;


	protected AudioFormat audioFormat = null;

	protected int frameSize;

	

	protected double framesPerPixel;

	protected double pixelsPerFrame;

	protected Selection selection = null;

	protected ViewSelection viewSelection = null;

	protected Vector<ActionListener> listenerList;

	protected float sampleRate;
	
	protected MediaLengthUnit mediaLengthUnit=MediaLengthUnit.FRAMES;

	protected Format timeFormat=TimeFormat.FIXED_SECONDS_MS_TIME_FORMAT;
	
	protected TickProvider<Long> timeScaleTickProvider;
	
	protected boolean showTimeScaleGrid=false;
	//protected preferredHeight;

	public boolean isShowTimeScaleGrid() {
		return showTimeScaleGrid;
	}

	public void setShowTimeScaleGrid(boolean showTimeScaleGrid) {
		this.showTimeScaleGrid = showTimeScaleGrid;
	}

	public BasicAudioClipUI() {
		super();
		listenerList = new Vector<ActionListener>();
		
		length=ThreadSafeAudioSystem.NOT_SPECIFIED;
	}

	public BasicAudioClipUI(AudioClip audioSample)
			throws AudioFormatNotSupportedException, AudioSourceException {
		this();
		setAudioSample(audioSample);
	}

	

	protected long mapPixelToFrame(int pixelPosition) {
		return (int) ((double) pixelPosition * framesPerPixel);
	}

	protected int mapFrameToPixel(long framePosition) {
		return (int) ((double) framePosition / framesPerPixel);
	}
	
	
	protected double framesToTimeInSeconds(long framePosition){
	    return framePosition /sampleRate;
	}

	private void reset() {
		audioFormat = null;
		length = ThreadSafeAudioSystem.NOT_SPECIFIED;
		lengthInSeconds = 0;
		
		audioSource = null;
		//resize();
	}

	public LocalizableMessage getLocalizableName(){
		return new LocalizableMessage(getName());
	}
	public void setAudioSample(AudioClip audioSample){
		if (audioSample == this.audioSample)
			return;
		if (this.audioSample != null)
			this.audioSample.removeAudioSampleListener(this);

		this.audioSample = audioSample;
		if (audioSample != null) {
			audioSample.addAudioSampleListener(this);
//			try {
//                setAudioSource(audioSample.getAudioSource());
//            } catch (AudioSourceException e) {
//                JOptionPane.showMessageDialog(this, "Cannot set audio source: \n"
//                        + e.getLocalizedMessage(), "Audio source error",
//                        JOptionPane.ERROR_MESSAGE);
//            }
			audioClipChanged(new AudioSourceChangedEvent(this,audioSample.getAudioSource()));
			
		} else {
			reset();
			try {
                setAudioSource(null);
            } catch (AudioSourceException e) {
                JOptionPane.showMessageDialog(this, "Cannot reset audio source: \n"
                        + e.getLocalizedMessage(), "Audio source error",
                        JOptionPane.ERROR_MESSAGE);
            }
            
		}
		
		setSelection();
	
	}

	protected void setAudioSource(AudioSource audioSource)
			throws AudioSourceException {
		close();
		this.audioSource = audioSource;	
		if (audioSource != null) {
			
			AudioInputStream ais = audioSource.getAudioInputStream();
			audioFormat = ais.getFormat();
			channels = audioFormat.getChannels();
			frameSize = audioFormat.getFrameSize();
			sampleRate = audioFormat.getSampleRate();
			long frameLength = ais.getFrameLength();

			try {
				ais.close();
			} catch (IOException e) {
				throw new AudioSourceException(e);
			}
			setProcessedFrameLength(frameLength);
		
		} else {
			reset();
		}
		//viewSelection=null;
        setSelection();
		revalidate();
		repaint();
	}
	public void setVisible(boolean visible){
	    super.setVisible(visible);

	    JComponent[] yScales=getYScales();
	    if(yScales!=null){
	        for(Component sc:yScales){
	            if(sc!=null){
	                sc.setVisible(visible);
	            }
	        }
	    }
//	    revalidate();
//	    repaint();

	    return;
	}
	
	public void doLayout(){
	    resized();
	}
	
	private void resized(){
	    if (length == ThreadSafeAudioSystem.NOT_SPECIFIED) {
			lengthInSeconds = 0;

		} else {
			lengthInSeconds = length / audioFormat.getFrameRate();
			int width=getWidth();
			if (width > 0 && length >0) {
				framesPerPixel = (double) length / (double) width;
				pixelsPerFrame = (double) width / (double) length;
			}else{
				framesPerPixel = 0.0;
				pixelsPerFrame = 0.0;
			}
			
//			if (1/framesPerPixel != pixelsPerFrame){
//				System.out.println("Ungleich !"+(1/framesPerPixel)+" "+pixelsPerFrame);
//			}
		}
		
	}

	public void setProcessedFrameLength(long frameLength) {

		length = frameLength;
		if(length==0){
			setSize(new Dimension(0,getHeight()));
		}
		resized();
	}

//	protected void resize() {
//		if (length == AudioSystem.NOT_SPECIFIED) {
//			lengthInSeconds = 0;
//
//		} else {
//			lengthInSeconds = length / audioFormat.getFrameRate();
//			//setSize(new Dimension((int)(lengthInSeconds * xZoom),getHeight()));
//			if (getWidth() > 0) {
//				framesPerPixel = (double) length / (double) getWidth();
//
//			}
//			if (length > 0) {
//				pixelsPerFrame = (double) getWidth() / (double) length;
//			}
//		}
//		if (viewSelection != null)
//			viewSelection.remap();
//		//setSize(width, height);
//		//setMinimumSize(getSize());
//		//setPreferredSize(getSize());
//	}

	public AudioClip getAudioSample() {
		return audioSample;
	}

	public double getLengthInSeconds() {
		return lengthInSeconds;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.arr.AudioClipListener#sourceChanged(java.lang.Object,
	 *      ipsk.audio.AudioSource)
	 */
	public void audioClipChanged(AudioClipChangedEvent event) {
		
	    if (event instanceof AudioSourceChangedEvent){
	        AudioSourceChangedEvent asEvent=(AudioSourceChangedEvent)event;
		try {
			setAudioSource(asEvent.getAudioSource());
		} catch (AudioSourceException e) {
			// Cannot handle this
		}
	    }else if(event instanceof SelectionChangedEvent){
	        setSelection();		
	    }
	}

	protected void setSelection(){
	    selection=null;
	    if (audioSample!=null)selection=audioSample.getSelection();
        
		if (selection == null) {
			viewSelection = null;
		} else {
			viewSelection = new ViewSelection(selection);
		}
		repaint();
	}
	
	

	public synchronized void addActionListener(ActionListener acl) {
		if (acl != null && !listenerList.contains(acl)) {
			listenerList.addElement(acl);
		}
	}

	public synchronized void removeActionListener(ActionListener acl) {
		if (acl != null) {
			listenerList.removeElement(acl);
		}
	}

	protected synchronized void fireActionEvent(ActionEvent ae) {
		Iterator<ActionListener> it = listenerList.iterator();
		while (it.hasNext()) {
			ActionListener listener = (ActionListener) it.next();
			listener.actionPerformed(ae);
		}
	}


    public Format getTimeFormat() {
        return timeFormat;
    }
    
    public void setTimeFormat(Format timeFormat) {
        this.timeFormat = timeFormat;
        repaint();
    }
    
    protected String formatPosition(long framePosition){
        if(MediaLengthUnit.TIME.equals(mediaLengthUnit)){

            Double timePostionSeconds = framesToTimeInSeconds(framePosition);

            if (timeFormat == null) {
                return timePostionSeconds.toString();
            } else {
                return timeFormat.format(timePostionSeconds);
            }
        }else{
            // frames
            return Long.toString(framePosition);
        }
    }

	public void setTimeScaleTickProvider(TickProvider<Long> timeScaleTickProvider) {
		this.timeScaleTickProvider=timeScaleTickProvider;	
	}

//	public JComponent getControlJComponent() {
//		return null;
//	}

	public JMenu[] getJMenus() {
		return new JMenu[0];
	}
	 
    public void close(){
 	   // does nothing 
    	// threaded workers have to overwrite
    }

	/* (non-Javadoc)
	 * @see ipsk.audio.arr.clip.ui.AudioClipUI#getYScales()
	 */
	public JComponent[] getYScales() {
		return new JComponent[0];
	}

    /* (non-Javadoc)
     * @see ipsk.audio.arr.clip.ui.AudioClipUI#setMediaLengthUnit(ips.media.MediaLengthUnit)
     */
    public void setMediaLengthUnit(MediaLengthUnit mediaLengthUnit) {
        this.mediaLengthUnit=mediaLengthUnit;
        repaint();
    }

    /* (non-Javadoc)
     * @see ips.incubator.awt.action.ActionProvider#getActionTree()
     */
    public ActionTreeRoot getActionTreeRoot() {
        return null;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.arr.clip.ui.AudioClipUI#showJControlDialog(java.awt.Component)
     */
    public void showJControlDialog(Component parentComponent) {
       // does nothing
    }

    /* (non-Javadoc)
     * @see ipsk.audio.arr.clip.ui.AudioClipUI#hasControlDialog()
     */
    public boolean hasControlDialog() {
        return false;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.arr.clip.ui.AudioClipUI#isPreferredFixedHeight()
     */
    public boolean isPreferredFixedHeight() {
        return false;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.arr.clip.ui.AudioClipUI#asComponent()
     */
    public Component asComponent() {
        return this;
    }
}
