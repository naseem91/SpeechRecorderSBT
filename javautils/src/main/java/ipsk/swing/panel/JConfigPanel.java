//    IPS Java Utils
// 	  (c) Copyright 2014
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.swing.panel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Config panel with reset buttons at bottom. 
 * @author klausj
 *
 */
public abstract class JConfigPanel extends JPanel implements ActionListener {
    
    private JPanel contentPane;
    private JPanel bottomBar;
    private JButton resetButton;
    private JButton resetToDefaultsButton;
    public JConfigPanel(){
        super(new BorderLayout());
        bottomBar =new JPanel();
        bottomBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        resetToDefaultsButton = new JButton("Reset to defaults");
        resetToDefaultsButton.addActionListener(this);
        bottomBar.add(resetToDefaultsButton);
        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        bottomBar.add(resetButton);
        contentPane=new JPanel();
        add(contentPane, BorderLayout.CENTER);
        add(bottomBar,BorderLayout.SOUTH);
    }
    public JPanel getContentPane() {
        return contentPane;
    }
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent arg0) {
        Object src=arg0.getSource();
        if(src==resetToDefaultsButton){
            resetToDefaults();
        }else if(src==resetButton){
            resetToInitial();
        }
    }
   
    
    public abstract void resetToDefaults();
    public abstract void resetToInitial();
    
}
