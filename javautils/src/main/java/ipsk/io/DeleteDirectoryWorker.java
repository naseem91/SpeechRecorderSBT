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

import ipsk.awt.ProgressWorker;
import ipsk.awt.WorkerException;
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Deletes directory structure in a worker
 * @author klausj
 *
 */
// TODO not localized
public class DeleteDirectoryWorker extends ProgressWorker{

	
	private File directory;
	
	public DeleteDirectoryWorker(){
		super();
	}
	
	public void open() throws WorkerException{
		progressStatus.setLength(ProgressStatus.LENGTH_UNKNOWN);
		
		super.open();
	}
	
	
	private void deleteDirRecursive(File dir) throws WorkerException{
	    File[] childs=dir.listFiles();
	    if(childs==null){
	        throw new WorkerException("Could not read directory contents of:'"+dir+"'");
	    }else{
	        for(File f:childs){
	            if(f.isDirectory()){

	                deleteDirRecursive(f);
	            }else{
	                progressStatus.setMessage(new LocalizableMessage("Delete: "+f.getName()));
	                fireProgressEvent();
	                boolean deleted=f.delete();
	                if(!deleted){
	                    String errMsg="Could not delete: "+f.getName();
	                    progressStatus.error(new LocalizableMessage(errMsg));
	                    fireProgressEvent();
	                    throw new WorkerException(errMsg);
	                }
	            }
	        }
	    }
	    dir.delete();
	}
	
	public void doWork() throws WorkerException{
		progressStatus.setMessage(new LocalizableMessage("Deleting..."));
		deleteDirRecursive(directory);
		progressStatus.setMessage(new LocalizableMessage("Deleted."));
	}

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }
	


	
}
