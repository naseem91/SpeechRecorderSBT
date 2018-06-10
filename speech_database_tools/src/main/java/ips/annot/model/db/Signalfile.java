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
 * Created on 22.03.2013
 *
 * Project: SpeechDatabaseTools
 * Original author: draxler
 */
package ips.annot.model.db;

import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

public class Signalfile {

    /*
     *      Table "public.signalfile"
    Column    |  Type   | Modifiers 
--------------+---------+-----------
 id           | integer | not null
 filename     | text    | 
 filepath     | text    | 
 speaker_id   | integer | 
 itemcode     | text    | 
 distribution | boolean | 
 project_id   | integer | 

     */
    
    @Id
    @SequenceGenerator(name="keys", sequenceName="KEYS", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="keys")

    private String filename;
    private String filepath;
    private String itemcode;
    private boolean distribution;
    
    // --- linking fields ----------
    @OneToMany(mappedBy="signalfile")
    private Set<Segment> segments;
    @ManyToOne
    private Speaker speaker;
    
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getFilepath() {
        return filepath;
    }
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }
    public String getItemcode() {
        return itemcode;
    }
    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }
    public boolean isDistribution() {
        return distribution;
    }
    public void setDistribution(boolean distribution) {
        this.distribution = distribution;
    }
    public Set<Segment> getSegments() {
        return segments;
    }
    public void setSegments(Set<Segment> segments) {
        this.segments = segments;
    }
    public Speaker getSpeaker() {
        return speaker;
    }
    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }
}
