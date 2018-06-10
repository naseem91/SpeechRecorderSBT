//    Speechrecorder
// 	  (c) Copyright 2015
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

import ips.annot.model.PredefinedLevelDefinition;
import ips.annot.text.SingleLevelTextFilePersistor;
import ipsk.util.LocalizableMessage;

/**
 * @author klausj
 *
 */
public class MAUSTemplateTextFilePersistor extends SingleLevelTextFilePersistor {

    /**
     * 
     */
    public MAUSTemplateTextFilePersistor() {
        super();
        String tpLdKn=PredefinedLevelDefinition.TPL.getKeyName();
        setLevelDefinitionKeyName(tpLdKn);
        
    }
    
    public String getPreferredFilenameSuffix(){
    	return "";
    }
    
    @Override
    public LocalizableMessage getTitle() {
       return new LocalizableMessage("Single text line loader/writer for annotation template");
    }

    @Override
    public LocalizableMessage getDescription() {
       return new LocalizableMessage("Simple text loader writer for annotation template for MAUS processing.");
    }

}
