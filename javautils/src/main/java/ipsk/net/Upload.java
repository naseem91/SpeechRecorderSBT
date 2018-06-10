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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Instances of this class will be uploaded to a given URL. Contains data to upload, the current
 * status of the uploading process and the destination URL where to put the
 * data.
 * @see ipsk.net.UploadCache
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public abstract class Upload implements Serializable{
    
    // Do not change this !!!
    /**
     * The upload cache uses object streaming, so we need a constant serial number of this class.
     */
    static final long serialVersionUID = -8377017842906075301L;
    
    /**
     * Ready for uploading
     */
	public final static int IDLE = 0;
	/**
	 * In progress
	 */
	public final static int UPLOADING = 1;
	/**
	 * Canceled. The upload will be expunged !
	 */
	public final static int CANCEL =2;
	/**
	 * Done
	 */
	public final static int DONE = 3;
	/**
	 * Upload failed. Uploading will be retried in this session.
	 */
	public final static int FAILED = -1;
	/**
	 * Upload failed. Upload should be retried the next session.
	 */
    public final static int DROPPED = -2;
    

	protected URL url;

	// status is accessed by different threads (Event- and Upload-thread)
	// otherwise all access methods have to be synchronized
    protected transient volatile int status=0;

	protected String name;
	protected String comment;
	protected int failedUploadAttempts = 0;
	protected String mimeType=null;
	protected byte[] checksum=null;
	protected String checksumDigest=null;
	
	/**
	 * Upload ID
	 */
	protected transient Long id=null;
	
	/**
	 * Create new upload.
	 *
	 */
	public Upload(){
        url=null;
	}
	
	/**
	 * Create upload.
	 * @param url target URL
	 */
	public Upload(URL url){
		this();
		this.url=url;	
	}
	/**
	 * Get the URL to upload.
     * 
	 * @return upload URL
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * Set the URL for upload.
     * 
     * @param url URL
	 */
	public void setUrl(URL url) {
		this.url = url;
	}


	/**
	 * Get the current status of this upload.
     * 
	 * @return current status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set status of this upload.
     * 
	 * @param i
	 */
	public void setStatus(int i) {
		status = i;
	}
	
	public abstract InputStream getInputStream() throws UploadException;
	public abstract long getLength();
	public abstract void delete();
	

	/**
	 * String representation.
	 * 
	 * @return string representation
	 */
	public String toString(){
		return new String(getClass().getName()+": URL: "+getUrl()+", Length: "+getLength()+", State: "+getStatus());
	}

	/**
	 * Returns comment.
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Returns name.
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set comment.
	 * @param string comment
	 */
	public void setComment(String string) {
		comment = string;
	}

	/** Set name.
	 * @param string name
	 */
	public void setName(String string) {
		name = string;
	}
	
	/**
	 * Get the MIME type of upload content.
	 * @return MIME type
	 */
	public String getMimeType() {
		return mimeType;
	}
	
	/**
	 * Set the MIME type of upload content.
	 * @param mimeType MIME type
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public void createChecksum(String digestName) throws UploadException{
		InputStream is =  getInputStream();

	     byte[] buffer = new byte[1024];
	     MessageDigest md;

			try {
				md = MessageDigest.getInstance(digestName);
			} catch (NoSuchAlgorithmException e) {
				
				e.printStackTrace();
				try {
					is.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				throw new UploadException(e);
			}
	
	     int read=0;
	     try{
	     do {
	      read = is.read(buffer);
	      if (read > 0) {
	        md.update(buffer, 0, read);
	        }
	      } while (read >=0);
	     }catch(IOException e){
	    	 
	     }finally{
	     if(is!=null)
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new UploadException(e);
			}
	     }
	     checksum=md.digest();
	     checksumDigest=digestName;
	}
	
    public int hashCode() {
        int hash = 7;
        int urlHC = (null == url ? 0 : url.hashCode());
        int nameHC = (null == name ? 0 : name.hashCode());
        int commentHC = (null == comment ? 0 : comment.hashCode());
        int mimeTypeHC = (null == mimeType ? 0 : mimeType.hashCode());
        hash = 31 * hash + urlHC;
        hash = 31 * hash + nameHC;
        hash = 31 * hash + commentHC;
        hash = 31 * hash +mimeTypeHC;
        return hash;
    }
	
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Upload))
            return false;
        Upload upload = (Upload) obj;
        if (!upload.getUrl().equals(url))
            return false;
	
        if (name == null) {
            if (upload.getName() != null)
                return false;
        } else {
            if (!name.equals(upload.getName()))
                return false;
        }
        if (comment == null) {
            if (upload.getComment() != null)
                return false;
        } else {
            if (!comment.equals(upload.getComment()))
                return false;
        }
        if (mimeType == null) {
            if (upload.getMimeType() != null)
                return false;
        } else {
            if (!mimeType.equals(upload.getMimeType()))
                return false;
        }
        return true;
}

    /**
     * Get number of failed upload attempts.
     * @return number of failed upload attempts
     */
    public int getFailedUploadAttempts() {
        return failedUploadAttempts;
    }
	
    /**
     * Set number of failed upload attempts.
     * @param failedUploadAttempts
     *            the failedUploadAttempts to set
     */
    public void setFailedUploadAttempts(int failedUploadAttempts) {
        this.failedUploadAttempts = failedUploadAttempts;
}
    public void incFailedAttemptsCounter() {
        failedUploadAttempts++;

    }

    /**
     * Get the checksum.
     * @return checksum data
     */
	public byte[] getChecksum() {
		return checksum;
	}	

	/**
	 * Get used checksum digest, e.g. MD5
	 * @return checksum digest name
	 */
	public String getChecksumDigest() {
		return checksumDigest;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
