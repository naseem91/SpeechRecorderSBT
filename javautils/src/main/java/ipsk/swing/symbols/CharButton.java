package  ipsk.swing.symbols;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * The class <code>CharButton</code> is derived from <code>JButton</code>.
 * A <code>CharButton</code> has the following properties:
 * <ul><li>a label (the character which it represents)</li>
 *     <li>maybe a <code>JPopupMenu</code> which provides the hexadecimal 
 *         value of the character in the Unicode character set.</li>
 * </ul>
 *
 * @author Simone Leonardi
 * @version 1.3 Import to IPS Java Utils
 */
public class CharButton extends JButton
{
    private JPopupMenu popup;


	private CharPane charPane;

    /**
     * Creates a new <code>CharButton</code> instance.
     * The constructor needs to <code>String</code> as label for the button.
     *
     * @param s a <code>String</code> as label for the button
     */
    public CharButton(String s)
    {
	super();
	setText(s);
	setBackground( new Color(255,255,255) );
    }


    /**
     * Creates a new <code>CharButton</code> instance.
     * The constructor needs to get a <code>String</code> as label
     * for the button and a <code>String</code> to be set as tool tip.
     *
     * @param s1 a <code>String</code> as label for the button
     * @param s2 a <code>String</code> as tool tip
     */
    public CharButton(String s1, String s2)
    {
	super();
	setText(s1);
	setToolTipText(s2);
	setBackground( new Color(255,255,255) );
	popup = new JPopupMenu();
    }


    /**
     * Creates a new <code>CharButton</code> instance.
     * The constructor needs to get a <code>String</code> as label for
     * the button and an <code>int</code>-value which is used to compute
     * the hexadecimal value of the character in the Unicode character set
     * (as label for the popup menu). The passes <code>ActionListener</code>
     * registered for the popup menu.
     *
     * @param s  a <code>String</code> as label for the button
     * @param i  a <code>int</code>-value to compute the hexadecimal value 
     *           of the character
     * @param listener an <code>ActionListener</code>
     */
    public CharButton(String s, int i, ActionListener listener)
    {
	super();
	setText(s);
	setBackground( new Color(255,255,255) );
	popup = new JPopupMenu();

	// Create the label for the popup menu
	StringBuffer unicode = new StringBuffer("\\u");
	String hex = Integer.toHexString(i);
	if(hex.length() == 1)
	    unicode.append("000");
	else if(hex.length() == 2)
	    unicode.append("00");
	else if(hex.length() == 3)
	    unicode.append("0");
	unicode.append(hex);

	JMenuItem insertChar = new JMenuItem( unicode.toString() );
	insertChar.addActionListener(listener);
	popup.add(insertChar);

	// Add a listener that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
	addMouseListener(popupListener);
    }


    /**
     * The class <code>PopupListener</code> is a <code>MouseAdapter</code>
     * which handles mouse events. If the mouse clicks a <code>CharButton</code>,
     * the appropriate popup menu is brought up.
     *
     */
    class PopupListener extends MouseAdapter
    {
	/**
	 * Causes the popup menu to be shown if the mouse clicks 
	 * on a <code>CharButton</code>.
	 *
	 * @param e a <code>MouseEvent</code>
	 * @see #maybeShowPopup
	 */
	public void mousePressed(MouseEvent e)
	{
	    maybeShowPopup(e);
	}
    
	/**
	 * Causes the popup menu to be shown if the mouse clicks 
	 * on a <code>CharButton</code>.
	 *
	 * @param e a <code>MouseEvent</code>
	 * @see #maybeShowPopup
	 */
	public void mouseReleased(MouseEvent e)
	{
	    maybeShowPopup(e);
	}
    
	/**
	 * Brings up the appropriate popup menu.
	 *
	 * @param e a <code>MouseEvent</code>
	 */
	private void maybeShowPopup(MouseEvent e)
	{
	    if(e.isPopupTrigger())
		popup.show(e.getComponent(), e.getX(), e.getY());
	}
    }
	public CharPane getCharPane() {
		return charPane;
	}

	public void setCharPane(CharPane charPane) {
		this.charPane = charPane;
	}

}
