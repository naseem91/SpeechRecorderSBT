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

/**
 * Speaker database configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
@DOMElements({"speakersUrl"})
public class SpeakersConfiguration {
	
	private String speakersUrl;
	
	
	public SpeakersConfiguration(){
		
		speakersUrl="file:Speakers.txt";
	}
	
	/**
	 * @return speaker database URL
	 */
	public String getSpeakersUrl() {
		return speakersUrl;
	}

	/**
	 * @param string speaker database URL
	 */
	public void setSpeakersUrl(String string) {
		speakersUrl = string;
	}

}
