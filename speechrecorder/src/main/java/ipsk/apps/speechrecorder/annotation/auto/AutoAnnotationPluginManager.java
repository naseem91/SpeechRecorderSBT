//    Speechrecorder
// 	  (c) Copyright 2014
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



package ipsk.apps.speechrecorder.annotation.auto;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotator;
import ips.annot.model.PredefinedLevelDefinition;
import ipsk.util.dependency.DependencyResolver;
import ipsk.util.services.ServiceDescriptorsInspector;

/**
 * @author klausj
 *
 */
public class AutoAnnotationPluginManager extends ServiceDescriptorsInspector<AutoAnnotationServiceDescriptor,AutoAnnotator> {

	private DependencyResolver<AutoAnnotationServiceDescriptor,PredefinedLevelDefinition> dependencyResolver;
	private List<AutoAnnotationServiceDescriptor> autoAnnotatorServiceDescriptors=null;

    /**
	 * @return the autoAnnotatorServiceDescriptors
	 */
	public List<AutoAnnotationServiceDescriptor> getAutoAnnotatorServiceDescriptors() {
		if(autoAnnotatorServiceDescriptors==null){
			try {
				autoAnnotatorServiceDescriptors=getTypedServiceDescriptors(AutoAnnotationServiceDescriptor.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return autoAnnotatorServiceDescriptors;
	}
	/**
     * @param serviceClass
     */
    public AutoAnnotationPluginManager() {
        super(AutoAnnotator.class);
        dependencyResolver=new DependencyResolver<AutoAnnotationServiceDescriptor,PredefinedLevelDefinition>();
        
    }
    
    public boolean checkDependencies(List<AutoAnnotationServiceDescriptor> activatedPlugins,AutoAnnotationServiceDescriptor aasd){
//    public boolean checkDeps(List<Dependent<PredefinedLevelDefinition>> activatedPlugins,Dependent<PredefinedLevelDefinition> aasd){
//        boolean depsOK=true;
//        List<PredefinedLevelDefinition> requiredLvlDefs=aasd.getDependencies();
//        for(PredefinedLevelDefinition requiredLvlDef:requiredLvlDefs){
//            boolean lvlFound=false;
//            // except implicit Speechrecorder plugins
//            if(PredefinedLevelDefinition.PRT.equals(requiredLvlDef) || PredefinedLevelDefinition.TPL.equals(requiredLvlDef)){
//                lvlFound=true;
//            }else{
//                // check if activated plugins provide this level
//
//                for(AutoAnnotationServiceDescriptor activatedPlugin:activatedPlugins){
//                    if(requiredLvlDef.equals(activatedPlugin)){
//                        // do not compare plugin with itself
//                        continue;
//                    }
//                    List<PredefinedLevelDefinition> availLvlDefs=activatedPlugin.getProvides();
//                    for(PredefinedLevelDefinition availLvldef:availLvlDefs){
//                        if(requiredLvlDef.equals(availLvldef)){
//                            lvlFound=true;
//                            break;
//                        }
//                    }
//                }
//            }
//            if(!lvlFound){
//                return false;
//            }
//        }
//        return depsOK;
    	return dependencyResolver.isResolvable(activatedPlugins,aasd);
    }
    
//    public boolean checkDependencies(List<AutoAnnotationServiceDescriptor> activatedPlugins){
////    	dependencyResolver.resolve(dependents)
//        for(AutoAnnotationServiceDescriptor activatedPlugin:activatedPlugins){
//            boolean depOk=checkDependencies(activatedPlugins,activatedPlugin);
//            if(!depOk){
//                return false;
//            }
//        }
//        return true;
//    }
    
    	public List<AutoAnnotationServiceDescriptor> resolve(Collection<AutoAnnotationServiceDescriptor> aads){
    		return dependencyResolver.resolve(aads);
    	}

}
