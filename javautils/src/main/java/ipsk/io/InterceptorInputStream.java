//    IPS Java Utils
// 	  (c) Copyright 2011
// 	  Institute of Phonetics and Speech Processing,
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * @author klausj
 *
 */
public class InterceptorInputStream extends InputStream {

    private Vector<IOutputStream> outputStreams=new Vector<IOutputStream>();
    private InputStream srcInputStream;
    private boolean outputStreamsClosed=false;
    
    private byte[] oneByteBuffer=new byte[1];
    
    public InterceptorInputStream(InputStream srcInputStream){
        super();
        this.srcInputStream=srcInputStream;
    }
    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
       int sr=srcInputStream.read();
       if(sr==-1){
           closeOutputStreams();
           return -1;
       }else{
           writeToOutputStreams(sr);
           return sr;
       }
    }
    
    
    public int read(byte[] buf,int offset,int len) throws IOException {
       int sr=srcInputStream.read(buf,offset,len);
       if(sr==-1){
           closeOutputStreams();
           return -1;
       }else{
           writeToOutputStreams(buf,offset,len);
           return sr;
       }
    }
    
    public int read(byte[] buf) throws IOException {
        int sr=srcInputStream.read(buf);
        if(sr==-1){
            closeOutputStreams();
            return -1;
        }else{
            writeToOutputStreams(buf,0,sr);
            return sr;
        }
     }
    
    public void close() throws IOException{
        closeOutputStreams();
        srcInputStream.close();
        
    }
    
    private void writeToOutputStreams(int b) throws IOException{
        for(IOutputStream os:outputStreams){
            oneByteBuffer[0]=(byte) (0x000000FF & b);
            os.write(oneByteBuffer,0,1);
        }
    }
    private void writeToOutputStreams(byte[] buf, int offset,int len) throws IOException{
        for(IOutputStream os:outputStreams){
            os.write(buf, offset, len);
        }
    }
    
    private void closeOutputStreams() throws IOException{
        if(!outputStreamsClosed){
        for(IOutputStream os:outputStreams){
                os.close();
         
        }
        outputStreamsClosed=true;
        }
    }
    
    /**
     * Add output stream.
     * @param os output stream
     */
    public void addOutputStream(IOutputStream os) {
        synchronized (outputStreams) {
            if (os != null && ! outputStreams.contains(os)) {
                outputStreams.addElement(os);
            }
        }
        
    }

    /**
     * Remove output stream
     * @param os output stream
     */
    public void removeOutputStream(IOutputStream os) {
        synchronized(outputStreams){
            if (os != null) {
                outputStreams.removeElement(os);
            }
        }
    }

}
