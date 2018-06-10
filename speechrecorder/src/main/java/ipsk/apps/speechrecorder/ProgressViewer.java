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

/**
 * @author Chr. Draxler
 * @date 19.02.2004
 * @param pm
 * 
 * ProgressViewer displays the current recording script status and progress. Items that have been recorded are marked.
 * The user can select the next item to record by clicking on the item.
 */


package ipsk.apps.speechrecorder;
import ipsk.apps.speechrecorder.actions.EditScriptAction;
import ipsk.apps.speechrecorder.actions.EditScriptEvent;
import ipsk.apps.speechrecorder.actions.SetIndexAction;
import ipsk.apps.speechrecorder.session.SessionManager;
import ipsk.apps.speechrecorder.session.SessionManagerEvent;
import ipsk.apps.speechrecorder.session.SessionManagerListener;
import ipsk.apps.speechrecorder.session.SessionPositionChangedEvent;
import ipsk.db.speech.PromptItem;
import ipsk.swing.table.AutoFontCellRenderer;
import ipsk.swing.table.DisableNullValueCellRenderer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ProgressViewer extends JPanel implements TableModelListener, SessionManagerListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7930881752326662930L;
	private JLabel label;
	private final static int PREFERRED_WIDTH = 200;
	private final static int PREFERRED_HEIGHT = 600;
	private static int COLUMN_WIDTH = 50;

	private Logger logger;
	
	private JTable progressViewTable;
	private ListSelectionModel lsm;
	
	private SessionManager sessionManager;
//	private final SetIndexAction setIndexAction;
	private EditScriptAction editScriptAction;
//	private RecStatus recStat;
	private JLabel recSectionDisplay;
	
	private UIResources uiString;
	
//	private PromptItemUI pie=null;
	private boolean editEnabled;
	
	private URL projectContext;
  
	private AutoFontCellRenderer promptTextCellRenderer;

	public ProgressViewer(SessionManager rSM, SetIndexAction setIndexAction, EditScriptAction editScriptAction) {
		super();
		sessionManager = rSM;
//		this.setIndexAction=setIndexAction;
		this.editScriptAction=editScriptAction;
		logger = Logger.getLogger("ipsk.apps.speechrecorder");
		
		uiString = UIResources.getInstance();
		
		progressViewTable = new JTable(sessionManager);
		progressViewTable.setShowGrid(true);
		
		sessionManager.addTableModelListener(progressViewTable);
				
		//progressViewTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        progressViewTable.setSelectionModel(rSM);

		//lsm = progressViewTable.getSelectionModel();
		lsm=rSM;
        rSM.addSessionManagerListener(this);
		progressViewTable.setColumnSelectionAllowed(false);
		
		// Problem: disabling row selection by user
		// the line following line disables row selection completely
		// the selected line is not visible anymore
		//progressViewTable.setRowSelectionAllowed(false);
		
		MouseAdapter listMouseListener = new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	        	if (e.getClickCount() == 2) {
	        		//System.out.println("Double click on row: " + progressViewTable.getSelectedRow());
	        	    int selRow=progressViewTable.getSelectedRow();
	        		if(editEnabled){
	        		    editSelectedItem(selRow);
	        		}
//	        		int shiftPressed = e.getModifiers()&InputEvent.SHIFT_MASK; 
	        	}
	    	}
	    };
	    progressViewTable.addMouseListener(listMouseListener); 
	    setIndexAction.addPropertyChangeListener(new PropertyChangeListener(){

            public void propertyChange(PropertyChangeEvent arg0) {
                String propertyNme=arg0.getPropertyName();
                Object newVal=arg0.getNewValue();
                if("enabled".equals(propertyNme) && newVal instanceof Boolean){
                    setIndexActionEnabled((Boolean)(newVal));
                }
            }

           
	        
	    });
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				if (e.getValueIsAdjusting()) {
				    return;
				}
				
				ListSelectionModel lsm =
					(ListSelectionModel)e.getSource();
				if (lsm.isSelectionEmpty()) {
					//no rows are selected 
				} else {
				 // scroll to selected row
				    
				    // the positions of the event indexes are wrong. Why? 
				    //System.out.println("Index: "+e.getFirstIndex()+" "+e.getLastIndex()+" "+progressViewTable.getSelectedRow());
				  int i=progressViewTable.getSelectedRow();
			      Rectangle rowFirstColRect=progressViewTable.getCellRect(i, 0, true);
			      progressViewTable.scrollRectToVisible(rowFirstColRect);
				}
			}
		}); 

		TableColumn column = null;
		for (int i = 0; i < sessionManager.getColumnCount(); i++) {
			column = progressViewTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth((int) COLUMN_WIDTH / 2); // item count column is narrower
			} else if (i == 1) {
				column.setPreferredWidth(2 * COLUMN_WIDTH); // file name column is wider
			} else if (i == 2) {
				column.setPreferredWidth(5 * COLUMN_WIDTH); // prompt text column is wider
				
			} else {
				column.setPreferredWidth(COLUMN_WIDTH);
			}
		}
		
		
		
		JScrollPane jsp = new JScrollPane(progressViewTable);
		
		
		
		label= new JLabel(uiString.getString("RecProgressStatus"), JLabel.CENTER);
		//label.setMinimumSize(new Dimension(0,0));
		recSectionDisplay = new JLabel("", JLabel.CENTER);
		 // section display label should not block decreasing width by user
        recSectionDisplay.setMinimumSize(new Dimension(0,0));
		setLayout(new BorderLayout());

		add(label,BorderLayout.NORTH);
		add(jsp,BorderLayout.CENTER);
		add(recSectionDisplay,BorderLayout.SOUTH);
		
		editEnabled=false;
		
		TableColumn txtTableCol=progressViewTable.getColumnModel().getColumn(SessionManager.COL_TEXT);
		TableCellRenderer txtTcr=txtTableCol.getCellRenderer();
		if(txtTcr==null){
		    txtTcr=progressViewTable.getDefaultRenderer(progressViewTable.getColumnClass(SessionManager.COL_TEXT));
		}
		promptTextCellRenderer=new AutoFontCellRenderer(txtTcr);
        txtTableCol.setCellRenderer(promptTextCellRenderer);
        
		TableColumn recTableCol=progressViewTable.getColumnModel().getColumn(SessionManager.COL_REC);
		TableCellRenderer tcr=recTableCol.getCellRenderer();
		if(tcr==null){
		    tcr=progressViewTable.getDefaultRenderer(progressViewTable.getColumnClass(SessionManager.COL_REC));
		}
        recTableCol.setCellRenderer(new DisableNullValueCellRenderer(tcr));
	}


	/**
	 * @param useableFontFamilies
	 * @see ipsk.swing.table.AutoFontCellRenderer#setPreferredFontFamilies(java.lang.String[])
	 */
	public void setUseablePromptFontFamilies(String[] useableFontFamilies) {
		promptTextCellRenderer.setPreferredFontFamilies(useableFontFamilies);
	}


	protected void setIndexActionEnabled(Boolean enabled) {
           sessionManager.setSetIndexActionsEnabled(enabled);
    }

