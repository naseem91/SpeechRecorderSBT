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

import ipsk.audio.AudioController2;
import ipsk.audio.AudioControllerException;
import ipsk.audio.DeviceInfo;
import ipsk.audio.DeviceProvider;
import ipsk.audio.DeviceProviderInfo;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.sound.sampled.Mixer;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * UI to choose multiple audio devices.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class DeviceChooser extends JPanel implements ActionListener,
        ListSelectionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -389739661431600386L;
    
//    private static final String PROTOTYPE_MIXERNAME="Audio dummy prototype mixer name foo bla (dummy)";
    public class DeviceView{
        private DeviceInfo deviceInfo;
        
        public DeviceView(){
            this(null);
        }
       
        public DeviceView(DeviceInfo deviceInfo){
            super();
            this.deviceInfo=deviceInfo;
        }
        
        public String toString(){
            if(deviceInfo==null){
                return "(Default device)";
            }else{
                Mixer.Info mInfo=deviceInfo.getMixerInfo();
                if(mInfo!=null){
                    return mInfo.getName();
                }
                return "";
            }
        }

        public Mixer.Info getMixerInfo() {
            Mixer.Info mInfo=null;
            if(deviceInfo!=null){
                mInfo=deviceInfo.getMixerInfo();
            }
            return mInfo;
        }
        public boolean equals(Object o){
            if(o==this)return true;
            if(o instanceof DeviceView){
                
                DeviceView oDv=(DeviceView)o;
                DeviceProviderInfo oDpi=oDv.getDeviceProviderInfo();
                if(oDpi!=null){
                    if(!oDpi.equals(getDeviceProviderInfo())){
                        return false;
                    }
                }else{
                    if(getDeviceProviderInfo()!=null){
                        return false;
                    }
                }
               Mixer.Info oMi=oDv.getMixerInfo();
               if(oMi!=null){
                   if(oMi.equals(getMixerInfo())){
                       return true;
                   }
               }else{
                   if(getMixerInfo()==null){
                       return true;
                   }
               }
                   
            }
            return false;
        }

        public DeviceProviderInfo getDeviceProviderInfo() {
            DeviceProviderInfo deviceProviderInfo=null;
            if(deviceInfo!=null){
                deviceProviderInfo=deviceInfo.getDeviceProviderInfo();
            }
            return deviceProviderInfo;
        }

        public DeviceInfo getDeviceInfo() {
            return deviceInfo;
        }
    }

   
    private JComboBox deviceSelector;
   
 
    private Vector<java.awt.event.ActionListener> listeners=new Vector<ActionListener>();

