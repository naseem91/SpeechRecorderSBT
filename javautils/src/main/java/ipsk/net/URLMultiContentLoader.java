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
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Downloader for URL content.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
// TODO localize messages
public class URLMultiContentLoader extends ProgressWorker{
	
	
	
	public final static boolean DEBUG = false;
	public final static int DEBUG_TOTAL_MIN_MS=10000; // minimum 10s debug download time
	public final int DEFAULT_BUFFER_SIZE=2048;
	
	
	private List<Download> downloadList;
	private byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
	private Long totalContentLength;
	
	private volatile Download currentDownload;
	
	public URLMultiContentLoader(){
		this(null,null);
	}
	public URLMultiContentLoader(String threadName){
		this(null,threadName);
	}
	
	
	/**
	 * Constructor.
	 * 
	 */
	public URLMultiContentLoader(List<Download> downloads){
		this(downloads,null);
	}
	
	/**
	 * Constructor.
	 * 
	 */
	public URLMultiContentLoader(List<Download> downloads,String threadName){
		super(threadName);
		downloadList=downloads;
	}
	

	public void open() throws WorkerException {
		int responseCode;
		String responseMsg = null;
		try {
		    totalContentLength=new Long(0);
			progressStatus.setMessage(new LocalizableMessage("Connecting ..."));
			//progressEventTransferAgent.fireEventAndWait(new ProgressEvent(this,0,"Connecting ..."));
			fireProgressEvent();
			for(Download download:downloadList){
			    URL url=download.getSourceUrl();
			    URLConnection urlConn = url.openConnection();
//			    urlConn.setDoInput(false);
//			    urlConn.setDoOutput(false);
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
			  
			    int contentLength=urlConn.getContentLength();
			   
			    if (contentLength==-1){
			        // URLConnection is limited to Integer 
			        String clStr=urlConn.getHeaderField("Content-Length");
			        if(clStr!=null){
			            try{
			                long parsedct=Long.parseLong(clStr);
			                totalContentLength+=parsedct;
			            }catch(NumberFormatException nfe){
			                totalContentLength=null; 
			            }
			        }else{
			            totalContentLength=null; 
			        }
			    }else{
			        if(totalContentLength!=null){
			            totalContentLength+=contentLength;
			        }
			    }
			    progressStatus.setMessage(new LocalizableMessage("Connected."));
			    
			}
			
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
		super.open();
	}
		
	public void doWork() throws WorkerException{
	    String responseMsg = null;
	    long transferred=0;
	    for(Download download:downloadList){
	        URL url=download.getSourceUrl();
	        OutputStream outputStream=download.getContentOutputStream();
	        try {
	            URLConnection urlConn = url.openConnection();

	            if (urlConn != null) {
	                //                if (urlConn instanceof HttpURLConnection) {
	                //                    responseCode=((HttpURLConnection) urlConn).getResponseCode();
	                //                } else if (urlConn instanceof HttpsURLConnection) {
	                //                    responseCode = ((HttpsURLConnection) urlConn).getResponseCode();
	                //                }
	                if (urlConn instanceof HttpURLConnection) {
	                    responseMsg = ((HttpURLConnection) urlConn)
	                    .getResponseMessage();
	                } else if (urlConn instanceof HttpsURLConnection) {
	                    responseMsg = ((HttpsURLConnection) urlConn)
	                    .getResponseMessage();
	                }
	            }



	            InputStream is = urlConn.getInputStream();
	            if (totalContentLength==null){
	                //
	                progressStatus.setLength(ProgressStatus.LENGTH_UNKNOWN);
	                progressStatus.setIndeterminate(true);
	                //progressEventTransferAgent.fireEvent(new ProgressEvent(this,true,false,0));
	            }else{
	                progressStatus.setLength(totalContentLength);
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
	                if (totalContentLength!=null){
	                    long perCentProgress=transferred*100/totalContentLength;
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
	}

	public static void main(String[] args){
		URL url=null;
		try {
			url = new URL("http://www.phonetik.uni-muenchen.de/~klausj/Trappa1.wav");
			File testFile=new File("test.wav");
			FileOutputStream fos=new FileOutputStream(testFile);
			URL url2=new URL("http://ftp5.gwdg.de/pub/opensuse/distribution/12.1/iso/openSUSE-12.1-DVD-x86_64.iso");
			File testFile2=new File("openSUSE-12.1-DVD-x86_64.iso");
			FileOutputStream fos2=new FileOutputStream(testFile2);
			ArrayList<Download> downloads=new ArrayList<Download>();
			Download dl=new Download(url,fos);
			downloads.add(dl);
			Download dl2=new Download(url2,fos2);
			downloads.add(dl2);
			URLMultiContentLoader cl=new URLMultiContentLoader(downloads,"Download");
			cl.addProgressListener(new ProgressListener(){

				public void update(ProgressEvent progressEvent) {
					System.out.println(progressEvent.getProgressStatus().getProgress());
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


}
