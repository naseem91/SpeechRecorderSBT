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

package ipsk.apps.speechrecorder.prompting;

import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterListener;
import ipsk.apps.speechrecorder.prompting.presenter.UnsupportedContentException;
import ipsk.apps.speechrecorder.prompting.presenter.event.PromptPresenterEvent;
import ipsk.beans.dyn.DynProperty;
import ipsk.beans.dyn.DynPropertyDescriptor;
import ipsk.db.speech.Mediaitem;
import ipsk.net.URLContext;

import java.awt.Font;
import java.awt.LayoutManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;



public abstract class BasicPromptPresenter extends JPanel implements PromptPresenter {

	private static final long serialVersionUID = 1L;
	protected URL contextURL;
    protected Mediaitem[] mediaitems;
    protected Vector<PromptPresenterListener> listeners=new Vector<PromptPresenterListener>();
	protected Font promptFont = new Font("sans-serif", Font.BOLD, 48);
    protected PromptPresenterListener listener;
    protected boolean emphasized=false;

	public BasicPromptPresenter() {
		super();
	}
	
	public BasicPromptPresenter(LayoutManager layoutManager) {
        super(layoutManager);
    }
	/**
	 * Sets font for prompts.	 
	 * @param promptFont
	 */
	public void setPromptFont(Font promptFont) {
		this.promptFont = promptFont;
	}
	
    public boolean getEmphasized() {    
        return emphasized;
    }

    public void setEmphasized(boolean emphasized) {
        this.emphasized=emphasized;
    }
    
    
//    public void setContents(String string) {
//        setContents(string,null,null);
//    }
//
//    
//
//
//    public void setContents(String string, String description){
//        setContents(string,description,null);
//    }
//
//    public abstract void setContents(String string, String description, String type);
//
//    public void setContents(URL url) {
//        setContents(url, null, null, null);        
//      }
//
//    public void setContents(URL url, String description) {
//        setContents(url, null, null, null);        
//    }
//
//    public void setContents(URL url, String description, String type) {
//        setContents(url, null, null, null);
//    }
//    
//    public abstract void setContents(URL url, String description, String type,
//            String charset);
// 
    
    
    public void setContents(Mediaitem[] mediaitems)
    throws PromptPresenterException {
        if(mediaitems==null || mediaitems.length==0){
            throw new UnsupportedContentException("No media item to display!");
        }
        this.mediaitems=mediaitems;
    }
    
    protected static String[][] getSupportedMIMETypes(String[] supportedMimeTypes){
        String[][] mtypes=new String[supportedMimeTypes.length][1];
        for(int i=0;i<supportedMimeTypes.length;i++){
            mtypes[i][0]=supportedMimeTypes[i];
        }
        return mtypes;
    }
    
    public String[] getSupportedLegacyMIMETypes(){
        return new String[0];
    }
    
    protected void updateListeners(PromptPresenterEvent event) {
        for(PromptPresenterListener ppl:listeners){
            ppl.update(event);
        }
    }
   
    public void addPromptPresenterListener(PromptPresenterListener listener) {
        synchronized(listeners){
        if (listener != null && !listeners.contains(listener)) {
            listeners.addElement(listener);
        }
        }
    }

   
    public void removePromptPresenterListener(PromptPresenterListener listener) {
        synchronized(listeners){
        if (listener != null) {
            listeners.removeElement(listener);
        }
        }
    }

    /**
     * Helper method to translate URL  to context.
     * @param mi Media item
     * @return absolute URL or null if media item URL is null
     * @throws PromptPresenterException
     */
    
    protected URL applyContextToMediaitemURL(Mediaitem mi) throws PromptPresenterException{
        URI miUri=mi.getSrc();
        if(miUri==null)return null;
        try {
            return URLContext.getContextURL(contextURL, miUri.toString());
        } catch (MalformedURLException e) {
           throw new PromptPresenterException(e);
        }
    }
    
    public URL getContextURL() {
        return contextURL;
    }

    public void setContextURL(URL contextURL) {
        this.contextURL = contextURL;
    }
    
    
//    public LocalizableMessage getTitle();
//    public LocalizableMessage getDescription();
//    public String getVendor();
//    public Version getSpecificationVersion();
//    public Version getImplementationVersion();
    
    public List<DynPropertyDescriptor> getDynamicPropertyDescriptors(){
        return new ArrayList<DynPropertyDescriptor>();
    }
    public void setDynamicProperty(DynProperty dynProperty){
        // does nothing
    }
    
    public DynProperty getDynamicProperty(String name){
        return null;
    }
    
}