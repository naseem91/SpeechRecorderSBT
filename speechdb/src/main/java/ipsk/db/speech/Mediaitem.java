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
import ipsk.db.speech.utils.BooleanValue;
import ipsk.db.speech.utils.MIMETypeWorkaround;
import ipsk.db.speech.Script.Scope;
import ipsk.persistence.ObjectImmutableIfReferenced;
import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;
import ipsk.util.annotations.TextAreaView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents a mediaitem element of the recording script.
 */
@Entity
@Table(name = "mediaitem")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("mediaitem")
@PluralResourceKey("mediaitems")
@PreferredDisplayOrder("mediaitemId,mimetype,srcStr,text,*")
public class Mediaitem extends BasicPropertyChangeSupport implements java.io.Serializable,Cloneable {

	// Fields    

	public static final String ELEMENT_NAME = "mediaitem";
	
	public final static String ATTSRC = "src";
    public final static String ATTMIME = "mimetype";
    public final static String ATTCHARSET = "charset";
    public final static String ATTLANGCODE="languageISO639code";
    public final static String ATTCOUNTRYCODE="countryISO3166code";
    public final static String ATTMODAL = "modal";
    public final static String ATTALT = "alt";
    public final static String ATTAUTOPLAY = "autoplay";
    public final static String ATTWIDTH = "width";
    public final static String ATTHEIGHT = "height";
    public final static String ATTVOLUME = "volume";
    public final static String ATTANNOTATION_TEMPLATE = "annotationTemplate";
    public final static String ATTLANGUAGE = "annotationTemplate";
    
    public final static String DEF_MIMETYPE="text/plain";
    public final static String DEF_CHARSET="UTF-8";
    private final static boolean DEF_MODAL=false;
    private final static int DEF_VOLUME=100;
    //private final static double DEF_VOLUME=1.0;
    private final static String DEF_ALT=null;
    private final static boolean DEF_AUTOPLAY=false;
    private final static int DEF_WIDTH=0;
    private final static int DEF_HEIGHT=0;
    
    private static final double LN = (float) (20 / Math.log(10));
    
   
	private int mediaitemId;

	private String mimetype=null;
	private String charSet=null;

	private String languageISO639code;
	private String countryISO3166code;
	private String srcStr=null;

	private URI src=null;

	private String alt=null;

	private Boolean autoplay=null;

	private Boolean modal=null;

	private Integer width=null;

	private Integer height=null;

	private Integer volume=null;

	private String text=null;

	private String author=null;
	
	private Scope scope=Scope.APPLICATION;
	
	private String description;
	
    private boolean annotationTemplate=false;

	private Set<PromptItem> recpromptsSet = new HashSet<PromptItem>(0);

	private String[] comments=new String[0];
	

	//private String promptText;

	// Constructors

	/** default constructor */
	public Mediaitem() {
		super();
		init();
	}

	/** minimal constructor */
	public Mediaitem(int mediaitemId) {
		this();
		this.mediaitemId = mediaitemId;
	}

	

