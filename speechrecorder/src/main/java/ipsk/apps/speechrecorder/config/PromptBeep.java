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
 * Prompt beep configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

@DOMElements({"beepFileURL","beepGainRatio"})
public class PromptBeep {

	private String beepFileURL=null;
	

	private Double beepGainRatio=null;
	
	public PromptBeep() {
		super();
	
	}
	
	/**
	 * @return the beepFileURL
	 */
	public String getBeepFileURL() {
		return beepFileURL;
	}

	/**
	 * @param beepFileURL the beepFileURL to set
	 */
	public void setBeepFileURL(String beepFileURL) {
		this.beepFileURL = beepFileURL;
	}
	
	/**
	 * Get volume (gain ratio)  of beep sound.
	 * @return the beepGainRatio in percent
	 */
	public Double getBeepGainRatio() {
		return beepGainRatio;
	}

	/**
	 * Set volume (gain ratio 0.0 .. 1.0 damping, 1.0 and more amplification ) of beep sound.
	 * @param beepGainRatio the beepGainRatio to set
	 */
	public void setBeepGainRatio(Double beepVolume) {
		this.beepGainRatio = beepVolume;
	}

	

}
