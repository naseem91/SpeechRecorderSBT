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
import ipsk.beans.Unit;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;
import ipsk.util.PluralResourceKey;
import java.util.Date;
import java.util.HashSet;
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
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * RecordingFile
 */
@Entity
@Table(name = "recording_file")
@NamedQueries( {
		@NamedQuery(name = "recording_files.bySession", query = "SELECT rf FROM RecordingFile rf WHERE rf.session = ?1"),
		@NamedQuery(name = "recording_file.byId", query = "SELECT rf FROM RecordingFile rf WHERE rf.recordingFileId = ?1")})
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("recording_file")
@PluralResourceKey("recording_files")
@PreferredDisplayOrder("recordingFileId,date,status,recording,annotations,session,*")
public class RecordingFile implements java.io.Serializable {

	public enum Status{REGISTERED,RECORDED,PROCESS_ERROR,PROCESSED}
	// Fields

	private int recordingFileId;

	private Session session;

	private Signal signal;

	private Recording recording;

	private String signalFile;

	private Status status;

	private String format;

	private Double samplerate;

	private Integer quantisation;

	private Long bytes;

	private Integer channels;

	private Date date;

	private String encoding;

	private Boolean bigendian;

	private Long frames;
	
	private Integer version;
	
	private Double maxLevel;

	private Set<RecordingTrack> recordingTracks=new HashSet<RecordingTrack>(0);
	
	private Set<Technician> technicians = new HashSet<Technician>(0);

	private Set<Annotation> annotations = new HashSet<Annotation>(0);

	private Set<Distribution> distributions=new HashSet<Distribution>();

	// Constructors

	/** default constructor */
	public RecordingFile() {
		super();
	}

	/** minimal constructor */
	public RecordingFile(int recordingFileId) {
		super();
		this.recordingFileId = recordingFileId;
	}

	

	// Property accessors
	@Id
	@Column(name = "recording_file_id", unique = true, nullable = false)
	//@SequenceGenerator(name = "ID_SEQ", sequenceName = "id_seq")
	@GeneratedValue(generator = "id_gen")
	@ResourceKey("id")
	public int getRecordingFileId() {
		return this.recordingFileId;
	}

	public void setRecordingFileId(int recordingFileId) {
		this.recordingFileId = recordingFileId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	@ResourceKey("session")
	public Session getSession() {
		return this.session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "signal_id")
	@ResourceKey("signal")
	public Signal getSignal() {
		return this.signal;
	}

	public void setSignal(Signal signal) {
		this.signal = signal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recording_id")
	@ResourceKey("recording.item")
	public Recording getRecording() {
		return this.recording;
	}

	public void setRecording(Recording recording) {
		this.recording = recording;
	}

	@Column(name = "signal_file", length = 1000)
	@ResourceKey("url")
	public String getSignalFile() {
		return this.signalFile;
	}

	public void setSignalFile(String signalFile) {
		this.signalFile = signalFile;
	}
	
	@Column(name = "status", length = 100)
	@Enumerated(EnumType.STRING)
	//@Column(name = "status", length = 100)
	@ResourceKey("status")
	public Status getStatus() {
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Column(name = "format", length = 100)
	@ResourceKey("audio.format")
	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Column(name = "quantisation")
	@ResourceKey("quantisation")
	public Integer getQuantisation() {
		return this.quantisation;
	}

	public void setQuantisation(Integer quantisation) {
		this.quantisation = quantisation;
	}

	@Column(name = "samplerate", precision = 8, scale = 0)
	@ResourceKey("samplerate")
	@Unit("Hz")
	public Double getSamplerate() {
		return this.samplerate;
	}

	public void setSamplerate(Double samplerate) {
		this.samplerate = samplerate;
	}

	@Column(name = "bytes")
	@ResourceKey("length.in_bytes")
	public Long getBytes() {
		return this.bytes;
	}

	public void setBytes(Long bytes) {
		this.bytes = bytes;
	}

	@Column(name = "encoding", length = 100)
	@ResourceKey("encoding")
	public String getEncoding() {
		return this.encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Column(name = "bigendian")
	@ResourceKey("bigendian")
	public Boolean getBigendian() {
		return this.bigendian;
	}

	public void setBigendian(Boolean bigendian) {
		this.bigendian = bigendian;
	}

	@Column(name = "frames")
	@ResourceKey("length.in_frames")
	public Long getFrames() {
		return this.frames;
	}

	public void setFrames(Long frames) {
		this.frames = frames;
	}

	@Transient
	//@Unit("s")  Eclipse annotation processing bug
	// throws an error if non persistence annotations exist
	public Double getLengthInSeconds() {
		Long frames=getFrames();
		Double sampleRate=getSamplerate();
		if(sampleRate != null && frames != null){
			return (double)frames/sampleRate;
		}
		return null;
	}
	
	@Column(name = "channels")
	@ResourceKey("channels")
	public Integer getChannels() {
		return this.channels;
	}

	public void setChannels(Integer channels) {
		this.channels = channels;
	}

	@Column(name = "date")
	@Temporal(TemporalType.TIMESTAMP)
	@ResourceKey("point_in_time")
	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@OneToMany(cascade = { CascadeType.ALL },mappedBy = "recordingFile")
	public Set<RecordingTrack> getRecordingTracks() {
		return recordingTracks;
	}

	public void setRecordingTracks(Set<RecordingTrack> recordingTracks) {
		this.recordingTracks = recordingTracks;
	}
	
	@ManyToMany(mappedBy="recordingFiles",fetch = FetchType.LAZY)
	@ResourceKey("technicians")
	public Set<Technician> getTechnicians() {
		return this.technicians;
	}

	public void setTechnicians(Set<Technician> technicians) {
		this.technicians = technicians;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "annotates", joinColumns = { @JoinColumn(name = "recording_id") }, inverseJoinColumns = { @JoinColumn(name = "annotation_id") })
	@ResourceKey("annotations")
	public Set<Annotation> getAnnotations() {
		return this.annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}

	@ManyToMany(mappedBy="recordingFiles")
	@ResourceKey("distributions")
	public Set<Distribution> getDistributions() {
		return distributions;
	}

	public void setDistributions(Set<Distribution> distributions) {
		this.distributions = distributions;
	}


	public String toString() {
		return "Recording file ID " + getRecordingFileId() + ": "
				+ getSignalFile();
	}
	
	@Column()
	@ResourceKey("version")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	/**
	 * Get the maximum normalized amplitude level
	 * @return values between 0.0 and 1.0 or null if not yet processed
	 */
	@Column(name="max_level")
	@ResourceKey("audio.level.max")
	public Double getMaxLevel() {
		return maxLevel;
	}

	/**
	 * Set the maximum normalized amplitude level
	 * @param maxRecordingLevel values between 0.0 and 1.0 or null if not yet processed
	 */
	public void setMaxLevel(Double maxRecordingLevel) {
		this.maxLevel = maxRecordingLevel;
	}

	@PostPersist
	public void notfiyListeners(){
		System.out.println("New recording file!");
	}
	
}
