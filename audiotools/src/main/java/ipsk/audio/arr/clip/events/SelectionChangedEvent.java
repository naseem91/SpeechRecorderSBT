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
 * Date  : Oct 17, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.arr.clip.events;

import ipsk.audio.arr.Selection;

/**
 * Event indicates a changed selection of the audio clip.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class SelectionChangedEvent extends SelectionsChangedEvent{

    private Selection selection;

  /**
   * Create event.
   * @param source the source of the event
   * @param selection the changed selection
   */
    public SelectionChangedEvent(Object source, Selection selection) {
        super(source);
        this.selection=selection;
    }

    /**
     * Get the changed selection.
     * @return selection
     */
    public Selection getSelection() {
        return selection;
    }
}
