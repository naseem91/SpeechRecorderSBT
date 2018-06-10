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



package ipsk.apps.speechrecorder.annotation.auto.impl;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ips.annot.autoannotator.AutoAnnotation;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotator;
import ips.annot.autoannotator.AutoAnnotatorException;
import ips.annot.autoannotator.BundleAutoAnnotation;
import ips.annot.model.PredefinedLevelDefinition;
import ips.annot.model.db.Bundle;
import ips.annot.model.db.Item;
import ips.annot.model.db.Level;

/**
 * @author klausj
 *
 */
public class PromptAutoAnnotator implements AutoAnnotator{

    private PromptAutoAnnotatorServiceDescriptor sd;
    private AnnotationRequest annotationRequest;
    private String promptText;
    public String getPromptText() {
        return promptText;
    }

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    /**
     * 
     */
    public PromptAutoAnnotator() {
       super();
       sd=new PromptAutoAnnotatorServiceDescriptor();
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public AutoAnnotation call() throws Exception {
        if(promptText==null){
            return null;
        }
        Bundle inputBundle=annotationRequest.getBundle();
        List<Level> lvls = inputBundle.getLevels();
        Level lvl = null;
        for (Level l : lvls) {
            if (PredefinedLevelDefinition.PRT.getKeyName().equals(l.getName())) {
                lvl = l;
                break;
            }
        }
        if (lvl == null) {
            lvl = new Level();
            lvl.setName(PredefinedLevelDefinition.PRT.getKeyName());
            lvl.setType(PredefinedLevelDefinition.PRT.getType());
            lvls.add(lvl);

        }
        Item it=new Item();
        it.setLevel(lvl);
        it.setLabel(PredefinedLevelDefinition.PRT.getKeyName(), promptText);
        lvl.getItems().add(it);
        return new BundleAutoAnnotation(inputBundle);
    }

    /* (non-Javadoc)
     * @see ipsk.util.services.ServiceDescriptorProvider#getServiceDescriptor()
     */
    @Override
    public AutoAnnotationServiceDescriptor getServiceDescriptor() {
        
        return sd;
    }

    /* (non-Javadoc)
     * @see ips.annot.autoannotator.AutoAnnotator#isBundleSupported(ips.annot.model.db.Bundle)
     */
    @Override
    public boolean isBundleSupported(Bundle bundle) throws IOException {
        return true;
    }

    /* (non-Javadoc)
     * @see ips.annot.autoannotator.AutoAnnotator#open()
     */
    @Override
    public void open() {
        
    }

    /* (non-Javadoc)
     * @see ips.annot.autoannotator.AutoAnnotator#setAnnotationRequest(ips.annot.autoannotator.AutoAnnotator.AnnotationRequest)
     */
    @Override
    public void setAnnotationRequest(AnnotationRequest annotationRequest) {
       this.annotationRequest=annotationRequest;
    }

    /* (non-Javadoc)
     * @see ips.annot.autoannotator.AutoAnnotator#close()
     */
    @Override
    public void close() {
      
    }

    /* (non-Javadoc)
     * @see ips.annot.autoannotator.AutoAnnotator#needsWorker()
     */
    @Override
    public boolean needsWorker() {
        return false;
    }

}
