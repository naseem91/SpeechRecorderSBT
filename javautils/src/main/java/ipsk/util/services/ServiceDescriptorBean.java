//    IPS Java Utils
// 	  (c) Copyright 2012
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

package ipsk.util.services;

import ipsk.util.InterfaceInfoBean;

import javax.xml.bind.annotation.XmlType;


/**
 * @author klausj
 *
 */
@XmlType(name="service",namespace="http://www.phonetik.uni-muenchen.de/schema/util")
public class ServiceDescriptorBean extends InterfaceInfoBean implements ipsk.util.services.ServiceDescriptor{
//    private Version implementationVersion=null;
//    private Version specificationVersion=null;
//    private LocalizableMessage title=null;
//    private LocalizableMessage description=null;
//    private String vendor=null;
    
    public ServiceDescriptorBean() {
        super();
       
    }

    private String serviceImplementationClassname;

   

//    /* (non-Javadoc)
//     * @see ipsk.util.InterfaceInfo#getTitle()
//     */
//    @XmlJavaTypeAdapter(LocalizableMessageXMLAdapter.class)
//    public LocalizableMessage getTitle() {
//       return title;
//    }
//
//    /* (non-Javadoc)
//     * @see ipsk.util.InterfaceInfo#getDescription()
//     */
//    @XmlJavaTypeAdapter(LocalizableMessageXMLAdapter.class)
//    public LocalizableMessage getDescription() {
//        return description;
//    }
//
//    /* (non-Javadoc)
//     * @see ipsk.util.InterfaceInfo#getVendor()
//     */
//    public String getVendor() {
//        return vendor;
//    }
//
//    /* (non-Javadoc)
//     * @see ipsk.util.InterfaceInfo#getSpecificationVersion()
//     */
//    @XmlJavaTypeAdapter(Version.VersionXMLAdapter.class)
//    public Version getSpecificationVersion() {
//        return specificationVersion;
//    }
//
//    /* (non-Javadoc)
//     * @see ipsk.util.InterfaceInfo#getImplementationVersion()
//     */
//    @XmlJavaTypeAdapter(Version.VersionXMLAdapter.class)
//    public Version getImplementationVersion() {
//      return implementationVersion;
//    }
//
    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getServiceImplementationClassname()
     */
    @javax.xml.bind.annotation.XmlElement(required=true)
    public String getServiceImplementationClassname() {
        return serviceImplementationClassname;
    }
//
//    public void setImplementationVersion(Version implementationVersion) {
//        this.implementationVersion = implementationVersion;
//    }
//
//    public void setTitle(LocalizableMessage title) {
//        this.title = title;
//    }

    public void setServiceImplementationClassname(
            String serviceImplementationClassname) {
        this.serviceImplementationClassname = serviceImplementationClassname;
    }

//    public void setSpecificationVersion(Version specificationVersion) {
//        this.specificationVersion = specificationVersion;
//    }
//
//    public void setDescription(LocalizableMessage description) {
//        this.description = description;
//    }
//
//    public void setVendor(String vendor) {
//        this.vendor = vendor;
//    }

    public boolean equals(Object o){
        if(this==o) return true;
        if(o instanceof ServiceDescriptor){
            ServiceDescriptor oSd=(ServiceDescriptor)o;
            String sICn=oSd.getServiceImplementationClassname();
            if(sICn!=null){
                if(!sICn.equals(getServiceImplementationClassname())){
                    return false;
                }
            }else{
                if(getServiceImplementationClassname()!=null){
                    return false;
                }
            }
            return super.equals(oSd);
        }
        return false;
    }
    
  
}
