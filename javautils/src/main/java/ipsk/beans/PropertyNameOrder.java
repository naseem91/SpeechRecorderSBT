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

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * 
 * @author klausj
 *
 */
public class PropertyNameOrder {

	public final static String PROPERTY_NAME_WILDCARD = "*";

	private String[] availableProperties;

	
	private ArrayList<String> order = null;

	public boolean removeAll(Collection<String> c) {
		return order.removeAll(c);
	}


	public PropertyNameOrder(String[] availableProperties) {
		this.availableProperties = availableProperties;
	}
	
	
	public PropertyNameOrder(Set<String> availablePropertiesSet) {
		this(availablePropertiesSet.toArray(new String[0]));
	}


	public PropertyNameOrder(PropertyDescriptor[] persistencePropertyDescriptors) {
		availableProperties=new String[persistencePropertyDescriptors.length];
		for(int i=0;i<persistencePropertyDescriptors.length;i++){
			availableProperties[i]=persistencePropertyDescriptors[i].getName();
		}
	}


	public String[] getOrder(){
		if(order==null){
			return availableProperties;
		}else{
			return order.toArray(new String[0]);
		}
	}
	
//	public void sortByPreferredOrder(String[] prefProperties) {
//		if(prefProperties==null)return;
//		ArrayList<String> newOrder = new ArrayList<String>();
//		int asteriskInsertPoint = -1;
//		for (int i = 0; i < prefProperties.length; i++) {
//			String prefProp = prefProperties[i];
//			if (prefProp.equals(PROPERTY_NAME_WILDCARD)) {
//				asteriskInsertPoint = newOrder.size();
//			} else {
//				if (order != null) {
//					for (int j = 0; j < order.size(); j++) {
//						String oProp = order.get(j);
//						if (oProp.equals(prefProp) && !newOrder.contains(prefProp)) {
//							newOrder.add(prefProp);
//						}
//					}
//				} else {
//					for (int k = 0; k < availableProperties.length; k++) {
//						String aProp = availableProperties[k];
//						if (aProp.equals(prefProp) && !newOrder.contains(prefProp)) {
//							newOrder.add(prefProp);
//						}
//					}
//				}
//			}
//		}
//		
//		
//		
//		if (asteriskInsertPoint >= 0) {
//			int asteriskInsertPoint2 = -1;
//			if (order != null) {
//				for (int j = 0; j < order.size(); j++) {
//					String prefProp = order.get(j);
//					if (prefProp.equals(PROPERTY_NAME_WILDCARD)) {
//						asteriskInsertPoint = newOrder.size();
//					} else {
//						if (!newOrder.contains(prefProp)) {
//							newOrder.add(asteriskInsertPoint++,prefProp);
//						}
//					}
//				}
//				
//				
//					for (int k = 0; k < availableProperties.length; k++) {
//						String prefProp = availableProperties[k];
//						if (!newOrder.contains(prefProp)) {
//							if(asteriskInsertPoint2>=0){
//								newOrder.add(asteriskInsertPoint2++,prefProp);
//							}else{
//								newOrder.add(prefProp);
//							}
//						}
//					}
//				
//				
//			} else {
//				for (int k = 0; k < availableProperties.length; k++) {
//					String prefProp = availableProperties[k];
//					if (!newOrder.contains(prefProp)) {
//						newOrder.add(asteriskInsertPoint++,prefProp);
//					}
//				}
//			}
//		}
//		order=newOrder;
//	}
	
	public void applyPreferredOrder(String[] prefProperties) {
		if(prefProperties==null)return;
		ArrayList<String> newOrder = new ArrayList<String>();
		int asteriskInsertPoint = -1;
		List<String> availList=Arrays.asList(availableProperties);
		for (int i = 0; i < prefProperties.length; i++) {
			String prefProp = prefProperties[i];
			if (prefProp.equals(PROPERTY_NAME_WILDCARD)) {
				asteriskInsertPoint = newOrder.size();
			} else {
				if(availList.contains(prefProp)){
					newOrder.add(prefProp);
				}
			}
		}
		if (asteriskInsertPoint >= 0) {
			int asteriskInsertPoint2 = -1;
			if (order != null) {
				for (int j = 0; j < order.size(); j++) {
					String prefProp = order.get(j);
					if (prefProp.equals(PROPERTY_NAME_WILDCARD)) {
						asteriskInsertPoint = newOrder.size();
					} else {
						if (!newOrder.contains(prefProp)) {
							newOrder.add(asteriskInsertPoint++,prefProp);
						}
					}
				}
				
				
					for (int k = 0; k < availableProperties.length; k++) {
						String prefProp = availableProperties[k];
						if (!newOrder.contains(prefProp)) {
							if(asteriskInsertPoint2>=0){
								newOrder.add(asteriskInsertPoint2++,prefProp);
							}else{
								newOrder.add(prefProp);
							}
						}
					}
				
				
			} else {
				for (int k = 0; k < availableProperties.length; k++) {
					String prefProp = availableProperties[k];
					if (!newOrder.contains(prefProp)) {
						newOrder.add(asteriskInsertPoint++,prefProp);
					}
				}
			}
		}
		order=newOrder;
	}

	/**
	 * Adds new properties order preference as comma separated string.
	 * 
	 * @param prefPropertiesAsList
	 */
	public void applyPreferredOrder(String prefPropertiesAsList) {
		if(prefPropertiesAsList==null)return;
		applyPreferredOrder(parsePropertyList(prefPropertiesAsList));
	}

	public static String[] parsePropertyList(String listAsString) {
		List<String> list = parsePropertyListAsList(listAsString); 
		return list.toArray(new String[0]);
	}
	
	public static List<String> parsePropertyListAsList(String listAsString) {
		ArrayList<String> list = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(listAsString, ",");
		while (st.hasMoreTokens()) {
			list.add(st.nextToken().trim());
		}
		return list;
	}
	
	public static void main(String[] args){
		PropertyNameOrder pno=new PropertyNameOrder(new String[]{"aaa","bbb","ccc","ddd","eee"});
		pno.applyPreferredOrder("aaa,*,bbb");
		pno.applyPreferredOrder("eee,*");
		for(String o:pno.getOrder()){
		System.out.println(o);
		}
	}


	public ArrayList<String> getOrderArrayList() {
		return order;
	}
	

}
