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

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class FontView extends JPanel implements ActionListener, ChangeListener, ListSelectionListener {

	private static final long serialVersionUID = 1L;

	private JSpinner sizeSpinner;

//    private Font f;
    private JComboBox familyBox;
//    private DefaultListModel altFamilyListModel=new DefaultListModel();
    private JList altFamilyList;
   
    private JButton addAltFamilyButt;
    private JButton removeAltFamilyButt;

    private JCheckBox boldBox;

    private JCheckBox italicBox;

    private JTextArea tc;
    
    private boolean adjusting=false;

	private JComboBox altFamilyBox;
    /**
     * 
     */
    public FontView() {
        this(null);
    }
	public FontView(Font f) {
        super(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.5;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        
        c.gridx=0;
        c.gridy++;
      
        c.weightx=0;
        c.weighty=0;
        c.gridwidth=3;
        c.fill = GridBagConstraints.HORIZONTAL;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        String[] familys = ge.getAvailableFontFamilyNames();
        add(new JLabel("Family"), c);
        familyBox = new JComboBox(familys);
        c.gridx++;
        add(familyBox, c);
        
        c.gridx=0;
        c.gridy++;
        c.gridwidth=1;
        add(new JLabel("Alt. families"), c);
        
        c.gridx++;
        c.gridwidth=3;
        altFamilyList=new JList();
        altFamilyList.setVisibleRowCount(3);
        altFamilyList.addListSelectionListener(this);
        JScrollPane famlistSp=new JScrollPane(altFamilyList);
        add(famlistSp,c);
        
        c.gridx=0;
        c.gridy++;
        c.gridwidth=1;
        
        altFamilyBox = new JComboBox(familys);
        c.gridx++;
        c.fill = GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.WEST;
        add(altFamilyBox, c);
        addAltFamilyButt=new JButton("+");
        addAltFamilyButt.addActionListener(this);
        c.gridx++;
        add(addAltFamilyButt,c);
        removeAltFamilyButt=new JButton("-");
        c.gridx++;
        add(removeAltFamilyButt,c);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Bold"), c);
        boldBox = new JCheckBox();

        // boldBox.setSelected((style & java.awt.Font.BOLD) > 0);
        c.gridx++;
        add(boldBox, c);
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Italic"), c);
        italicBox = new JCheckBox();
        // italicBox.setSelected((style & java.awt.Font.ITALIC) > 0);

        c.gridx++;
        add(italicBox, c);
        c.gridx = 0;
        c.gridy++;
        add(new JLabel("Size"), c);
        Float min = new Float(0);
        Float max = new Float(1000000);
        Float step = new Float(1);
        SpinnerNumberModel model = new SpinnerNumberModel(min, min, max, step);
        sizeSpinner = new JSpinner(model);

        // sizeSpinner.setValue(new Float(f.getSize()));
        c.gridx++;
        add(sizeSpinner, c);
        
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 4;
        c.weightx = 2;
        c.weighty = 2;
        c.fill=GridBagConstraints.BOTH;
        tc = new JTextArea();
        // tc.setSize(200,60);
        tc.setPreferredSize(new Dimension(100, 60));
        // tc.setFont(f.toFont());
        tc.setText("Example !");
        add(tc, c);
        if(f!=null){
            setSelectedFont(f);
        }
        
        familyBox.addActionListener(this);
        altFamilyBox.addActionListener(this);
        removeAltFamilyButt.addActionListener(this);
        boldBox.addActionListener(this);
        italicBox.addActionListener(this);
        sizeSpinner.addChangeListener(this);
        
    }

//    public FontView(Font f) {
//        this();
//        setSelectedFont(f);
//    }

   

    public void setSelectedFont(Font f) {
//        this.f = f;
        adjusting=true;
        String[] families = f.getFamily();
//        familyListModel.setSize(families.length);
//        for(int i=0;i<families.length;i++){
//        	familyListModel.set(i, families[i]);
//        }
        int famSize=families.length;
        if(famSize<=0){
        	throw new IllegalArgumentException("At least one font family expected!");
        }
        String family=families[0];
        String[] altFamilies=new String[families.length-1];
        for(int i=1;i<famSize;i++){
        	altFamilies[i-1]=families[i];
        }
        altFamilyList.setListData(altFamilies);
        familyBox.setSelectedItem(family);
        int style = f.getStyle();
        boldBox.setSelected((style & java.awt.Font.BOLD) > 0);

        italicBox.setSelected((style & java.awt.Font.ITALIC) > 0);

        sizeSpinner.setValue(new Float(f.getSize()));

        tc.setFont(f.toFont());
        // tc.setText("Example !");
        setDependencies();
        adjusting=false;
    }
    
    public Font getSelectedFont(){
//        if(f==null){
//            f=new Font();
//        }
        Font f=new Font();
        applySelectedFont(f);
        return f;
    }
    
    
    private String[] familiesInList(){
    	int fontsCount=altFamilyList.getModel().getSize();
    	String[] fontFams=new String[fontsCount];
    	for(int i=0;i<fontsCount;i++){
    		fontFams[i]=(String)altFamilyList.getModel().getElementAt(i);
    	}
    	return(fontFams);
    }
    
    private List<String> familiesInListAsList(){
    	int fontsCount=altFamilyList.getModel().getSize();
    	List<String> fontFams=new ArrayList<String>();
    	for(int i=0;i<fontsCount;i++){
    		fontFams.add((String)altFamilyList.getModel().getElementAt(i));
    	}
    	return(fontFams);
    }
    
    public void applySelectedFont(Font f){
    	List<String> fontFams=new ArrayList<String>();
    	fontFams.add((String)familyBox.getSelectedItem());
    	List<String> altFontFams=familiesInListAsList();
    	fontFams.addAll(altFontFams);
        f.setFamily(fontFams.toArray(new String[0]));
        
        if (boldBox.isSelected()) {
            f.setStyle(f.getStyle() | java.awt.Font.BOLD);
        } else {
            f.setStyle(f.getStyle() & ~java.awt.Font.BOLD);
        }
        if (italicBox.isSelected()) {
            f.setStyle(f.getStyle() | java.awt.Font.ITALIC);
        } else {
            f.setStyle(f.getStyle() & ~java.awt.Font.ITALIC);
        }
        f.setSize(((Float) (sizeSpinner.getValue())).floatValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent ce) {
        Object src = ce.getSource();
        if (src == sizeSpinner) {
        	 updatePreview();
        }
      
    }
    
    private void setDependencies(){
    	 if(!adjusting)updatePreview();
    	 
    	 Object altFamsel=altFamilyList.getSelectedValue();
    	 removeAltFamilyButt.setEnabled(altFamsel instanceof String);
    	 String selFont=(String)familyBox.getSelectedItem();
    	 List<String> selFonts=familiesInListAsList();
    	 selFonts.add(selFont);
    	 String selAltFont=(String)altFamilyBox.getSelectedItem();
    	 
    	 addAltFamilyButt.setEnabled(!(selFonts.contains(selAltFont)));
    	 
//    	 GraphicsEnvironment ge = GraphicsEnvironment
//                 .getLocalGraphicsEnvironment();
//    	 String[] allFonts= ge.getAvailableFontFamilyNames();
//    	 List<String> selectableFontsList=Arrays.asList(allFonts);
//    	 List<String> selAltFonts=familiesInListAsList();
//    	 // minus alternative fonts
//    	 selectableFontsList.removeAll(selAltFonts);
//    	 String selFont=(String)familyBox.getSelectedItem();
//    	 // minus selected (default) font
//    	 selectableFontsList.remove(selFont);
    	 
    }
    
    private void updatePreview(){
//    	 applySelectedFont(f);
        Font f=getSelectedFont();
    	 tc.setFont(f.toFont());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
        Object src = ae.getSource();
//        if(src==familyBox){
//        	String[] famsInList=familiesInList();
//        	famsInList[0]=(String)familyBox.getSelectedItem();
//        	familyList.setListData(famsInList);
//        	
//        }
//      else 
        if(src==addAltFamilyButt){
        	List<String> fams=familiesInListAsList();
        	fams.add((String)altFamilyBox.getSelectedItem());
        	altFamilyList.setListData(fams.toArray(new String[0]));
        }else if(src==removeAltFamilyButt){
        	Object sel=altFamilyList.getSelectedValue();
        	if(sel instanceof String){
        		List<String> fams=familiesInListAsList();
            	fams.remove(sel);
            	altFamilyList.setListData(fams.toArray(new String[0]));
        	}
        	
        }
       
        setDependencies();
    }
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent lse) {
		Object src=lse.getSource();
		if(src==altFamilyList){
			setDependencies();
		}
	}
    
    
}
