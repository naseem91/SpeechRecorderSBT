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

package ipsk.apps.speechrecorder.session;

import ipsk.apps.speechrecorder.RecLogger;
import ipsk.apps.speechrecorder.SpeechRecorder;
import ipsk.apps.speechrecorder.UIResources;
import ipsk.apps.speechrecorder.storage.StorageManager;
import ipsk.apps.speechrecorder.storage.StorageManagerException;
import ipsk.db.speech.Mediaitem;
import ipsk.db.speech.Nonrecording;
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.Recording;
import ipsk.db.speech.Script;
import ipsk.db.speech.Section;
import ipsk.io.StreamCopy;
import ipsk.net.URLContext;
import ipsk.xml.DOMConverter;
import ipsk.xml.DOMConverterException;

import java.applet.Applet;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 * RecScriptManager loads the recording script and manages the progress through
 * the recording. It keeps track of the items recorded within the current
 * sessionid, and it knows which item to record next.
 * 
 * RecScriptManager is the model for the ProgressViewer. ProgressViewer displays
 * the recording script in a table. The column names are defined in the resource
 * file of the GUI. Each table row represents a recording item.
 * 
 * @author Christoph Draxler
 * @version 2.0 Feb. 2004
 * 
 *  
 */

public class SessionManager extends AbstractTableModel implements ListSelectionModel{
    
    public final static String REC_SCRIPT_DTD_1 = "SpeechRecPrompts.dtd";
    public final static String REC_SCRIPT_DTD_2 = "SpeechRecPrompts_2.dtd";
//    public final static String REC_SCRIPT_DTD = REC_SCRIPT_DTD_2;
//    public final static String REC_SCRIPT_DTD_3 = "SpeechRecPrompts_3.dtd";
    public final static String REC_SCRIPT_DTD = REC_SCRIPT_DTD_2;

	public static final int AUTOMATIC = 0;
	public static final int MANUAL = 1;
	public static final int SEQUENTIAL = 2;
	public static final int RANDOM = 3;
	
	public static final int COL_ITEM = 0;
	public static final int COL_URL = 1;
	public static final int COL_TEXT = 2;
	public static final int COL_REC = 3;
	public static final int COL_UPLOAD = 4;
	public static final int COLUMNS = 4;

	// arbitrary identifier required to set a custom cell renderer
	public static final String RECORDED_COL_ID="progress.table.col.recorded";
	
	public static final int ERROR_MSG_MAX_ITEMS=20;
	
	private String [] tableHead;
	private Logger logger;

	private UIResources uiString = null;
	
	private Script script;
	private boolean scriptSaved=true;
	
	//private Vector recSections;
	
	private Integer recIndex;
	//private int maxIndex;
	private int [] recCounter;
	private boolean [] recProcessed;
	//private String scriptid;

	//recResources stores all objects pre-fetched from URLs
	private URL context=null;
	private ResourceLoader resourceLoader;
	private Hashtable promptResources;
	private String systemIdBase = null;
	private StorageManager storageManager;
	private static SessionManager _instance = null;

	//private MetaData metadata;
	
    private DefaultListSelectionModel selModel;
    
    private boolean defaultSpeakerDisplay;
	private Section.Mode defaultMode;
	private int defaultPreDelay;
	private int defaultPostDelay;
	private boolean defaultAutomaticPromptPlay=true;
	private boolean setIndexActionsEnabled=false;
	private String systemId=REC_SCRIPT_DTD;
    
    private boolean progresToNextUnrecorded=false;
   
    private Vector<SessionManagerListener> listeners=new Vector<SessionManagerListener>();
	
	/**
	 * RecScriptManager loads the recording script and organizes the sequence of
	 * recordings. A recording script can be either a text file (the use of text
	 * files is deprecated) or an XML file defined in a DTD or XML-Schema.
	 * Furthermore, RecScriptManager pre-fetches all resources referred to via
	 * URLs so that they can be displayed in the prompt window without delay.
	 * 
	 * The sequence of recordings can be either automatic mode in sequence or in
	 * random order, or it can be manual. For selecting prompts in manual mode
	 * the RecTransporter or the ProgressViewer are used.
	 * 
	 * RecScriptManager is implemented as a singleton because there can be only
	 * a single such manager for a given recording sessionid.
	 *  
	 */

