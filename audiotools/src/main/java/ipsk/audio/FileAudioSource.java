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
 * Date  : Aug 6, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


/**
 * A file based audio source.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class FileAudioSource extends BasicAudioSource {

    protected File file;

    /**
     * Create new audio source from file.
     * 
     * @param file
     */
    public FileAudioSource(File file) {
        this.file = file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioSource#getAudioInputStream()
     */
    public AudioInputStream getAudioInputStream() throws AudioSourceException {
        AudioInputStream ais=null;
        try {
            ais= ThreadSafeAudioSystem.getAudioInputStream(file);
        } catch (Exception e) {
            throw new AudioSourceException(e);
        }
        setAudioFormat(ais.getFormat());
        setFrameLength(ais.getFrameLength());
        return ais;
    }
    /**
     * Returns the audio file.
     * 
     * @return the audio file
     */
    public File getFile() {
        return file;
    }

    /**
     * Set a new audio file.
     * 
     * @param file
     *            new audio file
     */
    public void setFile(File file) {
        this.file = file;
    }

 

}
