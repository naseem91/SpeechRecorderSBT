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

package ipsk.awt;

import ipsk.awt.UpdateListener;
import java.util.EventObject;

/**
 * Convenience Class for listeners with single update() method interface.
 * Transfers update events to the AWT event thread.
 * @author klausj
 *
 */
public class UpdateAWTEventTransferAgent<L extends UpdateListener<E>, E extends EventObject> extends AWTEventTransferAgent<L,E> {
	
	protected void fireEvent(L listener,E event){
		listener.update(event);
	}
}
