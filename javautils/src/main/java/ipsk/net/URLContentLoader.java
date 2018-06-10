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

import ipsk.awt.ProgressListener;
import ipsk.awt.ProgressWorker;
import ipsk.awt.WorkerException;
import ipsk.awt.event.ProgressEvent;
import ipsk.net.http.ContentType;
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import javax.net.ssl.HttpsURLConnection;

/**
 * Asynchronous download engine for URL content.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
// TODO localize messages
public class URLContentLoader extends ProgressWorker{
	
	
	
	public final static boolean DEBUG = false;
	public final static int DEBUG_TOTAL_MIN_MS=10000; // minimum 10s debug download time
	public final int DEFAULT_BUFFER_SIZE=2048;
	
	
	protected URL url;
	private OutputStream outputStream;
	private URLConnection urlConn = null;
	private byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
	private ContentType contentType;
	private int contentLength;
	private boolean openConnectionInThread=true;
	private Integer connectTimeOut=null;
	
	/**
	 * @return the connectTimeOut, null if default of URLConnection is used
	 */
	public Integer getConnectTimeOut() {
		return connectTimeOut;
	}

	/**
	 * @param connectTimeOut the connectTimeOut to set, null to use default of URLConnection
	 */
	public void setConnectTimeOut(Integer connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}
	private Integer readTimeout=null;
	
	/**
	 * @return the readTimeout, null if default of URLConnection is used
	 */
	public Integer getReadTimeout() {
		return readTimeout;
	}

	/**
	 * @param readTimeout the readTimeout to set
	 */
	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	
	public void setTimeouts(Integer timeout){
		setConnectTimeOut(timeout);
		setReadTimeout(timeout);
	}

	/**
	 * @return the openConnectionInThread
	 */
	public boolean isOpenConnectionInThread() {
		return openConnectionInThread;
	}
	
	/**
	 * @param openConnectionInThread the openConnectionInThread to set
	 */
	public void setOpenConnectionInThread(boolean openConnectionInThread) {
		this.openConnectionInThread = openConnectionInThread;
	}
	public URLContentLoader(){
		this(null,null);
	}
	public URLContentLoader(String threadName){
		this(null,null,threadName);
	}
	
	
	/**
	 * Constructor.
	 * 
	 */
	public URLContentLoader(URL url,OutputStream outputStream){
		this(url,outputStream,null);
	}
	
	/**
	 * Constructor.
	 * 
	 */
	public URLContentLoader(URL url,OutputStream outputStream,String threadName){
		super(threadName);
		this.url=url;
		this.outputStream=outputStream;
	}
	
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void open() throws WorkerException {
		if(!openConnectionInThread){
			openConn();
		}
		super.open();
	}
	
	private void openConn() throws WorkerException{
		int responseCode;
		String responseMsg = null;
		try {
			progressStatus.setMessage(new LocalizableMessage("Connecting ..."));
			//progressEventTransferAgent.fireEventAndWait(new ProgressEvent(this,0,"Connecting ..."));
			fireProgressEvent();
			
			urlConn = url.openConnection();
//			System.out.println("Connect timeout: "+urlConn.getConnectTimeout()+" Read timeout:"+urlConn.getReadTimeout());
			if(connectTimeOut!=null){
				urlConn.setConnectTimeout(connectTimeOut);
			}
			if(readTimeout!=null){
				urlConn.setReadTimeout(readTimeout);
			}
			if (urlConn != null) {
				if (urlConn instanceof HttpURLConnection) {
					responseCode=((HttpURLConnection) urlConn).getResponseCode();
				} else if (urlConn instanceof HttpsURLConnection) {
					responseCode = ((HttpsURLConnection) urlConn).getResponseCode();
				}
				if (urlConn instanceof HttpURLConnection) {
						responseMsg = ((HttpURLConnection) urlConn)
								.getResponseMessage();
					} else if (urlConn instanceof HttpsURLConnection) {
						responseMsg = ((HttpsURLConnection) urlConn)
								.getResponseMessage();
					}
			}
			contentType=null;
			String contentTypeStr=urlConn.getContentType();
			if(contentTypeStr!=null){
			    contentType=ContentType.parseHttpString(contentTypeStr);
			}
			contentLength=urlConn.getContentLength();
			progressStatus.setMessage(new LocalizableMessage("Connected."));
			fireProgressEvent();
			//progressEventTransferAgent.fireEventAndWait(new ProgressEvent(this,0,"Connected."));
		} catch (IOException e) {
			if(DEBUG)e.printStackTrace();
			
			
//			String errMsg = null;
//			if (responseMsg != null) {
//				errMsg = "Error connecting '" + url + "': " + responseMsg;
//			} else {
//				errMsg = "Error connecting '" + url + "'";
//			}
			//System.err.println(errMsg);
			throw new WorkerException(responseMsg);
			//return;
		}
		
	}
		
	public void doWork() throws WorkerException{
		
		if(openConnectionInThread){
			openConn();
		}
		try {
			long transferred=0;
			InputStream is = urlConn.getInputStream();
			if (contentLength==-1){
				//
				progressStatus.setLength(ProgressStatus.LENGTH_UNKNOWN);
				progressStatus.setIndeterminate(true);
				//progressEventTransferAgent.fireEvent(new ProgressEvent(this,true,false,0));
			}else{
				progressStatus.setLength(contentLength);
			}
			fireProgressEvent();
			int read = 0;
			if(DEBUG){
				
			}
			long startTime=System.currentTimeMillis();
			progressStatus.setMessage(new LocalizableMessage("Download ..."));
			while (!hasCancelRequest() && (read = is.read(buf)) != -1) {
				outputStream.write(buf, 0, read);
				transferred+=read;
				if (contentLength>=0){
					long perCentProgress=transferred*100/contentLength;
					if(DEBUG){
						long currentTime=System.currentTimeMillis();
						long delayedTime=((perCentProgress*DEBUG_TOTAL_MIN_MS)/100);
						long waitTime=delayedTime-(currentTime-startTime);
						if(waitTime>=0){
							try {
								Thread.sleep(waitTime);
							} catch (InterruptedException e) {
								
								// OK
							}
						}
					}
					
					//progressStatus.setProgress(perCentProgress);
					//progressEventTransferAgent.fireEvent(new ProgressEvent(this,perCentProgress,"Download..."));
					progressStatus.setProgress(transferred);
					fireProgressEvent();
				}
			}
			
		} catch (IOException e) {
//			e.printStackTrace();
//			ProgressStatus errStatus=new ProgressStatus();
//			errStatus.setError(e.getLocalizedMessage());
//			status=State.ERROR;
//			progressEventTransferAgent.fireEvent(new ProgressEvent(errStatus));
//			
//			return;
			//System.out.println(e.getMessage());
			throw new WorkerException(e);
		} finally {
			if (outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					// leave it open
					e.printStackTrace();
				}
		}
	}

	public static void main(String[] args){
		URL url=null;
		try {
			url = new URL(args[0]);
			File testFile=new File(args[1]);
			FileOutputStream fos=new FileOutputStream(testFile);
			final URLContentLoader cl=new URLContentLoader(url,fos);
			cl.addProgressListener(new ProgressListener(){

				public void update(ProgressEvent progressEvent) {
					System.out.println(progressEvent.getProgressStatus().getPercentProgress()+" %");
					 ProgressStatus status=progressEvent.getProgressStatus( );
			            if(status.isDone()){
					    ContentType ct=cl.getContentType();
			            if(ct!=null){
			                String charsetName=ct.getCharsetParameter();
			                if(charsetName!=null){
			                    Charset cs=Charset.forName(charsetName);
			                    if(cs!=null){
			                        System.out.println(cs.displayName());
			                    }
			                }
			               
			            }
					}
				}
				
			});
			cl.open();
			cl.start();
			cl.close();
			cl.reset();
		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (WorkerException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}


	public OutputStream getOutputStream() {
		return outputStream;
	}


	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
    public ContentType getContentType() {
        return contentType;
    }

}
