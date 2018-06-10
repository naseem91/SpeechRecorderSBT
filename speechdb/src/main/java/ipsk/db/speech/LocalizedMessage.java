package ipsk.db.speech;

import java.util.HashSet;
import java.util.Set;

import ipsk.beans.PreferredDisplayOrder;

import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "localized_message", schema = "public")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("localized_message")
@PluralResourceKey("localized_messages")
@PreferredDisplayOrder("language,message,*")
public class LocalizedMessage {

	private int localizedMessageId;
	private String languageCodeISO639;
	private String message;
	
	private LocalizableMessage localizableMessage;
	
	
	
	public LocalizedMessage(){
		super();
	}
	@Id
	@Column(name = "localized_message_id", unique = true, nullable = false)
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getLocalizedMessageId() {
		return this.localizedMessageId;
	}

	public void setLocalizedMessageId(int id) {
		this.localizedMessageId = id;
	}
	
	@Column(length = 3, updatable = false)
	@ResourceKey("language.code")
	public String getLanguageCodeISO639() {
		return languageCodeISO639;
	}

	public void setLanguageCodeISO639(String languageCodeISO639) {
		this.languageCodeISO639 = languageCodeISO639;
	}
	@Column(length = 10000)
	@ResourceKey("loc_message")
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	@ManyToOne()
	@JoinColumn(name = "localizable_message_id")
	@ResourceKey("localizable_message")
	public LocalizableMessage getLocalizableMessage() {
		return localizableMessage;
	}
	public void setLocalizableMessage(LocalizableMessage localizableMessage) {
		this.localizableMessage = localizableMessage;
	}
	
}
