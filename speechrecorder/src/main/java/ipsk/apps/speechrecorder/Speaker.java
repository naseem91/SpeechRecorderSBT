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

import java.util.Vector;

/**
* A Speaker is described by an internal numerical and unique ID, and the visible
* attributes code, family name, first name, sex, accent and date of birth. The
* visible attributes may be accessed via a graphical user interface, the internal
* ID is used only to unambiguously identify every single speaker. The attribute
* code is a unique key to identify speakers; in contrast to the internal ID, the
* value of code can be set by the user.
*/

public class Speaker {
	
	public static int COL_ID=0;
	public static int COL_CODE=1;
	public static int COL_NAME=2;
	public static int COL_FORENAME=3;
	public static int COL_GENDER=4;
	public static int COL_ACCENT=5;
	public static int COL_BIRTHDATE=6;

	private int speakerID = 0;
    private String speakerCode;
    private String speakerName;
    private String speakerFirstName;
    private String speakerGender;
    private String speakerAccent;
    private String speakerDateOfBirth;

    private RecLogger recLog;
	private static UIResources uiString = UIResources.getInstance();


	/**
	 * Speaker creates a new speaker object with the given id. No other fields
	 * are set.
	 * @param ID speaker ID
	 */
    public Speaker(int ID) {
        speakerID = ID;
    }

	/**
	 * Speaker() creates a speaker object from a vector of string data. The 
	 * order of the vector elements must be: id, speaker code, name, first name,
	 * sex, accent, and date of birth.  
	 * @param spkData speaker data
	 */
    public Speaker(Vector<String> spkData) {
        speakerID = Integer.parseInt((String) spkData.elementAt(0));
        setData(spkData);
		uiString = UIResources.getInstance();
    }

	/**
	 * setSpeaker() sets the speaker fields according to the String parameters provided
	 * to the method. 
	 * 
	 * @param code speaker code
	 * @param name name
	 * @param fname first name
	 * @param gender gender
	 * @param accent accent
	 * @param birthdate date of birth
	 */
	public void setSpeaker(String code, String name, String fname, String gender, String accent, String birthdate) {
		setSpeakerData(COL_CODE, code);
		setSpeakerData(COL_NAME, name);
		setSpeakerData(COL_FORENAME, fname);
		setSpeakerData(COL_GENDER, gender);
		setSpeakerData(COL_ACCENT, accent);
		setSpeakerData(COL_BIRTHDATE, birthdate);
	}

	/**
	 * getSpeakerData() returns the speaker data identified by
	 * attribute position
	 * 
	 * @return Object speaker data
	 */
	public Object getSpeakerData(int i) {
		if (i == 0) {
			return getCode();
		} else if (i == 1) {
			return getName();
		} else if (i == 2) {
			return getFirstName();
		} else if (i == 3) {
			return getGender();
		} else if (i == 4) {
			return getAccent();
		} else if (i == 5) {
			return getDateOfBirth();
		} else {
			return null;
		}
	}
	
	/**
	 * sets the speaker data identified by
	 * attribute position to the given value
	 * 
	 * NOTE: all values are assumed to be Strings, and
	 * they are trimmed, i.e. leading and trailing blanks
	 * are removed.
	 * 
	 */
	public void setSpeakerData(int i, Object value) {
		String tmpValue = (String) value;
		String nomalizedValue = tmpValue.trim();
		if (i == 0) {
			setCode(nomalizedValue);
		} else if (i == 1) {
			setName(nomalizedValue);
		} else if (i == 2) {
			setFirstName(nomalizedValue);
		} else if (i == 3) {
			setGender(nomalizedValue);
		} else if (i == 4) {
			setAccent(nomalizedValue);
		} else if (i == 5) {
			setDateOfBirth(nomalizedValue);
		} 
	}
	
	/**
	 * returns the speaker's id
	 * 
	 * @return int speaker ID
	 */
	public int getID() {
		return speakerID;
	}
	
	/**
	 * returns the speaker's code
	 * @return String speaker code
	 */
	public String getCode() {
		return speakerCode;
	}
    
    /**
     * getName() returns the speaker's name
     * @return String speaker name
     */
	public String getName() {
		return speakerName;
	}
	
	/**
	 * returns the speaker's first name
	 * @return String first name
	 */
	public String getFirstName() {
		return speakerFirstName;
	}
	
