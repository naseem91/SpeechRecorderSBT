//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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

/*
 * Created on 29.12.2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package ipsk.audio;

import ipsk.io.SkipWorkaroundInputStream;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class SkipWorkaroundAudioInputStream extends AudioInputStream {

	public SkipWorkaroundAudioInputStream(AudioInputStream srcAis){
		this(srcAis,srcAis.getFormat(),srcAis.getFrameLength());
	}
	
	public SkipWorkaroundAudioInputStream(InputStream arg0, AudioFormat arg1, long arg2) {
		super(new SkipWorkaroundInputStream(arg0,arg1.getFrameSize()*2048), arg1, arg2);
		
	}

}
