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
 * Date  : Oct 24, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.dsp;

import ipsk.audio.AudioSourceException;
import ipsk.audio.arr.clip.AudioClip;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
// TODO documentation
public class AudioClipProcessor {

    public final int DEF_FRAME_BUF_SIZE=1024;
    public final double DEF_SNR_WINDOW_SIZE=0.1;
    
    private static double LN =20 / Math.log(10);
    private int bufSize=DEF_FRAME_BUF_SIZE;
    protected AudioClip audioClip;
    //private AudioBufferProcessor audioBufferProcessor;
    private double[] maxAmplitudes=null;
    private double[] minAmplitudes=null;
    
    
    private boolean calculateSBNR=false;
    private double signalToBackgroundNoiseRatioWindowSize=DEF_SNR_WINDOW_SIZE;
    private Double[] minWindowMeanPower=null;
    private double[] maxPower=null;
    private double[] energy=null;
    
    
    /**
     * 
     */
    public AudioClipProcessor(AudioClip audioClip) {
        super();
       this.audioClip=audioClip; 
      
    }
    
    public AudioClipDSPInfo process() throws AudioSourceException{

    	AudioClipDSPInfo info =null;
    	FloatAudioInputStream ais=audioClip.getFloatAudioInputStream();
    	if(ais!=null){
    		info=new AudioClipDSPInfo();
    	AudioFormat af=ais.getFormat();
    	long frameLength=ais.getFrameLength();
    	info.setAudioFormat(af);
    	info.setFrameLength(frameLength);
    	int channels=af.getChannels();
    	if(calculateSBNR){
    		// use SBNR window size as buffer size
    		double sampleRate=(double)af.getSampleRate();
    		bufSize=(int)(sampleRate*signalToBackgroundNoiseRatioWindowSize);
    	}
    	minAmplitudes=new double[channels];
    	maxAmplitudes=new double[channels];
    	maxPower=new double[channels];
    	energy=new double[channels];
    	for(int ch=0;ch<channels;ch++){
    		minAmplitudes[ch]=Double.POSITIVE_INFINITY;
    		//minAmplitudes[ch]=Double.MAX_VALUE;
    		maxAmplitudes[ch]=Double.NEGATIVE_INFINITY;
    		//maxAmplitudes[ch]=Double.MIN_VALUE;
    		
    	}
    	double[][]  buf=new double[bufSize][channels];

    	long frames=0;
    	try{
    		int r=0;
    		do{
    			int read=0;

    			double[] bufferEnergy=new double[channels];
    			
    			// fill buffer
    			do{
    				r=ais.read(buf,read,bufSize-read);
    				if(r==-1){
    					break;
    				}
    				read+=r;
    			}while(read<bufSize);

    			// process buffer
    			if(read>0){
    				frames+=read;
    				for (int i=0;i<read;i++){
    					for(int ch=0;ch<channels;ch++){
    						double val=buf[i][ch];
    						double power=val*val;
    						if(maxPower[ch] < power){
    							maxPower[ch]=power;
    						}
    						bufferEnergy[ch]+=power;
    						energy[ch]+=power;
    						if (minAmplitudes[ch]>val){
    							minAmplitudes[ch]=val;
    						}
    						if(maxAmplitudes[ch]<val){
    							maxAmplitudes[ch]=val;
    						}
    					}
    				}
    				if(calculateSBNR && read==bufSize){
    					// only use completely filled windows
    					if(minWindowMeanPower==null){
    						minWindowMeanPower=new Double[channels];
    					}

    					for(int ch=0;ch<channels;ch++){
    						//        					   System.out.println(bufferEnergy[ch]);
    						double bufferMeanPower=bufferEnergy[ch]/bufSize;
    						if(minWindowMeanPower[ch]==null || minWindowMeanPower[ch] > bufferMeanPower){
    							minWindowMeanPower[ch]=bufferMeanPower;
    						}
    						//        				   if(maxWindowMeanPower[ch]==null || maxWindowMeanPower[ch] < bufferMeanPower){
    						//        					   maxWindowMeanPower[ch]=bufferMeanPower;
    						//        				   }
    					}
    				}

    			}
    		}while(r!=-1);

    	}catch(IOException e){
    		throw new AudioSourceException(e);
    	}finally{
    		if(ais!=null) {
    			try {
    				ais.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    				throw new AudioSourceException(e);
    			}
    		}
    	}
    	info.setMinAmplitudes(minAmplitudes);
    	info.setMaxAmplitudes(maxAmplitudes);
    	info.setMinSegmentalMeanPower(minWindowMeanPower);
    	info.setMaxPower(maxPower);
    	//System.out.println(Double.toString(maxPower[0])+" "+minWindowMeanPower[0].toString());
    	audioClip.setClipDSPInfo(info);
    	}
    	return info;

    }
    
    /*
     * Returns level in dB. Input must be in > 0.0 and <= 1.0
     */
    public static double getLogarithmLevel(double linLevel){
        return  (float) (LN * Math.log((double) 2*linLevel));
    }
    /*
     * Returns level in dB. Input must be in > 0.0 and <= 1.0
     */
    public static float getLogarithmLevel(float linLevel){
        return  (float) (LN * Math.log((double) 2*linLevel));
    }

	public boolean isCalculateSBNR() {
		return calculateSBNR;
	}

	public void setCalculateSBNR(boolean calculateSBNR) {
		this.calculateSBNR = calculateSBNR;
	}

	public static void main(String args[]){
		
	}
	
}
