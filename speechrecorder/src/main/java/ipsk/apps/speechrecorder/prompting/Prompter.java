//    Speechrecorder
//    (c) Copyright 2012
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
 * Date  : Jun 1, 2010
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.prompting;

import ipsk.apps.speechrecorder.DialogTargetProvider;
import ipsk.apps.speechrecorder.prompting.PromptViewer.Status;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerClosedEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerPresenterClosedEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerOpenedEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerStartedEvent;
import ipsk.apps.speechrecorder.prompting.event.PromptViewerStoppedEvent;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.db.speech.Mediaitem;
import ipsk.db.speech.PromptItem;
import ipsk.db.speech.Reccomment;
import ipsk.db.speech.Recinstructions;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.sound.sampled.Mixer;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class Prompter implements PromptViewerListener {

    private PromptViewer experimenterViewer;
    private List<PromptViewer> subjectViewers=new ArrayList<PromptViewer>();
    private List<PromptViewer> allViewers=new ArrayList<PromptViewer>();
    
    private StartPromptPlaybackAction startPromptPlaybackAction;
    private StopPromptPlaybackAction stopPromptPlaybackAction;
    
    private Vector<PromptViewerListener> listeners=new Vector<PromptViewerListener>();
    private boolean startControlEnabled;
    private boolean stopControlEnabled=true;
    private boolean autoPlay = true;
    private boolean autoPlayStarted=true;
    private PromptItem promptItem;
    private Integer recIndex;
    private Mixer promptMixer=null;
    private int audioChannelOffset=0;
    
    private DialogTargetProvider dialogTargetProvider=null;
    
    /**
	 * @return the dialogTargetProvider
	 */
	public DialogTargetProvider getDialogTargetProvider() {
		return dialogTargetProvider;
	}

	/**
	 * @param dialogTargetProvider the dialogTargetProvider to set
	 */
	public void setDialogTargetProvider(DialogTargetProvider dialogTargetProvider) {
		this.dialogTargetProvider = dialogTargetProvider;
	}

	/**
	 * @return the audioChannelOffset
	 */
	public int getAudioChannelOffset() {
		return audioChannelOffset;
	}

	/**
	 * @param audioChannelOffset the audioChannelOffset to set
	 */
	public void setAudioChannelOffset(int audioChannelOffset) {
		this.audioChannelOffset = audioChannelOffset;
	}

	/**
	 * @return the promptMixer
	 */
	public Mixer getPromptMixer() {
		return promptMixer;
	}

	/**
	 * @param promptMixer the promptMixer to set
	 */
	public void setPromptMixer(Mixer promptMixer) {
		this.promptMixer = promptMixer;
	}

	private PromptViewer.Status status=PromptViewer.Status.CLOSED;
    
    public Prompter(){
        ImageIcon audioImage = new ImageIcon(getClass().getResource("icons/playAudio.gif"));
        startPromptPlaybackAction=new StartPromptPlaybackAction(this,audioImage);
        startPromptPlaybackAction.setEnabled(false);
        stopPromptPlaybackAction=new StopPromptPlaybackAction(this,audioImage);
        stopPromptPlaybackAction.setEnabled(false);
    }
    
    private void createAllViewersList(){
        synchronized(allViewers){
        allViewers.clear();
        allViewers.add(experimenterViewer);
        allViewers.addAll(subjectViewers);
        }
    }
    
    public PromptViewer getExperimenterViewer() {
        return experimenterViewer;
    }
    public void setExperimenterViewer(PromptViewer experimenterViewer) {
        if(this.experimenterViewer!=null){
            this.experimenterViewer.removePromptViewerListener(this);
        }
        this.experimenterViewer = experimenterViewer;
        if(this.experimenterViewer!=null){
            this.experimenterViewer.addPromptViewerListener(this);
        }
        createAllViewersList();
    }
    
    public void addSubjectViewer(PromptViewer sv) {
        subjectViewers.add(sv);
        sv.addPromptViewerListener(this);
        createAllViewersList();  
    }
    public void removeSubjectViewer(PromptViewer sv) {
        sv.removePromptViewerListener(this);
        subjectViewers.remove(sv);
        createAllViewersList();  
    }
    
    public void clearSubjectViewersList(){
        for(PromptViewer spv:subjectViewers){
            spv.removePromptViewerListener(this);
        }
        subjectViewers.clear();
        createAllViewersList();  
    }
    
    public List<PromptViewer> getSubjectViewers(){
        return Collections.unmodifiableList(subjectViewers);
    }
    
    public void setSubjectViewers(List<PromptViewer> subjectViewers) {
        this.subjectViewers = subjectViewers;
        createAllViewersList();
    }
    
    
    public void close() {
        for(PromptViewer pv:allViewers){
            pv.close();
        }
    }
    public void displayComments(Reccomment comments) {
        for(PromptViewer pv:allViewers){
            pv.displayComments(comments);
        }
    }
    public void displayInstructions(Recinstructions instructions) {
        for(PromptViewer pv:allViewers){
            pv.displayInstructions(instructions);
        }
    }
    public Font getInstructionsFont() {
        return experimenterViewer.getInstructionsFont();
    }
    public PromptItem getPromptItem() {
        return promptItem;
    }
    public int getRecIndex() {
        return recIndex;
    }
    
    
    public void play(){
    	try {
			open();
		} catch (PrompterException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(
					dialogTargetProvider.getDialogTarget(),
					e.getLocalizedMessage(), "Prompt open error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
    	start();
    }
    
    public void open() throws PrompterException{
    	for(PromptViewer pv:allViewers){
    		try {
				pv.open();
			} catch (PromptViewerException e) {
				close();
				throw new PrompterException("Prompter error: "+e.getLocalizedMessage(), e); 
			}
    	}
    	
    }
    

    public void prepare() throws PromptPresenterException {
    	 startPromptPlaybackAction.resetIcon();
    	 stopPromptPlaybackAction.resetIcon();
        for(PromptViewer pv:allViewers){
        	pv.setPromptMixer(promptMixer);
        	pv.setPromptAudioChannelOffset(audioChannelOffset);
            pv.prepare();
        }
    }
   
    public void setInstructionNumbering(boolean instructionNumbering) {
        for(PromptViewer pv:allViewers){
            pv.setInstructionNumbering(instructionNumbering);
        }
    }
    public void setInstructionsEmphased(boolean b) {
        for(PromptViewer pv:allViewers){
            pv.setInstructionsEmphased(b);
        }
    }
    public void setPromptEmphased(boolean b) {
        for(PromptViewer pv:allViewers){
            pv.setPromptEmphased(b);
        }
    }
    public void setPromptFont(Font font) {
        for(PromptViewer pv:allViewers){
            pv.setPromptFont(font);
        }
    }
    public void setInstructionsFont(Font font) {
        for(PromptViewer pv:allViewers){
            pv.setInstructionsFont(font);
        }
    }
    public void setDescriptionFont(Font font) {
        for(PromptViewer pv:allViewers){
            pv.setDescriptionFont(font);
        }
    }
    public void setPromptItem(PromptItem promptItem) {
        this.promptItem = promptItem;
        for(PromptViewer pv:allViewers){
            pv.setPromptItem(promptItem);
        }
        autoPlayStarted=false;
    }
    public void setPromptPresenterEnabled(boolean b) {
        for(PromptViewer pv:allViewers){
            pv.setPromptPresenterEnabled(b);
        }
    }
    public void setRecIndex(Integer recIndex) {
        this.recIndex=recIndex;
        for(PromptViewer pv:allViewers){
            pv.setRecIndex(recIndex);
        }
    }
    public void setShowComments(boolean comments) {
        for(PromptViewer pv:allViewers){
            pv.setShowComments(comments);
        }
    }
    public void setShowPrompt(boolean showPrompt) {
//    	System.out.println("Prompt viewer show: "+showPrompt);
    	for(PromptViewer pv:allViewers){
    		pv.setShowPrompt(showPrompt);
    	}
    	
    }
    
    public void autoPlay() throws PrompterException{
    	boolean resAutoPlay = autoPlay;

    	List<Mediaitem> mis=promptItem.getMediaitems();
    	Boolean piAutoPlay =null;
    	// if one of the media items is autoplay set autoplay
    	for(Mediaitem mi:mis){
    		Boolean miAp=mi.getAutoplay();
    		if(miAp!=null){
    			if(piAutoPlay==null){
    				piAutoPlay=miAp;
    			}else{
    				piAutoPlay=(piAutoPlay || miAp);
    			}
    		}
    	}
    	// Script attribute overrides project configuration
    	if (piAutoPlay != null) {
    		resAutoPlay = piAutoPlay;
    	}

    	if (!autoPlayStarted && resAutoPlay){
    		open();
    		autoPlayStarted=true;
    		start();
    	}else{
    		startPromptPlaybackAction.setEnabled(true);
    	}
    }

    public void init() {
        for(PromptViewer pv:allViewers){
            pv.init();
        }
    }
//    public boolean isClosed() {
//        return experimenterViewer.isClosed();
//    }
    public void start() {
    	if(Status.OPEN.equals(status) || Status.STOPPED.equals(status)){
    		for(PromptViewer pv:allViewers){
    			pv.start();
    		}
    	}
    }
    public void stop() {
        for(PromptViewer pv:allViewers){
            pv.stop();
        }
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
    
    private boolean checkAllViewersStatus(PromptViewer.Status[] states){
        PromptViewer.Status expStatus=experimenterViewer.getStatus();
        boolean ok=false;
        for(PromptViewer.Status status:states){
            if(status.equals(expStatus)){
                ok=true;
                break;
            }
        }
        if(!ok)return false;
        for(PromptViewer pv : subjectViewers){
            PromptViewer.Status pvStatus=pv.getStatus();
            ok=false;
            for(PromptViewer.Status status:states){
                if(status.equals(pvStatus)){
                    ok=true;
                    break;
                }
            }
            if(!ok)return false;
        }
        return true;
    }
    private boolean checkAllViewersStatus(PromptViewer.Status status){
        PromptViewer.Status expStatus=experimenterViewer.getStatus();
        if(!status.equals(expStatus)) return false;
        for(PromptViewer pv : subjectViewers){
            PromptViewer.Status pvStatus=pv.getStatus();
            if(!status.equals(pvStatus)){
                return false;
            }
        }
        return true;

    }
    
    public boolean isPresenterClosed(){
//        return checkAllViewersStatus(PromptViewer.Status.CLOSE);
        return (PromptViewer.Status.PRESENTER_CLOSED.equals(status) || PromptViewer.Status.CLOSED.equals(status));
    }
    
    public void update(PromptViewerEvent promptViewerEvent) {
        if (promptViewerEvent instanceof PromptViewerStartedEvent){
          if(checkAllViewersStatus(new PromptViewer.Status[]{PromptViewer.Status.RUNNING,PromptViewer.Status.STOPPED})){
              PromptItem pi=getPromptItem();
              // modal mode if one of the prompt items is modal
              boolean modal=false;
              List<Mediaitem> mis=pi.getMediaitems();
              for(Mediaitem mi:mis){
            	  boolean miModal=mi.getNNModal();
            	  if(miModal){
            		  modal=true;
            		  break;
            	  }
              }
              startPromptPlaybackAction.setEnabled(false);
              stopPromptPlaybackAction.setEnabled(stopControlEnabled && !modal);
              status=PromptViewer.Status.RUNNING;
               updateListeners(promptViewerEvent);
          }
         
        }else if (promptViewerEvent instanceof PromptViewerStoppedEvent){
            if(!PromptViewer.Status.STOPPED.equals(status) && checkAllViewersStatus(PromptViewer.Status.STOPPED)){
                stopPromptPlaybackAction.setEnabled(false);
                status=PromptViewer.Status.STOPPED;
                updateListeners(promptViewerEvent);
                for(PromptViewer pv:allViewers){
                    pv.closeMediaPresenter();
                }
               
           }
           
        }else if (promptViewerEvent instanceof PromptViewerOpenedEvent){
            if(!PromptViewer.Status.OPEN.equals(status) && checkAllViewersStatus(PromptViewer.Status.OPEN)){
//                startPromptPlaybackAction.setEnabled(startControlEnabled);
                status=PromptViewer.Status.OPEN;
                updateListeners(promptViewerEvent);
           }
        }else if (promptViewerEvent instanceof PromptViewerPresenterClosedEvent){
            if(!PromptViewer.Status.PRESENTER_CLOSED.equals(status) && checkAllViewersStatus(PromptViewer.Status.PRESENTER_CLOSED)){
                startPromptPlaybackAction.setEnabled(startControlEnabled);
                status=PromptViewer.Status.PRESENTER_CLOSED;
                updateListeners(promptViewerEvent);
           }
        }else if (promptViewerEvent instanceof PromptViewerClosedEvent){
            if(!PromptViewer.Status.CLOSED.equals(status) && checkAllViewersStatus(PromptViewer.Status.CLOSED)){
//                startPromptPlaybackAction.setEnabled(startControlEnabled);
                status=PromptViewer.Status.CLOSED;
                updateListeners(promptViewerEvent);
           }
        }
    }

    public boolean isStartControlEnabled() {
        return startControlEnabled;
    }

    public void setStartControlEnabled(boolean startControlEnabled) {
        this.startControlEnabled = startControlEnabled;
    }

    public boolean isStopControlEnabled() {
        return stopControlEnabled;
    }

    public void setStopControlEnabled(boolean stopControlEnabled) {
        this.stopControlEnabled = stopControlEnabled;
    }

    public StartPromptPlaybackAction getStartPromptPlaybackAction() {
        return startPromptPlaybackAction;
    }

    public StopPromptPlaybackAction getStopPromptPlaybackAction() {
        return stopPromptPlaybackAction;
    }
    
    public void setAutomaticPromptPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }
    
}
