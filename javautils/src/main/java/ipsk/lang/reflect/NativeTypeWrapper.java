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

package ipsk.lang.reflect;

/**
 * Utility to wrap native types.
 * @author klausj
 *
 */
public class NativeTypeWrapper {

	public static final Class<?>[] WRAP_TYPES={Byte.class,Short.class,Integer.class,Long.class,Float.class,Double.class,Character.class,Boolean.class};
	
	/**
	 * Get wrapper class of native types if available.
	 * @param nativeType
	 * @return wrapper class if available the input class else.
	 */
	public static Class<?> getWrapperClass(Class<?> nativeType){
		if(nativeType.equals(Byte.TYPE)){
			return Byte.class;
		}else if(nativeType.equals(Short.TYPE)){
			return Short.class;
		}else if(nativeType.equals(Integer.TYPE)){
			return Integer.class;
		}else if(nativeType.equals(Long.TYPE)){
			return Long.class;
		}else if(nativeType.equals(Float.TYPE)){
			return Float.class;
		}else if(nativeType.equals(Double.TYPE)){
			return Double.class;
		}else if(nativeType.equals(Character.TYPE)){
			return Character.class;
		}else if(nativeType.equals(Boolean.TYPE)){
			return Boolean.class;
		}else{
			// return the type itself because it is not native
			return nativeType;
		}
	}
	
	public static boolean isNativeWrapperClass(Class<?> c){
		for(Class<?> wc:WRAP_TYPES){
			if(wc.equals(c)){
				return true;
			}
		}
		return false;
	}

	public static boolean isPrimitiveNumber(Class<?> type){
		if(type==null)return false;
		if(type.equals(Byte.TYPE) || type.equals(Short.TYPE) || type.equals(Integer.TYPE) || type.equals(Long.TYPE)|| type.equals(Float.TYPE) || type.equals(Double.TYPE)){
			return true;
		}else{
			return false;
		}
		
	}
	
}
