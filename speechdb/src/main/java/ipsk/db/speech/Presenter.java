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

import java.util.ArrayList;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Defines a prompt presenter for recording and nonrecording elements.
 * Currently only Java class presenters supported. 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class Presenter {
    public final static String TYPE_JAVA_CLASS="javaclass";
    
    protected static final String ELEMENT_NAME = "presenter";
    public final static String ATTTYPE = "type";
    public final static String ATTCLASSNAME = "classname";
    
    private String[] comments=new String[0];
    
    
    private String type=TYPE_JAVA_CLASS;
    private String classname;
    
    public Presenter() {
        super();
    }
    public Presenter(Element e){
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
        Attr attTypeNode=e.getAttributeNode(ATTTYPE);
        if (attTypeNode != null) {
            setType(attTypeNode.getValue());
        } 
        Attr attClassnameNode=e.getAttributeNode(ATTCLASSNAME);
        if (attClassnameNode != null) {
            setClassname(attClassnameNode.getValue());
        } 
    }
    
    public Element toElement(Document d) {
        Element e = d.createElement(ELEMENT_NAME);
        
        for(String comm:comments){
            e.appendChild(d.createComment(comm));
        }
        String type=getType();
        if (type!= null) {
            e.setAttribute(ATTTYPE, type);
        }
        String classname=getClassname();
        if (classname!= null) {
            e.setAttribute(ATTCLASSNAME, classname);
        }
        
        return e;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getClassname() {
        return classname;
    }
    public void setClassname(String classname) {
        this.classname = classname;
    }
}
