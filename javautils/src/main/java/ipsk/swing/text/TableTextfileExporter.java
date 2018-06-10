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

package ipsk.swing.text;

import ipsk.swing.OkAction;
import ipsk.text.TableTextFormat;
import ipsk.text.TableTextFormats.Profile;
import ipsk.text.TableWriter;
import ipsk.text.table.ColumnDescriptor;
import ipsk.text.table.TableExportProvider;
import ipsk.text.table.TableExportSchemaProvider;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Exports to plain text tables.
 * 
 * @author klausj
 * 
 */
public class TableTextfileExporter<S extends TableExportSchemaProvider,E extends TableExportProvider> extends
        JPanel implements ActionListener,ClipboardOwner {

    private List<ColumnDescriptor> colDescriptors = null;

    private TableFormatSelector formatSelector;
    public void setSelectedProfile(Profile profile) {
        formatSelector.setSelectedProfile(profile);
    }

    private JButton clipboardButton;
    
    private JButton browseButton;
    private JFileChooser fileChooser;
    private File textFile;
    private JTextField textFilePathField;
    private JButton fileExportButton;

    private JLabel statusMessage;

    private S schemaProvider;
    private E data;

    private List<JComboBox> colDescriptorChoosers = new ArrayList<JComboBox>();

    private boolean exportPossible = false;

	private Clipboard clipboard;

    public final static String EXPORT_POSSIBLE_PROPNAME = "exportPossible";

    public static class ColumnSelectionItem {
        private ColumnDescriptor columnDescriptor = null;
        private String notAssignedText = "-Not assigned-";

        public ColumnSelectionItem(ColumnDescriptor columnDescriptor) {
            super();
            this.columnDescriptor = columnDescriptor;
        }

        public ColumnSelectionItem() {
            super();
        }

        public String toString() {
            if (columnDescriptor != null) {
                return columnDescriptor.toString();
            } else {
                return (notAssignedText);
            }
        }

        public boolean equals(Object o) {
            if (o instanceof ColumnSelectionItem) {
                ColumnSelectionItem csio = (ColumnSelectionItem) o;
                ColumnDescriptor cdo = csio.getColumnDescriptor();
                if (cdo == null) {
                    if (columnDescriptor == null) {
                        return true;
                    }
                } else {
                    return (cdo.equals(columnDescriptor));
                }
            }
            return false;
        }

        public ColumnDescriptor getColumnDescriptor() {
            return columnDescriptor;
        }
    }

    public class ColumnSelectionItems extends Vector<ColumnSelectionItem> {
        public ColumnSelectionItems(List<ColumnDescriptor> colDescrs) {
            super();
            // add not assigned value
            add(new ColumnSelectionItem());
            for (ColumnDescriptor cd : colDescrs) {
                add(new ColumnSelectionItem(cd));
            }
        }
    }

    // public TableTextfileExporter(Script script){
    // this(script,null);
    // }
    public TableTextfileExporter(S schemaProvider) {
        super(new GridBagLayout());
        this.schemaProvider=schemaProvider;
        try{
        	clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        } catch (SecurityException se) {
        	System.err.println("WARNING: System clipboard not accessible.");
        	clipboard = null;
        }
        colDescriptors = schemaProvider.getColumnDescriptors();
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        
        c.gridx=0;
        c.gridy=0;
        c.weightx=2;
        c.fill = GridBagConstraints.HORIZONTAL;
        JPanel mappingPanel = new JPanel(new GridBagLayout());
        Border mapBrd = BorderFactory.createTitledBorder("Mapping");
        mappingPanel.setBorder(mapBrd);
        GridBagConstraints c3 = new GridBagConstraints();

        if (colDescriptors != null) {
            c3.gridx = 0;
            c3.gridy = 0;
            c3.gridwidth = 1;
            for (ColumnDescriptor cd : colDescriptors) {

                ColumnSelectionItems colModel = new ColumnSelectionItems(
                        colDescriptors);
                ColumnSelectionItem csi = new ColumnSelectionItem(cd);
                JComboBox ch = new JComboBox(colModel);
                colDescriptorChoosers.add(ch);
                ch.setSelectedItem(csi);
                mappingPanel.add(ch, c3);
                c3.gridx++;

            }
        }

        add(mappingPanel, c);
        c.gridx = 0;
        c.gridy++;
        formatSelector = new TableFormatSelector();
        Border fmtBrd = BorderFactory.createTitledBorder("Format");
        formatSelector.setBorder(fmtBrd);
        add(formatSelector, c);
       
        c.gridx = 0;
        c.gridy++;
        JPanel exportDestinationPanel = new JPanel(new GridBagLayout());
        Border cbb = BorderFactory.createTitledBorder("Export destination");
        exportDestinationPanel.setBorder(cbb);
        
        GridBagConstraints c2 = new GridBagConstraints();
        
        c2.insets = new Insets(2, 2, 2, 2);
        c2.gridx = 0;
        c2.gridy = 0;

        exportDestinationPanel.add(new JLabel("Clipboard:"), c2);
        
        c2.gridx=3;
        c2.fill = GridBagConstraints.NONE;
        c2.anchor = GridBagConstraints.EAST;
        clipboardButton = new JButton("Export to Clipboard");
        clipboardButton.addActionListener(this);
        exportDestinationPanel.add(clipboardButton, c2);

        c2.gridx = 0;
        c2.gridy++;
        c2.anchor = GridBagConstraints.WEST;
        exportDestinationPanel.add(new JLabel("Table file:"), c2);

        c2.gridx++;
        c2.weightx = 3;
        c2.fill = GridBagConstraints.HORIZONTAL;
        textFilePathField = new JTextField(30);
        textFilePathField.setEnabled(false);
        textFilePathField.setEditable(false);
        exportDestinationPanel.add(textFilePathField, c2);

        c2.gridx++;
        c2.weightx = 0;
        c2.anchor = GridBagConstraints.EAST;
        c2.fill = GridBagConstraints.NONE;
        browseButton = new JButton("Browse...");
        browseButton.addActionListener(this);
        exportDestinationPanel.add(browseButton, c2);
        
        c2.gridx++;
        fileExportButton = new JButton("Export to file");
        fileExportButton.addActionListener(this);
        exportDestinationPanel.add(fileExportButton, c2);

        add(exportDestinationPanel, c);
        setDependencies();
    }
    
    public void setData(E data){
        this.data=data;
    }

    public void actionPerformed(ActionEvent arg0) {
        Object src = arg0.getSource();
        for (JComboBox rdch : colDescriptorChoosers) {
            if (src == rdch) {
                setDependencies();
                return;
            }
        }
        
        if (src == clipboardButton) {
        	try {
        		writeClipboard();
        	} catch (IOException e) {
        		e.printStackTrace();
        		JOptionPane.showMessageDialog(this, "Error writing table to system clipboard: "+e.getMessage());
        	}
        }else if (src == browseButton) {
        	fileChooser = new JFileChooser();
        	int res = fileChooser.showDialog(this,"Select");
        	if (res == JFileChooser.APPROVE_OPTION) {
        		textFile = fileChooser.getSelectedFile();
        		boolean oldExportPossible = exportPossible;
        		exportPossible = true;
        		if (oldExportPossible != exportPossible) {
        			firePropertyChange(EXPORT_POSSIBLE_PROPNAME,
        					oldExportPossible, exportPossible);
        		}
        		setDependencies();
        	}
        }else if (src == fileExportButton) {
        	if(textFile!=null){
        		try {
					writeFile();
				} catch (IOException e) {
					
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, "Error writing table to file: "+e.getMessage());
				}
        	}
        }

    }
    
    public void writeTable(Writer writer) throws IOException {

    	TableTextFormat format = formatSelector.getFormat();
    	TableWriter tw = new TableWriter(writer, format);
    	List<ColumnDescriptor> selColList = new ArrayList<ColumnDescriptor>();
    	for (JComboBox colSelBox : colDescriptorChoosers) {
    		ColumnSelectionItem csi = (ColumnSelectionItem) colSelBox
    				.getSelectedItem();
    		ColumnDescriptor cd = csi.getColumnDescriptor();
    		if (cd != null) {
    			selColList.add(cd);
    		}
    	}
    	List<List<List<String>>> groups = data.tableData(selColList);
    	try {
    		tw.writeGroups(groups);
    	} catch (IOException e) {
    		throw e;
    	} finally {
    		tw.close();
    	}

    }
    
    public void writeClipboard() throws IOException {
        if (clipboard != null) {
        	StringWriter sw=new StringWriter();
        	writeTable(sw);
        	
        	StringSelection sSel=new StringSelection(sw.toString());
        	clipboard.setContents(sSel, this);
          
        }
    }

    public void writeFile() throws IOException {
        if (textFile != null) {
            if(textFile.exists()){
                int ans=JOptionPane.showConfirmDialog(this, textFile.getName()+" exists!\nDo you want to overwrite?", "Overwrite table export file", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if(ans!=JOptionPane.YES_OPTION){
                    return;
                }
            }
            FileWriter fw = new FileWriter(textFile);
            writeTable(fw);
        }
    }

    // private boolean itemCodeColSelected(){
    // for(int i=0;i<colDescriptorChoosers.length;i++){
    //
    // if(ITEM_CODE_DESCRIPTOR.equals(colDescriptors[i])){
    // Column col=(Column)colDescriptorChoosers[i].getSelectedItem();
    // if(col !=null && col.getColumn()!=null){
    // return true;
    // }
    // }
    // }
    //
    // return false;
    // }

    private void setDependencies() {
        if (textFile != null) {
            textFilePathField.setText("file:" + textFile.getPath());
            textFilePathField.setEnabled(true);
        } else {
            textFilePathField.setText("");
            textFilePathField.setEnabled(false);
        }
        clipboardButton.setEnabled(clipboard!=null);
        fileExportButton.setEnabled(exportPossible && textFile!=null);
    }

    public boolean isExportPossible() {
        return exportPossible;
    }

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard, java.awt.datatransfer.Transferable)
	 */
	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// OK no problem
	}

    // private void writeFile() throws IOException {
    //
    // FileOutputStream fos=new FileOutputStream(textFile);
    // OutputStreamWriter osr=new
    // OutputStreamWriter(fos,(charSetMap.get(charSetsChooser.getSelectedItem())));
    //
    // EnumSelectionItem<UnitSeparator>
    // si=(EnumSelectionItem<UnitSeparator>)fieldSeparatorChooser.getSelectedItem();
    //
    // UnitSeparator fs=si.getEnumVal();
    // Character fieldSep=fs.value();
    //
    // List<List<String>> lines=sectionsToLines(script.getSections());
    //
    // try {
    // for(List<String> lineTks:lines){
    // String line=StringSequenceBuilder.buildString(lineTks, fieldSep);
    // osr.write(line+"\n");
    //
    // }
    // } catch (IOException e) {
    // throw e;
    // }finally{
    // if(fos!=null){
    // try {
    // fos.close();
    // } catch (IOException e) {
    // throw e;
    // }
    // }
    // }
    //
    //
    // }
    //
    // public List<String> promptItemToLine(PromptItem pi){
    // List<String> lineTokens=new ArrayList<String>();
    // for(int rdi=0;rdi<colDescriptors.length;rdi++){
    // ColumnDescriptor rd=colDescriptors[rdi];
    // JComboBox rdch=colDescriptorChoosers[rdi];
    //
    // Column col=(Column)rdch.getSelectedItem();
    // Integer colI=col.getColumn();
    // String name=rd.getName();
    // if(colI!=null){
    // if(name.equals("Code")){
    // String itemCode="";
    // if(pi instanceof Recording){
    // itemCode=((Recording)pi).getItemcode();
    // }
    // lineTokens.add(itemCode);
    // }
    //
    // if(name.equals("PromptText")){
    // List<Mediaitem> mis=pi.getMediaitems();
    // String misStr=StringSequenceBuilder.buildStringOfObjs(mis, ',');
    // lineTokens.add(misStr);
    // }
    // }
    // }
    // return lineTokens;
    // }

}
