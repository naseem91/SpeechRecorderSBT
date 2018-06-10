//    Speechrecorder
// 	  (c) Copyright 2012
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


package ipsk.apps.speechrecorder.script.ui;

import ipsk.apps.speechrecorder.script.ItemcodeGeneratorConfiguration;
import ipsk.swing.text.EditorKitMenu;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

/**
 * UI panel for the itemcode generator.
 * 
 * @author klausj
 *
 */
public class ItemcodeGeneratorConfigurationUI extends JPanel {


    private JTextField prefixField;
    private Document prefixDocument;
    private JSpinner fixedDecimalPlacesSpinner;
 
    private SpinnerNumberModel fixedDecimalPlacesModel;
    private SpinnerNumberModel incrSelModel;
    private JSpinner incrSpinner;
    private JCheckBox activeBox;
    private ItemcodeGeneratorConfiguration itemcodeGeneratorConfiguration;
    
    public ItemcodeGeneratorConfigurationUI(ItemcodeGeneratorConfiguration itemcodeGeneratorConfiguration){
        super(new GridBagLayout());
        this.itemcodeGeneratorConfiguration=itemcodeGeneratorConfiguration;
        GridBagConstraints c=new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.gridx=0;
        c.gridy=0;
        c.anchor=GridBagConstraints.WEST;
        
        activeBox=new JCheckBox("Active");
//        activeBox.addChangeListener(this);
        add(activeBox,c);
        
        c.gridx=0;
        c.gridy++;
        JLabel prefixLabel=new JLabel("Prefix:");
        add(prefixLabel,c);
        c.gridx++;
        c.weightx=2.0;
        c.fill=GridBagConstraints.HORIZONTAL;
        prefixField = new JTextField();
        EditorKitMenu prefixFieldEkm=new EditorKitMenu(prefixField);
        add(prefixField,c);
        prefixDocument=prefixField.getDocument();
//        prefixDocument.addDocumentListener(this);
      
        c.gridx=0;
        c.gridy++;
        c.weightx=0;
        c.fill=GridBagConstraints.NONE;
        JLabel counterLabel=new JLabel("Counter fixed decimal places:");
        add(counterLabel,c);
        c.gridx++;
        
        c.fill=GridBagConstraints.HORIZONTAL;
        fixedDecimalPlacesModel=new SpinnerNumberModel(ItemcodeGeneratorConfiguration.MIN_FIXED_DECIMAL_PLACES, 
                                                       0,
                                                       ItemcodeGeneratorConfiguration.MAX_FIXED_DECIMAL_PLACES,1);
        fixedDecimalPlacesSpinner=new JSpinner(fixedDecimalPlacesModel);
//        fixedDecimalPlacesModel.addChangeListener(this);
        add(fixedDecimalPlacesSpinner,c);
        c.gridx=0;
        c.gridy++;
        c.fill=GridBagConstraints.NONE;
        JLabel counterIncrLabel=new JLabel("Counter increment:");
        add(counterIncrLabel,c);
        
        c.gridx++;
        incrSelModel = new SpinnerNumberModel(0,0, 1000, 1);
        incrSpinner = new JSpinner(incrSelModel);
//        incrSelModel.addChangeListener(this);
        //incrSelModel.addChangeListener(this);
        add(incrSpinner,c);
        
       
    }
    
    /**
     * 
     */
    public ItemcodeGeneratorConfigurationUI() {
        this(new ItemcodeGeneratorConfiguration());
    }

    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        Component[] chCmps=getComponents();
        for(Component chC:chCmps){
            chC.setEnabled(enabled);
        }
    }
    
    
    public void setItemcodeGeneratorConfiguration(ItemcodeGeneratorConfiguration itemcodeGeneratorConfiguration){

        this.itemcodeGeneratorConfiguration=itemcodeGeneratorConfiguration;
        activeBox.setSelected(itemcodeGeneratorConfiguration.isActive());
        prefixField.setText(itemcodeGeneratorConfiguration.getPrefix());
        incrSelModel.setValue(itemcodeGeneratorConfiguration.getIncrement());
        Integer decimalPlaces=itemcodeGeneratorConfiguration.getFixedDecimalPlaces();
       
        fixedDecimalPlacesModel.setValue(decimalPlaces);
      
//        this.itemcodeGeneratorConfiguration.addPropertyChangeListener(this);
    }
    
    public void applyValues(){
        applyValues(this.itemcodeGeneratorConfiguration);
    }
    
    public void applyValues(ItemcodeGeneratorConfiguration itemcodeGeneratorConfiguration){
    	boolean active=activeBox.isSelected();
    	itemcodeGeneratorConfiguration.setActive(active);
        String prefix=prefixField.getText();
        itemcodeGeneratorConfiguration.setPrefix(prefix);
        int incr=(Integer)incrSelModel.getValue();
        itemcodeGeneratorConfiguration.setIncrement(incr);
        int deciPlaces=(Integer)fixedDecimalPlacesModel.getValue();
        itemcodeGeneratorConfiguration.setFixedDecimalPlaces(deciPlaces);
        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
       
        Runnable testUI=new Runnable() {
            
            public void run() {
               JFrame f=new JFrame();
               ItemcodeGeneratorConfigurationUI icgui=new ItemcodeGeneratorConfigurationUI(new ItemcodeGeneratorConfiguration());
               f.getContentPane().add(icgui);
               f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               f.pack();
               f.setVisible(true);
               
            }
        };
        
        SwingUtilities.invokeLater(testUI);
        
    }
    
   

}
