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
 * Date  : Jan 21, 2009
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.dsp;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.io.FramedInputStream;
import ipsk.io.InterleavedFloatStream;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

/**
 * Converts a float audio input stream to PCM signed encoded audio stream.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class FloatToPCMInputStream extends FramedInputStream{
    private AudioFormat pcmFormat;
   
    private InterleavedFloatStream inStream;
    private double[][] inBuf;
    private int frameSize;
    private AudioFrameProcessor frameProcessor;
    
    /**
     * Create new PCM stream. 
     * @param audioInStream the source float stream
     * @throws AudioFormatNotSupportedException
     */
    public FloatToPCMInputStream(FloatAudioInputStream audioInStream) throws AudioFormatNotSupportedException{
        this(audioInStream,audioInStream.getFormat());
    }
    
    /**
     * 
     * @param inStream raw interleaved float stream
     * @param pcmFormat the PCM audio format to convert to
     * @throws AudioFormatNotSupportedException
     */
    public FloatToPCMInputStream(InterleavedFloatStream inStream,AudioFormat pcmFormat) throws AudioFormatNotSupportedException{
        super(pcmFormat.getFrameSize());
        this.inStream=inStream;
        this.pcmFormat=pcmFormat;
        frameSize=pcmFormat.getFrameSize();
        frameProcessor=new AudioFrameProcessor(pcmFormat);
    }
  
   
    public int read(byte[] buf, int offset,int length) throws IOException{
        int read=0;
        int framesToRead=length /frameSize;
        if(length % frameSize >0)throw new IOException("Bytes to read must be multiple of framesize!");
        if(inBuf==null || framesToRead>inBuf.length){
            inBuf=new double[framesToRead][pcmFormat.getChannels()];
        }
        int framesRead=0;
        
        framesRead=inStream.read(inBuf, 0, framesToRead);
        
        if(framesRead!=-1){
            for(int f=0;f<framesRead;f++){
                frameProcessor.encodeValues(inBuf[f], buf,offset+read);
                read+=frameSize;
            }
            
        }else{
            read=-1;
        }
        
        return read;
    }
    
    
}
