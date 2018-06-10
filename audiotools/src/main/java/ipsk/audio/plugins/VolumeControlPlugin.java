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
 * Date  : Jan 21, 2009
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.plugins;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioPluginException;
import ipsk.audio.dsp.FloatAudioInputStream;
import ipsk.audio.dsp.FloatToPCMAudioInputStream;
import ipsk.audio.dsp.VolumeControlStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class VolumeControlPlugin extends BasicPCMPlugin {
    
    private static final double LN = (float) (20 / Math.log(10));
    
    public class VolumeControl extends FloatControl{
        
        public VolumeControl(){
            super(FloatControl.Type.VOLUME,(float)0.0,(float)1.0,(float)0.1,1,(float)1.0,"");
        }
        public void setValue(float newValue){
            setVolume(newValue);
        }
        
    }
    
    private VolumeControlStream vcis=null;
    private double gainRatio=1.0;
    private VolumeControl volumeControl;
    
    public VolumeControlPlugin(){
        // only signed PCM formats
    supportedAudioFormats = new AudioFormat[] {
            new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    (float) AudioSystem.NOT_SPECIFIED,
                    AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED,
                    AudioSystem.NOT_SPECIFIED,
                    (float) AudioSystem.NOT_SPECIFIED, true),
            new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    (float) AudioSystem.NOT_SPECIFIED,
                    AudioSystem.NOT_SPECIFIED, AudioSystem.NOT_SPECIFIED,
                    AudioSystem.NOT_SPECIFIED,
                    (float) AudioSystem.NOT_SPECIFIED, false)};
    }
    
    public void setVolume(float newValue) {
       gainRatio=newValue;
       if(vcis!=null){
           vcis.setVolume(newValue);
       }
    }
    
    public void setGainRatio(double newValue) {
        gainRatio=newValue;
       if(vcis!=null){
           vcis.setGainRatio(gainRatio);
       }
    }
    
    
    public void setVolumeInDezibel(float volumeInDezibel){
//        float gainRatio=(float)Math.exp(volumeInDezibel/LN);
        double gr=Math.pow(10.0,(volumeInDezibel/20.0));
        setGainRatio(gr);
    }

    @Override
    public AudioInputStream getAudioInputStream(AudioInputStream source)
            throws AudioPluginException {
        AudioFormat oFormat=getOutputFormat();
        if(oFormat==null){
            try {
                setOutputFormat(source.getFormat());
            } catch (AudioFormatNotSupportedException e) {
                e.printStackTrace();
                throw new AudioPluginException(e);
            }
        }
        
        try {
            vcis=new VolumeControlStream(source,true); 
            vcis.setGainRatio(gainRatio);
        } catch (AudioFormatNotSupportedException e) {
           throw new AudioPluginException(e);
        }
        
        FloatToPCMAudioInputStream fToPCM;
        try {
            fToPCM=new FloatToPCMAudioInputStream(vcis,getOutputFormat());
        } catch (AudioFormatNotSupportedException e) {
            e.printStackTrace();
            throw new AudioPluginException(e);
        }
        return fToPCM;
    }
    
    
    

}
