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

package ipsk.apps.speechrecorder.monitor;

import javax.swing.JComponent;

/**
 * Interface for the start stop signaling panel.
 * Plugin interface for starting and stopping speech signal (default plugin is a traffic light)
 */

public interface StartStopSignal{

   
    public enum State {IDLE,PRERECORDING,POSTRECORDING,RECORDING,OFF};
  
    public void setStatus(State status);
    public JComponent getComponent();
	
    
    
}