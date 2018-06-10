package  ipsk.swing.symbols;

import ipsk.swing.action.NamedAction;
import ipsk.swing.symbols.unicode.BasicLatin;
import ipsk.swing.symbols.unicode.Greek;
import ipsk.swing.symbols.unicode.Ipa;
import ipsk.swing.symbols.unicode.LatinExtA;
import ipsk.swing.symbols.unicode.LatinExtB;
import ipsk.swing.symbols.unicode.LatinSupp;
import ipsk.swing.text.EditorKitMenu;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.EditorKit;
import javax.swing.text.Keymap;


/**
 * The class <code>CharWindow</code> is derived from <code>JFrame</code>.
 * It contains several tabs for code pages from Unicode and for annotation
 * characters. Each tab shows a different set of characters. If a character
 * is selected with the mouse it is inserted into the <code>JTextArea</code>
 * ,which was passed to the constructor, at the current cursor position.<br>
 * Each tab is mainly constructed from a <code>CharPane</code> which contains
 * a certain range of characters.<p>
 * <strong>Attention</strong>:<br>
 * As the annotation characters are taken from the <code>Keymap</code> of the 
 * <code>JTextArea</code> the <code>CharWindow</code> will only provide those
 * annotation characters for which a short cut is defined!
 *
 * @author Simone Leonardi
 * @version $Revision: 1.1 $
 */
public class CharWindow extends JFrame
{
    /**
     * Creates a new <code>CharWindow</code> instance.<br>
     * The constructor needs a <code>JTextArea</code> in which selected characters 
     * shall be inserted at the cursor position.
     *
     * @param ta the <code>JTextArea</code> of the applet
     */
    public CharWindow(JTextArea ta)
    {
	super("Choose a character");

	CharPane basic_latin = new CharPane(ta, new BasicLatin());
	JScrollPane scroll1 = new JScrollPane(basic_latin);

	CharPane latin1_supp = new CharPane(ta, new LatinSupp());
	JScrollPane scroll2 = new JScrollPane(latin1_supp);

	CharPane latin_extend_a = new CharPane(ta, new LatinExtA());
	JScrollPane scroll3 = new JScrollPane(latin_extend_a);

	CharPane latin_extend_b = new CharPane(ta, new LatinExtB());
	JScrollPane scroll4 = new JScrollPane(latin_extend_b);

	CharPane greek = new CharPane(ta, new Greek());
	JScrollPane scroll5 = new JScrollPane(greek);

	CharPane ipa = new CharPane(ta, new Ipa());
	JScrollPane scroll6 = new JScrollPane(ipa);

	// Take all annotation signs from the key map of the text area.
	CharHash annot = new CharHash();
 	Keymap map = ta.getKeymap();
 	Action[] actions = map.getBoundActions();
 	for(int i=0; i<actions.length; ++i)
 	{
	    // Use the name of the action as label.
 	    NamedAction ca = (NamedAction) actions[i];

	    // Use as tool tip: "Strg+<key>".
 	    KeyStroke[] keys = map.getKeyStrokesForAction(actions[i]);
	    String str = new String("Strg+" + keys[0].toString().substring(12,13));
	    annot.put( ca.getName(), str);
 	}
	CharPane annotationChars = new CharPane(ta, annot);
	JScrollPane scroll7 = new JScrollPane(annotationChars);

	
	// ----- TabbedPane ----- //
	JTabbedPane tp = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	tp.addTab(basic_latin.getName(), scroll1);
 	tp.addTab(latin1_supp.getName(), scroll2);
 	tp.addTab(latin_extend_a.getName(), scroll3);
 	tp.addTab(latin_extend_b.getName(), scroll4);
 	tp.addTab(greek.getName(), scroll5);
 	tp.addTab(ipa.getName(), scroll6);
 	tp.addTab("Annotation", scroll7);

 	getContentPane().add(tp, BorderLayout.CENTER);

	setDefaultCloseOperation(HIDE_ON_CLOSE);
	setSize(740, 330);
	setVisible(false);
    }
    
    public static void main(String args[]){
        JTextArea ta=new JTextArea(20, 60);
        EditorKitMenu ekm=new EditorKitMenu(ta);
        JFrame f=new JFrame();
        f.getContentPane().add(ta);
        CharWindow charWindow=new CharWindow(ta);
        f.pack();
        f.setVisible(true);
        charWindow.setVisible(true);
    }
    
}
