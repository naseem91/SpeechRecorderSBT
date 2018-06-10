//    IPS Java Audio Tools
// 	  (c) Copyright 2014
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.audio;

/**
 * @author klausj
 *
 */
public enum Profile {
  
        CD_QUALITY("CD quality",(float)44100.0,16,true,null),
        SPEECH_RECORDING("Speech recording",(float)16000.0,16,true,new Integer(5000)),
        PHONE("Phone quality",(float)8000.0,8,false,null);
        
        private final float minSampleRate;
        private final int minSampleSizeInBits;
        private final boolean pcmSignedRecommended;
        private final Integer defaultMaxFreqSpectrumView;
        
        Profile(String value,float minSampleRate,int minSampleSizeInBits,boolean pcmSignedRecommended,Integer defaultMaxFreqSpectrumView) {
            this.value = value;
            this.minSampleRate=minSampleRate;
            this.minSampleSizeInBits=minSampleSizeInBits;
            this.pcmSignedRecommended=pcmSignedRecommended;
            this.defaultMaxFreqSpectrumView=defaultMaxFreqSpectrumView;
        }
        private final String value;

        public String value() {
            return value; 
        }
      
        public String toString() {
            return value; 
        }
        public static Profile getByValue(String value){
            for(Profile pp:Profile.values()){
                if(pp.value.equals(value)){
                    return pp;
                }
            }
            return null;
        }
        public float getMinSampleRate() {
            return minSampleRate;
        }
        public int getMinSampleSizeInBits() {
            return minSampleSizeInBits;
        }
        public boolean isPcmSignedRecommended() {
            return pcmSignedRecommended;
        }
        public Integer getDefaultMaxFreqSpectrumView() {
            return defaultMaxFreqSpectrumView;
        }
    
}
