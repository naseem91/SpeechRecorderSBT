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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Speech DB Info
 * Stores informations about the current speech DB schema.
 * Contains the JPA TableGenerator annotation for the shared ID generator.
 * We use a table generator instead of a sequence generator, because not all DB
 * implementations provide sequences (e.g. MySQL).
 * Update: back to sequence generator. More easy to use with manual DB modifications. 
 * The ID sequence has an allocation size of 1 (default: 50) to avoid whole in the ID sequence 
 * with manual modifications in the DB.
 * ( JPA allocates 50 ID's per default uses an internal counter to set single step ID's)   
 */
//@TableGenerator(name="id_gen", table="ID_GEN",
//        pkColumnName="ID_NAME", valueColumnName="ID_VAL",
//        pkColumnValue="SPEECHDB_GEN")

//@SequenceGenerator(name="id_gen",sequenceName="ID_SEQ",allocationSize=1,initialValue=100955200)

@SequenceGenerator(name="id_gen",sequenceName="ID_SEQ",allocationSize=1)

@Entity
@Table(name = "info", schema = "public")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@PreferredDisplayOrder("*")
public class SpeechDBInfo implements java.io.Serializable {

	// Fields  
	
	private int infoId;
	private String name;
	private String description;
	private Date deploymentDate;
	private String version;
	private String updateFrom;
	
	// Constructors

	/** default constructor */
	public SpeechDBInfo() {
	}

	/** minimal constructor */
	public SpeechDBInfo(int infoId) {
		this.infoId=infoId;
	}

	
	// Property accessors
	@Id
	@GeneratedValue(generator="id_gen")
	@Column(name = "info_id", unique = true, nullable = false)
	@ResourceKey("id")
	public int getInfoId() {
		return infoId;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}
	
	@Column(length = 100)
	@ResourceKey("name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(length = 1000)
	@ResourceKey("description")
	@TextAreaView
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(length = 100)
	@ResourceKey("version")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name = "update_from", length = 100)
	public String getUpdateFrom() {
		return updateFrom;
	}
	
	public void setUpdateFrom(String updateFrom) {
		this.updateFrom = updateFrom;
	}
	
	@Column(name="deployment_date")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDeploymentDate() {
		return deploymentDate;
	}

	public void setDeploymentDate(Date deploymentDate) {
		this.deploymentDate = deploymentDate;
	}

	


}
