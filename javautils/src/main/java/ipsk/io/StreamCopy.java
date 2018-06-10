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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Utility methods to copy streams and files.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class StreamCopy {

    public static int DEF_BUF_SIZE=8192;
    
    public static Charset DEF_CHARSET=Charset.forName("UTF-8");
    
    public static void copy(File src,File dst) throws IOException{
        copy(src,dst,false);
    }
    
    public static void copy(File src,File dst,boolean createParentDirectories) throws IOException{
        if(createParentDirectories){
            File parentDir=dst.getParentFile();
            //if(parentDir.isDirectory()){
                parentDir.mkdirs();
            //}
        }
        FileInputStream fis=new FileInputStream(src);
        FileOutputStream fos=new FileOutputStream(dst);
        copy(fis,fos);
    }
    
    public static void copy(InputStream src,File dst,boolean createParentDirectories) throws IOException{
        if(createParentDirectories){
            File parentDir=dst.getParentFile();
            //if(parentDir.isDirectory()){
                parentDir.mkdirs();
            //}
        }
       
        FileOutputStream fos=new FileOutputStream(dst);
        copy(src,fos);
    }
    /**
    * Reads data from input stream and appends it to the output stream.
    * Only the input stream is closed at end of input stream data.
    * @param is input stream
    * @param os ouput stream
    * @throws IOException
    */ 
    public static void append(InputStream is,OutputStream os) throws IOException{
    	int read=0;
        byte[] buf=new byte[DEF_BUF_SIZE];
        try{
        while((read=is.read(buf))!=-1){
            if(read>0){
            os.write(buf,0,read);
            }
        }
        }catch(IOException ioe){
            throw ioe;
        }finally{
            is.close();
        }
    }
    /**
     * Reads data from input stream and writes it to output stream.
     * Input and output stream are closed at end of input stream data.
     * @param is
     * @param os
     * @throws IOException
     */
    public static void copy(InputStream is,OutputStream os) throws IOException{
       copy(is,os,DEF_BUF_SIZE);
    }
    public static void copy(InputStream is,OutputStream os,boolean closeOutStream) throws IOException{
        copy(is,os,closeOutStream,DEF_BUF_SIZE);
    }
    public static void copy(InputStream is,OutputStream os,int bufSize) throws IOException{
        copy(is,os,true,bufSize);
    }
    public static void copy(InputStream is,OutputStream os,boolean closeOutStream,int bufSize) throws IOException{
        int read=0;
        byte[] buf=new byte[bufSize];
        try{
        while((read=is.read(buf))!=-1){
            if(read>0){
            os.write(buf,0,read);
            }
        }
        }catch(IOException ioe){
            throw ioe;
        }finally{
            try{
                if(closeOutStream){
                    os.close();
                }
            }catch(IOException ioe){
                throw ioe;
            }finally{
                is.close();
            }
        }
        
    }
    
    public static void copyChars(Reader is,Writer os) throws IOException{
        int read=0;
        char[] buf=new char[DEF_BUF_SIZE];
        try{
        while((read=is.read(buf))!=-1){
            if(read>0){
            os.write(buf,0,read);
            }
        }
        }catch(IOException ioe){
            throw ioe;
        }finally{
            try{
            os.close();
            }catch(IOException ioe){
                  throw ioe;
            }finally{
                is.close();
            }
        }
    }
    
    public static void writeUTF8TextFile(String text,File file) throws IOException{
            writeTextFile(text, file, DEF_CHARSET);
     }
    
    public static void writeTextFile(String text,File file,Charset charset) throws IOException{
       FileOutputStream fos=new FileOutputStream(file);
       OutputStreamWriter osw=new OutputStreamWriter(fos,charset);
       osw.write(text);
       osw.close();
    }
    
    public static String readTextStream(InputStream textStream,Charset charset) throws IOException{
        
        InputStreamReader isr=new InputStreamReader(textStream, charset);
        StringBuffer sb=new StringBuffer();
        int read=0;
        char[] buf=new char[DEF_BUF_SIZE];
        try{
            while((read=isr.read(buf))!=-1){
                if(read>0){
                    sb.append(buf,0,read);
                }
            }
        }catch(IOException ioe){
            throw ioe;
        }finally{
            try{
                isr.close();
            }catch(IOException ioe){
                throw ioe;
            }
        }
        return sb.toString();
    }
    public static String readTextFile(File textFile,Charset charset) throws IOException{
        FileInputStream fis=new FileInputStream(textFile);
        return readTextStream(fis, charset);
    }
    
    public static void toSystemOut(InputStream is) throws IOException{
        copy(is,System.out,false);
    }
    public static void toSystemErr(InputStream is) throws IOException{
        copy(is,System.err,false);
    }

}
