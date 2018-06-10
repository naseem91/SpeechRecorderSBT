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

package ipsk.lang;

// TODO internationalize !
public class DisplayBoolean {

	private Boolean value;

	private String displayName;

	public DisplayBoolean(Boolean value) {
		this.value = value;
		if (value == null) {
			displayName = "(Default)";
		} else if (value.booleanValue()) {
			displayName = "Yes";
		} else {
			displayName = "No";
		}
	}
	
	public static DisplayBoolean[] getDefinedvalues(){
		return new DisplayBoolean[]{new DisplayBoolean(null),new DisplayBoolean(false),new DisplayBoolean(true)};
	}

	public String toString() {
		return displayName;
	}

	public boolean equals(Object o) {
			if ( o instanceof DisplayBoolean) {
				DisplayBoolean odb=(DisplayBoolean)o;
				Boolean oValue=odb.getValue();
				if(value==null){
					if(oValue==null)return true; 
				}else{
				return value.equals(odb.getValue());
				}
			}
		
		return false;
	}

	public Boolean getValue() {
		return value;
	}

}
