//    IPS Speech database tools
// 	  (c) Copyright 2016
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Speech database tools
//
//
//    IPS Speech database tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Speech database tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Speech database tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Created on 16.07.2013
 *
 * Project: SpeechDatabaseTools
 * Original author: draxler
 */
package ips.annot.model.db;

import javax.xml.bind.annotation.XmlTransient;


public class SegmentItem extends Item {

//    private long sampleStart;
//    private long sampleDur;
// 
//    
//    public long getSampleStart() {
//        return sampleStart;
//    }
//
//    public void setSampleStart(long sampleStart) {
//        this.sampleStart = sampleStart;
//    }
//
//    public long getSampleDur() {
//        return sampleDur;
//    }
//
//    public void setSampleDur(long sampleDur) {
//        this.sampleDur = sampleDur;
//    }

    @XmlTransient
    public String getType() {
        return LevelDefinition.SEGMENT;
    }
    

    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append("[" + getSampleStart() + ":");
        buffer.append(getSampleDur() + "] ");
        return buffer.toString();
    }
}
