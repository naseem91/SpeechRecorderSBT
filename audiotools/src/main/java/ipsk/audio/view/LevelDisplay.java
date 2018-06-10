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

package ipsk.audio.view;

import ipsk.audio.dsp.LevelInfo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;

public class LevelDisplay extends JPanel implements PropertyChangeListener {
	
    
    public static final boolean DEBUG=false;
    
    // Type I PPMs fall back 20 dB in 1.7 seconds.
    // See: https://en.wikipedia.org/wiki/Peak_programme_meter
    private static final float NEEDLE_DEFLECTION_RETURN_SPEED = (float) (20.0/1700.0);
    
	private static final int PEAK_HOLD_BAR_WIDTH=5;
	
	private LevelInfo levelInfo=null;
	
	private float level = Float.NEGATIVE_INFINITY;

	private float peakHold;

	private int horizontalBorder = 2;

	private float minDisplayLevel;

	private float peak;

	private static final float ln = (float) (20 / Math.log(10));

	

	//float minLevelIndB;
	private float yellowThresHoldIndB = -6;

	private float redThresHoldIndB = -3;
	
//	private float integrationValue;
	private float currentPaintLevel;
	private float currentPaintPeakLevel;
	
	// backward compatibility:
	// 
	private boolean useIntervalPeakLevel=false;


	private long lastPainted;
	
	private boolean levelInfoPropertyListener=false;
	
	private Color levelColorPeak;
	private Color levelColorPeakDarker;
	private Color levelColorWarn;
	private Color levelColorWarnDarker;
	private Color levelColor;
    private Color levelColorDarker;
	
	private float transparency=1.0f;
	
	private boolean active=true;

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}


	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
		repaint();
	}


	public float getTransparency() {
        return transparency;
    }


    public void setTransparency(float transparency) {
        this.transparency = transparency;
        updateColors();
    }


    public LevelDisplay(float minDisplayLevel) {
		super();
		setMinDisplayDezibelLevel(minDisplayLevel);
		level = minDisplayLevel;
		currentPaintLevel=minDisplayLevel;
		currentPaintPeakLevel=minDisplayLevel;
		resetPeak();
		resetPeakHold();
		lastPainted=System.currentTimeMillis();
		updateColors();
	}
	
	
	private Color transparentColor(Color opaqueColor,float transParency){
	    int transpInt=(int)(255.0f*transparency);
        
        return new Color(opaqueColor.getRed(),opaqueColor.getGreen(),opaqueColor.getBlue(),transpInt);
	}
	
	private void updateColors(){


	    levelColorPeak=Color.RED;
	    levelColorPeakDarker=Color.RED.darker();
	    levelColorWarn=Color.YELLOW;
	    levelColorWarnDarker=Color.YELLOW.darker();
	    levelColor=Color.GREEN;
	    levelColorDarker=Color.GREEN.darker();

	    if(transparency!=1.0f){


	        levelColorPeak=transparentColor(levelColorPeak, transparency);
	        levelColorPeakDarker=transparentColor(levelColorPeakDarker, transparency);
	        levelColorWarn=transparentColor(levelColorWarn, transparency);
	        levelColorWarnDarker=transparentColor(levelColorWarnDarker, transparency);
	        levelColor=transparentColor(levelColor, transparency);
	        levelColorDarker=transparentColor(levelColorDarker, transparency);
	    }

	}

	public Dimension getPreferredSize() {
		return new Dimension(10, 100);
	}

	public Dimension getMinimumSize() {

		return new Dimension(1, 10);
	}

	public void setMinDisplayDezibelLevel(float minDisplayLevel) {

		this.minDisplayLevel = minDisplayLevel;
//		integrationValue=-minDisplayLevel/FULL_DISPLAY_INTEGRATION;
		repaint();
	}
	
	
	private void levelInfoUpdate(){
	    if (levelInfo == null) {
            level = 0;
            peak = 0;
        } else {
            level = (float) (ln * Math.log((double) levelInfo.getLevel()));
            float linPeak;
            if(levelInfoPropertyListener || !useIntervalPeakLevel){
            	linPeak= levelInfo.getPeakLevel();
//            	System.out.print("buffer peak: ");
            }else{
            	linPeak= levelInfo.getIntervalPeakLevel();
//            	System.out.print("Interval peak: ");
            }
            peak = (float) (ln * Math.log((double) linPeak));
//           System.out.println(peak);
            double peakLevelHold=levelInfo.getPeakLevelHold();
            peakHold = (float) (ln * Math.log(peakLevelHold));
        }
        repaint();
	}
	
	public void setLevelInfo(LevelInfo li) {
	    setLevelInfo(li,true);
	    
	}
	public void setLevelInfo(LevelInfo li,boolean listenToPropertyChanges) {
		// TODO IEC 60268-18
		// time constant 5ms , (makes no sense for digital sources ?) and
		//decay time (Abklingzeit):
		//  1.5 seconds 1/10 of value (-20dB)
		
		if (li == null) {
		    if(levelInfo!=null){
		        levelInfo.removePropertyChangeListener(this);
		    }
		} else {
		    if(li!=levelInfo){
		        if(levelInfo!=null){
		            levelInfo.removePropertyChangeListener(this);
		        }
		        levelInfo=li;
		        if(listenToPropertyChanges)levelInfo.addPropertyChangeListener(this);
		    }
		}
		levelInfoPropertyListener=listenToPropertyChanges;
		levelInfoUpdate();
	}

	public LevelInfo getLevelInfo() {
		return new LevelInfo(level, peak);
	}

	public void setLevel(float val) {
		level = (float) (ln * Math.log((double) val));
		setLevelInDezibel(level);
	}

	public void setLevelInDezibel(float val) {
		level = val;
		if (level > peak)
			peak = level;
		repaint();
	}
	
	public void abandonDecay(){
		currentPaintLevel=minDisplayLevel;
		currentPaintPeakLevel=minDisplayLevel;
		repaint();
	}

	public void audioFormatChanged(AudioFormat af) {
	}

	public void resetPeak() {
		peak = minDisplayLevel;

		repaint();
	}

	public void resetPeakHold() {
		peakHold = minDisplayLevel;
		repaint();
	}

	public void setPeak(float newPeak) {
		peak = newPeak;
	}

	public float getPeak() {
		return peak;
	}
    
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!active){
        	return;
        }
        // System.out.println("Repaint "+level);
        Insets insets = getInsets();
        int currentWidth = getWidth() - insets.left - insets.right;
        int currentHeight = getHeight() - insets.top - insets.bottom;
        // float heightDiv = (float) currentHeight / (float) (twentydBSectors
        // +2);
        Graphics2D g2 = (Graphics2D) g;
        Rectangle2D r2;
        float paintLevel;
        float levelDiff=level-currentPaintLevel;
        long now=System.currentTimeMillis();
        float timeIntervall=now-lastPainted;
