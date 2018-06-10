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



package ipsk.apps.speechrecorder.config.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ips.annot.BundleAnnotationPersistor;
import ips.annot.BundleAnnotationPersistorServiceDescriptor;
import ipsk.apps.speechrecorder.config.AnnotationPersistence;
import ipsk.apps.speechrecorder.config.AutoAnnotation.LaunchMode;
import ipsk.apps.speechrecorder.config.BundleAnnotationPersistorConfig;
import ipsk.swing.EnumVector;

/**
 * UI to choose multiple audio devices.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class AnnotationPersistorsChooser extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -217969118479629389L;
    private JComboBox launchModeBox;
    private List<AnnotationPersistorPanel> annotatorPanels=new ArrayList<AnnotationPersistorPanel>();

    public AnnotationPersistorsChooser(List<BundleAnnotationPersistorServiceDescriptor> aasds){
        super(new GridBagLayout());

        // this.p = p;

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(2, 5, 2, 5);
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridx = 0;
        c.gridy = 0;
        //c.weightx = 0;

//        c.gridx = 0;
//        c.gridy++;
//        add(new JLabel("Launch mode"), c);
//        c.gridx++;
//        EnumVector<LaunchMode> launchModesVector = new EnumVector<LaunchMode>(
//                LaunchMode.class);
//        launchModeBox = new JComboBox(launchModesVector);
//        add(launchModeBox, c);
//        
//        c.gridwidth=2;
//      c.fill=GridBagConstraints.BOTH;
//      c.weighty=1.0;
        for(BundleAnnotationPersistorServiceDescriptor sd:aasds){
            AnnotationPersistorPanel aap=new AnnotationPersistorPanel(sd);
            annotatorPanels.add(aap);
            c.gridx=0;
            c.gridy++;
            add(aap,c);
        }
    }

    /**
     * @param aanno
     */
    public void setAutoAnnotationConfig(AnnotationPersistence annoPers) {
//        LaunchMode lm=anno.getLaunchMode();
//         EnumSelectionItem<LaunchMode> lmEsi=new EnumSelectionItem<LaunchMode>(lm);
//        launchModeBox.setSelectedItem(lmEsi);
    	
    	// reset first
    	for(AnnotationPersistorPanel aap:annotatorPanels){
          aap.setConfig(null);
        }
        List<BundleAnnotationPersistorConfig>aas=annoPers.getBundleAnnotationPersistors();
        for(BundleAnnotationPersistorConfig aa:aas){
            String scNm=aa.getClassname();
            
            // find panel service class name acts as unique ID
            for(AnnotationPersistorPanel aap:annotatorPanels){
                String aapScNm=aap.getAnnotationPersistor().getServiceImplementationClassname();
                if(scNm.equals(aapScNm)){
                    // found
                    aap.setConfig(aa);
                    break;
                }
            }
            // TODO check if found
        }
    }
    
    public void applyValues(AnnotationPersistence annoPers){
//         EnumSelectionItem<LaunchMode> lmesi=(EnumSelectionItem<LaunchMode>)launchModeBox.getSelectedItem();
//        LaunchMode lm=lmesi.getEnumVal();
//        
//        aanno.setLaunchMode(lm);
        List<BundleAnnotationPersistorConfig> aastmp=new ArrayList<BundleAnnotationPersistorConfig>();
        
        for(AnnotationPersistorPanel aap:annotatorPanels){
            BundleAnnotationPersistorConfig aaTemplate=new BundleAnnotationPersistorConfig();
            aap.applyValues(aaTemplate);
            if(aaTemplate.isEnabled()){
                aastmp.add(aaTemplate);
            }
        }
//        AutoAnnotator[] aas=aastmp.toArray(new AutoAnnotator[0]);
        annoPers.setBundleAnnotationPersistors(aastmp);
    }
    
}
