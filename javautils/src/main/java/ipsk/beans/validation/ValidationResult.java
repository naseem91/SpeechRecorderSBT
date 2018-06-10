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

package ipsk.beans.validation;

import ipsk.beans.PropertyValidationResult;
import java.util.Hashtable;

public class ValidationResult{
	public enum Type {SUCCESS,WARNINGS,ERRORS,CANCELLED}
	
	private Type type;
	private Hashtable<String,PropertyValidationResult> propertyValidationResults=new Hashtable<String, PropertyValidationResult>();
	public ValidationResult() {
		this.type=ValidationResult.Type.SUCCESS;
	}
	public ValidationResult(Type type) {
		this.type=type;
	}

	
	public Type getType() {
		return type;
	}

	public void putPropertyValidationResult(String propertyName,PropertyValidationResult propertyMessage){
		propertyValidationResults.put(propertyName, propertyMessage);
	}
	
	public PropertyValidationResult getPropertyValidationResult(String propertyName){
		return propertyValidationResults.get(propertyName);
	}
	
	public Hashtable<String,PropertyValidationResult> getPropertyValidationResults() {
		return propertyValidationResults;
	}


	public void setPropertyValidationResults(Hashtable<String,PropertyValidationResult> propMessages) {
		this.propertyValidationResults = propMessages;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
	public boolean isValid(){
		return (type.equals(Type.SUCCESS) || type.equals(Type.WARNINGS));
	}
	public boolean isCancelled() {
		return Type.CANCELLED.equals(type);
	}
	
}
