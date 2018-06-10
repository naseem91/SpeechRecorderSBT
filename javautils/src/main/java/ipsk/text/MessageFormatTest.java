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

package ipsk.text;

import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

/**
 * @author klausj
 *
 */
public class MessageFormatTest {

	public MessageFormatTest(){
		super();
		MessageFormat mf1=new MessageFormat("Bla fasel {2} huh {0} nochwas {1} fasel ende");
		Format[] mf1Fmts=mf1.getFormats();
		System.out.println(mf1Fmts.length+" formats");
//		AttributedCharacterIterator aci=mf1.formatToCharacterIterator(new Object[]{new String("p0"),new String("p1")});
		AttributedCharacterIterator aci=mf1.formatToCharacterIterator(new Object[]{null,null,null});
		int end=aci.getEndIndex();
		for(int i=0;i<=end;i++){
			aci.setIndex(i);
			char cAt=aci.current();
			System.out.print("Char: "+cAt);
			Map<Attribute,Object> aMap=aci.getAttributes();
//			Set<Attribute> keys=aci.getAllAttributeKeys();
			Set<Attribute> attrs=aMap.keySet();
			for(Attribute a:attrs){
				Object ao=aMap.get(a);
				System.out.print(","+a+":"+ao);
				if(MessageFormat.Field.ARGUMENT.equals(a)){
					System.out.print(",argument field");
				}
			}
			System.out.println();
		}
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MessageFormatTest();
	}

}
