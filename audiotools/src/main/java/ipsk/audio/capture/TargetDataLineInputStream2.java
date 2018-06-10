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
 * Created on 30.08.2005
 * 
 */
package ipsk.audio.capture;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;
import javax.swing.SwingUtilities;

public class TargetDataLineInputStream2 extends InputStream {

	private final static boolean DEBUG = false;
	private final static boolean TEST_TRIGGER_RANDOM_FAKE_BUFFER_OVERRUN=false;
	public static final boolean ERROR_ON_BUFFER_OVER_UNDERRUN=true;
	public static double DEFAULT_MAX_BUFFER_FILL=0.9; 
	
	private TargetDataLine line;

	private boolean markBufferOverrun;

	//private AudioFormat format;

	private float byteRate;


	
	private long framePosition;
	
	private volatile boolean flushAndCloseRequest=false;
	private Integer toFlush=null;

//	private long bufSizeMs=0;
	private volatile boolean active=false;
	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	private boolean closed=false;
	
	private long lastValidReadOccurence=Long.MAX_VALUE; // initialized with latest possible timestamp
	
	private long leastSaveReadSyncTime=Long.MAX_VALUE;

//	private boolean readSomething=false;
	private boolean stopped=false;
	// private int reads=0;
	
	private int bufSize;
	private double maxBufferFill=DEFAULT_MAX_BUFFER_FILL;
	
	private volatile TargetDataLineListener listener=null;
	
	

	/**
	 * @return the listener
	 */
	public TargetDataLineListener getListener() {
		return listener;
	}


	/**
	 * @param listener the listener to set
	 */
	public void setListener(TargetDataLineListener listener) {
		this.listener = listener;
	}


	public TargetDataLineInputStream2(TargetDataLine line) {
		super();
		this.line = line;
		resetStream();
		bufSize = line.getBufferSize();
		if (DEBUG){
			System.out.println("Targetdataline debug on");
//			// TODO Test!! provoke Garbage collection 
//			byte[] test=new byte[4000000];
//			// put something into it to prevent compile optimization
//			for(int i=0;i<1000;i++){
//				test[i]=(byte)i;
//			}
		}
		
	}
	
	public void resetStream(){
		lastValidReadOccurence=Long.MAX_VALUE; // initialized with latest possible timestamp
		leastSaveReadSyncTime=Long.MAX_VALUE;
		markBufferOverrun = false;
		framePosition=0;
		closed=false;
		stopped=false;
		active=false;
//		if(line!=null){
//			line.flush();
//			int avail = line.available();
//	        if(DEBUG)System.out.println("Avail after flush: "+avail);
//		}
	}
	
	
	public void stop(){
	    stopped=true;
	}

	public int available() throws IOException {
		return line.available();
	}

	public void close() throws IOException {
		if(!closed){
//			if (line.isActive()) {
//				line.flush();
//				line.stop();
//			}
//			line.close();
			closed=true;
			toFlush=0;
			if (DEBUG)System.out.println("Target dataline audio stream closed.");
//			framePosition=0;
			if (DEBUG) {
			    System.out.println("Buffer size: "+bufSize+" bytes, max buffer fill ratio: "+maxBufferFill);
				//System.out.println("Max/min abberation : " + maxAberration+" / "+minAberration+" ms, bufSize : "+bufSizeMs+" ms");
				System.out.println("Least save async read time: "+leastSaveReadSyncTime+" ms");
			}
		}
	}

	public int read() throws IOException {

		if (line.available() >= line.getBufferSize())
			throw new IOException("Buffer overrun detected !");
		byte[] b = new byte[1];

		int value = read(b, 0, 1);

		if (value == -1) {
			return -1;
		}

		value = (int) b[0];

		if (line.getFormat().getEncoding().equals(
				AudioFormat.Encoding.PCM_SIGNED)) {
			value += 128;
		}

		return value;
	}

