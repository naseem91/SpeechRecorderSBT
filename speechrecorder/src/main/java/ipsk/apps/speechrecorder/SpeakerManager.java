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

//
//  SpeakerManager.java
//  JSpeechRecorder
//
//  Created by Christoph Draxler on Fri Dec 06 2002.
//

package ipsk.apps.speechrecorder;
import ipsk.apps.speechrecorder.SpeakerDatabaseLoader.DatabaseType;
import ipsk.db.speech.Person;
import ipsk.db.speech.Person.Sex;
import ipsk.db.speech.Session;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.net.URL;

import javax.swing.table.AbstractTableModel;

public class SpeakerManager extends AbstractTableModel {

	public static int COL_ID=0;
	public static int COL_CODE=1;
	public static int COL_NAME=2;
	public static int COL_FORENAME=3;
	public static int COL_GENDER=4;
	public static int COL_ACCENT=5;
	public static int COL_BIRTHDATE=6;
	
	private SpeakerDatabaseLoader spkDBLoader;
	private ipsk.apps.speechrecorder.db.Speaker spk;
//	private Vector tableRows;
	private List<ipsk.apps.speechrecorder.db.Speaker> tableRows;
	private Vector tableColumns = null;
	private int index;
	private boolean isEditable;
	private int maxID;
	private boolean databaseSaved;
	private Format sessionPersonIDFormat=new DecimalFormat("0000");
	
	
	/**
	 * @return the sessionPersonIDFormat
	 */
	public Format getSessionPersonIDFormat() {
		return sessionPersonIDFormat;
	}

	/**
	 * @param sessionPersonIDFormat the sessionPersonIDFormat to set
	 */
	public void setSessionPersonIDFormat(Format sessionPersonIDFormat) {
		this.sessionPersonIDFormat = sessionPersonIDFormat;
	}

	/**
	 * SpeakerManager creates an empty table to hold the speaker database.
	 * The table header is created from the speaker class description.
	 *
	 */
	public SpeakerManager() {
        super();
        tableRows=new ArrayList<ipsk.apps.speechrecorder.db.Speaker>();
		init();
	}
	
	/**
	 * SpeakerManager() retrieves from an external source the current speaker
	 * database. This speaker database is then used as a model for the graphical
	 * speaker database interface, a table.
	 * 
	 * @param url
	 */
	
	public SpeakerManager(URL url) {
		this();
		loadURL(url);
	}

	
	private void init(){
		spk = null;
		if(tableRows!=null){
		    tableRows.clear();
		}else{
		    // should not happen
		    tableRows=new ArrayList<ipsk.apps.speechrecorder.db.Speaker>();
		}
		tableColumns = Speaker.getDescription();
		index = -1;
		isEditable = false;
		databaseSaved=true;
	}

	/**
	 * loads the speaker database from a URL. The speaker database is written
	 * to the table, and the last entry of the table is selected.
	 * 
	 * @param url
	 */
	public void loadURL(URL url) {
		spk = new ipsk.apps.speechrecorder.db.Speaker(0);
		tableColumns = Speaker.getDescription();
		spkDBLoader = new SpeakerDatabaseLoader(url, tableColumns);
		
		tableRows = spkDBLoader.getDatabase();
		maxID = spkDBLoader.getMaxID();
		index = tableRows.size() - 1;
		databaseSaved=true;
		fireTableDataChanged();
	}


//	/**
//	 * returns true if the current speaker code is unique, false otherwise
//	 * @return boolean
//	 */
//	public boolean isCodeUnique(String code) {
//		boolean isUnique = false;
//		for (int i = 0; i < tableRows.size(); i++) {
//			String currCode = (String) tableRows.elementAt(i);
//			if(currCode.equals(code)) {
//				isUnique = true;
//				break;
//			}
//		}
//		return isUnique;
//	}


