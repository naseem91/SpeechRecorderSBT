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

import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMCollectionElement;
import ipsk.beans.dom.DOMElements;
import ipsk.beans.dom.DOMRoot;
import ipsk.beans.dom.DOMTextNodePropertyName;
import ipsk.beans.dom.RemoveIfDefault;
import ipsk.beans.dom.Temporal;
import ipsk.beans.test.Child1;
import ipsk.beans.test.Root;
import ipsk.beans.test.SuperRoot;
import ipsk.beans.test.Child1.Selection;
import ipsk.beans.test.Root.WeekDays;
import ipsk.text.ParserException;
import ipsk.text.RFC3339DateTimeFormat;
import ipsk.text.StringObjectConverter;
import ipsk.text.RFC3339DateTimeFormat.TemporalType;
import ipsk.xml.DOMAttribute;
import ipsk.xml.DOMConverter;
import ipsk.xml.DOMConverterException;
import ipsk.xml.DOMElement;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * A Java object to DOM (Document object model) converter.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class DOMCodec {

    public final static String attributePrefix = "attribute";

    private boolean elementNameUpperCase = true;

    private Package elementsPackage;

    private DocumentBuilder db = null;
    
    //private RFC3339DateTimeFormat rfcDateTimeFormat=new RFC3339DateTimeFormat();

    public DOMCodec() throws DOMCodecException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new DOMCodecException(e);
        }
    }

    /**
     * Creates new DOM encoder/decoder.
     * 
     * @param elementsPackage
     *            the package name to map Java objects to/from
     * @throws DOMCodecException
     */
    public DOMCodec(Package elementsPackage) throws DOMCodecException {
        this();
        setElementsPackage(elementsPackage);
    }

    /**
     * Creates new DOM encoder/decoder.
     * 
     * @param elementsPackage
     *            the package name to map Java objects to/from
     * @param elementNameUpperCase
     * @throws DOMCodecException
     */
    public DOMCodec(Package elementsPackage, boolean elementNameUpperCase)
            throws DOMCodecException {
        this(elementsPackage);
        this.elementNameUpperCase = elementNameUpperCase;
    }

    public void setElementsPackage(Package elementsPackage) {
        this.elementsPackage = elementsPackage;
    }

    /**
     * Generate a Java object (tree) from DOM document.
     * 
     * @param document
     * @return the object tree
     * @throws DOMCodecException
     */
    public Object readDocument(Document document) throws DOMCodecException {
        Element rootElement = document.getDocumentElement();
        // return parseDOM(null, rootElement);
        return createObject(rootElement);
    }

    private PropertyDescriptor getPropertyDescriptorByName(
            PropertyDescriptor[] descriptors, String name) {
        for (int i = 0; i < descriptors.length; i++) {
            if (descriptors[i].getName().equals(name))
                return descriptors[i];
        }
        return null;
    }
    
    private PropertyDescriptor getPropertyDescriptorByArrayType(
            PropertyDescriptor[] descriptors, String componentTypeName) {
        for (PropertyDescriptor pd:descriptors) {
           Class<?> pdType=pd.getPropertyType();
           if(pdType.isArray()){
        	   Class<?> pdCompType=pdType.getComponentType();
           if(pdCompType.getSimpleName().equals(componentTypeName)){
        	   return pd;
           }
           }
        }
        return null;
    }

    private Object createObject(Element e) throws DOMCodecException {
        return createObject(e, null);
    }

   
	private Object createObject(Element e, Class<?> c) throws DOMCodecException {
        BeanInfo bi = null;
        //Document d = null;
        // Class c = null;
        String className = null;
        if (c != null)
            className = c.getName();
        String elementClassname = new String(e.getTagName());
        if (!elementNameUpperCase) {
            elementClassname = new String(elementClassname.substring(0, 1)
                    .toUpperCase(Locale.ENGLISH)
                    + elementClassname.substring(1));
        }

        if (className == null) {
            className = elementsPackage.getName();
            className = className.concat("." + elementClassname);
            try {
                c = Class.forName(className);
            } catch (ClassNotFoundException e1) {
                throw new DOMCodecException(e1);
            }
        }
        Object bean = null;
        if (c.isPrimitive() || Number.class.isAssignableFrom(c)) {
            NodeList childs = e.getChildNodes();
            for (int i = 0; i < childs.getLength(); i++) {
                Node node = childs.item(i);
                short nodeType = node.getNodeType();

                // check for single text node child here
                if (nodeType == Node.TEXT_NODE) {
                    try {
                        // bean =
                        // StringObjectConverter.stringToObject(node.getNodeValue(),
                        // className);
                        bean = StringObjectConverter.stringToObject(node
                                .getNodeValue(), c);
                    } catch (ParserException e1) {
                        throw new DOMCodecException(e1);
                    }
                }
            }
		}else {
        	if(c.isEnum()){
        		String enumString=null;
        		NodeList childs = e.getChildNodes();
                for (int i = 0; i < childs.getLength(); i++) {
                    Node node = childs.item(i);
                    short nodeType = node.getNodeType();

                    // check for single text node child here
                    if (nodeType == Node.TEXT_NODE) {
                        enumString = node.getNodeValue();
                    }
                }
        		Object[] enumConstants=c.getEnumConstants();
            	for(Object enumConstant:enumConstants){
            		if(enumConstant instanceof Enum<?>){
            			Enum<?> eConst=(Enum<?>)enumConstant;
            			if(eConst.toString().equals(enumString)){
            				bean=eConst;
            				break;
            			}
            		}
            	}
        	}else if(UUID.class.equals(c)){
                String uuidString=null;
                NodeList childs = e.getChildNodes();
                for (int i = 0; i < childs.getLength(); i++) {
                    Node node = childs.item(i);
                    short nodeType = node.getNodeType();

                    // check for single text node child here
                    if (nodeType == Node.TEXT_NODE) {
                        uuidString = node.getNodeValue();
                    }
                }
                bean=UUID.fromString(uuidString);
            }else{
            try {
            	
                bean = c.newInstance();
           
            } catch (Exception ex) {
                throw new DOMCodecException(ex);
            }
            if (c.equals(java.lang.String.class)) {
                NodeList childs = e.getChildNodes();
                for (int i = 0; i < childs.getLength(); i++) {
                    Node node = childs.item(i);
                    short nodeType = node.getNodeType();

                    // check for single text node child here
                    if (nodeType == Node.TEXT_NODE) {
                        bean = node.getNodeValue();
                    }
                }
            } else {
                NamedNodeMap attrMap = e.getAttributes();
                if (bean instanceof DOMElement) {
                    int len = attrMap.getLength();
                    DOMAttribute[] attributes = new DOMAttribute[len];
                    for (int i = 0; i < len; i++) {
                        Attr attrNode = (Attr) attrMap.item(i);
                        attributes[i] = new DOMAttribute(attrNode.getName(),
                                attrNode.getValue());
                    }
                    ((DOMElement) bean).setAttributes(attributes);
                    NodeList childs = e.getChildNodes();
                    // TODO hier geht's weiter

                } else {
                	// all other custom classes
                    try {
                        bi = Introspector.getBeanInfo(c);
                    } catch (IntrospectionException e1) {
                        throw new DOMCodecException(e1);
                    }
                    PropertyDescriptor[] pds = bi.getPropertyDescriptors();
                   
                   

                    if (attrMap != null) {
                        int len = attrMap.getLength();
                        DOMAttributes domAttributesAnnot = c
                                .getAnnotation(DOMAttributes.class);
                        String[] domAttributeNames = null;
                        if (domAttributesAnnot != null) {
                            domAttributeNames = domAttributesAnnot.value();
                        }
                        for (int i = 0; i < len; i++) {
                            Attr attrNode = (Attr) attrMap.item(i);
                            String attrName = attrNode.getName();
                            String propName = null;
                            if (domAttributeNames != null) {
                                propName = attrName;
                            } else {
                                propName = new String(attributePrefix
                                        + attrName.substring(0, 1)
                                                .toUpperCase(Locale.ENGLISH));
                                propName = propName.concat(attrName
                                        .substring(1));
                                // }else{
                                // propName=new String(attributePrefixattrName);
                                // }
                            }
                            PropertyDescriptor pd = getPropertyDescriptorByName(
                                    pds, propName);

                            if (pd == null) {
                                throw new DOMCodecException(
                                        "Cannot associate attribute '"
                                                + propName + "'");
                            }
                            Class<?> propertyType = pd.getPropertyType();
                            
                            //String propertyTypeName = propertyType.getName();
                            Method setMethod = pd.getWriteMethod();
                            try {
                            	Object attributeValue=null;
        
                                if(propertyType.isEnum()){
                                	Object[] enumConstants=propertyType.getEnumConstants();
                                	for(Object enumConstant:enumConstants){
                                		if(enumConstant instanceof Enum<?>){
                                			Enum<?> eConst=(Enum<?>)enumConstant;
                                			if(attrNode.getValue().equals(eConst.toString())){
                                				attributeValue=eConst;
                                				break;
                                			}
                                		}
                                	}
                                }else if(Date.class.isAssignableFrom(propertyType)){
                                	RFC3339DateTimeFormat dateTimeFormat=new RFC3339DateTimeFormat();
                                	attributeValue=dateTimeFormat.parseObject(attrNode.getValue());
                                }else{
                                	attributeValue= StringObjectConverter.stringToObject(attrNode.getValue(),
                                                propertyType);
                                }
                                setMethod.invoke(bean,
                                        new Object[] { attributeValue });
                            } catch (Exception ex) {
                                throw new DOMCodecException(
                                        "Cannot associate attribute '"
                                                + propName + "'", ex);
                            }
                            }
                    }
                    
                    // Text node
                    String textNodePropertyName=null;
                    DOMTextNodePropertyName textNodePropAnno=c.getAnnotation(DOMTextNodePropertyName.class);
                    if(textNodePropAnno!=null){
                    	textNodePropertyName=textNodePropAnno.value();
                    	PropertyDescriptor pd=getPropertyDescriptorByName(pds, textNodePropertyName);
                    	
                    	if(pd!=null){
                    		String text=null;
                    		 Method setMethod = pd.getWriteMethod();
                    		 NodeList nl=e.getChildNodes();
                    		 for (int i = 0; i < nl.getLength(); i++) {
                    			 Node n=nl.item(i);
                    			 if(n.getNodeType()==Node.TEXT_NODE){
                    				 String nodeText=n.getTextContent();
                    				 if(text==null){
                    					 text=new String(nodeText);
                    				 }else{
                    					 text=text.concat(nodeText);
                    				 }
                    			 }
                    		 }
                    		 try {
								setMethod.invoke(bean, new Object[]{text});
							} catch (Exception e1) {
								throw new DOMCodecException("Cannot set '"
                                + textNodePropertyName+ "' text node property!", e1);
							} 
                    	}
                    }
                   
                    // Element nodes
                    NodeList childs = e.getChildNodes();
                    HashSet<String> arrayMethodsSet = new HashSet<String>();
                    for (int i = 0; i < childs.getLength(); i++) {
                        Node node = childs.item(i);
                        short nodeType = node.getNodeType();
                        if (nodeType == Node.ELEMENT_NODE) {
                            Element childElement = (Element) node;
                            String tagName = childElement.getTagName();
                            String propName = tagName.substring(0, 1)
                                    .toLowerCase(Locale.ENGLISH);
                            propName = propName.concat(tagName.substring(1));
                            propName = propName.replace('_', '.');
                            PropertyDescriptor pd = getPropertyDescriptorByName(
                                    pds, propName);
                            if (pd == null) {
                            	// No property with this name found
                            	// try to find an array type
                            	pd=getPropertyDescriptorByArrayType(pds, tagName);
                            	
                            	if(pd==null){
                                throw new DOMCodecException(
                                        "Cannot associate element '" + propName
                                                + "'");
                            	}
                            }
                            Class<?> propertyType = pd.getPropertyType();
//                            String propertyTypeName = propertyType.getName();
                            Object param = null;
                            Method setMethod = pd.getWriteMethod();
                            if (propertyType.isArray() ) {
                                Class<?> arrType = propertyType.getComponentType();
                                //String arrTypeName = arrType.getName();
                                Method getMethod = pd.getReadMethod();

                                if (arrayMethodsSet.contains(propName)) {
                                    // to avoid reading array entries of the
                                    // default bean
                                    // the object will only be read after the
                                    // first element
                                    try {
                                        param = getMethod.invoke(bean,
                                                new Object[0]);
                                    } catch (Exception e2) {
                                        throw new DOMCodecException(e2);
                                    }
                                }

                                if (param == null) {
                                    param = Array.newInstance(arrType, 0);
                                    try {
                                        setMethod.invoke(bean,
                                                new Object[] { param });
                                    } catch (Exception e3) {
                                        throw new DOMCodecException(e3);
                                    }
                                }
                                int arrLen = Array.getLength(param) + 1;
                                Object oldParam = param;
                                // Create new array with one more element
                                param = Array.newInstance(arrType, arrLen);
                                // copy old array
                                for (int a = 0; a < arrLen - 1; a++) {
                                    Array.set(param, a, Array.get(oldParam, a));
                                }
                                Object newParamElement = null;

                                newParamElement = createObject(childElement,
                                        arrType);

                                Array.set(param, arrLen - 1, newParamElement);
                                arrayMethodsSet.add(propName);
                            }else if (List.class.isAssignableFrom(propertyType)){
                              //Class<?> arrType = propertyType.getComponentType();
                                //String arrTypeName = arrType.getName();
                                Method getMethod = pd.getReadMethod();
                                String collElementName=propName;
                                DOMCollectionElement domCollectionElementAnno=getMethod.getAnnotation(DOMCollectionElement.class);
                                if(domCollectionElementAnno!=null){
                                    collElementName=domCollectionElementAnno.collectionElementName();
                                }
                                Type rt=getMethod.getGenericReturnType();
                                Class<Set<?>> setClass=null;
                                if(rt instanceof ParameterizedType){
                                    Type[] listTypes=((ParameterizedType)rt).getActualTypeArguments();
                                    if(listTypes.length!=1){
                                        throw new DOMCodecException("List with more than one parameterized types!");
                                    }else{
                                        Type setType=listTypes[0];
                                        if(setType!=null && setType instanceof Class<?>){
                                            setClass=(Class<Set<?>>)setType;
                                        }
                                    }
                                }else{
                                    throw new DOMCodecException("Cannot convert unparametrized collection types: "+propName+" !");
                                }

                                try {
                                    param = getMethod.invoke(bean,
                                            new Object[0]);
                                } catch (Exception e2) {
                                    throw new DOMCodecException(e2);
                                }


                                if (param == null) {

                                    param = new HashSet<Object>();

                                    try {
                                        setMethod.invoke(bean,
                                                new Object[] { param });
                                    } catch (Exception e3) {
                                        throw new DOMCodecException(e3);
                                    }
                                }

                                Object newParamElement = null;
                                 
                                @SuppressWarnings("unchecked")
                                java.util.List<Object> coll=(java.util.List<Object>)param;
                                
                                if(domCollectionElementAnno==null){
                                    // single element in this loop
                                    newParamElement = createObject(childElement,setClass);

                                    coll.add(newParamElement);
                                }else{
                                    // iterate through all collection elements
                                    NodeList collNodelist=childElement.getElementsByTagName(collElementName);
                                    int collElementsCount=collNodelist.getLength();
                                    for(int j=0;j<collElementsCount;j++){
                                        Node collElementNode=collNodelist.item(j);
                                        if(collElementNode instanceof Element){
                                            Element collElementElement=(Element)collElementNode;
                                            newParamElement = createObject(collElementElement,setClass);

                                            coll.add(newParamElement);
                                        }
                                    }

                                }

                            }else if (Set.class.isAssignableFrom(propertyType)){
                            	//Class<?> arrType = propertyType.getComponentType();
                            	//String arrTypeName = arrType.getName();
                            	Method getMethod = pd.getReadMethod();
                            	String collElementName=propName;
                            	DOMCollectionElement domCollectionElementAnno=getMethod.getAnnotation(DOMCollectionElement.class);
                            	if(domCollectionElementAnno!=null){
                            		collElementName=domCollectionElementAnno.collectionElementName();
                            	}
                            	Type rt=getMethod.getGenericReturnType();
                            	Class<Set<?>> setClass=null;
                            	if(rt instanceof ParameterizedType){
                            		Type[] setTypes=((ParameterizedType)rt).getActualTypeArguments();
                            		if(setTypes.length!=1){
                            			throw new DOMCodecException("Set with more than one parameterized types!");
                            		}else{
                            			Type setType=setTypes[0];
                            			if(setType!=null && setType instanceof Class<?>){
                            				setClass=(Class<Set<?>>)setType;
                            			}
                            		}
                            	}else{
                            		throw new DOMCodecException("Cannot convert unparametrized collection types: "+propName+" !");
                            	}

                            	try {
                            		param = getMethod.invoke(bean,
                            				new Object[0]);
                            	} catch (Exception e2) {
                            		throw new DOMCodecException(e2);
                            	}


                            	if (param == null) {

                            		param = new HashSet<Object>();

                            		try {
                            			setMethod.invoke(bean,
                            					new Object[] { param });
                            		} catch (Exception e3) {
                            			throw new DOMCodecException(e3);
                            		}
                            	}

                            	Object newParamElement = null;
                            	 
                            	@SuppressWarnings("unchecked")
                            	Set<Object> coll=(Set<Object>)param;
                            	
                            	if(domCollectionElementAnno==null){
                            		// single element in this loop
                            		newParamElement = createObject(childElement,setClass);

                            		coll.add(newParamElement);
                            	}else{
                            		// iterate through all collection elements
                            		NodeList collNodelist=childElement.getElementsByTagName(collElementName);
                            		int collElementsCount=collNodelist.getLength();
                            		for(int j=0;j<collElementsCount;j++){
                            			Node collElementNode=collNodelist.item(j);
                            			if(collElementNode instanceof Element){
                            				Element collElementElement=(Element)collElementNode;
                            				newParamElement = createObject(collElementElement,setClass);

                            				coll.add(newParamElement);
                            			}
                            		}

                            	}

                            } else if (Map.class.isAssignableFrom(propertyType)){
                            	//Class<?> arrType = propertyType.getComponentType();
                            	//String arrTypeName = arrType.getName();
                            	Method getMethod = pd.getReadMethod();
                            	
                            	String collElementName=propName;
                            	DOMCollectionElement domCollectionElementAnno=getMethod.getAnnotation(DOMCollectionElement.class);
                            	if(domCollectionElementAnno!=null){
                            		collElementName=domCollectionElementAnno.collectionElementName();
                            	}
                            	Type rt=getMethod.getGenericReturnType();
                            	Class<?> mapKeyClass=null;
                            	Class<?> mapValueClass=null;
                            	if(rt instanceof ParameterizedType){
                            		Type[] mapTypes=((ParameterizedType)rt).getActualTypeArguments();
                            		if(mapTypes.length!=2){
                            			throw new DOMCodecException("Map with more than two parameterized types!");
                            		}else{
                            			Type mapKeyType=mapTypes[0];
                            			Type mapValType=mapTypes[1];
                            			if(mapKeyType!=null && mapKeyType instanceof Class<?> && mapValType!=null && mapValType instanceof Class<?>){
                            				mapKeyClass=(Class<?>)mapKeyType;
                            				mapValueClass=(Class<?>)mapValType;
                            			}
                            		}
                            	}else{
                            		throw new DOMCodecException("Cannot convert unparametrized collection types: "+propName+" !");
                            	}

                            	try {
                            		param = getMethod.invoke(bean,
                            				new Object[0]);
                            	} catch (Exception e2) {
                            		throw new DOMCodecException(e2);
                            	}


                            	if (param == null) {

                            		param = new HashMap<Object,Object>();

                            		try {
                            			setMethod.invoke(bean,
                            					new Object[] { param });
                            		} catch (Exception e3) {
                            			throw new DOMCodecException(e3);
                            		}
                            	}

                            	Object newParamElement = null;
                            	 
                            	@SuppressWarnings("unchecked")
                            	Map<Object,Object> map=(Map<Object,Object>)param;
                            	
//                            	if(domCollectionElementAnno==null){
//                            		// single element in this loop
//                            		newParamElement = createObject(childElement,mapClass);
//
//                            		map.add(newParamElement);
//                            	}else{
                            		// iterate through all collection elements
                            		NodeList mapNodelist=childElement.getChildNodes();
                            		int mapElementsCount=mapNodelist.getLength();
                            		Object currentKey=null;;
                            		for(int j=0;j<mapElementsCount;j++){
                            			Node mapElementNode=mapNodelist.item(j);
                            			if(mapElementNode instanceof Element){
                            				Element mapElementElement=(Element)mapElementNode;
                            				String nName=mapElementElement.getNodeName();
                            				if("entry".equals(nName)){

                            					NodeList entryKeyList=mapElementElement.getElementsByTagName("key");
                            					int entryKeyCount=entryKeyList.getLength();
                            					if(entryKeyCount>1){
                            						throw new DOMCodecException("Ambigious keys for map entry!");
                            					}
                            					Object key=null;
                            					if(entryKeyCount==1){
                            						Node keyNode=entryKeyList.item(0);

                            						String nv=keyNode.getTextContent();

                            						try {
                            							key=StringObjectConverter.stringToObject(nv,mapKeyClass);
                            						} catch (ParserException e1) {
                            							throw new DOMCodecException("Cannot convert map key "+nv+" to object!");
                            						}
                            					}
                            					NodeList entryValList=mapElementElement.getElementsByTagName("value");
                            					int entryValCount=entryValList.getLength();
                            					if(entryValCount>1){
                            						throw new DOMCodecException("Ambigious values for map entry!");
                            					}
                            					Object val=null;
                            					if(entryValCount==1){
                            						Node valNode=entryValList.item(0);

                            						String valText=valNode.getTextContent();

                            						try {
                            							val=StringObjectConverter.stringToObject(valText,mapValueClass);
                            						} catch (ParserException e1) {
                            							throw new DOMCodecException("Cannot convert map value "+valText+" to object!");
                            						}
                            					}
                            					map.put(key, val);
                            				}

                            				//                            				newParamElement = createObject(mapElementElement,mapClass);

                            				//                            				map.add(newParamElement);
                            			}
                            		}

//                            	}

                            }else if (propertyType.isPrimitive() || propertyType.equals(Boolean.class)) {


                            	// A property in text node
                            	NodeList propertyValueNodes = childElement
                            	.getChildNodes();
                            	for (int k = 0; k < propertyValueNodes
                            	.getLength(); k++) {
                            		Node propValueNode = propertyValueNodes
                            		.item(k);
                            		if (propValueNode.getNodeType() == Node.TEXT_NODE) {

                            			String strValue = propValueNode
                            			.getNodeValue();

                            			try {
                            				param = StringObjectConverter
                            				.stringToObject(strValue,
                            						propertyType);
                            			} catch (ParserException e1) {
                            				throw new DOMCodecException(e1);
                            			}

                            			break;
                            		}
                            	}

                            } else if (propertyType.equals(java.lang.String.class)) {
                            	if(propName.equals(textNodePropertyName)){
                            		param=childElement.getTextContent();
                            	}else{

                            		// A property in text node
                            		NodeList propertyValueNodes = childElement
                            		.getChildNodes();
                            		for (int k = 0; k < propertyValueNodes
                            		.getLength(); k++) {
                            			Node propValueNode = propertyValueNodes
                            			.item(k);
                            			if (propValueNode.getNodeType() == Node.TEXT_NODE) {

                            				String strValue = propValueNode
                            				.getNodeValue();

                            				try {
                            					param = StringObjectConverter
                            					.stringToObject(strValue,
                            							propertyType);
                            				} catch (ParserException e1) {
                            					throw new DOMCodecException(e1);
                            				}

                            				break;
                            			}
                            		}
                            	}

                            } else if (!propertyType.isPrimitive()) {
                            	param = createObject(childElement, propertyType);
                            }
                            try {
                            	setMethod.invoke(bean, new Object[] { param });
                            } catch (Exception ex) {
                            	throw new DOMCodecException(ex);
                            }
                        }
                    }
                }
            }
        	}
        }
        return bean;
    }
	/**
     * Creates a cloned java bean (deep copy).
     * 
     * @param bean
     *            the bean to clone
     * @return the cloned bean
     * @throws DOMCodecException
     */
    public Object copy(Object bean) throws DOMCodecException {
        return copy(bean,new IdentityHashMap<Object, Object>());
    }
    
    /**
     * Creates a cloned java bean (deep copy).
     * This method does not use the Object.clone().
     * Only Java bean properties which have a getter and setter method are copied.
     * If the bean is found in the identity map of already copied object the existing copy is returned.
     * This avoids cyclic loops.
     * 
     * @param bean the bean to clone
     * @param copiedObjects identity hash map of already copied objects
     * @return the cloned bean
     * @throws DOMCodecException
     */
    public Object copy(Object bean,IdentityHashMap<Object,Object> copiedObjects) throws DOMCodecException {
        if(copiedObjects!=null){
            if(copiedObjects.containsKey(bean)){
            	Object copiedObj=copiedObjects.get(bean);
            	return copiedObj;
            }
        }
        Object beanCopy=null;
        BeanInfo bi = null;
        Class<? extends Object> c;
        c = bean.getClass();
        if (c.isArray()) {
            try {
                // recursive deep copy of an array
                int length = Array.getLength(bean);
                Class<?> compType = c.getComponentType();
                beanCopy = Array.newInstance(c.getComponentType(), length);
                copiedObjects.put(bean, beanCopy);
                for (int i = 0; i < length; i++) {
                    Object val = Array.get(bean, i);
                    if (compType.equals(String.class)) {
                        Array.set(beanCopy, i, new String((String) val));
                    } else {
                        Array.set(beanCopy, i, copy(val,copiedObjects));
                    }
                }
                return beanCopy;
            } catch (Exception e) {
                throw new DOMCodecException(e);
            }
        }
        if (Collection.class.isAssignableFrom(c)) {
            try {
                // recursive deep copy of an collection (set or list)
                Collection beanColl=(Collection) bean;
                
                Class<? extends Collection> collClass=beanColl.getClass();
                Collection copyColl=null;
                // exception if colection cannot be instantiated e.g.:
                //java.util.Arrays$ArrayList
                try{
                    copyColl=collClass.newInstance();
                }catch (InstantiationException ie){
                    if(bean instanceof java.util.List){
                        copyColl=new ArrayList();
                    }else if(bean instanceof Set){
                        copyColl=new HashSet();
                    }else{
                        throw new DOMCodecException(ie);
                    }
                }
                copiedObjects.put(bean, copyColl);
                for(Object co:beanColl){
                   Object coCopy=copy(co,copiedObjects);
                   copyColl.add(coCopy);
                }
                beanCopy=copyColl;
                return beanCopy;
            } catch (Exception e) {
                throw new DOMCodecException(e);
            }
        }
        if(c.isEnum()){
        	beanCopy=bean;
        }else if(Number.class.isAssignableFrom(c)){
        	// numbers are immutable
        	beanCopy=bean;
        }else if(UUID.class.equals(c)){
            // UUIDs are immutable
            beanCopy=bean;
        }else{
        try {
        	
            beanCopy = c.newInstance();
            copiedObjects.put(bean, beanCopy);
        } catch (Exception e) {
            throw new DOMCodecException(e);
        }
        }

        try {
            bi = Introspector.getBeanInfo(bean.getClass());
            //beanDefault = bean.getClass().newInstance();
        } catch (Exception ex) {
            throw new DOMCodecException(ex);
        }

        PropertyDescriptor[] pds = bi.getPropertyDescriptors();
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            Class<?> pClass = pd.getPropertyType();
            Method getMethod = pd.getReadMethod();
            Method setMethod = pd.getWriteMethod();
            String pName = pClass.getName();

            Object child = null;

            try {
                child = getMethod.invoke(bean, new Object[0]);
                if (child != null && setMethod!=null) {
                    if (pClass.isArray()) {
                        //Class compType = pClass.getComponentType();

                        Object childCopy = copy(child,copiedObjects);
                        setMethod.invoke(beanCopy, new Object[] { childCopy });
                    } else if (String.class.equals(pClass)) {

                        setMethod.invoke(beanCopy, new Object[] { new String(
                                (String) child) });

                    } else if (pClass.isPrimitive()) {
                        setMethod.invoke(beanCopy, new Object[] { child });
                    } else if (Boolean.class.equals(pClass)) {
                        setMethod.invoke(beanCopy, new Object[] { new Boolean((Boolean)child) });
                    } else if (Byte.class.equals(pClass)) {
                        setMethod.invoke(beanCopy, new Object[] { new Byte((Byte)child) });
                    } else if (Short.class.equals(pClass)) {
                        setMethod.invoke(beanCopy, new Object[] { new Short((Short)child) });
                    } else if (Integer.class.equals(pClass)) {
                        setMethod.invoke(beanCopy, new Object[] { new Integer((Integer)child) });
                    } else if (Long.class.equals(pClass)) {
                        setMethod.invoke(beanCopy, new Object[] { new Long((Long)child) });
                    } else if (Float.class.equals(pClass)) {
                        setMethod.invoke(beanCopy, new Object[] { new Float((Float)child) });
                    } else if (Double.class.equals(pClass)) {
                        setMethod.invoke(beanCopy, new Object[] { new Double((Double)child) });
                    }else if (URL.class.equals(pClass)) {
                        setMethod.invoke(beanCopy, new Object[] { new URL(((URL)child).toExternalForm()) });
                    }else if (URI.class.equals(pClass)) {
                        setMethod.invoke(beanCopy, new Object[] { new URI(((URI)child).toString()) });
                    }else if (Class.class.equals(pClass)) {
                        // Ignore
                    } else {
                        Object childCopy = copy(child,copiedObjects);
                        setMethod.invoke(beanCopy, new Object[] { childCopy });
                    }
                }
            } catch (Exception ex) {
                throw new DOMCodecException(ex);
            }
        }
        return beanCopy;
    }

    public Document createDocument(Object bean) throws DOMCodecException {

        Document document = db.newDocument();
        Class<?> c = bean.getClass();
        String pPackName = c.getPackage().getName();
        String elementName = c.getName().substring(pPackName.length() + 1);
        // inner classes are leaded by dollar sign
        elementName = elementName.replace('$', '_');
        if (!elementNameUpperCase) {
            elementName = new String(elementName.substring(0, 1).toLowerCase(Locale.ENGLISH)
                    + elementName.substring(1));
        }
        Element e = document.createElement(elementName);
        document.appendChild(e);
        try {
            appendToDOM(document, e, bean);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DOMCodecException(ex);
        }
        return document;
    }
    
    
