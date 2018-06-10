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

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
//import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXB;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.*;



/**
 * SpeakerDatabaseLoader is an auxiliary class that loads a speaker database from
 * an external source, e.g. a text or XML file or a DBMS. The data read in is converted 
 * to objects of class speaker, and these objects are stored in a vector so that the
 * original order is preserved.
 * 
 * SpeakerDatabase makes the database fields, the database contents, the number of
 * items read in and the maximum key value public to calling objects.
 * 
 * @author draxler
 */

public class SpeakerDatabaseLoader {

    private UIResources uiString;
	private Logger logger;
	
//	private Vector<Speaker> db = null;
//	private List<ipsk.db.speech.Speaker> db = null;
	private Speakers db=null;
	private Vector dbDescription = null;
	private int maxID = 0;
	private int dbIndex = 0;

	private String dbFileName;
	private String dbDirectory;
	
	public enum DatabaseType {SPEECH_DB,WIKISPEECH_XML,TXT};
	private DatabaseType databaseType=DatabaseType.SPEECH_DB;
	

	public SpeakerDatabaseLoader(){
		logger = Logger.getLogger("ipsk.apps.speechrecorder");
		
		uiString = UIResources.getInstance();
//		db = new ArrayList<ipsk.db.speech.Speaker>();
		db=new Speakers();
		dbIndex = 0;
	}
	
	/**
	 * SpeakerDatabaseLoader loads a speaker database from a file, given a 
	 * list of field descriptors. The file may be a plain text file (if the
	 * file extension is .txt or .TXT, an XML-formatted file otherwise.
	 * While reading in a database, the maximum speaker ID is logged.
	 * 
	 * @param file source from which to read the items
	 * @param description item fields
	 * @throws IOException 
	 */
	public SpeakerDatabaseLoader(File file, Vector description) throws IOException {
		this();
		dbDescription = description;
		if ((file == null) || (!file.isFile())) {
			FileDialog fd =
				new FileDialog((Frame)null,
					uiString.getString("LoadSpeakerFile"),
					FileDialog.LOAD);
			fd.setVisible(true);
			dbDirectory = fd.getDirectory();
			dbFileName = fd.getDirectory() + fd.getFile();
			file = new File(dbFileName);
		}

		if (file.getName().endsWith(".txt")
			|| file.getName().endsWith(".TXT")) {
			databaseType=DatabaseType.TXT;
			readDatabaseFile(file);
		} else {
		    databaseType=DatabaseType.SPEECH_DB;
			readDatabaseXMLFile(file);
		}

		dbIndex = db.getSpeakers().size() - 1;
	}

	/**
	 * if the ID of the current item is larger than the previous
	 * maximum ID value, then the new maximum ID value is set to the
	 * current value.
	 * 
	 * @param id
	 */
	private void setMaxID(int id) {
		if (id > maxID) {
			maxID = id;
		}
	}
	
	/**
	 * returns the maximum ID value currently in the database
	 * @return int maximum ID 
	 */
	public int getMaxID() {
		return maxID;
	}
	
	/**
	 * SpeakerDatabaseLoader loads a speaker database from a URL, given a 
	 * list of field descriptors. The URL may be a plain text file (if the
	 * file extension is .txt or .TXT, an XML-formatted file otherwise.
	 * 
	 * @param url source from which to read the items
	 * @param description vector of field descriptors
	 */
	public SpeakerDatabaseLoader(URL url, Vector description) {
		this();
		dbDescription = description;
		
		try {
			//InputStream is = url.openStream();
			// do not cache here to get updated session,speaker ID from server
			URLConnection urlConn=url.openConnection();
			urlConn.setUseCaches(false);
			InputStream is=urlConn.getInputStream();
			URI uri=url.toURI();
			String path=uri.getPath();
			if (path.endsWith(".txt")
				|| path.endsWith(".TXT")) {
			    databaseType=DatabaseType.TXT;
				readDatabaseStream(is);
			} else {
				if("https".equalsIgnoreCase(url.getProtocol())){
					databaseType=DatabaseType.WIKISPEECH_XML;
					readDatabaseXMLStream(is);
				}else{
					databaseType=DatabaseType.SPEECH_DB;
					readSpeakersXMLStream(is);
				}
			}
		} catch (Exception e) {
			logger.severe("SpeakerDatabaseLoader(" + url.toString() + "): " + e.toString());
		}
		dbIndex = db.getSpeakers().size() - 1;
	}


