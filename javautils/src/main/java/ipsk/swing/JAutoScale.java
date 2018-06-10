//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.swing;

import ipsk.awt.TickProvider;
import ipsk.awt.GridTick;
import ipsk.awt.JScale;
import ipsk.awt.JScale.Orientation;
import ipsk.text.MediaTimeFormat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Graphical scale with automatic labeling.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class JAutoScale extends JScale<Long> implements TickProvider<Long>{

	private final boolean DEBUG = false;

	private static final double DEFAULT_BASE = 10;

	private static final int[] DEFAULT_DIVIDERS = { 2, 5 };

	private static final double DEFAULT_AXIS_PADDING_FACTOR = 1.2;

	private static final double DEFAULT_NOAXIS_PADDING_FACTOR = 1.1;

	// The only implementation in 1.5.0 Java API is Dimension which stores data
	// as int
	public class Size extends Dimension2D {

		private double width;

		private double height;

		public Size(double width, double height) {
			this.width = width;
			this.height = height;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}

		public void setSize(double arg0, double arg1) {
			width = arg0;
			height = arg1;
		}

		public Object clone() {
			return new Size(width, height);
		}

	}

	private int width;

	private int height;

	private int minWidth = 0;

	private int minHeight = 0;

	private double horizontalPaddingFactor;

	private double verticalPaddingFactor;

	private double paddedLabelWidth;

	private double paddedLabelHeight;

	private Format labelFormat;

	private int lineLength;

	private int halfLineLength;

	public static final int HORIZONTAL = 0;

	public static final int VERTICAL = 1;

	private long scaleBegin;

	private long scaleEnd;

	private long delta;

	private double base;

	private int[] dividers;

	private Dimension maxLabelSize=new Dimension();

	private int maxNumTicks;

	private long majTick;

	private long firstTickValue;

//	private volatile  Dimension calcForSize = null;

	public JAutoScale() {
		this(Orientation.SOUTH, 0, 0);
	}

	/**
	 * Create scale.
	 * 
	 * @param orientation
	 * @param from start pint
	 * @param to end point
	 */
	public JAutoScale(Orientation orientation, long from, long to) {
		this(orientation, 0, from, to);
	}

	/**
	 * Create scale.
	 * 
	 * @param orientation
	 * @param length
	 *            in pixels
	 * @param from start pint
	 * @param to end point
	 */
	public JAutoScale(Orientation orientation, int length, long from, long to) {
		super(orientation);
		this.scaleBegin = from;
		this.scaleEnd = to;
		delta = to - from;
		base = DEFAULT_BASE;
		dividers = DEFAULT_DIVIDERS;

		if (Orientation.SOUTH.equals(orientation)) {
			horizontalPaddingFactor = DEFAULT_AXIS_PADDING_FACTOR;
			verticalPaddingFactor = DEFAULT_NOAXIS_PADDING_FACTOR;
			minWidth = length;
		} else if(Orientation.WEST.equals(orientation)){
			horizontalPaddingFactor = DEFAULT_NOAXIS_PADDING_FACTOR;
			verticalPaddingFactor = DEFAULT_AXIS_PADDING_FACTOR;
			minHeight = length;
		}

	}

	public double getBase() {
		return base;
	}

	public void setBase(double base) {
		this.base = base;
		rescale();
	}

	private String format(long tickValue) {
		Long longObj = new Long(tickValue);
		if (labelFormat == null) {
			return longObj.toString();
		} else {
			return labelFormat.format(longObj);
		}
	}

	private void computeTicks() {
		Dimension d = getSize();
		// System.out.println("Compute size: "+d);
		if (Orientation.SOUTH.equals(orientation)) {
		    maxNumTicks = (int) (d.width / paddedLabelWidth);
		} else if(Orientation.WEST.equals(orientation)){
		    maxNumTicks = (int) (d.height / paddedLabelHeight);
		}
		double minTick = (double) delta / (double) maxNumTicks;
		double minTickAbs=Math.abs(minTick);
		if (DEBUG)
			System.out.println("maxNumTicks: " + maxNumTicks + " minTick: "
					+ minTick);

		double minTickPow = Math.log(minTickAbs) / Math.log(base);
		int tickPow = (int) Math.ceil(minTickPow);
		long majTickAbs = (long) Math.pow(base, tickPow);
		if (majTickAbs < 1) {
			majTickAbs = 1;
		}
		for (int i = 0; i < dividers.length; i++) {
			if (majTickAbs / dividers[i] > minTickAbs) {
				majTickAbs = majTickAbs / dividers[i];
				// TODO break ??
			}
		}
		if(delta>=0){
			majTick=majTickAbs;
		}else{
			majTick=-majTickAbs;
		}
		if (DEBUG)
			System.out.println("Chosen majTick: " + majTick);
		// long firstTickValue;
		if (majTickAbs > 0) {
			long firstTickUnits=scaleBegin/majTickAbs;
			if(delta<0){
				firstTickUnits++;
			}
//			firstTickValue = ((scaleBegin / majTick)) * majTick;
			firstTickValue=firstTickUnits*majTickAbs;
		} else {
			firstTickValue = scaleBegin;
		}
		if (DEBUG)
			System.out.println("max num ticks: " + maxNumTicks + " minTick: "
					+ minTick + " tickPow: " + tickPow + " tick: " + majTick
					// + " numTicks: "
					// + numTicks
					+ " Size: " + d);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (isEnabled()) {
			g.setColor(getForeground());
		} else {
			g.setColor(getBackground().darker());
		}
		Rectangle a = g.getClipBounds();
//		d = getSize();
//		// System.out.println("Paint size: "+d);
//		if (!d.equals(calcForSize)) {
//			rescale();
//
//		}
		width = getWidth();
		height = getHeight();
		// calcMaxLabelSize();

		if (width == 0 || height == 0)
			return;
		int lineX1=0;
		int lineX2=0;
		int lineY1=0;
		int lineY2=0;

		if (Orientation.SOUTH.equals(orientation)) {
			lineX1 = a.x;
			lineX2 = a.x + a.width;
			lineY1 = height - getMinimumSize().height;
			lineY2 = lineY1;
		   } else if(Orientation.WEST.equals(orientation)){

			lineX1 = getMinimumSize().width-1;
			lineX2 = lineX1;
			lineY1 = a.y;
			lineY2 = a.y+a.height;
		}

		g.setColor(getBackground());
		g.fillRect(lineX1, lineY1, width, height);
		g.setColor(getForeground());
		g.drawLine(lineX1, lineY1, lineX2, lineY2);

		// TODO paint minor ticks
		// int ticks = (int) (width / delta);
		// Tick[] majTicks = new Tick[ticks];
		// for (int t = 0; t < ticks; t++) {
		//
		// if (orientation == HORIZONTAL) {
		// majTicks[t] =
		// new Tick(
		// new Point(
		// (int) (((tickValue - from) * width) / delta),
		// lineY1),
		// tickValue,
		// true);
		//						
		//
		// } else {
		// majTicks[t] =
		// new Tick(
		// new Point(
		// (int) (((tickValue - from) * height) / delta),
		// lineX1),
		// tickValue,
		// true);
		//
		// }
		//
		// tickValue += majTick;
		// }

//		double dDelta = (double) delta;

		if (Orientation.SOUTH.equals(orientation)) {
			GridTick<Long>[] ticks = getScaleTicks(a.x, a.x + a.width);
			for (GridTick<Long> gt : ticks) {
				int pos = gt.getPosition();
				g.drawString(format((Long) gt.getTickValue()), pos + 1, lineY1
						+ (int) maxLabelSize.height + halfLineLength);
				// g.drawLine(pos, lineY1, pos, lineY1 + lineLength);
				// System.out.println("Drawline: "+pos+" "+0+" "+ pos+" "+(
				// lineY1 + lineLength));
				g.drawLine(pos, 0, pos, lineY1 + lineLength);
				int halfPos = pos + ((gt.getNextPosition() - pos) / 2);
				g.drawLine(halfPos, lineY1, halfPos, lineY1 + halfLineLength);
			}

		   } else if(Orientation.WEST.equals(orientation)){
			GridTick<Long>[] ticks = getScaleTicks( a.y, a.y + a.height);
			for (GridTick<Long> gt : ticks) {
				int pos = gt.getPosition();
				g.drawString(format((Long) gt.getTickValue()), lineX1
						- (int) maxLabelSize.width - halfLineLength, pos
						+ (int) maxLabelSize.height);
				g.drawLine(lineX1, pos, lineX1 - lineLength, pos);
				int halfPos = pos + ((gt.getNextPosition() - pos) / 2);
				g.drawLine(lineX1, halfPos, lineX1 - halfLineLength, halfPos);
			}
		}
	}

	public GridTick<Long>[] getScaleTicks( int fromPixel, int toPixel) {
		ArrayList<GridTick<Long>> gridTicks = new ArrayList<GridTick<Long>>();
		double dDelta = (double) delta;
		width = getWidth();
		height = getHeight();
		//Scale scale = new Scale();
		if (Orientation.SOUTH.equals(orientation)) {
			
			double dWidth = (double) width;
			double dAx = (double) fromPixel;
			long areaBegValue = scaleBegin+(long) ((dAx * dDelta) / dWidth);
			long tickValue = 0;
			if (majTick != 0) {
				tickValue = ((areaBegValue - firstTickValue) / majTick)
						* majTick + firstTickValue;

				long areaEndValue = scaleBegin+(long) (((dAx + (double) (toPixel - fromPixel)) * dDelta) / dWidth);

				do {
//					int pos = (int) (((double) (tickValue - scaleBegin) * dWidth) / dDelta);
					int pos = (int) (((double) (tickValue - scaleBegin) / dDelta)*dWidth);
					int nextPos = (int) (((double) ((tickValue + majTick) - scaleBegin) / dDelta)*dWidth);
					GridTick<Long> newTick = new GridTick<Long>( pos, tickValue);
					newTick.setNextPosition(nextPos);
					gridTicks.add(newTick);

					tickValue += majTick;
				} while ((majTick>0 && tickValue <= areaEndValue)|| (majTick<0 && tickValue>=areaEndValue));
			}
		   } else if(Orientation.WEST.equals(orientation)){
			double dHeight = (double) height;
			double dAy = (double) fromPixel;
			long areaBegValue = scaleBegin+(long) ((dAy * dDelta) / dHeight);
			long tickValue = 0;
			if (majTick != 0) {
				tickValue =

				((areaBegValue - firstTickValue) / majTick) * majTick
						+ firstTickValue;
				long areaEndValue = scaleBegin+(long) (((dAy + (double) (toPixel - fromPixel)) * dDelta) / dHeight);
				do {

					int pos = (int) (((tickValue - scaleBegin)/dDelta)* dHeight );
					int nextPos = (int) ((((double) (tickValue + majTick) - scaleBegin)/ dDelta)*dHeight);
					GridTick<Long> newTick = new GridTick<Long>( pos, tickValue);
					newTick.setNextPosition(nextPos);
					gridTicks.add(newTick);
					tickValue += majTick;
				} while ((majTick>0 && tickValue <= areaEndValue)||(majTick<0 && tickValue >= areaEndValue) );
			}
		}

		GridTick<Long>[] emptyArray=new GridTick[0];
		GridTick<Long>[] array = gridTicks.toArray(emptyArray);
        return array;
	}

//	public GridTick[] getYGridTicks(int type, int from, int to) {
//		ArrayList<GridTick> gridTicks = new ArrayList<GridTick>();
//
//		double dDelta = (double) delta;
//		width = getWidth();
//		height = getHeight();
//		Scale scale = new Scale();
//		if (orientation == VERTICAL) {
//			scale.setOrientation(Scale.Y_SCALE);
//			double dHeight = (double) height;
//			double dAy = (double) from;
//			long areaBegValue = (long) ((dAy * dDelta) / dHeight);
//			long tickValue = 0;
//			if (majTick > 0) {
//				tickValue =
//
//				((areaBegValue - firstTickValue) / majTick) * majTick
//						+ firstTickValue;
//				long areaEndValue = (long) (((dAy + (double) (to - from)) * dDelta) / dHeight);
//				do {
//
//					int pos = (int) (((tickValue - scaleBegin) * height) / delta);
//					int nextPos = (int) ((((double) (tickValue + majTick) - scaleBegin) * dHeight) / dDelta);
//					GridTick newTick = new GridTick(scale, pos, tickValue);
//					newTick.setNextPosition(nextPos);
//					gridTicks.add(newTick);
//					tickValue += majTick;
//				} while (tickValue <= areaEndValue);
//			}
//			return gridTicks.toArray(new GridTick[0]);
//		} else {
//			return null;
//		}
//	}

	/**
	 * @return vertical padding factor
	 */
	public double getVerticalPaddingFactor() {
		return verticalPaddingFactor;
	}

	/**
	 * @param d vertical padding factor
	 */
	public void setVerticalPaddingFactor(double d) {
		verticalPaddingFactor = d;
		rescale();
	}

	/**
	 * @return horizontal padding factor
	 */
	public double getHorizontalPaddingFactor() {
		return horizontalPaddingFactor;
	}

	/**
	 * @param d horizontla padding factor
	 */
	public void setHorizontalPaddingFactor(double d) {
		horizontalPaddingFactor = d;
		rescale();
	}

	protected void calcMaxLabelSize() {
	    Font f=getFont();
	    if(f!=null){
		FontMetrics fontMetrics = getFontMetrics(f);
		Rectangle2D fromBounds = fontMetrics.getStringBounds(
				format(scaleBegin), getGraphics());
		Rectangle2D toBounds = fontMetrics.getStringBounds(format(scaleEnd),
				getGraphics());
		double fromWidth = fromBounds.getWidth();
		double toWidth = toBounds.getWidth();
		double fromHeight = fromBounds.getHeight();
		double toHeight = toBounds.getHeight();
		double maxLabelWidth = Math.max(fromWidth, toWidth);
		// fixed spacing for now
		paddedLabelWidth = maxLabelWidth * horizontalPaddingFactor;
		double maxLabelHeight = Math.max(fromHeight, toHeight);
		paddedLabelHeight = maxLabelHeight * verticalPaddingFactor;
		Dimension d = new Dimension();
		d.setSize(maxLabelWidth, maxLabelHeight);
		maxLabelSize = d;

	}
	}

	public Dimension getMinimumSize() {
		// fixed spacing for now
		calcMaxLabelSize();

		if (Orientation.SOUTH.equals(orientation)) {
			lineLength = (int) maxLabelSize.height;
			halfLineLength = lineLength / 2;
			minHeight = (int) paddedLabelHeight + 2 + halfLineLength;
			minWidth = 0;
		   } else if(Orientation.WEST.equals(orientation)){
			lineLength = (int) maxLabelSize.height;
			halfLineLength = lineLength / 2;
			minWidth = (int) paddedLabelWidth + 2 + halfLineLength;
			minHeight = 0;
		}
		return new Dimension(minWidth, minHeight);
	}

	// private int getScaleBreadth(){
	// Dimension minSize=
	// if (orientation == HORIZONTAL) {
	//			
	// } else {
	// lineLength = (int) maxLabelWidth;
	// halfLineLength = lineLength / 2;
	// minWidth = (int) paddedLabelWidth + 2 + halfLineLength;
	// }
	// return 0;
	// }

	public Dimension getPreferredSize() {
		return getMinimumSize();
	}

	
	/**
	 * @return label format
	 */
	public Format getLabelFormat() {
		return labelFormat;
	}

	/**
	 * @param format label format
	 */
	public void setLabelFormat(Format format) {
		labelFormat = format;
		rescale();
	}

	public void setFont(Font f) {
		super.setFont(f);
		rescale();
	}

	private void rescale() {
//		doLayout();
		revalidate();
		repaint();

	}

	/**
	 * @return Returns the scaleBegin.
	 */
	public long getScaleBegin() {
		return scaleBegin;
	}

	/**
	 * @param scaleBegin
	 *            The scaleBegin to set.
	 */
	public void setScaleBegin(long scaleBegin) {
		this.scaleBegin = scaleBegin;
		rescale();
	}

	/**
	 * @return Returns the scaleEnd.
	 */
	public long getScaleEnd() {
		return scaleEnd;
	}

	/**
	 * @param scaleEnd
	 *            The scaleEnd to set.
	 */
	public void setScaleEnd(long scaleEnd) {
		this.scaleEnd = scaleEnd;
		rescale();
	}



	/**
	 * @param orientation
	 *            The orientation to set.
	 */
	public synchronized void setOrientation(Orientation orientation) {

	    if (this.orientation.equals(orientation)){
	        return;
	    }else{
	        super.setOrientation(orientation);

	        int tmp1 = minWidth;
	        minWidth = minHeight;
	        minHeight = tmp1;
	        double tmp2 = horizontalPaddingFactor;
	        horizontalPaddingFactor = verticalPaddingFactor;
	        verticalPaddingFactor = tmp2;
	        rescale();
	    }
	}


	public void doLayout(){
	      delta = scaleEnd - scaleBegin;
	      calcMaxLabelSize();
	      computeTicks();
	}

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
//				JAutoScale jasHor = new JAutoScale(HORIZONTAL, 6000000,
//						100000000000L);
//				
//				//jasHor.setLabelFormat(new MediaTimeFormat());
//				jasHor.setBackground(Color.GREEN);
//				JFrame testFrameHor = new JFrame(jasHor.getClass().getName()
//						+ " Test Horizontal");
//				
//				testFrameHor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				Container content = testFrameHor.getContentPane();
//				content.setLayout(new BorderLayout());
//				content.add(jasHor,BorderLayout.CENTER);
//				jasHor.setFont(jasHor.getFont().deriveFont((float)10.0));
				// jasHor.setFont(jasHor.getFont().deriveFont(30));
//				JAutoScale jasVer = new JAutoScale(VERTICAL, 400, 10, 10000);
//				JAutoScale jasVer=new JAutoScale(JAutoScale.VERTICAL,800,(long)150,(long)900);
				JAutoScale jasVer=new JAutoScale(Orientation.WEST,800,(long)200,(long)100);
				// JAutoScale jasVer = new JAutoScale(VERTICAL, 400, 500);
				//JPanel testPanel = new JPanel();
				//testPanel.add(jasVer);
				//testPanel.setBackground(Color.YELLOW);
				JFrame testFrameVer = new JFrame(jasVer.getClass().getName()
						+ " Test Vertical");
				testFrameVer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//testPanel.setOpaque(true); // content panes must be opaque
				testFrameVer.getContentPane().add(jasVer);
				testFrameVer.pack();
//				testFrameHor.pack();
				testFrameVer.setVisible(true);
//				testFrameHor.setVisible(true);
			}
		});
	}

}
