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

package ipsk.apps.speechrecorder;
import ipsk.db.speech.Person.Sex;
import ipsk.db.speech.utils.EnumSexCellEditor;
import ipsk.util.SystemHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Vector;
//import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;

import com.toedter.calendar.JDateChooserCellEditor;

/**
 * SpeakerDatabaseViewer displays a speaker database in a table. From the table,
 * rows can be selected by clicking in a cell. Rows can be added or deleted by
 * selecting a row and then clicking on the "add" or "delete" buttons. The 
 * table cells can be edited if the table is set to editing mode via the edit
 * button. Finally, a speaker can be selected by double-clicking on a row or a 
 * simple click and a subsequent click on the "select" button.
 * 
 * The SpeakerDatabaseViewer maintains the index of the selected row. This index must
 * always be between 0 and the maximum number of table rows.
 * 
 * @author draxler
 *
 */

public class SpeakerDatabaseViewer extends JFrame implements TableModelListener {

	private static final long serialVersionUID = 390341902688247788L;

	private Logger logger;
	//	private Level logLevel = Level.FINE;

	private SpeakerManager spkManager = null;

	private JPanel buttonPanel = null;
	private JDialog dbViewer = null;
	private JTable dbTable = null;
	private ListSelectionModel lsm;
//	private int selectedIndex = 0;

	private JButton addButton;
	private JButton deleteButton;
	private JToggleButton editButton;
	private JButton selectButton;
	private UIResources uiString = null;

