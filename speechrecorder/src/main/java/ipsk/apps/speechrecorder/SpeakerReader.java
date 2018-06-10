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

/*
 * Created on Apr 5, 2003
 *
 * SpeakerReader reads in an XML formatted speaker database
 * file and returns a vector of speaker items.
 */

package ipsk.apps.speechrecorder;
import org.w3c.dom.*;
import java.util.Vector;


public class SpeakerReader {
	private Vector<Speaker> speakerData = new Vector<Speaker>();
	private int maxID;
	
//	private String nodeValue = "";

	private String spkName;
	private String spkFirstName;
	private String spkCode;
	private String spkKey;
	private String spkAccent;
	private String spkDateOfBirth;
	private String spkGender;
	
	/**
	 * SpeakerReader reads an XML formatted speaker database entry
	 * 
	 * @param speakerDoc the speaker database document to be read
	 */
	public SpeakerReader(Node speakerDoc) {
		speakerData = new Vector<Speaker>();
		maxID = 0;
		extractSpeakerListData(speakerDoc);
	}

	/**
	 * sets all internal fields to null
	 *
	 */
	public void initializeSpeakerReader() {
		spkName = null;
		spkFirstName = null;
		spkCode = null;
		spkKey = null;
		spkAccent = null;
		spkDateOfBirth = null;
		spkGender = null;
	}
	
	
	/**
	 * if the current speaker ID is larger than the previous
	 * maximum ID, then the current ID becomes the new 
	 * maximum ID.
	 */
	private void setMaxID(int id) {
		if (id > maxID) {
			maxID = id;
		}
	}
	
	/**
	 * returns the maximum ID
	 * @return int maximum ID
	 */
	public int getMaxID() {
		return maxID;
	}
	
	
	/**
	 * toString returns the speaker database as a String, one XML speaker item
	 * per line. The attributes and contents of a speaker item are delimited by tabs.
	 * 
	 * @return String a string representation of the speaker database items
	 */
	public String toString() {
		StringBuffer tmpBuffer = new StringBuffer();
		for (int i = 0; i < speakerData.size(); i++) {
			Object pi =  speakerData.elementAt(i);
			tmpBuffer.append(pi.toString() + "\n");
		}
		return tmpBuffer.toString();
	}
	
	
	/**
	 * returns the speaker database as a Vector of prompt items
	 * 
	 * @return Vector the speaker database
	 */
	public Vector<Speaker> getVector() {
		return speakerData;
	}


	/**
	 * extractSpeakerData reads an XML formatted speaker database and
	 * stores the speaker items into a Vector. 
	 * 
	 * @param node the document node to read
	 */
	private void extractSpeakerListData(Node node) {
		Node childNode;
		String childNodeName = "";

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			childNode = nodeList.item(i);
			
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				childNodeName = childNode.getNodeName();
				if (childNodeName.equals("speaker")) {
					initializeSpeakerReader();
					Speaker speaker = extractSpeakerData(childNode);
					setMaxID(speaker.getID());
					speakerData.add(speaker);
				}
			}
		}	
	 }

	/**
	 * extractSpeakerData extracts the item contents of a speaker item.
	 * 
	 * @param node to extract data from
	 */
	private Speaker extractSpeakerData(Node node) {
		NodeList nodeList = node.getChildNodes();
		Speaker speaker;
		int spkID = 0;
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node tmpNode = nodeList.item(i);
			if (tmpNode.getNodeType() == Node.ELEMENT_NODE) {
				//System.out.println("extractSpeakerData: " + tmpNode.getNodeName());
				if (tmpNode.getNodeName().equals("ID")) {
					spkKey = tmpNode.getFirstChild().getNodeValue().trim();
					spkID = Integer.parseInt(spkKey);
				} else if (tmpNode.getNodeName().equals("code")) {
					spkCode = tmpNode.getFirstChild().getNodeValue().trim();
				} else if (tmpNode.getNodeName().equals("familyname")) {
					spkName = tmpNode.getFirstChild().getNodeValue().trim();
				} else if (tmpNode.getNodeName().equals("givenname")) {
					spkFirstName = tmpNode.getFirstChild().getNodeValue().trim();
				} else if (tmpNode.getNodeName().equals("accent")) {
					spkAccent = tmpNode.getFirstChild().getNodeValue().trim();
				} else if (tmpNode.getNodeName().equals("sex")) {
					spkGender = tmpNode.getFirstChild().getNodeValue().trim();
				} else if (tmpNode.getNodeName().equals("dateofbirth")) {
					spkDateOfBirth = tmpNode.getFirstChild().getNodeValue().trim();
				}
			}
		}
		
		speaker = new Speaker(spkID);
		speaker.setCode(spkCode);
		speaker.setName(spkName);
		speaker.setFirstName(spkFirstName);
		speaker.setAccent(spkAccent);
		speaker.setGender(spkGender);
		speaker.setDateOfBirth(spkDateOfBirth);
		return speaker;
	}
}
