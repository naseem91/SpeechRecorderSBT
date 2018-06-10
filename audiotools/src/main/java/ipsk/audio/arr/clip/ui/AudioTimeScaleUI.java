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
 * Date  : Oct 7, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.arr.clip.ui;

import ips.media.MediaLengthUnit;
import ipsk.audio.arr.Selection;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.events.FramePositionChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.awt.TickProvider;
import ipsk.swing.JAutoScale;
import ipsk.swing.action.tree.ActionTreeRoot;
import ipsk.util.LocalizableMessage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.Format;

import javax.swing.JComponent;
import javax.swing.JMenu;


/**
 * The default time scale for an audio clip UI container.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class AudioTimeScaleUI extends JAutoScale implements AudioClipUI {

	private BasicAudioClipUI basicSampleUI;

	
//	private MouseEvent pressedEvent = null;
//
//	private MouseEvent dragStartEvent = null;
//
//	private MouseEvent selEndMoveEvent;
//
//	private MouseEvent selStartMoveEvent;
//
//	private MouseEvent mouseOverResizeWest;
//
//	private MouseEvent mouseOverResizeEast;

	private int pixelPosition;
	
	//private Selection selection;


	public AudioTimeScaleUI() {
		super();
		setBackground(Color.BLACK);
		setOpaque(false);
		// setLabelFormat(new MediaTimeFormat());
		// setLabelFormat(new
		// TimeFormat(TimeFormat.SECONDS,"################0.000"));
		setForeground(Color.YELLOW.darker());
		// setBackground(Color.BLACK);
		basicSampleUI = new BasicAudioClipUI();
	}

//	public void setCurosr(Cursor c) {
//		//System.out.println("Cursor set");
//	}
	
	public String getName(){
		return "Audio time scale";
	}
	
	public LocalizableMessage getLocalizableName(){
		return new LocalizableMessage(getName());
	}

	public void setTimeFormat(Format timeFormat) {
		basicSampleUI.setTimeFormat(timeFormat);
		setLabelFormat(timeFormat);
		rescale();
	}

	private void rescale() {
	    if(MediaLengthUnit.TIME.equals(basicSampleUI.mediaLengthUnit)){
	        setLabelFormat(basicSampleUI.getTimeFormat());
	        setScaleEnd((long) (basicSampleUI.lengthInSeconds * 1000000000));
	    }else{
	        setLabelFormat(null);
			setScaleEnd(basicSampleUI.length);
		}
	}
	

	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Dimension scaleSize=super.getMinimumSize();
		int y=getHeight()-scaleSize.height;	
		int polyHeight=scaleSize.height/2;
		
		if(basicSampleUI.selection!=null){
			g.setColor(Color.YELLOW);
			long frameLeft=basicSampleUI.selection.getLeft();
//			int leftX=basicSampleUI.mapFrameToPixel(frameLeft);
//			leftX=basicSampleUI.viewSelection.getXLeft();
			int leftX=mapFramePosToPixel(frameLeft);
			Polygon poSelL=new Polygon(new int[]{leftX-polyHeight/2,leftX,leftX},new int[]{y+polyHeight,y+polyHeight,y},3);
			long frameRight=basicSampleUI.selection.getRight();
			int rightX=mapFramePosToPixel(frameRight);
			Polygon poSelR=new Polygon(new int[]{rightX,rightX+polyHeight/2,rightX},new int[]{y+polyHeight,y+polyHeight,y},3);
			
			g.fillPolygon(poSelL);
			g.fillPolygon(poSelR);
		}
		Polygon po=new Polygon(new int[]{pixelPosition-polyHeight/2,pixelPosition+polyHeight/2,pixelPosition},new int[]{y+polyHeight,y+polyHeight,y},3);
		g.setColor(Color.RED);
		g.fillPolygon(po);
		
		
	}

	private int mapFramePosToPixel(long framePos){
		double pixelsPerFrame = 0;
		if (basicSampleUI.length > 0) {
			pixelsPerFrame = (double) getWidth()
					/ (double) basicSampleUI.length;
		}
		return (int) ((double) framePos * pixelsPerFrame);
	}
	
	private void setFramePosition(long position) {
		// framePosition = position;
		int oldPixelPosition = pixelPosition;
		double pixelsPerFrame = 0;
		if (basicSampleUI.length > 0) {
			pixelsPerFrame = (double) getWidth()
					/ (double) basicSampleUI.length;
		}
		pixelPosition = (int) ((double) position * pixelsPerFrame);

		Dimension scaleSize=super.getMinimumSize();
		int h=scaleSize.height;	
		int y=getHeight()-h;
		int polywidth=scaleSize.height/2;
		if (EventQueue.isDispatchThread()) {
			paintImmediately(oldPixelPosition-polywidth/2, y, polywidth, h);
			paintImmediately(pixelPosition-polywidth/2, y, polywidth, h);
		} else {
			repaint(oldPixelPosition-polywidth/2, y, polywidth, h);
			repaint(pixelPosition-polywidth/2, y, polywidth, h);
		}

	}
	
//	private void setSelection(Selection s) {
//		this.selection=s;
//		if (EventQueue.isDispatchThread()) {
//		
//		} else {
//			
//		}
//		repaint();
//	}

	
//	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		// paint frame position marker
//		g.setColor(Color.BLUE);
////g.drawLine(pixelPosition, 0, pixelPosition, getSize().height);
//
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.arr.ui.AudioClipUI#setAudioSample(ipsk.audio.arr.AudioClip)
	 */
	public void setAudioSample(AudioClip audiosample) {
		basicSampleUI.setAudioSample(audiosample);
		if (audiosample != null){
			audiosample.addAudioSampleListener(this);
		}
		rescale();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.arr.AudioClipListener#sourceChanged(java.lang.Object,
	 *      ipsk.audio.AudioSource)
	 */
	public void audioClipChanged(AudioClipChangedEvent event) {

		basicSampleUI.audioClipChanged(event);
		if (event instanceof FramePositionChangedEvent) {
			setFramePosition(((FramePositionChangedEvent) event).getPosition());
		} else if (event instanceof SelectionChangedEvent) {
			//setSelection(((SelectionChangedEvent) event).getSelection());
			SelectionChangedEvent sEv=(SelectionChangedEvent)event;
			Selection s=sEv.getSelection();
			
		}else if (event instanceof AudioSourceChangedEvent) {
			rescale();
			//setAudioSample(basicSampleUI.getAudioSample());
		}else if(event instanceof SelectionChangedEvent){
			
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.arr.ui.AudioClipUI#addActionListener(java.awt.event.ActionListener)
	 */
	public void addActionListener(ActionListener containerUI) {
		// currently no actions

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.arr.ui.AudioClipUI#removeActionListener(java.awt.event.ActionListener)
	 */
	public void removeActionListener(ActionListener containerUI) {
		// currently no actions

	}

    public void setTimeScaleTickProvider(TickProvider timeScaleTickProvider) {
        // TODO Auto-generated method stub
        
    }
    
	public JComponent getControlJComponent() {
		// No control
		return null;
	}

	public JMenu[] getJMenus() {
		
		return new JMenu[0];
	}

	public void close(){
//	   open=false;
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
        basicSampleUI.setMediaLengthUnit(mediaLengthUnit);
        rescale();
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
        // no control 
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
       
        return true;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.arr.clip.ui.AudioClipUI#asComponent()
     */
    public Component asComponent() {
        return this;
    }

}
