//    IPS Java Audio Tools
// 	  (c) Copyright 2011
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.audio.arr.clip.ui;

import ips.media.MediaLengthUnit;
import ipsk.swing.action.tree.ActionFolder;
import ipsk.swing.action.tree.ActionProvider;
import ipsk.swing.action.tree.ActionTreeRoot;
import ipsk.swing.action.tree.CheckActionLeaf;
import ipsk.util.LocalizableMessage;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.Format;

import javax.swing.JScrollPane;

/**
 * @author klausj
 *
 */
public class AudioClipScrollPane extends JScrollPane implements ActionProvider{

	private AudioClipUIContainer audioClipUiContainer;
	private boolean showYScales=true;
    private ActionTreeRoot ar;
	
	public class ToggleYScalesVisibilityAction extends CheckActionLeaf{

        /**
         * @param displayName
         */
        public ToggleYScalesVisibilityAction(LocalizableMessage displayName) {
            super(displayName);
           
        }

        /* (non-Javadoc)
         * @see ips.incubator.awt.action.CheckActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            setShowYScales(isSelected());
        }
	    
	}
	
	public boolean isShowYScales() {
		return showYScales;
	}

	public void setShowYScales(boolean showYScales) {
		boolean oldShowYScales=this.showYScales;
		this.showYScales=showYScales;
		if(this.showYScales!=oldShowYScales){
			updateYScales();
		}
	}

	public AudioClipUIContainer getAudioClipUiContainer() {
		return audioClipUiContainer;
	}

	public void setAudioClipUiContainer(AudioClipUIContainer audioClipUiContainer) {
		this.audioClipUiContainer = audioClipUiContainer;
		setViewportView(audioClipUiContainer);
		updateYScales();
	}

	private void createActions() {
	    ar = new ActionTreeRoot();
        ActionFolder vF=ActionFolder.buildTopLevelFolder(ActionFolder.VIEW_FOLDER_KEY);
        ar.add(vF);
        vF.add(new ToggleYScalesVisibilityAction(new LocalizableMessage("Show Y-scales")));
	}
	
	/**
	 * 
	 */
	public AudioClipScrollPane() {
		super();
		createActions();
	}

	/**
	 * Build scroll pane around audio clip UI container.
	 * @param audioClipUiContainer audio clip UI container
	 */
	public AudioClipScrollPane(AudioClipUIContainer audioClipUiContainer) {
		super(audioClipUiContainer);
		this.audioClipUiContainer=audioClipUiContainer;
		createActions();
		updateYScales();
	}

	/**
	 * @param vsbPolicy
	 * @param hsbPolicy
	 */
	public AudioClipScrollPane(int vsbPolicy, int hsbPolicy) {
		super(vsbPolicy, hsbPolicy);
	}

	/**
	 * @param view
	 * @param vsbPolicy
	 * @param hsbPolicy
	 */
	public AudioClipScrollPane(AudioClipUIContainer view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
		this.audioClipUiContainer=view;
		updateYScales();
	}
	
	private void updateYScales(){
		Component rowHeaderView=null;
		if(showYScales){
			rowHeaderView=audioClipUiContainer.getyScalesComponent();
		}
		setRowHeaderView(rowHeaderView);
		
	}

    public void setXZoom(double xZoom) {
        audioClipUiContainer.setXZoom(xZoom);
    }

    public void xZoomFitToPanel() {
        audioClipUiContainer.xZoomFitToPanel();
    }

    public void setFixXZoomFitToPanel(boolean b) {
        audioClipUiContainer.setFixXZoomFitToPanel(b);
    }

    public double getXZoom() {
        return audioClipUiContainer.getXZoom();
    }

    public void xZoomToSelection() {
        audioClipUiContainer.xZoomToSelection();
    }

    public void xZoomOnePixelPerSample() {
        audioClipUiContainer.xZoomOnePixelPerSample();
    }

    public void setTimeFormat(Format timeFormat) {
        audioClipUiContainer.setTimeFormat(timeFormat);
    }

    public void setMediaLengthUnit(MediaLengthUnit mediaLengthUnit) {
        audioClipUiContainer.setMediaLengthUnit(mediaLengthUnit);
    }

    /* (non-Javadoc)
     * @see ips.incubator.awt.action.ActionProvider#getActionTree()
     */
    public ActionTreeRoot getActionTreeRoot() {
        ActionFolder mergedTree=null;
       
        ActionFolder af=audioClipUiContainer.getActionTreeRoot();   
//        mergedTree=ar.merge(af);
            
        ar.merge(af);
        return ar;
//        return mergedTree;
    }
	

}
