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

import ipsk.db.speech.Script;
import ipsk.swing.JDialogPanel;
import ipsk.swing.text.TableTextfileExporter;
import ipsk.text.TableTextFormats;
import ipsk.util.SystemHelper;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JOptionPane;

public class ExportScriptUIDialog extends JDialogPanel implements
		ActionListener, PropertyChangeListener{

    private TableTextfileExporter<Script.ScriptTableSchemaProvider,Script> tableTextfileExporter;
	

	public ExportScriptUIDialog() {
//		super(JDialogPanel.OK_APPLY_CANCEL_OPTION);
	    super(JDialogPanel.Options.OK_CANCEL);
	    okButton.setText("Export");
	    setFrameTitle("Export recording script to text list/table");
	    setApplyingEnabled(false);
        tableTextfileExporter=new TableTextfileExporter<Script.ScriptTableSchemaProvider,Script>(Script.scriptTableSchemaProvider);
        TableTextFormats.Profile defaultProfile;
        if(SystemHelper.getInstance().isWindows()){
            defaultProfile=TableTextFormats.Profile.TAB_SEP_WIN;
        }else{
            defaultProfile=TableTextFormats.Profile.TAB_SEP_UNIX;
        }
        tableTextfileExporter.setSelectedProfile(defaultProfile);
        add(tableTextfileExporter);
        tableTextfileExporter.addPropertyChangeListener(TableTextfileExporter.EXPORT_POSSIBLE_PROPNAME, this);
	}

   public void setScript(Script script){
       tableTextfileExporter.setData(script);
   }

        protected void applyValues(){
            // TODO exception must be visible to user
            try {
                tableTextfileExporter.writeFile();
            } catch (IOException e) {
               
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error writing to text file: "+e, "Text table export error", JOptionPane.ERROR_MESSAGE);
            }
        }



        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent arg0) {
            String pName=arg0.getPropertyName();
            if(TableTextfileExporter.EXPORT_POSSIBLE_PROPNAME.equals(pName)){
                Object newValue=arg0.getNewValue();
                if(newValue instanceof Boolean){
                    boolean exportPossible=(Boolean)newValue;
                    setApplyingEnabled(exportPossible);
                }
            }
        }

   
	
}
