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

package ipsk.apps.speechrecorder.script;

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
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.SAXException;

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

public class RecScriptManager extends Object{
    
    public final static String REC_SCRIPT_DTD_1 = "SpeechRecPrompts.dtd";
    public final static String REC_SCRIPT_DTD_2 = "SpeechRecPrompts_2.dtd";
//    public final static String REC_SCRIPT_DTD = REC_SCRIPT_DTD_2;
//    public final static String REC_SCRIPT_DTD_3 = "SpeechRecPrompts_3.dtd";
    public final static String REC_SCRIPT_DTD = REC_SCRIPT_DTD_2;

	public static final int AUTOMATIC = 0;
	public static final int MANUAL = 1;
	public static final int SEQUENTIAL = 2;
	public static final int RANDOM = 3;
	

	// arbitrary identifier required to set a custom cell renderer
	public static final String RECORDED_COL_ID="progress.table.col.recorded";
	
	public static final int ERROR_MSG_MAX_ITEMS=20;
	
	private Logger logger;

	private UIResources uiString = null;
	
	private Script script;
	private boolean scriptSaved=true;
	

	//recResources stores all objects pre-fetched from URLs
	private URL context=null;
	private ResourceLoader resourceLoader;
	private Hashtable promptResources;
	private String systemIdBase = null;
//	private StorageManager storageManager;
	private static RecScriptManager _instance = null;

	//private MetaData metadata;
	
    
    private boolean defaultSpeakerDisplay;
	private Section.Mode defaultMode;
	private int defaultPreDelay;
	private int defaultPostDelay;
	private boolean defaultAutomaticPromptPlay=true;
	private boolean setIndexActionsEnabled=false;
	private String systemId=REC_SCRIPT_DTD;
    private ItemCodeValidator itemCodeValidator;
    private boolean progresToNextUnrecorded=false;
   
    private Vector<RecscriptManagerListener> listeners=new Vector<RecscriptManagerListener>();
	
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

	private RecScriptManager() {
		super();
		logger = Logger.getLogger("ipsk.apps.speechrecorder");

	
		
		// get description of table columns from the GUI property file
		uiString = UIResources.getInstance();
		
    
        initialize();
		//recStatus = RecStatus.getInstance();
	}

	/**
	 * getInstance() returns the singleton RecScriptManager object.
	 * 
	 * @return instance
	 */

	public static RecScriptManager getInstance() {
		if (_instance == null) {
			_instance = new RecScriptManager();
		}
		return _instance;
	}

	/**
	 * initializes the RecScriptManager, i.e. set the current recording index
	 * and the maximum index to 0 and creates a new recording sections Vector 
	 */
	public void initialize() {
		
		//maxIndex = 0;
		//recSections = new Vector();
//		selModel.clearSelection();
	}
	
	private File getRequiredDTDFile() throws MalformedURLException {
	    if(systemIdBase!=null){
	        URL systemIdBaseURL=new URL(systemIdBase);
	        if(systemIdBaseURL.getProtocol().equalsIgnoreCase("file")){
	            String systemIdBaseDirname;
				try {
					systemIdBaseDirname = systemIdBaseURL.toURI().getPath();
				} catch (URISyntaxException e) {
					throw new MalformedURLException(e.getMessage());
				}
	            File systemBaseDir=new File(systemIdBaseDirname);

	            File dtdFile=new File(systemBaseDir,REC_SCRIPT_DTD);
	            return dtdFile;
	        }
	    }
	    return null;
	}

	public boolean isNewVersionOfDTDFileRequired() throws MalformedURLException{
	    File requiredDtdFile=getRequiredDTDFile();
	    return (requiredDtdFile!=null && !requiredDtdFile.exists());
	}

