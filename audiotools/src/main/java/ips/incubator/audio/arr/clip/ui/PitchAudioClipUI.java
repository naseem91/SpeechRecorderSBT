/*
 * Date  : Mar 19, 2010
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ips.incubator.audio.arr.clip.ui;

import ips.dsp.AutoCorrelator;
import ips.dsp.AutoCorrelator.AutoCorrelationResult;
import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.ui.BasicAudioClipUI;
import ipsk.audio.dsp.FloatRandomAccessStream;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;



/**
 * (Status: Incubation) Calculation not in render thread!!
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class PitchAudioClipUI extends BasicAudioClipUI{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final boolean DEBUG = false;

    private double startCorrTime=0.004; // 8ms -
    private double endCorrTime=0.012; // 10ms

    private double corrLenTime=0.1;

    private double SILENCE_THRESHOLD=1.0/3.0;
    public String getName(){
        return "Pitch energy";
    }
    
    public PitchAudioClipUI(AudioClip annotatedAudioClip) throws AudioFormatNotSupportedException, AudioSourceException {
        super(annotatedAudioClip);
    }
    
    /**
	 * 
	 */
	public PitchAudioClipUI() {
		super();
	}

	public void setAudioClip(AudioClip annotatedAudioClip){
       
        super.setAudioSample(annotatedAudioClip);
     
    }
    
//    /* (non-Javadoc)
//     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
//     */
//    public void componentResized(ComponentEvent arg0) {
//       super.componentResized(arg0);
//       repaint();
//    }
  
    public Dimension getPreferredSize(){
        int prefHeight=100;
      
        return new Dimension(0,prefHeight);
    }

    public Dimension getMinimumSize(){
        return getPreferredSize();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g;
        int w = getSize().width;
        int h= getSize().height;
        Stroke defStroke=g2.getStroke();
        Stroke bStroke=new BasicStroke((float) 3.0);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLUE);
        AudioSource audioSource=getAudioSample().getAudioSource();
        if(audioSource!=null){
        	
        	double[] energyDensities=new double[w];
        	double maxenergyDensity=0.0;
        FloatRandomAccessStream fras;
        
			try {
				float sampleRate=audioSource.getFormat().getSampleRate();
				fras = new FloatRandomAccessStream(audioSource);
				int chs=fras.getChannels();
				
				long corrLenFrames=(long) (corrLenTime*sampleRate);
				long corrStartFrames=(long)(startCorrTime*sampleRate);
				long corrEndFrames=(long)(endCorrTime*sampleRate);
				long corrInterval=corrEndFrames-corrStartFrames;
				
				long bufLen=corrLenFrames+corrEndFrames;
				int paintOffset=(int) (bufLen/2);
				
				double[][] buf=new double[(int) bufLen][chs];
				double[] corrBuf=new double[(int) bufLen];
				long frameLength=audioSource.getFrameLength();
				
				int segCnt=(int) (frameLength/corrLenFrames);
				
				
				double maxEnergy=0.0;
				double maxCorrEnergy=0.0;
				double maxCorr=0.0;
				AutoCorrelationResult[] ress=new AutoCorrelationResult[segCnt];
				for(int s=0;s<segCnt;s++){
					long framePos=s*corrLenFrames;
					fras.setFramePosition(framePos);
					fras.readFrames(buf, 0,(int) bufLen);
					for(int i=0;i<bufLen;i++){
						corrBuf[i]=buf[i][0];
					}
					AutoCorrelator.AutoCorrelationResult res=AutoCorrelator.autoCorrelate(corrBuf, 0, (int)corrLenFrames, (int)corrStartFrames, (int)corrEndFrames);
					ress[s]=res;
					double e=res.getEnergy();
					if(e>maxEnergy){
						maxEnergy=e;
					}
					double ce=res.getCorrEnergyMax();
					if(ce>maxCorrEnergy){
						maxCorrEnergy=ce;
					}
					
					double corr=res.correlation();
					if(corr>maxCorr){
						maxCorr=corr;
					}
					
					
				}
				
				// data model calculated
				// paint
				g.setColor(Color.DARK_GRAY);
				int l1=h/3;
				int l2=h*2/3;
				g.drawLine(0, l1,w , l1);
				g.drawLine(0, l2,w , l2);
				g.setColor(Color.BLUE);
				for(int i=0;i<segCnt-1;i++){
					AutoCorrelator.AutoCorrelationResult res1=ress[i];
					AutoCorrelator.AutoCorrelationResult res2=ress[i+1];
					long framePos1=paintOffset+(i*corrLenFrames);
					int x1=mapFrameToPixel(framePos1);
					long framePos2=paintOffset+((i+1)*corrLenFrames);
					int x2=mapFrameToPixel(framePos2);
					double c1=res1.correlation();
					double c2=res2.correlation();
//					double c1=res1.getCorrEnergyMax();
//					double c2=res2.getCorrEnergyMax();
					if(c1>SILENCE_THRESHOLD || c2>SILENCE_THRESHOLD){
						g2.setStroke(bStroke);
						g.setColor(Color.BLUE);
						int y1=(int) ((double)h*res1.getPositionMax()/corrInterval);
						int y2=(int) ((double)h*res2.getPositionMax()/corrInterval);
						g.drawLine(x1, h-y1,x2, h-y2);
						g2.setColor(Color.RED);
						
					}else{
						g2.setColor(Color.GREEN);
						g2.setStroke(defStroke);
					}
					int y1=(int) ((double)h*res1.correlation()/maxCorr);
					int y2=(int) ((double)h*res2.correlation()/maxCorr);
					g.drawLine(x1, h-y1,x2, h-y2);
					
					g.setColor(Color.ORANGE);
					y1=(int) ((double)h*res1.getEnergy()/maxEnergy);
                    y2=(int) ((double)h*res2.getEnergy()/maxEnergy);
                    g.drawLine(x1, h-y1,x2, h-y2);
				}
				g.setColor(Color.BLACK);
				g2.setStroke(defStroke);
				g.drawString("Corr: "+Double.toString(maxCorr), 5, 20);
				g.drawString("Ener: "+Double.toString(maxEnergy), 5, 40);
				
			} catch (AudioSourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AudioFormatNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }

  
    /* (non-Javadoc)
     * @see ipsk.audio.arr.clip.ui.AudioClipUI#isPreferredFixedHeight()
     */
    public boolean isPreferredFixedHeight() {
        return false;
    }
    
  
}
