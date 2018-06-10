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


package ipsk.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * Helper class to read and write to and from DOM documents.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class DOMConverter implements ErrorHandler {

	private DocumentBuilderFactory dbf;
  
    private SAXParseException saxException=null;
    private int indentNumber=2;
    
	/**
	 *	Create new Converter.
	 * @throws DOMConverterException if the doc builder cannot be build
	 */
	public DOMConverter(){
		super();
		dbf = DocumentBuilderFactory.newInstance();
	}
    
	public Document newDocument() throws DOMConverterException{
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			return db.newDocument();
		} catch (ParserConfigurationException e1) {
			throw new DOMConverterException(e1);
		}
	}
	/**
	 * Writes DOM document as XML stream.
	 * @param document
	 * @param out
	 * @throws DOMConverterException
     * @deprecated it is not recommended to use raw In/outputStream for text
	 */
    @Deprecated
	public void writeXML(Document document, OutputStream out) throws DOMConverterException {

		writeXML(document,null,null,out);
	}
    
	/**
	 * Writes DOM document as XML stream.
	 * @param document
	 * @param publicId
	 * @param systemId
	 * @param out
	 * @throws DOMConverterException
     * @deprecated it is not recommended to use raw In/outputStream for text 
	 */
    @Deprecated
	public void writeXML(Document document, String publicId,String systemId,OutputStream out) throws DOMConverterException {

		TransformerFactory tff = TransformerFactory.newInstance();
         try{
                tff.setAttribute("indent-number", new Integer(indentNumber));
                }catch(IllegalArgumentException iae){
                    // Do nothing
                }
		Transformer tf = null;
		try {
			tf = tff.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new DOMConverterException(e);
		}
		tf.setOutputProperty(OutputKeys.METHOD, "xml");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		if (publicId !=null){
			tf.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,publicId);
		}
		if (systemId !=null){
			tf.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,systemId);
		}
		DOMSource ds = new DOMSource(document);
		StreamResult sr = new StreamResult(out);
		try {
			tf.transform(ds, sr);
		} catch (TransformerException e) {
			throw new DOMConverterException(e);
		}
	}
	
    
    /**
     * Writes DOM document as XML stream.
     * @param document
     * @param out
     * @throws DOMConverterException
     */
    public void writeXML(Document document, Writer out) throws DOMConverterException {

        writeXML(document,null,null,out);
    }
    
    /**
     * Writes DOM document as XML fragment stream.
     * @param document
     * @param out
     * @throws DOMConverterException
     */
    public void writeXMLFragment(Document document, Writer out) throws DOMConverterException {

        writeXML(document,null,null,out,true);
    }
    /**
     * Writes DOM document as XML stream.
     * @param document source document
     * @param publicId public ID string or null
     * @param systemId system ID string or null 
     * @param out output writer
     * @throws DOMConverterException
     */
    public void writeXML(Document document, String publicId,String systemId,Writer out) throws DOMConverterException {
    	writeXML(document,publicId,systemId,out,false);
    }
    
    /**
     * Writes DOM document as XML stream as text fragment without XML header line.
     * @param document source document
     * @param publicId public ID string or null
     * @param systemId system ID string or null 
     * @param out output writer
     * @param omitXMLDeclaration omit XML declaration
     * @throws DOMConverterException
     */
    public void writeXML(Document document, String publicId,String systemId,Writer out,boolean omitXMLDeclaration) throws DOMConverterException {

        TransformerFactory tff = TransformerFactory.newInstance();
        try{
        tff.setAttribute("indent-number", new Integer(indentNumber));
        }catch(IllegalArgumentException iae){
            // Do nothing
        }
        Transformer tf = null;
        try {
            tf = tff.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new DOMConverterException(e);
        }
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.METHOD, "xml");
        if(omitXMLDeclaration){
        	tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        if (publicId !=null){
            tf.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,publicId);
        }
        if (systemId !=null){
            tf.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,systemId);
        }
        DOMSource ds = new DOMSource(document);
        StreamResult sr = new StreamResult(out);
        try {
            tf.transform(ds, sr);
        } catch (TransformerException e) {
            throw new DOMConverterException(e);
        }
    }
    
    /**
     * Writes DOM document as XML stream.
     * @param document
     * @param res
     * @throws DOMConverterException
     */
    public void writeXML(Document document, Result res) throws DOMConverterException {

        TransformerFactory tff = TransformerFactory.newInstance();
        try{
        tff.setAttribute("indent-number", new Integer(indentNumber));
        }catch(IllegalArgumentException iae){
            // Do nothing
        }
        Transformer tf = null;
        try {
            tf = tff.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new DOMConverterException(e);
        }
        tf.setOutputProperty(OutputKeys.METHOD, "xml");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
       
        DOMSource ds = new DOMSource(document);
        
        try {
            tf.transform(ds, res);
        } catch (TransformerException e) {
            throw new DOMConverterException(e);
        }
    }
    
    public String writeToString(Document d) throws DOMConverterException{
    	StringWriter sw=new StringWriter();
    	writeXML(d, sw);
    	
    	return sw.toString();
    }
    
    public String writeFragmentToString(Document d) throws DOMConverterException{
    	StringWriter sw=new StringWriter();
    	writeXMLFragment(d, sw);
    	
    	return sw.toString();
    }
   
    
    public Document readXML(InputStream is) throws DOMConverterException {
        return readXML(is,null);
    }
    
    public Document readXML(Reader is) throws DOMConverterException {
        return readXML(new InputSource(is));
    }
    
       
    
    /**
     * Read DOM document from stream.
     * @param is
     * @return DOMdocument
     * @throws DOMConverterException
     */
    public Document readXML(InputStream is,String systemId) throws DOMConverterException {
        InputSource iSrc=new InputSource(is);
        if (systemId !=null){
            iSrc.setSystemId(systemId);
        }
        return readXML(iSrc);
    }
    
    /**
	 * Read DOM document from SAX input source.
	 * @param is
	 * @return DOM document
	 * @throws DOMConverterException
	 */
	public Document readXML(InputSource is) throws DOMConverterException {
		return readXML(is, (EntityResolver)null);
	}
	/**
	 * Read DOM document from SAX input source.
	 * @param is
	 * @param entityResolver entity resolver to inject DTD for validation
	 * @return DOM document
	 * @throws DOMConverterException
	 */
	public Document readXML(InputSource is,EntityResolver entityResolver) throws DOMConverterException {
        DocumentBuilder db=null;
        
        try {
            db = dbf.newDocumentBuilder();
            if(db.isValidating()){
                db.setErrorHandler(this);
            }
            if(entityResolver!=null){
            	db.setEntityResolver(entityResolver);
            }
            
        } catch (ParserConfigurationException e) {
            throw new DOMConverterException(e);
        }
		Document d=null;
        saxException=null;
		try{   
			d = db.parse(is);
            if (saxException!=null)throw saxException;
		}catch (SAXParseException ex){
			throw new DOMConverterException("SAXParse-Exception in line " + ex.getLineNumber() + 
					", column " + ex.getColumnNumber() + ":\n " + ex.getLocalizedMessage(),ex);
		}catch (SAXException ex){
			throw new DOMConverterException(ex);
		}catch (IOException ex){
			throw new DOMConverterException(ex);
		}finally{
			if(is!=null){
				try{
				Reader reader=is.getCharacterStream();
				if(reader!=null){
					reader.close();
				}else{
					InputStream byteStream=is.getByteStream();
					if(byteStream!=null){
						byteStream.close();
					}
				}
				}catch(IOException ioe){
					throw new DOMConverterException("Could not close XMLinput stream!");
				}
			}
		}
		return d;
	}
    
	
	
    /**
     * Read DOM document from URI.
     * @param uri
     * @return DOM document 
     * @throws DOMConverterException
     */
    public Document readXML(String uri) throws DOMConverterException {
    	return readXML((InputSource)null, uri);
    }
    /**
     * Read DOM document from URI.
     * @param uri
     * @return DOM document 
     * @throws DOMConverterException
     */
    public Document readXML(InputSource  is,String uri) throws DOMConverterException {
       
        DocumentBuilder db=null;
        
        try {
            db = dbf.newDocumentBuilder();
            if(db.isValidating()){
                db.setErrorHandler(this);
            }
        } catch (ParserConfigurationException e) {
            throw new DOMConverterException(e);
        }
        
        Document d=null;
        try
        {
        	if(is!=null){
        		d=db.parse(is);
        	}else{
        		d = db.parse(uri);
        	}
            if (saxException!=null)throw saxException;
        }
        catch (SAXParseException ex)
        {
          
            throw new DOMConverterException("SAXParse-Exception in line " + ex.getLineNumber() + 
                    ", column " + ex.getColumnNumber() + ":\n " + ex.getLocalizedMessage());
        }
        catch (SAXException ex)
        {
            throw new DOMConverterException(ex);
        } 
        catch (IOException ex)
        {
            throw new DOMConverterException(ex);
        }
        return d;
    }

    public void setValidating(boolean b) {
        dbf.setValidating(b);
        
    }



    public void error(SAXParseException exception) throws SAXException {
      saxException=exception;
    }



    public void fatalError(SAXParseException exception) throws SAXException {
       saxException=exception;
        
    }



    public void warning(SAXParseException exception) throws SAXException {
        // TODO Auto-generated method stub
        
    }
}
