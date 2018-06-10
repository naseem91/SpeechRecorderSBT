//    IPS Java Utils
// 	  (c) Copyright 2011
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

package ipsk.swing.action.tree;



/**
 * @author klausj
 *
 */
public class ActionGroup extends ActionList {
 // top level file
    public static final String NEW="new";
    public static final String OPEN="open";
    public static final String SAVE="save";
    public static final String PRINT="print";
    public static final String QUIT="quit";
    
    // top level edit
    public static final String UNDO_REDO="undo_redo";
    public static final String EDIT="edit";
    public static final String SELECT="select";
    public static final String DELETE="delete";

    // top level view
    public static final String ZOOM="zoom";
    
    // top level help
    
    public static final String HELP="help";
    public static final String ABOUT="about";
    
    
    /**
     * Create action group.
     * @param key key name
     */
    public ActionGroup(String key) {
        super(key);
    }

}
