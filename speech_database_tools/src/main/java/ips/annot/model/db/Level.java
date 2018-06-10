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


import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "level", propOrder = {
        "name",
        "type",
        "items"
        
        })

public class Level {
    
    @Id
    @SequenceGenerator(name="keys", sequenceName="KEYS", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="keys")
    @XmlTransient
    private int id;
    @ManyToOne
    private transient Bundle bundle;
//    private URL dataStream;
    @OneToOne
    private LevelDefinition definition = null;
   
    
    @OneToMany(mappedBy="level")
    private List<Item> items = new ArrayList<Item>();
    
  
    @XmlTransient
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    @XmlTransient
    public Bundle getBundle() {
        return bundle;
    }
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
    
//    @XmlTransient
//    public URL getDataStream() {
//        return dataStream;
//    }
//    public void setDataStream(URL dataStream) {
//        this.dataStream = dataStream;
//    }
    
    @XmlTransient
    public LevelDefinition getDefinition() {
        return definition;
    }
    public void setDefinition(LevelDefinition definition) {
        this.definition = definition;
    }
    
  
    
    @XmlElement(name="name")
    public String getName() {
        return getDefinition().getName();
    }
    public void setName(String name) {
        LevelDefinition ld=getDefinition();
        if(ld==null){
            ld=new LevelDefinition();
            setDefinition(ld);
        }
        ld.setName(name);
    }
    
    @XmlElement(name="type")
    public String getType() {
        return getDefinition().getType();
    }
    public void setType(String type) {
        LevelDefinition ld=getDefinition();
        if(ld==null){
            ld=new LevelDefinition();
            setDefinition(ld);
        }
        ld.setType(type);
    }
    
//    @XmlElementWrapper(name = "items")
    @XmlElements(@XmlElement(name = "items"))
    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> elements) {
        this.items = elements;
    }
    
   
    
//    //  --- auxiliary methods ----------
//    public int getLengthInSamples() {
//        if (getDefinition().getType().equals(LevelDefinition.SEGMENT)) {
//            SegmentItem firstItem = (SegmentItem) getItems().get(0);
//            SegmentItem lastItem = (SegmentItem) getItems().get(getItems().size() - 1);
//            return (int) ((lastItem.getSampleStart() + lastItem.getSampleDur()) - firstItem.getSampleStart());
//        } else {
//            return -1;
//        }
//    }
    
//    public Float getLengthInSeconds() {
//        Float seconds = null;
//        if (getSampleRate() != null) {
//            seconds = getLengthInSamples() / getSampleRate();
//        }
//        return seconds;
//    }
    
//    public long derivedStart(){
//        long minStart=0;        
//        List<Item>itList=getItems();
//        for(Item it:itList){
//            it.get
//        }
//    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer(getDefinition().getName() +": (" + items.size()+" items)\n");
        for (Item item : items) {
            buffer.append(item.toString() + "\n");
        }
        return buffer.toString();
    }
    
   
}
