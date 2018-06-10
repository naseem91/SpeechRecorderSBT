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

/**
 * @author klausj
 *
 */
public class InterleavedFloatStreamMonoAdapter implements InterleavedFloatStream {

    private FloatStream srcStream;
    private double[] monoBuf;
    
    public InterleavedFloatStreamMonoAdapter(FloatStream srcStream){
        this.srcStream=srcStream;
        monoBuf=new double[0];
    }
    
   
    /* (non-Javadoc)
     * @see ipsk.io.InterleavedFloatStream#read(double[][], int, int)
     */
    public int read(double[][] buf, int offset, int len) throws IOException {
        if(monoBuf.length<len){
            monoBuf=new double[len];
        }
        int r=srcStream.read(monoBuf,0, len);
        if(r>0){
            for(int i=0;i<r;i++){
                buf[offset+i][0]=monoBuf[i];
            }
        }
        return r;
        
    }

    /* (non-Javadoc)
     * @see ipsk.io.InterleavedFloatStream#skip(long)
     */
    public long skip(long skip) throws IOException {
      
        return srcStream.skip(skip);
    }

    /* (non-Javadoc)
     * @see ipsk.io.InterleavedFloatStream#close()
     */
    public void close() throws IOException {
        srcStream.close();

    }


    /* (non-Javadoc)
     * @see ipsk.io.InterleavedFloatStream#getChannels()
     */
    public Integer getChannels() {
       
        return 0;
    }

}
