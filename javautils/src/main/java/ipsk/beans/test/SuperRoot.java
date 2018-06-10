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

package ipsk.beans.test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ipsk.beans.DOMElementConvertible;

/**
 * @author klausj
 *
 */
public class SuperRoot implements DOMElementConvertible{
	public final static String ELEMENT_NAME="script";
	private String superRootName="Super root class name";

	public String getSuperRootName() {
		return superRootName;
	}

	public void setSuperRootName(String superRootName) {
		this.superRootName = superRootName;
	}

	/* (non-Javadoc)
	 * @see ipsk.beans.DOMElementConvertible#toElement(org.w3c.dom.Document)
	 */
	public Element toElement(Document d) {
		Element superRoot=d.createElement(ELEMENT_NAME);
		if(superRootName!=null){
		Element srnE=d.createElement("superRootName");
			superRoot.appendChild(srnE);
		}
		return superRoot;
	}


	
	
	
}
