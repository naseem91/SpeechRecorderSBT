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
import ipsk.beans.dom.Temporal.Type;
import ipsk.util.EnumResourceKeys;
import ipsk.util.MemberResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;
import ipsk.util.annotations.TextAreaView;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Person
 */
@Entity
@Table(name = "person")
@Inheritance(strategy=InheritanceType.JOINED)
@PreferredDisplayOrder("personId,name,forename,address,sex,email,dateOfBirth,birthPlace,profession,*,organisations,account,comments")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
public class Person implements java.io.Serializable {
	
	// Enums 
	@EnumResourceKeys(memberResourceKeys = {
			@MemberResourceKey(name = "MALE", key = "male"),
			@MemberResourceKey(name = "FEMALE", key = "female"),
			@MemberResourceKey(name = "OTHER", key = "other") }
	)
	public enum Sex{MALE,FEMALE,OTHER}
	
	// Fields    

	protected int personId;
	
	protected String uuid;

	private String code;

	private String name;

	private String forename;

	private Sex sex;
	
	private Date dateOfBirth;

	private Date registered=new Date();

	private String address;
	
	private String street;

	private String zipcode;

	private String city;

	private String country;

	private String birthPlace;

	private String profession;

	private String comments;

	private Account account;
	
	private Set<Organisation> organisations = new HashSet<Organisation>(0);

	private String email;

	// Constructors

	/** default constructor */
	public Person() {
	    super();
	}

	/** minimal constructor */
	public Person(int personId) {
	    this();
		this.personId = personId;
	}

	/** full constructor */
	public Person(int personId, String code, String name, String forename,
			Sex sex, Date dateOfBirth, Date registered,String address, String birthPlace,
			String profession, String comments, Account account) {
		this.personId = personId;
		this.code = code;
		this.name = name;
		this.forename = forename;
		this.sex = sex;
		this.dateOfBirth = dateOfBirth;
		this.registered=registered;
		this.address = address;
		this.birthPlace = birthPlace;
		this.profession = profession;
		this.comments = comments;
		this.account = account;
	}

	// Property accessors
	@Id
	@Column(name = "person_id", unique = true, nullable = false)
	//@SequenceGenerator(name="ID_SEQ",sequenceName="id_seq")
    @GeneratedValue(generator="id_gen")
    @LinkID
    @ResourceKey("id")
    public int getPersonId() {
		return this.personId;
	}

	public void setPersonId(int personId) {
	    this.personId = personId;
	}

	@Column(length = 36)
	@ResourceKey("uuid")
	public String getUuid() {
	    return uuid;
	}

	public void setUuid(String uuid) {
	    this.uuid = uuid;
	}

	@Column(name = "code", length = 10)
	@ResourceKey("code")
	public String getCode() {
	    return this.code;
	}

	public void setCode(String code) {
	    this.code = code;
	}

	@Column(name = "name", length = 100)
	@ResourceKey("name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "forename", length = 100)
	@ResourceKey("forename")
	public String getForename() {
		return this.forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	@Column(name = "sex", length = 10)
	@Enumerated(EnumType.STRING)
	@ResourceKey("sex")
	public Sex getSex() {
		return this.sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}
	
	@Temporal(TemporalType.DATE)
	@ipsk.beans.dom.Temporal(type=Type.DATE)
	@XmlElement
	@XmlJavaTypeAdapter(XMLDateAdapter.class)
	@Column(name = "date_of_birth", length = 4)
	@ResourceKey("date_of_birth")
	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	

	@Column(name = "address", length = 1000)
	@ResourceKey("address")
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "birth_place",  length = 100)
	@ResourceKey("birthplace")
	public String getBirthPlace() {
		return this.birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	@Column(name = "profession", length = 100)
	@ResourceKey("profession")
	public String getProfession() {
		return this.profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	@Column(name = "comments")
	@ResourceKey("comments")
	@TextAreaView
	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	@Column()
	@Temporal(TemporalType.TIMESTAMP)
	@ResourceKey("registered")
	public Date getRegistered() {
		return this.registered;
	}

	public void setRegistered(Date registered) {
		this.registered=registered;
	}
	
	@OneToOne(mappedBy = "person")
	@ResourceKey("account")
	@XmlTransient
	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@ManyToMany(mappedBy="persons", fetch = FetchType.LAZY)
	@ResourceKey("organisations")
	public Set<Organisation> getOrganisations() {
		return this.organisations;
	}

	public void setOrganisations(Set<Organisation> organisations) {
		this.organisations = organisations;
	}
	
	private String toWelcomeName(){
		if(name!=null && !"".equals(name) && forename != null && !"".equals(forename)){
			return name+" "+forename;
		}
		if(name!=null && !"".equals(name))return name;
		if(forename!=null && ! "".equals(forename))return forename;
		return null;
	}
	
	public String toString(){
		String welcomeName=toWelcomeName();
		if(welcomeName!=null){
			return welcomeName;
		}
		return "ID: "+Integer.toString(personId);
		
	}
	/**
	 * Returns name for welcome pages.
	 * Does not return the ID, if the person is anonymous (no name or forename) 
	 * @return welcome string
	 */
	@Transient
	public String getWelcomeName(){
		String welcomeName=toWelcomeName();
		if(welcomeName!=null){
			return welcomeName;
		}
		return "";
		
	}
	
	@Column(length = 1000)
	@ResourceKey("street")
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}
	
	@Column(length = 100)
	@ResourceKey("zip_code")
	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	
	@Column(length = 100)
	@ResourceKey("city")
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	@Column(length = 100)
	@ResourceKey("country")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	@Column(length = 1000)
	@ResourceKey("email")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
