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

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author klausj
 *
 */
public class InterceptorAudioInputStream extends AudioInputStream {

    private AudioInputStream srcAudioInputStream;
    private InterceptorInputStream iis;
    private volatile boolean interrupted=false;
    /**
     * @param stream
     * @param format
     * @param length
     */
    public InterceptorAudioInputStream(InputStream stream, AudioFormat format,
            long length) {
        super(stream, format, length);
        iis=new InterceptorInputStream(stream);
        srcAudioInputStream=new AudioInputStream(iis,format,length);
        
    }
    public InterceptorAudioInputStream(AudioInputStream stream) {
        super(stream, stream.getFormat(), stream.getFrameLength());
        iis=new InterceptorInputStream(stream);
        srcAudioInputStream=new AudioInputStream(iis,stream.getFormat(),stream.getFrameLength());
        
    }
    public int available() throws IOException {
        return srcAudioInputStream.available();
    }
    public void close() throws IOException {
        if(!interrupted){
            srcAudioInputStream.close();
        }
    }
    public AudioFormat getFormat() {
        return srcAudioInputStream.getFormat();
    }
    public long getFrameLength() {
        return srcAudioInputStream.getFrameLength();
    }
    public void mark(int readlimit) {
        srcAudioInputStream.mark(readlimit);
    }
    public boolean markSupported() {
        return false;
    }
    public int read() throws IOException {
        if(interrupted){
            return -1;
        }else{
            return srcAudioInputStream.read();
        }
    }
    public int read(byte[] b, int off, int len) throws IOException {
        if(interrupted){
            return -1;
        }else{
            return srcAudioInputStream.read(b, off, len);
        }
    }
    public int read(byte[] b) throws IOException {
        if(interrupted){
            return -1;
        }else{
            return srcAudioInputStream.read(b);
        }
    }
    public void reset() throws IOException {
        srcAudioInputStream.reset();
    }
    public long skip(long n) throws IOException {
        return srcAudioInputStream.skip(n);
    }
    
    
    public void addAudioOutputStream(IAudioOutputStream os) throws AudioFormatNotSupportedException {
        iis.addOutputStream(os);
        os.setAudioFormat(getFormat());
    }
    public void removeOutputStream(IAudioOutputStream os) {
        iis.removeOutputStream(os);
    }
    public boolean isInterrupted() {
        return interrupted;
    }
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }
 

}
