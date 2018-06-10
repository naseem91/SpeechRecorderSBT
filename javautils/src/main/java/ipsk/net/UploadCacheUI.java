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

import ipsk.net.event.UploadConnectionEvent;
import ipsk.net.event.UploadEvent;
import ipsk.net.event.UploadStateChangedEvent;
import ipsk.net.event.UploadConnectionEvent.ConnectionState;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;


/**
 * Progress view of {@link ipsk.net.UploadCache UploadCache}.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class UploadCacheUI
	extends JPanel
	implements UploadCacheListener, ActionListener {

	private final static boolean DEBUG = false;

	private static int DEFAULT_REFRESH_DELAY = 500;
	private long uploadStartTime;
	private long uploadDuration;
	private ImageIcon iconTryConnect;
	private UploadCache uc;
	private ImageIcon iconConnected;
	private ImageIcon iconDisconnected;
	private JLabel connectionLabel;
	private JLabel currentUploadLabel;
	private JLabel kbyteRateLabel;
	private JProgressBar progressBar;
	private ResourceBundle rb;
	private JLabel responseLabel;
	//private float byteRate = 0; //bytes per millisecond
	private Timer timer;
	//private long totalLength;
	//private long toUploadLength;
	//private long guessedUploadLength;
	private DecimalFormat rateFormat;
	
	private boolean showUploadsTable=false;

	/**
	 * Create progress view for upload cache.
	 * @param uc
	 */
	public UploadCacheUI(UploadCache uc) {
		super(new BorderLayout());
		this.uc = uc;

		String packageName = getClass().getPackage().getName();
		rb = ResourceBundle.getBundle(packageName + ".ResBundle");
		iconDisconnected =
			new ImageIcon(getClass().getResource("connect_no.png"));
		iconTryConnect =
			new ImageIcon(getClass().getResource("connect_creating.png"));
		iconConnected =
			new ImageIcon(getClass().getResource("connect_established.png"));
		JPanel connectionPanel = new JPanel(new BorderLayout());
		connectionLabel = new JLabel(iconDisconnected);
		responseLabel = new JLabel(uc.getResponseMessage());
		kbyteRateLabel = new JLabel("");
		rateFormat = new DecimalFormat("###.0");

		connectionPanel.add(connectionLabel,BorderLayout.WEST);
		connectionPanel.add(responseLabel,BorderLayout.CENTER);
		connectionPanel.add(kbyteRateLabel,BorderLayout.EAST);
		currentUploadLabel =
			new JLabel(
				rb.getString("uploading") + ": (" + rb.getString("none") + ")");
		currentUploadLabel.setOpaque(true);
		//currentURLLabel.setBackground(Color.GREEN);
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(connectionPanel,BorderLayout.NORTH);
		add(currentUploadLabel,BorderLayout.SOUTH);
		add(progressBar,BorderLayout.CENTER);
		if (uc.isConnected())
			connected();
		timer = new Timer(DEFAULT_REFRESH_DELAY, this);
		Upload currentUpload=uc.getCurrentUploadStream();
		int status=0;
		if(currentUpload!=null){
			status=currentUpload.getStatus();
		}
		stateChanged(currentUpload,status);
		uc.addUploadCacheListener(this);
	}

	
	/* (non-Javadoc)
	 * @see ipsk.net.http.UploadCacheListener#connected()
	 */
	public void connected() {
		if (DEBUG)
			System.out.println("Connected.");
		connectionLabel.setIcon(iconConnected);
		responseLabel.setText(uc.getResponseMessage());
	}

	/* (non-Javadoc)
	 * @see ipsk.net.http.UploadCacheListener#stateChanged(ipsk.net.UploadVectorBuffer)
	 */
	public void stateChanged(Upload uvb, int state) {
		if (uvb != null) {

			if (state == Upload.UPLOADING) {
				String uploadName=uvb.getName();
				String uploadToolTip=null;
				if (uploadName==null){
					uploadName=uvb.getUrl().toString();
					uploadToolTip=uploadName;
				}else{
					uploadToolTip=uploadName+"\nURL: "+uvb.getUrl().toString();
				}
				currentUploadLabel.setText(
					rb.getString("uploading") + ": " + uploadName);
				currentUploadLabel.setToolTipText(uploadToolTip);
				uploadDuration = 0;
				uploadStartTime = System.currentTimeMillis();
				//guessedUploadLength = toUploadLength;
				timer.start();
				if (DEBUG)
					System.out.println(
						rb.getString("uploading")
							+ ": "
							+ uvb.getUrl().toString());
			} else if (state == Upload.DONE) {
				Component parent=getParent();
				if(parent!=null){
					currentUploadLabel.setBackground(parent.getBackground());
				}
				currentUploadLabel.setText(
					rb.getString("uploading")
						+ ": ("
						+ rb.getString("none")
						+ ")");
				currentUploadLabel.setToolTipText("");
				//guessedUploadLength = toUploadLength;
				timer.stop();
				if (DEBUG)
					System.out.println(
						rb.getString("uploaded")
							+ ": "
							+ uvb.getUrl().toString());
			} else if (state == Upload.FAILED) {
				if (DEBUG)
					System.out.println(
						rb.getString("failed")
							+ ": "
							+ uvb.getUrl().toString());
				currentUploadLabel.setText(
					rb.getString("failed") + ": " + uvb.getUrl().toString());
				currentUploadLabel.setToolTipText(
					rb.getString("failed") + ": " + uvb.getUrl().toString());
				currentUploadLabel.setBackground(Color.RED);
				//guessedUploadLength = toUploadLength;
				timer.stop();
			}
		}
		responseLabel.setText(uc.getResponseMessage());
		// for long URLs (e.g. with complex queries) the label gets too long and blocks resizing,
		// so I set the minimum width to 0
		currentUploadLabel.setMinimumSize(new Dimension(0,currentUploadLabel.getMinimumSize().height));
		setProgress();

	}

	/* (non-Javadoc)
	 * @see ipsk.net.http.UploadCacheListener#disconnected()
	 */
	public void disconnected() {
		if (DEBUG)
			System.out.println("Disconnected.");
		connectionLabel.setIcon(iconDisconnected);
		responseLabel.setText(uc.getResponseMessage());
	}

	/* (non-Javadoc)
		 * @see ipsk.net.http.UploadCacheListener#tryConnect()
		 */
	public void tryConnect() {
		if (DEBUG)
			System.out.println("try to connect...");
		connectionLabel.setIcon(iconTryConnect);
		responseLabel.setText(uc.getResponseMessage());
	}

	private void setProgress() {
		long totalLength = uc.getTotalLength();
		long toUploadLength = uc.getToUploadLength();
		boolean idle = uc.isIdle();
		float byteRate = uc.getByteRate();
		if (byteRate == 0) {
			kbyteRateLabel.setText("");
		} else {
			kbyteRateLabel.setText(rateFormat.format(byteRate) + " kByte/s");
		}
		if (totalLength == 0) {
			
			progressBar.setString(null);
			progressBar.setIndeterminate(false);
			progressBar.setValue(100);
		} else if (byteRate == 0) {
			progressBar.setString("");
			progressBar.setIndeterminate(true);
		} else {
			progressBar.setString(null);
			progressBar.setIndeterminate(false);
			progressBar.setValue(
				(int) (100
					- (uc.getGuessedToUploadLength() * 100) / totalLength));
		}
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {

		setProgress();
	}


	public void update(UploadEvent event) {
		if(event instanceof UploadConnectionEvent){
			UploadConnectionEvent uce=(UploadConnectionEvent)event;
			ConnectionState cs=uce.getConnectionState();
			if(cs.equals(ConnectionState.DISCONNECTED)){
				disconnected();
			}else if(cs.equals(ConnectionState.TRY_CONNECT)){
				tryConnect();
			}else if(cs.equals(ConnectionState.CONNECTED)){
				connected();
			}
		}else if(event instanceof UploadStateChangedEvent){
			UploadStateChangedEvent usce=(UploadStateChangedEvent)event;
			stateChanged(usce.getUpload(),usce.getState());
		}
		
	}


	public boolean isShowUploadsTable() {
		return showUploadsTable;
	}


	public void setShowUploadsTable(boolean showUploadsTable) {
		this.showUploadsTable = showUploadsTable;
	}

}
