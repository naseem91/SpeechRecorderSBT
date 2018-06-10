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
 * Date  : Feb 1, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.sampled.spi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;


/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class AlawRawFileReader extends AudioFileReader {

    
    private static class RawAlawAudioFileFormatType extends AudioFileFormat.Type{
        public RawAlawAudioFileFormatType(){
        super("Raw Alaw 8kHz encoded","al");
        }
       
    }
    
    private static RawAlawAudioFileFormatType fileFormatType=new RawAlawAudioFileFormatType();
    private AudioFileFormat fileFormat;
    private AudioFormat format;
   
     
    public AlawRawFileReader() {
        super();
        //System.out.println("AlawRawFileReader loaded");
        format=new AudioFormat(AudioFormat.Encoding.ALAW,8000,8,1,1,8000,true);
        fileFormat=new AudioFileFormat(fileFormatType,format,AudioSystem.NOT_SPECIFIED);
    }

    /* (non-Javadoc)
     * @see javax.sound.sampled.spi.AudioFileReader#getAudioFileFormat(java.io.File)
     */
    public AudioFileFormat getAudioFileFormat(File arg0)
            throws UnsupportedAudioFileException, IOException {
        if (!arg0.getName().endsWith("." + fileFormatType.getExtension())) throw new UnsupportedAudioFileException();
        //System.out.println("AlawRawFileReader returning file format");
        return fileFormat;
    }

    /* (non-Javadoc)
     * @see javax.sound.sampled.spi.AudioFileReader#getAudioFileFormat(java.io.InputStream)
     */
    public AudioFileFormat getAudioFileFormat(InputStream arg0)
            throws UnsupportedAudioFileException, IOException {
        //System.out.println("AlawRawFileReader returning file format");
        // We cannot determine the extension here
        throw new UnsupportedAudioFileException();
        //return fileFormat;
    }

    /* (non-Javadoc)
     * @see javax.sound.sampled.spi.AudioFileReader#getAudioFileFormat(java.net.URL)
     */
    public AudioFileFormat getAudioFileFormat(URL arg0)
            throws UnsupportedAudioFileException, IOException {
        //System.out.println("AlawRawFileReader returning file format");
        if (!arg0.getFile().endsWith("." + fileFormatType.getExtension())) throw new UnsupportedAudioFileException();
        return fileFormat;
    }

    /* (non-Javadoc)
     * @see javax.sound.sampled.spi.AudioFileReader#getAudioInputStream(java.io.File)
     */
    public AudioInputStream getAudioInputStream(File arg0)
            throws UnsupportedAudioFileException, IOException {
        //System.out.println("AlawRawFileReader audio stream");
        if (!arg0.getName().endsWith("." + fileFormatType.getExtension())) throw new UnsupportedAudioFileException();
        return new AudioInputStream(new FileInputStream(arg0),format,arg0.length());
    }

    /* (non-Javadoc)
     * @see javax.sound.sampled.spi.AudioFileReader#getAudioInputStream(java.io.InputStream)
     */
    public AudioInputStream getAudioInputStream(InputStream arg0)
            throws UnsupportedAudioFileException, IOException {
        
        throw new UnsupportedAudioFileException();
       
    }

    /* (non-Javadoc)
     * @see javax.sound.sampled.spi.AudioFileReader#getAudioInputStream(java.net.URL)
     */
    public AudioInputStream getAudioInputStream(URL arg0)
            throws UnsupportedAudioFileException, IOException {
        //System.out.println("AlawRawFileReader audio stream");
        if (!arg0.getFile().endsWith("." + fileFormatType.getExtension())) throw new UnsupportedAudioFileException();
        return new AudioInputStream(arg0.openStream(),format,AudioSystem.NOT_SPECIFIED);
    }

}
