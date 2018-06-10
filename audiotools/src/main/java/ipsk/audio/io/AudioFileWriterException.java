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
 * Date  : Sep 16, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.io;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class AudioFileWriterException extends Exception {

	/**
	 *  
	 */
	public AudioFileWriterException() {
		super();

	}

	/**
	 * @param message
	 */
	public AudioFileWriterException(String message) {
		super(message);

	}

	/**
	 * @param message
	 * @param cause
	 */
	public AudioFileWriterException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * @param cause
	 */
	public AudioFileWriterException(Throwable cause) {
		super(cause);

	}

}
