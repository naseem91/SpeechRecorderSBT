//    Speechrecorder
//    (c) Copyright 2009-2011
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

package ipsk.apps.speechrecorder.config;

import java.util.ArrayList;
import java.util.List;

import ips.annot.BundleAnnotationPersistor;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMElements;

/**
 * Auto annotation  configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

@DOMElements({"autoAnnotators"})
@DOMAttributes({"launchMode"})
public class AutoAnnotation {

	
	private boolean enabled;
	
	private BundleAnnotationPersistor[] autoAnnotationPersistors=new BundleAnnotationPersistor[0];
	
	private AutoAnnotator[] autoAnnotators=new AutoAnnotator[0];

	/**
	 * @return the autoAnnotators
	 */
	public AutoAnnotator[] getAutoAnnotators() {
		return autoAnnotators;
	}

	/**
	 * @param autoAnnotators the autoAnnotators to set
	 */
	public void setAutoAnnotators(AutoAnnotator[] autoAnnotators) {
		this.autoAnnotators = autoAnnotators;
	}
	
	public enum LaunchMode {MANUAL,RECORD_FINISHED,SESSION_FINISHED}
	
	private LaunchMode launchMode=LaunchMode.RECORD_FINISHED;

	/**
	 * @return the launchMode
	 */
	public LaunchMode getLaunchMode() {
		return launchMode;
	}

	/**
	 * @param launchMode the launchMode to set
	 */
	public void setLaunchMode(LaunchMode launchMode) {
		this.launchMode = launchMode;
	}

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
	
}
