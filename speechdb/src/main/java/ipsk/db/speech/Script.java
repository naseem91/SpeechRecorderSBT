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
import ipsk.beans.PreferredDisplayOrder;
import ipsk.beans.dom.DOMRoot;
import ipsk.text.table.ColumnDescriptor;
import ipsk.text.table.TableExportProvider;
import ipsk.text.table.TableExportSchemaProvider;
import ipsk.util.LocalizableMessage;
import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents the script element of the recording script.
 */
@Entity
@DiscriminatorValue("script")
@DOMRoot
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("script")
@PluralResourceKey("scripts")
@PreferredDisplayOrder("name,projects,metadatasSet,sections,sessionsSet,description")
@XmlType(name="script")
public class Script extends Recordingscript implements Cloneable,DOMElementConvertible,TableExportProvider{
	
    
	public enum Scope {APPLICATION,PROJECT,SCRIPT,SECTION};
	
	public final static String ELEMENT_NAME="script";
	public final static String ATT_ID="id";
	// Fields    


	private String name;

	private String description;
	
	private Metadata metadata=null;


	private Set<Session> sessionsSet = new HashSet<Session>(0);
	
	private Set<Project> projects=new HashSet<Project>(0);
	
	//private String[] comments=new String[0];
    private ArrayList<ArrayList<String>> comments=new ArrayList<ArrayList<String>>();
    
    private Element element;
	
    public static class ScriptTableSchemaProvider implements TableExportSchemaProvider{
        /* (non-Javadoc)
         * @see ipsk.text.table.TableExportProvider#getColumnDescriptors()
         */
        @Override
        public List<ColumnDescriptor> getColumnDescriptors() {
           ColumnDescriptor itCodeDsc=new ColumnDescriptor("itemcode", true,new LocalizableMessage("Item code"));
           ColumnDescriptor promptDsc=new ColumnDescriptor("prompt", true,new LocalizableMessage("Prompt"));
           
           List<ColumnDescriptor> colDscs=new ArrayList<ColumnDescriptor>();
           colDscs.add(itCodeDsc);
           colDscs.add(promptDsc);
           return(colDscs);
        }

        /* (non-Javadoc)
         * @see ipsk.text.table.TableExportProvider#isCompleteTableLossless()
         */
        @Override
        public boolean isCompleteTableLossless() {
            
            return false;
        }
        
    }
    
    public static ScriptTableSchemaProvider scriptTableSchemaProvider=new ScriptTableSchemaProvider();
    
	// Constructors

	/** default constructor */
	public Script() {
		super();
	}

	/** minimal constructor */
	public Script(int scriptId, String name) {
		super(scriptId);
		this.name = name;
	}


	
	/** full constructor */
	public Script(int scriptId, String name, String description,
			Set<Section> sections, Set<Metadata> metadatas,
			Set<Session> sessions) {
		super(scriptId);
		this.name = name;
		this.description = description;
		
		this.sessionsSet = sessions;
	}

	
	public Script(Element e){
		super();
        element=e;
		insertElement(e);
	}
    
    public void init(){
        super.init();
        setName(null);
        setDescription(null);
        metadata=null;
        setSections(null);
        comments=new ArrayList<ArrayList<String>>();
    }
    
    public void insertElement(Element e){
        init();
        super.insertElement((Element)e.getElementsByTagName(Recordingscript.ELEMENT_NAME).item(0));
        String id=e.getAttribute("id");
        // Hmm 
        //id->name or id -> scriptId
        setName(id);
        NodeList childs=e.getChildNodes();
        int ePos=0;
        //ArrayList<String>commentsArrList=new ArrayList<String>();
//        ArrayList<Metadata> metadatasList=new ArrayList<Metadata>();
        for(int ci=0;ci<childs.getLength();ci++){
            Node n=childs.item(ci);
            if(n.getNodeType()==Node.COMMENT_NODE){
                //commentsArrList.add(n.getNodeValue());
                String comm=n.getNodeValue();
                ArrayList<String>pComms=null;
                while(comments.size()<=ePos){
                    comments.add(new ArrayList<String>());
//                    pComms=new ArrayList<String>();
//                    comments.set(ePos, pComms);
                }
                pComms=comments.get(ePos);
                pComms.add(comm);
            }else if (n.getNodeType()==Node.ELEMENT_NODE){
                Element el=(Element)n;
                if(el.getNodeName().equals("metadata")){
                    metadata=new Metadata(el);
                } else{
                    // TODO error
                }
                ePos++;
            }
        }
    }

	@Column(name = "name", nullable = false, length = 10)
	@ResourceKey("name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		String oldName=this.name;
		this.name = name;
		propertyChangeSupport.firePropertyChange("name", oldName, name);
	}

