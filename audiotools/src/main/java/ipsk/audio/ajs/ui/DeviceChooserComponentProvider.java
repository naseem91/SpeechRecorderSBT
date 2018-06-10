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

package ipsk.audio.ajs.ui;

import ipsk.audio.ajs.AJSAudioSystem;
import ipsk.audio.ajs.AJSAudioSystem.DeviceType;
import ipsk.audio.ajs.AJSDevice;
import ipsk.audio.ajs.AJSDeviceInfo;
import ipsk.audio.ajs.DeviceSelection;
import ipsk.audio.ajs.MixerProviderServiceDescriptor;
import ipsk.awt.util.gridbaglayout.form.FormGridComponentProvider;
import ipsk.awt.util.gridbaglayout.GridComponentProvider;
import ipsk.awt.util.gridbaglayout.Gridbag;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author klausj
 *
 */
public class DeviceChooserComponentProvider implements FormGridComponentProvider,ActionListener {

	private static final String PROTOTYPE_MIXERNAME="Audio dummy prototype mixer name foo bla (dummy)";
    private DeviceSelection deviceManager;
    private Component parentComponent;
 
    private JComboBox interfaceTypeSelector;
    private JComboBox availDevicesSelector;
    private AJSAudioSystem.DeviceType deviceType=null;
    private Gridbag[][] grid=new Gridbag[2][];
    private ColumnType[] columnTypes=new ColumnType[]{ColumnType.LABEL,ColumnType.VALUE};

    private Vector<InterfaceElement> interfaces;
    
    public class DeviceView{
        private Mixer.Info mixerInfo;
        public DeviceView(Mixer.Info mInfo){
            this.mixerInfo=mInfo;
        }
        
        public String toString(){
            if(mixerInfo==null){
                return "(Default device)";
            }else{
                return mixerInfo.getName();
            }
        }

        public Mixer.Info getMixerInfo() {
            return mixerInfo;
        }
        public boolean equals(Object o){
            if(o==this)return true;
            if(o instanceof DeviceView){
                DeviceView oDv=(DeviceView)o;
               Mixer.Info oMi=oDv.getMixerInfo();
               if(oMi!=null){
                   if(oMi.equals(mixerInfo)){
                       return true;
                   }
               }else{
                   if(mixerInfo==null){
                       return true;
                   }
               }
                   
            }
            return false;
        }
    }
//    private Vector<Mixer.Info> devicesList=new Vector<Mixer.Info>();
    public class InterfaceElement {
        private MixerProviderServiceDescriptor mpsd;
        InterfaceElement(MixerProviderServiceDescriptor mpsd){
            super();
            this.mpsd=mpsd;
        }
        public String toString(){
            return mpsd.getAudioInterfaceName();
        }
        public boolean equals(Object o){
            if(o==this)return true;
            if(o instanceof InterfaceElement){
                InterfaceElement oIe=(InterfaceElement)o;
                if(mpsd.equals(oIe.getMpsd())){
                    return true;
                }
                   
            }
            return false;
        }
        public MixerProviderServiceDescriptor getMpsd() {
            return mpsd;
        }
        
    }
   
    public DeviceChooserComponentProvider(DeviceSelection deviceManager){
        super();
        this.deviceManager=deviceManager;
        if(deviceManager!=null){
            this.deviceType=deviceManager.getDeviceType();
        }
        List<MixerProviderServiceDescriptor> mpsdList=AJSAudioSystem.listMixerProviderDescriptors();
        
        int mpsdCount=mpsdList.size();
       
        interfaces = new Vector<InterfaceElement>();
        for (int i=0;i<mpsdCount;i++){
            MixerProviderServiceDescriptor mpsd=mpsdList.get(i);
            if(deviceType==null || 
                    (DeviceType.CAPTURE.equals(deviceType) && mpsd.isProvidesCaptureDevices()) ||
                    (DeviceType.PLAYBACK.equals(deviceType) && mpsd.isProvidesPlaybackDevices())){
                //            String interfaceName=mpsd.getAudioInterfaceName();
                InterfaceElement ie=new InterfaceElement(mpsd);
                interfaces.add(ie);
            }
        }
        grid[0]=new Gridbag[2];
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(1, 1, 1, 1);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor=GridBagConstraints.EAST;
        JLabel interfaceTypeLabel = new JLabel("Interface:");
        grid[0][0]=new Gridbag(interfaceTypeLabel,c);
       
        c.gridx++;
        c.anchor=GridBagConstraints.WEST;
        interfaceTypeSelector=new JComboBox(interfaces);
        if(interfaces.size()<=1){
            interfaceTypeSelector.setEnabled(false);
        }
        interfaceTypeSelector.addActionListener(this);
        grid[0][1]=new Gridbag(interfaceTypeSelector,c);
        
        grid[1]=new Gridbag[2];
        c.gridx=0;
        c.gridy++;
        c.anchor=GridBagConstraints.EAST;
        JLabel deviceLabel = new JLabel("Device:");
        grid[1][0]=new Gridbag(deviceLabel,c);
        availDevicesSelector=new JComboBox();
        availDevicesSelector.setPrototypeDisplayValue(PROTOTYPE_MIXERNAME);
        c.gridx++;
        c.anchor=GridBagConstraints.WEST;
        grid[1][1]=new Gridbag(availDevicesSelector,c);
        
        AJSDevice selDevice=null;
        if(deviceManager!=null){
            selDevice=deviceManager.getDevice();
            if(selDevice!=null){
                MixerProviderServiceDescriptor mpsd=selDevice.getMixerproviderServiceDescriptor();
                InterfaceElement ie=new InterfaceElement(mpsd);
                interfaceTypeSelector.setSelectedItem(ie);
                boolean hasDevices=loadDeviceList(ie);
               
                Mixer m=selDevice.getMixer();
                Mixer.Info mInfo=null;
                if(m!=null){
                    mInfo=m.getMixerInfo();
                }
                availDevicesSelector.setSelectedItem(new DeviceView(mInfo));
            }
        }
        if(selDevice==null){
        int ii=0;
        int interfacesCount=interfaces.size();
        boolean hasDevices=false;
        while(!hasDevices && ii<interfacesCount){
            InterfaceElement ie=interfaces.get(ii);
            hasDevices=loadDeviceList(ie);
            if(hasDevices){
                interfaceTypeSelector.setSelectedItem(ie);
                break;
            }
            ii++;
        }
        }
        
    }
   


