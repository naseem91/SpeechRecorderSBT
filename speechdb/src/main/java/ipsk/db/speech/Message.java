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
import ipsk.util.annotations.TextAreaView;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A message for communication between users of the webapplication.
 *
 */
@Entity
@Table(name = "message")
@NamedQuery(
        name="message.forLogin",
        query="SELECT m FROM Message m WHERE m.accountByToLogin.login = ?1 ORDER BY m.dateSent DESC"
)
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("message")
@PreferredDisplayOrder("messageId,accountByFromLogin,subject,dateSent,status")
public class Message implements java.io.Serializable {
	
	public static final String SENT = "Sent";

	public static final String READ = "Read";

	public static final String ANSWERED = "Answered";

	// public static final String CONFIRMED="Confirmed";
	public static final String DELETED = "Deleted";

	//protected int id;

	// Fields

	private int messageId;

	private Message replyOf;

	private Account accountByToLogin;

	private Account accountByFromLogin;

	private String subject;

	private String contents;

	private Date dateSent;

	private String status;

	private String comment;

	private Date dateRead;

	private Date dateConfirmed;


	// Constructors

	/** default constructor */
	public Message() {
		super();
	}

	/** minimal constructor */
	public Message(int messageId) {
		this.messageId = messageId;
	}

	/** full constructor */
	public Message(int messageId, Message replyOf, Account accountByToLogin,
			Account accountByFromLogin, String subject, String contents,
			Date dateSent, String status, String comment, Date dateRead,
			Date dateConfirmed) {
		this.messageId = messageId;
		this.replyOf = replyOf;
		this.accountByToLogin = accountByToLogin;
		this.accountByFromLogin = accountByFromLogin;
		this.subject = subject;
		this.contents = contents;
		this.dateSent = dateSent;
		this.status = status;
		this.comment = comment;
		this.dateRead = dateRead;
		this.dateConfirmed = dateConfirmed;
		//this.messages = messages;
	}

	// Property accessors
	@Id
	@Column(name = "message_id", unique = true, nullable = false)
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getMessageId() {
		return this.messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reply_of")
	@ResourceKey("msg.reply_of")
	public Message getReplyOf() {
		return this.replyOf;
	}

	public void setReplyOf(Message message) {
		this.replyOf = message;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_login")
	@ResourceKey("to")
	public Account getAccountByToLogin() {
		return this.accountByToLogin;
	}

	public void setAccountByToLogin(Account accountByToLogin) {
		this.accountByToLogin = accountByToLogin;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_login")
	@ResourceKey("from")
	public Account getAccountByFromLogin() {
		return this.accountByFromLogin;
	}

	public void setAccountByFromLogin(Account accountByFromLogin) {
		this.accountByFromLogin = accountByFromLogin;
	}

	@Column(name = "subject",length = 1000)
	@ResourceKey("subject")
	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "contents", length = 10000)
	@TextAreaView
	@ResourceKey("message")
	public String getContents() {
		return this.contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	@Column(name = "date_sent")
	@Temporal(TemporalType.TIMESTAMP)
	@ResourceKey("date.sent")
	public Date getDateSent() {
		return this.dateSent;
	}

	public void setDateSent(Date dateSent) {
		this.dateSent = dateSent;
	}

	@Column(name = "status", length = 100)
	@ResourceKey("status")
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "comment")
	@ResourceKey("comments")
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "date_read")
	@Temporal(TemporalType.TIMESTAMP)
	@ResourceKey("date.read")
	public Date getDateRead() {
		return this.dateRead;
	}

	public void setDateRead(Date dateRead) {
		this.dateRead = dateRead;
	}

	@PrePersist
	public void send(){
		setDateSent(new Date());
		setStatus(SENT);
	}
	
	@Column(name = "date_confirmed")
	@Temporal(TemporalType.TIMESTAMP)
	@ResourceKey("date.confirmed")
	public Date getDateConfirmed() {
		return this.dateConfirmed;
	}

	public void setDateConfirmed(Date dateConfirmed) {
		this.dateConfirmed = dateConfirmed;
	}
	
	public String toString(){
		StringBuffer retBuffer=new StringBuffer();
		retBuffer.append("ID: ");
		retBuffer.append(messageId);
		if(subject!=null && !subject.equals("")){
			retBuffer.append(", ");
			retBuffer.append(subject);
		}
		return retBuffer.toString();
	}


}
