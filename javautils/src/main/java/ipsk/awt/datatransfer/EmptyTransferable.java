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

package ipsk.awt.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * An empty transfer object.
 * This is intended as a workaround to clear clipboard contents.
 * @author klausj
 *
 */
public class EmptyTransferable implements Transferable {

	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		throw new IOException("No transfer data available !");
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[0];
	}

	public boolean isDataFlavorSupported(DataFlavor arg0) {
		return false;
	}

}
