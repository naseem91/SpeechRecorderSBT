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

package ipsk.apps.speechrecorder.storage.net.ui;

import ipsk.apps.speechrecorder.PluginLoadingException;
import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import ipsk.apps.speechrecorder.config.RecordingConfiguration;
import ipsk.apps.speechrecorder.storage.StorageManager;
import ipsk.apps.speechrecorder.storage.StorageManagerException;
import ipsk.beans.DOMCodec;
import ipsk.net.UploadCache;
import ipsk.net.UploadCacheListener;
import ipsk.net.UploadCacheUI;
import ipsk.net.cookie.SessionCookieHandler;
import ipsk.net.event.UploadEvent;
import ipsk.net.event.UploadFinishedEvent;
import ipsk.util.optionparser.Option;
import ipsk.util.optionparser.OptionParser;
import ipsk.util.optionparser.OptionParserException;
import ipsk.xml.DOMConverter;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.w3c.dom.Document;

public class StorageManagerUploadUI extends JFrame implements UploadCacheListener, WindowListener {

	private UploadCache uploadCache;
	private UploadCacheUI uploadCacheUI;
	private StorageManager storageManager;
	private boolean finished=false;
	private String projectFileURL;
	
	public StorageManagerUploadUI(String projectFileURL){
		super("Speech recorder cache upload");
		this.projectFileURL=projectFileURL;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		
		
		//uploadCache=new SplittingHttpUploadCache();
		storageManager=new StorageManager();
		try {
			storageManager.setStorageURL(new URL("http://linux21:8080/corpusmachine/storage/RECS"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		getContentPane().setLayout(new BorderLayout());
		
	}
	
	public void open() throws StorageManagerException{
		if (projectFileURL != null) {
    		URL projectURL;
			try {
				projectURL = new URL(projectFileURL);
				DOMConverter domConverter=new DOMConverter();
	    		Package configBasePack = Class.forName("ipsk.apps.speechrecorder.config.ProjectConfiguration").getPackage();
	        	
	        	DOMCodec domCodec = new DOMCodec(configBasePack);
				Document d = domConverter.readXML(projectURL.openStream());
	    		ProjectConfiguration project = (ProjectConfiguration) domCodec.readDocument(d);

	    		RecordingConfiguration recCfg = project.getRecordingConfiguration();
	    		
	    		boolean overwrite = recCfg.getOverwrite();
	    		
	    		String recDirName = recCfg.getUrl();
	    		URL recBaseURL =new URL(recDirName);
	    		boolean	useUploadCache = true;

	    		storageManager.setUseAsCache(useUploadCache);
	    	
	    			String uploadCacheClassname = project.getCacheConfiguration()
	    					.getUploadCacheClassname();
	    			try {

	    				uploadCache = (UploadCache) Class.forName(uploadCacheClassname)
	    						.newInstance();
	    			} catch (Exception e) {
	    				throw (new PluginLoadingException(uploadCacheClassname, e));
	    			}
	    			// uploadCache.setHoldSize(CACHE_HOLD_SIZE); //100 MB
	    			// POST is default now
	    			// uploadCache.setRequestMethod("POST");
	    			uploadCache.setOverwrite(project.getRecordingConfiguration().getOverwrite());
	    			int transferRateLimit=project.getCacheConfiguration().getTransferRateLimit();
	    			if(transferRateLimit!=UploadCache.UNLIMITED){
	    				if(uploadCache.isTransferLimitSupported()){
	    					uploadCache.setTransferLimit(transferRateLimit);
	    					//logger.info("Upload cache set transfer rate limit: "+transferRateLimit);
	    				}else{
	    					//logger.warning("Upload cache does not support tarnsfer rate limiting !");
	    				}
	    			}
	    			

	    		storageManager.setUploadCache(uploadCache);
	    		storageManager.setUseAsCache(useUploadCache);
	    		storageManager.setOverwrite(overwrite);

	    		// set the audio compression to use, e.g. FLAC
	    		String audioCompression = project.getCacheConfiguration()
	    				.getAudioStorageType();
	    		if (audioCompression != null && !audioCompression.equals("")) {
	    			//logger.fine("Requested audio upload type: " + audioCompression);
	    			AudioFileFormat.Type[] types = AudioSystem.getAudioFileTypes();
	    			AudioFileFormat.Type type = null;
	    			for (int i = 0; i < types.length; i++) {
	    				if (types[i].toString().equalsIgnoreCase(audioCompression))
	    					type = types[i];
	    			}
	    			if (type != null) {
	    				storageManager.setUploadType(type);
	    				//logger.fine("Audio upload type " + audioCompression + " set.");
	    			} else {
	    				//logger.warning("Requested audio type \"" + audioCompression
	    				//		+ "\" not available.");
	    			}
	    		}
	    		storageManager.setStorageURL(recBaseURL);
	    		// We do not use a session ID
	    		storageManager.setCreateSessionDir(false);
	    		storageManager.setCreateSpeakerDir(false);
	    		storageManager.setUseScriptID(false);
	    		uploadCacheUI=new UploadCacheUI(uploadCache);
	    		getContentPane().add(uploadCacheUI,BorderLayout.CENTER);
	    		storageManager.open();
	    		uploadCache.addUploadCacheListener(this);
			} catch (Exception e) {
				
				e.printStackTrace();
			} 
    		
    	}
		
		
	}
	
	public void start(){
		uploadCache.start();
		
	}
	
	
	public void close() throws StorageManagerException{
		uploadCache.stop();
		uploadCache.clear();
		uploadCache.close();
		
		storageManager.close();
	}
	
	
	public void update(UploadEvent event) {
		if (event instanceof UploadFinishedEvent){
			finished=true;
			JOptionPane.showMessageDialog(this,"Upload of pending recording files finished! Thank you!");
			try {
				close();
			} catch (StorageManagerException e) {
				JOptionPane.showMessageDialog(null,"Error: "+e.getLocalizedMessage(), "Storage manager close error", JOptionPane.ERROR_MESSAGE);
			}finally{
				dispose();
				System.exit(-1);
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String projectFileURL = null;
		String[] params;
		String user = null;
		String password = "";
		OptionParser op = new OptionParser();
		op.addOption("u", "");
		op.addOption("p", "");
		op.addOption("s", "");
		try {
			op.parse(args);
		} catch (OptionParserException e) {
			System.err.println(e.getLocalizedMessage());
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),
					"ERROR", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		Option[] options = op.getOptions();
		for (int i = 0; i < options.length; i++) {
			if (options[i].getOptionName().equals("u")) {
				user = options[i].getParam();
			} else if (options[i].getOptionName().equals("p")) {
				password = options[i].getParam();
			} else if (options[i].getOptionName().equals("s")) {
				String sessionCookie = options[i].getParam();
				
				// Cookie handling: method 1:
				// disable cookie handler
				// Java web start version 6 receives cookies from requests which do not require a authentication
				// e.g. to download jar files
				// the cookie handler then holds a JSESSIONID which belongs to a new unauthenticated session.
				// If Tomcat receives the two session id's and seems to use only the first and rejects the request
				// with HTTP 401 Unauthorized
				// Tomcat bug ?
				// so I reset the cookie handler and set the given (authenticated) cookie for each URLConnection.
				
				// method 2: (used now)
				// implemented own cookie handler to avoid applying to each URLConnection.
			
				// Setting the cookie handler requires all permissions (signed jars) !!
				CookieHandler.setDefault(new SessionCookieHandler(sessionCookie));
				
//				// method 3:
//				// jar files download requires authentication as well
//				// add cookie to default cookie handler
//				SimpleCookie sc=new SimpleCookie(sessionCookie);
//				URI uri;
//				try {
//					uri = new URI(sc.getProperty("path"));
//					CookieHandler ch=CookieHandler.getDefault();
//					ch.put(uri, sc.getResponseHeaders());
//					
//				} catch (URISyntaxException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
		params = op.getParams();

		if (params.length != 1 && params.length != 0) {
			System.out
					.println("Usage\n\n\t"+StorageManagerUploadUI.class.getName()+" PROJECT_FILE_URL\n\n");
			System.exit(-1);
		}
		if (params.length == 1) {
			projectFileURL = params[0];
		}
		
		// "Delegate" to AWT event thread for Swing thread safety
		
		final String fprojectFileURL = projectFileURL;
		final String fuser = user;
		final String fpassword = password;
		Runnable buildAndShow=new Runnable(){

			public void run() {
				StorageManagerUploadUI uploadUi=new StorageManagerUploadUI(fprojectFileURL);
				
				uploadUi.setLocationRelativeTo(null);
				
				
				try {
					uploadUi.open();
					uploadUi.pack();
					uploadUi.setVisible(true);
					uploadUi.start();
				} catch (StorageManagerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};
		try {
			SwingUtilities.invokeAndWait(buildAndShow);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
		
	}

	public void windowActivated(WindowEvent e) {
		
	}

	public void windowClosed(WindowEvent e) {
		
	}

	public void windowClosing(WindowEvent e) {
		Window w=e.getWindow();
		if(finished){
			try {
				close();
			} catch (StorageManagerException e1) {
				e1.printStackTrace();
			}finally{
			w.dispose();
			System.exit(0);
			}
		}else{
			int answer=JOptionPane.showConfirmDialog(null,"upload not finshied yet.\nDow you really want to exit?");
			if(answer==JOptionPane.YES_OPTION){
				try {
					close();
				} catch (StorageManagerException e1) {
					e1.printStackTrace();
				}finally{
					w.dispose();
					System.exit(1);
				}
			}
		}
	}

	public void windowDeactivated(WindowEvent e) {
		
	}

	public void windowDeiconified(WindowEvent e) {
		
	}

	public void windowIconified(WindowEvent e) {
	
	}

	public void windowOpened(WindowEvent e) {
		
	}

	

}
