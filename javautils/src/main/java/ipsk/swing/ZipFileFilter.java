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

package ipsk.swing;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * File list filter to display zip files (*.zip) in filechoosers.
 * @author klausj
 *
 */
public class ZipFileFilter extends FileFilter {

	public static String extension = "zip";
	
	/**
	 * Create Zip file filter.
	 */
	public ZipFileFilter() {
		super();

	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File arg0) {
		if (arg0.isDirectory())
			return true;
		String name = arg0.getName();
		int extIndex = name.lastIndexOf('.');
		if (extIndex == -1)
			return false;
		String ext = name.substring(extIndex + 1);

		if (ext.equalsIgnoreCase(extension))
			return true;

		return false;

	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {

		return "ZIP compressed files (*." + extension + ")";
	}

}
