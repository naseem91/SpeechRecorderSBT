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
 * Created on May 3, 2004
 *
 * Project: JSpeechRecorder
 * Original author: draxler
 */
package ipsk.apps.audioeditor.ui;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author draxler
 *
 * InfoViewer displays the key parameters for a given session, i.e.
 * <ul>
 * 	<li>recording script file name,</li>
 * 	<li>speaker data, and</li>
 * 	<li>target recording directory.</li>
 * </ul>
 * InfoViewer obtains this data from SpeechRecorder which thus
 * serves as the data model.
 * 
 */
public class InfoViewer extends JPanel {

	private final static int LEFT = 5;
	private final static int RIGHT = 5;
	private final static int TOP = 5;
	private final static int BOTTOM = 5;

	
	private JLabel recFileLabel;

	private JLabel recFileValue;

	public InfoViewer() {
		super();
		
		setBorder(BorderFactory.createEmptyBorder(LEFT, TOP, RIGHT, BOTTOM));
		setLayout(new GridBagLayout());
		GridBagConstraints c=new GridBagConstraints();
        c.gridwidth=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 5, 2, 5);
        c.anchor = GridBagConstraints.PAGE_START;
        
       
		recFileLabel = new JLabel("Recording File: ");
		recFileValue = new JLabel("");
		
		c.gridx=0;
        c.gridy=0;
        add(new JLabel("Operating system: "),c);
        c.gridx++;
        add(new JLabel(System.getProperty("os.name") + " "
        		+System.getProperty("os.version") + " "
                + System.getProperty("os.arch")),c);
        
        c.gridx=0;
        c.gridy++;
        add(new JLabel("Java Runtime Environment: "),c);
        c.gridx++;
        add(new JLabel(System.getProperty("java.vendor") + " "
                + System.getProperty("java.version")),c);
        
        c.gridx=0;
        c.gridy++;
        
        // load utils dummy class
        try {
			Class.forName("ipsk.util.LocalizableMessage");
		} catch (ClassNotFoundException e) {
			// OK, but should work
			e.printStackTrace();
		}
        Package javaUtilsPkg=Package.getPackage("ipsk.util");
        
        String utilLibVers="n/a";
        if(javaUtilsPkg!=null) utilLibVers=javaUtilsPkg.getImplementationVersion();
        add(new JLabel(javaUtilsPkg.getImplementationTitle()+":"),c);
        c.gridx++; 
        add(new JLabel(utilLibVers),c);
        
        c.gridx=0;
        c.gridy++;
        Package javaAudioToolsPkg=Package.getPackage("ipsk.audio");
        String audioLibVers="n/a";
        if(javaAudioToolsPkg!=null)audioLibVers=javaAudioToolsPkg.getImplementationVersion();
        add(new JLabel(javaAudioToolsPkg.getImplementationTitle()+":"),c);
        c.gridx++; 
        add(new JLabel(audioLibVers),c);
       

        c.gridx=0;
        c.gridy++;
		add(recFileLabel,c);
        c.gridx++;
		add(recFileValue,c);
		setRecordingFile(null);
	}
	
	
	public void setRecordingFile(File recFile){
		String recFileName="";
		if (recFile !=null){
			recFileName=recFile.getAbsolutePath();
		}
      
      recFileValue.setText(recFileName);
      revalidate();
      repaint();
	}

	
}
