//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.swing.text;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;


/**
 * 
 * Styled document editor.
 * Experimental source and WYSIWYG editor for HTML (and RTF docs).
 * @version alpha
 * @author klausj
 *
 */
public class JStyledDocumentEditor extends JPanel implements ActionListener,
		DocumentListener {

	final private JTextPane editorPane;

	private JTextPane srcEditorPane;

	private JPanel buttonPanel;

	private EditorKit editorKit;

	private Document srcDoc;

	private Document doc;

	private Color editorForeGroundColor = Color.BLACK;
	//private boolean srcDocChange=false;

	public JStyledDocumentEditor() {
		super(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 2.0;
		c.weighty = 2.0;
		c.fill = GridBagConstraints.BOTH;
		editorPane = new JTextPane();
		editorPane.setPreferredSize(new Dimension(300, 100));
		editorPane.setContentType("text/html");
		editorPane
				.putClientProperty(JEditorPane.W3C_LENGTH_UNITS, Boolean.TRUE);
		doc = editorPane.getDocument();
		EditorKitMenu epEkm=new EditorKitMenu(editorPane);
		epEkm.setPopupMenuActiv(true);
		editorKit = editorPane.getEditorKit();

		srcEditorPane = new JTextPane();
		srcEditorPane.setPreferredSize(new Dimension(300, 100));
		srcEditorPane.setContentType("text/plain");
		EditorKitMenu sepEkm=new EditorKitMenu(srcEditorPane);
		sepEkm.setPopupMenuActiv(true);
		srcDoc = srcEditorPane.getDocument();
		add(new JScrollPane(editorPane), c);
		c.gridx++;
		add(new JScrollPane(srcEditorPane), c);
		c.gridx = 0;
		c.gridy++;
		buttonPanel = new JPanel();

		JToolBar toolBar = new JToolBar();

		JButton boldButton = new JButton("<html><b>B</b></html>");
		JButton italicButton = new JButton("<html><i>i</i></html>");
		for (Action a : editorKit.getActions()) {
			String name = (String) a.getValue(Action.NAME);
			if (name.equals("font-bold")) {
				boldButton.addActionListener(a);
			} else if (name.equals("font-italic")) {
				italicButton.addActionListener(a);
			}
			// buttonPanel.add(new JButton(a),c2);
			// c2.gridy++;
			// if(c2.gridy % 15==0){
			// c2.gridy=0;
			// c2.gridx++;
			// }
		}
		// c2.gridy++;
		JButton wButt = new JButton("Write");
		wButt.addActionListener(this);

		toolBar.add(boldButton);
		toolBar.add(italicButton);

		JButton colorButton = new JButton("Color");

		final Component parent = this;

		toolBar.add(colorButton);

		SpinnerNumberModel model = new SpinnerNumberModel(12, 8, 1000, 10);
		final JSpinner fontSizeSpinner = new JSpinner(model);
		fontSizeSpinner.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				Integer value = (Integer) fontSizeSpinner.getValue();
				Action fsAction = new StyledEditorKit.FontSizeAction(
						"font-size-" + value.toString(), value);
				fsAction.actionPerformed(new ActionEvent(editorPane,
						ActionEvent.ACTION_PERFORMED, value.toString()));
				// fsAction.actionPerformed(null);
			}

		});

		colorButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (editorKit instanceof StyledEditorKit
						&& doc instanceof StyledDocument) {
					StyledEditorKit sek = (StyledEditorKit) editorKit;
					StyledDocument sd = (StyledDocument) doc;
					editorForeGroundColor = sd.getForeground(sek
							.getInputAttributes());

				}
				Color c = JColorChooser.showDialog(parent, "Text color",
						editorForeGroundColor);
				if (c == null)
					return;
				Action fgAction = new StyledEditorKit.ForegroundAction("color",
						c);
				fgAction.actionPerformed(e);
				editorForeGroundColor = c;

			}
		});

		toolBar.add(colorButton);
		toolBar.add(fontSizeSpinner);

		buttonPanel.add(toolBar);
		buttonPanel.add(wButt);
		add(buttonPanel, c);

		doc.addDocumentListener(this);
		srcDoc.addDocumentListener(this);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		Runnable guiRunnable=new Runnable() {
            
            @Override
            public void run() {
                JFrame f = new JFrame("Test");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JStyledDocumentEditor e = new JStyledDocumentEditor();
                f.add(e);
                f.pack();
                f.setVisible(true);
            }
        };
        
        SwingUtilities.invokeLater(guiRunnable);
	
	}

	public void actionPerformed(ActionEvent e) {
		Document doc = editorPane.getDocument();
		try {
			editorKit.write(System.out, doc, 0, doc.getLength());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private void updateSourcePane(DocumentEvent e) {
		Document d = e.getDocument();
		if (d == doc) {
			StringWriter sw = new StringWriter();
			try {

				// editorPane.write(sw);
				editorKit.write(sw, d, 0, d.getLength());
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
			} catch (BadLocationException ble) {
				// TODO Auto-generated catch block
				ble.printStackTrace();
			}
			String srcText = sw.toString();
			srcDoc.removeDocumentListener(this);
			srcEditorPane.setText(srcText);
			srcDoc.addDocumentListener(this);
		} else if (d == srcDoc) {
			 StringReader in;
			 try {
			 in = new StringReader(srcDoc.getText(0, srcDoc.getLength()));
			 doc.removeDocumentListener(this);
			 doc.remove(0, doc.getLength());
			 editorKit.read(in, doc, 0);
			 doc.addDocumentListener(this);
			 } catch (BadLocationException e1) {
			 // TODO Auto-generated catch block
			 e1.printStackTrace();
			 } catch (IOException ioe) {
			 // TODO Auto-generated catch block
			 ioe.printStackTrace();
			 }
						
							

		}
	}

	public void changedUpdate(DocumentEvent e) {

		updateSourcePane(e);
	}

	public void insertUpdate(DocumentEvent e) {
		updateSourcePane(e);

	}

	public void removeUpdate(DocumentEvent e) {
		updateSourcePane(e);
	}

}
