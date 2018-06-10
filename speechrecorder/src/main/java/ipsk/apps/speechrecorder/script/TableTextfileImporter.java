//    Speechrecorder
// 	  (c) Copyright 2009-2011
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

package ipsk.apps.speechrecorder.script;

import ipsk.apps.speechrecorder.script.ui.ItemcodeGeneratorUI;
import ipsk.db.speech.Mediaitem;
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.Recording;
import ipsk.db.speech.Section;
import ipsk.swing.EnumSelectionItem;
import ipsk.swing.EnumVector;
import ipsk.text.TableReader;
import ipsk.util.LocalizableMessage;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Imports plain text tables.
 * @author klausj
 *
 */
public class TableTextfileImporter extends JPanel implements ActionListener, DocumentListener{
	
    
    public static class ColumnDescriptor{
        private String name;
        private LocalizableMessage description=null;
        
        
        public ColumnDescriptor(String name){
            this(name,null);
        }
        public ColumnDescriptor(String name,LocalizableMessage description){
            this.name=name;
            this.description=description;
        }
        public String getName() {
            return name;
        }
        public LocalizableMessage getDescription() {
            return description;
        }
        public boolean equals(Object o){
            if(o instanceof ColumnDescriptor){
                ColumnDescriptor oCD=(ColumnDescriptor)o;
                String oN=oCD.getName();
               
                return oN.equals(name);
                
            }
            return false;
        }
        public String toString(){
            if(description!=null){
                return description.toString();
            }else{
                return name;
            }
        }
        
    }

    public static ColumnDescriptor ITEM_CODE_DESCRIPTOR=new ColumnDescriptor("Code");
    
    public static class Column{
        private Integer col;
        
        public Column(Integer row){
            this.col=row;
        }
        
        public String toString(){
            if(col==null){
                return "(no column)";
            }else{
                return col.toString();
            }
            
        }
        public boolean equals(Object o){
            if(o instanceof Column){
                Column oRow=(Column)o;
                Integer oR=oRow.getColumn();
                if(oR==null){
                    if(col==null){
                        return true;
                    }
                }else{
                    return oR.equals(col);
                }
            }
            return false;
        }

        public Integer getColumn() {
            return col;
        }
        
    }
    
	public enum FieldSeparator {
        TAB('\t',"Tabulator"), COMMA(',',"',' Comma"), SEMICOLON(';',"';' Semicolon"), COLON(':',"':' Colon"),HYPHEN('-',"'-' Hyphen"),BLANK(' ',"' ' Blank"),US('\u001F'," US  (unit separator)");

        FieldSeparator(char value,String description) {
            this.value = value;
            this.description=description;
        }
        private final char value;
        private final String description;

        public char value() {
            return value; 
        }
        public String toString() {
            return description; 
        }
    }
	
	// TODO use it
	public enum RecordSeparator {
        LF("\n","New line (UNIX end of line)"),CRLF("\r\n","Carriage return/line feed (Windows end of line)"),CR("\r","Carriage return"),RS("\u001E"," RS  (record separator)");

        RecordSeparator(String value,String description) {
            this.value = value;
            this.description=description;
        }
        private final String value;
        private final String description;

        public String value() {
            return value; 
        }
        public String toString() {
            return description; 
        }
    }
	
	private ColumnDescriptor[] colDescriptors=null;
	
//	private static enum FieldDescriptor {CODE,TEXT,URL,RECORDING_LENGTH};

	private JButton browseButton;
	private JComboBox charSetsChooser;
	private JComboBox fieldSeparatorChooser;

	private JFileChooser fileChooser;
	private File textFile;
	private SortedMap<String,Charset> charSetMap;
	private Vector<String[]> lines=new Vector<String[]>();
	private TableReader tableReader;
	private JTextField textFilePathField;
	
    private JLabel statusMessage;
    private static EnumVector<FieldSeparator> FIELD_SEPARATORS=new EnumVector<FieldSeparator>(FieldSeparator.class);

    private JComboBox[] colDescriptorChoosers=null;
//    private Vector<Row>[] columnChooserValues=null;

