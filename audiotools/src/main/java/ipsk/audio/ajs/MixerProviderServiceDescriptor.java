//    IPS Java Audio Tools
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.audio.ajs;

import ipsk.audio.DeviceInfo;
import ipsk.audio.DeviceProvider;
import ipsk.audio.DeviceProviderInfo;
import ipsk.text.Version;
import ipsk.util.LocalizableMessage;
import ipsk.util.services.ServiceDescriptorBean;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.security.auth.kerberos.ServicePermission;
import javax.xml.bind.JAXB;

/**
 * @author klausj
 *
 */
public class MixerProviderServiceDescriptor extends ServiceDescriptorBean implements DeviceProviderInfo{

    private String audioInterfaceName;
    private String legacyJavaSoundSuffix;
    private boolean providesCaptureDevices=true;
    private boolean providesPlaybackDevices=true;
    
    @javax.xml.bind.annotation.XmlElement(required=true)
    public String getAudioInterfaceName(){
        return audioInterfaceName;
    }
    
    public boolean isStandardJavaSoundWrapper() {
        return false;
    }

    public void setAudioInterfaceName(String audioInterfaceName) {
        this.audioInterfaceName = audioInterfaceName;
    }

 

//    /* (non-Javadoc)
//     * @see ipsk.beans.dyn.DynamicPropertyContainer#getDynamicPropertyDescriptors()
//     */
//    public List<DynPropertyDescriptor> getDynamicPropertyDescriptors() {
//       
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see ipsk.beans.dyn.DynamicPropertyContainer#setDynamicProperty(ipsk.beans.dyn.DynProperty)
//     */
//    public void setDynamicProperty(DynProperty dynProperty) {
//       
//    }
//
//    /* (non-Javadoc)
//     * @see ipsk.beans.dyn.DynamicPropertyContainer#getDynamicProperty(java.lang.String)
//     */
//    public DynProperty getDynamicProperty(String name) {
//        
//        return null;
//    }
    
    public boolean equals(Object o){
        if(this==o)return true;
        if(o instanceof MixerProviderServiceDescriptor){
            MixerProviderServiceDescriptor oMpsd=(MixerProviderServiceDescriptor)o;
            String oAin=oMpsd.getAudioInterfaceName();
            if(oAin!=null){
                if(!oAin.equals(getAudioInterfaceName())){
                    return false;
                }
            }else{
                if(getAudioInterfaceName()!=null){
                    return false;
                }
            }
            if(oMpsd.isStandardJavaSoundWrapper()!=this.isStandardJavaSoundWrapper()){
                return false;
            }
            return super.equals(oMpsd);
        }
        return false;
        
    }
    
    public static void main(String[] args){
        MixerProviderServiceDescriptor td=new MixerProviderServiceDescriptor();
        td.setServiceImplementationClassname("test.bla.Klasse");
        td.setAudioInterfaceName("ALSA");
      
        Version iv=new Version(new int[]{2,2,14});
        td.setImplementationVersion(iv);
        HashMap<Locale,String> lStrs=new HashMap<Locale,String>();
        lStrs.put(null, "IPS ALSA JavaSound implementation");
        lStrs.put(new Locale("de"), "IPS ALSA JavaSound Implementierung");
        lStrs.put(new Locale("en"), "IPS ALSA JavaSound implementation");
        LocalizableMessage lm=new LocalizableMessage(lStrs);
        td.setTitle(lm);
        JAXB.marshal(td, System.out);
    }

    /**
     * @param legacyJavaSoundSuffix the legacyJavaSoundSuffix to set
     */
    public void setLegacyJavaSoundSuffix(String legacyJavaSoundSuffix) {
        this.legacyJavaSoundSuffix = legacyJavaSoundSuffix;
    }

    /**
     * @return the legacyJavaSoundSuffix
     */
    public String getLegacyJavaSoundSuffix() {
        return legacyJavaSoundSuffix;
    }

    public boolean isProvidesCaptureDevices() {
        return providesCaptureDevices;
    }

    public void setProvidesCaptureDevices(boolean providesCaptureDevices) {
        this.providesCaptureDevices = providesCaptureDevices;
    }

    public boolean isProvidesPlaybackDevices() {
        return providesPlaybackDevices;
    }

    public void setProvidesPlaybackDevices(boolean providesPlaybackDevices) {
        this.providesPlaybackDevices = providesPlaybackDevices;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.DeviceProviderInfo#getInstance()
     */
    public DeviceProvider getInstance() {
       String className=getServiceImplementationClassname();
       try {
        Class mpsdClass=Class.forName(className);
    } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
//       mpsdClass.n
       return null;
    }

    /* (non-Javadoc)
     * @see ipsk.audio.DeviceProviderInfo#getImplementationClassname()
     */
    public String getImplementationClassname() {
       return getServiceImplementationClassname();
    }
    
    public String toString(){
        return getAudioInterfaceName()+" ("+getImplementationClassname()+")";
    }
   
}
