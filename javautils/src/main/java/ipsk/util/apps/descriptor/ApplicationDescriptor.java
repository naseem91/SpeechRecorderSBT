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

import ipsk.text.ParserException;
import ipsk.text.Version;
import ipsk.text.VersionPattern;
import ipsk.util.LocalizableMessage;
import ipsk.util.i18n.LocalizableMessageXMLAdapter;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author klausj
 *
 */
@XmlType(name="application",propOrder={"name","vendor","url","description","versions"})
public class ApplicationDescriptor{

    private String name;
    private LocalizableMessage description;
    private String vendor;
    private URL url;
    private List<ApplicationVersionDescriptor> versions=new ArrayList<ApplicationVersionDescriptor>();
    
    
    /**
     * 
     */
    public ApplicationDescriptor() {
       super();
    }

    @javax.xml.bind.annotation.XmlElement(required=true)
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
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


    public URL getUrl() {
        return url;
    }


    public void setUrl(URL url) {
        this.url = url;
    }

    @XmlElement(name="version")
    public List<ApplicationVersionDescriptor> getVersions() {
        return versions;
    }


    public void setVersions(List<ApplicationVersionDescriptor> versions) {
        this.versions = versions;
    }
    
    
    public static void main(String[] args){
        ApplicationDescriptor ad=new ApplicationDescriptor();
        HashMap<Locale,String> lhm=new HashMap<Locale, String>();
        Locale loc=new Locale("de");
        lhm.put(null, "Speech recording tool");
        ad.setDescription(new LocalizableMessage(lhm));
        ApplicationVersionDescriptor avd1=new ApplicationVersionDescriptor();
        avd1.setVersion(new Version(new int[]{1,0,0}));
        
        Change ch1=new Change();
        ch1.setId("0001");
        ch1.setPriority(Change.Priority.OPTIONAL);
        Change ch2=new Change();
        HashMap<Locale,String> lhmCh2=new HashMap<Locale, String>();

        lhmCh2.put(loc, "Dies und das ...");
        lhmCh2.put(null, "(default) This and that ...");
        ch2.setDescription(new LocalizableMessage(lhmCh2));
        try {
            VersionPattern affVp2=VersionPattern.parseString("1.x.x");
            ch2.setAffectsVersions(affVp2);
        } catch (ParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ApplicationVersionDescriptor avd2=new ApplicationVersionDescriptor();
        avd2.setVersion(new Version(new int[]{1,1,0}));
        InstallationPackage ip2Win=new InstallationPackage();
        ip2Win.setOsName("Windows");
        try {
            ip2Win.setDownloadURL(new URL("http://www.bas.uni-muenchen.de/forschung/Bas/software/speechrecorder/SpeechRecorder-2.2.14-b03_Setup.msi"));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        avd2.getInstallationPackages().add(ip2Win);
        avd2.getChanges().add(ch1);
        avd2.getChanges().add(ch2);
        ad.getVersions().add(avd1);
        ad.getVersions().add(avd2);
        JAXB.marshal(ad, System.out);
    }


}
