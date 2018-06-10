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
 * Created on 03.02.2005
 *
 */
package ipsk.apps.speechrecorder.config;

import ipsk.beans.dom.DOMElements;
import ipsk.net.UploadCache;

/**
 * Configuration for upload cache.
 * @author klausj
 *
 */
@DOMElements({"uploadCacheClassname","audioStorageType","waitForCompleteUpload"})
public class CacheConfiguration {
	//public static long INFINITE=-1;

	private boolean waitForCompleteUpload=true;
	private String audioStorageType=null;
	private String uploadCacheClassname="ipsk.net.http.HttpUploadCache";
	private int transferRateLimit=UploadCache.UNLIMITED;
	
	/**
	 * 
	 */
	public CacheConfiguration() {
		super();
		
	}

	/**
	 * @return true if application should wait for complete upload before quit
	 */
	public boolean getWaitForCompleteUpload() {
		return waitForCompleteUpload;
	}

	/**
	 * @param b  true if application should wait for complete upload before quit
	 */
	public void setWaitForCompleteUpload(boolean b) {
		waitForCompleteUpload = b;
	}

	/**
	 * @return audio storage type
	 */
	public String getAudioStorageType() {
		return audioStorageType;
	}

	/**
	 * @param string audio storage type
	 */
	public void setAudioStorageType(String string) {
		audioStorageType = string;
	}

	/**
	 * @return class name of the upload cache implementation
	 */
	public String getUploadCacheClassname() {
		return uploadCacheClassname;
	}

	/**
	 * @param string class name of the upload cache implementation
	 */
	public void setUploadCacheClassname(String string) {
		uploadCacheClassname = string;
	}

	public int getTransferRateLimit() {
		return transferRateLimit;
	}

	public void setTransferRateLimit(int transferRateLimit) {
		this.transferRateLimit = transferRateLimit;
	}

}
