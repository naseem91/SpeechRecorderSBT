//    IPS Java Utils
//    (c) Copyright 2009-2010
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

import ipsk.beans.PreferredDisplayOrder;
import ipsk.beans.Unit;
import ipsk.persistence.ObjectImmutableIfReferenced;
import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;

//import javax.persistence.Embeddable;
//import javax.persistence.Entity;
//import javax.persistence.Transient;

public class ExtendedIntrospector {

	private static Hashtable<Class, ExtBeanInfo> cache=new Hashtable<Class, ExtBeanInfo>();
	
	/**
	 * Creates extended bean info.
	 * In this case the annotations would be not visible.
	 * Annotations related to identity of the entity, display order and localizations are processed.
	 * 
	 * @param entityClass
	 * @return extended bean info
	 * @throws IntrospectionException
	 */
	public static ExtBeanInfo getExtendedBeanInfo(Class<?> entityClass) throws IntrospectionException{
		return getExtendedBeanInfo(entityClass,true,false);
	}

	/**
	 * Creates bean info for persistence entity class.
	 * Tries to find superclass if searchEntityClass is true with Entity annotation, because some JPA implementations (OpenJPA and Hibernate for instance) use subclasses of the original entity class.
	 * In this case the annotations would be not visible.
	 * Annotations related to identity of the entity, display order and localizations are processed.
	 * 
	 * @param beanClass class of entity bean
	 * @param searchEntitySuperClass if true searches entity annotation in super classes
	 * @return extended bean info
	 * @throws IntrospectionException
	 */
	public static ExtBeanInfo getExtendedBeanInfo(Class<?> beanClass,boolean searchEntitySuperClass,boolean acceptPersistenceClassesOnly) throws IntrospectionException{
		// search cache
		ExtBeanInfo cachedBi=cache.get(beanClass);
		if(cachedBi!=null){
			return cachedBi;
		}
		Class<?> entityClass=beanClass;
		boolean searchHierarchy=false;
		// find real persistence entity class to avoid reflect problems (class not found, etc..) with OpenJPA
		//Class<?> entityClass=entityClass;
		if(searchEntitySuperClass){
		
		Class<?> eClass=entityClass;
		
		if(eClass!=null){
			// Use super class
			entityClass=eClass;
		}
		}
		
		BeanInfo bi=null;
		try{
			bi=Introspector.getBeanInfo(entityClass);
		}catch(IntrospectionException ie){
			
			throw ie;
		}
		
		//HashMap<String,String> resourecKeymap=new HashMap<String, String>();
		ExtBeanInfoImpl impl=new ExtBeanInfoImpl(bi);
		
		ResourceBundleName resBundleAnno = getAnnotationInHierarchy(entityClass,ResourceBundleName.class,searchHierarchy);
		if (resBundleAnno != null)
			impl.setResourceBundleName(resBundleAnno.value());
		ResourceKey resKeyClassAnno=getAnnotationInHierarchy(entityClass,ipsk.util.ResourceKey.class,searchHierarchy);
		if (resKeyClassAnno!=null){
			impl.setClassResourceKey(resKeyClassAnno.value());
		}
		PluralResourceKey pluralResKeyClassAnno=getAnnotationInHierarchy(entityClass,ipsk.util.PluralResourceKey.class,searchHierarchy);
		if (pluralResKeyClassAnno!=null){
			impl.setClassPluralResourceKey(pluralResKeyClassAnno.value());
		}
		PreferredDisplayOrder prefDisplayOrderAnno = null;
		Class<?> sClass=entityClass;
		do{
			prefDisplayOrderAnno = (PreferredDisplayOrder) sClass
			.getAnnotation(PreferredDisplayOrder.class);
			sClass=sClass.getSuperclass();
		}while(prefDisplayOrderAnno==null && sClass!=null);
		
		if (prefDisplayOrderAnno != null) {
			StringTokenizer st = new StringTokenizer(prefDisplayOrderAnno
					.value(), ",");
			ArrayList<String> tmpList = new ArrayList<String>();
			while (st.hasMoreTokens()) {
				tmpList.add(st.nextToken().trim());
			}
			impl.setPreferredDisplayOrder(tmpList.toArray(new String[0]));
		}
		
		
		ArrayList<PropertyDescriptor> persistencePds=new ArrayList<PropertyDescriptor>();
		
		for(PropertyDescriptor pd:bi.getPropertyDescriptors()){
			Method rm = pd.getReadMethod();
			
			if (rm != null && !pd.getName().equals("class")) {
				persistencePds.add(pd);
				
				ResourceKey resKeyAnno = getAnnotationInHierarchy(rm,ipsk.util.ResourceKey.class,searchHierarchy);
				if (resKeyAnno != null) {
					pd.setValue(ipsk.util.ResourceKey.class.getName(),
							resKeyAnno.value());
				}
				
				Unit unitAnno=getAnnotationInHierarchy(rm, Unit.class, searchHierarchy);
				if(unitAnno!=null){
					pd.setValue(Unit.class.getName(), unitAnno.value());
				}
			}
		}
		impl.setPersistencePropertyDescriptors(persistencePds.toArray(new PropertyDescriptor[0]));
		
		
		Set<PropertyDescriptor> referenceProperties=new HashSet<PropertyDescriptor>();
		sClass=entityClass;
		for(PropertyDescriptor pPd:persistencePds){
			Method rm=pPd.getReadMethod();
			ObjectImmutableIfReferenced oiirAnno=rm.getAnnotation(ObjectImmutableIfReferenced.class);
			if(oiirAnno!=null){
				referenceProperties.add(pPd);
			}
		}
		impl.setObjectImmutablePropertyDescriptors(referenceProperties);
		cache.put(beanClass, impl);
		return impl;
	}
	
	private static <T extends Annotation> T getAnnotationInHierarchy(Method method,Class<T> annotationClass,boolean searchHierarchy){
		T anno=method.getAnnotation(annotationClass);
		if(anno!=null || !searchHierarchy) return anno;
		Class<?> methodClass=method.getDeclaringClass();
		Type sType=null;
		while((sType=methodClass.getGenericSuperclass())!=null){
			if(sType instanceof Class){
				Class<?>  sClass=(Class<?>)sType;
				try {
					Method sMethod=sClass.getMethod(method.getName(), method.getParameterTypes());
					anno=sMethod.getAnnotation(annotationClass);
					if(anno!=null)return anno;
					
				} catch (NoSuchMethodException e) {
					// OK next super class
				} 
				methodClass=sClass;
			}
		}
		
		return null;
	}
	
	private static <A extends Annotation> A getAnnotationInHierarchy(Class<?> aClass,Class<A> annotationClass,boolean searchHierarchy){
		do{
			A anno = aClass.getAnnotation(annotationClass);
			if(anno!=null || !searchHierarchy)return anno;
			aClass=aClass.getSuperclass();
		}while(aClass!=null);
		return null;
	}
	
	
	
}
