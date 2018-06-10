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

import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author klausj
 *
 */
public class LocaleXMLAdapter extends XmlAdapter<LocaleXMLAdapted,Locale> {

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public LocaleXMLAdapted marshal(Locale v) throws Exception {
        if(v==null){
            return null;
        }
       LocaleXMLAdapted la=new LocaleXMLAdapted();
       String lang=v.getLanguage();
       if(!"".equals(lang)){
           la.setLanguage(lang);
       }
       String country=v.getCountry();
       if(!"".equals(country)){
           la.setCountry(country);
       }
       String var=v.getVariant();
       if(!"".equals(var)){
           la.setVariant(var);
       }
       return la;
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Locale unmarshal(LocaleXMLAdapted v) throws Exception {
        if(v==null){
            return null;
        }
        String language=v.getLanguage();
        String country=v.getCountry();
        String var=v.getVariant();
        if(country!=null){
            if(var!=null){
                return new Locale(language,country,var);
            }
            return new Locale(language, country);
        }
        return new Locale(language);
    }

}
