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

package ipsk.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

public class URLDownloader implements Runnable{
	private URL url;
	private URLConnection urlConnection;
	private int contentLength=-1;
	private int downloadLength=0;
	private OutputStream outstream;
	private Thread thread;
	private Vector<URLDownloaderListener> listeners=new Vector<URLDownloaderListener>();
	
	public URLDownloader(URL url,OutputStream outstream){
		this.url=url;
		this.outstream=outstream;
		
	}
	
	private void open() throws IOException{
		URLConnection urlConnection=url.openConnection();
		contentLength=urlConnection.getContentLength();
		//urlConnection.connect();
	}
	
	public void start() throws IOException{
		open();
		thread=new Thread(this,"Download");
		thread.start();
	}

	public void run() {
		InputStream is=null;
		try {
			urlConnection.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		byte[] buf=new byte[2048];
        int read=0;
        try {
			while ((read=is.read(buf))!=-1){
			    outstream.write(buf,0,read);
			    downloadLength+=read;
			}
			updateListeners(new URLDownloaderEvent(this));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int getCurrentDownloadLength(){
		return downloadLength;
	}
	
	public void addURLDownloaderListener(URLDownloaderListener l) {

		if (l != null && !listeners.contains(l)) {
			listeners.addElement(l);
		}
	}

	public void removeURLDownloaderListener(URLDownloaderListener l) {

		if (l != null) {
			listeners.removeElement(l);
		}
	}
	
	protected synchronized void updateListeners(URLDownloaderEvent e) {
		for(URLDownloaderListener listener:listeners){
			listener.update(e);
		}
	}

	public int getContentLength() {
		return contentLength;
	}
	
}
