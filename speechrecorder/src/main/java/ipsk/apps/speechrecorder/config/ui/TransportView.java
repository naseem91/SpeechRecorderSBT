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
 * Date  : Jul 27, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.config.ui;

import ipsk.apps.speechrecorder.config.Control;
import ipsk.apps.speechrecorder.config.KeyInputMap;
import ipsk.swing.TitledPanel;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.JPanel;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class TransportView extends JPanel {

	private static final long serialVersionUID = 1L;
	private KeyInputMapView keyInputMapView;
    
    public TransportView(Action[] actions){
        super(new BorderLayout());
        TitledPanel keyInputMapPanel=new TitledPanel("Key input/action map");
        keyInputMapView=new KeyInputMapView(actions);
        keyInputMapPanel.add(keyInputMapView);
        add(keyInputMapPanel,BorderLayout.CENTER);
        
    }
    /**
     * @param control
     */
    public void setControl(Control control) {
        KeyInputMap keyInputMap=control.getKeyInputMap();
        keyInputMapView.setKeyInputmap(keyInputMap);
    }
    
    public void applyValues(Control control){
        keyInputMapView.applyValues(control.getKeyInputMap());
    }

    public KeyInputMapView getKeyInputMapView() {
        return keyInputMapView;
    }

   
    
}