	 private void init(){
	        setMimetype(null);
	        setSrc(null);
	        setVolume(null);
	        setVolume(null);
	        setAlt(null);
	        setWidth(null);
	        setHeight(null);
	        setText(null);
	    }
	    
	
	    public Mediaitem(Element e){
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
			//	      REQUIRED attributes

	        // IMPLIED attributes; if the attribute is not specified, default values are used
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
            Attr attLangCode=e.getAttributeNode(ATTLANGCODE);
            if(attLangCode!=null){
                languageISO639code=attLangCode.getValue();
            }
            Attr attCountryCode=e.getAttributeNode(ATTCOUNTRYCODE);
            if(attCountryCode!=null){
                countryISO3166code=attCountryCode.getValue();
            }
            MIMETypeWorkaround mtw=new MIMETypeWorkaround(mimetypeAttrVal,charsetAttrVal);
            setMimetype(mtw.getConvertedMimeType());
            setCharSet(mtw.getConvertedCharset());
	        Attr attSrc = e.getAttributeNode(ATTSRC);
	        if (attSrc != null) {
	           URI srcURI=URI.create(attSrc.getValue());
	           setSrc(srcURI);
	        } 
	        Attr attModal=e.getAttributeNode(ATTMODAL);
	        if (attModal != null) {
	            setModal(BooleanValue.parseExtendedBoolean(attModal));
	        } 
	        Attr attVolumeNode=e.getAttributeNode(ATTVOLUME);
	        if (attVolumeNode != null) {  
	        	setVolume(Integer.parseInt(attVolumeNode.getValue()));
	        } 
	        Attr attAlt=e.getAttributeNode(ATTALT);
	        if (attAlt != null) {
	            setAlt(attAlt.getValue());
	        }
	        Attr attAutoPlay=e.getAttributeNode(ATTAUTOPLAY);
	        if (attAutoPlay != null) {
	           setAutoplay(BooleanValue.parseExtendedBoolean(attAutoPlay));   
	        } 
//            else {
//	            setAutoplay(false);
//	        }
	        Attr attWidthNode=e.getAttributeNode(ATTWIDTH);
	        if (attWidthNode != null) {
	            
	            setWidth(Integer.parseInt(attWidthNode.getValue()));
	        } 
	        Attr attHeightNode=e.getAttributeNode(ATTHEIGHT);
	        if (attHeightNode != null) {
	            
	            setHeight(Integer.parseInt(attHeightNode.getValue()));
	        } 
	        Attr attrAnnotationTemplate = e.getAttributeNode(ATTANNOTATION_TEMPLATE);
	        if (attrAnnotationTemplate != null) {
	            setAnnotationTemplate(BooleanValue.parseExtendedBoolean(attrAnnotationTemplate));
	        } 
            
	        // get node content text or the empty string if none available
	        setText(e.getTextContent());
//	        if (mediaNode.getFirstChild() != null) {        
//	            recAttText = mediaNode.getFirstChild().getNodeValue().trim();
//	        } else {
//	            recAttText = "";
//	        }
	    }
	    
	// Property accessors
	@Id
	@Column(name = "mediaitem_id", unique = true, nullable = false)
	//@SequenceGenerator(name="ID_SEQ",sequenceName="id_seq")
    @GeneratedValue(generator="id_gen")
    @ResourceKey("id")
    public int getMediaitemId() {
		return this.mediaitemId;
	}

	public void setMediaitemId(int mediaitemId) {
		int oldMediaItemId=this.mediaitemId;
		this.mediaitemId = mediaitemId;
		propertyChangeSupport.firePropertyChange("mediaitemId", oldMediaItemId, this.mediaitemId);
	}

	@Column(name = "mimetype", length = 100)
	@ResourceKey("mimetype")
	public String getMimetype() {
		return this.mimetype;
	}

	@Transient
	@XmlTransient
	public String getNNMimetype(){
		if(mimetype==null)return DEF_MIMETYPE;
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		String oldMimeType=this.mimetype;
		this.mimetype = mimetype;
		propertyChangeSupport.firePropertyChange("mimetype", oldMimeType, this.mimetype);
	}
	@Transient
	public void setNNMimetype(String mimetype) {
        if(DEF_MIMETYPE.equals(mimetype)){
            setMimetype(null);
        }else{
            setMimetype(mimetype);
        }
    }
	@Column(name = "charset", length = 100)
	@ResourceKey("charset")
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

	@Column(name = "src")
	@ResourceKey("url")
	public String getSrcStr() {
		return this.srcStr;
	}

	public void setSrcStr(String src) {
		this.srcStr = src;
		if (srcStr == null) {
			this.src = null;
		} else {
			
		    this.src = URI.create(srcStr);
			
		}
	}
	
	@Transient
	public URI getSrc() {
		return src;
	}

	public void setSrc(URI src) {
		URI oldSrc=this.src;
		this.src = src;
		if(src==null){
			srcStr=null;
		}else{
		srcStr=src.toString();
		}
		propertyChangeSupport.firePropertyChange("src", oldSrc,this.src );
	}

	@Column(name = "alt", unique = false, nullable = true, insertable = true, updatable = true)
	@ResourceKey("text.alternative")
	public String getAlt() {
		return this.alt;
	}

	public void setAlt(String alt) {
		String oldAlt=this.alt;
		this.alt = alt;
		propertyChangeSupport.firePropertyChange("alt", oldAlt, this.alt);
	}

	@Column(name = "autoplay")
	@ResourceKey("autoplay")
	public Boolean getAutoplay() {
		return this.autoplay;
	}

	public void setAutoplay(Boolean autoplay) {
		Boolean oldAutoplay=this.autoplay;
		this.autoplay = autoplay;
		propertyChangeSupport.firePropertyChange("autoplay", oldAutoplay, this.autoplay);
	}
    
    @Transient
    @XmlTransient
    public boolean getNNAutoplay() {
        if(this.autoplay==null)return DEF_AUTOPLAY;
        return this.autoplay;
    }
    
