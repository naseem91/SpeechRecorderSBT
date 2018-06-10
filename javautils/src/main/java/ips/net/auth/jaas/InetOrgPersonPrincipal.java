

package ips.net.auth.jaas;

import java.security.Principal;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

public class InetOrgPersonPrincipal implements Principal, java.io.Serializable {

    private String dName;
    private Attributes attributes;
 
    public InetOrgPersonPrincipal(String dName, Attributes attrs) {
	if (dName == null || attrs==null)
	    throw new NullPointerException();

	this.dName = dName;
	this.attributes=attrs;
    }
    
   

    public String getName() {
	return dName;
    }

    public String toString() {
	return("InetOrgPerson:  " + dName);
    }
    
    public boolean equals(Object o) {
	if (o == null)
	    return false;

        if (this == o)
            return true;
 
        if (!(o instanceof InetOrgPersonPrincipal))
            return false;
        InetOrgPersonPrincipal that = (InetOrgPersonPrincipal)o;

	if (this.getName().equals(that.getName()))
	    return true;
	return false;
    }
 
 
    public int hashCode() {
	return dName.hashCode();
    }

    public Attributes getAttributes() {
        return attributes;
    }



    public boolean isAttributesCaseIgnored() {
        return attributes.isCaseIgnored();
    }



    public int attrbutesSize() {
        return attributes.size();
    }



    public Attribute getAttribute(String attrID) {
        return attributes.get(attrID);
    }



    public NamingEnumeration<? extends Attribute> getAllAttributes() {
        return attributes.getAll();
    }



    public NamingEnumeration<String> getAttributeIDs() {
        return attributes.getIDs();
    }
    
    private String getStringAttribute(String attrID) throws NamingException {
        Attribute a=getAttribute(attrID);
        if(a==null){
            return null;
        }
        Object av;
       av = a.get();
       
        if(av==null){
            return null;
        }
        if(av instanceof String){
            return (String)av;
        }else{
            return av.toString();
        }
        
    }
    public String getAttrDisplayname() throws NamingException{
       return getStringAttribute("displayname");
    }
    public String getAttrGivenname() throws NamingException{
        return getStringAttribute("givenName");
     }
    public String getAttrSurname() throws NamingException{
        String sn=getStringAttribute("sn");
        if(sn!=null){
            return sn;
        }else{
            return getStringAttribute("surname");
        }
     }
    public String getAttrMail() throws NamingException{
        return getStringAttribute("mail");
     }
    public String getAttrTelephoneNumber() throws NamingException{
        return getStringAttribute("telephoneNumber");
     }
    
}
