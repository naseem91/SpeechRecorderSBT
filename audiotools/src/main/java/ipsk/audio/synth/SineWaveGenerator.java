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

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class SineWaveGenerator extends InputStream {

    private long position;

    private AudioFrameProcessor ap;

    private int channels;

    private float sampleRate;

    private int frameSize;

    private int bufPos;

    private long length;

    private int periodValues;

    private byte[] periodBuf;

    //private ByteBuffer pBuf;
    private int bufSize;

    public SineWaveGenerator(float frequency, float amplitudeFactor,
            AudioFormat audioFormat, long length)
            throws AudioFormatNotSupportedException {
        this.length = length;
        sampleRate = audioFormat.getSampleRate();
        channels = audioFormat.getChannels();
        // TODO check samplerate and frequency for int
        // (the continues version produced than more harmonic contents the
        // higher the
        // position value growed (!), so I use one single period buffer
        periodValues = (int) sampleRate;
        ap = new AudioFrameProcessor(audioFormat);
        frameSize = ap.getFrameSize();

        bufSize = frameSize * periodValues;
        periodBuf = new byte[bufSize];
        //pBuf=ByteBuffer.allocateDirect(bufSize);
        float[] values = new float[frameSize];
        int offset = 0;
        for (int i = 0; i < periodValues; i++) {
            double timePos = (double)i / sampleRate;
            double phase = timePos * frequency * 2.0 * Math.PI;
            //System.out.println("Phase:"+phase);
            float ampl = (float) Math.sin(phase) * amplitudeFactor;

            for (int ch = 0; ch < channels; ch++) {
                values[ch] = ampl;
            }
            ap.encodeValues(values, periodBuf, offset);
            offset += frameSize;
        }
        //pBuf.put(periodBuf,0,bufSize);

        close();
    }

    public void close() {
        bufPos = 0;
        position = 0;
        //pBuf.rewind();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        if (position == length)
            return -1;

        int retVal = 0xFF & (int) periodBuf[bufPos++];
        if (bufPos == bufSize)
            bufPos = 0;
        if (bufPos % frameSize == 0) {
            position++;
        }
        return retVal;
    }

    public int read(byte[] buf, int offset, int len) throws IOException {
        if (position == this.length)
            return -1;
        // fill one frame if necessary
        int toFill = bufPos % frameSize;
        int i;
        if (toFill != 0) {

            if (len < toFill)
                toFill = len;
            for (i = 0; i < toFill; i++) {
                buf[offset + i] = periodBuf[bufPos++];
            }
            if (bufPos == bufSize)
                bufPos = 0;
            if (bufPos % frameSize == 0) {
                position++;
            }
            return toFill;
        }
        int available = bufSize - bufPos;
        int toCopy = len;
        if (available < toCopy) {
            toCopy = available;
        }
        if (this.length != AudioSystem.NOT_SPECIFIED) {
            long rest = (this.length - position) * frameSize;
            if (rest < Integer.MAX_VALUE && toCopy > rest)
                toCopy = (int) rest;
        }
        //		for (i = 0; i < toCopy; i++) {
        //			
        //			currBuf[bufPos + i] = periodBuf[bufPos++];
        //		}

        System.arraycopy(periodBuf, bufPos, buf, offset, toCopy);
        bufPos += toCopy;
        if (bufPos == bufSize)
            bufPos = 0;
        position += toCopy / frameSize;
        return toCopy;
    }

}