    @Transient
    @XmlTransient
    public void setNNAutoplay(boolean autoPlay) {
        if(autoPlay==DEF_AUTOPLAY){
            setAutoplay(null);
        }else{
            setAutoplay(autoplay);
        }
    }

	@Column(name = "modal")
	@ResourceKey("modal")
	public Boolean getModal() {
		return this.modal;
	}

	public void setModal(Boolean modal) {
		Boolean oldModal=this.modal;
		this.modal = modal;
		propertyChangeSupport.firePropertyChange("modal", oldModal, this.modal);
	}
	@Transient
	@XmlTransient
	public boolean getNNModal() {
	    if(this.modal==null)return DEF_MODAL;
        return this.modal; 
	}
	@Transient
	@XmlTransient
    public void setNNModal(boolean modal) {
        if(modal==DEF_MODAL){
            setModal(null);
        }else{
            setModal(modal);
        }
    }
	
	@Column(name = "width")
	@ResourceKey("width")
	public Integer getWidth() {
		return this.width;
	}

	public void setWidth(Integer width) {
		Integer oldWidth=this.width;
		this.width = width;
		propertyChangeSupport.firePropertyChange("width", oldWidth, this.width);
	}

	@Transient
	@XmlTransient
    public int getNNWidth() {
        if(width==null)return DEF_WIDTH;
        return width;
    }
	@Transient
	@XmlTransient
    public void setNNWidth(int width) {
        if(width==DEF_WIDTH){
            setWidth(null);
        }else{
            setWidth(width);
        }
    }
    
	@Column(name = "height")
	@ResourceKey("height")
	public Integer getHeight() {
		return this.height;
	}

	public void setHeight(Integer height) {
		Integer oldHeight=this.height;
		this.height = height;
		propertyChangeSupport.firePropertyChange("height", oldHeight, this.height);
	}
	
	@Transient
	@XmlTransient
    public int getNNHeight() {
        if(height==null)return DEF_HEIGHT;
        return height;
    }
    @Transient
    public void setNNHeight(int height) {
        if(height==DEF_HEIGHT){
            setHeight(null);
        }else{
            setHeight(height);
        }
    }

	/**
	 * The volume to play this media-item in percentage.
	 * Volume 100: original amplitude
	 * @return volume in percentage of original
	 */
	@Column(name = "volume")
	@ResourceKey("volume")
	public Integer getVolume() {
		return this.volume;
	}

	public void setVolume(Integer volume) {
		Integer oldVolume=this.volume;
		this.volume = volume;
		propertyChangeSupport.firePropertyChange("volume", oldVolume, this.volume);
	}
	
	@Transient
	public int getNNVolume(){
	    if(volume==null)return DEF_VOLUME;
	    return volume;
	}
	
	@Transient
	@XmlTransient
    public void setNNVolume(int volume){
        if(volume==DEF_VOLUME){
            setVolume(null);
        }else{
            setVolume(volume);
        }
    }
	
	 /**
	 * Get normalized (0.0 ... 1.0) volume scaling value.
	 * Calculated from the logarithmic percent value.
	 * 0% volume is mapped to -60dB and 100% are mapped to 0dB
	 * @return normalized volume (0.0 ... 1.0)
	 */
	 @Transient
	 @XmlTransient
	public float getNormalizedVolume(){
	    if (volume==null)return (float)1.0;
	    // Mute for 0%
	    if(volume==0.0) return (float) 0.0;
//	    float normVol=(float)(Math.log(volume+1)/Math.log(101));
	    // map percent to dB scale
	    double dbLogVolume=((volume*60)/100)-60;
	    float normVol=(float)(Math.pow(10,dbLogVolume/20));
	    //System.out.println("Vol "+volume+"%, "+normVol);
	    return normVol;
	}

