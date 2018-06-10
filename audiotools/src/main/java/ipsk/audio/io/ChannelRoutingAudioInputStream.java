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

package ipsk.audio.io;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.io.push.IAudioOutputStream;
import ipsk.io.InterceptorInputStream;
import ipsk.io.InterleavedChannelRoutingInputStream;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author klausj
 *
 */
public class ChannelRoutingAudioInputStream extends AudioInputStream {

  
    private InterleavedChannelRoutingInputStream iis;
 
    /**
     * @param stream
     * @param format
     * @param length
     */
    public ChannelRoutingAudioInputStream(InputStream stream, AudioFormat format,
            long length,Integer[] channelRouting) {
    	 super(stream, format, length);
        int outChannels=format.getChannels();
        int sampleSize=format.getFrameSize()/outChannels;
        int maxInIdx=-1;
        for(int r:channelRouting){
        	if(r>maxInIdx){
        		maxInIdx=r;
        	}
        }
        int inputChannelCount=maxInIdx+1;
        iis=new InterleavedChannelRoutingInputStream(stream, sampleSize, inputChannelCount, channelRouting);
       
        
    }
    
    public ChannelRoutingAudioInputStream(AudioInputStream stream,int srcChannelCount, AudioFormat format,
            long length,Integer[] channelRouting) throws AudioFormatNotSupportedException {
    	 super(stream, format, length);
        int outChannels=format.getChannels();
        int sampleSize=format.getFrameSize()/outChannels;
        int maxInIdx=-1;
        for(int r:channelRouting){
        	if(r>maxInIdx){
        		maxInIdx=r;
        	}
        }
        int inputChannelCountByAssignment=maxInIdx+1;
        AudioFormat af=stream.getFormat();
       
        if(srcChannelCount<inputChannelCountByAssignment){
        	throw new AudioFormatNotSupportedException(af);
        }
        iis=new InterleavedChannelRoutingInputStream(stream, sampleSize, srcChannelCount, channelRouting);
       
        
    }
    
    public ChannelRoutingAudioInputStream(AudioInputStream stream, AudioFormat format,
            long length,Integer[] channelRouting) throws AudioFormatNotSupportedException {
    	 super(stream, format, length);
        int outChannels=format.getChannels();
        int sampleSize=format.getFrameSize()/outChannels;
        int maxInIdx=-1;
        for(int r:channelRouting){
        	if(r>maxInIdx){
        		maxInIdx=r;
        	}
        }
        int inputChannelCountByAssignment=maxInIdx+1;
        AudioFormat af=stream.getFormat();
        int srcChannelCount=af.getChannels();
        // TODO check other format properties
        if(srcChannelCount<inputChannelCountByAssignment){
        	throw new AudioFormatNotSupportedException(af);
        }
        iis=new InterleavedChannelRoutingInputStream(stream, sampleSize, srcChannelCount, channelRouting);
       
        
    }
    
}
