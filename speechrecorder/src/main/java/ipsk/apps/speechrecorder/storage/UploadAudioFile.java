//    Speechrecorder
//    (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Created on 02.02.2005
 *
 */
package ipsk.apps.speechrecorder.storage;

import java.io.File;
import java.io.Serializable;
import java.net.URL;

import ipsk.net.UploadFile;

/**
 * Describes an audiofile to upload. 
 * @author klausj
 */
public class UploadAudioFile extends UploadFile implements Serializable{
    
    // Do not change !!!
    /**
     * The upload cache uses object streaming, so we need a constant serial number of this class.
     */
    static final long serialVersionUID = 1791634102107005739L;
    
    
	protected File audioCacheFile;
    
    /**
     * Create an audio file upload. 
     * @param uploadFile the (compressed/encoded) file to upload
     * @param audioCacheFile the original recorded audio file
     * @param url
     */
	public UploadAudioFile(File uploadFile,File audioCacheFile,URL url){
		super(uploadFile,url);
		this.audioCacheFile=audioCacheFile;
		
	}

	/**
     * Get original audio file.
	 * @return original audio file
	 */
	public File getAudioCacheFile() {
		return audioCacheFile;
	}

	/**
     * Set original audio file.
	 * @param file
	 */
	public void setAudioCacheFile(File file) {
		audioCacheFile = file;
	}
	
    /**
     * Clone this audio upload.
     * @return cloned audio upload 
     */
	public synchronized Object clone(){
		UploadAudioFile newObj=new UploadAudioFile(file,audioCacheFile,url);
		newObj.status=status;
		return newObj;
	}

}
