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

package ips.incubator.io;

import ipsk.io.FloatStream;
import ipsk.io.InterleavedFloatStream;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author klausj
 *
 */
public class InterleavedJoinFloatStream implements InterleavedFloatStream{

    private static final int DEF_BUF_SIZE=1024;
    private int bufSize=DEF_BUF_SIZE;
    private FloatStream[] srcStreams;
    private Channel[] channels;
    public class Channel{
        FloatStream srcStream;
        int avail=0;
        int bufPos=0;
        double[] buf;
        boolean eof=false;
    }
   
   public InterleavedJoinFloatStream(FloatStream[] srcStreams){
       super();
       this.srcStreams=srcStreams;
       channels=new Channel[srcStreams.length];
       for (int ch=0;ch<channels.length;ch++){
           Channel c=channels[ch];
           c.srcStream=srcStreams[ch];
           c.buf=new double[bufSize];
       }
      
   }
/* (non-Javadoc)
 * @see ipsk.io.InterleavedFloatStream#read(double[][], int, int)
 */
public int read(double[][] buf, int offset, int len) throws IOException {
    if(len>bufSize){
        len=bufSize;
    }
    int minAvail=Integer.MAX_VALUE;
    int maxAvail=Integer.MIN_VALUE;
    boolean allEof=true;
    for(Channel c :channels){
        if(!c.eof){
            allEof=false;
        }
        if(c.avail<minAvail){
            minAvail=c.avail;
        }
        if(c.avail>maxAvail){
            maxAvail=c.avail;
        }
    }
    if(allEof)return -1;
    
    if(minAvail==0){
        minAvail=Integer.MAX_VALUE;
        // fill channels
        for(int ch=0;ch<channels.length;ch++){
            Channel c=channels[ch];
            if(c.eof){
                c.avail=bufSize;
            }else{
                int os=c.avail;
                int toRead=len-os;
                if(toRead>0){
                    int r=c.srcStream.read(c.buf, os, toRead);
                    if(r==-1){
                        c.eof=true;
                        Arrays.fill(c.buf, 0.0);
                        c.avail=bufSize;
                    }else{
                        c.avail+=r;
                    }
                }
            }
            if(c.avail<minAvail){
                minAvail=c.avail;
            }
        }
    }
    
    return 0;
}
/* (non-Javadoc)
 * @see ipsk.io.InterleavedFloatStream#skip(long)
 */
public long skip(long skip) throws IOException {
    // TODO Auto-generated method stub
    return 0;
}
/* (non-Javadoc)
 * @see ipsk.io.InterleavedFloatStream#close()
 */
public void close() throws IOException {
     for(FloatStream fs:srcStreams){
         fs.close();
     }
}
/* (non-Javadoc)
 * @see ipsk.io.InterleavedFloatStream#getChannels()
 */
public Integer getChannels() {
   return srcStreams.length;
   
}
    
}
