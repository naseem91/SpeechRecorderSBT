//    Speechrecorder
// 	  (c) Copyright 2011
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


package ipsk.apps.speechrecorder.config.ui.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import ipsk.apps.speechrecorder.config.AudioClipView;
import javax.swing.JCheckBox;
import javax.swing.JPanel;


/**
 * @author klausj
 *
 */
public class AudioClipViewConfigurationUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private JCheckBox showSignalViewJB;
    private JCheckBox showPlayActionBarJB;
    private JCheckBox showSonagramJB;
    
    
    public AudioClipViewConfigurationUI() {
     super(new GridBagLayout());
     
     showSignalViewJB=new JCheckBox("Show signal view");
     showPlayActionBarJB=new JCheckBox("Show playback action bar");
     showSonagramJB=new JCheckBox("Show songram");
     GridBagConstraints c = new GridBagConstraints();
     c.fill = GridBagConstraints.HORIZONTAL;
     c.insets = new Insets(2, 5, 2, 5);
     c.anchor = GridBagConstraints.PAGE_START;
     c.gridx = 0;
     c.gridy = 0;
     add(showSignalViewJB,c);
     c.gridy++;
     add(showPlayActionBarJB,c);
     c.gridy++;
     add(showSonagramJB,c);
    }

    /**
     * @param acv
     */
    public void setConfiguration(AudioClipView acv) {
        showSignalViewJB.setSelected(acv.getShowSignalView());
        showPlayActionBarJB.setSelected(acv.getShowPlayActionBar());
        showSonagramJB.setSelected(acv.getShowSonagram());
        
    }

    /**
     * @param audioClipView
     */
    public void applyValues(AudioClipView audioClipView) {
        audioClipView.setShowSignalView(showSignalViewJB.isSelected());
        audioClipView.setShowPlayActionBar(showPlayActionBarJB.isSelected());
        audioClipView.setShowSonagram(showSonagramJB.isSelected());
    }



   

}
