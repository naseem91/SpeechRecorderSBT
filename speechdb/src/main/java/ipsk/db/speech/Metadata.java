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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Represents the metadata element of the recording script.
 */
@Entity
@Table(name = "metadata", schema = "public")
public class Metadata extends BasicPropertyChangeSupport implements
        java.io.Serializable {

    // Fields

    private static final String ELEMENT_NAME = "metadata";

    private int metadataId;

    private Script script;

    private List<Property> properties=new ArrayList<Property>();

  

    private ArrayList<ArrayList<String>> comments = new ArrayList<ArrayList<String>>();

    // Constructors

    /** default constructor */
    public Metadata() {
        super();
    }

    /** minimal constructor */
    public Metadata(int metadataId) {
        this();
        this.metadataId = metadataId;
    }

 
    public Metadata(Element e) {
        this();
        NodeList nl = e.getChildNodes();
        properties.clear();
        String tmpKey = null;
        String tmpValue = null;
        int ePos = 0;
        for (int ci = 0; ci < nl.getLength(); ci++) {
            Node n = nl.item(ci);
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
                if (el.getNodeName().equals("key")) {
                    tmpKey = el.getTextContent();
                } else if (el.getNodeName().equals("value")) {
                    tmpValue = el.getTextContent();
                    Property p = new Property();
                    p.setKey(tmpKey);
                    p.setValue(tmpValue);
                    properties.add(p);
                }
                ePos++;
            }
        }

    }

    // Property accessors
    @Id
    @Column(name = "metadata_id", unique = true)
    //@SequenceGenerator(name = "ID_SEQ", sequenceName = "id_seq")
    @GeneratedValue(generator = "id_gen")
    @ResourceKey("id")
    public int getMetadataId() {
        return this.metadataId;
    }

    public void setMetadataId(int metadataId) {
        int oldMetadataId = this.metadataId;
        this.metadataId = metadataId;
        propertyChangeSupport.firePropertyChange("metadataId", oldMetadataId,
                this.metadataId);
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_id")
    public Script getScript() {
        return this.script;
    }

    public void setScript(Script script) {
        Script oldScript = this.script;
        this.script = script;
        propertyChangeSupport.firePropertyChange("script", oldScript,
                this.script);
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "metadata")
    public List<Property> getProperties() {
        return this.properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }


    private void appendComments(Document d, Element e, int ePos) {
        if (ePos < comments.size()) {
            ArrayList<String> posCmts = comments.get(ePos);
            if (posCmts != null) {
                for (String c : posCmts) {
                    e.appendChild(d.createComment(c));
                }
            }
        }
    }

    public Element toElement(Document d) {
    	Element e = d.createElement(ELEMENT_NAME);

    	int ePos = 0;
    	for (Property p : properties) {
    		appendComments(d, e, ePos);
    		Element keyE = d.createElement("key");
    		String keyStr=p.getKey();
    		if(keyStr==null){
    			keyStr="";
    		}
    		Text keyText=d.createTextNode(keyStr);
    		keyE.appendChild(keyText);
    		e.appendChild(keyE);
    		ePos++;
    		appendComments(d, e, ePos);
    		Element valueE = d.createElement("value");
    		String valStr=p.getValue();
    		if(valStr==null){
    			valStr="";
    		}
    		Text valText=d.createTextNode(valStr);
    		valueE.appendChild(valText);
    		e.appendChild(valueE);
    		ePos++;
    		appendComments(d, e, ePos);
    	}

    	for (ePos++; ePos < comments.size(); ePos++) {
    		appendComments(d, e, ePos);
    	}

    	return e;

    }
    

}
