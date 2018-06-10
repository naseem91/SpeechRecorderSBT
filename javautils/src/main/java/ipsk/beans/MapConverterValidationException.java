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

public class MapConverterValidationException extends MapConverterException {

	private HashMap<String,String[]> failedProperties=new HashMap<String, String[]>();
	
	public MapConverterValidationException() {
		super();
	}
	
	public MapConverterValidationException(HashMap<String,String[]> failedProperties) {
		super();
	}

	public MapConverterValidationException(String arg0) {
		super(arg0);
		
	}

	public MapConverterValidationException(Throwable arg0) {
		super(arg0);	
	}

	public MapConverterValidationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	public MapConverterValidationException(String arg0, Throwable arg1,HashMap<String,String[]> failedProperties) {
		super(arg0, arg1);
		this.failedProperties=failedProperties;
	}

	public HashMap<String, String[]> getFailedProperties() {
		return failedProperties;
	}

	public void setFailedProperties(HashMap<String, String[]> failedProperties) {
		this.failedProperties = failedProperties;
	}

}
