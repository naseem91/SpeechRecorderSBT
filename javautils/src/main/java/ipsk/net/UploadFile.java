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

import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;


/**
 * Holds a {@link java.io.File File} for upload to an URL.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class UploadFile extends Upload implements Serializable {
    
    // Do not change !!!
    /**
     * The upload cache uses object streaming, so we need a constant serial number of this class.
     */
    static final long serialVersionUID = 7573125662518988131L;
    
    
	protected File file;

    
    public UploadFile(){
        super();
        file=null;
    }
    
	/**
	 * Create an upload object.
	 * The data to upload is hold in the file. The data will be uploaded to the given URL.
	 * @param vb the data
	 * @param url URL to upload to
	 */
	public UploadFile(File vb, URL url) {
		super(url);
		this.file = vb;
		status = IDLE;
	}

	/**
	 * Get the file.
	 * @return file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Set the file.
	 * @param f file
	 */
	public void setFile(File f) {
		this.file = f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.net.Upload#getInputStream()
	 */
	public InputStream getInputStream() throws UploadException {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new UploadException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.net.Upload#getLength()
	 */
	public long getLength() {
		if(file==null){
			return 0;
		}else{
			return file.length();
		}
	}

	/**
	 * Deletes the file.
	 */
	public void delete() {
		file.delete();
	}

	public synchronized Object clone() {
		UploadFile newObj = new UploadFile(file, url);
		newObj.name=name;
		newObj.comment=comment;
		newObj.status = status;
		return newObj;
	}

	public int hashCode() {
	    int hash=super.hashCode();
	    int fileHC=(null == file ? 0 : file.hashCode());
	    hash = 31 * hash + fileHC;
		return hash;
	}

	public boolean equals(Object obj) {
	    if (obj==null) return false;
	    if (obj==this)return true;
	    if(obj instanceof UploadFile){
	        UploadFile uploadFile=(UploadFile)obj;
	        if (uploadFile.getFile().equals(file) && super.equals(obj)) return true;
	    }
			return false;
		}

	public static void main(String[] args) {
		try {
            UploadFile t1=new UploadFile(new File("cache_a.wav"),new URL("http://localhost:80/a1.wav"));
            System.out.println("T1: "+t1.hashCode());
            UploadFile t2=new UploadFile(new File("cache_a2.wav"),new URL("http://localhost:80/a1.wav"));
            System.out.println("T2: "+t2.hashCode());
            UploadFile t3=new UploadFile(new File("cache_a.wav"),new URL("http://127.0.0.1/a1.wav"));
            System.out.println("T3: "+t3.hashCode());
            System.out.println("T1==T2 ?: "+t1.equals(t2));
            System.out.println("T2==T3 ?: "+t2.equals(t3));
            System.out.println("T1==T3 ?: "+t1.equals(t3));
            XMLEncoder xmlEnc=new XMLEncoder(new FileOutputStream("Test.xml"));
            xmlEnc.writeObject(t1);
            xmlEnc.close();
		} catch (Exception e) {
            // TODO Auto-generated catch block
			e.printStackTrace();
		}

		}

	}

