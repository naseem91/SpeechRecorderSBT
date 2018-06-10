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
import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMCollectionElement;
import ipsk.beans.dom.DOMElements;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Project
 */

@Entity
@Table(name = "project", schema = "public")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@PreferredDisplayOrder("name,description,hosts,organisations,dialectRegions,audioDevices,sessionFinishedMessage,*")
@DOMAttributes({"name"})
@DOMElements({"description","audioFormat","sessionCode","organisations"})
@XmlRootElement
public class Project implements java.io.Serializable {

	// Fields    

	private String name;
	
	private String contextPath;

	private String description;

	private String hosts;
	
	private AudioFormat audioFormat;
	
	private String sessionCode;
	
	private Set<Account> adminAccounts=new HashSet<Account>(0);
	
	private Set<Account> accounts=new HashSet<Account>(0);

	private Set<Session> sessions = new HashSet<Session>(0);

	private Set<DialectRegion> dialectRegions = new HashSet<DialectRegion>(0);
	
	private List<AudioDevice> audioDevices = new ArrayList<AudioDevice>(0);
//	private Set<AudioDevice> audioDevices=new HashSet<AudioDevice>(0);
	
	private Set<Organisation> organisations = new HashSet<Organisation>(0);
	
	private Set<Script> scripts = new HashSet<Script>(0);
	
	private boolean speakerWindowShowStopRecordAction=true;
	
	private LocalizableMessage sessionFinishedMessage=null;
	

	
	
	//private Set<TypedPropertyDescriptor> immediateAnnotations=new HashSet<TypedPropertyDescriptor>(0);
	

	
	
//	private Set<FormConfiguration> formConfigurations=new HashSet<FormConfiguration>();
//	@ManyToMany
//	@ResourceKey("form.configurations")
//	public Set<FormConfiguration> getFormConfigurations() {
//		return formConfigurations;
//	}
//
//	public void setFormConfigurations(Set<FormConfiguration> formConfigurations) {
//		this.formConfigurations = formConfigurations;
//	}
	
	private FormConfiguration speakerFormConfiguration;
	@ManyToOne
	@ResourceKey("speaker.form.configuration")
	public FormConfiguration getSpeakerFormConfiguration() {
		return speakerFormConfiguration;
	}

	public void setSpeakerFormConfiguration(
			FormConfiguration speakerFormConfiguration) {
		this.speakerFormConfiguration = speakerFormConfiguration;
	}

	/** default constructor */
	public Project() {
		super();
	}

