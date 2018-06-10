package ipsk.db.speech;

import java.util.HashSet;
import java.util.Set;

import ipsk.beans.PreferredDisplayOrder;

import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "localizable_message", schema = "public")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("localizable_message")
@PluralResourceKey("localizable_messages")
@PreferredDisplayOrder("language,message,*")

public class LocalizableMessage {

	private int localizableMessageId;
	private String description;
	

	private Set<LocalizedMessage> localizedMessages;
	
	private Set<Project> projectsSessionFinishedMessage=new HashSet<Project>();
	
	
	public LocalizableMessage(){
		super();
	}
	@Id
	@Column(name = "localizable_message_id", unique = true, nullable = false)
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getLocalizableMessageId() {
		return this.localizableMessageId;
	}

	public void setLocalizableMessageId(int id) {
		this.localizableMessageId = id;
	}
	
	@Column(length=1000)
	@ResourceKey("description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@OneToMany(mappedBy = "localizableMessage")
	@ResourceKey("localized_messages")
	public Set<LocalizedMessage> getLocalizedMessages() {
		return localizedMessages;
	}
	
	public void setLocalizedMessages(Set<LocalizedMessage> localizedMessages) {
		this.localizedMessages = localizedMessages;
	}

	@OneToMany(mappedBy = "sessionFinishedMessage")
	@ResourceKey("session.finishedMessage")
	public Set<Project> getProjectsSessionFinishedMessage() {
		return projectsSessionFinishedMessage;
	}
	
	public void setProjectsSessionFinishedMessage(Set<Project> projects) {
		this.projectsSessionFinishedMessage = projects;
	}
	
}