	public int read(byte[] b, int off, int len) throws IOException {

//		 // Debug code to _generate_ random buffer overruns
//		 if (!markBufferOverrun && Math.random() > 0.995) {
//		 markBufferOverrun = true;
//		 throw new BufferOverrunException("Debug generated buffer overrun !!");
//		 } else {
//		 markBufferOverrun = false;
//		 }
//		 // End debug code to _generate_ random buffer overruns
		 
		int avail = line.available();
		if(DEBUG)System.out.println("Avail: "+avail);
//		if(avail>0 ){
//		    if(toFlush!=null && toFlush<=0){
//		        // reset flush and close request
//		        flushAndCloseRequest=false;
//		        lastValidReadOccurence=Long.MAX_VALUE;
//		        // stream flushed now close
//		        return -1;
//		    }
//		    if(flushAndCloseRequest && toFlush==null){
//		        // reset 
//		        flushAndCloseRequest=false;
//		        // set size of currently line buffered data 
//		        toFlush=avail;
//		    }
			
			// Bug ID: JAT-00007 Fixed
			// High CPU Load when capturing from some Linux devices (namely Intel HDA on Ubuntu 14.04 (i386)).
			// Speechrecorder bug ID 0045
			// Check if stopped and return the last incompletely filled data buffer.
//			if(stopped && avail<len){
//				len=avail;
//			}
//        }else{
            if(stopped) return -1;
//            if(flushAndCloseRequest){
//                flushAndCloseRequest=false;
//                // stream has no data to flush, close it
//                return -1;
//            }
//        }
		
		 if(flushAndCloseRequest){
             flushAndCloseRequest=false;
             // stream has no data to flush, close it
             return -1;
         }
		
//		int bufSize = line.getBufferSize();
		
		if(TEST_TRIGGER_RANDOM_FAKE_BUFFER_OVERRUN){
		    if(Math.random()<0.005){
		        markBufferOverrun = true;
		        String msg="Buffer overrun test fake error!";
	            System.err.println(msg);
	            throw new BufferOverrunException(msg);
		    }
		}
		
		if (ERROR_ON_BUFFER_OVER_UNDERRUN && !markBufferOverrun && avail >= ((double)bufSize)*maxBufferFill) {
			markBufferOverrun = true;
			System.err.println("Buffer overrun (buffer filled up to limit: filled/buffersize: "+avail+"/"+bufSize+" "+maxBufferFill+" frame position: "+framePosition+")!");
			throw new BufferOverrunException("Buffer overrun detected (buffer filled up to limit)!");
		} else {
			markBufferOverrun = false;
		}
//		if(stopped && avail==0){
//		    return -1;
//		}
		int read = 0;
		try {
			read = line.read(b, off, len);
		} catch (IllegalArgumentException e) {
			throw new IOException(e.getMessage());
		}
		if(stopped && read==0){
			// some drivers return zero even if avail is greater then zero !!
//			System.err.println("Stopped and zero length read!!!");
			return -1;
		}
		
		// the buffer overrun flag gives the application the chance to repeat the
		// read without throwing an IOException
		long readOccurence=System.currentTimeMillis();
		long saveReadAsyncTime=lastValidReadOccurence-readOccurence;
		if(saveReadAsyncTime<leastSaveReadSyncTime){
			leastSaveReadSyncTime=saveReadAsyncTime;
		}
		if(ERROR_ON_BUFFER_OVER_UNDERRUN && saveReadAsyncTime<=0){
			markBufferOverrun=true;
			System.err.println("Buffer overrun (buffer read too late)!");
			throw new BufferOverrunException("Buffer overrun detected (buffer read too late ("+(-saveReadAsyncTime)+"ms))!");
		}
		
		if(read!=-1){
		    if(toFlush!=null){
		        toFlush-=read;
		    }
//		    readSomething=true;
			framePosition+=(read/line.getFormat().getFrameSize());
			int availAfterRead=avail-read;
//			int availAfterRead=line.available();
			
			//int bufFree=((bufSize*90)/100)-(avail-read); // free space of 90 % of Java sound buffer size
			int bufFree=(int)(((double)bufSize*maxBufferFill)-(double)availAfterRead); // free space of 90 % of Java sound buffer size
			AudioFormat af = line.getFormat();
			byteRate = af.getFrameRate() * af.getFrameSize();
			long bufFreeMs=(long) (((float)bufFree*(float)1000)/byteRate); 
			lastValidReadOccurence=readOccurence+bufFreeMs;
			if(!active && read>0){
				active=true;
				if(listener!=null){
					listener.update(new TargetDataLineActiveEvent(this,framePosition));
				}
			}
		}
		
//		Thread.yield();
		
		return read;
	}

	public long getFramePosition() {
		return framePosition;
	}


//	public long getBufSizeMs() {
//		return bufSizeMs;
//	}
//
//	public void setBufSizeMs(long bufSizeMs) {
//		this.bufSizeMs = bufSizeMs;
//	}

    public double getMaxBufferFill() {
        return maxBufferFill;
    }

    public void setMaxBufferFill(double maxBufferFill) {
        this.maxBufferFill = maxBufferFill;
    }


   

    public void flushAndCloseStream() {
       flushAndCloseRequest=true;
        
    }


}
