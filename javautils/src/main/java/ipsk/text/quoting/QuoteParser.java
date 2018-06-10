//    IPS Java Utils
// 	  (c) Copyright 2012
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

package ipsk.text.quoting;

import java.util.ArrayList;
import java.util.List;

/**
 * @author klausj
 *
 */
public class QuoteParser {

    
    public static List<TextPart> parseText(String text,char quoteChar,Character escapeChar,boolean unquote){
        int i=0;
        boolean quoted=false;
        boolean charEscape=false;
        ArrayList<TextPart> textPartList=new ArrayList<TextPart>();
        StringBuffer currPart=new StringBuffer();
        while(i<text.length()){
          
            char c=text.charAt(i);
            boolean isEscChar=(escapeChar!=null && c==escapeChar);
            
            if(c==quoteChar && !charEscape){
                if(!unquote){
                    currPart.append(c);
                }
                 TextPart tp=new TextPart(currPart.toString(), quoted);
                 textPartList.add(tp);
                 currPart=new StringBuffer();
            
                 quoted=!quoted;
             
            }else{
               if(!isEscChar){
                    currPart.append(c);
               }
               
            }
           charEscape=isEscChar;
          i++;
        }
        // last part
        TextPart tp=new TextPart(currPart.toString(), quoted);
        textPartList.add(tp);
        return textPartList;
    }
}
