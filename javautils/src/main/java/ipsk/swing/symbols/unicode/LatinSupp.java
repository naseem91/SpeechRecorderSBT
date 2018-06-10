package ipsk.swing.symbols.unicode;



/**
 * An instance of the class <code>LatinSupp</code> represents the range 
 * of Unicode characters from <code>U0080</code> to <code>U00FF</code>,
 * which is called "Latin-1 Supplement".
 *
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 * @see <a href="http://www.unicode.org/charts/" target="_blank">http://www.unicode.org/charts/</a>
 */
public class LatinSupp extends DefaultCodePage
{
    /**
     * Creates a new <code>LatinSupp</code> instance.
     * The variable <code>firstCharacter</code> is set to 128, the variable 
     * <code>lastCharacter</code> is set to 255 and the variable <code>name</code> 
     * is set to "Latin-1 Supplement".
     */
    public LatinSupp()
    {
	setFirstCharacter(128);
	setLastCharacter(255);
	setName("Latin-1 Supplement");
    }
}
