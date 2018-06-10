//    IPS Java Utils
// 	  (c) Copyright 2018
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.swing.filechooser;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

/**
 * @author klausj
 *
 */
public class RestrictDirectoryFileSystemView extends FileSystemView {

    private File[] roots;
    
    /**
     * 
     */
    public RestrictDirectoryFileSystemView(File[] roots) {
        super();
        this.roots=roots;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.filechooser.FileSystemView#createNewFolder(java.io.File)
     */
    @Override
    public File createNewFolder(File arg0) throws IOException {
        throw new UnsupportedOperationException("Creation of directory not supported!");
    }
    
    public File getDefaultDirectory(){
        return roots[0];
    }
    
    public File getHomeDirectory(){
        return roots[0];
    }
    
    @Override
    public File[] getRoots()
    {
        return roots;
    }

    @Override
    public boolean isRoot(File file)
    {
        for (File root : roots) {
            if (root.equals(file)) {
                return true;
            }
        }
        return false;
    }

}
