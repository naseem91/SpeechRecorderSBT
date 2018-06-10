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

package ips.incubator.swing;

import ipsk.awt.TickProvider;
import ipsk.awt.GridTick;
import ipsk.awt.JScale;
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
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.Format;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Graphical scale with automatic labelling.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class JAutoScale2 extends JScale<BigDecimal>{

	private final static int DEBUG = 1;

	private static final double DEFAULT_BASE =10;

	private static final double[] DEFAULT_DIVIDERS = { 2 , 5 };

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

//	private double paddedLabelWidth;
//
//	private double paddedLabelHeight;

	private Format labelFormat;

	private int lineLength;

	private int halfLineLength;

	public static final int HORIZONTAL = 0;

	public static final int VERTICAL = 1;

	private BigDecimal scaleBegin;

	private BigDecimal scaleEnd;

	private BigDecimal bDdelta;
	private double delta;

	private double base;

	private double[] dividers;

	private Dimension maxLabelSize=new Dimension();

	private int maxNumTicks;

	private BigDecimal majTick;

	private BigDecimal firstTickValue;
	private int precision=1;
	
	private boolean normalizeLabelScales=true;
	private int requiredScale=0;
	
	

	private volatile  Dimension layoutForSize = null;

	public JAutoScale2() {
		this(Orientation.SOUTH, new BigDecimal(0), new BigDecimal(0));
	}

	/**
	 * Create scale.
	 * 
	 * @param orientation
	 * @param from start pint
	 * @param to end point
	 */
	public JAutoScale2(Orientation orientation, BigDecimal from, BigDecimal to) {
		this(orientation, 0, from, to);
	}

	/**
	 * Create scale.
	 * 
	 * @param orientation
	 * @param length
	 *            in pixels
	 * @param d start pint
	 * @param e end point
	 */
	public JAutoScale2(Orientation orientation, int length, BigDecimal d, BigDecimal e) {
		super(orientation);
		setLayout(null);
		if(normalizeLabelScales){
			scaleBegin=d.stripTrailingZeros();
			scaleEnd=e.stripTrailingZeros();
		}else{
			this.scaleBegin = d;
			this.scaleEnd = e;
		}
//		delta = e - d;
		bDdelta=e.subtract(d);
		delta=bDdelta.doubleValue();
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

	private String format(Number tickValue) {
//		Double ldoubleObj = new Double(tickValue);
		if (labelFormat == null) {
			return tickValue.toString();
		} else {
			return labelFormat.format(tickValue);
		}
	}
	
	
	
	protected BigDecimal[] computeExampleTicks() {
	   
//        Dimension d = getSize();
//        if (orientation == HORIZONTAL) {
//            maxNumTicks = (int) (d.width / (maxLabelSize.width*horizontalPaddingFactor));
//        } else {
//            maxNumTicks = (int) (d.height / (maxLabelSize.height*verticalPaddingFactor));
//        }
        int minNumExampleTicks=4;
       
        // minimum length of a tick
        double minTick = (double) delta / (double) minNumExampleTicks;
        double minTickAbs=Math.abs(minTick);
        
        double minTickPow = Math.log(minTickAbs) / Math.log(base);
        int tickPow = (int) Math.ceil(minTickPow);
       
     
        BigDecimal majTickAbs=BigDecimal.valueOf(1,-tickPow);
        for (int i = 0; i < dividers.length; i++) {
            BigDecimal division=majTickAbs.divide(new BigDecimal(dividers[i]));
            double divisionD=division.doubleValue();
//          if (majTickAbs / dividers[i] > minTickAbs) {
            if(divisionD>minTickAbs){
                majTickAbs = division;
                // TODO break ??
            }
        }
        BigDecimal majTick;
        if(delta>=0){
            majTick=majTickAbs;
        }else{
            majTick=majTickAbs.multiply(new BigDecimal(-1));
        }
        majTick=majTick.stripTrailingZeros();
        if (DEBUG>0)
            System.out.println("Chosen majTick: " + majTick);
        
        BigDecimal firstTickValue=null;
        BigDecimal nextTickValue=null;
        BigDecimal preLastTickValue=null;
        BigDecimal lastTickValue=null;
        
        BigDecimal[] samples=new BigDecimal[4];
        if (majTickAbs.signum() > 0) {
            BigDecimal firstTickUnits=scaleBegin.divideToIntegralValue(majTickAbs);
            if(bDdelta.signum()<0){
                firstTickUnits.add(BigDecimal.ONE);
            }
//          firstTickValue = ((scaleBegin / majTick)) * majTick;
            firstTickValue=firstTickUnits.multiply(majTickAbs).stripTrailingZeros();
            nextTickValue=firstTickValue.add(majTick).stripTrailingZeros();
            preLastTickValue=firstTickValue.add(majTick.multiply(new BigDecimal(minNumExampleTicks-1))).stripTrailingZeros();
            lastTickValue=firstTickValue.add(majTick.multiply(new BigDecimal(minNumExampleTicks))).stripTrailingZeros();
        }
        
        samples[0]=firstTickValue;
        samples[1]=nextTickValue;
        samples[2]=preLastTickValue;
        samples[3]=lastTickValue;
        
        
        
        if(normalizeLabelScales){
            int requiredScale=Integer.MIN_VALUE;
            for(BigDecimal s:samples){
                if(s!=null){
                    int sScale=s.scale();
                    if(sScale>requiredScale){
                        requiredScale=sScale;
                    }
                }
            }
            BigDecimal[] scaledSamples=new BigDecimal[samples.length];
            for(int i=0;i<samples.length;i++){
                BigDecimal s=samples[i];
                if(s!=null){
                     scaledSamples[i]=s.setScale(requiredScale);
                }
            }
            samples=scaledSamples;
            
        }
        return samples;
        
    }

	private void computeTicks() {
		Dimension d = getSize();
		// System.out.println("Compute size: "+d);
//		if (maxLabelSize==null){
		    computeExampleTicks();
//		}
		if (Orientation.SOUTH.equals(orientation)) {
			maxNumTicks = (int) (d.width / (maxLabelSize.width*horizontalPaddingFactor));
		} else if (Orientation.WEST.equals(orientation)){
			maxNumTicks = (int) (d.height / (maxLabelSize.height*verticalPaddingFactor));
		}
		double minTick = (double) delta / (double) maxNumTicks;
		double minTickAbs=Math.abs(minTick);
		if (DEBUG>1)
			System.out.println("maxNumTicks: " + maxNumTicks + " minTick: "
					+ minTick);

		double minTickPow = Math.log(minTickAbs) / Math.log(base);
		int tickPow = (int) Math.ceil(minTickPow);
		precision=(tickPow>0)?tickPow:-tickPow;
		BigDecimal bdBase=new BigDecimal(10);
//		BigDecimal majTickAbs=bdBase.pow(tickPow);
		BigDecimal majTickAbs=BigDecimal.valueOf(1,-tickPow);

//		BigDecimal majTickAbs = new BigDecimal( Math.pow(base, tickPow));
//		if (majTickAbs < 1) {
//			majTickAbs = 1;
//		}
		for (int i = 0; i < dividers.length; i++) {
			BigDecimal division=majTickAbs.divide(new BigDecimal(dividers[i]));
			double divisionD=division.doubleValue();
//			if (majTickAbs / dividers[i] > minTickAbs) {
			if(divisionD>minTickAbs){
				majTickAbs = division;
				// TODO break ??
			}
		}
		if(delta>=0){
			majTick=majTickAbs;
		}else{
			majTick=majTickAbs.multiply(new BigDecimal(-1));
		}
		majTick=majTick.stripTrailingZeros();
		if (DEBUG>0)
			System.out.println("Chosen real majTick: " + majTick);
		// long firstTickValue;
		if (majTickAbs.signum() > 0) {
			BigDecimal firstTickUnits=scaleBegin.divideToIntegralValue(majTickAbs);
			if(bDdelta.signum()<0){
				firstTickUnits.add(BigDecimal.ONE);
			}
//			firstTickValue = ((scaleBegin / majTick)) * majTick;
			firstTickValue=firstTickUnits.multiply(majTickAbs);
		} else {
			firstTickValue = scaleBegin;
		}
		firstTickValue=firstTickValue.stripTrailingZeros();
//		if(normalizeLabelScales){
//			// the first two values should be sufficient to calculate the highest required scale 
//			BigDecimal nextTickValue=firstTickValue.stripTrailingZeros().add(majTick);
//			requiredScale=firstTickValue.stripTrailingZeros().max(nextTickValue.stripTrailingZeros()).scale();
//			firstTickValue=firstTickValue.setScale(requiredScale);
//			
//		}
		if (DEBUG>1)
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
			lineY1 = height - getMinimumSizeFromMaxLabelSize(maxLabelSize).height;
			lineY2 = lineY1;
		} else if (Orientation.NORTH.equals(orientation)) {

            lineX1 = a.x;
            lineX2 = a.x + a.width;
            lineY1 = 1;
            lineY2 = lineY1;
        } else if (Orientation.WEST.equals(orientation)){

			lineX1 = getMinimumSizeFromMaxLabelSize(maxLabelSize).width-1;
			lineX2 = lineX1;
			lineY1 = a.y;
			lineY2 = a.y+a.height;
		}else if (Orientation.EAST.equals(orientation)){

            lineX1 = 1;
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
			GridTick<BigDecimal>[] ticks = getScaleTicks(a.x, a.x + a.width);
			for (GridTick<BigDecimal> gt : ticks) {
				int pos = gt.getPosition();
				BigDecimal tv=gt.getTickValue();
                Dimension tvDim=getLabelSize(tv);
                if(tvDim !=null){
//                  Container p=getParent();
                    if(tvDim.width>maxLabelSize.width){
                        maxLabelSize.width=tvDim.width;
                        layoutForSize=getSize();
                        if (DEBUG>2)System.err.println("Revalidate me!");
                        revalidate();
//                      p.validate();
                        repaint();
                        return;
                    }else if(tvDim.height>maxLabelSize.height){
                        maxLabelSize.height=tvDim.height;
                        layoutForSize=getSize();
                        if(DEBUG>2)System.err.println("Revalidate me!");
                        revalidate();
//                      p.validate();
                        repaint();
                        return;
                    }
                }
                if(DEBUG>2)System.out.println("Value "+tv+" Scale "+tv.scale());
				g.drawString(format(gt.getTickValue()), pos + 1, lineY1
						+ (int) maxLabelSize.height + halfLineLength);
				
				g.drawLine(pos, 0, pos, lineY1 + lineLength);
				int halfPos = pos + ((gt.getNextPosition() - pos) / 2);
				g.drawLine(halfPos, lineY1, halfPos, lineY1 + halfLineLength);
			}

		} else if (Orientation.WEST.equals(orientation)){
			GridTick<BigDecimal>[] ticks = getScaleTicks( a.y, a.y + a.height);
			for (GridTick<BigDecimal> gt : ticks) {
				int pos = gt.getPosition();
				BigDecimal tv=gt.getTickValue();
				Dimension tvDim=getLabelSize(tv);
				if(tvDim !=null){
//				    Container p=getParent();
				    if(tvDim.width>maxLabelSize.width){
				        maxLabelSize.width=tvDim.width;
				        layoutForSize=getSize();
				        if (DEBUG>2)System.err.println("Revalidate me!");
				        revalidate();
//				        p.validate();
				        repaint();
				    }else if(tvDim.height>maxLabelSize.height){
				        maxLabelSize.height=tvDim.height;
				        layoutForSize=getSize();
				        if(DEBUG>2)System.err.println("Revalidate me!");
				        revalidate();
//				        p.validate();
				        repaint();
				    }
				}
				if(DEBUG>2)System.out.println("Value "+tv+" Scale "+tv.scale());
				g.drawString(format(gt.getTickValue()), lineX1
						- (int) maxLabelSize.width - halfLineLength, pos
						+ (int) maxLabelSize.height);
				g.drawLine(lineX1, pos, lineX1 - lineLength, pos);
				int halfPos = pos + ((gt.getNextPosition() - pos) / 2);
				g.drawLine(lineX1, halfPos, lineX1 - halfLineLength, halfPos);
			}
		}
	}

	public GridTick<BigDecimal>[] getScaleTicks( int fromPixel, int toPixel) {
	    computeTicks();
		ArrayList<GridTick<BigDecimal>> gridTicks = new ArrayList<GridTick<BigDecimal>>();
		double dDelta = (double) delta;
		width = getWidth();
		height = getHeight();
		//Scale scale = new Scale();
		if (Orientation.SOUTH.equals(orientation)) {
			
			double dWidth = (double) width;
			double dAx = (double) fromPixel;
			BigDecimal areaBegValue = scaleBegin.add(new BigDecimal((dAx * dDelta) / dWidth));
			BigDecimal tickValue = BigDecimal.ZERO;
			if (!majTick.equals(BigDecimal.ZERO)) {
//				BigDecimal temp=areaBegValue.subtract(firstTickValue).divideToIntegralValue(majTick).multiply(majTick);
//				tickValue = temp.add( firstTickValue).stripTrailingZeros();
				tickValue=firstTickValue;
				double areaEndValue = scaleBegin.doubleValue()+ (((dAx + (double) (toPixel - fromPixel)) * dDelta) / dWidth);
//				int c=0;
				do {
//					if(normalizeLabelScales){
//						tickValue=tickValue.setScale(requiredScale);
//					}
//					int pos = (int) (((double) (tickValue - scaleBegin) * dWidth) / dDelta);
					int pos = (int) (((double) (tickValue.subtract(scaleBegin)).doubleValue() / dDelta)*dWidth);
					int nextPos = (int) ((((tickValue.add( majTick)).subtract(scaleBegin)).doubleValue() / dDelta)*dWidth);
					GridTick<BigDecimal> newTick = new GridTick<BigDecimal>( pos, tickValue);
					newTick.setNextPosition(nextPos);
					gridTicks.add(newTick);

					tickValue=tickValue.add( majTick);
				} while ((majTick.signum()>0 && tickValue.doubleValue() <= areaEndValue)|| (majTick.signum()<0 && tickValue.doubleValue()>=areaEndValue));
			}
		 } else if (Orientation.WEST.equals(orientation)){
			double dHeight = (double) height;
			double dAy = (double) fromPixel;
			BigDecimal areaBegValue = scaleBegin.add(new BigDecimal((dAy * dDelta) / dHeight));
			BigDecimal tickValue = BigDecimal.ZERO;
//			double startTickValue;
			if (!majTick.equals(BigDecimal.ZERO)) {
//				tickValue =
//
//					((areaBegValue.subtract(firstTickValue)).divideToIntegralValue(majTick)).multiply(majTick).add(firstTickValue).stripTrailingZeros();
				tickValue=firstTickValue;
//				tickValue=tickValue.add(majTick).subtract(majTick);
				double areaEndValue = scaleBegin.doubleValue()+ (((dAy + (double) (toPixel - fromPixel)) * dDelta) / dHeight);
//				MathContext mc=new MathContext(precision);
//				startTickValue=tickValue;
//				int c=0;
//				
//				BigDecimal tValBd=new BigDecimal(tickValue, mc);
//				BigDecimal majTBd=new BigDecimal(majTick, mc);
				do {
//					tickValue=startTickValue+(c*majTick);
//					if(normalizeLabelScales){
//						tickValue=tickValue.setScale(requiredScale);
//					}
					int pos = (int) (((tickValue.subtract(scaleBegin).doubleValue()/dDelta)* dHeight ));
					int nextPos = (int) (((tickValue.add( majTick).subtract(scaleBegin).doubleValue())/ dDelta)*dHeight);
//					BigDecimal tickValBgDec=new BigDecimal(tickValue, mc);
					GridTick<BigDecimal> newTick = new GridTick<BigDecimal>( pos, tickValue);
					newTick.setNextPosition(nextPos);
					gridTicks.add(newTick);
//					c++;
//					tValBd=tValBd.add(majTBd, mc);
					tickValue = tickValue.add(majTick);
			
					
				} while ((majTick.signum()>0 && tickValue.doubleValue() <= areaEndValue)||(majTick.signum()<0 && tickValue.doubleValue() >= areaEndValue) );
			}
		}
		
		if(normalizeLabelScales){
		    int requiredScale=Integer.MIN_VALUE;
		    for(GridTick<BigDecimal> gt:gridTicks){
		        BigDecimal gtVal=gt.getTickValue().stripTrailingZeros();
		        int valScale=gtVal.scale();
		        if(valScale>requiredScale){
		            requiredScale=valScale;
		        }
		    }
		    for(GridTick<BigDecimal> gt:gridTicks){
              
                BigDecimal gtVal=gt.getTickValue();
                BigDecimal gtScaledval=gtVal.setScale(requiredScale);
                gt.setTickValue(gtScaledval);
            }
		}
		GridTick<BigDecimal>[] emptyArray=new GridTick[0];
		GridTick<BigDecimal>[] array = gridTicks.toArray(emptyArray);
        return array;
	}
	
	

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

	
	private Dimension getLabelSize(BigDecimal val){
	    Font f=getFont();
	    if(f!=null){
	        FontMetrics fontMetrics = getFontMetrics(f);
	        Rectangle2D valBounds = fontMetrics.getStringBounds(
	                format(val), getGraphics()); 
	        double valWidth = valBounds.getWidth();
	        double valHeight = valBounds.getHeight();
	        Dimension d = new Dimension();
	        d.setSize(valWidth,valHeight);
	        return d;
	    }
	    return null;
	}
	
	protected void calcMaxLabelSize() {
	    BigDecimal[] exampleTickValues=computeExampleTicks();
	    int maxLabelWidth=0;
	    int maxLabelHeight=0;
	    for(BigDecimal exampleTickVal:exampleTickValues){
	        Dimension exampleValueDim=getLabelSize(exampleTickVal);
	        if(exampleValueDim!=null){
	            if(exampleValueDim.width>maxLabelWidth){
	                maxLabelWidth=exampleValueDim.width;
	            }
	            if(exampleValueDim.height>maxLabelHeight){
	                maxLabelHeight=exampleValueDim.height;
	            }
	        }
	    }
	    maxLabelSize=new Dimension(maxLabelWidth,maxLabelHeight);
	    
	}
	
	protected void calcMaxLabelSize(int length) {
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
//		paddedLabelWidth = maxLabelWidth * horizontalPaddingFactor;
		double maxLabelHeight = Math.max(fromHeight, toHeight);
//		paddedLabelHeight = maxLabelHeight * verticalPaddingFactor;
		Dimension d = new Dimension();
		d.setSize(maxLabelWidth, maxLabelHeight);
		maxLabelSize = d;

	}
	}

	
	private Dimension getMinimumSizeFromMaxLabelSize(Dimension maxLabelSize){
	    double paddedLabelWidth = maxLabelSize.width * horizontalPaddingFactor;
        double paddedLabelHeight=maxLabelSize.height * verticalPaddingFactor;
        if (Orientation.SOUTH.equals(orientation)){
            lineLength = (int) maxLabelSize.height;
            halfLineLength = lineLength / 2;
            minHeight = (int) paddedLabelHeight + 2 + halfLineLength;
            minWidth = 0;
        } else if (Orientation.WEST.equals(orientation)){
            lineLength = (int) maxLabelSize.height;
            halfLineLength = lineLength / 2;
            minWidth = (int) paddedLabelWidth + 2 + halfLineLength;
            minHeight = 0;
        }
        return new Dimension(minWidth, minHeight);
	}
	
	public Dimension getMinimumSize() {
//		// fixed spacing for now
//		calcMaxLabelSize();
//	    System.out.println("getMinimumSize()");
	    Dimension currSize=getSize();
	    if(layoutForSize==null || !layoutForSize.equals(currSize)){
	        calcMaxLabelSize();
	    }
	    Dimension minSize=getMinimumSizeFromMaxLabelSize(maxLabelSize);
	    return minSize;
		
	}
	
