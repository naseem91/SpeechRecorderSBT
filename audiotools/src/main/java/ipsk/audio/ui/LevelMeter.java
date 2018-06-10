//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.audio.ui;

import ipsk.audio.dsp.LevelInfo;
import ipsk.audio.dsp.LevelInfosBean;
import ipsk.audio.view.AudioStatus;
import ipsk.audio.view.LevelDisplay;
import ipsk.awt.JScale.Orientation;
import ipsk.swing.JAutoScale;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class LevelMeter extends JPanel implements ActionListener, PropertyChangeListener {

	final static boolean DEBUG = false;

	public final static String ACTION_PEAK_HOLD_RESET_CMD = "Reset peak hold";

	private JLabel scaleLabel;

	private JComboBox resButt;

	private int nrChannels;
	
	private LevelInfosBean levelInfosBean;

	private LevelDisplay[] ld;
	
	// backward compatibility:
	private boolean useIntervalPeakLevel=false;

	
	//private AudioController ac;
	private JPanel levels;

	private JButton peakResButt;

	private JAutoScale scale;

	private float minLevelIndB;

	private float DEFAULT_MIN_LEVEL = -60;

	private float[] scaleResolutions = { -100, -80, -60, -40, -20 };

	private Float[] scaleRes;

	private boolean scaleEnabled = false;

	private Vector<ActionListener> listeners;

	private ResourceBundle rb;
	
	private float transparency=1.0f;

	private AudioStatus as;
	
	private boolean showCaptureLevel=true;

	private JCheckBoxMenuItem showCaptureLvlChBx;

	private JPopupMenu menu;

	public LevelMeter() {
		super();
		String packageName = getClass().getPackage().getName();
		rb = ResourceBundle.getBundle(packageName + ".ResBundle");
		listeners = new Vector<ActionListener>();
		minLevelIndB = DEFAULT_MIN_LEVEL;
		//twentydBSectors = (int) (-minLevelIndB / 20);
		scaleRes = new Float[scaleResolutions.length];
		for (int i = 0; i < scaleResolutions.length; i++) {
			scaleRes[i] = new Float(scaleResolutions[i]);
		}
		resButt = new JComboBox(scaleRes);
		resButt.setSelectedIndex(2); // -60dB default
		resButt.addActionListener(this);
		levels = new JPanel();
		nrChannels = 2;
		as = new AudioStatus();
		create();
	}

	public void setAudioFormat(AudioFormat af) {
		if (af == null)
			nrChannels = 1;
		else {
			nrChannels = af.getChannels();
		}
		create();
	}
	
	
    
	private void create() {
        levels.removeAll();
		removeAll();
		levels.setLayout(new GridLayout(1, nrChannels + 1));
		scale = new JAutoScale(Orientation.WEST, 0, (long) -minLevelIndB - 1);
		scale.setEnabled(false);
		levels.add(scale);

		ld = new LevelDisplay[nrChannels];
		for (int ch = 0; ch < nrChannels; ch++) {
			ld[ch] = new LevelDisplay(minLevelIndB);
			ld[ch].setUseIntervalPeakLevel(useIntervalPeakLevel);
			levels.add(ld[ch]);
		}
		setTransparency(transparency);
		setLayout(new GridBagLayout());
		GridBagConstraints gcLevels = new GridBagConstraints();
		gcLevels.gridx = 0;
		gcLevels.gridy = 0;
		gcLevels.fill = GridBagConstraints.BOTH;
		gcLevels.weighty = 1.0;
		gcLevels.gridwidth=2;
		add(levels, gcLevels);
		//levels.revalidate();
		JPanel legend = new JPanel(new GridLayout(1, nrChannels + 1));
		scaleLabel = new JLabel("-dB");

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx=0.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridwidth=2;
		legend.add(scaleLabel);
		for (int i = 0; i < nrChannels; i++) {
			legend.add(new JLabel(""));
		}

		add(legend, constraints);

		constraints.gridy = 2;
		add(resButt, constraints);

		//peakResButt = new JButton(rb.getString("reset"));
		peakResButt = new JButton("R");
		peakResButt.setToolTipText(rb.getString("reset"));
		peakResButt.addActionListener(this);
		constraints.gridy = 3;
		constraints.gridwidth=1;
		add(peakResButt, constraints);

		constraints.gridx++;
		
		add(as,constraints);
		
		setScaleEnabled(scaleEnabled);
		
		revalidate();
		repaint();
	}
	
	private void createPopuMenu(){
		menu = new JPopupMenu();
		showCaptureLvlChBx = new JCheckBoxMenuItem("Show level during recording pauses");
		showCaptureLvlChBx.setSelected(true);
		menu.add(showCaptureLvlChBx);
		showCaptureLvlChBx.addActionListener(this);
		setComponentPopupMenu(menu);
	}
	
	public void setAudioStatus(AudioStatus.Status status){
		as.setStatus(status);
		// popup menu currently only required for deactivating level meter in capture status
		// if audio status is not set the popup menu is never build
		if(menu==null){
			createPopuMenu();
		}
		updateActivation();
	}
    
	 /**
     * @return the transparency
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * @param transparency the transparency to set
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
        if(ld!=null){
            for(LevelDisplay l:ld){
                l.setTransparency(transparency);
            }
        }
    }

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == peakResButt) {
			resetPeakHold();
			ActionEvent peakResetAction = new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, ACTION_PEAK_HOLD_RESET_CMD);
			updateListeners(peakResetAction);
		} else if (ae.getSource() == resButt) {

			minLevelIndB = ((Float) (resButt.getSelectedItem())).floatValue();
			float[] savePeak = new float[nrChannels];
			for (int i = 0; i < nrChannels; i++) {
				savePeak[i] = ld[i].getPeakHold();
			}

			create();
			for (int i = 0; i < nrChannels; i++) {
				ld[i].setPeakHold(savePeak[i]);
			}

		}else if (ae.getSource() == showCaptureLvlChBx) {
			updateActivation();
		}

	}
	
	private void updateActivation(){
		// check capture mode
		if(as!=null && AudioStatus.Status.CAPTURE.equals(as.getStatus())){
			setActive(showCaptureLvlChBx.isSelected());
		}else{
			setActive(true);
		}
	}

	public void setScaleEnabled(boolean enabled) {
		scaleEnabled = enabled;
		scale.setEnabled(enabled);
		scaleLabel.setEnabled(enabled);

	}
	
	private void setActive(boolean active){
		
		for(LevelDisplay l:ld){
			l.setActive(active);
		}
	}

	public void abandonDecay(){
		if(ld!=null){
		for(LevelDisplay l:ld){
			l.abandonDecay();
		}
		}
	}
	
	public void resetPeakHold() {
		for (int ch = 0; ch < nrChannels; ch++) {
			ld[ch].resetPeakHold();
		}
	}

	public void setLevels(float[] levels) {

		for (int ch = 0; ch < nrChannels; ch++) {

			if (levels == null || levels.length <= ch) {
				ld[ch].setLevel(0);
				if (DEBUG)
					System.out.println("level null");
			} else {

				ld[ch].setLevel(levels[ch]);
				if (DEBUG)
					System.out.println("level " + ch + ": " + levels[ch]);
			}
		}
	}

	
	
	public void setLevelInfos(LevelInfo[] levelInfos) {
	    setLevelInfos(levelInfos, false);
	}
	public void setLevelInfos(LevelInfo[] levelInfos,boolean listenToPropertyChanges) {
		for (int ch = 0; ch < nrChannels; ch++) {
			if (levelInfos == null || levelInfos.length <= ch) {
				ld[ch].setLevelInfo(new LevelInfo(),listenToPropertyChanges);
				if (DEBUG)
					System.out.println("level null");
			} else {

				ld[ch].setLevelInfo(levelInfos[ch],listenToPropertyChanges);
				if (DEBUG)
					System.out.println("level " + ch + ": " + levelInfos[ch]);
			}
		}
	}
	
	public void setLevelInfosBean(LevelInfosBean levelInfosBean) {
	    if(this.levelInfosBean!=null){
	        this.levelInfosBean.removePropertyChangeListener(this);
	    }
	    this.levelInfosBean=levelInfosBean;
	    if(this.levelInfosBean!=null){
	        setLevelInfos(this.levelInfosBean.getLevelInfos(),true);
	        this.levelInfosBean.addPropertyChangeListener(this);
	    }
    }
	
	public LevelInfosBean getLevelInfosBean() {
	        return levelInfosBean;
	}
	
	/**
	 * @return the useIntervalPeakLevel
	 */
	public boolean isUseIntervalPeakLevel() {
		return useIntervalPeakLevel;
	}

	/**
	 * @param useIntervalPeakLevel the useIntervalPeakLevel to set
	 */
	public void setUseIntervalPeakLevel(boolean useIntervalPeakLevel) {
		this.useIntervalPeakLevel = useIntervalPeakLevel;
		if(ld!=null){
			for(LevelDisplay l:ld){
				l.setUseIntervalPeakLevel(useIntervalPeakLevel);
			}
		}
	}


	public synchronized void addActionListener(ActionListener actionEvent) {
		if (actionEvent != null && !listeners.contains(actionEvent)) {
			listeners.addElement(actionEvent);
		}
	}

	public synchronized void removeActionListener(ActionListener actionEvent) {
		if (actionEvent != null) {
			listeners.removeElement(actionEvent);
		}
	}

	protected synchronized void updateListeners(ActionEvent actionEvent) {

		Iterator<ActionListener> it = listeners.iterator();
		while (it.hasNext()) {
			ActionListener listener = it.next();
			listener.actionPerformed(actionEvent);

		}
	}

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        setLevelInfos(this.levelInfosBean.getLevelInfos(),true);
    }
    
    public static void main(String[] args){
        final LevelMeter lm=new LevelMeter();
        Runnable sr=new Runnable() {
            
            @Override
            public void run() {
                JFrame f=new JFrame();
                f.getContentPane().add(lm);
                f.pack();
                f.setVisible(true);
           
                
            }
        };
        SwingUtilities.invokeLater(sr);
        LevelInfo[] lifs=new LevelInfo[1];
        lifs[0]=new LevelInfo(0.3f, 0.9f);
        lm.setLevelInfos(lifs);
    }

   
   
}
