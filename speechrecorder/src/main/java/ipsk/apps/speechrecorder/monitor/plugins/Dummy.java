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

package ipsk.apps.speechrecorder.monitor.plugins;

import ipsk.apps.speechrecorder.monitor.StartStopSignal;

import java.awt.Dimension;

import javax.swing.JComponent;

public class Dummy extends JComponent implements StartStopSignal {

	private static final long serialVersionUID = 1L;
	
    private static final int WIDTH = 0;
    private static final int HEIGHT = 0;
	private static final int BORDER_WIDTH=0;

	private Dimension fixedSize=new Dimension(WIDTH+BORDER_WIDTH*2,HEIGHT+BORDER_WIDTH*2); 
	
	public Dummy() {
		super();
	}
	
    public JComponent getComponent() {
       return this;
    }


    public void setStatus(State status) {
        // ignored
    }
    
 
    public Dimension getPreferredSize(){
        return fixedSize;
    }
   
  
}