	/**
	 * returns the number of entries in the speaker database
	 * @return int number of speakers
	 */
	public int getSpeakerCount() {
		return tableRows.size();
	}
	
	
	/**
	 * setIndex() sets the index of the currently selected speaker
	 * item to the given index. The index must be a valid value,
	 * i.e. between 0 and lower than the number of speaker items in the
	 * database.
	 * 
	 * @param i selected speaker item
	 */
	public void setIndex(int i) {
		if (i >= -1 && i < getSpeakerCount()) {
			index = i;
		} else {
			index = 0;
		}
	}
	
	/**
	 * getIndex() returns the index of the currently selected speaker
	 * item
	 * 
	 * @return int currently selected item
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * getSpeaker() returns the speaker object at the given index
	 * position within the speaker list
	 * 
	 * @return Speaker speaker object
	 */
	public ipsk.apps.speechrecorder.db.Speaker getSpeaker() {
		if (index<0 || index>=tableRows.size()) return null;
		return tableRows.get(getIndex());
	}

	/**
	 * computes a new speaker ID as the number
	 * following the highest current speaker ID.
	 * This cannot be computed in the speaker class because
	 * the new ID must be guaranteed to be unique within the 
	 * set of currently loaded speaker IDs.
	 * 
	 *	@return int speaker ID
	 */
	public int getNewSpeakerID() {
		maxID++;
		return maxID;
	}
	
	/**
	 * extends the Vector of speakers by adding
	 * a new speaker at the end. 
	 *
//	 */
	public void addNewSpeaker() {
	    int spkAndSessId=getNewSpeakerID();
	    ipsk.db.speech.Session sess=new Session(spkAndSessId);
	    ipsk.apps.speechrecorder.db.Speaker spk = new ipsk.apps.speechrecorder.db.Speaker(spkAndSessId);
	    spk.setUuid(UUID.randomUUID().toString());
	    spk.getSessions().add(sess);
		//initialize speaker values
//		spk.setSpeaker("","","","","","");
		tableRows.add(spk);
		setIndex(getSpeakerCount() - 1);
		fireTableRowsInserted(getIndex(), getIndex());
		editSpeaker(true);
		databaseSaved=false;
	}

	/**
	 * removes the item at the given index position
	 * from the vector of speaker items
	 * 
	 * @param delIndex index to delete
	 */
	public void deleteSpeaker(int delIndex) {
		if ((delIndex < tableRows.size()) && (delIndex >= 0)) {
			tableRows.remove(delIndex);
			fireTableRowsDeleted(delIndex, delIndex);
			if (delIndex > 0) {
				setIndex(delIndex - 1);
			}
			databaseSaved=false;
		}
	}
	
	
	/**
	 * sets the isEditable switch to true or false
	 */
	public void editSpeaker(boolean allowEditing) {
		isEditable = allowEditing;
	}


