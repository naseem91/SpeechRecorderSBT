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

import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMElements;

/**
 * Configuration of a prompter.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
@DOMElements({"speakerWindowType","fullScreenMode","transportPanel"})
@DOMAttributes({"name"})
public class Prompter {
//    public static final String SPEAKER_DISPLAY_NAME = "speakerdisplay";
    public static enum SpeakerWindowType {FRAME,WINDOW};
    private String name;
	

	private SpeakerWindowType speakerWindowType=SpeakerWindowType.FRAME;
	private Boolean fullScreenMode=null;
	private TransportPanel transportPanel=new TransportPanel();
	
    public Prompter(){
        super();
    }
    
    public Prompter(String name){
        super();
        this.name=name;
    }
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    public TransportPanel getTransportPanel() {
        return transportPanel;
    }
    public void setTransportPanel(TransportPanel transportPanel) {
        this.transportPanel = transportPanel;
    }

    public SpeakerWindowType getSpeakerWindowType() {
        return speakerWindowType;
    }

    public void setSpeakerWindowType(SpeakerWindowType speakerWindowType) {
        this.speakerWindowType = speakerWindowType;
    }

    public Boolean getFullScreenMode() {
        return fullScreenMode;
    }

    public void setFullScreenMode(Boolean fullScreenMode) {
        this.fullScreenMode = fullScreenMode;
    }
	
}
