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

package ipsk.swing.text.xml;

import ipsk.swing.text.JTextPaneEditor;

import ipsk.swing.text.LinePosition;
import ipsk.xml.DOMConverterException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.StringReader;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * Simple validating XML editor.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class JXMLPaneEditor extends JPanel implements DocumentListener, XMLParserListener {

  
    
   
    private JPanel validationPanel;
    private JLabel validLabel=new JLabel("Invalid");
    private JLabel posLabel=new JLabel("-:-");
    private JTextField messageLabel=new JTextField();
    private String systemId=null;;
  
   
    private Document xmlSrcDoc;
 
    private XMLParserThread validator=null;
    private ThreadGroup validatorThreadGroup;
   
    private JTextPaneEditor textPaneEditor;
	private Vector<XMLParserListener> validationListeners=new Vector<XMLParserListener>();
    
    public JXMLPaneEditor(){
        super(new BorderLayout());
        validatorThreadGroup=new ThreadGroup("XML Validators");
        textPaneEditor=new JTextPaneEditor();
        
        xmlSrcDoc=textPaneEditor.getDocument();
        
        xmlSrcDoc.addDocumentListener(this);
        messageLabel.setEditable(false);
        GridBagLayout l=new GridBagLayout();
        GridBagConstraints c=new GridBagConstraints();
        c.gridy=0;
        c.gridx=0;
        c.insets=new Insets(2,2,2,2);
        validationPanel=new JPanel(l);
        validationPanel.add(validLabel,c);
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx=1;
        c.gridx++;
        validationPanel.add(messageLabel,c);
        c.fill=GridBagConstraints.NONE;
        c.weightx=0;
        c.gridx++;
        validationPanel.add(posLabel,c);
        
        
       
        add(textPaneEditor,BorderLayout.CENTER);
        add(validationPanel,BorderLayout.SOUTH);   
        
      validateXml();
    }
    
    public void setText(String text){
    	xmlSrcDoc.removeDocumentListener(this);
        textPaneEditor.setText(text);
        validateXml();
        xmlSrcDoc.addDocumentListener(this);
    }


    public String getText() {
        return textPaneEditor.getText();
    }

    private synchronized void validateXml(){
        
       if(validator!=null){
            validator.removeListener(this);
            validator.interrupt();
       }
        
        InputSource is=new InputSource(new StringReader(textPaneEditor.getText()));
        if(systemId!=null){
            is.setSystemId(systemId);
        }
       
        try {
            validator = new XMLParserThread(validatorThreadGroup,is);
            
        } catch (DOMConverterException e) {
            validLabel.setText("Invalid");
            validLabel.setEnabled(true);
            validator=null;
            return;
        }
        
        validLabel.setEnabled(false);
        validator.addListener(this);
        validator.start();
    }
    
    public synchronized void update(XMLParserEvent e) {
        Exception pe=e.getParseException();
        if(pe==null){
          setXMLValid(true);
            posLabel.setText("");
            messageLabel.setText("OK");
            messageLabel.setToolTipText("XML parser validation OK");
        }else{
            Throwable peThr=pe.getCause();
            if(peThr instanceof SAXParseException){
                SAXParseException spe=(SAXParseException)peThr;
                //System.out.println("SAX: "+spe.getMessage()+ " at "+spe.getLineNumber()+":"+spe.getColumnNumber());
                posLabel.setText(new LinePosition(spe.getLineNumber(),spe.getColumnNumber()).toString());
                messageLabel.setText(spe.getMessage());
                messageLabel.setToolTipText("XML parser (SAX) message: \n"+spe.getMessage());
            }else{
                posLabel.setText("-:-");
                messageLabel.setText(peThr.getLocalizedMessage());
                messageLabel.setToolTipText("DOM converter message: \n"+peThr.getLocalizedMessage()); 
            }
           setXMLValid(false);
        }
        validLabel.setEnabled(true);
        fireXMLValidatorUpdate(e);
        
    }
     
     
    protected void setXMLValid(boolean valid){
        if(valid){
            validLabel.setForeground(Color.GREEN);
            validLabel.setText("Valid");
        }else{
            validLabel.setForeground(Color.RED);
            validLabel.setText("Invalid");
            
        }
    }

    public void changedUpdate(DocumentEvent e) {
        validateXml();
        
    }



    public void insertUpdate(DocumentEvent e) {
      
        validateXml();
        
    }



    public void removeUpdate(DocumentEvent e) {
        validateXml();
        
    }
    
    public static void main(String[] args){
        JXMLPaneEditor xmlPaneEditor=null;
       
            xmlPaneEditor = new JXMLPaneEditor();
       
        JFrame f=new JFrame();
        f.getContentPane().add(xmlPaneEditor);
        f.pack();
        f.setVisible(true);
        
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    /**
     * Add listener.
     * 
     * @param l new validation listener
     */
    public synchronized void addValidationListener(XMLParserListener l) {
        if (l != null && !validationListeners.contains(l)) {
            validationListeners.addElement(l);
        }
    }

    /**
     * Remove listener.
     * 
     * @param l validation listener to remove
     */
    public synchronized void removeValidationListener(XMLParserListener l) {
        if (l != null) {
            validationListeners.removeElement(l);
        }
    }
    
    protected synchronized void fireXMLValidatorUpdate(XMLParserEvent event) {
        
        for( XMLParserListener listener:validationListeners){
         listener.update(event);
     }
 }
    
}
