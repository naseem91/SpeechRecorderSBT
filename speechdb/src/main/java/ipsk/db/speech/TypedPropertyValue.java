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
 * A typed value.
 */
//@Entity
@Table(name = "typed_property_value")
public class TypedPropertyValue implements java.io.Serializable {


	private int typedPropertyValueId;

	private TypedPropertyDescriptor descriptor;
	
	private Long longValue;
	private Double doubleValue;
	private String stringValue;
	private String enumValue;
	
	// Constructors

	/** default constructor */
	public TypedPropertyValue() {
		super();
	}


	@Id
	@Column(name = "typed_property_value_id", unique = true, nullable = false)
	//@SequenceGenerator(name="ID_SEQ",sequenceName="id_seq")
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getTypedPropertyValueId() {
		return typedPropertyValueId;
	}

	public void setTypedPropertyValueId(int typedPropertyValueId) {
		this.typedPropertyValueId = typedPropertyValueId;
	}
	

	@Column()
	public Long getLongValue() {
		return longValue;
	}


	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	@Column()
	public Double getDoubleValue() {
		return doubleValue;
	}

	
	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	@Column(length = 1000)
	public String getStringValue() {
		return stringValue;
	}


	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	@Column(length = 1000)
	public String getEnumValue() {
		return enumValue;
	}


	public void setEnumValue(String enumValue) {
		this.enumValue = enumValue;
	}


	public TypedPropertyDescriptor getDescriptor() {
		return descriptor;
	}


	public void setDescriptor(TypedPropertyDescriptor descriptor) {
		this.descriptor = descriptor;
	}


}
