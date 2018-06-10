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
import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Technician
 */
@Entity
@Table(name = "technician")
@PrimaryKeyJoinColumn(name="person_id")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("technician")
@PluralResourceKey("technicians")
public class Technician extends Person{

	// Fields    

	//private int personId;

	private String email;

	private String mobile;

	//private String login;

	private Set<RecordingFile> recordingFiles = new HashSet<RecordingFile>(0);

	

	// Constructors

	/** default constructor */
	public Technician() {
	}

	/** minimal constructor */
	public Technician(int personId) {
		this.personId = personId;
	}

	/** full constructor */
	public Technician(int personId, String email, String mobile,
			Set<RecordingFile> recordingFiles) {
		this.personId = personId;
		this.email = email;
		this.mobile = mobile;	
		this.recordingFiles = recordingFiles;

	}

	@Column(name = "email", length = 100)
	@ResourceKey("email")
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "mobile", length = 100)
	@ResourceKey("phone.mobile")
	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

//	@Column(name = "login", length = 100)
//	public String getLogin() {
//		return this.login;
//	}
//
//	public void setLogin(String login) {
//		this.login = login;
//	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		        name="supervises",
		        joinColumns={@JoinColumn(name="technician_id")},
		        inverseJoinColumns={@JoinColumn(name="recording_id")}
		    )
	@ResourceKey("recording_files")
	public Set<RecordingFile> getRecordingFiles() {
		return this.recordingFiles;
	}

	public void setRecordingFiles(Set<RecordingFile> recordingFiles) {
		this.recordingFiles = recordingFiles;
	}

	

}
