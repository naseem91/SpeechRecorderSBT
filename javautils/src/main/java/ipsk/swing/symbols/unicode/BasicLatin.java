package  ipsk.swing.symbols.unicode;



/**
 * An instance of the class <code>BasicLatin</code> represents the range 
 * of Unicode characters from <code>U0000</code> to <code>U007F</code>,
 * which is called "Basic Latin".
 * 
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 * @see <a href="http://www.unicode.org/charts/" target="_blank">http://www.unicode.org/charts/</a>
 */
public class BasicLatin extends DefaultCodePage
{
    /**
     * Creates a new <code>BasicLatin</code> instance.<br>
     * The variable <code>firstCharacter</code> is set to 0, the variable 
     * <code>lastCharacter</code> is set to 127 and the variable <code>name</code> 
     * is set to "Basic Latin".
     */
    public BasicLatin()
    {
	setFirstCharacter(0);
	setLastCharacter(127);
	setName("Basic Latin");
    }
}
