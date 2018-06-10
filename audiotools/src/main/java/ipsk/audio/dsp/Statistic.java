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

package ipsk.audio.dsp;

/**
 * Contains values concerning the level of an audio buffer.
 * @see AudioBufferProcessor
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

/**
 * Create new statistic.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */

public class Statistic {

	/**
	 * Start position of corresponding audio buffer.
	 */
	public long framePosition = -1;

	/**
	 * Number of channels of the audio data.
	 */
	public int channels = -1;

	/**
	 * Number of frames.
	 */
	public int frames = -1;

	/**
	 * The maximum amplitude in the buffer.
	 */
	public float maxValue[] = null;

	/**
	 * The minimum amplitude.
	 */
	public float minValue[] = null;

	/**
	 * Normalized (0.0 - 1.0) linear minimal level.
	 */
	public float linMinLevel[] = null;

	/**
	 * Normalized (0.0 - 1.0) linear maximal level.
	 */
	public float linMaxLevel[] = null;

	/**
	 * Normalized minimum logarithmic level.
	 */
	public float logMinLevel[] = null;

	/**
	 * Normalized maximum logarithmic level.
	 */
	public float logMaxLevel[] = null;

	/**
	 * Creates new statistic object.
	 */
	public Statistic() {
	}

	/**
	 * Creates new statistic object.
	 * 
	 * @param channels
	 *            number of audio channels
	 */
	public Statistic(int channels) {
		this.channels = channels;
		maxValue = new float[channels];
		minValue = new float[channels];
		linMinLevel = new float[channels];
		linMaxLevel = new float[channels];
		logMinLevel = new float[channels];
		logMaxLevel = new float[channels];
	}

	/**
	 * String representation.
	 * 
	 * @return representation
	 */
	public String toString() {
		String r = super.toString();
		for (int i = 0; i < channels; i++) {
			r = r.concat(",\n maxLevel[" + i + "]: " + linMaxLevel[i] + "="
					+ logMaxLevel[i] + "dB,\n minLevel[" + i + "]: "
					+ logMinLevel[i] + "dB");
		}
		return r;
	}

	/**
	 * Get number of channels.
	 * 
	 * @return channels
	 */
	public int getChannels() {
		return channels;
	}

	/**
	 * Get frame position.
	 * 
	 * @return frame position
	 */
	public long getFramePosition() {
		return framePosition;
	}

	/**
	 * Get number of frames.
	 * 
	 * @return frames
	 */
	public int getFrames() {
		return frames;
	}

	/**
	 * Get maximum level.
	 * 
	 * @return maximum level
	 */
	public float[] getLinMaxLevel() {
		return linMaxLevel;
	}

	/**
	 * Get minimum level.
	 * 
	 * @return minimum level
	 */
	public float[] getLinMinLevel() {
		return linMinLevel;
	}

	/**
	 * Get logarythmic maximum level.
	 * 
	 * @return log. maximum level
	 */
	public float[] getLogMaxLevel() {
		return logMaxLevel;
	}

	/**
	 * Get logarythmic minimum level.
	 * 
	 * @return log. minimum level
	 */
	public float[] getLogMinLevel() {
		return logMinLevel;
	}

	/**
	 * Get maximum value.
	 * 
	 * @return max value
	 */
	public float[] getMaxValue() {
		return maxValue;
	}

	/**
	 * Get minimum value.
	 * 
	 * @return min value
	 */
	public float[] getMinValue() {
		return minValue;
	}

	/**
	 * Set number of channels.
	 * 
	 * @param i
	 *            number of channels
	 */
	public void setChannels(int i) {
		channels = i;
	}

	/**
	 * Set frame position.
	 * 
	 * @param l
	 *            new frame position
	 */
	public void setFramePosition(long l) {
		framePosition = l;
	}

	/**
	 * Set numbner of frames.
	 * 
	 * @param i
	 *            number of frames
	 */
	public void setFrames(int i) {
		frames = i;
	}

	/**
	 * @param fs
	 */
	public void setLinMaxLevel(float[] fs) {
		linMaxLevel = fs;
	}

	/**
	 * @param fs
	 */
	public void setLinMinLevel(float[] fs) {
		linMinLevel = fs;
	}

	/**
	 * @param fs
	 */
	public void setLogMaxLevel(float[] fs) {
		logMaxLevel = fs;
	}

	/**
	 * @param fs
	 */
	public void setLogMinLevel(float[] fs) {
		logMinLevel = fs;
	}

	/**
	 * @param fs
	 */
	public void setMaxValue(float[] fs) {
		maxValue = fs;
	}

	/**
	 * @param fs
	 */
	public void setMinValue(float[] fs) {
		minValue = fs;
	}

}
