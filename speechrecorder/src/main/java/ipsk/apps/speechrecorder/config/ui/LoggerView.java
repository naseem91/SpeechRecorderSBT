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

import ipsk.apps.speechrecorder.SpeechRecorder;
import ipsk.apps.speechrecorder.config.Logger;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Configuration view for logging.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class LoggerView extends JPanel implements ActionListener {
  
	private static final long serialVersionUID = 1L;

	private Level[] availLevels = { Level.OFF, Level.SEVERE, Level.WARNING, Level.INFO,
            Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.ALL };

//    private Logger p;
    protected JTextField nameField;
    protected JComboBox levelSelect;
    //protected enabled=false;

    private JComboBox handlerBox;

    //protected FormatterView formatterView;
   
      
    // java.util.Locale[] availLocales;

    public LoggerView() {
        super(new GridBagLayout());

//        this.p = p;
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 5, 2, 5);
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
       
        c.gridx=0;
        c.gridy++;
        add(new JLabel("Name"), c);
        c.gridx=1;
//        add(new JLabel(p.getName()),c);
        nameField=new JTextField();
        nameField.setEditable(false);
        add(nameField,c);
        c.gridx=0;
        c.gridy++;
        add(new JLabel("Level"), c);
        c.gridx++;
        c.weightx = 1;
        levelSelect = new JComboBox(availLevels);
//        levelSelect.setSelectedItem(Level.parse(p.getLevel()));
        add(levelSelect,c);
        levelSelect.addActionListener(this);
        c.gridx=0;
        c.gridy++;
       
        
        c.gridx++;
        c.gridx=0;
        c.gridy++;
        add(new JLabel("Handlername"), c);
        c.gridx=1;
        String[] defHandlers=new String[SpeechRecorder.DEF_LOG_HANDLERS.length];
        for(int i=0;i<SpeechRecorder.DEF_LOG_HANDLERS.length;i++){
            defHandlers[i]=SpeechRecorder.DEF_LOG_HANDLERS[i].getName();
        }
        handlerBox = new JComboBox(defHandlers);
        
//        handlerBox.setSelectedItem(p.getHandlerName());
        add(handlerBox,c);
       
        
    }

    public void setLogger(Logger logger){
        nameField.setText(logger.getName());
        levelSelect.setSelectedItem(Level.parse(logger.getLevel()));
        handlerBox.setSelectedItem(logger.getHandlerName());
    }
    
    
    public void setEnabled(boolean enabled){
        Component[] childs=getComponents();
        for(int i=0;i<childs.length;i++){
            childs[i].setEnabled(enabled);
        }
    }
    
    
    public void applyValues(Logger l){
        l.setLevel(((Level) levelSelect.getSelectedItem()).getName());
    }
    
    public void revalidatePanel(){
      
      
        revalidate();
        repaint();
    }
    
    /*
     * /* (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        Object src = arg0.getSource();
        if (src == levelSelect) {
//            p.setLevel(((Level) levelSelect.getSelectedItem()).getName());
        
        }
        
    }

 
    
    

}
