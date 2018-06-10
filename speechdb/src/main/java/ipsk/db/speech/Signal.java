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


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Media Signal
 */
@Entity
@Table(name = "signal")
public class Signal implements java.io.Serializable {

	// Fields    

	private int signalId;

	private Integer signalData;

	private Set<RecordingFile> recordingFiles = new HashSet<RecordingFile>(0);

	// Constructors

	/** default constructor */
	public Signal() {
	}

	/** minimal constructor */
	public Signal(int signalId) {
		this.signalId = signalId;
	}

	/** full constructor */
	public Signal(int signalId, Integer signalData,
			Set<RecordingFile> recordingFiles) {
		this.signalId = signalId;
		this.signalData = signalData;
		this.recordingFiles = recordingFiles;
	}

	// Property accessors
	@Id
	@Column(name = "signal_id", unique = true, nullable = false)
	@GeneratedValue(generator="id_gen")
	public int getSignalId() {
		return this.signalId;
	}

	public void setSignalId(int signalId) {
		this.signalId = signalId;
	}

	@Column(name = "signal_data")
	public Integer getSignalData() {
		return this.signalData;
	}

	public void setSignalData(Integer signalData) {
		this.signalData = signalData;
	}

	@OneToMany(cascade = { CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "signal")
	public Set<RecordingFile> getRecordingFiles() {
		return this.recordingFiles;
	}

	public void setRecordingFiles(Set<RecordingFile> recordingFiles) {
		this.recordingFiles = recordingFiles;
	}

}
