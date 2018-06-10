//    IPS Java Audio Tools
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.audio.io.push;

/**
 * @author klausj
 *
 */
public class GrowEvent {

    private boolean finished;
    private long frameLength;
   
    private GrowingAudioSource source;
    
    
    public GrowEvent(long frameLength, GrowingAudioSource source) {
        super();
        this.frameLength = frameLength;
        this.source = source;
        this.finished=false;
    }


    public GrowEvent(boolean finished, long frameLength,
            GrowingAudioSource source) {
        super();
        this.finished = finished;
        this.frameLength = frameLength;
        this.source = source;
    }


    public boolean isFinished() {
        return finished;
    }


    public long getFrameLength() {
        return frameLength;
    }


    public GrowingAudioSource getSource() {
        return source;
    }
    
}