//    private AudioController2 controller;

  
//    private ipsk.audio.AudioController2.DeviceType deviceType;


    private DeviceProviderInfo defaultDeviceProviderInfo;

    private boolean fireActionEvents=true;
  
    /**
     * Create device chooser.
     * 
     * @param controller
     *            audio controller
     * @param deviceType
     *            device type
     */
    public DeviceChooser(AudioController2 controller,AudioController2.DeviceType deviceType) {
        super();
//        this.controller=controller;
//        this.deviceType=deviceType;
        
        deviceSelector=new JComboBox();
        deviceSelector.addActionListener(this);
        deviceSelector.addItem(new DeviceView());
        
        List<? extends DeviceProviderInfo> providerInfos=null;
        try {
            providerInfos = controller.getDeviceProviderInfos();
            
        } catch (AudioControllerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
      
        for(DeviceProviderInfo dpi:providerInfos){
            if(AudioController2.DeviceType.CAPTURE.equals(deviceType)){
                if(dpi.isProvidesCaptureDevices()){
                    defaultDeviceProviderInfo=dpi;
                    break;
                }
                
            }else if(AudioController2.DeviceType.PLAYBACK.equals(deviceType)){
                if(dpi.isProvidesPlaybackDevices()){
                    defaultDeviceProviderInfo=dpi;
                    break;
                }
            }
        }
        if(defaultDeviceProviderInfo!=null){     
            DeviceProvider defDp=null;
            try {
                defDp = controller.getInstance(defaultDeviceProviderInfo);
            } catch (AudioControllerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
            List<? extends DeviceInfo> dis=null;
            if(ipsk.audio.AudioController2.DeviceType.CAPTURE.equals(deviceType)){
                dis=defDp.getCaptureDeviceInfos();
            }else if(ipsk.audio.AudioController2.DeviceType.PLAYBACK.equals(deviceType)){
                dis=defDp.getPlaybackDeviceInfos();
            }

            if(dis!=null && dis.size()>0){

                for(DeviceInfo di:dis){
                    
                    DeviceView dv=new DeviceView(di);
                    deviceSelector.addItem(dv);
                }
            }
        }
       
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(1, 1, 1, 1);
        c.gridx = 0;
        c.gridy = 0;
        
        add(new JLabel("Select device:"),c);
        c.gridx++;
        add(deviceSelector,c);
        
       
        
        updateEnabledActions();
    }
    
    
    public List<DeviceInfo> selectableDeviceInfos(){
        ComboBoxModel cbm=deviceSelector.getModel();
        int cbmSize=cbm.getSize();
        List<DeviceInfo> dis=new ArrayList<DeviceInfo>();
       
        for(int i=0;i<cbmSize;i++){
            DeviceView idv=(DeviceView)cbm.getElementAt(i);
            dis.add(idv.getDeviceInfo());
        }
        return dis;
    }
    
    
 
  
    public void setSelectedDeviceInfo(DeviceInfo deviceInfo){
        fireActionEvents=false;
        ComboBoxModel cbm=deviceSelector.getModel();
        int cbmSize=cbm.getSize();
        DeviceView reqSel=new DeviceView(deviceInfo);
        DeviceView sel=null;
        for(int i=0;i<cbmSize;i++){
            DeviceView idv=(DeviceView)cbm.getElementAt(i);
            if(reqSel.equals(idv)){
                sel=reqSel;
                break;
            }
        }
        if(sel==null){
            deviceSelector.setEnabled(false);
        }else{
            deviceSelector.setSelectedItem(sel);
        }
        fireActionEvents=true;
    }
    
   
    
    public DeviceInfo getSelectedDeviceInfo(){
       
        DeviceView dv=(DeviceView)deviceSelector.getSelectedItem();
       return dv.getDeviceInfo();
    }



    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Component[] children = getComponents();
        for (Component ch : children) {
            ch.setEnabled(enabled);
        }
        
        if (enabled) {
            updateEnabledActions();
        }
    }

   
    
    private boolean simpleViewPossible(){
//        List<MixerName> mns=selectedDevicesTableModel.getDeviceNames();
//        if(mns.size()==0){
//            return true;
//        }else if(mns.size()==1){
//            MixerName mn=mns.get(0);
////            defaultDeviceProviderInfo.
//            String cn=mn.providerIdAsJavaClassName();
//            if(cn!=null && cn.equals(defaultDeviceProviderInfo.getImplementationClassname())){
//                return true;
//            }
//        }
         return false;
        
    }
     
    
    private void updateEnabledActions() {
       
        boolean simpleViewPossible=simpleViewPossible();
        if(!simpleViewPossible){
//            expertViewCheckBox.setEnabled(false);
//            expertViewCheckBox.setSelected(true);
        }
//        boolean expertView=expertViewCheckBox.isSelected();
        if (isEnabled()) {
            
//            deviceSelector.setEnabled(!expertView);
           
//            if(availMixers==null || availMixers.length==0){
//                list.setEnabled(false);
//            }
//            boolean addAble=false;
//           MixerName mn=getSelected();
//            if(mn!=null){
//                addAble=!selectedDevicesTableModel.contains(mn);
//            }
//            addButt.setEnabled(addAble && expertView);
//            
//            if (selectedDevicesTable.getSelectionModel().isSelectionEmpty()) {
//                removeButt.setEnabled(false);
//            } else {
//                removeButt.setEnabled(expertView);
//            }
//            
//            selectedDevicesTable.setVisible(expertView);
//            interfaceTypeSelector.setVisible(expertView);
//            availDevicesSelector.setEnabled(expertView);
//            listScrollPane.setVisible(expertView);
//            
            
        }
    }

 

    public void actionPerformed(ActionEvent arg0) {
        Object src = arg0.getSource();
        if(fireActionEvents && src==deviceSelector){
            fireAction();
        }
        updateEnabledActions();
    }

    public void valueChanged(ListSelectionEvent arg0) {
        updateEnabledActions();
    }

    protected synchronized void fireAction() {
        for (ActionListener listener : listeners) {
            listener
                    .actionPerformed(new ActionEvent(this,
                            ActionEvent.ACTION_PERFORMED,
                            "device_chooser_changed"));
        }
    }

    /**
     * Add action listener.
     * 
     * @param actionListener
     */
    public synchronized void addActionListener(ActionListener actionListener) {
        if (actionListener != null && !listeners.contains(actionListener)) {
            listeners.addElement(actionListener);
        }
    }

    /**
     * Remove action listener.
     * 
     * @param actionListener
     */
    public synchronized void removeActionListener(ActionListener actionListener) {
        if (actionListener != null) {
            listeners.removeElement(actionListener);
        }
    }







    public DeviceProviderInfo getDefaultDeviceProviderInfo() {
        return defaultDeviceProviderInfo;
    }

   

}
