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

import ipsk.audio.arr.Selection;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.events.FramePositionChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.awt.TickProvider;
import ipsk.swing.JAutoScale;

import java.awt.Color;
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
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class TimeScaleMarker extends JComponent {

	public enum TriangleStyle {CENTER,LEFT,RIGHT}
	private MouseEvent pressedEvent = null;

	private MouseEvent dragStartEvent = null;

	private MouseEvent selEndMoveEvent;

	private MouseEvent selStartMoveEvent;

	private MouseEvent mouseOverResizeWest;

	private MouseEvent mouseOverResizeEast;

	// TODO !!!
	

	public TimeScaleMarker() {
		super();
		setBackground(Color.BLACK);
		setOpaque(false);
	}

	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Dimension scaleSize=super.getMinimumSize();
		int y=getHeight()-scaleSize.height;	
		int polyHeight=scaleSize.height/2;
//		
//		if(basicSampleUI.selection!=null){
//			g.setColor(Color.YELLOW);
//			long frameLeft=basicSampleUI.selection.getLeft();
////			int leftX=basicSampleUI.mapFrameToPixel(frameLeft);
////			leftX=basicSampleUI.viewSelection.getXLeft();
//			int leftX=mapFramePosToPixel(frameLeft);
//			Polygon poSelL=new Polygon(new int[]{leftX-polyHeight/2,leftX,leftX},new int[]{y+polyHeight,y+polyHeight,y},3);
//			long frameRight=basicSampleUI.selection.getRight();
//			int rightX=mapFramePosToPixel(frameRight);
//			Polygon poSelR=new Polygon(new int[]{rightX,rightX+polyHeight/2,rightX},new int[]{y+polyHeight,y+polyHeight,y},3);
//			
//			g.fillPolygon(poSelL);
//			g.fillPolygon(poSelR);
//		}
//		Polygon po=new Polygon(new int[]{pixelPosition-polyHeight/2,pixelPosition+polyHeight/2,pixelPosition},new int[]{y+polyHeight,y+polyHeight,y},3);
//		g.setColor(Color.RED);
//		g.fillPolygon(po);
//		
		
	}


}
