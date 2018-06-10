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

import ipsk.util.LocalizableMessage;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class ExtPropertyDescriptor extends PropertyDescriptor {

	private LocalizableMessage localizableDisplayName;
	public LocalizableMessage getLocalizableDisplayName() {
		return localizableDisplayName;
	}

	public void setLocalizableDisplayName(LocalizableMessage localizableDisplayName) {
		this.localizableDisplayName = localizableDisplayName;
	}

	public LocalizableMessage getDescription() {
		return description;
	}

	public void setDescription(LocalizableMessage description) {
		this.description = description;
	}

	private LocalizableMessage description;
	
	public ExtPropertyDescriptor(String propertyName, Class<?> beanClass)
			throws IntrospectionException {
		super(propertyName, beanClass);
	}

	public ExtPropertyDescriptor(String propertyName, Method readMethod,
			Method writeMethod) throws IntrospectionException {
		super(propertyName, readMethod, writeMethod);
	}

	public ExtPropertyDescriptor(String propertyName, Class<?> beanClass,
			String readMethodName, String writeMethodName)
			throws IntrospectionException {
		super(propertyName, beanClass, readMethodName, writeMethodName);
	}

	

}
