//    IPS Java Utils
// 	  (c) Copyright 2017
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

package ipsk.awt;

import java.awt.Color;
import java.util.StringTokenizer;

import ipsk.text.ParserException;

/**
 * @author klausj
 *
 */
public class ColorUtils {

  
//    public static String colorToRGBString(Color c){
//        int rgb=c.getRGB();
//        return "#"+Integer.toHexString(rgb);
//       
//    }
    
    public static String colorToRGBAString(Color c){
       return "rgba("+c.getRed()+","+c.getGreen()+","+c.getBlue()+","+((float)c.getAlpha()/255.0f)+")";
    }
    /**
     * Converts AWT color to string.
     * The method only converts AWT constant colors to their names, other colors are returned as hex representations in the form #rrggbbaa.
     * @param c AWT color
     * @return color string
     */
    public static String colorToString(Color c){
        String s;
        // first check constants
        if(Color.BLACK.equals(c)){
            s="black";
        }else if(Color.WHITE.equals(c)){
            s="white";
        }else if(Color.GRAY.equals(c)){
            s="gray";
        }else if(Color.DARK_GRAY.equals(c)){
            s="darkgray";
        }else if(Color.LIGHT_GRAY.equals(c)){
            s="lightgray"; 
        }else if(Color.RED.equals(c)){
            s="red";
        }else if(Color.GREEN.equals(c)){
            s="green";
        }else if(Color.BLUE.equals(c)){
            s="blue";
        }else if(Color.CYAN.equals(c)){
            s="cyan";
        }else if(Color.MAGENTA.equals(c)){
            s="magenta";
        }else if(Color.YELLOW.equals(c)){
            s="yellow";
        }else if(Color.ORANGE.equals(c)){
            s="orange";
        }else if(Color.PINK.equals(c)){
            s="pink";
        }else{
            s=colorToRGBAString(c);
        }
        return s;
    }
    
    /**
     * Parses AWT color names (available constants) and hex representations.
     * The method only converts AWT constant color names and parses hex representations in the form #rrggbbaa.
     * @param colorString
     * @return decoded AWT color
     * @throws ParserException 
     */
    public static Color stringToColor(String colorString) throws ParserException{
       
        String cc=colorString.trim();
        Color c=null;
        // first check constants
        if(cc.equalsIgnoreCase("black")){
        c=Color.BLACK;
        }else if(cc.equalsIgnoreCase("white")){
            c=Color.WHITE;
        }else if(cc.equalsIgnoreCase("gray") || cc.equalsIgnoreCase("grey")){
           c=Color.GRAY;
        }else if(cc.equalsIgnoreCase("darkgray") || cc.equalsIgnoreCase("darkgrey")){
           c=Color.DARK_GRAY;
        }else if(cc.equalsIgnoreCase("lightgray") || cc.equalsIgnoreCase("lightgrey")){
           c=Color.LIGHT_GRAY;
        }else if(cc.equalsIgnoreCase("red")){
            c=Color.RED;
        }else if(cc.equalsIgnoreCase("green")){
            c=Color.GREEN;
        }else if(cc.equalsIgnoreCase("blue")){
            c=Color.BLUE;
        }else if(cc.equalsIgnoreCase("cyan")){
            c=Color.CYAN;
        }else if(cc.equalsIgnoreCase("magenta")){
            c=Color.MAGENTA;
        }else if(cc.equalsIgnoreCase("yellow")){
            c=Color.YELLOW;
        }else if(cc.equalsIgnoreCase("orange")){
            c=Color.ORANGE;
        }else if(cc.equalsIgnoreCase("pink")){
            
            c=Color.PINK;
        }else{
            if(cc.startsWith("#")){
                String cHex=cc.replaceFirst("^[#]","").trim();
                int rgb=Integer.parseInt(cHex, 16);
                c=new Color(rgb);
            }else if(cc.startsWith("rgba")){
                
                String rgbaValStr=cc.replaceFirst("^rgba\\s*[(]","").replaceFirst("\\s*[)]\\s*$","");
                String[] rgbaVals=rgbaValStr.split("\\s*,\\s*");
                if(rgbaVals==null || rgbaVals.length!=4){
                    throw new ParserException("Expected exactly four number values in RGBA color value: "+cc);
                }
                try{
                    int r=Integer.parseInt(rgbaVals[0]);
                    int g=Integer.parseInt(rgbaVals[1]);
                    int b=Integer.parseInt(rgbaVals[2]);
                    float aFloat=Float.parseFloat(rgbaVals[3]);
                    int a=(int)(aFloat*255.0);
                    c=new Color(r, g, b, a);
                }catch(NumberFormatException nfe){
                    throw new ParserException("Could not parse number in RGBA color value: "+cc); 
                }
            }else if(cc.startsWith("rgb")){
                String rgbValStr=cc.replaceFirst("^rgb\\s*[(]","").replaceFirst("\\s*[)]\\s*$","");
                String[] rgbVals=rgbValStr.split("\\s*,\\s*");
                if(rgbVals==null || rgbVals.length!=3){
                    throw new ParserException("Expected exactly three values for RGB color value: "+cc);
                }
                try{
                    int r=Integer.parseInt(rgbVals[0]);
                    int g=Integer.parseInt(rgbVals[1]);
                    int b=Integer.parseInt(rgbVals[2]);
                    c=new Color(r, g, b);
                }catch(NumberFormatException nfe){
                    throw new ParserException("Could not parse number in RGB color value: "+cc); 
                }
            }
        }
        return c;
    }
    
    
    public static void main(String[] args){
        String test1Str="blue";
        Color c1;
       
        Color c3=new Color(255, 0, 0, 128);
        try {
            c1 = stringToColor(test1Str);

            System.out.println("String: "+test1Str+" -> Color: "+c1+" -> String (again): "+colorToString(c1)+" "+colorToRGBAString(c1));
            String test2Str="#003456";
            Color c2=stringToColor(test2Str);
            System.out.println("String: "+test2Str+" -> Color: "+c2+" -> String (again): "+colorToString(c2));
            
            
            System.out.println("String: "+colorToString(c3)+" -> Color: "+c3+" -> Color (again) "+stringToColor(colorToString(c3)));
        } catch (ParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
