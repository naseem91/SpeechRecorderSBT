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
import ipsk.beans.dom.DOMElements;
import ipsk.beans.dom.DOMRoot;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * DialectRegion
 */
@Entity
@Table(name = "dialect_region", schema = "public")
@DOMRoot
@DOMElements({"name","iso_3166_2_code","country","state"})
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@PreferredDisplayOrder("dialectRegionId,name,iso_3166_2_code,country,state,alternative,projects,speakers")
public class DialectRegion implements java.io.Serializable {

	// Fields    

	/**
	 * 
	 */
	private static final long serialVersionUID = -4971565995450009131L;

	private int dialectRegionId;

	private String state;
	private Integer position;
	private String country;
	private String iso_3166_2_code;
	private String name;
	private boolean alternative=false;



	private Set<Project> projects = new HashSet<Project>(0);
	private Set<Speaker> speakers = new HashSet<Speaker>(0);

	// Constructors

	/** default constructor */
	public DialectRegion() {
		super();
	}

	/** minimal constructor */
	public DialectRegion(int dialectRegionId) {
		super();
		this.dialectRegionId = dialectRegionId;
	}

	/** full constructor */
	public DialectRegion(int dialectRegionId, String state, String country,
			String name, Integer position,Set<Project> projects,
			Set<Speaker> speakers) {
		super();
		this.dialectRegionId = dialectRegionId;
		this.state = state;
		this.country = country;
		this.name = name;
		this.position=position;
		this.projects = projects;
		this.speakers=speakers;
	}

	// Property accessors
	@Id
	@Column(name = "dialect_region_id", unique = true, nullable = false)
    @GeneratedValue(generator="id_gen")
    @LinkID
    @ResourceKey("id")
    public int getDialectRegionId() {
		return this.dialectRegionId;
	}

	public void setDialectRegionId(int dialectRegionId) {
		this.dialectRegionId = dialectRegionId;
	}

	@Column(name = "state", length = 1000)
	@ResourceKey("state")
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "country", length = 1000)
	@ResourceKey("country")
	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Column(name = "name", length = 1000)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "position")
	public Integer getPosition() {
		return this.position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}
	
	@ManyToMany(mappedBy="dialectRegions",fetch = FetchType.LAZY)
	@ResourceKey("projects")
	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	
	@ResourceKey("speakers")
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dialectRegion")
	public Set<Speaker> getSpeakers() {
		return this.speakers;
	}

	public void setSpeakers(Set<Speaker> speakers) {
		this.speakers = speakers;
	}

	@Column(length = 6)
	@ResourceKey("iso-3166-2_code")
	public String getIso_3166_2_code() {
		return iso_3166_2_code;
	}

	public void setIso_3166_2_code(String iso_3166_2_code) {
		this.iso_3166_2_code = iso_3166_2_code;
	}
	
	/** 
	 * Marks dialect region records like "(Other)" as alternative (and not real dialect region).
	 * 
	 * @return true if record is an alternative
	 */
	@Column()
	@ResourceKey("alternative")
	public boolean isAlternative() {
		return alternative;
	}

	public void setAlternative(boolean alternative) {
		this.alternative = alternative;
	}

	public String toString(){
		return name;
	}
	
	
}
