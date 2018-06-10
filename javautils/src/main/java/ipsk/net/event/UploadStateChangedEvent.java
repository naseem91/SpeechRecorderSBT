//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.net.event;

import ipsk.net.Upload;

/**
 * The specified upload has changed its state.
 * @author klausj
 *
 */
public class UploadStateChangedEvent extends UploadEvent {

	private Upload upload;
	private int state;
	public UploadStateChangedEvent(Object source,Upload upload) {
		super(source);
		this.upload=upload;
		// save the state in can change fast in multithreaded apps
		if(upload!=null){
		this.state=upload.getStatus();
		}
	}
	public Upload getUpload() {
		return upload;
	}
	public int getState() {
		return state;
	}

}
