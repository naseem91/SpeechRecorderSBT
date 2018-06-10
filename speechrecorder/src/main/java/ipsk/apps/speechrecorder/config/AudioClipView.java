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

 
package ipsk.apps.speechrecorder.config;

import ipsk.beans.dom.DOMElements;

/**
 * audio signal view configuration
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
@DOMElements({"showSignalView","showPlayActionBar","showSonagram","audioSignalView"})
public class AudioClipView {
    
    private Boolean showSignalView=true;
    private Boolean showPlayActionBar=true;
    private Boolean showSonagram=false;
    
    private AudioSignalView audioSignalView=null;
   

    
    public AudioClipView(){
    	super();
    
    }


	public AudioSignalView getAudioSignalView() {
		return audioSignalView;
	}


	public void setAudioSignalView(AudioSignalView audioSignalView) {
		this.audioSignalView = audioSignalView;
	}


    public Boolean getShowSignalView() {
        return showSignalView;
    }


    public void setShowSignalView(Boolean showSignalView) {
        this.showSignalView = showSignalView;
    }


    public Boolean getShowPlayActionBar() {
        return showPlayActionBar;
    }


    public void setShowPlayActionBar(Boolean showPlayActionBar) {
        this.showPlayActionBar = showPlayActionBar;
    }


    public Boolean getShowSonagram() {
        return showSonagram;
    }


    public void setShowSonagram(Boolean showSonagram) {
        this.showSonagram = showSonagram;
    }




    
}
