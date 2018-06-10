//    Speechrecorder
//    (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Jul 19, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.storage;

import ipsk.apps.speechrecorder.MetaData;
import ipsk.apps.speechrecorder.UIResources;
import ipsk.apps.speechrecorder.storage.net.HTTPStorageProtocol;
import ipsk.audio.capture.session.info.RecordingSession;
import ipsk.audio.utils.AudioFormatUtils;
import ipsk.io.FileUtils;
import ipsk.net.Upload;
import ipsk.net.UploadCache;
import ipsk.net.UploadCacheListener;
import ipsk.net.UploadFile;
import ipsk.net.event.UploadConnectionEvent;
import ipsk.net.event.UploadConnectionEvent.ConnectionState;
import ipsk.net.event.UploadEvent;
import ipsk.text.NaturalNumberFormat;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * The storage manager takes care about filenames for audio, log and label files.
 * In standalone mode it generates ordinary filenames.
 * In web recording mode temporary files are used and pushed as {@link ipsk.net.Upload} to an {@link ipsk.net.UploadCache}.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class StorageManager implements UploadCacheListener {
    
    public enum Status{CLOSED,OPEN}

	private static final int DEF_SESSION_ID_FORMAT_DIGIT_COUNT = 4;
    
    public static String DEF_SESSION_ID_FORMAT="0000";
    public static String DEF_SPEAKER_ID_FORMAT="0000";
    public static String DEF_SCRIPT_ID_FORMAT="0000";
    public static String DEF_RECVERSION_FORMAT="_00";
    
    public static String XML_FILENAME_EXTENSION="xml";
    
    public static String DEF_ANNOTATION_FILE_SUFFIX="_anno";
    
    private class PrefixNameFilter implements FilenameFilter {
        private File dir;

        private String prefix;

        public PrefixNameFilter(File dir, String prefix) {
            this.dir = dir;
            this.prefix = prefix;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File arg0, String arg1) {
            //System.out.println("Check: Dir: "+arg0+" String: "+arg1);
            if (arg0.equals(dir) && arg1.startsWith(prefix))
                return true;
            return false;
        }
    }
    
//    private class RecversionsFilter implements FilenameFilter {
//        private File dir;
//
//        private String body;
//        private String extension;
//
//        public RecversionsFilter(File dir, String body,String extension) {
//            this.dir = dir;
//            this.body=body;
//        }
//
//        /*
//         * (non-Javadoc)
//         * 
//         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
//         */
//        public boolean accept(File arg0, String arg1) {
//            //System.out.println("Check: Dir: "+arg0+" String: "+arg1);
//            String dotAndExtension="."+extension;
//            if (arg0.equals(dir) && arg1.startsWith(body) && arg1.endsWith(dotAndExtension)){
//                String versionAndExtension=arg1.substring(body.length());
//                String versionPortion=versionAndExtension.substring(0,versionAndExtension.length()-dotAndExtension.length());
//                try {
//                    Integer version=(Integer)recVersionFormat.parse(versionPortion);
//                } catch (ParseException e) {
//                   return false;
//                }
//                return true;
//            }
//                return false;
//           
//        }
//    }
    
	private UploadCache uploadCache;
	private URL storageURL = null;
	private int numLines = 1;								// number of audio lines (not channels)
	private int sessionID;
	private String scriptID;
	
	private MetaData metadata = new MetaData();
	private String promptCode = "";
	private String speakerCode = null;
    private int recVersion;
    private boolean overwrite;
	private String labelFileExtension = "txt";
	private String audioFileExtension = "wav";
	private String logFileExtension = "log";
	private String logFileSuffix = "_log";
	private String timeLogFileSuffix = "_timelog";
	private boolean useSessionID = true;
	private boolean useScriptID = true;
	private boolean createSpeakerDir = true;
	private boolean createScriptDir = true;
	//private URL[] audioFiles = null;
	//private URL labelFile = null;
	private DecimalFormat speakerIDFormat;
	private NaturalNumberFormat sessionIDFormat;
	private DecimalFormat scriptIDFormat;
    private DecimalFormat recVersionFormat;

	private boolean useAsCache = false;
	private String tmpDirName;
    private String tmpFilePrefix;
	//private File tmpDir;
	private String cacheTmpFilePrefix = getClass().getName() + "_cache_";
	private String convertedTmpFilePrefix = getClass().getName() + "_upload_";
	private String cacheMapFilename;
    private String cacheLockFilename;
    private File cacheLockFile;
    private String lockFileExtension = "lck";
    private FileLock lock;
    private FileOutputStream cacheLockFos;
	private AudioFileFormat.Type uploadType = null;
	private AudioFileFormat.Type storageType;
	//private Properties cacheMap = new Properties();
	private File cacheMapFile;
     private String mapFileExtension = "map";
	private Vector<UploadFile[]> cache;
	private Logger logger;
    //private ResourceBundle rb;
    private File tmpDir;
    private UIResources uiString;

    private File[] currentItemRecordingFiles;
   


	private Status status=Status.CLOSED;
    
	/**
	 * Create new storage manager.
	 */
	public StorageManager() {
		super();
		String packageName=getClass().getPackage().getName();
		logger=Logger.getLogger(packageName);
		logger.setLevel(Level.INFO);
//		sessionIDFormat = new DecimalFormat(DEF_SESSION_ID_FORMAT);
		sessionIDFormat=new NaturalNumberFormat(DEF_SESSION_ID_FORMAT_DIGIT_COUNT);
		speakerIDFormat = new DecimalFormat(DEF_SESSION_ID_FORMAT);
		scriptIDFormat = new DecimalFormat(DEF_SCRIPT_ID_FORMAT);
		recVersionFormat=new DecimalFormat(DEF_RECVERSION_FORMAT);
		storageType = AudioFileFormat.Type.WAVE;
		uploadType = storageType;
        tmpDirName = System.getProperty("java.io.tmpdir");
        if (!tmpDirName.endsWith(File.separator)) {
            tmpDirName = tmpDirName.concat(File.separator);
        }
        tmpDir = new File(tmpDirName);
        tmpFilePrefix = getClass().getName();
        uiString=UIResources.getInstance();
        logger.info("Storage manager created");
        
	}
    
    
     // Due to some JAVA bugs (Id: 4171239) (deleteOnExit() does not work on
    // Win32 if files rare not closed.
    // there may remain some abandoned files in the temporary directory.
    // This happens also if the StorageManger.close() method was not called
    // Maybe we should use Runtime.addShutDownHook in SpeechREcorder to avoid
    // this.
    protected void deleteAllTmpDirFiles() {

        File[] tmpFiles = tmpDir.listFiles(new PrefixNameFilter(tmpDir,
                tmpFilePrefix));
        if (tmpFiles == null) {
            //System.out.println("temp file list is null");
            return;
        }
        for (int i = 0; i < tmpFiles.length; i++) {
            tmpFiles[i].delete();
        }

    }
    
     /**
     * Open the storage manager. Reads local cache map and puts cached files to
     * the upload engine (again). The force flag controls the behavior on
     * broken cache map files or on missing files to upload. If the flag is set
     * force removes unreadable cache map files, ignores missing upload files
     * and removes all files not found in the map. This holds the temporary directory
     * clean. If not set an exception is thrown if an invalid condition in the
     * cache is found.
     * 
     * @param force
     *            force repairing inconsistent cache states
     * @throws StorageManagerException
     *             if an inconsistent cache is found
     */
    public void open(boolean force) throws StorageManagerException {
        if (uploadType == null) {
            uploadType = storageType;
        }
        audioFileExtension = storageType.getExtension();

        if (useAsCache) {
            // lock cache
            cacheLockFilename = tmpDirName + tmpFilePrefix + "_cache."
                    + lockFileExtension;
            cacheLockFile = new File(cacheLockFilename);
            try {
                cacheLockFos = new FileOutputStream(cacheLockFile);
                FileChannel ch = cacheLockFos.getChannel();
                lock = ch.tryLock();
            } catch (FileNotFoundException e3) {
                throw new StorageManagerException(
                        "Error locking storage cache !");
            } catch (IOException e) {
                throw new StorageManagerException(
                        "Error locking storage cache !");
            } finally {
                //                if (cacheLockFos!=null)
                //                    try {
                //                        cacheLockFos.close();
                //                    } catch (IOException e1) {
                //                       // we already throwing an exception
                //                    }
            }

            if (lock == null) {
                logger.warning("Cache is locked !");
                throw new StorageManagerException(uiString.getString("CacheAlreadyInUse"));
            }
            
            // load pending audio files
            cacheMapFilename = tmpDirName + tmpFilePrefix + "_cache."
                    + mapFileExtension;
            cacheMapFile = new File(cacheMapFilename);
            if (cacheMapFile.exists()) {
                FileInputStream fis = null;
                ObjectInputStream ois = null;
                try {
                    fis = new FileInputStream(cacheMapFile);
                    ois = new ObjectInputStream(fis);
                    cache = (Vector<UploadFile[]>) ois.readObject();
                    logger.info("Read existing cache map "+cacheMapFilename);
                } catch (Exception e) {
                	logger.severe("Cannot read cache map!"
                            + e.getMessage());
                    if (force) {
                        try {
                            if (ois!=null)ois.close();
                        } catch (IOException e1) {

                        } finally {
                            try {
                                if (fis!=null)fis.close();
                            } catch (IOException e2) {

                            }
                            //                           Remove this corrupted map !
                            cacheMapFile.delete();
                            logger.warning("Removed unreadable cache map!");
                            cache = new Vector<UploadFile[]>();
                        }

                    } else {
                        
                        throw new StorageManagerException(
                                "Cannot read cache map!", e);
                    }

                } finally {

                    try {
                        if (ois != null)
                            ois.close();

                    } catch (IOException e1) {
                        logger
                                .severe("Could not close object stream of cache map !"
                                        + e1.getMessage());
                        throw new StorageManagerException(
                                "Could not close object stream of cache map.",
                                e1);
                    } finally {
                        if (fis != null)
                            try {
                                fis.close();
                            } catch (IOException e2) {
                                logger.severe("Could not close !"
                                        + e2.getMessage());
                                throw new StorageManagerException(
                                        "Could not close.", e2);
                            }
                    }

                }

                // Put pending files to the uploadcache (again)
                for (int i = 0; i < cache.size(); i++) {
                    UploadAudioFile[] upload = (UploadAudioFile[]) cache.get(i);
                    
                    try {
                        upload(upload);
                    } catch (StorageManagerException e) {
                        logger.severe("Storage exception !" + e);
                        if (force) {
                            //System.out.println("Exception, remove and
                            // continue: "+upload);

                            cache.remove(i);
                            logger
                                    .warning("Removed upload from cache on exception.");
                            continue;
                        } else {
                            //System.out.println("Exception throwing:
                            // "+upload);
                            throw e;
                        }
                    }
                }

            } else {
                //if (force)deleteAllTmpDirFiles();
                cache = new Vector<UploadFile[]>();
            }

        }
        status=Status.OPEN;
        logger.info("Storage manager opened.");
    }

    public void close() throws StorageManagerException {
        close(false);
    }

    public synchronized void close(boolean forceDelete)
            throws StorageManagerException {
    	if(status.equals(Status.CLOSED))return;
        if (useAsCache) {
            // Remove already uploaded items
            int size = cache.size();
            for (int i = size - 1; i >= 0; i--) {

                UploadAudioFile[] uploads = (UploadAudioFile[]) cache.get(i);
                boolean pungeOut = true;
                for (int j = 0; j < uploads.length; j++) {
                    int uploadStatus = uploads[j].getStatus();
                    if (uploadStatus != UploadAudioFile.DONE
                            && uploadStatus != UploadAudioFile.CANCEL) {
                        pungeOut = false;
                        logger.info("Hold in cache for next session: "+uploads[j]+" Status: "+uploads[j].getStatus());
                        break;
                    }
                }
                if (pungeOut) {
                    for (int j = 0; j < uploads.length; j++) {
                    	UploadAudioFile uAf=uploads[j];
                        File audioFile = uAf.getAudioCacheFile();
                        //audioFile.deleteOnExit();
                        //System.out.println("Try remove " + audioFile);
                        if(!audioFile.exists()){
                        	logger.warning("Cached audio file " + audioFile+ " does not exist!");
                        }else{
                        	
                        	if (!audioFile.delete()) {
                        		logger.severe("Cannot remove cached audio file " + audioFile);
                        		
                        		System.err.println("Cannot remove " + audioFile);
                        		// try to delete on exit
                        		audioFile.deleteOnExit();
                        	}
                        }

                    }
                    cache.remove(i);
                    
                }
            }
            size = cache.size();
            if (size > 0) {
                // store cache content
                FileOutputStream fos = null;
                ObjectOutputStream oos = null;
                try {
                    fos = new FileOutputStream(cacheMapFile);

                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(cache);

                } catch (Exception e) {
                    throw new StorageManagerException(e);

                } finally {
                    try {
                        if (oos != null)
                            oos.close();
                    } catch (IOException e1) {
                        logger.severe("Error closing object stream: "
                                + e1.getMessage());
                        throw new StorageManagerException(e1);
                    } finally {
                        if (fos != null)
                            try {
                                fos.close();
                            } catch (IOException e2) {
                                throw new StorageManagerException(e2);
                            }
                    }
                }
            } else {
                cacheMapFile.delete();
                if (forceDelete)
                    deleteAllTmpDirFiles();

            }
            try {
            	try{
            	if(lock!=null){
            		lock.release();
            	}
            	}catch(IOException ioe){
            		 throw new StorageManagerException("Cannot unlock storage cache: "+ioe.getLocalizedMessage());
            	}finally{
                if (cacheLockFos != null)
                    cacheLockFos.close();
            	}
            } catch (IOException e) {
                throw new StorageManagerException("Cannot close lock stream !");
            } finally {
                if (cacheLockFile != null)
                	cacheLockFile.deleteOnExit();
                    cacheLockFile.delete();
            }

        }
        status=Status.CLOSED;
    }
    
    public void open() throws StorageManagerException{
        open(false);
    }
    
//	public void open() throws StorageManagerException {
//		if (uploadType == null) {
//			uploadType = storageType;
//		}
//		audioFileExtension = storageType.getExtension();
//		if (useAsCache) {
//			// load pending audio files
//			cacheMapFilename =
//				System.getProperty("java.io.tmpdir")+File.separator
//					+ getClass().getName()
//					+ "_cache.map";
//			cacheMapFile = new File(cacheMapFilename);
//			if (cacheMapFile.exists()) {
//				FileInputStream fis;
//				try {
//					fis = new FileInputStream(cacheMapFile);
//
//					ObjectInputStream ois = new ObjectInputStream(fis);
//					cache = (Vector) ois.readObject();
//					ois.close();
//					fis.close();
//				} catch (Exception e) {
//					throw new StorageManagerException(e);
//
//				}
//				// Put pending files to the uploadcache (again)
//				for (int i = 0; i < cache.size(); i++) {
//					UploadAudioFile[] upload = (UploadAudioFile[]) cache.get(i);
//					upload(upload);
//				}
//
//			} else {
//				cache = new Vector();
//			}
//
//		}
//	}

//	public synchronized void close() throws StorageManagerException {
//		if (useAsCache) {
//			// Remove already uploaded items
//			int size = cache.size();
//			for (int i = size - 1; i >= 0; i--) {
//
//				UploadAudioFile[] uploads = (UploadAudioFile[]) cache.get(i);
//				boolean pungeOut = true;
//				for (int j = 0; j < uploads.length; j++) {
//					int uploadStatus = uploads[j].getStatus();
//					if (uploadStatus != UploadAudioFile.DONE
//						&& uploadStatus != UploadAudioFile.CANCEL) {
//						pungeOut = false;
//						break;
//					}
//				}
//				if (pungeOut) {
//					for (int j = 0; j < uploads.length; j++) {
//						File audioFile = uploads[j].getAudioCacheFile();
//						//audioFile.deleteOnExit();
//						//System.out.println("Try remove " + audioFile);
//						if (!audioFile.delete()) {
//							System.err.println("Cannot remove " + audioFile);
//							// try to delete on exit
//							audioFile.deleteOnExit();
//						}
//
//					}
//					cache.remove(i);
//				}
//			}
//			size = cache.size();
//			if (size > 0) {
//				// store cache content 
//				FileOutputStream fos;
//				try {
//					fos = new FileOutputStream(cacheMapFile);
//
//					ObjectOutputStream oos = new ObjectOutputStream(fos);
//					oos.writeObject(cache);
//					oos.close();
//					fos.close();
//				} catch (Exception e) {
//					throw new StorageManagerException(e);
//
//				}
//			} else {
//				cacheMapFile.delete();
//			}
//
//		}
//	}

	/**
	 * set the upload cache thread for the StorageManager.
	 * @param uploadCache
	 */
	public void setUploadCache(UploadCache uploadCache) {
		this.uploadCache = uploadCache;
		if (uploadCache != null) {
			uploadCache.addUploadCacheListener(this);
		}
	}

	//	public Upload[] getUploads(){
	//		 tmpDir=new File(tmpDirName);
	//		File[] cachedFiles=tmpDir.listFiles(new CacheFilenameFilter());
	//		int num=cachedFiles.length;
	//		Upload[] uploads=new Upload[num];
	//		for(int i=0;i<num;i++){
	//			URL url=
	//		}
	//	}

	/**
	 * starts uploading of the collected data.
	 * @throws StorageManagerException
	 */
	public void upload() throws StorageManagerException
	{
		if (useAsCache)
		{
			try
			{
				upload(getUpload());
			}
			catch (IOException ex)
			{
				String msg = "IOException: " + ex.getLocalizedMessage();
				logger.severe(msg); throw new StorageManagerException(msg, ex);
			}
		}
	}

	/**
	 * @param uploadFiles
	 * @throws StorageManagerException
	 */
	private void upload(UploadAudioFile[] uploadFiles) throws StorageManagerException
	{
		try
		{
			URL[] urls = getConvertedAudioFiles();
			for (int line = 0; line < numLines; line++)
			{
				try
				{
					File audioFile = uploadFiles[line].getAudioCacheFile();
					//if (uploadType != storageType) {
					File compressedFile = File.createTempFile(convertedTmpFilePrefix,
							"." + uploadType.getExtension());
					compressedFile.deleteOnExit();
					AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
					//FileOutputStream o=new FileOutputStream(compressedFile);
					try
					{
						AudioSystem.write(ais, uploadType, compressedFile);
						ais.close();
					}
					catch (IllegalArgumentException ex)
					{
						logger.warning(ex.getLocalizedMessage() + " ais: " + ais);
					}
					uploadFiles[line].setFile(compressedFile);
					uploadFiles[line].setMimeType(AudioFormatUtils.getMimetype(uploadType));
					if (uploadFiles[line].getUrl() == null)
						uploadFiles[line].setUrl(urls[line]);
					//					} else {
					//						upload[i].setFile(audioFile);
					//					}
				}
				catch (Exception ex)
				{
					String msg = "Exception: " + ex.getLocalizedMessage();
					logger.severe(msg); throw new StorageManagerException(msg, ex);
				}
			}
			//			// We need a copy of the cacheentrys (without this overwriting does not
			// work correctly)
			//			UploadAudioFile[] toUpload = new UploadAudioFile[numLines];
			//			for (int i = 0; i < toUpload.length; i++) {
			//				toUpload[i] = (UploadAudioFile) upload[i].clone();
			//			}
			//			uploadCache.upload(toUpload);
			uploadCache.upload(uploadFiles);
		}
		catch (Exception ex)
		{
			String msg = "Exception: " + ex.getLocalizedMessage();
			logger.severe(msg); throw new StorageManagerException(msg, ex);
		}		
	}

	/**
	 * @throws StorageManagerException
	 */
	public void uploadAnnotation() throws StorageManagerException{
		try{
			URL url=generateAnnotationFileURL();
			uploadCache.upload(getAnnotationUpload(new URL[]{url}));
		}catch (Exception ex){
			String msg = "Exception: " + ex.getLocalizedMessage();
			logger.severe(msg); throw new StorageManagerException(msg, ex);
		}		
	}
	
	private UploadAudioFile[] getUpload() throws IOException, StorageManagerException {
		return getUpload(generateAudioFileURLs(), true);
	}

	private UploadAudioFile[] getUpload(URL[] urls) throws IOException{
		return getUpload(urls, false);
	}
	
	private UploadAudioFile[] getUpload(URL[] urls, boolean create) throws IOException
	{
		//URL[] urls = getAudioFiles();
		UploadAudioFile[] cacheEntryAudio = null;

		// Search backwards to find actual recordings first
		for (int i = cache.size() - 1; i >= 0; i--)
		{
			UploadFile[] cacheEntry = cache.get(i);
			if(cacheEntry instanceof UploadAudioFile[]){
				cacheEntryAudio=(UploadAudioFile[])cacheEntry;
			boolean match = true;
			for (int j = 0; j < urls.length; j++)
			{
				if (!cacheEntry[j].getUrl().sameFile(urls[j]))
				{
					match = false;
					break;
				}
			}
			if (match)
			{
				return cacheEntryAudio;
			}
			}
		}
		return null;
	}
	
	private UploadFile[] getAnnotationUpload(URL[] urls) throws IOException
	{
		
		UploadFile[] cacheEntry = null;

		// Search backwards to find actual annotation first
		for (int i = cache.size() - 1; i >= 0; i--)
		{
			cacheEntry = cache.get(i);
			
			boolean match = true;
			for (int j = 0; j < urls.length; j++)
			{
				if (!cacheEntry[j].getUrl().sameFile(urls[j]))
				{
					match = false;
					break;
				}
			}
			if (match)
			{
				return cacheEntry;
			}
		}
		return null;
	}

	/**
	 * get a stream for 
	 * @return array of input streams, one for each audio line
	 * @throws StorageManagerException
	 */
	public InputStream[] getCachedInputStreams() throws StorageManagerException
	{
		InputStream[] iss = new InputStream[numLines];
		int line;
		try
		{
			if (useAsCache)
			{
				//System.out.println("get cached streams for version: "+recVersion);
				File[] files = getRecordingFiles();
				for (line = 0; line < numLines; line++)
					iss[line] = new FileInputStream(files[line]);
			}
			else
			{
				URL[] urls = generateAudioFileURLs();
				for (line = 0; line < numLines; line++)
				{
					iss[line] = urls[line].openStream();
				}
			}
		}
		catch (IOException ex)
		{
			String msg = "IOException: " + ex.getLocalizedMessage();
			try
			{
				for (line = 0; line < numLines; line++)
				{	
					iss[line].close(); 
				}
			}
			catch (IOException closeex)
			{ msg += " " + closeex.getLocalizedMessage(); }
			logger.severe(msg); throw new StorageManagerException(msg, ex);
		}
		return iss;
	}

	/**
     * Get cached storage URLs 
     * @return array of URLs, one for each audio line
     * @throws StorageManagerException
     */
    public URL[] getCachedInputURLs() throws StorageManagerException {
        URL[] lineURLs = new URL[numLines];
        int line;

        if (useAsCache) {
            File[] files = getRecordingFiles();
            for (line = 0; line < numLines; line++)
                try {
//                    lineURLs[line] = files[line].toURL();
                    lineURLs[line]=files[line].toURI().toURL();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    logger.severe(e.getMessage());
                    throw new StorageManagerException("File to URL: "
                            + e.getMessage(), e);
                }
        } else {
            URL[] urls = generateAudioFileURLs();
            for (line = 0; line < numLines; line++) {
                lineURLs[line] = urls[line];
            }
        }

        return lineURLs;
    }
    
    /**
     * Get cached storage files 
     * @return array of files, one for each audio line
     * @throws StorageManagerException
     */
    public File[] getCachedInputFiles() throws StorageManagerException {
        
        int line;

        if (useAsCache) {
            return getRecordingFiles();
           
        } else {
            URL[] urls = generateAudioFileURLs();
            File[] files = new File[numLines];
            for (line = 0; line < numLines; line++) {
                URL lineURL=urls[line];
                if(lineURL.getProtocol().equalsIgnoreCase("file")){
                    try {
						files[line]=new File(lineURL.toURI().getPath());
					} catch (URISyntaxException e) {
						throw new StorageManagerException("Cannot convert URL/URI to file path");
					}
                }else{
                    throw new StorageManagerException("Cannot convert URL to file path");
                }
            }
            return files;
        }
    }
//    /**
//     * @return a File for each audio line
//     * @throws StorageManagerException
//     */
//    public File[] getNewRecordingFiles(String promptCode) throws StorageManagerException{
//        
//    }
    
	/**
	 * @return a File for each audio line
	 * @throws StorageManagerException
	 */
	public File[] getNewRecordingFiles() throws StorageManagerException
	{
		currentItemRecordingFiles = new File[numLines];
		try
		{
			if (useAsCache)					// Online-Mode
			{
				//				Create new cache entry
				//System.out.println("Create new URLs for version: "+recVersion);
				URL[] urls = generateAudioFileURLs(promptCode,recVersion+1);
				UploadAudioFile[] cacheEntry;
				cacheEntry = new UploadAudioFile[urls.length];
				for (int j = 0; j < urls.length; j++)
				{
					File tmpAudioFile;
					try
					{
						tmpAudioFile = File.createTempFile(cacheTmpFilePrefix, "."
								+ audioFileExtension);
					}
					catch (IOException e)
					{
						throw new StorageManagerException(e);
					}
					
					cacheEntry[j] = new UploadAudioFile(null, tmpAudioFile, urls[j]);
				}
				
				cache.add(cacheEntry);
					
	
				for (int i = 0; i < cacheEntry.length; i++)
				{
					currentItemRecordingFiles[i] = cacheEntry[i].getAudioCacheFile();
				}
			}
			else									// Offline-Mode
			{
				URL[] urls = generateAudioFileURLs(promptCode,recVersion+1);
	
				for (int i = 0; i < numLines; i++)
				{
					if (!urls[i].getProtocol().equals("file"))
						throw new StorageManagerException("URL protocol is not \"file\"");
					
					currentItemRecordingFiles[i] = new File(urls[i].toURI().getPath());
				}
			}
		}
		catch (Exception ex)
		{
			String msg = "Exception: " + ex.getLocalizedMessage();
			logger.severe(msg); throw new StorageManagerException(msg, ex);
		}
		return currentItemRecordingFiles;
	}
	
	
	 public File[] getCurrentItemRecordingFiles() {
			return currentItemRecordingFiles;
	}
	
	
	public static File fileURLToFile(URL url) throws StorageManagerException{

	    if (!url.getProtocol().equals("file")){
	        throw new StorageManagerException("URL protocol is not \"file\"");
	    }
	    try {
	        URI uri=url.toURI();
	        return new File(uri.getPath());
	    } catch (URISyntaxException e) {
	        throw new StorageManagerException(e);
	    }
	}
	
	/**
	 * get a list of all available audio data.
	 * @return array of Files, one for each recorded item
	 * @throws StorageManagerException
	 */
	public File[] getRecordingFiles() throws StorageManagerException
	{
		File[] files = new File[numLines];
		try
		{
			if (useAsCache)					// in online-mode just copy the entrys from the upload cache
			{
				UploadAudioFile[] cacheEntry = getUpload();
				for (int i = 0; i < cacheEntry.length; i++)
					files[i] = cacheEntry[i].getAudioCacheFile();
			}										
			else									// in offline-mode, generate the necessary file URLs
			{
				URL[] urls = generateAudioFileURLs();
	
				for (int i = 0; i < numLines; i++)
				{
					if (!urls[i].getProtocol().equals("file"))
						throw new StorageManagerException("URL protocol is not \"file\"");
					files[i] = new File(urls[i].toURI().getPath());
				}
			}	
		}
		catch (Exception ex)
		{
			String msg = "Exception: " + ex.getLocalizedMessage();
			logger.severe(msg); throw new StorageManagerException(msg, ex);
		}
		return files;
	}
	/**
     * @return URL of recording session info file
     * @throws MalformedURLException
     */
    public URL getRecordingSessionInfoFile() throws StorageManagerException {
        URL sessionURL = getSessionURL();
        String recSessInfoFilename=getRecordingSessionInfoFileName();
        sessionURL = addDirToURL(sessionURL, recSessInfoFilename);
        return sessionURL; 
    }
    
	private URL getLabelFile() throws StorageManagerException {
		URL sessionURL = getSessionURL();
		String labelFileName = getLabelFileName();
		sessionURL = addDirToURL(sessionURL, labelFileName);
		return sessionURL; 
	}
	
	/**
	  * generates a dynamic filename for the actual item based on the storage URL.
	  * In online mode, when <code>isUseAsCache()</code> is <b>true</b>, the following
	  * parameters are appended on the <code>storageURL</code> as query string: </br>
	  * <code>promptCode, speakerCode, extension, session, line</code> </br>
	  * In offline mode, promt, speaker code etc. are appended on the storage URL as
	  * directory fragments to build a valid and individual filename.
	  * @return array of URLs, one entry for each audio line
	  * @throws StorageManagerException
		 */
		public URL[] generateAudioFileURLs() throws StorageManagerException
		{
			return generateAudioFileURLs(promptCode,recVersion);
		}
		
	
		/**
	     * Generates dynamic audio filename for the actual item based on the storage URL.
	     * In online mode, when <code>isUseAsCache()</code> is <b>true</b>, the following
	     * parameters are appended on the <code>storageURL</code> as query string: </br>
	     * <code>promptCode, speakerCode, extension, session, line</code> </br>
	     * In offline mode, prompt, speaker code etc. are appended on the storage URL as
	     * directory fragments to build a valid and individual filename.
	     * @param promptCode the item code
	     * @param recVersion of the recording
	     * @return array of URLs, one entry for each audio line
	     * @throws StorageManagerException
	     */
	    public URL[] generateAudioFileURLs(String promptCode,int recVersion) throws StorageManagerException{
	        return generateAudioFileURLs(sessionID,speakerCode, promptCode, recVersion);
	    }
		
	/**
	 * Generates dynamic audio filename for the actual item based on the storage URL.
	 * In online mode, when <code>isUseAsCache()</code> is <b>true</b>, the following
	 * parameters are appended on the <code>storageURL</code> as query string: </br>
	 * <code>promptCode, speakerCode, extension, session, line</code> </br>
	 * In offline mode, prompt, speaker code etc. are appended on the storage URL as
	 * directory fragments to build a valid and individual filename.
	 * @param sessionID session ID
	 * @param speakerCode speaker code
	 * @param promptCode the item code
     * @param recVersion of the recording
	 * @return array of URLs, one entry for each audio line
	 * @throws StorageManagerException
	 */
	public URL[] generateAudioFileURLs(int sessionID,String speakerCode,String promptCode,int recVersion) throws StorageManagerException
	{
		String extension = uploadType.getExtension();
		URL[] audioFileURLs = new URL[numLines];
		URL sessionURL = getSessionURL();
		URL newURL;
		String lineNr;
		String audioFileName;

		try
		{
			for (int line = 0; line < numLines; line++)	// for all audio-channels
			{		
				if (useAsCache)
				{
					newURL = addQueryToURL(storageURL, HTTPStorageProtocol.ITEM_CODE_KEY, promptCode);
					if(speakerCode!=null){
					newURL = addQueryToURL(newURL, HTTPStorageProtocol.SPEAKER_CODE_KEY, speakerCode);
					}
                    newURL = addQueryToURL(newURL,HTTPStorageProtocol.SESSION_ID_KEY,sessionIDFormat.format(sessionID));
                    //newURL = addQueryToURL(newURL,HTTPStorageProtocol.SPEAKER_ID_KEY,speakerIDFormat.format(speakerID));
					newURL = addQueryToURL(newURL,HTTPStorageProtocol.EXTENSION_KEY, extension);
					if(scriptID!=null)newURL = addQueryToURL(newURL,HTTPStorageProtocol.SCRIPT_ID_KEY, "" + scriptID);
					newURL = addQueryToURL(newURL, HTTPStorageProtocol.LINE_KEY, "" + line+1);
                    newURL=addQueryToURL(newURL,HTTPStorageProtocol.OVERWRITE_KEY,new Boolean(overwrite).toString());
//                    if(!overwrite){
//                    	// Note: this is only for the client to distinguish the cached versions
//                    	// the server uses the DB to determine the version
//                    newURL=addQueryToURL(newURL,HTTPStorageProtocol.VERSION_KEY,Integer.toString(recVersion));
//                    }
					
					Enumeration keys = metadata.elements();
					Enumeration values = metadata.elements();
					while (keys.hasMoreElements() && values.hasMoreElements())
						newURL = addQueryToURL(newURL, (String) keys.nextElement(), (String) values.nextElement());
				}
				else
				{
					newURL = sessionURL;
					
					audioFileName = getRootFileName(sessionID,promptCode, speakerCode,recVersion);
					
					if (numLines == 1) 
						lineNr = "";
					else
						lineNr = "_" + line;
					
					audioFileName += lineNr + "." + extension;
					newURL = addDirToURL(newURL, audioFileName);					
				}
				audioFileURLs[line] = newURL;
			}
		}
		catch (Exception ex)
		{
			String msg = "Exception: " + ex.getLocalizedMessage();
			logger.severe(msg); throw new StorageManagerException(msg, ex);
		}		
		return audioFileURLs;
	}
	/**
	 * Generates a dynamic annotation filename for the actual item based on the storage URL.
	 * In online mode, when <code>isUseAsCache()</code> is <b>true</b>, the following
	 * parameters are appended on the <code>storageURL</code> as query string: </br>
	 * <code>promptCode, speakerCode, extension, session, line</code> </br>
	 * In offline mode, prompt, speaker code etc. are appended on the storage URL as
	 * directory fragments to build a valid and individual filename.
	 * @return URL
	 * @throws StorageManagerException
	 */
	public URL generateAnnotationFileURL() throws StorageManagerException{

		URL sessionURL = getSessionURL();
		URL annotationURL;
		
		try{
			if (useAsCache){
					annotationURL= addQueryToURL(storageURL, HTTPStorageProtocol.CMD_KEY, HTTPStorageProtocol.STORE_ANNOTATION);
					annotationURL = addQueryToURL(annotationURL, HTTPStorageProtocol.ITEM_CODE_KEY, promptCode);
					if(speakerCode!=null){
					annotationURL = addQueryToURL(annotationURL, HTTPStorageProtocol.SPEAKER_CODE_KEY, speakerCode);
					}
                    annotationURL = addQueryToURL(annotationURL,HTTPStorageProtocol.SESSION_ID_KEY,sessionIDFormat.format(sessionID));
                    //newURL = addQueryToURL(newURL,HTTPStorageProtocol.SPEAKER_ID_KEY,speakerIDFormat.format(speakerID));
                    
					annotationURL = addQueryToURL(annotationURL,HTTPStorageProtocol.EXTENSION_KEY, XML_FILENAME_EXTENSION);
					if(scriptID!=null)annotationURL = addQueryToURL(annotationURL,HTTPStorageProtocol.SCRIPT_ID_KEY, "" + scriptID);
                    annotationURL=addQueryToURL(annotationURL,HTTPStorageProtocol.OVERWRITE_KEY,new Boolean(overwrite).toString());
//                    if(!overwrite){
//                    	// Note: this is only for the client to distinguish the cached versions
//                    	// the server uses the DB to determine the version
//                    newURL=addQueryToURL(newURL,HTTPStorageProtocol.VERSION_KEY,Integer.toString(recVersion));
//                    }
					
//					Enumeration keys = metadata.elements();
//					Enumeration values = metadata.elements();
//					while (keys.hasMoreElements() && values.hasMoreElements())
//						newURL = addQueryToURL(newURL, (String) keys.nextElement(), (String) values.nextElement());
				}else{
					annotationURL = sessionURL;
					String annotationFileName = getAnnotationFileName();
					annotationURL = addDirToURL(annotationURL, annotationFileName);					
				}
		}catch (Exception ex){
			String msg = "Exception: " + ex.getLocalizedMessage();
			logger.severe(msg); throw new StorageManagerException(msg, ex);
		}		
		return annotationURL;
	}
	
	public URL[] getConvertedAudioFiles() throws StorageManagerException {
		if (uploadType == null)
			return null;
		return generateAudioFileURLs();
/*			promptCode,
			speakerCode,
			uploadType.getExtension());
*/		// Test to go through tomcat security 
		//return getAudioFiles(promptCode, speakerCode,"wav");
	}

	//	public URL getCachedURL(File cachedFile){
	//		String filename=cachedFile.getName();
	//			sessionIDFormat.
	//	}

	public String getLogFileName() {
		return getSessionNamePart().concat(
			logFileSuffix + "." + logFileExtension);
	}
	public URL getLogFile() throws StorageManagerException {
        
        URL sessionDir = getSessionURL();
        //URL logFile = null;
        URL newURL=null;
        try
        {
            newURL = addDirToURL(sessionDir, getLogFileName());
            if (useAsCache)
            {
                newURL = addQueryToURL(newURL, HTTPStorageProtocol.CMD_KEY, HTTPStorageProtocol.STORE_LOG);
                if(speakerCode!=null){
                newURL = addQueryToURL(newURL, HTTPStorageProtocol.SPEAKER_CODE_KEY, speakerCode);
                }
                newURL = addQueryToURL(newURL,HTTPStorageProtocol.SESSION_ID_KEY,sessionIDFormat.format(sessionID));
                //newURL = addQueryToURL(newURL,HTTPStorageProtocol.SPEAKER_ID_KEY,speakerIDFormat.format(speakerID));
                newURL = addQueryToURL(newURL,HTTPStorageProtocol.EXTENSION_KEY, logFileExtension);
                if (scriptID !=null)newURL = addQueryToURL(newURL,HTTPStorageProtocol.SCRIPT_ID_KEY, "" + scriptID);
            }
        }
        catch (Exception ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        return newURL;
        
     
	}
	
	/**
     * Computes a file name fragment for this session.
     * The name starts always with the given speaker code. 
     * If <b>useSpeakerID</b> is set, it is followed by the speaker ID.  
     * If <b>useSessionID</b> is set, the the session ID is added.
     * @param sessionID session ID  
     * @param speakerCode speaker code
     * @return a file name fragment for this session
     */
    private String getSessionNamePart(int sessionID,String speakerCode) {
        String sessionFileName = "";
        if(speakerCode!=null){
            sessionFileName=sessionFileName.concat(speakerCode);
        }
        if (useSessionID) {
            sessionFileName =
                sessionFileName.concat(sessionIDFormat.format(sessionID));
        }
        if (useScriptID) {
            if (scriptID !=null){
            sessionFileName =
                sessionFileName.concat(scriptID);
            }
        }
        return sessionFileName;
    }

	/**
	 * Computes a file name fragment for this session.
	 * The name starts always with the given speaker code. 
	 * If <b>useSpeakerID</b> is set, it is followed by the speaker ID.  
	 * If <b>useSessionID</b> is set, the the session ID is added.  
	 * @param speakerCode
	 * @return a file name fragment for this session
	 */
	private String getSessionNamePart(String speakerCode) {
		return getSessionNamePart(sessionID, speakerCode);
	}

	// If argument is omitted, use the member variable instead of
	private String getSessionNamePart() { return getSessionNamePart(speakerCode); }
	
	/**
     * Computes a root file name, subject to several options.
     * The root file name consists at least of the speaker code, plus the promt code.
     * @see #getSessionNamePart(String speakerCode)
     * @param promptCode
     * @param speakerCode
     * @return a root file name
     */
    private String getRootFileName(int sessionID,String promptCode, String speakerCode, int recVersion) {
        String rootFileName = getSessionNamePart(sessionID,speakerCode);
        rootFileName = rootFileName.concat(promptCode);
        if(!overwrite && !useAsCache){
            rootFileName = rootFileName.concat("_"+recVersionFormat.format(new Integer(recVersion)));
        }
        return rootFileName;
    }
	
	/**
	 * Computes a root file name, subject to several options.
	 * The root file name consists at least of the speaker code, plus the promt code.
	 * @see #getSessionNamePart(String speakerCode)
	 * @param promptCode
	 * @param speakerCode
	 * @return a root file name
	 */
	private String getRootFileName(String promptCode, String speakerCode, int recVersion) {
		String rootFileName = getSessionNamePart(speakerCode);
		rootFileName = rootFileName.concat(promptCode);
        if(!overwrite && !useAsCache){
            rootFileName = rootFileName.concat("_"+recVersionFormat.format(new Integer(recVersion)));
        }
		return rootFileName;
	}
	
	// If argument is omitted, use the member variable instead of
    public String getNewRootFileName() {
        return getRootFileName(promptCode, speakerCode,recVersion+1);
    }
	// If argument is omitted, use the member variable instead of
	public String getRootFileName() {
	    return getRootFileName(promptCode, speakerCode,recVersion);
	}
	public String[] getRootFileNames() { 
	    String[] rootFileNames = new String[numLines];
	    String rootFileName = getRootFileName(promptCode, speakerCode,recVersion);
        if (numLines == 1) {
            rootFileNames[0] = rootFileName;
        } else {
            for (int i = 0; i < numLines; i++) {
                rootFileNames[i] =
                    rootFileName.concat("_" + i);
            }
        }
        return rootFileNames;
	}
	
	public URL getItemFileURL(String suffix) throws StorageManagerException { 
	    URL customURL = getSessionURL();
        String customFileName = getRootFileName();
        customFileName=customFileName.concat(suffix);
        customURL = addDirToURL(customURL, customFileName);
        return customURL; 
    }
	
	public String[] getAudioFileNames() {
		return getAudioFileNames(promptCode, speakerCode, audioFileExtension);
	}

	public String[] getAudioFileNames(String itemCode) {
		return getAudioFileNames(itemCode, speakerCode, audioFileExtension);
	}
	
	public String[] getAudioFileNames(
		String promptCode,
		String speakerCode,
		String extension) {
		String[] audioFileNames = new String[numLines];
		String rootFileName = getRootFileName(promptCode, speakerCode,recVersion);
		if (numLines == 1) {
			audioFileNames[0] = rootFileName.concat("." + extension);
		} else {
			for (int i = 0; i < numLines; i++) {
				audioFileNames[i] =
					rootFileName.concat("_" + i + "." + extension);
			}
		}
		return audioFileNames;
	}

	public String getLabelFileName() {
		return getRootFileName().concat("." + labelFileExtension);
	}

	public String getRecordingSessionInfoFileName() {
	   return RecordingSession.DEFAULT_RECORDING_SESSION_INFO_FILENAME;
    }

	
	public File getAnnoationFile() throws StorageManagerException{
		File file = null;
		try{
			if (useAsCache){
				//				Create new cache entry
				URL url = generateAnnotationFileURL();
				UploadFile[] cacheEntry=new UploadFile[1];
				File tmpAnnoFile;
					try{
						tmpAnnoFile = File.createTempFile(cacheTmpFilePrefix, DEF_ANNOTATION_FILE_SUFFIX+"."+ XML_FILENAME_EXTENSION);
					}catch (IOException e){
						throw new StorageManagerException(e);
					}
					cacheEntry[0] = new UploadFile(tmpAnnoFile, url);
				cache.add(cacheEntry);
				file = cacheEntry[0].getFile();
				}else{
				URL url = generateAnnotationFileURL();
					if (!url.getProtocol().equals("file"))
						throw new StorageManagerException("URL protocol is not \"file\"");
					file = new File(url.toURI().getPath());
				}
		}catch (Exception ex){
			String msg = "Exception: " + ex.getLocalizedMessage();
			logger.severe(msg); throw new StorageManagerException(msg, ex);
		}
		return file;
	}
	
	
	public String getAnnotationFileName() {
		return getRootFileName(promptCode, speakerCode,recVersion+1).concat(DEF_ANNOTATION_FILE_SUFFIX+"." + XML_FILENAME_EXTENSION);
	}


	/**
	 * Creates a base URL for this session, which is used to build
	 * several individual URLs for the upload.
	 * @return the base URL
	 * @throws StorageManagerException 
	 */
	public URL getSessionURL() throws StorageManagerException {

		String sessionDir = "";
		if (createSpeakerDir) {
			String tmpDir =
				sessionDir.concat("/" + sessionIDFormat.format(sessionID));
			sessionDir = tmpDir;
		}
		if (createScriptDir && scriptID !=null) {
			
			String tmpDir =
				sessionDir.concat("/" + scriptIDFormat.format(scriptID));
			sessionDir = tmpDir;
		}
		URL sessionURL = storageURL;
//		try
//		{
		sessionURL = addDirToURL(storageURL, sessionDir);
//		}
//		catch (StorageManagerException ex)
//		{
//			return null;
//		}
		return sessionURL;
	}

	public boolean isRecorded() throws StorageManagerException {
		return isRecorded(promptCode);
	}

    /**
     * Checks if the item is already recorded.
     * @param promptCode code of the item
     * @return true if a recording exists
     * 
     * @throws StorageManagerException 
     */
    public boolean isRecorded(String promptCode) throws StorageManagerException{
        return isRecorded(promptCode,0);
    }
    /**
     * Checks if a particular version of an item is already recorded.
     * @param promptCode code of the item
     * @param recVersion number
     * @return true if a recording exists
     * @throws StorageManagerException 
     * @throws URISyntaxException 
     */
    public boolean isRecorded(String promptCode,int recVersion) throws StorageManagerException {
        return isRecorded(sessionID, speakerCode,promptCode, recVersion);
    }
    
	/**
	 * Checks if a particular version of an item is already recorded.
	 * @param promptCode code of the item
     * @param recVersion number
	 * @return true if a recording exists
	 * @throws StorageManagerException 
	 * @throws URISyntaxException 
	 */
	public boolean isRecorded(int sessionID,String speakerCode,String promptCode,int recVersion) throws StorageManagerException 
	{
		boolean recorded = true;
        
//		try
		{
			URL[] urls = generateAudioFileURLs(sessionID,speakerCode,promptCode,recVersion); 
			if (useAsCache) {
				
				UploadAudioFile[] upload = null;
				try {
					upload = getUpload(urls, false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (upload == null || upload[0].getFile() == null)
					recorded = false;
			} else {
				for (int i = 0; i < numLines; i++) {
					String path;
					try {
						path = urls[i].toURI().getPath();
					} catch (URISyntaxException e) {
						e.printStackTrace();
						throw new StorageManagerException(e);
					}
					File f = new File(path);
					if (!f.exists()) {
						recorded = false;
						break;
					}
				}
			}
		}
//		catch (Exception ex)
//		{
//			String msg = "Exception: " + ex.getLocalizedMessage();
//			logger.severe(msg); //throw new StorageManagerException(msg, ex);
//		}		
		return recorded;
	}
    /**
     * Returns count of already recorded version beginning from zero.
     * Note that only subsequent version numbers are recognized.
     * @return number of recorded versions
     * @throws StorageManagerException 
     */
    public int getRecordedVersions() throws StorageManagerException{
        return getRecordedVersions(promptCode);
        
    }
    /**
     * Returns count of already recorded version beginning from zero.
     * Note that only subsequent version numbers are recognized.
     * @param promptCode
     * @return number of recorded versions
     * @throws StorageManagerException 
     */
    public int getRecordedVersions(String promptCode) throws StorageManagerException{
    	if(promptCode==null){
    		throw new StorageManagerException("No prompt code given");
    	}
        if (!overwrite && !useAsCache){
        	 int testRecversion=0;
             while(isRecorded(promptCode,testRecversion)){
                 testRecversion++;
             }     
             //System.out.println("recorded versions for "+promptCode+" = "+testRecversion);
            return testRecversion;
           
        }else{
        	 return isRecorded(promptCode)?1:0;
        }
    }
    
    
    public boolean sessionHasRecordings(int sessionID,String speakerCode,List<String> promptCodes) throws StorageManagerException{
       
        for(String promptCode:promptCodes){
            boolean recordingFound=isRecorded(sessionID, speakerCode, promptCode, 0);
            if(recordingFound){
                return true;
            }
        }
        return false;
    }

	/**
	 * Get audio file names
	 * @param itemCode item code
	 * @param extension audio extension
	 * @return array of audio file names
	 */
	private String[] getAudioFileNames(String itemCode, String extension) {

		String[] audioFileNames = new String[numLines];
		String rootFileName = getSessionNamePart();
		rootFileName = rootFileName.concat(itemCode);

		if (numLines == 1) {
			audioFileNames[0] = rootFileName.concat("." + extension);
		} else {
			for (int i = 0; i < numLines; i++) {
				audioFileNames[i] =
					rootFileName.concat("_" + i + "." + extension);
			}
		}
		return audioFileNames;
	}

	/**
	 * @throws StorageManagerException 
	 * 
	 */
	public boolean createSessionDirectory() throws StorageManagerException {
		//if (useAsCache)return true;
		URL sessionDir = getSessionURL();

		if (!useAsCache) {
			File sessionDirAsFile;
			try {
				sessionDirAsFile = new File(sessionDir.toURI().getPath());
			} catch (URISyntaxException e) {
				return false;
			}
			return sessionDirAsFile.mkdirs();
		} else {
			//return tmpDir.mkdirs();
			return true;
		}
	}

	/**
	 * adds (or extends) a query string to a URL. 
	 * If the givem URL already has a query string attached, the new part is added. 
	 * The method adds one <i>name/value</i> pair to the URL. It does not verify if the
	 * parameter <code>name</code> already exists in the query.
	 * @param  the URL to be extended
	 * @param the variable name for the query string without initial delimiter
	 * @param the variable value for the query string without initial delimiter
	 * @return the new URL
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException 
	 */
	private URL addQueryToURL(URL baseURL, String name, String value) throws MalformedURLException, UnsupportedEncodingException
	{
		String filePart = baseURL.getFile();
        // key and value strings must be URL encoded
        String urlEncName=URLEncoder.encode(name,"UTF-8");
        String urlEncValue=URLEncoder.encode(value,"UTF-8");
        
		if (baseURL.getQuery() == null)
			filePart += "?" + urlEncName + "=" + urlEncValue;
		else
			filePart += "&" + urlEncName + "=" + urlEncValue;
		URL url = new URL(baseURL.getProtocol(), baseURL.getHost(), baseURL.getPort(), filePart);
		return url;
	}

	/**
	 * appends a path fragment on a URL.
	 * @param baseURL
	 * @param dir
	 * @return the new URL
	 * @throws MalformedURLException
	 * @throws StorageManagerException 
	 */
	private URL addDirToURL(URL baseURL, String dir) throws StorageManagerException {
		String resPath;
		URI resUri;
		URL url;
		try {
			resUri=baseURL.toURI();
			String resUriPath=resUri.getPath();
			if(resUriPath==null){
				throw new StorageManagerException("Could not get path from URI: "+resUri.toString());
			}
			resPath = resUriPath.concat("/" + dir);
			resUri=new URI(resUri.getScheme(),resUri.getUserInfo(),resUri.getHost(),resUri.getPort(),resPath,resUri.getQuery(),resUri.getFragment());
			url=resUri.toURL();
		} catch (URISyntaxException e) {
			throw new StorageManagerException(e.getMessage());
		} catch (MalformedURLException e) {
			throw new StorageManagerException(e.getMessage());
		}

//		URL url = new URL(baseURL.getProtocol(), baseURL.getHost(), baseURL.getPort(), resURL);
		
		return url;
	}

	/**
	 * @return audio file extension
	 */
	public String getAudioFileExtension() {
		return audioFileExtension;
	}

	
	public boolean isCreateSessionDir() {
		return createScriptDir;
	}

	
	public boolean isCreateSpeakerDir() {
		return createSpeakerDir;
	}

	/**
	 * @return label file extension
	 */
	public String getLabelFileExtension() {
		return labelFileExtension;
	}

	/**
	 * @return number of lines
	 */
	public int getNumLines() {
		return numLines;
	}

	/**
	 * @return prompt code
	 */
	public String getPromptCode() {
		return promptCode;
	}

	/**
	 * @return script ID
	 */
	public String getScriptID() {
		return scriptID;
	}

	/**
	 * @return session ID
	 */
	public int getSessionID() {
		return sessionID;
	}

	/**
	 * @return the target URL for uploads.
	 */
	public URL getStorageURL() {
		return storageURL;
	}

	
	public boolean isUseScriptID() {
		return useScriptID;
	}

	
	public boolean isUseSpeakerID() {
		return useSessionID;
	}

	
	/**
	 * @return session ID format
	 */
	public NumberFormat getSessionIDFormat() {
		return sessionIDFormat;
	}

	/**
	 * @return speaker ID format
	 */
	public DecimalFormat getSpeakerIDFormat() {
		return speakerIDFormat;
	}

	/**
	 * @param format  session ID format
	 */
	public void setSessionIDFormat(NaturalNumberFormat format) {
		sessionIDFormat = format;
	}

	/**
	 * @param format speaker ID format
	 */
	public void setSpeakerIDFormat(DecimalFormat format) {
		speakerIDFormat = format;
	}

	/**
	 * @return speaker code
	 */
	public String getSpeakerCode() {
		return speakerCode;
	}

	/**
	 * @param string speaker code
	 */
	public void setSpeakerCode(String string) {
		speakerCode = string;
	}

	
	public URL getTimeLogFile() throws StorageManagerException {
		URL sessionDir = getSessionURL();
		//URL logFile = null;
        URL newURL=null;
		try
		{
			newURL = addDirToURL(sessionDir, getTimeLogFileName());
            if (useAsCache)
            {
                newURL = addQueryToURL(newURL, HTTPStorageProtocol.CMD_KEY, HTTPStorageProtocol.STORE_TIMELOG);
                if(speakerCode!=null){
                newURL = addQueryToURL(newURL, HTTPStorageProtocol.SPEAKER_CODE_KEY, speakerCode);
                }
                newURL = addQueryToURL(newURL,HTTPStorageProtocol.SESSION_ID_KEY,sessionIDFormat.format(sessionID));
                //newURL = addQueryToURL(newURL,HTTPStorageProtocol.SPEAKER_ID_KEY,speakerIDFormat.format(speakerID));
                newURL = addQueryToURL(newURL,HTTPStorageProtocol.EXTENSION_KEY, logFileExtension);
                if (scriptID != null)newURL = addQueryToURL(newURL,HTTPStorageProtocol.SCRIPT_ID_KEY, "" + scriptID);
            }
		}
		catch (Exception ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return newURL;
	}

	/**
	 * @return time log file name
	 */
	private String getTimeLogFileName() {
		return getSessionNamePart().concat(
			timeLogFileSuffix + "." + logFileExtension);
	}

	/**
	 * @return upload audio type
	 */
	public AudioFileFormat.Type getUploadType() {
		return uploadType;
	}

	/**
	 * @param string audio file extension
	 */
	public void setAudioFileExtension(String string) {
		audioFileExtension = string;
	}

	
	public void setCreateSessionDir(boolean b) {
		createScriptDir = b;
	}

	
	public void setCreateSpeakerDir(boolean b) {
		createSpeakerDir = b;
	}

	/**
	 * @param string label file extension
	 */
	public void setLabelFileExtension(String string) {
		labelFileExtension = string;
	}

	/**
	 * @param i number of lines
	 */
	public void setNumLines(int i) {
		numLines = i;
	}

	/**
	 * @param string prompt code
	 */
	public void setPromptCode(String string) {
		promptCode = string;
	}

	/**
	 * @param scriptID script ID
	 */
	public void setScriptID(String scriptID) {
		this.scriptID = scriptID;
	}

	/**
	 * @param i session ID
	 */
	public void setSessionID(int i) {
		sessionID = i;
	}

	/**
	 * defines the target URL for uploads.
	 * That means the protocol, server name and possibly a 
	 * application specific path, e.g. 
	 * <code>http://OurAppServer/DemoApplication/RecordingData/StoreServlet.xml</code></br>
	 * If the protocol of the given URL is <i>file</i>, the storageManager
	 * switches to local mode. Otherwise it switches to remote mode.
	 * 
	 * @param target any valid URL
	 */
	public void setStorageURL(URL target) {
		storageURL = target;
		if (target.getProtocol().equalsIgnoreCase("file"))
		{	
			useAsCache = false;
			try {
				tmpDirName = storageURL.toURI().getPath();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//tmpDir = new File(tmpDirName);
		}
		else
		{	
			useAsCache = true;
//			tmpDirName = System.getProperty("java.io.tmpdir")
//					+ File.separator	+ getClass().getName();
			//tmpDir = new File(tmpDirName);
		} 
	}

	
	public void setUseScriptID(boolean b) {
		useScriptID = b;
	}

	
	public void setUseSpeakerID(boolean b) {
		useSessionID = b;
	}

	
	/**
	 * @param type audio upload type
	 */
	public void setUploadType(AudioFileFormat.Type type) {
		uploadType = type;
	}

	/**
	 * @return true if upload cache is used
	 */
	public boolean isUseAsCache() {
		return useAsCache;
	}

	/**
	 * @param b true if upload cache is used
	 */
	public void setUseAsCache(boolean b) {
		useAsCache = b;
	}

	/* (non-Javadoc)
	 * @see ipsk.net.http.UploadCacheListener#tryConnect()
	 */
	public void tryConnect() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ipsk.net.http.UploadCacheListener#connected()
	 */
	public void connected() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see ipsk.net.http.UploadCacheListener#stateChanged(ipsk.net.Upload)
	 */
	public void stateChanged(Upload uvb) {

	}

	/* (non-Javadoc)
	 * @see ipsk.net.http.UploadCacheListener#disconnected()
	 */
	public void disconnected() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return Returns the metadata.
	 */
	public MetaData getMetadata()
	{
		return metadata;
	}
	/**
	 * @param metadata The metadata to set.
	 */
	public void setMetadata(MetaData metadata)
	{
		this.metadata = metadata;
	}


    /**
     * @return the recVersion
     */
    public int getRecVersion() {
        return recVersion;
    }


    /**
     * @param recVersion the recVersion to set
     */
    public void setRecVersion(int recVersion) {
        this.recVersion = recVersion;
    }


    /**
     * @param overwrite the overwrite to set
     */
    public void setOverwrite(boolean overwrite) {
    	logger.info("Overwrite mode: "+overwrite);
        this.overwrite = overwrite;
    }


	public void update(UploadEvent event) {
		if(event instanceof UploadConnectionEvent){
			UploadConnectionEvent uce=(UploadConnectionEvent)event;
			ConnectionState cs=uce.getConnectionState();
			if(cs.equals(ConnectionState.TRY_CONNECT)){
				tryConnect();
			}
		}
	}
	
	/**
	 * Get highest session number in stand alone file based mode.
	 * Returns null if no session exists or for other modes (Web mode;WikiSpeech)
	 * @return highest session ID or null
	 * @throws StorageManagerException
	 */
	public Integer highestSessionID() throws StorageManagerException{
		Integer highestId=null;
		URL stUrl=getStorageURL();
		if(stUrl!=null){
			String urlProtocol=stUrl.getProtocol();
			if("file".equalsIgnoreCase(urlProtocol)){
				File stDir=fileURLToFile(stUrl);
				if(stDir.exists()){
					File[] files=stDir.listFiles();
					for(File file:files){
						if(file.isDirectory()){
							try {
								Number sessionIdNr=sessionIDFormat.parse(file.getName());
								if(sessionIdNr!=null){
									int sId=sessionIdNr.intValue();
									if(highestId==null || sId>highestId){
										highestId=sId;
									}
								}
							} catch (ParseException e) {

							}
						}
					}
				}
				return highestId;
			}
		}
		return null;
	}


	public File recentRecordingFile(String itemCode)
			throws StorageManagerException {
		File rf = null;
		// recent file is file with greatest version index
		int versCnt = getRecordedVersions(itemCode);
		if (versCnt > 0) {
			int versIdx=versCnt-1;
			if (useAsCache) {
				// recent file is original (uncompressed) file found in upload cache
				URL[] urls = generateAudioFileURLs(sessionID, speakerCode,
						itemCode, versIdx);
				try {
					UploadAudioFile[] uplAudioFile = getUpload(urls);
					if (uplAudioFile != null && uplAudioFile.length > 0) {
						rf = uplAudioFile[0].getAudioCacheFile();
					}
				} catch (IOException e) {
					throw new StorageManagerException(e);
				}

			} else {
				// recent file is file with greatest version index

				String rfNm = getRootFileName(itemCode, getSpeakerCode(),
						versIdx);
				URL sessionURl = getSessionURL();
				File sessF = StorageManager.fileURLToFile(sessionURl);
				rf = new File(sessF, rfNm + "." + getAudioFileExtension());

			}
		}
		return rf;
	}


	

}
