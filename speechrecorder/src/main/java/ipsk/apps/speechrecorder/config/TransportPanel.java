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

/**
 * Configuration of an transport panel in a prompter window.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
//@DOMElements({})
@DOMAttributes({"showStartRecordAction","showStopRecordAction"})
public class TransportPanel {

	private boolean showStartRecordAction=true;
	private boolean showStopRecordAction=true;
    
    // TODO the other actions are not implemented yet
//    private boolean showStartAutoRecording=true;
//    private boolean showPauseAutoRecording=true;
//    private boolean showContinueAutoRecording=true;
	
    public boolean isShowStartRecordAction() {
        return showStartRecordAction;
    }
    public void setShowStartRecordAction(boolean showStartRecordAction) {
        this.showStartRecordAction = showStartRecordAction;
    }
    public boolean isShowStopRecordAction() {
        return showStopRecordAction;
    }
    public void setShowStopRecordAction(boolean showStopRecordAction) {
        this.showStopRecordAction = showStopRecordAction;
    }
//    public boolean isShowContinueAutoRecording() {
//        return showContinueAutoRecording;
//    }
//    public void setShowContinueAutoRecording(boolean showContinueAutoRecording) {
//        this.showContinueAutoRecording = showContinueAutoRecording;
//    }
//    public boolean isShowPauseAutoRecording() {
//        return showPauseAutoRecording;
//    }
//    public void setShowPauseAutoRecording(boolean showPauseAutoRecording) {
//        this.showPauseAutoRecording = showPauseAutoRecording;
//    }
//    public boolean isShowStartAutoRecording() {
//        return showStartAutoRecording;
//    }
//    public void setShowStartAutoRecording(boolean showStartAutoRecording) {
//        this.showStartAutoRecording = showStartAutoRecording;
//    }
	
}
