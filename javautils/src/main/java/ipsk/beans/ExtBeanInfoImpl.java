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

import ipsk.beans.ExtPropertyDescriptor;

import ipsk.lang.reflect.NativeTypeWrapper;
import ipsk.util.ResourceKey;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class ExtBeanInfoImpl implements ExtBeanInfo {

	private BeanInfo beanInfo;

	private PropertyDescriptor idPropertyDescriptor = null;
	 private boolean idEmbedded=false;
	 private ExtBeanInfo embeddedIdBeanInfo=null;

	private boolean idGenerated;
	
	private PropertyDescriptor[] persistencePropertyDescriptors;
	private Hashtable<String,PropertyDescriptor>  persistencePropertyTable;

	private String resourceBundleName = null;

	private String classResourceKey = null;
	private String classPluralResourceKey = null;

	private String[] preferredDisplayOrder = null;

	
	private Set<PropertyDescriptor> ObjectImmutablePropertyDescriptors=null;
	
	


	public Set<PropertyDescriptor> getObjectImmutablePropertyDescriptors() {
		return ObjectImmutablePropertyDescriptors;
	}

	public void setObjectImmutablePropertyDescriptors(
			Set<PropertyDescriptor> objectImmutablePropertyDescriptors) {
		ObjectImmutablePropertyDescriptors = objectImmutablePropertyDescriptors;
	}

	public ExtBeanInfoImpl(BeanInfo beanInfo) {
		this.beanInfo = beanInfo;
		

	}

	public Object getIdValue(Object bean) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Object idValue = null;
		if (idPropertyDescriptor != null) {

			Method rm = idPropertyDescriptor.getReadMethod();
			idValue = rm.invoke(bean, new Object[0]);
		}
		return idValue;
	}

	public Object createIdValueByString(String idStr) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Class<?> idType = idPropertyDescriptor.getPropertyType();
		idType = NativeTypeWrapper.getWrapperClass(idType);
		Constructor<?> idC = idType.getConstructor(new Class[] { String.class });
		return idC.newInstance(new Object[] { idStr });
	}

	public BeanInfo[] getAdditionalBeanInfo() {
		return beanInfo.getAdditionalBeanInfo();
	}

	public BeanDescriptor getBeanDescriptor() {
		return beanInfo.getBeanDescriptor();
	}

	public int getDefaultEventIndex() {
		return beanInfo.getDefaultEventIndex();
	}

	public int getDefaultPropertyIndex() {
		return beanInfo.getDefaultPropertyIndex();
	}

	public EventSetDescriptor[] getEventSetDescriptors() {
		return beanInfo.getEventSetDescriptors();
	}

	public Image getIcon(int iconKind) {
		return beanInfo.getIcon(iconKind);
	}

	public MethodDescriptor[] getMethodDescriptors() {
		return beanInfo.getMethodDescriptors();
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		return beanInfo.getPropertyDescriptors();
	}

	public PropertyDescriptor getIdPropertyDescriptor() {
		return idPropertyDescriptor;
	}

	public void setIdPropertyDescriptor(PropertyDescriptor idPropertyDescriptor) {
		this.idPropertyDescriptor = idPropertyDescriptor;
	}

	public void setIdGenerated(boolean b) {
		idGenerated = b;

	}

	public boolean isIdGenerated() {
		return idGenerated;
	}

	public String[] getPreferredDisplayOrder() {
		return preferredDisplayOrder;
	}

	public String getResourceBundleName() {
		return resourceBundleName;
	}

	public void setResourceBundleName(String resourceBundleName) {
		this.resourceBundleName = resourceBundleName;
	}

	public void setPreferredDisplayOrder(String[] preferredDisplayOrder) {
		this.preferredDisplayOrder = preferredDisplayOrder;
	}

	public void setClassResourceKey(String string) {
		classResourceKey = string;
	}

	public String getClassResourceKey() {
		return classResourceKey;
	}
	
	public String getPropertyResourceKey(String propertyName){
		String resKey=null;
		PropertyDescriptor pd=getPersistencePropertyDescriptor(propertyName);
		if(pd!=null){
		    Method rm=pd.getReadMethod();
		    ResourceKey rkAnno=rm.getAnnotation(ResourceKey.class);
		    if(rkAnno!=null){
		        resKey=rkAnno.value();
		    }
		}
		return resKey;
	}

	public boolean isIdEmbedded() {
		return idEmbedded;
	}

	public void setIdEmbedded(boolean idEmbedded) {
		this.idEmbedded = idEmbedded;
	}

	public ExtBeanInfo getEmbeddedIdBeanInfo() {
		return embeddedIdBeanInfo;
	}

	public void setEmbeddedIdBeanInfo(ExtBeanInfo embeddedIdBeanInfo) {
		this.embeddedIdBeanInfo = embeddedIdBeanInfo;
	}

	public PropertyDescriptor[] getPersistencePropertyDescriptors() {
		return persistencePropertyDescriptors;
	}

	public void setPersistencePropertyDescriptors(
			PropertyDescriptor[] persistencePropertyDescriptors) {
		this.persistencePropertyDescriptors = persistencePropertyDescriptors;
		if(this.persistencePropertyDescriptors!=null){
		persistencePropertyTable=new Hashtable<String, PropertyDescriptor>();
		for(PropertyDescriptor pd: this.persistencePropertyDescriptors){
			persistencePropertyTable.put(pd.getName(),pd);
		}
		}else{
			persistencePropertyTable=null;
		}
	}


	public PropertyDescriptor getPersistencePropertyDescriptor(String name) {
//		if(persistencePropertyDescriptors!=null){
//			for(PropertyDescriptor pd:persistencePropertyDescriptors){
//				if(pd.getName().equals(name))return pd;
//			}
//		}
		if(persistencePropertyTable!=null)return persistencePropertyTable.get(name);
		return null;
	}

	public String getClassPluralResourceKey() {
		return classPluralResourceKey;
	}

	public void setClassPluralResourceKey(String classPluralResourceKey) {
		this.classPluralResourceKey = classPluralResourceKey;
	}

	public Set<String> getPersistencePropertyNames() {
		if(persistencePropertyTable!=null){
			return persistencePropertyTable.keySet();
		}
		return null;
	}

	public List<ExtPropertyDescriptor> getExtPropertyDescriptors() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ExtPropertyDescriptor> getExtPersistencePropertyDescriptors() {
		// TODO Auto-generated method stub
		return null;
	}



}