	/**
	 * SpeakerDatabaseViewer takes as input a speaker manager that acts as a model
	 * for the graphical table.
	 * 
	 * @param sm SpeakerManager model for the table
	 */
	SpeakerDatabaseViewer(SpeakerManager sm) {
		super();
		spkManager = sm;

		logger = Logger.getLogger("ipsk.apps.speechrecorder");
		//logger.setLevel(logLevel);

		uiString = UIResources.getInstance();

		Vector description = Speaker.getDescription();
		Vector tableHeader = new Vector();
		for (int i = 0; i < description.size(); i++) {
			tableHeader.addElement(description.elementAt(i));
		}

		dbTable = new JTable(spkManager);

		dbTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = dbTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				//if (e.getValueIsAdjusting()) return;
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					//no rows are selected
					//logger.info("nothing selected");
				} else {
					//logger.info("Selected(valueChanged): " + lsm.getMinSelectionIndex());
					setIndex(lsm.getMinSelectionIndex());
					int r=dbTable.getSelectedRow();
                    Rectangle rowFirstColRect=dbTable.getCellRect(r, 0, true);
                    dbTable.scrollRectToVisible(rowFirstColRect);
				}
			}
		});
		dbTable.setDefaultEditor(Sex.class, new EnumSexCellEditor());
		dbTable.setDefaultEditor(Date.class, new JDateChooserCellEditor());
		
		dbTable.setShowGrid(true);
		dbTable.setPreferredScrollableViewportSize(new Dimension(500, 300));
		int rowIndex = spkManager.getIndex();
		
		JScrollPane scrollPane = new JScrollPane(dbTable);

		makeButtonPanel();

		dbViewer = new JDialog(this, uiString.getString("SpeakerManagerFrameTitle"), true);
		//dbViewer.setResizable(false);
		dbViewer.setBackground(Color.white);

		dbViewer.getContentPane().setLayout(new BorderLayout());
		dbViewer.getContentPane().add(scrollPane,BorderLayout.CENTER);
		dbViewer.getContentPane().add(buttonPanel,BorderLayout.SOUTH);
		if (rowIndex >= 0){
            dbTable.setRowSelectionInterval(rowIndex, rowIndex);
		}
		updateUIDependencies();
	}
	

	public void displayViewer() {
		Runnable doShow = new Runnable() {
			public void run() {
			    // per default not editable
			    setSpeakerDataEditable(false);
			    updateSelection();
			    updateUIDependencies();
				dbViewer.pack();
				dbViewer.setVisible(true);
				dbViewer.toFront();
				
			}
		};
		if (java.awt.EventQueue.isDispatchThread()) {
			doShow.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(doShow);
			} catch (InterruptedException e) {
				// cannot open another window
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	//	/**
	//	 * shows the speaker table and allows the user to
	//	 * select or enter a new speaker.
	//	 */
	//	public void setSpeaker() {
	//		displayViewer();
	//	}
	//	
	
	
	public void updateSelection(){
	    int currIndex=getIndex();
	    if(currIndex>=0){
	        setSelectedIndex(currIndex);
	    }
	}
	
	private void setSelectedIndex(int index){
	    ListSelectionModel lsm=dbTable.getSelectionModel();
	    if(lsm!=null){
	        lsm.setSelectionInterval(index, index);
	    }
	}

	/**
	 * sets the index of the speaker list to the currently selected
	 * table row
	 */
	public void setIndex(int index) {
		spkManager.setIndex(index);
		int newIndex=getIndex();
		setSelectedIndex(newIndex);
	}

	/**
	 * returns the index of the currently selected speaker row
	 * @return int index
	 */
	public int getIndex() {
		return spkManager.getIndex();
	}

	/**
	 * an auxiliary method that creates four buttons and arranges them
	 * in a grid layout. These buttons serve as controllers to the
	 * data model of the table. 
	 */
	private void makeButtonPanel() {

		addButton = new JButton(uiString.getString("AddButtonText"));
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//dbTableModel.addRow();
				//setIndex(dbTableModel.getRowCount());
				addSpeaker();
			}
		});
		addButton.setEnabled(true);

		deleteButton = new JButton(uiString.getString("DeleteButtonText"));
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSpeaker();
			}
		});
		deleteButton.setEnabled(true);

		editButton = new JToggleButton(uiString.getString("EditButtonText"));
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleEditSpeaker();
			}
		});
		editButton.setEnabled(true);

		selectButton = new JButton(uiString.getString("SelectButtonText"));
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    // Users loose speaker data if an active cell editor
			    // is not stopped before disposing
			    TableCellEditor cellEditor=dbTable.getCellEditor();
			    if(cellEditor!=null){
			        cellEditor.stopCellEditing();
			    }
			    SystemHelper.disposeWindowForReuse(dbViewer);
			}
		});

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 4, 10, 5));

		buttonPanel.add(addButton);
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(selectButton);

		getRootPane().setDefaultButton(selectButton);

		buttonPanel.setBackground(Color.white);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	
	private void stopCellEditing(){
        TableCellEditor cellEditor=dbTable.getCellEditor();
        if(cellEditor!=null){
            cellEditor.stopCellEditing();
        }
    }
	
	/**
	 * adds a new speaker to the list of speakers; once a speaker 
	 * has been added, the edit, delete and select buttons can
	 * be enabled.
	 *
	 */
	public void addSpeaker() {
		spkManager.addNewSpeaker();
//		editButton.setEnabled(true);
		setSpeakerDataEditable(true);
//		selectButton.setEnabled(true);
//		deleteButton.setEnabled(true);
		dbTable.setRowSelectionInterval(spkManager.getIndex(), spkManager.getIndex());
		updateUIDependencies();
	}

	/**
	 * deletes the speaker with the given index from the list of speakers. If
	 * there are no more speakers in the database, the edit, delete and select
	 * buttons are disabled
	 *
	 */
	public void deleteSpeaker() {
	    String msg=uiString.getString("delete.row.selected.confirm.request");
	    String msgTitle=uiString.getString("delete.speaker.data");
	    int ans=JOptionPane.showConfirmDialog(dbViewer, msg, msgTitle, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	    if(ans==JOptionPane.YES_OPTION){
	        spkManager.deleteSpeaker(getIndex());
	        if (spkManager.getSpeakerCount() > 0) {
	            int ind=getIndex();
	            if(ind>=0){
	                dbTable.setRowSelectionInterval(ind,ind);
	            }else{
	                dbTable.getSelectionModel().clearSelection();
	            }
	        }
	        //		else {
	        //			// no more speakers in the database, so disable the
	        //			// edit, delete and select buttons
	        //			editButton.setEnabled(false);
	        //			selectButton.setEnabled(false);
	        //			deleteButton.setEnabled(false);
	        //		}
	    }
	    updateUIDependencies();
	}

	private void setSpeakerDataEditable(boolean editable){
	    if(!editable){
	        stopCellEditing();
	    }
	    spkManager.editSpeaker(editable);
	    editButton.setSelected(editable);
	}
	
	/**
	 * enables/disables the editing mode for the speaker database. All cells can be
	 * edited.
	 *
	 */
	public void toggleEditSpeaker() {
		setSpeakerDataEditable(!spkManager.isEditable());
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
	 */
	
	
	public void tableChanged(TableModelEvent te) {
		logger.info("tableChanged: " + te.toString());
	}
	
	
	private void updateUIDependencies(){
	    boolean hasData=(spkManager.getSpeakerCount() > 0);
	    boolean spkSelected=false;
	    if(hasData){
	        ListSelectionModel lsm=dbTable.getSelectionModel();
	        spkSelected=(lsm!=null && !lsm.isSelectionEmpty());
	    }
	    boolean editable=spkManager.isEditable() && hasData;
	    editButton.setSelected(editable);
	    editButton.setEnabled(hasData);
	    deleteButton.setEnabled(spkSelected);
	    selectButton.setEnabled(spkSelected);
	}
}
