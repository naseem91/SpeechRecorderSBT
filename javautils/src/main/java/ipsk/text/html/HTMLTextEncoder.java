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


package ipsk.text.html;

/**
 * This class encodes text to HTML.
 * It is intended to convert arbitrary text input.
 * @author klausj
 *
 */
public class HTMLTextEncoder {

  
    public static String encode(String fm){
        //String fm=new String(message);
       int len=fm.length();
       int i=0;
       
       // first check for escape chars '&' and ';'
        while (i<len){
            if (fm.charAt(i)=='&'){
                String c=fm.substring(0,i);
                c=c.concat("&#38;");
               
                c=c.concat(fm.substring(i+1));
                fm=c;
                i+=5;
                len=fm.length();
            }else if(fm.charAt(i)==';'){
                String c=fm.substring(0,i);
                c=c.concat("&#59;");
                c=c.concat(fm.substring(i+1));
                fm=c;
                i+=5;
                len=fm.length();
            }else{
                i++;
            }
        }
      
        // Replace other meta characters
       
        fm = fm.replaceAll("<","&#60;");
        fm = fm.replaceAll(">","&#62;");
        fm = fm.replaceAll("\"","&#34;");
        fm = fm.replaceAll("\\\\","&#92;");
        fm = fm.replaceAll("%","&#37;");     
        fm = fm.replaceAll("[(]","&#40;");
        fm = fm.replaceAll("[)]","&#41;");
        fm = fm.replaceAll("[+]","&#43;");
        fm=fm.replaceAll("\n","<br>");
        return fm;
    }
    
    public static void main(String[] args){
    	String testString="Input string: Hello World ! <> && ;; &auml;";
    	System.out.println("HTML Encoded: "+HTMLTextEncoder.encode(testString));
    }
    

}
