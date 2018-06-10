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
 * Date  : Apr 24, 2003
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio;

/**
 * Listens to {@link AudioController}playback and/or capture events.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
public interface AudioControllerListener {
	/**
	 * Notifys the AudioController about a playback and/or capture event. The
	 * parameters should be null if the state is unchanged.
	 * 
	 * @param ps
	 *            event from the playback engine or <code>null</code> if
	 *            unchanged
	 * @param cs
	 *            event from the recording/capturing engine or <code>null</code>
	 *            if unchanged
	 */
	public void update(AudioController.PlaybackStatus ps,
			AudioController.CaptureStatus cs);

}
