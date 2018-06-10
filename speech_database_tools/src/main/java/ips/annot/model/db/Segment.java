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

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

public class Segment {

    /*
     *             Table "public.segment"
        Column     |       Type       | Modifiers 
    ---------------+------------------+-----------
     tier          | text             | 
     position      | integer          | 
     label         | text             | 
     begin         | double precision | 
     duration      | double precision | 
     reference     | integer          | 
     signalfile_id | integer          | 
     id            | integer          | 

     * @author draxler
     *
     */

    @Id
    @SequenceGenerator(name="keys", sequenceName="KEYS", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="keys")
    private int id;
    private int position;
    private String label;
    private double begin;
    private double duration;

    // --- linking fields ----------
    @OneToMany(mappedBy="reference")
    private List<Segment> segments;
    @ManyToOne
    private Segment reference;
    @ManyToOne
    private Signalfile signalfile;

    public Segment () {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getBegin() {
        return begin;
    }

    public void setBegin(double begin) {
        this.begin = begin;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public Segment getReference() {
        return reference;
    }

    public void setReference(Segment reference) {
        this.reference = reference;
    }

    public Signalfile getSignalfile() {
        return signalfile;
    }

    public void setSignalfile(Signalfile signalfile) {
        this.signalfile = signalfile;
    }
}
