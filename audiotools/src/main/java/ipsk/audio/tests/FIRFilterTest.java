//    IPS Java Audio Tools
// 	  (c) Copyright 2011
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

package ipsk.audio.tests;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import ips.dsp.FIRFilter;
import ips.dsp.FIRFilterBuilder;
import ips.dsp.FIRFilterSimple;
import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioPluginException;
import ipsk.audio.dsp.FloatAudioInputStream;
import ipsk.audio.dsp.FloatToPCMAudioInputStream;
import ipsk.audio.dsp.FloatToPCMInputStream;
import ipsk.audio.synth.WhiteNoiseGenerator;
import ipsk.audio.synth.WhiteNoiseGeneratorSource;
import ipsk.io.FloatStreamAdapter;
import ipsk.io.InterleavedFloatStreamMonoAdapter;
import ipsk.math.Complex;
import ipsk.math.HammingWindow;
import ipsk.math.random.GaussianDistributionRandomGenerator;

/**
 * @author klausj
 *
 */
public class FIRFilterTest {

    private GaussianDistributionRandomGenerator whiteNoise;
//    private double[] TEST_COEFF=new double[]{ -0.0000 ,   0.0052 ,  -0.0000  , -0.0232 ,  -0.0000 ,   0.0761 ,  -0.0000  , -0.3077 ,   0.5009,
//             -0.3077  , -0.0000   , 0.0761 ,  -0.0000 ,  -0.0232  , -0.0000  ,  0.0052  , -0.0000};

    private double[] TEST_COEFF=new double[]{ -0.0000  , 0.0004  , 0.0000 , -0.0005 , -0.0000  , 0.0006 , -0.0000 , -0.0007  , 0.0000
    , 0.0008  , 0.0000 , -0.0010 , -0.0000  , 0.0012 , -0.0000 , -0.0015 , -0.0000  , 0.0019

 

    , 0.0000 , -0.0022 , -0.0000  , 0.0027 , -0.0000 , -0.0032 , -0.0000  , 0.0038  , 0.0000

    

   , -0.0045 , -0.0000  , 0.0053 , -0.0000 , -0.0062 , -0.0000  , 0.0072  , 0.0000 , -0.0084

  

   , -0.0000  , 0.0098 , -0.0000 , -0.0114 , -0.0000  , 0.0132 , -0.0000 , -0.0155 , -0.0000

  

    , 0.0184 , -0.0000 , -0.0220 , -0.0000  , 0.0268 , -0.0000 , -0.0336 , -0.0000  , 0.0441

 

   , -0.0000 , -0.0627 , -0.0000  , 0.1055 , -0.0000 , -0.3182  , 0.5002 , -0.3182 , -0.0000

 

    , 0.1055 , -0.0000 , -0.0627 , -0.0000  , 0.0441 , -0.0000 , -0.0336 , -0.0000  , 0.0268

  

   , -0.0000 , -0.0220 , -0.0000  , 0.0184 , -0.0000 , -0.0155 , -0.0000  , 0.0132 , -0.0000

  

   , -0.0114 , -0.0000  , 0.0098 , -0.0000 , -0.0084  , 0.0000  , 0.0072 , -0.0000 , -0.0062

  

   , -0.0000  , 0.0053 , -0.0000 , -0.0045  , 0.0000  , 0.0038 , -0.0000 , -0.0032 , -0.0000

   

    , 0.0027 , -0.0000 , -0.0022  , 0.0000  , 0.0019 , -0.0000 , -0.0015 , -0.0000  , 0.0012

  

   , -0.0000 , -0.0010  , 0.0000  , 0.0008  , 0.0000 , -0.0007 , -0.0000  , 0.0006 , -0.0000


   , -0.0005  , 0.0000  , 0.0004 , -0.0000};
    
    public FIRFilterTest(){
        AudioFormat audioFormat=new AudioFormat(44100, 16, 1, true, false);
        long frameLength=1000000;
        whiteNoise=new GaussianDistributionRandomGenerator(0.0,0.1,frameLength);
        Complex[] impulseResponse=new Complex[300];
        //Ein BandPass
        //
        //      _____
        // _____     _____
        int i=0;
        for(;i<100;i++){
            impulseResponse[i]=new Complex(0,0);
   
        }
        for(;i<200;i++){
            impulseResponse[i]=new Complex(1,0);
        }
        for(;i<300;i++){
            impulseResponse[i]=new Complex(0,0);
        }
      
//        HammingWindow hw=new HammingWindow(impulseResponse.length);
        double[] coeff=FIRFilterBuilder.buildCoefficientsFromImpulsResponse(impulseResponse);
//        double[] coeff=TEST_COEFF;
//        for(int j=0;j<coeff.length;j++){
//            coeff[j]=coeff[j]*2;
//        }
        for(double d:coeff){
                   System.out.println(d);
        }
        
//        AudioInputStream srcAis=null;
//        try {
//            srcAis = AudioSystem.getAudioInputStream(new File("wn.wav"));
//        } catch (UnsupportedAudioFileException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        FloatAudioInputStream fais=null;
//        try {
//            fais = new FloatAudioInputStream(srcAis);
//        } catch (AudioFormatNotSupportedException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        FloatStreamAdapter fsa=new InterleavedFloatStreamAdapter(whiteNoise);
        FIRFilter fir=new FIRFilter(whiteNoise, coeff);
        InterleavedFloatStreamMonoAdapter ia=new InterleavedFloatStreamMonoAdapter(fir);
        FloatToPCMInputStream fToPCM;
            try {
                fToPCM=new FloatToPCMInputStream(ia,audioFormat);
                AudioInputStream ais=new AudioInputStream(fToPCM, audioFormat,frameLength);
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File("test.wav"));
            } catch (AudioFormatNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
       
        
    }
    
    public static void main(String[] args){
        new FIRFilterTest();
    }
    
    
}