	public void createDTDFileIfRequired() throws IOException{
	    File dtdFile=getRequiredDTDFile();
	    if(dtdFile !=null && !dtdFile.exists()){
	        // copy recording script DTD
	        InputStream is = SpeechRecorder.class.getResourceAsStream(RecScriptManager.REC_SCRIPT_DTD);
	        FileOutputStream fos = new FileOutputStream(dtdFile);
	        StreamCopy.copy(is, fos);
	    }
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
		applyDefaults();
//		if (this.script!=null)this.script.addPropertyChangeListener(this);
		initialize();
       
//		TODO: right place to check for duplicate item codes?
		//cd, 24.11.2005
		//Klausj, moved to set script
		if (validItemCodes()) {
		    logger.info("Item codes unique.");
		} else {
		    logger.warning("Warning: NON-UNIQUE item codes!");		    
		}
////		 pre-fetch all resources from URLs
//		resourceLoader = new ResourceLoader(this);
//		promptResources = resourceLoader.getResources();
//		
		
        fireRecscriptManagerUpdate(new RecScriptChangedEvent(this));
	}
	
	public void shuffleItems(){
		if(script!=null){
			script.shuffleItems();
		}
	}
	
	/**
	 * loads a recording script from a URL. The format of the recording
	 * script file is expected to be XML, if the the filename extension is
	 * not <i>txt</i> or <i>TXT</i>. <br />
	 * Note that the use of text files is deprecated.
	 * 
	 * @param promptURL The location of the recording script
	 * @throws IOException
	 * @throws DOMConverterException 
	 * @throws URISyntaxException 
	 * @throws StorageManagerException 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public void load(URL promptURL) throws RecscriptManagerException{
		//recSections.clear();
	
		String promptURLString=promptURL.toExternalForm();
		
		try {
			promptURL=URLContext.getContextURL(context,promptURLString);
		} catch (MalformedURLException e) {
			throw new RecscriptManagerException(e);
		}
		
//		if (promptFileName.endsWith("txt") || promptFileName.endsWith("TXT")) 
//			readRecScriptTextFile(promptURL);
//		else
		Script script=readRecScriptXMLFile(promptURL);
		setScriptSaved(true);
		setScript(script);
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

//	/**
//	 * reads a prompt text file.
//	 * After that, a vector with the prompt file contents can be obtained via
//	 * <code>getRecSections()</code>.
//	 * 
//	 * @param recScriptFile
//	 *            full filename String
//	 */
//	public void readRecScriptTextFile(URL recScriptUrl) {
//		//promptDirectory = recScriptFile.getPath();
//		recSections = new Vector();
//		RecSection recSection = null;
//		try {
//			BufferedReader promptReader = new BufferedReader(
//					new InputStreamReader(recScriptUrl.openStream()));
//
//			String recScriptFileLine;
//			PromptItem promptItem;
//			recSection = new RecSection();
//			recSection.setName("DEFAULT");
//			recSection.setMode(RecSection.MANUAL);
//			recSection.setOrder(RecSection.SEQUENTIAL);
//			recSection.setPromptphase(RecSection.IDLE);
//			ArrayList promptItems = new ArrayList();
//			while ((recScriptFileLine = promptReader.readLine()) != null) {
//				promptItem = new PromptItem();
//				//promptItem.setContext(context);
//				promptItem.parsePromptItem(recScriptFileLine);
//				promptItems.add(promptItem);
//			}
//			recSection.setPromptItems(promptItems);
//			promptReader.close();
//		} catch (IOException e) {
//			logger.warning("readRecScriptURL(): " + e);
//		}
//		recSections.add(recSection);
//	}

