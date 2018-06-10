//    Speechrecorder
//    (c) Copyright 2012
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

package ipsk.apps.speechrecorder.annotation;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

public class EnumAnnotationAction extends AbstractAction{

	private static final long serialVersionUID = 1L;
	private AnnotationManager annotationManager;
	private String annotationName;
	private String propertyName;
	private String enumConstantName;

	/**
	 *  
	 */
	public EnumAnnotationAction(AnnotationManager annotationManager,String annotationName,String propertyName,String enumConstantName,String enumConstantDisplayName) {
		super();
		this.annotationManager=annotationManager;
		this.annotationName=annotationName;
		this.propertyName=propertyName;
		this.enumConstantName=enumConstantName;
		putValue(Action.ACTION_COMMAND_KEY, enumConstantName);
		String actionDisplayName=enumConstantDisplayName;
		if(actionDisplayName==null){
			actionDisplayName=enumConstantName;
		}
		putValue(Action.NAME, actionDisplayName);
	}

	public String getActionCommand() {
		return (String) getValue(Action.ACTION_COMMAND_KEY);
	}

	public void actionPerformed(ActionEvent e) {
		
		annotationManager.annotate(null,annotationName, propertyName,enumConstantName);
	}

}
