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

package ipsk.net;

import ipsk.io.VectorBuffer;
import ipsk.io.VectorBufferedInputStream;

import java.io.InputStream;
import java.net.URL;


/**
 * Holds a {@link ipsk.io.VectorBuffer VectorBuffer} for upload to an URL.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class UploadVectorBuffer extends Upload{
    /**
     * The upload cache uses object streaming, so we need a constant serial number of this class.
     */
    private static final long serialVersionUID = -916847294237350649L;
    
	private VectorBuffer vb;

	/**
	 * Create an upload object.
	 * The data to upload is hold in the vector buffer. The data will be uploaded to the given URL.
	 * @param vb the data
	 * @param url URL to upload to
	 */
	public UploadVectorBuffer(VectorBuffer vb, URL url) {
		super(url);
		this.vb = vb;
		status=IDLE;
	}

	/**
	 * Get the buffer.
	 * @return vector buffer
	 */
	public VectorBuffer getVectorBuffer() {
		return vb;
	}

	/**
	 * Set the vector buffer.
	 * @param vb vector buffer
	 */
	public void setVectorBuffer(VectorBuffer vb) {
		this.vb = vb;
	}

	/* (non-Javadoc)
	 * @see ipsk.net.Upload#getInputStream()
	 */
	public InputStream getInputStream() {
		return new VectorBufferedInputStream(getVectorBuffer());
	}

	/* (non-Javadoc)
	 * @see ipsk.net.Upload#getLength()
	 */
	public long getLength() {
		return getVectorBuffer().getLength();
	}

	/* (non-Javadoc)
	 * @see ipsk.net.Upload#delete()
	 */
	public void delete() {
		vb.getBuffers().clear();
	}

}
