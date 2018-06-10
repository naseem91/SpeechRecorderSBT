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



package ipsk.apps.speechrecorder.config.ui.audio;

import ipsk.apps.speechrecorder.config.MixerName;
import ipsk.audio.AudioController2;
import ipsk.audio.DeviceInfo;
import ipsk.audio.DeviceProviderInfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.sound.sampled.Mixer;
import javax.swing.JTabbedPane;

/**
 * @author klausj
 *
 */
public class DeviceChooserTabs extends JTabbedPane implements ActionListener {

	private static final long serialVersionUID = 1L;
	private DeviceChooser chooser;
    private DeviceListChooser listChooser;
    
//    private MixerName[] mixerNames;
    
    public DeviceChooserTabs(AudioController2 controller,AudioController2.DeviceType deviceType){
        super();
        setTabPlacement(JTabbedPane.RIGHT);
        chooser=new DeviceChooser(controller, deviceType);
        chooser.addActionListener(this);
        addTab("Simple", chooser);
        listChooser=new DeviceListChooser(controller, deviceType);
        listChooser.addActionListener(this);
        addTab("Expert", listChooser);
    }
    /**
     * @param mixerNames
     */
    public void setSelectedMixerNames(MixerName[] mixerNames) {
        listChooser.setSelectedMixerNames(mixerNames);

        DeviceInfo selDeviceInfo=null;
        boolean chooserEnabled=false;
        if (mixerNames != null){
            if(mixerNames.length==0) {
                chooserEnabled=true;
            }else if(mixerNames.length==1){
                MixerName mn=mixerNames[0];
                String cn=mn.providerIdAsJavaClassName();
                DeviceProviderInfo defaultDeviceProviderInfo=chooser.getDefaultDeviceProviderInfo();
                if(!mn.isRegex() && cn!=null && cn.equals(defaultDeviceProviderInfo.getImplementationClassname())){
                    String devName=mn.getName();
                    List<DeviceInfo> diList=chooser.selectableDeviceInfos();
                    for(DeviceInfo di:diList){
                        if(di!=null){
                        Mixer.Info mInfo=di.getMixerInfo();

                        if(mInfo!=null){
                            String mName=mInfo.getName();
                            if(mName!=null && mName.equals(devName)){
                                selDeviceInfo=di;
                                chooserEnabled=true;
                                break;
                            }
                        }
                        }
                    }
                }
            }
        }else{
            chooserEnabled=true;
        }
        chooser.setSelectedDeviceInfo(selDeviceInfo);
        chooser.setEnabled(chooserEnabled);
        if(!chooserEnabled){
            setSelectedComponent(listChooser);
        }
        setEnabledAt(0, chooserEnabled);
    }
    /**
     * 
     */
    public void stopEditing() {
        listChooser.stopEditing();
    }
    
    
    
    public MixerName[] getSelectedMixerNames() {
       
       return listChooser.getSelectedMixerNames();
    }
    
    
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src=e.getSource();
        if(src==chooser){
            DeviceInfo di=chooser.getSelectedDeviceInfo();
            if(di==null){
                setSelectedMixerNames(new MixerName[0]);
            }else{
                MixerName mn=new MixerName(MixerName.javaClassnameToProviderId(di.getDeviceProviderInfo().getImplementationClassname()), di.getMixerInfo().getName());
                setSelectedMixerNames(new MixerName[]{mn});
            }
        }else if(src==listChooser){
            MixerName[] mns=listChooser.getSelectedMixerNames();
            setSelectedMixerNames(mns);
        }
        
    }
}
