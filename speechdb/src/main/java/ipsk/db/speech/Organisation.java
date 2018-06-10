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


import ipsk.beans.LinkID;
import ipsk.beans.PreferredDisplayOrder;
import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMElements;
import ipsk.beans.dom.DOMRoot;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;
import ipsk.util.annotations.TextAreaView;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Organisation
 */
@Entity
@Table(name = "organisation")
@DOMRoot
@DOMAttributes("organisationId")
@DOMElements({"name","type","street","zipcode","city","country","uri","telephone","fax","email","note"})
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@PreferredDisplayOrder("organisationId,name,street,zipcode,city,country,telephone,fax,email,dialectRegion,recordingEquipmentNr,waitForCompleteUpload,*,note")
public class Organisation implements java.io.Serializable{

	// Fields    

	private int organisationId;

	private String name="";

	private String type="";

	private String street="";

	private String zipcode="";

	private String city="";

	private String country="";

	private String uri="";

	private String telephone="";

	private String fax="";

	private String dialectRegion="";

	private String email="";

	private String note="";

	private Boolean waitForCompleteUpload=true;
	
	private Integer transferRateLimit=null;

	private Integer recordingEquipmentNr=-1;
	
	private String baseDN;

	

	private Set<Account> accounts=new HashSet<Account>(0);

	private Set<Person> persons = new HashSet<Person>(0);

	private Set<Project> projects=new HashSet<Project>(0);
	
	private Set<Session> sessions=new HashSet<Session>(0);

	// Constructors

	/** default constructor */
	public Organisation() {
	}

	/** minimal constructor */
	public Organisation(int organisationId) {
		this.organisationId = organisationId;
	}

	

	// Property accessors
	@Id
	@Column(name = "organisation_id", unique = true)
	//@SequenceGenerator(name="ID_SEQ",sequenceName="ID_SEQ")
    @GeneratedValue(generator="id_gen")
    @LinkID
    @ResourceKey("id")
    public int getOrganisationId() {
		return this.organisationId;
	}

	public void setOrganisationId(int organisationId) {
		this.organisationId = organisationId;
	}

	@Column(length = 100)
	@ResourceKey("name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 100)
	@ResourceKey("type")
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(length = 100)
	@ResourceKey("street")
	public String getStreet() {
		return this.street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Column(length = 10)
	@ResourceKey("zip_code")
	public String getZipcode() {
		return this.zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	@Column(length = 100)
	@ResourceKey("city")
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(length = 100)
	@ResourceKey("country")
	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(length = 1000)
	@ResourceKey("uri")
	public String getUri() {
		return this.uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Column(length = 100)
	@ResourceKey("phone")
	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Column(length = 100)
	@ResourceKey("fax")
	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(name = "dialect_region", length = 100)
	@ResourceKey("dialect_region")
	public String getDialectRegion() {
		return this.dialectRegion;
	}

	public void setDialectRegion(String dialectRegion) {
		this.dialectRegion = dialectRegion;
	}

	@Column(length = 1000)
	@ResourceKey("email")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column()
	@ResourceKey("note")
	@TextAreaView
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = "wait_for_complete_upload")
	@ResourceKey("wait_for_complete_upload")
	public Boolean getWaitForCompleteUpload() {
		return this.waitForCompleteUpload;
	}

	public void setWaitForCompleteUpload(Boolean waitForCompleteUpload) {
		this.waitForCompleteUpload = waitForCompleteUpload;
	}
	
	@Column(name = "transfer_rate_limit")
	@ResourceKey("transfer_rate_limit")
	public Integer getTransferRateLimit() {
		return transferRateLimit;
	}

	public void setTransferRateLimit(Integer transferRateLimit) {
		this.transferRateLimit = transferRateLimit;
	}
	
	@Column(name = "recording_equipment_nr")
	@ResourceKey("recording_equipment_nr")
	public Integer getRecordingEquipmentNr() {
		return this.recordingEquipmentNr;
	}

	public void setRecordingEquipmentNr(Integer recordingEquipmentNr) {
		this.recordingEquipmentNr = recordingEquipmentNr;
	}
	
	@Column(name = "base_dn", length = 1000)
	public String getBaseDN() {
		return baseDN;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}

	@OneToMany( cascade=CascadeType.REMOVE,fetch = FetchType.EAGER, mappedBy = "organisation")
	@ResourceKey("accounts")
	@XmlTransient
	public Set<Account> getAccounts() {
		return this.accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}


	@ManyToMany( cascade=CascadeType.MERGE,fetch = FetchType.LAZY)
	// owning side
	@JoinTable(
		        name="belongs_to",
		        joinColumns={@JoinColumn(name="organisation_id")},
		        inverseJoinColumns={@JoinColumn(name="person_id")}
		    )
	@ResourceKey("persons")
	public Set<Person> getPersons() {
		return this.persons;
	}

	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}
	
	@ManyToMany(mappedBy="organisations",fetch = FetchType.EAGER)
	// inverse side
	@ResourceKey("projects")
	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	

	public String toString(){
		if (name!=null)return name;
		return "Organisation: "+getOrganisationId();
	}

	@OneToMany( fetch = FetchType.LAZY, mappedBy = "organisation")
	@ResourceKey("sessions")
	public Set<Session> getSessions() {
		return sessions;
	}

	public void setSessions(Set<Session> sessions) {
		this.sessions = sessions;
	}



	
	

}
