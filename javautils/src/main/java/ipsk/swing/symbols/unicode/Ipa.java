package  ipsk.swing.symbols.unicode;



/**
 * An instance of the class <code>Ipa</code> represents the range 
 * of Unicode characters from <code>U0250</code> to <code>U02AF</code>,
 * which is called "IPA Extensions".
 *
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 * @see <a href="http://www.unicode.org/charts/" target="_blank">http://www.unicode.org/charts/</a>
 */
public class Ipa extends DefaultCodePage
{
    /**
     * Creates a new <code>Ipa</code> instance.
     * The variable <code>firstCharacter</code> is set to 592, the variable 
     * <code>lastCharacter</code> is set to 687 and the variable <code>name</code> 
     * is set to "IPA Extensions".
     */
    public Ipa()
    {
	setFirstCharacter(592);
	setLastCharacter(687);
	setName("IPA Extensions");
    }
}
