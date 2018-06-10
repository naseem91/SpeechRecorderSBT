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

package ipsk.util.apps.descriptor;

import ipsk.text.Version;

import ipsk.util.LocalizableMessage;
import ipsk.util.i18n.LocalizableMessageXMLAdapter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author klausj
 *
 */
@XmlType(name="applicationVersion",propOrder={"installationPackages","changes"})
public class ApplicationVersionDescriptor implements Comparable<ApplicationVersionDescriptor>{
    private Version version;
    
   
//    private URL downloadURL;
    
    private List<InstallationPackage> installationPackages=new ArrayList<InstallationPackage>();
    private List<Change> changes=new ArrayList<Change>();
 
    private InstallationPackage platformInstallationPackage;
    
    /**
     * 
     */
    public ApplicationVersionDescriptor() {
       super();
    }

    @XmlJavaTypeAdapter(Version.VersionXMLAdapter.class)
    @javax.xml.bind.annotation.XmlAttribute(required=true)
    public Version getVersion() {
        return version;
    }


    public void setVersion(Version version) {
        this.version = version;
    }


    @XmlElement(name="change")
    public List<Change> getChanges() {
        return changes;
    }


    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(ApplicationVersionDescriptor o) {
       Version oVersion=o.getVersion();
       return version.compareTo(oVersion);
    }
    
    @XmlElement(name="installationPackage")
    public List<InstallationPackage> getInstallationPackages() {
        return installationPackages;
    }

    public void setInstallationPackages(
            List<InstallationPackage> installationPackages) {
        this.installationPackages = installationPackages;
    }

    @XmlTransient
    public InstallationPackage getPlatformInstallationPackage() {
        return platformInstallationPackage;
    }

    public void setPlatformInstallationPackage(
            InstallationPackage platformInstallationPackage) {
        this.platformInstallationPackage = platformInstallationPackage;
    }
    
    public String toString(){
    	StringBuffer sb=new StringBuffer("Version:"+version.toString());
    	return sb.toString();
    }

}
