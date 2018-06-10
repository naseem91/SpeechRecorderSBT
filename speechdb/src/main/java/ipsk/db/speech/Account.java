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
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Account
 */
@Entity
@Table(name = "account")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("account")
@PreferredDisplayOrder("login,password,created,userRoles,organisation,person,adminOfProjects,projects,messagesForToLogin,messagesForFromLogin")
@NamedQuery(
        name="account.byLogin",
        query="SELECT acc FROM Account acc WHERE acc.login = ?1"  
)
public class Account implements java.io.Serializable {

	// Fields    

	private String login;

	private Organisation organisation;

	private Person person;
	
	private Set<Project> projects=new HashSet<Project>(0);

	private String password;
	
	private String sha5HexPassword;

	private String rfc2307Password;
	
	@Column(name = "rfc2307_password", length = 46)
	@ResourceKey("password.rfc2307")
	public String getRfc2307Password() {
		return rfc2307Password;
	}

	public void setRfc2307Password(String rfc2307Password) {
		this.rfc2307Password = rfc2307Password;
	}


	private String strongPassword;
	//private String status;

	//private Date expires;

	

	@Column(name = "strong_password", length = 64)
	@ResourceKey("password.rfc2307")
	public String getStrongPassword() {
		return strongPassword;
	}

	public void setStrongPassword(String strongPassword) {
		this.strongPassword = strongPassword;
	}


	private Date created=new Date();
	

	private Set<UserRole> userRoles = new HashSet<UserRole>(0);

	private Set<Message> messagesForToLogin = new HashSet<Message>(0);

	private Set<Message> messagesForFromLogin = new HashSet<Message>(0);
	
	private Set<Project> adminOfProjects=new HashSet<Project>(0);

	// Constructors

	/** default constructor */
	public Account() {
		created=new Date();
		//expires=new Date(created.getTime()+1000000000);
		
	}

	/** minimal constructor */
	public Account(String login) {
		this();
		this.login = login;
	}

	
	// Property accessors
	@Id
	@Column(name = "login", unique = true, nullable = false, length = 100)
	@ResourceKey("login")
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@ManyToOne(optional=true)
	@JoinColumn(name = "organisation_id")
	@ResourceKey("organisation")
	public Organisation getOrganisation() {
		return this.organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	@OneToOne()
	@JoinColumn(name = "person_id")
	@ResourceKey("person")
	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	
	
	@Column(name = "password", length = 100)
	@ResourceKey("password")
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name = "sha5_hex_password", length = 128)
	@ResourceKey("password.sha5")
	public String getSha5HexPassword() {
		return sha5HexPassword;
	}

	public void setSha5HexPassword(String sha5HexPassword) {
		this.sha5HexPassword = sha5HexPassword;
	}

//	@Column(name = "status", length = 100)
//	@ResourceKey("status")
//	public String getStatus() {
//		return this.status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	@Column()
//	@ResourceKey("expires")
//	@Temporal(TemporalType.TIMESTAMP)
//	public Date getExpires() {
//		return this.expires;
//	}
//
//	public void setExpires(Date expires) {
//		this.expires = expires;
//	}

	@Column(name = "created",nullable=false,updatable=false)
	@ResourceKey("created")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	// Remove corresponding user roles
	@OneToMany(cascade = { CascadeType.REMOVE }, mappedBy = "account")
	@ResourceKey("user_roles")
	public Set<UserRole> getUserRoles() {
		return this.userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles) {
		this.userRoles = userRoles;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accountByToLogin")
	@ResourceKey("msgs.to")
	public Set<Message> getMessagesForToLogin() {
		return this.messagesForToLogin;
	}

	public void setMessagesForToLogin(Set<Message> messagesForToLogin) {
		this.messagesForToLogin = messagesForToLogin;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accountByFromLogin")
	@ResourceKey("msgs.from")
	public Set<Message> getMessagesForFromLogin() {
		return this.messagesForFromLogin;
	}

	public void setMessagesForFromLogin(Set<Message> messagesForFromLogin) {
		this.messagesForFromLogin = messagesForFromLogin;
	}
	@ManyToMany(mappedBy="accounts")
	@ResourceKey("projects")
	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	@ManyToMany(mappedBy="adminAccounts")
	@ResourceKey("admin.projects")
	public Set<Project> getAdminOfProjects() {
		return adminOfProjects;
	}

	public void setAdminOfProjects(Set<Project> adminOfProjects) {
		this.adminOfProjects = adminOfProjects;
	}
	
	@Transient
	public Set<Project> associatedProjects(){
		Set<Project> assProjs=new HashSet<Project>(0);
		assProjs.addAll(getProjects());
		if(person!=null){
			Set<Organisation> organisations=person.getOrganisations();
			for(Organisation orga:organisations){
				assProjs.addAll(orga.getProjects());
			}
		}else if(organisation!=null){
			return organisation.getProjects();
		}
		
		return assProjs;
	}
	
	
	@Transient
	public Set<Organisation> associatedOrganisations(){
		Set<Organisation> assOrgas=new HashSet<Organisation>(0);
		if(person!=null){
			return person.getOrganisations();
		}else if(organisation!=null){
			assOrgas.add(organisation);
		}
		return assOrgas;
	}
	
	@Transient
	public boolean hasUserRole(UserRoleId.RoleName roleName){
		for (UserRole userRole:userRoles){
			if(userRole.getId().getRoleName().equals(roleName)){
				return true;
			}
		}
		return false;
	}

	@Transient
	public String getDisplayString(){
		Person p=getPerson();
		if(p!=null){
			return getLogin()+": "+p.toString();
		}
		Organisation orga=getOrganisation();
		if(orga!=null){
			return getLogin()+": "+orga.toString();
		}
		return toString();
	}
	@Transient
	public String contactEmail(){
		Person p=getPerson();
		if(p!=null){
			return p.getEmail();
		}
		Organisation orga=getOrganisation();
		if(orga!=null){
			return orga.getEmail();
		}
		return null;
	}
	
	
	public String toString(){
		return getLogin();
	}
	
	

}