	/**
	 * getDatabase() returns the speaker database as a vector of speaker objects
	 * @return Vector of speaker objects
	 */
	public List<ipsk.apps.speechrecorder.db.Speaker> getDatabase() {
		return db.getSpeakers();
	}

    public Speakers getSpeakersDb(){
        return db;
    }

	/**
	 * parses an input stream for speaker data. The input stream consists of lines
	 * with fields; each line corresponds to a single speaker, each field to a 
	 * speaker attribute. 
	 * <br>
	 * The sequence of fields in the line must be 
	 * <br>
	 * ID, code, name, first name, sex, accent, date of birth. 
	 * <br>
	 * The speaker ID must be 
	 * a natural number, it must be unique for every speaker. The speaker code
	 * must also be unique, however it may be any value. Speaker ID and code
	 * must be provided, all other fields can be empty.
	 *  
	 * @param InputStream 
	 * @throws IOException
	 */
	private void readDatabaseStream(InputStream is) throws IOException {
		String fileLine;

		InputStreamReader fr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(fr);

		int lineCount = 0;
		while ((fileLine = br.readLine()) != null) {
			lineCount++;
			
			if (fileLine.trim().startsWith("#")) {
				// comment line found, skip this line
				break;
			}
			
			// content line found
			
			//StringTokenizer st = new StringTokenizer(fileLine, "\t",false);
			//int fieldCount = st.countTokens();
			
			String[]  fields=fileLine.split("\t");
			int fieldCount=fields.length;
			
//			// the number of fields in the line read in must be at 
//			// least two because a line must consist of the speaker
//			// ID and code. 
//			if (fieldCount >= 2) {
				
//			klausj: but the speaker editor allows to add speakers without any field filled
//			this speakers did not appear in the list on loading 
			if (fieldCount >= 1) {	
				// first field in the row must be the speaker ID
				// The maximum ID is logged so that new speakers
				// get an ID that is larger than the current maximum ID
				
				int ID = Integer.parseInt(fields[0]);
				setMaxID(ID);
				ipsk.apps.speechrecorder.db.Speaker spk = new ipsk.apps.speechrecorder.db.Speaker(ID);
				
				
//				for (int i = 0; i < fieldCount - 1; i++) {
//					spk.setSpeakerData(i, fields[i+1]);
//				}
				for(int i=0;i<fieldCount-1;i++){
					int col=i+1;
					if(col==Speaker.COL_CODE){
						spk.setCode(fields[col]);
					}else if(col==Speaker.COL_NAME){
						spk.setName(fields[col]);
					}else if(col==Speaker.COL_FORENAME){
						spk.setForename(fields[col]);
					}else if(col==Speaker.COL_GENDER){
						spk.setGender(fields[col]);
					}else if(col==Speaker.COL_ACCENT){
	                        spk.setAccent(fields[col]);
					}else if(col==Speaker.COL_BIRTHDATE){
						spk.setDateOfBirthString(fields[col]);
					}
				}
				db.getSpeakers().add(spk);
//				JAXB.marshal(spk, System.out);
			} else {
				logger.warning("Incomplete entry in line " + lineCount + ". A valid entry must have an ID and a code field. " + fileLine);
			}
		}
		fr.close();
		br.close();
	}


