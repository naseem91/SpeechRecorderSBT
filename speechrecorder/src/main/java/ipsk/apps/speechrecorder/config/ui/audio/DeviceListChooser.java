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
import ipsk.apps.speechrecorder.config.ui.AudioDevicesTableModel;
import ipsk.audio.AudioController2;
import ipsk.audio.AudioControllerException;
import ipsk.audio.DeviceInfo;
import ipsk.audio.DeviceProvider;
import ipsk.audio.DeviceProviderInfo;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.sound.sampled.Mixer;
import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * UI to choose multiple audio devices.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class DeviceListChooser extends JPanel implements ActionListener,
        ListSelectionListener, TableModelListener {

    /**
     * 
     */
    private static final long serialVersionUID = -389739661431600386L;
    
    private static final String PROTOTYPE_MIXERNAME="Audio dummy prototype mixer name foo bla (dummy)";
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
        private DeviceProviderInfo mpsd;
        InterfaceElement(DeviceProviderInfo mpsd){
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
        public DeviceProviderInfo getMpsd() {
            return mpsd;
        }
        
    }
   
//    private JComboBox deviceSelector;
   
    private JButton addButt;

    private JButton removeButt;

    private JComboBox interfaceTypeSelector;
    private JComboBox availDevicesSelector;
    

    private Vector<java.awt.event.ActionListener> listeners;

    private AudioController2 controller;

    private AudioDevicesTableModel selectedDevicesTableModel;
    private JTable selectedDevicesTable;

    private ipsk.audio.AudioController2.DeviceType deviceType;

//    private JCheckBox expertViewCheckBox;

//    private DeviceProviderInfo defaultDeviceProviderInfo;

    private JScrollPane listScrollPane;
    
    private boolean adjusting=false;
    
    public DeviceListChooser(AudioController2 controller,AudioController2.DeviceType deviceType) {
        this(controller,deviceType,new MixerName[0]);
    }
    /**
     * Create device chooser.
     * 
     * @param controller
     *            audio controller
     * @param deviceType
     *            device type
     */
    public DeviceListChooser(AudioController2 controller,AudioController2.DeviceType deviceType,MixerName[] selMixerNames) {
        super();
        this.controller=controller;
        this.deviceType=deviceType;
        List<? extends DeviceProviderInfo> providerInfos=null;
        try {
            providerInfos = controller.getDeviceProviderInfos();
        } catch (AudioControllerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        int mpsdCount=providerInfos.size();
        if(mpsdCount<1)return; 
//        defaultDeviceProviderInfo = providerInfos.get(0);
        
//        DeviceProvider defDp=null;
//        try {
//            defDp = controller.getInstance(defaultDeviceProviderInfo);
//        } catch (AudioControllerException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return;
//        }
//        List<? extends DeviceInfo> dis=null;
//        if(ipsk.audio.AudioController2.DeviceType.CAPTURE.equals(deviceType)){
//            dis=defDp.getCaptureDeviceInfos();
//        }else if(ipsk.audio.AudioController2.DeviceType.PLAYBACK.equals(deviceType)){
//            dis=defDp.getPlaybackDeviceInfos();
//        }
//        deviceSelector=new JComboBox();
//        deviceSelector.addItem(new DeviceView(null));
//        if(dis!=null && dis.size()>0){
//           
//            for(DeviceInfo di:dis){
//                Mixer.Info mInfo=di.getMixerInfo();
//                DeviceView dv=new DeviceView(mInfo);
//                deviceSelector.addItem(dv);
//            }
//        }
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(1, 1, 1, 1);
        c.gridx = 0;
        c.gridy = 0;
        
//        add(new JLabel("Select device:"),c);
//        c.gridx++;
//        add(deviceSelector,c);
//        
//        c.gridy++;
//        c.gridx=0;
//        add(new JLabel("Expert view:"),c);
//        c.gridx++;
//        expertViewCheckBox = new JCheckBox();
//        expertViewCheckBox.setSelected(false);
//        expertViewCheckBox.addActionListener(this);
//        add(expertViewCheckBox,c);
        
        
//        this.availMixers=availMixers;
        selectedDevicesTableModel=new AudioDevicesTableModel();
        if(selMixerNames!=null){
           for(MixerName mn:selMixerNames){
               selectedDevicesTableModel.add(mn);
           }
           
        }
        selectedDevicesTableModel.addTableModelListener(this);
        selectedDevicesTable=new JTable(selectedDevicesTableModel);
        selectedDevicesTable.setPreferredScrollableViewportSize(new Dimension(100,100));
        
        
        listeners = new Vector<java.awt.event.ActionListener>();
        
       
        Vector<InterfaceElement> interfaces=new Vector<InterfaceElement>();
        for (int i=0;i<mpsdCount;i++){
            DeviceProviderInfo mpsd=providerInfos.get(i);
            if((ipsk.audio.AudioController2.DeviceType.CAPTURE.equals(deviceType) &&
                    mpsd.isProvidesCaptureDevices()) || 
                    (ipsk.audio.AudioController2.DeviceType.PLAYBACK.equals(deviceType) &&
                    mpsd.isProvidesPlaybackDevices())){
//                String interfaceName=mpsd.getAudioInterfaceName();
                InterfaceElement ie=new InterfaceElement(mpsd);
                interfaces.add(ie);
            }
        }
       
        c.gridx=0;
        c.gridy++;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 2.0;
        JLabel listedMixersLabel = new JLabel("Allowed devices:");
        add(listedMixersLabel, c);
        c.gridy++;
        c.weighty = 2.0;
      
//        // giving the list a prototype fixed the problem that the list was
//        // collapsing on revalidate when it contained one or more entries.
//        MixerName prototypeDummyMixerName=new MixerName(PROTOTYPE_MIXERNAME);

        selectedDevicesTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        selectedDevicesTable.getSelectionModel().addListSelectionListener(this);
        selectedDevicesTable.setDragEnabled(true);
        
        listScrollPane = new JScrollPane(selectedDevicesTable);
        add(listScrollPane, c);
        c.weighty = 0.0;
        c.gridy++;
       
        JPanel buttPanel = new JPanel() {
            /**
             * 
             */
            private static final long serialVersionUID = 8969034884681272616L;

            public void setEnabled(boolean enabled) {
                for (Component child : getComponents()) {
                    child.setEnabled(enabled);
                }
            }
        };

        addButt = new JButton("Add");
        addButt.addActionListener(this);
        buttPanel.add(addButt);
        // c.gridx=0;
        // add(addButt,c);
        removeButt = new JButton("Remove");
        removeButt.addActionListener(this);
        buttPanel.add(removeButt);
        // c.gridx++;
        // add(removeButt,c);
        add(buttPanel, c);
        
        c.gridwidth = 1;
       
        c.gridx=0;
        c.gridy++;
        c.weightx=0.0;
        c.fill=GridBagConstraints.NONE;
        JLabel interfaceTypeLabel = new JLabel("Interface:");
        add(interfaceTypeLabel,c);

        c.gridx++;
        c.weightx=2.0;
        c.anchor=GridBagConstraints.WEST;
        c.fill=GridBagConstraints.HORIZONTAL;
        interfaceTypeSelector=new JComboBox(interfaces);
        interfaceTypeSelector.addActionListener(this);
        add(interfaceTypeSelector,c);
        
        
        c.gridx=0;
        c.gridy++;
        c.weightx=0.0;
        c.fill=GridBagConstraints.NONE;
        c.anchor=GridBagConstraints.EAST;
        JLabel deviceLabel = new JLabel("Device:");
        add(deviceLabel,c);
        availDevicesSelector=new JComboBox();
        availDevicesSelector.addActionListener(this);
        availDevicesSelector.setPrototypeDisplayValue(PROTOTYPE_MIXERNAME);
        c.gridx++;
        c.weightx=2.0;
        c.anchor=GridBagConstraints.WEST;
        c.fill=GridBagConstraints.HORIZONTAL;
        add(availDevicesSelector,c);
        if(mpsdCount>0){
            InterfaceElement ie=(InterfaceElement)interfaceTypeSelector.getSelectedItem();
            loadDeviceList(ie);
        }
        
        setSelectedMixerNames(selMixerNames);
        
        updateEnabledActions();
    }
    
    
    public void stopEditing(){
        CellEditor ce=selectedDevicesTable.getCellEditor();
        if(ce!=null){
            ce.stopCellEditing();
        }
    }
    
    
    private boolean loadDeviceList(InterfaceElement ie){
        boolean hasMatchingDevices=false;
       
        availDevicesSelector.removeAllItems();
        availDevicesSelector.setEnabled(false);
        DeviceProviderInfo dpi=ie.getMpsd();
        DeviceProvider dp=null;
        try {
            dp = controller.getInstance(dpi);
            List<? extends DeviceInfo> dis=null;
            if(ipsk.audio.AudioController2.DeviceType.CAPTURE.equals(deviceType)){
                dis=dp.getCaptureDeviceInfos();
            }else if(ipsk.audio.AudioController2.DeviceType.PLAYBACK.equals(deviceType)){
                dis=dp.getPlaybackDeviceInfos();
            }
            if(dis!=null && dis.size()>0){
                hasMatchingDevices=true;
                availDevicesSelector.setEnabled(true);
                for(DeviceInfo di:dis){
                    Mixer.Info mInfo=di.getMixerInfo();
                    DeviceView dv=new DeviceView(mInfo);
                    availDevicesSelector.addItem(dv);
                }
            }
        } catch (AudioControllerException e) {
            hasMatchingDevices=false;
        }

        return hasMatchingDevices;

    }

  
    public void setSelectedMixerNames(MixerName[] selectedMixerNames){
        adjusting=true;
        selectedDevicesTableModel.clear();
        if (selectedMixerNames != null) {
            
            for (MixerName sm : selectedMixerNames) {
                selectedDevicesTableModel.add(sm);
            }
        }
        adjusting=false;
        updateEnabledActions();
    }
    
    public MixerName[] getSelectedMixerNames(){
        List<MixerName> selMnList=selectedDevicesTableModel.getDeviceNames();
        return selMnList.toArray(new MixerName[0]);
    }



    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        Component[] children = getComponents();
        for (Component ch : children) {
            ch.setEnabled(enabled);
        }
        selectedDevicesTable.setEnabled(enabled);
        if (enabled) {
            updateEnabledActions();
        }
    }

    private MixerName getSelected(){
        InterfaceElement selInterfaceE=(InterfaceElement) interfaceTypeSelector.getSelectedItem();
//        String interfaceName=selInterfaceE.getMpsd().getAudioInterfaceName();
        DeviceProviderInfo dpi=selInterfaceE.getMpsd();
        String pClname=dpi.getImplementationClassname();
        String providerId=MixerName.javaClassnameToProviderId(pClname);
        String interfaceName=dpi.getAudioInterfaceName();
        DeviceView dv=(DeviceView) availDevicesSelector.getSelectedItem();
        if(dv==null) return null;
        String deviceName=dv.getMixerInfo().getName();
        MixerName mn=new MixerName(providerId,interfaceName,deviceName);
        return mn;
    }
    
    
//    private boolean simpleViewPossible(){
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
//         return false;
//        
//    }
     
    
    private void updateEnabledActions() {
       
//        boolean simpleViewPossible=simpleViewPossible();
//        if(!simpleViewPossible){
//            expertViewCheckBox.setEnabled(false);
//            expertViewCheckBox.setSelected(true);
//        }
//        boolean expertView=expertViewCheckBox.isSelected();
        boolean enabled=isEnabled();
//        if (enabled) {
            
//            deviceSelector.setEnabled(!expertView);
           
//            if(availMixers==null || availMixers.length==0){
//                list.setEnabled(false);
//            }
            boolean addAble=false;
           MixerName mn=getSelected();
            if(mn!=null){
                addAble=!selectedDevicesTableModel.contains(mn);
            }
            addButt.setEnabled(addAble && enabled);
            
            if (selectedDevicesTable.getSelectionModel().isSelectionEmpty()) {
                removeButt.setEnabled(false);
            } else {
                removeButt.setEnabled(enabled);
            }
            
            selectedDevicesTable.setEnabled(enabled);
            interfaceTypeSelector.setEnabled(enabled);
            availDevicesSelector.setEnabled(enabled);
            listScrollPane.setEnabled(enabled);
            
            
//        }
    }

 

    public void actionPerformed(ActionEvent arg0) {
        Object src = arg0.getSource();
        if(src==interfaceTypeSelector){
            InterfaceElement ie=(InterfaceElement)(interfaceTypeSelector.getSelectedItem());
            loadDeviceList(ie);
        }else if (src == addButt) {
//            String selInfo = ((Mixer.Info) availBox.getSelectedItem())
//                    .getName();
//            MixerName rmn=new MixerName(selInfo);
//            if (!listData.contains(rmn)) {
//                listData.addElement(rmn);
//                // list.setListData(listData);
//
//                // list.setVisibleRowCount(3);
//                fireAction();
//            }
            MixerName mn=getSelected();
            selectedDevicesTableModel.add(mn);
            fireAction();
        } else if (src == removeButt) {
//            Object[] selMixers = list.getSelectedValues();
//            for (Object selMixer : selMixers) {
//                listData.removeElement(selMixer);
//            }
            // list.setListData(listData);
            // list.setVisibleRowCount(3);
            int selRow=selectedDevicesTable.getSelectedRow();
            if(selRow>=0){
                selectedDevicesTableModel.remove(selRow);
            }
            fireAction();
        } 
//            else if (src == availBox) {
//            updateInfoFields();
//        }
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
                            "device_chooser_list_changed"));
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
    /* (non-Javadoc)
     * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
     */
    public void tableChanged(TableModelEvent arg0) {
       if(!adjusting){
           fireAction();
       }
    }

   

}
