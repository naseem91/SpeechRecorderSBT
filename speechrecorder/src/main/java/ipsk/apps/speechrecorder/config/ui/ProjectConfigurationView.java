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

package ipsk.apps.speechrecorder.config.ui;

import ips.annot.BundleAnnotationPersistorServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ipsk.apps.speechrecorder.annotation.auto.AutoAnnotationPluginManager;
import ipsk.apps.speechrecorder.config.Annotation;
import ipsk.apps.speechrecorder.config.Control;
import ipsk.apps.speechrecorder.config.LoggingConfiguration;
import ipsk.apps.speechrecorder.config.ProjectConfiguration;
import ipsk.apps.speechrecorder.config.SpeakersConfiguration;
import ipsk.audio.AudioController2;
import ipsk.swing.JDialogPanel;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.List;

import javax.help.HelpBroker;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;


/**
 * Panel for Speechrecorder project configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class ProjectConfigurationView extends JDialogPanel implements ActionListener, WindowListener {

	private static final long serialVersionUID = 1L;
	private SpeakersView speakersView;
	private ProjectConfiguration project;
	private JTabbedPane tabPane;

	private ProjectConfigurationPanel projectPanel;
//	private ProjectAudioConfigPanel audioPanel;
	private RecordingView recView;
	private PlaybackView playView;
    private TransportView transportView;
	private PromptConfigurationView promptingView;
//	private ViewConfigurationUI viewUI;
	private AnnotationPanel annotationPanel;
    private LoggingConfigurationView loggingView;
    
 
	public ProjectConfigurationView(ProjectConfiguration project,AudioController2 audioController,Action[] actions,List<BundleAnnotationPersistorServiceDescriptor> bapsds,AutoAnnotationPluginManager autoAnnotationPluginManager,String defaultScriptUrl,HelpBroker helpBroker) {
		super(Options.OK_CANCEL,(helpBroker!=null));
		this.project = project;
		
		setFrameTitle("Project \""+project.getName()+"\" configuration");
		if(helpBroker!=null){
		    helpBroker.enableHelpOnButton(getHelpButton(), "subsection-29", null);
		}
		tabPane = new JTabbedPane();
		projectPanel = new ProjectConfigurationPanel(audioController);
//		audioPanel = new ProjectAudioConfigPanel(audioController);
		speakersView = new SpeakersView();
		recView = new RecordingView(audioController);
		playView=new PlaybackView(audioController);
        transportView=new TransportView(actions);
		promptingView = new PromptConfigurationView(audioController,transportView.getKeyInputMapView(),defaultScriptUrl);
//		viewUI=new ViewConfigurationUI();
		annotationPanel=new AnnotationPanel(bapsds,autoAnnotationPluginManager);
        loggingView=new LoggingConfigurationView();
		tabPane.addTab("Project", projectPanel);
//		tabPane.addTab("Audio",audioPanel);
		tabPane.addTab("Speakers", speakersView);
		tabPane.addTab("Recording", recView);
		tabPane.addTab("Playback",playView);
        tabPane.addTab("Control", transportView);
		tabPane.addTab("Prompting", promptingView);
//		tabPane.addTab("View", viewUI);
		tabPane.addTab("Annotation", annotationPanel);
        tabPane.addTab("Logging", loggingView);
		//add(tabPane,BorderLayout.CENTER);
        setContentPane(tabPane);
//		optionPanel=new JPanel();
//		okButton=new JButton("OK");
//		okButton.addActionListener(this);
//		cancelButton=new JButton("Cancel");
//		cancelButton.addActionListener(this);
//		optionPanel.add(okButton);
//		optionPanel.add(cancelButton);
//		add(optionPanel,BorderLayout.SOUTH);
        setProjectConfiguration(project);
	}
	
	
	public void setProjectConfiguration(ProjectConfiguration projectConfiguration){
	    this.project=projectConfiguration;
	   
	    setFrameTitle("Project \""+project.getName()+"\" configuration");
	    projectPanel.setProjectConfiguration(project);
//        audioPanel.setProjectConfiguration(project);
        SpeakersConfiguration speakersConfig=project.getSpeakers();
        speakersView.setSpeakersConfiguration(speakersConfig);
//        RecordingConfiguration recordingConfig=project.getRecordingConfiguration();
        recView.setProjectConfiguration(project);
        playView.setProjectConfiguration(project);
        Control control= project.getControl();
        transportView.setControl(control);
//        PromptConfiguration promptConfiguration=project.getPromptConfiguration();
        promptingView.setProjectConfiguration(project);
//        ViewConfiguration viewConfiguration=project.getViewConfiguration();
//        viewUI.setViewConfiguration(viewConfiguration);
        Annotation anno=project.getAnnotation();
        annotationPanel.setAnnotationConfig(anno);
        LoggingConfiguration loggingConfiguration=project.getLoggingConfiguration();
        loggingView.setLoggingConfiguration(loggingConfiguration);
	}
	
	public ProjectConfiguration getProjectConfiguration(){
	    ProjectConfiguration pc=new ProjectConfiguration();
	    projectPanel.applyValues(pc);
//	    audioPanel.applyValues(pc);
	    speakersView.applyValues(pc.getSpeakers());
	    recView.applyValues(pc);
	    playView.applyValues(pc);
	    transportView.applyValues(pc.getControl());
	    promptingView.applyValues(pc);
//	    viewUI.applyValues(pc.getViewConfiguration());
	    annotationPanel.applyValues(pc.getAnnotation());
	    loggingView.applyValues(pc.getLoggingConfiguration());
	 
	    return pc;
	}
	
	
//	public Object getValue(){
//		return value;
//	}
	
//	public JDialog createDialog(Frame owner){
//		JDialog dialog=new JDialog(owner,"Project configuration",true);
//		dialog.getContentPane().add(this);
//		dialog.addWindowListener(this);
//		return dialog;
//	}
//	
//	public Object showDialog(Frame parent){
//		d=createDialog(parent);
//		d.pack();
//		if(parent!=null){
//		d.setLocationRelativeTo(parent);
//		}
//		d.setVisible(true);
//		
//		return getValue();
//	}

//	public static Object showDialog(Component parent, ProjectConfiguration initialConfiguration,AudioController2 audioController) {
//		ProjectConfigurationView pv = new ProjectConfigurationView(initialConfiguration,audioController);
//		pv.doLayout();
//		JOptionPane selPane = new JOptionPane(pv, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
//		selPane.doLayout();
//		final JDialog d = selPane.createDialog(parent, "Project configuration");
//		d.doLayout();
//	
//		d.setResizable(true);
//		
//		d.setVisible(true);
//	
//		return selPane.getValue();
//	}
	
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	
//	public static void main(String[] args){
//		JFrame f=new JFrame();
//		//f.getContentPane().add(pv);
//		f.pack();
//		f.setVisible(true);
//		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		AudioController2 ac=new J2AudioController2();
//		ProjectConfigurationView v=new ProjectConfigurationView(new ProjectConfiguration(),ac);
//		
//		Object res=v.showDialog(f);
//		System.out.println("Res: "+res);
//		
//	}



	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent arg0) {
		setValue(JOptionPane.CANCEL_OPTION);
		
	}

	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * @param projectUrl
	 */
	public void setProjectContext(URL projectContext) {
		promptingView.setProjectContext(projectContext);
	}
}
