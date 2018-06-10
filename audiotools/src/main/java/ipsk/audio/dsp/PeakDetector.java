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
 * Date  : 17.11.2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.dsp;

import ipsk.audio.AudioFormatNotSupportedException;

import javax.sound.sampled.AudioFormat;

/**
 * Calculates peak levels of an audio signal.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class PeakDetector extends AudioFrameProcessor {

	//private final static boolean DEBUG = false;
	private float[] levels;

	private float[] peakLevels;

	protected static float ln = (float) (20 / Math.log(10));

	private float[] max;

	private float[] min;

	private double[] absSum;

	//private static double RMSfactor = Math.sqrt(2.0);

	/**
	 * Create peak detector
	 * 
	 * @param af
	 *            audio format
	 * @throws AudioFormatNotSupportedException
	 */
	public PeakDetector(AudioFormat af) throws AudioFormatNotSupportedException {
		super(af);
		levels = new float[channels];
		peakLevels = new float[channels];
		max = new float[channels];
		min = new float[channels];
		absSum = new double[channels];
	}

	/**
	 * Process a new data buffer. Only level and RMS level is set.
	 * @param data PCM coded data buffer
	 * @param offset offset in data
	 * @param length length of bytes to process
	 * @return array of level infos
	 */
	public LevelInfo[] processBuffer(byte[] data, int offset, int length) {
		for (int i = 0; i < channels; i++) {
			max[i] = Float.NEGATIVE_INFINITY;
			min[i] = Float.POSITIVE_INFINITY;
			absSum[i] = 0.0;
		}
		int bc = offset;
		int frames = length / frameSize;
		if (frames == 0)
			return null;
		for (int f = 0; f < frames; f++) {
			processFrame(getFloatValues(data, bc));
			bc += frameSize;
		}
		LevelInfo[] levelInfos = new LevelInfo[channels];
		for (int i = 0; i < channels; i++) {

//			levelInfos[i] = new LevelInfo(
//					(float) ((absSum[i] / frames) * RMSfactor), Math.max(Math
//							.abs(max[i]), Math.abs(min[i])) * 2);
			levelInfos[i] = new LevelInfo(
                    (float) (absSum[i] / frames), Math.max(Math
                            .abs(max[i]), Math.abs(min[i])) * 2);
		}

		return levelInfos;
	}
	/**
	 * Process a new data buffer. Only level and RMS level is set.
	 * This method prevents the peak detector from allocating new level info objects.
	 * The peak level hold value is set if current peak level is higher.
	 * @param data PCM coded data buffer
	 * @param offset offset in data
	 * @param length length of bytes to process
	 * @param levelInfos array of level infos
	 */
    public void processBuffer(byte[] data, int offset, int length,LevelInfo[] levelInfos) {
        if (levelInfos.length!=channels){
            throw new IllegalArgumentException("Level info array must match channel count !");
        }
        for (int i = 0; i < channels; i++) {
            max[i] = Float.NEGATIVE_INFINITY;
            min[i] = Float.POSITIVE_INFINITY;
            absSum[i] = 0.0;
        }
        int bc = offset;
        int frames = length / frameSize;
        if (frames == 0){
            for(int i=0;i<levelInfos.length;i++){
            levelInfos[i].setLevel(0);
            levelInfos[i].setPeakLevel(0);
            }
        }
        for (int f = 0; f < frames; f++) {
            processFrame(getFloatValues(data, bc));
            bc += frameSize;
        }
     
        for (int i = 0; i < channels; i++) {
           // levelInfos[i].setLevel((float) ((absSum[i] / frames) * RMSfactor));
        	LevelInfo li=levelInfos[i];
            li.setLevel((float) (absSum[i] / frames));
            float peakLevel = (Math.max(Math
                    .abs(max[i]), Math.abs(min[i]))*2);
            li.setPeakLevel(peakLevel);
            float currentPeakLevelHold=li.getPeakLevelHold();
				if (currentPeakLevelHold < peakLevel) {
					li.setPeakLevelHold(peakLevel);
				}
        }

    }
    
	/**
	 * Process data buffer.
	 * @param data PCM coded data buffer
	 * @param offset offset in data
	 * @param length length of bytes to process
	 */
	public void process(byte[] data, int offset, int length) {
		for (int i = 0; i < channels; i++) {
			max[i] = Float.NEGATIVE_INFINITY;
			min[i] = Float.POSITIVE_INFINITY;
			absSum[i] = 0.0;
		}
		int bc = offset;
		int frames = length / frameSize;
		for (int f = 0; f < frames; f++) {
			processFrame(getFloatValues(data, bc));
			bc += frameSize;
		}
		for (int i = 0; i < channels; i++) {
			//levels[i] = (float) ((absSum[i] / frames) * RMSfactor);
			levels[i] = (float) (absSum[i] / frames);
			peakLevels[i] = Math.max(Math.abs(max[i]), Math.abs(min[i])) * 2;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioBufferProcessor#processFrame(float[])
	 */
	protected void processFrame(float[] f) {
		for (int i = 0; i < f.length; i++) {
			absSum[i] += Math.abs(f[i]);
			if (f[i] > max[i])
				max[i] = f[i];
			if (f[i] < min[i])
				min[i] = f[i];
		}
	}

	public float[] getPeakLevels() {
		return peakLevels;
	}

	/**
	 * Get levels.
	 * @return array of levels
	 */
	public float[] getLevels() {
		return levels;
	}

	/**
	 * Set levels.
	 * @param levels array of levels
	 */
	public void setLevels(float[] levels) {
		this.levels = levels;
	}
}
