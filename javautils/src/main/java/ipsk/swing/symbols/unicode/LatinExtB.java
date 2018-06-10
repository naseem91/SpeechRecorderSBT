package ipsk.swing.symbols.unicode;



/**
 * An instance of the class <code>LatinExtB</code> represents the range 
 * of Unicode characters from <code>U0180</code> to <code>U024F</code>,
 * which is called "Latin Extended B".
 *
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 * @see <a href="http://www.unicode.org/charts/" target="_blank">http://www.unicode.org/charts/</a>
 */
public class LatinExtB extends DefaultCodePage
{
    /**
     * Creates a new <code>LatinExtB</code> instance.<br>
     * The variable <code>firstCharacter</code> is set to 384, the variable 
     * <code>lastCharacter</code> is set to 591 and the variable <code>name</code> 
     * is set to "Latin Extended B".
     */
    public LatinExtB()
    {
	setFirstCharacter(384);
	setLastCharacter(591);
	setName("Latin Extended B");
    }
}
