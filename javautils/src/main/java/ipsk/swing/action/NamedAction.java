package ipsk.swing.action;

import javax.swing.*;
import java.awt.event.*;


/**
 * The abstract class <code>NamedAction</code> is derived from 
 * <code>AbstractAction</code> and provides an additional instance 
 * variable called <code>name</code>. Thus it is possible to identify 
 * an <code>Action</code> by its name.
 *
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 */
public abstract class NamedAction extends AbstractAction
{
    private String name;
    

    /**
     * Creates a new <code>NamedAction</code> instance.
     *
     * @param n the name by which the action can be identified
     */
    public NamedAction(String n)
    {
	super();
	name = n;
    }


    /**
     * Returns the <code>name</code> of the <code>Action</code> object.
     *
     * @return the value of the variable <code>name</code>
     */
    public String getName()
    {
	return name;
    }


    /**
     * Invoked when an action occurs.
     * The method <code>actionPerformed</code> must be implemented by 
     * the user of <code>NamedAction</code>.
     *
     * @param e an <code>ActionEvent</code>
     */
    public abstract void actionPerformed(ActionEvent e);
}
