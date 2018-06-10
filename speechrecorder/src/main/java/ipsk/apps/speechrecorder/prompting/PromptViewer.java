//    Speechrecorder
//    (c) Copyright 2002-2011
//    Institute of Phonetics and Speech Processing,
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

//
//  PromptViewer.java
//  JSpeechRecorder
//
//  Created by Christoph Draxler on Thu Dec 05 2002.
//

package ipsk.apps.speechrecorder.prompting;

import ipsk.apps.speechrecorder.DialogTargetProvider;
import ipsk.apps.speechrecorder.MIMETypes;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerClosedEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerOpenedEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerPresenterClosedEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerStartedEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerStoppedEvent;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterListener;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterPluginException;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterClosedEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterOpenedEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterStartEvent;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterStopEvent;
import ipsk.db.speech.Mediaitem;
import ipsk.db.speech.Nonrecording;
import ipsk.db.speech.Presenter;
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.Reccomment;
import ipsk.db.speech.Recinstructions;
import ipsk.db.speech.Recording;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.sound.sampled.Mixer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * PromptViewer displays the recording script prompt on the experimenter and the
 * speaker display. The display changes according to the prompt item's type of
 * media:
 * <ul>
 * <li>prompt texts are displayed as is</li>
 * <li>image items are scaled to fit the display size</li>
 * <li>audio items are shown with a clickable audio button</li>
 * <li>video items are displayed with a controller to start, stop and pause the
 * display</li>
 * </ul>
 * PromptViewer also displays descriptive text associated with a prompt item.
 * 
 * Some display attributes change according to the recording status: the
 * instruction text is printed in black only during the recording phase,
 * otherwise it is gray.
 * 
 * @author draxler
 * 
 */
public class PromptViewer extends JPanel implements PropertyChangeListener, PromptPresenterListener,DialogTargetProvider {

    /**
     * 
     */
    private static final long serialVersionUID = -6099442015723739386L;
    private static final boolean DEBUG = false;
    public static  enum Status {CLOSED,OPEN,RUNNING,STOPPED,PRESENTER_CLOSED}
//  public static final String SVG_PROMPT_PRESENTER_CLASS_NAME="ips.prompting.SVGPromptPresenter";
    
//  private Vector<PromptPresenterPlugin> plugins=new Vector<PromptPresenterPlugin>();
    
    private List<PromptPresenterServiceDescriptor> promptPresenterServiceDescriptors;
    private List<PromptPresenter> plugins;
    private final String EMPTY_STRING = "";

    private Font promptFont;

    private Font instructionsFont = new Font("sans-serif", Font.BOLD, 48);

    private Font descriptionFont = new Font("sans-serif", Font.PLAIN, 14);

    private JLabel promptInstructions;

    //private PromptPlainTextViewer promptTextViewer;

    //private PromptImageViewer promptImageViewer;

    //private PromptAudioViewer promptAudioViewer;
    //private PromptAudioJavaSoundViewer promptAudioViewer;

    //private PromptFormattedTextViewer promptFormattedTextViewer;

    // private PromptVideoViewer promptVideoViewer;
    private PromptPresenter promptPresenter;

    private JLabel promptDescription;

    private URL context;

    //private RecStatus recStat;

    //private RecScriptManager recScriptManager;
    
    private ipsk.db.speech.PromptItem promptItem = null;
    
    private Integer recIndex;

    
    

    private boolean isSilent = false;

    private boolean showComments = true;
    private boolean instructionNumbering=true;

    private Vector<PromptViewerListener> listeners;

    private boolean showPrompt;
   
    private StartPromptPlaybackAction startPromptPlaybackAction;
    private StopPromptPlaybackAction stopPromptPlaybackAction;
//    private boolean running=false;
//    private boolean closed;

    
//    private ServicesInspector<PromptPresenter> pluginManager;
    
    private Mixer promptMixer;
    
    private Status status;
	private int audioChannelOffset=0;
	
