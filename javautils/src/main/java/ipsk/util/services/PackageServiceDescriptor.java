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
public class PackageServiceDescriptor<D extends ServiceDescriptor,S> implements ServiceDescriptor {

    private URL packageURL;
    private Class<S> serviceClass;
    private String serviceImplementationClassname;
    private Package p;
    private Attributes mainAttributes;
    private LocalizableMessage title=null;
    private Version specVersion=null;
    private Version implementationVersion=null;
    
    public PackageServiceDescriptor(Class<S> serviceClass,String serviceImplementationClassname,Package p){
        this.serviceClass=serviceClass;
        this.serviceImplementationClassname=serviceImplementationClassname;
        this.p=p;
        if(p!=null){
            String titleStr=p.getImplementationTitle();
            if(titleStr!=null){
                title=new LocalizableMessage(titleStr);
            }
            String specVersStr=p.getSpecificationVersion();
            try {
                specVersion=Version.parseString(specVersStr);
            } catch (ParserException e) {

            }
            String implVersStr=p.getImplementationVersion();
            try {
                implementationVersion=Version.parseString(implVersStr);
            } catch (ParserException e) {

            }

        }
    }
    
   
    
    
    private String getMainAttribute(Attributes.Name key){
        if(mainAttributes!=null){
            Object value=mainAttributes.get(key);
            if(value!=null && value instanceof String){
                return (String)value;
            }
        }
        return null;
    }
    
    private LocalizableMessage getMainAttributeAsLocalizableMessage(Attributes.Name key){
        if(mainAttributes!=null){
            
            Object value=mainAttributes.get(key);
            if(value!=null && value instanceof String){
                return new LocalizableMessage((String)value);
            }
        }
        return null;
    }
    
    private Version getMainAttributeAsVersion(Attributes.Name key){
        if(mainAttributes!=null){
            String versStr=getMainAttribute(key);
            if(versStr!=null){
                try {
                    return Version.parseString(versStr);
                } catch (ParserException e) {
                    // return null
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getTitle()
     */
    public LocalizableMessage getTitle() {
      
        return title;
 
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getDescription()
     */
    public LocalizableMessage getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getVendor()
     */
    public String getVendor() {
        return p.getImplementationVendor();
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getSpecificationVersion()
     */
    public Version getSpecificationVersion() {
       return specVersion;
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getImplementationVersion()
     */
    public Version getImplementationVersion() {
        return implementationVersion;
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
