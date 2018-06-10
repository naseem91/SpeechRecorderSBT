//    IPS Java Audio Tools
// 	  (c) Copyright 2011
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.audio.dsp;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import ipsk.audio.io.push.FloatAudioOutputStream;

/**
 * @author klausj
 *
 */
public class LevelMeasureFloatAudioOutputStream implements
        FloatAudioOutputStream{

    protected static double LN = 20 / Math.log(10);

    private double[] max;

    private double[] min;

    private double[] absSum;
    
    private int channels;
    
    private LevelInfo[] levelInfos; 
    
    private LevelInfosBean levelInfosBean=new LevelInfosBean();
    
    /* (non-Javadoc)
     * @see ipsk.io.InterleavedFloatOutputStream#setChannels(int)
     */
    public void setChannels(int channels) {
    	if(this.channels!=channels){
    		this.channels=channels;
    		max = new double[channels];
    		min = new double[channels];
    		absSum = new double[channels];
    		levelInfos=new LevelInfo[channels];
    		for(int i=0;i<channels;i++){
    			levelInfos[i]=new LevelInfo();
    		}
    		levelInfosBean.setLevelInfos(levelInfos);
    	}
    }

    /* (non-Javadoc)
     * @see ipsk.io.InterleavedFloatOutputStream#write(double[][], int, int)
     */
    public void write(double[][] buf, int offset, int len) throws IOException {
        if (levelInfos.length!=channels){
            throw new IllegalArgumentException("Level info array must match channel count !");
        }
        for (int i = 0; i < channels; i++) {
            max[i] = Float.NEGATIVE_INFINITY;
            min[i] = Float.POSITIVE_INFINITY;
            absSum[i] = 0.0;
        }
        int frames = len;
        if (frames == 0){
            for(int i=0;i<levelInfos.length;i++){
            levelInfos[i].setLevel(0);
            levelInfos[i].setPeakLevel(0);
            }
        }
        int ch;
        for (int i = 0; i < frames; i++) {
            for(ch=0;ch<channels;ch++){
                double v=buf[offset+i][ch];
                absSum[ch] += Math.abs(v);
            if (v > max[ch])
                max[ch] = v;
            if (v < min[ch])
                min[ch] = v;
            }
        }
        for (ch = 0; ch< channels; ch++) {
            
           // levelInfos[i].setLevel((float) ((absSum[i] / frames) * RMSfactor));
            LevelInfo li=levelInfos[ch];
            li.setLevel((float) (absSum[ch] / frames));
            double peakLevel = (Math.max(Math
                    .abs(max[ch]), Math.abs(min[ch])));
            li.setPeakLevel((float)peakLevel);
            double currentPeakLevelHold=li.getPeakLevelHold();
                if (currentPeakLevelHold < peakLevel) {
                    li.setPeakLevelHold((float)peakLevel);
                }
        }

    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    public void close() throws IOException {
        for(int i=0;i<channels;i++){
            levelInfos[i].setLevel(0f);
            levelInfos[i].setPeakLevel(0f);
        }
    }

    /* (non-Javadoc)
     * @see java.io.Flushable#flush()
     */
    public void flush() throws IOException {
       //
    }

    /* (non-Javadoc)
     * @see ipsk.audio.io.push.FloatAudioOutputStream#setAudioFormat(javax.sound.sampled.AudioFormat)
     */
    public void setAudioFormat(AudioFormat audioFormat) {
        setChannels(audioFormat.getChannels()); 
        //System.out.println("Set audio format");
    }

    public LevelInfosBean getLevelInfosBean() {
        return levelInfosBean;
    }

    public void setLevelInfosBean(LevelInfosBean levelInfosBean) {
        this.levelInfosBean = levelInfosBean;
    }

  

   
}
