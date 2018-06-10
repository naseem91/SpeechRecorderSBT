//    IPS Java Utils
//    (c) Copyright 2011
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

package ipsk.swing.applet;

import java.beans.PropertyChangeListener;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

/**
 * Abstract class to build thread safe swing applets.
 * The applet lifecycle methods init(), start(),stop() and destroy() are not guaranteed to
 * to be called by the AWT event dispatch thread.
 * This class provides methods which are called by the event dispatch thread.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public abstract class JAppletDispatchThreadWrapper extends JApplet implements PropertyChangeListener  {

	public final static boolean DEBUG = false;

	/**
     * Dispatch thread transfered call from init(). 
     */
    protected abstract void initByDT();
    /**
     * Dispatch thread transfered call from start(). 
     */
    protected abstract void startByDT();
    /**
     * Dispatch thread transfered call from stop(). 
     */
    protected abstract void stopByDT();
    /**
     * Dispatch thread transfered call from destroy(). 
     */
    protected abstract void destroyByDT();
	
	public void init(){
		if(java.awt.EventQueue.isDispatchThread()){
			initByDT();
		}else{
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
				    initByDT();
				}
			});
		} catch (InterruptedException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		}
		}
	}
	
	

    public void start(){
		if(java.awt.EventQueue.isDispatchThread()){
			startByDT();
		}else{
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					startByDT();
				}
			});
		} catch (InterruptedException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		}
		}
	}
	
	public void stop(){
		if(java.awt.EventQueue.isDispatchThread()){
			stopByDT();
		}else{
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					stopByDT();
				}
			});
		} catch (InterruptedException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		}
		}
	}
	
	public void destroy(){
		if(java.awt.EventQueue.isDispatchThread()){
			destroyByDT();
		}else{
		try {
			SwingUtilities.invokeAndWait(new Runnable(){
				public void run(){
					destroyByDT();
				}
			});
		} catch (InterruptedException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			showStatus(e.getMessage());
			e.printStackTrace();
		}
		}
	}
	
	
}
