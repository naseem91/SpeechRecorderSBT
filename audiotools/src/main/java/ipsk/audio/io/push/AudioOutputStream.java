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

package ipsk.audio.io.push;

import java.io.IOException;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;

/**
 * @author klausj
 *
 */
public class AudioOutputStream extends OutputStream{

    private OutputStream trgStream;
    private AudioFormat audioFormat;
    
    
    public AudioOutputStream(){
        super();
    }
    public AudioOutputStream(AudioFormat audioFormat,OutputStream trgStream){
        this();
        this.audioFormat=audioFormat;
        this.trgStream=trgStream;
    }

    public void close() throws IOException {
        trgStream.close();
    }

    public void flush() throws IOException {
        trgStream.flush();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        trgStream.write(b, off, len);
    }

    public void write(byte[] b) throws IOException {
        trgStream.write(b);
    }

    public void write(int b) throws IOException {
        trgStream.write(b);
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public OutputStream getTrgStream() {
        return trgStream;
    }

    public void setTrgStream(OutputStream trgStream) {
        this.trgStream = trgStream;
    }

    public void setAudioFormat(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }
    
    
}
