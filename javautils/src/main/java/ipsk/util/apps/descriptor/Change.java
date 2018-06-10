//    IPS Java Utils
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.util.apps.descriptor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ipsk.text.VersionPattern;
import ipsk.text.VersionPattern.VersionPatternXMLAdapter;
import ipsk.util.LocalizableMessage;
import ipsk.util.i18n.LocalizableMessageXMLAdapter;

/**
 * @author klausj
 *
 */
@XmlType(name="applicationChange",propOrder={"affectsVersions","description"})
public class Change {

    @XmlType()
    public static enum Priority {OPTIONAL,RECOMMENDED,STRONGLY_RECOMMENDED};
    
    public static enum Type {SECURITY_FIX,BUGFIX,ENHANCEMENT,FEATURE};
    
    private String id;
    private Priority priority=null;
    private Type type=null;
    
    private LocalizableMessage description;
    
    private VersionPattern affectsVersions;
   
    /**
     * 
     */
    public Change() {
        super();
        
    }
    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
  
    @XmlAttribute(required=false)
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    @XmlJavaTypeAdapter(VersionPatternXMLAdapter.class)
    public VersionPattern getAffectsVersions() {
        return affectsVersions;
    }
    public void setAffectsVersions(VersionPattern affectsVersions) {
        this.affectsVersions = affectsVersions;
    }
    @XmlJavaTypeAdapter(LocalizableMessageXMLAdapter.class)
    public LocalizableMessage getDescription() {
        return description;
    }

    public void setDescription(LocalizableMessage description) {
        this.description = description;
    }
   
    @XmlAttribute
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    
    public String toString(){
        StringBuffer sb=new StringBuffer();
        boolean lineBegin=true;
        if(id!=null){
            sb.append("ID: ");
            sb.append(id);
            lineBegin=false;
            
        }
        if(description!=null){
            if(!lineBegin){
                sb.append(',');
            }
            lineBegin=false;
            sb.append(description.localize());
        }
        if(type!=null){
            if(!lineBegin){
                sb.append(',');
            }
            lineBegin=false;
            sb.append(type);
        }
        if(priority!=null){
            if(!lineBegin){
                sb.append(',');
            }
            lineBegin=false;
            sb.append(priority);
        }
        return sb.toString();
    }

}
