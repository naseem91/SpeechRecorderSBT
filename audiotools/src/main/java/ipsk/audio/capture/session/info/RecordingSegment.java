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

package ipsk.audio.capture.session.info;

import java.util.Date;

import javax.xml.bind.annotation.XmlType;

/**
 * @author klausj
 *
 */
@XmlType(propOrder={"id","startTime","startFrame","frameLength" })
public class RecordingSegment {

    private String id;
    private long startFrame;
    private Date startTime;
    private long frameLength;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public long getStartFrame() {
        return startFrame;
    }
    public void setStartFrame(long startFrame) {
        this.startFrame = startFrame;
    }
    public long getFrameLength() {
        return frameLength;
    }
    public void setFrameLength(long frameLength) {
        this.frameLength = frameLength;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
}
