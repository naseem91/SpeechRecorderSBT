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

import java.util.EnumSet;
import java.util.Set;
import java.util.Vector;

/**
 * 
 * @author klausj
 *
 * @param <E>
 */
public class EnumVector<E extends Enum<E>> extends Vector<EnumSelectionItem<E>> {
	
	/**
	 * Creates vector of enum section items for JComboBox
	 * @param c
	 */
	public EnumVector(Class<E> c){
		super();	
		fillEnumItems(c);
	}
	/**
	 * Creates vector of enum section items for JComboBox with additional 
	 * default null value
	 * @param c 
	 * @param nullValueString
	 */
	public EnumVector(Class<E> c,String nullValueString){
		super();	
		EnumSelectionItem<E> esi=new EnumSelectionItem<E>(null,nullValueString);
		add(esi);
		fillEnumItems(c);
	}
	
	private void fillEnumItems(Class<E> c){
		Set<E> eSet=EnumSet.allOf(c);
		
		for(E ee:eSet){
			add(new EnumSelectionItem<E>(ee));
		}
	}
    
    public EnumSelectionItem<E> getItem(Enum<E> enumVal){
        for(EnumSelectionItem<E> esi:this){
            Enum<E> enu=esi.getEnum();
            if(enu==null){
                if(enumVal==null) return esi;
            }else{
               if(enu.equals(enumVal)){
                   return esi;
               }
            }
        }
        return null;
    }
    
}