	/**
	 * reads a speaker database file in text format.
	 * 
	 * @param fileName
	 */
	private void readDatabaseFile(File fileName) {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
			readDatabaseStream(fis);
		} catch (FileNotFoundException e) {
			logger.severe("readDatabaseFile(" + fileName.getAbsolutePath() + "): " +e.toString());
		} catch (IOException e) {
			logger.severe("readDatabaseFile(" + fileName.getAbsolutePath() + "): " +e.toString());
		}
	}
	
	/**
	 * reads a speaker database in new XML format.
	 * @param is
	 * @throws IOException 
	 */
	private void readSpeakersXMLStream(InputStream is) throws IOException {

	    db=JAXB.unmarshal(is, Speakers.class);
	    is.close();
	    db.getSpeakers();
	    
        setMaxID(db.maxID());
	}
	
	/**
	 * reads a speaker database in XML format.
	 * @param is
	 * @throws IOException 
	 */
	private void readDatabaseXMLStream(InputStream is) throws IOException {

	  
		DocumentBuilder builder;
		Document doc;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		//factory.setNamespaceAware(true);
		try {
			builder = factory.newDocumentBuilder();

			doc = builder.parse(is);
			extractData(doc.getDocumentElement());

		} catch (SAXException sxe) {
			Exception x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();

		} catch (IOException e) {
			System.out.println(e);
		}
	}


	/**
	 * opens an XML formatted speaker database 
	 * @param file
	 * @throws IOException 
	 */
	private void readDatabaseXMLFile(File file) throws IOException {

	   
	    FileInputStream fis = null;

	    fis = new FileInputStream(file);
	    readDatabaseXMLStream(fis);
//	    try {
//	        fis.close();
//	    } catch (IOException e) {
//
//	        logger.severe("readDatabaseFile(" + file.getAbsolutePath() + "): " + e.toString());
//	        throw e;
//	    }
	}


	/**
	 * traverses the speaker database DOM tree until it finds a 
	 * speakerlist node and then calls the extractData() method
	 * for the speakerlist.
	 * 
	 * @param node
	 */
	private void extractData(Node node) {
		NodeList nodeList;
		Node currentNode;
		// TODO remove this !! use speechdb XML format for Wikispeech !!
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			//System.out.println("extractData(" +  node.getNodeName() + ")");        
			if (node.getNodeName().equals("speakerlist")) {
				SpeakerReader speakerReader = new SpeakerReader(node);
				Vector<Speaker> dbV = speakerReader.getVector();
				List<ipsk.apps.speechrecorder.db.Speaker> dbSpks=new ArrayList<ipsk.apps.speechrecorder.db.Speaker>();
				for(Speaker sp:dbV){
					ipsk.apps.speechrecorder.db.Speaker nSpk=new ipsk.apps.speechrecorder.db.Speaker(sp.getID());
					dbSpks.add(nSpk);
				}
				db.setSpeakers(dbSpks);
						
				setMaxID(speakerReader.getMaxID());
			} else {
				nodeList = node.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					currentNode = nodeList.item(i);
					extractData(currentNode);
				}
			}
		}
	}

	public void writeDatabaseFile(File dbFile) throws IOException {
				
//			Enumeration<Speaker> e = db.elements();
//			while (e.hasMoreElements()) {
//				Speaker spk=(Speaker)e.nextElement();
			if(DatabaseType.SPEECH_DB.equals(databaseType)){
			    JAXB.marshal(db, dbFile);
			}else{
			    // legacy text file
			    BufferedWriter dbOutput =
		                new BufferedWriter(new FileWriter(dbFile));
			for(ipsk.apps.speechrecorder.db.Speaker spk:db.getSpeakers()){
			   
				StringBuffer s = new StringBuffer(Integer.toString(spk.getPersonId()));
//				Vector<String> v = (Vector<String>) spk.getData();	
				Vector<String> v =new Vector<String>();
				
				v.add(spk.getCode());
				v.add(spk.getName());
				v.add(spk.getForename());
				v.add(spk.getGender());
				v.add(spk.getAccent());
				v.add(spk.getDateOfBirthString());
				for (int i = 0; i < v.size(); i++) {
					String element=v.elementAt(i);
					String dbValue="";
					if(element!=null){
						dbValue=element.toString();
					}
					s.append("\t" + dbValue);
				}
				dbOutput.write(s + "\n");
			}
			dbOutput.close();
			}
	}

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(DatabaseType databaseType) {
        this.databaseType = databaseType;
    }
}
