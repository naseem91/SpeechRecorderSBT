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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
//@XmlSeeAlso({EventItem.class, SegmentItem.class})
public class Item {

    
    @Id
    @SequenceGenerator(name="keys", sequenceName="KEYS", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="keys")   
    private int id;
    private Integer bundleId=null;
    private Set<Item> fromItems=new HashSet<Item>();
    @XmlTransient
    @Transient
    public Set<Item> getFromItems() {
        return fromItems;
    }
    @XmlTransient
    @Transient
    public Set<Item> getToItems() {
        return toItems;
    }

    private Set<Item> toItems=new HashSet<Item>();
    
    private Integer position;
    @XmlTransient
    @Transient
    public Integer getPosition() {
        return position;
    }
    public void setPosition(Integer position) {
        this.position = position;
    }

    private Long sampleStart;
    private Long samplepoint;
    private Long sampleDur;

    @ManyToOne
    private transient Level level;
    private Map<String, Object> labels = new HashMap<String, Object>();
    
   
    @XmlTransient
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @XmlElement(name="id")
    public Integer getBundleId() {
        return bundleId;
    }

    public void setBundleId(Integer bundleId) {
        this.bundleId = bundleId;
    }
    

    public Long getSamplepoint() {
        return samplepoint;
    }

    public void setSamplepoint(Long samplepoint) {
        this.samplepoint = samplepoint;
    }
 
    
    public Long getSampleStart() {
        return sampleStart;
    }

    public void setSampleStart(Long sampleStart) {
        this.sampleStart = sampleStart;
    }

    public Long getSampleDur() {
        return sampleDur;
    }

    public void setSampleDur(Long sampleDur) {
        this.sampleDur = sampleDur;
    }
    
    @XmlTransient
    public Long getSampleEnd(){
        if(sampleStart!=null && sampleDur!=null){
            return sampleStart+sampleDur;
        }
        return null;
    }
    
    public Long sampleDur(){
        if(sampleDur!=null){
            return sampleDur+1;
        }else{
            return null;
        }
    }

    
    @XmlTransient
    public Level getLevel() {
        return level;
    }
    
    public void setLevel(Level tier) {
        this.level = tier;
    }
    
    @XmlTransient
    public String getType() {
        if(level!=null){
            return level.getType();
        }
        return null;
    }
    
    @XmlTransient
    public Map<String, Object> getLabels() {
        return labels;
    }
   
    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }
    
    public void setLabel(String name, Object value) {
        labels.put(name, value);
    }
    
    
    @XmlElement(name="labels")
    public List<Label> getLabelsList() {
        
       List<Label> ll=new ArrayList<Label>();
       for (String key : labels.keySet()) {
           Object v=labels.get(key);
           Label l=new Label();
           l.setName(key);
           l.setValue(v);
           ll.add(l);
       }
//       return Collections.unmodifiableList(ll);
       return ll;
    }

    public void setLabelsList(List<Label> labelsList) {
        labels.clear();
        for(Label l:labelsList){
            labels.put(l.getName(), l.getValue());
        }
    }
    
    public Collection<Object> getLabelValues() {
    	return labels.values();
    }
    
    public String getLabelText() {
        StringBuffer buffer = new StringBuffer();
        Set<String> labelsKeySet=labels.keySet();
        int ksSize=labelsKeySet.size();
        int c=0;
        for (String key : labelsKeySet) {
            buffer.append(key + ":");
            if (labels.get(key) != null) {
                buffer.append(labels.get(key).toString());
            } else {
                buffer.append("NN");
            }
            if(c<ksSize-1){
                buffer.append(";");
            }
            c++;
        }
        return buffer.toString();
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if(bundleId != null){
        	buffer.append("(");
        	buffer.append(bundleId);
        	buffer.append(") ");
        }
        buffer.append(getLabelText());
//        if (buffer.charAt(buffer.length() - 1) == ',') {
//            buffer.deleteCharAt(buffer.length() - 1);
//        }
        return buffer.toString();
     }
}
