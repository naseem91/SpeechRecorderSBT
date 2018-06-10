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
 * Created on May 3, 2004
 *
 * Project: JSpeechRecorder
 * Original author: draxler
 */
package ipsk.apps.speechrecorder.ui;

import ipsk.apps.speechrecorder.SpeechRecorder;
import ipsk.util.LocalizableMessage;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
public class InfoViewer extends JPanel implements ActionListener {

	private final static int LEFT = 5;
	private final static int RIGHT = 5;
	private final static int TOP = 5;
	private final static int BOTTOM = 5;

	private SpeechRecorder speechRecorder;
	private JLabel projectPathLabel;
	private JLabel projectPathValue;
	private JButton projectPathOpenButt;
	private JLabel recScriptLabel;
	private JLabel recDirLabel;

	private JLabel recScriptValue;
	private JLabel recDirValue;

	public InfoViewer(SpeechRecorder sr) {
		super();
		speechRecorder = sr;

		setBorder(BorderFactory.createEmptyBorder(LEFT, TOP, RIGHT, BOTTOM));
		setLayout(new GridBagLayout());
		GridBagConstraints c=new GridBagConstraints();
        c.gridwidth=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 5, 2, 5);
        c.anchor = GridBagConstraints.PAGE_START;
        projectPathLabel = new JLabel("Project directory: ");
        projectPathValue = new JLabel("");
        projectPathOpenButt=new JButton("Open in file manager");
        projectPathOpenButt.addActionListener(this);
        recScriptLabel = new JLabel("Script file: ");
		recDirLabel = new JLabel("Recording directory: ");
		recScriptValue = new JLabel("");
		recDirValue = new JLabel("");
        
        c.gridx=0;
        c.gridy=0;
        add(new JLabel("Java Runtime Environment: "),c);
        c.gridx++;
        add(new JLabel(System.getProperty("java.vendor") + " "
                + System.getProperty("java.version")),c);
        
        c.gridx=0;
        c.gridy++;
        
        // load utils dummy class
        try {
			Class.forName(LocalizableMessage.class.getName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Package javaUtilsPkg=Package.getPackage("ipsk.util");
        
        String utilLibVers="n/a";
        if(javaUtilsPkg!=null) utilLibVers=javaUtilsPkg.getImplementationVersion();
        add(new JLabel("Utils library:"),c);
        c.gridx++; 
        add(new JLabel(utilLibVers),c);
        c.gridx=0;
        c.gridy++;
        
        Package javaSpeechDbPkg=Package.getPackage("ipsk.db.speech");
        String speechDbLibVers="n/a";
        if(javaSpeechDbPkg!=null) speechDbLibVers=javaSpeechDbPkg.getImplementationVersion();
        add(new JLabel("Speech DB library:"),c);
        c.gridx++; 
        add(new JLabel(speechDbLibVers),c);
        c.gridx=0;
        c.gridy++;
        
        Package speechDbToolsPkg=Package.getPackage("ips.annot");
        String speechDbToolsLibVers="n/a";
        if(speechDbToolsPkg!=null) speechDbToolsLibVers=speechDbToolsPkg.getImplementationVersion();
        add(new JLabel("Speech DB Tools library:"),c);
        c.gridx++; 
        add(new JLabel(speechDbToolsLibVers),c);
        c.gridx=0;
        c.gridy++;
        
        Package javaAudioToolsPkg=Package.getPackage("ipsk.audio");
        String audioLibVers="n/a";
        if(javaAudioToolsPkg!=null)audioLibVers=javaAudioToolsPkg.getImplementationVersion();
        add(new JLabel("Audio tool library:"),c);
        c.gridx++; 
        add(new JLabel(audioLibVers),c);
       
        c.gridx=0;
        c.gridy++;
        Package speechRecorderPkg=Package.getPackage("ipsk.apps.speechrecorder");
        String speechrecLibVersion="n/a";
        if (speechRecorderPkg!=null) speechrecLibVersion=speechRecorderPkg.getImplementationVersion();
        add(new JLabel("Speechrecorder library:"),c);
        c.gridx++; 
        add(new JLabel(speechrecLibVersion),c);
        
        c.gridx=0;
        c.gridy++;
        add(projectPathLabel,c);
        c.gridx++;
        add(projectPathValue,c);
        c.gridx++;
        add(projectPathOpenButt,c);
        c.gridx=0;
        c.gridy++;
		add(recScriptLabel,c);
        c.gridx++;
        c.gridwidth=2;
		add(recScriptValue,c);
        c.gridx=0;
        c.gridy++;
        c.gridwidth=1;
		add(recDirLabel,c);
        c.gridx++;
        c.gridwidth=2;
		add(recDirValue,c);
        setData();
	}

	
	private boolean projectPathOpenable(){
	    File projectPath=null;;
        try {
            projectPath = speechRecorder.getProjectDir();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    return (Desktop.isDesktopSupported() && projectPath !=null && projectPath.exists());
	}
	/**
	 * Sets the display values to the parameters
	 * provided
	 * 
	 */
	public void setData() {
	    File projectPath=null;;
        try {
            projectPath = speechRecorder.getProjectDir();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    String projectPathStr="";
	    if(projectPath!=null){
	        projectPathStr=projectPath.toString();
	    }
	    boolean openable=projectPathOpenable();
	    projectPathOpenButt.setEnabled(openable);   
	    
	    projectPathValue.setText(projectPathStr);    
        String recScriptName = speechRecorder.getRecScriptName();
        if (recScriptName == null) {
            recScriptName = new String("");
        }
        recScriptValue.setText(recScriptName);
        String recDirName = speechRecorder.getRecDirName();
        if (recDirName == null) {
            recDirName = new String("");
        }
        recDirValue.setText(recDirName);
		revalidate();
		repaint();
	}

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        if(Desktop.isDesktopSupported()){
            Desktop d=Desktop.getDesktop();
            File projectDir=null;
            try {
                projectDir=speechRecorder.getProjectDir();
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if(projectDir!=null){
            try {
                d.open(projectDir);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Could not open project directory:\n"+e.getLocalizedMessage(),
                        "Project directory open error", JOptionPane.ERROR_MESSAGE);
            }
            }
           
        }
    }
}
