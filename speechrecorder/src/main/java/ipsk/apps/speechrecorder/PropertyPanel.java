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


package ipsk.apps.speechrecorder;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class PropertyPanel extends JPanel {

	private static final long serialVersionUID = -3497207105895556102L;
	private JLabel[] desc;
    private JLabel[] val;
        
    final static Font labelFont = new Font("sans-serif",Font.PLAIN,12);
    final static Font valueFont = new Font("sans-serif",Font.BOLD,12);

    PropertyPanel(Vector labels, Vector data) {
        
        final int l = labels.size();
        final int cols = (int) ((l + 1) / 2) * 2;
        final int rows = (int) (l * 2 / cols) + 1;
        final int hspace = 2;
        final int vspace = 2;
                
        setLayout(new GridLayout(rows, cols, hspace, vspace));
        setForeground(Color.black);

        desc = new JLabel[l];
        val = new JLabel[l];
                
        for(int i = 0; i < l; i++) {
            desc[i] = new JLabel((String) labels.elementAt(i) + ":" , JLabel.RIGHT);
            desc[i].setFont(labelFont);
            val[i] = new JLabel((String) data.elementAt(i), JLabel.LEFT);
            val[i].setFont(valueFont);
            add(desc[i]);
            add(val[i]);
        }

        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        setSize(getPreferredSize());
    }

    public Dimension getPreferredSize() {
        return(new Dimension(400,50));
    }
}

