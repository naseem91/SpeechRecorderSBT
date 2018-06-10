//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.beans;

import java.util.HashMap;

import ipsk.beans.validation.ValidationResult.Type;
import ipsk.util.LocalizableMessage;


public class PropertyValidationResult {
	public enum Type {OK,WARNING,ERROR}
	
	private Type type;
	private Exception validationException;
	private LocalizableMessage validationMessage;
	//private String propertyParameter;
	private HashMap<String,String[]> failedProperties=new HashMap<String, String[]>();
	
	public PropertyValidationResult(Type type){
		this.type=type;
	}
	public PropertyValidationResult(Type type, Exception validationException) {
		this.type=type;
		this.validationException=validationException;
	}
	public PropertyValidationResult(Type type, LocalizableMessage validationMessage) {
		this.type=type;
		this.validationMessage=validationMessage;
	}
	public Exception getValidationException() {
		return validationException;
	}
	public void setValidationException(Exception valiadtionException) {
		this.validationException = valiadtionException;
	}
	public LocalizableMessage getValidationMessage() {
		return validationMessage;
	}
	public void setValidationMessage(LocalizableMessage valiadtionMessage) {
		this.validationMessage = valiadtionMessage;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public boolean isValid() {
		return (type.equals(Type.OK) || type.equals(Type.WARNING));
	}
//	public String getPropertyParameter() {
//		return propertyParameter;
//	}
//	public void setPropertyParameter(String propertyParameter) {
//		this.propertyParameter = propertyParameter;
//	}
	public HashMap<String, String[]> getFailedProperties() {
		return failedProperties;
	}
	public void setFailedProperties(HashMap<String, String[]> failedProperties) {
		this.failedProperties = failedProperties;
	}

}
