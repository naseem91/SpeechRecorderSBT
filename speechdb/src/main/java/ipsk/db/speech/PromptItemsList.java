/*
 * Date  : 11.01.2015
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.db.speech;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class PromptItemsList extends ArrayList<PromptItem> implements Transferable {
	public static final DataFlavor CLASS_DATA_FLAVOR = new DataFlavor(
			PromptItemsList.class, null);

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer
	 * .DataFlavor)
	 */
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { CLASS_DATA_FLAVOR };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.
	 * datatransfer.DataFlavor)
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (CLASS_DATA_FLAVOR.equals(flavor));
	}

}
