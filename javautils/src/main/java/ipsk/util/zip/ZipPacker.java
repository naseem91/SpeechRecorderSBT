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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Packs directories to Zip archives.
 * @author klausj
 *
 */
public class ZipPacker {

	private ZipOutputStream zipStream;
	private int bufSize=2048;
	private byte[] buf;
	private long sizeDone=0;
	
	
	/**
	 * Create Zip packer.
	 * @param outputStream Zip coded output stream
	 */
	public ZipPacker(OutputStream outputStream){
		zipStream=new ZipOutputStream(outputStream);
		buf=new byte[bufSize];
	}
	
	
	public String packDir(String dirName) throws IOException{
	    
	    String zipEntryName=new String (dirName);
        // make sure directory name ends with "/"
        if (!zipEntryName.endsWith(File.separator)){
            zipEntryName=zipEntryName.concat("/");
        }else{
            zipEntryName.replaceFirst(File.separator+"$", "/");
        }

        // Fix: Only add zip entries for empty directories      
        // Fix fix: Most ZIP Archives contain the directories
        //         add the directory entries again
        ZipEntry entry=new ZipEntry(zipEntryName);
        zipStream.putNextEntry(entry);
        zipStream.closeEntry();
        return zipEntryName;
	}
	
	
	
	public void packFile(File file,String zipEntryName) throws IOException{
	    FileInputStream fis=new FileInputStream(file);
        ZipEntry fe=new ZipEntry(zipEntryName);
        zipStream.putNextEntry(fe);
        int read;
        while((read=fis.read(buf))!=-1){
            zipStream.write(buf, 0, read);
            sizeDone+=read;
        }
        zipStream.closeEntry();
        fis.close();
    }
	
	public void packStream(InputStream stream,String zipEntryName) throws IOException{
       
        ZipEntry fe=new ZipEntry(zipEntryName);
        zipStream.putNextEntry(fe);
        int read;
        while((read=stream.read(buf))!=-1){
            zipStream.write(buf, 0, read);
            sizeDone+=read;
        }
        zipStream.closeEntry();
        stream.close();
    }
	
	public void packData(byte[] data,String zipEntryName) throws IOException{
	    packData(data, 0, data.length, zipEntryName);
    }
	
	public void packData(byte[] data,int off, int len,String zipEntryName) throws IOException{
        ZipEntry fe=new ZipEntry(zipEntryName);
        zipStream.putNextEntry(fe);
        zipStream.write(data, off, len);
        sizeDone+=len;
        zipStream.closeEntry();
    }
	
	/**
	 * Packs all files in given directory recursively. 
	 * @param dir source directory
	 * @throws IOException
	 */
	public void packDirRecursive(File dir) throws IOException{
		packDirRecursive("",dir);
	}
	
	/**
	 * Calculate size of raw data (not the exact size of the Zip archive file).
	 * Returns sum of all file sizes of files found in directory. 
	 * @param dir source directory
	 * @return size of data in directory
	 */
	public long calcRawSize(File dir){
		long size=0;
		for(File f: dir.listFiles()){
			if (f.isDirectory()){
				size+=calcRawSize(f);
			}else{
				size+=f.length();
			}
		}
		return size;
	}
	
	protected void packDirRecursive(String base,File dir) throws IOException{

		String dirName=base+dir.getName();
		packDir(dirName);
		for(File f: dir.listFiles()){
			if (f.isDirectory()){
				packDirRecursive(dirName, f);
			}else{
			    packFile(f,dirName+f.getName());
			}
		}
	}
	
	/**
	 * Get the size of already processed data in bytes.
	 * @return input bytes already processed
	 */
	public long getSizeDone() {
		return sizeDone;
	}

	/**
	 * Set the size of already processed data in bytes.
	 * @param sizeDone
	 */
	public void setSizeDone(long sizeDone) {
		this.sizeDone = sizeDone;
	}
	
	/**
	 * Close the packer.
	 * Closes the underlying Zip output stream.
	 * @throws IOException
	 */
	public void close() throws IOException{
		zipStream.close();
	}
	
	/**
	 * Test method.
	 * @param args
	 */
	public static void main(String[] args){
		if(args.length !=2){
			System.err.println("Usage: "+ZipPacker.class.getName()+" zipFile srcDir");
		}
		File zip=new File(args[0]);
		ZipPacker zp=null;
		try {
			zp=new ZipPacker(new FileOutputStream(zip));
			zp.packDirRecursive(new File(args[1]));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}finally{
			try {
				zp.close();
			} catch (IOException e) {
			
				e.printStackTrace();
			}
		}
		
	}


}
