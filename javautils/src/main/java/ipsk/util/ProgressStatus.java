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

package ipsk.util;

import ipsk.awt.Worker;
import ipsk.awt.WorkerException;
import ipsk.awt.Worker.State;

import java.util.Date;
//TODO ugly code 
// extending ProgressWorker.Status ??
public class ProgressStatus implements Cloneable{

	private Worker.State status=Worker.State.INIT;
	public static long LENGTH_UNKNOWN=-1;
	
	private volatile boolean indeterminate=false;
	private volatile long progress=0;
	private long length=LENGTH_UNKNOWN;
	
	private volatile LocalizableMessage message=null;
	private volatile ProgressStatus subStatus=null;
	private volatile long subStatusLength;
	private volatile Date startTime=null;
	private volatile Date finishedTime=null;
	private volatile Date canceledTime=null;
	private volatile Date errorTime=null;
	
	public ProgressStatus() {
		super();
		length=100;
	}
	
	
	
	public ProgressStatus(boolean indeterminate,boolean finished,long progress) {
		this(indeterminate,finished,100,progress);
	}
	public ProgressStatus(boolean indeterminate,boolean finished,long length,long progress) {
		super();
		this.indeterminate=indeterminate;
		this.progress=progress;
		setLength(length);
		
	}
	
	public ProgressStatus(long progress) {
		this(false,false,progress);
	}
	public ProgressStatus(Object arg0,boolean finished) {
		this(false,finished,0);
	}
	public ProgressStatus(boolean finished,long progress) {
		this(false,finished,progress);
	}
	public ProgressStatus(long progress,LocalizableMessage message) {
		this(progress);
		this.message=message;
	}
	
	public synchronized ProgressStatus clone(){
		ProgressStatus clonedProgressStatus;
		try {
			clonedProgressStatus = (ProgressStatus)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
			
		}
		// deep copy of objects
		if(message==null){
			clonedProgressStatus.message=null;
		}else{
		clonedProgressStatus.message=(LocalizableMessage) message.clone();
		}
		if(subStatus!=null){
			clonedProgressStatus.subStatus=subStatus.clone();
		}
		if(startTime!=null){
			clonedProgressStatus.startTime=(Date)startTime.clone();
		}
		if(finishedTime!=null){
			clonedProgressStatus.finishedTime=(Date)finishedTime.clone();
		}
		if(canceledTime!=null){
			clonedProgressStatus.canceledTime=(Date)canceledTime.clone();
		}
		if(errorTime!=null){
			clonedProgressStatus.errorTime=(Date)errorTime.clone();
		}
		return clonedProgressStatus;
	}
	
	
	public void open(){
		status=State.OPEN;
	}
	
	public void start(){
		status=State.STARTED;
		startTime=new Date();
	}
	
	public void running(){
		status=State.RUNNING;
	}
	
	public void reset(){
		subStatus=null;
		indeterminate=false;
		setLength(length);
		progress=0;
		status=State.INIT;
		startTime=null;
		canceledTime=null;
		finishedTime=null;
		message=null;
	
		
	}
	
	public long getProgress() {
		long progress=this.progress;
		if(subStatus!=null){
			long sLength=subStatus.getLength();
			long sProgress=subStatus.getProgress();
			progress+=(subStatusLength*sProgress)/sLength;
		}
		return progress;
	}
	
//	public short getPercentProgress() {
//		if(length==LENGTH_UNKNOWN)return -1;
//		return (short)((progress *100)/length);
//	}
//	
	public Short getPercentProgress() {
		if(indeterminate)return null;
		long prPercent=((getProgress() *100)/length);
		return (short)prPercent;	
	}
	
	public boolean isIndeterminate() {
		return indeterminate;
	}
	
	public boolean isDone() {
		return State.DONE.equals(status);
	}
	public void setIndeterminate(boolean indeterminate) {
		this.indeterminate = indeterminate;
	}
	public void setProgress(long progress) {
		this.progress = progress;
	}
	public synchronized void done() {
		if(length!=LENGTH_UNKNOWN){
			progress=length;
		}
		if(finishedTime==null){
			finishedTime=new Date();
		}
		status=State.DONE;
	}
	public LocalizableMessage getMessage() {
		return message;
	}
	public void setMessage(LocalizableMessage message) {
		this.message = message;
	}
	
	public LocalizableMessage[] getMessages(){
		LocalizableMessage[] msgs;
		if(subStatus!=null){
			LocalizableMessage[] subMsgs=subStatus.getMessages();
			msgs=new LocalizableMessage[subMsgs.length+1];
			msgs[0]=message;
			for(int i=0;i<subMsgs.length;i++){
				msgs[i+1]=subMsgs[i];
			}
		}else{
			msgs=new LocalizableMessage[]{message};
		}
		return msgs;
	}

	public ProgressStatus getSubStatus() {
		return subStatus;
	}

	public synchronized void setSubStatus(ProgressStatus subStatus,long subStatusLength) {
		this.subStatus = subStatus;
		this.subStatusLength=subStatusLength;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
		indeterminate=(length==LENGTH_UNKNOWN);
	}
//	public Date getStartTime() {
//		return startTime;
//	}
//	public void setStartTime(Date startTime) {
//		this.startTime = startTime;
//	}
	public Date getFinishedTime() {
		return finishedTime;
	}
	public void setFinishedTime(Date finishedTime) {
		this.finishedTime = finishedTime;
	}
	
	/** Get elapsed time in milliseconds.
	 * @return elapsed time or null if unknown
	 */
	public synchronized Long elapsedTimeMillis() {
		if(startTime!=null){
			
			if(errorTime!=null){
				return errorTime.getTime()-startTime.getTime();
			}else if(canceledTime!=null){
				return canceledTime.getTime()-startTime.getTime();
			}else if(finishedTime!=null){
				return finishedTime.getTime()-startTime.getTime();
			}else{
				return new Date().getTime()-startTime.getTime();
			}
		}
		return null;
	}
	
	public synchronized Long estimatedFinishMillis() {
		if(length!=LENGTH_UNKNOWN){
			long progress=getProgress();
			Long elapsedMillis=elapsedTimeMillis();
			if(elapsedMillis!=null && status.isActive() && progress!=0){
				return(length*elapsedMillis)/progress;
			}
		}
		return null;
	}

	public synchronized Date estimatedFinishTime() {

		if(finishedTime!=null){
			return finishedTime;
		}
		if(status.isActive()){
			Long estimatedFinishMillis=estimatedFinishMillis();
			if(estimatedFinishMillis!=null && startTime!=null){
				return new Date(startTime.getTime()+estimatedFinishMillis);
			}
		}
		return null;
	}
	
	public synchronized void cancel() {
		if(status.isActive()){
			status=State.CANCEL;
		}
	}
	
	public boolean hasCancelRequest() {
		return (State.CANCEL.equals(status));
	}
	
	
	public synchronized void canceled() {
		status=State.CANCELLED;
		if(canceledTime==null){
			canceledTime=new Date();
		}
	}
	
	public boolean isCanceled(){
	    return (State.CANCELLED.equals(status));
	}
	
	public boolean isError(){
		return (State.ERROR.equals(status));
	}
	
	public synchronized void error() {
		status=State.ERROR;
			if(errorTime==null){
				errorTime=new Date();
			}
	}
	
	public synchronized void error(LocalizableMessage errorMsg) {
		this.message = errorMsg;
		error();
	}
	public Date getErrorTime() {
		return errorTime;
	}
	

	public Worker.State getStatus() {
		return status;
	}

	public String toString(){
	    return status.toString()+" "+getPercentProgress()+" % done";
	}
	
}
