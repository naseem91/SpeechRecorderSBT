//    IPS Java Speech Database
//    (c) Copyright 2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Speech Database
//
//
//    IPS Java Speech Database is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Speech Database is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Speech Database.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.db.speech;


import ipsk.beans.DOMElementConvertible;
import ipsk.beans.LinkID;
import ipsk.util.ResourceKey;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Script generated by hbm2java
 */
@Entity
@Table(name = "script")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("recscript")
public class Recordingscript extends BasicPropertyChangeSupport implements
        java.io.Serializable, PropertyChangeListener,DOMElementConvertible {

    protected static final String ELEMENT_NAME = "recordingscript";

    // Fields

    protected int scriptId;
    
    private List<Section> sections = new ArrayList<Section>();

//    private Set<Section> sectionsSet = new HashSet<Section>(0);

    private ArrayList<ArrayList<String>> comments = new ArrayList<ArrayList<String>>();

    private boolean propertyChangeSupportEnabled=false;

    @Transient
    @XmlTransient
    public boolean isPropertyChangeSupportEnabled() {
		return propertyChangeSupportEnabled;
	}

	public void setPropertyChangeSupportEnabled(boolean propertyChangeSupportEnabled) {
		this.propertyChangeSupportEnabled = propertyChangeSupportEnabled;
	}

	/** default constructor */
    public Recordingscript() {
        super();
    }

    /** minimal constructor */
    public Recordingscript(int scriptId) {
        this();
        this.scriptId = scriptId;

    }

//    /** full constructor */
//    public Recordingscript(int scriptId, Set<Section> sections) {
//        this(scriptId);
//        this.sectionsSet = sections;
//
//    }

    public Recordingscript(Element e) {
        super();
        insertElement(e);
    }

    public void init() {
//        sectionsSet = new HashSet<Section>(0);
    	sections=new ArrayList<Section>();
        comments = new ArrayList<ArrayList<String>>();
    }
    public Set<List<String>> requiredMIMETypeCombinations(){
        HashSet<List<String>> reqMIMETypes=new HashSet<List<String>>();
        List<Section> secList=getSections();
        for(Section sec:secList){
          Set<List<String>> reqMimesForSec=sec.requiredMIMETypeCombinations();
           reqMIMETypes.addAll(reqMimesForSec);
        }
        return reqMIMETypes;
    }
    
    protected void insertElement(Element e) {
        NodeList childs = e.getChildNodes();

        ArrayList<Section> sectionsList = new ArrayList<Section>();
        int ePos = 0;
        int sectionPos=0;
        for (int ci = 0; ci < childs.getLength(); ci++) {
            Node n = childs.item(ci);
            if (n.getNodeType() == Node.COMMENT_NODE) {
                String comm = n.getNodeValue();
                ArrayList<String> pComms = null;
                while (comments.size() <= ePos) {
                    comments.add(new ArrayList<String>());
                }
                pComms = comments.get(ePos);
                pComms.add(comm);

            } else if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) n;
                String elName = el.getNodeName();
                if (elName.equals("section")) {
                    Section sectionFromElement=new Section(el);
                    if(this instanceof Script){
                        sectionFromElement.setScript((Script)this);
                    }
                    sectionFromElement.setSectionPosition(sectionPos++);
                    sectionFromElement.setPropertyChangeSupportEnabled(true);
                    sectionsList.add(sectionFromElement);
                }
                ePos++;
            }
        }

//        setSections(sectionsList.toArray(new Section[0]));
        setSections(sectionsList);
    }

    // Property accessors
    @Id
    @Column(name = "script_id", unique = true, nullable = false)
    //@SequenceGenerator(name = "ID_SEQ", sequenceName = "id_seq")
    @GeneratedValue(generator = "id_gen")
    @LinkID
    @ResourceKey("id")
    public int getScriptId() {
        return this.scriptId;
    }

    public void setScriptId(int scriptId) {
        int oldScriptId = this.scriptId;
        this.scriptId = scriptId;
        propertyChangeSupport.firePropertyChange("scriptId", oldScriptId,
                scriptId);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "script")
    @OrderColumn(name="section_position")
    @ResourceKey("sections")
    public List<Section> getSections(){
    	return sections;
            }