    private boolean loadDeviceList(InterfaceElement ie){
        boolean hasMatchingDevices=false;
        MixerProviderServiceDescriptor mpsd=ie.getMpsd();
//        String siClassname=mpsd.getServiceImplementationClassname();
//        try {
//            Class<?> siClass=Class.forName(siClassname);
//            Object siObj=siClass.newInstance();
            MixerProvider mp=AJSAudioSystem.getMixerProvider(mpsd);
            availDevicesSelector.removeAllItems();
           if(mp!=null){
//            if(siObj instanceof MixerProvider){
                List<Mixer.Info> mixerInfosList=null;
//                MixerProvider mp=(MixerProvider)siObj;
                if(deviceType==null){
                    Mixer.Info[] miInfos=mp.getMixerInfo();
                    mixerInfosList=Arrays.asList(miInfos);
                }else if(AJSAudioSystem.DeviceType.CAPTURE.equals(deviceType)){
                    mixerInfosList=AJSAudioSystem.availableCaptureMixerInfos(mpsd);
                }else if(AJSAudioSystem.DeviceType.PLAYBACK.equals(deviceType)){
                    mixerInfosList=AJSAudioSystem.availablePlaybackMixerInfos(mpsd);
                }
                if(mixerInfosList!=null && mixerInfosList.size()>0){
                    hasMatchingDevices=true;
                    availDevicesSelector.setEnabled(true);
                    DeviceView defDv=new DeviceView(null);
                    
                    availDevicesSelector.addItem(defDv);
                    for(Mixer.Info mi:mixerInfosList){
                        
                        DeviceView mDv=new DeviceView(mi);
                        availDevicesSelector.addItem(mDv);
                    }
                }else{
                    availDevicesSelector.setEnabled(false);
                }
            }
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        return hasMatchingDevices;

    }
    
    
    public AJSDeviceInfo getSelectedDeviceInfo(){
        InterfaceElement ie=(InterfaceElement)interfaceTypeSelector.getSelectedItem();
        MixerProviderServiceDescriptor mpsd=ie.getMpsd();
        DeviceView dv=(DeviceView)availDevicesSelector.getSelectedItem();
        if(dv==null){
            return null;
        }
        Mixer.Info mixerInfo=dv.getMixerInfo();
        AJSDeviceInfo di=new AJSDeviceInfo(mpsd, mixerInfo);
        return di;
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
       Object evSrc=ae.getSource();
       if(evSrc==interfaceTypeSelector){
           InterfaceElement ie=(InterfaceElement)(interfaceTypeSelector.getSelectedItem());
           MixerProviderServiceDescriptor mpsd=ie.getMpsd();
           boolean hasDevices=loadDeviceList(ie);
           if(!hasDevices){
               JOptionPane.showMessageDialog(parentComponent, "\""+mpsd.getAudioInterfaceName()+"\" provider provides no devices.", "Audio device warning", JOptionPane.WARNING_MESSAGE);
              
           }
       }
    }





    /* (non-Javadoc)
     * @see ipsk.awt.util.gridbaglayout.GridComponentProvider#getGrid()
     */
    public Gridbag[][] getGrid() {
        return grid;
    }





    /* (non-Javadoc)
     * @see ipsk.awt.util.gridbaglayout.form.FormGridComponentProvider#getColumnTypes()
     */
    public ColumnType[] getColumnTypes() {
      return columnTypes;
    }



    /* (non-Javadoc)
     * @see ipsk.awt.util.gridbaglayout.GridComponentProvider#setParentComponent(java.awt.Component)
     */
    public void setParentComponent(Component parentComponent) {
        this.parentComponent=parentComponent;
        
    }
    
    

}

