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



package ipsk.util.zip;

import ipsk.awt.ProgressWorker;
import ipsk.awt.WorkerException;
import ipsk.io.FileUtils;
import ipsk.io.StreamCopy;
import ipsk.swing.JProgressDialogPanel;
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Packs Zip archive in separate thread.
 * @see ipsk.util.zip.ZipPacker
 * @author klausj
 *
 */
// TODO not localized
public class UnzipWorker extends ProgressWorker{

	public static final boolean DEFAULT_LIMIT_TO_32BIT_SIZES=false;
	public static long MAX_32BIT_ZIP_FILE_SIZE=4294967295L;
	public static int MAX_32BIT_ZIP_FILE_ENTRIES=65536;
	
	public static int DEFAULT_BUF_SIZE=32768;
	
//	private ZipInputStream zipStream;
	private int bufferSize=DEFAULT_BUF_SIZE;
//	private byte[] buf;
	private long sizeDone=0;
	
	private File trgDir;
//	private boolean packRecusive;
//	private InputStream inputStream;
	private File sourceZipFile;
	
	private boolean limitTo32bitSizes;
	private boolean overwrite=false;
	private boolean trgDirCreated=false;
	

	public UnzipWorker(){
		super();
	}
	
	public void open() throws WorkerException{
		
		progressStatus.setLength(ProgressStatus.LENGTH_UNKNOWN);
		super.open();
	}
	
	
	public boolean isLimitTo32bitSizes() {
		return limitTo32bitSizes;
	}

	public void setLimitTo32bitSizes(boolean limitTo32bitSizes) {
		this.limitTo32bitSizes = limitTo32bitSizes;
	}

	
	public void doWork() throws WorkerException{
		progressStatus.setMessage(new LocalizableMessage("Calculating ZIP archive size..."));
		long length=0;
		try {
			length=calcZipSize();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new WorkerException("Could not determine ZIP file size",e1);
		}
		progressStatus.setLength(length);
		fireProgressEvent();
	
		try {
			unpack();
			
		} catch (IOException e) {
			throw new WorkerException(e);
		}
	}
	

	private long calcZipSize() throws IOException{
	    long totalSize=0;
	    
	    ZipFile zipFile=new ZipFile(sourceZipFile);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        try{
            while (entries.hasMoreElements()) {
                if(hasCancelRequest()){
                    progressStatus.setMessage(new LocalizableMessage("Canceled !"));
                    if(zipFile!=null){
                        zipFile.close();
                    }
                    return -1;
                }
                ZipEntry entry=entries.nextElement();
                long entrySize=entry.getSize();
                totalSize+=entrySize;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally{
            if(zipFile!=null){
                try {
                    zipFile.close();
                } catch (IOException e) {
                   throw e;
                }
            }
	}
	    return totalSize;
	}
	
	
	private void unpack() throws IOException{
	    if(hasCancelRequest()){
	        progressStatus.setMessage(new LocalizableMessage("Canceled !"));
	        return;
	    }

	    ZipFile zipFile=new ZipFile(sourceZipFile);
	    Enumeration<? extends ZipEntry> entries = zipFile.entries();
	    if(!trgDir.exists()){
	    	trgDirCreated=trgDir.mkdirs();
	    }
	    try{
	        while (entries.hasMoreElements()) {
	        	if(hasCancelRequest()){
	        		progressStatus.setMessage(new LocalizableMessage("Canceled !"));
	        		zipFile.close();
	        		if(!overwrite){
	        			if(trgDirCreated){
	        				FileUtils.deleteRecursive(trgDir);
	        			}else{
	        				// remove already created files
	        				zipFile=new ZipFile(sourceZipFile);
	        				entries = zipFile.entries();
	        				while (entries.hasMoreElements()) {

	        					ZipEntry entry = (ZipEntry) entries.nextElement();
	        					// We expect filenames relative to the workspace
	        					String name = entry.getName();
	        					File f = new File(trgDir, name);
	        					if(f.exists()){
	        						if(entry.isDirectory() && f.isDirectory()){
	        							FileUtils.deleteRecursive(f);
	        						}else{
	        							f.delete();
	        						}
	        					}
	        				}
	        				zipFile.close();
	        			}
	        		}
	        		return;
	        	}
	            ZipEntry entry = (ZipEntry) entries.nextElement();
	            // We expect filenames relative to the workspace
	            String name = entry.getName();
	            File f = new File(trgDir, name);
	           
	            progressStatus.setMessage(new LocalizableMessage("Unpack "+name+" ..."));
                fireProgressEvent();
	            if (entry.isDirectory()) {
	                f.mkdirs();
	            } else {
	                // Create missing directories
	                File dir = f.getParentFile();
	                if (!dir.exists()) {
	                    dir.mkdirs();
	                }
	                long entrySize=entry.getSize();
	                InputStream is = zipFile.getInputStream(entry);
	                FileOutputStream fos = new FileOutputStream(f);
	                // TODO no progress update for stream copy
	                StreamCopy.copy(is, fos,bufferSize);
	                sizeDone+=entrySize;
                    progressStatus.setProgress(sizeDone);
                    fireProgressEvent();
	            }
	        }
	    }catch(IOException ioe){
	        throw ioe;
	    }finally{
	        zipFile.close();
	    }

	}
	
	public long getSizeDone() {
		return sizeDone;
	}

	public void setSizeDone(long sizeDone) {
		this.sizeDone = sizeDone;
	}
	

	public int getBufferSize() {
		return bufferSize;
	}


	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

    public File getTrgDir() {
        return trgDir;
    }

    public void setTrgDir(File trgDir) {
        this.trgDir = trgDir;
    }

    public File getSourceZipFile() {
        return sourceZipFile;
    }

    public void setSourceZipFile(File sourceZipFile) {
        this.sourceZipFile = sourceZipFile;
    }

    public static void main(String[] args){
     
        UnzipWorker unzipWorker=new UnzipWorker();
        unzipWorker.setSourceZipFile(new File(args[0]));
        unzipWorker.setTrgDir(new File(args[1]));
      
       
        JProgressDialogPanel progressDialog=new JProgressDialogPanel(unzipWorker,"Unzip test","Unpacking...");
        try {
            unzipWorker.open();
        } catch (WorkerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        unzipWorker.start();
        
        JFrame f=new JFrame("Unzip test");
        Object val=progressDialog.showDialog(f);
       
        try {
            unzipWorker.close();
        } catch (WorkerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        State ws=unzipWorker.getStatus();
        if(!State.DONE.equals(ws)){
        	 if(State.CANCELLED.equals(ws)){
                 JOptionPane.showMessageDialog(f, "Unzip canceled.");
                 System.exit(1);
             }else{
            	 JOptionPane.showMessageDialog(f, "Unzip error!");
     
                System.exit(-1);
             }
        	
        }
    System.exit(0);
    }
       
        
    }
    