//	public Dimension getMinimumSize(){
//		return new Dimension(1,1);
//	}

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
	public BigDecimal getScaleBegin() {
		return scaleBegin;
	}

	/**
	 * @param scaleBegin
	 *            The scaleBegin to set.
	 */
	public void setScaleBegin(BigDecimal scaleBegin) {
		this.scaleBegin = scaleBegin;
		rescale();
	}

	/**
	 * @return Returns the scaleEnd.
	 */
	public BigDecimal getScaleEnd() {
		return scaleEnd;
	}

	/**
	 * @param scaleEnd
	 *            The scaleEnd to set.
	 */
	public void setScaleEnd(BigDecimal scaleEnd) {
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
	    
	    if(layoutForSize!=null){
	        Dimension currSize=getSize();
	        if(!layoutForSize.equals(currSize)){
	            layoutForSize=null;
	            calcMaxLabelSize();
	        }
	    }else{
	        calcMaxLabelSize();
	    }
	     bDdelta = scaleEnd.subtract(scaleBegin);
	     delta=bDdelta.doubleValue();
//	     if(normalizeLabelScales){
//	    	 int currentReqScale=requiredScale;
//	    	 do{
//	    		 currentReqScale=requiredScale;
//	    		 calcMaxLabelSize();
////	    		 computeTicks();
//	    		 if(requiredScale>currentReqScale){
//	    			 System.out.println("Will repeat!");
//	    		 }
//	    	 }while(requiredScale>currentReqScale);
//	     }else{
//	      calcMaxLabelSize();
////	      computeTicks();
//	     }
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
				double test=0.1;
//				System.out.println("Test: "+0.1*7);
				BigDecimal bd=BigDecimal.valueOf(1, 1);
				BigDecimal bd2=bd.multiply(new BigDecimal(7));
//				System.out.println("Test2: "+bd.doubleValue()+" "+bd2);
				BigDecimal bd3=BigDecimal.valueOf(-65432, 10);
				
//				BigDecimal bd2=bd.multiply(new BigDecimal(7));
//				System.out.println("Test3: "+bd.doubleValue()+" "+bd3);
				BigDecimal s1=BigDecimal.valueOf(-10,1);
				BigDecimal s2=s1.stripTrailingZeros();
//				System.out.println(s1+" "+s2);
				for(int i=0;i<20;i++){
//				System.out.println((BigDecimal.valueOf(-3,i)).toString());
				}
//				System.exit(0);
//				JAutoScale2 jasVer=new JAutoScale2(JAutoScale2.VERTICAL,800,new BigDecimal(0.00500001),new BigDecimal(0.00500002));
				
				JAutoScale2 jasVer=new JAutoScale2(Orientation.SOUTH,800,BigDecimal.valueOf(-130002,5),BigDecimal.valueOf(1,0));
				BigDecimal[] samples=jasVer.computeExampleTicks();
				for(BigDecimal s:samples){
				    System.out.println("sample: "+s);
				}
				Dimension pSize=new Dimension(200,600);
				jasVer.setSize(pSize);
				jasVer.setPreferredSize(pSize);
				// JAutoScale jasVer = new JAutoScale(VERTICAL, 400, 500);
				//JPanel testPanel = new JPanel();
				//testPanel.add(jasVer);
				//testPanel.setBackground(Color.YELLOW);
				JFrame testFrameVer = new JFrame(jasVer.getClass().getName()
						+ " Test Vertical");
				testFrameVer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//testPanel.setOpaque(true); // content panes must be opaque
				testFrameVer.getContentPane().add(jasVer);
				testFrameVer.getContentPane().setSize(pSize);
				testFrameVer.getContentPane().setPreferredSize(pSize);
				testFrameVer.pack();
//				testFrameHor.pack();
				testFrameVer.setVisible(true);
//				testFrameHor.setVisible(true);
			}
		});
	}

}