	private DialogTargetProvider dialogTargetProvider;

//    private List<Class<PromptPresenter>> pluginClassList;
    
   
    /**
     * Displays the individual prompt items according to their MIME type.
     * PromptViewer is notified of status changes from the prompt presenter.
     * 
     * @param promptPresenterServiceDescriptors list of available prompt presenter descriptors
     * @param startPromptPlaybackAction action to start prompt playback
     * @param stopPromptPlaybackAction action to stop prompt playback
     */
    public PromptViewer(List<PromptPresenterServiceDescriptor> promptPresenterServiceDescriptors,StartPromptPlaybackAction startPromptPlaybackAction,StopPromptPlaybackAction stopPromptPlaybackAction) {
        super(new BorderLayout());
        this.promptPresenterServiceDescriptors=promptPresenterServiceDescriptors;
        this.dialogTargetProvider=this;
//      plugins=promptPresenters;
//      this.pluginClassList=pluginClassList;
//      pluginManager=new ServicesInspector<PromptPresenter>(PromptPresenter.class);
        this.startPromptPlaybackAction=startPromptPlaybackAction;
        this.stopPromptPlaybackAction=stopPromptPlaybackAction;
        status=Status.CLOSED;
        promptInstructions = new JLabel(EMPTY_STRING);
        promptInstructions.setForeground(Color.darkGray);
        promptInstructions.setBackground(Color.lightGray);
        promptInstructions.setFont(instructionsFont);

        promptDescription = new JLabel(EMPTY_STRING);
        promptDescription.setFont(descriptionFont);

        //promptTextViewer = new PromptPlainTextViewer();
        //promptAudioViewer = new PromptAudioViewer();
//        ImageIcon audioImage = new ImageIcon(getClass().getResource("icons/playAudio.gif"));
//        startPromptPlaybackAction=new StartPromptPlaybackAction(this,audioImage);
//        startPromptPlaybackAction.setEnabled(false);
//        stopPromptPlaybackAction=new StopPromptPlaybackAction(this,audioImage);
//        stopPromptPlaybackAction.setEnabled(false);
//      promptAudioViewer= new PromptAudioJavaSoundViewer(startPromptPlaybackAction);
//      promptImageViewer = new PromptImageViewer();
//      promptFormattedTextViewer = new PromptFormattedTextViewer();
        // To avoid unnecessary loading of plugins (JMF archive (jmf.jar), Batik SVG) we do not
        // initialize
        // promptVideoViewer = new PromptVideoViewer();

        //promptPresenter = promptTextViewer;
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        //setLayout(new BorderLayout());
        listeners = new Vector<PromptViewerListener>();
//        closed=true;
//        running=false;
        //recStat = RecStatus.getInstance();
        //recStat.attach(this);
        
//      try {
//            pluginClassList = pluginManager.getServiceImplementorClasses();
//        } catch (IOException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this,"Error loading prompt presenter plugins: \n"+e.getMessage(), "Prompt presenter plugin loading error",JOptionPane.ERROR_MESSAGE);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this,"Prompt presenter class not found: \n"+e.getMessage(), "Prompt presenter plugin loading error",JOptionPane.ERROR_MESSAGE);
//        }
        
//        // fill default plugins
//        
//        plugins.add(new PromptPlainTextViewer());   
//        plugins.add(new PromptFormattedTextViewer());
//        plugins.add(new PromptBufferedImageViewer());
//        MediaPromptPresenter app=new PromptAudioJavaSoundViewer();
////        app.setStartControlAction(startPromptPlaybackAction);
////        app.setStopControlAction(stopPromptPlaybackAction);
//        plugins.add(app);
//        MediaPromptPresenter app2=new PlainTextAndAudioJavaSoundViewer();
////        app2.setStartControlAction(startPromptPlaybackAction);
////        app2.setStopControlAction(stopPromptPlaybackAction);
//        plugins.add(app2);
        
        
//        try {
//          Class.forName(SVG_PROMPT_PRESENTER_CLASS_NAME);
//           plugins.add(new PromptPresenterPlugin(SVG_PROMPT_PRESENTER_CLASS_NAME,MIMETypes.GRAPHICMIMETYPES));
//      }  catch (NoClassDefFoundError e) {
//            // OK external SVG plugin not available
//        }catch (ClassNotFoundException e) {
//          // OK external SVG plugin not available
//      }
       
        //plugins.add(new PromptPresenterPlugin( new ips.prompting.SVGPromptPresenter(),MIMETypes.GRAPHICMIMETYPES));
          plugins=new ArrayList<PromptPresenter>();
          
          // do NOT load all presenters
          
//      for(Class<? extends PromptPresenter> ppClass:promptPresentersClassList){
//          PromptPresenter pp=null;
//            try {
//                pp = ppClass.newInstance();
//                plugins.add(pp);
//            } catch (InstantiationException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//         
//      }
    }
    

    
     /**
     * Returns combinations of supported MIME types
     * @return combinations of supported MIME types
     */
    public Set<List<String>> getSupportedMIMETypes(){
//        ArrayList<String[][]> suppMts=new ArrayList<String[][]>();
        Set<List<String>> s=new HashSet<List<String>>();
        String[][] ppMtcArrs=null;
        for(PromptPresenter pp:plugins){
            PromptPresenterServiceDescriptor ppsd=pp.getServiceDescriptor();
            ppMtcArrs=ppsd.getSupportedMIMETypes();
            for(String[] ppMtcArr:ppMtcArrs){
                List<String> ppMtc=Arrays.asList(ppMtcArr);
                s.add(ppMtc);
            }
        }
//        if(pluginClassList !=null && promptPresenter==null){
//             //try to load
//             for (Class<PromptPresenter> c:pluginClassList){
//                 
//                     PromptPresenter pp;
//                    try {
//                        pp = c.newInstance();
//                    } catch (InstantiationException e) {
//                        e.printStackTrace();
//                        // ... and ignore
//                        continue;
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                        // ... and ignore
//                        continue;
//                    }
//                    ppMtcArrs=pp.getSupportedMIMETypes();
//                
//            for(String[] ppMtcArr:ppMtcArrs){
////                MIMETypeCombination ppMtc=new MIMETypeCombination(ppMtcArr);
//                List<String> ppMtc=Arrays.asList(ppMtcArr);
//                s.add(ppMtc);
//            }
//             }
//         }
//        
        return s;
        
    }

    
    public void setPromptMixer(Mixer mixer) throws PromptPresenterPluginException{
        promptMixer=mixer;
        for(PromptPresenter pp:plugins){
//            PromptPresenter pp=ppp.getInstance();
            if(pp !=null && pp instanceof MediaPromptPresenter){
                ((MediaPromptPresenter)pp).setAudioMixer(promptMixer);
            }
        }
        
    }
    