//    public Set<Section> getSectionsSet() {
//        if (sectionsSet != null)
//            return this.sectionsSet;
//        if (sections != null) {
//            HashSet<Section> tmpSectionsSet = new HashSet<Section>();
//            for (int pos = 0; pos < sections.length; pos++) {
//                Section s = sections[pos];
//                s.setSectionPosition(pos);
//                // each section is unique now
//                // ( Set does not alow duplicate entries)
//                tmpSectionsSet.add(s);
//            }
//            return tmpSectionsSet;
//        }
//        return null;
//    }

//    public void setSectionsSet(Set<Section> sectionsSet) {
//        this.sectionsSet = sectionsSet;
//        sections = null;
//        // // sort by section_position column
//        // ArrayList<Section> sortedSections = new ArrayList<Section>();
//        // if(this.sectionsSet != null){
//        // sortedSections.addAll(this.sectionsSet);
//        // }
//        // Collections.sort(sortedSections, new Comparator<Section>() {
//        // public int compare(Section o1, Section o2) {
//        // return o1.getSectionPosition() - o2.getSectionPosition();
//        // }
//        // });
//        // sections=sortedSections.toArray(new Section[0]);
//    }

    /**
     * Returns all itemcodes used without duplicates.
     * A section may have duplicate itemcodes. To retrieve
     * ietmcodes including duplictes use {@link #itemCodesList() itemCodesList}.
     * @return set of unique itemcodes
     */
    @Transient
    public Set<String> itemCodesSet(){
        HashSet<String> ics=new HashSet<String>();
        for(Section s:sections){
            Set<String> sIcs=s.itemCodesSet();
            ics.addAll(sIcs);
            
        }
        return ics;
    }
    
    /**
     * Returns all itemcodes used with duplicates.
     * @return list of itemcodes
     */
    @Transient
    public List<String> itemCodesList(){
        List<String> icl=new ArrayList<String>();
        for(Section s:sections){
            List<String> sIcl=s.itemCodesList();
            icl.addAll(sIcl);
            
        }
        return icl;
    }
    
    public Element toElement(Document d) {
        Element e = d.createElement(ELEMENT_NAME);

//        Section[] sortedSections = null;
//        if (sections != null) {
//            sortedSections = sections;
//
//        } else if (sectionsSet != null) {
//
        // // sort by section_position column
//            ArrayList<Section> sortedSectionsList = new ArrayList<Section>(
//                    getSectionsSet());
//            Collections.sort(sortedSectionsList, new Comparator<Section>() {
        // public int compare(Section o1, Section o2) {
        // return o1.getSectionPosition() - o2.getSectionPosition();
        // }
        // });
//            sortedSections = sortedSectionsList.toArray(new Section[0]);
//        }
        int ePos = 0;
//        for (; ePos < sortedSections.length; ePos++) {
        List<Section> sectionsList=getSections();
        for (; ePos < sectionsList.size(); ePos++) {
            if (ePos < comments.size()) {
                ArrayList<String> posCmts = comments.get(ePos);
                if (posCmts != null) {
                    for (String c : posCmts) {
                        e.appendChild(d.createComment(c));
                    }
                }
            }
//            Section s = sortedSections[ePos];
            Section s=sectionsList.get(ePos);
            e.appendChild(s.toElement(d));
        }
        for (; ePos < comments.size(); ePos++) {
            ArrayList<String> posCmts = comments.get(ePos);
            if (posCmts != null) {
                for (String c : posCmts) {
                    e.appendChild(d.createComment(c));
                }
            }
        }

        return e;
    }
    
    
   

