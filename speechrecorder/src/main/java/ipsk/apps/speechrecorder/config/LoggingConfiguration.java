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
 * Created on 14.02.2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ipsk.apps.speechrecorder.config;

import ipsk.beans.dom.DOMElements;

/**
 * Container for logging configurations.
 * @author klausj
 *
 */
@DOMElements({"logger","handler"})
public class LoggingConfiguration{

	private Logger[] logger=new Logger[0];
    private Handler[] handler=new Handler[0];
    
    
	/**
	 * 
	 */
	public LoggingConfiguration() {
	}
    
    /**
     * @return Returns the loggers.
     */
    public Logger[] getLogger() {
        return logger;
    }
    
    /**
     * @param logger The loggers to set.
     */
    public void setLogger(Logger[] logger) {
        this.logger = logger;
    }

    /**
     * @return Returns the handler.
     */
    public Handler[] getHandler() {
        return handler;
    }

    /**
     * @param handler The handler to set.
     */
    public void setHandler(Handler[] handler) {
        this.handler = handler;
    }

	
}
