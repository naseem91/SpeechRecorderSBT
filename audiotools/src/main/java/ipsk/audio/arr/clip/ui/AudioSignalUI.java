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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.Scrollable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.FileAudioSource;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.AudioClipListener;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.events.FramePositionChangedEvent;
import ipsk.audio.dsp.DSPUtils;
import ipsk.awt.GridTick;
import ipsk.awt.JScale;
import ipsk.awt.JScale.Orientation;
import ipsk.swing.action.tree.AbstractActionLeaf;
import ipsk.swing.action.tree.ActionFolder;
import ipsk.swing.action.tree.ActionTreeRoot;
import ipsk.swing.action.tree.RadioActionGroup;
import ipsk.swing.action.tree.RadioActionLeaf;
import ipsk.swing.scale.JDecimalAutoScale;
import ipsk.swing.scale.JMinimalScale;
import ipsk.util.LocalizableMessage;

public class AudioSignalUI extends BasicAudioClipUI implements Scrollable, MouseListener, MouseMotionListener, AudioClipUI,
		AudioClipListener, AudioSignalRendererListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8102370942833020055L;

	private final boolean DEBUG = false;

	// private final double DEFAULT_XZOOM = 400;

	private final int DEFAULT_BORDER_LENGTH = 5;

    private final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
	private final Color DEFAULT_SIGNAL_COLOR = Color.GREEN;

	private final Color DEFAULT_SIGNAL_COLOR_SELECTED = Color.YELLOW.darker();
	

//	private final Color DEFAULT_SELECT_COLOR = Color.YELLOW;

	//private final Color DEFAULT_YAXIS_COLOR = Color.GRAY;
	
	public enum AmplitudeScaleType{LINEAR,LOGARITHM};
	public static final int DEFAULT_BASELOG_LEVEL = -40;

	private boolean rendered;
	private int imgHeight;
	private int borderLength = DEFAULT_BORDER_LENGTH;

	private MouseEvent pressedEvent = null;

	private MouseEvent dragStartEvent = null;

	private MouseEvent selEndMoveEvent;

	private MouseEvent selStartMoveEvent;

	private MouseEvent mouseOverResizeWest;

	private MouseEvent mouseOverResizeEast;

	// private Vector listenerList;
	private int preferredHeight = 60;

	private boolean imgHeightSet = false;

//	private float yMin;
//
//	private float yMax;
//
//	private JLabel yMinLabel;
//
//	private JLabel yMaxLabel;

	private JMinimalScale[] yScales;
	private JPanel yScalesComponent;

	private AmplitudeScaleType amplitudeScaleType=AmplitudeScaleType.LINEAR;
	private int baseLogLevel=DEFAULT_BASELOG_LEVEL;
	
	private JPanel controlPanel;
