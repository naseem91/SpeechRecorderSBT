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


package ipsk.swing;

/**
 * Wrapper for enum member (Enum) for usage in a JComboBox
 * @author klausj
 *
 * @param <E> Enum type
 */
public class EnumSelectionItem<E extends Enum<E>>{
	private String displayName;
	private E enumVal;
//	private E enumValue;
	
	/**
	 * Create new enum representation with optional display name
	 * @param e enum member
	 * @param displayName display string
	 */
	public EnumSelectionItem(E e,String displayName){
		this.enumVal = e;
		this.displayName = displayName;
	}

	/**
	 * Create new enum representation
	 * @param e enum member
	 */
	public EnumSelectionItem(E e) {
		this(e,e==null?null:e.toString());
	}
	
	public Enum<E> getEnum(){
		return enumVal;
	}
	
	public E getEnumVal(){
	    return enumVal;
        
    }
	
	public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
	public String toString() {
		return displayName;
	}

	public boolean equals(Object o) {
		if (o != null) {
			if (o instanceof EnumSelectionItem) {
				EnumSelectionItem<?> mo = (EnumSelectionItem<?>) o;
				if (enumVal == null) {
					if (mo.getEnum() == null)
						return true;
				} else {
					if (enumVal.equals(mo.getEnum())) {
						return true;
					}
				}
			}
		}
		return false;
	}
 
}
