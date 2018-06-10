//    IPS Java Utils
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.util.apps.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ipsk.swing.JDialogPanel;
import ipsk.text.ParserException;
import ipsk.text.Version;
import ipsk.util.apps.UpdateManager;
import ipsk.util.apps.UpdateManagerEvent;
import ipsk.util.apps.UpdateManagerListener;
import ipsk.util.apps.descriptor.ApplicationDescriptor;
import ipsk.util.apps.descriptor.ApplicationVersionDescriptor;
import ipsk.util.apps.descriptor.Change;

/**
 * @author klausj
 * 
 */
public class UpdateDialogUI extends JDialogPanel implements UpdateManagerListener {

    public class  DownloadActionOption{
        private ApplicationVersionDescriptor applicationVersionDescriptor;
        public ApplicationVersionDescriptor getApplicationVersionDescriptor() {
            return applicationVersionDescriptor;
        }
        public boolean isRequestApplicationQuit() {
            return requestApplicationQuit;
        }
        private boolean requestApplicationQuit;
        public DownloadActionOption(ApplicationVersionDescriptor applicationVersionDescriptor,
                boolean requestApplicationQuit) {
            super();
            this.applicationVersionDescriptor = applicationVersionDescriptor;
            this.requestApplicationQuit = requestApplicationQuit;
        }
        
    }
	private ApplicationDescriptor applicationDescriptor;
	private UpdateManager updateManager;

	/**
     * 
     */
	public UpdateDialogUI(UpdateManager updateManager) {
		super(Options.CANCEL);
		
		this.updateManager = updateManager;
		cancelButton.setText("Close");
		updateManager.addUpdateManagerListener(this);
		updateContent();
	}

	