//
//    private void setSelectedRow(){
//	    Integer recIndex=recScriptManager.getRecIndex();
//	    if(recIndex!=null){
//	    int rows=progressViewTable.getRowCount();
//	    if(recIndex<rows){
//	    progressViewTable.setRowSelectionInterval(recIndex,recIndex);
//	    }else{
//	        progressViewTable.clearSelection();
//	    }
//	    }else{
//	        progressViewTable.clearSelection();
//	    }
//	}


	public void editSelectedItem(int i) {
		sessionManager.setRecIndex(i);
		PromptItem pi=sessionManager.getCurrentPromptItem();
		EditScriptEvent ese=new EditScriptEvent(this, pi);
		editScriptAction.actionPerformed(ese);
		// TODO use EditScripTAction
	        
////		if(pi instanceof Recording){
//		    if(pie==null){
//		        pie = new PromptItemEditor(pi,projectContext);
//		    }else{
//		        pie.setPromptItem(pi);
//		    }
//		pie.showDialog(new JFrame());
////		}
		
		// TODO open script editor with current item selected 
	}
	
//	public void setSelectedIndex(int i,boolean userReq) {
//		if (userReq && setIndexAction.isEnabled()){	
//		    setIndexAction.actionPerformed(new SetIndexEvent(this,i));
//		}
//		// the user request to select a new prompt item by selecting a row 
//		// will be reset by the following line
//		// if the index action is disabled
//		setSelectedRow();
//		// scroll to selected row
//		Rectangle rowFirstColRect=progressViewTable.getCellRect(i, 0, true);
//		progressViewTable.scrollRectToVisible(rowFirstColRect);
////		setIndexAction.actionPerformed(new SetIndexEvent(this,i));
//	}
	
	public void tableChanged(TableModelEvent te) {
		logger.info("tableChanged: " + te.toString());
	}
	
	public void setEnabled(boolean enabled){
		progressViewTable.setEnabled(enabled);
		label.setEnabled(enabled);
		
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT);
	}
	
	public void setEditEnabled(boolean editEnabled){
		this.editEnabled=editEnabled;
//		if (pie!=null)pie.setEnabled(editEnabled);
	}




    public void update(SessionManagerEvent e) {
        String sectionInfo=null;
        if(e instanceof SessionPositionChangedEvent){
            sectionInfo=sessionManager.getRecSectionInfo();
//            setSelectedRow();
        }
//        else if(e instanceof RecScriptChangedEvent){
//           sectionInfo=null;
////           setSelectedRow();
//        }
        
        String sectionInfoLabel="";
        if(sectionInfo!=null){
            sectionInfoLabel=sectionInfo;
        }
        recSectionDisplay.setText(sectionInfoLabel);
        // section display label should not block decreasing width by user
        recSectionDisplay.setMinimumSize(new Dimension(0,0));
    }
    public URL getProjectContext() {
        return projectContext;
    }
    public void setProjectContext(URL projectContext) {
        this.projectContext = projectContext;
//        if(pie!=null){
//            pie.setProjectContext(projectContext);
//        }
    }
    
    public Dimension getMinimumSize(){
        return new Dimension(0,0);
    }


	
}