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

//
//	PromptPresenter.java
//	JSpeechRecorder
//
//	Created by Christoph Draxler on Fri Dec 27 2002.
//	Copyright (c) 2002 University of Munich, Germany. All rights reserved.
//

/**
* PromptPresenter is an interface for presenting prompts to the speaker. It 
* provides method signatures for showing and hiding prompts, for loading
* prompt data from a URL, and for setting prompts via a String.
*
* @version 1.0 Dec. 2002
*/

package ipsk.apps.speechrecorder.prompting.presenter;
import ipsk.apps.speechrecorder.prompting.PromptPresenterServiceDescriptor;
import ipsk.db.speech.Mediaitem;
import ipsk.util.services.ServiceDescriptorProvider;

import java.awt.Font;
import java.net.URL;

public interface PromptPresenter extends ServiceDescriptorProvider<PromptPresenterServiceDescriptor>{
    /**
     * Add a listener.
     * @param promptPresenterListener
     */
    public void addPromptPresenterListener(PromptPresenterListener promptPresenterListener);
    
    /**
     * Remove a listener.
     * @param promptPresenterListener
     */
    public void removePromptPresenterListener(PromptPresenterListener promptPresenterListener);
    
    
    public PromptPresenterServiceDescriptor getServiceDescriptor();
    
//    /**
//     * Returns combinations of supported MIME types
//     * @return combinations of supported MIME types
//     */
//    public String[][] getSupportedMIMETypes();
    
    /**
     * Returns legacy MIME types.
     * 
     * @return supported legacy MIME types (e.g. text/UTF-8)
     */
    public String[] getSupportedLegacyMIMETypes();
    
//	/**
//	* showContents() displays the current prompt on the screen
//	*/
//	public void showContents();
//
//	/**
//	* hideContents() hides the current prompt
//	*/
//	public void hideContents();

	public void setContextURL(URL contextURL);
	
	

//	/**
//	* setContents() sets the current prompt with a String
//	* @param string String representing the contents to display
//	*/
//	public void setContents(String string);    
//
//	/**
//	* setContents() sets the current prompt with a String
//	* @param string String representing the contents to display
//	* @param description String describing the contents to display
//	*/
//	public void setContents(String string, String description);
//	
//	/**
//	* setContents() sets the current prompt with a String
//	* @param string String representing the contents to display
//	* @param description String describing the contents to display
//	* @param type MIME-type associated with this string
//	*/
//	public void setContents(String string, String description,String type);
//	
//	/**
//	* setContents() sets the current prompt with a URL
//	* @param URL representing the contents to display
//	*/
//	public void setContents(URL url);    
//
//	/**
//	* setContents() sets the current prompt with a URL
//	* @param URL representing the contents to display
//	* @param description String describing the contents to display
//	*/
//	public void setContents(URL url, String description);
//	
//	/**
//	* setContents() sets the current prompt with a URL
//	* @param URL representing the contents to display
//	* @param description String describing the contents to display
//	* @param type MIME-type associated with this URL
//	*/
//	public void setContents(URL url, String description, String type);
//	
//    /**
//    * setContents() sets the current prompt with a URL
//    * @param URL representing the contents to display
//    * @param description String describing the contents to display
//    * @param type MIME-type associated with this URL
//    * @param charset charset associated with this URL
//    */
//    public void setContents(URL url, String description, String type,String charset);
    
	public void setContents(Mediaitem[] mediaitems) throws PromptPresenterException;
	
	/**
	   * Loads the current prompt data from a URL
	   */
	public void loadContents() throws PromptPresenterException;
	    
    /**
     * Set the prompt emphasize status.
     * 
     * @param emphasized true if emphasize
     */
    public void setEmphasized(boolean emphasized);
    
    /**
     * Return emphasized status.
     * @return true if emphasized
     */
    public boolean getEmphasized();
    
    /**
     * Set the prompt font to use.
     * @param font prompt font
     */
    public void setPromptFont(Font font);
    
}
