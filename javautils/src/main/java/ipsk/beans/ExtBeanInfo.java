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

import java.util.List;



public interface ExtBeanInfo extends java.beans.BeanInfo{

	public List<ExtPropertyDescriptor> getExtPropertyDescriptors();
	
	/**
	 * Get the preferred order of the properties to display.
	 * @return array of ordered property names
	 */
	public String[] getPreferredDisplayOrder();

	/**
	 * Get the name of the resource bundle to use.
	 * @return name of resource bundle
	 */
	public String getResourceBundleName();

	
	/**
	 * Get the resource key for the localized (descriptive) name of bean class.
	 * @return resource key for class name
	 */
	public String getClassResourceKey();
	
	/**
	 * Get the resource key for the localized (descriptive) name of a plural objects of the bean class.
	 * @return resource key for plural objects of the class
	 */
	public String getClassPluralResourceKey();
	
	/**
	 * Returns resource key of bean property.
	 * @param propertyName name of property
	 * @return resource key or null if not annotated
	 */
	public String getPropertyResourceKey(String propertyName);

	
}
