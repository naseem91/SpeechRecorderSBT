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
 * Date  : 02.01.2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.plugins;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioPlugin;
import ipsk.audio.AudioPluginException;
import ipsk.io.ChannelRouting;
import ipsk.io.InterleavedChannelRoutingInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;


/**
 * Audio plugin to route channels.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class ChannelRoutingPlugin implements AudioPlugin {

//	private int[] channelRouting;
//	private Integer minInputChannelCount;
	private ChannelRouting channelRouting;
	
	/**
	 * @return the channelRouting
	 */
	public ChannelRouting getChannelRouting() {
		return channelRouting;
	}


	/**
	 * @param channelRouting the channelRouting to set
	 */
	public void setChannelRouting(ChannelRouting channelRouting) {
		this.channelRouting = channelRouting;
	}




	private AudioFormat inputFormat;
	private AudioFormat outputFormat;

	
	public ChannelRoutingPlugin(ChannelRouting channelRouting) {
		super();
		
		this.channelRouting=channelRouting;
	}
	
	
	/**
	 * 
	 */
	public ChannelRoutingPlugin() {
		super();
	}

//	private int calcInputChannelCount(){
//		int minRequiredChannels=0;
//		if(channelRouting!=null){
//			int maxInChIdx=-1;
//			for(int inChIdx:channelRouting){
//				if(inChIdx>maxInChIdx){
//					maxInChIdx=inChIdx;
//				}
//			}
//			minRequiredChannels=maxInChIdx+1;
//		}
//		if(minInputChannelCount!=null && minInputChannelCount>minRequiredChannels){
//			minRequiredChannels=minInputChannelCount;
//		}
//		return minRequiredChannels;
//	}
//	
	

	
	public AudioInputStream getAudioInputStream(AudioInputStream ais)
			throws AudioPluginException {

	    AudioFormat af=ais.getFormat();
	    long frameLength=ais.getFrameLength();
	    int channels=af.getChannels();
	    //long length=frameLength/channels;
	    int inChs=channelRouting.getSrcChannels();
	    
	    if(channels <inChs)throw new AudioPluginException("Cannot route out channel with index "+(inChs-1)+", stream has only "+channels+" channels.");
	    int frameSize=af.getFrameSize();
	    int sampleSize=frameSize/channels;
//		int frameOffset=selectedChannel*sampleSize;
	    Integer[] assnment=channelRouting.getAssignment();
		AudioFormat outFormat=new AudioFormat(af.getSampleRate(),af.getSampleSizeInBits(),assnment.length,(af.getEncoding()==AudioFormat.Encoding.PCM_SIGNED),af.isBigEndian());
		InterleavedChannelRoutingInputStream editInputStream =new InterleavedChannelRoutingInputStream(ais, sampleSize, channels, assnment);
		AudioInputStream editAudioInputStream=new AudioInputStream(editInputStream, outFormat, frameLength);
		return editAudioInputStream;
	}
	

	/* (non-Javadoc)
	 * @see ipsk.audio.AudioPlugin#getSupportedInputFormats()
	 */
	@Override
	public AudioFormat[] getSupportedInputFormats() {
		int inChannels=channelRouting.getSrcChannels();
		AudioFormat[] supportedAudioFormats = new AudioFormat[] {
				new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED,inChannels ,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, true),
				new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED, inChannels,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, false),
				new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED, inChannels,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, true),
				new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED, inChannels,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, false) };
		return supportedAudioFormats;
	}




	/* (non-Javadoc)
	 * @see ipsk.audio.AudioPlugin#getSupportedOutputFormats(javax.sound.sampled.AudioFormat)
	 */
	@Override
	public AudioFormat[] getSupportedOutputFormats(AudioFormat inputFormat) {
		int channels=channelRouting.getTrgChannels();
		AudioFormat[] supportedAudioFormats = new AudioFormat[] {
				new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED,channels ,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, true),
				new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED, channels,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, false),
				new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED, channels,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, true),
				new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED,
						(float) AudioSystem.NOT_SPECIFIED,
						AudioSystem.NOT_SPECIFIED, channels,
						AudioSystem.NOT_SPECIFIED,
						(float) AudioSystem.NOT_SPECIFIED, false) };
		return supportedAudioFormats;
	}




	/* (non-Javadoc)
	 * @see ipsk.audio.AudioPlugin#isInputFormatSupported(javax.sound.sampled.AudioFormat)
	 */
	@Override
	public boolean isInputFormatSupported(AudioFormat inputFormat) {
		AudioFormat.Encoding e=inputFormat.getEncoding();
		int inChs=inputFormat.getChannels();
		return((AudioFormat.Encoding.PCM_SIGNED.equals(e) || AudioFormat.Encoding.PCM_UNSIGNED.equals(e)) 
		 && inChs>=channelRouting.getSrcChannels());
	
	}

	private boolean matchFormatsIgnoringChannelCount(AudioFormat in,AudioFormat out){
		
	return(in.getEncoding().equals(out.getEncoding())
			&& in.getSampleRate() == out.getSampleRate()
			&& in.getSampleSizeInBits() == out.getSampleSizeInBits()
			&& in.getFrameRate() == out.getFrameRate()
			&& in.isBigEndian() == out.isBigEndian());
	}


	/* (non-Javadoc)
	 * @see ipsk.audio.AudioPlugin#isOutputFormatSupported(javax.sound.sampled.AudioFormat, javax.sound.sampled.AudioFormat)
	 */
	@Override
	public boolean isOutputFormatSupported(AudioFormat inputFormat,
			AudioFormat outputFormat) {
		boolean match=outputFormat.getChannels()==channelRouting.getTrgChannels();
		if(inputFormat!=null){
			match=match & (isInputFormatSupported(inputFormat) 
				&& matchFormatsIgnoringChannelCount(inputFormat, outputFormat)
				&& inputFormat.getChannels()>=channelRouting.getSrcChannels());
		}
		return match;
	}




	/* (non-Javadoc)
	 * @see ipsk.audio.AudioPlugin#setInputFormat(javax.sound.sampled.AudioFormat)
	 */
	@Override
	public void setInputFormat(AudioFormat inputFormat)
			throws AudioFormatNotSupportedException {
		if(inputFormat!=null && !isInputFormatSupported(inputFormat)){
			throw new AudioFormatNotSupportedException(inputFormat);
		}
		this.inputFormat=inputFormat;
	}




	/* (non-Javadoc)
	 * @see ipsk.audio.AudioPlugin#getInputFormat()
	 */
	@Override
	public AudioFormat getInputFormat() {
		if(inputFormat==null){
			if(outputFormat!=null){
				int inputChannels=channelRouting.getSrcChannels();
				int outChannels=outputFormat.getChannels();
				int sampleSize=outputFormat.getFrameSize()/outChannels;
				int inputFrameSize=inputChannels*sampleSize;
				inputFormat=new AudioFormat(outputFormat.getEncoding(), outputFormat.getSampleRate(), outputFormat.getSampleSizeInBits(), inputChannels, inputFrameSize, outputFormat.getFrameRate(), outputFormat.isBigEndian());
			}
		}
		return inputFormat;
	}




	/* (non-Javadoc)
	 * @see ipsk.audio.AudioPlugin#setOutputFormat(javax.sound.sampled.AudioFormat)
	 */
	@Override
	public void setOutputFormat(AudioFormat outputFormat)
			throws AudioFormatNotSupportedException {
		if(outputFormat==null){
			this.outputFormat=null;
		}else{
			if(isOutputFormatSupported(inputFormat, outputFormat)){
				this.outputFormat=outputFormat;
			}else{
				throw new AudioFormatNotSupportedException(outputFormat);
			}
		}
	}




	/* (non-Javadoc)
	 * @see ipsk.audio.AudioPlugin#getOutputFormat()
	 */
	@Override
	public AudioFormat getOutputFormat() {
		if(outputFormat==null){
			if(inputFormat!=null){
				int outputChannels=channelRouting.getTrgChannels();
				int inputChannels=inputFormat.getChannels();
				int sampleSize=inputFormat.getFrameSize()/inputChannels;
				int outputFrameSize=outputChannels*sampleSize;
				outputFormat=new AudioFormat(inputFormat.getEncoding(), inputFormat.getSampleRate(), inputFormat.getSampleSizeInBits(), outputChannels, outputFrameSize, inputFormat.getFrameRate(), inputFormat.isBigEndian());
			}
		}
		return outputFormat;
	}




	/* (non-Javadoc)
	 * @see ipsk.audio.AudioPlugin#getControls()
	 */
	@Override
	public Control[] getControls() {
		
		return null;
	}
	
	
    
}
