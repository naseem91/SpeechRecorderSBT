//    Speechrecorder
//    (c) Copyright 2012
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
import ipsk.apps.speechrecorder.script.TableTextfileImporter;
import ipsk.db.speech.Section;
import ipsk.swing.JDialogPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

public class ImportScriptUIDialog extends JDialogPanel implements
		ActionListener, PropertyChangeListener {

    private TableTextfileImporter tableTextfileImporter;
	
	private Section section;

	public ImportScriptUIDialog(URL projectContext,ItemcodeGenerator itemcodeGenerator) {
//		super(JDialogPanel.OK_APPLY_CANCEL_OPTION);
	    super(JDialogPanel.Options.OK_CANCEL);
	    okButton.setText("Import as new section");
	    setFrameTitle("Import text list/table to recording script");
	    setApplyingEnabled(false);
		TableTextfileImporter.ColumnDescriptor[] rows=new TableTextfileImporter.ColumnDescriptor[]{
		        TableTextfileImporter.ITEM_CODE_DESCRIPTOR,
		        new TableTextfileImporter.ColumnDescriptor("PromptText")
		};
        tableTextfileImporter=new TableTextfileImporter(rows,itemcodeGenerator);
        tableTextfileImporter.addPropertyChangeListener(TableTextfileImporter.VALID_SCRIPT_AVAILABLE_PROPNAME,this);
        Container contentPane=getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(tableTextfileImporter,BorderLayout.CENTER);
      
		
	}
	public ImportScriptUIDialog(ItemcodeGenerator itemcodeGenerator) {
	       this(null,itemcodeGenerator);
	   }
	public ImportScriptUIDialog() {
       this(null,new ItemcodeGenerator());
    }

  

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ImportScriptUIDialog sui = new ImportScriptUIDialog(null,new ItemcodeGenerator());
	}

	

//	private void editSelectedSection() {
//		ListSelectionModel selModel = sectionsTable.getSelectionModel();
//		int sel = selModel.getMinSelectionIndex();
//
//		if (sectionUI == null) {
//			sectionUI = new SectionUI(projectContext);
//        }
//		
//		sectionUI.setSection(sectionTableModel.getSections()[sel]);
//       
//	}
    
    
        
        protected void applyValues(){
            section=tableTextfileImporter.createSection();
        }

    
        public Section getSection() {
            return section;
        }

        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent arg0) {
            String pName=arg0.getPropertyName();
            if(TableTextfileImporter.VALID_SCRIPT_AVAILABLE_PROPNAME.equals(pName)){
                Object newValue=arg0.getNewValue();
                if(newValue instanceof Boolean){
                    boolean validAvailable=(Boolean)newValue;
                    setApplyingEnabled(validAvailable);
                }
            }
        }

       


	
}
