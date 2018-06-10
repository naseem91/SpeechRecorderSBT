//    Speechrecorder
//    (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Aug 19, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder;

/**
 * Exception to throw on prompt file parsing errors.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class PromptParsingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5704828086257637475L;

	/**
	 * Exception on prompt parsing.
	 */
	public PromptParsingException() {
		
	}

	/**
	 * Exception on prompt parsing.
	 * @param arg0 message
	 */
	public PromptParsingException(String arg0) {
		super(arg0);
	}

	/**
	 * Exception on prompt parsing.
	 * @param arg0 cause
	 */
	public PromptParsingException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * Exception on prompt parsing.
	 * @param arg0 message
	 * @param arg1 cause
	 */
	public PromptParsingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
