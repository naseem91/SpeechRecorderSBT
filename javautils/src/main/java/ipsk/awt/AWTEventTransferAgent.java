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

import java.lang.reflect.InvocationTargetException;

import java.util.EventListener;
import java.util.EventObject;
import java.util.Vector;
/**
 *  Agent transfers events from arbitrary threads to the AWT-Event-Thread.
 *  Swing components are not thread save. With this class events of concurrent running threads can be transferred
 *  to the AWT-Event-Thread. Actions triggered by these events can call Swing methods in a safety way without
 *  placing the method calls in an extra Runnable and push it to the AWT-Event-Thread queue.
 * @author klausj
 *
 */
public abstract class AWTEventTransferAgent<L extends EventListener,E extends EventObject>{

	private Vector<L> listeners=new Vector<L>();
	protected boolean eventsInAWTEventThread=true;
	//private EventObject event;
	
	public class EventRunnable implements Runnable{
		private E event;
		//private Vector<EventListener> listeners;
		public EventRunnable(E event){
			this.event=event;
		}
		
		public void run(){
			synchronized(listeners){
				for(L l:listeners){
					fireEvent(l, event);
				}
			}
		}
		
	}
	
	//private Object fireLock=new Object();
	
	public synchronized void addListener(L eventListener) {
		   if (eventListener != null && !listeners.contains(eventListener)) {
	            listeners.addElement(eventListener);
	        }
	}
	
	public synchronized void removeListener(L eventListener) {
		  if (eventListener != null) {
	            listeners.removeElement(eventListener);
	        }
	}
	
	public synchronized boolean hasListeners(){
		return (listeners!=null && listeners.size()>0);
	}
	
//	public void run(){
//		for(EventListener l:listeners){
//			fireEvent(l, event);
//		}
//	}
	
	public void fireEvent(E event){
		if(eventsInAWTEventThread){
			fireAWTEventLater(event);
		}else{
			for(L l:listeners){
				fireEvent(l, event);
			}
		}
	}
	
	public void fireEventAndWait(E event){
		if(eventsInAWTEventThread){
			fireAWTEventAndWait(event);
		}else{
			for(L l:listeners){
				fireEvent(l, event);
			}
		}
	}
	public void fireAWTEventAndWait(E event){
		//synchronized (fireLock) {
		//this.event=event;
		
	        if (java.awt.EventQueue.isDispatchThread()) {
	            //eventRunnable.run();
	        	synchronized(listeners){
					for(L l:listeners){
						fireEvent(l, event);
					}
				}
	        } else {
	        	EventRunnable eventRunnable=new EventRunnable(event);
	            try {
	                java.awt.EventQueue.invokeAndWait(eventRunnable);
	            } catch (InterruptedException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		//}
	}
	
	public void fireAWTEventLater(E event){
	        if (java.awt.EventQueue.isDispatchThread()) {
	            //eventRunnable.run();
	        	synchronized(listeners){
					for(L l:listeners){
						fireEvent(l, event);
					}
				}
	        } else {
	        	EventRunnable eventRunnable=new EventRunnable(event);
	                java.awt.EventQueue.invokeLater(eventRunnable);
	        }
		//}
	}
	
	protected abstract void fireEvent(L listener,E event);

	public boolean isEventsInAWTEventThread() {
		return eventsInAWTEventThread;
	}

	public void setEventsInAWTEventThread(boolean eventsInAWTEventThread) {
		this.eventsInAWTEventThread = eventsInAWTEventThread;
	}
	
}
