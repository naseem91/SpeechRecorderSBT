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
 * Date  : Jun 24, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config.ui.audio;

import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;



/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class ControllerSelector extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private JTextField acClassNameField;
//    private Document acClassNameDoc;
    
	public ControllerSelector() {
		super(new GridBagLayout());
		GridBagConstraints c=new GridBagConstraints();
		
		c.gridx=0;
		c.gridy=0;
		
		add(new JLabel("Audiocontroller class name:"),c);
		c.weightx = 2.0;
        c.fill=GridBagConstraints.HORIZONTAL;
        acClassNameField = new JTextField(20);
        c.gridx++;
        add(acClassNameField,c);
       

	}

	/**
     * @param project
     */
    public void setProjectConfiguration(ProjectConfiguration project) {
        acClassNameField.setText(project.getAudioControllerClass());
    }
  


	public void applyValues(ProjectConfiguration p){
	    p.setAudioControllerClass(acClassNameField.getText());
	}
	



}
