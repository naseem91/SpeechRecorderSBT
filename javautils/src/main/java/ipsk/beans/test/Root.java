//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.beans.test;

import java.util.Date;
import java.util.Set;

import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMCollectionElement;
import ipsk.beans.dom.DOMElements;
import ipsk.beans.dom.DOMTextNodePropertyName;

//import ipsk.beans.test.namespace.TestElement;


/**
 * Test Class for DOMCodec.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
@DOMElements({"optionalInteger","routing","childrenSet","child","name","weekDay","anotherChild"})
@DOMTextNodePropertyName("textContent")
@DOMAttributes({"optionalOffset","annotatedTestAttribute","timestamp","isSomethingSpecial"})
public class Root extends SuperRoot{

	private String attributeId;
	private String name;
	private String[] mixerNames;
	private int[] routing;
	private Integer optionalOffset=null;
	private Integer optionalInteger=null;
	
	/**
	 * @return the optionalInteger
	 */
	public Integer getOptionalInteger() {
		return optionalInteger;
	}


	/**
	 * @param optionalInteger the optionalInteger to set
	 */
	public void setOptionalInteger(Integer optionalInteger) {
		this.optionalInteger = optionalInteger;
	}


	/**
	 * @return the optionalOffset
	 */
	public Integer getOptionalOffset() {
		return optionalOffset;
	}


	/**
	 * @param optionalOffset the optionalOffset to set
	 */
	public void setOptionalOffset(Integer optionalOffset) {
		this.optionalOffset = optionalOffset;
	}


	/**
	 * @return the routing
	 */
	public int[] getRouting() {
		return routing;
	}


	/**
	 * @param routing the routing to set
	 */
	public void setRouting(int[] routing) {
		this.routing = routing;
	}

	private Boolean isSomethingSpecial;
	private Set<Child1> childrenSet;
   // private TestElement test;
	
	private Child1[] child;
	
	private Child1[] anotherChild;
    
    public Child1[] getAnotherChild() {
        return anotherChild;
    }


    public void setAnotherChild(Child1[] anotherChild) {
        this.anotherChild = anotherChild;
    }

    private String annotatedTestAttribute;
	
    public enum WeekDays {MONDAY,TUESDAY,WEDNESDAY};
    private WeekDays weekDay=WeekDays.MONDAY;
    
    private Date timestamp;
    
   private String textContent;
    

	public Root() {
		super();
		timestamp=new Date();
		
	}

	
	public String getName() {
		return name;
	}

	
	public void setName(String string) {
		name = string;
	}
	

	public Child1[] getChild() {
		return child;
	}

	public void setChild(Child1[] child) {
		this.child = child;
	}

	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String string) {
		attributeId = string;
	}

    public String[] getMixerNames() {
        return mixerNames;
    }
    public void setMixerNames(String[] mixerNames) {
        this.mixerNames = mixerNames;
    }

    public String getAnnotatedTestAttribute() {
        return annotatedTestAttribute;
    }

    public void setAnnotatedTestAttribute(String annotatedTestAttribute) {
        this.annotatedTestAttribute = annotatedTestAttribute;
    }

	public WeekDays getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(WeekDays weekDay) {
		this.weekDay = weekDay;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@DOMCollectionElement(collectionElementName="child1")
	public Set<Child1> getChildrenSet() {
		return childrenSet;
	}

	public void setChildrenSet(Set<Child1> childrenSet) {
		this.childrenSet = childrenSet;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}


    public Boolean getIsSomethingSpecial() {
        return isSomethingSpecial;
    }


    public void setIsSomethingSpecial(Boolean isSomethingSpecial) {
        this.isSomethingSpecial = isSomethingSpecial;
    }

//    public TestElement getTest() {
//        return test;
//    }
//
//    public void setTest(TestElement test) {
//        this.test = test;
//    }
}
