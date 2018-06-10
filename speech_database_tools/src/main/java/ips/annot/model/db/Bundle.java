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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlType(name = "bundle", propOrder = {
        "name",
        "annotates",
        "sampleRate",
        "levels",
        "links"})

public class Bundle {

    private transient Session session;
    @Id
    @SequenceGenerator(name="keys", sequenceName="KEYS", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="keys")
    private int id;
    private String name;
    private String annotates=null;
  

    private Float sampleRate;
    private Long frameLength;
    @XmlTransient
    public Long getFrameLength() {
		return frameLength;
	}
	public void setFrameLength(Long frameLength) {
		this.frameLength = frameLength;
	}
	private List<String> signalpaths = new Vector<String>();
    @OneToMany(mappedBy="tier")
    private List<Level> levels = new ArrayList<Level>();
    private Set<Link> linksSet = new HashSet<Link>();
    
    // --- auxiliary fields ----------
//    private transient Set<String> levelNames = new HashSet<String>();
    
    private Locale locale;

    @XmlTransient
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    @XmlTransient
    public Session getSession() {
        return session;
    }
    public void setSession(Session session) {
        this.session = session;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAnnotates() {
        if(annotates!=null){
            return annotates;
        }else{
            // try to get it from signalpath
            if(signalpaths!=null && signalpaths.size()>0){
                // Use first signal path as master 
                String masterSignalPath=signalpaths.get(0);
                // and set the annotates property 
                File masterFile=new File(masterSignalPath);
                return masterFile.getName();
            }
            return null;
        }

    }
    public void setAnnotates(String annotates) {
        this.annotates = annotates;
    }
    
    public Float getSampleRate() {
        return sampleRate;
    }
    public void setSampleRate(Float sampleRate) {
        this.sampleRate = sampleRate;
    }
 
//    @XmlElementWrapper(name = "signalpaths")
//    @XmlElements(@XmlElement(name = "signalpath"))
    @XmlTransient
    public List<String> getSignalpaths() {
        return signalpaths;
    }
    
    public void setSignalpaths(List<String> signalurls) {
        this.signalpaths = signalurls;
       
    }
    
//    @XmlElementWrapper(name = "levels")
    @XmlElements(@XmlElement(name = "levels"))
    public List<Level> getLevels() {
        return levels;
    }
    
    public void setLevels(List<Level> levels) {
		this.levels=levels;
	}
    
    @XmlElement(name="links")
//    @XmlElements(@XmlElement(name = "links"))
    public List<Link> getLinks() {
        List<Link> ll=new ArrayList<Link>();
        for(Link l:linksSet){
            ll.add(l);
        }
        return ll;
    }
    public void setLinks(List<Link> linkList) {
        linksSet.clear();
        for(Link l:linkList){
           linksSet.add(l); 
        }
    }
    
    //  @XmlElementWrapper(name = "linksSet")
    //  @XmlElements(@XmlElement(name = "linksSet"))
    @XmlTransient
    public Set<Link> getLinksAsSet() {
        return linksSet;
    }
    
    // Language metadata is not part of EMU DB model 
    @Transient
    @XmlTransient
    public Locale getLocale() {
        return locale;
    }
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public boolean isValidConstraint(Item fromItem, Item toItem) {
        Level fromTier = fromItem.getLevel();
        Level toTier = toItem.getLevel();
        if (session != null) {
            for (LinkDefinition c : session.getDatabase().getSchema().getConstraints()) {
                if (c.isValidTierLinkDefinition(fromTier.getDefinition(), toTier.getDefinition())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Set<Link> linksOfItem(Item it){
        Set<Link> linksOfItem=new HashSet<Link>();
        for(Link l:getLinksAsSet()){
            if(l.getFrom().equals(it) || l.getTo().equals(it)){
                linksOfItem.add(l);
            }
        }
        return linksOfItem;
    }
    
    public Long startByLinkedSegmentItems(Item item,boolean forward){

    	Long minStart=null;
    	if(LevelDefinition.SEGMENT.equals(item.getType()) || item.getSampleStart()!=null){
    		return item.getSampleStart();
    	}else{

    		for(Link l:getLinksAsSet()){
    			Integer itId=item.getBundleId();
    			int lfId=l.getFromID();
    			int ltId=l.getToID();
    			//    			System.out.println("Link: "+lfId+" Item: "+itId);
    			Item linkIt=null;
    			if(forward && lfId==itId){
    				linkIt=l.getTo();
    			}else if(!forward && ltId==itId){
    				linkIt=l.getFrom();
    			}
    			if(linkIt!=null){
    				Long minLinkStart=startByLinkedSegmentItems(linkIt,forward);
    				if(minLinkStart!=null){
    					if(minStart==null){
    						minStart=minLinkStart;
    					}else{
    						if(minLinkStart<minStart){
    							minStart=minLinkStart;
    						}
    					}
    				}
    			}
    		}
    	}
    	return minStart;
    }

    public Long endByLinkedSegmentItems(Item item,boolean forward){

    	Long maxEnd=null;
    	if(LevelDefinition.SEGMENT.equals(item.getType()) || item.getSampleStart()!=null){
    		return item.getSampleStart()+item.getSampleDur();
    	}else{

    		for(Link l:getLinksAsSet()){
    			Integer itId=item.getBundleId();
    			int lfId=l.getFromID();
    			int ltId=l.getToID();
    			//    			System.out.println("Link: "+lfId+" Item: "+itId);
    			Item linkIt=null;
    			if(forward && lfId==itId){
    				linkIt=l.getTo();
    			}else if(!forward && ltId==itId){
    				linkIt=l.getFrom();
    			}
    			if(linkIt!=null){
    				Long maxLinkEnd=endByLinkedSegmentItems(linkIt,forward);
    				if(maxLinkEnd!=null){
    					if(maxEnd==null){
    						maxEnd=maxLinkEnd;
    					}else{
    						if(maxLinkEnd>maxEnd){
    							maxEnd=maxLinkEnd;
    						}
    					}
    				}
    			}
    		}
    	}
    	return maxEnd;
    }

    public Level getTierByName(String name) {
        for (Level tier : getLevels()) {
            if (tier.getName().equalsIgnoreCase(name)) {
                return tier;
            }
        }
        return null;
    }
     
//    public void addLevel(Level level) {
//        if (levelNames.contains(level.getName())) {
//            // what to do if tier name is used already?
//        } else {
//            levelNames.add(level.getName());
//            levels.add(level);
//        }
//    }
    
    public Set<Integer> itemIdsInUse(){
        HashSet<Integer> idsInUse=new HashSet<Integer>();
        List<Level> lvls=getLevels();
        for(Level lvl :lvls){
            List<Item> its=lvl.getItems();
            for(Item it:its){
                Integer itemId=it.getBundleId();
                if(itemId!=null){
                    // item ID is set
                    idsInUse.add(itemId);
                }
            }
        }
        return idsInUse;
    }
    
    public Item itemByBundleScopeId(int bundleScopeId){
    	Item item=null;
    	for(Level lvl:getLevels()){
    		for(Item it:lvl.getItems()){
    			Integer bsId=it.getBundleId();
    			if(bsId!=null && bsId==bundleScopeId){
    				return it;
    			}
    		}
    	}
    	return item;
    }
    
    public void resolveLinkReferences(){
    	for(Link l:linksSet){
    	    int fromId=l.getFromID();
    	    Item fromIt=itemByBundleScopeId(fromId);
    	    int toId=l.getToID();
            Item toIt=itemByBundleScopeId(toId);
            
            toIt.getFromItems().add(fromIt);
            fromIt.getToItems().add(toIt);
    		if(l.getFrom()==null){
    		
    			l.setFrom(fromIt);
    		}
    		if(l.getTo()==null){
    			
    			l.setTo(toIt);
    		}
    	}
    }
    
    public void applyLevelsToItems(){
        for(Level l:getLevels()){
            for(Item it:l.getItems()){
                it.setLevel(l);
            }
        }
    }
    
    public void applyBundleToLevels(){
        for(Level l:getLevels()){
            l.setBundle(this);
        }
    }
    
    public void applyItemPositions(){
        for(Level l:getLevels()){
            List<Item> items=l.getItems();
            for(int i=0;i<items.size();i++){
                Item it=items.get(i);
                it.setPosition(i);
            }
        }
    }
    
    public Integer highestID(){
        Set<Integer> idsInUse=itemIdsInUse();
        Integer hId=null;
        for(Integer id:idsInUse){
            if(id!=null && (hId==null || id>hId)){
                hId=id;
            }
        }
        return hId;
    }
    
    
    public void removeLinksForItem(Item item){
        Set<Link> linksToRemove=new HashSet<Link>();
        Set<Link> linksSet=getLinksAsSet();
        for(Link l:linksSet){
            Item lf=l.getFrom();
            Item lt=l.getTo();
            if(item.equals(lf) || item.equals(lt)){
                linksToRemove.add(l);    
            }
        }
        for(Link l:linksToRemove){
            linksSet.remove(l);
        }
    }
    
    public void removeLevelAndAssociatedLinks(Level level){
         List<Item> its=level.getItems();
         for(Item it:its){
             removeLinksForItem(it);
         }
         levels.remove(level);
           
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer("Bundle: "+name+"\n");
//        for (String path : getSignalpaths()) {
//            buffer.append(path + "\n");
//        }
//        
        buffer.append("Levels:\n");
       
        for (Level tier : levels) {
            buffer.append(tier.toString());
        }
        buffer.append("Links ("+getLinks().size()+"):\n");
        for (Link link : getLinks()) {
            buffer.append(link.toString());
            buffer.append("\n");
        }
        
        return buffer.toString();
    }
    
    public void toXML() {
        String xmlFilename = "/Users/draxler/Bundle.xml";
//        if (getSignalurls().size() > 0) {
//            xmlFilename = getSignalurls().get(0).toExternalForm() + ".xml";
//        } 
        File xml = new File(xmlFilename);
        JAXB.marshal(this, xml);
    }
    
    public Bundle fromXML() {
        String xmlFilename = "/Users/draxler/Bundle.xml";
        File xml = new File(xmlFilename);
        Bundle b = JAXB.unmarshal(xml, Bundle.class);
        for (Level tier : b.getLevels()) {
            tier.setBundle(b);
            for (Item item : tier.getItems()) {
                item.setLevel(tier);
            }
        }
        return b;
    }
    public Long startByLinkedSegmentItems(Item item) {
       Long startFwd=startByLinkedSegmentItems(item, true);
       if(startFwd!=null){
           return startFwd;
       }else{
           return startByLinkedSegmentItems(item, false);
       }
    }
    public Long endByLinkedSegmentItems(Item item) {
        Long endFwd=endByLinkedSegmentItems(item, true);
        if(endFwd!=null){
            return endFwd;
        }else{
            return endByLinkedSegmentItems(item, false);
        }
     }
    
    public Long startByContext(Item item) {
        Long startByLks=startByLinkedSegmentItems(item);
        if(startByLks!=null){
            return startByLks;
        }else{
            Level lvl=item.getLevel();
            if(lvl!=null && LevelDefinition.ITEM.equals(lvl.getType())){
            	List<Item> its=lvl.getItems();
            	int itCnt=its.size();
            	for(int i=0;i<itCnt;i++){
            		Item lIt=its.get(i);
            		if(item==lIt){
            			Long fl=getFrameLength();
            			if(fl!=null){
            				long pos=fl*i/itCnt;
            				return pos;
            			}
            		}
            	}
            }
        }
        return null;
     }
     public Long endByContext(Item item) {
         Long endByLks=endByLinkedSegmentItems(item);
         if(endByLks!=null){
             return endByLks;
         }else{
        	 Level lvl=item.getLevel();
             if(lvl!=null && LevelDefinition.ITEM.equals(lvl.getType())){
             	List<Item> its=lvl.getItems();
             	int itCnt=its.size();
             	for(int i=0;i<itCnt;i++){
             		Item lIt=its.get(i);
             		if(item==lIt){
             			Long fl=getFrameLength();
             			if(fl!=null){
             				long pos=fl*(itCnt-i)/itCnt;
             				return pos;
             			}
             		}
             	}
             }
         }
         return null;
      }
    
   
}
