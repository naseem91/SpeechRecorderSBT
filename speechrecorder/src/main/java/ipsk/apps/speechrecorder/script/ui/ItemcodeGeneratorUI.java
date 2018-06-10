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

import ipsk.apps.speechrecorder.script.ItemcodeGenerator;
import ipsk.apps.speechrecorder.script.ItemcodeGeneratorConfiguration;
import ipsk.swing.text.EditorKitMenu;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
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
public class ItemcodeGeneratorUI extends JPanel implements DocumentListener, ChangeListener, PropertyChangeListener {

    private ItemcodeGenerator itemcodeGenerator;
    private JTextField prefixField;
    private Document prefixDocument;
    private JSpinner fixedDecimalPlacesSpinner;
    private JSpinner currentCounterValueSpinner;
    private JTextField currValueDisplay;
    private SpinnerNumberModel fixedDecimalPlacesModel;
    private SpinnerNumberModel incrSelModel;
    
    private JSpinner incrSpinner;
    
    private JCheckBox activeBox;
    private boolean changingValue=false;

	public ItemcodeGeneratorUI(ItemcodeGenerator itemcodeGenerator){
        super(new GridBagLayout());
       
        GridBagConstraints c=new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.gridx=0;
        c.gridy=0;
        c.anchor=GridBagConstraints.WEST;
        
        activeBox=new JCheckBox("Active");
        activeBox.addChangeListener(this);
        add(activeBox,c);
        
        c.gridx=0;
        c.gridy++;
        JLabel prefixLabel=new JLabel("Prefix:");
        add(prefixLabel,c);
        c.gridx++;
        c.weightx=2.0;
        c.fill=GridBagConstraints.HORIZONTAL;
        prefixField = new JTextField();
        add(prefixField,c);
        prefixDocument=prefixField.getDocument();
        prefixDocument.addDocumentListener(this);
      
        c.gridx=0;
        c.gridy++;
        c.weightx=0;
        c.fill=GridBagConstraints.NONE;
        JLabel counterLabel=new JLabel("Fixed decimal places:");
        add(counterLabel,c);
        c.gridx++;
        
        c.fill=GridBagConstraints.HORIZONTAL;
        fixedDecimalPlacesModel=new SpinnerNumberModel(ItemcodeGeneratorConfiguration.MIN_FIXED_DECIMAL_PLACES, 
                                                       0,
                                                       ItemcodeGeneratorConfiguration.MAX_FIXED_DECIMAL_PLACES,1);
        fixedDecimalPlacesSpinner=new JSpinner(fixedDecimalPlacesModel);
        fixedDecimalPlacesModel.addChangeListener(this);
        add(fixedDecimalPlacesSpinner,c);
        
        c.gridx=0;
        c.gridy++;
        c.fill=GridBagConstraints.NONE;
        JLabel counterIncrLabel=new JLabel("Increment:");
        add(counterIncrLabel,c);
        
        c.gridx++;
        c.fill=GridBagConstraints.HORIZONTAL;
        incrSelModel = new SpinnerNumberModel(1,1, 1000, 1);
        incrSpinner = new JSpinner(incrSelModel);
        incrSelModel.addChangeListener(this);
        //incrSelModel.addChangeListener(this);
        add(incrSpinner,c);
        
        c.gridx=0;
        c.gridy++;
        c.gridwidth=2;
        JSeparator sep=new JSeparator();
        add(sep,c);
        
        c.gridx=0;
        c.gridy++;
        c.gridwidth=1;
        c.fill=GridBagConstraints.NONE;
        JLabel counterValueLabel=new JLabel("Counter value:");
        add(counterValueLabel,c);
        
        c.gridx++;
        c.fill=GridBagConstraints.HORIZONTAL;   
        SpinnerNumberModel counterModel=new SpinnerNumberModel(0,0,Integer.MAX_VALUE, 1);
        currentCounterValueSpinner = new JSpinner(counterModel);
      
        
        currentCounterValueSpinner.addChangeListener(this);
        //incrSelModel.addChangeListener(this);
        add(currentCounterValueSpinner,c);
        
        c.gridx=0;
        c.gridy++;
        c.fill=GridBagConstraints.NONE;
        JLabel currValueLabel=new JLabel("Itemcode:");
        add(currValueLabel,c);
        c.gridx++;
        c.fill=GridBagConstraints.HORIZONTAL;
//        c.weighty=2.0;
        currValueDisplay = new JTextField();
        
        EditorKitMenu currValueEkm=new EditorKitMenu(currValueDisplay,false);
        currValueDisplay.setEditable(false);
        add(currValueDisplay,c);
        
        
       
        setItemcodeGenerator(itemcodeGenerator);
        
    }
    
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        Component[] chCmps=getComponents();
        for(Component chC:chCmps){
            chC.setEnabled(enabled);
        }
    }
    
    private void configChanged(){
    	if(!changingValue){
    	ItemcodeGeneratorConfiguration cfg=itemcodeGenerator.getConfig();
        activeBox.setSelected(cfg.isActive());
        prefixField.setText(cfg.getPrefix());
        incrSelModel.setValue(cfg.getIncrement());
        Integer decimalPlaces=cfg.getFixedDecimalPlaces();
       
        fixedDecimalPlacesModel.setValue(decimalPlaces);
        DecimalFormat counterFormat=cfg.counterFormat();
        int value=itemcodeGenerator.getCounterValue();
//        String cvStr=counterFormat.format(value);
        currentCounterValueSpinner.setValue(value);
    	}
        currValueDisplay.setText(itemcodeGenerator.getItemCode());
        
    }
    
    public void setItemcodeGenerator(ItemcodeGenerator itemcodeGenerator){
        ItemcodeGenerator oldIcg=this.itemcodeGenerator;
        if(oldIcg!=null){
            oldIcg.removePropertyChangeListener(this);
        }
        this.itemcodeGenerator=itemcodeGenerator;
        
        configChanged();
        
        this.itemcodeGenerator.addPropertyChangeListener(this);
    }
    
