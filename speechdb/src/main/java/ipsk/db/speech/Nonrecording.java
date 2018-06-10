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


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Represents a non recording element of the recording script.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class Nonrecording extends PromptItem implements Serializable,PropertyChangeListener{
    public final static String ELEMENT_NAME="nonrecording";
    private static final String ATT_DURATION = "duration";
    
	private String[] comments;
	
	private Integer duration;

    public Nonrecording() {
        super();
//       init();
    }

//    private void init(){
//        setMediaitem(new Mediaitem());
//    }
    
    public Nonrecording(Element e){
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
		NodeList prE=e.getElementsByTagName(Presenter.ELEMENT_NAME);
        int prELen=prE.getLength();
        if(prELen>=1){
            Presenter presenter=new Presenter((Element)prE.item(0));
            setPresenter(presenter);
        }
		ArrayList<Mediaitem> mis=new ArrayList<Mediaitem>();
        NodeList miE=e.getElementsByTagName(Mediaitem.ELEMENT_NAME);
        int miELen=miE.getLength();
        for(int i=0;i<miELen;i++){
            Mediaitem mi=new Mediaitem((Element)miE.item(i));
            mis.add(mi);
        }
        setMediaitems(mis);
        
        Attr attDuration=e.getAttributeNode(ATT_DURATION);
        if(attDuration!=null){
            duration=Integer.parseInt(attDuration.getValue());
        }
        
    }

    
//	/**
//	 * getDescription() returns descriptive information about an item. For this,
//	 * the item attributes are checked in the following order:
//	 * <ol>
//	 * <li>ALT-tag text</li>
//	 * <li>text contents of item</li>
//	 * <li>file name of item URL</li>
//	 * </ol>
//	 *  
//	 * @param pi prompt item
//	 * @return String descriptive text
//	 */
//    @Transient
//	public String getDescription() {
//		Mediaitem mi=getMediaitem();
//		if(mi.getAlt() !=null) {
//		return mi.getAlt();
//		} else if (mi.getPromptText() !=null) {
//			return mi.getPromptText();
//		} else if (mi.getSrc() != null) {
//			return mi.getSrc().getFile();
//		} else {
//			return "";
//		}
//	}

    public Element toElement(Document d) {
        Element e =d.createElement(ELEMENT_NAME);
        if(duration!=null){
            e.setAttribute(ATT_DURATION, duration.toString());
        }
        List<Mediaitem> mis=getMediaitems();
        for(Mediaitem mi:mis){
            e.appendChild(mi.toElement(d));
        }
        return e;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
		String propName=evt.getPropertyName();
		String hPropName=ELEMENT_NAME+"."+propName;
		propertyChangeSupport.firePropertyChange(hPropName, evt.getOldValue(), evt.getNewValue());
	}

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        Integer oldDuration=this.duration;
        this.duration = duration;
        propertyChangeSupport.firePropertyChange("duration", oldDuration, this.duration);
    }

}
