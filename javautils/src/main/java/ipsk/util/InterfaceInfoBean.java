//    IPS Java Utils
// 	  (c) Copyright 2011
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.util;

import ipsk.text.Version;
import ipsk.util.i18n.LocalizableMessageXMLAdapter;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author klausj
 *
 */
@XmlType(name="interfaceInfo",namespace="http://www.phonetik.uni-muenchen.de/schema/util")
public class InterfaceInfoBean implements InterfaceInfo{
    
    private LocalizableMessage title;
    private LocalizableMessage description;
    private String vendor;
    private String[] links;
   
    private Version specificationVersion;
    private Version implementationVersion;
    
    @XmlJavaTypeAdapter(LocalizableMessageXMLAdapter.class)
    public LocalizableMessage getTitle() {
        return title;
    }
    public void setTitle(LocalizableMessage title) {
        this.title = title;
    }
    @XmlJavaTypeAdapter(LocalizableMessageXMLAdapter.class)
    public LocalizableMessage getDescription() {
        return description;
    }
    public void setDescription(LocalizableMessage description) {
        this.description = description;
    }
    public String getVendor() {
        return vendor;
    }
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    public String[] getLinks() {
        return links;
    }
    public void setLinks(String[] links) {
        this.links = links;
    }

    @XmlJavaTypeAdapter(Version.VersionXMLAdapter.class)
    public Version getSpecificationVersion() {
        return specificationVersion;
    }
    public void setSpecificationVersion(Version specificationVersion) {
        this.specificationVersion = specificationVersion;
    }
    @XmlJavaTypeAdapter(Version.VersionXMLAdapter.class)
    public Version getImplementationVersion() {
        return implementationVersion;
    }
    public void setImplementationVersion(Version implementationVersion) {
        this.implementationVersion = implementationVersion;
    }
  
    public boolean equals(Object o){
        if(this==o)return true;
        if(o instanceof InterfaceInfoBean){
            InterfaceInfoBean oIib=(InterfaceInfoBean)o;
            String oV=oIib.getVendor();
            if(oV!=null){
                if(!oV.equals(vendor)){
                    return false;
                }
            }else{
                if(vendor!=null){
                    return false;
                }
            }
            Version oSv=oIib.getSpecificationVersion();
            if(oSv!=null){
                if(!oSv.equals(specificationVersion)){
                    return false;
                }
            }else{
                if(specificationVersion!=null){
                    return false;
                }
            }
            Version oIv=oIib.getImplementationVersion();
            if(oIv!=null){
                if(!oIv.equals(implementationVersion)){
                    return false;
                }
            }else{
                if(implementationVersion!=null){
                    return false;
                }
            }
            return true;
            
        }
        return false;
    }
   
    
}
