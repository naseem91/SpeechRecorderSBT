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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author klausj
 *
 */

public class RecordingSequence {
    
    private long startFrame;
    private long frameLength;
    private List<RecordingFile> recordingFileList=new ArrayList<RecordingFile>();

    
    /**
     * @param recordingFileList the recordingFileList to set
     */
    public void setRecordingFileList(List<RecordingFile> recordingFileList) {
        this.recordingFileList = recordingFileList;
    }

    /**
     * @return the recordingFileList
     */
    @XmlElement(name="recordingFile")
    public List<RecordingFile> getRecordingFileList() {
        return recordingFileList;
    }
    @XmlAttribute
    public long getStartFrame() {
        return startFrame;
    }

    public void setStartFrame(long startFrame) {
        this.startFrame = startFrame;
    }
    @javax.xml.bind.annotation.XmlAttribute
    public long getFrameLength() {
        return frameLength;
    }

    public void setFrameLength(long frameLength) {
        this.frameLength = frameLength;
    }
}
