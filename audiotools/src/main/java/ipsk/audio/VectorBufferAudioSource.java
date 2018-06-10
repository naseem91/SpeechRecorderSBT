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

import ipsk.io.VectorBuffer;
import ipsk.io.VectorBufferedInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class VectorBufferAudioSource extends BasicAudioSource implements
		AudioSource {

    private VectorBuffer vectorBuffer;

    public VectorBufferAudioSource(VectorBuffer vectorBuffer) {
        this.vectorBuffer = vectorBuffer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioSource#getAudioInputStream()
     */
    public AudioInputStream getAudioInputStream() throws AudioSourceException {
        AudioInputStream ais = null;
        try {
            VectorBufferedInputStream vbis = new VectorBufferedInputStream(
                    vectorBuffer);
//            ais = AudioSystem.getAudioInputStream(vbis);
            ais=ThreadSafeAudioSystem.getAudioInputStream(vbis);
        } catch (Exception e) {
            throw new AudioSourceException(e);
        }
        return ais;
    }

    /**
     * Get audio data as vector buffer.
     * @return vector buffer
     */
    public VectorBuffer getVectorBuffer() {
        return vectorBuffer;
    }

    /**
     * Set audio data as vector buffer.
     * @param buffer vector buffer
     */
    public void setVectorBuffer(VectorBuffer buffer) {
        vectorBuffer = buffer;
    }

}
