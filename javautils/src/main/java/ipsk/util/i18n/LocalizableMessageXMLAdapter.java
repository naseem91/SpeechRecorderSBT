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

import ipsk.util.LocalizableMessage;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * @author klausj
 *
 */
@XmlType()
public class LocalizableMessageXMLAdapter extends XmlAdapter<LocalizableMessageXMLAdapted,LocalizableMessage> {

    private LocaleXMLAdapter localeXmlAdapter=new LocaleXMLAdapter();
    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public LocalizableMessageXMLAdapted marshal(LocalizableMessage arg0)
            throws Exception {
        if(arg0==null){
            return null;
        }
        LocalizableMessageXMLAdapted lm=new LocalizableMessageXMLAdapted();
        HashMap<LocaleXMLAdapted,String> newMap=new HashMap<LocaleXMLAdapted, String>();
        Map<Locale,String> map=arg0.getLocalizedStringMap();
        if(map!=null){
        Set<Entry<Locale,String>> entrySet=map.entrySet();
        
        for(Entry<Locale, String> entry:entrySet){
            Locale loc=entry.getKey();
            LocaleXMLAdapted locX=localeXmlAdapter.marshal(loc);
            String val=entry.getValue();
            newMap.put(locX, val);
        }
        }
        lm.setLocalizedStringMap(newMap);
        return lm;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public LocalizableMessage unmarshal(LocalizableMessageXMLAdapted arg0)
            throws Exception {
        if(arg0==null){
            return null;
        }
        HashMap<Locale,String> newMap=new HashMap<Locale, String>();
        Map<LocaleXMLAdapted,String> map=arg0.getLocalizedStringMap();
        Set<Entry<LocaleXMLAdapted,String>> entrySet=map.entrySet();
        
        for(Entry<LocaleXMLAdapted, String> entry:entrySet){
            LocaleXMLAdapted locX=entry.getKey();
            Locale loc=localeXmlAdapter.unmarshal(locX);
            String val=entry.getValue();
            newMap.put(loc, val);
        }
        return new LocalizableMessage(newMap);
    }
    
}
