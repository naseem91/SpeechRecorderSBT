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
 * Created on Jan 23, 2007
 *
 */
package ipsk.audio.dsp.ui;

import ipsk.audio.dsp.AudioClipDSPInfo;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;

import javax.sound.sampled.AudioFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AudioClipDSPInfoViewer extends JPanel {

    private static DecimalFormat LEN_SECONDS_FORMAT=new DecimalFormat("0.000 s");
    private static DecimalFormat AMPL_LOG_FORMAT=new DecimalFormat("0.00 db");
    private static DecimalFormat AMPL_LIN_FORMAT=new DecimalFormat("0.0000");
    private static DecimalFormat SNR_LOG_FORMAT=new DecimalFormat("0.00 db");

    private AudioClipDSPInfo audioClipDSPInfo;
    
    public AudioClipDSPInfoViewer(AudioClipDSPInfo info) {
    	super(new GridBagLayout());
    	this.audioClipDSPInfo=info;
    	if(info!=null){
    		GridBagConstraints c = new GridBagConstraints();
    		c.fill = GridBagConstraints.HORIZONTAL;
    		c.insets = new Insets(2, 5, 2, 5);
    		c.anchor = GridBagConstraints.PAGE_START;
    		c.gridx = 0;
    		c.gridy = 0;
    		add(new JLabel("Length: "), c);
    		c.gridx++;
    		add(new JLabel(LEN_SECONDS_FORMAT.format(info.getFrameLengthInSeconds())+" , "+info.getFrameLength()+" frames"), c);
    		c.gridx = 0;
    		c.gridy++;

    		AudioFormat af = info.getAudioFormat();

    		int channels = af.getChannels();
    		add(new JLabel("Format: "), c);
    		c.gridx++;
    		add(new JLabel(af.getSampleRate()+" Hz, "+af.getSampleSizeInBits()+" bit, "), c);
    		c.gridx = 0;
    		c.gridy++;
    		double[] maxAmpl = info.getMaxAmplitudes();
    		double[] minAmpl = info.getMinAmplitudes();
    		double[] maxLogAmpl=info.getMaxLogarithmLevels();
    		double[] minLogAmpl=info.getMinLogarithmLevels();
    		Double[] snrs=info.getEstimatedLogarithmSignalToBackgroundNoiseRatio();
    		for (int i = 0; i < channels; i++) {
    			add(new JLabel("Channel "+Integer.toString(i)+":"), c);
    			c.gridx=0;
    			c.gridy++;
    			add(new JLabel("Max amplitude:"), c);
    			c.gridx++;
    			double maxAmplNorm1=maxAmpl[i]*2;
    			JLabel maxLabel=new JLabel(AMPL_LIN_FORMAT.format(maxAmplNorm1)+" ("+AMPL_LOG_FORMAT.format(maxLogAmpl[i])+")");
    			if (Math.abs(maxAmplNorm1) >=0.999){
    				maxLabel.setForeground(Color.RED);
    			}
    			add(maxLabel, c); 

    			c.gridx=0;
    			c.gridy++;
    			add(new JLabel("Min amplitude:"), c);
    			c.gridx++;
    			double minAmplNorm1=minAmpl[i]*2;
    			JLabel minLabel=new JLabel(AMPL_LIN_FORMAT.format(minAmplNorm1)+" ("+AMPL_LOG_FORMAT.format(minLogAmpl[i])+")");
    			if (Math.abs(minAmplNorm1) >=0.999){
    				minLabel.setForeground(Color.RED);
    			}
    			add(minLabel, c); 
    			if(snrs!=null && snrs[i]!=null){

    				c.gridx=0;
    				c.gridy++;
    				add(new JLabel("(Estimated SNR: "), c);
    				c.gridx++;
    				JLabel snrLabel=new JLabel(SNR_LOG_FORMAT.format(snrs[i])+")");
    				add(snrLabel,c);
    			}

    			c.gridx=0;
    			c.gridy++;
    		}
    	}
    }

}