	private SessionManager() {
		super();
		logger = Logger.getLogger("ipsk.apps.speechrecorder");

	
		
		// get description of table columns from the GUI property file
		uiString = UIResources.getInstance();
		tableHead = new String[COLUMNS];
		tableHead[COL_ITEM] = uiString.getString("ItemNo");
		tableHead[COL_URL] = uiString.getString("ItemFile");
		tableHead[COL_TEXT] = uiString.getString("ItemPrompt");
		tableHead[COL_REC] = uiString.getString("ItemRecStatus");
		
		// TODO Not implemented yet !
		//tableHead[COL_UPLOAD] = uiString.getString("ItemRecSaved");
        
        selModel=new DefaultListSelectionModel();
        selModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      
        
        initialize();
		//recStatus = RecStatus.getInstance();
	}

	/**
	 * getInstance() returns the singleton RecScriptManager object.
	 * 
	 * @return instance
	 */

	public static SessionManager getInstance() {
		if (_instance == null) {
			_instance = new SessionManager();
		}
		return _instance;
	}

	/**
	 * initializes the RecScriptManager, i.e. set the current recording index
	 * and the maximum index to 0 and creates a new recording sections Vector 
	 */
	public void initialize() {
		recIndex = null;
		//maxIndex = 0;
		//recSections = new Vector();
		selModel.clearSelection();
	}
	

	
	/**
	 * Get currently used script.
	 * @return script
	 */
	public Script getScript() {
		return script;
	}
	private void applyDefaults(){
		if(script!=null){
			List<Section> recSections=script.getSections();
			if(recSections!=null){
				for(Section rs:recSections){
					rs.setDefaultMode(defaultMode);
					rs.setDefaultSpeakerDisplay(defaultSpeakerDisplay);
				}
			}
		}
	}
	
	/**
	 * Set the script to use.
	 * @param script
	 * @throws StorageManagerException 
	 */
	public void setScript(Script script){
		this.script = script;
		
//		if (this.script!=null)this.script.addPropertyChangeListener(this);
		initialize();
        resetItemMarkers();
        try {
			updateItemMarkers();
		} catch (SessionManagerException e) {
			e.printStackTrace();
		}
		
//        fireRecscriptManagerUpdate(new RecScriptChangedEvent(this));
	}
	
	
    public boolean needsSilenceDetector(){
        if(script!=null){
            return script.needsSilenceDetector();
        }
        return false;
    }
    
    public boolean needsBeep(){
        if(script!=null){
            return script.needsBeep();
        }
        return false;
    }

	/**
	 * checks which items have been recorded already and sets their
	 * recording counter and processing status accordingly.
	 * @throws StorageManagerException 
	 *
	 */
	public void updateItemMarkers () throws SessionManagerException{
		try {
			checkRecordedItems();
			for (int i = 0; i < getMaxIndex(); i++) {
				if (recCounter[i] > 0) {
					recProcessed[i] = true;
				} else {
					recProcessed[i] = false;
				}
			}
		} catch (SessionManagerException e) {
			e.printStackTrace();
			throw new SessionManagerException(e);
		}finally{
			fireTableDataChanged();
		}
		
		
	}

	/**
	 * resets the recording index to 0 and marks all items as
	 * not recorded and not processed.
	 */
	public void resetItemMarkers() {
//		recIndex = null;
		int maxIndex=getMaxIndex();
		recCounter = new int[maxIndex];
		recProcessed = new boolean[maxIndex];
	}

	public void setStorageManager(StorageManager sm) {
		storageManager = sm;
	}

	/**
	 * returns the prompt resources that were
	 * pre-fetched via URLs.
	 * 
	 * @return Hashtable resources pre-fetched from URLs
	 */
	public Hashtable getRecScriptResources() {
		return promptResources;
	}

	/**
	 * isResourceLoaded() returns true if a resource identified by a given URL
	 * has been loaded into the system
	 * 
	 * @param resourceURL
	 * @return boolean true if a resource has been loaded
	 */
	public boolean isResourceLoaded(URL resourceURL) {
		return (getRecScriptResources().get(resourceURL) != null);
	}



