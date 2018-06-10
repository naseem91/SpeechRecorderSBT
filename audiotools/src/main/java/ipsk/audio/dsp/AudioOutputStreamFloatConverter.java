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
 * Date  : Jun 10, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.dsp;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.io.push.AudioOutputStream;
import ipsk.audio.io.push.FloatAudioOutputStream;
import ipsk.audio.io.push.IAudioOutputStream;
import ipsk.io.InterleavedFloatOutputStream;
import ipsk.io.InterleavedFloatStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class AudioOutputStreamFloatConverter implements IAudioOutputStream{
	private byte[] oneByteBuffer=new byte[1];
	private double[][] normBuffer;

	private int frameSize;
	private int channels;

	private AudioFormat audioFormat;
	private AudioFrameProcessor bufferProcessor;
	
	private Vector<FloatAudioOutputStream> floatOutputStreams=new Vector<FloatAudioOutputStream>();

	public AudioOutputStreamFloatConverter(){
		super();
	}
	
    /* (non-Javadoc)
     * @see java.io.Flushable#flush()
     */
    public void flush() throws IOException {
        for(FloatAudioOutputStream faos:floatOutputStreams){
            faos.flush();
        }
    }



    /* (non-Javadoc)
     * @see ipsk.audio.io.push.IAudioOutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) throws IOException {
        if(len % frameSize >0){
            throw new IOException("Audio output stream out of frame constraints.");
        }
        int frames=len/frameSize;
        if(normBuffer.length<frames){
            normBuffer=new double[frames][channels];
        }
            bufferProcessor.getNormalizedInterleavedValues(b,off, frames, normBuffer, 0);
        if(frames>0){
            synchronized(floatOutputStreams){
                for(FloatAudioOutputStream faos:floatOutputStreams){
                    faos.write(normBuffer,0,frames);
                }
            }
        }
        
    }



    /* (non-Javadoc)
     * @see ipsk.audio.io.push.IAudioOutputStream#write(byte[])
     */
    public void write(byte[] b) throws IOException {
        write(b,0,b.length);
    }



    /* (non-Javadoc)
     * @see ipsk.audio.io.push.IAudioOutputStream#write(int)
     */
    public void write(int b) throws IOException {
       oneByteBuffer[0]=(byte)(b & 0x000000ff);
       write(oneByteBuffer);
    }



    /* (non-Javadoc)
     * @see ipsk.audio.io.push.IAudioOutputStream#getAudioFormat()
     */
    public AudioFormat getAudioFormat() {
       return audioFormat;
    }



    /* (non-Javadoc)
     * @see ipsk.audio.io.push.IAudioOutputStream#setAudioFormat(javax.sound.sampled.AudioFormat)
     */
    public void setAudioFormat(AudioFormat audioFormat) throws AudioFormatNotSupportedException {
          this.audioFormat=audioFormat;
          frameSize = audioFormat.getFrameSize();
          channels = audioFormat.getChannels();
          bufferProcessor=new AudioFrameProcessor(audioFormat);
          normBuffer=new double[1][channels];
          synchronized(floatOutputStreams){
              for(FloatAudioOutputStream faos:floatOutputStreams){
                  faos.setChannels(channels);
                  faos.setAudioFormat(audioFormat);
//                  System.out.println("AudioOutputStreamFloatConverter.setAudioFormat2");
              }
          }
    }



    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    public void close() throws IOException {
        synchronized(floatOutputStreams){
            for(FloatAudioOutputStream faos:floatOutputStreams){
                faos.close();
            }
        }
    }

    
    /**
     * Add float audio output stream.
     * @param floatAudioOutputStream float audio output stream
     */
    public void addFloatAudioOutputStream(FloatAudioOutputStream floatAudioOutputStream) {
        synchronized (floatOutputStreams) {
            if (floatAudioOutputStream != null && ! floatOutputStreams.contains(floatAudioOutputStream)) {
            	AudioFormat af=getAudioFormat();
            	if(af!=null){
            		floatAudioOutputStream.setAudioFormat(af);
            	}
                floatOutputStreams.addElement(floatAudioOutputStream);
            }
        }
        
    }

    /**
     * Remove float audio output stream
     * @param floatAudioOutputStream float audio output stream
     */
    public void removeFloatAudioOutputStream(FloatAudioOutputStream floatAudioOutputStream) {
        synchronized(floatOutputStreams){
            if (floatAudioOutputStream != null) {
                floatOutputStreams.removeElement(floatAudioOutputStream);
            }
        }
    }


    
}