//	private JComboBox scaleTypeComboBox;
	
	private ActionTreeRoot actionTree=new ActionTreeRoot();
	
	private boolean paintPolygons=true;
	
	private class SetAmplitudeScaleTypeLinearAction extends RadioActionLeaf{

	   public SetAmplitudeScaleTypeLinearAction() {
	       super(new LocalizableMessage("Linear amplitude scale"));
	   }
        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            setAmplitudeScaleType(AmplitudeScaleType.LINEAR);
        }  
	}
	
	private class SetAmplitudeScaleTypeLogarithmAction extends RadioActionLeaf{
	    public SetAmplitudeScaleTypeLogarithmAction(){
	        super(new LocalizableMessage("Logarithm amplitude scale"));
	    }
        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            setAmplitudeScaleType(AmplitudeScaleType.LOGARITHM);
        }    
    }
	
	private SetAmplitudeScaleTypeLinearAction setAmplitudeScaleTypeLinearAction=new SetAmplitudeScaleTypeLinearAction();
	private SetAmplitudeScaleTypeLogarithmAction setAmplitudeScaleTypeLogarithmAction=new SetAmplitudeScaleTypeLogarithmAction();
	
	private class SetBaseLogLevelAction extends AbstractActionLeaf{
        public SetBaseLogLevelAction(){
            super(new LocalizableMessage("Logarithm amplitude scale base level..."));
        }
        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
           doSetBaseLogLevel();
        }    
    }
    private SetBaseLogLevelAction setBaseLogLevelAction;
	
	
	public int getBaseLogLevel() {
		return baseLogLevel;
	}

	public void setBaseLogLevel(int baseLogLevel) {
		this.baseLogLevel = baseLogLevel;
		if(AmplitudeScaleType.LOGARITHM.equals(amplitudeScaleType)){
			updateYScales();
		}
		repaint();
	}

	private AudioSignalRenderer renderer;
	
	private boolean useThread=true;

	private int pixelPosition;
	private int selectorWidth=3;

    private SpinnerNumberModel amplitudeBaseLevelSpinnerModel;

    private JSpinner amplitudeBaseLevelSpinner;

    private boolean changeSelectionOnDrag=false;
	
	//private TickProvider timeTickProvider;

	public AudioSignalUI() {
		super();

		// xZoom = DEFAULT_XZOOM;
		rendered = false;
		setBackground(DEFAULT_BACKGROUND_COLOR);
		ActionFolder viewFolder=ActionFolder.buildTopLevelFolder(ActionFolder.VIEW_FOLDER_KEY);
		
//		actionNodes.add(setAmplitudeScaleTypeLinearAction);
//		actionNodes.add(setAmplitudeScaleTypeLogarithmAction);
		RadioActionGroup actionGroup=new RadioActionGroup();
		actionGroup.add(setAmplitudeScaleTypeLinearAction);
		actionGroup.add(setAmplitudeScaleTypeLogarithmAction);
		setAmplitudeScaleTypeLinearAction.setSelected(true);
		setAmplitudeScaleTypeLinearAction.setRadioActionGroup(actionGroup);
		setAmplitudeScaleTypeLogarithmAction.setRadioActionGroup(actionGroup);
		ActionFolder timeSignalFolder=new ActionFolder("time_signal", new LocalizableMessage("Time signal"));
		timeSignalFolder.add(setAmplitudeScaleTypeLinearAction);
		timeSignalFolder.add(setAmplitudeScaleTypeLogarithmAction);
		setBaseLogLevelAction=new SetBaseLogLevelAction();
		timeSignalFolder.add(setBaseLogLevelAction);
		amplitudeBaseLevelSpinnerModel=new SpinnerNumberModel(AudioSignalUI.DEFAULT_BASELOG_LEVEL,-120,0,1);
        amplitudeBaseLevelSpinner=new JSpinner(amplitudeBaseLevelSpinnerModel);
		viewFolder.add(timeSignalFolder);
		actionTree.add(viewFolder);
		listenerList = new Vector<ActionListener>();
//		addComponentListener(this);
//		yMin = -1;
//		yMax = 1;
//		yScale = new JPanel(new BorderLayout());
//		yMinLabel = new JLabel(Float.toString(yMin));
//		yScale.add(yMinLabel,BorderLayout.SOUTH);
//		yScale=new JAutoScale(JAutoScale.VERTICAL, -100, 100);
		yScalesComponent=new JPanel();
		yScalesComponent.setLayout(null);
		addMouseMotionListener(this);
		addMouseListener(this);
		
		
//		EnumVector<AmplitudeScaleType> scaleTypeEnumVector=new EnumVector<AmplitudeScaleType>(AmplitudeScaleType.class, "Default ("+AmplitudeScaleType.LINEAR.toString()+")");
//		scaleTypeComboBox=new JComboBox(scaleTypeEnumVector);
//		controlPanel=new JPanel();
//		GridBagConstraints gbcl=new GridBagConstraints();
//		gbcl.anchor = GridBagConstraints.WEST;
//		GridBagConstraints gbcv=new GridBagConstraints();
//        gbcv.anchor=GridBagConstraints.EAST;
//		controlPanel.add(new JLabel("Amplitude scale type:"),gbcl);
//	
//		gbcv.gridx=1;
//		controlPanel.add(scaleTypeComboBox,gbcv);
		
	}

	public AudioSignalUI(AudioClip audioSample)
			throws AudioFormatNotSupportedException, AudioSourceException {
		this();
		setAudioSample(audioSample);
	}
	
	public String getName(){
		return "Audio signal";
	}
	
	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
		imgHeightSet = true;
	}
	public AmplitudeScaleType getAmplitudeScaleType() {
		return amplitudeScaleType;
	}

	public void setAmplitudeScaleType(AmplitudeScaleType amplitudeScaleType) {
		this.amplitudeScaleType = amplitudeScaleType;
		setAmplitudeScaleTypeLinearAction.setEnabled(!AmplitudeScaleType.LINEAR.equals(amplitudeScaleType));
		setAmplitudeScaleTypeLogarithmAction.setEnabled(!AmplitudeScaleType.LOGARITHM.equals(amplitudeScaleType));
		updateYScales();
		repaint();
	}

	// public void setXZoom(double xZoom) {
	// clear();
	// super.setXZoom(xZoom);
	// revalidate();
	// repaint();
	// }

	


	public void clear() {
		if (DEBUG)
			System.out.print("Clearing screen...");
		clearScreen();
		if (DEBUG)
			System.out.println("O.K.");
		//audioSource = null;

	}

	public void clearScreen() {		
		setCursor();
		repaint();
	}

	public void audioClipChanged(AudioClipChangedEvent event) {

		super.audioClipChanged(event);
		if (event instanceof FramePositionChangedEvent) {
			setFramePosition(((FramePositionChangedEvent) event).getPosition());
		}else if (event instanceof AudioSourceChangedEvent) {
			if (DEBUG)
				System.out.print("Close...");
			close();
			clear();
			AudioSource as = ((AudioSourceChangedEvent) event).getAudioSource();
			if (as != null){
				try {
					if (renderer!=null)renderer.close();
					renderer = new AudioSignalRenderer(as,this);
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
		}
	}

	private void updateYScales(){
		yScalesComponent.removeAll();
		if(audioSource!=null){
			if(amplitudeScaleType==null || AmplitudeScaleType.LINEAR.equals(amplitudeScaleType)){
				yScales=new JMinimalScale[channels];
				for(int i=0;i<yScales.length;i++){
					yScales[i]=new JMinimalScale(Orientation.WEST,  BigDecimal.valueOf(-1, 0),BigDecimal.valueOf(1,0));

					yScalesComponent.add(yScales[i]);
				}
			}else if(AmplitudeScaleType.LOGARITHM.equals(amplitudeScaleType)){
				yScales=new JMinimalScale[channels*2];
				for(int ch=0;ch<channels;ch++){
					int i=ch*2;
					yScales[i]=new JMinimalScale(Orientation.WEST,  BigDecimal.valueOf(0, 0),BigDecimal.valueOf(baseLogLevel,0));
					yScalesComponent.add(yScales[i]);
					yScales[i+1]=new JMinimalScale(Orientation.WEST,  BigDecimal.valueOf(baseLogLevel, 0),BigDecimal.valueOf(0,0));
					yScalesComponent.add(yScales[i+1]);
				}
			}
		}
		doScalesLayout();
		yScalesComponent.revalidate();
		yScalesComponent.repaint();
	}
	
	private void doScalesLayout(){
	    int compH=getSize().height;
	    if(yScales!=null && yScales.length>0){

	        int scaleHeight=(compH-2*borderLength)/yScales.length;
	        
	        // compute scale width
	        int scaleWidth=0;
	        for(int i=0;i<yScales.length;i++){
	            JScale<BigDecimal> yScale=yScales[i];
	            if(yScale!=null){
	                if(yScale instanceof JDecimalAutoScale){
	                    ((JDecimalAutoScale)yScale).setLength(scaleHeight);
	                }
	                Dimension scalePrefSize=yScale.getPreferredSize();
	                int sW=scalePrefSize.width;
	                if (sW>scaleWidth){
	                    scaleWidth=sW;
	                }
	            }
	        }
	        
	        for(int i=0;i<yScales.length;i++){
                JMinimalScale yScale=yScales[i];
                if(yScale!=null){
                    int sYPos=borderLength+scaleHeight*i;
                    Dimension scalePrefSize=yScale.getPreferredSize();
                    int sW=scalePrefSize.width;
                    // right aligned 
                    yScale.setBounds(scaleWidth-sW,sYPos,sW,scaleHeight);
                    yScale.doLayout();
                }
            }
	        Dimension preferredSize=new Dimension(scaleWidth,compH);
//	        yScalesComponent.setSize(scaleWidth,compH);
	        yScalesComponent.setPreferredSize(preferredSize);
	    }else{
	        Dimension preferredSize=new Dimension(0,compH);
//	        yScalesComponent.setSize(0,compH);
	        yScalesComponent.setPreferredSize(preferredSize);
	    }
	}
	
	public void doLayout(){
	  super.doLayout();
	  clear();
	  updateYScales();
	  doScalesLayout();
	}
	
	public double getLengthInSeconds() {
	    return lengthInSeconds;
	}

	

	private void setCursor() {
		if (dragStartEvent != null) {
			if (dragStartEvent == selStartMoveEvent) {
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			} else if (dragStartEvent == selStartMoveEvent) {
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				//System.out.println("drag");
			}
		} else if (mouseOverResizeWest != null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			//System.out.println("west");
		} else if (mouseOverResizeEast != null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			//System.out.println("east");
		} else {
			//System.out.println("else");
			if (rendered) {

				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			} else {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
		}
	}
	private void setFramePosition(long position) {
		// framePosition = position;
		int oldPixelPosition = pixelPosition;
		
		pixelPosition = mapFrameToPixel(position);
		if (EventQueue.isDispatchThread()){
		paintImmediately(oldPixelPosition, 0, 1, getHeight());
		paintImmediately(pixelPosition, 0, 1, getHeight());
		}else{
			repaint(oldPixelPosition, 0, 1, getHeight());
			repaint(pixelPosition, 0, 1, getHeight());
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
		if (audioSample == null || audioSample.getAudioSource() == null || channels == 0 || pixelsPerFrame == 0.0 || renderer == null)
			return;
		GridTick<Long>[] ticks=null;
		
		FontMetrics gf = g.getFontMetrics();
		int fontHeight = gf.getHeight();
		int height = getHeight();

		imgHeight = (height - 2 * borderLength) / channels;
		
		int paintFrom = clipBounds.x - (int) pixelsPerFrame - 1;
		if (paintFrom < 0)
			paintFrom = 0;
		int paintTo = clipBounds.x + clipBounds.width + (int) pixelsPerFrame
				+ 1;
		if(DEBUG)System.out.println("Signal paint : "+paintFrom+" - "+paintTo);
		//setCursor();
		//System.out.println("width: "+clipBounds.width);
		//removeMouseListener(this);
		//removeMouseMotionListener(this);
		
//		 Paint time scale vertical lines
		if(showTimeScaleGrid && timeScaleTickProvider!=null){
			
			ticks=timeScaleTickProvider.getScaleTicks(clipBounds.x, clipBounds.x+clipBounds.width);
			if(ticks!=null){
			g.setColor(DEFAULT_TIME_GRID_COLOR);
			for(GridTick<?> gt:ticks){
				int gtPos=gt.getPosition();
				g.drawLine(gtPos, 0, gtPos, height);
			}
			}
		}

		AudioSignalRenderer.RenderResult rr = null;
		try {
			rr = renderer.render(paintFrom, paintTo, framesPerPixel,useThread,printMode);
		} catch (AudioSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rendered=rr.rendered;
		//if(!printMode)setCursor();
		//addMouseMotionListener(this);
		//addMouseListener(this);
		// AudioSignalRenderer.Value[] vals=renderer.getValues();
		//rendered = true;
		int[] minY = new int[channels];
		int[] maxY = new int[channels];
		int[][] polyX = new int[channels][4];
		int[][] polyY = new int[channels][4];
		
		g.setColor(DEFAULT_SIGNAL_COLOR);
		boolean firstValue = true;
		int valPos;
//		// find first min/max point value to paint
//		for (valPos = 0; valPos < vals.length - 1; valPos++) {
//			int valPixelPos = vals[valPos + 1].pixelPos;
//			if (valPixelPos > paintFrom)
//				break;
//		}
		int to=rr.offset+rr.length;
		//System.out.println("Paint "+rr.length);
		for (valPos=rr.offset; valPos < to; valPos++) {
			AudioSignalRenderer.Value v=(rr.values)[valPos];
			if (v == null) {
				continue;
			}
			//int pixelPos = v.pixelPos;
			int pixelPos=valPos+rr.pixelOffset;
			// paint selected segement in different color
			if (viewSelection != null) {
				if (viewSelection.isInXSelection(pixelPos)) {
					g.setColor(DEFAULT_SIGNAL_COLOR_SELECTED);
				} else {
					g.setColor(DEFAULT_SIGNAL_COLOR);
				}
			}
			
			for (int i = 0; i < channels; i++) {
//				minY[i] = (int) (((1-v.min[i]) /2) * (float) imgHeight
//						+ i * imgHeight + borderLength);
//				maxY[i] = (int) (((1 - v.max[i])/2) * (float) imgHeight
//						+ i * imgHeight + borderLength);

				double minLevel;
				double maxLevel;
				if(amplitudeScaleType.equals(AmplitudeScaleType.LINEAR)){
					minLevel=v.min[i];
					maxLevel=v.max[i];
				}else{
					
					double logLevelMin=DSPUtils.toPowerLevelInDB(Math.abs(v.min[i]));
					double logLevelMax=DSPUtils.toPowerLevelInDB(Math.abs(v.max[i]));
					double normDblevelMin=1-(logLevelMin/baseLogLevel);
					if(normDblevelMin<0)normDblevelMin=0;
					double normDblevelMax=1-(logLevelMax/baseLogLevel);
					if(normDblevelMax<0)normDblevelMax=0;
					if(v.min[i]<0)normDblevelMin=-normDblevelMin;
					if(v.max[i]<0)normDblevelMax=-normDblevelMax;
					minLevel= normDblevelMin;
					maxLevel= normDblevelMax;
				}
				minY[i] = (int) (((float) 0.5 - minLevel/2) * (float) imgHeight
						+ i * imgHeight + borderLength);
				maxY[i] = (int) (((float) 0.5 - maxLevel/2) * (float) imgHeight
						+ i * imgHeight + borderLength);
				if(paintPolygons){
				    // paint rendered values as polygons
				    // this works for all x-zoom values ( frame/pixel greater or
				    // less than 1)
				    if (firstValue) {
				        // We do not have the min/max values of the previous pixel
				        polyX[i][0] = pixelPos;
				        polyY[i][0] = minY[i];
				        polyX[i][1] = pixelPos;
				        polyY[i][1] = maxY[i];
				        polyX[i][2] = pixelPos;
				        polyY[i][2] = minY[i];
				        polyX[i][3] = pixelPos;
				        polyY[i][3] = maxY[i];
				        
				    } else {
				        polyX[i][0] = polyX[i][2];
				        polyY[i][0] = polyY[i][2];
				        polyX[i][1] = polyX[i][3];
				        polyY[i][1] = polyY[i][3];
				        polyX[i][2] = pixelPos;
				        polyY[i][2] = minY[i];
				        polyX[i][3] = pixelPos;
				        polyY[i][3] = maxY[i];
				    }

				    
//				    Polygon p = new Polygon(Arrays.copyOf(polyX[i],4), Arrays.copyOf(polyY[i],4), 4);
//				    
//				    g.drawPolygon(p);
				    
				    g.drawLine(polyX[i][0], polyY[i][0], polyX[i][1], polyY[i][1]);
				    g.drawLine(polyX[i][1], polyY[i][1], polyX[i][2], polyY[i][2]);
				    g.drawLine(polyX[i][2], polyY[i][2], polyX[i][3], polyY[i][3]);
				    g.drawLine(polyX[i][3], polyY[i][3], polyX[i][0], polyY[i][0]);
//				    

				}else{
				    g.drawLine(pixelPos, minY[i], pixelPos, maxY[i]);
				}
			}
			firstValue = false;
//			// ignore values after paintTo
//			if (paintTo < pixelPos)
//				break;
		}
		if (viewSelection != null) {
			// paint select constraint time labels
			g.setColor(Color.YELLOW);
			 ((Graphics2D)g).setRenderingHint(
	                  RenderingHints.KEY_TEXT_ANTIALIASING,
	                  RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
			int pixelPos = viewSelection.getXStart();
			g.drawLine(pixelPos, 0, pixelPos, height);
			String startStr = null;
			String endStr = null;
//			if(MediaLengthUnit.TIME.equals(mediaLengthUnit)){
//			    Double startInSeconds = new Double((double) (viewSelection
//                        .getStart())
//                        / sampleRate);
//               
//                Double endInSeconds = new Double((double) (viewSelection
//                        .getEnd())
//                        / sampleRate);
//			
//			    if (timeFormat == null) {
//	                startStr = startInSeconds.toString();
//	                endStr = endInSeconds.toString();
//	            } else {
//	                // seconds
//	                startStr = timeFormat.format(startInSeconds);
//	                endStr = timeFormat.format(endInSeconds);
//	            }
//			}else{
//				// frames
//				startStr = new Long(viewSelection.getStart()).toString();
//				endStr = new Long(viewSelection.getEnd()).toString();
//			}
			startStr=formatPosition(viewSelection.getStart());
			endStr=formatPosition(viewSelection.getEnd());
			g.drawString(startStr, pixelPos, height - fontHeight);
			String selLengthStr="Sel. length: "+formatPosition(viewSelection.getLength());
			g.drawString(selLengthStr,viewSelection.getXLeft(), 0 +  fontHeight);
			
			pixelPos = viewSelection.getXEnd();
			g.drawLine(pixelPos, 0, pixelPos, height);
			g.drawString(endStr, pixelPos, height - fontHeight);
			
		}
//		// Paint zero and full scale lines
//		g.setColor(DEFAULT_YAXIS_COLOR);
//		String minLabel;
//		String maxLabel;
//		String unit;
//		if(amplitudeScaleType.equals(AmplitudeScaleType.LINEAR)){
//			minLabel=Float.toString(yMin);
//			maxLabel=Float.toString(yMax);
//			unit="";
//		}else{
//			minLabel="0";
//			maxLabel="0";
//			unit=" dB";
//		}
//		for (int i = 0; i < channels; i++) {
//			int nullLineY = i * imgHeight + imgHeight / 2 + borderLength;
////			g.drawLine(clipBounds.x, nullLineY, clipBounds.x
////					+ (int) clipBounds.getWidth(), nullLineY);
//			if(amplitudeScaleType.equals(AmplitudeScaleType.LOGARITHM)){
//				String baseLevel=Double.toString(baseLogLevel);
//				g.drawString(baseLevel+unit, 0, nullLineY + fontHeight
//						+ 2);
//				g.drawString(baseLevel+unit, 0, nullLineY - 1);
//			}
//			int topScaleLineY = i * imgHeight + borderLength;
//			
//			g.drawString(maxLabel, 0, topScaleLineY + fontHeight
//					+ 2);
//			int bottomScaleLineY = topScaleLineY + imgHeight;
//			g.drawLine(clipBounds.x, topScaleLineY, clipBounds.x
//					+ (int) clipBounds.getWidth(), topScaleLineY);
//			g.drawLine(clipBounds.x, bottomScaleLineY, clipBounds.x
//					+ (int) clipBounds.getWidth(), bottomScaleLineY);
//			
//			
//			g.drawString(minLabel+unit, 0, bottomScaleLineY - 1);
//		}
		if(!printMode){
		// paint frame position marker	
		g.setColor(Color.RED);
		g.drawLine(pixelPosition, 0, pixelPosition, height);
		setCursor();
		}
	}


	public Dimension getMinimumSize() {
		return new Dimension(getWidth(), 3);
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
	
	private void doSetBaseLogLevel(){
	    int currentBaseLevel=getBaseLogLevel();
	    amplitudeBaseLevelSpinnerModel.setValue(currentBaseLevel);
	    int retVal=JOptionPane.showOptionDialog(this, amplitudeBaseLevelSpinner, "Logarithm base level",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE, null, null, null);
	    if(retVal==JOptionPane.OK_OPTION){
	        setBaseLogLevel((Integer)amplitudeBaseLevelSpinnerModel.getValue());
	    }else{
	        amplitudeBaseLevelSpinner.setValue(getBaseLogLevel());
	    }
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

	private void selectionByMouse(MouseEvent me){
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
            checkMouseResizeSelection(me);
        }
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
			getParent().dispatchEvent(arg0);
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

			selectionByMouse(arg0);

		}
		dragStartEvent = null;
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

			viewSelection.setXStart(dragStartEvent.getX());
			viewSelection.setXEnd(arg0.getX());

			repaint();
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
		if(changeSelectionOnDrag){
            selectionByMouse(arg0);
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
		Iterator<ActionListener> it = listenerList.iterator();
		while (it.hasNext()) {
			ActionListener listener = it.next();
			listener.actionPerformed(ae);
		}
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
//	 */
//	public void componentHidden(ComponentEvent arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
//	 */
//	public void componentMoved(ComponentEvent arg0) {
//		// TODO Auto-generated method stub
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
//	 */
//	public void componentResized(ComponentEvent arg0) {
//
//		clear();
//		doScalesLayout();
//		super.componentResized(arg0);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
//	 */
//	public void componentShown(ComponentEvent arg0) {
//		// TODO Auto-generated method stub
//
//	}

	public void update(AudioSignalRendererEvent event) {
		Throwable renderException=event.getRenderException();
		if(renderException!=null){
			JOptionPane.showMessageDialog(this,
					"Audio renderer error: \n" +renderException.getLocalizedMessage(),
					"Audio signal renderer error",
					JOptionPane.ERROR_MESSAGE);
		}else{
			if(AudioSignalRendererEvent.Type.DONE.equals(event.getType())){
				repaint();
				if(DEBUG)System.out.println("Repainted all");
			}else{
				int startPixle=event.getStartPixel();
				int len=event.getLen();
				if(len>0){
					repaint(startPixle, 0, len, getHeight());
					if(DEBUG)System.out.println("Repainted: "+startPixle+" "+len);
				}
			}
		}
		
	}

    public void close() {
        if(renderer!=null)renderer.close();
        //updateYScales();
        
    }
	public JComponent getControlJComponent() {
		return controlPanel;
	}

    public boolean isUseThread() {
        return useThread;
    }

    public void setUseThread(boolean useThread) {
        this.useThread = useThread;
    }
    
    public ActionTreeRoot getActionTree() {
        return actionTree;
    }
    
    public static void main(final String[] args){
    	 Runnable show=new Runnable() {
             
             public void run() {
                 JFrame f=new JFrame();
                 AudioClip ac=new AudioClip();
                 AudioSignalUI jfm=new AudioSignalUI();
                 f.getContentPane().add(jfm);
                 f.pack();
            
                 f.setVisible(true);
//                 AudioClip ac=new AudioClip(new FileAudioSource(new File(args[0])));
                 jfm.setAudioSample(ac);
                 ac.setAudioSource(new FileAudioSource(new File(args[0])));
             }
         };
         
         SwingUtilities.invokeLater(show);
    }

}
