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

package ipsk.audio.arr.clip.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.dsp.BufferedFloatRandomAccessStream;
import ipsk.audio.dsp.DSPUtils;
import ipsk.audio.dsp.FloatAudioInputStream;
import ipsk.audio.dsp.FloatRandomAccessStream;
import ipsk.audio.dsp.FourierAudioInputStream;
import ipsk.math.Complex;

import javax.sound.sampled.AudioSystem;
import javax.swing.JPanel;
import javax.swing.JSpinner;

public class FourierRenderer implements Runnable {

	private final static boolean DEBUG=false;
	private final static boolean CALC_MAXIMUM=true;
	private final static boolean CONSIDER_PRE_EMPHASIS_CALC_MAXIMUM=true;
	private final static int DEFAULT_NOTIFY_ON_PIXELS = 200;

//	public final static double THRESHOLD_OF_HEARING=2 * 10e-5; // Pascal
//	public final static double VIRTUAL_THRESHOLD_OF_HEARING=1/65536.0; // 1bit of 16bit ampltitude 
	private final static int WAIT_FOR_THREAD_ON_CLOSE=20000;
	
	public class RenderResult {
		public int N;

		// [pixelPos][channel][freqval]
		public double[][][] values;

		public BufferedImage[] renderedImages;

		public int pixelOffset;

		public int offset;

		public int length;

		public int renderedLength;

		public boolean rendered;

		boolean isValid = true;

		public double max = Double.MIN_VALUE;

		public double min = Double.MAX_VALUE;

		public Double totalMaximum=null;
		
		public RenderResult(int pixels, int N, int channels) {
			this.N = N;
			values = new double[pixels][channels][N];
		}
	}

	public class Request {
		int fromPixel;

		int toPixel;

		int height;

		double maxFrequency;

		double framesPerPixel;

		int n;

		int windowSize;
		double dynRangeInDb;
		double emphasisPerOctave;
		double emphasisStartFreq;
		boolean calculationOfMaxRequired;
		boolean rendered;

		public Request() {
		}

		public Request(int fromPixel, int toPixel, int height,double maxFrequency,
				double framesPerPixel, int n, int windowSize,double dynRangeInDb,double emphasisPerOctave,double emphasisStartFreq) {
			this.fromPixel = fromPixel;
			this.toPixel = toPixel;
			this.height = height;
			this.maxFrequency=maxFrequency;
			this.framesPerPixel = framesPerPixel;
			this.n = n;
			this.windowSize = windowSize;
			this.dynRangeInDb=dynRangeInDb;
			this.emphasisPerOctave=emphasisPerOctave;
			this.emphasisStartFreq=emphasisStartFreq;
			calculationOfMaxRequired=true;
			rendered = false;
		}

		public void copy(Request r) {
			fromPixel = r.fromPixel;
			toPixel = r.toPixel;
			height = r.height;
			maxFrequency=r.maxFrequency;
			framesPerPixel = r.framesPerPixel;
			n = r.n;
			windowSize = r.windowSize;
			dynRangeInDb=r.dynRangeInDb;
			emphasisPerOctave=r.emphasisPerOctave;
			emphasisStartFreq=r.emphasisStartFreq;
			calculationOfMaxRequired=r.calculationOfMaxRequired;
		}

		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (!(o instanceof Request))
				return false;
			Request r = (Request) o;
			boolean eqls1= (fromPixel == r.fromPixel && toPixel == r.toPixel && 
					height == r.height && maxFrequency==r.maxFrequency &&
					framesPerPixel == r.framesPerPixel && 
					n == r.n && windowSize == r.windowSize && dynRangeInDb==r.dynRangeInDb);
			    
			    if(eqls1){
//			        if(r.emphasisPerOctave!=null){
			            if(r.emphasisPerOctave==emphasisPerOctave &&
			             r.emphasisStartFreq==emphasisStartFreq){
			                return true;
			            }
//			        }else{
//			            if(emphasisPerOctave==null){
//			                return true;
//			            }
//			        }
			    }
			return false;
		}