	/**
	 * reads recording and prompting instructions from an XML-Document.
	 * After that, a vector with the prompt file contents can be obtained via
	 * <code>getRecSections()</code>.
	 * 
	 * @param recScriptURL URL
	 * @return the script
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws DOMConverterException 
	 * @throws IOException 
	 */
	public Script readRecScriptXMLFile(URL recScriptURL) throws RecscriptManagerException {
		logger.entering("readRecScriptXMLFile", "recScriptURLString");
		
		Document doc=null;
		//DocumentBuilderFactory	factory = DocumentBuilderFactory.newInstance();
        //factory.setValidating(true);
		//DocumentBuilder			builder = factory.newDocumentBuilder();
		//recSections = new Vector();		// (re-)set member variable
		String recScriptURLString=recScriptURL.toExternalForm();
		DOMConverter domConverter=new DOMConverter();
        domConverter.setValidating(true);
		
        try {
        	if (systemIdBase == null) {				// Pfadpraefix fuer DTD relativ zum Projekt
        		doc=domConverter.readXML(recScriptURLString);
        	}
        	else {
        		//doc=domConverter.readXML(recScriptURL.openStream(), systemIdBase);
        		URLConnection recScriptUrlConn=recScriptURL.openConnection();
    			recScriptUrlConn.setUseCaches(false);
    			InputStream recScriptIs=recScriptUrlConn.getInputStream();
                doc=domConverter.readXML(recScriptIs, systemIdBase);       
        	}
        } catch (Exception e) {
        	throw new RecscriptManagerException(e);
        } 
        DocumentType documentType=doc.getDoctype();
        systemId=documentType.getSystemId();
		//scriptid = doc.getDocumentElement().getAttribute("id");
		
		//extractMetaData(doc.getDocumentElement());
		//extractRecordingScriptData(doc.getDocumentElement());
		
		return new Script(doc.getDocumentElement());
	
	}

	
	
	
//	/**
//	 * @return Vector prompt file contents
//	 */
//	public Vector getRecSections() { return recSections; }	

//	private void extractMetaData(Node node)
//	{
//		NodeList nodeList;
//		Node 		currentNode;
//		String	key, value;
//		metadata = new MetaData();
//
//		if (node.getNodeType() == Node.ELEMENT_NODE)
//		{
//			if (node.getNodeName().equals("metadata"))
//			{
//				nodeList = node.getChildNodes();
//				for (int i = 0; i < nodeList.getLength(); i++)
//				{
//					currentNode = nodeList.item(i); key = "";
//					if (node.getNodeName().equals("key"))
//						key = node.getNodeValue();
//					else if (node.getNodeName().equals("value"))
//					{	
//						value = node.getNodeValue(); 
//						metadata.put(key, value);	
//					}
//				}
//			}
//			else
//			{
//				nodeList = node.getChildNodes();
//				for (int i = 0; i < nodeList.getLength(); i++)
//				{
//					currentNode = nodeList.item(i);
//					extractMetaData(currentNode);
//				}
//			}
//		}
//	}

//	public MetaData getMetaData() { return metadata; }
	
//	/**
//	 * recursively descends the XML document tree hierarchy until it finds
//	 * a &quot;recordingscript&quot; subtree. The recording script sections are then
//	 * extracted by a RecScriptLoader() from this subtree.
//	 * 
//	 * NOTE: the recording script is computed as a side effect of this 
//	 * method.
//	 * 
//	 * @param node
//	 */
//	private void extractRecordingScriptData(Node node) {
//		NodeList nodeList;
//		Node currentNode;
//
//		if (node.getNodeType() == Node.ELEMENT_NODE) {
//			//System.out.println("extractData(" + node.getNodeName() + ")");
//			if (node.getNodeName().equals("recordingscript")) {
//				RecScriptLoader recScriptReader = new RecScriptLoader(node);
//				recSections = recScriptReader.getScript();
//			} else {
//				nodeList = node.getChildNodes();
//				for (int i = 0; i < nodeList.getLength(); i++) {
//					currentNode = nodeList.item(i);
//					extractRecordingScriptData(currentNode);
//				}
//			}
//		}
//	}

//	/**
//	 * returns the recording section by section index
//	 * 
//	 * @param sectionIndex index in the range of 0 and (nuber of sections - 1)
//	 * @return RecSection
//	 */
//	public RecSection getRecSection(int sectionIndex) {
//		return (RecSection) recSections.elementAt(sectionIndex);
//	}
	
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
     * checks whether all recording codes are unique; if not,
     * a warning is displayed on the screen
     * 
     * @return boolean true if all item codes of the recording script
     * are unique, false otherwise
     */
    
