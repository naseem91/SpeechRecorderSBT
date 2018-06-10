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
 * Date  : Jun 3, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config;

import ipsk.beans.dom.DOMElements;
import ipsk.beans.dom.RemoveIfDefault;

/**
 * Prompting configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

@DOMElements({"promptsUrl","startStopSignal","instructionsFont","promptFont","descriptionFont","showPromptWindow","showButtonsInPromptWindow","instructionNumbering","automaticPromptPlay","recManualPlay","prompter","itemcodeGeneratorConfiguration","audioChannelOffset","promptBeep"})
public class PromptConfiguration {

	private String promptsUrl;
	private PromptFont promptFont;
	private InstructionFont instructionFont;
	private DescriptionFont descriptionFont;
	private boolean automaticPromptPlay=true;
	private boolean recManualPlay=false;
    private boolean showPromptWindow;
	private boolean showButtonsInPromptWindow;
	private boolean instructionNumbering=true;
	private ItemcodeGeneratorConfiguration itemcodeGeneratorConfiguration=new ItemcodeGeneratorConfiguration();
	
	private StartStopSignal startStopSignal=null;
	private Prompter[] prompter=new Prompter[]{new Prompter()};
	private int audioChannelOffset=0;
	private PromptBeep promptBeep;
	


	

	public PromptConfiguration() {
		promptFont = new PromptFont();
		
		//	set defaults -> see PromptViewer class
		instructionFont = new InstructionFont();
		descriptionFont = new DescriptionFont();
		//promptsUrl = "file:prompts.xml";
		// Changed for user interactive selection of script. 
		promptsUrl=null;
		showPromptWindow=false;
		showButtonsInPromptWindow=true;
		promptBeep=new PromptBeep();
	}

	/**
	 * @return URL of the recording script
	 */
	public String getPromptsUrl() {
		return promptsUrl;
	}

	/**
	 * @param string URL of the recording script
	 */
	public void setPromptsUrl(String string) {
		promptsUrl = string;
	}

	
	public InstructionFont getInstructionsFont() {
		return instructionFont;
	}

	
	public PromptFont getPromptFont() {
		return promptFont;
	}

	
	public void setInstructionsFont(InstructionFont font) {
		instructionFont = font;
	}

	
	public void setPromptFont(PromptFont font) {
		promptFont = font;
	}

	
	public DescriptionFont getDescriptionFont() {
		return descriptionFont;
	}

	
	public void setDescriptionFont(DescriptionFont font) {
		descriptionFont = font;
	}

	/**
	 * @return true if speaker addressed window to be shown
	 */
	public boolean getShowPromptWindow() {
		return showPromptWindow;
	}

	/**
	 * @param b  true if speaker addressed window to be shown
	 */
	public void setShowPromptWindow(boolean b) {
		showPromptWindow = b;
	}

	/**
	 * @return  true if control buttons should be shown in speaker window
	 */
	public boolean getShowButtonsInPromptWindow() {
		return showButtonsInPromptWindow;
	}

	/**
	 * @param b true if control buttons should be shown in speaker window
	 */
	public void setShowButtonsInPromptWindow(boolean b) {
		showButtonsInPromptWindow = b;
	}

	/**
	 * @return true if prompt should start automatically
	 */
	public boolean getAutomaticPromptPlay() {
		return automaticPromptPlay;
	}

	/**
	 * @param b true if prompt should start automatically
	 */
	public void setAutomaticPromptPlay(boolean b) {
		automaticPromptPlay = b;
	}
	
	/**
	 * Returns true if manual start and stop of media prompts is allowed during recording.
	 * @return true if prompt start and stop enabled while recording
	 */
	public boolean getRecManualPlay() {
        return recManualPlay;
    }

	/**
	 * Determines if manual start and stop of media prompts is allowed during recording.
	 * @param recManualPlay set true if prompt start and stop should enabled while recording
	 */
    public void setRecManualPlay(boolean recManualPlay) {
        this.recManualPlay = recManualPlay;
    }
	
	/**
	 * 
	 * @return true if instructions should contain index numbering
	 */
	public boolean getInstructionNumbering() {
		return instructionNumbering;
	}

	/**
	 * 
	 * @param instructionNumbering true if instructions should contain index numbering
	 */
	public void setInstructionNumbering(boolean instructionNumbering) {
		this.instructionNumbering = instructionNumbering;
	}

    @RemoveIfDefault
	public Prompter[] getPrompter() {
		return prompter;
	}

	public void setPrompter(Prompter[] prompter) {
		this.prompter = prompter;
	}

    public StartStopSignal getStartStopSignal() {
        return startStopSignal;
    }

    public void setStartStopSignal(StartStopSignal startStopSignal) {
        this.startStopSignal = startStopSignal;
    }

    public ItemcodeGeneratorConfiguration getItemcodeGeneratorConfiguration() {
        return itemcodeGeneratorConfiguration;
    }

    public void setItemcodeGeneratorConfiguration(
            ItemcodeGeneratorConfiguration itemcodeGeneratorConfiguration) {
        this.itemcodeGeneratorConfiguration = itemcodeGeneratorConfiguration;
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
	 * @param promptBeep the promptBeep to set
	 */
	public void setPromptBeep(PromptBeep promptBeep) {
		this.promptBeep = promptBeep;
	}
	
	/**
	 * @return the promptBeep
	 */
	public PromptBeep getPromptBeep() {
		return promptBeep;
	}

}
