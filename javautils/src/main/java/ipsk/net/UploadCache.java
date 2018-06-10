//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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


package ipsk.net;


import ipsk.awt.AWTEventTransferAgent;

import ipsk.net.event.UploadConnectionEvent;
import ipsk.net.event.UploadEvent;
import ipsk.net.event.UploadFinishedEvent;
import ipsk.net.event.UploadStateChangedEvent;
import ipsk.net.event.UploadConnectionEvent.ConnectionState;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;


/**
 * Caches upload data and sends it to remote server.
 * @see ipsk.net.Upload
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public abstract class UploadCache implements Runnable {
	public class EventTransformer extends AWTEventTransferAgent<EventListener,EventObject>{
		@Override
		public void fireEvent(EventListener l,EventObject ev){
			UploadCacheListener ucl=(UploadCacheListener)l;
			ucl.update((UploadEvent)ev);
		}
	}

	public static final String DEF_CHECKSUM_ALGORITHM = "MD5";
	
	
	protected static boolean DEBUG = false;
	public final static int UNLIMITED=-1;
	protected static int ON_IDLE_DELAY=2000;
	protected static int CONNECT_RETRY_DELAY=10000;
	protected static int DEFAULT_CONNECT_RETRIES=5;
	
	protected static int UPLOAD_RETRY_DELAY=4000;
	protected static int UPLOAD_RETRIES=20;

	//protected static int DEBUG_DELAY = 500;
	protected static int DEBUG_DELAY = 0;
	protected static int DEFAULT_BUFSIZE = 2048;
	
	protected float byteRate; // bytes per milliseconds
	protected long connectedTimeInMillis;
	protected long totalUploadLength;
	protected boolean connected = false;
	protected boolean idle;
	protected boolean idleNotified=false;
	protected int bufSize;
	protected OutputStream outputStream;
	protected InputStream inputStream;
	protected byte[] buffer;
	//private URL url;
	protected URLConnection connection;
	protected String requestMethod;
	private Vector<Upload[]> uploadStreams = new Vector<Upload[]>();

	// The currentUpload and currentStream variables are only used
	// by the upload thread, but however should I declare them volatile? 
	protected Upload[] currentUpload = null;
	protected Upload currentStream = null;
	
	protected int currentStreamIndex = 0;
	protected boolean running = false;
	//protected Vector<UploadCacheListener> uploadCacheListenerList;
	protected EventTransformer eventTransformer=new EventTransformer();
	private Thread uploadThread;
	protected long startConnect;
	protected long totalLength;

	protected long toUploadLength;
	protected long guessedToUploadLength;
	protected long holdLength;
	protected long holdSize;
	protected boolean synced;


	protected String responseMessage = new String("O.K.");
	private int responseCode = 0;
	
	protected int uploadRetryCount=0;
	private String digestName=DEF_CHECKSUM_ALGORITHM;
	private boolean overwrite=false;
	protected boolean transferRateLimitSupported=false;
	protected int transferRateLimit=UNLIMITED;
	private Logger logger;

	/**
	 * Create new empty cache.
	 *
	 */
	public UploadCache() {
		super();
		bufSize = DEFAULT_BUFSIZE;
		buffer = new byte[bufSize];
		//uploadCacheListenerList = new Vector<UploadCacheListener>();
		totalLength = 0; // toUploadLength +holdLength;
		toUploadLength = 0;
		guessedToUploadLength = 0;
		holdLength = 0;
		holdSize = 0;
		totalUploadLength = 0;
		connectedTimeInMillis = 0;
		byteRate = 0;
		idle = true;
		synced = true;
		String packageName=getClass().getPackage().getName();
		logger=Logger.getLogger(packageName);
	}

	//	/**
	//	 * Set the authenticator.
	//	 * @param authenticator
	//	 */
	//	public void setAuthenticator(Authenticator authenticator) {
	//		UploadCache.authenticator = authenticator;
	//	}

	protected synchronized void calculateLength() {
		long toUploadLength = 0;
		long holdLength = 0;
		for (int i = 0; i < uploadStreams.size(); i++) {
			Upload[] upload = (Upload[]) uploadStreams.get(i);
			for (int j = 0; j < upload.length; j++) {
				Upload uvb = upload[j];
				int status = uvb.getStatus();
				long len = uvb.getLength();
				if (status == Upload.DONE || status==Upload.CANCEL || status==Upload.DROPPED) {
					holdLength += len;
				} else {
					toUploadLength += len;
				}
			}
		}
		this.toUploadLength = toUploadLength;
		this.holdLength = holdLength;
		this.totalLength = toUploadLength + holdLength;
		if (connectedTimeInMillis != 0)
			byteRate =
				(float) totalUploadLength / (float) connectedTimeInMillis;
	}

	/**
	 * Request an upload of the referenced data buffers to the given URLs.
	 * @param vbs the container of buffer references with data to upload
	 * @throws UploadException 
	 */
	public synchronized void upload(Upload[] vbs) throws UploadException {

		idle = false;
		idleNotified=false;
		if(digestName!=null){
			for(Upload upl:vbs){
				upl.createChecksum(digestName);
			}
		}
		// replace existing uploads if in overwrite mode
		if(overwrite){
			URL[] urls = new URL[vbs.length];
			for (int i = 0; i < vbs.length; i++) {
				urls[i] = vbs[i].getUrl();
			}
			Upload[] ubs = findMatch(urls);
			if (ubs != null) {
				uploadStreams.remove(ubs);
				if (ubs != currentUpload) {
					for (int i = 0; i < ubs.length; i++) {
						ubs[i].setStatus(Upload.CANCEL);
						logger.info("Upload "+ubs[i]+" cancelled.");
					}
				}
			}
		}
		boolean doneAvailable = true;
		while (holdLength > holdSize && doneAvailable) {
			doneAvailable = false;
			for (int i = 0; i < uploadStreams.size(); i++) {
				Upload[] holds = (Upload[]) uploadStreams.get(i);
				boolean done = true;
				int size = 0;
				for (int j = 0; j < holds.length; j++) {
					size += holds[j].getLength();
					if (holds[j].getStatus() != Upload.DONE) {
						done = false;
						break;
					}
				}
				if (done) {
					uploadStreams.remove(holds);
					for (int j = 0; j < holds.length; j++) {
						holds[j].delete();
					}
					holdLength -= size;
					doneAvailable = true;
					if (DEBUG)
						System.out.println("Expired " + size + " bytes.");
				}
			}
		}
		//			Upload[] uvbs = new Upload[vbs.length];
		//			for (int i = 0; i < vbs.length; i++) {
		//				uvbs[i] = new Upload(vbs[i], urls[i]);
		//			}
		//			uploadStreams.add(uvbs);
		//			calculateLength();
		//			for (int i = 0; i < uvbs.length; i++) {
		//				fireStateChanged(uvbs[i]);
		//			}
		uploadStreams.add(vbs);
		calculateLength();
		//		for (int i = 0; i < vbs.length; i++) {
		//			fireStateChangedWait(vbs[i]);
		//		}

	}

	private synchronized Upload[] findMatch(URL[] urls) {

		Enumeration<Upload[]> e = uploadStreams.elements();
		while (e.hasMoreElements()) {

			Upload[] uvbs = (Upload[]) e.nextElement();

			if (uvbs.length != urls.length)
				continue;
			boolean match = true;
			for (int i = 0; i < uvbs.length; i++) {
				if (!uvbs[i].getUrl().sameFile(urls[i])) {
					match = false;
					break;
				}
				if (match)
					return uvbs;
			}
		}
		return null;
	}

	/**
	 * Try to get an input stream to cached data for the given URL.
	 * @param urls
	 * @return <ul><li>the input stream if the requested data was found in the cache</li><li><code>null</code> if the requested URL data was not found in the cache</li>
	 */
	public synchronized InputStream[] getCachedInputStream(URL urls[])
		throws UploadException {
		InputStream[] res = null;
		Upload[] us = findMatch(urls);
		if (us != null) {
			synchronized (this) {
				res = new InputStream[us.length];
				for (int i = 0; i < us.length; i++) {
					//VectorBuffer vb = (VectorBuffer) us[i].getVectorBuffer().clone();
					res[i] = us[i].getInputStream();
				}
			}
		}
		return res;
	}

	/**
	 * Start upload.
	 */
	public void start() {
		if(running)return;
		running = true;
		if (uploadThread == null || !uploadThread.isAlive()) {
			uploadThread = new Thread(this);
			uploadThread.setPriority(Thread.MIN_PRIORITY);
			uploadThread.start();
		}
	}
	/**
	 * Stop upload.
	 *
	 */
	public void stop() {
		running = false;
		uploadThread.interrupt();
		try {
			uploadThread.join();
		} catch (InterruptedException e) {
		}
	}

	
	protected synchronized void getNextUpload() {

		currentStream = null;
		currentUpload = null;
		for (int i = 0; i < uploadStreams.size(); i++) {
			Upload[] upload = (Upload[]) uploadStreams.get(i);
			for (currentStreamIndex = 0;
				currentStreamIndex < upload.length;
				currentStreamIndex++) {
				int status = upload[currentStreamIndex].getStatus();
				if (status == Upload.IDLE || status == Upload.FAILED) {
					if (DEBUG)
						System.out.println("Found something to upload.");
					currentUpload = upload;
					currentStream = upload[currentStreamIndex];
					idle = false;
					return;
				}
			}
		}
		idle = true;
	}



	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();

	/**
	 * Get the current upload.
	 * @return current upload
	 */
	public Upload getCurrentUploadStream() {
		return currentStream;
	}

	/**
	 * Get connected status.
	 * @return true if connected
	 */
	public boolean isConnected() {
		return connected;
	}

	/**
	 * Clear all contents.
	 *
	 */
	public void clear(){
		uploadStreams.clear();
		uploadRetryCount=0;
	}
	
	/**
	 * Close upload cache.
	 * Same as stop() and clear().
	 *
	 */
	public void close() {
		stop();
		clear();
	}

	/**
	 * Add listener.
	 * @param acl listener to add
	 */
	public synchronized void addUploadCacheListener(UploadCacheListener acl) {
//		if (acl != null && !uploadCacheListenerList.contains(acl)) {
//			uploadCacheListenerList.addElement(acl);
//		}
		eventTransformer.addListener(acl);
	}

	/**
	 * Remove listener.
	 * @param acl listener to remove
	 */
	public synchronized void removeUploadCacheListener(UploadCacheListener acl) {
//		if (acl != null) {
//			uploadCacheListenerList.removeElement(acl);
//		}
		eventTransformer.removeListener(acl);
	}

	protected void fireConnected() {

//		Iterator it = uploadCacheListenerList.iterator();
//		while (it.hasNext()) {
//			UploadCacheListener listener = (UploadCacheListener) it.next();
//			listener.connected();
//		}
		
		eventTransformer.fireAWTEventLater(new UploadConnectionEvent(this,ConnectionState.CONNECTED));
		
	}
	protected void fireTryConnect() {

//		Iterator it = uploadCacheListenerList.iterator();
//		while (it.hasNext()) {
//			UploadCacheListener listener = (UploadCacheListener) it.next();
//			listener.tryConnect();
//		}
		eventTransformer.fireAWTEventLater(new UploadConnectionEvent(this,ConnectionState.TRY_CONNECT));
	}
	protected void fireStateChanged(Upload uvb) {

//		Iterator it = uploadCacheListenerList.iterator();
//		while (it.hasNext()) {
//			UploadCacheListener listener = (UploadCacheListener) it.next();
//			listener.stateChanged(uvb);
//		}
		eventTransformer.fireAWTEventLater(new UploadStateChangedEvent(this,uvb));
	}
	
	protected void fireStateChangedWait(Upload uvb) {

		eventTransformer.fireAWTEventAndWait(new UploadStateChangedEvent(this,uvb));
	}

	protected void fireDisconnected() {

//		Iterator it = uploadCacheListenerList.iterator();
//		while (it.hasNext()) {
//			UploadCacheListener listener = (UploadCacheListener) it.next();
//			listener.disconnected();
//		}
		
		eventTransformer.fireAWTEventLater(new UploadConnectionEvent(this,ConnectionState.DISCONNECTED));
	}
	
	protected void fireFinished(){
		// do not wait here for the event to finish, to avoid deadlock with
		// (UploadCache.close() waits for the thread to finish)
		if(!idleNotified){
		eventTransformer.fireAWTEventLater(new UploadFinishedEvent(this));
		idleNotified=true;
		}
	}

	/**
	 * Get total length in bytes.
	 * @return total length of 
	 */
	public long getTotalLength() {
		return totalLength;
	}

	/**
	 * Get total bytes already uploaded.
	 * @return uploaded count of bytes
	 */
	public long getTotalUploadLength() {
		return totalUploadLength;
	}

	/**
	 * Get idle status.
	 * @return idle status
	 */
	public boolean isIdle() {
		return idle;
	}

	//	public static void main(String[] args) {
	//		// TODO write test program:
	//		// Usage: UploadCache url1 file url2 file2 ... urln filen
	//		UploadCache uc=new UploadCache();
	//		uc.setAuthenticator(
	//			new SimplePasswordAuthentication("speechdatintern", "ipsk97"));
	//		URL url = null;
	//		FileInputStream fi = null;
	//		for (int i = 0; i < args.length; i += 2) {
	//			try {
	//				url = new URL(args[i]);
	//			} catch (MalformedURLException e) {
	//				System.err.println(args[i] + " is not a valid URL");
	//				continue;
	//			}
	//			try {
	//				fi = new FileInputStream(new File(args[i + 1]));
	//			} catch (FileNotFoundException e1) {
	//				System.err.println("File: " + args[i + 1] + " not found.");
	//				continue;
	//			}
	//
	//			uploadStream(fi, url);
	//		}
	//
	//	}

	/**
	 * Get current HTTP response code.
	 * @return HTTP response code
	 */
	public int getResponseCode() {
		return responseCode;
	}

	/**
	 * Get HTTP response message.
	 * @return HTTP response message
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/**
	 * Get maximum of bytes to hold in the cache.
	 * @return maximum hold size
	 */
	public long getHoldSize() {
		return holdSize;
	}

	/**
	 * Set maximum of bytes to hold in the cache.
	 * @param l maximum hold size
	 */
	public void setHoldSize(long l) {
		holdSize = l;
	}

	/**
	 * Get current count of hold bytes.
	 * @return bytes currently hold
	 */
	public long getHoldLength() {
		return holdLength;
	}

	/**
	 * Get estimated upload byte rate.
	 * @return byte rate in bytes/second
	 */
	public float getByteRate() {
		return byteRate;
	}

	/**
	 * Get bytes to upload.
	 * The current upload is ignored.
	 * @see #getGuessedToUploadLength()
	 * @return bytes to upload
	 * 
	 */
	public long getToUploadLength() {
		return toUploadLength;
	}

	/**
	 * Get guessed bytes to upload.
	 * The progress of the current upload can only be estimated.
	 * @return estimated count of bytes not yet uploaded
	 */
	public long getGuessedToUploadLength() {
		if (synced)
			return toUploadLength;
		long now = System.currentTimeMillis();
		long uploadDuration = now - startConnect;
		return toUploadLength - (long) ((float) uploadDuration * byteRate);
		//return guessedToUploadLength;
	}

	/**
	 * Get HTTP request method.
	 * @return request method
	 */
	public String getRequestMethod() {
		return requestMethod;
	}

	/**
	 * Set HTTP request method.
	 * @param string request method
	 */
	public void setRequestMethod(String string) {
		requestMethod = string;
	}

	/**
	 * Get upload running status.
	 * @return true if upload thread is running.
	 */
	public boolean isRunning() {
		return running;
}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public int getTransferLimit() {
		return transferRateLimit;
	}

	public void setTransferLimit(int transferLimit) {
		this.transferRateLimit = transferLimit;
	}

	public boolean isTransferLimitSupported() {
		return transferRateLimitSupported;
	}

	public void setTransferLimitSupported(boolean transferLimitSupported) {
		this.transferRateLimitSupported = transferLimitSupported;
	}
}
