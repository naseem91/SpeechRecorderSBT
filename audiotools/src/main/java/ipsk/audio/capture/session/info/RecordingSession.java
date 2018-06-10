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

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author klausj
 *
 */
@XmlRootElement
public class RecordingSession {

    public static final String DEFAULT_RECORDING_SESSION_INFO_FILENAME="recording_session_info.xml";
    private List<RecordingSequence> recordingSequenceList=new ArrayList<RecordingSequence>();

    @XmlElement(name="recordingSequence")
    public List<RecordingSequence> getRecordingSequenceList() {
        return recordingSequenceList;
    }

    public void setRecordingSequenceList(
            List<RecordingSequence> recordingSequenceList) {
        this.recordingSequenceList = recordingSequenceList;
    }
    
    public RecordingFile removeRecordingFile(File recFile){
        List<RecordingSequence> seqInfos=getRecordingSequenceList();
        RecordingFile rfFound=null;
        RecordingSequence rseqFound=null;
        int seqFoundInd=-1;
        ArrayList<RecordingFile> beforeList=new ArrayList<RecordingFile>();
        ArrayList<RecordingFile>  afterList=new ArrayList<RecordingFile>();
        for(int i=0;i<seqInfos.size();i++){
            RecordingSequence rSeq=seqInfos.get(i);
            List<RecordingFile> rfs=rSeq.getRecordingFileList();
            
            for(RecordingFile rf:rfs){
                File f=rf.getFile();
                if(f.equals(recFile) ||
                        (!f.isAbsolute() && recFile.getName().equals(f.getName()))){
                    // found in list
                    // save the found item
                    // we cannot remove it in the loop
                    rfFound=rf;
                    rseqFound=rSeq;
                    seqFoundInd=i;
                    //break;
                }else{
                    if(rfFound==null){
                        beforeList.add(rf);
                    }else{
                        afterList.add(rf);
                    }
                }
            }
            if(rfFound!=null){
                break;
            }
        }
        if(rfFound!=null){
            seqInfos.remove(seqFoundInd);
            if(afterList.size()>0){
                RecordingSequence afterSeq=new RecordingSequence();
                afterSeq.setRecordingFileList(afterList);
                seqInfos.add(seqFoundInd, afterSeq);
            }
            if(beforeList.size()>0){
                RecordingSequence beforeSeq=new RecordingSequence();
                beforeSeq.setRecordingFileList(beforeList);
                seqInfos.add(seqFoundInd, beforeSeq);
            }
            
            //rseqFound.getRecordingFileList().remove(rfFound);
        }
        return rfFound;
    }
    
    public static void main(String[] args){
        RecordingFile rf=new RecordingFile(new File("blafile.wav"));
        rf.setLastModified(new Date());
        RecordingFile rf2=new RecordingFile(new File("blafile2.wav"));
       
        RecordingSequence rseq=new RecordingSequence();
        rseq.getRecordingFileList().add(rf);
        rseq.getRecordingFileList().add(rf2);
        rseq.setFrameLength(12345000);
        RecordingSession rs=new RecordingSession();
        rs.getRecordingSequenceList().add(rseq);
        JAXB.marshal(rs, System.out);
      
    }
}