    public void setPromptAudioChannelOffset(int audioChannelOffset) throws PromptPresenterPluginException{
        this.audioChannelOffset=audioChannelOffset;
        for(PromptPresenter pp:plugins){
//            PromptPresenter pp=ppp.getInstance();
            if(pp !=null && pp instanceof MediaPromptPresenter2){
                ((MediaPromptPresenter2)pp).setAudioChannelOffset(audioChannelOffset);
            }
        }
        
    }

    

//  public void setRecScriptResources(Hashtable rs) {
//      promptAudioViewer.setRecScriptResources(rs);
//      promptImageViewer.setRecScriptResources(rs);
//      // Disabled image prefetching due to memory overflow problems
//      // promptImageViewer.setRecScriptResources(promptImageViewer.fetchImages(getImageResources()));
//      // promptVideoViewer.setRecScriptResources(recScriptManager.getRecScriptResources());
//  }

    /**
     * Sets the switch for audio output. If true, then audio sound
     * is muted. This switch is necessary to have only one source of audio
     * output, e.g. the speaker display, but not the experimenter display.
     * 
     * @param silence
     *            true to mute audio output
     */
    public void setSilent(boolean silence) {
        isSilent = silence;
    }

    /**
     * Returns the audio mute switch for the current display
     * 
     * @return true if the audio is muted
     */
    public boolean getSilent() {
        return isSilent;
    }

    /**
     * getShowComments() returns true if the comments text is to be displayed,
     * false otherwise
     * 
     * @return true to show comments text
     */
    public boolean getShowComments() {
        return showComments;
    }

    /**
     * setShowComments() sets the switch for displaying comments text to true or
     * false
     * 
     * @param comments
     */
    public void setShowComments(boolean comments) {
        showComments = comments;
    }

    /**
     * checks whether instructions text from the current prompt item is the
     * empty string or null. If yes, then the instructions are not displayed,
     * otherwise the text is displayed.
     * 
     * @param instructions
     *            text of the instructions field of the current prompt item
     */
    public void displayInstructions(Recinstructions instructions) {
      
        this.remove(promptInstructions);
        String instructionsText = null;
        
        if (instructions != null) {
            instructionsText = instructions.getRecinstructions();
            instructionsText.trim();
            if(instructionNumbering){
                instructionsText=recIndex + ": "+instructionsText;
            }
        }
        if (instructionsText == null || instructionsText.equals("")) {
            //this.remove(promptInstructions);
        } else {
            
            promptInstructions.setText(instructionsText);
            
            this.add(promptInstructions,BorderLayout.NORTH);

        }
    }

    /**
     * displayComments() checks whether comments should be displayed at all. If
     * yes, then it checks whether the comments text from the current prompt
     * item is the empty string or null. If yes, then the comments are not
     * displayed, otherwise the comments text is displayed.
     * 
     * @param comments
     *            text of the comment field of the current prompt item
     */
    public void displayComments(Reccomment comments) {
        this.remove(promptDescription);
        String commentsText = null;
        if (comments != null) {
            commentsText = comments.getReccomment();
            commentsText.trim();
        }
        if (!getShowComments()) {
            //this.remove(promptDescription);
        } else {
            if (commentsText == null || commentsText.equals("")) {
                //this.remove(promptDescription);
            } else {
                promptDescription.setText(commentsText);
                this.add(promptDescription,BorderLayout.SOUTH);
            }
        }
    }
    
    
    public void init() {
        promptInstructions.setText(EMPTY_STRING);
        displayInstructions(null);
        displayComments(null);
        //((Component) promptPresenter).setEnabled(false);
        setPromptPresenterEnabled(false);
        //setRecScriptResources();
        setShowPrompt(false);
    }

