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
 * Date  : May 5, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.ui;

import ipsk.audio.Profile;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * UI to choose an audio format.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class AudioFormatChooser extends JPanel implements ActionListener {
    final static boolean DEBUG = false;

   
    
    private final static int MAX_CHANNELS = 32;

    private final static int MAX_FRAME_SIZE_BYTES = 4;
    
    protected GridBagConstraints c = new GridBagConstraints();

    private String UNKNOWN_STRING = new String("Unknown");

    private JComboBox sampleSizeBox;

    private AudioFormat audioFormat;

    private float[] sampleRatesF = { 8000, 11025, 16000, 22050, 32000, 44100, 48000,
            96000 };

    private Float[] sampleRates;

    private Integer[] numChannelArr = new Integer[MAX_CHANNELS];
    
    private JComboBox endianBox;
    private String bigEndianStr;
    private String littleEndianStr;
    
    private String[] byteOrderStrs={UNKNOWN_STRING,bigEndianStr,littleEndianStr};

    private AudioFormat.Encoding[] encodings = {
            AudioFormat.Encoding.PCM_SIGNED, AudioFormat.Encoding.PCM_UNSIGNED,
            AudioFormat.Encoding.ALAW, AudioFormat.Encoding.ULAW };

    //private String[] signedStrs = { "Signed", "Unsigned" };

    private Integer[] frameSizes = new Integer[MAX_FRAME_SIZE_BYTES];

    

    private JComboBox sampleRateBox;


    private JComboBox numChannelsBox;


    private JComboBox encodingBox;


    private boolean enabled = true;

    protected  Vector<ChangeListener> listeners;

    protected  ResourceBundle resourceBundle;
    
    private Profile profile=null;
    
//    private boolean restrictUserSettingsToProfileMinimumRequirements=false;
    
    public static float DEF_SAMPLE_RATE=44100;
    public static int DEF_SAMPLE_SIZE_IN_BITS=16;
    public static int DEF_CHANNELS=2;
    public static boolean DEF_SIGNED=true;
    public static boolean DEF_BIG_ENDIAN=false;
    public static AudioFormat DEF_AUDIOFORMAT=new AudioFormat(DEF_SAMPLE_RATE, DEF_SAMPLE_SIZE_IN_BITS, DEF_CHANNELS, DEF_SIGNED, DEF_BIG_ENDIAN);
    
    public AudioFormatChooser() {
        this(DEF_AUDIOFORMAT);
    }

   

    public AudioFormatChooser(AudioFormat audioFormat) {
        super(new GridBagLayout());
        
        this.audioFormat = audioFormat;
       
        ComboBoxModel<String> sampleRateModel;
        String packageName = getClass().getPackage().getName();
        resourceBundle = ResourceBundle.getBundle(packageName + ".ResBundle");

        
		c.fill = GridBagConstraints.HORIZONTAL;

		c.insets = new Insets(2, 5, 2, 5);
		
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
       
        listeners = new Vector<ChangeListener>();
        
        JLabel sampleRateLabel = new JLabel(resourceBundle.getString("samplerate")+":");
        add(sampleRateLabel,c);
        sampleRates = new Float[sampleRatesF.length];
        for (int i = 0; i < sampleRatesF.length; i++) {
            sampleRates[i] = new Float(sampleRatesF[i]);

        }
        sampleRateBox = new JComboBox(sampleRates);
//        sampleRateBox.addItem(UNKNOWN_STRING);
        sampleRateBox.setEnabled(enabled);
        c.gridx++;
        add(sampleRateBox,c);
        c.gridx++;
        add(new JLabel("Hz"),c);
        
        JLabel channelLabel = new JLabel(resourceBundle.getString("channels")+":");
        c.gridx=0;
        c.gridy++;
        add(channelLabel,c);
        for (int i = 0; i < MAX_CHANNELS; i++) {
            numChannelArr[i] = new Integer(i+1);

        }
        numChannelsBox = new JComboBox(numChannelArr);
        c.gridx++;
        add(numChannelsBox,c);
        
        
        JLabel endianLabel = new JLabel(resourceBundle.getString("byteorder")+":");
        c.gridx=0;
        c.gridy++;
      add(endianLabel,c);
        bigEndianStr=resourceBundle.getString("bigEndian");
        littleEndianStr=resourceBundle.getString("littleEndian");
//        byteOrderStrs=new String[]{bigEndianStr,littleEndianStr,UNKNOWN_STRING};
        byteOrderStrs=new String[]{bigEndianStr,littleEndianStr};
        endianBox=new JComboBox(byteOrderStrs);
        
        c.gridx++;
        add(endianBox,c);
        
        JLabel encodingLabel = new JLabel(resourceBundle.getString("encoding")+":");
        //createRadioButtonGroup(signedPanel, signedStrs);
        c.gridx=0;
        c.gridy++;
        add(encodingLabel,c);
        encodingBox = new JComboBox(encodings);
//        encodingBox.addItem(UNKNOWN_STRING);
       
       
        c.gridx++;
        c.weightx=1.0;
        add(encodingBox,c);

        JLabel sampleSizeLabel= new JLabel(resourceBundle.getString("samplesize")+":");
        c.gridx=0;
        c.gridy++;
        add(sampleSizeLabel,c);
        for (int i = 0; i < MAX_FRAME_SIZE_BYTES; i++) {
            frameSizes[i] = (i+1)*8;
        }
        sampleSizeBox = new JComboBox(frameSizes);
        
        c.gridx++;
        add(sampleSizeBox,c);
        c.gridx++;
        add(new JLabel("bits"),c);
        
        
        setAudioFormat(audioFormat);
        
        sampleRateBox.addActionListener(this);
        numChannelsBox.addActionListener(this);
        endianBox.addActionListener(this);
        encodingBox.addActionListener(this);
        sampleSizeBox.addActionListener(this);
        
        revalidate();
        repaint();
    }

    void createRadioButtonGroup(JPanel target, String[] buttonNames) {
        ButtonGroup bg = new ButtonGroup();
        int numButtons = buttonNames.length;
        JRadioButton[] radioButtons = new JRadioButton[numButtons];
        for (int i = 0; i < numButtons; i++) {
            radioButtons[i] = new JRadioButton(buttonNames[i]);
            target.add(radioButtons[i]);
            bg.add(radioButtons[i]);
        }

    }
    
  

    public void setAudioFormat(AudioFormat newFormat) {
        if (newFormat == null) {
//            sampleRateBox.setSelectedItem(UNKNOWN_STRING);
//            numChannelsBox.setSelectedItem(new Integer(DEF_CHANNELS));
//            endianBox.setSelectedItem(UNKNOWN_STRING);
//           
//            encodingBox.setSelectedItem(UNKNOWN_STRING);
//            sampleSizeBox.setSelectedItem(new Integer(DEF_SAMPLE_SIZE_IN_BITS));
            newFormat=DEF_AUDIOFORMAT;
        } 
//        else 
//        {

            sampleRateBox.setSelectedItem(new Float(newFormat.getSampleRate()));
            numChannelsBox
                    .setSelectedItem(new Integer(newFormat.getChannels()));
            if (newFormat.isBigEndian()) {
                endianBox.setSelectedItem(bigEndianStr);
            } else {
                endianBox.setSelectedItem(littleEndianStr);
            }
            encodingBox.setSelectedItem(newFormat.getEncoding());
            sampleSizeBox.setSelectedItem(new Integer(newFormat.getSampleSizeInBits()));
//        }
        audioFormat = newFormat;

    }

    public AudioFormat getAudioFormat() {
        
        AudioFormat.Encoding ae = ((AudioFormat.Encoding) encodingBox
                .getSelectedItem());
        float sampleRate = ((Float) sampleRateBox.getSelectedItem())
                .floatValue();
        int channels = ((Integer) numChannelsBox.getSelectedItem()).intValue();
        boolean bigEndian = (endianBox.getSelectedItem() ==bigEndianStr);

        int sampleSizeInBits = ((Integer) sampleSizeBox.getSelectedItem())
                .intValue();
        int frameSize = (sampleSizeInBits / 8) * channels;
        AudioFormat af = new AudioFormat(ae, sampleRate, sampleSizeInBits,
                channels, frameSize, sampleRate, bigEndian);

        if (DEBUG)
            System.out.println(af);
        return af;
    }

    
   
    
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev) {
        AudioFormat newAudioFormat = getAudioFormat();
//        checkWarnings(newAudioFormat);
        if (!audioFormat.equals(newAudioFormat)) {
            audioFormat = newAudioFormat;
            Enumeration<ChangeListener> e = listeners.elements();
            while (e.hasMoreElements()) {
                ChangeListener cl = (ChangeListener) e.nextElement();
                cl.stateChanged(new ChangeEvent(this));
            }
        }
    }

    public void addChangeListener(ChangeListener cl) {
        if (cl != null && !listeners.contains(cl)) {
            synchronized (listeners) {
                listeners.addElement(cl);
            }
        }
    }

    public void removeChangeListener(ChangeListener cl) {
        if (cl != null) {
            synchronized (listeners) {
                listeners.removeElement(cl);
            }
        }
    }

    public static AudioFormat showDialog(Component parent,
            AudioFormat initialFormat) {
        AudioFormatChooser afc = new AudioFormatChooser();
        afc.setAudioFormat(initialFormat);
        JOptionPane selPane = new JOptionPane(afc, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);
        JDialog d = selPane.createDialog(parent, "Audio format chooser");
        d.setVisible(true);
        Object selectedValue = selPane.getValue();
        if (selectedValue == null)
            return initialFormat;
        if (selectedValue instanceof Integer) {
            int value = ((Integer) selectedValue).intValue();
            if (value == JOptionPane.OK_OPTION) {
                return afc.getAudioFormat();
            } else {
                return initialFormat;
            }
        }
        return initialFormat;
    }

    public static void main(String args[]) {
        JFrame f = new JFrame("AudioFormatChooser");
        AudioFormatChooser afc = new AudioFormatChooser();
        f.getContentPane().add(afc);
        f.pack();
        f.setVisible(true);
        afc.setProfile(Profile.SPEECH_RECORDING);
        afc.setAudioFormat(new AudioFormat(11025, 16, 1, true, true));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        afc.setAudioFormat(new AudioFormat(96000, 8, 5, false, false));
    }



    public Profile getProfile() {
        return profile;
    }



    public void setProfile(Profile profile) {
        this.profile = profile;
    }



//    public boolean isRestrictUserSettingsToProfileMinimumRequirements() {
//        return restrictUserSettingsToProfileMinimumRequirements;
//    }
//
//
//
//    public void setRestrictUserSettingsToProfileMinimumRequirements(
//            boolean restrictUserSettingsToProfileMinimumRequirements) {
//        this.restrictUserSettingsToProfileMinimumRequirements = restrictUserSettingsToProfileMinimumRequirements;
//    }

}