	/**
	 * returns true if the table cells can be edited
	 * @return boolean
	 */
	public boolean isEditable() {
		return isEditable;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public String getColumnName(int c) {
		return (String) tableColumns.elementAt(c);
	}
	
	public int getRowCount() {
		return tableRows.size();
	};

	public int getColumnCount() {
		return tableColumns.size();
	};

	public Object getValueAt(int row, int column) {
	    boolean isSpeechDB=(SpeakerDatabaseLoader.DatabaseType.SPEECH_DB.equals(getDatabaseType()));
	    ipsk.apps.speechrecorder.db.Speaker spk = tableRows.get(row);
		Object val=null;
		if(spk!=null){
			if (column == COL_ID) {
				int personId=spk.getPersonId();
				if(sessionPersonIDFormat==null){
					val=personId;
				}else{
					val=sessionPersonIDFormat.format(personId);
				}
			} else if (column == COL_CODE) {
				val= spk.getCode();
			} else if (column == COL_NAME) {
				val=spk.getName();
			} else if (column == COL_FORENAME) {
				val=spk.getForename();
			} else if (column == COL_GENDER) {
				if(isSpeechDB){
					val=spk.getSex();
				}else{
					val=spk.getGender();
				}
			} else if (column == COL_ACCENT) {
				val=spk.getAccent();
			} else if (column == COL_BIRTHDATE) {
				if(isSpeechDB){
					val=spk.getDateOfBirth();
				}else{
					val=spk.getDateOfBirthString();
				}
			} 
		}
		//		return spk.getSpeakerData(column);
//		if(val==null){
//			return "";
//		}else{
//			return val;
//		}
		return val;
		
	};		

	public void setValueAt(Object value, int row, int column) {
	    boolean isSpeechDB=(SpeakerDatabaseLoader.DatabaseType.SPEECH_DB.equals(getDatabaseType()));
	    ipsk.apps.speechrecorder.db.Speaker spk = tableRows.get(row);
//		spk.setSpeakerData(column, value);
		String strVal=null;
		if(value instanceof String){
			strVal=(String)value;
		}
		if (column == COL_CODE) {
			spk.setCode(strVal);
		} else if (column == COL_NAME) {
			spk.setName(strVal);
		} else if (column == COL_FORENAME) {
			spk.setForename(strVal);
		} else if (column == COL_GENDER) {
		    if(isSpeechDB){
		        spk.setSex((Sex)value);
		    }else{
		        spk.setGender(strVal);
		    }
		} else if (column == COL_ACCENT) {
			spk.setAccent(strVal);
		} else if (column == COL_BIRTHDATE) {
		    if(isSpeechDB){
		        if(value instanceof Date){
		            spk.setDateOfBirth((Date)value);
		        }else{
		            // Hmm error
		        }
		    }else{
		        spk.setDateOfBirthString(strVal);
		    }
		} 
		databaseSaved=false;
	};		


	public Class<?> getColumnClass(int column) {

	    if(SpeakerDatabaseLoader.DatabaseType.SPEECH_DB.equals(getDatabaseType())){
	        Class<?> cl=String.class;
	        if (column == COL_CODE) {

	        } else if (column == COL_NAME) {
	        } else if (column == COL_FORENAME) {
	        } else if (column == COL_GENDER) {
	            cl=ipsk.db.speech.Person.Sex.class;
	        } else if (column == COL_ACCENT) {

	        } else if (column == COL_BIRTHDATE) {
	            cl=Date.class;
	        }

	        return cl;
	    }else{
	        Object columnObject=getValueAt(0,column);
	        if (columnObject==null){
	            return (String.class);
	        }
	        return columnObject.getClass();
	    }
	}
	
	/**
	 * returns true if editing has been switched on, false 
	 * otherwise.
	 * TODO: cells other than the speaker code can be edited only if 
	 * a unique speaker code has been entered.
	 */
	public boolean isCellEditable(int row, int col) {
//		String spkCode = (String) getValueAt(row, 0);
//		if (col > 0 && spkCode.equals("")) {
//			return false;
//		} else {
			return (col>0 && isEditable);
//		}
	}
	
	
	public void updateRow() {
		int index = getColumnCount();
		//System.out.println("update row in table model: " + index);
	}
	
	public SpeakerDatabaseLoader getDatabaseLoader(){
		return spkDBLoader;
	}
	
	/**
	 * returns whether the speaker database needs to be saved, e.g. after
	 * modification of speaker entries. 
	 * @return true if the database was not modified since last save or open
	 */
	public boolean isDatabaseSaved() {
		return databaseSaved;
	}
	
	/**
	 * Marks database as saved/unsaved.
	 * @param databaseSaved 
	 */
	public void setDatabaseSaved(boolean databaseSaved) {
		this.databaseSaved=databaseSaved;
	}
	
	/**
	 * Closes (reinitializes) the database. 
	 */
	public void close(){
		init();
	}

    public DatabaseType getDatabaseType() {
        return spkDBLoader.getDatabaseType();
    }


	
}