	/** minimal constructor */
	public Project(String name) {
		this.name = name;
	}

	
	// Property accessors
	@Id
	@Column(name = "name", unique = true, nullable = false, length = 100)
	@ResourceKey("name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", length = 1000)
	@ResourceKey("description")
	@ipsk.util.annotations.TextAreaView
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "hosts")
	@ResourceKey("hosts")
	public String getHosts() {
		return this.hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	@OneToMany( fetch = FetchType.LAZY, mappedBy = "project")
	@ResourceKey("sessions")
//	@XmlIDREF
	public Set<Session> getSessions() {
		return this.sessions;
	}

	public void setSessions(Set<Session> sessions) {
		this.sessions = sessions;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		        name="project_audio_device",
		        joinColumns={@JoinColumn(name="project")},
		        //inverseJoinColumns={@JoinColumn(name="ad_name",referencedColumnName="name"),@JoinColumn(name="ad_is_playback",referencedColumnName="is_playback"),@JoinColumn(name="ad_api",referencedColumnName="api"),@JoinColumn(name="ad_regex",referencedColumnName="regex")}
		        inverseJoinColumns={@JoinColumn(name="audio_device_id",referencedColumnName="audio_device_id")}
		        )
    @ResourceKey("audio.devices")
    @OrderColumn
    @XmlTransient
	public List<AudioDevice> getAudioDevices() {
		return this.audioDevices;
	}


// @OrderColumn does not work with a ManyToMany relationship with EclipseLink JPA
// see 
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=309039
//
// Storage of the ordered list works, but the SQL SELECT command for reading from DB does not use an ORDER BY clause	
	
//	/**
//	 * Get list of audio devices
//	 * @return
//	 */
//	@ManyToMany(fetch = FetchType.EAGER)
//	@JoinTable(
//		        name="project_audio_device",
//		        joinColumns={@JoinColumn(name="project")},
//		        inverseJoinColumns={@JoinColumn(name="ad_name",referencedColumnName="name"),@JoinColumn(name="ad_is_playback",referencedColumnName="is_playback"),@JoinColumn(name="ad_api",referencedColumnName="api"),@JoinColumn(name="ad_regex",referencedColumnName="regex")})
//	@OrderColumn(name="index",insertable=true,updatable=true)	        
//    @ResourceKey("audio.devices")
//	public List<AudioDevice> getAudioDevices() {
//		return this.audioDevices;
//	}
//
//	public void setAudioDevices(List<AudioDevice> audioDevices) {
//		this.audioDevices = audioDevices;
//	}
	
	
	private List<AudioDevice> filterAudioDeviceList(Collection<AudioDevice> adColl,boolean hasJExt,boolean isRegex){
		ArrayList<AudioDevice> filteredList=new ArrayList<AudioDevice>();
		for(AudioDevice ad:adColl){
			String jExt=ad.getJextension();
			boolean devHasJExt=(jExt!=null && ! jExt.equals(""));
//			boolean devIsRegex=ad.getId().isRegex();
			boolean devIsRegex=ad.isRegex();
			if((devHasJExt == hasJExt) && (devIsRegex == isRegex)){
				filteredList.add(ad);
			}
		}
		return filteredList;
	}
//// Workaround for EclipseLink bug:
//// Order the devices: First order rule is:
////	First devices which need an extension (e.g. DSJavaSound).
////	Next rule:
////	Non regular expressions first, then regular expressions named devices.	
//	
//	@Transient
//	public List<AudioDevice> getSpeechrecorderOrderedAudioDevices(){
//		Set<AudioDevice> aDevSet=getAudioDevices();
//		ArrayList<AudioDevice> aDevList=new ArrayList<AudioDevice>();
//		
//		aDevList.addAll(filterAudioDeviceList(aDevSet, true, false));
//		aDevList.addAll(filterAudioDeviceList(aDevSet, true, true));
//		aDevList.addAll(filterAudioDeviceList(aDevSet, false, false));
//		aDevList.addAll(filterAudioDeviceList(aDevSet, false, true));
//		return aDevList;
//	}
	
	public void setAudioDevices(List<AudioDevice> audioDevices) {
		this.audioDevices = audioDevices;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		        name="project_dialect_region",
		        joinColumns={@JoinColumn(name="project")},
		        inverseJoinColumns={@JoinColumn(name="dialect_region")}
		    )
    @ResourceKey("dialect_regions")
    @XmlTransient
	public Set<DialectRegion> getDialectRegions() {
		return this.dialectRegions;
	}

	public void setDialectRegions(Set<DialectRegion> dialectRegions) {
		this.dialectRegions = dialectRegions;
	}
	
	public String toString(){
		return name;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		        name="project_organisations",
		        joinColumns={@JoinColumn(name="project")},
		        inverseJoinColumns={@JoinColumn(name="organisation")}
		    )
	@DOMCollectionElement(collectionElementName="organisation")
    @ResourceKey("organisations")
    @XmlTransient
	public Set<Organisation> getOrganisations() {
		return organisations;
	}

	public void setOrganisations(Set<Organisation> organisations) {
		this.organisations = organisations;
	}

	@Column(name = "context_path",length = 20)
	@ResourceKey("context.path")
	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "audioformat")
	@ResourceKey("audio.format")
	@XmlTransient
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	public void setAudioFormat(AudioFormat audioFormat) {
		this.audioFormat = audioFormat;
	}
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		        name="project_scripts",
		        joinColumns={@JoinColumn(name="project")},
		        inverseJoinColumns={@JoinColumn(name="script")}
		    )
    @ResourceKey("scripts")
    @XmlTransient
	public Set<Script> getScripts() {
		return scripts;
	}

	public void setScripts(Set<Script> scripts) {
		this.scripts = scripts;
	}
	
	@ManyToMany()
	@JoinTable(
		        name="project_account",
		        joinColumns={@JoinColumn(name="project")},
		        inverseJoinColumns={@JoinColumn(name="account")}
		    )
    @ResourceKey("accounts")
    @XmlTransient
	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}
	
	@ManyToMany()
	@JoinTable(
		        name="project_admin_account",
		        joinColumns={@JoinColumn(name="project")},
		        inverseJoinColumns={@JoinColumn(name="admin_account")}
		    )
    @ResourceKey("accounts.admin")
    @XmlTransient
	public Set<Account> getAdminAccounts() {
		return adminAccounts;
	}

	public void setAdminAccounts(Set<Account> adminAccounts) {
		this.adminAccounts = adminAccounts;
	}
	
	@Column(name = "session_code", length = 100)
	@ResourceKey("session.code")
	public String getSessionCode() {
		return sessionCode;
	}

	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

	@Column(name = "show_stop_record_button")
	@ResourceKey("prompter.showStopRecordButton")
	public boolean isSpeakerWindowShowStopRecordAction() {
		return speakerWindowShowStopRecordAction;
	}

	public void setSpeakerWindowShowStopRecordAction(
			boolean speakerWindowShowStopRecordAction) {
		this.speakerWindowShowStopRecordAction = speakerWindowShowStopRecordAction;
	}
		
//	public Set<TypedPropertyDescriptor> getImmediateAnnotations() {
//		return immediateAnnotations;
//	}
//
//	public void setImmediateAnnotations(
//			Set<TypedPropertyDescriptor> immediateAnnotations) {
//		this.immediateAnnotations = immediateAnnotations;
//	}

	@ManyToOne()
	@JoinColumn(name = "session_finished_l_msg_id")
    @ResourceKey("session.finishedMessage")
	public LocalizableMessage getSessionFinishedMessage() {
		return sessionFinishedMessage;
	}

	public void setSessionFinishedMessage(LocalizableMessage sessionFinishedMessage) {
		this.sessionFinishedMessage = sessionFinishedMessage;
	}
	
//	@OneToOne()
//	@JoinColumn(name = "speaker_formconfiguration_id")
//    @ResourceKey("speaker.form.configuration")
//	public FormConfiguration getSpeakerFormConfiguration() {
//		return speakerFormConfiguration;
//	}
//
//	public void setSpeakerFormConfiguration(
//			FormConfiguration speakerFormConfiguration) {
//		this.speakerFormConfiguration = speakerFormConfiguration;
//	}
}