	/**
	 * returns the recording section corresponding to a given
	 * recording item index
	 * 
	 * @param itemIndex recording index in the range of 0 and (number of items - 1)
	 * @return RecSection
	 */
	public Section getRecSectionForItem(int itemIndex) {
		int index = itemIndex;
		Section recSection = null;
        if (script != null) {
            List<Section> sections = script.getSections();
            if (sections != null) {
                for (int i = 0; i < sections.size(); i++) {
                    Section tmpRecSection = sections.get(i);

                    if (index >= tmpRecSection.getPromptItems().size()) {
                        index = index - tmpRecSection.getPromptItems().size();
                    } else {
                        recSection = tmpRecSection;
                        break;
                    }
                }
            }
        }
		return recSection;
	}
	
	/**
	 * returns the recording section corresponding to the given
	 * recording item index
	 * 
	 * @return Section
	 */
	public Section getCurrentRecSection() {
	    Integer recIndex=getRecIndex();
	    if(recIndex==null){
	        return null;
	    }else{
	        return getRecSectionForItem(getRecIndex());
	    }
	}
	
	public Section.PromptPhase currentPromptPhase(){
		Section cs=getCurrentRecSection();
		if(cs!=null){
			return cs.getNNPromptphase();
		}
		return null;
	}
	
	/**
	 * returns the information string of the current recording
	 * section.
	 * 
	 * @return String
	 */
	public String getRecSectionInfo() {
        Section section=getCurrentRecSection();
        if(section!=null){
            return section.getInfo();
        }else{
		return null;
        }
	}
	
	/**
	 * returns the prompt item corresponding to the given recording index
	 * @param promptIndex
	 * @return PromptItem
	 */
	public ipsk.db.speech.PromptItem getPromptItem(int promptIndex) {
        ipsk.db.speech.PromptItem promptItem = null;
		int index = promptIndex;
		if(script!=null){
		List<Section> sections=script.getSections();
		if(sections!=null){
		for (int i = 0; i < sections.size(); i++) {
			
		    Section s=sections.get(i);
		    List<PromptItem> pis=s.getShuffledPromptItems();
		    int pisSize=pis.size();
			if (index >= pisSize) {
				index = index - pisSize;
			} else {
				promptItem = pis.get(index);
				break;
			}
		}
		}
		}
		return promptItem;
	}
	
	/**
	 * returns the prompt item for the current recording item index
	 * @return PromptItem
	 */
	public ipsk.db.speech.PromptItem getCurrentPromptItem() {
	    Integer recIndex=getRecIndex();
	    if(recIndex==null){
	        return null;
	    }else{
	        return getPromptItem(recIndex);
	    }
	}
	
	public boolean currentPromptBlocking(){
		ipsk.db.speech.PromptItem pi=getCurrentPromptItem();
		if(pi instanceof Recording){
			return ((Recording)pi).getNNBlocked();
		}
		return false;
	}
	
	/**
	 * checks whether all items have been recorded or not
	 * @throws StorageManagerException 
	 */
	private void checkRecordedItems() throws SessionManagerException {
		InputStream testInputStream = null;
		for (int i = 0; i < getMaxIndex(); i++) {
            ipsk.db.speech.PromptItem pi=getPromptItem(i);
            if (pi instanceof Recording){
			String itemCode = ((Recording)pi).getItemcode();
			if (itemCode != null) {
//				if (storageManager.isRecorded(itemCode)) {
//					recCounter[i] = 1;
//				}
              
                try {
					recCounter[i] = storageManager.getRecordedVersions(itemCode);
				} catch (StorageManagerException e) {
					throw new SessionManagerException(e);
				}
                
			}
            }
		}
	}


	/**
	 * returns the number of recordings for the prompt item with
	 * a given index
	 * 
	 * @param index
	 * @return the number of times the item was recorded
	 */
	public int getRecCounter(int index) {
	    
		if (index >= recCounter.length) return 0;
		return recCounter[index];
	}

	/**
	 * increments the recording counter for a given item
	 * 
	 * Discussion: should the fact that the count of recordings of this item has
	 * increased be reported to the ProgressViewer? How?
	 * 
	 * @param index
	 */
	public void incrementRecCounter(int index) {
		recCounter[index]++;
		//notify listeners that data has changed
		fireTableCellUpdated(index, COL_REC);
	}

