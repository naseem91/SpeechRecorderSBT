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

import java.util.ArrayList;
import java.util.List;

import ips.annot.BundleAnnotationPersistor;
import ips.annot.BundleAnnotationPersistorServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ips.annot.text.SingleLevelTextFilePersistor;
import ipsk.apps.speechrecorder.annotation.auto.AutoAnnotationPluginManager;
import ipsk.apps.speechrecorder.config.Annotation;
import ipsk.apps.speechrecorder.config.AnnotationPersistence;
import ipsk.apps.speechrecorder.config.AutoAnnotation;

import javax.swing.JTabbedPane;

/**
 * @author klausj
 *
 */
public class AnnotationPanel extends JTabbedPane {

	private Annotation annotation;
	private AnnotationPersistorsChooser persistorsChooser;
	private AutoAnnotationPanel autoAnnotationPanel;
	public AnnotationPanel(List<BundleAnnotationPersistorServiceDescriptor> bundlePersistorSds,AutoAnnotationPluginManager autoAnnotationPluginManager){
		super();
		persistorsChooser=new AnnotationPersistorsChooser(bundlePersistorSds);
		addTab("Persist", persistorsChooser);
		autoAnnotationPanel = new AutoAnnotationPanel(autoAnnotationPluginManager);
		addTab("Auto annotation", autoAnnotationPanel);
	}

	/**
	 * @param anno
	 */
	public void setAnnotationConfig(Annotation anno) {
		this.annotation=anno;
		persistorsChooser.setAutoAnnotationConfig(anno.getPersistence());
		AutoAnnotation aanno=anno.getAutoAnnotation();
		autoAnnotationPanel.setAutoAnnotationConfig(aanno);
	}
	
	public void applyValues(Annotation anno){
	    AnnotationPersistence annoPers=anno.getPersistence();
	    persistorsChooser.applyValues(annoPers);
		AutoAnnotation aanno=anno.getAutoAnnotation();
		autoAnnotationPanel.applyValues(aanno);
	}
	
}
