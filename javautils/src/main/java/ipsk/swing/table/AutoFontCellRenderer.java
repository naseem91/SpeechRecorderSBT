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

 
package ipsk.swing.table;

import ipsk.awt.font.AutoFontFamilyManager;

import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Wrapper to disable the default cell renderer component if the value of the cell is null.
 * This is currently used to disable the check box of Boolean null values.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class AutoFontCellRenderer implements TableCellRenderer {

    private TableCellRenderer defRenderer;
	private AutoFontFamilyManager fontManager=null;
    
	
	/**
	 * @return array of preferred font families
	 * @see ipsk.awt.font.AutoFontFamilyManager#getPreferredFontFamilies()
	 */
	public String[] getPreferredFontFamilies() {
		return fontManager.getPreferredFontFamilies();
	}

	/**
	 * @param preferredFontFamilies
	 * @see ipsk.awt.font.AutoFontFamilyManager#setPreferredFontFamilies(java.lang.String[])
	 */
	public void setPreferredFontFamilies(String[] preferredFontFamilies) {
		fontManager.setPreferredFontFamilies(preferredFontFamilies);
	}

	/**
	 * @return true if searching on all other fonts
	 * @see ipsk.awt.font.AutoFontFamilyManager#isSearchOtherFonts()
	 */
	public boolean isSearchOtherFonts() {
		return fontManager.isSearchOtherFonts();
	}

	/**
	 * @param searchOtherFonts
	 * @see ipsk.awt.font.AutoFontFamilyManager#setSearchOtherFonts(boolean)
	 */
	public void setSearchOtherFonts(boolean searchOtherFonts) {
		fontManager.setSearchOtherFonts(searchOtherFonts);
	}

	/**
	 * @return list of font families, in which a suitable font will be searched
	 * @see ipsk.awt.font.AutoFontFamilyManager#getFontFamilies()
	 */
	public List<String> getFontFamilies() {
		return fontManager.getFontFamilies();
	}

	/**
     * Create Cell renderer wrapper which disables components for null values. 
     * @param defRenderer
     */
    public AutoFontCellRenderer(TableCellRenderer defRenderer){
        super();
        this.defRenderer=defRenderer;
       fontManager=new AutoFontFamilyManager();
    }
    
    /**
     * Create Cell renderer wrapper which disables components for null values. 
     * @param defRenderer
     */
    public AutoFontCellRenderer(TableCellRenderer defRenderer,String[] preferredFontFamilies,boolean searchOtherFonts){
        super();
        this.defRenderer=defRenderer;
        fontManager=new AutoFontFamilyManager(preferredFontFamilies, searchOtherFonts);
    }
    
    /**
     * Implementation of TableCellRenderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component defComp = defRenderer.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        if (defComp != null && value !=null) {
        	
        	String valStr=value.toString();
        	Font cmpFont=defComp.getFont();
        	Font dFont=fontManager.getFontCanDisplay(cmpFont, valStr);
        	if(dFont!=cmpFont){
        		defComp.setFont(dFont);
        	}
        }
        return defComp;
    }

}
