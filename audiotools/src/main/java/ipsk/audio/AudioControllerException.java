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
 * Date  : 10.03.2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio;


/**
 * Every exception thrown in an {@link ipsk.audio.AudioController}
 * implementation, e.g I/O exception will be wrapped by this exception.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de*
 */
public class AudioControllerException extends Exception {

   
	

	/**
     * Create new exception.
     */
    public AudioControllerException() {
        super();
    }

    /**
     * Create exception with message.
     * 
     * @param message
     */
    public AudioControllerException(String message) {
        super(message);
    }

    /**
     * Create exception giving the cause.
     * 
     * @param cause
     */
    public AudioControllerException(Throwable cause) {
        super(cause);
    }

    /**
     * Create exception with message and cause.
     * 
     * @param message
     * @param cause
     */
    public AudioControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
