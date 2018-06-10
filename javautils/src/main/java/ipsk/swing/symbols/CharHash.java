package  ipsk.swing.symbols;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;


/**
 * A <code>CharHash</code> is a <code>Hashtable</code> where the key 
 * is the label for a button in the character window and the value is the 
 * tool tip text for this button. In addition to the methods inherited 
 * from <code>Hashtable</code> it provides a method which returns a 
 * sorted <code>Enumeration</code> of the contained keys.
 *
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 */
public class CharHash extends Hashtable 
{
    /**
     * Returns an alphabetically sorted <code>Enumeration</code> of the 
     * contained keys. For that purpose the method 
     * <code>Collections.sort(List)</code> is used.
     *
     * @return a sorted <code>Enumeration</code>
     * @see Collections#sort(List)
     */
    public Enumeration sortedKeys()
    {
	Enumeration e = super.keys();
	Vector keys = new Vector();

	// Extract all keys in a vector.
	while( e.hasMoreElements() )
	{
	    keys.addElement((String) e.nextElement());
	}

	// Sort the vector containing the keys.
	Collections.sort(keys);

	return keys.elements();
    }    
}
