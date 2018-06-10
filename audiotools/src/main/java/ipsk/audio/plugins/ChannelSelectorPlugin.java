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
import ipsk.audio.AudioPluginException;
import ipsk.io.InterleaveEditInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;


/**
 * Audio plugin to pick a channel.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class ChannelSelectorPlugin extends BasicPCMPlugin {



	private int selectedChannel;

	public ChannelSelectorPlugin(int channel) {
		selectedChannel=channel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#getSupportedOutputFormats(javax.sound.sampled.AudioFormat)
	 */
	public AudioFormat[] getSupportedOutputFormats(AudioFormat inputFormat) {
		if (isInputFormatSupported(inputFormat))
			return new AudioFormat[] { getOutputFormat(inputFormat)};
		else
			return new AudioFormat[0];
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#isOutputFormatSupported(javax.sound.sampled.AudioFormat,
	 *      javax.sound.sampled.AudioFormat)
	 */
	public boolean isOutputFormatSupported(AudioFormat inputFormat,
			AudioFormat outputFormat) {
		if (isInputFormatSupported(inputFormat)
				&& outputFormat.matches(getOutputFormat(inputFormat)))
			return true;
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioPlugin#setOutputFormat(javax.sound.sampled.AudioFormat)
	 */
	public void setOutputFormat(AudioFormat outputFormat)
			throws AudioFormatNotSupportedException {
		if (!isOutputFormatSupported(inputFormat, outputFormat))
			throw new AudioFormatNotSupportedException(outputFormat);
		this.outputFormat = outputFormat;
	}
	
	public AudioFormat getOutputFormat(AudioFormat inputFormat){
	    if(!isInputFormatSupported(inputFormat))return null;
	    //int channels=inputFormat.getChannels();
	    AudioFormat outputFormat=new AudioFormat(inputFormat.getSampleRate(),inputFormat.getSampleSizeInBits(),1,(inputFormat.getEncoding()==AudioFormat.Encoding.PCM_SIGNED),inputFormat.isBigEndian());
	    return outputFormat;
	}
	
	
	

	public AudioInputStream getAudioInputStream(AudioInputStream ais)
			throws AudioPluginException {

	    AudioFormat af=ais.getFormat();
	    long frameLength=ais.getFrameLength();
	    int channels=af.getChannels();
	    //long length=frameLength/channels;
	    if(channels <=selectedChannel)throw new AudioPluginException("Cannot select channel "+selectedChannel+": stream has only "+channels+" channels.");
	    int frameSize=af.getFrameSize();
	    int sampleSize=frameSize/channels;
		int frameOffset=selectedChannel*sampleSize;
		AudioFormat outFormat=new AudioFormat(af.getSampleRate(),af.getSampleSizeInBits(),1,(af.getEncoding()==AudioFormat.Encoding.PCM_SIGNED),af.isBigEndian());
		InterleaveEditInputStream eis = new InterleaveEditInputStream(ais,frameSize, frameOffset, sampleSize);
		AudioInputStream editAudioInputStream = new AudioInputStream(eis, outFormat,
				frameLength);
		return editAudioInputStream;
	}
    public int getSelectedChannel() {
        return selectedChannel;
    }
    public void setSelectedChannel(int selectedChannel) {
        this.selectedChannel = selectedChannel;
    }
}
