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

package ipsk.audio.arr.clip.ui.audiosignal;

import java.awt.Color;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.concurrent.Callable;

import javax.swing.SwingWorker;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.arr.clip.ui.AudioSignalRendererEvent;
import ipsk.audio.arr.clip.ui.AudioSignalRendererListener;
import ipsk.audio.arr.clip.ui.AudioSignalUI;
import ipsk.audio.arr.clip.ui.AudioSignalUI.AmplitudeScaleType;
import ipsk.audio.dsp.DSPUtils;
import ipsk.audio.dsp.FloatRandomAccessStream;


public class AudioSignalModelRenderer implements Runnable,Callable<AudioSignalModelRenderer.RenderResult> {
	
	private final static boolean DEBUG=false;
	private final static long MAX_BUF_SIZE_IN_SAMPLES=32768;
	private final static int DEFAULT_NOTIFY_ON_PIXELS=100;
    private final static int WAIT_FOR_THREAD_ON_CLOSE=1000;
    private final Color DEFAULT_SIGNAL_COLOR = Color.GREEN;
    public static final int DEFAULT_BASELOG_LEVEL = -40;
    
    
    
	public static class Value {
		// int pixelPos;

		double[] min;

		double[] max;

		public Value(int pixelPos, double[] min, double[] max) {
			// this.pixelPos = pixelPos;
			this.min = min;
			this.max = max;
		}

		public Value(int channels) {
			// this.pixelPos = pixelPos;
			min = new double[channels];
			max = new double[channels];
		}
	}

	public static class RenderResult {
	    public Integer channels=null;
		public volatile Value[] values;

		public volatile int pixelOffset;

		public volatile int offset;

		public volatile int length;

		public volatile boolean rendered;

		volatile boolean isValid = true;

		public RenderResult() {
		}
	}

	public class Request {
		int fromPixel;

		int toPixel;

		double framesPerPixel;

		boolean rendered;

		public Request() {
		}

		public Request(int fromPixel, int toPixel, double framesPerPixel) {
			this.fromPixel = fromPixel;
			this.toPixel = toPixel;
			this.framesPerPixel = framesPerPixel;
			rendered = false;
		}

		public void copy(Request r) {
			fromPixel = r.fromPixel;
			toPixel = r.toPixel;
			framesPerPixel = r.framesPerPixel;

		}

		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (!(o instanceof Request))
				return false;
			Request r = (Request) o;
			if (fromPixel == r.fromPixel && toPixel == r.toPixel
					&& framesPerPixel == r.framesPerPixel)
				return true;
			return false;
		}

		public boolean isIn(Request r) {
			if (r == null)
				return false;
			if (framesPerPixel != r.framesPerPixel)
				return false;
			if (fromPixel < r.fromPixel)
				return false;
			if (toPixel > r.toPixel)
				return false;
			return true;
		}