	@Column(name = "mediaitem")
	@TextAreaView
	@ResourceKey("text")
	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		String oldText=this.text;
		this.text = text;
		PropertyChangeEvent pce=new PropertyChangeEvent(this, "text", oldText, this.text);
		propertyChangeSupport.firePropertyChange(pce);
		
	}

	
	@Transient
	public String getPromptText(){
		if (text==null)return null;
//		 TODO for compatibility reasons 
		// should go to the (prompt) viewer(s) ??
		return text.replaceAll("\\s{2,}"," ");
	}
	

	@Column(name = "author", length = 100)
	@ResourceKey("author")
	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		String oldAuthor=this.author;
		this.author = author;
		propertyChangeSupport.firePropertyChange("author", oldAuthor, this.author);
	}
	
	@Column(length = 20)
	@Enumerated(EnumType.STRING)
	@ResourceKey("scope")
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	// TODO not yet implemented with JPA
//	@Column(name = "description", length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldDescr=this.description;
        this.description = description;
        propertyChangeSupport.firePropertyChange("description", oldDescr, this.description);
    }

    public boolean getAnnotationTemplate() {
        return annotationTemplate;
    }

    public void setAnnotationTemplate(boolean annotationTemplate) {
        this.annotationTemplate = annotationTemplate;
    }
    
 

	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "mediaitems")
	@ResourceKey("promptitems")
	@XmlTransient
	@ObjectImmutableIfReferenced
	public Set<PromptItem> getPromptItemsSet() {
		return this.recpromptsSet;
	}

	public void setPromptItemsSet(Set<PromptItem> recprompts) {
		this.recpromptsSet = recprompts;
	}
	
	@PrePersist
	public void prePersist(){
		consistencyCheck();
	}
	@PreUpdate
	public void preUpdate(){
		consistencyCheck();
	}
	
	private void consistencyCheck(){
		// the new/editing form for Mediaitems cannot send a null value
		// so we interpret an empty string as not set
		if("".equals(srcStr) && text!=null){
			setSrcStr(null);
		}
	}
	
	public String toString(){
		String mType=getNNMimetype();
		String mimeClass=mType.substring(0, mType.indexOf("/"));
		if(srcStr!=null){
			return mimeClass+": ["+srcStr+"]";
		}else if(text!=null){
			return mimeClass+": "+text;
		}
		return mimeClass;
	}
	
	public Element toElement(Document d){
		Element e=d.createElement(ELEMENT_NAME);
		String mimeType =getMimetype();
		if(mimeType!=null){
            
            e.setAttribute(ATTMIME,mimeType);
        }
        String cs =getCharSet();
        if(cs!=null){
            e.setAttribute(ATTCHARSET,cs);
        }
        if(autoplay!=null) e.setAttribute(ATTAUTOPLAY,autoplay.toString());
        if(modal!=null) e.setAttribute(ATTMODAL,modal.toString());
        if(volume!=null)e.setAttribute(ATTVOLUME, volume.toString());
        if(annotationTemplate){
            e.setAttribute(ATTANNOTATION_TEMPLATE, Boolean.toString(annotationTemplate));
        }
        if(languageISO639code!=null){
            e.setAttribute(ATTLANGCODE, languageISO639code);
        }
        if(countryISO3166code!=null){
            e.setAttribute(ATTCOUNTRYCODE,countryISO3166code);
        }
		String src=getSrcStr();
		if(src!=null){
			e.setAttribute("src", src);
		}
//		else{
			if(text !=null){
                // Put formatted text in CDATA section
                if("text/html".equals(mimeType) || "text/rtf".equals(mimeType)){
                    e.appendChild(d.createCDATASection(text));
                }else{
			e.appendChild(d.createTextNode(text));
                }
			}
//		}
		
		return e;
	}
	
	// TODO
//	public boolean equals(Object o){
//	    boolean se=super.equals(o);
//	    if(se)return se;
//	    if(o instanceof Mediaitem){
//	        Mediaitem oMi=(Mediaitem)o;
//	        if(beanInfo==null){
//	            try {
//                    beanInfo=Introspector.getBeanInfo(getClass());
//                } catch (IntrospectionException e) {
//                   //
//                } 
//	        }
//	        PropertyDescriptor[] pds=beanInfo.getPropertyDescriptors();
//	        for(PropertyDescriptor pd:pds){
//	            pd.getPr
//	        }
//	    }
//	    
//	    return false;
//	}
	
	public Object clone() throws CloneNotSupportedException{
	    
	    Object c=super.clone();
	    Mediaitem mi=(Mediaitem)c;
        mi.propertyChangeSupport=new PropertyChangeSupport(this);
        return mi;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		String propName=evt.getPropertyName();
		String hPropName=ELEMENT_NAME+"."+propName;
		propertyChangeSupport.firePropertyChange(hPropName, evt.getOldValue(), evt.getNewValue());
	}

    public String getLanguageISO639code() {
        return languageISO639code;
    }

    public void setLanguageISO639code(String languageISO639code) {
        this.languageISO639code = languageISO639code;
    }

    public String getCountryISO3166code() {
        return countryISO3166code;
    }

    public void setCountryISO3166code(String countryISO3166code) {
        this.countryISO3166code = countryISO3166code;
    }

 
	

}
