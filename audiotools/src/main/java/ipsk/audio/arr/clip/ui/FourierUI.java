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

import ipsk.audio.AudioFormatNotSupportedException;

import ipsk.audio.AudioSourceException;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.AudioClipListener;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.events.FramePositionChangedEvent;
import ipsk.audio.dsp.DSPUtils;
import ipsk.awt.GridTick;
import ipsk.awt.JScale;
import ipsk.awt.JScale.Orientation;
import ipsk.swing.JDialogPanel;
import ipsk.swing.scale.JDecimalAutoScale;
import ipsk.swing.scale.JMinimalScale;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.Scrollable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FourierUI extends BasicAudioClipUI implements Scrollable, MouseListener, MouseMotionListener,
		AudioClipListener, FourierRendererListener {

	// profile not used yet
	public enum Profile{
		// Physical: Nyquist frequency is maximum frequency, no emphasis
	    PHYSICAL("Physical",null,60,0.05,0),
	    
	    // Phonetic1 similar to Praat defaults
	    PHONETIC1("Phonetic1",5000.0,70,0.005,6);

	    Profile(String value,Double maxFrequency,double dynamicRangeDB,double windowLength,double emphasisPerOctaveDB) {
	    	this.value = value;
	    	this.maxFrequency=maxFrequency;
	    	this.dynamicRangeDB=dynamicRangeDB;
	    	this.windowLength=windowLength;
	    	this.emphasisPerOctaveDB=emphasisPerOctaveDB;
	    }
	    private final String value;
	    private final Double maxFrequency;
	    private final double dynamicRangeDB;
	    /**
		 * @return the dynamicRangeDB
		 */
		public double getDynamicRangeDB() {
			return dynamicRangeDB;
		}
		private final double windowLength;
	    private final double emphasisPerOctaveDB;

	    /**
		 * @return the maxFrequency
		 */
		public Double getMaxFrequency() {
			return maxFrequency;
		}
		/**
		 * @return the windowLength
		 */
		public double getWindowLength() {
			return windowLength;
		}
		/**
		 * @return the emphasisPerOctaveDB
		 */
		public double getEmphasisPerOctaveDB() {
			return emphasisPerOctaveDB;
		}
		public String value() {
	    	return value; 
	    }
	
	    public String toString() {
	    	StringBuffer sb=new StringBuffer(value);
	    	sb.append(": ");
	    	if(maxFrequency!=null){
	    		sb.append("Max freq. :"+maxFrequency);
	    		sb.append(", ");
	    	}
	    	sb.append("Window length: "+windowLength+" s");
	    	if(emphasisPerOctaveDB!=0.0){
	    		sb.append(", ");
	    		sb.append("Emphasis per octave: "+emphasisPerOctaveDB+" dB");
	    	}
	    	return sb.toString();
	    }
	    
	   
	    
	}
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final int DEFAULT_SELECTOR_WIDTH=3; 

    private final boolean DEBUG = false;

//	private final int DEFAULT_BORDER_LENGTH = 0;

	private final Color DEFAULT_SIGNAL_COLOR = Color.GREEN;

//	private final Color DEFAULT_SIGNAL_COLOR_SELECTED = Color.YELLOW.darker();
//
//	private final Color DEFAULT_SELECT_COLOR = Color.YELLOW;
//
//	private final Color DEFAULT_YAXIS_COLOR = Color.GRAY;

	private final int DEFAULT_N=512;
	private final double DEFAULT_WINDOW_SIZE=0.005;

	private int pixelPosition = 0;
	private boolean rendered;
	private int imgHeight;
	
	private MouseEvent pressedEvent = null;

	private MouseEvent dragStartEvent = null;

	private MouseEvent selEndMoveEvent;

	private MouseEvent selStartMoveEvent;

	private MouseEvent mouseOverResizeWest;

	private MouseEvent mouseOverResizeEast;

	// private Vector listenerList;
	private int preferredHeight = 100;

	private boolean imgHeightSet = false;

//	private float yMin;
//
//	private float yMax;
//
//	private JLabel yMinLabel;
//
//	private JLabel yMaxLabel;

//	private JAutoScale yScale;
	private JScale<BigDecimal>[] yScales;
	private JPanel yScalesComponent;

	private FourierRenderer renderer;
//	private FourierRenderer printRenderer;
//	private int borderLength = DEFAULT_BORDER_LENGTH;

//	private float factor=(float)0.5;
	private int dftLength=DEFAULT_N;
	//private int dftLength=2048;
	
	public int getDftLength() {
		return dftLength;
	}

	public void setDftLength(int dftLength) {
		this.dftLength = dftLength;
		rerender();
	}

	private Double maxFrequency=null;
	
	public static double DEFAULT_EMPHASIS_START_FREQUENCY=1000;
//	private double emphasisStartFrequency=1000;
	private double emphasisStartFrequency=DEFAULT_EMPHASIS_START_FREQUENCY;
	private double emphasisPerOctaveDB=0;
//	private Double emphasisPerOctave=DSPUtils.toPowerLinearLevel(6);
	

//	private int discretWindowSize;
	
	private double windowSize=DEFAULT_WINDOW_SIZE;
	
	private double dynamicRangeDB=50;
	
	
	
	private boolean useThread=false;

//    private AudioSource audioSource;
    
    private FourierControlUI controlComponent;
    private int selectorWidth=DEFAULT_SELECTOR_WIDTH;
    
    public class FourierControlUI extends ipsk.swing.JDialogPanel implements ChangeListener,ActionListener{
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
        
        private SpinnerNumberModel windowSizeModel;
        private JSpinner windowSizeSpinner;
        
       private JComboBox dftLengthBox;
        
        private SpinnerNumberModel maxFrequencySpinnerModel;
        private JSpinner maxFrequencySpinner;
        
        private SpinnerNumberModel dynamicRangeDbModel;
        private JSpinner dynamicRangeDbSpinner;
        
        private SpinnerNumberModel emphasisPerOctaveModel;
        private JSpinner emphasisPerOctaveSpinner;


		private JCheckBox autoApplyCheckBox;
        
        public FourierControlUI(){
            super(JDialogPanel.Options.OK_APPLY_CANCEL);
            GridBagLayout gbl=new GridBagLayout();
            Container ct=getContentPane();
            ct.setLayout(gbl);
            // default for now 
            // for audio
            double nyquistFreq=22000;
            if(audioSource!=null){
                // return Nyquist freq
                nyquistFreq=(double)sampleRate/2;
            
           }
            double maxFreq=nyquistFreq;
            Double maxFreqSet=getMaxFrequency();
            if(maxFreqSet!=null){
                maxFreq=maxFreqSet;
            }
            windowSizeModel=new SpinnerNumberModel(getWindowSize(),0.001,1,0.001);
            windowSizeModel.addChangeListener(this);
           
            dftLengthBox=new JComboBox(new Integer[]{32,64,128,256,512,1024,2048,4096,8192});
            dftLengthBox.setSelectedItem(dftLength);
            dftLengthBox.addActionListener(this);
            maxFrequencySpinnerModel=new SpinnerNumberModel(maxFreq,0.0, nyquistFreq, 1000);
            maxFrequencySpinnerModel.addChangeListener(this);
            dynamicRangeDbModel=new SpinnerNumberModel(dynamicRangeDB, 1, 120,1);
            dynamicRangeDbModel.addChangeListener(this);
//            int emphasisPerOctaveIn=0;
//            if(emphasisPerOctaveDB!=null){
//            	emphasisPerOctaveIn=emphasisPerOctaveDB.intValue();
//            }
            emphasisPerOctaveModel=new SpinnerNumberModel(emphasisPerOctaveDB, 0.0, 96.0,1.0);
            emphasisPerOctaveModel.addChangeListener(this);
            GridBagConstraints c = new GridBagConstraints();
            
            c.insets = new Insets(2, 5, 2, 5);
            c.anchor=GridBagConstraints.NORTHWEST;
            c.fill=GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            
            JLabel dftLengthLabel=new JLabel("DFT length:");
            ct.add(dftLengthLabel,c);
            c.gridx++;
            ct.add(dftLengthBox,c);
            dftLengthBox.addActionListener(this);
            c.gridx=0;
            c.gridy++;
            
            JLabel windowSizeLabel = new JLabel("Window size:");
            ct.add(windowSizeLabel, c);
            c.gridx++;
           
            windowSizeSpinner=new JSpinner(windowSizeModel);
            ct.add(windowSizeSpinner,c);
            windowSizeModel.addChangeListener(this);
            c.gridx++;
            JLabel secLabel = new JLabel("s");
            ct.add(secLabel,c);
            
            c.gridx = 0;
            c.gridy++;
            JLabel maxFreqLabel = new JLabel("Max. frequency:");
            ct.add(maxFreqLabel, c);
            c.gridx++;
            maxFrequencySpinner=new JSpinner(maxFrequencySpinnerModel);
            ct.add(maxFrequencySpinner,c);
            maxFrequencySpinnerModel.addChangeListener(this);
            c.gridx++;
            JLabel hzLabel = new JLabel("Hz");
            ct.add(hzLabel,c);
            
            c.gridx = 0;
            c.gridy++;
            JLabel dynamicRangeLabel = new JLabel("Dynamic range:");
            ct.add(dynamicRangeLabel, c);
            c.gridx++;
            dynamicRangeDbSpinner=new JSpinner(dynamicRangeDbModel);
            ct.add(dynamicRangeDbSpinner,c);
            dynamicRangeDbModel.addChangeListener(this);
            c.gridx++;
            JLabel dbLabel = new JLabel("dB");
            ct.add(dbLabel,c);
            
            c.gridx = 0;
            c.gridy++;
            JLabel emphasisPerOctaveLabel = new JLabel("Emphasis per Octave:");
            ct.add(emphasisPerOctaveLabel, c);
            c.gridx++;
            emphasisPerOctaveSpinner=new JSpinner(emphasisPerOctaveModel);
            ct.add(emphasisPerOctaveSpinner,c);
            emphasisPerOctaveModel.addChangeListener(this);
            c.gridx++;
            JLabel dbLabel2 = new JLabel("dB");
            ct.add(dbLabel2,c);
            
            c.gridx = 0;
            c.gridy++;
            JLabel autoApplyLabel = new JLabel("Auto apply:");
            ct.add(autoApplyLabel, c);
            c.gridx++;
            autoApplyCheckBox = new JCheckBox();
            autoApplyCheckBox.setSelected(false);
            ct.add(autoApplyCheckBox,c);
            autoApplyCheckBox.addActionListener(this);
            
        }
        
        public void updateValues(){
            double nyquistFreq=22000;
            if(audioSource!=null){
                // return Nyquist freq
                nyquistFreq=(double)sampleRate/2;
           }
            
            maxFrequencySpinnerModel.setMaximum(nyquistFreq);
            Double mf=getMaxFrequency();
            if(mf!=null){
                maxFrequencySpinnerModel.setValue(mf);
            }
            if((Double)maxFrequencySpinnerModel.getValue() > nyquistFreq){
                maxFrequencySpinnerModel.setValue(nyquistFreq);
            }
            windowSizeModel.setValue(windowSize);
            dftLengthBox.setSelectedItem(getDftLength());
            dynamicRangeDbModel.setValue(dynamicRangeDB);
            emphasisPerOctaveModel.setValue(emphasisPerOctaveDB);
        }
        
        public void applyValues(){
            setWindowSize((Double)windowSizeModel.getValue());
            setDftLength((Integer)dftLengthBox.getSelectedItem());
            setMaxFrequency((Double)maxFrequencySpinnerModel.getValue());
            setDynamicRangeDB(((Double)dynamicRangeDbModel.getValue()).doubleValue());
            setEmphasisPerOctaveDB(((Double)emphasisPerOctaveModel.getValue()).doubleValue());
            
        }

        /* (non-Javadoc)
         * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
         */
        @Override
        public void stateChanged(ChangeEvent ce) {
        	Object src=ce.getSource();
        	if(src==windowSizeModel ||
        			src==maxFrequencySpinnerModel ||
        			src==dynamicRangeDbModel ||
        			src==emphasisPerOctaveModel){
        		if(autoApplyCheckBox.isSelected()){
        			applyValues();
        		}
        	}

        }
        @Override
        public void actionPerformed(ActionEvent ae) {
        	Object src=ae.getSource();

        	if(src==dftLengthBox || src==autoApplyCheckBox){
        		if(autoApplyCheckBox.isSelected()){
        			applyValues();
        		} 
        	}else{
        		super.actionPerformed(ae);
        	}

        }

    }
    
    
	public FourierUI() {
		super();
		rendered = false;
		pixelPosition = 0;
		setBackground(Color.BLACK);
		listenerList = new Vector<ActionListener>();
//		addComponentListener(this);
//		yMin = -1;
//		yMax = 1;
//		yScale = new JPanel();
//		yScale.setLayout(null);
//		yMinLabel = new JLabel(Float.toString(yMin));
//		yScale.add(yMinLabel);
//		yScale=new JAutoScale(JAutoScale.VERTICAL,100000,100000);
		yScalesComponent=new JPanel();
		yScalesComponent.setLayout(null);
		controlComponent=new FourierControlUI();
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public FourierUI(AudioClip audioSample)
			throws AudioFormatNotSupportedException, AudioSourceException {
		this();
		setAudioSample(audioSample);
	}

	public String getName(){
		return "Sonagram";
	}
	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
		imgHeightSet = true;
	}

	// public void setXZoom(double xZoom) {
	// clear();
	// super.setXZoom(xZoom);
	// revalidate();
	// repaint();
	// }

	private void setFramePosition(long position) {
		// framePosition = position;
		int oldPixelPosition = pixelPosition;

		pixelPosition = mapFrameToPixel(position);
		if (EventQueue.isDispatchThread()) {
			paintImmediately(oldPixelPosition, 0, 1, getHeight());
			paintImmediately(pixelPosition, 0, 1, getHeight());
		} else {
			repaint(oldPixelPosition, 0, 1, getHeight());
			repaint(pixelPosition, 0, 1, getHeight());
		}
		
	}


	public void clear() {
		if (DEBUG)
			System.out.print("Clearing screen...");
		clearScreen();
		if (DEBUG)
			System.out.println("O.K.");
//		audioSource = null;

	}

	public void clearScreen() {
		
		setCursor();
		repaint();
	}

	@SuppressWarnings("unchecked")
    private void updateYScales(){
	    yScalesComponent.removeAll();
	    if (audioSource != null){
	        double maxFreq=getMaxFrequency();
	        yScales=(JScale<BigDecimal>[])new JScale[channels];
	        for(int i=0;i<yScales.length;i++){
//	            yScales[i]=new JDecimalAutoScale(JDecimalAutoScale.VERTICAL,BigDecimal.valueOf(maxFreq), BigDecimal.valueOf(0));
	            yScales[i]=new JMinimalScale(Orientation.WEST,"Hz",BigDecimal.valueOf(maxFreq), BigDecimal.valueOf(0));
	            yScalesComponent.add(yScales[i]);
	        }
	    }
	    //yScalesComponent.revalidate();
	    revalidate();
	}

	public void audioClipChanged(AudioClipChangedEvent event) {

		super.audioClipChanged(event);
		if (event instanceof FramePositionChangedEvent) {
			setFramePosition(((FramePositionChangedEvent) event).getPosition());
		} else if (event instanceof AudioSourceChangedEvent) {
			clear();
//			yScalesComponent.removeAll();
			audioSource = ((AudioSourceChangedEvent) event).getAudioSource();
			if (audioSource != null){
				try {
					if (renderer!=null)renderer.close();
					renderer = new FourierRenderer(audioSource,this);
//					AudioFormat af=audioSource.getFormat();
//					float sampleRate=af.getSampleRate();
//					int channels=af.getChannels();
					
					
//					double maxFreq=getMaxFrequency();
//					
//					yScales=new JDecimalAutoScale[channels];
//					for(int i=0;i<yScales.length;i++){
//						yScales[i]=new JDecimalAutoScale(JDecimalAutoScale.VERTICAL,BigDecimal.valueOf(maxFreq), BigDecimal.valueOf(0));
//						yScalesComponent.add(yScales[i]);
//					}
//					updateYScales();
				} catch (AudioFormatNotSupportedException e) {
					JOptionPane.showMessageDialog(this,
							"Audio format not supported\n"
									+ e.getLocalizedMessage(),
							"Audio signal renderer",
							JOptionPane.INFORMATION_MESSAGE);
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (AudioSourceException e) {
					JOptionPane.showMessageDialog(this,
							"Audio source error: \n" + e.getLocalizedMessage(),
							"Audio signal renderer error",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
			 updateYScales();
			controlComponent.updateValues();
		}
	}

	public double getLengthInSeconds() {
		return lengthInSeconds;
	}

	

	private void setCursor() {
		Component parent=getParent();
		if(parent!=null){
		if (dragStartEvent != null) {
			if (dragStartEvent == selStartMoveEvent) {
				parent.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			} else if (dragStartEvent == selStartMoveEvent) {
				parent.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			}
		} else if (mouseOverResizeWest != null) {
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
		} else if (mouseOverResizeEast != null) {
			parent.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
		} else {
			
			if (rendered) {

				parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			} else {
				parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
		}
		}
	}

	public void printComponent(Graphics g) {
	        super.printComponent(g);
	        _paintOrPrintComponent(g, false,true);
	    }
	    
	public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        _paintOrPrintComponent(g, useThread,false);
	}
	    
	private void _paintOrPrintComponent(Graphics g,boolean useThread,boolean printMode) {
		Rectangle clipBounds = g.getClipBounds();
		
		if (audioSample == null || audioSample.getAudioSource() == null
				|| channels == 0 || pixelsPerFrame == 0.0 || renderer == null)
			return;
		FontMetrics gf = g.getFontMetrics();
		int fontHeight = gf.getHeight();
		int height = getHeight();
		if(height<=0) return;
		//System.out.println(clipBounds);
		imgHeight = (height ) / channels;
		int paintFrom = clipBounds.x - (int) pixelsPerFrame - 1;
		if (paintFrom < 0)
			paintFrom = 0;
		int paintTo = clipBounds.x + clipBounds.width + (int) pixelsPerFrame
				+ 1;
		//System.out.println("Fourier paintTo: "+paintTo);
		setCursor();
		//System.out.println("width: "+clipBounds.width);
		//removeMouseListener(this);
		//removeMouseMotionListener(this);

		// Paint zero and full scale lines
//		g.setColor(DEFAULT_YAXIS_COLOR);
//		for (int i = 0; i < channels; i++) {
//			int nullLineY = i * imgHeight + imgHeight / 2 + borderLength;
//			g.drawLine(clipBounds.x, nullLineY, clipBounds.x
//					+ (int) clipBounds.getWidth(), nullLineY);
//			int topScaleLineY = i * imgHeight + borderLength;
//			g.drawString(Float.toString(yMax), 0, topScaleLineY + fontHeight
//					+ 2);
//			int bottomScaleLineY = topScaleLineY + imgHeight;
//			g.drawLine(clipBounds.x, topScaleLineY, clipBounds.x
//					+ (int) clipBounds.getWidth(), topScaleLineY);
//			g.drawLine(clipBounds.x, bottomScaleLineY, clipBounds.x
//					+ (int) clipBounds.getWidth(), bottomScaleLineY);
//			g.drawString(Float.toString(yMin), 0, bottomScaleLineY - 1);
//		}
		//rendered = false;
		// renderer.request(paintFrom,paintTo,framesPerPixel);
		FourierRenderer.RenderResult rr = null;
		try {
			//System.out.println("Call render method... ("+(requestCount++)+")");
		    double maxDftFreq=dftLength/2;
		    if(maxFrequency!=null){
		        maxDftFreq=dftLength*(maxFrequency/sampleRate);
		    }
		    int discretWindowSize=(int)(sampleRate*windowSize);
		    if(emphasisPerOctaveDB!=0.0 && emphasisStartFrequency >0 && emphasisStartFrequency<(sampleRate/2)){
		       double dftEmphasisStartFreq=((double)dftLength*emphasisStartFrequency)/(2*sampleRate);
//		    
//		       renderer.setEmphasisStartFrequency(startEmphDftFreq);
		       rr = renderer.render(paintFrom, paintTo,imgHeight, maxDftFreq,framesPerPixel,dftLength,discretWindowSize,dynamicRangeDB,emphasisPerOctaveDB,dftEmphasisStartFreq,useThread);
		    }else{
		    	rr = renderer.render(paintFrom, paintTo,imgHeight, maxDftFreq,framesPerPixel,dftLength,discretWindowSize,dynamicRangeDB,useThread);
		    }
//			System.out.println("Render method returned.");
		} catch (AudioSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rendered=rr.rendered;
		
		setCursor();
		//if (!rendered) return;

		g.setColor(DEFAULT_SIGNAL_COLOR);
//		boolean firstValue = true;
//		int valPos;
//
//		int to=rr.offset+rr.length;
//		//System.out.println("Paint "+rr.length);
//		float minVal=Float.MAX_VALUE;
//		float maxVal=Float.MIN_VALUE;
		int availPaintTo=paintFrom+rr.renderedLength;
		if (availPaintTo>paintTo)availPaintTo=paintTo;
		//if (paintTo-paintFrom >2)System.out.println("Repainting "+(availPaintTo-paintFrom) +" values");
		if (rr.renderedImages != null && rr.renderedImages.length > 0) {
			// g.drawImage(rr.renderedImages[0],paintFrom,0,rr.renderedLength,height,null);
			//System.out.println("Draw images (width "+(availPaintTo-paintFrom)+") ...");
			for (int ii = 0; ii < rr.renderedImages.length; ii++) {
				int y = imgHeight * ii;
				boolean painted = g.drawImage(rr.renderedImages[ii], paintFrom,
						y, availPaintTo, imgHeight + y, paintFrom
								- rr.pixelOffset, 0, availPaintTo
								- rr.pixelOffset, imgHeight, this);
				if (!painted) {
//					System.out.println("Not complete");
				}
			}
			//System.out.println("Images drawn.");
		} else {
			//System.err.println("No fourier image available !!");
		}
		
		if(showTimeScaleGrid){
			GridTick<Long>[] ticks=null;
//			int tickIndex=0;
			if(timeScaleTickProvider !=null){
				ticks=timeScaleTickProvider.getScaleTicks(clipBounds.x, clipBounds.x+clipBounds.width);
			}
			//		 Paint time scale vertical lines
			if(ticks!=null){
				g.setColor(DEFAULT_TIME_GRID_COLOR);
				for(GridTick<Long> gt:ticks){
					int gtPos=gt.getPosition();
					g.drawLine(gtPos, 0, gtPos, height);
				}
			}
		}
		if (viewSelection != null) {
			// paint select constraint time labels
			g.setColor(Color.YELLOW);
			int pixelPos = viewSelection.getXStart();
			g.drawLine(pixelPos, 0, pixelPos, height);
			String startStr = formatPosition(viewSelection.getStart());
			String endStr = formatPosition(viewSelection.getEnd());
			
			g.drawString(startStr, pixelPos, height - fontHeight);
			pixelPos = viewSelection.getXEnd();
			g.drawLine(pixelPos, 0, pixelPos, height);
			g.drawString(endStr, pixelPos, height - fontHeight);
		}
		//System.out.println("Min: "+minVal+" Max: "+maxVal);
		// paint frame position marker
		if(!printMode){
		g.setColor(Color.RED);
		g.drawLine(pixelPosition, 0, pixelPosition, height);
		setCursor();
		}
	}


	public Dimension getMinimumSize() {
		return new Dimension(getWidth(), 200);
	}

	public Dimension getPreferredSize() {
		if (imgHeightSet) {
			return new Dimension(getWidth(), imgHeight);
		}
		return new Dimension(getWidth(), preferredHeight);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	public boolean getScrollableTracksViewportHeight() {
		// Scale the heigth of the view automatically
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth() {
		// the view has a fixed width depending on xZoom
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {

		return getPreferredSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle,
	 *      int, int)
	 */
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		// arbitrary value for now
		return 50;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle,
	 *      int, int)
	 */
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {

		return 1;
	}

	// /**
	// * Get X-zoom factor.
	// *
	// * @return X-zoom factor
	// */
	// public double getXZoom() {
	// return xZoom;
	// }

	/**
	 * Get sample rate.
	 * @return sample rate
	 */
	public float getSampleRate() {
		return sampleRate;
	}
	
	private boolean isInSelectorStart(int x){
        int xStart=viewSelection.getXStart();
        return( x >= xStart-selectorWidth && x<=xStart+selectorWidth);
    }

    private boolean isInSelectorEnd(int x){
        int xEnd=viewSelection.getXEnd();
        return( x >= xEnd-selectorWidth && x<=xEnd+selectorWidth);
    }
    
	private boolean isInSelectorLeft(int x){
        int xLeft=viewSelection.getXLeft();
        return( x >= xLeft-selectorWidth && x<=xLeft+selectorWidth);
    }

    private boolean isInSelectorRight(int x){
        int xRight=viewSelection.getXRight();
        return( x >= xRight-selectorWidth && x<=xRight+selectorWidth);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		long newSamplePosition = mapPixelToFrame(arg0.getX());
		audioSample.setFramePosition(newSamplePosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {

		dragStartEvent = null;
		pressedEvent = null;
		selStartMoveEvent = null;
		selEndMoveEvent = null;

		if (arg0.isPopupTrigger()) {
			Component parent=getParent();
			if(parent!=null){
			parent.dispatchEvent(arg0);
			}
		} else {
			int x = arg0.getX();
			if (viewSelection != null) {
				if (isInSelectorStart(x)) {
					selStartMoveEvent = arg0;
				} else if (isInSelectorEnd(x)) {
					selEndMoveEvent = arg0;
				} else {
					pressedEvent = arg0;
				}
				repaint();
			} else {
				pressedEvent = arg0;
			}
			//repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		if (arg0.isPopupTrigger()) {
			getParent().dispatchEvent(arg0);
		} else {

			if (dragStartEvent != null) {

				if (viewSelection != null) {
					viewSelection.limitTo(0, length);
					audioSample.setSelection(viewSelection.getSelection());
					if (DEBUG)
						System.out.println(viewSelection.getStart() + " - "
								+ viewSelection.getEnd() + " l: "
								+ viewSelection.getLength());
					repaint();
				}
			}

		}
		dragStartEvent = null;
		checkMouseResizeSelection(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public synchronized void mouseDragged(MouseEvent arg0) {
		// TODO repaint only neccessary areas
		if (pressedEvent != null) {
			dragStartEvent = pressedEvent;
			if (viewSelection == null) {
				viewSelection = new ViewSelection();
			}
			int dragStart=dragStartEvent.getX();
			viewSelection.setXStart(dragStart);
			int dragEnd=arg0.getX();
			int oldDragEnd=viewSelection.getXEnd();
			repaint(oldDragEnd,0,1,getHeight());
			viewSelection.setXEnd(dragEnd);
			repaint(dragStart-1,0,3,getHeight());
			repaint(dragEnd,0,1,getHeight());
		} else if (selStartMoveEvent != null) {
			dragStartEvent = selStartMoveEvent;
			viewSelection.setXStart(arg0.getX());
			setCursor();
			repaint();
		} else if (selEndMoveEvent != null) {
			dragStartEvent = selEndMoveEvent;
			viewSelection.setXEnd(arg0.getX());
			setCursor();
			repaint();
		}
	}
	private void checkMouseResizeSelection(MouseEvent arg0){
        int x = arg0.getX();
        if (viewSelection != null) {
            if (isInSelectorLeft(x)) {
                mouseOverResizeEast = null;
                mouseOverResizeWest = arg0;
            } else if (isInSelectorRight(x)) {
                mouseOverResizeEast = arg0;
                mouseOverResizeWest = null;
            } else {
                mouseOverResizeEast = null;
                mouseOverResizeWest = null;

            }
        }
    }
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent arg0) {
		checkMouseResizeSelection(arg0);
		setCursor();

	}

	// private void setSelection(Selection s) {
	// if (s == null) {
	// viewSelection = null;
	// } else {
	// viewSelection = new ViewSelection(s);
	// }
	// repaint();
	// }

	public JComponent[] getYScales() {
		return new JComponent[] { yScalesComponent };
	}
	
//	public JMenu[] getJMenus(){
//		
//	}

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

	protected synchronized void updateListeners(ActionEvent ae) {
		for(ActionListener listener:listenerList){
			listener.actionPerformed(ae);
		}
	}

	protected void rerender(){
	    updateYScales();
	    //  revalidate();
	    invalidate();
	    validate();
	    repaint();
	}
	
	public void doLayout(){
	    super.doLayout();
	    clear();
        if(yScales!=null && yScales.length>0){
            int compH=getSize().height;
            int scaleHeight=compH/yScales.length;
            int scaleWidth=0;
            for(int i=0;i<yScales.length;i++){
                JComponent yScale=yScales[i];
                if(yScale!=null){
                    if(yScale instanceof JDecimalAutoScale){
                        ((JDecimalAutoScale)yScale).setLength(scaleHeight);
                    }
                    Dimension scalePrefSize=yScale.getPreferredSize();
                    int sW=scalePrefSize.width;
                    if (sW>scaleWidth){
                        scaleWidth=sW;
                    }
                    yScale.setSize(sW,scaleHeight);
                }
            }
            for(int i=0;i<yScales.length;i++){
                JComponent yScale=yScales[i];
                if(yScale!=null){
                    Dimension ssize=yScale.getSize();
                    int sW=ssize.width;
//                    yScale.setBounds(scaleWidth-sW,scaleHeight*i,sW,scaleHeight);
                    yScale.setLocation(scaleWidth-sW, scaleHeight*i);
                }
            }
            Dimension preferredSize=new Dimension(scaleWidth,compH); 
//            yScalesComponent.setSize(scaleWidth,compH);
            yScalesComponent.setPreferredSize(preferredSize);
//            yScalesComponent.setBackground(Color.YELLOW);
            Container yP=yScalesComponent.getParent();
            if(yP!=null){
                Container clipCont=yP.getParent();
                if(clipCont!=null){
                    clipCont.invalidate();
                    clipCont.validate();
                }
                yP.invalidate();
               yP.validate();
            }
        }
        
    }

	public void update(FourierRendererEvent event) {
		repaint();
	}
	
	public void close(){
		if(renderer!=null)renderer.close();
//		maxFrequency=null;
		super.close();
	}

    public boolean isUseThread() {
        return useThread;
    }

    public void setUseThread(boolean useThread) {
        this.useThread = useThread;
    }
   
    public Double getMaxFrequency() {
        if(maxFrequency==null){
           if(audioSource!=null){
                // return Nyquist freq
                return (double)sampleRate/2;
            
           }
        }
        return maxFrequency;
    }

    public void setMaxFrequency(Double maxFrequency) {
        this.maxFrequency = maxFrequency;
        rerender();
    }
    
//    public JComponent getControlJComponent() {
//        if(controlComponent==null){
//            controlComponent=new FourierControlUI();
//        }
//        return controlComponent;
//    }
    public boolean  hasControlDialog(){
        return  true;
    }
    /**
     * Show a dialog component for plugin controls. 
     */
    public void showJControlDialog(Component parentComponent){
        controlComponent.showNonModalDialog((JDialog)null);
    }

    public double getDynamicRangeDB() {
        return dynamicRangeDB;
    }

    public void setDynamicRangeDB(double dynamicRangeDB) {
        this.dynamicRangeDB = dynamicRangeDB;
       rerender();
    }

    public double getEmphasisPerOctaveDB() {
        return emphasisPerOctaveDB;
    }

    public void setEmphasisPerOctaveDB(double emphasisPerOctaveDB) {
        this.emphasisPerOctaveDB = emphasisPerOctaveDB;
        rerender();
    }

    public double getEmphasisStartFrequency() {
        return emphasisStartFrequency;
    }

    public void setEmphasisStartFrequency(double emphasisStartFrequency) {
        this.emphasisStartFrequency = emphasisStartFrequency;
        rerender();
    }

    public double getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(double windowSize) {
        this.windowSize = windowSize;
        rerender();
    }
   
}