	/**
	 * returns the speaker's gender
	 * @return String gender
	 */
	public String getGender() {
		return speakerGender;
	}
	
	/**
	 * returns the speaker's accent
	 * @return String accent
	 */
	public String getAccent() {
		return speakerAccent;
	}
	
	/**
	 * returns the speaker's date of birth. Note that
	 * the date is stored as a simple string without any semantics
	 * @return String date of birth
	 */
	public String getDateOfBirth() {
		return speakerDateOfBirth;
	}


	/**
	 * sets the speaker's code
	 * @param code
	 */
	public void setCode(String code) {
		if(code==null){
			speakerCode=code;
		}else{
		    // Should we use Locale.ENGLISH here or not
		speakerCode = code.toUpperCase();
	}
	}
	
	/**
	 * sets the speaker's name
	 * @param name
	 */
	public void setName(String name) {
 		speakerName = name;
	}
	
	/**
	 * sets the speaker's first name
	 * @param fname
	 */
	public void setFirstName(String fname) {
		speakerFirstName = fname;
	}
	
	/**
	 * sets the speaker's gender
	 * @param gender
	 */
	public void setGender(String gender) {
		speakerGender = gender;
	}
    
    /**
     * sets the speaker's accent
     * @param accent
     */
	public void setAccent(String accent) {
        speakerAccent = accent;
    }
    
    /**
     * sets the speaker's date of birth. Note that the
     * date is stored as a simple string without any semantic check.
     * @param date
     */
	public void setDateOfBirth(String date) {
        speakerDateOfBirth = date;
    }

	/**
	 * returns a string with tab-delimited fields. The order of fields
	 * is the speaker code, name, first name, gender, accent and date of birth
	 * @return String speaker data as string
	 */
    public String toString() {
        String s=new String();
    	String code=getCode();
    	if(code!=null){
    		s=s.concat(getCode() + "\t");
    	}
        s=s.concat(getName() + "\t" + getFirstName() + "\t" + getGender() + "\t" + getAccent() + "\t" + getDateOfBirth());
        return s;
    }


	/**
	 * returns the names of the fields for a speaker object in
	 * the currently selected user interface language.
	 * 
	 * Note that the speaker ID is not returned.
	 * 
	 * @return Vector speaker field names
	 */
    final public static Vector<String> getDescription() {
        Vector<String> v = new Vector<String>(7);
        v.addElement("ID");
        v.addElement(uiString.getString("SpeakerCode"));
        v.addElement(uiString.getString("SpeakerName"));
        v.addElement(uiString.getString("SpeakerFirstName"));
        v.addElement(uiString.getString("SpeakerGender"));
        v.addElement(uiString.getString("SpeakerAccent"));
        v.addElement(uiString.getString("SpeakerDateOfBirth"));
        return v;
    }

	/**
	 * Returns the speaker object fields in a Vector of Strings
	 *
	 * Note that the speaker ID is not returned.
	 * 
	 * @return vector with speaker fields as string
	 */
    public Vector<String> getData() {
        Vector<String> v = new Vector<String>(7);
        v.addElement(Integer.toString(getID()));
        v.addElement(getCode());
        v.addElement(getName());
        v.addElement(getFirstName());
        v.addElement(getGender());
        v.addElement(getAccent());
        v.addElement(getDateOfBirth());
        return v;
    }

	/**
	 * setData() sets the speaker object fields from a vector of String data.
	 * The vector must contain the following fields: speaker code, name,
	 * first name, sex, accent, date of birth
	 * 
	 * Note that speaker ID cannot be set this way.
	 * 
	 * @param spkData speaker fields as strings 
	 */
    public void setData(Vector<String> spkData) {
//        setSpeaker(
//            (String) spkData.elementAt(0),
//            (String) spkData.elementAt(1),
//            (String) spkData.elementAt(2),
//            (String) spkData.elementAt(3),
//            (String) spkData.elementAt(4),			
//            (String) spkData.elementAt(5)
//        );
        for (int i = 0; i < spkData.size(); i++) {
        	setSpeakerData(i, spkData.elementAt(i));
        }
    }

    /**
    *
    * generate data for log file
    *
    **/

    public void setLogEntries() {
        recLog.setLogEntry("SCD: ",String.valueOf(getID()));
        recLog.setLogEntry("AGE: ", getDateOfBirth());
        recLog.setLogEntry("ACC: ", getAccent());
        recLog.setLogEntry("SEX: ", getGender());
    }
}