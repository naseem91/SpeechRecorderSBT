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

import java.net.URL;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author klausj
 *
 */
@XmlType(name="installationPackage",propOrder={"osVersionPattern","options","downloadURL"})
public class InstallationPackage {

	private String osName;
	private String type;
	private Map<String,String> options=null;
    /**
	 * @return the options
	 */
	@XmlElement
	public Map<String, String> getOptions() {
		return options;
	}


	/**
	 * @param options the options to set
	 */
	public void setOptions(Map<String, String> options) {
		this.options = options;
	}

	private String osArch;
    private String osVersionPattern;
    private URL downloadURL;
    
    /**
     * 
     */
    public InstallationPackage() {
        super(); 
    }
    
  
    @XmlAttribute
    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }
    
    /**
	 * @return the type
	 */
	@XmlAttribute
	public String getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

    
    @XmlAttribute
    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }
    @XmlElement
    public URL getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(URL downloadURL) {
        this.downloadURL = downloadURL;
    }
    @XmlElement
    public String getOsVersionPattern() {
        return osVersionPattern;
    }

    public void setOsVersionPattern(String osVersionPattern) {
        this.osVersionPattern = osVersionPattern;
    }

}
