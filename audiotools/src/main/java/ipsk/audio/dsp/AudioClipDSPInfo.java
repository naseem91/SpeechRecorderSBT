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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;


/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class AudioClipDSPInfo {
	public static double LN = 20 / Math.log(10);
    protected AudioFormat audioFormat;
    protected double[] minAmplitudes;
    protected double[] maxAmplitudes;
    
    private Double[] minSegmentalMeanPower=null;
    private double[] maxPower=null;

   
    protected long frameLength;
    
    /**
     * 
     */
    public AudioClipDSPInfo() {
        super();
       audioFormat=null;
       frameLength=AudioSystem.NOT_SPECIFIED; 
    }

    public long getFrameLength() {
        return frameLength;
    }
    public void setFrameLength(long frameLength) {
        this.frameLength = frameLength;
    }
    
    
    public double getFrameLengthInSeconds(){
        return (double)frameLength /audioFormat.getFrameRate();
    }
   
   
   
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }
    public void setAudioFormat(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }
    
    public double getNormalizedMaxPeakLevelOfAllChannels(){
    	double[] maxPeakLevels=getNormalizedMaxPeakLevels();
    	double maxPeakLevel=0;
    	for(double maxChPeakLevel:maxPeakLevels){
    		if(maxPeakLevel<maxChPeakLevel){
    			maxPeakLevel=maxChPeakLevel;
    		}
    	}
    	return maxPeakLevel;
    }
    public double[] getNormalizedMaxPeakLevels() {
        int chs=audioFormat.getChannels();
        double[] maxNormPeakLevels=new double[chs];
        for(int c=0;c<audioFormat.getChannels();c++){
            maxNormPeakLevels[c]=2*Math.max(Math.abs(minAmplitudes[c]),Math.abs(maxAmplitudes[c]));
        }
        return maxNormPeakLevels;
    }
    
    public double[] getMaxPeakLevels() {
        int chs=audioFormat.getChannels();
        double[] maxPeakLevels=new double[chs];
        for(int c=0;c<audioFormat.getChannels();c++){
            maxPeakLevels[c]=Math.max(Math.abs(minAmplitudes[c]),Math.abs(maxAmplitudes[c]));
        }
        return maxPeakLevels;
    }
   
    public double[] getMinPeakLevels() {
        int chs=audioFormat.getChannels();
        double[] minPeakLevels=new double[chs];
        for(int c=0;c<chs;c++){
            minPeakLevels[c]=Math.min(Math.abs(minAmplitudes[c]),Math.abs(maxAmplitudes[c]));
        }
        return minPeakLevels;
    }
    
    /**
     * Returns minimal amplitudes in the range -1.0 ... 1.0 for each channel
     * @return normalized minimal amplitudes
     */
    public double[] getMinNormalizedAmplitudes() {
    	double[] mnas=null;
        if(minAmplitudes!=null){
        	mnas=new double[minAmplitudes.length];
        	for(int i=0;i<mnas.length;i++){
        		mnas[i]=minAmplitudes[i]*2;
        	}
        }
        return mnas;
    }
    
    /**
     * Returns maximal amplitudes in the range -1.0 ... 1.0 for each channel
     * @return normalized maximal amplitudes
     */
    public double[] getMaxNormalizedAmplitudes() {
    	double[] mnas=null;
        if(maxAmplitudes!=null){
        	mnas=new double[maxAmplitudes.length];
        	for(int i=0;i<mnas.length;i++){
        		mnas[i]=maxAmplitudes[i]*2;
        	}
        }
        return mnas;
    }

    /**
     * Return minimal amplitudes  in the range -0.5 ... 0.5 for each channel
     * @return minimal amplitudes
     */
    public double[] getMinAmplitudes() {
        return minAmplitudes;
    }

    /**
     * @param minAmplitudes the minAmplitudes to set
     */
    public void setMinAmplitudes(double[] minAmplitudes) {
        this.minAmplitudes = minAmplitudes;
    }

    /**
     * @return the maxAmplitudes
     */
    public double[] getMaxAmplitudes() {
        return maxAmplitudes;
    }

    /**
     * @param maxAmplitudes the maxAmplitudes to set
     */
    public void setMaxAmplitudes(double[] maxAmplitudes) {
        this.maxAmplitudes = maxAmplitudes;
    }
    
    /*
     * Returns max amplitudes in dB.
     */
    public double[] getMaxLogarithmLevels(){
   
    	  int chs=maxAmplitudes.length;
          double[] maxLogLevels=new double[chs];
          
          for(int c=0;c<chs;c++){
        	  
              maxLogLevels[c]=(LN * Math.log(Math.abs(2*maxAmplitudes[c])));
          }
          return maxLogLevels;
        
    }
   
    /*
     * Returns min amplitudes in dB.
     */
    public double[] getMinLogarithmLevels(){
    	  //int chs=audioFormat.getChannels();
       
          
          int chs=minAmplitudes.length;
          double[] minLogLevels=new double[chs];
          for(int c=0;c<chs;c++){
        	  
              minLogLevels[c]=(LN * Math.log(Math.abs(2*minAmplitudes[c])));
          }
          return minLogLevels;
        
    }

    public Double[] getEstimatedLogarithmSignalToBackgroundNoiseRatio() {
    	Double[] logSNR=null;
    	Double[] snrRatio=getEstimatedSignalToBackgroundNoiseRatio();
    	if(snrRatio!=null){
    		logSNR=new Double[snrRatio.length];
    		for(int c=0;c<snrRatio.length;c++){
    			if(snrRatio[c]!=null){
    			logSNR[c]=10*Math.log10(snrRatio[c]);
    			}
    		}
    	}
    	return logSNR;
	}
    
    /**
     * Returns an estimated value for the ratio between the maximum power and the minimum mean power in small window (typical 10ms) measured in the audio clip.
     * We use this value to distinguish different recording environments of channels.
     * @return estimated SNR value
     */
	public Double[] getEstimatedSignalToBackgroundNoiseRatio() {
		Double[] snrs=null;
		if(maxPower!=null && minSegmentalMeanPower !=null){
			snrs=new Double[minSegmentalMeanPower.length];
			for(int c=0;c<snrs.length;c++){
				if(minSegmentalMeanPower[c]!=null && maxPower[c]>minSegmentalMeanPower[c]){
					//System.out.println(maxPower[c]+" / "+minSegmentalMeanPower[c]);
				snrs[c]=(maxPower[c]-minSegmentalMeanPower[c])/minSegmentalMeanPower[c];
				}
			}
		}
		return snrs;
	}

	public Double[] getMinSegmentalMeanPower() {
		return minSegmentalMeanPower;
	}

	public void setMinSegmentalMeanPower(Double[] minSegmentalMeanPower) {
		this.minSegmentalMeanPower = minSegmentalMeanPower;
	}

	public double[] getMaxPower() {
		return maxPower;
	}

	public void setMaxPower(double[] maxPower) {
		this.maxPower = maxPower;
	}

	public String toString(){
		StringBuffer sb=new StringBuffer("Audio clip DSP info:\n");
		sb.append("Audio format: "+audioFormat+"\n");
		int channels=audioFormat.getChannels();
		for (int i = 0; i < channels; i++) {
            sb.append("Channel "+Integer.toString(i)+":\n");
           	sb.append("Max amplitude:\n");
            sb.append(maxAmplitudes[i]*2);
         // TODO complete !!
		}
		return sb.toString();
	}

    
}
