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


import ipsk.beans.PreferredDisplayOrder;
import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents a section element of the recording script.
 */
@Entity
@Table(name = "section")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("section")
@PluralResourceKey("sections")
@PreferredDisplayOrder("sectionId,name,sectionPosition,promptItems,mode,order,*")
public class Section extends BasicPropertyChangeSupport implements
		java.io.Serializable, Cloneable,Comparable<Section>, Transferable {

	public final static String ELEMENT_NAME = "section";
	
	public static final DataFlavor CLASS_DATA_FLAVOR=new DataFlavor(Section.class,null);


	// TODO: should one not use the status constants as defined in RecStatus?
	public enum PromptPhase {
	    IDLE("idle"), RECORDING("recording"), PRERECORDING("prerecording");

	    PromptPhase(String value) {
	    	this.value = value;
	    }
	    private final String value;

	    public String value() {
	    	return value; 
	    }
	    public String toString() {
	    	return value; 
	    }
	    public static PromptPhase getByValue(String value){
	    	for(PromptPhase pp:PromptPhase.values()){
	    		if(pp.value.equals(value)){
	    			return pp;
	    		}
	    	}
	    	return null;
	    }
	}
	public enum Order {
	    SEQUENTIAL("sequential"), RANDOM("random");

	    Order(String value) {
	    	this.value = value;
	    }
	    private final String value;

	    public String value() {
	    	return value; 
	    }
	    public String toString() {
	    	return value; 
	    }
	    public static Order getByValue(String value){
	    	for(Order pp:Order.values()){
	    		if(pp.value.equals(value)){
	    			return pp;
	    		}
	    	}
	    	return null;
	    }
	}
	
	public enum Mode {
	    MANUAL("manual"), AUTOPROGRESS("autoprogress"),AUTORECORDING("autorecording");

	    Mode(String value) {
	    	this.value = value;
	    }
	    private final String value;

	    public String value() {
	    	return value; 
	    }
	    public String toString() {
	    	return value; 
	    }
	    public static Mode getByValue(String value){
	    	for(Mode pp:Mode.values()){
	    		if(pp.value.equals(value)){
	    			return pp;
	    		}
	    	}
	    	return null;
	    }
	}

	public static final PromptPhase DEF_PROMPT_PHASE=PromptPhase.IDLE;
	

	private int sectionId;

	// private Recordingscript recordingscript;
	private Script script;

	private String name;

	private Boolean speakerDisplay;

	private String speakerdisplayStr;

	private Integer sectionPosition;

	private PromptPhase promptphase;

	private Mode mode;

	private Order order;

//	private Set<PromptItem> promptItemsSet = new HashSet<PromptItem>(0);
//
//	// TODO use base class for Recording, Nonrecording: PromptItem
//	// private Recording[] recordings=new Recording[0];
//	private PromptItem[] promptItems = null;
	private List<PromptItem> promptItems=new ArrayList<PromptItem>();
	
	private String[] comments=new String[0];
	
	private boolean defaultSpeakerDisplay=false;

	private Mode defaultMode=Mode.MANUAL;
	
	private Order defaultOrder=Order.SEQUENTIAL;
	
	private boolean training=false;
	
	private List<PromptItem> shuffledPromptItems=new ArrayList<PromptItem>();
	
	private boolean propertyChangeSupportEnabled=false;

	@Transient
	@XmlTransient
	public boolean isPropertyChangeSupportEnabled() {
		return propertyChangeSupportEnabled;
	}

	public void setPropertyChangeSupportEnabled(boolean propertyChangeSupportEnabled) {
		this.propertyChangeSupportEnabled = propertyChangeSupportEnabled;
	}

	// Constructors

	/** default constructor */
	public Section() {
		super();
	}
	
	public Section(boolean initialize) {
		this();
		if (initialize) {
			// section requires at least one prompt Item
//			setPromptItems(new PromptItem[] { new Recording() });
			
			promptItems=new ArrayList<PromptItem>();
			promptItems.add(new Recording());
		}
	}
		
		

	/** minimal constructor */
	public Section(int sectionId) {
		this();
		this.sectionId = sectionId;
	}

//	/** full constructor */
//	public Section(int sectionId, Script script, String name,
//			String speakerdisplay, Integer sectionPosition, PromptPhase promptphase,
//			Mode mode, Order order, Set<PromptItem> recordings) {
//		this(sectionId);
//		this.script = script;
//		this.name = name;
//		this.speakerdisplayStr = speakerdisplay;
//		this.sectionPosition = sectionPosition;
//		this.promptphase = promptphase;
//		this.mode = mode;
//		this.order = order;
//		this.promptItemsSet = recordings;
//	}

	public Section(Element e) {
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
		initializeSection();
		// all attributes are optional
		Attr attr = e.getAttributeNode("name");
		if (attr != null)
			setName(attr.getValue());
		attr = e.getAttributeNode("mode");
		if (attr != null)
			setMode(Mode.getByValue(attr.getValue()));
		attr = e.getAttributeNode("order");
		if (attr != null)
			setOrder(Order.getByValue(attr.getValue()));
		attr = e.getAttributeNode("promptphase");
		if (attr != null)
			setPromptphase(PromptPhase.getByValue(attr.getValue()));

		attr = e.getAttributeNode("speakerdisplay");
		if (attr != null) {
			setSpeakerdisplayStr(attr.getValue());
		}

//		ArrayList<Recording> recordingArrList = new ArrayList<Recording>();
//		NodeList recordingsEList = e
//				.getElementsByTagName(Recording.ELEMENT_NAME);
//		for (int i = 0; i < recordingsEList.getLength(); i++) {
//			Element recordingE = (Element) recordingsEList.item(i);
//			Recording recording = new Recording(recordingE);
//			recording.setPosition(i);
//			recordingArrList.add(recording);
//		}
		ArrayList<PromptItem> promptItemArrList=new ArrayList<PromptItem>();
		NodeList eNodeList=e.getChildNodes();
		int promptItemPosition=0;
		for(int i=0;i<eNodeList.getLength();i++){
		    Node n=eNodeList.item(i);
		    if(n.getNodeType()==Node.ELEMENT_NODE){
		        Element piE=(Element)n;
		        if(piE.getTagName().equals(Recording.ELEMENT_NAME)){
		            Recording recording = new Recording(piE);
		          recording.setPosition(promptItemPosition++);
		          recording.setSection(this);
		          promptItemArrList.add(recording);
		        }else if(piE.getTagName().equals(Nonrecording.ELEMENT_NAME)){
                    Nonrecording nonrecording = new Nonrecording(piE);
                    nonrecording.setPosition(promptItemPosition++);
                    nonrecording.setSection(this);
                    promptItemArrList.add(nonrecording);
                  }
		    }
		}
		setPromptItems(promptItemArrList);
	}

	/**
	 * sets all recording section data to the empty string or false
	 * 
	 */
	private void initializeSection() {
		//setName("");
		// setSpeakerdisplay(false);
		//setMode(null);
		//setOrder(Order.SEQUENTIAL);
		//setPromptphase(IDLE);
		// promptItems = null;
	}

	public Set<List<String>> requiredMIMETypeCombinations(){
	    HashSet<List<String>> reqMIMETypes=new HashSet<List<String>>();
	    List<PromptItem> pisList=getPromptItems();
	    for(PromptItem pi:pisList){
	       List<String> piMimes=pi.getMIMETypes();
	       reqMIMETypes.add(piMimes);
	    }
	    return reqMIMETypes;
	}
	
	// Property accessors
	@Id
	@Column(name = "section_id", unique = true, nullable = false)
	//@SequenceGenerator(name = "ID_SEQ", sequenceName = "id_seq")
	@GeneratedValue(generator = "id_gen")
	@ResourceKey("id")
	public int getSectionId() {
		return this.sectionId;
	}

	public void setSectionId(int sectionId) {
		int oldsectionId = this.sectionId;
		this.sectionId = sectionId;
		propertyChangeSupport.firePropertyChange("sectionId", oldsectionId,
				this.sectionId);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "script_id")
	@ResourceKey("script")
	@XmlTransient
	public Script getScript() {
		return this.script;
	}

	public void setScript(Script script) {
		// Script oldScript=this.script;
		this.script = script;
		// propertyChangeSupport.firePropertyChange("script", oldScript,
		// this.script);
	}

	// @ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	// @JoinColumn(name = "recording_id", unique = false, nullable = true,
	// insertable = true, updatable = true)
	// public Recordingscript getRecordingscript() {
	// return this.recordingscript;
	// }
	//
	// public void setRecordingscript(Recordingscript recordingscript) {
	// this.recordingscript = recordingscript;
	// }

	@Column(name = "name", length = 100)
	@ResourceKey("name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		PropertyChangeEvent pce=new PropertyChangeEvent(this, "name", oldName,this.name);
		//propertyChangeSupport.firePropertyChange("name", oldName, this.name);
		propertyChangeSupport.firePropertyChange(pce);
	}

	@Column(name = "speakerdisplay", length = 10)
	@ResourceKey("speakerdisplay")
	public String getSpeakerdisplayStr() {
		return this.speakerdisplayStr;
	}

	public void setSpeakerdisplayStr(String speakerdisplayStr) {
		// TODO fire prop change here ??
		this.speakerdisplayStr = speakerdisplayStr;
		if (speakerdisplayStr != null) {
			if (speakerdisplayStr.equalsIgnoreCase("yes")) {
				speakerDisplay = true;
			} else {
				speakerDisplay = new Boolean(speakerdisplayStr);
			}
		} else {
			this.speakerDisplay = false;
		}
		
	}
	
	@Transient
	@XmlTransient
	public boolean getNNSpeakerDisplayStr(){
		if(speakerDisplay==null)return defaultSpeakerDisplay;
		return speakerDisplay;
	}

	@Column(name = "section_position_in_script")
	@ResourceKey("position")
	public Integer getSectionPosition() {
		return this.sectionPosition;
	}

	public void setSectionPosition(Integer sectionPosition) {
		// TODO fire property change ??
		this.sectionPosition = sectionPosition;
	}

	@Column(name = "promptphase", length = 100)
	@Enumerated(EnumType.STRING)
	@ResourceKey("promptphase")
	public PromptPhase getPromptphase() {
		return this.promptphase;
	}
	
	@Transient
	@XmlTransient
	public PromptPhase getNNPromptphase(){
		if (this.promptphase==null){
			return DEF_PROMPT_PHASE;
		}else{
			return this.promptphase;
		}
	}

	public void setPromptphase(PromptPhase promptphase) {
		PromptPhase oldPromptphase = this.promptphase;
		this.promptphase = promptphase;
		if(propertyChangeSupportEnabled){
		propertyChangeSupport.firePropertyChange("promptphase", oldPromptphase,
				this.promptphase);
	}
	}

	@Column(name = "mode", length = 100)
	@Enumerated(EnumType.STRING)
	@ResourceKey("mode")
	public Mode getMode() {
		return this.mode;
	}

	public void setMode(Mode mode) {
		Mode oldMode = this.mode;
		this.mode = mode;
		if(propertyChangeSupportEnabled){
		propertyChangeSupport.firePropertyChange("mode", oldMode, this.mode);
	}
	}
	
	@Transient
	@XmlTransient
	public Mode getNNMode() {
		if(mode==null)return defaultMode;
		return this.mode;
	}

	// Renamed order column to ordering (order is SQL keyword, most JPA providers cannot handle this)
	@Column(name = "ordering", length = 100)
	@Enumerated(EnumType.STRING)
	@ResourceKey("order")
	public Order getOrder() {
		return this.order;
	}

	public void setOrder(Order order) {
		Order oldOrder = this.order;
		this.order = order;
		propertyChangeSupport.firePropertyChange("order", oldOrder, this.order);
	}
	
	@Transient
	@XmlTransient
	public Order getNNOrder() {
		if(order==null)return defaultOrder;
		return this.order;
	}

	@OneToMany(cascade={CascadeType.MERGE},fetch = FetchType.LAZY, mappedBy = "section")
	@OrderColumn(name="position")
	//@OrderBy("position")
	@ResourceKey("promptitems")
	public List<PromptItem> getPromptItems(){
		return promptItems;
	}
//	public Set<PromptItem> getPromptItemsSet() {
//		if (promptItemsSet != null)
//			return this.promptItemsSet;
//		if (promptItems != null) {
//			HashSet<PromptItem> tmpPromptItemsSet = new HashSet<PromptItem>();
//			for (int pos = 0; pos < promptItems.length; pos++) {
//				PromptItem s = promptItems[pos];
//				s.setPosition(pos);
//				// each promptItem is unique now
//				// ( Set does not alow duplicate entries)
//				tmpPromptItemsSet.add(s);
//			}
//			return tmpPromptItemsSet;
//		}
//		return null;
//	}
	public void setPromptItems(List<PromptItem> promptItems){
	    List<PromptItem> oldPromptItems = this.promptItems;
	    ////	if (oldPromptItems != null) {
	    ////		for (PromptItem opi : oldPromptItems) {
	    ////			opi.removePropertyChangeListener(this);
	    ////		}
	    ////	}
	    this.promptItems = promptItems;
	    if(getNNOrder().equals(Order.RANDOM)){
	        shuffleItems();
	    }
	    if(propertyChangeSupportEnabled){
	        if (this.promptItems != null) {
	            for (PromptItem pi : this.promptItems) {
	                pi.addPropertyChangeListener(new PropertyChangeListener(){
	                    public void propertyChange(PropertyChangeEvent evt) {
	                        String propName=evt.getPropertyName();
	                        String hPropName="promptItems."+propName;
	                        propertyChangeSupport.firePropertyChange(hPropName, evt.getOldValue(), evt.getNewValue());
	                    }
	                });
	            }
	        }
	        //

	        propertyChangeSupport.firePropertyChange("promptItems", oldPromptItems,
	                this.promptItems);
	    }	
	}
//
//	public void setPromptItemsSet(Set<PromptItem> recordingsSet) {
//		this.promptItemsSet = recordingsSet;
//		promptItems = null;
//		// // convert to array
//		// // sort by position column
//		// ArrayList<PromptItem> sortedRecordings = new ArrayList<PromptItem>(
//		// recordingsSet);
//		// Collections.sort(sortedRecordings, new Comparator<PromptItem>() {
//		// public int compare(PromptItem o1, PromptItem o2) {
//		// return o1.getPosition() - o2.getPosition();
//		// }
//		// });
//		// setPromptItems(sortedRecordings.toArray(new PromptItem[0]));
		// }

	
	@ResourceKey("training")
	@Basic
	public boolean isTraining() {
		return training;
	}

	public void setTraining(boolean training) {
		this.training = training;
	}

	public int compareTo(Section o) {
		return getSectionPosition().compareTo(o.getSectionPosition());
	}

	public Element toElement(Document d) {
		Element e = d.createElement(ELEMENT_NAME);
		for(String comm:comments){
			e.appendChild(d.createComment(comm));
		}
		String name = getName();
		if (name != null)
			e.setAttribute("name", name);
		Order order = getOrder();
		if (order != null)
			e.setAttribute("order", order.value());
		PromptPhase promptphase = getPromptphase();
		if (promptphase != null)
			e.setAttribute("promptphase", promptphase.value);
		Mode mode = getMode();
		if (mode != null)
			e.setAttribute("mode", mode.value());
		String speakerdisplay = getSpeakerdisplayStr();
		if (speakerdisplay != null)
			e.setAttribute("speakerdisplay", speakerdisplay);

//		if (promptItems != null) {
			List<PromptItem> pis=getPromptItems();
			for (PromptItem p : pis) {
				e.appendChild(p.toElement(d));
			}
//		} else if (promptItemsSet != null) {
//			// sort by position column
//			ArrayList<PromptItem> sortedRecordings = new ArrayList<PromptItem>(
//					promptItemsSet);
//			Collections.sort(sortedRecordings, new Comparator<PromptItem>() {
//				public int compare(PromptItem o1, PromptItem o2) {
//					return o1.getPosition() - o2.getPosition();
//				}
//			});
//
//			for (PromptItem r : sortedRecordings) {
//				// System.out.println("Recording pos: "+r.getPosition());
//				e.appendChild(r.toElement(d));
//			}
//		}

		return e;
	}

	@Transient
	public Boolean getSpeakerDisplay() {
		return speakerDisplay;
	}

	public void setSpeakerDisplay(Boolean speakerDisplay) {
		Boolean oldSpeakerDisplay = this.speakerDisplay;
		this.speakerDisplay = speakerDisplay;
		if (speakerDisplay == null) {
			speakerdisplayStr = null;
		} else {
			speakerdisplayStr = speakerDisplay.toString();
		}
		if(propertyChangeSupportEnabled){
		propertyChangeSupport.firePropertyChange("speakerDisplay",
				oldSpeakerDisplay, this.speakerDisplay);
	}
	}

	public String toString() {
//		StringBuffer sb = new StringBuffer();
//		sb.append(getName() + ", " + getOrder() + ", " + getMode() + ", "
//				+ getNNPromptphase() + ", " + getSpeakerDisplay() + "\n");
//
//		PromptItem[] promptItems = getPromptItems();
//		for (int i = 0; i < promptItems.length; i++) {
//			PromptItem pi = (PromptItem) promptItems[i];
//			sb.append(pi.toString() + "\n");
//		}
//		return sb.toString();
		return getName();
	}

	@Transient
	public String getInfo() {
		
		String attrs=getNNMode() + ", " + getNNOrder() + ", "
				+ getNNPromptphase() + ", " + getNNSpeakerDisplayStr();
		if(name==null){
			return attrs;
		}else{
			return name+", "+attrs;
		}
	}


//	@Transient
//	public PromptItem[] getPromptItems() {
//
//		if (promptItems != null)
//			return promptItems;
//		if (promptItemsSet != null) {
//			ArrayList<PromptItem> sortedpromptItems = new ArrayList<PromptItem>(
//					promptItemsSet);
//			Collections.sort(sortedpromptItems, new Comparator<PromptItem>() {
//				public int compare(PromptItem o1, PromptItem o2) {
//					return o1.getPosition() - o2.getPosition();
//				}
//			});
//
//			return sortedpromptItems.toArray(new PromptItem[0]);
//		}
//		return null;
//	}

//	public void setPromptItems(PromptItem[] promptItems) {
//		PromptItem[] oldPromptItems = this.promptItems;
////		if (oldPromptItems != null) {
////			for (PromptItem opi : oldPromptItems) {
////				opi.removePropertyChangeListener(this);
////			}
////		}
//		this.promptItems = promptItems;
//		if (this.promptItems != null) {
//			for (PromptItem opi : this.promptItems) {
//				opi.addPropertyChangeListener(new PropertyChangeListener(){
//					public void propertyChange(PropertyChangeEvent evt) {
//						String propName=evt.getPropertyName();
//						String hPropName="promptItems."+propName;
//						propertyChangeSupport.firePropertyChange(hPropName, evt.getOldValue(), evt.getNewValue());
//					}
//				});
//			}
//		}
//		//
		//
//		promptItemsSet = null;
//		propertyChangeSupport.firePropertyChange("promptItems", oldPromptItems,
//				this.promptItems);
//	}


	@Transient
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return(CLASS_DATA_FLAVOR.equals(flavor));
	}

	public void setDefaultMode(Mode defaultMode) {
		this.defaultMode=defaultMode;
	}

	@Transient
	public void setDefaultSpeakerDisplay(boolean defaultSpeakerDisplay) {
		this.defaultSpeakerDisplay=defaultSpeakerDisplay;
	}

	public Object clone() throws CloneNotSupportedException{
	       
        Object c=super.clone();
        Section cs=(Section)c;
        cs.propertyChangeSupport=new PropertyChangeSupport(this);
        List<PromptItem> pis=promptItems;
        ArrayList<PromptItem> cPis=new ArrayList<PromptItem>();
        for(PromptItem pi:pis){
            PromptItem cPi=(PromptItem)pi.clone();
            cPis.add(cPi);
        }
        cs.promptItems=cPis;
       return cs;
   }
	
	@Transient
	@XmlTransient
	public Mode getDefaultMode() {
		return defaultMode;
	}
	@Transient
	@XmlTransient
	public boolean isDefaultSpeakerDisplay() {
		return defaultSpeakerDisplay;
	}

	@Transient
	@XmlTransient
	public boolean recordingCodesUnique(){
	    HashSet<String> itemCodes=new HashSet<String>();

	    for(PromptItem pi:promptItems){
	        if(pi instanceof Recording){
	            Recording r=(Recording)pi;
	            String itemCode=r.getItemcode();
	            if(itemCodes.contains(itemCode)){
	                return false;
	            }else{
	                itemCodes.add(itemCode);
	            }
	        }
	    }


	    return true;
	}
	
	@Transient
	 public void updatePositions(){

	        int pisSize=promptItems.size();
	        for(int i=0;i<pisSize;i++){
	            PromptItem pi=promptItems.get(i);
	            pi.setPosition(i);
	            pi.setSection(this);
	        }
	 }
	 
	/**
     * Shuffles the promptItems.
     */
	@Transient
    public void shuffleItems() {
        List<PromptItem> shuffledItems = new ArrayList<PromptItem>(promptItems);
        
        Random rnd = new Random();
        for (int i = promptItems.size(); i > 1; i--) {
                swap(shuffledItems, i - 1, rnd.nextInt(i));
        }
       shuffledPromptItems=shuffledItems;
    }
    
    private void swap(List<PromptItem> items, int i, int j) {
        PromptItem tmp = items.get(i);
        items.set(i, items.get(j));
        items.set(j, tmp);
    }
    
    /**
     * Get shuffled prompt items.
     * @return previously shuffled prompt items if section has random order otherwise sequential ordered items
     */
    @Transient
    public List<PromptItem> getShuffledPromptItems() {
        if(getNNOrder().equals(Order.RANDOM) && shuffledPromptItems!=null){
            return shuffledPromptItems;
        }else{
            return promptItems;
        }
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
	
	public static void main(String[] args){
		Mode m=Mode.getByValue("autoprogress");
		System.out.println(m.value());
	}

    /**
     * Returns all itemcodes used without duplicates.
     * A section may have duplicate itemcodes. To retrieve
     * ietmcodes including duplictes use {@link #itemCodesList() itemCodesList}.
     * @return set of unique itemcodes
     */
	@Transient
    public Set<String> itemCodesSet() {
        Set<String> ics=new HashSet<String>();
        for(PromptItem pi:promptItems){
            if(pi instanceof Recording){
                Recording r=(Recording)pi;
                String ic=r.getItemcode();
                if(ic!=null){
                    ics.add(ic);
                }
            }
        }
        return ics;
    }
    /**
     * Returns all itemcodes used with duplicates.
     * @return list of itemcodes
     */
	@Transient
    public List<String> itemCodesList() {
        List<String> icl=new ArrayList<String>();
        for(PromptItem pi:promptItems){
            if(pi instanceof Recording){
                Recording r=(Recording)pi;
                String ic=r.getItemcode();
                if(ic!=null){
                    icl.add(ic);
                }
            }
        }
        return icl;
    }
    @Transient
    @XmlTransient
    public boolean needsSilenceDetector(){
        for(PromptItem pi:promptItems){
            if(pi instanceof Recording){
                Recording r=(Recording)pi;
                if(r.needsSilenceDetector()){
                    return true;
                }
            }
        }
        return false;
    }
    
    @Transient
    @XmlTransient
    public boolean needsBeep(){
        for(PromptItem pi:promptItems){
            if(pi instanceof Recording){
                Recording r=(Recording)pi;
                if(r.needsBeep()){
                    return true;
                }
            }
        }
        return false;
    }
}