		public boolean isIn(Request r) {
			if (r == null)
				return false;
			if (framesPerPixel != r.framesPerPixel || n != r.n
					|| maxFrequency != r.maxFrequency
					|| windowSize != r.windowSize
					|| dynRangeInDb != r.dynRangeInDb)
				return false;
//			if(emphasisPerOctave!=null){
			    if(emphasisPerOctave!=r.emphasisPerOctave || emphasisStartFreq!=r.emphasisStartFreq){
			        return false;
			    }
//			}else{
//			    if(r.emphasisPerOctave!=null){
//			        return false;
//			    }
//			}
			if (fromPixel < r.fromPixel)
				return false;
			if (toPixel > r.toPixel)
				return false;
			if (height != r.height)
				return false;
			return true;
		}
		
		public void checkCalculateMaxRequired(Request r) {
		    boolean cr=false;
            if (r == null 
                    || n != r.n
                    || maxFrequency != r.maxFrequency
                    || windowSize != r.windowSize){
               cr=true;
            }else if(emphasisPerOctave!=r.emphasisPerOctave || emphasisStartFreq!=r.emphasisStartFreq){
                    cr=true;
            }
            calculationOfMaxRequired=cr;
        }

		public String toString() {
			return new String("Render request from: " + fromPixel + ",to: "
					+ toPixel + ",frames per pixel: " + framesPerPixel
					+ ", N: " + n + ", window size: " + windowSize);
		}
	}

	private AudioSource audioSource;
	// private FloatRandomAccessStream fras;
	//private FourierAudioInputStream fouAis;

	private volatile Request request = new Request();

	private Complex[][][] buf = null;

	private volatile RenderResult rs = null;

	private Thread thread = null;
	
	private volatile Object threadNotify=new Object();

	private volatile boolean open = true;

	private FourierRendererListener listener;

	private int notifyOnPixels = DEFAULT_NOTIFY_ON_PIXELS;
	
//	private JPanel controlComponent;

	private Double totalMaximum=null;
	private Double maxPsd=null;
	
//	private double emphasisPerOctave=(float)DSPUtils.toPowerLinearLevel(6);
//	private double emphasisPerOctave=(float)DSPUtils.toLinearLevel(6);
//	private double emphasisStartFrequency=20;
	
	

