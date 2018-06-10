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
 * Date  : Jul 27, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.actions;

import javax.swing.Action;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class RecTransporterActions {
    public StartRecordAction startRecordAction;
    public StopRecordAction stopRecordAction;
    public StopNonrecordingAction stopNonrecordingAction;
    public StartAutoRecordingAction startAutoRecordingAction;
    public PauseAutoRecordingAction pauseAutoRecordingAction;
    public ContinueAutoRecordingAction continueAutoRecordingAction;
    public Action[] annotationActions;
    public AdvanceToNextAction advanceToNextAction;
    public ForwardAction forwardAction;
    public BackwardAction backwardAction;
    public StartPlaybackAction startPlaybackAction;
    public PausePlaybackAction pausePlaybackAction;
    public StopPlaybackAction stopPlaybackAction;
    public ContinuePlaybackAction continuePlaybackAction;
    
    
}
