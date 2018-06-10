package  ipsk.swing.symbols.unicode;



/**
 * An instance of the class <code>DefaultCodePage</code> represents 
 * a range of Unicode characters. This range is defined by the first 
 * and the last character in the range, both as integer values. In 
 * addition a name can be assigned to the range. 
 * <p>In this default implementation of the interface <code>CodePage</code>
 * the code page ranges from 0 to 0 and has the name "CodePage". In 
 * order to get a useful code page a new class may be derived from 
 * this one which overwrites the constructor in a way that it sets 
 * the first and the last character and an appropriate name for the 
 * code page.
 *
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 * @see <a href="http://www.unicode.org/charts/" target="_blank">http://www.unicode.org/charts/</a>
 */
public class DefaultCodePage implements CodePage
{
    private int firstCharacter;
    private int lastCharacter;
    private String name;
    

    /**
     * Creates a new <code>CodePage</code> instance.
     * Here the instance variables <code>firstCharacter</code>, <code>lastCharacter</code>
     * and <code>name</code> are set with the appropriate methods.
     */
    public DefaultCodePage()
    {
	setFirstCharacter(0);
	setLastCharacter(0);
	setName("CodePage");
    }
    

    /**
     * Get the value of the first character in the range.
     * @return the <code>int</code> value of a character
     */
    public int getFirstCharacter() {
	return firstCharacter;
    }
    /**
     * Set the value of the first character in the range.
     * @param v the <code>int</code> value of a character
     */
    public void setFirstCharacter(int  v) {
	this.firstCharacter = v;
    }

    
    /**
     * Get the value of the last character in the range.
     * @return the <code>int</code> value of a character
     */
    public int getLastCharacter() {
	return lastCharacter;
    }
    /**
     * Set the value of the last character in the range.
     * @param v the <code>int</code> value of a character
     */
    public void setLastCharacter(int  v) {
	this.lastCharacter = v;
    }


    /**
     * Get the name of the code page.
     * @return the name as <code>String</code> value
     */
    public String getName() {
	return name;
    }
    /**
     * Set the name of the code page.
     * @param v the name as <code>String</code> value
     */
    public void setName(String  v) {
	this.name = v;
    }
}
