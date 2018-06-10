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

package ipsk.util.i18n;

import java.util.Map;


import javax.xml.bind.annotation.XmlType;

/**
 * @author klausj
 *
 */
 @XmlType(name="localizableMessage",namespace="http://www.phonetik.uni-muenchen.de/schema/util/i18n")
public class LocalizableMessageXMLAdapted {
   
    private Map<LocaleXMLAdapted,String> localizedStringMap=null;
    public LocalizableMessageXMLAdapted() {
        super();
       
    }
    public Map<LocaleXMLAdapted, String> getLocalizedStringMap() {
        return localizedStringMap;
    }
    public void setLocalizedStringMap(Map<LocaleXMLAdapted, String> localizedStringMap) {
        this.localizedStringMap = localizedStringMap;
    }

    
}
