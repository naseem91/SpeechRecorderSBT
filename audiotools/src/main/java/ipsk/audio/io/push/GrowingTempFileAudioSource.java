//    IPS Java Audio Tools
// 	  (c) Copyright 2012
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSourceException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author klausj
 *
 */
public class GrowingTempFileAudioSource implements GrowingAudioSource,IAudioOutputStream {

    private volatile File tempRawFile;
    private volatile FileOutputStream fileOutputStream; 
    private volatile long frameLength=0;
    private volatile AudioFormat format;
    private volatile boolean finished=false; 
    
    private Vector<GrowListener> listeners=new Vector<GrowListener>();
    
    public GrowingTempFileAudioSource(){
        super();
        
    }
    
    public AudioInputStream getAudioInputStream() throws AudioSourceException {
        synchronized (tempRawFile) {
            InputStream fis;
            try {
                fis = new FileInputStream(tempRawFile);
            } catch (FileNotFoundException e) {
               throw new AudioSourceException(e);
            }
            AudioInputStream ais=new AudioInputStream(fis, format, frameLength);
            return ais;
        }
    }

  
    public long getFrameLength() throws AudioSourceException {
        synchronized(tempRawFile){
            return frameLength;
        }
    }

    public AudioFormat getFormat() throws AudioSourceException {
       return format;
    }

    public void addGrowListener(GrowListener l) {
        listeners.add(l);
    }

    public void removeGrowListener(GrowListener l) {
        listeners.remove(l);

    }

    private FileOutputStream getTempFileOutputStream() throws IOException{
        if(fileOutputStream==null){
          
            if(tempRawFile==null){
               tempRawFile=File.createTempFile(getClass().getName()+"_audio",".raw");
               
            }
            fileOutputStream=new FileOutputStream(tempRawFile);
        }
        return fileOutputStream;
    }
    /* (non-Javadoc)
     * @see ipsk.io.IOutputStream#write(byte[], int, int)
     */
    public void write(byte[] buf, int offset, int len) throws IOException {
       
       FileOutputStream fos=getTempFileOutputStream();
       fos.write(buf, offset, len);
       int frameSize=format.getFrameSize();
       
       long frames=len/frameSize;
       frameLength+=frames;
       GrowEvent ge=new GrowEvent(frameLength, this);
       fireGrowEvent(ge);
    }

    /**
     * @param ge
     */
    private void fireGrowEvent(GrowEvent ge) {
        for(GrowListener gl:listeners){
            gl.update(ge);
        }
    }

    /* (non-Javadoc)
     * @see java.io.Flushable#flush()
     */
    public void flush() throws IOException {
        FileOutputStream fos=getTempFileOutputStream();
        fos.flush();
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    public void close() throws IOException {
        FileOutputStream fos=getTempFileOutputStream();
        fos.close();
        finished=true;
        GrowEvent ge=new GrowEvent(true,frameLength,this);
        fireGrowEvent(ge);
    }

    /* (non-Javadoc)
     * @see ipsk.audio.io.push.IAudioOutputStream#getAudioFormat()
     */
    public AudioFormat getAudioFormat() {
       try {
        return getFormat();
    } catch (AudioSourceException e) {
       return null;
    }
    }

    /* (non-Javadoc)
     * @see ipsk.audio.io.push.IAudioOutputStream#setAudioFormat(javax.sound.sampled.AudioFormat)
     */
    public void setAudioFormat(AudioFormat audioFormat)
            throws AudioFormatNotSupportedException {
        this.format=audioFormat;  
    }
    
    public boolean isFinished(){
        return finished;
    }
    
    public void release(){
        if(tempRawFile!=null){
            tempRawFile.delete();
        }
    }
    
    

}