	/**
	 * sets the processing flag of the current recording
	 * item to true.
	 * 
	 * @param index
	 */
	public void markItemAsProcessed(int index) {
		recProcessed[index] = true;
		fireTableCellUpdated(index, COL_UPLOAD);
	}

	/**
	 * adds the current prompt item data (LBR, CCD) to the recording logger
	 */
	public void setLogEntries() {
		RecLogger recLog = RecLogger.getInstance();
		PromptItem pi = (PromptItem) getPromptItem(recIndex);
		if (pi instanceof Recording){
			Recording r=(Recording)pi;
		recLog.setLogEntry("LBR: ", r.getMediaitems().get(0).getText());
		recLog.setLogEntry("CCD: ", r.getItemcode());
		}
	}

	/**
	 * decrements the current recording index by 1 if the
	 * original index was greater than 0; otherwise the index is set to the
	 * maximum recording index.
	 */
	public void decrementIndex(){
		if (recIndex > 0) {
			setRecIndex(recIndex-1);
		} else {
			setRecIndex(getMaxIndex() - 1);
		}
	}

	/**
	 * increments the current recording index by 1 if the
	 * original index was less than the maximum recording index; otherwise the
	 * index is set to 0.
	 */
	public void incrementIndex(){
		if (recIndex == getMaxIndex() - 1) {
			setRecIndex(0);
		} else {
			setRecIndex(recIndex+1);
		}
       
	}

	/**
	 * returns the index of the current recording.
	 * 
	 * @return index of current recording
	 */
	public Integer getRecIndex() {
		return recIndex;
	}

	/**
	 * sets the recording index to a given value.
	 *  
	 */
	public void setRecIndex(Integer index){

	    if (index!=null && (index <0 || index >= getMaxIndex())) {
	        throw new IllegalArgumentException();
	    }
	    boolean changed;
	    if(recIndex==null){
	        changed=(index!=null);
	    }else{
	        if(index==null){
	            changed=true;
	        }else{
	            changed=(index.intValue() != recIndex.intValue());
	        }
	    }
	    recIndex=index;
	    if(changed){
	        if(recIndex!=null){
	            selModel.setSelectionInterval(recIndex, recIndex);
	        }else{
	            selModel.clearSelection();
	        }
	        fireSessionManagerUpdate(new SessionPositionChangedEvent(this,recIndex));
	    }
	    //        if(recIndex==null|| index != recIndex.intValue()){
	    //		recIndex = new Integer(index);
	    //        selModel.setSelectionInterval(recIndex, recIndex);
	    //        fireRecscriptManagerUpdate(new RecScriptPositionChangedEvent(this));
	    //        }
	}

	/**
	 * returns the number of items in the recording script. If the
	 * index has been computed once it is not recomputed for this script.
	 * 
	 * @return int prompt item count
	 */
	// TODO method name is misleading. returns number of prompt items 
	public int getMaxIndex() {
		//return promptList.size();
		if (script==null)return 0;
//		if (maxIndex > 0) {
//			return maxIndex;
//		} else {
			int index = 0;
			if(script!=null && script.getSections() != null){
				for (Section s:script.getSections()) {
					index = index + s.getPromptItems().size();
				}
			}
			//maxIndex = index;
			return index;
		//}
	}

	/**
	 * advanceToNextRecording() selects the next non-recorded recording item. 
	 * If no free item can be found a dialog is displayed to inform the speaker 
	 * that the last item has been reached.
	 */
	public void advanceToNextRecording(){
		int tmpIndex = getRecIndex();
		int maxIndex = getMaxIndex();

		boolean itemAlreadyRecorded = true;
		while (itemAlreadyRecorded && tmpIndex < maxIndex) {
            ipsk.db.speech.PromptItem pi = getPromptItem(tmpIndex);
            if (pi instanceof Recording) {
                logger.info("recCounter[" + tmpIndex + "] = "
                        + recCounter[tmpIndex]);
                if (recCounter[tmpIndex] == 0) {
                    itemAlreadyRecorded = false;
                    break;
                }
            }

            tmpIndex++;
            if (tmpIndex == maxIndex) {
                // ask user whether searching for an unrecorded item
                // should start at the first item of the list and search up to
                // the current index
                int response = JOptionPane
                        .showConfirmDialog(null,
                                "No unrecorded items found. Continue search at first item?");
                if (response == JOptionPane.YES_OPTION) {
                    tmpIndex = 0;
                    
                    // the following line sets the index to 0, but doesn't continue search
                    //maxIndex = tmpIndex;
                } else {
                    tmpIndex = getRecIndex();
                    break;
                }
            }

        }
		setRecIndex(tmpIndex);
	}


