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

package ipsk.audio.ui.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.Icon;

/**
 * @author klausj
 *
 */
public class PlayIcon implements ConfigurableIcon {

	public enum Type {NORM,MOUSE_OVER,PRESSED};
	private Type type=Type.NORM;
	private int edgeLength;
	private int border=5;
	//private Polygon paintPoly;
	private int[] xCoords;
	private int[] yCoords;
	
	private boolean highLighted=false;
	private Polygon poly;
	
	public PlayIcon(){
		this(Type.NORM);
	}
	public PlayIcon(Type type){
		super();
		this.type=type;
		// use the default font size as orientation for the size
		edgeLength=Font.decode(null).getSize()*2;
//		int cornerLo=(edgeLength*2)/10;
//		int cornerHi=(edgeLength*8)/10;
//		xCoords=new int[]{cornerLo,cornerHi,cornerLo};
//		yCoords=new int[]{cornerLo,edgeLength/2,cornerHi};

//		xCoords=new int[]{2,edgeLength-2,2};
//		yCoords=new int[]{2,edgeLength/2,edgeLength-2};
//		
//		if(type.equals(Type.PRESSED)){
//			poly=new Polygon(new int[]{1,edgeLength-(border*2)+1,border*2+1},new int[]{border,edgeLength/2,edgeLength-border},3);
//		}else{
		poly=new Polygon(new int[]{border,edgeLength-border,border},new int[]{border,edgeLength/2,edgeLength-border},3);
//		}
		
	}
	
	public boolean isHighLighted() {
		return highLighted;
	}
	public void setHighLighted(boolean highLighted) {
		this.highLighted = highLighted;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconHeight()
	 */
	public int getIconHeight() {
		return edgeLength;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconWidth()
	 */
	public int getIconWidth() {
		return edgeLength;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		
		if(highLighted){
			g.setColor(Color.GREEN);
		}else{
			if(type.equals(Type.PRESSED)){
				g.setColor(Color.GREEN.darker());
			}else{
			g.setColor(Color.BLACK);
			}
		}
		if(type.equals(Type.PRESSED)){
			System.out.println("pressed");
		}
		g.fillRect(x, y, edgeLength, edgeLength);

		Polygon trPoly=new Polygon(poly.xpoints,poly.ypoints,poly.npoints);
		trPoly.translate(x, y);

			if(type.equals(Type.PRESSED)){
				g.setColor(Color.BLACK);
				g.fillPolygon(trPoly);
			}else{
				if(highLighted){
			g.setColor(Color.BLACK);
			g.drawPolygon(trPoly);
				}else{
					g.setColor(Color.GREEN);
					g.fillPolygon(trPoly);
				}
			}
		
		
	}

}
