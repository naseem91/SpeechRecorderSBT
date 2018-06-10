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


package ipsk.apps.speechrecorder.prompting;

import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter;
import ipsk.util.services.ServiceDescriptor;
import ipsk.util.services.ServiceDescriptorsInspector;

import java.io.IOException;
import java.util.List;

/**
 * @author klausj
 *
 */
public class PromptPresenterPluginManager extends ServiceDescriptorsInspector<PromptPresenterServiceDescriptor,PromptPresenter> {
    List<PromptPresenterServiceDescriptor> serviceDescriptors;
    
    public PromptPresenterPluginManager(){
        super(PromptPresenter.class);
    }
    public List<PromptPresenterServiceDescriptor> getPromptPresenterServiceDescriptors(){
        if(serviceDescriptors==null){
            reload();
        }
        return serviceDescriptors;
    }
    public void reload(){
        try {
            serviceDescriptors=(List<PromptPresenterServiceDescriptor>)getTypedServiceDescriptors(PromptPresenterServiceDescriptor.class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    public boolean isPromptPresenterAvailable(String[][] mimeTypeCombi){
       if(serviceDescriptors==null){
           reload();
       }
       for(ServiceDescriptor sd:serviceDescriptors){
           if(sd instanceof PromptPresenterServiceDescriptor){
               PromptPresenterServiceDescriptor ppsd=(PromptPresenterServiceDescriptor)sd;
               ppsd.getSupportedMIMETypes();
           }
       }
       return false;
    }
    
}
