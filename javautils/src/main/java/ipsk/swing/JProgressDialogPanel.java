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

package ipsk.swing;

import ipsk.awt.ProgressListener;
import ipsk.awt.Worker;
import ipsk.awt.WorkerException;
import ipsk.awt.event.ProgressErrorEvent;
import ipsk.awt.event.ProgressEvent;
import ipsk.awt.test.DemoWorker;
import ipsk.util.LocalizableMessage;
import ipsk.util.ProgressStatus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class JProgressDialogPanel extends JDialogPanel implements ProgressListener {
	
	private Worker worker;
	private JLabel messageLabel;
	private Dimension maxLabelSize=new Dimension();
	
	
	private JProgressBar progressBar;
	public JProgressDialogPanel(Worker worker,String title,String message){
		this(title,message);
		setWorker(worker);
	}
	public JProgressDialogPanel(String title,String message){
		super(JDialogPanel.Options.CANCEL,title);
		//this.worker=worker;
		progressBar=new JProgressBar(0,100);
		progressBar.setStringPainted(true);
		getContentPane().setLayout(new GridBagLayout());
		messageLabel=new JLabel(message);
		GridBagConstraints gc=new GridBagConstraints();
		gc.insets=new Insets(5,5,5,5);
		gc.gridx=0;
		gc.gridy=0;
		setResizable(false);
		java.net.URL imgURL = getClass().getResource("/toolbarButtonGraphics/general/Information24.gif");
	    if (imgURL != null) {
	        ImageIcon infoIcon=new ImageIcon(imgURL);
	        gc.gridheight=2;
	        getContentPane().add(new JLabel(infoIcon),gc);
	        gc.gridx++;
	    }
		gc.gridheight=1;
		getContentPane().add(messageLabel,gc);
		gc.gridy++;
		gc.fill=GridBagConstraints.HORIZONTAL;
		gc.weightx=2.0;
		getContentPane().add(progressBar,gc);
	
	}

	
	public void setEnabled(boolean enabled){
		super.setEnabled(enabled);
		progressBar.setEnabled(enabled);
		cancelButton.setEnabled(cancelButton.isEnabled() && enabled);
	}
	
	
	public void update(ProgressEvent progressEvent) {
		if(progressEvent instanceof ProgressErrorEvent){
			setEnabled(false);
			disposeDialog();
		}else{
			ProgressStatus ps=progressEvent.getProgressStatus();
			if (ps != null) {
			    Short pProgr=ps.getPercentProgress();
			    if(pProgr!=null){
				progressBar.setValue((int) ps.getPercentProgress());
			    }
				LocalizableMessage message = ps.getMessage();
				if (message != null) {
//				    Dimension sizeBefore=messageLabel.getPreferredSize();
					messageLabel.setText(message.localize());
					Dimension sizeNow=messageLabel.getPreferredSize();
					if(sizeNow.width > maxLabelSize.width || sizeNow.height > maxLabelSize.height){
//					    System.out.println("Resize: "+maxLabelSize+" -> "+sizeNow);
					    maxLabelSize=sizeNow;
					    messageLabel.revalidate();
					    revalidate();
					    if (dialog != null) {
					        dialog.pack();
					    }
					}
					repaint();
					
				}
				if(ps.isDone()){
					cancelButton.setEnabled(false);
					setValue(OK_OPTION);
					disposeDialog();
				}
			}
		}
	}

	
	
	protected void doCancel(){
		
		super.doCancel();
		worker.cancel();
		
	}

	public static void main(String[] args){

		Runnable runnable=new Runnable(){

			public void run(){
				DemoWorker dw = new DemoWorker();

				JProgressDialogPanel jpdp=new JProgressDialogPanel(dw,"Progress title","Progress message");
				try {
					dw.open();
				} catch (WorkerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dw.start();
				Object res=jpdp.showDialog(new JFrame());
				if(res.equals(JOptionPane.CANCEL_OPTION)){
					//dw.cancel();

					System.out.println("Cancelled");
				}
				try {
					dw.close();
					dw.reset();
				} catch (WorkerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(-1);
				}
				System.exit(0);
				//jpdp.disposeDialog();
			}
		};
		
		SwingUtilities.invokeLater(runnable);
	}


	public Worker getWorker() {
		return worker;
	}


	public void setWorker(Worker worker) {
		this.worker = worker;
		if(worker!=null)worker.addProgressListener(this);
	}

}

