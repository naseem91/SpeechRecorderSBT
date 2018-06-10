//    Speechrecorder
//    (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Jun 3, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config;

import javax.sound.sampled.AudioFormat;

import ipsk.beans.dom.DOMElements;


/**
 * Audio format configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
@DOMElements({"name","encoding","channels","frameSize","sampleRate","bigEndian","sampleSizeInBits"})
public class Format {

	private String name = "";
	private String encoding = "PCM_SIGNED";
	private int channels = 2;
	private double sampleRate = 44100;
	private boolean bigEndian = false;
	private int sampleSizeInBits = 16;
	private int frameSize = 4;

	public Format() {
	}

	public String getEncoding() {
		return encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding=encoding;
	}

	/**
	 * @return channels
	 */
	public int getChannels() {
		return channels;
	}

	/**
	 * @return frame size
	 */
	public int getFrameSize() {
		return frameSize;
	}

	/**
	 * @return sample rate
	 */
	public double getSampleRate() {
		return sampleRate;
	}

	/**
	 * @return sample size in bits
	 */
	public int getSampleSizeInBits() {
		return sampleSizeInBits;
	}

	public boolean getBigEndian() {
		return bigEndian;
	}

	/**
	 * @param b true if big endion byte ordering
	 */
	public void setBigEndian(boolean b) {
		bigEndian = b;
	}

	/**
	 * @param i channels
	 */
	public void setChannels(int i) {
		channels = i;
	}

	/**
	 * @param i fraem size
	 */
	public void setFrameSize(int i) {
		frameSize = i;
	}

	/**
	 * @param string name
	 */
	public void setName(String string) {
		name = string;
	}
	
	public String getName() {
		return name;
	}
	/**
	 * @param d sample rate
	 */
	public void setSampleRate(double d) {
		sampleRate = d;
	}

	/**
	 * @param i samlpe size in bits
	 */
	public void setSampleSizeInBits(int i) {
		sampleSizeInBits = i;
	}

	public AudioFormat toAudioFormat(){
	    AudioFormat.Encoding ae=null;
	    if(AudioFormat.Encoding.PCM_SIGNED.toString().equals(encoding)){
	        ae=AudioFormat.Encoding.PCM_SIGNED;
	    }else if(AudioFormat.Encoding.PCM_UNSIGNED.toString().equals(encoding)){
	        ae=AudioFormat.Encoding.PCM_UNSIGNED;
	    }else if(AudioFormat.Encoding.ALAW.toString().equals(encoding)){
            ae=AudioFormat.Encoding.ALAW;
        }else if(AudioFormat.Encoding.ULAW.toString().equals(encoding)){
            ae=AudioFormat.Encoding.ULAW;
        }else{
            ae=new AudioFormat.Encoding(encoding);
        }
	return new AudioFormat(ae, (float)sampleRate,
            sampleSizeInBits,
            channels, frameSize, (float)sampleRate,bigEndian);
	}
}
