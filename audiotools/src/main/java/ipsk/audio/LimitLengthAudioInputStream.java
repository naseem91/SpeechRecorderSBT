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

package ipsk.audio;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

/**
 * @author klausj
 *
 */
public class LimitLengthAudioInputStream extends AudioInputStream {

    private AudioInputStream srcAudioInputStream;
    private long maxFrameLength;
    private int frameSize;
    private long bytesRead=0;
    private long maxBytesLength;
    public LimitLengthAudioInputStream(AudioInputStream srcAudioInputStream,long maxFrameLength){
        super(srcAudioInputStream, srcAudioInputStream.getFormat(),
                srcAudioInputStream.getFrameLength());
        this.srcAudioInputStream=srcAudioInputStream;
        this.maxFrameLength=maxFrameLength;
        
        this.frameSize=srcAudioInputStream.getFormat().getFrameSize();
        this.maxBytesLength=maxFrameLength*frameSize;
    }
    public int available() throws IOException {
        return srcAudioInputStream.available();
    }
    public void close() throws IOException {
        srcAudioInputStream.close();
    }
    public AudioFormat getFormat() {
        return srcAudioInputStream.getFormat();
    }
    public long getFrameLength() {
        long srcFrameLength=srcAudioInputStream.getFrameLength();
        if(srcFrameLength>maxFrameLength){
            return maxFrameLength;
        }else{
            return srcFrameLength;
        }
    }
    public void mark(int readlimit) {
        srcAudioInputStream.mark(readlimit);
    }
    public boolean markSupported() {
        return srcAudioInputStream.markSupported();
    }
    
    
    public int read() throws IOException {
        byte[] buf = new byte[1];

        int read = read(buf, 0, 1);
        if (read == -1)
            return -1;
        return 0x00FF & (int) read;
    }
    public int read(byte[] b, int off, int len) throws IOException {
        if(bytesRead+len>maxBytesLength){
            len=(int)(maxBytesLength-bytesRead);
        }
        if(len<=0){
            return -1;
        }
        int r=srcAudioInputStream.read(b, off, len);
        bytesRead+=r;
        return r;
    }
    public int read(byte[] b) throws IOException {
        return read(b,0,b.length);
    }
    public void reset() throws IOException {
        srcAudioInputStream.reset();
    }
    public long skip(long n) throws IOException {
        return srcAudioInputStream.skip(n);
    }

}
