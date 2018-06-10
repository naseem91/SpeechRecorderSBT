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

import ipsk.text.ParserException;
import ipsk.text.Version;
import ipsk.util.LocalizableMessage;

import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author klausj
 *
 */
public class ManifestServiceDescriptor implements ServiceDescriptor {

    private URL packageURL;
    private Class<?> serviceClass;
    private String serviceImplementationClassname;
    private Manifest manifest;
    private Attributes mainAttributes;
    private Attributes serviceSectionAttributes;
    private Package p;
    
    
    public ManifestServiceDescriptor(Class<?> serviceClass,String serviceImplementationClassname,Manifest manifest){
        this.serviceClass=serviceClass;
        this.serviceImplementationClassname=serviceImplementationClassname;
        this.manifest=manifest;
        if(manifest!=null){
            mainAttributes=manifest.getMainAttributes();
            serviceSectionAttributes=manifest.getAttributes(serviceImplementationClassname);
        }
    }
    
    
    private String getAttribute(Attributes.Name key){
        if(serviceSectionAttributes!=null){
            Object value=serviceSectionAttributes.get(key);
            if(value!=null && value instanceof String){
                return (String)value;
            }
        }
        if(mainAttributes!=null){
            Object value=mainAttributes.get(key);
            if(value!=null && value instanceof String){
                return (String)value;
            }
        }
        return null;
    }
    
    private LocalizableMessage getAttributeAsLocalizableMessage(Attributes.Name key){
        if(serviceSectionAttributes!=null){
            Object value=serviceSectionAttributes.get(key);
            if(value!=null && value instanceof String){
                return new LocalizableMessage((String)value);
            }
        }
        if(mainAttributes!=null){
            Object value=mainAttributes.get(key);
            if(value!=null && value instanceof String){
                return new LocalizableMessage((String)value);
            }
        }
        return null;
    }
    
    private Version getAttributeAsVersion(Attributes.Name key){
        String versStr=getAttribute(key);
        if(versStr!=null){
            try {
                return Version.parseString(versStr);
            } catch (ParserException e) {
                // return null
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getTitle()
     */
    public LocalizableMessage getTitle() {
      
        return getAttributeAsLocalizableMessage(Attributes.Name.IMPLEMENTATION_TITLE);
 
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getDescription()
     */
    public LocalizableMessage getDescription() {
        return null;
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getVendor()
     */
    public String getVendor() {
        return getAttribute(Attributes.Name.IMPLEMENTATION_VENDOR);
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getSpecificationVersion()
     */
    public Version getSpecificationVersion() {
       return getAttributeAsVersion(Attributes.Name.SPECIFICATION_VERSION);
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getImplementationVersion()
     */
    public Version getImplementationVersion() {
        return getAttributeAsVersion(Attributes.Name.IMPLEMENTATION_VERSION);
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getServiceClass()
     */
    public Class<?> getServiceClass() {
        return serviceClass;
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getServiceImplementationClassname()
     */
    public String getServiceImplementationClassname() {
       return serviceImplementationClassname;
    }


    public URL getPackageURL() {
        return packageURL;
    }


    public void setPackageURL(URL packageURL) {
        this.packageURL = packageURL;
    }

}
