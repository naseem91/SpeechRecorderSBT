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
 * Date  : Oct 31, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class AudioFileFormatChooser extends AudioFormatChooser{

    protected AudioFileFormat audioFileFormat;
    protected Type[] availTypes;
    protected JComboBox fileTypeBox;
    protected Type fileType;
    
    
    public AudioFileFormatChooser(){
        super();
        fileType=AudioFileFormat.Type.WAVE;
        audioFileFormat=new AudioFileFormat(fileType,getAudioFormat(),AudioSystem.NOT_SPECIFIED);
        create();
    }
    
    /**
     * 
     */
    public AudioFileFormatChooser(AudioFormat audioFormat,Type fileType) {
        super(audioFormat);
        this.fileType=fileType;
        audioFileFormat=new AudioFileFormat(fileType,audioFormat,AudioSystem.NOT_SPECIFIED);
        create();
        
    }
    
    private void create(){
        
        JLabel fileFormatLabel=new JLabel("File format");
        c.gridx=0;
        c.gridy++;
        add(fileFormatLabel,c);
        availTypes=AudioSystem.getAudioFileTypes();
        fileTypeBox=new JComboBox(availTypes);
       c.gridx++;
       add(fileTypeBox,c);
    }

    public static AudioFileFormat showDialog(Component parent,
            AudioFileFormat initialFormat) {
        AudioFileFormatChooser afc = new AudioFileFormatChooser();
        afc.setAudioFileFormat(initialFormat);
        JOptionPane selPane = new JOptionPane(afc, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);
        JDialog d = selPane.createDialog(parent, "Audio file format chooser");
        d.setVisible(true);
        Object selectedValue = selPane.getValue();
        if (selectedValue == null)
            return initialFormat;
        if (selectedValue instanceof Integer) {
            int value = ((Integer) selectedValue).intValue();
            if (value == JOptionPane.OK_OPTION) {
                return afc.getAudioFileFormat();
            } else {
                return initialFormat;
            }
        }
        return initialFormat;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public synchronized void actionPerformed(ActionEvent ev) {
        AudioFileFormat newAudioFileFormat = getAudioFileFormat();
        if (!audioFileFormat.equals(newAudioFileFormat)) {
            audioFileFormat = newAudioFileFormat;
            Enumeration<ChangeListener> e = listeners.elements();
            while (e.hasMoreElements()) {
                ChangeListener cl = (ChangeListener) e.nextElement();
                cl.stateChanged(new ChangeEvent(this));
            }
        }
    }

    public AudioFileFormat.Type getFileType(){
        if (fileTypeBox==null) return null;
        return (AudioFileFormat.Type)(fileTypeBox.getSelectedItem());
    }
    
    public AudioFileFormat getAudioFileFormat() {
        fileType=getFileType();
        audioFileFormat=new AudioFileFormat(fileType,getAudioFormat(),audioFileFormat.getFrameLength());
        return audioFileFormat;
    }
    public void setAudioFileFormat(AudioFileFormat audioFileFormat) {
        fileType=audioFileFormat.getType();
        fileTypeBox.setSelectedItem(fileType);
        setAudioFormat(audioFileFormat.getFormat());
        this.audioFileFormat = audioFileFormat;
    }
}
