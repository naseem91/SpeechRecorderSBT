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
 * Date  : Jul 14, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.impl.j2audio;

/**
 * Represents current status of the capture engine.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class CaptureStatus extends SynchronizedStatus {

	public static final String CLOSED = "Closed";

	public static final String OPEN = "Open";

	public static final String PREPARED = "Prepared";

	public static final String CAPTURING = "Capturing";

	public static final String RECORDING = "Recording";

	public static final String RECORDED = "Recorded";

	public static final String SAVING = "Saving";

	//public static final String SAVED = "Saved";
	public static final String ERROR = "Error";

	private Exception exception = null;

	public CaptureStatus(String status) {
		super(status);
		exception = null;
	}

	
	public Exception getException() {
		return exception;
	}


	public void setException(Exception exception) {
		this.exception = exception;
	}

}
