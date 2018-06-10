package  ipsk.swing.symbols.unicode;



/**
 * An instance of the class <code>Greek</code> represents the range 
 * of Unicode characters from <code>U0370</code> to <code>U03FF</code>,
 * which is called "Greek and Coptic".
 *
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 * @see <a href="http://www.unicode.org/charts/" target="_blank">http://www.unicode.org/charts/</a>
 */
public class Greek extends DefaultCodePage
{
    /**
     * Creates a new <code>Greek</code> instance.
     * The variable <code>firstCharacter</code> is set to 880, the variable 
     * <code>lastCharacter</code> is set to 1023 and the variable <code>name</code> 
     * is set to "IPA Extensions".
     */
    public Greek()
    {
	setFirstCharacter(880);
	setLastCharacter(1023);
	setName("Greek and Coptic");
    }
}
