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

import ipsk.awt.AWTEventTransferAgent;
import ipsk.xml.DOMConverter;
import ipsk.xml.DOMConverterException;

import org.xml.sax.InputSource;

/**
 * XML parser in background thread.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class XMLParserThread extends Thread {

	private AWTEventTransferAgent<XMLParserListener,XMLParserEvent> etTa=new AWTEventTransferAgent<XMLParserListener, XMLParserEvent>(){

		@Override
		protected void fireEvent(XMLParserListener listener,
				XMLParserEvent event) {
			listener.update(event);
			
		}
		
	};
	private volatile DOMConverter domConverter;

//	private XMLParserListener listener;

	private volatile InputSource inputSource;

	public XMLParserThread(ThreadGroup group, InputSource inputSource)
			throws DOMConverterException {
		super(group, "XML Validator");
		this.inputSource = inputSource;
		domConverter = new DOMConverter();
		domConverter.setValidating(true);

	}

	public void run() {

		try {
			domConverter.readXML(inputSource);
			
//			if (listener != null) {
//				listener.update(new XMLParserEvent(this));
				etTa.fireAWTEventLater(new XMLParserEvent(this));
//			}

		} catch (DOMConverterException e) {

//			if (listener != null) {
//				listener.update(new XMLParserEvent(this, e));
				etTa.fireAWTEventLater(new XMLParserEvent(this, e));
//			}

		}
	}

	
	public synchronized void addListener(XMLParserListener listener) {
//		this.listener = listener;
		etTa.addListener(listener);
	}
	public synchronized void removeListener(XMLParserListener listener) {
//		this.listener = listener;
		etTa.removeListener(listener);
	}

}
