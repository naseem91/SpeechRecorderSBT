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

import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterPluginException;
import ipsk.audio.ChannelGroupLocator;

/**
 * Extended prompt presenter interface for media prompts.
 *  
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public interface MediaPromptPresenter2 extends MediaPromptPresenter{
 
	public void setAudioChannelOffset(int channelOffset);
	public void setAudioChannelGroupLocator(ChannelGroupLocator channelGroupLocator)throws PromptPresenterPluginException;
	
	
}
