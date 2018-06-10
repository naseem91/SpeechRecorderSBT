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

/**
 * Enumeration constant.
 */
//@Entity
@Table(name = "typed_property_enum_const")
public class TypedPropertyEnumConstant implements java.io.Serializable {

	private int typedPropertyEnumConstantId;

	private String displayName;
	private String displayResourceKey;
	
	private String value;
	
	
	// Constructors

	/** default constructor */
	public TypedPropertyEnumConstant() {
		super();
	}


	@Id
	@Column(name = "typed_property_enum_const_id", unique = true, nullable = false)
	//@SequenceGenerator(name="ID_SEQ",sequenceName="id_seq")
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getTypedPropertyEnumConstantId() {
		return typedPropertyEnumConstantId;
	}

	public void setTypedPropertyEnumConstantId(int typedPropertyId) {
		this.typedPropertyEnumConstantId = typedPropertyId;
	}
	
	@Column(name="display_name",length = 1000)
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(length = 10,nullable = false,updatable=false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(name="display_resource_key",length = 100)
	public String getDisplayResourceKey() {
		return displayResourceKey;
	}


	public void setDisplayResourceKey(String displayResourceKey) {
		this.displayResourceKey = displayResourceKey;
	}

}
