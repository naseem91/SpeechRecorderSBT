//    IPS Speech database tools
// 	  (c) Copyright 2016
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Speech database tools
//
//
//    IPS Speech database tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Speech database tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Speech database tools.  If not, see <http://www.gnu.org/licenses/>.


package ips.annot.autoannotator.impl.ws.bas.g2p;

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
public class G2PAnnotatorServiceDescriptor implements AutoAnnotationServiceDescriptor{
	
	private List<PredefinedLevelDefinition> depends;
	private List<PredefinedLevelDefinition> provides;
	
    /**
     * 
     */
    public G2PAnnotatorServiceDescriptor() {
        super();
        depends= new ArrayList<PredefinedLevelDefinition>(1);
        depends.add(PredefinedLevelDefinition.TPL);
        provides=new ArrayList<PredefinedLevelDefinition>(2);
        provides.add(PredefinedLevelDefinition.ORT);
        provides.add(PredefinedLevelDefinition.KAN);
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptor#getServiceImplementationClassname()
     */
    @Override
    public String getServiceImplementationClassname() {
        return "ips.annot.autoannotator.impl.ws.bas.g2p.G2PServiceClient";
    }

    /* (non-Javadoc)
     * @see ipsk.util.InterfaceInfo#getTitle()
     */
    @Override
    public LocalizableMessage getTitle() {
        return new LocalizableMessage("Grapheme-phoneme conversion and word stress assignment");
    }

    /* (non-Javadoc)
     * @see ipsk.util.InterfaceInfo#getDescription()
     */
    @Override
    public LocalizableMessage getDescription() {
        return new LocalizableMessage("Language-independent grapheme-phoneme conversion and word stress assignment as a web service");
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

   
    public String[] getLinks() {
        // use the persistent identifier
       return new String[]{"http://hdl.handle.net/11858/00-1779-0000-000C-DAAF-B","https://clarin.phonetik.uni-muenchen.de/BASWebServices"};
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
