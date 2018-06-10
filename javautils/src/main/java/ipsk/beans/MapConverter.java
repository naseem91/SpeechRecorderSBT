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

import ipsk.beans.validation.EMail;
import ipsk.beans.validation.ValidationResult;
import ipsk.lang.reflect.NativeTypeWrapper;
import ipsk.util.LocalizableMessage;
import ipsk.util.UnitConverter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Applies property maps from HTTP requests to bean objects.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class MapConverter {

	private boolean DEBUG = false;
	
	public final static Object NULL_VALUE=new Object();
	public final static Object NULL_VALUE_AS_DEF=new Object();

	public static final String OBJECT_ID = "_id";
	public static final String DATE_MILLISECOND_KEY = "_millisecond";
	public static final String DATE_SECOND_KEY = "_second";

	public static final String DATE_MINUTE_KEY = "_minute";

	public static final String DATE_HOUR_KEY = "_hour";

	public static final String DATE_DAY_KEY = "_day";

	public static final String DATE_MONTH_KEY = "_month";

	public static final String DATE_YEAR_KEY = "_year";

	public static final String LEN_CM_KEY = "_cm";

	public static final String LEN_FEET_KEY = "_feet";

	public static final String LEN_INCH_KEY = "_inch";

	public static final String WEIGHT_POUND_KEY = "_pound";

	public static final String WEIGHT_STONE_KEY = "_stone";

	public static final String DATE_HOUR_OF_DAY_KEY = "_hour_of_day";
	
	public static final String SET_NULL="_set_null";
	public static final String SET_NULL_AS_DEFAULT="_set_null_default";

	public MapConverter() {
	}

	private boolean isDateKey(String key) {
		return (key.equals(DATE_MILLISECOND_KEY) || key.equals(DATE_SECOND_KEY) || key.equals(DATE_MINUTE_KEY)
				|| key.equals(DATE_HOUR_OF_DAY_KEY) || key.equals(DATE_HOUR_KEY) || key.equals(DATE_DAY_KEY)
				|| key.equals(DATE_MONTH_KEY) || key.equals(DATE_YEAR_KEY));
	}
	
	private boolean isNumberKey(String key) {
		return (key.equals(LEN_CM_KEY) || key.equals(LEN_FEET_KEY) || key.equals(LEN_INCH_KEY) || key.equals(LEN_INCH_KEY) || key.equals(WEIGHT_POUND_KEY) || key.equals(WEIGHT_STONE_KEY));
	}

	protected Object getValue(PropertyDescriptor pd, Map<String,String[]> properties)
			throws 
			MapConverterException {
		Object val = null;
		Calendar date = null;
		String propertyName = pd.getName();
		try{
		Class<?> valType = NativeTypeWrapper.getWrapperClass(pd.getPropertyType());

		if (properties.containsKey(propertyName)) {
			
			String[] mapValues=properties.get(propertyName);
			// simple value
			String strVal = mapValues[0];
			
			
			if(valType.equals(java.util.Date.class)){
				DateFormat defDateFormat=DateFormat.getDateInstance();
				try {
					return defDateFormat.parse(strVal);
				} catch (ParseException e) {
					throw new DateConversionException(e);
				}
			}if(valType.equals(java.util.UUID.class)){
                return UUID.fromString(strVal);
            }else if (valType.isEnum()){
				Object[] valTypeConstants=valType.getEnumConstants();
				for(Object co:valTypeConstants){
					Enum<?> valTypeConstant=(Enum<?>)co;
					if(strVal.equals(valTypeConstant.name())){
						return co;
					}
				}
				throw new IllegalArgumentException("Property \""+propertyName+"\": Enum constant \""+strVal+"\" not found !");
			}else if (valType.equals(Boolean.class)){
				// nullable boolean
				if(! pd.getPropertyType().isPrimitive() && strVal.equals("")){
					// Interpret empty fields for Boolean as Null values (not set)
					return NULL_VALUE;
				}else{
					return new Boolean(strVal);
				}
			}else if (Number.class.isAssignableFrom(valType)) {
				if(! pd.getPropertyType().isPrimitive() && strVal.equals("")){
					// Interpret empty fields for numbers as Null values (not set)
					return NULL_VALUE;
				}
			Constructor<?> constructor = valType
					.getConstructor(new Class[] { String.class });
			
			if (constructor == null)
				return null;
			try{
			Object value= constructor.newInstance(new Object[] { strVal });
			return value;
			}catch (InvocationTargetException e) {
				Throwable cause=e.getCause();
				if(cause instanceof NumberFormatException){
					NumberConversionException nfe = new NumberConversionException(e);
					HashMap<String, String[]> failedPrMap = new HashMap<String, String[]>();
					failedPrMap.put(propertyName, mapValues);
					nfe.setFailedProperties(failedPrMap);
					throw nfe;
				}

				e.printStackTrace();
				throw e;
			}

			} else if(Collection.class.isAssignableFrom(valType)){
				// e.g.list of enums
				return null;
			}else {
				Constructor<?> constructor = valType.getConstructor(new Class[] { String.class });
				if (constructor == null)
					return null;
				try{
					Object value= constructor.newInstance(new Object[] { strVal });
					return value;
				}catch (InvocationTargetException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}else {

			// component value, e.g Date
			Set<Map.Entry<String,String[]>> entries = properties.entrySet();
			Iterator<Map.Entry<String,String[]>> ei = entries.iterator();
			while (ei.hasNext()) {

				Map.Entry<String,String[]> entry = ei.next();
				String key = (String) entry.getKey();
				Object value = entry.getValue();

				int dotIndex = key.indexOf('.');
				String keyName = null;
					String fieldName = null;
					if (dotIndex > 0) {
						keyName = key.substring(0, dotIndex);
						fieldName = key.substring(dotIndex + 1);
						if (!keyName.equals(propertyName)) {
							continue;
						}
						try {
							if (fieldName.equals(SET_NULL)) {
								// returning null means the property is not set
								// so we need a constant to indicate that
								// the property should be set to null
								return NULL_VALUE;
							}
							if (fieldName.equals(SET_NULL_AS_DEFAULT)) {
								// returning null means the property is not set
								// so we need a constant to indicate that
								// the property should be set to null
								// null as default is intended for multi
								// selection requests with checkboxes
								return NULL_VALUE_AS_DEF;
							} else if (isNumberKey(fieldName)) {

								Constructor<?> constructor = valType
										.getConstructor(new Class[] { String.class });
								if (constructor == null)
									return null;
								String strVal = ((String[]) value)[0];
								if(strVal!=null && ! "".equals(strVal)){
								try {
									Object addVal = constructor
											.newInstance(new Object[] { strVal });

									if (fieldName.equals(LEN_CM_KEY)) {
										
										addVal = UnitConverter
												.centiMeterToMeter(((Number) addVal)
														.doubleValue());

									} else if (fieldName.equals(LEN_FEET_KEY)) {

										addVal = UnitConverter
												.feetToMeter(((Number) addVal)
														.doubleValue());
									} else if (fieldName.equals(LEN_INCH_KEY)) {

										addVal = UnitConverter
												.inchToMeter(((Number) addVal)
														.doubleValue());
									} else if (fieldName
											.equals(WEIGHT_POUND_KEY)) {

										addVal = UnitConverter
												.poundToKilogramme(((Number) addVal)
														.doubleValue());
									} else if (fieldName
											.equals(WEIGHT_STONE_KEY)) {

										addVal = UnitConverter
												.stoneToKilogramme(((Number) addVal)
														.doubleValue());
									}

									if (val == null) {
										val = addVal;
									} else {
										// add

										Double newVal = ((Number) val)
												.doubleValue()
												+ ((Number) addVal)
														.doubleValue();
										val = constructor
												.newInstance(new Object[] { newVal
														.toString() });

									}
								} catch (NumberFormatException e) {
									NumberConversionException nfe = new NumberConversionException(
											e);

									throw nfe;
								}catch (InvocationTargetException e) {
									Throwable cause=e.getCause();
									if(cause instanceof NumberFormatException){
										throw new NumberConversionException(cause);
									}
									throw e;
								}
								}
							} else if (isDateKey(fieldName)) {
								if (date == null) {
									date = Calendar.getInstance();
									date.clear();
								}
								int dVal = 0;

								try {
									dVal = Integer
											.parseInt(((String[]) value)[0]);
								} catch (NumberFormatException nfe) {
									throw new DateConversionException(nfe);
								}
								if (fieldName.equals(DATE_YEAR_KEY)) {

									if (dVal < date
											.getActualMinimum(Calendar.YEAR)
											|| dVal > date
													.getActualMaximum(Calendar.YEAR))
										throw new DateConversionException(
												"Invalid value for year field");
									date.set(Calendar.YEAR, dVal);
								} else if (fieldName.equals(DATE_MONTH_KEY)) {

									int mVal = dVal - 1;
									if (mVal < date
											.getActualMinimum(Calendar.MONTH)
											|| mVal > date
													.getActualMaximum(Calendar.MONTH))
										throw new DateConversionException(
												"Invalid value for month field");
									date.set(Calendar.MONTH, mVal);
								} else if (fieldName.equals(DATE_DAY_KEY)) {

									if (dVal < date
											.getActualMinimum(Calendar.DATE)
											|| dVal > date
													.getActualMaximum(Calendar.DATE))
										throw new DateConversionException(
												"Invalid value for day of month field");
									date.set(Calendar.DATE, dVal);
								} else if (fieldName
										.equals(DATE_HOUR_OF_DAY_KEY)) {

									if (dVal < date
											.getActualMinimum(Calendar.HOUR_OF_DAY)
											|| dVal > date
													.getActualMaximum(Calendar.HOUR_OF_DAY))
										throw new DateConversionException(
												"Invalid value for hour of day field");
									date.set(Calendar.HOUR_OF_DAY, dVal);
								} else if (fieldName.equals(DATE_HOUR_KEY)) {

									if (dVal < date
											.getActualMinimum(Calendar.HOUR)
											|| dVal > date
													.getActualMaximum(Calendar.HOUR))
										throw new TimeConversionException(
												"Invalid value for hour field");
									date.set(Calendar.HOUR, dVal);
								} else if (fieldName.equals(DATE_MINUTE_KEY)) {

									if (dVal < date
											.getActualMinimum(Calendar.MINUTE)
											|| dVal > date
													.getActualMaximum(Calendar.MINUTE))
										throw new TimeConversionException(
												"Invalid value for minute field");
									date.set(Calendar.MINUTE, dVal);
								} else if (fieldName.equals(DATE_SECOND_KEY)) {

									if (dVal < date
											.getActualMinimum(Calendar.SECOND)
											|| dVal > date
													.getActualMaximum(Calendar.SECOND))
										throw new TimeConversionException(
												"Invalid value for second field");
									date.set(Calendar.SECOND, dVal);
								} else if (fieldName
										.equals(DATE_MILLISECOND_KEY)) {

									if (dVal < date
											.getActualMinimum(Calendar.MILLISECOND)
											|| dVal > date
													.getActualMaximum(Calendar.MILLISECOND))
										throw new TimeConversionException(
												"Invalid value for millisecond field");
									date.set(Calendar.MILLISECOND, dVal);
								}

							}
						} catch (MapConverterValidationException e) {
							HashMap<String, String[]> failedPrMap = new HashMap<String, String[]>();
							failedPrMap.put(key, ((String[]) value));
							e.setFailedProperties(failedPrMap);
							throw e;
						}

					}
				}
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new MapConverterException(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new MapConverterException(e);
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new MapConverterException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new MapConverterException(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new MapConverterException(e);
		}
		if (date != null) {
			val = date.getTime();
		}

		return val;

	}

	
	
	
	public Object setBeanProperties(Object bean, Map<String,String[]> properties)
			throws MapConverterException {
		BeanModel beanModel=validateAndSetBeanProperties(bean, properties,false);
		return beanModel.getBean();
	}

	public BeanModel validateAndSetBeanProperties(Object bean, Map<String,String[]> properties,boolean validate)
	throws MapConverterException {
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(bean.getClass());
		} catch (IntrospectionException e1) {
			throw new MapConverterException(e1);
		}
		BeanModel beanModel=new BeanModel(bean);
		
		PropertyDescriptor[] beanProperties = beanInfo.getPropertyDescriptors();
	
		for (int i = 0; i < beanProperties.length; i++) {
			PropertyValidationResult pvr=null;
			PropertyDescriptor propertyDescriptor = beanProperties[i];
			//Class type = propertyDescriptor.getPropertyType();
			Method writeMethod = propertyDescriptor.getWriteMethod();

			try {
				Object val = getValue(propertyDescriptor, properties);
				if (val != null) {
					if(val.equals(NULL_VALUE)){
						writeMethod.invoke(bean, new Object[] { null });
					}else{
					writeMethod.invoke(bean, new Object[] { val });
					}
				}
			} catch (DateConversionException e) {
				if(!validate) throw e;
				pvr=new PropertyValidationResult(PropertyValidationResult.Type.ERROR);
				pvr.setValidationException(e);
			}catch (TimeConversionException e) {
				if(!validate) throw e;
				pvr=new PropertyValidationResult(PropertyValidationResult.Type.ERROR);
				pvr.setValidationException(e);
			}catch (NumberConversionException e) {
				if(!validate) throw e;
				pvr=new PropertyValidationResult(PropertyValidationResult.Type.ERROR);
				pvr.setValidationException(e);
			}catch(MapConverterValidationException e){
				if(!validate) throw e;
				pvr=new PropertyValidationResult(PropertyValidationResult.Type.ERROR);
				pvr.setValidationException(e);
			}catch (IllegalArgumentException e) {
			
				e.printStackTrace();
				throw new MapConverterException(e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new MapConverterException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new MapConverterException(e);
			}
			if(pvr!=null){
				ValidationResult validationResult=beanModel.getValidationResult();
				if(validationResult ==null){
					validationResult=new ValidationResult(ValidationResult.Type.ERRORS);
					beanModel.setValidationResult(validationResult);
				}
				validationResult.putPropertyValidationResult(propertyDescriptor.getName(), pvr);
				
			}
		}
		
		if (DEBUG)
			System.out.println("Converting finished");
		return beanModel;
	}
	
	
	

	public String getDATE_DAY_KEY() {
		return DATE_DAY_KEY;
	}

	public String getDATE_HOUR_KEY() {
		return DATE_HOUR_KEY;
	}

	public  String getDATE_HOUR_OF_DAY_KEY() {
		return DATE_HOUR_OF_DAY_KEY;
	}

	public String getDATE_MILLISECOND_KEY() {
		return DATE_MILLISECOND_KEY;
	}

	public String getDATE_MINUTE_KEY() {
		return DATE_MINUTE_KEY;
	}

	public String getDATE_MONTH_KEY() {
		return DATE_MONTH_KEY;
	}

	public String getDATE_SECOND_KEY() {
		return DATE_SECOND_KEY;
	}

	public  String getDATE_YEAR_KEY() {
		return DATE_YEAR_KEY;
	}

	public String getLEN_CM_KEY() {
		return LEN_CM_KEY;
	}

	public String getLEN_FEET_KEY() {
		return LEN_FEET_KEY;
	}

	public String getLEN_INCH_KEY() {
		return LEN_INCH_KEY;
	}

	public  String getSET_NULL() {
		return SET_NULL;
	}

	public String getWEIGHT_POUND_KEY() {
		return WEIGHT_POUND_KEY;
	}

	public String getWEIGHT_STONE_KEY() {
		return WEIGHT_STONE_KEY;
	}

}
