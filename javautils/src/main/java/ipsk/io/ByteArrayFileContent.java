//    IPS Java Utils
// 	  (c) Copyright 2014
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

package ipsk.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author klausj
 *
 */
public class ByteArrayFileContent extends AbstractFileContent{

    private byte[] data;
    /**
     * 
     */
    public ByteArrayFileContent(byte[] data,String mimeType,Charset charset,String preferredFilenameSuffix,String preferredExtension) {
        super(mimeType,charset,preferredFilenameSuffix,preferredExtension);
        this.data=data;
    }
    
    public ByteArrayFileContent(byte[] data,String mimeType,Charset charset,String preferredExtension) {
        super(mimeType,charset,null,preferredExtension);
        this.data=data;
    }

   

    /* (non-Javadoc)
     * @see ipsk.io.FileContent#getInputStream()
     */
    @Override
    public InputStream getInputStream() {
         return new ByteArrayInputStream(data);
    }

}
