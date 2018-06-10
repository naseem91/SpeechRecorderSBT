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

package ipsk.net.http;

import ipsk.net.Upload;
import ipsk.net.UploadCache;
import ipsk.net.UploadException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Cache to upload data via HTTP PUT or POST method to an remote server.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class HttpUploadCache extends UploadCache {

   // private static boolean DEBUG = true;

   // private static int THREAD_IDLE_TIME = 0;

    // private static int DEBUG_DELAY = 100;
    private Logger logger;

    private int responseCode = 0;

    //private String encoding;

    private Vector<String> sessionCookies = new Vector<String>();

    private boolean acceptCookies = false;

    /**
     * Create new empty cache using PUT as default request method..
     * 
     */
    public HttpUploadCache() {
        super();
        logger = Logger.getLogger(getClass().getName());
        logger.setLevel(Level.FINEST);
        bufSize = DEFAULT_BUFSIZE;
        buffer = new byte[bufSize];
        //uploadCacheListenerList = new Vector();
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
        requestMethod = "POST";

    }

    /**
     * Set cookies to send to remote server.
     * @param sessionCookies
     */
    public void setSessionCookies(String[] sessionCookies) {
        for (int i = 0; i < sessionCookies.length; i++) {
            this.sessionCookies.add(sessionCookies[i]);
        }
    }

    private boolean tryConnect() {
        int retryCount = 0;
        int idleCount = 0;
        do {
            getNextUpload();
            while (currentUpload == null && running) {
            	fireFinished();
                try {
                    Thread.sleep(ON_IDLE_DELAY);
                } catch (InterruptedException e1) {
                }
                idleCount++;
                // idle = true;
                getNextUpload();
            }
            if (!running)
                return false;
            idle = false;
            fireTryConnect();
            logger.fine("Try connect...");
            try {
                connection = currentStream.getUrl().openConnection();
                // connection.setRequestProperty ("Authorization", "Basic " +
                // encoding);
                if (sessionCookies != null) {
                    for (int i = 0; i < sessionCookies.size(); i++) {

                        // if available set a session cookie
                        connection.addRequestProperty("Cookie",
                                (String) (sessionCookies.get(i)));
                        // System.out.println("Sending Cookie:
                        // "+(String)sessionCookies.get(i));
                    }
                    // allow new user authentication if the session is expired
                    connection.setAllowUserInteraction(true);
                }
                long contentLength = currentStream.getLength();
                logger.info("Content length of upload: " + contentLength);
          
                if (contentLength >0 && contentLength <= Integer.MAX_VALUE){
                	((HttpURLConnection)connection).setFixedLengthStreamingMode((int)contentLength);
                }else{
//                	 TODO test this
                	// Tomcat says: invalid chunk
                    //((HttpURLConnection)connection).setChunkedStreamingMode(1024);
                }

                connection.addRequestProperty("Content-length", new Long(
                        currentStream.getLength()).toString());
                
                connection.setDoOutput(true);
                if (connection instanceof HttpURLConnection) {
                	HttpURLConnection httpConn=(HttpURLConnection) connection;
                    httpConn.setRequestMethod(requestMethod);
                    String mimeType=currentStream.getMimeType();
                    if (mimeType!=null){
                    	httpConn.setRequestProperty("Content-type", mimeType);
                    }else{
                    httpConn.setRequestProperty(
                            "Content-type", "application/octet-stream");
                    }
                }
                connection.connect();

            } catch (IOException e) {
                responseMessage = new String("Cannot connect. Retry # "
                        + retryCount);
                logger.warning(responseMessage + " " + e.getLocalizedMessage());
                fireStateChanged(null);
                if (!running)
                    break;
                // fireDisconnected();
                try {
                    Thread.sleep(CONNECT_RETRY_DELAY);

                } catch (InterruptedException e1) {
                }

                retryCount++;
				if (retryCount > DEFAULT_CONNECT_RETRIES) {
					responseMessage= new String("Cannot connect.");
					fireStateChanged(null);
					running=false;
					break;
				} else {
                continue;
            }
			}
            connected = true;
            logger.fine("Connected.");
            fireConnected();
            return true;
        } while (running);
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {

        // String userPassword = username + ":" + password;
        // String userPassword="speechrecorder:speak123!";
        // System.out.println("Auth: "+userPassword);
        // encoding = new sun.misc.BASE64Encoder().encode
        // (userPassword.getBytes());
        do {
            // try {
            // Thread.sleep(10);
            // } catch (InterruptedException e2) {
            // // no problem
            // }
            if (!tryConnect())
                break;
            startConnect = System.currentTimeMillis();
            logger.fine("starting upload");
            synced = false;
            try {
				try {
                inputStream = currentStream.getInputStream();
				} catch (UploadException e) {
					// this means the file could not be opened
				    logger.severe("Cannot get input stream: "+e.getMessage());
					currentStream.setStatus(Upload.CANCEL);
					continue;
				}
                outputStream = connection.getOutputStream();
                int read = 0;
                guessedToUploadLength = toUploadLength;
                currentStream.setStatus(Upload.UPLOADING);
                fireStateChanged(currentStream);

				try {
                while (read >= 0) {
                    read = inputStream.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        outputStream.write(buffer, 0, read);
                    }
                    Thread.sleep(2);
                    if (DEBUG_DELAY > 0) {
                        try {
                            Thread.sleep(DEBUG_DELAY);
                        } catch (InterruptedException e1) {
                            // no problem
                        }
                    }

                }
				} catch (IOException e) {
				    logger.severe("Cannot upload !: "+e.getMessage());
					currentStream.setStatus(Upload.FAILED);
				} finally {
					try {
						if (inputStream != null)
                inputStream.close();
					} catch (IOException e) {
					    logger.severe("Close error !: "+e.getMessage());
					} finally {
						if (outputStream != null)
                outputStream.close();
					}
				}
                if (connection instanceof HttpURLConnection) {
                    // Commented out to hold the underlying cached persistent
                    // connection
                    // ((HttpURLConnection) connection).disconnect();
                    responseCode = ((HttpURLConnection) connection)
                            .getResponseCode();
                    responseMessage = ((HttpURLConnection) connection)
                            .getResponseMessage();
                    
                    // Only HTTP responses 200-299 will be accepted
                    if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST || responseCode < HttpURLConnection.HTTP_OK) {
                        logger.warning("Upload failed: " + currentStream);
                        logger.warning("HTTP: " + responseMessage + ", "
                                + responseCode);
                        currentStream.setStatus(Upload.FAILED);
                        if(uploadRetryCount>UPLOAD_RETRIES){
        					fireStateChanged(null);
        					running=false;
        					break;
                        }
                        Thread.sleep(UPLOAD_RETRY_DELAY);
                        uploadRetryCount++;
                        
                    } else {

                        currentStreamIndex++;
                        if (currentStreamIndex == currentUpload.length) {
                            currentStreamIndex = 0;
                            currentUpload = null;
                        }
                        connectedTimeInMillis += System.currentTimeMillis()
                                - startConnect;
                        totalUploadLength += currentStream.getLength();
                        logger.info("Uploaded: " + currentStream);
                        currentStream.setStatus(Upload.DONE);
                        if (acceptCookies) {
                            String cookie = ((HttpURLConnection) connection)
                                    .getHeaderField("Set-Cookie");
                            if (cookie != null) {
                                sessionCookies.add(cookie);
                                // System.out.println("Received Cookie: " +
                                // cookie);
                            }
                        }
                        logger.info("HTTP: " + responseMessage + ", "
                                + responseCode);
                    }
                }
            } catch (Exception e) {
			    logger.severe("Cannot upload !: "+e.getMessage());
                currentStream.setStatus(Upload.FAILED);
                if(uploadRetryCount>UPLOAD_RETRIES){
                	responseMessage="Exception: "+e.getMessage();
					fireStateChanged(null);
					running=false;
					break;
                }
                try {
					Thread.sleep(UPLOAD_RETRY_DELAY);
				} catch (InterruptedException e1) {
					// Nothing to do
				}
                uploadRetryCount++;
                
            } finally {
                synced = true;
                try {
					if (inputStream !=null)inputStream.close();
					if (outputStream !=null)outputStream.close();
                } catch (IOException e1) {
				    logger.severe("Close error: "+e1.getMessage());
                    currentStream.setStatus(Upload.FAILED);
                }
                calculateLength();
                fireStateChanged(currentStream);
                connected = false;
                fireDisconnected();
            }
        } while (running);
        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).disconnect();
        }
		idle=true;
    }

    /**
     * Get permission to accept cookies.
     * @return true if cookies are accepted
     */
    public boolean isAcceptCookies() {
        return acceptCookies;
    }

    /**
     * Set permission to accept cookies.
     * @param b true to accept cookies
     */
    public void setAcceptCookies(boolean b) {
        acceptCookies = b;
    }

}
