//    Speechrecorder
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.


package ipsk.apps.speechrecorder.prompting;

import java.net.URL;

import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter;
import ipsk.text.Version;
import ipsk.util.LocalizableMessage;

/**
 * @author klausj
 *
 */
public class BasicPromptPresenterServiceDescriptor implements
        PromptPresenterServiceDescriptor {

    private URL packageURL;
    private String implClassname;
    private LocalizableMessage title;
    private Version implVersion;
    private String vendor;
    private LocalizableMessage description;
    private String[][] supportedMimetypes;
    
    public BasicPromptPresenterServiceDescriptor(String implClassName, LocalizableMessage title,String vendor,Version implVersion,LocalizableMessage description,String[][] supportedMimeTypes){
        super();
        this.implClassname=implClassName;
        this.title=title;
        this.implVersion=implVersion;
        this.vendor=vendor;
        this.description=description;
        this.supportedMimetypes=supportedMimeTypes;
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
        return description;
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getVendor()
     */
    public String getVendor() {
        
        return vendor;
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getSpecificationVersion()
     */
    public Version getSpecificationVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getImplementationVersion()
     */
    public Version getImplementationVersion() {
      
        return implVersion;
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getServiceClass()
     */
    public Class<?> getServiceClass() {
     return PromptPresenter.class;
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getServiceImplementationClassname()
     */
    public String getServiceImplementationClassname() {
       return implClassname;
    }

    /* (non-Javadoc)
     * @see ipsk.apps.speechrecorder.prompting.PromptPresenterServiceDescriptor#getSupportedMIMETypes()
     */
    public String[][] getSupportedMIMETypes() {
       
        return supportedMimetypes;
    }

    public URL getPackageURL() {
        return packageURL;
    }

    public void setPackageURL(URL packageURL) {
        this.packageURL = packageURL;
    }


}
