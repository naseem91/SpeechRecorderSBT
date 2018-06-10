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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



/**
 * @author klausj
 *
 */
//@XmlType(propOrder={"file", "id","lastModified" , "startFrame", "frameLength","recordingSegmentList" })
@XmlType(propOrder={"file","lastModified","recordingSegmentList" })
public class RecordingFile {
//    private String id;
//    
//    private long startFrame;
//    private long frameLength;
    private Date lastModified;
   
    private File file;
    private List<RecordingSegment> recordingSegmentList=new ArrayList<RecordingSegment>();
    public RecordingFile(){
        super();
       
    }
    public RecordingFile(File file){
        super();
        this.file=file;
    }
    
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

//    public long getStartFrame() {
//        return startFrame;
//    }
//
//    public void setStartFrame(long startFrame) {
//        this.startFrame = startFrame;
//    }
//
//    public long getFrameLength() {
//        return frameLength;
//    }
//
//    public void setFrameLength(long frameLength) {
//        this.frameLength = frameLength;
//    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    @XmlElement(name="recordingSegment")
    public List<RecordingSegment> getRecordingSegmentList() {
        return recordingSegmentList;
    }
    public void setRecordingSegmentList(List<RecordingSegment> recordingSegmentList) {
        this.recordingSegmentList = recordingSegmentList;
    }
//    public String getId() {
//        return id;
//    }
//    public void setId(String id) {
//        this.id = id;
//    }
//    
    
}