//	private double emphasisPerOctave=(float)1.0;

	public FourierRenderer(AudioSource audioSource,
			FourierRendererListener listener)
			throws AudioFormatNotSupportedException, AudioSourceException {
		this.audioSource = audioSource;
		this.listener = listener;
		
//		controlComponent=new JPanel();
//		JSpinner dynamicIntervallSpinner=new JSpinner();
//		controlComponent.add(dynamicIntervallSpinner);
		// fullResult.offset=0;
		// fullResult.values=valArr;
	}
	public RenderResult render(int fromPixel, int toPixel, int height,
			double framesPerPixel, int n, int windowSize,double dynRangeInDb, boolean useThread)
			throws AudioSourceException {
		return render(fromPixel,toPixel,height,n/2,framesPerPixel,n,windowSize,dynRangeInDb,useThread);
	}

	public RenderResult render(int fromPixel, int toPixel, int height,double maxFrequency,
			double framesPerPixel, int n, int windowSize,double dynRangeInDb, boolean useThread)
			throws AudioSourceException {
	    return render(fromPixel,toPixel,height,maxFrequency,framesPerPixel,n,windowSize,dynRangeInDb,0.0,0,useThread);
	}
	public RenderResult render(int fromPixel, int toPixel, int height,double maxFrequency,
	            double framesPerPixel, int n, int windowSize, double dynRangeInDb,double emphasisPerOctave,double emphasisStartFreq,boolean useThread)
	            throws AudioSourceException {   
	    
		boolean render = false;
		//System.out.println("New request ...");
		synchronized (threadNotify) {

			Request newRequest = new Request(fromPixel, toPixel, height,maxFrequency,
					framesPerPixel, n, windowSize,dynRangeInDb,emphasisPerOctave,emphasisStartFreq);
			if (useThread && newRequest.equals(request)) {
				// System.out.println("Equal: "+newRequest);
				rs.offset = 0;
				rs.length = rs.values.length;
				return rs;
			} else {
				long startFramePos = (long) (framesPerPixel * newRequest.fromPixel);
				int startValuePixelPos = (int) (startFramePos / framesPerPixel);
				long endFramePos = (long) (framesPerPixel * newRequest.toPixel) + 1;
				int endValuePixelPos = (int) (endFramePos / framesPerPixel);
				if (!useThread || !newRequest.isIn(request)) {
					// new rendering required
					// System.out.println("New " + newRequest);
					// values.clear();
				    
					if (rs != null)
						rs.isValid = false;
					int length = endValuePixelPos - startValuePixelPos;
					int channels=audioSource.getFormat().getChannels();
					//fouAis.setNAndWindowSize(n, windowSize);
					rs = new RenderResult(length, n, channels);
					rs.pixelOffset = startValuePixelPos;
					rs.offset = 0;
					rs.length = length;
					// rs.values = new float[rs.length][channels][n];
					rs.renderedLength = 0;
					rs.rendered = false;
					//int channels = fouAis.getChannels();
					newRequest.checkCalculateMaxRequired(request);
					request.copy(newRequest);
					int reqWidth = request.toPixel - request.fromPixel;
					if (height == 0 || reqWidth <= 0) {
						rs.renderedImages = null;
					} else {
						rs.renderedImages = new BufferedImage[channels];
						for (int ch = 0; ch < channels; ch++) {

							rs.renderedImages[ch] = new BufferedImage(reqWidth,
									height, BufferedImage.TYPE_3BYTE_BGR);

						}
					}
					// System.out.println("Rendering required");
					render = true;
				} else {
					// no rendering required

					rs.offset = (int) startValuePixelPos - rs.pixelOffset;
					rs.length = endValuePixelPos - startValuePixelPos;
				}
			}
			// }
			if (render || !useThread) {
				if (useThread) {
					if (thread == null) {
						thread = new Thread(this, "FourierRenderer");
						thread.setPriority(Thread.MIN_PRIORITY);
						thread.start();
						thread.setPriority(Thread.MIN_PRIORITY);
					}
					// synchronized (request) {
					//request.notifyAll();
//					synchronized(threadNotify){
					threadNotify.notifyAll();
//					}
					// }
				} else {
					// _render(request.fromPixel, request.toPixel,
					// request.framesPerPixel, n, windowSize, rs);
//				    if(thread!=null){
//				        try {
//                            thread.join();
//                        } catch (InterruptedException e) {
//                           // OK
//                        }
//				    }
				    try {
		                _render(request.calculationOfMaxRequired, request.fromPixel, request.toPixel,
		                        request.framesPerPixel, request.n, request.windowSize,request.dynRangeInDb,
		                        request.height,request.maxFrequency,request.emphasisPerOctave,request.emphasisStartFreq);
		            } catch (AudioSourceException e) {
		                // TODO Auto-generated catch block
		                e.printStackTrace();
		            }
				}
			}
		}
		//System.out.println("New request done.");
		return rs;

	}

	public void run() {
		do {

			try {
				_render(request.calculationOfMaxRequired,request.fromPixel, request.toPixel,
						request.framesPerPixel, request.n, request.windowSize,request.dynRangeInDb,
						request.height,request.maxFrequency, request.emphasisPerOctave, request.emphasisStartFreq);
			} catch (AudioSourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (threadNotify) {
				while (open && (rs == null || !rs.isValid || rs.rendered)) {
					try {
//						synchronized(threadNotify){
						threadNotify.wait(50);
//						}
					} catch (InterruptedException e) {
						// OK
					}
				}
			}
		} while (open);
	}
//	private double emphasizeFactor(int in){
//	    double emph=Math.pow(emphasisPerOctave,1.0+DSPUtils.toOctaves(in+1));
//	    return emph;
//	}
	private void _render(boolean calculationOfMaxRequired, int startPixel, int endPixel, double framesPerPixel,
			int n, int windowSize,double dynRangeInDb, int height,double maxFrequency, double emphasisPerOctave, double emphasisStartFreq)
			throws AudioSourceException {
		RenderResult rs=null;
		synchronized(threadNotify){
			rs=this.rs;
		}
		FloatRandomAccessStream fras;
		try {
			fras = new BufferedFloatRandomAccessStream(audioSource);
		} catch (AudioFormatNotSupportedException e) {
			e.printStackTrace();
			throw new AudioSourceException(e);
		}
		FourierAudioInputStream fouAis = new FourierAudioInputStream(fras);
		fouAis.setNAndWindowSize(n, windowSize);
		int channels = fouAis.getChannels();
//		if (height == 0) {
//			rs.renderedImages = null;
//		} else {
//			rs.renderedImages = new BufferedImage[channels];
//			for (int ch = 0; ch < channels; ch++) {
//
//				rs.renderedImages[ch] = new BufferedImage(
//						endPixel - startPixel, height,
//						BufferedImage.TYPE_3BYTE_BGR);
//
//			}
//		}
		long framePos=0;
		if (buf == null || channels > buf[0].length || n > buf[0][0].length) {
		    // DFT result for all channels at one time
			buf = new Complex[1][channels][n];
		}
	
		if (CALC_MAXIMUM && (totalMaximum==null || calculationOfMaxRequired)){
			// calculate maximum value first
			//System.out.println("Calc max");
		    maxPsd=0.0;
			long start=System.currentTimeMillis();
			int step=(int)framesPerPixel;
//			if(step==0)step=1;
			if(step<windowSize)step=windowSize;
			if(framesPerPixel<1)framesPerPixel=1;
			double totalMax=Double.MIN_VALUE;
			fouAis.setFramePosition(framePos);
			long frameLength=fouAis.getFrameLength();
			while(fouAis.readFrame(buf, 0) && open){
				for(int i=0;i<channels;i++){
					for(int ni=0;ni<n/2;ni++){
						double mag=buf[0][i][ni].magnitude();
						double psd=(2 * Math.pow(mag, 2))/windowSize;
						
						if(emphasisPerOctave!=0.0 && ni>=emphasisStartFreq){
							
							double emphFactorInDB=DSPUtils.toOctaves(((double)ni)/emphasisStartFreq)*emphasisPerOctave;
							double emphFactor=DSPUtils.toLinearLevel(emphFactorInDB);
							psd*=emphFactor;
						}
						if(maxPsd<psd){
						    maxPsd=psd;
						}
					if (totalMax<mag){
						totalMax=mag;
					}
					}
				}
				//framePos+=n+2;
				framePos+=step;
				if(framePos>=frameLength){
					break;
				}
				//if(framePos % 100 ==0){
					Thread.yield();
				//}
				fouAis.setFramePosition(framePos);
				
			}
			
			totalMaximum=totalMax;
			rs.totalMaximum=totalMax;
//			maxPsd=(2 * Math.pow(totalMaximum, 2))/windowSize;
			 //double maxPsd= (2 * Math.pow(totalMaximum, 2))/windowSize;
			 //maxPsdLog = Math.log10(maxPsd / 1.0);
			 //totalMaximum=(float)maxPsd;
			if(DEBUG)System.out.println("Max: "+totalMaximum+" calc time "+(System.currentTimeMillis()-start)+" ms");
			
		}
		int lastValuePixelPos = -1;
		int minRgbVal=255;
		int valuePixelPos;
		int renderedPixels = 0;
		
		
		int xOffset=0;
		for (int pixelPos = startPixel; pixelPos <= endPixel && rs.isValid; pixelPos++) {
			framePos = (long) (framesPerPixel * (double)pixelPos);
			valuePixelPos = (int) ((double)framePos / framesPerPixel);
			if (valuePixelPos == lastValuePixelPos){
				//continue;
			}

			long nextFramePos = (long) (framesPerPixel * (double)(pixelPos + 1));
			//long lastFrame = (long) (request.framesPerPixel * (double)(request.toPixel + 1));
			long lastFrame = (long) (framesPerPixel * (endPixel+1));
			long frameLength = fouAis.getFrameLength();

			if (frameLength != AudioSystem.NOT_SPECIFIED) {
				if (lastFrame > frameLength - 1)
					lastFrame = frameLength - 1;
			}
			if (framePos > lastFrame){
				break;
			}
			
			//if(nextFramePos> framePos){
			if (nextFramePos > lastFrame){
				nextFramePos = lastFrame;
			}

			// Value v = new Value(channels);

			
			fouAis.setFramePosition(framePos);

			// read complex spectrum in Pa/Hz -> Spectrum
			boolean read = fouAis.readFrame(buf, 0);
			if (!read){
				break;
			}
//			}else{
//				System.out.println("Equal frame pos.");
//			}
//			int arrPos = valuePixelPos - rs.pixelOffset;

//			int bandsDivider = 1;
//			int bandsShown = n / 2 / bandsDivider;
//			highestBandindex=n/2;
//			double freqStepHeight = (double)height / (double)maxFrequency;
//			int paintHeight=((int)freqStepHeight)+1;
			
			Graphics[] gs=new Graphics[channels];
			for (int ch = 0; ch < channels; ch++) {
				if (rs.renderedImages != null) {
					BufferedImage bi = rs.renderedImages[ch];
					gs[ch] = bi.getGraphics();
				}
			}
			for (int y = 0; y < height; y++) {
				for (int ch = 0; ch < channels; ch++) {

					double bandIndex = ((double)maxFrequency * ((double)height - (double)y)) / (double)height;
					int lowIndex=(int) bandIndex;
					int hiIndex=lowIndex+1;
					
//					double val0Emph=emphasizeFactor(lowIndex);
//					double val1Emph=emphasizeFactor(hiIndex);
					
					double emphFactor=0.0;
					if(emphasisPerOctave!=0.0){
//						emphFactor=emphasizeFactor(bandIndex,emphasisStartFreq,emphasisPerOctave);
					    emphFactor=DSPUtils.toOctaves(bandIndex/emphasisStartFreq)*emphasisPerOctave;
					}
					
					double val0 = buf[0][ch][lowIndex].magnitude();
					double val1=buf[0][ch][hiIndex].magnitude();
//					double val=((val1-val0)*(bandIndex-(double)lowIndex)+val0)*emphFactor;
					double val=((val1-val0)*(bandIndex-(double)lowIndex)+val0);
					if (rs.renderedImages != null) {
//						BufferedImage bi = rs.renderedImages[ch];
						Graphics g = gs[ch];
						
						// calculate the one sided power spectral density PSD (f, t) in Pa2/Hz
						// PSD(f) proportional to 2|X(f)|2 / (t2 - t1)
						
						double psd = (2 * Math.pow(val, 2))/windowSize;
//						double empPsd=psd*emphFactor;
						    
						// Calculate logarithmic 
						//double psdLog = Math.log10(psd / 1.0);
//						double psdLog = 10*Math.log10(psd / maxPsd);
						double psdLog=DSPUtils.toLevelInDB(psd/maxPsd);
						if(bandIndex>=emphasisStartFreq){
							psdLog+=emphFactor;
						}
//						System.out.println("PSD log: "+psdLog);
//						double levelLog = 10*Math.log10(val / totalMaximum);
						
						if (rs.max < psd)
							rs.max =  psd;
						if (rs.min > psd)
							rs.min =  psd;			
						
						//double scaledVal=(double)((psdLog/4.5)+1.3);
						
						
						
						double scaledVal=(double)((psdLog+dynRangeInDb)/dynRangeInDb);
						
						//double scaledVal=(double)((levelLog+dynRangeInDB)/dynRangeInDB);
//						double minPsd=maxPsd
						if (scaledVal > 1)
							scaledVal = 1;
						if(scaledVal<0){
							scaledVal=0;
						}
						int rgbVal = (int) (255 * scaledVal);	
						if (rgbVal < 0) {
//							System.out.println("Neg RGB val: "+rgbVal);
							rgbVal = 0;		
						}
						if (rgbVal > 255) {
							rgbVal = 255;
						}
						rgbVal = 255 - rgbVal;
						if(rgbVal<minRgbVal){
						    minRgbVal=rgbVal;
						}
						Color c = new Color(rgbVal, rgbVal, rgbVal);
						g.setColor(c);
						g.fillRect(pixelPos - startPixel + xOffset, y, 1, 1);
//						g.dispose();
					}else{
//						System.out.println("Images null!");
					}
				}
			}
			for(Graphics g:gs){
				if(g!=null){
					g.dispose();
				}
			}
			
//			int x0=pixelPos - startPixel + xOffset;
//			//System.out.println("x: "+x0);
//				for (int ch = 0; ch < channels; ch++) {
//					for(int bandIndex=0;bandIndex<bandsShown;bandIndex++){
//						float yf=height-bandIndex*freqStepHeight;
//
//					//float bandIndex = (bandsShown * (height - y)) / height;
//					float val = buf[0][ch][bandIndex].magnitude();
//					if (rs.renderedImages != null) {
//						BufferedImage bi = rs.renderedImages[ch];
//						Graphics g = bi.getGraphics();
//						
//						// calculate the one sided power spectral density PSD (f, t) in Pa2/Hz
//						// PSD(f) proportinal to 2|X(f)|2 / (t2 - t1)
//						
//						double psd = (2 * Math.pow(val, 2))/windowSize;
//
////						// calculate logarithmic values with threshold of hearing as reference 
////						double psdLog = Math.log10(psd
////								/ (Math.pow(VIRTUAL_THRESHOLD_OF_HEARING, 2)));
////						But where is the threshold of hearing of an digital signal ?
//						
//						double psdLog = Math.log10(psd / 1.0);
//						
//						if (rs.max < psdLog)
//							rs.max = (float) psdLog;
//						if (rs.min > psdLog)
//							rs.min = (float) psdLog;			
//						float scaledVal=(float)((psdLog)+5)/4;
//						if (scaledVal > 1)
//							scaledVal = 1;
//						int rgbVal = (int) (255 * scaledVal);	
//						if (rgbVal < 0) {
//							rgbVal = 0;		
//						}
//						if (rgbVal > 255) {
//							rgbVal = 255;
//						}
//						rgbVal = 255 - rgbVal;
//						Color c = new Color(rgbVal, rgbVal, rgbVal);
//						g.setColor(c);
//						
//						int y0=(int)yf;
//						//g.drawLine(x0,y0,x0, y0+paintHeight);	
//						g.fillRect(x0,y0,1, paintHeight);	
//						//System.out.println("Line: "+x0+" "+y0+" "+(y0+paintHeight));
//					}else{
//						
//					}
//				}
//			}
			Thread.yield();
			lastValuePixelPos = valuePixelPos;
			renderedPixels=pixelPos-startPixel;
			//renderedPixels++;
			//rs.renderedLength++;
			synchronized(threadNotify){
			rs.renderedLength=renderedPixels;
			if (rs.isValid && renderedPixels % notifyOnPixels == 0) {
				//System.out.println("Partial rendered event ...");
				synchronized (request) {
					listener.update(new FourierRendererEvent(this, rs));
				}
				//System.out.println("Partial rendered event sent.");
				//System.out.println("Max: "+rs.max+" Min: "+rs.min);
			}
			}
		}
		fouAis.close();
//		System.out.println("Darkest"+minRgbVal);
		//System.out.println("Complete rendered event ...");
		synchronized (threadNotify) {
			if (rs.isValid){
			rs.rendered = true;
			request.rendered = true;
			listener.update(new FourierRendererEvent(this, rs));
			
			}else{
				//System.out.println("Not valid");
			}
		}
		//System.out.println("Complete rendered event sent.");

	}

//	/**
//	 * @param lowIndex
//	 * @return
//	 */
//	private double emphasizeFactor(double index,double emphasisStartFrequency,double emphasisPerOctave) {
//		double factor=1.0;
//		if(index>=emphasisStartFrequency){
//			// if frequency above start frequency
//			// octaves >=1.0
//			
//			double octaves=DSPUtils.toOctaves((double)index/(double)emphasisStartFrequency);
////			double octaves=Math.log(index/emphasisStartFrequency)/Math.log(2);
////			double octaves=Math.exp(index/emphasisStartFrequency);
////			factor=(1.0+(octaves*2));
////			factor=octaves*emphasisPerOctave;
//			factor=Math.pow(emphasisPerOctave, octaves);
//		}
//		if(factor<1.0){
//			System.out.println("Factor:"+ factor+" "+index);
//			factor=1.0;
//			
//		}
//		return factor;
//	}
	
	public void close() {
		synchronized (threadNotify) {
			open = false;
			if(rs!=null)rs.isValid = false;
//			synchronized(threadNotify){
			threadNotify.notifyAll();
//			}
		}
		try {
            if(thread!=null && thread.isAlive()){
            thread.join(WAIT_FOR_THREAD_ON_CLOSE);
            }
        } catch (InterruptedException e) {
         // OK
        }

	}

	public int getNotifyOnPixels() {
		return notifyOnPixels;
	}

	public void setNotifyOnPixels(int notifyOnPixels) {
		this.notifyOnPixels = notifyOnPixels;
	}

//	public JPanel getControlComponent() {
//		return controlComponent;
//	}





}
