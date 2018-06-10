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

package ipsk.swing.scale;

import ipsk.awt.JScale;
import ipsk.awt.JScale.Orientation;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.text.Format;
import java.util.List;

import javax.swing.JFrame;

/**
 * Graphical decimal scale with automatic labeling.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class JMinimalScale extends JScale<BigDecimal>{

	private final static int DEBUG = 0;


	private static final int DEFAULT_LABEL_PADDING = 1;
	
	
	private BigDecimal[] tickList=new BigDecimal[2]; //begin and end tick

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
	
	public class RenderModel{
		private Dimension size;
		private Dimension maxLabelSize=new Dimension();
		public Dimension getMaxLabelSize() {
			return maxLabelSize;
		}
		public void setMaxLabelSize(Dimension maxLabelSize) {
			this.maxLabelSize = maxLabelSize;
		}
		private int lineLength;
		public int getLineLength() {
			return lineLength;
		}
		public void setLineLength(int lineLength) {
			this.lineLength = lineLength;
		}
		private BigDecimal majTick;
		private BigDecimal majTickAbs;
		private List<BigDecimal> tickValueList;
		public List<BigDecimal> getTickValueList() {
			return tickValueList;
		}
		public void setTickValueList(List<BigDecimal> tickValueList) {
			this.tickValueList = tickValueList;
		}
		public void setMajTickAbs(BigDecimal majTickAbs) {
			this.majTickAbs = majTickAbs;
		}
		public void setSize(Dimension size) {
			this.size = size;
		}
		public Dimension getSize() {
			return size;
		}
		public void setMajTick(BigDecimal majTick) {
			this.majTick = majTick;
		}
		
		public BigDecimal getMajTick() {
			return majTick;
		}
		
		public BigDecimal getMajTickAbs() {
			return majTickAbs;
		}
		
	}


	
	private int labelPadding;

	private Format labelFormat;


	public static final int HORIZONTAL = 0;

	public static final int VERTICAL = 1;

	private BigDecimal scaleBegin;
	
	private BigDecimal scaleEnd;
	
	private boolean normalizeLabelScales=true;
	
	

	public JMinimalScale() {
		this(Orientation.SOUTH, new BigDecimal(0), new BigDecimal(0));
	}

	/**
	 * Create scale.
	 * 
	 * @param orientation
	 * @param from start point
	 * @param to end point
	 */
	public JMinimalScale(Orientation orientation, BigDecimal from, BigDecimal to) {
		this(orientation, null, from, to);
	}
	
	
	/**
	 * Create scale.
	 * 
	 * @param orientation
	 * @param d start point
	 * @param e end point
	 */
	public JMinimalScale(Orientation orientation, String unit, BigDecimal d, BigDecimal e) {
		super(orientation);
		setLayout(null);
		
		labelPadding=DEFAULT_LABEL_PADDING;
		this.unit=unit;
		scaleBegin=d;
		scaleEnd=e;
		
	}
	
	
    private void _rescale( BigDecimal d, BigDecimal e) {
		if(normalizeLabelScales){
			scaleBegin=d.stripTrailingZeros();
			scaleEnd=e.stripTrailingZeros();
		}else{
			this.scaleBegin = d;
			this.scaleEnd = e;
		}
	}


	private String format(BigDecimal tickValue){
		return tickValue.toPlainString();
	}
	
	private String format(Number tickValue) {
//		Double ldoubleObj = new Double(tickValue);
		if (labelFormat == null) {
			return tickValue.toString();
	
		} else {
			return labelFormat.format(tickValue);
		}
	}
	
	




	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (isEnabled()) {
			g.setColor(getForeground());
		} else {
			g.setColor(getBackground().darker());
		}
		Rectangle a = g.getClipBounds();

		// calcMaxLabelSize();
		Dimension size=getSize();
		int width = size.width;
		int height = size.height;
		if (width == 0 || height == 0){
			return;
		}
	
		Dimension bs=new Dimension();
		if(scaleBegin!=null){
		    bs=getLabelSize(scaleBegin);
		}
		Dimension us=new Dimension();
        if(unit!=null){
            us=getLabelSize(unit);
        }
        
		Dimension es=new Dimension();
		if(scaleEnd!=null){
		    es=getLabelSize(scaleEnd);
		}
		if (Orientation.SOUTH.equals(orientation)) {
			
		   if(scaleBegin!=null){
		       g.drawString(format(scaleBegin), labelPadding, labelPadding);
		   }
		   if(scaleEnd!=null){
            g.drawString(format(scaleEnd), width-labelPadding-es.width, labelPadding);
		   }

		} else {
		    if(scaleBegin!=null){
				g.drawString(format(scaleBegin), labelPadding, labelPadding+es.height);
		    }
		    if(unit!=null){
                g.drawString(unit,labelPadding, labelPadding+(height/2)+us.height/2);
            }
		    if(scaleEnd!=null){
				g.drawString(format(scaleEnd), labelPadding, height-labelPadding);
		    }
		}
	}

	
	private Dimension getLabelSize(BigDecimal val){
	   
	   return getLabelSize(format(val));
	       
	}
	
	private Dimension getLabelSize(String label){
        Font f=getFont();
       
        if(f!=null){
            FontMetrics fontMetrics = getFontMetrics(f);
            Rectangle2D valBounds = fontMetrics.getStringBounds(label, getGraphics()); 
            double valWidth = valBounds.getWidth();
            double valHeight = valBounds.getHeight();
            Dimension d = new Dimension();
            d.setSize(valWidth,valHeight);
            return d;
        }
        return new Dimension();
    }
	