//    @Transient
//    public Section[] getSections() {
//        if (sections != null)
//            return sections;
//        if (sectionsSet != null) {
//            ArrayList<Section> sortedSections = new ArrayList<Section>(
//                    getSectionsSet());
//            Collections.sort(sortedSections, new Comparator<Section>() {
//                public int compare(Section o1, Section o2) {
//                    return o1.getSectionPosition() - o2.getSectionPosition();
//                }
//            });
//
//            return sortedSections.toArray(new Section[0]);
//        }
//        return null;
//    }

//    public void setSections(Section[] sections) {
//        Section[] oldSections = this.sections;
//        if (oldSections != null) {
//            for (Section os : oldSections) {
//                os.removePropertyChangeListener(this);
//            }
//        }
//        this.sections = sections;
//        if (this.sections != null) {
//            for (Section os : this.sections) {
//                os.addPropertyChangeListener(new PropertyChangeListener() {
//                    public void propertyChange(PropertyChangeEvent evt) {
//                        String propName = evt.getPropertyName();
//                        String hPropName = "sections." + propName;
//                        propertyChangeSupport.firePropertyChange(hPropName, evt
//                                .getOldValue(), evt.getNewValue());
//                    }
//                });
//            }
//        }
//        sectionsSet = null;
//        propertyChangeSupport.firePropertyChange("sections", oldSections,
//                sections);
//    }


    public void setSections(List<Section> sections){
    	List<Section> oldSections=null;
    	if(propertyChangeSupportEnabled){
    		oldSections = this.sections;
        if (oldSections != null) {
            for (Section os : oldSections) {
                os.setScript(null);
                os.removePropertyChangeListener(this);
            }
        }
    	}
        this.sections = sections;
    	if(propertyChangeSupportEnabled){
        if (this.sections != null) {
            for (Section os : this.sections) {
                if(this instanceof Script){
                    os.setScript((Script)this);
                }
                os.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        String propName = evt.getPropertyName();
                        String hPropName = "sections." + propName;
                        propertyChangeSupport.firePropertyChange(hPropName, evt
                                .getOldValue(), evt.getNewValue());
                    }
                });
            }
        }
        propertyChangeSupport.firePropertyChange("sections", oldSections,
                sections);
    }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String propName = evt.getPropertyName();
        String hPropName = ELEMENT_NAME + "." + propName;
        propertyChangeSupport.firePropertyChange(hPropName, evt.getOldValue(),
                evt.getNewValue());
    }
    
    public void updatePositions(){

        int sSize=sections.size();
        for(int i=0;i<sSize;i++){
            Section s=sections.get(i);
            s.setSectionPosition(i);
            if(this instanceof Script){
                Script script=(Script)this;
                s.setScript(script);
            }
            s.updatePositions();
        }
    }
    
    @Transient
     @XmlTransient
    public boolean recordingCodesUnique(){
       
        for(Section s:sections){
            boolean sectionItemsUnique=s.recordingCodesUnique();
            if(!sectionItemsUnique){
                return false;
            }
        }
        return true;
    }
    
    @Transient
    @XmlTransient
    public boolean needsSilenceDetector(){
        for(Section s:sections){
            if(s.needsSilenceDetector()){
                return true;
            }
        }
        return false;
    }
    
    @Transient
    @XmlTransient
    public boolean needsBeep(){
        for(Section s:sections){
            if(s.needsBeep()){
                return true;
            }
        }
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException{
        
        Object c=super.clone();
        Recordingscript crs=(Recordingscript)c;
        crs.propertyChangeSupport=new PropertyChangeSupport(this);
        List<Section> sections=this.sections;
        ArrayList<Section> csections=new ArrayList<Section>();
        for(Section s:sections){
            Section cS=(Section)s.clone();
            csections.add(cS);
        }
        crs.sections=csections;
       return crs;
   }

   

//    @Transient
//    public String[] getComments() {
//        return comments;
//    }
//
//    public void setComments(String[] comments) {
//        this.comments = comments;
//    }

}
