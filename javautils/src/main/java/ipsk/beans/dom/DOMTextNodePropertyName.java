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

package ipsk.beans.dom;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;;


/**
 * Convert the named bean property as XML text node.
 * A bean can only have one property as text node.
 * @author klausj
 *
 */
@Target(ElementType.TYPE)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface DOMTextNodePropertyName {
	public String value();
	public boolean asCDATANode() default false;
}