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
 * Date  : 01.03.2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.io;

import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.io.event.AudioFileWriterCancelledEvent;
import ipsk.audio.io.event.AudioFileWriterErrorEvent;
import ipsk.audio.io.event.AudioFileWriterEvent;
import ipsk.audio.io.event.AudioFileWriterWrittenEvent;
import ipsk.awt.AWTEventTransferAgent;
import ipsk.awt.ProgressListener;
import ipsk.awt.ProgressWorker;
import ipsk.awt.WorkerException;
import ipsk.awt.event.ProgressErrorEvent;
import ipsk.awt.event.ProgressEvent;
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;

import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Vector;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


/**
 * Audio file writer worker.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
//TODO localize messages
public class AudioFileWriter extends ProgressWorker implements ProgressListener{

	public class EventTransformer extends AWTEventTransferAgent{
		@Override
		public void fireEvent(EventListener l,EventObject ev){
			AudioFileWriterListener afwl=(AudioFileWriterListener)l;
			afwl.update((AudioFileWriterEvent)ev);
		}
	}

	private static final String THREAD_NAME = "Audiofile-Writer";
	private AudioInputStream ais;

	private AudioFileFormat.Type aff;

	private File outFile;
	private EventTransformer evTrans=new EventTransformer();
	
	private File tmpFile = null;

	//private AudioFileWriterListener listener;

	//private Component parentComponent;

	private boolean useTempFile=false;
	private ProgressMonitorAudioInputStream pmais=null;
	private boolean cancelled=false;
	//private JProgressDialogPanel progressDialog;
	private Vector<EventListener> listeners=new Vector<EventListener>();
	/**
	 * Create new file writer.
	 * 
	 * @param listener
	 *            notified on events
	 * @param ais
	 *            the audio stream to read from
	 * @param aff
	 *            the audio file format
	 * @param outFile
	 *            the audio file to write to
	 */
	public AudioFileWriter(AudioFileWriterListener listener,
			AudioInputStream ais, AudioFileFormat.Type aff, File outFile) {
		super(THREAD_NAME);
		evTrans.addListener(listener);
		//this.listener = listener;
		this.ais = ais;
		this.aff = aff;
		this.outFile = outFile;

	}

	/**
	 * Create new file writer with progress monitor popup.
	 * 
	 * @param listener
	 *            notified on events
	 * @param ais
	 *            the audio stream to read from
	 * @param aff
	 *            the audio file format
	 * @param outFile
	 *            the audio file to write to
	 * @param parentComponent
	 *            the parent GUI component for the monitoring popup
	 */
	public AudioFileWriter(AudioFileWriterListener listener,
			AudioInputStream ais, AudioFileFormat.Type aff, File outFile,
			Component parentComponent) {
		this(listener, ais, aff, outFile, false);
	}

