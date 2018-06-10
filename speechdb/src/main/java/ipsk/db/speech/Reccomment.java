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

import ipsk.persistence.ObjectImmutableIfReferenced;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents a reccomment element of the recording script.
 */
@Entity
@Table(name = "reccomment")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
public class Reccomment extends BasicPropertyChangeSupport implements java.io.Serializable {

	public static final String ELEMENT_NAME = "reccomment";

	// Fields    

	private int reccommentId;

	private String reccomment;

	private Set<Recording> recordingsSet = new HashSet<Recording>(0);

	private String[] comments=new String[0];

	// Constructors

	/** default constructor */
	public Reccomment() {
		super();
	}

	/** minimal constructor */
	public Reccomment(int reccommentId) {
		this();
		this.reccommentId = reccommentId;
	}

	/** full constructor */
	public Reccomment(int reccommentId, String reccomment,
			Set<Recording> recordings) {
		this(reccommentId);
		this.reccomment = reccomment;
		this.recordingsSet = recordings;
	}
	
	public Reccomment(Element e) {
		this();
		NodeList childs=e.getChildNodes();
		ArrayList<String>commentsArrList=new ArrayList<String>();
		for(int ci=0;ci<childs.getLength();ci++){
			Node n=childs.item(ci);
			if(n.getNodeType()==Node.COMMENT_NODE){
				commentsArrList.add(n.getNodeValue());
			}
		}
		comments=commentsArrList.toArray(new String[0]);
//		Attr attr=e.getAttributeNode(ATTMIME);
//		if(attr != null){
//			setMimetype(attr.getValue());
//		}
		setReccomment(e.getTextContent());
	}
	
	// Property accessors
	@Id
	@Column(name = "reccomment_id", unique = true, nullable = false)
	@GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getReccommentId() {
		return this.reccommentId;
	}

	public void setReccommentId(int reccommentId) {
		int oldReccommentId=this.reccommentId;
		this.reccommentId = reccommentId;
		propertyChangeSupport.firePropertyChange("reccommentId", oldReccommentId, this.reccommentId);
	}

	@Column(name = "reccomment", length = 1000)
	@ResourceKey("reccomment")
	public String getReccomment() {
		return this.reccomment;
	}

	public void setReccomment(String reccomment) {
		String oldReccomment=this.reccomment;
		this.reccomment = reccomment;
		propertyChangeSupport.firePropertyChange("reccomment", oldReccomment, this.reccomment);
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reccomment")
	@ObjectImmutableIfReferenced
	public Set<Recording> getRecordingsSet() {
		return this.recordingsSet;
	}

	public void setRecordingsSet(Set<Recording> recordings) {
		this.recordingsSet = recordings;
	}
	
	public String toString(){
		if(reccomment!=null){
			return reccomment;
		}else{
			return "";
		}
	}
	
	public boolean equals(Object o){
		if (o==null)return false;
		if(! (o instanceof Reccomment))return false;
		if(o==this)return true;
		
		Reccomment other=(Reccomment)o;
		if(other.getReccommentId()!=reccommentId)return false;
		
		if(other.getReccomment()==null){
			if(reccomment!=null)return false;
		}else{
			if(! other.getReccomment().equals(reccomment))return false;
		}
		return true;
	}

	
	public Element toElement(Document d) {
		Element e = d.createElement(ELEMENT_NAME);
		for(String comm:comments){
			e.appendChild(d.createComment(comm));
		}
		e.appendChild(d.createTextNode(getReccomment()));

		return e;
	}


}
