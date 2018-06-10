//    IPS Java Utils
// 	  (c) Copyright 2009-2011
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

import ipsk.text.quoting.TextPart;

import java.util.ArrayList;
import java.util.List;

/**
 * Alternative string tokenizer.
 * The JRE class cannot handle empty fields.
 * @author klausj
 *
 */
public class StringTokenizer {
    
   
    
	public static String[] split(String string,char fieldSeparator){
		return split(string, fieldSeparator, false);
	}
	
    public static String[] split(String string,char fieldSeparator,boolean trim){
    if(string==null){
        return null;
    }
    ArrayList<String> cols=new ArrayList<String>();

        int i=0;
        int fieldSepIndex=-1;
        do{ 
            fieldSepIndex=string.indexOf(fieldSeparator,i);
            if(fieldSepIndex!=-1){
                String newCol=string.substring(i, fieldSepIndex);
                if(trim){
                	newCol=newCol.trim();
                }
                cols.add(newCol);
                i=fieldSepIndex+1;
            }else{
                String newCol=string.substring(i);
                if(trim){
                	newCol=newCol.trim();
                }
                cols.add(newCol);
            }
        }while(fieldSepIndex!=-1);
        return cols.toArray(new String[0]);
    }
    
    public static String[] split(List<TextPart> textPartList,char fieldSeparator,boolean trim){

        ArrayList<String> cols=new ArrayList<String>();
        StringBuffer currentCol=new StringBuffer();
        for(TextPart tp:textPartList){
            String string =tp.getText();
            boolean quoted=tp.isQuoted();
            if(quoted){
                currentCol.append(string);
            }else{
                int i=0;
                int fieldSepIndex=-1;
                do{ 
                    fieldSepIndex=string.indexOf(fieldSeparator,i);
                    if(fieldSepIndex!=-1){
                        String colPart=string.substring(i, fieldSepIndex);
                        currentCol.append(colPart);
                        String newCol=currentCol.toString();
                        currentCol=new StringBuffer();
                        if(trim){
                            newCol=newCol.trim();
                        }
                        cols.add(newCol);
                        i=fieldSepIndex+1;
                    }else{
                        //String newCol=string.substring(i);
                        currentCol.append(string.substring(i));
//                        if(trim){
//                            newCol=newCol.trim();
//                        }
//                        cols.add(newCol);
                    }
                }while(fieldSepIndex!=-1);
            }
           
        }
        // last 
        String newCol=currentCol.toString();
        currentCol=new StringBuffer();
        if(trim){
            newCol=newCol.trim();
        }
        cols.add(newCol);
        return cols.toArray(new String[0]);
    }
    
    
    
    public static void main(String[] args){
        String testString="\ta\t\tb\tc\t";
        String[] tks=split(testString,'\t');
        System.out.println(tks.length+" tokens:");
        for(String tk:tks){
            System.out.println(tk);
        }
        
        for(String ss:"ABC ,DEF, bla".trim().split("\\s*,\\s*")){
        	System.out.println("Token: __"+ss+"__");
        }
    }
    
}