    private ItemcodeGenerator itemcodeGenerator;
    
    
    private boolean validScriptAvailable=false;
    public final static String VALID_SCRIPT_AVAILABLE_PROPNAME="validScriptAvailable";
    
//    private PropertyChangeSupport propertyChangeSupport;

    private ItemcodeGeneratorUI itemCodeGeneratorPanel;
    
    public TableTextfileImporter(){
        this(null,new ItemcodeGenerator());
    }
    public TableTextfileImporter(ColumnDescriptor[] columnDescriptors,ItemcodeGenerator itemcodeGenerator){
   
		super(new GridBagLayout());
		this.colDescriptors=columnDescriptors;
		this.itemcodeGenerator=itemcodeGenerator;
		
		GridBagConstraints c=new GridBagConstraints();
		c.insets = new Insets(2, 2, 2, 2);
		c.gridx=0;
		c.gridy=0;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.weightx=2.0;
		JPanel textFileImportPanel=new JPanel(new GridBagLayout());
		Border tb=BorderFactory.createTitledBorder("Text file import");
		textFileImportPanel.setBorder(tb);
		GridBagConstraints c2=new GridBagConstraints();
		c2.insets = new Insets(2, 2, 2, 2);
		c2.gridx=0;
        c2.gridy=0;
        c2.anchor=GridBagConstraints.WEST;
        textFileImportPanel.add(new JLabel("Table file:"),c2);
        
        c2.gridx++;
        c2.weightx=2.0;
        c2.fill=GridBagConstraints.HORIZONTAL;
		textFilePathField = new JTextField(30);
		textFilePathField.setEnabled(false);
		textFilePathField.setEditable(false);
		textFileImportPanel.add(textFilePathField,c2);
		
		c2.gridx++;
		c2.weightx=0;
		c2.fill=GridBagConstraints.NONE;
		browseButton=new JButton("Browse...");
		browseButton.addActionListener(this);
		textFileImportPanel.add(browseButton,c2);
		
		c2.gridx=0;
		c2.gridy++;
		JLabel charSetLabel=new JLabel("Charset:");
		textFileImportPanel.add(charSetLabel,c2);
		c2.gridx++;
		c2.gridwidth=1;
		charSetMap=Charset.availableCharsets();
		Set<String> charSetKeys=charSetMap.keySet();
		Charset defCharSet=Charset.defaultCharset();
		String defCharsetName=defCharSet.name();
		String[] charSetKeyStrs=charSetKeys.toArray(new String[0]);
		charSetsChooser=new JComboBox(charSetKeyStrs);
		charSetsChooser.setSelectedItem(defCharsetName);
		charSetsChooser.addActionListener(this);
		textFileImportPanel.add(charSetsChooser,c2);
		c2.gridx=0;
        c2.gridy++;
        c2.gridwidth=1;
        c2.anchor=GridBagConstraints.WEST;
        JLabel fsLabel=new JLabel("Field Separator:");
        textFileImportPanel.add(fsLabel,c2);
        
        c2.gridx++;
       
        fieldSeparatorChooser=new JComboBox(FIELD_SEPARATORS);
        fieldSeparatorChooser.addActionListener(this);
        textFileImportPanel.add(fieldSeparatorChooser,c2);

        c2.gridx=0;
        c2.gridy++;
        c2.anchor=GridBagConstraints.WEST;
        textFileImportPanel.add(new JLabel("Status:"),c2);
       
        c2.gridx++;
        c2.fill=GridBagConstraints.HORIZONTAL;
        c2.weightx=2.0;
        statusMessage=new JLabel("No text file loaded.");
        textFileImportPanel.add(statusMessage,c2);

        c.gridy++;
        
		add(textFileImportPanel,c);
		
		JPanel mappingPanel=new JPanel(new GridBagLayout());
		Border tb2=BorderFactory.createTitledBorder("Mapping");
		mappingPanel.setBorder(tb2);
		GridBagConstraints c3=new GridBagConstraints();
		
		
		if(colDescriptors!=null){
		    colDescriptorChoosers=new JComboBox[colDescriptors.length];
//		    columnChooserValues=new Vector[rowDescriptors.length];
		    c3.gridx=0;
		    c3.gridy=0;
		    c3.gridwidth=1;
		  
		    for(int i=0;i<colDescriptors.length;i++){
		        c3.gridx=0;
		        String label=colDescriptors[i].toString();
		        mappingPanel.add(new JLabel(label+":"),c3);
		        c3.gridx++;
//		        Vector<Row> vals=new Vector<Row>();
//		        vals.add(new Row(null));
		        colDescriptorChoosers[i]=new JComboBox();
		        colDescriptorChoosers[i].addItem(new Column(null));
		        colDescriptorChoosers[i].addActionListener(this);
		        mappingPanel.add(colDescriptorChoosers[i],c3);
//		        rowDescriptorChoosers[i].setSelectedIndex(i);
		        
		        c3.gridy++;
		    }
		}

        
		c.gridy++;
		add(mappingPanel,c);
		itemCodeGeneratorPanel = new ItemcodeGeneratorUI(itemcodeGenerator);
		
		Border tbicg=BorderFactory.createTitledBorder("Item code generator");
		itemCodeGeneratorPanel.setBorder(tbicg);
		c.gridy++;
		add(itemCodeGeneratorPanel,c);
		
		
		tableDataChanged();
	}

