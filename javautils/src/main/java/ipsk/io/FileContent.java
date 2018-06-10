/*
 * Date  : 19.03.2014
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.io;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public interface FileContent {

    public String getMimeType();

    public Charset getCharset();

    public String getPreferredExtension();

    public String getPreferredFilenameSuffix();
   
    public InputStream getInputStream();

}
