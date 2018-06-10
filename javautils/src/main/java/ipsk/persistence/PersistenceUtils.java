//    IPS Java Utils
// 	  (c) Copyright 2013
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.persistence;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author klausj
 *
 */
public class PersistenceUtils {
	
	public static Class<?> getPersistenceCollectionParameterizedClass(PropertyDescriptor pd){
		Method readMethod=pd.getReadMethod();
		Class<?> getterType=readMethod.getReturnType();
		
		if (Collection.class.isAssignableFrom(getterType)) {
			// collection 
			// we need the parameterized type argument
			
			Type rt = readMethod.getGenericReturnType();
			if (rt instanceof ParameterizedType) {
				ParameterizedType prt = (ParameterizedType) rt;
				Type[] pts = prt.getActualTypeArguments();
				if (pts.length == 1) {
					Type pt=pts[0];
					if(pt instanceof Class){
						return (Class<?>)pt;
					}
				}
				
			} 
			
		}
		return null;
	}
	
	public static Class<?> getPersistenceReferenceClass(PropertyDescriptor pd){
		Method readMethod=pd.getReadMethod();
		Class<?> getterType=readMethod.getReturnType();
		
		if (Collection.class.isAssignableFrom(getterType)) {
			// collection 
			// we need the parameterized type argument
			
			Type rt = readMethod.getGenericReturnType();
			if (rt instanceof ParameterizedType) {
				ParameterizedType prt = (ParameterizedType) rt;
				Type[] pts = prt.getActualTypeArguments();
				if (pts.length == 1) {
					Type pt=pts[0];
					if(pt instanceof Class){
						return (Class<?>)pt;
					}
				}
				
			} 
			
		}else{
			return readMethod.getReturnType();
		}
		return null;
	}
	
	public static int getPersistenceReferenceCount(Object bean,PropertyDescriptor pd) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		int count=0;
		Method readMethod=pd.getReadMethod();
		Object res=readMethod.invoke(bean,new Object[0]);
		if(res!=null){

			Class<?> resType=res.getClass();

			if (Collection.class.isAssignableFrom(resType)) {
				// collection 
				Collection<?> resColl=(Collection<?>)res;
				count=resColl.size();

			}else{
				count=1;
			}
		}
		return count;
	}
}
