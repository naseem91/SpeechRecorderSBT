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

package ipsk.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Basic class for framed editing input streams.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public abstract class FramedEditingInputStream extends FramedInputStream {

    protected InputStream is;
    
    /**
     * @param frameSize
     */
    public FramedEditingInputStream(InputStream is,int frameSize) {
        super(frameSize);
        this.is=is;
       
    }

   public void close() throws IOException {
       is.close();
   }

}
