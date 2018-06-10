package  ipsk.swing.symbols.unicode;



/**
 * The interface <code>CodePage</code> should be used to represent a 
 * range of Unicode characters. This range is defined by the first and 
 * the last character in the range, both as integer values. In addition 
 * a name has to be assigned to the range.
 * <p>An implementation of this interface must at least provide the 
 * instance variables <code>firstCharacter</code>, <code>lastCharacter</code>
 * and <code>name</code> for which the get- and set-methods are declared 
 * in this interface. All variables should be set in the constructor.
 * <p>A default implementation <code>DefaultCodePage</code> is provided
 * in order to facilitate the implementation of further code pages.
 *
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 * @see <a href="http://www.unicode.org/charts/" target="_blank">http://www.unicode.org/charts/</a>
 * @see DefaultCodePage
 */
public interface CodePage
{
    /**
     * Get the value of the first character in the range.
     * @return the <code>int</code> value of a character
     */
    public int getFirstCharacter();

    /**
     * Set the value of the first character in the range.
     * @param v the <code>int</code> value of a character
     */
    public void setFirstCharacter(int  v);

    
    /**
     * Get the value of the last character in the range.
     * @return the <code>int</code> value of a character
     */
    public int getLastCharacter();

    /**
     * Set the value of the last character in the range.
     * @param v the <code>int</code> value of a character
     */
    public void setLastCharacter(int  v);


    /**
     * Get the name of the code page.
     * @return the name as <code>String</code> value
     */
    public String getName();

    /**
     * Set the name of the code page.
     * @param v the name as <code>String</code> value
     */
    public void setName(String  v);
}
