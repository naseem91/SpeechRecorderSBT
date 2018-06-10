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
public class InterleavedFloatStreamSplitter {

    private InterleavedFloatStream interleavedSrcStream;
    
    public InterleavedFloatStreamSplitter(InterleavedFloatStream interleavedFloatStream){
        this.interleavedSrcStream=interleavedFloatStream;
    }
    
    
    public FloatStream[] getFloatStreams(){
        FloatStream[] streams=null;
        Integer channels=interleavedSrcStream.getChannels();
        if(channels!=null){
            streams=new FloatStream[channels];
            for(int ch=0;ch<channels;ch++){
                streams[ch]=new FloatStreamAdapter(interleavedSrcStream, ch);
            }
        }
        return streams;
    }
    
    public FloatStream getFloatStream(int channel){
        FloatStream fs=new FloatStreamAdapter(interleavedSrcStream, channel);
        return fs;
    }
    
}
