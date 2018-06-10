package  ipsk.swing.symbols;

import ipsk.swing.symbols.unicode.CodePage;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


/**
 * A <code>CharPane</code> is a <code>JPanel</code>. It creates several
 * <code>CharButtons</code> which are arranged on it: the labels on top 
 * and on the left side on the pane indicate the hexadecimal number of 
 * the character in the Unicode character set. In addition to the 
 * properties inherited from the <code>JPanel</code> it knows the 
 * <code>JTextArea</code> of the applet and it has a <code>Font</code>,
 * taken from the text area and an optional name.
 *
 * @author Simone Leonardi
 * @version 1.4 Import to Java Utils
 */
public class CharPane extends JPanel implements ActionListener
{
    private String name;
    private Font paneFont;
    private JTextArea textArea;    


	private CharButton charButton;

    /**
     * Creates a <code>CharPane</code> instance.
     * The constructor creates a button for every entry which is contained
     * in the passed <code>CharHash</code>. A <code>CharHash</code> is a 
     * <code>Hashtable</code> where the key is the label for the button and
     * the value is the tool tip text for the button.
     *
     * @param ta the <code>JTextArea</code> of the applet
     * @param ch a <code>CharHash</code> which provides a label and a tool 
     *           tip text for every button
     * @see CharHash
     */
    public CharPane(JTextArea ta, CharHash ch)
    {
	super();
	textArea = ta;
	paneFont = textArea.getFont();

	Enumeration e = ch.sortedKeys();
	while( e.hasMoreElements() )
	{
	    String label = (String) e.nextElement();
	    String tooltip = (String) ch.get(label);
	    CharButton b = new CharButton(label, tooltip);
	    b.setFont(paneFont);
	    b.addActionListener(this);
	    add(b);
	}
    }


    /**
     * Creates a <code>CharPane</code> instance.
     * The constructor creates a button for every entry which is contained
     * in the passed <code>CodePage</code>
     *
     * @param ta the <code>JTextArea</code> of the applet
     * @param cp a <code>CodePage</code> containing the area of the Unicode
     *           character set for which the buttons are to be created
     * @see <a href="http://www.unicode.org/charts/" target="_blank">http://www.unicode.org/charts/</a>
     * @see GridBagLayout
     */
    public CharPane(JTextArea ta, CodePage cp)
    {
	super();
	textArea = ta;
	name = cp.getName();
	paneFont = textArea.getFont();

	// Create a GridBagLayout.
	GridBagLayout gridbag = new GridBagLayout();
	setLayout(gridbag);
	GridBagConstraints constraints = new GridBagConstraints();
	constraints.fill = GridBagConstraints.BOTH;
	constraints.weightx = 0.0;

	// Label the top row from 0 to F.
	for(int j=0; j<16; ++j)
	{
	    String hex = Integer.toHexString(j);
	    JLabel label = new JLabel( hex.toUpperCase() );
	    label.setFont(paneFont);
	    constraints.gridx = j+1;
	    constraints.gridy = 0;
	    constraints.insets = new Insets(3,18,3,10);
	    gridbag.setConstraints(label, constraints);
	    add(label);
	}

	int start = cp.getFirstCharacter();
	int end = cp.getLastCharacter();
	int column = 0;
	int row = 1;

	// Create the buttons row after row:
	// the 0. column always contains a label
	for(int i = start; i <= end; ++i)
	{
	    if( (column>16) || (i==start) )
	    // Do this on the first cycle and always when a row was finished.
	    {
		column = 0;
		++row;
		
		StringBuffer tmp = new StringBuffer("");
		String hex = Integer.toHexString(i);
		if(hex.length() == 1)
		    tmp.append("000");
		else if(hex.length() == 2)
		    tmp.append("00");
		else if(hex.length() == 3)
		    tmp.append("0");
		tmp.append(hex);

		JLabel label = new JLabel( tmp.substring(0,tmp.length()-1).toUpperCase() );
		label.setFont(paneFont);
		constraints.gridx = column;
		constraints.gridy = row;
		constraints.insets = new Insets(5,5,5,5);
		gridbag.setConstraints(label, constraints);
		add(label);

		++column;
	    }
	    char c = (char) i;
	    CharButton button = new CharButton( String.valueOf(c), i, this );
	    button.setFont(paneFont);
	    button.addActionListener(this);
	    
	    constraints.gridx = column;
	    constraints.gridy = row;
	    constraints.insets = new Insets(0,0,0,0);
	    gridbag.setConstraints(button, constraints);
	    add(button);
	    
	    ++column;
	}
    }


    /**
     * Get the value of name.
     * @return value of name.
     */
    public String getName() {
	return name;
    }



    /**
     * Invoked when an action occurs.<br>
     * Calls the method <code>JTextArea.insert(String, int)</code>
     * with the label of the invoking <code>AbstractButton</code> and
     * the current cursor position.
     *
     * @param e an <code>ActionEvent</code>
     */
    public void actionPerformed(ActionEvent e)
    {
	// The source can be a CharButton or a JMenuItem as both
	// are derived from AbstractButton!

	AbstractButton source = (AbstractButton) e.getSource();
	textArea.insert( source.getText(), textArea.getCaretPosition() );
    }
	public CharButton getCharButton() {
		return charButton;
	}

	public void setCharButton(CharButton charButton) {
		this.charButton = charButton;
	}

}