	private boolean validItemCodes() {
	    ItemCodeValidator itemCodeValidator=new ItemCodeValidator();
	    boolean allValid = true;
	    StringBuffer errorMessage = new StringBuffer("Invalid item codes!\n");
	    int invalidItemCount=0;
	    for (int i = 0; i < getMaxIndex(); i++) {
	        ipsk.db.speech.PromptItem pi=getPromptItem(i);
	        if (pi instanceof Recording){
	            String itemCode = ((Recording)pi).getItemcode();
	            String validationMessage=itemCodeValidator.validateItemCode(itemCode);
	            if(validationMessage==null){
	                itemCodeValidator.getExistingCodes().add(itemCode);
	            }else{
	                allValid=false;
	                if(invalidItemCount<ERROR_MSG_MAX_ITEMS){
	                errorMessage.append("item #" + i+" code \"" + itemCode + "\": "+validationMessage);
	                errorMessage.append("\n");
	                	
	                }
	                invalidItemCount++;
	            }
	        }
	    }
	    if(invalidItemCount>ERROR_MSG_MAX_ITEMS){
	    	int moreInvalidItems=invalidItemCount-ERROR_MSG_MAX_ITEMS;
	    	errorMessage.append("...and "+moreInvalidItems+" more invalid item code(s).");
	    	errorMessage.append('\n');
	    }

	    if (! allValid) {
	        errorMessage.append("Please correct the recording script.");
	        JOptionPane.showMessageDialog(null,errorMessage.toString(), "Warning", JOptionPane.WARNING_MESSAGE);
	    }
	    return allValid;
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
		
		initialize();
//		fireTableDataChanged();
        fireRecscriptManagerUpdate(new RecScriptManagerClosedEvent(this));
    }

  
	public boolean isDefaultSpeakerDisplay() {
		return defaultSpeakerDisplay;
	}

	public void setDefaultSpeakerDisplay(boolean defaultSpeakerDisplay){
		this.defaultSpeakerDisplay = defaultSpeakerDisplay;
		applyDefaults();
        fireRecscriptManagerUpdate(new RecScriptChangedEvent(this));
	}

	public Section.Mode getDefaultMode() {
		return defaultMode;
	}

	public void setDefaultMode(Section.Mode defaultMode){
		this.defaultMode = defaultMode;
		applyDefaults();
        fireRecscriptManagerUpdate(new RecScriptChangedEvent(this));
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

	

	
	  /**
     * Add listener.
     * 
     * @param acl
     *            new listener
     */
    public synchronized void addRecscriptManagerListener(RecscriptManagerListener acl) {
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
    public synchronized void removeRecscriptManagerListener(RecscriptManagerListener acl) {
        if (acl != null) {
            listeners.removeElement(acl);
        }
    }

    protected synchronized void fireRecscriptManagerUpdate(RecscriptManagerEvent event){
       
    	for( RecscriptManagerListener listener:listeners){
            listener.update(event);
        }
    }

	public boolean isScriptSaved() {
		return scriptSaved;
	}

	public void setScriptSaved(boolean scriptSaved){
		this.scriptSaved = scriptSaved;
		fireRecscriptManagerUpdate(new RecScriptStoreStatusChanged(this));
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

	private RecScriptManager recScriptManager;
	
	/**
	 * creates a Hashtable for the resources to load and starts a thread for
	 * loading them
	 * 
	 * @param RecScriptManger for which to load the data
	 */

	ResourceLoader(RecScriptManager rsm) {
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