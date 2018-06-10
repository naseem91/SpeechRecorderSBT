/*
 * Date  : Mar 19, 2010
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ips.incubator.audio.arr.clip.ui;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.ui.BasicAudioClipUI;
import ipsk.audio.dsp.FloatAudioInputStream;
import ipsk.audio.dsp.FloatRandomAccessStream;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;


/**
 * 
 * (Status: Incubation) Calculation not in render thread!!
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class EnergyAudioClipUI extends BasicAudioClipUI{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final boolean DEBUG = false;

 
   

    public String getName(){
        return "Energy density";
    }
    
    public EnergyAudioClipUI(AudioClip annotatedAudioClip) throws AudioFormatNotSupportedException, AudioSourceException {
        super(annotatedAudioClip);
    }
    
    /**
	 * 
	 */
	public EnergyAudioClipUI() {
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
        int w = getSize().width;
        int h= getSize().height;
        //System.out.println("paint "+w+"x"+h);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLUE);
        AudioSource audioSource=getAudioSample().getAudioSource();
        if(audioSource!=null){
        	double[] energyDensities=new double[w];
        	double maxenergyDensity=0.0;
        FloatRandomAccessStream fras;
			try {
				fras = new FloatRandomAccessStream(audioSource);
				int chs=fras.getChannels();
				long pixelFrames= mapPixelToFrame(1);
				double[][] buf=new double[(int) pixelFrames][chs];
				
				for(int p=0;p<w-1;p++){
					long startframe=mapPixelToFrame(p);
					long endFrame=mapPixelToFrame(p+1);
					fras.setFramePosition(startframe);
					int r=fras.readFrames(buf,0, (int) pixelFrames);
					double energy=0.0;
					for(int f=0;f<r;f++){
						double amp=buf[f][0];
						energy+=amp*amp;
					}
					double ed=energy/r;
					if(ed>maxenergyDensity){
						maxenergyDensity=ed;
					}
					energyDensities[p]=ed;
					
				
				}
				
				// data model calculated
				// paint
				for(int x=0;x<w-1;x++){
					int y1=(int) ((double)h*energyDensities[x]/maxenergyDensity);
					int y2=(int) ((double)h*energyDensities[x+1]/maxenergyDensity);
					g.drawLine(x, h-y1,x+1, h-y2);
				}
				
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