	/**
	 * allRecordingsDone() returns true if all items have been recorded at least
	 * once, false otherwise. The RecScriptManager keeps track of all recordings
	 * performed in the current sessionid by incrementing a counter for every
	 * prompt item after it has been recorded.
	 */
	public boolean allRecordingsDone() {
		boolean allRecordingsDone = true;
		for (int i = 0; i < getMaxIndex(); i++) {
            // System.out.println("allRecsDone [" + i + "] = " +
            // getRecCounter(i));
            ipsk.db.speech.PromptItem pi = getPromptItem(i);
            if (pi instanceof Recording) {
                if (getRecCounter(i) < 1) {
                    allRecordingsDone = false;
                    break;
                }
            }
        }
		return allRecordingsDone;
	}

	// overwriting AbstracTableModel methods with data from the promptList

	/**
	 * getRowCount() returns the number of prompt items in the current recording
	 * script
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return getMaxIndex();
	}

	/**
	 * getColumnCount() returns the number of items to be displayed in a prompt
	 * item. Currently, five items are displayed:
	 * <ol>
	 * <li>sequence number</li>
	 * <li>prompt item URL</li>
	 * <li>prompt item text or description</li>
	 * <li>recording indicator</li>
	 * <li>upload indicator</li>
	 * </ol>
	 * The display of the URL is truncated, recording and upload indicators are
	 * check boxes or integer values.
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return COLUMNS;
	}

	/**
	 * getValueAt() returns the appropriate item for the given row and column
	 * coordinates. The columns are given as
	 * <ol>
	 * <li>sequence number</li>
	 * <li>recording URL</li>
	 * <li>prompt item text or description</li>
	 * <li>recording indicator</li>
	 * <li>upload indicator</li>
	 * </ol>
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int column) {
		//logger.info("getValueAt(): " + row + ", " + column);
		if (getMaxIndex() == 0)
			return null;
        
		ipsk.db.speech.PromptItem pi =  getPromptItem(row);
		Mediaitem mi=pi.getMediaitems().get(0);
        String itemCode="";
       
        if(pi instanceof Recording){
            Recording recItem=(Recording)pi; 
            itemCode=recItem.getItemcode();
        }
		if (column == 0) {
			return new Integer(row);
		} else if (column == 1) {
			return itemCode;
		} else if (column == 2) {
			// Moved to PromptItem getDescription()
			// K. Jaensch 04/2007
			
//			if (mimeType.startsWith("image")) {
//				return "IMAGE: " + description;
//			} else if (mimeType.startsWith("audio")) {
//				return "AUDIO: " + description;
//			} else if (mimeType.startsWith("video")) {
//				return "VIDEO: " + description;
//			} else {
//				return text;
//			}
			return pi.getDescription();
		} else if (column == 3) {
            if(pi instanceof Nonrecording || row >=recCounter.length){
              return null; 
            }else {
			return new Boolean(recCounter[row] > 0);
        }
		} else if (column == 4) {
			return new Boolean(recProcessed[row]);
		} else {
			// TODO what to do if the table is broken
			return null;
		}
	}

	/**
	 * isCellEditable() returns true if the cell at a given row and column index
	 * is editable, false otherwise
	 * 
	 * @param row
	 *            row index
	 * @param column
	 *            column index
	 * @return boolean true if cell is editable
	 */
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/**
	 * getColumnName() returns the name of the column specified by the column
	 * index
	 * 
	 * @param col
	 *            column index
	 * @return String column name
	 */
	public String getColumnName(int col) {
		return tableHead[col];
	}

