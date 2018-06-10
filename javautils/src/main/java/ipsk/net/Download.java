//    IPS Java Utils

// 	  (c) Copyright 2012
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

package ipsk.net;

import java.io.OutputStream;
import java.net.URL;

/**
 * @author klausj
 *
 */
public class Download {

    private URL sourceUrl;
    private OutputStream contentOutputStream;
    
    public Download(URL sourceUrl,OutputStream contentOutputStream){
        super();
        this.sourceUrl=sourceUrl;
        this.contentOutputStream=contentOutputStream;
    }
    public URL getSourceUrl() {
        return sourceUrl;
    }
    public OutputStream getContentOutputStream() {
        return contentOutputStream;
    }
}
