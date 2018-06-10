package ipsk.swing.symbols.unicode;



/**
 * An instance of the class <code>LatinExtA</code> represents the range 
 * of Unicode characters from <code>U0100</code> to <code>U017F</code>,
 * which is called "Latin Extended A".
 * 
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 * @see <a href="http://www.unicode.org/charts/" target="_blank">http://www.unicode.org/charts/</a>
 */
public class LatinExtA extends DefaultCodePage
{
    /**
     * Creates a new <code>LatinExtA</code> instance.<br>
     * The variable <code>firstCharacter</code> is set to 256, the variable 
     * <code>lastCharacter</code> is set to 383 and the variable <code>name</code> 
     * is set to "Latin Extended A".
     */
    public LatinExtA()
    {
	setFirstCharacter(256);
	setLastCharacter(383);
	setName("Latin Extended A");
    }
}
