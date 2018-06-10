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

package ipsk.apps.speechrecorder.config;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class HandlerView extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	protected JComboBox levelSelect;
    protected FormatterView formatView;
    protected JTextField nameField;


    // java.util.Locale[] availLocales;

    public HandlerView(Handler p) {
        super(new GridBagLayout());

//        this.p = p;
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 5, 2, 5);
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        add(new JLabel("Name"),c);
        c.gridx++;
        c.weightx=2.0;
        c.fill=GridBagConstraints.HORIZONTAL;
        nameField=new JTextField(p.getName());
        nameField.setEditable(false);
        add(nameField,c);
        c.gridx++;
        //c.gridy++;
        add(new JLabel("Format"),c);
        c.gridx++;
        Formatter f=p.getFormatter();
//        if (f==null){
//           
//            add(new JLabel("(default formatter)"),c);
//        }else{
        formatView=new FormatterView(p.getFormatter());
        formatView.addActionListener(this);
        add(formatView,c);
        //}
//        c.gridx=0;
//        c.gridy++;
//        add(new JLabel("Level"), c);
//        c.gridx++;
//        c.weightx = 1;
//        levelSelect = new JComboBox(availLevels);
//        levelSelect.setSelectedItem(Level.parse(p.getAttributeLevel()));
//        add(levelSelect,c);
//        levelSelect.addActionListener(this);
    }

    public void applyValues(Handler h){
        h.setFormatter(formatView.getFormatter());
    }
    
    /*
     * /* (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        Object src = arg0.getSource();
        if (src == levelSelect) {
            //p.setAttributeLevel(((Level) levelSelect.getSelectedItem()).getName());
        }else if(src==formatView){
//            p.setFormatter(formatView.getFormatter());
        }

    }

 
    

}
