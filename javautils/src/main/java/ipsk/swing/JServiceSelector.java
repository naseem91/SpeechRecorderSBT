//    IPS Java Utils
// 	  (c) Copyright 2009-2011
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

package ipsk.swing;

import ipsk.util.services.ServiceDescriptor;
import ipsk.util.services.ServiceDescriptorsInspector;
import ipsk.util.services.ServicesInspector;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;


/**
 * Presents a selectable list of service implementing classes.
 * @author klausj
 *
 */
public class JServiceSelector<S> extends JPanel implements ActionListener {
  
    
    private JCheckBox useDefault;
    private JTable pluginsList;
    private ServicesTableModel stm=new ServicesTableModel();
    
    private String selectedClassName=null;
    
    /**
     * Simple constructor.
     */
    public JServiceSelector() {
        super(new GridBagLayout());
        createUI();
    }
    
    
    public void setServiceDescriptorList(List<? extends ServiceDescriptor> serviceDescriptorList){
        stm.setServiceDescriptorList(serviceDescriptorList);
        selectClassName();
    }
    
    
    public void setPluginManager(ServicesInspector<S> pluginManager){
        List<ServiceDescriptor> serviceDescriptorList=new ArrayList<ServiceDescriptor>();
        try {
            serviceDescriptorList=pluginManager.getServiceDescriptors();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "I/O Error building plugin list: "+e.getMessage(), "Plugin error", JOptionPane.ERROR_MESSAGE);
        } 
        setServiceDescriptorList(serviceDescriptorList);
    }
    
    /**
     * Create service class selector for given service class.
     * Searches for classes implementing the interface (service) class and fills the list.
     * @param serviceClass the service class
     */
    public JServiceSelector(Class<S> serviceClass) {
        super(new GridBagLayout());
        ServicesInspector<S> pluginManager=new ServicesInspector<S>(serviceClass);
      
//        List<Class<? extends S>> pluginClasses = new ArrayList<Class<? extends S>>();
        List<ServiceDescriptor> serviceDescriptorList=new ArrayList<ServiceDescriptor>();
        try {
//            pluginClasses = pluginManager.getServiceImplementorClasses();
            serviceDescriptorList=pluginManager.getServiceDescriptors();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "I/O Error building plugin list: "+e.getMessage(), "Plugin error", JOptionPane.ERROR_MESSAGE);
        } 
//        catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Class not found: "+e.getMessage(), "Plugin error", JOptionPane.ERROR_MESSAGE);
//           
//        }
//        stm.setPluginClasses(pluginClasses);
        stm.setServiceDescriptorList(serviceDescriptorList);
        createUI();
    }
    
    
//    /**
//     * Create selector for the given service implementation classes.
//     * @param pluginClasses
//     */
//    public JServiceSelector(List<Class<? extends S>> pluginClasses) {
//        super(new GridBagLayout());
//        stm.setPluginClasses(pluginClasses);
//        createUI();
//    }
//        
        
    private void createUI(){
        
        
//        Object[][] tableData=new Object[pluginClasses.size()][];
//        for(int i=0;i<pluginClasses.size();i++){
//            
////            System.out.println("Found StartStopSignal plugin: "+pcn);
//            Class<? extends S> c=pluginClasses.get(i);
//            Package cp=c.getPackage();
//            tableData[i]=new Object[]{c.getName(),cp.getImplementationTitle(),cp.getImplementationVendor(),cp.getImplementationVersion()};
//            
//        }
        
        //uiString = UIResources.getInstance();
        Border loweredbevel = BorderFactory.createLoweredBevelBorder();
        
        GridBagConstraints c = new GridBagConstraints();
        
        JLabel useDefaultLabel=new JLabel("Use default:");
       
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 5, 2, 5);
        c.gridx = 0;
        c.gridy = 0;
        add(useDefaultLabel,c);
        
        useDefault=new JCheckBox();
        useDefault.setSelected(true);
        useDefault.addActionListener(this);
        c.gridx++;
        add(useDefault,c);
        
        
//        pluginsList=new JTable(tableData,new Object[]{"Class","Title","Vendor","Version"});
        pluginsList=new JTable(stm);
        pluginsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pluginsList.setColumnSelectionAllowed(false);
        
        JTableHeader tableHeader=pluginsList.getTableHeader();
        c.gridwidth=2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 5, 2, 5);
        c.gridx = 0;
        c.gridy++;
        
        
//      pluginsList.
        add(tableHeader, c);
        
        c.weightx = 2;
        c.weighty=2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 5, 2, 5);
        c.gridx = 0;
        c.gridy++;
        add(pluginsList,c);
        setDependencies();
        
    }
    
//    /**
//     * Get list of selectable implementation classes.
//     * @return
//     */
//    public List<Class<? extends S>> getPluginClasses() {
//        return stm.getPluginClasses();
//    }
//
//    /**
//     * Set list of selectable implementation classes.
//     * @param pluginClasses
//     */
//    public void setPluginClasses(List<Class<? extends S>> pluginClasses) {
//        stm.setPluginClasses(pluginClasses);
//    }
//    
    
    
    private void selectClassName(){
    	if(this.selectedClassName==null){
            useDefault.setSelected(true);
        }else{
        	useDefault.setSelected(false);
        	List<? extends ServiceDescriptor> pluginClasses=stm.getServiceDescriptorList();
        	if(pluginClasses!=null){
        		for(int i=0;i<pluginClasses.size();i++){
        			String cName=pluginClasses.get(i).getServiceImplementationClassname();
        			if(this.selectedClassName.equals(cName)){
        				pluginsList.getSelectionModel().setSelectionInterval(i,i);
        				break;
        			}
        		}
        	}
        }
        setDependencies();
    }
    
    /**
     * Select implementation class by name.
     * @param classname classname of implementation class
     */
    public void setSelectedClassname(String classname){
        this.selectedClassName=classname;
        selectClassName();
    }
    
    
    /**
     * Get name of selected implementation class. 
     * @return name of selected implementation class
     */
    public String getSelectedClassname(){
        if(!useDefault.isSelected()){
            ListSelectionModel lsm=pluginsList.getSelectionModel();
            if(lsm!=null && ! lsm.isSelectionEmpty()){
                int selInd=lsm.getMinSelectionIndex();
                ServiceDescriptor c=stm.getServiceDescriptorList().get(selInd);
                return c.getServiceImplementationClassname();
            }
        }
        return null;
    }
    

    private void setDependencies() {
    	useDefault.setEnabled(isEnabled());
    	
        boolean tableEnabled=(!useDefault.isSelected());
         pluginsList.setEnabled(tableEnabled && isEnabled()); 
         pluginsList.setRowSelectionAllowed(tableEnabled);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae) {
        Object src = ae.getSource();
        
        setDependencies();
    }
    
   
    public void setEnabled(boolean enabled){
    	super.setEnabled(enabled);
    	setDependencies();
    }
   
    

}
