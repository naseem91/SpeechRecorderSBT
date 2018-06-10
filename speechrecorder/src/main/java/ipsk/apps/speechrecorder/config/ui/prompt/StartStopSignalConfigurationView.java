//Speechrecorder
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

/*
 * Date  : Jun 24, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config.ui.prompt;

import ipsk.apps.speechrecorder.config.PromptConfiguration;
import ipsk.apps.speechrecorder.monitor.StartStopSignal;
import ipsk.swing.JServiceSelector;

/**
 * UI panel for start stop signal plugins configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class StartStopSignalConfigurationView extends JServiceSelector<StartStopSignal>{

	private static final long serialVersionUID = 1L;

	public StartStopSignalConfigurationView(){
        super(StartStopSignal.class);
        

    }
    
    public void setPromptConfiguration(PromptConfiguration promptConfiguration){
        String className=null;
        ipsk.apps.speechrecorder.config.StartStopSignal sssc=promptConfiguration.getStartStopSignal();
        if(sssc!=null){
            className=sssc.getClassname();
        }
        setSelectedClassname(className);
    }

    public void applyValues(PromptConfiguration p){
//        ipsk.apps.speechrecorder.config.StartStopSignal sssc=p.getStartStopSignal();
        String classname=null;
        classname=getSelectedClassname();
        if(classname==null){
            p.setStartStopSignal(null);
        }else{
            ipsk.apps.speechrecorder.config.StartStopSignal nc=new ipsk.apps.speechrecorder.config.StartStopSignal();
            nc.setClassname(classname);
            p.setStartStopSignal(nc);
        }
    }

}
