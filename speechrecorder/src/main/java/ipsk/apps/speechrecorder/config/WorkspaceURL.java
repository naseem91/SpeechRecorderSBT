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

/*
 * Date  : Oct 19, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A URL referencing files in a workspace.
 * Implements an own protocol for files in a workspace.
 * This class is intended to reference files relative to a workspace project to allow
 * The URL has the form: speechrecorder:
 * host, platform and path independent project packaging.    
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class WorkspaceURL {

	String protocolName="speechrecorder";
	WorkspaceProject project;
	URL url;
	
	
	public WorkspaceURL(WorkspaceProject project,String relFilePath) {
		this.project=project;
	}

	public static void main(String[] args) {
	}
	
	public InputStream openStream() throws IOException {
		return url.openStream();
	}

}
