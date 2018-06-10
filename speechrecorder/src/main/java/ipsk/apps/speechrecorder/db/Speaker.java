//    IPS Java Speech Database
//    (c) Copyright 2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Speech Database
//
//
//    IPS Java Speech Database is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Speech Database is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Speech Database.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.apps.speechrecorder.db;



import ipsk.beans.PreferredDisplayOrder;
import ipsk.beans.Unit;
import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMElements;
import ipsk.beans.dom.DOMRoot;
import ipsk.beans.validation.Input;
import ipsk.persistence.ObjectImmutableIfReferenced;
import ipsk.text.table.ColumnDescriptor;
import ipsk.text.table.TableExportProvider;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


@DOMAttributes("personId")
@DOMElements({"name","forename","address","sex","dateOfBirth","birthPlace","profession","dialectRegion","additionalLanguage","comments"})
@PreferredDisplayOrder("name,forename,address,sex,dateOfBirth,height,weight,smoker,brace,mouthPiercing,birthPlace,zipcode,profession,dialectRegion,motherTongue,motherTongueMother,motherTongueFather,additionalLanguage,comments")
//@XmlType(name="speaker",propOrder={"name","forename","address","sex","dateOfBirth","height","weight","smoker","brace","mouthPiercing","birthPlace","zipcode","profession","dialectRegion","motherTongue","motherTongueMother","motherTongueFather","additionalLanguage","comments"})
@XmlType(name="speaker",namespace="speechrecorder")
public class Speaker extends ipsk.db.speech.Speaker{
	public final static String ELEMENT_NAME="speaker";

	
	// backward compatibility to Speechrecorder speakers.txt file
    private String gender;
    
    // backward compatibility to Speechrecorder speakers.txt file
    private String dateOfBirthString;
    
    private DateFormat dateOfBirthFormat=DateFormat.getDateInstance();
    
	public Speaker() {
		super();
		
	}

	/** minimal constructor */
	public Speaker(int personId) {
	    super(personId);
	
	}

    public String getGender() {
        Sex s=getSex();
        if(s!=null){
            return s.toString();
        }
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirthString() {
        Date dob=getDateOfBirth();
        if(dob!=null){
            return dateOfBirthFormat.format(dob);
        }
        return dateOfBirthString;
    }

    public void setDateOfBirthString(String dateOfBirthString) {
        this.dateOfBirthString = dateOfBirthString;
    }

   

}
