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


import ipsk.util.ResourceKey;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generic element which represents a sequential prompt in a section.
 * Base class for Recording and Nonrecording elements.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
@Entity
@Table(name = "recording", uniqueConstraints = {})
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@XmlSeeAlso({Recording.class, Nonrecording.class})
public abstract class PromptItem extends BasicPropertyChangeSupport implements Serializable,Cloneable,Transferable{
	public static final DataFlavor CLASS_DATA_FLAVOR=new DataFlavor(PromptItem.class,null);
	protected int recpromptId;
	protected Presenter presenter;
	protected List<Mediaitem> mediaitems=new ArrayList<Mediaitem>();

    protected int recscriptIndex;
    protected Integer position;
    
    protected Section section; 
   
    public PromptItem() {
        super();
    }
    public PromptItem(boolean initialize) {
		super();
		if (initialize) {
//			mediaitem = new Mediaitem();
		}
	}
    public abstract Element toElement(Document d);
    
    @Transient
    public List<String> getMIMETypes(){
        List<Mediaitem> misList=getMediaitems();
        ArrayList<String> mimeList=new ArrayList<String>();
        for(Mediaitem mi:misList){
            mimeList.add(mi.getMimetype());
        }
        return mimeList;
    }
    
//  Property accessors
	@Id
	@Column(name = "recording_id", unique = true, nullable = false)
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getRecpromptId() {
		return this.recpromptId;
	}

	public void setRecpromptId(int recpromptId) {
		int oldRecpromptId=this.recpromptId;
		this.recpromptId = recpromptId;
		propertyChangeSupport.firePropertyChange("recpromptId", oldRecpromptId, this.recpromptId);
	}

	// TODO DB structure change !!
	@ManyToMany(fetch = FetchType.EAGER)
	 @JoinTable(
		      name="recording_mediaitem",
		      joinColumns=@JoinColumn(name="promptitems_recording_id", referencedColumnName="recording_id")
		      )
	@ResourceKey("media.item")
	public List<Mediaitem> getMediaitems() {
		return this.mediaitems;
	}

	public void setMediaitems(List<Mediaitem> mediaitems) {
		List<Mediaitem> oldMediaItems=this.mediaitems;
		// TODO
		//if(oldMediaItem!=null)oldMediaItem.removePropertyChangeListener((PropertyChangeListener)this);
		this.mediaitems = mediaitems;
//		if(this.mediaitem!=null){
//		    PropertyChangeListener pchListener=(PropertyChangeListener)this;
//		    this.mediaitem.addPropertyChangeListener(pchListener);
//		}
//		propertyChangeSupport.firePropertyChange("mediaitem", oldMediaItem, this.mediaitem);
	}

  
    
    @Transient
    public String getDescription(){
        StringBuffer description=new StringBuffer();
        List<Mediaitem> mis=getMediaitems();
        int misSize=mis.size();
        for(int i=0;i<misSize;i++){
            Mediaitem mi=mis.get(i);
            if(mi.getAlt() !=null) {
                description.append(mi.getAlt());
            }else{
                URI src=mi.getSrc();
                String mimeType=mi.getNNMimetype();
                if (mimeType.startsWith("image")) {
                    description.append("IMAGE: ");
                } else if (mimeType.startsWith("audio")) {
                    description.append("AUDIO: ");
                } else if (mimeType.startsWith("video")) {
                    description.append("VIDEO: ");
                }
                if (mi.getPromptText() !=null) {
                    description.append(mi.getPromptText());
                } else if (src != null) {
                    description.append(mi.getSrc().getPath());
                } 
            }
            if(i+1<misSize){
                // not last item
                description.append(", ");
            }
        }
        return description.toString();
    }

    @Transient
    @XmlTransient
    public int getRecscriptIndex() {
        return recscriptIndex;
    }

    public void setRecscriptIndex(int recscriptIndex) {
    	int oldRecscriptIndex=this.recscriptIndex;
        this.recscriptIndex = recscriptIndex;
        propertyChangeSupport.firePropertyChange("recscriptIndex", oldRecscriptIndex, this.recscriptIndex);
    }

    //@Column(name = "position")
    @ResourceKey("position")
    @XmlTransient
    public Integer getPosition() {
    	return position;
    }

    public void setPosition(Integer position) {
    	//TODO fire property change or not ?
    	this.position=position;
    }

   
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_id")
	@ResourceKey("section")
	@XmlTransient
	public Section getSection() {
		return this.section;
	}

	public void setSection(Section section) {
		Section oldSection=this.section;
		this.section = section;
		propertyChangeSupport.firePropertyChange("section", oldSection,this.section);
	}
	
	public Object clone() throws CloneNotSupportedException{
	   
         Object c=super.clone();
         PromptItem pi=(PromptItem)c;
         pi.propertyChangeSupport=new PropertyChangeSupport(this);
         List<Mediaitem> mis=mediaitems;
         ArrayList<Mediaitem> cMis=new ArrayList<Mediaitem>();
         for(Mediaitem mi:mis){
             Mediaitem cMi=(Mediaitem)mi.clone();
             cMis.add(cMi);
         }
         pi.mediaitems=cMis;
        return pi;
	}
	
	@Transient
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		 if (!isDataFlavorSupported(flavor)) {
           throw new UnsupportedFlavorException(flavor);
       }
       return this;
	}
	@Transient
	public DataFlavor[] getTransferDataFlavors() {
		
		return new DataFlavor[]{CLASS_DATA_FLAVOR};
	}
	@Transient
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return(CLASS_DATA_FLAVOR.equals(flavor));
	}
	@Transient
    public Presenter getPresenter() {
        return presenter;
    }
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    
}
