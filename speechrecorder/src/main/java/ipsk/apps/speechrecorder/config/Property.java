//    Speechrecorder
//    (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.apps.speechrecorder.config;

import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMElements;

@DOMAttributes({"name","type"})
@DOMElements({"displayName","value","enumConstants"})
public class Property {
	
	public enum Type {INTEGER,LONG,FLOAT,DOUBLE,STRING,BOOLEAN,ENUM}
	private String name=null;
	private Type type=Type.STRING;
	private String value=null;
	private String displayName=null;
	
	private EnumConstant[] enumConstants=null;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public EnumConstant[] getEnumConstants() {
		return enumConstants;
	}
	public void setEnumConstants(EnumConstant[] enumConstants) {
		this.enumConstants = enumConstants;
	}
	
}
