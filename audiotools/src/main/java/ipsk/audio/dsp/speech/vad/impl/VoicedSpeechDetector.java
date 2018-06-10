//    IPS Java Audio Tools
// 	  (c) Copyright 2015
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

package ipsk.audio.dsp.speech.vad.impl;

import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.swing.SwingUtilities;

import ips.dsp.AutoCorrelator;
import ips.dsp.SampledTime;
import ipsk.audio.dsp.DSPUtils;
import ipsk.audio.dsp.speech.vad.VoiceActivityDetector;
import ipsk.audio.dsp.speech.vad.VoiceActivityDetectorEvent;
import ipsk.audio.dsp.speech.vad.VoiceActivityDetectorListener;

//
// TODO nach Wechsel des Sprechers mit capture scope session:
//Exception in thread "Audio-Capture" java.lang.NullPointerException
//at ipsk.audio.dsp.speech.vad.impl.VoicedSpeechDetector.write(VoicedSpeechDetector.java:159)
//at ipsk.audio.dsp.AudioOutputStreamFloatConverter.write(AudioOutputStreamFloatConverter.java:89)
//at ipsk.io.InterceptorInputStream.writeToOutputStreams(InterceptorInputStream.java:97)
//at ipsk.io.InterceptorInputStream.read(InterceptorInputStream.java:67)
//at javax.sound.sampled.AudioInputStream.read(AudioInputStream.java:292)
//at javax.sound.sampled.AudioInputStream.read(AudioInputStream.java:232)
//at ipsk.audio.io.InterceptorAudioInputStream.read(InterceptorAudioInputStream.java:99)
//at ipsk.audio.capture.Capture3.run(Capture3.java:843)
//at java.lang.Thread.run(Thread.java:745)


/**
 * Implementation of a voiced speech detector.
 *  
 * The implementation uses autocorrelation and dynamic signal to noise ratio measuring.
 * @author klausj
 *
 */
public class VoicedSpeechDetector implements VoiceActivityDetector {

    // TODO use frequencies instead
//    private double startCorrTime=0.002; // 2ms -
//    private double endCorrTime=0.020; // 20ms
    
    private double startCorrTime=0.004; // 8ms -
    private double endCorrTime=0.015; // 10ms

    private double corrLenTime=0.1;
    
    // if relation of autocorrelation to energy of the signal buffer is above this value the buffer is classified as voiced   
    private double VOICED_THRESHOLD=0.5;
    
    // signal buffers which are below this power level. Reference level is the buffer with the highest energy level already processed in this stream.   
    private double MIN_RELATIVE_ENERGY_LEVEL=-80.0;
    
    // currently not used
    private double LOWEST_ENERGY_MIN=DSPUtils.toPowerLinearLevel(-120);

    public static final boolean DEBUG=false;
    
	private AudioFormat audioFormat;
	private float sampleRate;
	private int channels;
	
	// buffer for channel 0
	private double[] buf;
	private volatile int avail=0;
	private volatile long framePosition=0;
	
	private int processLen;
	private boolean voiced=false;
	private VoiceActivityDetectorListener listener;
    private long corrStartFrames;
    private long corrEndFrames;
    
    private double lowestEnergy=Double.MAX_VALUE;
    private double highestEnergy=LOWEST_ENERGY_MIN;
    
    private volatile boolean running=true;
    

	/**
	 * 
	 */
	public VoicedSpeechDetector() {
		super();
		
	}
	
	
	private void init(){
	    long corrLenFrames=(long) (corrLenTime*sampleRate);
        corrStartFrames = (long)(startCorrTime*sampleRate);
        corrEndFrames = (long)(endCorrTime*sampleRate);
        long corrInterval=corrEndFrames-corrStartFrames;
        
        processLen=(int)(corrLenFrames+corrEndFrames);
        buf=new double[processLen*2];
        if(DEBUG){
            System.out.println("Init");
        }
	}
	
	private void reset(){
	    avail=0;
	    voiced=false;
	}
	


	/* (non-Javadoc)
	 * @see ipsk.audio.io.push.FloatAudioOutputStream#setAudioFormat(javax.sound.sampled.AudioFormat)
	 */
	@Override
	public void setAudioFormat(AudioFormat audioFormat) {
		this.audioFormat=audioFormat;
		this.sampleRate=audioFormat.getSampleRate();
		init();
	}

