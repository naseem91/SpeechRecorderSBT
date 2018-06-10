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
import ipsk.util.annotations.TextAreaView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * Recording Session 
 */
@Entity
@Table(name = "session")
@NamedQueries(value = { 
@NamedQuery(
        name="session.bySpeaker",
        query="SELECT sess FROM Session sess WHERE ?1 MEMBER OF sess.speakers"
       
),
@NamedQuery(
        name="sessions.forProject",
        query="SELECT s FROM Session s WHERE s.project = ?1"
       
)
})
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@PreferredDisplayOrder("sessionId,project,status,date,type,script,code,speakers,recordingFiles,comment")
@DOMAttributes("sessionId")
@DOMElements({"date","code","speakers","script","comment"})
@XmlType(name="session")
public class Session implements java.io.Serializable {
	
	public enum Status{CREATED,STARTED,CONNECTED,PAUSED,FINISHED}
	
	@XmlType(name="sessionType")
//	@XmlEnum
	public enum Type{TEST,SINE_TEST,NORM}
	
	public static enum LogLevel{SEVERE,WARNING,INFO,CONFIG,FINE,FINER,FINEST};
	
	// 
//	public final static String TYPE_NORM="NORM";
//	public final static String TEST="TEST";
//	public final static String TYPE_SINE_TEST="SINE_TEST";

	// Fields    

	private int sessionId;
	
	private Script script;
	
	private Project project;
	
	private Organisation organisation;
	
	private String code;

	private String environment;

	private String comment;

	private Date date;

	private Type type;

	private Status status=Status.CREATED;
	
	private String httpSessionId;
	
	private String storageDirectoryURL;
	
//	private LogLevel logLevel;



	private Set<RecordingFile> recordingFiles = new HashSet<RecordingFile>(0);

	private Set<Speaker> speakers = new HashSet<Speaker>(0);

	// Constructors

	/** default constructor */
	public Session() {
		super();
	}

	/** minimal constructor */
	public Session(int sessionId) {
		this.sessionId = sessionId;
	}

	

	// Property accessors
	@Id
	@Column(name = "session_id", unique = true, nullable = false)
	//@SequenceGenerator(name="ID_SEQ",sequenceName="id_seq")
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
   
    public int getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}
	
//	// XMLID only accepts Strings
//	@XmlID
//	@Transient
//	public String getSessionIdString() {
//		return Integer.toString(this.sessionId);
//	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "script_id")
	@ResourceKey("script")
	@XmlTransient
	public Script getScript() {
		return this.script;
	}

	public void setScript(Script script) {
		this.script = script;
	}
	
	@Column(name = "code", length = 10)
	@ResourceKey("code")
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "environment", length = 100)
	@ResourceKey("environment")
	public String getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	@Column(name = "comment", length = 1000)
	@ResourceKey("comments")
	@TextAreaView
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@ManyToOne( fetch = FetchType.LAZY)
	@JoinColumn(name = "project")
	@ResourceKey("project")
	@XmlTransient
	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Column(updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	@ResourceKey("point_in_time")
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}


	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "session")
	@ResourceKey("recording_files")
	@XmlTransient
	public Set<RecordingFile> getRecordingFiles() {
		return this.recordingFiles;
	}

	public void setRecordingFiles(Set<RecordingFile> recordingFiles) {
		this.recordingFiles = recordingFiles;
	}

	//@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "sessions")
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		        name="participates",
		        joinColumns={@JoinColumn(name="session_id")},
		        inverseJoinColumns={@JoinColumn(name="speaker_id")}
		    )
	@DOMCollectionElement(collectionElementName="speaker")
    @ResourceKey("participants")
    @XmlTransient
	public Set<Speaker> getSpeakers() {
		return this.speakers;
	}

	public void setSpeakers(Set<Speaker> speakers) {
		this.speakers = speakers;
	}
	
	
	
	@Column(name = "type", length = 10)
	@Enumerated(EnumType.STRING)
	@ResourceKey("type")
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	@Column(name = "status", length = 10)
	@Enumerated(EnumType.STRING)
	@ResourceKey("status")
	@XmlTransient
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * The HTTP web session ID
	 * Used for authentication.
	 * @return HTTP session ID
	 */
	@Column(name="jsessionid",length = 100)
	@XmlTransient
	public String getHttpSessionId() {
		return httpSessionId;
	}
	
	/**
	 * Set HTTP session ID.
	 * If a new web recording session is created the current HTTP session ID of the
	 * authenticated user is stored here. If the user logs out, the  web application is 
	 * redeployed or the server is restarted the web recorder can still upload data, though
	 * the corresponding HTTP session is invalidated.
	 * @param httpSessionId HTTP session ID
	 */
	public void setHttpSessionId(String httpSessionId) {
		this.httpSessionId = httpSessionId;
	}
	
	@PrePersist
	public void setCurrentDate(){
		if(date==null){
			setDate(new Date());
		}
	}
	
	
	public String toString(){
		return "Session: "+sessionId;
	}
	
	@Column(name = "storage_dir_url", length = 1000)
	@ResourceKey("storage.dir")
	public String getStorageDirectoryURL() {
		return storageDirectoryURL;
	}

	public void setStorageDirectoryURL(String storageDirectoryURL) {
		this.storageDirectoryURL = storageDirectoryURL;
	}
	
