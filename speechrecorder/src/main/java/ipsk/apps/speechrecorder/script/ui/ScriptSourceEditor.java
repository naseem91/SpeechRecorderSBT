//    Speechrecorder
//    (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Sep 17, 2007
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.script.ui;

import ipsk.apps.speechrecorder.script.RecscriptHandler;
import ipsk.db.speech.Script;
import ipsk.swing.JDialogPanel;
import ipsk.swing.text.xml.JXMLPaneEditor;
import ipsk.swing.text.xml.XMLParserEvent;
import ipsk.swing.text.xml.XMLParserListener;
import ipsk.xml.DOMConverterException;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;

/**
 * Editor for recording script XML source text.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class ScriptSourceEditor extends JDialogPanel implements
        PropertyChangeListener, XMLParserListener {

    private Script script;
    private String scriptId=null;
    private RecscriptHandler handler;
    private String systemIdBase;
    private String systemId;
    private JXMLPaneEditor editor;

    private boolean applying;
    

    public ScriptSourceEditor() {
//        super(JDialogPanel.OK_APPLY_CANCEL_OPTION);
        super(JDialogPanel.Options.OK_CANCEL);
        handler = new RecscriptHandler();
        editor=new JXMLPaneEditor();
        editor.addValidationListener(this);
       editor.setPreferredSize(new Dimension(500,600));
       setContentPane(editor);
//        EditorKitMenu tpEkm=new EditorKitMenu(textPane);
//        tpEkm.setPopupMenuActiv(true);
        revalidate();
        repaint();
        //textPane.setEditable(false);
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;

        if (this.script != null){
            this.script.addPropertyChangeListener(this);
        }
        try {
            updateText();
        } catch (DOMConverterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //revalidate();
        //repaint();
    }
    
    private void updateText() throws DOMConverterException, ParserConfigurationException{
        if (this.script != null){
            StringWriter strWriter = new StringWriter();
            handler.writeXML(script,systemId, strWriter); 
            editor.setText(strWriter.toString());
            //textPane.setEnabled(true);
        }else{
            editor.setText("");
            editor.setEnabled(false);
        }
    }
    
    protected void applyValues(){
        //StringBufferInputStream strReader=new StringBufferInputStream(textPane.getText());
      applying=true;
        InputSource is=new InputSource(systemIdBase);
        String xmlSource=new String(editor.getText());
        is.setCharacterStream(new StringReader(xmlSource));
        try {
            // insert into the root element 
           handler.insertScriptElementsFromXML(script,is);
        } catch (DOMConverterException e) {
           JOptionPane.showMessageDialog(this,e.getMessage(),"Error parsing XML script",JOptionPane.ERROR_MESSAGE);
        }
        applying=false;
       try {
        updateText();
    } catch (DOMConverterException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (ParserConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    }

    public void propertyChange(PropertyChangeEvent evt) {
       
       try {
       if(!applying) updateText();
    } catch (DOMConverterException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (ParserConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
       
    }

	public void update(XMLParserEvent e) {
		Exception ex=e.getParseException();
		setApplyingEnabled(ex==null);
	}
	
	public void disposeDialog(){
		if (this.script != null){
            this.script.removePropertyChangeListener(this);         
        }
//		editor.removeValidationListener(this);
		super.disposeDialog();
	}

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getSystemIdBase() {
        return systemIdBase;
    }

    public void setSystemIdBase(String systemIdBase) {
        this.systemIdBase = systemIdBase;
        editor.setSystemId(systemIdBase);
    }

}
