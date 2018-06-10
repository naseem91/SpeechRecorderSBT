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

import ipsk.db.speech.utils.MIMETypeWorkaround;
import ipsk.beans.PreferredDisplayOrder;
import ipsk.persistence.ObjectImmutableIfReferenced;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents a recinstructions element of the recording script.
 *
 */
@Entity
@Table(name = "recinstructions")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@PreferredDisplayOrder("recinstructionsId,recinstructions")
public class Recinstructions extends BasicPropertyChangeSupport implements java.io.Serializable {

	public static final String ELEMENT_NAME = "recinstructions";
	public final static String ATTMIME = "mimetype";
	public final static String ATTCHARSET = "charset";
	
	public final static String DEF_CHARSET="UTF-8";
	 
	private int recinstructionsId;

	private String mimetype;

	private String recinstructions;

	private Set<Recording> recordingsSet = new HashSet<Recording>(0);
	private String[] comments=new String[0];
    private String charSet;

	// Constructors

	/** default constructor */
	public Recinstructions() {
		super();
	}

	/** minimal constructor */
	public Recinstructions(int recinstructionsId) {
		this();
		this.recinstructionsId = recinstructionsId;
	}

	/** full constructor */
	public Recinstructions(int recinstructionsId, String mimetype,
			String recinstructions, Set<Recording> recordings) {
		this(recinstructionsId);
		this.mimetype = mimetype;
		this.recinstructions = recinstructions;
		this.recordingsSet = recordings;
	}

	public Recinstructions(Element e) {
		super();
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
		  Attr attMimetype=e.getAttributeNode(ATTMIME);
          Attr attCharset=e.getAttributeNode(ATTCHARSET);
         
          String mimetypeAttrVal=null;
          String charsetAttrVal=null;
          if(attMimetype!=null){
              mimetypeAttrVal=attMimetype.getValue();
          }
          if(attCharset!=null){
              charsetAttrVal=attCharset.getValue();
          }
          MIMETypeWorkaround mtw=new MIMETypeWorkaround(mimetypeAttrVal,charsetAttrVal);
          setMimetype(mtw.getConvertedMimeType());
          setCharSet(mtw.getConvertedCharset());
          
		setRecinstructions(e.getTextContent());
	}

	// Property accessors
	@Id
	@Column(name = "recinstructions_id", unique = true, nullable = false)
	//@SequenceGenerator(name="ID_SEQ",sequenceName="id_seq")
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getRecinstructionsId() {
		return this.recinstructionsId;
	}

	public void setRecinstructionsId(int recinstructionsId) {
		int oldRecinstructionsId=this.recinstructionsId;
		this.recinstructionsId = recinstructionsId;
		propertyChangeSupport.firePropertyChange("recinstructionsId", oldRecinstructionsId, this.recinstructionsId);
	}

	@Column(name = "mimetype", updatable=false,length = 100)
	public String getMimetype() {
		return this.mimetype;
	}

	public void setMimetype(String mimetype) {
		String oldMimetype=this.mimetype;
		this.mimetype = mimetype;
		propertyChangeSupport.firePropertyChange("mimetype", oldMimetype, this.mimetype);
	}

	   @Column(name = "charset", length = 100)
	    public String getCharSet() {
	        return charSet;
	    }

	    public void setCharSet(String charSet) {
	        this.charSet = charSet;
	    }
	    
	    @Transient
	    public String getNNCharSet() {
	        if(charSet==null)return DEF_CHARSET; 
	        return charSet;
	    }
	    
	@Column(name = "recinstructions", updatable=false,length = 1000)
	@ResourceKey("recinstructions")
	public String getRecinstructions() {
		return this.recinstructions;
	}

	public void setRecinstructions(String recinstructions) {
		String oldRecinstructions=this.recinstructions;
		this.recinstructions = recinstructions;
		propertyChangeSupport.firePropertyChange("recinstructions", oldRecinstructions, this.recinstructions);
	}

	
	
	@OneToMany(mappedBy = "recinstructions")
	@ObjectImmutableIfReferenced
	public Set<Recording> getRecordingsSet() {
		return this.recordingsSet;
	}

	public void setRecordingsSet(Set<Recording> recordings) {
		this.recordingsSet = recordings;
	}
	
	public String toString(){
		if(recinstructions!=null){
			return recinstructions;
		}else{
			return "";
		}
	}
	
	public boolean equals(Object o){
		if (o==null)return false;
		if(! (o instanceof Recinstructions))return false;
		if(o==this)return true;
		
		Recinstructions other=(Recinstructions)o;
		if(other.getRecinstructionsId()!=recinstructionsId)return false;
		if(other.getMimetype()==null){
			if(mimetype !=null)return false;
		}else{
			if(! other.getMimetype().equals(mimetype))return false;
		}
		if(other.getRecinstructions()==null){
			if(recinstructions!=null)return false;
		}else{
			if(! other.getRecinstructions().equals(recinstructions))return false;
		}
		return true;
	}

	public Element toElement(Document d) {
		Element e = d.createElement(ELEMENT_NAME);
		for(String comm:comments){
			e.appendChild(d.createComment(comm));
		}
		String mimeType = getMimetype();
		if (mimeType != null)
			e.setAttribute(ATTMIME, mimeType);
		
		String cs =getCharSet();
	        if(cs!=null){
	            e.setAttribute(ATTCHARSET,cs);
	        }
		e.appendChild(d.createTextNode(getRecinstructions()));

		return e;
	}

}