//	@Column(name = "log_level", length = 10)
//	@Enumerated(EnumType.STRING)
//	@ResourceKey("logging.level")
//	public LogLevel getLogLevel() {
//		return logLevel;
//	}
//
//	public void setLogLevel(LogLevel logLevel) {
//		this.logLevel = logLevel;
//	}
	
	@ManyToOne()
	@JoinColumn(name = "organisation_id")
	@ResourceKey("organisation")
	@XmlTransient
	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
	/**
	 * Returns amximum recording level of all recording items.
	 * Training sections are ignored. 
	 * @return Maximum level 0.0...1.0
	 */
	@Transient
//	@XmlTransient
	public Double getMaxLevel(){
		Double maxLevel=null;
		Set<RecordingFile> recordingFiles=getRecordingFiles();
		if(recordingFiles!=null){
			for(RecordingFile rf:recordingFiles){
				Recording r=rf.getRecording();
				if(r!=null){
					Section section=r.getSection();
					if(section!=null && section.isTraining()){
						continue;
					}
				}
				Double rfML=rf.getMaxLevel();
				if(rfML!=null){
				if(maxLevel!=null){
					if(rfML > maxLevel)maxLevel=rfML;
				}else{
					maxLevel=rfML;
				}
				}
			}
		}
		return maxLevel;
	}
	
	/**
	 * Returns missing (not yet recorded) recording items.
	 * Training sections are ignored. 
	 * @return list of missing recording items
	 */
	@Transient
//	@XmlTransient
	public List<Recording> getMissingRecordingItems(){
		ArrayList<Recording> missingRecordingItems=new ArrayList<Recording>();
		
		// add all recording items of script sorted
		Script script=getScript();
		if(script==null)return null;
//		Section[] sections=script.getSections();
		List<Section> sections=script.getSections();
		for(Section s:sections){
			// Ignore training sections
			if(!s.isTraining()){
//			PromptItem[] pis=s.getPromptItems();
			List<PromptItem> pis=s.getPromptItems();
			for(PromptItem pi:pis){
				if(pi instanceof Recording){
					missingRecordingItems.add((Recording)pi);
				}
			}
			}
		}
		
		// remove recorded items
		Set<RecordingFile> rfs=getRecordingFiles();
		for(RecordingFile rf:rfs){
			Recording r=rf.getRecording();
			missingRecordingItems.remove(r);
		}
		
		return missingRecordingItems;
	}

	
	
}
