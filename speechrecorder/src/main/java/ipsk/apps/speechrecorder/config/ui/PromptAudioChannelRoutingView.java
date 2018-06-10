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

package ipsk.apps.speechrecorder.config.ui;

import ipsk.apps.speechrecorder.config.PromptConfiguration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * UI panel for audio prompts (stimuli) configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class PromptAudioChannelRoutingView extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JSpinner channelOffsetSpinner;
	private SpinnerNumberModel channelOffsetModel;
	public PromptAudioChannelRoutingView() {
		super(new GridBagLayout());
		GridBagConstraints c=new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		add(new JLabel("Channel offset:"),c);
		c.gridx++;
		channelOffsetModel=new SpinnerNumberModel(0, 0,255,1);
		channelOffsetSpinner=new JSpinner(channelOffsetModel);
		add(channelOffsetSpinner,c);
	}
	
	/**
     * @param promptConfiguration
     */
    public void setPromptConfiguration(PromptConfiguration promptConfiguration) {
    	int channelOffset=promptConfiguration.getAudioChannelOffset();
    	channelOffsetModel.setValue(channelOffset);
       setDependencies();
    }

	public void applyValues(PromptConfiguration p){
		Number chOffsetAsNumber=channelOffsetModel.getNumber();
		p.setAudioChannelOffset(chOffsetAsNumber.intValue());
	}
	
	private void setDependencies() {
		//buttonsInPromptWindowCheckBox.setEnabled(p.getShowPromptWindow());
		//buttonsInPromptWindowLabel.setEnabled(p.getShowPromptWindow());
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		
		setDependencies();
	}


    
}
