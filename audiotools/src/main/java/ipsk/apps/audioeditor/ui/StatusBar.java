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

/*
 * Date  : Oct 25, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.audioeditor.ui;

import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.dsp.AudioClipDSPInfo;
import ipsk.awt.StatusBarLayout;
import ipsk.text.MediaTimeFormat;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Status bar component at bottom of audioeditor.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class StatusBar extends JPanel {

    private Border b;
    private JLabel sampleRateLabel;
    private JLabel sampleSizeInBitsLabel;
    private JLabel frameLengthLabel;
    private MediaTimeFormat mtf;
    private AudioFormat audioFormat=null;
 
    
    public StatusBar() {
        super(new StatusBarLayout());
       b=BorderFactory.createLoweredBevelBorder();
      sampleRateLabel=new JLabel();
      sampleRateLabel.setBorder(b);
      sampleRateLabel.setToolTipText("Sample rate");
      add(sampleRateLabel,StatusBarLayout.ALIGN_LEFT);
      sampleSizeInBitsLabel=new JLabel();
      sampleSizeInBitsLabel.setBorder(b);
      sampleSizeInBitsLabel.setToolTipText("Sample size");
      add(sampleSizeInBitsLabel,StatusBarLayout.ALIGN_LEFT);
      frameLengthLabel=new JLabel("Unknown");
      frameLengthLabel.setBorder(b);
      frameLengthLabel.setToolTipText("Length of audio file (hour:min:sec.msec)");
      mtf=new MediaTimeFormat();
      add(frameLengthLabel,StatusBarLayout.ALIGN_LEFT);
    }
 
    public void setAudioClipInfo(AudioClipDSPInfo info){
       
        setAudioFormat(info.getAudioFormat());
    }
    
    public void setAudioFormat(AudioFormat af){
    	audioFormat=af;
        if (af==null){
            sampleRateLabel.setText("-");
            sampleSizeInBitsLabel.setText("-");
        }else{
        sampleRateLabel.setText(Float.toString(af.getSampleRate())+" Hz");
        sampleSizeInBitsLabel.setText(Integer.toString(af.getSampleSizeInBits())+ " bit");
        }
        revalidate();
    }
    public void setFrameLength(long frameLength){
    	Object formatObj = null;
		if (frameLength != ThreadSafeAudioSystem.NOT_SPECIFIED && audioFormat!=null) {
			double seconds = frameLength / audioFormat.getFrameRate();
			formatObj = new Double(seconds);
			frameLengthLabel.setText(mtf.format(formatObj));
		}else{
		frameLengthLabel.setText(mtf.format(null));
		}
    	
    	
    }
    public void setAudioFileFormat(AudioFileFormat aff){
    	setAudioFormat(aff.getFormat());
    	setFrameLength(aff.getFrameLength());
    }
    

 
    public static void main(String[] args){
        JFrame f=new JFrame();
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(new JLabel("Hello"),BorderLayout.CENTER);
        f.getContentPane().add(new StatusBar(),BorderLayout.SOUTH);
        f.pack();
        f.setVisible(true);
    }

}
