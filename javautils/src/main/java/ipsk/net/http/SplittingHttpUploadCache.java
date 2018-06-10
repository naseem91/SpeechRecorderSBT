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
import ipsk.util.RadixConverters;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

/**
 * Cache to upload data via HTTP PUT or POST method to an remote server. This
 * upload cache can split the uploads into smaller pieces.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class SplittingHttpUploadCache extends UploadCache implements HttpUploadCacheExtension{
	
	public static final String STATUS_KEY="status";
	public static final String STATUS_OK="OK";
	
	public static final String UPLOAD_ID_KEY = "id";
	
	public static final String PARTITIONS_COUNT_KEY="_parts";
	public static final String PARTITION_NUM_KEY="_part";
	public static final String CHECK_SUM_TYPE_KEY="_chsumalg";
	public static final String CHECK_SUM_KEY="_chsum";
	
	private static final int MINIMUM_LIMIT = 256;
	
    private static boolean DEBUG = false;

    //private static int TRANSFER_RATE_LIMIT_BYTES_PER_SECOND=9500;  // 9000 bytes per second
    private static boolean RANDOMIZE_PART_SIZE=false;
    private static int DEFAULT_SPLIT_LIMIT=300000;
    private static int DEFAULT_SPLIT_MIN_LIMIT=10000;
    
    // private final static int DEBUG_SIMULATE_MAX_UPLOAD_LIMIT=1000000;
    private static int THREAD_IDLE_TIME = 0;

    public static int MAX_UPLOAD_ATTEMPTS = 4;

    // private static int DEBUG_DELAY = 100;
    private Logger logger;

    private int responseCode = 0;

    private String encoding;

    private Vector<String> sessionCookies = new Vector<String>();

    private boolean acceptCookies = false;

    private int limit;
   
    private long totalUploadedBytes=0;
    private long totalStartTimeMs=0;

    /**
     * Create new empty cache using PUT as default request method..
     * 
     */
    public SplittingHttpUploadCache() {
        super();

        logger = Logger.getLogger(getClass().getName());
        logger.setLevel(Level.FINEST);
        this.limit = DEFAULT_SPLIT_LIMIT;
        bufSize = DEFAULT_BUFSIZE;
        buffer = new byte[bufSize];
        //uploadCacheListenerList = new Vector();
        totalLength = 0; // toUploadLength +holdLength;
        toUploadLength = 0;
//        guessedToUploadLength = 0;
        holdLength = 0;
        holdSize = 0;
        totalUploadLength = 0;
        connectedTimeInMillis = 0;
        byteRate = 0;
        idle = true;
        synced = true;
        requestMethod = "POST";
        transferRateLimitSupported=true;
        // logger.warning("test warning");
        // logger.severe("test error");

    }

    public void setSessionCookies(String[] sessionCookies) {
        for (int i = 0; i < sessionCookies.length; i++) {
            this.sessionCookies.add(sessionCookies[i]);
        }
    }

    private boolean tryConnect(URL url, int partLength) throws IOException {
        int retryCount = 0;

        do {

//            if (!running){
//                return false;
//            }
            idle = false;

            fireTryConnect();
            logger.info("Try connect...");

            try {

                connection = url.openConnection();
                
                // connection.setRequestProperty ("Authorization", "Basic " +
                // encoding);
                if (DEBUG)
                    System.out.println("Got connection...");
                if (sessionCookies != null) {
                    if (DEBUG)
                        System.out.println(sessionCookies.size() + " Cookies.");
                    for (int i = 0; i < sessionCookies.size(); i++) {

                        // if available set a session cookie
                        connection.addRequestProperty("Cookie",
                                (String) (sessionCookies.get(i)));
                        // System.out.println("Sending
                        // Cookie:"+(String)sessionCookies.get(i));
                    }
                    // allow new user authentication if the session is expired
                    connection.setAllowUserInteraction(true);
                } else {
                    if (DEBUG)
                        System.out.println("Session cookies null !!");
                }

                logger.info("Content length of upload: " + partLength);
                connection.addRequestProperty("Content-type",
                        "application/octet-stream");
                connection.addRequestProperty("Content-length", new Integer(
                        partLength).toString());
                connection.setDoOutput(true);
                connection.setDoInput(true);
                if (connection instanceof HttpURLConnection) {
                    ((HttpURLConnection) connection)
                            .setRequestMethod(requestMethod);
                    if (DEBUG)
                        System.out.println("Request method :" + requestMethod);
                    
                } else {
                    if (DEBUG)
                        System.out.println("No HttpURLConnection.");
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
                    responseMessage = new String("Cannot connect.");
                    fireStateChanged(null);
                    running = false;
                    //break;
                    throw new IOException("Could not connect!");
                } else {
                    continue;
                }
            }
            connected = true;
            logger.info("Connected.");
            fireConnected();
            return true;
        } while (running);
        return false;
    }
    
    private URL createURL(String sumDigest,String chsumHex,boolean partioned,int part,int parts, Long uploadId) throws MalformedURLException{
    	URL streamUrl = currentStream.getUrl();
    	String urlStr = streamUrl.toExternalForm();
    	if(sumDigest !=null || partioned){
        String q = streamUrl.getQuery();
        if (q == null) {
            urlStr = urlStr.concat("?");
        }else{
            urlStr = urlStr.concat("&");
        }
    	}
    	if(sumDigest!=null){
        urlStr = urlStr.concat(CHECK_SUM_TYPE_KEY+"="+sumDigest+"&"+CHECK_SUM_KEY+"=" + chsumHex);
    	}
    	if(partioned){
    		if(!(urlStr.endsWith("?") || urlStr.endsWith("&"))){
    			urlStr = urlStr.concat("&");
    		}
        urlStr = urlStr.concat(PARTITIONS_COUNT_KEY+"=" + parts + "&"+PARTITION_NUM_KEY+"=" + part);
    	}
    	if(uploadId!=null){
    		if(!(urlStr.endsWith("?") || urlStr.endsWith("&"))){
    			urlStr = urlStr.concat("&");
    		}
        urlStr = urlStr.concat(UPLOAD_ID_KEY+"="+uploadId.toString());
    	}
        URL url=null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            logger.severe("Cannot create URL for upload: "
                    + e.getMessage());
            currentStream.setStatus(Upload.CANCEL);
            throw e;
        }
        return url;
    }
    

    private void upload() throws Exception {
        //URL streamUrl = currentStream.getUrl();
        URL url=null;
        long contentLength = currentStream.getLength();
        
        //long partToUploadLength = contentLength;
        int partUploadLength;
        ArrayList<Integer> partitionLengths=new ArrayList<Integer>();
       
        long partsLength=0;
        if(RANDOMIZE_PART_SIZE){
        	
        	if(contentLength<=(long)limit){
        		// do not split
        		partitionLengths.add((int)contentLength);
        	}else{
        		
        		double randomIntervall=(double)(limit-DEFAULT_SPLIT_MIN_LIMIT);
        		
        		int pL;
        		while(partsLength<contentLength){
        			long toDivideLength=contentLength-partsLength;
        			if(toDivideLength<=(long)limit){
        				pL=(int)toDivideLength;
        			}else{
        				pL=MINIMUM_LIMIT+(int)(Math.random()*randomIntervall);
        			}
        			partitionLengths.add(pL);
    				partsLength+=pL;
        		}
        	}
        }else{
//        	parts = (int) (contentLength / (long) limit);
//        	if (contentLength % (long) limit > 0) {
//        		parts++;
//        	}
        	while(partsLength<contentLength){
        		long pL=contentLength-partsLength;
        		if(pL>limit){
        			pL=limit;
        		}
        		partitionLengths.add((int)pL);
        		partsLength+=pL;
        	}
        	
        }
        int parts=partitionLengths.size();
        String sumDigest=currentStream.getChecksumDigest();
        String chsumHex=null;
        byte[] chsum=currentStream.getChecksum();
        if(sumDigest!=null && chsum!=null){
        	chsumHex=RadixConverters.bytesToHex(chsum);
        }
        try {
            inputStream = currentStream.getInputStream();
        } catch (UploadException e) {
            // this means the file could not be opened
            logger.severe("Cannot get input stream: " + e.getMessage());
            currentStream.setStatus(Upload.CANCEL);
           throw e;
        }
        currentStream.setStatus(Upload.UPLOADING);
        logger.info("Stream set to uploading state");
        fireStateChanged(currentStream);
        //while (partToUploadLength > 0) {
        for(int part=0;part<parts;part++){	
//            if (partToUploadLength > (long) limit) {
//                partUploadLength = limit;
//            } else {
//                partUploadLength = (int) partToUploadLength;
//            }
        	partUploadLength=partitionLengths.get(part);
            if (parts > 1) {
                
                logger.info("Partition: "+part);
            }
            url=createURL(sumDigest, chsumHex,(parts>1), part, parts,currentStream.getId());
            logger.info("URL created: "+url);
           
           if(!tryConnect(url, partUploadLength)){
        	   throw new UploadException("Uploading interrupted");
           }
               
//            startConnect = System.currentTimeMillis();
            logger.info("starting upload");
//            synced = false;

            
            
            try {
                uploadPart(partUploadLength);
            } catch (Exception e) {
                logger
                        .severe("Cannot upload part of upload: "
                                + e.getMessage());
                if (inputStream != null)
                    inputStream.close();
                throw e;
            }
            
            // check bandwidth limitation
            long totalTimeMs=System.currentTimeMillis()-totalStartTimeMs;
            if(totalTimeMs==0)totalTimeMs=1;
            double totalTimeSeconds=(double)totalTimeMs/1000;
            if(totalTimeSeconds >0.0){
            	double byteRate=(double)totalUploadedBytes/totalTimeSeconds;
            	if(transferRateLimit!=UNLIMITED && byteRate>transferRateLimit){
            		double timeToWaitSeconds=(totalUploadedBytes-(transferRateLimit*totalTimeSeconds))/transferRateLimit;
            		logger.info("Limiting transfer rate: actual: "+byteRate+" quota: "+transferRateLimit+" (bytes per second).");
            		logger.info("Waiting "+timeToWaitSeconds+" s");
            		Thread.sleep((int)(timeToWaitSeconds*1000.0));
            	}
            }
            
            //part++;
            //partToUploadLength-=partUploadLength;
        }
        try {
            if (inputStream != null)
                inputStream.close();
        } catch (IOException e) {
            logger.severe("Cannot close input stream: " + e.getMessage());
            currentStream.setStatus(Upload.DROPPED);
            throw e;
        }

    }

    private void uploadPart(int length) throws Exception {
    	
    	
        try {
            outputStream = connection.getOutputStream();
            
            int read = 0;
//            guessedToUploadLength = toUploadLength;

            int toRead = length;
            //boolean close = false;
            try {
                while (read >= 0 && toRead > 0) {
                    int bufToRead = buffer.length;
                    if (bufToRead > toRead)
                        bufToRead = toRead;
                    read = inputStream.read(buffer, 0, bufToRead);
                    if (read > 0) {
                        toRead -= read;
                        outputStream.write(buffer, 0, read);
                        totalUploadedBytes+=read;
                    } 
                    try{
                    	// TODO legacy sleep to avoid recording drop outs during upload. Still necessary ?
                    	// Thread.yield should be sufficient 
                    	Thread.sleep(2);
                    }catch(InterruptedException ie) {
                    	// TODO cancel upload of part here ?
                        // no problem
                    }
                    if (DEBUG_DELAY > 0) {
                        try {
                            Thread.sleep(DEBUG_DELAY);
                        } catch (InterruptedException e1) {
                            // no problem
                        }
                    }
                }
            } catch (IOException e) {
                logger.severe("I/O error)!: " + e.getMessage());
                if (inputStream != null) {
                    inputStream.close();
                }
                throw e;
            }finally{
                try {
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException e1) {
                    logger.severe("Close error: " + e1.getMessage());
                    throw e1;
                }
            }
            
            InputStream sis=null;
            try{
            	sis=connection.getInputStream();
            }catch(IOException ioe){
            	logger.severe("Could not get server response stream");
            	throw ioe;
            }
            
            InputStreamReader sisr=new InputStreamReader(sis);
            LineNumberReader slnr=new LineNumberReader(sisr);
            String result=slnr.readLine();
            if(result==null){
            	logger.severe("No response string from server!");
            	throw new IOException("No OK from storage server !");
            }else if(!result.equals(STATUS_OK)){
            	logger.severe("No response string from server! (response follows:)");
            	logger.severe("Response: "+result);
        		String nextLine;
        		while((nextLine=slnr.readLine())!=null){
        			logger.severe("Response: "+nextLine);
        		}
        		throw new IOException("No OK from storage server !");
            }
           
            String uploadIDStr=slnr.readLine();
            if(uploadIDStr!=null){
            	
            	try{
            		Long uploadID=null;
            	uploadID=Long.parseLong(uploadIDStr);
            	currentStream.setId(uploadID);
            	}catch(NumberFormatException nfe){
            		String msg=new String("Could not parse upload ID: "+uploadIDStr);
            		logger.severe(msg);
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
                    if(responseCode==HttpURLConnection.HTTP_ENTITY_TOO_LARGE){
                    	// try again with half size
                    	
                    	int newLimit=this.limit/2;
                    	if(newLimit>=MINIMUM_LIMIT){
                    		this.limit=newLimit;
                    		logger.info("Upload size limit cutted in half; new limit: "+this.limit);
                    	}
                    }
                    throw new IOException("Part upload failed");
                } else {

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
            logger.severe("Cannot upload !: " + e.getMessage());
            throw e;

        } finally {
            connected = false;
            fireDisconnected();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
    	totalUploadedBytes=0;
    	totalStartTimeMs=System.currentTimeMillis();
        do {
            int idleCount = 0;

            getNextUpload();
            while (currentUpload == null && running) {
            	fireFinished();
            	// wait for something to do
                try {
                    Thread.sleep(ON_IDLE_DELAY);
                } catch (InterruptedException e1) {
                }
                idleCount++;
                // idle = true;
                getNextUpload();
             // reset byte rate measuring
            	totalUploadedBytes=0;
            	totalStartTimeMs=System.currentTimeMillis();
            }
            if(currentUpload!=null && currentStream!=null){
            	synced=false;
            	startConnect = System.currentTimeMillis();
            	try {
            		upload();
            	} catch (Exception e) {
            		calculateLength();
            		synced = true;
            		logger.warning("Exception during upload: "+e.getMessage());
            		//	            StackTraceElement[] st=e.getStackTrace();
            		//	            if(st.length>0){
            		//	            	logger.warning("at "+st[0].getClassName()+" ("+st[0].getLineNumber()+")");
            		//	            }
            		logger.warning("Upload failed: "+currentStream);
            		// first drop the upload in this session
            		currentStream.incFailedAttemptsCounter();

            		if (currentStream.getFailedUploadAttempts() >= MAX_UPLOAD_ATTEMPTS) {
            			currentStream.setStatus(Upload.DROPPED);
            			logger.warning("Dropped upload " + currentStream
            					+ "\nafter " + MAX_UPLOAD_ATTEMPTS + " attempts !");

            		} else {
            			currentStream.setStatus(Upload.FAILED);
            		}
            		fireStateChanged(currentStream);
            		if(uploadRetryCount>UPLOAD_RETRIES){
            			responseMessage="Exception: "+e.getMessage();
            			fireStateChanged(null);
            			running=false;
            			logger.warning(uploadRetryCount+" failed uploads. Uploadcache will be terminated!");
            			break;
            		}
            		try {
            			logger.info("Upload cache pauses for "+UPLOAD_RETRY_DELAY+" ms.");
            			Thread.sleep(UPLOAD_RETRY_DELAY);
            		} catch (InterruptedException e1) {
            			// Nothing to do
            		}
            		uploadRetryCount++;


            		continue;
            	} 
            	// Success:
            	uploadRetryCount=0;
            	currentStreamIndex++;
            	if (currentStreamIndex == currentUpload.length) {
            		currentStreamIndex = 0;
            		currentUpload = null;
            	}
            	connectedTimeInMillis += System.currentTimeMillis() - startConnect;
            	totalUploadLength += currentStream.getLength();
            	logger.info("Uploaded: " + currentStream);
            	currentStream.setStatus(Upload.DONE);
            	calculateLength();
            	synced=true;
            	fireStateChanged(currentStream);
            }
        } while (running);
        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).disconnect();
        }
        idle = true;
    }

    /**
     * @return true if cookies accepted
     */
    public boolean isAcceptCookies() {
        return acceptCookies;
    }

    /**
     * @param b true to accept cookies
     */
    public void setAcceptCookies(boolean b) {
        acceptCookies = b;
    }

}
