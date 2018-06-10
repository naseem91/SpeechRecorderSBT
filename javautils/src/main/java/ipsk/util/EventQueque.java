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

import java.util.EventObject;
import java.util.Vector;


/**
 * An event queue.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class EventQueque implements Runnable{

    private Vector<EventObject> events;
    private boolean running=false; 
    private Thread thread;
    private EventQuequeListener el;
   

    /**
     * @param name
     */
    public EventQueque(String name,EventQuequeListener el) {
        events=new Vector<EventObject>();
        thread = new Thread(this,name);
        this.el=el;
        running=true;
        thread.start();
        
    }
    
    public void sendEvent(EventObject eventObject){
    	synchronized(events){
    		events.add(eventObject);
    		events.notifyAll();
    	}
    	
    }

  
    public void run(){
        EventObject eo=null;
        while(running){
        synchronized(events){
            while(events.isEmpty()){
                try {
                    events.wait();
                } catch (InterruptedException e) {
                   // OK
                }
            }
            eo=(EventObject)(events.remove(0));
        }
        
        el.update(eo);
        }
        
    }
    
    public void clear(){
        events.clear();
    }
    
    public void close(){
        events.clear();
        running=false;
        try {
            thread.join();
        } catch (InterruptedException e) {
           // OK
        }
    }
  
}
