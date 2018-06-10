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

import java.io.File;
import java.io.IOException;

/**
 * @author klausj
 * 
 */
public class FileUtils {

	//private volatile int tempDirCount = 0;

	public static void copy(File src, File dst) throws IOException {
		StreamCopy.copy(src, dst, false);
	}

	/*
	  * 
	  */
	public static synchronized File createTempDir(String prefix, File directory) throws IOException {
		if (directory == null) {
			directory = new File(System.getProperty("java.io.tmpdir"));
		}
		File tmpDir = null;
		do {

			int rand = (int) (Math.random() * (double) 1000000);
			
			String tmpDirName = prefix + Integer.toString(rand);
			tmpDir = new File(directory, tmpDirName);

		} while (tmpDir.exists());
		boolean created = tmpDir.mkdir();
		if (!created) {
			throw new IOException("Could not create temporary directory!");
		}

		return tmpDir;

	}
	
	public static boolean deleteRecursive(File dir){
		File[] fileList=dir.listFiles();
		// remove contents
		if(fileList!=null){
			for(File f:fileList){
				if(f!=null){
					if(f.isDirectory()){
						FileUtils.deleteRecursive(f);
					}else{
						f.delete();
					}
				}
			}
		}
		// remove directory itself
		return dir.delete();
	}
	
	
	public static File moveToBackup(File file,String backupSuffix){
	    File retFile=null;
		if(file!=null && file.exists()){
		    File backupFile=new File(file.getPath()+backupSuffix);
		    boolean bkfDeleted=true;
		    if(backupFile.exists()){
		    	bkfDeleted=backupFile.delete();
		    }
		    if(bkfDeleted){
		    	boolean moved=file.renameTo(backupFile);
		    	if(!moved){
		    		new IOException("Backup file error! Could not move "+file+" as backup to "+backupFile+" !");
		    	}else{
		    	    // Success
		    	    retFile=backupFile;
		    	}
		    }else{
		    	new IOException("Backup file error! Could not delete old backup file:\n"+backupFile+" !");
		    }
		}
		return retFile;
	}

}
