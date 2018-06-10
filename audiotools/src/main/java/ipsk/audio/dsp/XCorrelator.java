//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Nov 28, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.dsp;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSourceException;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class XCorrelator {

    public final static boolean DEBUG=true;
    
    public class CorrResult{
        private long position;
        private double minDistance;
        
        public CorrResult(long position,double minDistance){
            this.position=position;
            this.minDistance=minDistance;
        }
        /**
         * @return the minDistance
         */
        public double getMinDistance() {
            return minDistance;
        }
        /**
         * @param minDistance the minDistance to set
         */
        public void setMinDistance(double minDistance) {
            this.minDistance = minDistance;
        }
        /**
         * @return the position
         */
        public long getPosition() {
            return position;
        }
        /**
         * @param position the position to set
         */
        public void setPosition(long position) {
            this.position = position;
        }
        
        public String toString(){
            return "Correlation result: Maximum at frame position "+position+" with distance: "+minDistance; 
        }
        
    }
    
    
    public static int DEFAULT_BUFSIZE=2048;
    // the src stream
    private FloatRandomAccessStream srcStream;
    // the correlation stream
    private FloatRandomAccessStream corrStream;
    //long srcPos;
    private double[][] srcBuf;
    private double[][] corrBuf;
    int channels;
    
    
    public XCorrelator(FloatRandomAccessStream srcStream,FloatRandomAccessStream corrStream) throws AudioSourceException, AudioFormatNotSupportedException{
        this.srcStream=srcStream;
        this.corrStream=corrStream;
        channels=srcStream.getChannels();
        int corrChannels=corrStream.getChannels();
        if (channels!=corrChannels)throw new AudioFormatNotSupportedException();
        srcBuf=new double[DEFAULT_BUFSIZE][channels];
        corrBuf=new double[DEFAULT_BUFSIZE][channels];
        
    }
    
    
    public CorrResult correlate(long from, long to) throws AudioSourceException{
        if (DEBUG) System.out.println("Correlating from: "+from+" to: "+to);
     
       CorrResult res=new CorrResult(from,Double.MAX_VALUE);
       
        for(long srcPos=from;srcPos<=to;srcPos++){
           
            srcStream.setFramePosition(srcPos);
            corrStream.setFramePosition(0);
            double distance=0;
            int corrRead=0;
            while((corrRead=corrStream.readFrames(corrBuf, 0, DEFAULT_BUFSIZE)) != -1){
                int srcRead=0;
                while (srcRead < corrRead){
                int srcR=srcStream.readFrames(srcBuf,srcRead,corrRead-srcRead);
                if (srcR==-1){
                    // the source stream does not fit into the length of the corr stream
                    // so correlation is finished
                    return res;
                }
                srcRead+=srcR;
                }
//              calc buff distance
               
                for (int i=0;i<corrRead;i++){
                    for (int ch=0;ch<channels;ch++){
                        distance+=Math.abs(srcBuf[i][ch]-corrBuf[i][ch]);
                        
                    }
                }
                if (distance > res.getMinDistance()) continue;
            }
            
            if(distance<res.getMinDistance()){
                
                res.setMinDistance(distance);
                res.setPosition(srcPos);
            }
            
            
        }
        
        return res;
        
        
    }
    
    
    
    
}