//        float step=integrationValue*timeIntervall;
        float step=NEEDLE_DEFLECTION_RETURN_SPEED*timeIntervall;
//        System.out.println("Time: "+timeIntervall+" step: "+step);
        
        if(levelDiff >=0){
        	//paintLevel=currentPaintLevel+step;
        	paintLevel=level;
        	if(paintLevel >level){
        		paintLevel=level;
        	}
        }else{
        	paintLevel=currentPaintLevel-step;
        	if(paintLevel <level){
        		paintLevel=level;
        	}
        }
        
        float peakLevelDiff=peak-currentPaintPeakLevel;
        if(peakLevelDiff >=0){
        	currentPaintPeakLevel=peak;
        	
        }else{
        	currentPaintPeakLevel=currentPaintPeakLevel-step;
        	if(currentPaintPeakLevel <peak){
        		currentPaintPeakLevel=peak;
        	}
        }
        
//        System.out.println("LveelDiff: "+levelDiff+" Level: "+level+" step: "+step+" piantLvl: "+paintLevel);
        if(paintLevel <minDisplayLevel){
    		paintLevel=minDisplayLevel;
    	}
        currentPaintLevel=paintLevel;
        lastPainted=now;
        float thYellow = ((float) yellowThresHoldIndB / minDisplayLevel)
                * currentHeight;
        float thRed = ((float) redThresHoldIndB / minDisplayLevel)
                * currentHeight;
        float levelBlank = (paintLevel / minDisplayLevel) * currentHeight;
        float peakPos = (currentPaintPeakLevel / minDisplayLevel) * currentHeight;
        if (peakPos <= currentHeight) {
            float levelPeakPosTmp = peakPos;

            float levelHeight = currentHeight - peakPos;
            float levelHeightTmp = levelHeight;
            // System.out.println(level+" "+currentHeight+" "+levelHeight+"
            // "+thYellow+" "+thRed);
            if (peakPos < thRed) {
                  
                g2.setPaint(levelColorPeakDarker);
                float h = thRed - levelPeakPosTmp;
                r2 = new Rectangle2D.Float(horizontalBorder, peakPos,
                        currentWidth - (2 * horizontalBorder), h);
                g2.fill(r2);
                levelPeakPosTmp = thRed;
                levelHeightTmp -= h;

            }
            if (peakPos < thYellow) {
                g2.setPaint(levelColorWarnDarker);
                float h = thYellow - levelPeakPosTmp;
                r2 = new Rectangle2D.Float(horizontalBorder, levelPeakPosTmp,
                        currentWidth - (2 * horizontalBorder), h);
                g2.fill(r2);
                levelPeakPosTmp = thYellow;
                levelHeightTmp -= h;
            }
            r2 = new Rectangle2D.Float(horizontalBorder, levelPeakPosTmp,
                    currentWidth - (2 * horizontalBorder), levelHeightTmp);
            g2.setPaint(levelColorDarker);
            g2.fill(r2);
        }
        if (levelBlank <= currentHeight) {
            float levelBlankTmp = levelBlank;

            float levelHeight = currentHeight - levelBlank;
            float levelHeightTmp = levelHeight;
            // System.out.println(level+" "+currentHeight+" "+levelHeight+"
            // "+thYellow+" "+thRed);
            if (levelBlank < thRed) {

                g2.setPaint(levelColorPeak);
                float h = thRed - levelBlankTmp;
                r2 = new Rectangle2D.Float(horizontalBorder, levelBlank,
                        currentWidth - (2 * horizontalBorder), h);
                g2.fill(r2);
                levelBlankTmp = thRed;
                levelHeightTmp -= h;

            }
            if (levelBlank < thYellow) {
                g2.setPaint(levelColorWarn);
                float h = thYellow - levelBlankTmp;
                r2 = new Rectangle2D.Float(horizontalBorder, levelBlankTmp,
                        currentWidth - (2 * horizontalBorder), h);
                g2.fill(r2);
                levelBlankTmp = thYellow;
                levelHeightTmp -= h;
            }
            r2 = new Rectangle2D.Float(horizontalBorder, levelBlankTmp,
                    currentWidth - (2 * horizontalBorder), levelHeightTmp);
            g2.setPaint(levelColor);
            g2.fill(r2);
        }
       
        float peakHoldPos = (peakHold / minDisplayLevel) * currentHeight;

        if (peakHoldPos <= currentHeight) {
            if (peakHoldPos < thRed) {
                g2.setPaint(levelColorPeak);
            } else if (peakHoldPos < thYellow) {
                g2.setPaint(levelColorWarn);
            } else {
                g2.setPaint(levelColor);
            }
            //System.out.println(level+" "+currentHeight+" "+levelHeight+"
            // "+thYellow+" "+thRed+ " "+peakPos);
            float x=horizontalBorder;
            float y=peakHoldPos;
            float w=currentWidth - (2 * horizontalBorder);
            float h=PEAK_HOLD_BAR_WIDTH;
//            System.out.println("peak hold: level="+peakHold+" "+x+","+y+","+w+"x"+h);
            r2 = new Rectangle2D.Float(horizontalBorder, peakHoldPos,
                    currentWidth - (2 * horizontalBorder), PEAK_HOLD_BAR_WIDTH);
            g2.fill(r2);
        }
    }

	/**
	 * Get minimum level stored in display.
	 * 
	 * @return minimum level of display
	 */
	public float getMinDisplayLevel() {
		return minDisplayLevel;
	}

	/**
	 * @return red threshold in dB
	 */
	public float getRedThresHoldIndB() {
		return redThresHoldIndB;
	}

	/**
	 * @return yellow threshold in dB
	 */
	public float getYellowThresHoldIndB() {
		return yellowThresHoldIndB;
	}

	/**
	 * @param f
	 */
	public void setMinDisplayLevel(float f) {
		setMinDisplayDezibelLevel(minDisplayLevel);
	}

	/**
	 * @param f
	 */
	public void setRedThresHoldIndB(float f) {
		redThresHoldIndB = f;
	}

	/**
	 * @param f
	 */
	public void setYellowThresHoldIndB(float f) {
		yellowThresHoldIndB = f;
	}

	public float getPeakHold() {
		return peakHold;
	}

	public void setPeakHold(float peakHold) {
		this.peakHold = peakHold;
	}
	
	/**
	 * @return the useIntervalPeakLevel
	 */
	public boolean isUseIntervalPeakLevel() {
		return useIntervalPeakLevel;
	}

	/**
	 * @param useIntervalPeakLevel the useIntervalPeakLevel to set
	 */
	public void setUseIntervalPeakLevel(boolean useIntervalPeakLevel) {
		this.useIntervalPeakLevel = useIntervalPeakLevel;
	}


    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent arg0) {
        if (DEBUG)System.out.println("PropertyChange");
        if(arg0.getSource()==levelInfo){
            levelInfoUpdate();
        }
    }
    
  
}