//	/**
//	 * Create new file writer with progress monitor popup and use of temporary
//	 * file.
//	 * 
//	 * @param listener
//	 *            notified on events
//	 * @param ais
//	 *            the audio stream to read from
//	 * @param aff
//	 *            the audio file format
//	 * @param outFile
//	 *            the audio file to write to
//	 * @param parentComponent
//	 *            the parent GUI component for the monitoring popup
//	 */
//	public AudioFileWriter(AudioFileWriterListener listener,
//			AudioInputStream ais, AudioFileFormat.Type aff, File outFile,
//			Component parentComponent, boolean useTempFile) {
//		super();
//		this.listener = listener;
//		this.useTempFile = useTempFile;
//		String aisWriterMsg = null;
//
//		this.parentComponent = parentComponent;
//		this.aff = aff;
//		this.outFile = outFile;
//		if (useTempFile) {
//			aisWriterMsg = "Creating temporary audio file ...";
//		} else {
//			aisWriterMsg = "Saving to audio file " + outFile.getName() + " ...";
//		}
//
//		ProgressMeasuringAudioInputStream pmais = new ProgressMeasuringAudioInputStream(
//				ais, parentComponent, aisWriterMsg, "");
//		//pmais.get
//		this.ais = pmais;
//	}
	
	/**
	 * Create new file writer.
	 * 
	 * @param listener
	 *            notified on events
	 * @param ais
	 *            the audio stream to read from
	 * @param aff
	 *            the audio file format
	 * @param outFile
	 *            the audio file to write to
	 * @param useTempFile determines usage of temporary recording file
	 */
	public AudioFileWriter(AudioFileWriterListener listener,
			AudioInputStream ais, AudioFileFormat.Type aff, File outFile, boolean useTempFile) {
		super(THREAD_NAME);
		//this.listener = listener;
		evTrans.addListener(listener);
		this.useTempFile = useTempFile;
		String aisWriterMsg = null;

		//this.parentComponent = parentComponent;
		this.aff = aff;
		this.outFile = outFile;
		if (useTempFile) {
			aisWriterMsg = "Creating temporary audio file ...";
		} else {
			aisWriterMsg = "Saving to audio file " + outFile.getName() + " ...";
		}

		pmais=new ProgressMonitorAudioInputStream(ais);
		this.ais = pmais;
	}

	public void create() throws AudioFileWriterException {
		ByteArrayInputStream zeroBis = new ByteArrayInputStream(new byte[0]);
		AudioInputStream zeroAis = new AudioInputStream(zeroBis, ais
				.getFormat(), 0L);
		try {
			ThreadSafeAudioSystem.write(zeroAis, aff, outFile);
		} catch (IOException ioe) {
			throw new AudioFileWriterException(ioe);
		} finally {
			try {
				zeroBis.close();
			} catch (IOException e) {
				throw new AudioFileWriterException(e);
			} finally {
				try {
					zeroAis.close();
				} catch (IOException e1) {
					throw new AudioFileWriterException(e1);
				}
			}
		}

	}
	
	public void write() throws WorkerException{
		open();
		start();
		close();
		reset();
	}

	public void doWork() throws WorkerException {
	
		File aisOutFile = null;
		if (useTempFile) {
			try {
				tmpFile = File.createTempFile(getClass().getName(), "."
						+ aff.getExtension());
			} catch (IOException e1) {
                evTrans.fireAWTEventAndWait(new AudioFileWriterErrorEvent(this, e1));
				return;
			}
			aisOutFile = tmpFile;
		} else {
			aisOutFile = outFile;
		}
		if(ais instanceof ProgressMonitorAudioInputStream){
			((ProgressMonitorAudioInputStream)ais).addProgressListener(this);
		}
		progressStatus.setMessage(new LocalizableMessage("Creating temporary audio file ..."));
		//progressEventTransferAgent.fireAWTEventAndWait(new ProgressEvent(this,0,"Creating temporary audio file ..."));
		fireProgressEvent();
		try {
			ThreadSafeAudioSystem.write(ais, aff, aisOutFile);
//			if(cancelled){
//				aisOutFile.delete();
//				listener.update(new AudioFileWriterCancelledEvent(this));
//			}
		} catch (IOException e) {
			aisOutFile.delete();
			if (e instanceof CancelledException) {
				if(cancelled){
				//listener.update(new AudioFileWriterCancelledEvent(this));
					evTrans.fireAWTEventAndWait(new AudioFileWriterCancelledEvent(this));
				}
			} else {
				//listener.update(new AudioFileWriterErrorEvent(this, e));
				progressEventTransferAgent.fireAWTEventAndWait(new ProgressErrorEvent(this,progressStatus));
				evTrans.fireAWTEventAndWait(new AudioFileWriterErrorEvent(this, e));
			}
			return;
		} finally {
			try {
				if (ais != null){
					if(ais instanceof ProgressMonitorAudioInputStream){
						((ProgressMonitorAudioInputStream)ais).removeProgressListener(this);
					}
					ais.close();
				}
			} catch (IOException e1) {
                if (aisOutFile!=null) aisOutFile.delete();
				//listener.update(new AudioFileWriterErrorEvent(this, e1));
				return;
			}
			pmais=null;
		}

		if (useTempFile && !cancelled) {
			InputStream is = null;
			FileOutputStream o = null;
			int bufSize = 2048;
			byte[] buf = new byte[bufSize];
			progressStatus.setProgress(50);
			progressStatus.setMessage(new LocalizableMessage("Saving to audio file " + outFile.getName()));

			//progressEventTransferAgent.fireAWTEventAndWait(new ProgressEvent(this,50,"Saving to audio file " + outFile.getName()));
			fireProgressEvent();
			try {
					long length=tmpFile.length();
					is = new FileInputStream(tmpFile);
				o = new FileOutputStream(outFile);
				int b;
				long written=0;
				while ((b = is.read(buf)) != -1) {
					o.write(buf, 0, b);
					written+=b;
					progressStatus.setProgress(50+(written*50)/length);
					//progressEventTransferAgent.fireAWTEventAndWait(new ProgressEvent(this,(50+(written*50)/length)));
					fireProgressEvent();
					if(cancelled){
						try{
							o.close();
							o=null;
							evTrans.fireAWTEventAndWait(new AudioFileWriterCancelledEvent(this));
							return;
						}catch(IOException ioe){
							throw ioe;
						
						}finally{
							try{
								is.close();
								is=null;
							}catch(IOException ioe){
								throw ioe;
							}finally{
								outFile.delete();
							}
						}

					}
				}

			} catch (IOException e) {
				outFile.delete();
				if (e instanceof InterruptedIOException) {
					evTrans.fireAWTEventAndWait(new AudioFileWriterCancelledEvent(this));
				} else {
					progressEventTransferAgent.fireAWTEventAndWait(new ProgressErrorEvent(this));
					evTrans.fireAWTEventAndWait(new AudioFileWriterErrorEvent(this, e));
				}
				return;
			} finally {

				try {
					if (is != null)
						is.close();
				} catch (IOException e1) {
					progressEventTransferAgent.fireAWTEventAndWait(new ProgressErrorEvent(this));
					evTrans.fireAWTEventAndWait(new AudioFileWriterErrorEvent(this, e1));
					return;
				} finally {

					try {
						if (o != null)
							o.close();
					} catch (IOException e2) {
						progressEventTransferAgent.fireAWTEventAndWait(new ProgressErrorEvent(this));
						evTrans.fireAWTEventAndWait(new AudioFileWriterErrorEvent(this, e2));
						return;
					} finally {
						if (tmpFile != null) {
							tmpFile.deleteOnExit();
							tmpFile.delete();
						}

					}
				}
			}
//			if (ais instanceof ProgressMonitorAudioInputStream) {
//				//progressEventTransferAgent.fireAWTEventAndWait(new ProgressEvent(this,true));
//				fireProgressEvent();
//			}
		}

		//listener.update(new AudioFileWriterWrittenEvent(this));
		evTrans.fireAWTEventAndWait(new AudioFileWriterWrittenEvent(this,outFile));
		
	}

	public void cancel() {
		cancelled=true;
		ProgressMonitorAudioInputStream pmaisC=pmais;
		if(pmaisC!=null){
		pmaisC.setCancelled(true);
//		try {
//			join();
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		try {
			pmaisC.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	

	
//	@Override
//	public void addProgressListener(ProgressListener progressListener) {
//	pmais.addProgressListener(progressListener);
//	}
//	@Override
//	public void removeProgressListener(ProgressListener progressListener) {
//		pmais.removeProgressListener(progressListener);
//		
//	}
	
	
	
	
	public synchronized void addListener(EventListener eventListener) {
		   if (eventListener != null && !listeners.contains(eventListener)) {
	            listeners.addElement(eventListener);
	        }
	}
	
	public synchronized void removeListener(EventListener eventListener) {
		  if (eventListener != null) {
	            listeners.removeElement(eventListener);
	        }
	}
//	public synchronized void addProgressListener(
//			ProgressListener progressListener) {
//		progressEventTransferAgent.addListener(progressListener);
//	
//	}
//
//	public synchronized void removeProgressListener(
//			ProgressListener progressListener) {
//		progressEventTransferAgent.removeListener(progressListener);
//		// if (progressListener != null) {
//		// listeners.removeElement(progressListener);
//		// }
//	}

	public void update(ProgressEvent progressEvent){
		if(useTempFile){
			ProgressStatus ps=progressEvent.getProgressStatus();
			// progress has two parts: create temporary file and copy audio file
			progressStatus.setProgress(ps.getPercentProgress() /2);
			// filter finish event
//			if(ps.isDone()){
//				//System.out.println("Filtered finish event.");
//				return;
//			}
		}
		//progressEventTransferAgent.fireAWTEventAndWait(progressEvent);
		fireProgressEvent();
		
	}


}
