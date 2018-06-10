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

package ipsk.sql;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class ResultSetEditor {

	private static boolean DEBUG = false;
	//private String primaryKeyName=null;
	private int columnCount;
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	/**
	 * 
	 */
	public ResultSetEditor(ResultSet resultSet) throws SQLException {
		super();
		this.resultSet = resultSet;
		//this.primaryKeyName = primaryKeyName;
		metaData = resultSet.getMetaData();
		columnCount = metaData.getColumnCount();

	}

	public void setBeanValues(Object bean)
		throws
			SQLException,
			IntrospectionException,
			IllegalArgumentException,
			IllegalAccessException,
			InvocationTargetException {
		BeanInfo bInfo = Introspector.getBeanInfo(bean.getClass());
		PropertyDescriptor[] propertyDescriptors = bInfo.getPropertyDescriptors();
		for (int i = 1; i <= columnCount; i++) {

			String columnName = metaData.getColumnName(i);
			//Class columnType=metaData.getColumnType(i);
			for (int j = 0; j < propertyDescriptors.length; j++) {
				String propertyName = propertyDescriptors[j].getName();
				Class<?> propertyType = propertyDescriptors[j].getPropertyType();
				String propertyTypeName = propertyType.getName();
				Method writeMethod = propertyDescriptors[j].getWriteMethod();
				Object val = null;
				if (DEBUG) System.out.println("Set bean property: "+propertyName+" ("+propertyTypeName+")");
				if (columnName.equals(propertyName)) {
					if (propertyType.isPrimitive()) {
						if (propertyTypeName.equals("int")) {
							val = new Integer(resultSet.getInt(i));
						} else if (propertyTypeName.equals("boolean")) {
							val = new Boolean(resultSet.getBoolean(i));
						} else {
							throw new SQLException("type "+propertyName+" not supported !");
						}
					} else {
						if (propertyTypeName.equals("java.lang.String")) {
							val = resultSet.getString(i);
						} else if (propertyTypeName.equals("java.sql.Date")) {
							val = resultSet.getDate(i);
						} else if (propertyTypeName
								.equals("java.sql.Timestamp")) {
							val = resultSet.getTimestamp(i);
						} else if (propertyTypeName.equals("java.util.Date")) {
							val = resultSet.getTimestamp(i);
						} else {
							throw new SQLException("type "+propertyName+" not supported !");
						}
					}
					if (val != null) {
						writeMethod.invoke(bean, new Object[] { val });
					}
					// TODO do we have to set null values ?
				}
			}
		}
	}

	private void updateCurrentRow(Object bean)
		throws
			IntrospectionException,
			IllegalArgumentException,
			SQLException,
			IllegalAccessException,
			InvocationTargetException {
		BeanInfo bInfo = Introspector.getBeanInfo(bean.getClass());
		PropertyDescriptor[] propertyDescriptors = bInfo.getPropertyDescriptors();

		for (int i = 1; i <= columnCount; i++) {

			String columnName = metaData.getColumnName(i);
			//Class columnType=metaData.getColumnType(i);
			for (int j = 0; j < propertyDescriptors.length; j++) {
				String propertyName = propertyDescriptors[j].getName();
				Class<?> propertyType = propertyDescriptors[j].getPropertyType();
				String propertyTypeName = propertyType.getName();
				Method readMethod = propertyDescriptors[j].getReadMethod();
				//if (columnName.equals(propertyName) && (!columnName.equals(primaryKeyName))) {
				if (columnName.equals(propertyName)) {
					if (propertyType.isPrimitive()) {
						if (propertyTypeName.equals("int")) {
							int iVal = ((Integer) readMethod.invoke(bean, new Object[0])).intValue();
							if (DEBUG)
								System.out.println("Set: " + propertyName + " " + iVal);
							resultSet.updateInt(i, iVal);
						} else if (propertyTypeName.equals("boolean")) {
							boolean bVal = ((Boolean) readMethod.invoke(bean, new Object[0])).booleanValue();
							if (DEBUG)
								System.out.println("Set: " + propertyName + " " + bVal);
							resultSet.updateBoolean(i, bVal);
						}
					} else {
						if (propertyTypeName.equals("java.lang.String")) {
							String val = (String) readMethod.invoke(bean, new Object[0]);
							if (DEBUG)
								System.out.println("Set: " + propertyName + " " + val);
							if (val == null) {
								resultSet.updateNull(i);
							} else {
								resultSet.updateString(i, val);

							}
						} else if (propertyTypeName.equals("java.sql.Date")) {
							java.sql.Date val = (java.sql.Date) readMethod.invoke(bean, new Object[0]);
							if (DEBUG)
								System.out.println("Set: " + propertyName + " " + val);
							if (val == null) {
								resultSet.updateNull(i);
							} else {
								resultSet.updateDate(i, val);
							}
						} else if (propertyTypeName
								.equals("java.sql.Timestamp")) {
							java.sql.Timestamp val = (java.sql.Timestamp) readMethod
									.invoke(bean, new Object[0]);
							if (DEBUG)
								System.out.println("Set: " + propertyName + " "
										+ val);
							if (val == null) {
							    // throws null pointer exception 
								//resultSet.updateNull(i);
							} else {
								resultSet.updateTimestamp(i, val);
							}
						} else if (propertyTypeName.equals("java.util.Date")) {
							java.util.Date val = (java.util.Date) readMethod
									.invoke(bean, new Object[0]);
							if (DEBUG)
								System.out.println("Set: " + propertyName + " "
										+ val);
							if (val == null) {
								//resultSet.updateNull(i);
							} else {
								resultSet.updateTimestamp(i,
										new java.sql.Timestamp(val.getTime()));
							}
						}
					}
				}
			}
		}
	}

	public void updateRow(Object bean)
		throws
			IllegalArgumentException,
			IntrospectionException,
			SQLException,
			IllegalAccessException,
			InvocationTargetException {
		updateCurrentRow(bean);
		if (DEBUG)
			System.out.println("Updating row: " + resultSet.getRow());
		resultSet.updateRow();
	}

	public void insertBean(Object bean)
		throws
			IntrospectionException,
			IllegalArgumentException,
			SQLException,
			IllegalAccessException,
			InvocationTargetException {

		resultSet.moveToInsertRow();
		updateCurrentRow(bean);
		resultSet.insertRow();
	}
}
