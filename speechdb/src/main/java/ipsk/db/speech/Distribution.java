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


import ipsk.beans.LinkID;
import ipsk.beans.PreferredDisplayOrder;
import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 * Organisation
 */
@Entity
@Table(name = "distribution")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("distribution")
@PluralResourceKey("distributions")
@PreferredDisplayOrder("distributionId,name,version,description,*")
public class Distribution implements java.io.Serializable{

	// Fields    

	private int distributionId;

	private String name;

	private String version;

	

	private String description;

	
	private List<RecordingFile> recordingFiles=new ArrayList<RecordingFile>();

	// Constructors

	
	public Distribution() {
		super();
	}



	// Property accessors
	@Id
	@Column(name = "distribution_id", unique = true)
    @GeneratedValue(generator="id_gen")
    @LinkID
    @ResourceKey("id")
    public int getDistributionId() {
		return this.distributionId;
	}

	public void setDistributionId(int distributionId) {
		this.distributionId = distributionId;
	}

	@Column(length = 100)
	@ResourceKey("name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 20)
	@ResourceKey("version")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(length = 10000)
	@ResourceKey("description")
	@ipsk.util.annotations.TextAreaView
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		        name="distribution_recording_files",
		        joinColumns={@JoinColumn(name="disribution_id")},
		        inverseJoinColumns={@JoinColumn(name="recording_file_id")}
		    )
	@OrderColumn
	@ResourceKey("recording_files")
	public List<RecordingFile> getRecordingFiles() {
		return recordingFiles;
	}


	public void setRecordingFiles(List<RecordingFile> recordingFiles) {
		this.recordingFiles = recordingFiles;
	}
	
	

}
