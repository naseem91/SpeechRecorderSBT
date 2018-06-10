//    Speechrecorder
//    (c) Copyright 2012
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
 * Date  : Sep 26, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.prompting;

import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterListener;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterClosedEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterOpenedEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterStartEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterStopEvent;

import java.util.Vector;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public abstract class BasicMediaPromptPresenter extends BasicPromptPresenter implements MediaPromptPresenter{
  
	private static final long serialVersionUID = 1L;
	protected Vector<PromptPresenterListener> listeners=new Vector<PromptPresenterListener>();
    protected boolean open=false;
    protected boolean running=false;
    
    public void setContents(String string, String description,String type){
    	
    }
    
    public void open() {
        if (!open) {
            open = true;
            updateListeners(new PromptPresenterOpenedEvent(this));
        }
    }
    
    public void start() {
        if(!running){
        running=true;
        updateListeners(new PromptPresenterStartEvent(this));
        }
    }

 
    public void stop() {
        if(running){
            running=false;
            updateListeners(new PromptPresenterStopEvent(this));
        }
    }

  
    public void close() {
        stop();
        if(!open){
        open=false;
        updateListeners(new PromptPresenterClosedEvent(this));
        }
    }

    
    protected synchronized void updateListeners(PromptPresenterEvent event) {
        for(PromptPresenterListener ppl:listeners){
            ppl.update(event);
        }
    }
   
    public void addPromptPresenterListener(PromptPresenterListener listener) {
        
        if (listener != null && !listeners.contains(listener)) {
            listeners.addElement(listener);
        }
    }

   
    public void removePromptPresenterListener(PromptPresenterListener listener) {
        
        if (listener != null) {
            listeners.removeElement(listener);
        }
    }
    
 
    
}
