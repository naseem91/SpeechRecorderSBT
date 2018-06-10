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

import java.util.List;

import javax.swing.table.AbstractTableModel;


/**
 * Table model of list of service implementation classes.
 * @author klausj
 *
 */
public class ServicesTableModel extends AbstractTableModel {
  
    
    public static final String[] COL_NAMES=new String[]{"Class","Title","Vendor","Version"};
    
//    private List<Class<? extends S>> pluginClasses;
    private List<? extends ServiceDescriptor> serviceDescriptorList;
    
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int col){
       return COL_NAMES[col];
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
       
        return COL_NAMES.length;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if(serviceDescriptorList!=null){
            
            return serviceDescriptorList.size();
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
        if(serviceDescriptorList!=null){
//            Class<? extends S> c;
            ServiceDescriptor sd;
            synchronized (serviceDescriptorList) {
                int size=serviceDescriptorList.size();
                if(row>=size){
                    return null;
                }

                sd=serviceDescriptorList.get(row);
            }
            
//            Package cp=sd.getPackage();
            if(col==0){
                return sd.getServiceImplementationClassname();
            }else if(col==1){
//                Title titleAnno=c.getAnnotation(Title.class);
//                if(titleAnno!=null){
//                    return titleAnno.value();
//                }else{
//                    return cp.getImplementationTitle();
//                }
                return sd.getTitle();
            } else if (col==2){
//                Vendor vendorAnno=c.getAnnotation(Vendor.class);
//                if(vendorAnno!=null){
//                    return vendorAnno.value();
//                }
//                return cp.getImplementationVendor();
                return sd.getVendor();
            }else if(col==3){
//               
//                ipsk.util.services.Version versionAnno=c.getAnnotation(ipsk.util.services.Version.class);
//                if(versionAnno!=null){
//                    return versionAnno.major()+"."+versionAnno.minor()+"."+versionAnno.subminor();
//                }
//                return cp.getImplementationVersion();
                return sd.getImplementationVersion();
            }
        }
        return null;
    }

    public List<? extends ServiceDescriptor> getServiceDescriptorList() {
        return serviceDescriptorList;
    }

    public void setServiceDescriptorList(
            List<? extends ServiceDescriptor> serviceDescriptorList) {
        this.serviceDescriptorList = serviceDescriptorList;
        fireTableDataChanged();
    }

//    /**
//     * Get list of plugin (service implementation) classes
//     * @return list of plugin classes
//     */
//    public List<Class<? extends S>> getPluginClasses() {
//        return pluginClasses;
//    }
//
//    /**
//     * Set list of plugin (service implementation) classes
//     * @param pluginClasses list of plugin classes
//     */
//    public void setPluginClasses(List<Class<? extends S>> pluginClasses) {
//        this.pluginClasses = pluginClasses;
//        fireTableDataChanged();
//    }
//    
   

}
