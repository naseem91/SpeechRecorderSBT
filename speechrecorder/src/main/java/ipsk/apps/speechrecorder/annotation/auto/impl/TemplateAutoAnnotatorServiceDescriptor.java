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



package ipsk.apps.speechrecorder.annotation.auto.impl;

import java.util.ArrayList;
import java.util.List;

import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ips.annot.model.PredefinedLevelDefinition;
import ipsk.text.Version;
import ipsk.util.LocalizableMessage;

/**
 * @author klausj
 *
 */
public class TemplateAutoAnnotatorServiceDescriptor implements AutoAnnotationServiceDescriptor{

	private List<PredefinedLevelDefinition> depends;
	private List<PredefinedLevelDefinition> provides;
	
    /**
     * 
     */
    public TemplateAutoAnnotatorServiceDescriptor() {
        super();
        depends= new ArrayList<PredefinedLevelDefinition>(0);
        provides=new ArrayList<PredefinedLevelDefinition>(1);
        provides.add(PredefinedLevelDefinition.TPL);
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getServiceImplementationClassname()
     */
    @Override
    public String getServiceImplementationClassname() {
        return "ipsk.apps.speechrecorder.annotation.auto.impl.TemplateAutoAnnotator";
    }

    /* (non-Javadoc)
     * @see ipsk.util.InterfaceInfo#getTitle()
     */
    @Override
    public LocalizableMessage getTitle() {
        return new LocalizableMessage("Prompt template auto annotator");
    }

    /* (non-Javadoc)
     * @see ipsk.util.InterfaceInfo#getDescription()
     */
    @Override
    public LocalizableMessage getDescription() {
        return new LocalizableMessage("Annotates prompt template text.");
    }

    /* (non-Javadoc)
     * @see ipsk.util.InterfaceInfo#getVendor()
     */
    @Override
    public String getVendor() {
        return "Institute of Phonetics and Speech processing, Munich";
    }

    /* (non-Javadoc)
     * @see ipsk.util.InterfaceInfo#getSpecificationVersion()
     */
    @Override
    public Version getSpecificationVersion() {
       return new Version(new int[]{0,1,0});
    }

    /* (non-Javadoc)
     * @see ipsk.util.InterfaceInfo#getImplementationVersion()
     */
    @Override
    public Version getImplementationVersion() {
        return new Version(new int[]{0,0,1});
    }
    
    @Override
    public List<PredefinedLevelDefinition> getDependencies(){
    	return depends;
    }

   
    @Override
    public List<PredefinedLevelDefinition> getProvides(){
        return provides;
    }

}
