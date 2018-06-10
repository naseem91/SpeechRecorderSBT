/*
 * Date  : 04.11.2014
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.db.speech.utils;

import ipsk.db.speech.Person.Sex;
import ipsk.swing.table.EnumCellEditor;


/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class EnumSexCellEditor extends EnumCellEditor<Sex> {
   
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public EnumSexCellEditor(){
        super(Sex.class);
    }
}
