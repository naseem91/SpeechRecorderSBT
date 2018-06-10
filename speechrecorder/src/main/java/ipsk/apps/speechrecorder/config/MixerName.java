//    Speechrecorder
//    (c) Copyright 2012
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
 
package ipsk.apps.speechrecorder.config;


import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMTextNodePropertyName;

/**
 * A JavaSound device name or regular expression match.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

@DOMAttributes({"providerId","interfaceName","regex"})
@DOMTextNodePropertyName("name")
public class MixerName {

    public static final String ID_JAVA_CLASS="java:class:";
    private String name;
    private String interfaceName=null;
    private String providerId=null;
    private boolean regex=false;


    public MixerName(){
        super();
    }
    
    public MixerName(String name){
        this(null,name);
    }
    public MixerName(String providerId,String name){
        this(providerId,name,false);
    }
    public MixerName(String providerId,String name,boolean regex){
        this(providerId,null,name,regex);
    }
    /**
     * @param providerId
     * @param interfaceName
     * @param name
     */
    public MixerName(String providerId, String interfaceName,
            String name) {
        this(providerId,interfaceName,name,false);
    }
    public MixerName(String providerId,String interfaceName,String name,boolean regex){
        this();
        this.providerId=providerId;
        this.interfaceName=interfaceName;
        this.name=name;
        this.regex=regex;
    }
    
    

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getProviderId() {
        return providerId;
    }
    public String providerIdAsJavaClassName(){
       return providerIdToJavaClassName(providerId);
    }
    public static String providerIdToJavaClassName(String providerId){
        if(providerId!=null){
            String trimmedIId=providerId.trim();
            if(trimmedIId.startsWith(ID_JAVA_CLASS)){
                return providerId.substring(ID_JAVA_CLASS.length()).trim();
            }
        }
        return null;
    }
    
    public static String javaClassnameToProviderId(String className){
        return ID_JAVA_CLASS+className;
    }
    
    public void setProviderId(String interfaceId) {
        this.providerId = interfaceId;
    }
    
   
    
    public boolean equals(Object o){
        if (o!=null && o instanceof MixerName){
            MixerName oRmn=(MixerName)o;
            if(oRmn.isRegex()==isRegex()){
                String oProviderId=oRmn.getProviderId();
                if(oProviderId==null){
                    if(providerId!=null){
                        return false;
                    }
                    // no provider ID
                    // check interface names
                    String oInterfaceName=oRmn.getInterfaceName();
                    if(oInterfaceName==null){
                        if(interfaceName!=null){
                            return false;
                        }
                    }else{
                        if(!oInterfaceName.equals(interfaceName)){
                            return false;
                        }
                    }
                }else{
                    if(!oProviderId.equals(providerId)){
                        return false;
                    }
                    // do not care about the 
                }
                String oRmnName=oRmn.getName();
                if(oRmnName==null){
                    if(name==null){
                        return true;
                    }
                }else{
                    if(oRmnName.equals(name)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public String toString(){
        StringBuffer s=new StringBuffer();
        if(providerId!=null){
            s.append(providerId);
            s.append(": ");
        }
        if(interfaceName!=null){
            s.append(interfaceName);
            s.append(": ");
        }
        if(name!=null){
            s.append(name);
        }
        if(regex){
            s.append(" (regular expr)");
        }
        return s.toString();
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

   
}
