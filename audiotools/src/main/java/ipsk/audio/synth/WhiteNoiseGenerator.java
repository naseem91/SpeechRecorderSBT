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
 * Date  : Mar 17, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.synth;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.dsp.AudioFrameProcessor;
import ipsk.io.FramedInputStream;
import ipsk.math.random.GaussianDistributionRandomGenerator;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class WhiteNoiseGenerator extends FramedInputStream {

    public static final int DEFAULT_BUF_SIZE_FRAMES=1024; 
    private long position=0;

    private AudioFrameProcessor ap;

    private int channels;

    private int frameSize;


    private GaussianDistributionRandomGenerator gg;
 
    private double[] valBuf;

    long length;

    public WhiteNoiseGenerator(AudioFormat audioFormat, long length)
            throws AudioFormatNotSupportedException {
        super(audioFormat.getFrameSize());
        this.length = length;   
        channels = audioFormat.getChannels();
        ap = new AudioFrameProcessor(audioFormat);
        frameSize = ap.getFrameSize();
        valBuf=new double[1*channels];
        gg=new GaussianDistributionRandomGenerator();
        
    }

  
    public int read(byte[] buf, int offset, int len) throws IOException {
       int frames=len/frameSize;
       if(length!=AudioSystem.NOT_SPECIFIED){
           long rest=length-position;
           if(rest==0){
               return -1; 
           }
           
           if((long)frames>rest){
               frames=(int)rest;
           }
       }
       int samples=frames*channels;
       if(valBuf.length < samples){
           valBuf=new double[samples];
       }
       
       gg.fillWithGaussionDistributedValues(valBuf, 0, samples);
       ap.encode(valBuf,0, buf, 0,samples);
       position+=frames;
       return frames*frameSize;
    }

}
