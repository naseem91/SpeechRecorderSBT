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

package ipsk.awt;

import ipsk.awt.event.ProgressErrorEvent;

import ipsk.awt.event.ProgressEvent;
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Worker class.
 * Does some work in a separate thread in the {@link #doWork()} method.
 * Create a subclass and implement the the {@link #doWork()} method.
 * @author klausj
 *
 */
public abstract class ProgressWorker  implements Worker,Runnable {
	public class ProgressEventTransferAgent extends AWTEventTransferAgent<EventListener,EventObject> {
		@Override
		public void fireEvent(EventListener l, EventObject ev) {
			ProgressListener pl = (ProgressListener) l;
			pl.update((ProgressEvent) ev);
		}
	}
	
	protected ProgressEventTransferAgent progressEventTransferAgent=new ProgressEventTransferAgent();
	protected String threadName;
	protected volatile boolean generateEvents=false;
	
	protected Thread thread;
	//protected volatile State status=State.INIT;
	protected volatile ProgressStatus progressStatus;
	
	public ProgressWorker() {
		this(null);
	}
	
	public ProgressWorker(String threadName) {
		super();
		this.threadName=threadName;
		progressStatus=new ProgressStatus();
	}

	protected void fireProgressEvent(){
		if(generateEvents){
			if(State.ERROR.equals(progressStatus.getStatus())){	
				fireProgressEvent(new ProgressErrorEvent(this,progressStatus.clone()));
			}else{
				fireProgressEvent(new ProgressEvent(this,progressStatus.clone()));
			}
		}
	}
	
	protected void fireProgressEvent(ProgressEvent progressEvent){
		progressEventTransferAgent.fireEvent(progressEvent);
	}
	
	

	public void open() throws WorkerException{
		if(threadName==null){
			thread=new Thread(this);
		}else{
			thread=new Thread(this,threadName);
		}
		//status=State.OPEN;
		progressStatus.open();
		fireProgressEvent();
	}
	
	public void start(){
		if(thread!=null){
			progressStatus.start();
			thread.start();
		}
	}

	public void run(){
		progressStatus.running();
		try {
			doWork();
		} catch (WorkerException e) {
			synchronized(progressStatus){
				progressStatus.error(new LocalizableMessage(e.getMessage()));
				fireProgressEvent();
			}
		}
		synchronized(progressStatus){
			if(hasCancelRequest()){
				progressStatus.canceled();
				fireProgressEvent();
			}else if (! progressStatus.isError()){
				progressStatus.done();
				fireProgressEvent();
				
			}
		}
	}
	
	public void setRunningWithParentWorker(){
		progressStatus.running();
	}
	/**
	 * 
	 */
	protected void doWork() throws WorkerException{
		// Does nothing
		
	}
	
	protected boolean hasCancelRequest(){
		return progressStatus.hasCancelRequest();
	}
	
	
//	public boolean isWorking(){
//		synchronized(status){
//			return status.isActive();
//			//return (State.STARTED.equals(status) || State.RUNNING.equals(status));
//		}
//	}
//	
//	public boolean isBusy(){
//		synchronized(status){
//			return (State.STARTED.equals(status) || State.RUNNING.equals(status));
//		}
//	}

	public void cancel() {
		progressStatus.cancel();
		if(thread!=null){
			// In an applet we get sometimes a modifyThread permission denied exception
			// why ? it is our own user thread
			//System.getSecurityManager().checkAccess(thread);
			thread.interrupt();
		}
	}
	
	public State getStatus() {
		return progressStatus.getStatus();
	}
	
	public void reset(){
		thread=null;
		progressStatus.reset();
		
	}
	
	public void close() throws WorkerException{
		
		try {
			if( thread!=null){
				thread.join();
			}
		} catch (InterruptedException e) {
			throw new WorkerException(e);
		}
//		reset();
	}

	

	
	public synchronized void addProgressListener(
			ProgressListener progressListener) {
			progressEventTransferAgent.addListener(progressListener);
			generateEvents=true;
	}

	public synchronized void removeProgressListener(
			ProgressListener progressListener) {
		progressEventTransferAgent.removeListener(progressListener);
		generateEvents=progressEventTransferAgent.hasListeners();
	}

	public ProgressStatus getProgressStatus() {
		return progressStatus;
	}

}
