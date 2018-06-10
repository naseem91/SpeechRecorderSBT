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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Audio track.
 * Usually one audio channel of an RecordingFile 
 */
@Entity
@Table(name = "recording_track")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("recording_track")
@PluralResourceKey("recording_tracks")
@PreferredDisplayOrder("recordingTrackId,recordingFile,channelIndex,*")
public class RecordingTrack implements java.io.Serializable {

	private int recordingTrackId;
	private int channelIndex;
	private RecordingFile recordingFile;
	private Double maxLevel=null;
	private Double estimatedSNR=null;
	
	@Id
	@Column(name = "recording_track_id", unique = true, nullable = false)
	@GeneratedValue(generator = "id_gen")
	@ResourceKey("id")
	public int getRecordingTrackId() {
		return recordingTrackId;
	}
	public void setRecordingTrackId(int recordingTrackId) {
		this.recordingTrackId = recordingTrackId;
	}
	
	
	@Column(name="channel_index")
	public int getChannelIndex() {
		return channelIndex;
	}
	public void setChannelIndex(int channelIndex) {
		this.channelIndex = channelIndex;
	}
	
	/**
	 * Get the maximum normalized amplitude level
	 * @return values between 0.0 and 1.0 or null if not yet processed
	 */
	@Column(name="max_level",nullable=true)
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
	
	/**
	 * Get the estimated signal to noise ratio (SNR) (non logarithm).
	 * @return non logarithm estimated SNR value or null if unknown
	 */
	@Column(name="snr_estimated",nullable=true)
	public Double getEstimatedSNR() {
		return estimatedSNR;
	}
	
	/**
	 * Set the estimated signal to noise ratio (SNR) (non logarithm)
	 * @param estimatedSNR estimated SNR value or null if unknown
	 */
	public void setEstimatedSNR(Double estimatedSNR) {
		this.estimatedSNR = estimatedSNR;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "recording_file_id")
	@ResourceKey("session")
	public RecordingFile getRecordingFile() {
		return recordingFile;
	}
	public void setRecordingFile(RecordingFile recordingFile) {
		this.recordingFile = recordingFile;
	}
	
}