	/* (non-Javadoc)
	 * @see ipsk.io.InterleavedFloatOutputStream#setChannels(int)
	 */
	@Override
	public void setChannels(int channels) {
		this.channels=channels;
		
	}

	/* (non-Javadoc)
	 * @see ipsk.io.InterleavedFloatOutputStream#write(double[][], int, int)
	 */
	@Override
	public void write(double[][] buf, int offset, int len) throws IOException {
	    // Is NOT called on EDT thread!!!
//	    if(!SwingUtilities.isEventDispatchThread()){
//	        System.out.println("Not EDT !!");
//	    }
	    if(running){
	    // buffer large enough ?
	    int reqBufLen=avail+len;
	    if(this.buf.length<reqBufLen){
	        // increase
	        this.buf=Arrays.copyOf(this.buf, reqBufLen);
	    }
	    // copy
	    for(int i=0;i<len;i++){
	        this.buf[avail+i]=buf[offset+i][0];
	    }
	    avail+=len;
	    if(DEBUG){
            System.out.println("Avail: "+avail);
        }
	    while(avail>=processLen){
	        framePosition+=processLen;
	        // process
	       
	        AutoCorrelator.AutoCorrelationResult res=AutoCorrelator.autoCorrelate(this.buf, 0, processLen, (int)corrStartFrames, (int)corrEndFrames);
	        double bufE=res.getEnergy();
	        if(bufE>highestEnergy){
	            highestEnergy=bufE;
	        }
//	        if(bufE> LOWEST_ENERGY_MIN && bufE<lowestEnergy){
	        if(bufE<lowestEnergy){
	            lowestEnergy=bufE;
	        }
	        
	        double currE=DSPUtils.toPowerLevelInDB(bufE/highestEnergy);
	        
	        
	        if(DEBUG){
                System.out.println("Processed "+processLen+" frames: "+res.correlation()+ " "+corrStartFrames+" "+corrEndFrames);
            }
//	        if(currE> -80){
//	            System.out.println("SNR: "+DSPUtils.toPowerLevelInDB(highestEnergy/lowestEnergy)+" curr: "+currE);
	            boolean currVoiced=(currE>MIN_RELATIVE_ENERGY_LEVEL && res.correlation()>VOICED_THRESHOLD);
	            if(voiced!=currVoiced){
	                voiced=currVoiced;
	                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                           listener.update(new VoiceActivityDetectorEvent(this,voiced,new SampledTime(sampleRate,framePosition)));
                        }
                    });
	               
	            }
//	        }
	        // shift rest of data beginning  of process buffer 
	        // TODO better use ring buffer ?
	        avail-=processLen;
//	        double[] newBuf=new double[buf.length];
//	        System.out.println("Shift "+avail+" samples");
	        for(int j=0;j<avail;j++){
	            this.buf[j]=this.buf[processLen+j];
	        }
	    }
	    }
	}

	/* (non-Javadoc)
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
//		stop();
		reset();
	}

	/* (non-Javadoc)
	 * @see java.io.Flushable#flush()
	 */
	@Override
	public void flush() throws IOException {
	    // procesing in sync: nothing to do
	}

	/* (non-Javadoc)
	 * @see ipsk.audio.dsp.speech.vad.VoiceActivityDetector#setVoiceActivityDetectorListener(ipsk.audio.dsp.speech.vad.VoiceActivityDetectorListener)
	 */
	@Override
	public void setVoiceActivityDetectorListener(
			VoiceActivityDetectorListener voiceActivityDetectorListener) {
		this.listener=voiceActivityDetectorListener;
	}

//
//    /* (non-Javadoc)
//     * @see ipsk.audio.dsp.speech.vad.VoiceActivityDetector#start()
//     */
//    @Override
//    public void start() {
//        reset();
//        running=true;
//    }
//
//
//    /* (non-Javadoc)
//     * @see ipsk.audio.dsp.speech.vad.VoiceActivityDetector#stop()
//     */
//    @Override
//    public void stop() {
//        running=false;
//        
//    }

}
