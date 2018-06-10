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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlType;

/**
 * Generic typed key,value pair.
 */
//@Entity
@Table(name = "typed_property_descr")
public class TypedPropertyDescriptor implements java.io.Serializable {

	@XmlType(name="typedPropertyType")
	public enum Type {LONG,DOUBLE,STRING,ENUM}
	// Fields    

	private int typedPropertyId;

	private String name;
	
	private String displayName;
	private String displayResourceKey;
	
	private Type type=Type.STRING;
	
	
	
	// Constructors

	/** default constructor */
	public TypedPropertyDescriptor() {
		super();
	}


	@Id
	@Column(name = "typed_property_descr_id", unique = true, nullable = false)
	//@SequenceGenerator(name="ID_SEQ",sequenceName="id_seq")
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getTypedPropertyId() {
		return typedPropertyId;
	}

	public void setTypedPropertyId(int typedPropertyId) {
		this.typedPropertyId = typedPropertyId;
	}
	
	@Column(name = "name", length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	public Type getType() {
		return type;
	}


	public void setType(Type type) {
		this.type = type;
	}

	@Column(name="display_name",length = 1000)
	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(name="display_resource_key",length = 100)
	public String getDisplayResourceKey() {
		return displayResourceKey;
	}


	public void setDisplayResourceKey(String displayResourceKey) {
		this.displayResourceKey = displayResourceKey;
	}
	
}
