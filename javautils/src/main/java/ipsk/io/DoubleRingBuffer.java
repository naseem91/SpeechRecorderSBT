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

/**
 * @author klausj
 *
 */
public class DoubleRingBuffer {

    private double[] buffer;
    private int size=0;
    long writePosition=0;
    long readPosition=0;
  
    
    public DoubleRingBuffer(int size){
        super();
        buffer=new double[size];
        this.size=size;
    }
    
    
    public double[] getBuffer(){
        return buffer;
    }
    
    public int filled(){
        return (int)(writePosition-readPosition);
    }
 
    public int free(){
        return (size-filled());
    }
    
    private int bufferPosition(long position){
        return (int)(position % (long)size);
    }
    
    public int continuosAvailableToWrite(){
        int bufWritePosition=bufferWritePosition();
        int bufReadPosition=bufferReadPosition();
        if(bufReadPosition<=bufWritePosition){
            return size-bufWritePosition;
        }else{
            return bufReadPosition-bufWritePosition;
        }
        
    }
    
    public int bufferWritePosition(){
       return bufferPosition(writePosition);
    }
    
    public void written(int write){
        writePosition+=write;
    }
    
    public int continuosAvailableToRead(){ 
        int bufWritePosition=bufferWritePosition();
        int bufReadPosition=bufferReadPosition();
        if(bufReadPosition<=bufWritePosition){
            return bufWritePosition-bufReadPosition;
        }else{
            return size-bufReadPosition;
        }
    }
    
    
    
    public int bufferReadPosition(){
        return bufferPosition(readPosition);
        
    }
    
    
    public void read(int read){
        readPosition+=read;
        
    }
    
    public Double valueAtBufferPosition(int pos){
        return buffer[pos];
    }
    
    public Double valueAtPosition(long position){
        int bufPos=bufferPosition(position);
        return buffer[bufPos];
    }
    
    public Double read(){
        if(filled()<=0){
            return null;
        }
        int brp=bufferReadPosition();
        double val=buffer[brp];
        read(1);
        return val;
    }
    
    public void skip(long skip){
        readPosition+=skip;
    }
    
    public int getSize() {
        return size;
    }


    public long getWritePosition() {
        return writePosition;
    }



    public long getReadPosition() {
        return readPosition;
    }



}