	public void actionPerformed(ActionEvent arg0) {
		Object src=arg0.getSource();
		for(JComboBox rdch:colDescriptorChoosers){
		    if(src==rdch){
		        setDependencies();
		        return;
		    }
		}
		if(src==browseButton){
		    fileChooser=new JFileChooser();
	        int res=fileChooser.showDialog(this,"Select");
	        if (res==JFileChooser.APPROVE_OPTION){
	            textFile=fileChooser.getSelectedFile();
	        }
		}
		if(textFile!=null){
			try {
                readFile();
            } catch (IOException e) {
               JOptionPane.showMessageDialog(this, "Could not read file: "+e.getMessage());
               return;
            }
            tableDataChanged();
		}
	}
	
	
	public String[][] getTableData(){
	   if(lines==null){
	       return null;
	   }else{
	       return lines.toArray(new String[0][]);
	   }
	}
	
	/**
     * @param validScriptAvailable the validScriptAvailable to set
     */
    public void setValidScriptAvailable(boolean validScriptAvailable) {
        boolean oldValidScriptAvailable=this.validScriptAvailable;
        this.validScriptAvailable = validScriptAvailable;
        
        if(oldValidScriptAvailable!=this.validScriptAvailable){
            firePropertyChange(VALID_SCRIPT_AVAILABLE_PROPNAME, oldValidScriptAvailable, this.validScriptAvailable);
        }
    }
    /**
     * @return the validScriptAvailable
     */
    public boolean isValidScriptAvailable() {
        return validScriptAvailable;
    }
    
    private boolean itemCodeColSelected(){
        for(int i=0;i<colDescriptorChoosers.length;i++){
            
            if(ITEM_CODE_DESCRIPTOR.equals(colDescriptors[i])){
                Column col=(Column)colDescriptorChoosers[i].getSelectedItem();
                if(col !=null && col.getColumn()!=null){
                    return true;
                }
            }
        }
        
        return false;
    }
    
	private void setDependencies(){
	    if (textFile!=null){
	        textFilePathField.setText("file:"+textFile.getPath());
	        textFilePathField.setEnabled(true);
	    }else{
	        textFilePathField.setText("");
	        textFilePathField.setEnabled(false);
	    }
	   
	    boolean itemCodeRowSelected=itemCodeColSelected();
	    itemCodeGeneratorPanel.setEnabled(!itemCodeRowSelected);
	   
	    
	}
	
