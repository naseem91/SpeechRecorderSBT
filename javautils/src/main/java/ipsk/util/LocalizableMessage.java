//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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


package ipsk.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="i18n",namespace="http://www.phonetik.uni-muenchen.de/schema/util/i18n")
public class LocalizableMessage {
	
    private String resourceKey;
	private String resourceBundleName;
	private String standardMessage;
//	private Set<LocalizedString> localizedStringSet;
	private Map<Locale,String> localizedStringMap=null;
	private Object[] arguments;
	
	
	public String getStandardMessage() {
		return standardMessage;
	}

	public LocalizableMessage(String standardMessage){
		this(null,null);
		this.standardMessage=standardMessage;
	}
	public LocalizableMessage(Map<Locale,String> localizedStringMap){
        this(null,null);
        this.localizedStringMap=localizedStringMap;
     
    }
	public LocalizableMessage(String resourceBundleName,String resourceKey){
		this(resourceBundleName,resourceKey,null);
	}
	
	public LocalizableMessage(String resourceBundleName,String resourceKey,Object[] parameters){
		this.resourceBundleName=resourceBundleName;
		this.resourceKey=resourceKey;
		this.arguments=parameters;
	}
	
	public String getResourceKey() {
		return resourceKey;
	}
	
	public String getResourceBundleName() {
		return resourceBundleName;
	}
	
	
	
	public String localize(){
		return localize(null);
	}
	
	public String toString(){
	    return localize();
	}
	
	public String localize(Locale locale){
		if(standardMessage!=null){
			return standardMessage;
		}
		if(localizedStringMap!=null){
		    
		    if(locale==null){
		        locale=Locale.getDefault();
		    }
		    String lStr=localizedStringMap.get(locale);
		    if(lStr!=null){
		        return lStr;
		    }
		    Locale noVariantLocale=new Locale(locale.getLanguage(),locale.getCountry());
		    lStr=localizedStringMap.get(noVariantLocale);
            if(lStr!=null){
                return lStr;
            }
            Locale noCountryLocale=new Locale(locale.getLanguage());
            lStr=localizedStringMap.get(noCountryLocale);
            if(lStr!=null){
                return lStr;
            }
            lStr=localizedStringMap.get(null);
            if(lStr!=null){
                return lStr;
            }
            return null;
		}
		ResourceBundle rb;
		if(resourceBundleName!=null){
		    if(locale!=null){
		        rb=ResourceBundle.getBundle(resourceBundleName,locale);
		    }else{
		        rb=ResourceBundle.getBundle(resourceBundleName);
		    }
		    String pattern=rb.getString(resourceKey);
		    String msg;
		    MessageFormat mf;
		    if(locale!=null){
		        mf=new MessageFormat(pattern,locale);
		    }else{
		        mf=new MessageFormat(pattern);
		    }
		    Object[] args=new Object[0];
		    if(arguments!=null){
		        args=arguments;
		    }
		    msg=mf.format(args, new StringBuffer(), null).toString();
		    return msg;
		}
		return null;
	}
	
	public Object clone(){
		if(standardMessage!=null){
			return new LocalizableMessage(standardMessage);
		}else{
			return new LocalizableMessage(resourceBundleName, resourceKey);
		}
	}
	
	public static void main(String[] args){
	   
	    HashMap<Locale,String> lStrs=new HashMap<Locale,String>();
	    lStrs.put(null, "Hello (default)");
	    lStrs.put(new Locale("de"), "Hallo");
	    lStrs.put(new Locale("de","DE"), "Hallo Deutschland");
	    lStrs.put(new Locale("de","AT"), "Hallo Oesterreich");
	    lStrs.put(new Locale("en"), "Hello");
	    LocalizableMessage lm=new LocalizableMessage(lStrs);
	    System.out.println(lm.localize(new Locale("de")));
	}

    public Map<Locale, String> getLocalizedStringMap() {
        return localizedStringMap;
    }
}
