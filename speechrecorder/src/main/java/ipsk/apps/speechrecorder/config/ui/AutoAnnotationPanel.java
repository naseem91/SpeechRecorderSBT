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

package ipsk.apps.speechrecorder.config.ui;

import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ipsk.apps.speechrecorder.annotation.auto.AutoAnnotationPluginManager;
import ipsk.apps.speechrecorder.config.AutoAnnotation;
import ipsk.apps.speechrecorder.config.AutoAnnotation.LaunchMode;
import ipsk.apps.speechrecorder.config.AutoAnnotator;
import ipsk.apps.speechrecorder.config.Prompter;
import ipsk.apps.speechrecorder.config.Prompter.SpeakerWindowType;
import ipsk.swing.EnumSelectionItem;
import ipsk.swing.EnumVector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author klausj
 * 
 */
public class AutoAnnotationPanel extends JPanel implements ActionListener {

//	private JComboBox launchModeBox;
	private List<AutoAnnotatorPanel> annotatorPanels=new ArrayList<AutoAnnotatorPanel>();
	private AutoAnnotationPluginManager autoAnnotationPluginManager;
	public AutoAnnotationPanel(AutoAnnotationPluginManager autoAnnotationPluginManager){
        super(new GridBagLayout());
        this.autoAnnotationPluginManager=autoAnnotationPluginManager;
        List<AutoAnnotationServiceDescriptor> aasds=autoAnnotationPluginManager.getAutoAnnotatorServiceDescriptors();
		// this.p = p;

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 5, 2, 5);
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridx = 0;
		c.gridy = 0;
		//c.weightx = 0;

		c.gridx = 0;
//		add(new JLabel("Launch mode"), c);
//		c.gridx++;
//		EnumVector<LaunchMode> launchModesVector = new EnumVector<LaunchMode>(
//				LaunchMode.class);
//		launchModeBox = new JComboBox(launchModesVector);
//		add(launchModeBox, c);
//		c.gridy++;
//		
		c.gridwidth=2;
//		c.fill=GridBagConstraints.BOTH;
//		c.weighty=1.0;
		for(AutoAnnotationServiceDescriptor sd:aasds){
			AutoAnnotatorPanel aap=new AutoAnnotatorPanel(sd);
			aap.setEnabled(false);
			aap.setActionListener(this);
			annotatorPanels.add(aap);
			c.gridx=0;
			c.gridy++;
			add(aap,c);
		}
		updateUIdependencies();
	}
	
	private void updateUIdependencies(){
	    
	    boolean changed=false;
	    do{

	        List<AutoAnnotationServiceDescriptor> activatedAas=new ArrayList<AutoAnnotationServiceDescriptor>();
	        for(AutoAnnotatorPanel aap:annotatorPanels){
	            if(aap.isSelected()){
	                AutoAnnotationServiceDescriptor aasd=aap.getServiceDescriptor();
	                activatedAas.add(aasd);
	            }
	        }
	        changed=false;
	        for(AutoAnnotatorPanel aap:annotatorPanels){
	            AutoAnnotationServiceDescriptor aasd=aap.getServiceDescriptor();
	            boolean enabledBefore=aap.isEnabled();
	            boolean depsOK=autoAnnotationPluginManager.checkDependencies(activatedAas, aasd);
	            changed=changed || (enabledBefore!=depsOK);
	            aap.setEnabled(depsOK);

	        }
	    }while(changed);
	}

	/**
	 * @param aanno
	 */
	public void setAutoAnnotationConfig(AutoAnnotation aanno) {
//		LaunchMode lm=aanno.getLaunchMode();
//		 EnumSelectionItem<LaunchMode> lmEsi=new EnumSelectionItem<LaunchMode>(lm);
//		launchModeBox.setSelectedItem(lmEsi);
		
		// reset first
		for(AutoAnnotatorPanel aap:annotatorPanels){
			aap.setConfig(null);
		}
		AutoAnnotator[] aas=aanno.getAutoAnnotators();
		for(AutoAnnotator aa:aas){
			String scNm=aa.getClassname();
			
			// find panel service class name acts as unique ID
			for(AutoAnnotatorPanel aap:annotatorPanels){
				String aapScNm=aap.getServiceDescriptor().getServiceImplementationClassname();
				if(scNm.equals(aapScNm)){
					// found
					aap.setConfig(aa);
					break;
				}
			}
			// TODO check if found
		}
		updateUIdependencies();
	}
	
	public void applyValues(AutoAnnotation aanno){
//		 EnumSelectionItem<LaunchMode> lmesi=(EnumSelectionItem<LaunchMode>)launchModeBox.getSelectedItem();
//		LaunchMode lm=lmesi.getEnumVal();
//		
//		aanno.setLaunchMode(lm);
		List<AutoAnnotator> aastmp=new ArrayList<AutoAnnotator>();
		
		for(AutoAnnotatorPanel aap:annotatorPanels){
			AutoAnnotator aaTemplate=new AutoAnnotator();
			aap.applyValues(aaTemplate);
			if(aaTemplate.isEnabled()){
				aastmp.add(aaTemplate);
			}
		}
		AutoAnnotator[] aas=aastmp.toArray(new AutoAnnotator[0]);
		aanno.setAutoAnnotators(aas);
	}

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        updateUIdependencies();
    }
	
}
