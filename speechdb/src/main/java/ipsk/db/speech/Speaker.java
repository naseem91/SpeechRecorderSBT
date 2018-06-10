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

package ipsk.db.speech;



import ipsk.beans.PreferredDisplayOrder;
import ipsk.beans.Unit;
import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMElements;
import ipsk.beans.dom.DOMRoot;
import ipsk.beans.validation.Input;
import ipsk.persistence.ObjectImmutableIfReferenced;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.util.ArrayList;
import java.util.HashSet;
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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Speaker
 */
@Entity
@Table(name = "speaker")
@PrimaryKeyJoinColumn(name="person_id")
@DOMRoot
@ResourceBundleName("ipsk.db.speech.PropertyNames")
// Changed sequence in FROM clause
// See http://forum.java.sun.com/thread.jspa?threadID=780197
// TopLink bug ?
//@NamedQuery(
//	        name="speakers.byOrganisation",
//	        query="SELECT speaker FROM Speaker speaker,Organisation orga WHERE orga = ?1 AND speaker MEMBER OF orga.persons"  
//)
// OK this works :)
@NamedQueries(value={
@NamedQuery(
        name="speakers.byOrganisation",
        query="SELECT spk FROM Organisation orga,Speaker spk WHERE orga = ?1 AND spk MEMBER OF orga.persons"  
),

@NamedQuery(
        name="speakers.byOrganisation.orderedByRegisteredDesc",
        query="SELECT spk FROM Organisation orga,Speaker spk WHERE orga = ?1 AND spk MEMBER OF orga.persons ORDER BY spk.registered DESC"  
) })
@DOMAttributes("personId")
@DOMElements({"name","forename","address","sex","dateOfBirth","birthPlace","profession","dialectRegion","additionalLanguage","comments"})
@PreferredDisplayOrder("name,forename,address,sex,dateOfBirth,height,weight,smoker,brace,mouthPiercing,birthPlace,zipcode,profession,dialectRegion,motherTongue,motherTongueMother,motherTongueFather,additionalLanguage,comments")
public class Speaker extends Person {
	public final static String ELEMENT_NAME="speaker";
	// Fields    

	//private int personId;

	// Renamed from "code" to "speakerCode" because openJPA 1.0.2 seems to use this field
	// Renamed to code again, do not use openJPA !!
	private String code;

	private String accent;

	private Boolean smoker;

	
	private Double height;

	private String motherTongue;

	private DialectRegion dialectRegion;
	
	private Double weight;

	private Boolean brace;

	private Boolean mouthPiercing;

	private String motherTongueMother;

	private String motherTongueFather;
	
	private String additionalLanguage;
	
	private String musicalInstrument;

	

	private String type;

	private Set<Session> sessions = new HashSet<Session>(0);

	// Constructors

	/** default constructor */
	public Speaker() {
		super();
	}

	/** minimal constructor */
	public Speaker(int personId) {
	    this();
		this.personId = personId;
	}

	

//	@Id
//	@Column(name = "person_id", unique = true, nullable = false, insertable = true, updatable = true)
//	//@SequenceGenerator(name="ID_SEQ",sequenceName="id_seq")
//    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ID_SEQ")
//    @ResourceKey("id")
//    public int getPersonId() {
//		return this.personId;
//	}
//
//	public void setPersonId(int personId) {
//		this.personId = personId;
//	}

	@Column(name = "code", length = 10)
	 @ResourceKey("code")
	 public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "accent",  length = 100)
	 @ResourceKey("accent")
	 public String getAccent() {
		return this.accent;
	}

	public void setAccent(String accent) {
		this.accent = accent;
	}

	@Column(name = "smoker")
	@ResourceKey("smoker")
	public Boolean getSmoker() {
		return this.smoker;
	}

	public void setSmoker(Boolean smoker) {
		this.smoker = smoker;
	}

	
	@Column(name = "mother_tongue", length = 100)
	@ResourceKey("mother_tongue")
	public String getMotherTongue() {
		return this.motherTongue;
	}

	public void setMotherTongue(String motherTongue) {
		this.motherTongue = motherTongue;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dialect_region")
	@ResourceKey("dialect_region")
	public DialectRegion getDialectRegion() {
		return this.dialectRegion;
	}

	public void setDialectRegion(DialectRegion dialectRegion) {
		this.dialectRegion = dialectRegion;
	}

	
	@Column(name = "brace")
	@ResourceKey("brace")
	public Boolean getBrace() {
		return this.brace;
	}

	public void setBrace(Boolean brace) {
		this.brace = brace;
	}

	@Column(name = "mouth_piercing")
	@ResourceKey("mouth_piercing")
	public Boolean getMouthPiercing() {
		return this.mouthPiercing;
	}

	public void setMouthPiercing(Boolean mouthPiercing) {
		this.mouthPiercing = mouthPiercing;
	}

	@Column(name = "mother_tongue_mother", length = 100)
	@ResourceKey("mother_tongue_of_mother")
	public String getMotherTongueMother() {
		return this.motherTongueMother;
	}

	public void setMotherTongueMother(String motherTongueMother) {
		this.motherTongueMother = motherTongueMother;
	}

	@Column(name = "mother_tongue_father", length = 100)
	@ResourceKey("mother_tongue_of_father")
	public String getMotherTongueFather() {
		return this.motherTongueFather;
	}

	public void setMotherTongueFather(String motherTongueFather) {
		this.motherTongueFather = motherTongueFather;
	}

	@Column(name = "type", length = 8)
	@ResourceKey("type")
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "speakers")
//	@JoinTable(
//		        name="participates",
//		        joinColumns={@JoinColumn(name="speaker_id")},
//		        inverseJoinColumns={@JoinColumn(name="session_id")}
//		    )
	@ResourceKey("sessions")
	@ObjectImmutableIfReferenced
	public Set<Session> getSessions() {
		return this.sessions;
	}

	public void setSessions(Set<Session> sessions) {
		this.sessions = sessions;
	}
	
	@Unit("m")
	@Column(name = "height",precision = 8)
	@ResourceKey("height")
	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}
	
	@Unit("kg")
	@Column(name = "weight",precision = 8)
	@ResourceKey("weight")
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public void setAdditionalLanguage(String additionalLanguage) {
		this.additionalLanguage = additionalLanguage;
	}

	@Column(name = "additional_language",length=100)
	@ResourceKey("language.additional")
	@Input(required=false)
	public String getAdditionalLanguage() {
		return additionalLanguage;
	}
	
	@Column
	@ResourceKey("musical_instrument")
	@Input(required=false)
	public String getMusicalInstrument() {
		return musicalInstrument;
	}

	public void setMusicalInstrument(String musicalInstrument) {
		this.musicalInstrument = musicalInstrument;
	}
	
	public Element toElement(Document d){
		Element e=d.createElement(ELEMENT_NAME);
        e.setAttribute("personId", Integer.toString(personId));
        if(code!=null){
        	Element codeElement=d.createElement("code");
        	codeElement.setTextContent(code);
        	e.appendChild(codeElement);
        }
        if(code!=null){
        	Element codeElement=d.createElement("code");
        	codeElement.setTextContent(code);
        	e.appendChild(codeElement);
        }
        
        
		return e;
	}
	
	
}
