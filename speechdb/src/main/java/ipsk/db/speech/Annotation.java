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

import ipsk.util.ResourceKey;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Annotation
 */
@Entity
@Table(name = "annotation", uniqueConstraints = {})
public class Annotation implements java.io.Serializable {

	// Fields    

	private int annotationId;

	private String signalUrl;

	private String status;

	private String annotator;

	private String project;

	private String annoText;

	private String quality;

	private Date annoBegin;

	private Date annoEnd;

	private Integer segmentBegin;

	private Integer segmentEnd;

	private String comment;

	private String annoType;

	private String prompt;

	private Integer priority;

	private Set<RecordingFile> recordingFiles = new HashSet<RecordingFile>(0);

	// Constructors

	/** default constructor */
	public Annotation() {
	}

	/** minimal constructor */
	public Annotation(int annotationId) {
		this.annotationId = annotationId;
	}

	/** full constructor */
	public Annotation(int annotationId, String signalUrl, String status,
			String annotator, String project, String annoText, String quality,
			Date annoBegin, Date annoEnd, Integer segmentBegin,
			Integer segmentEnd, String comment, String annoType, String prompt,
			Integer priority, Set<RecordingFile> recordingFiles) {
		this.annotationId = annotationId;
		this.signalUrl = signalUrl;
		this.status = status;
		this.annotator = annotator;
		this.project = project;
		this.annoText = annoText;
		this.quality = quality;
		this.annoBegin = annoBegin;
		this.annoEnd = annoEnd;
		this.segmentBegin = segmentBegin;
		this.segmentEnd = segmentEnd;
		this.comment = comment;
		this.annoType = annoType;
		this.prompt = prompt;
		this.priority = priority;
		this.recordingFiles = recordingFiles;
	}

	// Property accessors
	@Id
	@Column(name = "annotation_id", unique = true, nullable = false)
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getAnnotationId() {
		return this.annotationId;
	}

	public void setAnnotationId(int annotationId) {
		this.annotationId = annotationId;
	}

	@Column(name = "signal_url", length = 1000)
	public String getSignalUrl() {
		return this.signalUrl;
	}

	public void setSignalUrl(String signalUrl) {
		this.signalUrl = signalUrl;
	}

	@Column(name = "status", length = 100)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "annotator", length = 100)
	public String getAnnotator() {
		return this.annotator;
	}

	public void setAnnotator(String annotator) {
		this.annotator = annotator;
	}

	@Column(name = "project", length = 100)
	public String getProject() {
		return this.project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	@Column(name = "anno_text", length = 1000)
	public String getAnnoText() {
		return this.annoText;
	}

	public void setAnnoText(String annoText) {
		this.annoText = annoText;
	}

	@Column(name = "quality", length = 100)
	public String getQuality() {
		return this.quality;
	}

	public void setQuality(String quality) {
		this.quality = quality;
	}

	@Column(name = "anno_begin")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getAnnoBegin() {
		return this.annoBegin;
	}

	public void setAnnoBegin(Date annoBegin) {
		this.annoBegin = annoBegin;
	}

	@Column(name = "anno_end")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getAnnoEnd() {
		return this.annoEnd;
	}

	public void setAnnoEnd(Date annoEnd) {
		this.annoEnd = annoEnd;
	}

	@Column(name = "segment_begin")
	public Integer getSegmentBegin() {
		return this.segmentBegin;
	}

	public void setSegmentBegin(Integer segmentBegin) {
		this.segmentBegin = segmentBegin;
	}

	@Column(name = "segment_end")
	public Integer getSegmentEnd() {
		return this.segmentEnd;
	}

	public void setSegmentEnd(Integer segmentEnd) {
		this.segmentEnd = segmentEnd;
	}

	@Column(length = 1000)
	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "anno_type",length = 100)
	public String getAnnoType() {
		return this.annoType;
	}

	public void setAnnoType(String annoType) {
		this.annoType = annoType;
	}

	@Column(name = "prompt", length = 1000)
	public String getPrompt() {
		return this.prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	@Column()
	public Integer getPriority() {
		return this.priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	@ManyToMany(mappedBy="annotations",fetch = FetchType.LAZY)
	public Set<RecordingFile> getRecordingFiles() {
		return this.recordingFiles;
	}

	public void setRecordingFiles(Set<RecordingFile> recordingFiles) {
		this.recordingFiles = recordingFiles;
	}

}
