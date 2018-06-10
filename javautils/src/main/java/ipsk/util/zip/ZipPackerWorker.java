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
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Packs Zip archive in separate thread.
 * @see ipsk.util.zip.ZipPacker
 * @author klausj
 *
 */
// TODO not localized
public class ZipPackerWorker extends ProgressWorker{

	public static final boolean DEFAULT_LIMIT_TO_32BIT_SIZES=false;
	public static long MAX_32BIT_ZIP_FILE_SIZE=4294967295L;
	public static int MAX_32BIT_ZIP_FILE_ENTRIES=65536;
	
	public static int DEFAULT_BUF_SIZE=32768;
	
	private ZipOutputStream zipStream;
	private int bufferSize=DEFAULT_BUF_SIZE;
	private byte[] buf;
	private long sizeDone=0;
	
	private File srcDir;
	private boolean packRecusive;
	private OutputStream outputStream;
	
	private boolean limitTo32bitSizes;
	
	

	public ZipPackerWorker(){
		super();
	}
	
	public void open() throws WorkerException{
		progressStatus.setLength(ProgressStatus.LENGTH_UNKNOWN);
		zipStream=new ZipOutputStream(outputStream);
		buf=new byte[bufferSize];
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
			if(limitTo32bitSizes){

				length = calcRawSize(srcDir,MAX_32BIT_ZIP_FILE_SIZE,MAX_32BIT_ZIP_FILE_ENTRIES,0);

			}else{
				length=calcRawSize(srcDir);
			}
		} catch (ZipPackerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		progressStatus.setLength(length);
		fireProgressEvent();
	
		try {
			packDirRecursive(srcDir);
			zipStream.close();
		} catch (IOException e) {
			throw new WorkerException(e);
		}
	}
	
	
	private void packDirRecursive(File dir) throws IOException{
		packDirRecursive("",dir);
	}
	
	private long calcRawSize(File dir,long maxSingleFileSize,long maxEntries,long entries) throws ZipPackerException{
		long size=0;
		for(File f: dir.listFiles()){
			entries++;
			if(entries>maxEntries){
				throw new ZipPackerException("Too many ZIP archive entries.");
			}
			if (f.isDirectory()){
				size+=calcRawSize(f,maxSingleFileSize,maxEntries,entries);
			}else{
				long fileLength=f.length();
				if(fileLength>maxSingleFileSize){
					throw new ZipPackerException("File entry "+f+" too large.");
				}
				size+=fileLength;
			}
		}
		return size;
	}
	
	private long calcRawSize(File dir) throws ZipPackerException{
		long size=0;
		for(File f: dir.listFiles()){
			
			if (f.isDirectory()){
				size+=calcRawSize(f);
			}else{
				long fileLength=f.length();
				size+=fileLength;
			}
		}
		return size;
	}
	
	
	private void packDirRecursive(String base,File dir) throws IOException{
		if(hasCancelRequest()){
			progressStatus.setMessage(new LocalizableMessage("Canceled !"));
			return;
		}
		String dirName=base+dir.getName();
		// make sure directory name ends with "/"
		if (!dirName.endsWith(File.separator)){
			dirName=dirName.concat("/");
		}else{
			dirName.replaceFirst(File.separator+"$", "/");
		}
		ZipEntry entry=new ZipEntry(dirName);
        zipStream.putNextEntry(entry);
        zipStream.closeEntry();
//		boolean dirEmpty=true;
		for(File f: dir.listFiles()){
			if(hasCancelRequest()){
				progressStatus.setMessage(new LocalizableMessage("Canceled !"));
				return;
			}
			if (f.isDirectory()){
				packDirRecursive(dirName, f);
			}else{
				progressStatus.setMessage(new LocalizableMessage("Pack "+f.getName()+" ..."));
				fireProgressEvent();
				FileInputStream fis=new FileInputStream(f);
				ZipEntry fe=new ZipEntry(dirName+f.getName());
				zipStream.putNextEntry(fe);
				int read;
				while((read=fis.read(buf))!=-1){
					zipStream.write(buf, 0, read);
					sizeDone+=read;
					progressStatus.setProgress(sizeDone);
					fireProgressEvent();
				}
				zipStream.closeEntry();
				fis.close();
//				dirEmpty=false;
			}
		}
//		if(dirEmpty){
//			// add empty directories separately
//			ZipEntry entry=new ZipEntry(dirName);
//			zipStream.putNextEntry(entry);
//			zipStream.closeEntry();
//		}
		
	}
	
	public long getSizeDone() {
		return sizeDone;
	}

	public void setSizeDone(long sizeDone) {
		this.sizeDone = sizeDone;
	}
	
	
	public boolean isPackRecusive() {
		return packRecusive;
	}

	public void setPackRecusive(boolean packRecusive) {
		this.packRecusive = packRecusive;
	}

	public File getSrcDir() {
		return srcDir;
	}

	public void setSrcDir(File srcDir) {
		this.srcDir = srcDir;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}


	public int getBufferSize() {
		return bufferSize;
	}


	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}


}