    private boolean supportsMIMEtypeCombination(PromptPresenter pp,String[] mimeTypeCombination){
        // workaround for false legacy MIME types
        if(pp instanceof PromptPlainTextViewer){
            if(mimeTypeCombination!=null && mimeTypeCombination.length==1){
                for (String falseMt:MIMETypes.FALSE_PLAINTEXTMIMETYPES){
                    if(falseMt.equals(mimeTypeCombination[0]))return true;
                }
                
            }
        }
        PromptPresenterServiceDescriptor ppsd=pp.getServiceDescriptor();
        String[][] suppMimes=ppsd.getSupportedMIMETypes();
        if(suppMimes!=null && mimeTypeCombination !=null){
             
            for (String[] suppMimeCombination:suppMimes){
                boolean combinationMatch=true;
                if(suppMimeCombination.length != mimeTypeCombination.length){
                    combinationMatch=false;
                    
                }else{
                Vector<String> suppMimeCombinationVector=new Vector<String>(Arrays.asList(suppMimeCombination));

                
                for(String mimeType:mimeTypeCombination){
                    boolean match=false;

                    //                  int i=0;
                    int availsuppMimes=suppMimeCombinationVector.size();
                    for(int i=0;i<availsuppMimes;i++){
                        String am=suppMimeCombinationVector.get(i);
                        if(am.equals(mimeType)){
                            // OK match
                            match=true;
                            suppMimeCombinationVector.remove(i);
                            break;
                        }
                    }
                    if(!match){
                        combinationMatch=false;
                        break;
                    }

                }
                }
                if(combinationMatch){
                   return true;
                }
            }
        }
        return false;
            
        } 
    
    private boolean supportsMIMEtypeCombination(PromptPresenterServiceDescriptor ppsd,String[] mimeTypeCombination){
        // workaround for false legacy MIME types
        String serviceImplClassname=ppsd.getServiceImplementationClassname();
        if(PromptPlainTextViewer.class.getName().equals(serviceImplClassname)){
            if(mimeTypeCombination!=null && mimeTypeCombination.length==1){
                for (String falseMt:MIMETypes.FALSE_PLAINTEXTMIMETYPES){
                    if(falseMt.equals(mimeTypeCombination[0]))return true;
                }
                
            }
        }
        String[][] suppMimes=ppsd.getSupportedMIMETypes();
        if(suppMimes!=null && mimeTypeCombination !=null){
             
            for (String[] suppMimeCombination:suppMimes){
                boolean combinationMatch=true;
                if(suppMimeCombination.length != mimeTypeCombination.length){
                    combinationMatch=false;
                    
                }else{
                Vector<String> suppMimeCombinationVector=new Vector<String>(Arrays.asList(suppMimeCombination));

                
                for(String mimeType:mimeTypeCombination){
                    boolean match=false;

                    //                  int i=0;
                    int availsuppMimes=suppMimeCombinationVector.size();
                    for(int i=0;i<availsuppMimes;i++){
                        String am=suppMimeCombinationVector.get(i);
                        if(am.equals(mimeType)){
                            // OK match
                            match=true;
                            suppMimeCombinationVector.remove(i);
                            break;
                        }
                    }
                    if(!match){
                        combinationMatch=false;
                        break;
                    }

                }
                }
                if(combinationMatch){
                   return true;
                }
            }
        }
        return false;
            
        } 
    
