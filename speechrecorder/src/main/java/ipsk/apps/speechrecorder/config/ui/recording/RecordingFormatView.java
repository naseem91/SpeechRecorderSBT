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
 * Date  : Jun 2, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config.ui.recording;

import ipsk.apps.speechrecorder.config.Format;
import ipsk.apps.speechrecorder.config.RecordingConfiguration;
import ipsk.audio.Profile;
import ipsk.audio.ui.AudioFormatChooser;
import ipsk.swing.panel.JConfigPanel;

import java.awt.BorderLayout;

import javax.sound.sampled.AudioFormat;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class RecordingFormatView extends JConfigPanel implements ChangeListener {

	private static final long serialVersionUID = 1L;

	private AudioFormatChooser afc;

//	private JPanel recCfgPanel;
	private RecordingConfiguration currentConfig;
	
	private ChannelAssignmentUI channelRoutingUI;
	
	private boolean changeByUser=true;
	
	public RecordingFormatView(ChannelAssignmentUI channelRoutingUI) {
		super();
		this.channelRoutingUI=channelRoutingUI;
		changeByUser=false;
		JPanel contentPane=getContentPane();

		contentPane.setLayout(new BorderLayout());

		afc = new AudioFormatChooser();
		afc.setProfile(Profile.SPEECH_RECORDING);
		afc.addChangeListener(this);
		contentPane.add(afc, BorderLayout.NORTH);
		changeByUser=true;
	}
	
	public void resetToInitial(){
        
        setRecordingConfiguration(currentConfig);
    }
	public void resetToDefaults(){
	    RecordingConfiguration defaultConfig=new RecordingConfiguration();
	    RecordingConfiguration oldCurrentConfig=currentConfig;
	    setRecordingConfiguration(defaultConfig);
	    // do not override current config with defaults
	    currentConfig=oldCurrentConfig;
	}

	/**
     * @param recordingConfiguration recording configuration
     */
    public void setRecordingConfiguration(RecordingConfiguration recordingConfiguration) {
    	changeByUser=false;
        AudioFormat af=getAudioFormat(recordingConfiguration.getFormat());
        afc.setAudioFormat(af);
        channelRoutingUI.setTargetChannelCount(af.getChannels());
       
        setDependencies();
        currentConfig=recordingConfiguration;
        changeByUser=true;
    }
    
	private AudioFormat getAudioFormat(Format f) {
	    return f.toAudioFormat();
	}
	

	
	public void applyValues(RecordingConfiguration r){
	    AudioFormat af = afc.getAudioFormat();
       
        Format f = r.getFormat();
        f.setEncoding(af.getEncoding().toString());
        f.setSampleRate((double) af.getSampleRate());
        f.setSampleSizeInBits(af.getSampleSizeInBits());
        f.setFrameSize(af.getFrameSize());
        f.setChannels(af.getChannels());
        f.setBigEndian(af.isBigEndian());
        channelRoutingUI.applyValues(r);
	}
	
	private void setDependencies() {
	}

    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    @Override
    public void stateChanged(ChangeEvent ce) {
        Object src = ce.getSource();
        if (src == afc) {
            // inform channel routing about potentially changed channel count 
            AudioFormat af=afc.getAudioFormat();
            
            int chs=af.getChannels();
            channelRoutingUI.setTargetChannelCount(chs);
            
            // Deferred to next version
//            
//            if(changeByUser && chs==1 && !channelRoutingUI.anyChannelRoutingSelected() && ){
//            	int ret=JOptionPane.showConfirmDialog(this,"Linux audio devices sometimes mix both stereo channels together for mono recording.\nThe dynamic range will be reduced to the half for each channel in such cases.\nDo you want to capture stereo and pick the first channel for the the final recording?","Mono recording warning",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
//            	if(ret==JOptionPane.YES_OPTION){
//            		channelRoutingUI.applyStereoCaptureForMonoRecording();
//            		JOptionPane.showMessageDialog(this,"Configured stereo capture for mono recording. You can change this setting in the \"Channel routing\" tab","Stereo to mono channel routing configuration",JOptionPane.INFORMATION_MESSAGE);
//            	}
//            }
        }
    }
	
}
