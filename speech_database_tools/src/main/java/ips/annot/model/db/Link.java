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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Link {

    private String label;
    private Item from=null;
    private Integer fromID=null;
    private Item to=null;
    private Integer toID=null;
    
    @XmlTransient
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    @XmlTransient
    public Item getFrom() {
        return from;
    }
    public void setFrom(Item from) {
        this.from = from;
    }
    @XmlTransient
    public Item getTo() {
        return to;
    }
    public void setTo(Item to) {
        this.to = to;
    }
    
    public int getFromID(){
    	if(from!=null){
    		return from.getBundleId();
    	}else{
    		return fromID;
    	}
    }
    public void setFromID(int id){
       fromID=id;
    }
    
    public int getToID(){
    	if(to!=null){
    		return to.getBundleId();
    	}else{
    		return toID;
    	}
    }
    public void setToID(int id){
       toID=id;
    }
    
    public String toString() {
    	StringBuffer sb=new StringBuffer();
    	
        sb.append(getFromID());
        sb.append(" -> ");
        sb.append(getToID());
        if(label!=null){
    		sb.append(": "+ label);
    	}
        return sb.toString();
    }
}