		public String toString() {
			return new String("Render request from: " + fromPixel + ",to: "
					+ toPixel + ",frames per pixel: " + framesPerPixel);
		}
	}

	//private FloatRandomAccessStream fras;
	private AudioSource audioSource;

	private volatile Request request = new Request();

	private double[][] buf = null;
	
	private volatile RenderResult rs = new RenderResult();

	private Thread thread = null;

	private boolean open = true;

	private AudioSignalModelRendererListener listener;
	private volatile Object threadNotify=new Object();
	private int notifyOnPixels=DEFAULT_NOTIFY_ON_PIXELS;
	private Throwable renderException;
	private AudioSignalUI.AmplitudeScaleType amplitudeScaleType=AmplitudeScaleType.LINEAR;
	private double baseLogLevel;
	private double borderLength;
	
	private Color backgroundColor=null;    //default transparent
	private Color signalColor=DEFAULT_SIGNAL_COLOR;
	
	private boolean paintPolygons=true;

	public AudioSignalModelRenderer(AudioSource audioSource,
			AudioSignalModelRendererListener listener)
			throws AudioFormatNotSupportedException, AudioSourceException {
		this.audioSource = audioSource;
		this.listener = listener;
		//fras = new FloatRandomAccessStream(audioSource);
		// fullResult.offset=0;
		// fullResult.values=valArr;
	}


	public RenderResult render(int fromPixel, int toPixel,
			double framesPerPixel, boolean useThread, boolean forceRendering)
			throws AudioSourceException {

		synchronized (threadNotify) {

			Request newRequest = new Request(fromPixel, toPixel, framesPerPixel);
			if ((useThread || request.rendered) && newRequest.equals(request) && ! forceRendering) {
				//System.out.println("Equal: "+newRequest);
				rs.offset = 0;
				rs.length = rs.values.length;
				return rs;
			} else {
				long startFramePos = (long) (framesPerPixel * newRequest.fromPixel);
				int startValuePixelPos = (int) (startFramePos / framesPerPixel);
				long endFramePos = (long) (framesPerPixel * newRequest.toPixel) + 1;
				int endValuePixelPos = (int) (endFramePos / framesPerPixel);
				if ((useThread || request.rendered) && newRequest.isIn(request) && ! forceRendering) {
//                no rendering required
                    //System.out.println("Is in: "+newRequest);

                    // rs.pixelOffset = (int) (startFramePos / framesPerPixel);
                    rs.offset = (int) startValuePixelPos - rs.pixelOffset;
                    rs.length = endValuePixelPos - startValuePixelPos;
                    return rs;
                }else{
					// new rendering required
                    if (DEBUG)System.out.println("New " + newRequest);
					// values.clear();
					rs.isValid = false;
					rs = new RenderResult();
					rs.pixelOffset = startValuePixelPos;
					rs.offset = 0;
					rs.length = endValuePixelPos - startValuePixelPos;
					rs.values = new Value[rs.length];
					//System.out.println("length: "+rs.length);
					rs.rendered = false;

					request.copy(newRequest);
					renderException=null;
					if (useThread) {
						if (thread == null) {
							thread = new Thread(this, "AudioSignalRenderer");
							thread.start();
						}
						threadNotify.notifyAll();
					} else {
						_render(true);
					}
					return rs;
				}
			}
		}

	}

	public Value[] startRender(int fromPixel, int toPixel, double framesPerPixel)
			throws AudioSourceException {
		return null;
	}

	public void run(){
		do {

			try {
				_render(false);
			} catch (AudioSourceException e) {
				e.printStackTrace();
				_close();
				listener.update(new AudioSignalModelRendererEvent(this,e));
			}
			synchronized (threadNotify) {
				while (open && (rs == null || !rs.isValid || rs.rendered)) {
					try {
						threadNotify.wait(20);
					} catch (InterruptedException e) {
						// OK
					}
				}
			}
		} while (open);
	}


	
	private void _render(boolean dry) throws AudioSourceException {
		RenderResult rs=null;
		int startPixel;
		int endPixel;
		double framesPerPixel;
		synchronized(threadNotify){
			startPixel=request.fromPixel;
			endPixel=request.toPixel;
			framesPerPixel=request.framesPerPixel;
			rs=this.rs;
		}
		int lastValuePixelPos = -1;

		int valuePixelPos;
		int renderedPixels=0;
		FloatRandomAccessStream fras;
		try {
			fras = new FloatRandomAccessStream(audioSource);
		} catch (AudioFormatNotSupportedException e) {
			e.printStackTrace();
			throw new AudioSourceException(e);
		}
		for (int pixelPos = startPixel; pixelPos <= endPixel && rs.isValid; pixelPos++) {
			try {
				if(!dry)Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long framePos = (long) (framesPerPixel * pixelPos);
			valuePixelPos = (int) (framePos / framesPerPixel);
			if (valuePixelPos == lastValuePixelPos)
				continue;

			long nextFramePos = (long) (framesPerPixel * (pixelPos + 1));
			long lastFrame = (long) (framesPerPixel * endPixel + 1);
			long frameLength = fras.getFrameLength();
			int channels = fras.getChannels();
			rs.channels=channels;
			if (frameLength != ThreadSafeAudioSystem.NOT_SPECIFIED) {
				if (lastFrame > frameLength - 1)
					lastFrame = frameLength - 1;
			}
			if (framePos > lastFrame)
				break;
			if (nextFramePos > lastFrame)
				nextFramePos = lastFrame;

			Value v = new Value(channels);
			
			long bufSizeLong = (int) (nextFramePos - framePos);
			long samples=bufSizeLong*channels;
			if (samples>MAX_BUF_SIZE_IN_SAMPLES){
				bufSizeLong=MAX_BUF_SIZE_IN_SAMPLES/channels;
				if (DEBUG) System.out.println("Limited bufsize:"+bufSizeLong);
			}
			int bufSize=(int)bufSizeLong;
			if (bufSize == 0)
				bufSize = 1;
			
			if (buf == null || buf.length < bufSize) {
				buf = new double[bufSize][channels];
			}
			fras.setFramePosition(framePos);
			int read = 0;
			int toRead = bufSize;
			while (read < bufSize) {
				int r = fras.readFrames(buf, read, toRead);
				if (r == -1)
					break;
				read += r;
				toRead -= r;
			}
			if (read > 0) {
				v = new Value(channels);

				for (int i = 0; i < channels; i++) {

					v.min[i] = buf[0][i];

					v.max[i] = buf[0][i];
					
				}
				for (int j = 1; j < read; j++) {
					for (int i = 0; i < channels; i++) {
						if (v.min[i] > buf[j][i])
							v.min[i] = buf[j][i];
						if (v.max[i] < buf[j][i])
							v.max[i] = buf[j][i];
						
					}
				}
				// values.add(v);
				int arrPos = valuePixelPos - rs.pixelOffset;
				if(arrPos < rs.values.length){
					// Sometimes arrPos == values.length here
					// I guess due to rounding errors (framesPerPixel)
					// It is not an multithreading sync problem
					// because rs is a local (method stack) copy
					
					// Update rs is only the reference on a field
					// used by different threads
					// I declare this variables volatile 
					
				rs.values[arrPos] = v;
			}
			}
			lastValuePixelPos = valuePixelPos;
			renderedPixels++;
			
			synchronized (threadNotify) {
				if (!dry && rs.isValid && renderedPixels % notifyOnPixels == 0) {
					listener.update(new AudioSignalModelRendererEvent(this, rs));
				}
			}
		}
		// valArr=(Value[]) values.toArray(new Value[0]);
		// fullResult.values=valArr;
		// fullResult.length=valArr.length;
		fras.close();
		synchronized (threadNotify) {
			if (rs.isValid) {
				rs.rendered = true;
				request.rendered = true;
				listener.update(new AudioSignalModelRendererEvent(this, rs));
			}
		}

	}

	private void _close(){
		synchronized (threadNotify) {
			open = false;
			rs.isValid = false;
			threadNotify.notifyAll();
		}
	}
	public void close() {
		_close();
		try {
            if(thread!=null && thread.isAlive()){
            thread.join(WAIT_FOR_THREAD_ON_CLOSE);
            }
        } catch (InterruptedException e) {
         // Hopefully no problem
        }

	}

	public int getNotifyOnPixels() {
		return notifyOnPixels;
	}

	public void setNotifyOnPixels(int notifyOnPixels) {
		this.notifyOnPixels = notifyOnPixels;
	}



	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}



	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}



	/**
	 * @param signalColor the signalColor to set
	 */
	public void setSignalColor(Color signalColor) {
		this.signalColor = signalColor;
	}



	/**
	 * @return the signalColor
	 */
	public Color getSignalColor() {
		return signalColor;
	}

    public RenderResult call()
            throws Exception {
        _render(false);
        return rs;
    }



//    /* (non-Javadoc)
//     * @see javax.swing.SwingWorker#doInBackground()
//     */
//    @Override
//    protected RenderResult doInBackground()
//            throws Exception {
//        // TODO Auto-generated method stub
//        return null;
//    }





	// public Value[] getValues() {
	// //synchronized (values) {
	// return valArr;
	// //}
	// }

}
