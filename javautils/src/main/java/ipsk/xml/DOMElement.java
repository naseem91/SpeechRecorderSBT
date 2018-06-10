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

import org.w3c.dom.Attr;
import org.w3c.dom.Node;


/**
 * Classes which implement this interface provide their own DOM representation and are not inspected by {@link ipsk.beans.DOMCodec }.
 *
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public interface DOMElement {

	public void setChildElements(Node[] childElements);
	public Node[] getChildElements();
	public void setAttributes(DOMAttribute[] attributes);
	public Attr[] getAttributes();
	
}
