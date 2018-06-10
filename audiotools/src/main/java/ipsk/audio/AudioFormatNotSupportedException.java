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

package ipsk.audio;

import javax.sound.sampled.AudioFormat;

/**
 * Exception indicating an unsupported audio format.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class AudioFormatNotSupportedException extends Exception {

    /**
     * Create an exception.
     * 
     * @param af
     *            the unsupported format
     */
    public AudioFormatNotSupportedException(AudioFormat af) {
        super("Audio format " + af.toString() + " not supported !");
    }

    /**
     * Create an detailed exception.
     * 
     * @param isSource
     *            true if the mixer is used for playback
     * @param af
     *            the unsupprted audio format
     */
    public AudioFormatNotSupportedException(boolean isSource, AudioFormat af) {

        super(isSource ? "Playback " : "Recording " + "using audio format\n"
                + af.toString() + " is not supported !");

    }

    /**
     * Create an detailed exception.
     * 
     * @param isSource
     *            true if the mixer is used for playback
     * @param mixerName
     *            the name of the mixer
     * @param af
     *            the unsupprted audio format
     */
    public AudioFormatNotSupportedException(boolean isSource, String mixerName,
            AudioFormat af) {

        super(isSource ? "Playback" : "Recording" + " device " + mixerName
                + ":\naudio format " + af.toString() + " not supported !");

    }

    public AudioFormatNotSupportedException() {
        super("Audio format not supported !");
    }

}
