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

package ipsk.apps.speechrecorder.config.ui;

import ipsk.apps.speechrecorder.config.MixerName;
import ipsk.swing.TitledPanel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * UI to choose multiple audio devices.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class MixerListChooser extends JPanel implements ActionListener,
		ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -389739661431600386L;
	
	private static final String PROTOTYPE_MIXERNAME="Audio dummy prototype mixer name foo bla (dummy)";
	
	private JList list;

	private JButton addButt;

	private JButton removeButt;

	private JComboBox availBox;

	private JTextField vendorField;

	private JTextField versionField;

	private DefaultListModel listData = new DefaultListModel();

	private Vector<java.awt.event.ActionListener> listeners;

	private JTextArea descrArea;
	
	private Mixer.Info[] availMixers;
	/**
	 * Create device chooser.
	 * 
	 * @param availMixers
	 *            available mixers (from audio system)
	 * @param selMixerNames
	 *            initial selected devices
	 */
	public MixerListChooser(Mixer.Info[] availMixers, MixerName[] selMixerNames) {
		super();
		this.availMixers=availMixers;
		listeners = new Vector<java.awt.event.ActionListener>();
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(1, 1, 1, 1);
		c.gridx = 0;
		c.gridy = 0;

		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 2.0;
		JLabel listedMixersLabel = new JLabel("Listed mixers:");
		add(listedMixersLabel, c);
		c.gridy++;
		c.weighty = 2.0;
		list = new JList(listData);
		// giving the list a prototype fixed the problem that the list was
        // collapsing on revalidate when it contained one or more entries.
        MixerName prototypeDummyMixerName=new MixerName(PROTOTYPE_MIXERNAME);
        list.setPrototypeCellValue(prototypeDummyMixerName);
		list.setVisibleRowCount(3);
		list.addListSelectionListener(this);

		JScrollPane listScrollPane = new JScrollPane(list);
		add(listScrollPane, c);
		c.weighty = 0.0;
		c.gridy++;
		c.gridwidth = 1;
		c.weightx = 2.0;
		JPanel buttPanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 8969034884681272616L;

			public void setEnabled(boolean enabled) {
				for (Component child : getComponents()) {
					child.setEnabled(enabled);
				}
			}
		};

		addButt = new JButton("Add");
		addButt.addActionListener(this);
		buttPanel.add(addButt);
		// c.gridx=0;
		// add(addButt,c);
		removeButt = new JButton("Remove");
		removeButt.addActionListener(this);
		buttPanel.add(removeButt);
		// c.gridx++;
		// add(removeButt,c);
		add(buttPanel, c);
		c.gridx = 0;
		c.gridy++;
		add(new JLabel("Available mixers:"), c);
		c.gridy++;
		c.gridwidth = 2;
		availBox = new JComboBox(availMixers);
		availBox.addActionListener(this);
		// availBox.setEditable(true);
		add(availBox, c);
		c.weightx = 2.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy++;
		add(new JLabel("Vendor:"), c);
		c.gridy++;
		vendorField = new JTextField();
		vendorField.setEditable(false);
		add(vendorField, c);
		c.gridy++;
		add(new JLabel("Version:"), c);
		versionField = new JTextField();
		versionField.setEditable(false);
		c.gridy++;
		add(versionField, c);
		c.gridy++;
		add(new JLabel("Description:"), c);
		descrArea = new JTextArea();
		descrArea.setEditable(false);
		descrArea.setRows(2);
		c.gridy++;
		add(descrArea, c);
		setSelectedMixerNames(selMixerNames);
		updateInfoFields();
		updateEnabledActions();
	}
	
	
	
    public MixerListChooser(Info[] availMixerInfos) {
       this(availMixerInfos,null);
    }


    public void setSelectedMixerNames(MixerName[] selectedMixerNames){
	    listData.clear();
	    if (selectedMixerNames != null) {
            
            for (MixerName sm : selectedMixerNames) {
                listData.addElement(sm);
            }
        }
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MixerName rmn=new MixerName();
		rmn.setName("Haha");
		MixerListChooser mlch = new MixerListChooser(
				AudioSystem.getMixerInfo(), new MixerName[] {rmn});
		TitledPanel tPanel = new TitledPanel("Playback");
		tPanel.add(mlch);
		f.getContentPane().add(tPanel);
		f.pack();
		f.setVisible(true);
		mlch.setEnabled(false);
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		Component[] children = getComponents();
		for (Component ch : children) {
			ch.setEnabled(enabled);
		}
		list.setEnabled(enabled);
		if (enabled) {
			updateEnabledActions();
		}
	}

	private void updateEnabledActions() {
		
		if (isEnabled()) {
			if(availMixers==null || availMixers.length==0){
				list.setEnabled(false);
			}
			boolean addAble=false;
			Mixer.Info selInfo=(Mixer.Info) availBox.getSelectedItem();
			if(selInfo!=null){
				MixerName listSelMn=new MixerName(selInfo.getName());
				addAble=!listData.contains(listSelMn);
			}
			addButt.setEnabled(addAble);
			
			if (list.isSelectionEmpty()) {
				removeButt.setEnabled(false);
			} else {
				removeButt.setEnabled(true);
			}
		}
	}

	private void updateInfoFields() {
		Mixer.Info selInfo = ((Mixer.Info) availBox.getSelectedItem());
		if (selInfo == null) {
			vendorField.setText("");
			versionField.setText("");
			descrArea.setText("");
		} else {
			vendorField.setText(selInfo.getVendor());
			versionField.setText(selInfo.getVersion());
			descrArea.setText(selInfo.getDescription());
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		Object src = arg0.getSource();
		if (src == addButt) {
			String selInfo = ((Mixer.Info) availBox.getSelectedItem())
					.getName();
			MixerName rmn=new MixerName(selInfo);
			if (!listData.contains(rmn)) {
				listData.addElement(rmn);
				// list.setListData(listData);

				// list.setVisibleRowCount(3);
				fireAction();
			}
		} else if (src == removeButt) {
			Object[] selMixers = list.getSelectedValues();
			// TODO change when we switch to Java 1.7
			//List<Object> selMixers=list.getSelectedValuesList();
			for (Object selMixer : selMixers) {
				listData.removeElement(selMixer);
			}
			// list.setListData(listData);
			// list.setVisibleRowCount(3);
			fireAction();
		} else if (src == availBox) {
			updateInfoFields();
		}
		updateEnabledActions();
	}

	public void valueChanged(ListSelectionEvent arg0) {
		updateEnabledActions();
	}

	protected synchronized void fireAction() {
		for (ActionListener listener : listeners) {
			listener
					.actionPerformed(new ActionEvent(this,
							ActionEvent.ACTION_PERFORMED,
							"mixer_chooser_list_changed"));
		}
	}

	/**
	 * Add action listener.
	 * 
	 * @param actionListener
	 */
	public synchronized void addActionListener(ActionListener actionListener) {
		if (actionListener != null && !listeners.contains(actionListener)) {
			listeners.addElement(actionListener);
		}
	}

	/**
	 * Remove action listener.
	 * 
	 * @param actionListener
	 */
	public synchronized void removeActionListener(ActionListener actionListener) {
		if (actionListener != null) {
			listeners.removeElement(actionListener);
		}
	}

	/**
	 * Get list of current selected device names.
	 * 
	 * @return array of device descriptors
	 */
	public MixerName[] getMixerList() {

		Object[] objData = listData.toArray();
		MixerName[] strData = new MixerName[objData.length];
		for (int i = 0; i < objData.length; i++) {
			strData[i] = (MixerName) objData[i];
		}
		return strData;
	}

}