//	protected void calcMaxLabelSize() {
//	    BigDecimal[] exampleTickValues=computeExampleTicks();
//	    int maxLabelWidth=0;
//	    int maxLabelHeight=0;
//	    for(BigDecimal exampleTickVal:exampleTickValues){
//	        Dimension exampleValueDim=getLabelSize(exampleTickVal);
//	        if(exampleValueDim!=null){
//	            if(exampleValueDim.width>maxLabelWidth){
//	                maxLabelWidth=exampleValueDim.width;
//	            }
//	            if(exampleValueDim.height>maxLabelHeight){
//	                maxLabelHeight=exampleValueDim.height;
//	            }
//	        }
//	    }
//	    maxLabelSize=new Dimension(maxLabelWidth,maxLabelHeight);
//	    
//	}
	
//	protected void calcMaxLabelSize(int length) {
//	    Font f=getFont();
//	    if(f!=null){
//		FontMetrics fontMetrics = getFontMetrics(f);
//		Rectangle2D fromBounds = fontMetrics.getStringBounds(
//				format(scaleBegin), getGraphics());
//		Rectangle2D toBounds = fontMetrics.getStringBounds(format(scaleEnd),
//				getGraphics());
//		double fromWidth = fromBounds.getWidth();
//		double toWidth = toBounds.getWidth();
//		double fromHeight = fromBounds.getHeight();
//		double toHeight = toBounds.getHeight();
//		double maxLabelWidth = Math.max(fromWidth, toWidth);
//		// fixed spacing for now
////		paddedLabelWidth = maxLabelWidth * horizontalPaddingFactor;
//		double maxLabelHeight = Math.max(fromHeight, toHeight);
////		paddedLabelHeight = maxLabelHeight * verticalPaddingFactor;
//		Dimension d = new Dimension();
//		d.setSize(maxLabelWidth, maxLabelHeight);
//		maxLabelSize = d;
//
//	}
//	}

	
	
	public Dimension getMinimumSize() {
		Dimension minimumSize=null;
		Dimension sb=new Dimension();
		if(scaleBegin!=null){
		    sb=getLabelSize(scaleBegin);
		}
		Dimension su=new Dimension();
        if(unit!=null){
            su=getLabelSize(unit);
        }
		Dimension se=new Dimension();
		if(scaleEnd!=null){
		    se=getLabelSize(scaleEnd);
		}
		 double width=0;
         double height=0;
         if (Orientation.SOUTH.equals(orientation)) {
		    width=sb.getWidth()+su.getWidth()+se.getWidth()+4*labelPadding;
		    height=sb.getHeight();
		    if(su.getHeight()>height){
		        height=su.getHeight();
		    }
		    if(se.getHeight()>height){
                height=se.getHeight();
            }
		    height+=labelPadding*2;
		  } else if (Orientation.WEST.equals(orientation)){
		    height=sb.getHeight()+su.getHeight()+se.getHeight()+4*labelPadding;
            width=sb.getWidth();
            if(su.getWidth()>width){
                width=su.getWidth();
            }
            if(se.getWidth()>width){
                width=se.getWidth();
            }
            
            width+=labelPadding*2;
		}
		minimumSize=new Dimension((int)width,(int) height);
		return minimumSize;
	}

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
		rescale();
		}
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
				
//				JAutoScale3 jasVer=new JAutoScale3(JAutoScale3.VERTICAL,400,BigDecimal.valueOf(-5,2),BigDecimal.valueOf(24,2));
				
				JMinimalScale jasVer=new JMinimalScale(Orientation.WEST,BigDecimal.valueOf(10000,-1),BigDecimal.valueOf(500,-1));
				jasVer.setUnit("db(A)");
				
				jasVer.setSize(100,800);
				
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