	/**
	 * getColumnClass() returns the Class of the column specified by the column
	 * index
	 * 
	 * @param col
	 *            column index
	 * @return Class of column
	 */
	public Class getColumnClass(int col) {
		//logger.info("getColumnClass(): " + col);
		//return getValueAt(0, col).getClass();
		if(col==0){
		    return Integer.class;
		}else if(col==1){
		   return String.class;
		}else if(col==2){
           return String.class;
        }else if(col==3){
           return Boolean.class;
        }else if(col==4){
           return Boolean.class;
        }else{
            // Error!
            return String.class;
        }
	}

	// implement update() method from RecObserver interface

	
	public String getSystemIdBase() {
		return systemIdBase;
	}

	
	public void setSystemIdBase(String string) {
		systemIdBase = string;
	}

	/**
	 * @return Returns the script id attribute.
	 */
	public String getScriptID() {
		return script.getName();
	}

	/**
	 * Set the URL context (usually the project directory in the workspace). 
	 * @param context
	 */
	public void setContext(URL context) {
		this.context=context;
		
	}
	/** 
	 * Get the workspace project context.
	 * @return the URL workspace context
	 */
	public URL getContext() {
		return context;
	}

	/**
	 * @return logger
	 */
	public Logger getLogger() {
		return logger;
	}

    /**
     * resets the manager to the initial state
     */
    public void doClose(){
		setScript(null);
		resetItemMarkers();
		initialize();
//		fireTableDataChanged();
//        fireRecscriptManagerUpdate(new RecScriptManagerClosedEvent(this));
    }

    /**
     * @param l
     * @see javax.swing.DefaultListSelectionModel#addListSelectionListener(javax.swing.event.ListSelectionListener)
     */
    public void addListSelectionListener(ListSelectionListener l) {
        selModel.addListSelectionListener(l);
    }

    /**
     * @param index0
     * @param index1
     * @see javax.swing.DefaultListSelectionModel#addSelectionInterval(int, int)
     */
    public void addSelectionInterval(int index0, int index1) {
        selModel.addSelectionInterval(index0, index1);
    }

    /**
     * 
     * @see javax.swing.DefaultListSelectionModel#clearSelection()
     */
    public void clearSelection() {
        selModel.clearSelection();
    }

    /**
     * @see javax.swing.DefaultListSelectionModel#getAnchorSelectionIndex()
     */
    public int getAnchorSelectionIndex() {
        return selModel.getAnchorSelectionIndex();
    }

    /**
     * @see javax.swing.DefaultListSelectionModel#getLeadSelectionIndex()
     */
    public int getLeadSelectionIndex() {
        return selModel.getLeadSelectionIndex();
    }

    /**
     * @param <T>
     * @param listenerType
     * @return event listeners
     * @see javax.swing.DefaultListSelectionModel#getListeners(java.lang.Class)
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return selModel.getListeners(listenerType);
    }

    /**
     * @return selection listeners
     * @see javax.swing.DefaultListSelectionModel#getListSelectionListeners()
     */
    public ListSelectionListener[] getListSelectionListeners() {
        return selModel.getListSelectionListeners();
    }

    /**
     * @return max selection index
     * @see javax.swing.DefaultListSelectionModel#getMaxSelectionIndex()
     */
    public int getMaxSelectionIndex() {
        return selModel.getMaxSelectionIndex();
    }

   
    public int getMinSelectionIndex() {
        return selModel.getMinSelectionIndex();
    }

    /**
     * @return selection mode
     * @see javax.swing.DefaultListSelectionModel#getSelectionMode()
     */
    public int getSelectionMode() {
        return selModel.getSelectionMode();
    }

    /**
     * @return is value adjusting
     * @see javax.swing.DefaultListSelectionModel#getValueIsAdjusting()
     */
    public boolean getValueIsAdjusting() {
        return selModel.getValueIsAdjusting();
    }

    /**
     * @param index
     * @param length
     * @param before
     * @see javax.swing.DefaultListSelectionModel#insertIndexInterval(int, int, boolean)
     */
    public void insertIndexInterval(int index, int length, boolean before) {
        selModel.insertIndexInterval(index, length, before);
    }

    
    public boolean isLeadAnchorNotificationEnabled() {
        return selModel.isLeadAnchorNotificationEnabled();
    }

    
    public boolean isSelectedIndex(int index) {
        return selModel.isSelectedIndex(index);
    }

    
    public boolean isSelectionEmpty() {
        return selModel.isSelectionEmpty();
    }