	private void tableDataChanged(){
        boolean tableDataDetected=false;
        if (textFile!=null){
            textFilePathField.setText("file:"+textFile.getPath());
        }else{
            textFilePathField.setText("");
        }
        String message="No table data";
        int maxCols=0;
        int minCols=0;
        if(lines!=null && lines.size()>0){
            minCols=Integer.MAX_VALUE;
            for(String[] row:lines){
                if(row.length>maxCols){
                    maxCols=row.length;
                }
                if(row.length<minCols){
                    minCols=row.length;
                }
            }
           message=new String("Table with "+lines.size()+" rows and "+maxCols+" columns");
           tableDataDetected=true;
        }
        statusMessage.setText(message);   
        if( colDescriptorChoosers!=null){
            for(int i=0;i< colDescriptorChoosers.length;i++){
                JComboBox colDescriptorChooser=colDescriptorChoosers[i];
                ColumnDescriptor cd=colDescriptors[i];
                colDescriptorChooser.removeAllItems();
                colDescriptorChooser.addItem(new Column(null));
                
                for(int c=0;c<maxCols;c++){
                    colDescriptorChooser.addItem(new Column(c));
                }
                if(minCols==1 && cd.getName().equals("PromptText")){
                    colDescriptorChooser.setSelectedIndex(1);
                }
                if(minCols>=2){
                    colDescriptorChooser.setSelectedIndex(i+1);
                }
                
            }
        }
       
        setValidScriptAvailable(tableDataDetected);
        
    }
	
	
	private void readFile() throws IOException {
		
			FileInputStream fis=new FileInputStream(textFile);
			InputStreamReader isr=new InputStreamReader(fis,(charSetMap.get(charSetsChooser.getSelectedItem())));
			
			tableReader=new TableReader(isr);
//			Character fieldSep=(Character)(fieldSeparatorChooser.getSelectedItem());
			EnumSelectionItem<FieldSeparator> si=(EnumSelectionItem<FieldSeparator>)fieldSeparatorChooser.getSelectedItem();
			
			FieldSeparator fs=si.getEnumVal();
			Character fieldSep=fs.value();
//			fs.compareTo(UnitSeparator.COLON);
			tableReader.setFieldSeparator(fieldSep);
			lines.clear();
			String[] line=null;
			try {
                while((line=tableReader.readLineColumns())!=null){
                	lines.add(line);
                }
            } catch (IOException e) {
                lines.clear();
               throw e;
            }finally{
                if(fis!=null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                       throw e;
                    }
                }
            }
         
		
	}
	public Section createSection(){
//	    itemCodeGeneratorPanel.applyValues();
        Section section=new Section();
//        section.setPropertyChangeSupportEnabled(true);
        ArrayList<PromptItem> recordings=new ArrayList<PromptItem>();
        
        for(String[] line:lines){
            Recording r=new Recording();
            for(int rdi=0;rdi<colDescriptors.length;rdi++){
                ColumnDescriptor rd=colDescriptors[rdi];
                JComboBox rdch=colDescriptorChoosers[rdi];
           
                Column col=(Column)rdch.getSelectedItem();
                Integer colI=col.getColumn();
                String name=rd.getName();
               
                if(name.equals("Code")){
                    if(colI!=null){
                    if(line.length>colI){
                        r.setItemcode(line[colI]);
//                        r.setRecduration(20000);
                    }
                    }else{
                       String genItemcode=itemcodeGenerator.getItemCode();
                     
                       r.setItemcode(genItemcode);
                       itemcodeGenerator.next();
                    }
                }
                if(colI!=null){
                if(name.equals("PromptText")){
                    if(line.length>colI){
                        Mediaitem mi=new Mediaitem();
                        mi.setText(line[colI]);
                        ArrayList<Mediaitem> mil=new ArrayList<Mediaitem>();
                        mil.add(mi);
                        r.setMediaitems(mil);
//                        section.
                    }
                }
                }
            }
            if(r.getMediaitems().size()==0){
                Mediaitem emptyItem=new Mediaitem();
                emptyItem.setText("");
                
                r.getMediaitems().add(emptyItem);
            }
            recordings.add(r);
        }
        section.setPromptItems(recordings);
        return section;
    }
	
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        JFrame f=new JFrame();
       
        f.getContentPane().add(new TableTextfileImporter());
       
        f.pack();
        f.setVisible(true);

    }
    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent arg0) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent arg0) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent arg0) {
        // TODO Auto-generated method stub
        
    }
   
}
