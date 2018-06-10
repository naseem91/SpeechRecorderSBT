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
 * Date  : Jan 21, 2009
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.dsp;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.io.FramedInputStream;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class FloatToPCMAudioInputStream extends AudioInputStream{
   
    public FloatToPCMAudioInputStream(FloatAudioInputStream inStream,AudioFormat pcmFormat) throws AudioFormatNotSupportedException{
        super(new FloatToPCMInputStream(inStream,pcmFormat),pcmFormat,inStream.getFrameLength());
    } 
}
