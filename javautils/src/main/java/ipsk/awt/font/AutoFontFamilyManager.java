//    IPS Java Utils
// 	  (c) Copyright 2014
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

package ipsk.awt.font;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.JTextComponent;

/**
 * @author klausj
 *
 */
public class AutoFontFamilyManager {

	private List<String> fontFamilies=new ArrayList<String>();
	private String[] preferredFontFamilies;
	/**
	 * @return the preferredFontFamilies
	 */
	public String[] getPreferredFontFamilies() {
		return preferredFontFamilies;
	}

	/**
	 * @param preferredFontFamilies the preferredFontFamilies to set
	 */
	public void setPreferredFontFamilies(String[] preferredFontFamilies) {
		this.preferredFontFamilies = preferredFontFamilies;
		buildFontFamiliesList();
		
	}

	private boolean searchOtherFonts=false;
	
	/**
	 * @return the searchOtherFonts
	 */
	public boolean isSearchOtherFonts() {
		return searchOtherFonts;
	}

	/**
	 * @param searchOtherFonts the searchOtherFonts to set
	 */
	public void setSearchOtherFonts(boolean searchOtherFonts) {
		this.searchOtherFonts = searchOtherFonts;
		buildFontFamiliesList();
	}

	/**
	 * @return the fontFamilies
	 */
	public List<String> getFontFamilies() {
		return fontFamilies;
	}
	
	public AutoFontFamilyManager(){
		this(null,true);
	}
	public AutoFontFamilyManager(String[] fontFamilies){
		this(fontFamilies,false);
	}
	public AutoFontFamilyManager(String[] preferredFontFamilies,boolean searchOtherFonts){
		super();
		this.preferredFontFamilies=preferredFontFamilies;
		this.searchOtherFonts=searchOtherFonts;
		buildFontFamiliesList();
	}
	
	private void buildFontFamiliesList(){
		if(preferredFontFamilies!=null){
			fontFamilies.addAll(Arrays.asList(preferredFontFamilies));
		}
		if(searchOtherFonts){
			GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
			if(ge!=null){
				String[] allFams=ge.getAvailableFontFamilyNames();
				List<String> allFamsList=Arrays.asList(allFams);
//				allFamsList.removeAll(fontFamilies);
				fontFamilies.addAll(allFamsList);
			}
		}
	}
	
	public Font getFontCanDisplay(Font orgFont,String text){
		Font canFont=null;
		if(orgFont.canDisplayUpTo(text)==-1){
			// is able to display, return original
			canFont=orgFont;
		}else{
			
			int fStyle=orgFont.getStyle();
			int fSize=orgFont.getSize();
			for(String tFf:fontFamilies){
				Font trialfont=new Font(tFf,fStyle,fSize);
				if(trialfont.canDisplayUpTo(text)==-1){
					// found
					canFont=trialfont;
					break;
				}
			}
		}
		return(canFont);
	}
	
	public void applyFontCanDisplay(JTextComponent textComponent){
		String text=textComponent.getText();
		if(text!=null){
			Font orgFont=textComponent.getFont();
			if(orgFont!=null){
				Font canFont=getFontCanDisplay(orgFont, text);
				if(canFont!=null){
					if(!orgFont.equals(canFont)){
						textComponent.setFont(canFont);
					}
				}
			}
		}
	}
	
}