	@Column(name = "description", length = 100)
	@ResourceKey("description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		String oldDescription=this.description;
		this.description = description;
		propertyChangeSupport.firePropertyChange("description", oldDescription, this.description);	
	}

	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "script")
	@ResourceKey("metadata")
	public Metadata getMetadata() {
		return this.metadata;
	}

    public void setMetadata(Metadata metadata) {
        Metadata oldMetadata=this.metadata;
        this.metadata = metadata;
        propertyChangeSupport.firePropertyChange("metadata", oldMetadata, this.metadata);
    }
    
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "script")
	@ResourceKey("sessions")
	@XmlTransient
	public Set<Session> getSessionsSet() {
		return this.sessionsSet;
	}

	public void setSessionsSet(Set<Session> sessions) {
		this.sessionsSet = sessions;
	}
	
	@ManyToMany(mappedBy="scripts",fetch = FetchType.LAZY)
	@ResourceKey("projects")
	@XmlTransient
	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	
	
	
	public void insertIntoElement(Document d,Element e){
		Element de=d.getDocumentElement();
		
//		for(String comm:comments){
//			de.appendChild(d.createComment(comm));
//		}
		de.setAttribute(ATT_ID, getName());
        int ePos=0;
//		for(;ePos<metadatas.length;ePos++){
            if(ePos<comments.size()){
            ArrayList<String> posCmts=comments.get(ePos);
            if(posCmts!=null){
                for(String c:posCmts){
                    de.appendChild(d.createComment(c));
                }
            }
            }
            if(metadata!=null){
                de.appendChild(metadata.toElement(d));
                ePos++;
            }
//		}
		for(;ePos<comments.size();ePos++){
            ArrayList<String> posCmts=comments.get(ePos);
            if(posCmts!=null){
                for(String c:posCmts){
                    de.appendChild(d.createComment(c));
                }
            }
        }
		de.appendChild(super.toElement(d));
	}
    
	public Element toElement(Document d){
		Element e=d.createElement(ELEMENT_NAME);
        e.setAttribute(ATT_ID, getName());
        int ePos=0;
//        for(;ePos<metadatas.length;ePos++){
            if(ePos<comments.size()){
            ArrayList<String> posCmts=comments.get(ePos);
            if(posCmts!=null){
                for(String c:posCmts){
                    e.appendChild(d.createComment(c));
                }
            }
            }
            if(metadata!=null){
                
                e.appendChild(metadata.toElement(d));
                ePos++;
            }
//        }
        for(;ePos<comments.size();ePos++){
            ArrayList<String> posCmts=comments.get(ePos);
            if(posCmts!=null){
                for(String c:posCmts){
                    e.appendChild(d.createComment(c));
                }
            }
        }
        e.appendChild(super.toElement(d));
		return e;
	}
	
	public void shuffleItems(){
		for(Section sect:getSections()){
			sect.shuffleItems();
		}
	}


	
	public Object clone() throws CloneNotSupportedException{
        
        Object c=super.clone();
        Script cs=(Script)c;
//        metadata=(Metadata) metadata.clone();
        cs.propertyChangeSupport=new PropertyChangeSupport(this);
        
       return cs;
   }
	
	public String toString(){
		return name;
	}

   

    /* (non-Javadoc)
     * @see ipsk.text.table.TableExportProvider#tableData(java.util.List)
     */
    @Override
    public List<List<List<String>>> tableData(List<ColumnDescriptor> columns) {
       
        
        List<Section> sects=getSections();
        List<List<List<String>>> groups=new ArrayList<List<List<String>>>(sects.size());
        for(Section sect:sects){
            List<PromptItem> pis=sect.getPromptItems();
            List<List<String>> group=new ArrayList<List<String>>();
            for(PromptItem pi:pis){
                // only recordings
               if(pi instanceof Recording){
                   // one record (table line) per prompt item
                   List<String> record=new ArrayList<String>();
                   Recording r=(Recording)pi;
                   
                   for(ColumnDescriptor cd:columns){
                       String colKeyNm=cd.getKeyName();
                       if("itemcode".equals(colKeyNm)){
                           String itemCode=r.getItemcode();
                           record.add(itemCode);
                       }else if("prompt".equals(colKeyNm)){
                           record.add(r.getDescription());
                       }
                   }
                   group.add(record);
               }
            }
            groups.add(group);
        }
        return(groups);
    }

    /* (non-Javadoc)
     * @see ipsk.text.table.TableExportProvider#tableData()
     */
    @Override
    public List<List<List<String>>> tableData() {
        return(tableData(null));
    }

}
