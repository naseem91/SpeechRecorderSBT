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

package ipsk.awt.event;

import ipsk.util.ProgressStatus;

import java.util.EventObject;
/**
 * Progress event.
 * @see ipsk.awt.ProgressWorker
 * @see ipsk.awt.ProgressListener
 * @author Klaus Jaensch
 *
 */
public class ProgressEvent extends EventObject {

	
	private ProgressStatus progressStatus;
	
	public ProgressEvent(Object arg0,ProgressStatus progressStatus) {
		super(arg0);
		this.progressStatus=progressStatus;
	}
	public ProgressEvent(Object arg0) {
		this(arg0,null);
	}
//	public ProgressEvent(Object arg0,boolean indeterminate,boolean finished,long progress) {
//		super(arg0);
//		progressStatus=new ProgressStatus(indeterminate,finished,progress);
//	}
//	public ProgressEvent(Object arg0,long progress) {
//		this(arg0,false,false,progress);
//	}
//	public ProgressEvent(Object arg0,boolean finished) {
//		this(arg0,false,finished,0);
//	}
//	public ProgressEvent(Object arg0,boolean finished,long progress) {
//		this(arg0,false,finished,progress);
//	}
//	public ProgressEvent(Object arg0,long progress,String message) {
//		this(arg0,progress);
//		progressStatus.setMessage(message);
//	}
	public ProgressStatus getProgressStatus() {
		return progressStatus;
	}
	public void setProgressStatus(ProgressStatus progressStatus) {
		this.progressStatus = progressStatus;
	}
	
	public String toString(){
	    return "Progress status: "+progressStatus.toString();
	}
	
}