//    private String objectToString(Object object){
//    	Class<?> pClass=object.getClass();
//    	
//    	if (pClass.equals(String.class)) {
//          return (String)object;
//        } else if (java.util.Date.class.isAssignableFrom(pClass)) {
//            	RFC3339DateTimeFormat dateFormat=new RFC3339DateTimeFormat();
//            	String formattedDateStr=dateFormat.format(object);
//            	return formattedDateStr;
//        } else {
//        	return object.toString();
//        }
//    }
    
    
    // returns the attribute name if an attribute, null else
    private String getAttributePropertyName(String pdName){
    	
    	if(attributePrefix==null){
    		return null;
    	}
    	 if (pdName.startsWith(attributePrefix)) {
             String attrName = pdName.substring(attributePrefix
                     .length());
             attrName = new String(attrName.substring(0, 1)
                     .toLowerCase(Locale.ENGLISH)
                     + attrName.substring(1));
            return attrName;
         }
    	 return null;
    }

    /**
     * Convert a bean to DOM element and append it as child.
     * 
     * @param d
     *            the whole document
     * @param e
     *            the element to which the generated DOM element will appended
     *            as child
     * @param bean
     *            the bean to convert to DOM element
     * @throws DOMCodecException
     * @throws DOMCodecException
     */
    public void appendToDOM(Document d, Element e, Object bean)
            throws DOMCodecException {
        appendToDOM(d, e, bean, false);
    }

    /**
     * Convert a bean to DOM element and append it as child.
     * 
     * @param d
     *            the whole document
     * @param e
     *            the element to which the generated DOM element will appended
     *            as child
     * @param bean
     *            the bean to convert to DOM element
     * @param appendIfDefault
     *            if true elements are appended even if they are equal to the
     *            default
     * 
     * @throws DOMCodecException
     */
    public void appendToDOM(Document d, Element e, Object bean,
            boolean appendIfDefault) throws DOMCodecException {
        BeanInfo bi = null;
        
        Class<? extends Object> c = bean.getClass();
        
        if (e instanceof Document) {
            d = (Document) e;
        } else {
            d = e.getOwnerDocument();
        }
     // check if the type directly implements the DOM convertible interface
        // if the class is only derived from a DOM convertible class
        // the toElement() method should not be called,because properties
        // of the derived class may be ignored
        Type[] types=c.getGenericInterfaces();
        for(Type t:types){
        	if(t.equals(DOMElementConvertible.class)){
        		e.appendChild(((DOMElementConvertible)bean).toElement(d));
        		return;
        	}
        }
       
        Object beanDefault = null;

        try {
            bi = Introspector.getBeanInfo(c);
            if(! (Number.class.isAssignableFrom(c))){
            	beanDefault = c.newInstance();
            }
            
            // beanDefault=Beans.instantiate(null,bean.getClass().getName());
        } catch (Exception ex) {
            throw new DOMCodecException(ex);
        }
        PropertyDescriptor[] allPds = bi.getPropertyDescriptors();
        PropertyDescriptor[] pds = null;
        DOMElements domElementsAnnot = c.getAnnotation(DOMElements.class);
        String[] domElementNames = null;
        DOMTextNodePropertyName domTextNodePropAnnot = c.getAnnotation(DOMTextNodePropertyName.class);
        String domTextNodePropertyName = null;
        boolean domTextNodeAsCDATA=false;
        if(domTextNodePropAnnot!=null){
            domTextNodePropertyName=domTextNodePropAnnot.value();
            domTextNodeAsCDATA=domTextNodePropAnnot.asCDATANode();
        }
        DOMAttributes domAttributesAnnot = c.getAnnotation(DOMAttributes.class);
        String[] domAttributeNames = null;
        if (domAttributesAnnot != null) {
            domAttributeNames = domAttributesAnnot.value();
        }
        
        if (domElementsAnnot != null) {
            domElementNames = domElementsAnnot.value();

            ArrayList<PropertyDescriptor> pdsTmp = new ArrayList<PropertyDescriptor>();
            for (String elmName : domElementNames) {
                for (PropertyDescriptor pd : allPds) {
                    if (pd.getName().equals(elmName)) {
                        pdsTmp.add(pd);
                        break;
                    }
                }
            }
            pds = pdsTmp.toArray(new PropertyDescriptor[0]);
           
        } else {
            // If no DOMElements annotation set
            // every property which is not mapped as attribute will be converted
            if (domAttributeNames != null) {
                ArrayList<PropertyDescriptor> pdsArrList = new ArrayList<PropertyDescriptor>();
                for (PropertyDescriptor pd : allPds) {
                    // check if attribute
                    boolean isAttr = false;
                    for (String a : domAttributeNames) {
                        if (pd.getName().equals(a)) {
                            isAttr = true;
                            break;
                        }
                    }
                    if (!isAttr) {
                        pdsArrList.add(pd);
                    }
                }
                pds = pdsArrList.toArray(new PropertyDescriptor[0]);
            } else {
                pds = allPds;
            }
        }

        // Attributes first

        if (domAttributeNames != null) {
            for (String attrName : domAttributeNames) {
                for (int i = 0; i < allPds.length; i++) {
                    PropertyDescriptor pd = allPds[i];
                    String pdName = pd.getName();
                    if (pdName.equals(attrName)) {

                       // Class pClass = pd.getPropertyType();
                        Method getMethod = pd.getReadMethod();
                        //String pName = pClass.getName();
                        Object child = null;
                        Object defChild = null;
                        
                        try {
                            child = getMethod.invoke(bean, new Object[0]);
                            defChild = getMethod.invoke(beanDefault,
                                    new Object[0]);
                        } catch (Exception ex) {
                            throw new DOMCodecException(ex);
                        }
                        if (child != null
                                && ((defChild == null) || !(defChild
                                        .equals(child)))) {
                        	Class<?> childClass=child.getClass();
                        	String attrText;
                        	if(Date.class.isAssignableFrom(childClass)){
                        		RFC3339DateTimeFormat rfcDateTimeFormat=new RFC3339DateTimeFormat();
                        		Temporal temporalAnno=getMethod.getAnnotation(Temporal.class);
                        		if(temporalAnno!=null){
                        			
                        			if(Temporal.Type.DATE.equals(temporalAnno.type())){
                        				rfcDateTimeFormat.setTemporalType(TemporalType.DATE);
                        			}
                        		}
                        		attrText=rfcDateTimeFormat.format(child);
                        	}else{
                        		attrText=child.toString();
                        	}
                        	e.setAttribute(attrName, attrText);

                        }
                    }

                }
            }
        } else {

            for (int i = 0; i < allPds.length; i++) {
                PropertyDescriptor pd = allPds[i];
                Class<?> pClass = pd.getPropertyType();
                Method getMethod = pd.getReadMethod();
//                String pName = pClass.getName();
                String pdName = pd.getName();
                Object child = null;
                Object defChild = null;
                try {
                    child = getMethod.invoke(bean, new Object[0]);
                    defChild = getMethod.invoke(beanDefault, new Object[0]);
                } catch (Exception ex) {
                    throw new DOMCodecException(ex);
                }

                if (pClass.equals(String.class)) {
                    if (child != null
                            && ((defChild == null) || !((String) defChild)
                                    .equals((String) child))) {
                        // Check if attribute
                        if (pdName.startsWith(attributePrefix)) {
                            String attrName = pdName.substring(attributePrefix
                                    .length());
                            attrName = new String(attrName.substring(0, 1).toLowerCase(Locale.ENGLISH)
                                    + attrName.substring(1));
                            e.setAttribute(attrName, (String) (child));
                        }
                    }
                } else if (pClass.isPrimitive()) {
                    if (child != null
                            && ((defChild == null) || !(defChild.equals(child)))) {
                        // Check if attribute
                        if (pdName.startsWith(attributePrefix)) {
                            String attrName = pdName.substring(attributePrefix
                                    .length());
                            attrName = new String(attrName.substring(0, 1)
                                    .toLowerCase(Locale.ENGLISH)
                                    + attrName.substring(1));
                            e.setAttribute(attrName, child.toString());
                        }
                    }
                }
            }
        }

        // Text node
        
        if(domTextNodePropertyName!=null){
        	for (int i = 0; i < allPds.length; i++) {
                PropertyDescriptor pd = allPds[i];
                if(domTextNodePropertyName.equals(pd.getName())){
                Class<?> pClass = pd.getPropertyType();
                Method getMethod = pd.getReadMethod();
                
               
                if(!String.class.equals(pClass)){
                	throw new DOMCodecException("Text node property \""+domTextNodePropertyName+"\" must be String type!");
                }
                Object child = null;
               // Object defChild = null;
                try {
                    child = getMethod.invoke(bean, new Object[0]);
                    //defChild = getMethod.invoke(beanDefault, new Object[0]);
                } catch (Exception ex) {
                    throw new DOMCodecException(ex);
                }
                if(child!=null){
                	if(domTextNodeAsCDATA){
                	e.appendChild(d.createCDATASection((String)(child)));
                	}else{
                	e.setTextContent((String)child);
                	}
                }
                }
        	}
        }
        
        
        // Now elements

        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            Class<?> pClass = pd.getPropertyType();
            Method getMethod = pd.getReadMethod();
            ipsk.beans.dom.DOMElement propElemAnno=getMethod.getAnnotation(ipsk.beans.dom.DOMElement.class);
//            String pName = pClass.getName();
            String pdName = pd.getName();
            Object child = null;
            Object defChild = null;
            try {
                child = getMethod.invoke(bean, new Object[0]);
                defChild = getMethod.invoke(beanDefault, new Object[0]);
            } catch (Exception ex) {
                throw new DOMCodecException(ex);
            }

            if (pClass.isArray()) {
              
                if (child != null
                        && ((defChild == null) || !(defChild.equals(child)))) {
                    Class<?> arrClass = pClass.getComponentType();
                    // if the class is annotated as standalone DOM tree
                    // append only the ID as attribute
                    DOMRoot domRootAnno=arrClass.getAnnotation(DOMRoot.class);
                    PropertyDescriptor arrChLinkIDPd=null;
                    if(domRootAnno!=null){
                    	BeanInfo arrChBi=null;
                    	try {
                    		arrChBi=Introspector.getBeanInfo(arrClass);
						} catch (IntrospectionException e1) {
							throw new DOMCodecException(e1);
						}
						PropertyDescriptor[] arrChPds=arrChBi.getPropertyDescriptors();
						for(PropertyDescriptor arrChPd:arrChPds){
							Method arrChGetMethod=arrChPd.getReadMethod();
							LinkID linkIdanno=arrChGetMethod.getAnnotation(LinkID.class);
							if(linkIdanno!=null){
								// found Link ID property
								arrChLinkIDPd=arrChPd;
								break;
							}
						}
                    }
                    if (arrClass.isArray()) {
                    	  // TODO
                        throw new DOMCodecException(
                                "Multidimension arrays cannot be mapped !");
                    }
                    if (child != null) {
                        for (int a = 0; a < Array.getLength(child); a++) {

                            Object arrChild = Array.get(child, a);
//                            String arrClassname = arrClass.getName();
                            if (arrClass.equals(java.lang.String.class)) {
                                Element se = d.createElement(pdName);
                                se.appendChild(d
                                        .createTextNode((String) (arrChild)));
                                e.appendChild(se);
                            } else if (arrClass.isPrimitive()) {

                                Element se = d.createElement(pdName);
                                se.appendChild(d.createTextNode(arrChild
                                        .toString()));
                                e.appendChild(se);

                            } else if (arrClass.equals(Boolean.class)) {

                                Element se = d.createElement(pdName);
                                se.appendChild(d.createTextNode(arrChild
                                        .toString()));
                                e.appendChild(se);

                            } else if (pClass.equals(java.lang.Class.class)) {
                                // Not interesting
                            } else {
                                String pdElementName = null;
                                if(propElemAnno!=null){
                                    pdElementName=propElemAnno.name();
                                }
                                if(pdElementName==null){
                                    if (elementNameUpperCase) {
                                        pdElementName = pdName.substring(0, 1)
                                        .toUpperCase(Locale.ENGLISH);
                                        pdElementName = pdElementName.concat(pd
                                                .getName().substring(1));
                                    } else {
                                        pdElementName = new String(pdName);
                                    }
                                }
                                Element se = d.createElement(pdElementName);
                                e.appendChild(se);
                                
                                // fixedElements.add(se);
                                if (arrChild != null) {
                 
                                    if(domRootAnno!=null){
                                    	Method rMethod=arrChLinkIDPd.getReadMethod();
                                    	Object linkVal;
										try {
											linkVal = rMethod.invoke(arrChild, new Object[0]);
										} catch (Exception e1) {
											throw new DOMCodecException(e1);
										}
                						se.setAttribute(arrChLinkIDPd.getName(), linkVal.toString());
                                    }else{
                                    	// class belongs to same namespace
                                    	 boolean appIfDefault=true;
                                         RemoveIfDefault riDAnnot=getMethod.getAnnotation(RemoveIfDefault.class);
                                        if(riDAnnot!=null)appIfDefault=false;
                                        appendToDOM(d, se, arrChild, appIfDefault);
                                    }
                                   
                                }
                            }
                        }
                    }
                }

            }else if(java.util.List.class.isAssignableFrom(pClass)){ 
                if (child != null
                        && ((defChild == null) || !(defChild.equals(child)))) {
                     
                    
                    PropertyDescriptor collChLinkIDPd=null;
//                    if(domRootAnno!=null){
                        // get collection class type
                        Type rt = pd.getReadMethod().getGenericReturnType();
                        if(rt instanceof ParameterizedType){
                            ParameterizedType prt=(ParameterizedType)rt;
                            Type[] prtTypes=prt.getActualTypeArguments();
                            if(prtTypes!=null && prtTypes.length==1){
                                Type collParametrizedType=prtTypes[0];
                                if(collParametrizedType instanceof Class<?>){
                                    Class<?> collClass=(Class<?>)collParametrizedType;
                                    DOMRoot domRootAnno=collClass.getAnnotation(DOMRoot.class);
                                    if(domRootAnno!=null){
                                    BeanInfo collChBi=null;
                                    try {
                                        collChBi=Introspector.getBeanInfo(collClass);
                                    } catch (IntrospectionException e1) {
                                        throw new DOMCodecException(e1);
                                    }
                                    PropertyDescriptor[] arrChPds=collChBi.getPropertyDescriptors();
                                    for(PropertyDescriptor arrChPd:arrChPds){
                                        Method arrChGetMethod=arrChPd.getReadMethod();
                                        LinkID linkIdanno=arrChGetMethod.getAnnotation(LinkID.class);
                                        if(linkIdanno!=null){
                                            // found Link ID property
                                            collChLinkIDPd=arrChPd;
                                            break;
                                        }
                                    }
                                    }
                                }
                            }
                        }
//                    }
                    String collElementName=null;
                    Element collParent=e;
                    DOMCollectionElement domCollElemAnno=getMethod.getAnnotation(DOMCollectionElement.class);
                    if(domCollElemAnno!=null){
                        // use enclosing element
                        collElementName=domCollElemAnno.collectionElementName();
                        collParent=d.createElement(pdName);
                        e.appendChild(collParent);                  
                    }else{
                        collParent=e;
                    }
                    // if the class is annotated as standalone DOM tree
                    // append only the ID as attribute
                   
                    for (Object setChild:(java.util.List<?>)child) {

                        Class<?> setClass=setChild.getClass();
//                      String setClassname = setClass.getName();
                        if (String.class.equals(setClass)) {
                            Element se = d.createElement(pdName);
                            se.appendChild(d
                                    .createTextNode((String) (setChild)));
                            collParent.appendChild(se);
                        } else if (setClass.isPrimitive()) {

                            Element se = d.createElement(pdName);
                            se.appendChild(d.createTextNode(setChild
                                    .toString()));
                            collParent.appendChild(se);

                        } else if (Class.class.equals(setClass)) {
                            // Ignore
                        } else {

                            String pdElementName = null;
                            if(propElemAnno!=null){
                                pdElementName=propElemAnno.name();
                            }
                            if(pdElementName==null){
                                if (elementNameUpperCase) {
                                    pdElementName = pdName.substring(0, 1)
                                    .toUpperCase(Locale.ENGLISH);
                                    pdElementName = pdElementName.concat(pd
                                            .getName().substring(1));
                                } else {
                                    pdElementName = new String(pdName);
                                }
                            }
                            Element se=null;
                            if(collElementName==null){
                                se = d.createElement(pdElementName);

                            }else{
                                se= d.createElement(collElementName);
                            }
                            collParent.appendChild(se);
                            // fixedElements.add(se);
                            if (setChild != null) {
                                if(collChLinkIDPd!=null){
                                    Method rMethod=collChLinkIDPd.getReadMethod();
                                    Object linkVal;
                                    try {
                                        linkVal = rMethod.invoke(setChild, new Object[0]);
                                    } catch (Exception e1) {
                                        throw new DOMCodecException(e1);
                                    }
                                    se.setAttribute(collChLinkIDPd.getName(), linkVal.toString());
                                }else{
                                    boolean appIfDefault=true;
                                    RemoveIfDefault riDAnnot=getMethod.getAnnotation(RemoveIfDefault.class);
                                    if(riDAnnot!=null)appIfDefault=false;
                                    appendToDOM(d, se, setChild, appIfDefault);
                                }
                            }
                        }
                    }
                    

                }
            }else if (Set.class.isAssignableFrom(pClass)) {

            	if (child != null
            			&& ((defChild == null) || !(defChild.equals(child)))) {
            		 
            		
                    PropertyDescriptor collChLinkIDPd=null;
//                    if(domRootAnno!=null){
                    	// get collection class type
                    	Type rt = pd.getReadMethod().getGenericReturnType();
                    	if(rt instanceof ParameterizedType){
                    		ParameterizedType prt=(ParameterizedType)rt;
                    		Type[] prtTypes=prt.getActualTypeArguments();
                    		if(prtTypes!=null && prtTypes.length==1){
                    			Type collParametrizedType=prtTypes[0];
                    			if(collParametrizedType instanceof Class<?>){
                    				Class<?> collClass=(Class<?>)collParametrizedType;
                    				DOMRoot domRootAnno=collClass.getAnnotation(DOMRoot.class);
                    				if(domRootAnno!=null){
                    				BeanInfo collChBi=null;
                    				try {
                    					collChBi=Introspector.getBeanInfo(collClass);
                    				} catch (IntrospectionException e1) {
                    					throw new DOMCodecException(e1);
                    				}
                    				PropertyDescriptor[] arrChPds=collChBi.getPropertyDescriptors();
                    				for(PropertyDescriptor arrChPd:arrChPds){
                    					Method arrChGetMethod=arrChPd.getReadMethod();
                    					LinkID linkIdanno=arrChGetMethod.getAnnotation(LinkID.class);
                    					if(linkIdanno!=null){
                    						// found Link ID property
                    						collChLinkIDPd=arrChPd;
                    						break;
                    					}
                    				}
                    				}
                    			}
                    		}
                    	}
//                    }
            		String collElementName=null;
            		Element collParent=e;
            		DOMCollectionElement domCollElemAnno=getMethod.getAnnotation(DOMCollectionElement.class);
            		if(domCollElemAnno!=null){
            			// use enclosing element
            			collElementName=domCollElemAnno.collectionElementName();
            			collParent=d.createElement(pdName);
            			e.appendChild(collParent);            		
            		}else{
            			collParent=e;
            		}
                    // if the class is annotated as standalone DOM tree
                    // append only the ID as attribute
                   
                    	
            		for (Object setChild:(Set<?>)child) {

            			Class<?> setClass=setChild.getClass();
//            			String setClassname = setClass.getName();
            			if (String.class.equals(setClass)) {
            				Element se = d.createElement(pdName);
            				se.appendChild(d
            						.createTextNode((String) (setChild)));
            				collParent.appendChild(se);
            			} else if (setClass.isPrimitive()) {

            				Element se = d.createElement(pdName);
            				se.appendChild(d.createTextNode(setChild
            						.toString()));
            				collParent.appendChild(se);

            			} else if (Class.class.equals(setClass)) {
            				// Ignore
            			} else {

            			    String pdElementName = null;
            			    if(propElemAnno!=null){
            			        pdElementName=propElemAnno.name();
            			    }
            			    if(pdElementName==null){
            			        if (elementNameUpperCase) {
            			            pdElementName = pdName.substring(0, 1)
            			            .toUpperCase(Locale.ENGLISH);
            			            pdElementName = pdElementName.concat(pd
            			                    .getName().substring(1));
            			        } else {
            			            pdElementName = new String(pdName);
            			        }
            			    }
            			    Element se=null;
            			    if(collElementName==null){
            			        se = d.createElement(pdElementName);

            			    }else{
            			        se= d.createElement(collElementName);
            			    }
            			    collParent.appendChild(se);
            			    // fixedElements.add(se);
            			    if (setChild != null) {
            			    	if(collChLinkIDPd!=null){
            			    		Method rMethod=collChLinkIDPd.getReadMethod();
                                	Object linkVal;
									try {
										linkVal = rMethod.invoke(setChild, new Object[0]);
									} catch (Exception e1) {
										throw new DOMCodecException(e1);
									}
            						se.setAttribute(collChLinkIDPd.getName(), linkVal.toString());
            			    	}else{
            			    		boolean appIfDefault=true;
            			    		RemoveIfDefault riDAnnot=getMethod.getAnnotation(RemoveIfDefault.class);
            			    		if(riDAnnot!=null)appIfDefault=false;
            			    		appendToDOM(d, se, setChild, appIfDefault);
            			    	}
            			    }
            			}
            		}
            		

            	}

            } else if (Map.class.isAssignableFrom(pClass)) {

            	if (child != null
            			&& ((defChild == null) || !(defChild.equals(child)))) {
            		 
            		
                    PropertyDescriptor collChLinkIDPd=null;
//                    if(domRootAnno!=null){
                    	// get collection class type
                    	Type rt = pd.getReadMethod().getGenericReturnType();
                    	if(rt instanceof ParameterizedType){
                    		ParameterizedType prt=(ParameterizedType)rt;
                    		Type[] prtTypes=prt.getActualTypeArguments();
                    		if(prtTypes!=null && prtTypes.length==2){
                    			Type keyParametrizedType=prtTypes[0];
                    			if(keyParametrizedType instanceof Class<?>){
                    				Class<?> keyClass=(Class<?>)keyParametrizedType;
                    				DOMRoot domRootAnno=keyClass.getAnnotation(DOMRoot.class);
                    				if(domRootAnno!=null){
                    				BeanInfo collChBi=null;
                    				try {
                    					collChBi=Introspector.getBeanInfo(keyClass);
                    				} catch (IntrospectionException e1) {
                    					throw new DOMCodecException(e1);
                    				}
                    				PropertyDescriptor[] arrChPds=collChBi.getPropertyDescriptors();
                    				for(PropertyDescriptor arrChPd:arrChPds){
                    					Method arrChGetMethod=arrChPd.getReadMethod();
                    					LinkID linkIdanno=arrChGetMethod.getAnnotation(LinkID.class);
                    					if(linkIdanno!=null){
                    						// found Link ID property
                    						collChLinkIDPd=arrChPd;
                    						break;
                    					}
                    				}
                    				}
                    			}
                    		}
                    	}
//                    }
            		String collElementName=null;
            		Element collParent=e;
            		DOMCollectionElement domCollElemAnno=getMethod.getAnnotation(DOMCollectionElement.class);
            		if(domCollElemAnno!=null){
            			// use enclosing element
            			collElementName=domCollElemAnno.collectionElementName();
            			collParent=d.createElement(pdName);
            			e.appendChild(collParent);            		
            		}else{
            			collParent=e;
            		}
                   
            		Element gse = d.createElement(pdName);
            		collParent.appendChild(gse);
            		Map<?,?> m=(Map<?,?>)child;
                    Set<?> keySet=m.keySet();
            		for (Object keyChild:keySet) {
            			
            			Element se = d.createElement("entry");
            			
            			gse.appendChild(se);
            			// add key element
            			if(keyChild!=null){
            			Node keyNode=se.appendChild(d.createElement("key"));
            			Class<?> keyClass=keyChild.getClass();
//            			String setClassname = setClass.getName();
            			if (String.class.equals(keyClass)) {
            				keyNode.appendChild(d
            						.createTextNode((String) (keyChild)));
            			} else if (keyClass.isPrimitive()) {
            				keyNode.appendChild(d.createTextNode(keyChild
            						.toString()));

            			} else if (Class.class.equals(keyClass)) {
            				// Ignore
            			}else{
            				throw new DOMCodecException("For maps type of key is restricted to String or primitive type!");
            			}
            			}
            			// add value element
            			Object valChild=m.get(keyChild);
            			if(valChild!=null){
            			Node valNode=se.appendChild(d.createElement("value"));
            			Class<?> valClass=valChild.getClass();
//            			String setClassname = setClass.getName();
            			if (String.class.equals(valClass)) {
            				valNode.appendChild(d
            						.createTextNode((String) (valChild)));
            				
            			} else if (valClass.isPrimitive()) {
            				valNode.appendChild(d.createTextNode(valChild
            						.toString()));

            			} else if (Class.class.equals(valClass)) {
            				// Ignore
            			}else{
            				throw new DOMCodecException("For maps type of kex is resctricteed to String or primitive type!");
            			}
            			}
            		}
            		

            	}

            }else if (pClass.isEnum()) {
            	if (child != null
                        && ((defChild == null) || !(defChild).equals(child))) {
                    // Check if attribute
                    if (pdName.startsWith(attributePrefix)) {
                        String attrName = pdName.substring(attributePrefix
                                .length());
                        attrName = new String(attrName.substring(0, 1)
                                .toLowerCase(Locale.ENGLISH)
                                + attrName.substring(1));
                        e.setAttribute(attrName, child.toString());
                    } else {
                        Element se = d.createElement(pdName);
                        se.appendChild(d.createTextNode(child.toString()));
                        e.appendChild(se);
                    }
                }
            	
            }else if (pClass.equals(java.lang.String.class)) {
                if (child != null
                        && ((defChild == null) || !((String) defChild)
                                .equals((String) child))) {
                    // Check if attribute
                    if (pdName.startsWith(attributePrefix)) {
                        String attrName = pdName.substring(attributePrefix
                                .length());
                        attrName = new String(attrName.substring(0, 1)
                                .toLowerCase(Locale.ENGLISH)
                                + attrName.substring(1));
                        e.setAttribute(attrName, (String) (child));
                    } else {
                    	// Check if this is the text node property
                    	if(pdName.equals(domTextNodePropertyName)){
//                    		e.appendChild(d.createTextNode((String)(child)));
                    	    // already done
                    	}else{
                    		Element se = d.createElement(pdName);
                    		se.appendChild(d.createTextNode((String) (child)));
                    		e.appendChild(se);
                    	}
                    }
                }
            } else if (java.util.Date.class.isAssignableFrom(pClass)) {
                if (child != null
                        && ((defChild == null) || !((Date) defChild)
                                .equals((Date) child))) {
                	RFC3339DateTimeFormat dateFormat=new RFC3339DateTimeFormat();
            		Temporal temporalAnno=getMethod.getAnnotation(Temporal.class);
            		if(temporalAnno!=null){
            			if(Temporal.Type.DATE.equals(temporalAnno.type())){
            				dateFormat.setTemporalType(TemporalType.DATE);
            			}
            		}
                	String formattedDateStr=dateFormat.format(child);
                    // Check if attribute
                    if (pdName.startsWith(attributePrefix)) {
                        String attrName = pdName.substring(attributePrefix
                                .length());
                        attrName = new String(attrName.substring(0, 1)
                                .toLowerCase(Locale.ENGLISH)
                                + attrName.substring(1));
                        e.setAttribute(attrName, formattedDateStr);
                    } else {
                        Element se = d.createElement(pdName);
                        
                        
                        se.appendChild(d.createTextNode(formattedDateStr));
                        e.appendChild(se);
                    }
                }
            }else if (java.util.UUID.class.isAssignableFrom(pClass)) {
                if (child != null
                        && ((defChild == null) || !((UUID) defChild)
                                .equals((UUID) child))) {
                    
                    String uuidStr=child.toString();
                    // Check if attribute
                    if (pdName.startsWith(attributePrefix)) {
                        String attrName = pdName.substring(attributePrefix
                                .length());
                        attrName = new String(attrName.substring(0, 1)
                                .toLowerCase(Locale.ENGLISH)
                                + attrName.substring(1));
                        e.setAttribute(attrName, uuidStr);
                    } else {
                        Element se = d.createElement(pdName);
                        
                        
                        se.appendChild(d.createTextNode(uuidStr));
                        e.appendChild(se);
                    }
                }
            }else if (pClass.equals(Boolean.class)) {

                if (child != null
                        && ((defChild == null) || !(defChild).equals(child))) {
                    // Check if attribute
                    if (pdName.startsWith(attributePrefix)) {
                        String attrName = pdName.substring(attributePrefix
                                .length());
                        attrName = new String(attrName.substring(0, 1)
                                .toLowerCase(Locale.ENGLISH)
                                + attrName.substring(1));
                        e.setAttribute(attrName, child.toString());
                    } else {
                        Element se = d.createElement(pdName);
                        se.appendChild(d.createTextNode(child.toString()));
                        e.appendChild(se);
                    }
                }
            } else if (pClass.isPrimitive() || Number.class.isAssignableFrom(pClass)) {

                if (child != null
                        && ((defChild == null) || !(defChild).equals(child))) {
                    // Check if attribute
                    if (pdName.startsWith(attributePrefix)) {
                        String attrName = pdName.substring(attributePrefix
                                .length());
                        attrName = new String(attrName.substring(0, 1)
                                .toLowerCase(Locale.ENGLISH)
                                + attrName.substring(1));
                        e.setAttribute(attrName, child.toString());
                    } else {
                        Element se = d.createElement(pdName);
                        se.appendChild(d.createTextNode(child.toString()));
                        e.appendChild(se);
                    }
                }
            } else if (pClass.equals(java.lang.Class.class)) {
                // Ignore
            } else {
                if (child != null) {
                    String pdElementName = null;
                    if(propElemAnno!=null){
                        pdElementName=propElemAnno.name();
                    }
                    if(pdElementName==null){
                        if (elementNameUpperCase) {
                            pdElementName = pdName.substring(0, 1)
                            .toUpperCase(Locale.ENGLISH);
                            pdElementName = pdElementName.concat(pd
                                    .getName().substring(1));
                        } else {
                            pdElementName = new String(pdName);
                        }
                    }
                    Element se = d.createElement(pdElementName);
                    e.appendChild(se);
                    
                    // if the class is annotated as standalone DOM tree
                    // append only the ID as attribute
                    DOMRoot domRootAnno=pClass.getAnnotation(DOMRoot.class);
                    if(domRootAnno!=null){
                    	BeanInfo chBi=null;
                    	try {
                    		chBi=Introspector.getBeanInfo(pClass);
						} catch (IntrospectionException e1) {
							throw new DOMCodecException(e1);
						}
						PropertyDescriptor[] chPds=chBi.getPropertyDescriptors();
						for(PropertyDescriptor chPd:chPds){
							Method chGetMethod=chPd.getReadMethod();
							LinkID linkIdanno=chGetMethod.getAnnotation(LinkID.class);
							if(linkIdanno!=null){
								// found Link ID property
								String linkIDpropname=chPd.getName();
								try {
									Object linkVal=chGetMethod.invoke(child, new Object[0]);
									se.setAttribute(linkIDpropname, linkVal.toString());
									break;
								} catch (Exception e1) {
									throw new DOMCodecException(e1);
								} 
							}
						}
                    }else{
                    	// class belongs to same namespace
                    	appendToDOM(d, se, child);
                    }
                }
            }
        }
        // Remove empty elements

        // check for elements and text nodes (attributes are not included here)

        if (!appendIfDefault) {
            NodeList nl = e.getChildNodes();
            boolean isEmpty = true;
            for (int l = 0; l < nl.getLength(); l++) {
                Node n = nl.item(l);
                // if (fixedElements.contains(n))continue;
                short nodeType = n.getNodeType();
                if (nodeType == Node.ATTRIBUTE_NODE
                        || nodeType == Node.ELEMENT_NODE
                        || nodeType == Node.TEXT_NODE
                        || nodeType == Node.DOCUMENT_NODE) {
                    isEmpty = false;

                }
            }

            // check for attributes
            NamedNodeMap nMap = e.getAttributes();
            if (nMap.getLength() > 0)
                isEmpty = false;
            Node parent = e.getParentNode();
            if (isEmpty && parent.getNodeType() != Node.DOCUMENT_NODE)
                parent.removeChild(e);
        }
    }

    /**
     * Test method. generates Java test objects, converts them to DOM, write XML
     * file from DOM, reads XML file, converts to DOM and finally to Java
     * objects again.
     * 
     * @param args
     */
    public static void main(String[] args) {
        File f = new File("test.xml");
        try {
            Package basePack = Class.forName("ipsk.beans.test.Root")
                    .getPackage();
            DOMCodec ph = new DOMCodec(basePack, false);
            DOMConverter dc = new DOMConverter();

            Root r = new Root();
            r.setRouting(new int[]{3,5});
            r.setAttributeId("test");
            r.setAnnotatedTestAttribute("testAttribut Annotated");
            r.setName("Heyhey");
            HashSet<Child1> childrenSet=new HashSet<Child1>();
            Child1 childrenSet1=new Child1();
            childrenSet1.setSampleRate((float)96000.0);
            Child1 childrenSet2=new Child1();
            childrenSet2.setSampleRate((float)24000.0);
            childrenSet.add(childrenSet1);
            childrenSet.add(childrenSet2);
            r.setChildrenSet(childrenSet);
            String s1 = "Erstes String element";
            String s2 = "Zweites String element";
            // //String s1 =new String();
            // //String s2=new String();
            String[] sd1 = new String[] { s1, s2 };
            //            
            r.setMixerNames(sd1);
            r.setWeekDay(WeekDays.WEDNESDAY);
            r.setTextContent("Dies ist Text!");
            Child1 c1 = new Child1();
            c1.setSampleSizes(new int[] { 8, 16, 24, 32 });
            c1.setAttributeSelection(Selection.GREEN);
            // Child1 c2 = new Child1();
            // // //c2.setAttributeType("AUTO");
            c1.setAttributeType("NORM");
            // // c1.setSampleRate((float) 22050.0);
            r.setChild(new Child1[] { c1 });
            // Child1 singleChild=new Child1();
            // singleChild.setSampleRate(12000);
            // r.setSingleChild(singleChild);
            Child1 c3 =new Child1();
            c3.setSampleRate(128000);
            r.setAnotherChild(new Child1[]{c3,c3});
            
            Document d = ph.createDocument(r);

            FileOutputStream fos = new FileOutputStream(f);
            dc.writeXML(d, new OutputStreamWriter(System.out));
            dc.writeXML(d, new OutputStreamWriter(fos));
            fos.close();

            Document d2 = dc.readXML(new FileInputStream(f));
            Root rr = (Root) ph.readDocument(d2);
            System.out.println(rr.getName() + " "+ rr.getChild()[0].getAttributeType()+" "+rr.getTextContent());
            ph.createDocument(new SuperRoot());
        } catch (DOMCodecException e) {
            e.printStackTrace();
        } catch (DOMConverterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // ph.save();
        //	 	
        // JFrame f=new JFrame("Project");
        // ph.createGUI(f);

    }
}
