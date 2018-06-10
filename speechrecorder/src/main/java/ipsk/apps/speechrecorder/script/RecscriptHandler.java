//    Speechrecorder
//    (c) Copyright 2009-2011
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

package ipsk.apps.speechrecorder.script;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import ipsk.db.speech.Script;
import ipsk.xml.DOMConverter;
import ipsk.xml.DOMConverterException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;


public class RecscriptHandler extends DOMConverter{
	
	public static final String NAME="script";
//	public static final String SYSTEM_ID="SpeechRecPrompts_2.dtd"; 
	private DocumentBuilderFactory docFactory;
	private String systemId=RecScriptManager.REC_SCRIPT_DTD;
	
	public RecscriptHandler(){
		super();
		docFactory=DocumentBuilderFactory.newInstance();
	}
	
	public Document newRecScriptDocument() throws ParserConfigurationException{
	
		DocumentBuilder docBuilder=docFactory.newDocumentBuilder();
		DOMImplementation domImpl=docBuilder.getDOMImplementation();
		DocumentType docType=domImpl.createDocumentType(NAME, null,systemId);
		return domImpl.createDocument(null, NAME, docType);
	}
	
	/*
     * (non-Javadoc)
     * @see ipsk.xml.DOMConverter#writeXML(org.w3c.dom.Document, java.io.OutputStream)
	 */
    @Deprecated
	public void writeXML(Document doc,OutputStream out) throws DOMConverterException{
		super.writeXML(doc,null,systemId, out);
		
	}
	
    @Deprecated
	public void writeXML(Script s,OutputStream out) throws DOMConverterException, ParserConfigurationException{
		Document d=newRecScriptDocument();
		//d.appendChild(s.toElement(d));
		s.insertIntoElement(d,d.getDocumentElement());
		super.writeXML(d,null,systemId, out);
	}
    
    public void writeXML(Document d,Writer out) throws DOMConverterException{
        super.writeXML(d,null,systemId, out);
    }
    
    public void writeXML(Script s,Writer out) throws DOMConverterException, ParserConfigurationException{
        if(s==null)throw new DOMConverterException("No script object to convert (null) !");
        Document d=newRecScriptDocument();
        //d.appendChild(s.toElement(d));
        s.insertIntoElement(d,d.getDocumentElement());
        super.writeXML(d,null,systemId, out);
    }
    
    public void writeXML(Script s,String systemID,Writer out) throws DOMConverterException, ParserConfigurationException{
        if(s==null)throw new DOMConverterException("No script object to convert (null) !");
        Document d=newRecScriptDocument();
        //d.appendChild(s.toElement(d));
        s.insertIntoElement(d,d.getDocumentElement());
        String sID=systemID;
        if(sID==null){
            sID=this.systemId;
        }
        super.writeXML(d,null,sID, out);
    }
    
	public Script readScriptFromXML(InputStream is,String systemId) throws DOMConverterException{
		Document d=readXML(is,systemId);
		Element rootE=d.getDocumentElement();
		Script s=new Script(rootE);
		s.setPropertyChangeSupportEnabled(true);
		return s;
	}
    
    public Script readScriptFromXML(Reader isr,String systemId) throws DOMConverterException{
        InputSource is=new InputSource(isr);
        if(systemId!=null){
            is.setSystemId(systemId);
        }
        Document d=readXML(is);
        Element rootE=d.getDocumentElement();
        Script s=new Script(rootE);
        s.setPropertyChangeSupportEnabled(true);
        return s;
    }
    
    public Script insertScriptElementsFromXML(Script s,InputSource is) throws DOMConverterException{
        Document d=readXML(is);
        Element rootE=d.getDocumentElement();
        s.insertElement(rootE);
        return s;
    }
    public Script readScriptFromXML(Reader is) throws DOMConverterException{
        Document d=readXML(is);
        Element rootE=d.getDocumentElement();
        Script s=new Script(rootE);
        s.setPropertyChangeSupportEnabled(true);
        return s;
    }
	
	public static void main(String[] args){
		// Test: create empty script
		
		try {
			RecscriptHandler h=new RecscriptHandler();
			if (args.length==2){
				FileInputStream fis=new FileInputStream(args[1]);
				Script script=h.readScriptFromXML(fis,args[0]);
				System.out.println(script);
				h.writeXML(script,new OutputStreamWriter(System.out,Charset.forName("UTF-8")));
			}else{
			
			
			h.writeXML(h.newRecScriptDocument(),new OutputStreamWriter(System.out,Charset.forName("UTF-8")));
			}
		} catch (DOMConverterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
}