//    public void applyValues(){
//        
//        ItemcodeGeneratorConfiguration cfg=itemcodeGenerator.getConfig();
//        cfg.setActive(activeBox.isSelected());
//        String prefix=prefixField.getText();
//        cfg.setPrefix(prefix);
//        int incr=(Integer)incrSelModel.getValue();
//        cfg.setIncrement(incr);
//        int deciPlaces=(Integer)fixedDecimalPlacesModel.getValue();
//        cfg.setFixedDecimalPlaces(deciPlaces);
//        
//    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
       
        Runnable testUI=new Runnable() {
            
            public void run() {
               JFrame f=new JFrame();
               ItemcodeGeneratorUI icgui=new ItemcodeGeneratorUI(new ItemcodeGenerator());
               f.getContentPane().add(icgui);
               f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               f.pack();
               f.setVisible(true);
               
            }
        };
        
        SwingUtilities.invokeLater(testUI);
        
    }
    
    private void documentUpdate(DocumentEvent de){
        if(changingValue)return;
        Document d=de.getDocument();
        if(d==prefixDocument){
            ItemcodeGeneratorConfiguration cfg=itemcodeGenerator.getConfig();
            changingValue=true;
            cfg.setPrefix(prefixField.getText());
            changingValue=false;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent arg0) {
       documentUpdate(arg0);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent arg0) {
        documentUpdate(arg0);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent arg0) {
        documentUpdate(arg0);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent arg0) {
    	if(changingValue)return;
    	Object src=arg0.getSource();
    		ItemcodeGeneratorConfiguration cfg=itemcodeGenerator.getConfig();
    		if(src==activeBox){
    			cfg.setActive(activeBox.isSelected());
    		}else if(src==incrSelModel){
    			changingValue=true;
    			Number in=incrSelModel.getNumber();
    			cfg.setIncrement(in.intValue());
    			changingValue=false;
    		}else if(src==fixedDecimalPlacesSpinner || src==fixedDecimalPlacesModel){
    			Number fdpn=fixedDecimalPlacesModel.getNumber();
    			changingValue=true;
    			cfg.setFixedDecimalPlaces(fdpn.intValue());
    			changingValue=false;
    		}else if(src==currentCounterValueSpinner){
    			Object val=currentCounterValueSpinner.getValue();
    			if(val instanceof Number){
    				Number valNum=(Number)val;
    				changingValue=true;
    				itemcodeGenerator.setCounterValue(valNum.intValue());
    				changingValue=false;
    			}
    		}

    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent arg0) {
        String propName=arg0.getPropertyName();
        if("counterValue".equals(propName)){
            Object valO=arg0.getNewValue();
            if(valO instanceof Number){
                int val=((Number)valO).intValue();
                currentCounterValueSpinner.setValue(val);
            }
        }else if(propName.startsWith("config")){
        	configChanged();
        }
        
        currValueDisplay.setText(itemcodeGenerator.getItemCode());
    }


}
