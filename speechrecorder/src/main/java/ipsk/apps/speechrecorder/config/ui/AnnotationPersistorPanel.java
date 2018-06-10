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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import ips.annot.BundleAnnotationPersistor;
import ips.annot.BundleAnnotationPersistorServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ipsk.apps.speechrecorder.config.AutoAnnotation.LaunchMode;
import ipsk.apps.speechrecorder.config.AutoAnnotator;
import ipsk.apps.speechrecorder.config.BundleAnnotationPersistorConfig;
import ipsk.swing.EnumVector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 * @author klausj
 *
 */
public class AnnotationPersistorPanel extends JPanel {
	
	private BundleAnnotationPersistorServiceDescriptor annotationPersistor;
	
	private JCheckBox enabledCheckBox;
	public AnnotationPersistorPanel(BundleAnnotationPersistorServiceDescriptor sd){
		super(new GridBagLayout());
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.annotationPersistor=sd;
		
        
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 5, 2, 5);
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;

		add(new JLabel("Enable:"), c);
		c.gridx++;
		enabledCheckBox=new JCheckBox();
		add(enabledCheckBox, c);
		
		c.gridx = 0;
		c.gridy++;
		add(new JLabel("Name:"), c);
		c.gridx++;
		String title=annotationPersistor.getTitle().localize();
		c.weightx=2;
		add(new JLabel(title), c);
		c.weightx=0;
		
		c.gridx = 0;
		c.gridy++;
		add(new JLabel("Description:"), c);
		c.gridx++;
		c.weightx=2;
		String descr=annotationPersistor.getDescription().localize();
		add(new JLabel(descr), c);
		c.weightx=0;
	}
	
	public void applyValues(BundleAnnotationPersistorConfig aaTemplate){
		aaTemplate.setClassname(annotationPersistor.getServiceImplementationClassname());
		aaTemplate.setEnabled(enabledCheckBox.isSelected());
	}

	/**
	 * @param aa
	 */
	public void setConfig(BundleAnnotationPersistorConfig aa) {
		if(aa==null){
			enabledCheckBox.setSelected(false);
		}else{
			enabledCheckBox.setSelected(aa.isEnabled());
		}
		
	}

    public BundleAnnotationPersistorServiceDescriptor getAnnotationPersistor() {
        return annotationPersistor;
    }
	
	
	
}