    /**
     * @param leadIndex
     * @see javax.swing.DefaultListSelectionModel#moveLeadSelectionIndex(int)
     */
    public void moveLeadSelectionIndex(int leadIndex) {
        selModel.moveLeadSelectionIndex(leadIndex);
    }

    /**
     * @param index0
     * @param index1
     * @see javax.swing.DefaultListSelectionModel#removeIndexInterval(int, int)
     */
    public void removeIndexInterval(int index0, int index1) {
        selModel.removeIndexInterval(index0, index1);
    }

    /**
     * @param l
     * @see javax.swing.DefaultListSelectionModel#removeListSelectionListener(javax.swing.event.ListSelectionListener)
     */
    public void removeListSelectionListener(ListSelectionListener l) {
        selModel.removeListSelectionListener(l);
    }

    /**
     * @param index0
     * @param index1
     * @see javax.swing.DefaultListSelectionModel#removeSelectionInterval(int, int)
     */
    public void removeSelectionInterval(int index0, int index1) {
        selModel.removeSelectionInterval(index0, index1);
    }

    /**
     * @param anchorIndex
     * @see javax.swing.DefaultListSelectionModel#setAnchorSelectionIndex(int)
     */
    public void setAnchorSelectionIndex(int anchorIndex) {
        selModel.setAnchorSelectionIndex(anchorIndex);
    }

    /**
     * @param flag
     * @see javax.swing.DefaultListSelectionModel#setLeadAnchorNotificationEnabled(boolean)
     */
    public void setLeadAnchorNotificationEnabled(boolean flag) {
        selModel.setLeadAnchorNotificationEnabled(flag);
    }

    /**
     * @param leadIndex
     * @see javax.swing.DefaultListSelectionModel#setLeadSelectionIndex(int)
     */
    public void setLeadSelectionIndex(int leadIndex) {
        selModel.setLeadSelectionIndex(leadIndex);
    }

    /**
     * @param index0
     * @param index1
     * @see javax.swing.DefaultListSelectionModel#setSelectionInterval(int, int)
     */
    public void setSelectionInterval(int index0, int index1) {
        // user events from the progress viewer table
        if(setIndexActionsEnabled){
        selModel.setSelectionInterval(index0, index1);
		setRecIndex(index0);
        }
        
    }

    /**
     * @param selectionMode
     * @see javax.swing.DefaultListSelectionModel#setSelectionMode(int)
     */
    public void setSelectionMode(int selectionMode) {
        selModel.setSelectionMode(selectionMode);
    }

    /**
     * @param isAdjusting
     * @see javax.swing.DefaultListSelectionModel#setValueIsAdjusting(boolean)
     */
    public void setValueIsAdjusting(boolean isAdjusting) {
        selModel.setValueIsAdjusting(isAdjusting);
    }

	

	public Section.Mode getDefaultMode() {
		return defaultMode;
	}

	
	public int getDefaultPreDelay() {
		return defaultPreDelay;
	}

	public void setDefaultPreDelay(int defaultPreDelay) {
		this.defaultPreDelay = defaultPreDelay;
	}

	public int getDefaultPostDelay() {
		return defaultPostDelay;
	}

	public void setDefaultPostDelay(int defaultPostDelay) {
		this.defaultPostDelay = defaultPostDelay;
	}
	
	
	
	public PromptItem setCurrentPromptItem(PromptItem promptItem){
	    // try to find the item
	    if(script==null){
//	        throw new IllegalArgumentException();
	    	return null;
	    }
	    int newIndex=0;
	    List<Section> sections=script.getSections();
	    if(sections!=null){
	        for(Section s :sections){
	            List<PromptItem> pis=s.getPromptItems();
	            for(PromptItem pi:pis){
	                if(promptItem==pi){
	                    setRecIndex(newIndex);
	                    return pi;
	                }
	                newIndex++;
	            }
	        }
	    }
//	    throw new IllegalArgumentException();
	    return null;
	}
	
	
	  /**
     * Add listener.
     * 
     * @param acl
     *            new listener
     */
    public synchronized void addSessionManagerListener(SessionManagerListener acl) {
        if (acl != null && !listeners.contains(acl)) {
            listeners.addElement(acl);
        }
    }

