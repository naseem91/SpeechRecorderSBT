//    Speechrecorder
//    (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Jun 24, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.config;

import java.text.AttributedCharacterIterator;
import java.util.HashMap;


/**
 * Font configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class Font {
	private int style;
	private java.awt.Font font;
	private String[] family;
	private float size;
	
	public Font(){
		font=new java.awt.Font(new HashMap<AttributedCharacterIterator.Attribute,AttributedCharacterIterator.Attribute>());
		size=font.getSize();
		style=font.getStyle();
		family=new String[1];
		family[0]=font.getFamily();
	}
			

	/**
	 * @return font size
	 */
	public float getSize() {
		return size;
	}

	/**
	 * @param f font size
	 */
	public void setSize(float f) {
		size = f;
		font=font.deriveFont(size);
	}

	/**
	 * @return font family
	 */
	public String[] getFamily() {
		return family;
	}

	/**
	 * @param string font family
	 */
	public void setFamily(String[] string) {
		family = string;
	}

	/**
	 * @return font style
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * @param i font style
	 */
	public void setStyle(int i) {
		style = i;
	}
	
	public java.awt.Font toFont(){
		return new java.awt.Font(family[0],style,(int)size);
	}
	public java.awt.Font[] toFonts(){
		java.awt.Font[] fonts=new java.awt.Font[family.length];
		for(int i=0;i<family.length;i++){
			fonts[i]=new java.awt.Font(family[i],style,(int)size);
		}
		return(fonts);
	}


    public Object clone(){
        Font clone=new Font();
        clone.setFamily(getFamily());
        clone.setSize(getSize());
        clone.setStyle(getStyle());
        return clone;
    }
    
}