    public void prepare() throws PromptPresenterException {
        //ipsk.db.speech.PromptItem promptItem = null;
        Recording recItem = null;
//        Nonrecording nonrecItem = null;
        //      String mimeType = null;
//        String charSet=null;
//        URL src = null;
//        String text = null;
//        float volume=(float)1.0;
        
        Mediaitem[] mediaitemsArr=null;
        String[] mimeTypes=null;

        if(promptItem!=null){
            List<Mediaitem> miList = promptItem.getMediaitems();
            mediaitemsArr=miList.toArray(new Mediaitem[0]);
            mimeTypes=new String[miList.size()];
            for(int i=0;i<mimeTypes.length;i++){
                mimeTypes[i]=miList.get(i).getNNMimetype();
            }
            //promptItem = recScriptManager.getCurrentPromptItem();
            // property listeners can be registered more than once
            if(promptItem!=null && !Arrays.asList(promptItem.getPropertyChangeListeners()).contains(this)){
                promptItem.addPropertyChangeListener(this);
                for(Mediaitem mi:miList){
                    mi.addPropertyChangeListener(this);
                }
            }
            
            if (promptItem instanceof Recording) {
                recItem = (Recording) promptItem;
                //          //TODO
                //          Mediaitem mi = recItem.getMediaitems().get(0);
                //          mimeType = mi.getNNMimetype();
                //            charSet=mi.getCharSet();
                //          src = mi.getSrc();
                //          
                //          volume=mi.getNormalizedVolume();
                promptInstructions.setForeground(Color.black);
//                String recInstrStr = "";
                Recinstructions instr = recItem.getRecinstructions();
//                if (instr != null)
//                    recInstrStr = instr.getRecinstructions();

                displayInstructions(instr);
                Reccomment rc = recItem.getReccomment();
                displayComments(rc);
            } else if (promptItem instanceof Nonrecording) {
//                nonrecItem = (Nonrecording) promptItem;
                //          //TODO
                //          Mediaitem mi = nonrecItem.getMediaitems().get(0);
                //          mimeType = mi.getNNMimetype();
                //            //charSet=nonrecItem.getNNCharSet();
                //          src = mi.getSrc();
                ////            if (src == null) {
                //              text = mi.getText();
                ////            }
                //          volume=mi.getNormalizedVolume();
                displayInstructions(null);
                displayComments(null);
            }
        }
        if(DEBUG)System.out.println("Prepare");
        setShowPrompt(false);
        
//      if(promptPresenter!=null){
//          remove((Component) promptPresenter);
//      }
        promptPresenter=null;
        if(promptItem!=null){
            Presenter presenter=promptItem.getPresenter();
            if(presenter!=null){
                // prompt requests a particular presenter plugin
                String type=presenter.getType();
                if(Presenter.TYPE_JAVA_CLASS.equalsIgnoreCase(type)){
                    String presenterClassname=presenter.getClassname();
                    if(presenterClassname!=null){
                        boolean found=false;
                     // Check already loaded plugins
                    for(PromptPresenter pp:plugins){
                        Class<?> ppClass=pp.getClass();
                        if(presenterClassname.equals(ppClass.getName())){
                            promptPresenter=pp;
                            found=true;
                            break;
                        }
                    }
                    if(!found){
                        Class<?> pClass=null;
                        Object presenterObj=null;
                        try {
                            pClass = Class.forName(presenterClassname);
                            presenterObj = pClass.newInstance();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        if(presenterObj !=null && presenterObj instanceof PromptPresenter){
                            promptPresenter=(PromptPresenter)presenterObj;
                            plugins.add(promptPresenter);
                        }
                    }
                    }
                }
            }
            // Lookup list of already loaded plugins
            for(PromptPresenter pp:plugins){
                if(supportsMIMEtypeCombination(pp, mimeTypes)){
                    promptPresenter=pp;
                    break;
                }
            }
            
            if(promptPresenter ==null && promptPresenterServiceDescriptors !=null){
                // lookup presenter service descriptors
                for(PromptPresenterServiceDescriptor ppsd:promptPresenterServiceDescriptors){
                    if(supportsMIMEtypeCombination(ppsd, mimeTypes)){
                        String ppcn=ppsd.getServiceImplementationClassname();
                        Class<?> ppc=null;
                        Object ppObj=null;
//                      URL packageURL=ppsd.getPackageURL();
//                      if(packageURL!=null){
//                      ServiceClassLoader scl=new ServiceClassLoader(new URL[]{packageURL}, getClass().getClassLoader());
//                          Class<?> sc=scl.isClassLoaded(ppcn);
//                          if(sc==null){
//                              LocalizableMessage ppTitle=ppsd.getTitle();
//                              JOptionPane.showMessageDialog(this, "Loading "+ppTitle+" ...");
//                          }else{
//                              System.out.println("Class found");
//                          }
//                          
//                      }
                            
                            try {
                                //load class
                                ppc = Class.forName(ppcn);
                            } catch (ClassNotFoundException e) {
                                // fail silently and try next
                                if(DEBUG)e.printStackTrace();
                                continue;
                            }
                        try {
                            ppObj=ppc.newInstance();
                        } catch (InstantiationException e) {
                            // fail silently and try next
                            if(DEBUG)e.printStackTrace();
                            continue;
                        } catch (IllegalAccessException e) {
                            // fail silently and try next
                            if(DEBUG)e.printStackTrace();
                            continue;
                        }
                        if(ppObj!=null && ppObj instanceof PromptPresenter){
                            // found presenter
                            promptPresenter=(PromptPresenter)ppObj;
                            plugins.add(promptPresenter);
                            break;
                        }

                    }
                }
            }
            
            if (promptPresenter==null){
                // no presenter found
                if(mimeTypes.length==1){
                    throw new PromptPresenterException("Could not load presenter plugin for MIME type \""+mimeTypes[0]+"\"");
                }else{
                    StringBuffer mimetypeCombinationSb=new StringBuffer();
                    int mimeTypesCount=mimeTypes.length;
                    for(int i=0;i<mimeTypesCount;i++){
                        mimetypeCombinationSb.append(mimeTypes[i]);
                        if(i+1<mimeTypesCount){
                            mimetypeCombinationSb.append(',');
                        }
                    }
                    throw new PromptPresenterException("Could not load presenter plugin for MIME type combination \""+mimetypeCombinationSb+"\"");
                }
            }
            setPromptEmphased(false);

            if (recItem != null) {

            }
            if(promptPresenter instanceof MediaPromptPresenter){

                // ((MediaPromptPresenter)promptPresenter).setVolume((float)volumeInPercent/(float)100.0);
                MediaPromptPresenter mpp=((MediaPromptPresenter)promptPresenter);
                mpp.setStartControlAction(startPromptPlaybackAction);
                mpp.setStopControlAction(stopPromptPlaybackAction);
                //          mpp.setVolume(volume);
                mpp.setSilent(isSilent);
                mpp.setAudioMixer(promptMixer);
                if(promptPresenter instanceof MediaPromptPresenter2){
                	MediaPromptPresenter2 mpp2=(MediaPromptPresenter2)mpp;
                	mpp2.setAudioChannelOffset(audioChannelOffset);
                }
            }

            if(promptPresenter!=null){
                promptPresenter.setContextURL(context);
                promptPresenter.setPromptFont(promptFont);
                promptPresenter.setContents(mediaitemsArr);
                promptPresenter.loadContents();
            }
        }
    }
    
    public boolean isShowPrompt() {
        return showPrompt;
    }

    public void setShowPrompt(boolean showPrompt) {
        if(showPrompt){
            if(!this.showPrompt){
            if (promptItem instanceof Recording) {
                Recording recItem = (Recording) promptItem;
                displayInstructions(recItem.getRecinstructions());
                displayComments(recItem.getReccomment());
            }
           
            if(! isAncestorOf((Component) promptPresenter)){
                add((Component) promptPresenter,BorderLayout.CENTER);
            }

            setPromptPresenterEnabled(true);
            }
        }else{
            if(promptPresenter !=null){
                remove((Component) promptPresenter);
            } 
        }
        this.showPrompt = showPrompt;
        if(DEBUG)System.out.println(this.hashCode()+ " show: "+this.showPrompt);
        //      startPromptPlaybackAction.setEnabled(true);
        revalidate();
        repaint();     
    }
    
    
    public void open() throws PromptViewerException{
        if (Status.CLOSED.equals(status) || Status.PRESENTER_CLOSED.equals(status)) {
            if(DEBUG)System.out.println(this.hashCode()+ " opening...");
            if (promptPresenter instanceof MediaPromptPresenter) {
                MediaPromptPresenter mpp = (MediaPromptPresenter) promptPresenter;
                    mpp.removePromptPresenterListener(this);
                    mpp.addPromptPresenterListener(this);
                    try {
                        mpp.open();
                    
                    } catch (PromptPresenterException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(
                        		dialogTargetProvider.getDialogTarget(), 
                        		e.getLocalizedMessage(),
                                "Prompt presenter error",
                                JOptionPane.ERROR_MESSAGE);
                       throw new PromptViewerException(e);
                    }
                    
                    //updateListeners(new PromptViewerStartedEvent(this));
//                }
            } else {
                // generate open event for non multimedia (immediately displaying) prompters to start
                // recording
                
                status=Status.OPEN;
                if(DEBUG)System.out.println(this.hashCode()+ " open");
                updateListeners(new PromptViewerOpenedEvent(this));
            }
        }
    }
    
    public void start() {
        if (!Status.RUNNING.equals(status) && !Status.CLOSED.equals(status) && !Status.PRESENTER_CLOSED.equals(status)) {
//            startPromptPlaybackAction.setEnabled(false);
            if (promptPresenter instanceof MediaPromptPresenter) {
                MediaPromptPresenter mpp = (MediaPromptPresenter) promptPresenter;
//                if (!getSilent()) {
                    //mpp.addPromptPresenterListener(this);
                    try {
                      
                        mpp.start();
                    } catch (PromptPresenterException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(
                        		dialogTargetProvider.getDialogTarget(),
                        		e.getLocalizedMessage(),
                                "Prompt presenter error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    //updateListeners(new PromptViewerStartedEvent(this));
//                }
            } else {
                // generate directly the final close event for non multimedia prompters to start
                // recording
                status=Status.PRESENTER_CLOSED;
                updateListeners(new PromptViewerPresenterClosedEvent(this));
                //close();
            }
        }
    }

    public void stop() {
    	if(Status.RUNNING.equals(status)){
    		promptInstructions.setForeground(Color.lightGray);
    		// ipsk.db.speech.PromptItem promptItem = null;
    		// promptItem = recScriptManager.getCurrentPromptItem();
    		if (promptItem instanceof Recording) {
    			Recording recItem = (Recording) promptItem;
    			displayInstructions(recItem.getRecinstructions());
    			displayComments(recItem.getReccomment());
    		}
    		if (promptPresenter instanceof MediaPromptPresenter) {
    			try {
    				((MediaPromptPresenter) promptPresenter).stop();
    			} catch (PromptPresenterException e) {
    				e.printStackTrace();
    				JOptionPane.showMessageDialog(
    						dialogTargetProvider.getDialogTarget(),
    						e.getLocalizedMessage(),
    						"Prompt presenter error", JOptionPane.ERROR_MESSAGE);
    			}
    		}else{
    			//            running=false;
    			status=Status.STOPPED;
    		}
    	}
    }

    
    private void _close(){
        status=Status.CLOSED;
        promptInstructions.setText(EMPTY_STRING);
        displayComments(null);
        if(promptPresenter !=null){
            remove((Component) promptPresenter);
        }
        if(DEBUG)System.out.println("Prompt viewer close. showPrompt aus");
        //    showPrompt=false;
        setShowPrompt(false);
        revalidate();
        repaint();
        updateListeners(new PromptViewerClosedEvent(this));
    }
    
    public void close() {

    	if (!Status.CLOSED.equals(status)) {

    		if (promptPresenter instanceof MediaPromptPresenter && !status.equals(Status.PRESENTER_CLOSED)) {

    			try {
    				((MediaPromptPresenter) promptPresenter).close();
    			} catch (PromptPresenterException e) {
    				e.printStackTrace();
    				JOptionPane.showMessageDialog(
    						dialogTargetProvider.getDialogTarget(),
    						e.getLocalizedMessage(), "Prompt presenter error",
    						JOptionPane.ERROR_MESSAGE);
    			}

    		}else{
    			_close();
    		}
    	}
    }
    
    
    public void setInstructionsEmphased(boolean b){
        if(b){
            promptInstructions.setForeground(Color.black);
        }else{
            promptInstructions.setForeground(Color.lightGray);
        }
    }
    
    public void setPromptEmphased(boolean b){
        //ipsk.db.speech.PromptItem promptItem = null;
        //promptItem = recScriptManager.getCurrentPromptItem();
//      String mimeType = null;
//      if (promptItem instanceof Recording) {
//          // TODO
//          mimeType = ((Recording) promptItem).getMediaitems().get(0)
//                  .getNNMimetype();
//      } else if (promptItem instanceof Nonrecording) {
//          mimeType = ((Nonrecording) promptItem).getMediaitems().get(0)
//                  .getNNMimetype();
//      }
//      
        if(promptPresenter!=null)promptPresenter.setEmphasized(b);
//      if(b){
//          if (MIMETypes.isOfType(mimeType, MIMETypes.PLAINTEXTMIMETYPES)) {
//              promptTextViewer.setForeground(Color.black);
//          }
//      }else{
//          if (MIMETypes.isOfType(mimeType, MIMETypes.PLAINTEXTMIMETYPES)) {
//              promptTextViewer.setForeground(Color.lightGray);
//          }
//      }
    }
    
    public void setPromptPresenterEnabled(boolean b){
        if(promptPresenter !=null){
        ((Component) promptPresenter).setEnabled(b);
        }
    }
    
//  /**
//   * receives status changes and updates the prompt display accordingly. The
//   * prompt display consists of a mandatory instruction field and the prompt
//   * display panel and an optional description field. The description field is
//   * visible on the experimenter screen only.
//   * 
//   * @param status
//   *            recording status
//   */
//  public void update(int status) {
//
//      if (status == RecStatus.INIT) {
//          //init();
//          return;
//      } else if (status == RecStatus.CLOSE) {
//          //close();
//          return;
//      }
//
//      //((Component) promptPresenter).setEnabled(true);
//      //setPromptPresenterEnabled(true);
//
//      if (status == RecStatus.PRERECWAITING) {
//          // promptInstructions.setForeground(Color.lightGray);
//          //promptInstructions.setForeground(Color.black);
////            if (recScriptManager.getCurrentRecSection().getNNPromptphase()
////                    .equals(Section.PRERECORDING)) {
////                start();
////            }
//
//      } else if (status == RecStatus.RECORDING) {
//          //promptInstructions.setForeground(Color.lightGray);
//          //setInstructionsEmphased(false);
////            if (recScriptManager.getCurrentRecSection().getNNPromptphase()
////                    .equals(Section.RECORDING)) {
////                //add((Component) promptPresenter);
////                start();
////            }
//
//          
//
//      } else if (status == RecStatus.IDLE) {
//          //prepare();
//
//      } else if (status == RecStatus.POSTRECWAITING) {
//          //stop();
//      }
//      
//      // redraw panel contents
//      //revalidate();
//      //repaint();
//  }

    /**
     * @param font
     */
    public void setPromptFont(Font font) {
        promptFont = font;
        if(promptPresenter!=null){
        promptPresenter.setPromptFont(promptFont);
        }
    }

    
    public Font getDescriptionFont() {
        return descriptionFont;
    }

    
    public Font getInstructionsFont() {
        return instructionsFont;
    }

    public void setDescriptionFont(Font font) {
        descriptionFont = font;
        promptDescription.setFont(descriptionFont);
    }

    
    public void setInstructionsFont(Font font) {
        instructionsFont = font;
        promptInstructions.setFont(instructionsFont);
    }

    public boolean isInstructionNumbering() {
        return instructionNumbering;
    }

    public void setInstructionNumbering(boolean instructionNumbering) {
        this.instructionNumbering = instructionNumbering;
    }

    /**
     * Get the context URL for prompt sources.
     * 
     * @return context URL
     */
    public URL getContext() {
        return context;
    }

    /**
     * Set the context URL for prompt sources. This is usually the workspace
     * project directory.
     * 
     * @param url
     *            context
     */
    public void setContext(URL url) {
        context = url;
    }

    protected synchronized void updateListeners(PromptViewerEvent event) {
        for (PromptViewerListener ppl : listeners) {
            ppl.update(event);
        }
    }

    public void addPromptViewerListener(PromptViewerListener listener) {

        if (listener != null && !listeners.contains(listener)) {
            listeners.addElement(listener);
        }
    }

    public void removePromptViewerListener(PromptViewerListener listener) {

        if (listener != null) {
            listeners.removeElement(listener);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        boolean showingBefore=isShowPrompt();
        if(evt.getPropertyName().startsWith("recording")){
            if(DEBUG)System.out.println("PromptViewer: "+evt.getPropertyName());
        }
        try {
            prepare();
            if(showingBefore){
                setShowPrompt(true);
            }
        } catch (PromptPresenterException e) {
//            JOptionPane.showMessageDialog(this, e.getMessage(), "Prompt presenter error", JOptionPane.ERROR_MESSAGE);
//            e.printStackTrace();
        	// excption occurs on every property, do not handle it here
        }
    }

    public ipsk.db.speech.PromptItem getPromptItem() {
        return promptItem;
    }

    public void setPromptItem(ipsk.db.speech.PromptItem promptItem) {
        PromptItem oldPromptItem=this.promptItem;
        if(oldPromptItem!=null){
           
            List<Mediaitem> miList=oldPromptItem.getMediaitems();
            for(Mediaitem mi:miList){
                mi.removePropertyChangeListener(this);
            }
            oldPromptItem.removePropertyChangeListener(this);
        }
        this.promptItem = promptItem;
    
    }

    public int getRecIndex() {
        return recIndex;
    }

    public void setRecIndex(Integer recIndex) {
        this.recIndex = recIndex;
    }

    public void update(PromptPresenterEvent promptPresenterEvent) {
        if (promptPresenterEvent instanceof PromptPresenterOpenedEvent) {   
            if(Status.PRESENTER_CLOSED.equals(status) || Status.CLOSED.equals(status)){
                status=Status.OPEN;
                if(DEBUG)System.out.println(this.hashCode()+ " open");
                updateListeners(new PromptViewerOpenedEvent(this));
            }
        } else if (promptPresenterEvent instanceof PromptPresenterStartEvent) {
            if(!Status.RUNNING.equals(status)){
//            running = true;
            status=Status.RUNNING;
//            PromptItem pi=getPromptItem();
//            boolean modal=pi.getMediaitem().getNNModal();
//            startPromptPlaybackAction.setEnabled(false);
//            stopPromptPlaybackAction.setEnabled(stopControlEnabled && !modal);
            updateListeners(new PromptViewerStartedEvent(this));
            }
        } else if (promptPresenterEvent instanceof PromptPresenterStopEvent) {
//            running = false;
            if(Status.RUNNING.equals(status)){
            status=Status.STOPPED;
//            closeMediaPresenter();

            updateListeners(new PromptViewerStoppedEvent(this));

            }
        } else if (promptPresenterEvent instanceof PromptPresenterClosedEvent) {   
            if(!Status.PRESENTER_CLOSED.equals(status)){
            status=Status.PRESENTER_CLOSED;
            updateListeners(new PromptViewerPresenterClosedEvent(this));

            }
        }       
    }

    public void closeMediaPresenter(){
        if (promptPresenter instanceof MediaPromptPresenter && !Status.PRESENTER_CLOSED.equals(status)) {
//            if (!Status.CLOSE.equals(status)) {
                try {
                    ((MediaPromptPresenter) promptPresenter).close();
                } catch (PromptPresenterException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                    		dialogTargetProvider.getDialogTarget(),
                    		e.getLocalizedMessage(),
                            "Prompt presenter error",
                            JOptionPane.ERROR_MESSAGE);
                }
//            }

        }
    }
    
//    public boolean isStartControlEnabled() {
//        return startControlEnabled;
//    }
//
//    public void setStartControlEnabled(boolean startControlEnabled) {
//        this.startControlEnabled = startControlEnabled;
//    }

    public boolean isClosed() {
        return (Status.CLOSED.equals(status));
    }

//    public void setClosed(boolean closed) {
//        this.closed = closed;
//    }

//    public boolean isStopControlEnabled() {
//        return stopControlEnabled;
//    }
//
//    public void setStopControlEnabled(boolean stopControlEnabled) {
//        this.stopControlEnabled = stopControlEnabled;
//    }

    public Status getStatus() {
        return status;
    }

	/**
	 * @param dialogTargetProvider
	 */
	public void setDialogTargetProvider(DialogTargetProvider dialogTargetProvider) {
		this.dialogTargetProvider=dialogTargetProvider;
	}



	/* (non-Javadoc)
	 * @see ipsk.apps.speechrecorder.DialogTargetProvider#getDialogTarget()
	 */
	@Override
	public Component getDialogTarget() {
		return this;
	}

 
  

}