    /**
     * Remove listener.
     * 
     * @param acl
     *            listener to remove
     */
    public synchronized void removeSessionManagerListener(SessionManagerListener acl) {
        if (acl != null) {
            listeners.removeElement(acl);
        }
    }

    protected synchronized void fireSessionManagerUpdate(SessionManagerEvent event){
       
    	for( SessionManagerListener listener:listeners){
            listener.update(event);
        }
    }

	public boolean isScriptSaved() {
		return scriptSaved;
	}

	
    public boolean isSetIndexActionsEnabled() {
        return setIndexActionsEnabled;
    }

    public void setSetIndexActionsEnabled(boolean setIndexActionsEnabled) {
        this.setIndexActionsEnabled = setIndexActionsEnabled;
    }

    public boolean isDefaultAutomaticPromptPlay() {
        return defaultAutomaticPromptPlay;
    }

    public void setDefaultAutomaticPromptPlay(boolean automaticPromptPlayDefault) {
        this.defaultAutomaticPromptPlay = automaticPromptPlayDefault;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String scriptDTD) {
        this.systemId = scriptDTD;
    }

    public boolean isProgresToNextUnrecorded() {
        return progresToNextUnrecorded;
    }

    public void setProgresToNextUnrecorded(boolean progresToNextUnrecorded) {
        this.progresToNextUnrecorded = progresToNextUnrecorded;
    }


}

/**
 * Helper class to load resources via URLs in a thread so that the program does
 * not block.
 * 
 * @author draxler
 *  
 */

class ResourceLoader extends Thread {
	private Hashtable resourceList;

	private SessionManager recScriptManager;
	
	/**
	 * creates a Hashtable for the resources to load and starts a thread for
	 * loading them
	 * 
	 * @param RecScriptManger for which to load the data
	 */

	ResourceLoader(SessionManager rsm) {
		super("ResourceLoader");
		recScriptManager = rsm;
		resourceList = new Hashtable();
		start();
	}

	public void run() {
		for (int i = 0; i < recScriptManager.getMaxIndex(); i++) {
            ipsk.db.speech.PromptItem promptItem = null;
            Recording recItem=null;
            Nonrecording nonrecItem=null;
            String mimeType=null;
            URI src=null;
            String text=null;
            promptItem =recScriptManager.getPromptItem(i);
            if (promptItem instanceof Recording){
                recItem=(Recording)promptItem;
                Mediaitem mi=recItem.getMediaitems().get(0);
                mimeType=mi.getNNMimetype();
            src=mi.getSrc();
            if(src==null){
                text=mi.getPromptText();
            }
           
            }else if (promptItem instanceof Nonrecording){
                nonrecItem=(Nonrecording)promptItem;
                mimeType=nonrecItem.getMediaitems().get(0).getNNMimetype();
                src=nonrecItem.getMediaitems().get(0).getSrc();
                if(src==null){
                    text=nonrecItem.getMediaitems().get(0).getText();
                }
              
            }
			
			
            if (src != null) {
                URL contextPromptSrc=null;
                try {
                    contextPromptSrc=URLContext.getContextURL(recScriptManager.getContext(),src.toString());
                } catch (MalformedURLException e) {
                    recScriptManager.getLogger().severe("Cannot transform prompt URL: "+src);
                    return;
                }
                if(contextPromptSrc!=null){
                    if (mimeType.startsWith("audio")) {
                        resourceList.put(src, Applet.newAudioClip(contextPromptSrc));
                    } else if (mimeType.startsWith("image")) {	
                        // Disabled due to memory overflow problems		
                        //resourceList.put(promptSrc, (Image) Toolkit.getDefaultToolkit().getImage(promptSrc));
                    } else if (mimeType.startsWith("video")) {
                        //TODO: for videos, only the URL is stored in the hashtable. The video
                        //will be loaded when it is needed; this needs to be changed.
                        //resourceList.put(src,src);
                    }
                }
            }
		}
	}

	/**
	 * returns a Hashtable of loaded resources
	 * 
	 * @return Hashtable
	 */

	public Hashtable getResources() {
		return resourceList;
	}
}