    private void updateContent() {
	    Container cp=getContentPane();
//		Container cp = this;
		cp.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridwidth=1;
		gc.weightx=1.0;
		gc.weighty=1.0;
		        
		gc.gridx = 0;
		gc.gridy = 0;
		GridBagConstraints gc2 = new GridBagConstraints();
		gc2.anchor = GridBagConstraints.WEST;
		gc2.weightx = 2;
		gc2.weighty = 2;
		gc2.fill = GridBagConstraints.BOTH;
		gc2.gridx = 0;
		gc2.gridy = 0;
		gc2.insets = new Insets(1, 1, 1, 1);
		cp.removeAll();
		ApplicationDescriptor ad = updateManager.getApplicationDescriptor();

		if (ad == null) {
			if (UpdateManager.Status.ERROR.equals(updateManager.getStatus())) {
				JLabel noUpdateLabel = new JLabel(
						"Error: Could not get update information!");
				cp.add(noUpdateLabel, gc);
			} else if (UpdateManager.Status.LOADING.equals(updateManager
					.getStatus())) {
				JLabel noUpdateLabel = new JLabel(
						"Loading update information...");
				cp.add(noUpdateLabel, gc);
			}
		} else {
			String name = ad.getName();
			// if (name != null) {
			// JLabel nameLabel = new JLabel(name);
			// cp.add(nameLabel, gc);
			// gc.gridy++;
			// }

			ApplicationVersionDescriptor appVd = updateManager
					.updateAvailableForPlatform();
			Version currentVersion = updateManager.getCurrentVersion();

			JPanel overviewPanel = new JPanel(new GridBagLayout());
			// overviewPanel.setBackground(Color.YELLOW);
			overviewPanel.setBorder(BorderFactory.createTitledBorder(name));
			String overViewMsg;
			Change.Priority pr = null;
			if (appVd == null) {
				overViewMsg = "No update available. You are using the latest version ("
						+ currentVersion + ").";

			} else {
				pr = updateManager.updatePriority();

				overViewMsg = "Update available: " + appVd.getVersion()
						+ " (current version: " + currentVersion + ").";
				if (Change.Priority.STRONGLY_RECOMMENDED.equals(pr)) {
					overViewMsg = overViewMsg
							.concat(" Installation strongly recommended!");
				} else if (Change.Priority.RECOMMENDED.equals(pr)) {
					overViewMsg = overViewMsg
							.concat(" Installation recommended.");
				}
			}
			JLabel overViewLabel = new JLabel(overViewMsg);
			overviewPanel.add(overViewLabel, gc2);
			cp.add(overviewPanel, gc);
			gc.gridy++;

			if (appVd != null) {

				List<ApplicationVersionDescriptor> newerVersions = updateManager
						.newerVersions();
				Collections.reverse(newerVersions);

				StringBuffer chgSb = new StringBuffer();
				for (ApplicationVersionDescriptor nvd : newerVersions) {
					Version nvv = nvd.getVersion();
					chgSb.append(nvv.toString());
					chgSb.append(":\n");
					List<Change> chgs = nvd.getChanges();
					for (Change chg : chgs) {
						chgSb.append('\t');
						chgSb.append(chg);
						chgSb.append('\n');
					}

				}

				ApplicationVersionDescriptorUI appVdUi = new ApplicationVersionDescriptorUI(
						appVd,updateManager.desktopDownloadPossible());
				appVdUi.setActionListener(this);
				appVdUi.setBorder(BorderFactory
						.createTitledBorder("Latest version"));
				cp.add(appVdUi, gc);
				gc.gridy++;
				JPanel changesPanel = new JPanel(new BorderLayout());
				changesPanel.setBorder(BorderFactory
						.createTitledBorder("Changes"));

				// JLabel changesLabel = new JLabel("Changes:");
				// cp.add(changesLabel, gc);
				// gc.gridy++;
				JTextArea ta = new JTextArea(chgSb.toString());
				JScrollPane changesScrollPane=new JScrollPane(ta);
				changesScrollPane.setPreferredSize(new Dimension(400,300));
				changesPanel.add(changesScrollPane, BorderLayout.CENTER);
				gc.weighty=20.0;
				gc.fill=GridBagConstraints.BOTH;
				cp.add(changesPanel, gc);
			}
		}

//		cp.invalidate();
//		cp.validate();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Version currentVersion;
		try {
			currentVersion = Version.parseString(args[0]);
			URL appDescrUrl = new URL(args[1]);
			final UpdateManager um = new UpdateManager(currentVersion);
			um.addUpdateManagerListener(new UpdateManagerListener() {

				public void update(UpdateManagerEvent event) {

					if (UpdateManager.Status.SUCCESS.equals(event)) {
						UpdateDialogUI ud = new UpdateDialogUI(um);
						// ud.showDialog((JFrame)null);
						JOptionPane.showMessageDialog(null, ud, "Updates",
								JOptionPane.INFORMATION_MESSAGE);
						// ud.setVisible(true);
						System.exit(0);
					}
				}
			});
			um.startLoadApplicationDescriptor(appDescrUrl);
		} catch (ParserException e) {

			e.printStackTrace();
		} catch (MalformedURLException e) {

			e.printStackTrace();
		}
	}

	public ApplicationDescriptor getApplicationDescriptor() {
		return applicationDescriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ipsk.util.apps.UpdateManagerListener#update(ipsk.util.apps.UpdateManagerEvent
	 * )
	 */
	public void update(UpdateManagerEvent event) {
		updateContent();

		Container tlAn = getTopLevelAncestor();
//		Container tlAn = this;
		if (tlAn != null && tlAn instanceof Window) {
			Window w = (Window) tlAn;
			w.pack();
			Container frameParent = w.getParent();
			if (frameParent != null) {
				w.setLocationRelativeTo(frameParent);
			} else {
				w.setLocationRelativeTo(null);
			}
		}

		repaint();

	}
	
	public void actionPerformed(ActionEvent ae){
	    if(ae instanceof ApplicationVersionDescriptorUI.DownloadActionEvent){
	        ApplicationVersionDescriptorUI.DownloadActionEvent dae=(ApplicationVersionDescriptorUI.DownloadActionEvent)ae;
	        DownloadActionOption dao=new DownloadActionOption(dae.getApplicationVersionDescriptor(),true);
	        setValue(dao);
	        disposeDialog();
	    }else{
	        super.actionPerformed(ae);
	    }
	}

	// public void dispose(){
	// updateManager.removeUpdateManagerListener(this);
	// }

}
