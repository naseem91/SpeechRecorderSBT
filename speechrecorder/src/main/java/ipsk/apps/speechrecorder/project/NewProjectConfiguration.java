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

/*
 * Date  : Aug 21, 2009
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.project;

import ipsk.apps.speechrecorder.config.ProjectConfiguration;


/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class NewProjectConfiguration {
    private ProjectConfiguration projectConfiguration;
    private boolean useExampleScript=false;
    
    public NewProjectConfiguration(){
        super();
        projectConfiguration=new ProjectConfiguration();
    }

    public ProjectConfiguration getProjectConfiguration() {
        return projectConfiguration;
    }

    public void setProjectConfiguration(ProjectConfiguration projectConfiguration) {
        this.projectConfiguration = projectConfiguration;
    }

    public boolean isUseExampleScript() {
        return useExampleScript;
    }

    public void setUseExampleScript(boolean useExampleScript) {
        this.useExampleScript = useExampleScript;
    }
    
}
